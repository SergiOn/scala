package com.service.routes

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.ActorContext
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.server.Directives.{complete, extractRequest, onComplete}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.Timeout
import com.service.actors.{ProxyActor, SlaActor}
import com.service.actors.users.{UnauthorizedUserActor, UserActorStatus}
import com.service.models.Sla
import com.service.models.marshalling.ModelMarshalling

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import spray.json._

object Routes {
  def apply(context: ActorContext[Nothing], timeout: Timeout): Route =
    new Routes(
      createSlaActor(context),
      createUnauthorizedUserActor(context),
      createProxyActor(context)
    )(
      context.system,
      timeout
    ).routes

  private def createSlaActor(context: ActorContext[Nothing]): ActorRef[SlaActor.Command] = {
    val actor = context.spawn(SlaActor(), SlaActor.name)
    context.watch(actor)
    actor
  }

  private def createUnauthorizedUserActor(context: ActorContext[Nothing]): ActorRef[UnauthorizedUserActor.Command] = {
    val actor = context.spawn(UnauthorizedUserActor(), UnauthorizedUserActor.name)
    context.watch(actor)
    actor
  }

  private def createProxyActor(context: ActorContext[Nothing]): ActorRef[ProxyActor.Command] = {
    val actor = context.spawn(ProxyActor(), ProxyActor.name)
    context.watch(actor)
    actor
  }
}

class Routes(
              slaActor: ActorRef[SlaActor.Command],
              unauthorizedUserActor: ActorRef[UnauthorizedUserActor.Command],
              proxyActor: ActorRef[ProxyActor.Command]
            )
            (implicit val system: ActorSystem[_], implicit val requestTimeout: Timeout)
  extends ModelMarshalling {

  implicit val executionContext: ExecutionContext = system.executionContext

/*
                                     request
                    /                                       \
             token existed                              token missed
                 /                                            \
           request to SLA                               unauthorized flow
         /               \                               /             \
    success             failure                    below limit       over limit
      /                    \                          /                  \
authorized flow       unauthorized flow            proxy              failure: TooManyRequests
                                                /         \
                                            success     failure
   (authorized flow | unauthorized flow)
             /              \
       below limit        over limit
          /                   \
       proxy              failure: TooManyRequests
    /         \
success     failure
 */

  def routes: Route = extractRequest { request =>

    val authorizationHeader: Option[String] = request.headers
      .find(h => h.is(Authorization.lowercaseName))
      .map(_.value())

    val userActorStatus: Future[UserActorStatus] = authorizationHeader
      .map(header => {
        getSla(header).flatMap {
          case SlaActor.GetSlaSuccess(sla) =>
            system.log.info(s"AuthorizationHeader: $header")
            system.log.info(s"GetSlaSuccess: $sla")
            // Authorized User Flow
            unauthorizedUserFlow()
          case SlaActor.GetSlaFailure(message) =>
            system.log.info(s"AuthorizationHeader: $header")
            system.log.info(s"GetSlaFailure: $message")
            // Unauthorized User Flow
            unauthorizedUserFlow()
        }
      })
      .getOrElse {
        system.log.info(s"AuthorizationHeader: missed")
        // Unauthorized User Flow
        unauthorizedUserFlow()
      }

    val proxyResult: Future[(UserActorStatus, Any)] = userActorStatus.flatMap {
      case unauthorized @ UnauthorizedUserActor.BelowLimit => doProxy(request)
        .map {
          case success @ ProxyActor.DoProxySuccess(_) => (unauthorized, success)
          case failure @ ProxyActor.DoProxyFailure(_) => (unauthorized, failure)
        }
      case unauthorized @ UnauthorizedUserActor.OverLimit => Future.successful(unauthorized, ())
    }

    onComplete(proxyResult) {
      case Success((UnauthorizedUserActor.BelowLimit, ProxyActor.DoProxySuccess(response))) =>
        unauthorizedUserFlowComplete()
        system.log.info(s"Success: $response")
        complete(StatusCodes.OK, Unmarshal(response.entity).to[String])

      case Success((UnauthorizedUserActor.BelowLimit, ProxyActor.DoProxyFailure(exception))) =>
        unauthorizedUserFlowComplete()
        system.log.error(s"Failure: $exception")
        complete(StatusCodes.BadRequest, exception.getMessage)

      case Success((UnauthorizedUserActor.OverLimit, _)) =>
        system.log.error(s"Failure: OverLimit")
        complete(StatusCodes.TooManyRequests)

      case Failure(e) =>
        system.log.error(s"Failure: ${e.getMessage}")
        complete(StatusCodes.TooManyRequests, e.getMessage)
    }

//    complete(s"Request method is ${request.method.name} and content-type is ${request.entity.contentType}")
  }

  private def getSla(authorizationHeader: String): Future[SlaActor.CommandStatus] =
    slaActor.ask(SlaActor.GetSla(authorizationHeader, _: ActorRef[SlaActor.CommandStatus]))

  def authorizedUserFlow(sla: Sla): Future[Boolean] = {
    Future.successful(true)
  }

  def unauthorizedUserFlow(): Future[UserActorStatus] =
    unauthorizedUserActor.ask(UnauthorizedUserActor.DefineRPS(_: ActorRef[UnauthorizedUserActor.CommandStatus]))

  def unauthorizedUserFlowComplete(): Unit =
    unauthorizedUserActor.tell(UnauthorizedUserActor.DefineRPSComplete)

  def doProxy(request: HttpRequest): Future[ProxyActor.CommandStatus] =
    proxyActor.ask(ProxyActor.DoProxy(request, _: ActorRef[ProxyActor.CommandStatus]))

}
