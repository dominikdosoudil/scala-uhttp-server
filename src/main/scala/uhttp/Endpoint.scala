package uhttp

import uhttp.http.{Request, Response}

trait Endpoint {
  val prefix: String = ""

  val routes: Map[String, Request => Response]
}
