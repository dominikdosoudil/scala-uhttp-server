import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.Tcp.Register
import akka.io.{IO, Udp, UdpConnected}

import java.net.InetSocketAddress

object Main extends App {
  val system = ActorSystem.create("system");
  system.actorOf(Props(classOf[Server]), "tcp-listener")
}