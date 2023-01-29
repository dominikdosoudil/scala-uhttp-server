package uhttp.endpoint

import uhttp.Endpoint
import uhttp.http.{Request, Response, Method}

object Lorem extends Endpoint {
  override val prefix: String = "/lorem"

  override val routes: Map[(Method, String), Request => Response] = Map(
    (Method.Get, "/ipsum") -> ipsum,
    (Method.Get, "/cake") -> cake
  )

  def ipsum(rq: Request): Response = Response.Ok("Lorem ispum dolor sit amet")

  def cake(rq: Request): Response = Response.Ok("Cake ipsum dolor muffin")
}
