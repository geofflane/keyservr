package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import domain.PublicKey
import com.github.nscala_time.time._
import data.{KeyRepository, DbKeyRepository}
import scala.None
import play.api.Logger

/**
 * @author geoff
 * @since 6/12/13
 */
object Keys extends Controller with KeyController {
    val keyRepository = DbKeyRepository
}

trait KeyController {
    this: Controller =>

  val keyRepository: KeyRepository

  implicit object PublicKeyWrites extends Writes[PublicKey] {
    def writes(pk: PublicKey): JsValue = JsObject(Seq(
      "id" -> JsString(pk.id),
      "createDate" -> JsString(pk.createDate.toString(StaticDateTimeFormat.forPattern("YYYY-mm-dd'T'hh:MM:ssZ"))),
      "algorithm" -> JsString(pk.algorithm),
      "fingerprint" -> JsString(pk.fingerprint),
      "bitStrength" -> JsNumber(pk.bitStrength),
      "userIds" -> JsArray(pk.userIds.map(JsString(_))),
      "signatures" -> JsArray(pk.signatures.map(sig => {
        JsObject(Seq(
          "id"-> JsString(sig.id),
          "createDate" -> JsString(sig.createDate.toString(StaticDateTimeFormat.forPattern("YYYY-mm-dd'T'hh:MM:ssZ")))
        ))
      })),
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
          PublicKey((json \ "rawPk").as[String]) match {
            case Some(pk) => {
              keyRepository.save(pk)
              Ok(Json.toJson(pk))
            }
            case None => BadRequest("Expecting Json Data with rawPk element containing PublicKey")
          }
        }
      }.getOrElse {
        BadRequest("Expecting Json Data")
      }
  }

  def update = Action {
    BadRequest("Not yet implemented")
  }

  def getByEmail(email: String) = Action {
    implicit request =>
      Logger.info("Searching for PK by '%s'".format(email))

      val pks = keyRepository.findByEmail(email)
      Ok(Json.toJson(pks))
  }
}

