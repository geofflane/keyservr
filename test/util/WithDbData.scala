package util

import play.api.test.{FakeApplication, WithApplication}
import org.specs2.execute.{Result, AsResult}
import domain.PublicKey
import data.DbKeyRepository

/**
 * @author geoff
 * @since 6/20/13
 */
abstract class WithDbData extends WithApplication(FakeApplication(additionalConfiguration = TestUtil.inMemoryDatabase)) {
  override def around[T](t : => T)(implicit evidence : AsResult[T]) : Result = super.around {
    PublicKey(TestUtil.rawPublicKey).map { pk =>
      DbKeyRepository.save(pk)
    }
    t
  }
}
