package uhttp.endpoint

import uhttp.Endpoint
import uhttp.http.{Request, Response, Method}

object Lorem extends Endpoint {
  override val prefix: String = "/lorem"

  override val routes
      : Map[(Method, String), (Request, Map[String, String]) => Response] = Map(
    (Method.Get, "") -> cake,
    (Method.Get, "/ipsum") -> ipsum,
    (Method.Get, "/cake") -> cake,
    (Method.Get, "/custom/{custom}") -> custom
  )

  def ipsum(rq: Request, vars: Map[String, String]): Response =
    Response.Ok("Lorem ispum dolor sit amet")

  def cake(rq: Request, vars: Map[String, String]): Response =
    Response.Ok("Cake ipsum dolor muffin")

  def custom(rq: Request, vars: Map[String, String]): Response =
    Response.Ok(
      s"${vars.getOrElse("custom", "-var not found-")} ipsum dolor muffin"
    )
}
