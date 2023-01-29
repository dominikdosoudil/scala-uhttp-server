package uhttp.http

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
          "   POST http://localhost:8080/foo/bar HTTP/1.1\nAccept: text/plain\nUser-Agent: PostmanRuntime/7.29.2\n\nbody body".stripMargin
        )
        .get
        ==
          Request(
            Header(
              Method.Post,
              URI(Protocol.Http, "localhost", 8080, "/foo/bar"),
              Version.Http1_1,
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
          "   GET http://localhost:8080/foo/bar HTTP/1.1\nAccept: text/plain\nUser-Agent: PostmanRuntime/7.29.2".stripMargin
        )
        .get
        ==
          Request(
            Header(
              Method.Get,
              URI(Protocol.Http, "localhost", 8080, "/foo/bar"),
              Version.Http1_1,
              Map(
                "accept" -> "text/plain",
                "user-agent" -> "PostmanRuntime/7.29.2"
              )
            ),
            None
          )
    )
  }

  it should "parse a lot of body" in {
    assert(
      RequestParser
        .parseAll(
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
        .get
        == Request(
          Header(
            Method.Post,
            "/foo/bar",
            Version.Http1_1,
            Map(
              "accept" -> "text/plain",
              "user-agent" -> "PostmanRuntime/7.29.2"
            )
          ),
          Some("""body body
                 |ssd
                 |f
                 |sdfjhsdkjf 
                 |
                 |
                 |sdfjhs dfkujh
                 |""".stripMargin)
        )
    )
  }

  it should "parse a lot of body 2" in {
    assert(
      RequestParser
        .parseAll(
          RequestParser.request,
          """POST / HTTP/1.1
            |Content-Type: text/plain
            |Accept: */*
            |Postman-Token: d1e4a893-7edb-4da5-bf41-17c84a1507fb
            |Host: localhost:3333
            |Content-Length: 28
            |
            |Lorem ipsum dol
            |or si t
            |amet
            |""".stripMargin
        )
        .get
        == Request(
          Header(
            Method.Post,
            "/",
            Version.Http1_1,
            Map(
              "accept" -> "text/plain",
              "content-type" -> "text/plain",
              "accept" -> "*/*",
              "postman-token" -> "d1e4a893-7edb-4da5-bf41-17c84a1507fb",
              "host" -> "localhost:3333",
              "content-length" -> "28"
            )
          ),
          Some("""Lorem ipsum dol
                 |or si t
                 |amet
                 |""".stripMargin)
        )
    )
  }
}
