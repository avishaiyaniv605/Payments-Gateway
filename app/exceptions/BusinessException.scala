package exceptions

case class BusinessException(reason: String) extends Throwable
