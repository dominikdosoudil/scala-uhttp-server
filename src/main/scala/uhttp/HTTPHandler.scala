package uhttp

import uhttp.http.{Request, Response, Status, Method}
import uhttp.Endpoint
import uhttp.HTTPHandler.{endpoints, flatEndpoints}
import uhttp.endpoint.{Echo, Lorem}
import http.{Path, URI}

class HTTPHandler extends Handler {
  override def handle(rq: Request): Response = {
    println(s"Handling ${rq.header.method} ${rq.header.url}")

    flatEndpoints
      .filter({ case ((method, _), _) => method == rq.header.method })
      .find({
        case ((_, path), _) => {
          println(path)
          rq.header.url match {
            case URI(_, _, _, requestPath) => requestPath.startsWith(path)
            case requestPath: Path         => requestPath.startsWith(path)
          }
        }
      }) match {
      case Some((_, handler)) => handler(rq)
      case None               => Response(Status.NotFound, "Path not found")
    }
  }
}
object HTTPHandler {
  val endpoints: List[Endpoint] = List(Echo, Lorem)

  lazy val flatEndpoints: List[((Method, String), Request => Response)] =
    endpoints.flatMap(endpoint =>
      endpoint.routes
        .map({ case ((method, k), v) =>
          (method, s"${endpoint.prefix}$k") -> v
        })
        .toList
    )
}
