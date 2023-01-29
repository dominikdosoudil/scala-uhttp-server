package uhttp

import uhttp.http.{Request, Response, Status, Method}
import uhttp.Endpoint
import uhttp.HTTPHandler.{endpoints, flatEndpoints}
import uhttp.endpoint.{Echo, Lorem}
import http.{Path, URI}
import scala.util.parsing.combinator.*

class HTTPHandler extends Handler {
  override def handle(rq: Request): Response = {
    println(s"Handling ${rq.header.method} ${rq.header.url}")

    val p = (rq.header.url match
      case p: Path         => p
      case URI(_, _, _, p) => p
    )
      .stripPrefix("/")
      .stripSuffix("/")
      .split("/")
      .toList

    flatEndpoints
      .filter({ case ((method, _), _) => method == rq.header.method })
      .map({ case ((method, endpointPath), handler) =>
        ((method, HTTPHandler.pathMatch(p, endpointPath, (0, Map()))), handler)
      })
      .sortBy({
        case (
              (method: Method, pathMatch: (Int, Map[String, String])),
              handler
            ) =>
          pathMatch._1
      })
      .findLast({
        case (
              (method: Method, pathMatch: (Int, Map[String, String])),
              handler
            ) =>
          pathMatch._1 > 0
      }) match {
      case Some(((_, (_, variables)), handler)) => handler(rq, variables)
      case None               => Response(Status.NotFound, "Path not found")
    }
  }
}

enum PathToken(foo: String):
  case PathSegment(string: String) extends PathToken(string)
  case PathVar(variable: String) extends PathToken(variable)

object EndpointPathParsers extends RegexParsers {
  lazy val variableParser: Parser[PathToken.PathVar] =
    "{" ~> """[a-zA-Z0-9]+""".r <~ "}" ^^ { PathToken.PathVar.apply }
  lazy val segmentParser: Parser[PathToken.PathSegment] =
    """[a-zA-Z0-9]+""".r ^^ {
      PathToken.PathSegment.apply
    }

  lazy val pathParser: Parser[List[PathToken]] =
    repsep(segmentParser | variableParser, "/")

}
object HTTPHandler {

  def pathMatch(
      requestPath: List[String],
      endpointPath: List[PathToken],
      acc: (Int, Map[String, String])
  ): (Int, Map[String, String]) = {
    endpointPath match
      case List() => acc
      case endpointPathToken :: restEndpointPath => {
        requestPath match
          case requestPathSegment :: restRequestPath => {
            endpointPathToken match
              case PathToken.PathVar(key) =>
                pathMatch(
                  restRequestPath,
                  restEndpointPath,
                  (acc._1 + 1, acc._2 + (key -> requestPathSegment))
                )
              case PathToken.PathSegment(endpointPathSegment)
                  if endpointPathSegment == requestPathSegment => {
                pathMatch(
                  restRequestPath,
                  restEndpointPath,
                  (acc._1 + 1, acc._2)
                )
              }
              case _ => (0, Map())
          }
          case List() => (0, Map())
      }
  }

  val endpoints: List[Endpoint] = List(Echo, Lorem, Chat)

  lazy val flatEndpoints
      : List[((Method, List[PathToken]), (Request, Map[String, String]) => Response)] =
    endpoints.flatMap(endpoint =>
      endpoint.routes
        .map({ case ((method, k), v) =>
          (
            method,
            EndpointPathParsers.parseAll(
              EndpointPathParsers.pathParser,
              s"${endpoint.prefix}$k".stripPrefix("/").stripSuffix("/")
            )
          ) -> v
        })
        .filter({
          case ((_, EndpointPathParsers.Success(_)), _) => true
          case _                                        => false
        })
        .map({ case ((method, pathParseResult), handler) =>
          (method, pathParseResult.get) -> handler
        })
        .toList
    )
}
