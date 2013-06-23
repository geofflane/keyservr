package controllers

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
import play.api.test.FakeApplication
import play.api.libs.json.JsObject
import play.api.libs.json.JsString

/**
  */
class KeyParseSpec extends Specification {

  "Key" should {

    "parse with invalid json request should return error" in {
      running(FakeApplication()) {
        val parsed = route(FakeRequest(POST, "/keys/parse")).get
        status(parsed) must equalTo(BAD_REQUEST)
      }
    }

    "parse a valid json pk request" in {
      running(FakeApplication()) {
        val parsed = route(FakeRequest(POST, "/keys/parse")
          .withJsonBody(JsObject(Seq("rawPk" -> JsString(_root_.util.TestUtil.rawPublicKey))))).get

        status(parsed) must equalTo(OK)
        contentType(parsed) must beSome.which(_ == "application/json")
        val jsonResult = Json.parse(contentAsString(parsed))
        (jsonResult \ "id").as[String] must be equalTo "0FBB10185B6BF75E"
      }
    }
  }
}