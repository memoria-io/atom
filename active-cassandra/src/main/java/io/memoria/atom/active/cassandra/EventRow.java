package io.memoria.atom.active.cassandra;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import io.memoria.atom.core.eventsourcing.StateId;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

record EventRow(StateId stateId, int seqId, long createdAt, String event) {
  // StateId
  static final String stateIdCol = "state_id";
  static final DataType stateIdColType = DataTypes.TEXT;
  // SeqId
  static final String seqCol = "seq_id";
  static final DataType seqColType = DataTypes.INT;
  // CreatedAt
  static final String createdAtCol = "created_at";
  static final DataType createAtColType = DataTypes.BIGINT;
  // Event
  static final String eventCol = "event";
  static final DataType eventColType = DataTypes.TEXT;

  public EventRow {
    if (seqId < 0)
      throw new IllegalArgumentException("Seq can't be less than zero!.");
  }

  public EventRow(StateId stateId, int seq, String event) {
    this(stateId, seq, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), event);
  }

  public static EventRow from(Row row) {
    var rStateId = Objects.requireNonNull(row.getString(stateIdCol));
    var rSeqId = row.getInt(seqCol);
    var rCreatedAt = row.getLong(createdAtCol);
    var rEvent = Objects.requireNonNull(row.getString(eventCol));
    return new EventRow(StateId.of(rStateId), rSeqId, rCreatedAt, rEvent);
  }
}
