package controllers

import com.gilt.akk.cluster.api.test.v0.models.json._
import com.gilt.akk.cluster.api.test.v0.models.{Healthcheck => HealthCheckModel}
import play.api.libs.json._
import play.api.mvc._

class Healthcheck extends Controller {

  def get() = Action{ implicit request =>
    Ok(Json.toJson(HealthCheckModel("Akka cluster test")))
  }
}
