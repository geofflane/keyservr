package data

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import domain.PublicKey
import com.github.nscala_time.time.Imports._
import anorm.~

/**
 * @author geoff
 * @since 6/12/13
 */
case class DbPublicKey(id: String, fingerprint: String, algorithm: String, bitStrength: Int,
                       createDate: DateTime, isRevoked: Boolean, rawKey: String, userIds: List[String]) extends PublicKey

trait KeyRepository {
  def save(pk: PublicKey)

  def findByEmail[B >: PublicKey](email: String): List[B]
}

object DbKeyRepository extends KeyRepository {
  val userIdColumns = {
    str("user_id")
  }

  val pubKeyColumns = {
    long("id") ~
      str("key_id") ~
      str("fingerprint") ~
      str("algorithm") ~
      int("bit_strength") ~
      date("create_date") ~
      bool("is_revoked") ~
      str("rawkey")
  }

  def save(pk: PublicKey) {
    DB.withConnection { implicit conn =>
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
        }
    }
  }

  // Good luck wrapping your head around this one...
  // JOIN 2 Tables and you get them back as a flat list
  // group by the pubKeyColumns give you Map[pubKeyColumns, Seq(pubKeyColumns, userIdColumns)]
  // map over that and _1 = pubKeyColumns, where _2 = Seq(pubKeyColumns, userIdColumns)
  // match on those parts to build the public key
  // uids.map { _._2 } pulls out the userIdColumns into a List[String]
  def findByEmail[B >: PublicKey](email: String): List[B] = {
    DB.withConnection {
      implicit conn =>
        SQL(
          """SELECT public_key.id, key_id, fingerprint, algorithm, bit_strength, create_date, is_revoked, rawkey, user_id
            |FROM public_key
            |INNER JOIN public_key_user ON public_key_user.public_key_id = public_key.id
            |WHERE public_key_user.user_id LIKE '%' || {email} || '%';""".stripMargin)
          .on("email" -> email)
          .as(pubKeyColumns ~ userIdColumns *)
          .groupBy(_._1)
          .map {
          case ((i ~ id ~ fp ~ al ~ bs ~ cd ~ ir ~ rk), uids) => DbPublicKey(id, fp, al, bs, new DateTime(cd), ir, rk, uids.map {
            _._2
          })
        }.toList
    }
  }
}
