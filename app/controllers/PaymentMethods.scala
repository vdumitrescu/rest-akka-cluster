package controllers

import java.util.UUID

import actors.{MainActorMessages, SessionActorMessages}
import akka.actor.ActorRef
import akka.pattern.ask
import com.gilt.akk.cluster.api.test.v0.models.PaymentMethod
import com.gilt.akk.cluster.api.test.v0.models.json._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future

class PaymentMethods(mainActor: ActorRef) extends BaseController {

  def get(uuid: UUID) = Action.async {


    for {
      actorRef <- ask(mainActor, MainActorMessages.FindActor(uuid)).mapTo[ActorRef]
      response <- ask(actorRef, SessionActorMessages.GetPaymentMethods(uuid)).mapTo[Future[Seq[PaymentMethod]]]
      seq <- response
    } yield Ok(Json.toJson(seq))


//    val actorRefFuture: Future[ActorRef] = ask(mainActor, MainActorMessages.FindActor(uuid)).mapTo[ActorRef]
//    actorRefFuture flatMap { actorRef =>
//      val response: Future[Seq[PaymentMethod]] = ask(actorRef, SessionActorMessages.GetPaymentMethods(uuid)).mapTo[Seq[PaymentMethod]]
//      response map { seq => Ok(Json.toJson(seq)) } recover {
//        case t =>
//          InternalServerError(s"Boom! ${t.getMessage}")
//      }
//    } recover {
    //      case t =>
    //        InternalServerError(s"Boom! ${t.getMessage}")
    //    }

//    val response: Future[Seq[PaymentMethod]] = ask(mainActor, MainActorMessages.GetPaymentMethods(uuid)).mapTo[Seq[PaymentMethod]]
//
//    response map {
//      seq => Ok(Json.toJson(seq))
//    }

  }
}
