package controllers


import play.api.mvc._
import play.api.libs.json._
import com.gilt.akk.cluster.api.test.v0.models.json._
import service.SessionService

class Sessions(sessionService: SessionService) extends BaseController {

  def get() = Action.async { implicit request =>
    sessionService.get().map(
      session => Ok(Json.toJson(session))
    )
  }
}
