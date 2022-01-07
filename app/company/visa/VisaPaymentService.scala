package company.visa

import api.requests.ChargeRequest
import com.google.inject.Inject
import company.CreditCardCompany.CreditCardCompany
import company.{CompanyChargeRequest, CompanyPaymentService, CreditCardCompany}
import exceptions.BusinessException
import play.api.http.Status
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global

class VisaPaymentService @Inject()(wsClient: WSClient) extends CompanyPaymentService(wsClient) {
  private val FAILURE = "Failure"
  override def companyPaymentUrl: String = "https://interview.riskxint.com/visa/api/chargeCard"
  override protected def getRequestHeaders: (String, String) = ("identifier", "Avishai Yaniv")

  override protected def chargeToCompanyCharge(chargeRequest: ChargeRequest): CompanyChargeRequest =
    VisaChargeRequest.fromChargeRequest(chargeRequest)

  override protected def handleResponse(res: WSResponse): Unit = {
    res.status match {
      case Status.OK => handleValidResponse(res)
      case _ => handleBadStatus(res)
    }
  }

  private def handleValidResponse(res: WSResponse): Unit = {
    val visaResponse = res.body[JsValue].as[VisaChargeResponse]
    if (visaResponse.chargeResult == FAILURE)
      throw BusinessException(visaResponse.resultReason)
  }

  private def handleBadStatus(res: WSResponse) = {
    throw new Throwable("Invalid Request")
  }

  override protected def getCompany: CreditCardCompany = CreditCardCompany.VISA
}
