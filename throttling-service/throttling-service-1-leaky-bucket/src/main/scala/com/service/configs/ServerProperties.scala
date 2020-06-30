package com.service.configs

import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.{Duration, FiniteDuration}

object ServerProperties {
  private val config = ConfigFactory.load()

  val host: String = config.getString("http.host")
  val port: Int = config.getInt("http.port")

  val requestTimeout: Timeout = {
    val t: String = config.getString("akka.http.server.request-timeout")
    val d: Duration = Duration(t)
    FiniteDuration(d.length, d.unit)
  }

}
