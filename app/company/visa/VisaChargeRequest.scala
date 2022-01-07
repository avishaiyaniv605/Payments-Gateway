package company.visa

import api.requests.ChargeRequest
import company.CompanyChargeRequest
import play.api.libs.json.{Json, OFormat}

case class VisaChargeRequest(
                              fullName: String,
                              number: String,
                              expiration: String,
                              cvv: String,
                              totalAmount: Float
                            ) extends CompanyChargeRequest

object VisaChargeRequest {
  def fromChargeRequest(chargeRequest: ChargeRequest): VisaChargeRequest = {
    VisaChargeRequest(
      fullName = chargeRequest.fullName,
      number = chargeRequest.creditCardNumber,
      expiration = chargeRequest.expirationDate,
      cvv = chargeRequest.cvv,
      totalAmount = chargeRequest.amount,
    )
  }

  implicit lazy val format: OFormat[VisaChargeRequest] = Json.format[VisaChargeRequest]
}
