package com.service.actors.users

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import com.service.actors.SlaActor
import com.service.actors.SlaActor.Command
import com.service.models.Sla

import scala.concurrent.ExecutionContext


class AuthorizedUserActor(context: ActorContext[Command]) extends AbstractBehavior[Command](context) {
  import SlaActor._
  implicit val executionContext: ExecutionContext = context.executionContext

  override def onMessage(message: Command): Behavior[Command] = message match {
    case GetSla(token, replyTo) =>
      replyTo ! GetSlaSuccess(Sla("", 0))
      Behaviors.same
  }

}
