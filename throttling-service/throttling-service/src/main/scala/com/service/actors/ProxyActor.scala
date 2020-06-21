package com.service.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.{Materializer, SystemMaterializer}
import com.service.actors.ProxyActor.Command

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object ProxyActor {
  def name: String = "ProxyActor"
  def apply(): Behavior[Command] = Behaviors.setup(context => new ProxyActor(context))

  sealed trait Command
  final case class DoProxy(request: HttpRequest, replyTo: ActorRef[CommandStatus]) extends Command

  sealed trait CommandStatus
  final case class DoProxySuccess(response: HttpResponse) extends CommandStatus
  final case class DoProxyFailure(exception: Throwable) extends CommandStatus
}

class ProxyActor(context: ActorContext[Command]) extends AbstractBehavior[Command](context) {
  import ProxyActor._
  implicit val executionContext: ExecutionContext = context.executionContext

  override def onMessage(message: Command): Behavior[Command] = message match {
    case DoProxy(request, replyTo) =>
      doProxy(request).onComplete {
        case Success(response) => replyTo ! DoProxySuccess(response)
        case Failure(exception) => replyTo ! DoProxyFailure(exception)
      }
      Behaviors.same
  }

  private def doProxy(request: HttpRequest): Future[HttpResponse] = {
    implicit val materializer: Materializer = SystemMaterializer(context.system.toClassic).materializer

    val rawQueryString: String = request.uri.rawQueryString.map(q => s"?${q}").getOrElse("")
    val uri: String = s"${request.uri.scheme}://localhost:8083${request.uri.path}${rawQueryString}"

    val httpRequest: HttpRequest = HttpRequest(
      method = request.method,
      uri = uri,
      headers = request.headers,
      entity = request.entity,
      protocol = request.protocol
    )

    val httpResponse: Future[HttpResponse] = Http()(context.system.toClassic).singleRequest(httpRequest)
    httpResponse
  }

}
