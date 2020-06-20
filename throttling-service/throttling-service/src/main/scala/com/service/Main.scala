package com.service

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, ResponseEntity}
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import com.service.models.Sla
import com.service.models.marshalling.ModelMarshalling
import com.service.routes.Routes

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import spray.json._

object Main extends ModelMarshalling {
  import com.service.configs.ServerProperties._

  def main(args: Array[String]): Unit = {

    val rootBehavior: Behavior[Nothing] = Behaviors.setup[Nothing] { context =>

//      implicit val classicSystem: akka.actor.ActorSystem = context.system.toClassic
//      implicit val ec: ExecutionContext = context.executionContext

//      val request = HttpRequest(uri = "http://127.0.0.1:8081/", headers = Seq(Authorization(OAuth2BearerToken("token"))))
//      val response = Http().singleRequest(request)
//        .map(response => response.entity.withContentType(ContentTypes.`application/json`))
//        .flatMap(entity => Unmarshal(entity).to[String])
//        .map(_.parseJson.convertTo[Sla])

//      response.onComplete {
//        case Success(value) =>
//          println(value)
////          println(value.user)
////          println(value.rps)
//        case Failure(error) =>
//          println(error)
//      }


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
