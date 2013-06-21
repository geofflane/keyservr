package domain

import scala.collection.JavaConversions._

import org.bouncycastle.openpgp._
import java.io.ByteArrayInputStream
import org.apache.commons.codec.binary.Hex
import org.bouncycastle.bcpg.PublicKeyAlgorithmTags
import org.apache.commons.lang3.StringUtils
import com.github.nscala_time.time.Imports._

/**
 * @author geoff
 * @since 6/11/13
 */
abstract class PublicKey {
  val id: String
  val fingerprint: String
  val bitStrength: Int
  val algorithm: String
  val createDate: DateTime
  val userIds: List[String]
  val isRevoked: Boolean
  val rawKey: String
}

object PublicKey {

  case class ParsedPublicKey(rawKey: String, key: PGPPublicKey) extends PublicKey {
    val id = formatId(key.getKeyID)
    val fingerprint = formatFingerprint(key.getFingerprint)
    val bitStrength = key.getBitStrength
    val createDate = new DateTime(key.getCreationTime)
    val algorithm = algorithmForKey(key.getAlgorithm)
    val userIds = key.getUserIDs.asInstanceOf[java.util.Iterator[String]].toList
    val isRevoked = key.isRevoked

    private def algorithmForKey(key: Int) = key match {
      case PublicKeyAlgorithmTags.DIFFIE_HELLMAN => "Diffie Hellman"
      case PublicKeyAlgorithmTags.DSA => "DSA"
      case PublicKeyAlgorithmTags.EC => "EC"
      case PublicKeyAlgorithmTags.ECDSA => "ECDSA"
      case PublicKeyAlgorithmTags.ELGAMAL_ENCRYPT => "Elgamal (Encrypt)"
      case PublicKeyAlgorithmTags.ELGAMAL_GENERAL => "Elgamal (Encrypt & Sign)"
      case PublicKeyAlgorithmTags.RSA_ENCRYPT => "RSA (Encrypt)"
      case PublicKeyAlgorithmTags.RSA_GENERAL => "RSA (Encrypt & Sign)"
      case PublicKeyAlgorithmTags.RSA_SIGN => "RSA (Sign)"
      case _ => "Unknown"
    }

    private def formatId(id: Long) = {
      StringUtils.leftPad(id.toHexString, 16, "0").toUpperCase
    }
    private def formatFingerprint(bytes: Array[Byte]) =
      new String(Hex.encodeHex(bytes)).toUpperCase.grouped(4).mkString(" ")
  }

  def apply(publicKey: String): Option[PublicKey] = {
    val in = new ByteArrayInputStream(publicKey.getBytes)
    val publicKeys = getKeyring(in) map { k => k.getPublicKeys.asInstanceOf[java.util.Iterator[PGPPublicKey]].toList } getOrElse List()
    publicKeys.find(k => k.isEncryptionKey)
      .map((k: PGPPublicKey) => ParsedPublicKey(publicKey, k))
  }

  /**
   * Decode a PGP public key block and return the keyring it represents.
   */
  private def getKeyring(keyBlockStream: java.io.InputStream): Option[PGPPublicKeyRing] = {
    // PGPUtil.getDecoderStream() will detect ASCII-armor automatically and decode it,
    // the PGPObject factory then knows how to read all the data in the encoded stream
    val factory = new PGPObjectFactory(PGPUtil.getDecoderStream(keyBlockStream))
    factory.nextObject() match {
      case ring: PGPPublicKeyRing =>
        Option(ring)
      case _ => None
    }
  }
}

