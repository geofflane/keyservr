package controllers

import util.WithDbData
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.{Json, JsObject, JsString}

/**
  */
class KeyWithDbSpec extends Specification {

  "Key" should {
    "be queryable by email" in new WithDbData {
      val parsed = route(FakeRequest(GET, "/keys/forEmail/geoff@zorched.net")).get
      status(parsed) must equalTo(OK)
      val jsonResult = Json.parse(contentAsString(parsed))
      (jsonResult \\ "id").head.as[String] must be equalTo "0FBB10185B6BF75E"
    }

    "be saveable" in new WithDbData {
      val parsed = route(FakeRequest(POST, "/keys/save")
        .withJsonBody(JsObject(Seq("rawPk" -> JsString(_root_.util.TestUtil.rawPublicKey))))).get
      status(parsed) must equalTo(OK)
    }

    "will fail saving if bad data is passed" in new WithDbData {
      val parsed = route(FakeRequest(POST, "/keys/save")
        .withJsonBody(JsObject(Seq("rawPk" -> JsString("Some random string"))))).get
      status(parsed) must equalTo(BAD_REQUEST)
    }
  }
}
