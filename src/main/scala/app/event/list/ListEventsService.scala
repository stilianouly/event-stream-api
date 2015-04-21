package app.event.list

import app.action.AppAction
import app.action.AppAction.Script
import app.action.EventStoreAction.{getEventsCount, listEvents}
import app.model._

import scalaz.Scalaz._

object ListEventsService {

  def getEvents(entityId: Option[EntityId], maybePageSize: Option[Int], pageNumber: Option[Long]): Script[LinkedResponse] = for {
    config <- AppAction.getConfig
    pageSize = maybePageSize.getOrElse(config.defaultPageSize)
    events <- listEvents(entityId, pageSize, pageNumber)
    totalCount <- getEventsCount(entityId)
    lastPage = Math.max((totalCount/pageSize)-1,0)
  } yield LinkedResponse(events, pageNumber, pageSize, createLinks(entityId, pageNumber.getOrElse(lastPage), lastPage, pageSize))

  private def createLinks(entityId: Option[EntityId], currentPage: Long, lastPage: Long, pageSize: Int): Links = {
    val link = makeLink(entityId , pageSize) _
    val nextPage = if (currentPage == lastPage) None else link(currentPage+1).some
    val prevPage = if (currentPage == 0) None else link(currentPage-1).some
    Links(link(currentPage), link(0), nextPage, prevPage)
  }

  private def makeLink(entityId: Option[EntityId], pageSize: Int)(pageNumber: Long) = {
    val entity = ~entityId.map(id => s"/${id.id}")
    URI(s"/events$entity?pageNumber=$pageNumber&pageSize=$pageSize")
  }

}