package io.memoria.active.web;

import io.helidon.http.Status;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class HealthController implements HttpService {
  private static final Logger log = LoggerFactory.getLogger(HealthController.class.getName());
  private final Callable<String> checkMessage;

  public HealthController(Callable<String> checkMessage) {
    this.checkMessage = checkMessage;
  }

  @Override
  public void routing(HttpRules httpRules) {
    httpRules.get("/", this::health);
  }

  private void health(ServerRequest req, ServerResponse res) {
    try {
      String msg = "Health check succeeded: %s".formatted(checkMessage.call());
      log.info(msg);
      res.status(Status.OK_200).send();
    } catch (Exception e) {
      String msg = "Health check failed: %s".formatted(e.getMessage());
      log.error(msg);
      log.debug("Health check failed:", e);
      res.status(Status.INTERNAL_SERVER_ERROR_500).send();
    }
  }
}
