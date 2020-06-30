package com.service.services

import com.service.models.Sla

object SlaService {
  def apply(): SlaService = new SlaService()
//  def apply(): SlaService = new SlaService()(ExecutionContext.global)
//  def apply(executionContext: ExecutionContext): SlaService = new SlaService()(executionContext)
}

class SlaService {

  private val map: Map[String, Sla] = Map(
    "0" -> Sla("User0", 0),
    "1" -> Sla("User1", 1),
    "2" -> Sla("User2", 2),
    "3" -> Sla("User3", 3),
    "token" -> Sla("User", 2),
    "token-1" -> Sla("User-1", 5),
    "token-2" -> Sla("User-2", 10),
    "12345" -> Sla("User-3", 15),
    "123" -> Sla("User-5", 20),
    "124" -> Sla("User-5", 20),
    "125" -> Sla("User-5", 20),
    "100" -> Sla("User-100", 100),
    "101" -> Sla("User-100", 100),
    "102" -> Sla("User-100", 100),
    "103" -> Sla("User-100", 100),
    "104" -> Sla("User-100", 100),
    "105" -> Sla("User-100", 100),
    "200" -> Sla("User-200", 200),
    "500" -> Sla("User-500", 500),
    "501" -> Sla("User-500", 500),
    "502" -> Sla("User-500", 500),
    "503" -> Sla("User-500", 500),
    "504" -> Sla("User-500", 500),
    "505" -> Sla("User-500", 500),
    "1000" -> Sla("User-1000", 1000),
    "1001" -> Sla("User-1000", 1000),
    "1002" -> Sla("User-1000", 1000),
    "1003" -> Sla("User-1000", 1000),
    "1004" -> Sla("User-1000", 1000),
    "1005" -> Sla("User-1000", 1000),
    "1500" -> Sla("User-1500", 1500),
    "2000" -> Sla("User-2000", 2000),
    "2001" -> Sla("User-2000", 2000),
    "2002" -> Sla("User-2000", 2000),
    "2003" -> Sla("User-2000", 2000),
    "2004" -> Sla("User-2000", 2000),
    "2005" -> Sla("User-2000", 2000),
    "2500" -> Sla("User-2500", 2500)
  )

  def getSlaByToken(token: String): Option[Sla] = map.get(token)

//  def getSlaByToken(token: String): Future[Sla] = map.get(token)
//    .map(Future.successful)
//    .getOrElse(Future.failed(new RuntimeException(s"User not found with token: $token")))

}
