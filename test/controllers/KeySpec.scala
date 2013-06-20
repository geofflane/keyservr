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
class KeySpec extends Specification {
  val PK_STRING = """-----BEGIN PGP PUBLIC KEY BLOCK-----
Version: OpenPGP.js v.1.20130228
Comment: http://openpgpjs.org

xsBNBFGx8n4BCACLcaVvDGeQuyJN7t0DwjA5fDsDXwQTOcsNV3amcF8A7oHn
0kWmVKdeq3IvTK7YHqzmGrtmkOpb7aLw+zkZb+srlbaB1FfYaFbPBVk6XaQY
ByEHVex9pPLj9Moa+2/KRKfbo1rcejbISLykGFKqOg6qPwjM0/pjJ4WxKNni
ISzwh+EIYpyNUux5nAcB1X0peoMq0azubmMawi9o/WYCdv90HyHwXapAGk1l
orVpmbXQdMwd1Cs4xMrPHEKrK6nhRLFG/pYTmNDlEIECemntkfx1hXiw7EXP
zj193SiJEFknK5E4+Jw/4qmHf3ZV8jOfrEyzALcAja1Ht/veKcbQyTcNABEB
AAHNI0dlb2ZmcmV5IE0gTGFuZSA8Z2VvZmZAem9yY2hlZC5uZXQ+wsBcBBAB
AgAQBQJRsfJ/CRAPuxAYW2v3XgAACE0H/3UfXijiTcCEA8HNK/P0ioUz9q/Z
R/HIlnjq2gTG+Q9Dsb1Up7wpQUZn2fjcZjNIAAgXRBc6IiOcF3SLSWGsV2ap
2fK2GxyXAy272IMzLo0f3hbBnRkEXr8WdqPuwOcxV10rDSWdy79MzO8IDg77
4BqJmapdELI0dg1I/T5sYIq9yRPShADdMM9LIAkGcJJpsOnaTf39VdJqeFO5
CxAlrpoYen8PWef+Y8xdTOA/1UGOVeDTD6Yz78o1KhJGAfKvdRxTUXzWV88l
6JlWUD86jE2CYpnhPmJqndq/aaw56a4BmNQiTpUCztXfOgBwde3SsYI2aF+o
k2jMa3uu9qerETw=
=DOgA
-----END PGP PUBLIC KEY BLOCK-----
                  """

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
          .withJsonBody(JsObject(Seq("rawPk" -> JsString(PK_STRING))))).get

        status(parsed) must equalTo(OK)
//        println(contentAsString(parsed))
        contentType(parsed) must beSome.which(_ == "application/json")
        val jsonResult = Json.parse(contentAsString(parsed))
        (jsonResult \ "id").as[String] must be equalTo "0FBB10185B6BF75E"
      }
    }
  }
}