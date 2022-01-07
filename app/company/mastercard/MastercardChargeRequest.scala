package company.mastercard

import api.requests.ChargeRequest
import company.CompanyChargeRequest
import play.api.libs.json.{Json, OFormat}

case class MastercardChargeRequest(
                                    first_name: String,
                                    last_name: String,
                                    card_number: String,
                                    expiration: String,
                                    cvv: String,
                                    charge_amount: Float
                                  ) extends CompanyChargeRequest

object MastercardChargeRequest {
  def fromChargeRequest(chargeRequest: ChargeRequest): MastercardChargeRequest = {
    val splitFullName: Array[String] = chargeRequest.fullName.split(" ")
    val firstName: String = splitFullName(0)
    val lastName: String = if (splitFullName.length == 2) splitFullName(1) else ""
    val mcFormattedExpiration: String = chargeRequest.expirationDate.replace("/", "-")

    MastercardChargeRequest(
      first_name = firstName,
      last_name = lastName,
      card_number = chargeRequest.creditCardNumber,
      expiration = mcFormattedExpiration,
      cvv = chargeRequest.cvv,
      charge_amount = chargeRequest.amount
    )
  }

  implicit lazy val format: OFormat[MastercardChargeRequest] = Json.format[MastercardChargeRequest]
}
