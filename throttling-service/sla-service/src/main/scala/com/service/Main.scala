package com.service

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import com.service.routes.Routes

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object Main {
  import com.service.configs.ServerProperties._

  def main(args: Array[String]): Unit = {

    val rootBehavior: Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
      val routes: Route = Routes(context, requestTimeout)
      startHttpServer(routes, context.system, host, port)
      Behaviors.empty
    }

    ActorSystem[Nothing](rootBehavior, "HttpServer")
  }

  def startHttpServer(routes: Route, system: ActorSystem[_], host: String, port: Int): Unit = {
    implicit val classicSystem: akka.actor.ActorSystem = system.toClassic
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val futureBinding: Future[ServerBinding] = Http().bindAndHandle(routes, host, port)

    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

}
