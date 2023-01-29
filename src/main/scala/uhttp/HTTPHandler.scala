package uhttp

import uhttp.http.{Request, Response, Status}

class HTTPHandler extends Handler {
  override def handle(rq: Request): Response = {
    println(s"Handling ${rq.header.method} ${rq.header.url}")
    rq.header.url match
      case "/hello" => {
        Response.Ok("hello there")
      }
      case "/lorem" => {
        Response.Ok("Ipsum")
      }
      case _ => {
        println("rest operator")
        Response(Status.NotFound, "")
      }
  }
}
