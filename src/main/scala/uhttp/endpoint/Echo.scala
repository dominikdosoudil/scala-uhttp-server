package uhttp.endpoint

import uhttp.Endpoint
import uhttp.http.{Request, Response}

class Echo extends Endpoint {
  override val routes: Map[String, Request => Response] = Map(
    "/echo" -> echo
  )

  def echo(request: Request): Response = {
    Response.Ok(request.body.getOrElse(""))
  }
}
