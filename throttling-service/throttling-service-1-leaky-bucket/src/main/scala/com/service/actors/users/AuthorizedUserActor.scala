package com.service.actors.users

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.util.Timeout
import com.service.actors.users.AuthorizedUserActor.Command
import com.service.models.Sla

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object AuthorizedUserActor {
  def name: String = "AuthorizedUserActor"
  def apply(timeout: Timeout): Behavior[Command] = Behaviors.setup(context => new AuthorizedUserActor(context)(timeout))

  sealed trait Command
  final case class DefineRPS(sla: Sla, replyTo: ActorRef[CommandStatus]) extends Command
  final case class DefineRPSComplete(user: String) extends Command

  sealed trait CommandStatus extends UserActorStatus
  final case class BelowLimit(user: String) extends CommandStatus
  final object OverLimit extends CommandStatus
}

class AuthorizedUserActor(context: ActorContext[Command])(implicit val requestTimeout: Timeout) extends AbstractBehavior[Command](context) {
  import AuthorizedUserActor._
  implicit val system: ActorSystem[Nothing] = context.system
  implicit val executionContext: ExecutionContext = context.executionContext

  override def onMessage(message: Command): Behavior[Command] = message match {
    case DefineRPS(sla, replyTo) =>
      val actor: ActorRef[AuthorizedGenericUserActor.Command] = getAuthorizedGenericUserActor(sla.user)

      actor
        .ask(AuthorizedGenericUserActor.DefineRPS(sla.rps, _: ActorRef[AuthorizedGenericUserActor.CommandStatus]))
        .onComplete {
          case Success(AuthorizedGenericUserActor.BelowLimit) =>
            context.system.log.info("AuthorizedUserActor | BelowLimit")
            replyTo ! AuthorizedUserActor.BelowLimit(sla.user)
          case Success(AuthorizedGenericUserActor.OverLimit) =>
            context.system.log.info("AuthorizedUserActor | OverLimit")
            replyTo ! AuthorizedUserActor.OverLimit
        }
      Behaviors.same
    case DefineRPSComplete(user) =>
      context.system.log.info("AuthorizedUserActor | DefineRPSComplete")
      val actor: ActorRef[AuthorizedGenericUserActor.Command] = getAuthorizedGenericUserActor(user)
      actor ! AuthorizedGenericUserActor.DefineRPSComplete
      Behaviors.same
  }

  private def getAuthorizedGenericUserActor(user: String): ActorRef[AuthorizedGenericUserActor.Command] = {
    context
      .child(AuthorizedGenericUserActor.name(user))
      .asInstanceOf[Option[ActorRef[AuthorizedGenericUserActor.Command]]]
      .getOrElse(createAuthorizedGenericUserActor(user))
  }

  private def createAuthorizedGenericUserActor(user: String): ActorRef[AuthorizedGenericUserActor.Command] = {
    val actor = context.spawn(AuthorizedGenericUserActor(), AuthorizedGenericUserActor.name(user))
    context.watch(actor)
    actor
  }

}
