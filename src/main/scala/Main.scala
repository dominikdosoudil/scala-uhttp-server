import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.Tcp.Register
import akka.io.{IO, Udp, UdpConnected}
import uhttp.{Handler, Server}

import java.net.InetSocketAddress
import uhttp.HTTPHandler

object Main extends App {
  val system = ActorSystem.create("system")

  val rqHandler = HTTPHandler()
  given Handler = rqHandler

  system.actorOf(Props(classOf[Server], rqHandler), "tcp-listener")
}
