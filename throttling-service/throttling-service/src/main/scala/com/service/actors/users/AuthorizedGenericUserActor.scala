package com.service.actors.users

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import com.service.actors.users.AuthorizedGenericUserActor.Command

import scala.concurrent.ExecutionContext

object AuthorizedGenericUserActor {
  def name(user: String): String = s"AuthorizedGenericUserActor${user}"
  def apply(): Behavior[Command] = Behaviors.setup(context => new AuthorizedGenericUserActor(context, 0))

  sealed trait Command
  final case class DefineRPS(rps: Int, replyTo: ActorRef[CommandStatus]) extends Command
  final object DefineRPSComplete extends Command

  sealed trait CommandStatus extends UserActorStatus
  final object BelowLimit extends CommandStatus
  final object OverLimit extends CommandStatus
}

class AuthorizedGenericUserActor(context: ActorContext[Command], rpsInProgress: Int) extends AbstractBehavior[Command](context) {
  import AuthorizedGenericUserActor._
  implicit val executionContext: ExecutionContext = context.executionContext

  override def onMessage(message: Command): Behavior[Command] = message match {
    case DefineRPS(rps, replyTo) =>
      if (rpsInProgress < rps) {
        context.system.log.info("AuthorizedGenericUserActor | DefineRPS: BelowLimit")
        replyTo ! BelowLimit
        next(rpsInProgress + 1)
      } else {
        context.system.log.info("AuthorizedGenericUserActor | DefineRPS: OverLimit")
        replyTo ! OverLimit
        Behaviors.same
      }
    case DefineRPSComplete =>
      context.system.log.info("AuthorizedGenericUserActor | DefineRPSComplete")
      next(rpsInProgress - 1)
  }

  private def next(rpsInProgress: Int): AuthorizedGenericUserActor = {
    new AuthorizedGenericUserActor(context, rpsInProgress)
  }

}
