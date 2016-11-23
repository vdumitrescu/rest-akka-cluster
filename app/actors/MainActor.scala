package actors

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.pattern.pipe
import akka.util.Timeout
import com.gilt.akk.cluster.api.test.v0.models.{Address, PaymentMethod}
import scaldi.akka.AkkaInjectable
import service.{AddressService, PaymentMethodService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object MainActorMessages {
  case class ResolveSession(uuid: UUID)
  case class GetPaymentMethods(uuid: UUID)
  case class GetShippingAddresses(uuid: UUID)
  case class FindActor(uUID: UUID)
}

class MainActor(paymentMethodService: PaymentMethodService, addressService: AddressService) extends Actor with AkkaInjectable with ActorLogging {

  implicit val timeout: Timeout = 10.seconds
  import MainActorMessages._

  log.info(s"Main actor starting on ${self.path}")

  override def receive: Receive = {

    case ResolveSession(uuid) =>
      log.warning(s"ResolveSession received for uuid $uuid!")
      findActor(uuid) map { actor =>
        log.warning("Actor found!")
        actor ! SessionActorMessages.ResolveSession
      } recover {
        case t =>
          log.warning("Actor not found!")
          val actor = createActor(uuid)

          log.warning("Sending SessionActorMessages.ResolveSession!")
          actor ! SessionActorMessages.ResolveSession(uuid)
          log.warning("Sent SessionActorMessages.ResolveSession!")
      }

    case FindActor(uuid) =>
      val caller = context.sender
      log.warning(s"FindActor received for uuid $uuid")
      findActor(uuid) andThen {
        case Success(actor) =>
          log.warning(s"Actor found, sending ${actor.path} back to ${caller.path}")
          caller ! actor

        case Failure(t) =>
          log.warning("Actor not found.")
          caller ! new RuntimeException(s"Actor not found for uuid $uuid")
      }
  }


  private[this] def findActor(uuid: UUID): Future[ActorRef] = {
    log.warning(s"Looking for actor for session $uuid...")
    context.system.actorSelection("/user/" + actorName(uuid)).resolveOne()
  }

  private[this] def createActor(uuid: UUID): ActorRef = {
    log.warning(s"Creating actor for session $uuid...")
    val actor = context.system.actorOf(Props(new SessionActor(paymentMethodService, addressService)), actorName(uuid))
    log.warning(s"Actor created for session $uuid.")
    actor
  }

  private[this] def actorName(uuid: UUID) = s"actor.$uuid"
}
