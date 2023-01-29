import http.{Request, Response}

trait Handler {
  def handle(rq: Request): Response
}
