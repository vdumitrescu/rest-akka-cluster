package service

import java.util.UUID

import com.gilt.akk.cluster.api.test.v0.models.PaymentMethod

import scala.concurrent.{ExecutionContext, Future}


class PaymentMethodService {

  def getAll()(implicit ec: ExecutionContext) : Future[Seq[PaymentMethod]] = {
    //simulate remote call
    Future {
      Thread.sleep(15000)
      Seq.fill(3)(PaymentMethod(UUID.randomUUID(), "test"))
    }
  }
}
