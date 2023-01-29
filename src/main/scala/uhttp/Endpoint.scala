package uhttp

import uhttp.http.{Request, Response, Method}

trait Endpoint {
  val prefix: String = ""

  val routes: Map[(Method, String), (Request, Map[String, String]) => Response]
}
