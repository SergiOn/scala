package com.service.actors.users

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import com.service.actors.users.UnauthorizedUserActor.Command
import com.service.configs.UnauthorizedUserProperties

object UnauthorizedUserActor {
  def name: String = "UnauthorizedUserActor"
  def apply(): Behavior[Command] = Behaviors.setup(context => new UnauthorizedUserActor(context, 0))

  private val graceRps: Int = UnauthorizedUserProperties.graceRps

  sealed trait Command
  final case class DefineRPS(replyTo: ActorRef[CommandStatus]) extends Command
  final object DefineRPSComplete extends Command

  sealed trait CommandStatus extends UserActorStatus
  final object BelowLimit extends CommandStatus
  final object OverLimit extends CommandStatus
}

class UnauthorizedUserActor(context: ActorContext[Command], rpsInProgress: Int) extends AbstractBehavior[Command](context) {
  import UnauthorizedUserActor._

  override def onMessage(message: Command): Behavior[Command] = message match {
    case DefineRPS(replyTo) =>
      if (rpsInProgress < graceRps) {
        context.system.log.info("UnauthorizedUserActor | DefineRPS: BelowLimit")
        replyTo ! BelowLimit
        next(rpsInProgress + 1)
      } else {
        context.system.log.info("UnauthorizedUserActor | DefineRPS: OverLimit")
        replyTo ! OverLimit
        Behaviors.same
      }
    case DefineRPSComplete =>
      context.system.log.info("UnauthorizedUserActor | DefineRPSComplete")
      next(rpsInProgress - 1)
  }

  private def next(rpsInProgress: Int): UnauthorizedUserActor = {
    new UnauthorizedUserActor(context, rpsInProgress)
  }

}
