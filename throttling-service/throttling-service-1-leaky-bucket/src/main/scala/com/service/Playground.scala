package com.service

import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.model.headers.{Authorization, GenericHttpCredentials, OAuth2BearerToken}
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer, SystemMaterializer}
import spray.json._

object Playground extends App {

//      implicit val classicSystem: akka.actor.ActorSystem = context.system.toClassic
//      implicit val ec: ExecutionContext = context.system.executionContext
//
//      val request = HttpRequest(uri = "http://127.0.0.1:8081/", headers = Seq(Authorization(OAuth2BearerToken("token"))))
//      val response = Http().singleRequest(request)
//        .map(response => response.entity.withContentType(ContentTypes.`application/json`))
//        .flatMap(entity => Unmarshal(entity).to[String])
//        .map(_.parseJson.convertTo[Sla])
//
//      response.onComplete {
//        case Success(value) =>
//          println(value)
////          println(value.user)
////          println(value.rps)
//        case Failure(error) =>
//          println(error)
//      }


//    val httpRequest = HttpRequest(uri = "http://127.0.0.1:8081/", headers = Seq(Authorization(GenericHttpCredentials("", token.get))))
//    val httpResponse = Http()(system.toClassic).singleRequest(httpRequest)
//      .map(_.entity.withContentType(ContentTypes.`application/json`))
//      .flatMap(entity => Unmarshal(entity).to[String])
//      .map(_.parseJson.convertTo[Sla])

//    httpResponse.onComplete {
//    onComplete(httpResponse) {
//      case Success(value) =>
//        println(value)
//        println(value.user)
//        println(value.rps)
//        complete(StatusCodes.OK, value)
//      case Failure(error) =>
//        println(error)
//        complete(StatusCodes.BadRequest)
//    }

}
