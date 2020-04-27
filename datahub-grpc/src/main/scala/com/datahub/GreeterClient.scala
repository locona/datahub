package com.datahub

//#import
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Success}

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

//#import

//#client-request-reply
object GreeterClient {

  def main(args: Array[String]): Unit = {
    implicit val sys = ActorSystem("HelloWorldClient")
    implicit val mat = ActorMaterializer()
    implicit val ec  = sys.dispatcher

    val client = GreeterServiceClient(
      GrpcClientSettings.fromConfig("datahub.GreeterService")
    )

    val names =
      if (args.isEmpty) List("Alice", "Bob")
      else args.toList

    names.foreach(singleRequestReply)

    //#client-request-reply
    if (args.nonEmpty)
      names.foreach(streamingBroadcast)
    //#client-request-reply

    def singleRequestReply(name: String): Unit = {
      println(s"Performing request: $name")
      val reply = client.sayHello(HelloRequest(name))
      reply.onComplete {
        case Success(msg) =>
          println(msg)
        case Failure(e) =>
          println(s"Error: $e")
      }
    }

    //#client-request-reply
    //#client-stream
    def streamingBroadcast(name: String): Unit = {
      println(s"Performing streaming requests: $name")

      val requestStream: Source[HelloRequest, NotUsed] =
        Source
          .tick(1.second, 1.second, "tick")
          .zipWithIndex
          .map { case (_, i) => i }
          .map(i => HelloRequest(s"$name-$i"))
          .mapMaterializedValue(_ => NotUsed)

      val responseStream: Source[HelloReply, NotUsed] =
        client.sayHelloToAll(requestStream)
      val done: Future[Done] =
        responseStream.runForeach(reply =>
          println(s"$name got streaming reply: ${reply.message}")
        )

      done.onComplete {
        case Success(_) =>
          println("streamingBroadcast done")
        case Failure(e) =>
          println(s"Error streamingBroadcast: $e")
      }
    }
    //#client-stream
    //#client-request-reply

  }

}
//#client-request-reply
