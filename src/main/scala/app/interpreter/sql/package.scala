package app.interpreter

import java.sql.Timestamp
import java.time.{ZoneOffset, OffsetDateTime}

import app.model._
import argonaut._, Argonaut._

package object sql {
  def fromTimestamp(ts: java.sql.Timestamp): OffsetDateTime = OffsetDateTime.ofInstant(ts.toInstant, ZoneOffset.UTC)

  def fromOffsetDateTime(date: OffsetDateTime) = Timestamp.from(date.toInstant)

  def createSnapshot(id: String, entityId: String, systemName: String,
                     timestamp: Timestamp, body: String): Snapshot =
    Snapshot(SnapshotId(id), EntityId(entityId),
      SystemName(systemName), fromTimestamp(timestamp), body.parseOption.getOrElse(Json()))

  def snapshotToFields(snapshot: Snapshot): SnapshotsTable.Fields =
    (snapshot.id.id, snapshot.entityId.id, snapshot.systemName.name, fromOffsetDateTime(snapshot.timestamp), snapshot.body.nospaces)

  def eventToFields(event: Event): EventsTable.Fields =
    (event.id.id, event.entityId.id, event.systemName.name,
      fromOffsetDateTime(event.createdTimestamp), fromOffsetDateTime(event.suppliedTimestamp), event.body.nospaces)

  def createEvent(id:String, entityId:String, systemName:String, createdTimestamp:Timestamp, suppliedTimestamp:Timestamp,body:String) =
  Event(EventId(id), EntityId(entityId), SystemName(systemName), fromTimestamp(createdTimestamp), fromTimestamp(suppliedTimestamp), body.parse.getOrElse(Json()))
}
