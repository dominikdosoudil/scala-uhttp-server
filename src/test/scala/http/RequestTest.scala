package http

import org.scalatest.flatspec.AnyFlatSpec

class RequestTest extends AnyFlatSpec {

  it should "parse basic url" in {
    assert(
      RequestParser.parse(RequestParser.uri, "http://localhost").get == URI(
        Protocol.Http,
        "localhost",
        80,
        ""
      )
    )
  }
  it should "parse basic url with path" in {
    assert(
      RequestParser
        .parse(RequestParser.uri, "http://localhost/hello/world/")
        .get == URI(
        Protocol.Http,
        "localhost",
        80,
        "/hello/world/"
      )
    )
  }

  it should "parse basic url with port" in {
    assert(
      RequestParser
        .parse(RequestParser.uri, "http://localhost:8080")
        .get == URI(
        Protocol.Http,
        "localhost",
        8080,
        ""
      )
    )
  }

  it should "parse complex url with port and path" in {
    assert(
      RequestParser
        .parse(RequestParser.uri, "http://foo.example.ex--com:8080/hello/world")
        .get == URI(
        Protocol.Http,
        "foo.example.ex--com",
        8080,
        "/hello/world"
      )
    )
  }

  it should "parse whole request" in {
    assert(
      RequestParser
        .parseAll(
          RequestParser.request,
          "   POST http://localhost:8080/foo/bar Accept: \"text/plain\" \nUser-Agent: \"PostmanRuntime/7.29.2\" \n\nbody body".stripMargin
        )
        .get
        ==
          Request(
            Header(
              Method.Post,
              URI(Protocol.Http, "localhost", 8080, "/foo/bar"),
              Map(
                "accept" -> "text/plain",
                "user-agent" -> "PostmanRuntime/7.29.2"
              )
            ),
            Some("body body")
          )
    )
  }

  it should "parse request without body" in {
    assert(
      RequestParser
        .parseAll(
          RequestParser.request,
          "   GET http://localhost:8080/foo/bar Accept: \"text/plain\" \nUser-Agent: \"PostmanRuntime/7.29.2\"".stripMargin
        )
        .get
        ==
          Request(
            Header(
              Method.Get,
              URI(Protocol.Http, "localhost", 8080, "/foo/bar"),
              Map(
                "accept" -> "text/plain",
                "user-agent" -> "PostmanRuntime/7.29.2"
              )
            ),
            None
          )
    )
  }
}
