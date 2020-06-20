package com.service.services

import com.service.models.Sla

object SlaService {
  def apply(): SlaService = new SlaService()
//  def apply(): SlaService = new SlaService()(ExecutionContext.global)
//  def apply(executionContext: ExecutionContext): SlaService = new SlaService()(executionContext)
}

class SlaService {

  private val map: Map[String, Sla] = Map(
    "token" -> Sla("User", 2),
    "token-1" -> Sla("User-1", 5),
    "token-2" -> Sla("User-2", 10),
    "12345" -> Sla("User-3", 15),
    "123" -> Sla("User-5", 20),
    "124" -> Sla("User-5", 20),
    "125" -> Sla("User-5", 20)
  )

  def getSlaByToken(token: String): Option[Sla] = map.get(token)

//  def getSlaByToken(token: String): Future[Sla] = map.get(token)
//    .map(Future.successful)
//    .getOrElse(Future.failed(new RuntimeException(s"User not found with token: $token")))

}
