package api.response

import play.api.libs.json.{Json, OFormat}

case class ChargeResponse(error: String)

object ChargeResponse {
  implicit lazy val format: OFormat[ChargeResponse] = Json.format[ChargeResponse]
}
