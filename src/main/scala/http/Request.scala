package http

import scala.util.parsing.combinator.*

enum Method:
  case Get, Post, Put, Patch, Delete, Options, Head

enum Version:
  case Http1_1

enum Protocol:
  case Http

type Path = String
type Host = String
type Port = Int
type RequestHeaders = Map[String, String]

case class URI(protocol: Protocol, host: Host, port: Port, path: Path)

case class Header(
    method: Method,
    url: URI | Path,
    version: Version,
    headers: RequestHeaders
)

object RequestParser extends RegexParsers {
  override protected def handleWhiteSpace(
      source: CharSequence,
      offset: Int
  ): Int = {
    val subSequence = source.subSequence(offset, source.length())

    if ("""[^\S\r\n]*\n{2,}[\S\s]*""".r.matches(subSequence)) {
      return """[^\S\r\n]*""".r.findPrefixMatchOf(subSequence) match {
        case Some(matched) => offset + matched.end
        case None          => offset
      }
    }
    super.handleWhiteSpace(source, offset)
  }
  lazy val protocol: Parser[Protocol] =
    """^http""".r ^^ { case "http" =>
      Protocol.Http
    }

  lazy val host: Parser[Host] = """[A-Za-z0-9-.]+""".r

  lazy val port: Parser[Port] =
    """\d+""".r.^?(
      _.toIntOption match {
        case Some(v) if v >= 0 && v <= 65535 => v
      },
      _ => "port number not in range 0-65535"
    )

  lazy val path: Parser[Path] = """[a-zA-Z0-9/]*""".r

  lazy val optionalPort: Parser[Option[Port]] =
    ((""":""".r ~> port) | "") ^^ {
      case x: Int => Some(x)
      case _      => None
    }

  lazy val uri: Parser[URI | Path] =
    opt(protocol ~ ("://" ~> host) ~ optionalPort) ~ path ^^ {
      case None ~ path => path
      case Some(proto ~ host ~ port) ~ path =>
        URI(
          proto,
          host,
          port.getOrElse(80),
          path
        )
    }

  lazy val method: Parser[Method] =
    """^(GET|POST|PUT|PATCH|DELETE|OPTIONS|HEAD)""".r ^^ {
      case "GET"     => Method.Get
      case "POST"    => Method.Post
      case "PUT"     => Method.Put
      case "PATCH"   => Method.Patch
      case "DELETE"  => Method.Delete
      case "OPTIONS" => Method.Options
      case "HEAD"    => Method.Head
    }

  lazy val headerKey: Parser[String] = """[a-zA-Z\-]+""".r

  lazy val headerVal: Parser[String] = """.+""".r

  lazy val requestHeaders: Parser[RequestHeaders] =
    rep((headerKey <~ ":") ~ headerVal) ^^ { a =>
      a.map { case k ~ v =>
        k.toLowerCase() -> v
      }.toMap
    }

  lazy val version: Parser[Version] = """HTTP/1\.1""".r ^^ { _ =>
    Version.Http1_1
  }

  lazy val header: Parser[Header] =
    method ~ uri ~ version ~ requestHeaders ^^ { case method ~ url ~ v ~ rqHs =>
      Header(method, url, v, rqHs)
    }

  lazy val bodySep: Parser[String] = """[^\S\r\n]*\n{2,}""".r

  lazy val request: Parser[Request] =
    header ~ opt(bodySep ~> """[\s\S]*""".r) ^^ { case header ~ maybeBody =>
      Request(header, maybeBody)
    }
}

case class Request(header: Header, body: Option[String])

object Main extends App {
  println(
    RequestParser.parseAll(
      RequestParser.request,
      """POST /foo/bar HTTP/1.1
        |Accept: text/plain
        |User-Agent: PostmanRuntime/7.29.2
        |
        |
        |
        |body body
        |ssd
        |f
        |sdfjhsdkjf
        |
        |
        |sdfjhs dfkujh
        |""".stripMargin
    )
  )
}
