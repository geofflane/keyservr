package data

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import domain.PublicKey
import domain.Signature
import com.github.nscala_time.time.Imports._
import anorm.~

/**
 * @author geoff
 * @since 6/12/13
 */
trait KeyRepository {
  def save(pk: PublicKey)

  def findByEmail[B >: PublicKey](email: String): List[B]
}

object DbKeyRepository extends KeyRepository {

  case class DbSignature(id: String, createDate: DateTime) extends Signature

  case class SimplePublicKey(id: String, fingerprint: String, algorithm: String, bitStrength: Int,
                             createDate: DateTime, isRevoked: Boolean, rawKey: String) {
  }

  case class DbPublicKey(id: String, fingerprint: String, algorithm: String, bitStrength: Int,
                         createDate: DateTime, isRevoked: Boolean, rawKey: String, userIds: List[String],
                         signatures: List[Signature]) extends PublicKey {
  }

  val userId = {
    str("user_id") map {
      case uid => uid
    }
  }

  val signature = {
    str("sig_key_id") ~
      date("sig_create_date") map {
      case id ~ cd => DbSignature(id, new DateTime(cd))
    }
  }

  val pubKey = {
    long("id") ~
      str("key_id") ~
      str("fingerprint") ~
      str("algorithm") ~
      int("bit_strength") ~
      date("create_date") ~
      bool("is_revoked") ~
      str("rawkey") map {
      case i ~ id ~ fp ~ al ~ bs ~ cd ~ ir ~ rk => SimplePublicKey(id, fp, al, bs, new DateTime(cd), ir, rk)
    }
  }

  val withKeysAndSigs = pubKey ~ userId ~ (signature ?) map {
    case pk ~ uid ~ sigs => (pk, uid, sigs)
  }

  def save(pk: PublicKey) {
    DB.withTransaction { implicit conn =>
        val id: Option[Long] = SQL( """INSERT INTO public_key (key_id, fingerprint, algorithm, bit_strength, create_date, is_revoked, rawkey)
                             |VALUES({keyId}, {fingerprint}, {algorithm}, {bitStrength}, {createDate}, {isRevoked}, {rawKey});""".stripMargin)
          .onParams(pk.id, pk.fingerprint, pk.algorithm, pk.bitStrength, pk.createDate.toDate, pk.isRevoked, pk.rawKey)
          .executeInsert()

        id.map { pkId: Long =>
          pk.userIds.foreach {
            uid => {
              SQL("INSERT INTO public_key_user (public_key_id, user_id) VALUES({publicKeyId}, {userId});")
                .onParams(pkId, uid)
                .executeInsert()
            }
          }

          pk.signatures.foreach {
            sig => {
              SQL("INSERT INTO public_key_signature (public_key_id, key_id, create_date) VALUES ({public_key_id}, {key_id}, {create_date})")
              .onParams(pkId, sig.id, sig.createDate.toDate)
              .executeInsert()
            }
          }
        }
    }
  }

  // Good luck wrapping your head around this one...
  // JOIN 2 Tables and you get them back as a flat list
  // group by the pubKeyColumns give you Map[pubKeyColumns, Seq(pubKeyColumns, userIdColumns)]
  // map over that and _1 = pubKeyColumns, where _2 = Seq(pubKeyColumns, userIdColumns)
  // match on those parts to build the public key
  // rest.map { _._2 } pulls out the userIdColumns into a List[String]
  def findByEmail[B >: PublicKey](email: String): List[B] = {
    DB.withConnection {
      implicit conn =>
        SQL(
          """SELECT public_key.id, public_key.key_id, fingerprint, algorithm, bit_strength, public_key.create_date, is_revoked, rawkey,
            |   user_id, public_key_signature.key_id as sig_key_id, public_key_signature.create_date as sig_create_date
            |FROM public_key
            |INNER JOIN public_key_user ON public_key_user.public_key_id = public_key.id
            |LEFT JOIN public_key_signature ON public_key_signature.public_key_id = public_key.id
            |WHERE public_key_user.user_id LIKE '%' || {email} || '%';""".stripMargin)
          .on("email" -> email)
          .as(withKeysAndSigs *)
          .groupBy(_._1). map {
        case (pk, rest) => {
          val sigs = rest.unzip3._3.map(_.orNull)   // convert Option[DbSignature]s to List[DbSignatures]
          val uids = rest.map { _._2 }              // Convert String to List[String]
          DbPublicKey(pk.id, pk.fingerprint, pk.algorithm, pk.bitStrength, pk.createDate, pk.isRevoked, pk.rawKey, uids, sigs)
        }
      }.toList
    }
  }
}
