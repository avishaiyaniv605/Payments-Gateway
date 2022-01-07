package company.mastercard

import play.api.libs.json.{Json, OFormat}

case class MastercardChargeResponse(decline_reason: String)

object MastercardChargeResponse {
  implicit lazy val format: OFormat[MastercardChargeResponse] = Json.format[MastercardChargeResponse]
}
