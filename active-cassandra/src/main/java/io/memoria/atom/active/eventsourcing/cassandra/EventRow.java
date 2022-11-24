package io.memoria.atom.active.eventsourcing.cassandra;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

record EventRow(String stateId, int seqId, String value, long createdAt) {
  // StateId
  static final String stateIdCol = "state_id";
  static final DataType stateIdColType = DataTypes.TEXT;
  // SeqId
  static final String seqCol = "seq_id";
  static final DataType seqColType = DataTypes.INT;
  // Value
  static final String eventCol = "value";
  static final DataType eventColType = DataTypes.TEXT;
  // CreatedAt
  static final String createdAtCol = "created_at";
  static final DataType createAtColType = DataTypes.BIGINT;

  public EventRow {
    if (seqId < 0)
      throw new IllegalArgumentException("Seq can't be less than zero!.");
  }

  public EventRow(String stateId, int seq, String event) {
    this(stateId, seq, event, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
  }

  public static EventRow from(Row row) {
    var rStateId = Objects.requireNonNull(row.getString(stateIdCol));
    var rSeqId = row.getInt(seqCol);
    var rCreatedAt = row.getLong(createdAtCol);
    var rEvent = Objects.requireNonNull(row.getString(eventCol));
    return new EventRow(rStateId, rSeqId, rEvent, rCreatedAt);
  }
}
