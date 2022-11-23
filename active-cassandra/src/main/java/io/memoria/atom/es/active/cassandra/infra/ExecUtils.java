package io.memoria.atom.es.active.cassandra.infra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ExecUtils {

  public static Stream<Row> execSelect(CqlSession session, SimpleStatement st) {
    var rs = session.execute(st);
    return StreamSupport.stream(rs.spliterator(), false);
  }

  public static boolean exec(CqlSession session, SimpleStatement st) {
    return session.execute(st).wasApplied();
  }

  private ExecUtils() {}
}
