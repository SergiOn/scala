package com.service.actors

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import com.service.actors.SlaActor.Command
import com.service.models.Sla

import scala.concurrent.ExecutionContext

import scala.util.{Failure, Success}

object SlaActor {
  def name: String = "SlaActor"
  def apply(timeout: Timeout): Behavior[Command] = Behaviors.setup(context => new SlaActor(context)(timeout))

  sealed trait Command
  final case class GetSla(authorizationHeader: String, replyTo: ActorRef[CommandStatus]) extends Command

  sealed trait CommandStatus
  final case class GetSlaSuccess(sla: Sla) extends CommandStatus
  final case class GetSlaFailure(message: String = "User not found") extends CommandStatus
}

class SlaActor(context: ActorContext[Command])(implicit val requestTimeout: Timeout) extends AbstractBehavior[Command](context) {
  import SlaActor._
  implicit val system: ActorSystem[Nothing] = context.system
  implicit val executionContext: ExecutionContext = context.executionContext

  override def onMessage(message: Command): Behavior[Command] = message match {
    case GetSla(authorizationHeader, replyTo) =>
      val slaTokenActorName = authorizationHeader.substring(7)
      val actor: ActorRef[SlaTokenActor.Command] = getSlaTokenActor(slaTokenActorName)

      actor
        .ask(SlaTokenActor.GetSla(authorizationHeader, _: ActorRef[SlaTokenActor.CommandStatus]))
        .onComplete {
          case Success(SlaTokenActor.GetSlaSuccess(sla)) =>
            context.system.log.info("SlaActor | GetSlaSuccess: {}", sla)
            replyTo ! SlaActor.GetSlaSuccess(sla)
          case Success(SlaTokenActor.GetSlaFailure(message)) =>
            context.system.log.error("SlaActor | GetSlaFailure: {}", message)
            replyTo ! SlaActor.GetSlaFailure(message)
        }
      Behaviors.same
  }

  private def getSlaTokenActor(name: String): ActorRef[SlaTokenActor.Command] = {
    context
      .child(SlaTokenActor.name(name))
      .asInstanceOf[Option[ActorRef[SlaTokenActor.Command]]]
      .getOrElse(createSlaTokenActor(name))
  }

  private def createSlaTokenActor(name: String): ActorRef[SlaTokenActor.Command] = {
    val actor = context.spawn(SlaTokenActor(), SlaTokenActor.name(name))
    context.watch(actor)
    actor
  }

}
