package controllers

import java.util.UUID

import com.gilt.akk.cluster.api.test.v0.models.Order
import com.gilt.akk.cluster.api.test.v0.models.json._
import play.api.libs.json.Json
import play.api.mvc.Action

class Orders extends BaseController {

  def get(uuid: UUID) = Action {
    Ok(Json.toJson(Order(uuid, uuid.toString)))
  }

}
