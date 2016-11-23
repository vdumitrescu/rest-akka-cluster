package actors

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import scaldi.Module
import scaldi.akka.AkkaInjectable
import service.{AddressService, PaymentMethodService, SessionService}

class ActorsModule extends Module with AkkaInjectable {

  bind[AddressService] toProvider new AddressService()
  bind[PaymentMethodService] toProvider new PaymentMethodService()
  bind[MainActor] to new MainActor(inject[PaymentMethodService], inject[AddressService])

  bind[ActorSystem] to {
    val actorSystem: ActorSystem = ActorSystem("rest-akka-cluster", new AkkaConfig(None).config)

    // TODO: add cluster initialization here

    // create the main actor
    actorSystem.actorOf(
      ClusterSingletonManager.props(
        singletonProps = Props(inject[MainActor]),
        terminationMessage = PoisonPill,
        settings = ClusterSingletonManagerSettings(actorSystem).withSingletonName("MainActor")
      ),
      name = "singleton"
    )

    actorSystem
  } destroyWith(_.terminate())

  binding identifiedBy "MainActorProxy" to {
    val system = inject[ActorSystem]
    system.actorOf(
      ClusterSingletonProxy.props(
        singletonManagerPath = "/user/singleton",
        settings = ClusterSingletonProxySettings(system).withSingletonName("MainActor")
      ),
      name = "main-proxy"
    )
  }

  bind[SessionService] toProvider new SessionService(inject[ActorRef]("MainActorProxy"))
}
