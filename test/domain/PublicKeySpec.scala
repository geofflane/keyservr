package domain

import org.specs2.mutable._

/**
 * @author geoff
 * @since 6/11/13
 */
class PublicKeySpec extends Specification {

  "Reading a valid public key" should {
    val pk = PublicKey(util.TestUtil.rawPublicKey).get

    "generate the proper id" in {
      pk.id must be equalTo "0FBB10185B6BF75E"
    }
    "generate the proper fingerprint" in {
      pk.fingerprint must be equalTo "B97D 244A 92DF A4DD 69CC 0DA7 0FBB 1018 5B6B F75E"
    }
    "generate the proper length" in {
      pk.bitStrength must be equalTo 2048
    }
    "generate the proper algorithm" in {
      pk.algorithm must be equalTo "RSA (Encrypt & Sign)"
    }
    "has a user id" in {
      pk.userIds.head must be equalTo "Geoffrey M Lane <geoff@zorched.net>"
    }
  }

  "Reading an invalid public key" should {
    val pk = PublicKey("")

    "return none" in {
      pk must beNone
    }
  }
}
