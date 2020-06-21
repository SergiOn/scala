package com.service.routes

import akka.actor.typed.scaladsl.ActorContext
import akka.http.scaladsl.server.Directives.{complete, extractRequest}
import akka.http.scaladsl.server.Route
import com.service.models.marshalling.ModelMarshalling

import scala.concurrent.duration._

object Routes {
  def apply(context: ActorContext[Nothing]): Route = new Routes(context).routes
}

class Routes(context: ActorContext[Nothing]) extends ModelMarshalling {

  def routes: Route = extractRequest { request =>
    val response = s"""
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

    Thread.sleep(8.seconds.toMillis)

    context.system.log.info(response)
    complete(response)
  }
}
