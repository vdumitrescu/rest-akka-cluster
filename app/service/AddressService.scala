package service

import java.util.UUID

import com.gilt.akk.cluster.api.test.v0.models.Address

import scala.concurrent.{ExecutionContext, Future}

class AddressService {

  def getAll()(implicit ec: ExecutionContext) : Future[Seq[Address]] = {
    //simulate remote call
    Future {
      Thread.sleep(1000)
      Seq.fill(2)(Address(UUID.randomUUID(), ""))
    }
  }
}
