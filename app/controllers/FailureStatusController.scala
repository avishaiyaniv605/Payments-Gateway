package controllers

import com.google.inject.Inject
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.FailureStatusService

import scala.concurrent.{ExecutionContext, Future}

class FailureStatusController @Inject()(cc: ControllerComponents,
                                        failureStatusService: FailureStatusService)
                                       (implicit val ec: ExecutionContext) extends AbstractController(cc) {

  val logger: Logger = Logger("play")

  def getFailureStatuses: Action[AnyContent] = Action.async { req =>
    logger.info(s"Received a charge request")

    req.headers.get("merchant-identifier") match {
      case Some(identifier) => Future successful Ok(Json.toJson(failureStatusService.getAggStatuses(identifier)))
      case None => Future successful BadRequest("")
    }
  }
}
