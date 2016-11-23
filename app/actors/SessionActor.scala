package actors

import java.util.UUID

import akka.actor.{ActorLogging, Actor}
import com.gilt.akk.cluster.api.test.v0.models.{Address, PaymentMethod}
import scaldi.akka.AkkaInjectable
import service.{AddressService, PaymentMethodService}

import scala.concurrent.{ExecutionContext, Future}

object SessionActorMessages {
  case class ResolveSession(uuid: UUID)
  case class GetPaymentMethods(uuid: UUID)
  case class GetShippingAddresses(uuid: UUID)
}

class SessionActor(paymentMethodService: PaymentMethodService, shippingAddressService: AddressService) extends Actor with AkkaInjectable with ActorLogging {

  var paymentMethods: Option[Future[Seq[PaymentMethod]]] = None
  var shippingAddresses: Option[Future[Seq[Address]]] = None

  log.info(s"Session actor starting on ${self.path}")

  implicit val ec: ExecutionContext = context.dispatcher

  import SessionActorMessages._

  override def receive: Receive = {

    case ResolveSession(uuid) =>
      log.warning(s"Received SessionActorMessages.ResolveSession for $uuid")
      paymentMethods = Some(paymentMethodService.getAll())
      shippingAddresses = Some(shippingAddressService.getAll())

    case GetPaymentMethods(uuid) =>
      val caller = context.sender
      log.warning(s"Received SessionActorMessages.GetPaymentMethods for $uuid")
      paymentMethods match {
        case Some(resolvedPaymentMethods) =>
          log.warning("Served payment methods from actor result")
          log.warning(s"Sending response back to ${caller.path}")
          caller ! resolvedPaymentMethods

        case None =>
          log.warning("Getting payment methods")
          paymentMethods = Some(paymentMethodService.getAll())
          caller ! paymentMethods.get
      }

    case GetShippingAddresses(uuid) =>
      val caller = context.sender
      log.warning(s"Received SessionActorMessages.GetShippingAddresses for $uuid")
      shippingAddresses match {
        case Some(resolvedShippingAddresses) =>
          log.warning("Served addresses from actor result")
          caller ! resolvedShippingAddresses

        case None =>
          log.warning("Getting addresses")
          shippingAddresses = Some(shippingAddressService.getAll())
          caller ! shippingAddresses.get
      }
  }
}
