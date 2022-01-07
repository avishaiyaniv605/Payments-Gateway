package company.visa

import play.api.libs.json.{Json, OFormat}

case class VisaChargeResponse(
                             chargeResult: String,
                             resultReason: String
                             )

object VisaChargeResponse {
  implicit lazy val format: OFormat[VisaChargeResponse] = Json.format[VisaChargeResponse]
}
