package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import _root_.domain.PublicKey
import com.github.nscala_time.time._
import data.DbKeyRepository

/**
 * @author geoff
 * @since 6/12/13
 */
object Keys extends Controller with KeyController {
    val keyRepository = DbKeyRepository
}

trait KeyController {
    this: Controller =>

  implicit object PublicKeyWrites extends Writes[PublicKey] {
    def writes(pk: PublicKey): JsValue = JsObject(Seq(
      "id" -> JsString(pk.id),
      "createDate" -> JsString(pk.createDate.toString(StaticDateTimeFormat.forPattern("YYYY-mm-dd'T'hh:MM:ssZ"))),
      "algorithm" -> JsString(pk.algorithm),
      "fingerprint" -> JsString(pk.fingerprint),
      "bitStrength" -> JsNumber(pk.bitStrength),
      "userIds" -> JsArray(pk.userIds.map(JsString(_))),
      "rawPk" -> JsString(pk.rawKey)
    ))
  }

  def parseKey = Action {
    implicit request =>
      request.body.asJson.map {
        json => {
          val pk = PublicKey((json \ "rawPk").as[String])
          Ok(Json.toJson(pk))
        }
      }.getOrElse {
        BadRequest("Expecting Json Data")
      }
  }

  def save = Action {
    implicit request =>
      request.body.asJson.map {
        json => {
          val pk = PublicKey((json \ "rawPk").as[String])

        }
      }

      Ok("")
  }

  def getByEmail(email: String) = Action {
    Ok("")
  }
}

