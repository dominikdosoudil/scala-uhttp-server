package uhttp.http

import scala.compiletime.ops.any.ToString

enum Status(val value: Int, val name: String):
  case Ok extends Status(200, "OK")
  case BadRequest extends Status(400, "Bad Request")
  case NotFound extends Status(404, "Not Found")
  case InternalError extends Status(500, "Internal uhttp.Server Error")

case class Response(val status: Status, body: String) {
  override def toString: _root_.java.lang.String = {
    s"""${Version.Http1_1} ${status.value} ${status.name}
      |Content-Type: text/plain
      |Content-Length: ${body.length}
      |
      |$body""".stripMargin
  }
}

object Response {
  // New needs to be used so it does not loop indefinitely
  def apply(status: Status, body: String): Response = new Response(status, body)
  def Ok(): Response = new Response(Status.Ok, "")
  def Ok(body: String): Response = new Response(Status.Ok, body)
  def Error(msg: String): Response = new Response(Status.InternalError, msg)
}
