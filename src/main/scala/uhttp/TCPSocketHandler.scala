package uhttp

import akka.actor.{Actor, ActorRef}
import akka.io.Udp
import akka.util.ByteString
import uhttp.http.{RequestParser, Response}

import java.net.InetSocketAddress

class TCPSocketHandler(rqHandler: Handler) extends Actor {
  import akka.io.Tcp.*

  def receive: Receive = {
    case Received(data) => {
//      println(data.decodeString("utf-8"))

      sender() ! Write(
        ByteString.fromString(process(data.decodeString("utf-8")))
      )
      sender() ! Close
    }
    case PeerClosed => context.stop(self)
  }

  def process(data: String): String = {
    val response: Response = RequestParser.parseAll(
      RequestParser.request,
      data
    ) match
      case RequestParser.Success(rq) => HTTPHandler().handle(rq)
      case RequestParser.Failure(msg) => {
        println(msg)
        Response.Error(msg)
      }
      case RequestParser.Error(msg) => {
        println(msg)
        Response.Error("")
      }
    println(response.toString)
    response.toString
  }
}
