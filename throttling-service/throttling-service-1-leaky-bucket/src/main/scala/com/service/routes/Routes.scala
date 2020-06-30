package com.service.routes

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.ActorContext
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.server.Directives.{complete, extractRequest, onComplete}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.Timeout
import com.service.actors.{ProxyActor, SlaActor}
import com.service.actors.users.{AuthorizedUserActor, UnauthorizedUserActor, UserActorStatus}
import com.service.models.Sla
import com.service.models.marshalling.ModelMarshalling

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import spray.json._

object Routes {
  sealed trait Status
  final case class AuthorizedBelowLimit(user: String) extends Status
  final object AuthorizedOverLimit extends Status
  final object UnauthorizedBelowLimit extends Status
  final object UnauthorizedOverLimit extends Status

  def apply(context: ActorContext[Nothing], timeout: Timeout): Route =
    new Routes(
      createSlaActor(context, timeout),
      createAuthorizedUserActor(context, timeout),
      createUnauthorizedUserActor(context),
      createProxyActor(context)
    )(
      context.system,
      timeout
    ).routes

  private def createSlaActor(context: ActorContext[Nothing], timeout: Timeout): ActorRef[SlaActor.Command] = {
    val actor = context.spawn(SlaActor(timeout), SlaActor.name)
    context.watch(actor)
    actor
  }

  private def createAuthorizedUserActor(context: ActorContext[Nothing], timeout: Timeout): ActorRef[AuthorizedUserActor.Command] = {
    val actor = context.spawn(AuthorizedUserActor(timeout), AuthorizedUserActor.name)
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
              authorizedUserActor: ActorRef[AuthorizedUserActor.Command],
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
    import Routes._

    val authorizationHeader: Option[String] = request.headers
      .find(h => h.is(Authorization.lowercaseName))
      .map(_.value())

    val userActorStatus: Future[UserActorStatus] = authorizationHeader
      .map(header => {
        getSla(header).flatMap {
          case SlaActor.GetSlaSuccess(sla) =>
            system.log.info("userActorStatus | AuthorizationHeader: {}", header)
            system.log.info("userActorStatus | GetSlaSuccess: {}", sla)
            // Authorized User Flow
            authorizedUserFlow(sla)
          case SlaActor.GetSlaFailure(message) =>
            system.log.error("userActorStatus | AuthorizationHeader: {}", header)
            system.log.error("userActorStatus | GetSlaFailure: {}", message)
            // Unauthorized User Flow
            unauthorizedUserFlow()
        }
      })
      .getOrElse {
        system.log.info("AuthorizationHeader: missed")
        // Unauthorized User Flow
        unauthorizedUserFlow()
      }

    val proxyResult: Future[(Status, Option[HttpResponse], Option[Throwable])] = userActorStatus.flatMap {
      case AuthorizedUserActor.BelowLimit(user) => doProxy(request)
        .map {
          case ProxyActor.DoProxySuccess(response) =>
            system.log.info("proxyResult | AuthorizedUserActor | DoProxySuccess")
            (AuthorizedBelowLimit(user), Some(response), None)
          case ProxyActor.DoProxyFailure(exception) =>
            system.log.error("proxyResult | AuthorizedUserActor | DoProxyFailure")
            (AuthorizedBelowLimit(user), None, Some(exception))
        }
      case AuthorizedUserActor.OverLimit => Future.successful((AuthorizedOverLimit, None, None))

      case UnauthorizedUserActor.BelowLimit => doProxy(request)
        .map {
          case ProxyActor.DoProxySuccess(response) =>
            system.log.info("proxyResult | UnauthorizedUserActor | DoProxySuccess")
            (UnauthorizedBelowLimit, Some(response), None)
          case ProxyActor.DoProxyFailure(exception) =>
            system.log.error("proxyResult | UnauthorizedUserActor | DoProxyFailure")
            (UnauthorizedBelowLimit, None, Some(exception))
        }
      case UnauthorizedUserActor.OverLimit => Future.successful((UnauthorizedOverLimit, None, None))
    }

    onComplete(proxyResult) {

      case Success((AuthorizedBelowLimit(user), Some(response), None)) =>
        authorizedUserFlowComplete(user)
        system.log.info("onComplete | AuthorizedBelowLimit | Success: {}", response)
        complete(StatusCodes.OK, Unmarshal(response.entity).to[String])

      case Success((AuthorizedBelowLimit(user), None, Some(exception))) =>
        authorizedUserFlowComplete(user)
        system.log.error("onComplete | AuthorizedBelowLimit | Failure: {}", exception)
        complete(StatusCodes.BadRequest, exception.getMessage)

      case Success((AuthorizedOverLimit, None, None)) =>
        system.log.error("onComplete | AuthorizedOverLimit | Success: TooManyRequests")
        complete(StatusCodes.TooManyRequests)


      case Success((UnauthorizedBelowLimit, Some(response), None)) =>
        unauthorizedUserFlowComplete()
        system.log.info("onComplete | UnauthorizedBelowLimit | Success: {}", response)
        complete(StatusCodes.OK, Unmarshal(response.entity).to[String])

      case Success((UnauthorizedBelowLimit, None, Some(exception))) =>
        unauthorizedUserFlowComplete()
        system.log.error("onComplete | UnauthorizedBelowLimit | Failure: {}", exception)
        complete(StatusCodes.BadRequest, exception.getMessage)

      case Success((UnauthorizedOverLimit, None, None)) =>
        system.log.error("onComplete | UnauthorizedOverLimit | Success: TooManyRequests")
        complete(StatusCodes.TooManyRequests)


      case Failure(e) =>
        system.log.error("Failure: {}", e.getMessage)
        complete(StatusCodes.TooManyRequests, e.getMessage)
    }

//    complete(s"Request method is ${request.method.name} and content-type is ${request.entity.contentType}")
  }

  private def getSla(authorizationHeader: String): Future[SlaActor.CommandStatus] =
    slaActor.ask(SlaActor.GetSla(authorizationHeader, _: ActorRef[SlaActor.CommandStatus]))

  def authorizedUserFlow(sla: Sla): Future[UserActorStatus] =
    authorizedUserActor.ask(AuthorizedUserActor.DefineRPS(sla, _: ActorRef[AuthorizedUserActor.CommandStatus]))

  def authorizedUserFlowComplete(user: String): Unit =
    authorizedUserActor.tell(AuthorizedUserActor.DefineRPSComplete(user))

  def unauthorizedUserFlow(): Future[UserActorStatus] =
    unauthorizedUserActor.ask(UnauthorizedUserActor.DefineRPS(_: ActorRef[UnauthorizedUserActor.CommandStatus]))

  def unauthorizedUserFlowComplete(): Unit =
    unauthorizedUserActor.tell(UnauthorizedUserActor.DefineRPSComplete)

  def doProxy(request: HttpRequest): Future[ProxyActor.CommandStatus] =
    proxyActor.ask(ProxyActor.DoProxy(request, _: ActorRef[ProxyActor.CommandStatus]))

}
