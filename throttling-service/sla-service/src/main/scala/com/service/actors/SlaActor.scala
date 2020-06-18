package com.service.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import com.service.actors.SlaActor.Command
import com.service.models.Sla
import com.service.services.SlaService

import scala.concurrent.ExecutionContext

object SlaActor {
  def name: String = "SlaActor"

  def apply(): Behavior[Command] = Behaviors.setup(context => new SlaActor(context, SlaService()))

  sealed trait Command
  final case class GetSla(token: String, replyTo: ActorRef[CommandStatus]) extends Command

  sealed trait CommandStatus
  final case class GetSlaSuccess(sla: Sla) extends CommandStatus
  final case class GetSlaFailure(message: String = "User not found") extends CommandStatus

}

class SlaActor(context: ActorContext[Command], userService: SlaService) extends AbstractBehavior[Command](context) {
  import SlaActor._
  implicit val executionContext: ExecutionContext = context.executionContext

  override def onMessage(message: Command): Behavior[Command] = message match {
    case GetSla(token, replyTo) =>
      val commandStatus: CommandStatus = userService.getSlaByToken(token)
        .map(GetSlaSuccess)
        .getOrElse(GetSlaFailure(s"User not found with token: $token"))

      replyTo ! commandStatus

//    case GetSla(token, replyTo) =>
//      userService.getSlaByToken(token)
//        .onComplete {
//          case Success(v) => replyTo ! GetSlaSuccess(v)
//          case Failure(e) => replyTo ! GetSlaFailure(e.getMessage)
//        }
      Behaviors.same
  }

}
