package controllers

import java.util.UUID

import akka.actor.ActorRef
import play.api.mvc._

class Addresses(mainActor: ActorRef) extends BaseController {

  def get(uuid: UUID) = Action { request =>
    Ok
  }
}
