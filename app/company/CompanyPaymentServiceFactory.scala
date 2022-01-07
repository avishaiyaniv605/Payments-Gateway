package company

import com.google.inject.Inject
import company.CreditCardCompany.CreditCardCompany
import company.mastercard.MastercardPaymentService
import company.visa.VisaPaymentService
import play.api.libs.ws.WSClient

class CompanyPaymentServiceFactory @Inject()(wsClient: WSClient) {
  def buildCompanyPaymentService(creditCardCompany: CreditCardCompany): CompanyPaymentService = {
    creditCardCompany match {
      case CreditCardCompany.VISA => new VisaPaymentService(wsClient)
      case CreditCardCompany.MASTERCARD => new MastercardPaymentService(wsClient)
      case _ => throw new IllegalArgumentException("Unknown credit card company")
    }
  }
}
