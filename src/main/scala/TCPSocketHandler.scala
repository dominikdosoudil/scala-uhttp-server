import akka.actor.{Actor, ActorRef}
import akka.io.Udp
import akka.util.ByteString

import java.net.InetSocketAddress

class TCPSocketHandler extends Actor {
  import akka.io.Tcp._
  def receive: Receive = {
    case Received(data) => {
      println(data.decodeString("utf-8"));
      sender() ! Write(data)
    }
    case PeerClosed => context.stop(self)
  }
}
