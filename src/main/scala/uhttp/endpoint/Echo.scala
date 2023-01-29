package uhttp.endpoint

import uhttp.Endpoint
import uhttp.http.{Request, Response, Method}

object Echo extends Endpoint {
  override val routes
      : Map[(Method, String), (Request, Map[String, String]) => Response] = Map(
    (Method.Get, "/echo") -> echo
  )

  def echo(request: Request, vars: Map[String, String]): Response = {
    Response.Ok(request.body.getOrElse(""))
  }
}
