package controllers

import api.requests.ChargeRequest
import api.response.ChargeResponse
import com.google.inject.Inject
import exceptions.{BusinessException, ValidationException}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Result}
import services.PaymentsService

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class PaymentsController @Inject()(cc: ControllerComponents,
                                   paymentsService: PaymentsService)
                                  (implicit val ec: ExecutionContext) extends AbstractController(cc) {

  val logger: Logger = Logger("play")

  def charge: Action[JsValue] = Action.async(parse.json) { req =>
    logger.info(s"Received a charge request")

    val merchantIdentifier: String = req.headers
      .get("merchant-identifier")
      .getOrElse("")

    val parseRes = Try { req.body.as[ChargeRequest] }

    parseRes match {
      case Failure(exception) =>
        logger.error(s"Failed to parse json for charge request, errors: $exception")
        Future successful BadRequest("")
      case Success(chargeRequest) =>
        paymentsService.charge(chargeRequest, merchantIdentifier)
          .map(_ => Ok(""))
          .recoverWith { e => Future successful handleChargeErrors(e) }
    }
  }

  private def handleChargeErrors(e: Throwable): Result = {
    e match {
      case BusinessException(_) => Ok(Json.toJson(ChargeResponse("Card declined")))
      case ValidationException(_) => BadRequest("")
      case _ => InternalServerError("")
    }
  }
}
