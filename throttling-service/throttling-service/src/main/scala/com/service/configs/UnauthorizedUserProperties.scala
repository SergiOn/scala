package com.service.configs

import com.typesafe.config.ConfigFactory

object UnauthorizedUserProperties {
  private val config = ConfigFactory.load()
  val graceRps: Int = config.getInt("graceRps")
}
