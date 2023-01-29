package uhttp
import uhttp.http.{Method, Request, Response, Status}

object Chat extends Endpoint {
  override val prefix: String = "/chat"
  override val routes: Map[(Method, String), Request => Response] = Map(
    (Method.Post, "/messages") -> send,
    (Method.Get, "/messages") -> list
  )

  // just an example, the main goal is the http server itself, not endpoints
  var messages: List[String] = List()

  def send(request: Request): Response = {
    request.body match
      case None     => Response(Status.BadRequest, "Message not specified")
      case Some("") => Response(Status.BadRequest, "Message too short")
      case Some(msg) => {
        messages = msg :: messages
        Response.Ok()
      }
  }

  def list(request: Request): Response = {
    Response.Ok(messages.mkString("\n---------\n"))
  }
}
