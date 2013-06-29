package data

import org.specs2.mutable._
import util.WithDbData

/**
 * @author geoff
 * @since 6/19/13
 */
class DbKeyRepositorySpec extends Specification {

  "With a public key saved in the database the system" should {
    "Allow you to query for it by email" in new WithDbData {
      val keys = DbKeyRepository.findByEmail("geoff@zorched.net")
      keys.size must be equalTo 1
      keys.head.userIds.head must contain("geoff@zorched.net")
      keys.head.signatures.size must be equalTo 1
    }

    "And not return anything for a non-existant email" in new WithDbData {
      val keys = DbKeyRepository.findByEmail("bad@example.com")
      keys must beEmpty
    }
  }
}
