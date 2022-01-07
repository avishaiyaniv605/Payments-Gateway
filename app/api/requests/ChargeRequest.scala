package api.requests

import play.api.libs.json.{Json, OFormat}

case class ChargeRequest(
                        fullName: String,
                        creditCardNumber: String,
                        creditCardCompany: String,
                        expirationDate: String,
                        cvv: String,
                        amount: Float
                        )

object ChargeRequest {
  implicit lazy val chargeRequestFormat: OFormat[ChargeRequest] = Json.format[ChargeRequest]
}
