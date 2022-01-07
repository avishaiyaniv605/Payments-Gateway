package services

import api.requests.ChargeRequest
import com.google.inject.Inject
import company.CreditCardCompany.CreditCardCompany
import company.{CompanyPaymentServiceFactory, CreditCardCompany}
import exceptions.ValidationException
import play.api.Logger
import validators.RequestValidator

import scala.concurrent.Future
import scala.util.{Failure, Success}

class PaymentsService @Inject()(
                                 requestValidator: RequestValidator,
                                 paymentServiceFactory: CompanyPaymentServiceFactory
                               ) {

  val logger: Logger = Logger("play")

  def charge(chargeRequest: ChargeRequest, merchantId: String): Future[Unit] = {
    requestValidator.validateRequest(chargeRequest, merchantId) match {
      case Failure(e) =>
        logger.error(s"Validation of charge request has failed, $e")
        Future failed ValidationException(e.getMessage)
      case Success(()) => chargeValidRequest(chargeRequest, merchantId)
    }
  }

  private def chargeValidRequest(chargeRequest: ChargeRequest, merchantId: String): Future[Unit] = {
    val creditCardCompany: CreditCardCompany = CreditCardCompany.from(chargeRequest.creditCardCompany).get
    paymentServiceFactory
      .buildCompanyPaymentService(creditCardCompany)
      .payToCompany(chargeRequest, merchantId)
  }
}
