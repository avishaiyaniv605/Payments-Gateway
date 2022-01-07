package services

import inMemoryDb.{InMemoryStatusEntry, InMemoryStatusRepo}

class FailureStatusService {
  def getAggStatuses(identifier: String): Seq[InMemoryStatusEntry] = {
    InMemoryStatusRepo.getAggStatuses(identifier)
  }

  def saveAggStatuses(identifier: String, error: String): Unit = {
    InMemoryStatusRepo.saveOrUpdateAggStatuses(identifier, error)
  }
}
