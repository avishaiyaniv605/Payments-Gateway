package company

import play.api.libs.json.{Format, Json}

object CreditCardCompany extends Enumeration {
  type CreditCardCompany = Value

  val VISA, MASTERCARD = Value
  def from(s: String): Option[Value] = values.find(_.toString.toLowerCase == s.toLowerCase)

  implicit val format: Format[CreditCardCompany] = Json.formatEnum(this)
}
