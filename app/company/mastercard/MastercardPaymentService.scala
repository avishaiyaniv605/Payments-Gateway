package company.mastercard

import api.requests.ChargeRequest
import com.google.inject.Inject
import company.CreditCardCompany.CreditCardCompany
import company.{CompanyChargeRequest, CompanyPaymentService, CreditCardCompany}
import exceptions.BusinessException
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSClient, WSResponse}
import play.mvc.Http.Status
import services.FailureStatusService

import scala.concurrent.ExecutionContext.Implicits.global

class MastercardPaymentService @Inject()(wsClient: WSClient, failureStatusService: FailureStatusService)
  extends CompanyPaymentService(wsClient, failureStatusService) {

  override def companyPaymentUrl: String = "https://interview.riskxint.com/mastercard/capture_card"
  override protected def getRequestHeaders: (String, String) = ("identifier", "Avishai Yaniv")

  override protected def chargeToCompanyCharge(chargeRequest: ChargeRequest): CompanyChargeRequest =
    MastercardChargeRequest.fromChargeRequest(chargeRequest)

  override protected def handleResponse(res: WSResponse): Unit = {
    res.status match {
      case Status.OK => ()
      case _ => handleFailure(res)
    }
  }

  private def handleFailure(res: WSResponse): Nothing = {
    val mcResponse = res.body[JsValue].as[MastercardChargeResponse]
    throw BusinessException(mcResponse.decline_reason)
  }

  override protected def getCompany: CreditCardCompany = CreditCardCompany.MASTERCARD
}
