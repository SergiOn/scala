package com.service.models.marshalling

import com.service.models.Sla
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait ModelMarshalling extends DefaultJsonProtocol {
  implicit val slaMarshalling: RootJsonFormat[Sla] = jsonFormat2(Sla)
}
