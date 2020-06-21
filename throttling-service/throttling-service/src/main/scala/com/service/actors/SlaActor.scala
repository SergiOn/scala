package com.service.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpRequest}
import akka.http.scaladsl.model.headers.{Authorization, GenericHttpCredentials}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{Materializer, SystemMaterializer}
import com.service.actors.SlaActor.Command
import com.service.models.Sla
import com.service.models.marshalling.ModelMarshalling

import scala.concurrent.{ExecutionContext, Future}
import spray.json._

import scala.util.{Failure, Success}

object SlaActor {
  def name: String = "SlaActor"
  def apply(): Behavior[Command] = Behaviors.setup(context => new SlaActor(context))

  sealed trait Command
  final case class GetSla(authorizationHeader: String, replyTo: ActorRef[CommandStatus]) extends Command

  sealed trait CommandStatus
  final case class GetSlaSuccess(sla: Sla) extends CommandStatus
  final case class GetSlaFailure(message: String = "User not found") extends CommandStatus
}

class SlaActor(context: ActorContext[Command]) extends AbstractBehavior[Command](context) with ModelMarshalling {
  import SlaActor._
  implicit val executionContext: ExecutionContext = context.executionContext

  override def onMessage(message: Command): Behavior[Command] = message match {
    case GetSla(authorizationHeader, replyTo) =>
      getSla(authorizationHeader).onComplete {
        case Success(sla) => replyTo ! GetSlaSuccess(sla)
        case Failure(e) => replyTo ! GetSlaFailure(s"User not found with Authorization: $authorizationHeader. ${e.getMessage}")
      }
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
