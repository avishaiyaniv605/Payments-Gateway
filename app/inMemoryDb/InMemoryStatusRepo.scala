package inMemoryDb

import exceptions.UnknownIdentifierException
import play.api.libs.json.{Json, OFormat}

import scala.collection.mutable
import scala.language.postfixOps

case class InMemoryStatusEntry(reason: String, count: Int)
object InMemoryStatusEntry {
  implicit lazy val format: OFormat[InMemoryStatusEntry] = Json.format[InMemoryStatusEntry]
}

object InMemoryStatusRepo {
  private val inMemoryDb = mutable.Map[String, mutable.Map[String, Int]]()

  def getAggStatuses(identifier: String): Seq[InMemoryStatusEntry] = {
    inMemoryDb.get(identifier) match {
      case Some(statusesMap) => buildStatusesRes(statusesMap)
      case _ => throw UnknownIdentifierException()
    }
  }

  def saveOrUpdateAggStatuses(identifier: String, reason: String): Unit = {
    createIfNeeded(identifier, reason)
    inMemoryDb(identifier)(reason) += 1
  }

  private def createIfNeeded(identifier: String, reason: String): Unit = {
    if (!(inMemoryDb isDefinedAt identifier)) {
      val newReasonsMap = mutable.Map[String, Int]() += (reason -> 0)
      inMemoryDb += (identifier -> newReasonsMap)
    } else if (!(inMemoryDb(identifier) isDefinedAt reason)) {
      inMemoryDb(identifier) += (reason -> 0)
    }
  }

  private def buildStatusesRes(statusesMap: mutable.Map[String, Int]): Seq[InMemoryStatusEntry] = {
    statusesMap.map { mapEntry: (String, Int) =>
      InMemoryStatusEntry(mapEntry._1, mapEntry._2)
    }.toSeq
  }
}
