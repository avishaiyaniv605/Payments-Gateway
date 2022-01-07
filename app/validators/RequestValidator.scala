package validators

import api.requests.ChargeRequest
import company.CreditCardCompany
import org.joda.time.format._

import scala.util.{Failure, Success, Try}

class RequestValidator {
  private val DATE_FORMAT = "MM/YY"
  private val CREDIT_CARD_LENGTH = 16
  private val dateFormatter: DateTimeFormatter = DateTimeFormat forPattern DATE_FORMAT

  def validateRequest(chargeRequest: ChargeRequest, merchantIdentifier: String): Try[Unit] = {
    val validationResults: Seq[Try[Unit]] = Seq(
      validateCvv(chargeRequest.cvv),
      validateExpirationDate(chargeRequest.expirationDate),
      validateCreditCardNumber(chargeRequest.creditCardNumber),
      validateCreditCardCompany(chargeRequest.creditCardCompany),
      validateString("full name", chargeRequest.fullName),
      validateString("full name", chargeRequest.fullName),
      validateString("merchant id", merchantIdentifier)
    )

    validationResults.find(t => t.isFailure) match {
      case Some(failureValidationRes) => failureValidationRes
      case _ => Success(())
    }
  }

  private def validateCvv(cvv: String): Try[Unit] = {
    if (cvv.isEmpty || cvv.length != 3 || cvv.toIntOption.isEmpty) {
      Failure(new Error("cvv is missing or incorrect"))
    } else {
      Success(())
    }
  }

  private def validateCreditCardCompany(creditCardCompany: String): Try[Unit] = {
    if (creditCardCompany.isEmpty || CreditCardCompany.from(creditCardCompany).isEmpty) {
      Failure(new Error("credit card company is missing or incorrect"))
    } else {
      Success(())
    }
  }

  private def validateCreditCardNumber(creditCardNum: String): Try[Unit] = {
    if (creditCardNum.isEmpty || creditCardNum.length != CREDIT_CARD_LENGTH || creditCardNum.toLongOption.isEmpty) {
      Failure(new Error("credit card num is missing or incorrect"))
    } else {
      Success(())
    }
  }

  private def validateExpirationDate(expirationDateStr: String): Try[Unit] = {
    val expDate =
      try {
        Some(dateFormatter parseDateTime expirationDateStr)
      } catch {
        case _: IllegalArgumentException => Option.empty
      }

    if (expirationDateStr.isEmpty || expDate.isEmpty) {
      Failure(new Error("Expiration date is missing or invalid"))
    } else {
      Success(())
    }
  }

  private def validateString(key: String, value: String): Try[Unit] = value.length match {
    case 0 => Failure(new Error(s"$key is missing!"))
    case _ => Success(())
  }
}