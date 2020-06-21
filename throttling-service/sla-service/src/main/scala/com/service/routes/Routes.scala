package com.service.routes

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.actor.typed.scaladsl.ActorContext
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{authenticateOAuth2, complete, get, onComplete, pathEndOrSingleSlash}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import akka.util.Timeout
import com.service.actors.SlaActor
import com.service.actors.SlaActor.{Command, CommandStatus, GetSla, GetSlaFailure, GetSlaSuccess}
import com.service.models.marshalling.ModelMarshalling

import scala.concurrent.Future
import scala.util.{Failure, Success}

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

  private val responseTimeInMillis = 250 - 15

  def routes: Route = getSlaRoute

  private def getSlaRoute: Route = pathEndOrSingleSlash {
    get {
      authenticateOAuth2(realm = "bearer token", tokenAuthenticator) { token =>
        val timeInMillis: Long = System.currentTimeMillis()

        onComplete(getSla(token)) {
          case Success(GetSlaSuccess(sla)) =>
            // Simulate latency
            val waitingTimeInMillis: Long = System.currentTimeMillis() - timeInMillis
            val timeToSleep = if (waitingTimeInMillis >= responseTimeInMillis) 0 else responseTimeInMillis - waitingTimeInMillis
            Thread.sleep(timeToSleep)

            system.log.info(s"Success: ${token}")
            complete(StatusCodes.OK, sla)

          case Success(GetSlaFailure(message)) =>
            // Simulate latency
            val waitingTimeInMillis: Long = System.currentTimeMillis() - timeInMillis
            val timeToSleep = if (waitingTimeInMillis >= responseTimeInMillis) 0 else responseTimeInMillis - waitingTimeInMillis
            Thread.sleep(timeToSleep)

            system.log.error(s"Failure: ${message}")
            complete(StatusCodes.Unauthorized, message)

          case Failure(e) =>
            system.log.error(s"Failure: ${e.getMessage}")
            complete(StatusCodes.InternalServerError, e.getMessage)
        }
      }
    }
  }

  def tokenAuthenticator(credentials: Credentials): Option[String] =
    credentials match {
      case Credentials.Provided(token) => Some(token)
      case _ => None
    }

  private def getSla(token: String): Future[CommandStatus] =
    slaActor.ask(GetSla(token, _: ActorRef[CommandStatus]))

}
