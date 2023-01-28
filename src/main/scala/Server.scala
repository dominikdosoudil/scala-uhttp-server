import akka.actor.{Actor, Props}
import akka.io.IO

import java.net.InetSocketAddress

class Server extends Actor {

  import akka.io.Tcp._
  import context.system

  IO(akka.io.Tcp) ! Bind(self, new InetSocketAddress("localhost", 3333))

  def receive: Receive = {
    case b @ Bound(localAddress) =>
      context.parent ! b

    case CommandFailed(_: Bind) => context.stop(self)

    case Connected(remote, local) =>
      val handler = context.actorOf(Props[TCPSocketHandler]())
      val connection = sender()
      connection ! Register(handler)
  }

}
