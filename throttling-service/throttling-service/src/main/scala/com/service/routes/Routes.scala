package com.service.routes

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.actor.typed.scaladsl.ActorContext
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpHeader, HttpMethods, HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, extractRequest}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import akka.util.Timeout
import com.service.actors.SlaActor
import com.service.actors.SlaActor.{Command, CommandStatus, GetSla, GetSlaFailure, GetSlaSuccess}
import com.service.models.marshalling.ModelMarshalling

object Routes {
  def apply(context: ActorContext[Nothing], timeout: Timeout): Route =
    new Routes(createSlaActor(context))(context.system, timeout).routes

  private def createSlaActor(context: ActorContext[Nothing]): ActorRef[Command] = {
    val actor = context.spawn(SlaActor(), SlaActor.name)
    context.watch(actor)
    actor
  }
}

class Routes(slaActor: ActorRef[Command])
            (implicit val system: ActorSystem[_], implicit val requestTimeout: Timeout) extends ModelMarshalling {

  def routes: Route = extractRequest { request =>
    val token = request.headers(1)
    val token2 = request.headers.find(h => h.is("authorization"))

    complete(s"Request method is ${request.method.name} and content-type is ${request.entity.contentType}")
  }


//      import akka.actor.typed.scaladsl.adapter._
//
//      implicit val ec = system.executionContext
//      implicit val classicSystem: akka.actor.ActorSystem = system.toClassic
//
//
//      implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()
//      import akka.http.scaladsl.unmarshalling._

//      import akka.actor.typed.scaladsl.adapter._
      val request = HttpRequest(uri = "http://127.0.0.1:8081/")
//      val request = HttpRequest(uri = "http://127.0.0.1:8081/", headers = Seq(Authorization(OAuth2BearerToken("token"))))
//      val response = HttpResponse()
//      val response = Http()(system.toClassic).singleRequest(request)
//      Unmarshal(response).to[Source[Sla, NotUsed]]
//      Unmarshal(response).to[Sla]

//      response.onComplete {
//          case Success(value) => println(value)
//          case Failure(exception) => println(exception)
//        }
//
//      Thread.sleep(3000)

  def tokenAuthenticatorOption(credentials: Credentials): Option[Option[String]] =
    credentials match {
      case Credentials.Provided(token) => Some(Some(token))
      case _ => Some(None)
    }

}
