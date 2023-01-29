package uhttp

import akka.actor.{Actor, ActorRef, Props}
import akka.io.IO

import java.net.InetSocketAddress

class Server(using rqHandler: Handler) extends Actor {

  import akka.io.Tcp.*
  import context.system

  IO(akka.io.Tcp) ! Bind(self, new InetSocketAddress("localhost", 3333))

  def receive: Receive = {
    case b @ Bound(localAddress) =>
      context.parent ! b

    case CommandFailed(_: Bind) => context.stop(self)

    case Connected(remote, local) =>
      println("connected")
      val handler = createHandler
      val connection = sender()
      connection ! Register(handler)
  }

  def createHandler: ActorRef =
    context.actorOf(Props(classOf[TCPSocketHandler], rqHandler))
}
