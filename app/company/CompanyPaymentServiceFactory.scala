package company

import com.google.inject.Inject
import company.CreditCardCompany.CreditCardCompany
import company.mastercard.MastercardPaymentService
import company.visa.VisaPaymentService
import play.api.libs.ws.WSClient
import services.FailureStatusService

class CompanyPaymentServiceFactory @Inject()(wsClient: WSClient, failureStatusService: FailureStatusService) {
  def buildCompanyPaymentService(creditCardCompany: CreditCardCompany): CompanyPaymentService = {
    creditCardCompany match {
      case CreditCardCompany.VISA => new VisaPaymentService(wsClient, failureStatusService)
      case CreditCardCompany.MASTERCARD => new MastercardPaymentService(wsClient, failureStatusService)
      case _ => throw new IllegalArgumentException("Unknown credit card company")
    }
  }
}
