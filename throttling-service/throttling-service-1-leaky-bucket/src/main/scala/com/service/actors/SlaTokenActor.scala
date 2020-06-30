package com.service.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors, StashBuffer}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.{Authorization, GenericHttpCredentials}
import akka.http.scaladsl.model.{ContentTypes, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{Materializer, SystemMaterializer}
import com.service.actors.SlaTokenActor.Command
import com.service.models.Sla
import com.service.models.marshalling.ModelMarshalling
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object SlaTokenActor {
  def name(name: String): String = s"SlaTokenActor${name}"
  def apply(): Behavior[Command] = Behaviors.withStash(Int.MaxValue)(buffer =>
    Behaviors.setup(context =>
      new SlaTokenActor(context, buffer).start()))

  sealed trait Command
  final case class GetSla(authorizationHeader: String, replyTo: ActorRef[CommandStatus]) extends Command
  final case class GetSlaResponseSuccess(sla: Sla) extends Command
  final case class GetSlaResponseFailure(message: String = "User not found") extends Command

  sealed trait CommandStatus
  final case class GetSlaSuccess(sla: Sla) extends CommandStatus
  final case class GetSlaFailure(message: String = "User not found") extends CommandStatus
}

class SlaTokenActor(context: ActorContext[Command], buffer: StashBuffer[Command]) extends ModelMarshalling {

  import SlaTokenActor._
  implicit val executionContext: ExecutionContext = context.executionContext

  def start(): Behavior[Command] = Behaviors.receiveMessage {
    case GetSla(authorizationHeader, replyTo) =>
      context.pipeToSelf(getSla(authorizationHeader)) {
        case Success(sla) =>
          context.system.log.error("SlaTokenActor | Success: {}", sla)
          GetSlaResponseSuccess(sla)
        case Failure(e) =>
          context.system.log.error("SlaTokenActor | Failure: {}", e.getMessage)
          GetSlaResponseFailure(s"User not found with Authorization: $authorizationHeader. ${e.getMessage}")
      }
      nextResponse(replyTo)
  }

  def nextResponse(replyTo: ActorRef[CommandStatus]): Behavior[Command] = Behaviors.receiveMessage {
    case GetSlaResponseSuccess(sla) =>
      context.system.log.info("SlaTokenActor | GetSlaResponseSuccess: {}", sla)
      context.system.log.info("SlaTokenActor | GetSlaResponseSuccess | unstashAll")
      replyTo ! GetSlaSuccess(sla)
      buffer.unstashAll(nextSuccess(sla))

    case GetSlaResponseFailure(message) =>
      context.system.log.info("SlaTokenActor | GetSlaResponseFailure: {}", message)
      context.system.log.info("SlaTokenActor | GetSlaResponseFailure | unstash")
      replyTo ! GetSlaFailure(message)
      buffer.unstash(start(), 1, value => {
        context.system.log.info("SlaTokenActor | unstash: {}", value)
        value
      })

    case other =>
      buffer.stash(other)
      Behaviors.same
  }

  def nextSuccess(sla: Sla): Behavior[Command] = Behaviors.receiveMessage {
    case GetSla(_, replyTo) =>
      replyTo ! GetSlaSuccess(sla)
      Behaviors.same
  }

  private def getSla(authorizationHeader: String): Future[Sla] = {
    implicit val materializer: Materializer = SystemMaterializer(context.system.toClassic).materializer

    val header: Authorization = Authorization(GenericHttpCredentials("", authorizationHeader))
    val httpRequest: HttpRequest = HttpRequest(uri = "http://127.0.0.1:8081/", headers = Seq(header))
    val httpResponse: Future[Sla] = Http()(context.system.toClassic).singleRequest(httpRequest)
      .map(_.entity.withContentType(ContentTypes.`application/json`))
      .flatMap(entity => Unmarshal(entity).to[String])
      .map(_.parseJson.convertTo[Sla])

    httpResponse
  }

}

