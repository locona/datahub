package com.datahub

//#import
import scala.concurrent.Future

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Keep, MergeHub, Sink, Source}

//#import

//#service-request-reply
//#service-stream
class GreeterServiceImpl(materializer: Materializer) extends GreeterService {
  import materializer.executionContext
  implicit private val mat: Materializer = materializer

  //#service-request-reply
  val (
    inboundHub: Sink[HelloRequest, NotUsed],
    outboundHub: Source[HelloReply, NotUsed]
  ) =
    MergeHub
      .source[HelloRequest]
      .map(request => HelloReply(s"Hello, ${request.name}"))
      .toMat(BroadcastHub.sink[HelloReply])(Keep.both)
      .run()
  //#service-request-reply

  override def sayHello(request: HelloRequest): Future[HelloReply] = {
    Future.successful(HelloReply(s"Hello, ${request.name}"))
  }

  //#service-request-reply
  override def sayHelloToAll(
      in: Source[HelloRequest, NotUsed]
  ): Source[HelloReply, NotUsed] = {
    in.runWith(inboundHub)
    outboundHub
  }
  //#service-request-reply
}
//#service-stream
//#service-request-reply
