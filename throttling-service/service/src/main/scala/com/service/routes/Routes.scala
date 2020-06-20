package com.service.routes

import akka.http.scaladsl.server.Directives.{complete, extractRequest}
import akka.http.scaladsl.server.Route
import com.service.models.marshalling.ModelMarshalling

object Routes {
  def apply(): Route = new Routes().routes
}

class Routes extends ModelMarshalling {

  def routes: Route = extractRequest { request =>
    complete(
      s"""
        |Request method is: ${request.method}
        |
        |Request uri is: ${request.uri}
        |
        |Request headers is: ${request.headers}
        |
        |Request entity is: ${request.entity}
        |
        |Request protocol is: ${request.protocol}
      |""".stripMargin
    )
  }

}
