package com.service.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import com.service.actors.SlaActor.Command
import com.service.models.Sla

import scala.concurrent.ExecutionContext

object SlaActor {
  def name: String = "SlaActor"

  def apply(): Behavior[Command] = Behaviors.setup(context => new SlaActor(context))

  sealed trait Command
  final case class GetSla(token: String, replyTo: ActorRef[CommandStatus]) extends Command

  sealed trait CommandStatus
  final case class GetSlaSuccess(sla: Sla) extends CommandStatus
  final case class GetSlaFailure(message: String = "User not found") extends CommandStatus

}

class SlaActor(context: ActorContext[Command]) extends AbstractBehavior[Command](context) {
  import SlaActor._
  implicit val executionContext: ExecutionContext = context.executionContext

  override def onMessage(message: Command): Behavior[Command] = message match {
    case GetSla(token, replyTo) =>
      replyTo ! GetSlaSuccess(Sla("", 0))
      Behaviors.same
  }

}
