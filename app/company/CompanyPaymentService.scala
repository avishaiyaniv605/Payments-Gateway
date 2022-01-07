package company

import api.requests.ChargeRequest
import com.google.inject.Inject
import company.CreditCardCompany.CreditCardCompany
import exceptions.BusinessException
import play.api.Logger
import play.api.libs.ws.{WSClient, WSResponse}
import play.libs.Json

import scala.concurrent.{ExecutionContext, Future}


abstract class CompanyPaymentService @Inject()(val ws: WSClient)
                                              (implicit ec: ExecutionContext) {
  protected val logger: Logger = Logger("play")

  protected def retryCount: Int = 3
  protected def companyPaymentUrl: String
  protected def getRequestHeaders: (String, String)
  protected def chargeToCompanyCharge(cr: ChargeRequest): CompanyChargeRequest
  protected def handleResponse(res: WSResponse): Unit
  protected def getCompany: CreditCardCompany

  private val jsonRequestHeader = ("Content-type", "application/json")

  def payToCompany(chargeRequest: ChargeRequest, merchantId: String): Future[Unit] = {
    logger.info(s"sending request to pay company for merchant: $merchantId")

    val companyRequest: CompanyChargeRequest = chargeToCompanyCharge(chargeRequest)
    val requestBody: String = Json.stringify(Json.toJson(companyRequest))

    payWithRetry(requestBody, retryCount)
  }

  private def payWithRetry(requestBody: String, attempt: Int): Future[Unit] = {
    sendPayRequest(requestBody)
      .recoverWith { e => e match {
          case BusinessException(_) if attempt != 0 =>
            logger.info(s"Failed to communicate with company $getCompany")
            val next: Int = getNextRetry(attempt)
            if (next == 0) throw e

            Thread.sleep(next * 1000L)

            logger.info(s"retrying after $next sec")
            payWithRetry(requestBody, attempt - 1)

          case t: Throwable => throw t
        }
      }
  }

  private def getNextRetry(currAttempt: Int): Int = {
    if (currAttempt == 0) 0
    else ((retryCount + 1 - currAttempt) * (retryCount + 1 - currAttempt))
  }

  private def sendPayRequest(requestBody: String): Future[Unit] = {
    ws.url(companyPaymentUrl)
      .withHttpHeaders(getRequestHeaders, jsonRequestHeader)
      .post(requestBody)
      .map { res => handleResponse(res) }
  }
}