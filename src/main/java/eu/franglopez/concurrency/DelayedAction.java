package eu.franglopez.concurrency;

import java.time.Duration;
import org.slf4j.Logger;

public record DelayedAction<R>(String name, R result, Duration delay) implements Action<R> {
  private static final Logger log = org.slf4j.LoggerFactory.getLogger(DelayedAction.class);


  public DelayedAction {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name must not be null or blank");
    }
    if (result == null) {
      throw new IllegalArgumentException("Result must not be null");
    }
    if (delay == null || delay.isNegative()) {
      throw new IllegalArgumentException("Delay must not be negative");
    }
  }

  public R run() {
    try {
      log.info("Starting delayed action: {}", name);
      Thread.sleep(delay);
      log.info("Action finished: {}", name);
      return result;
    } catch (InterruptedException e) {
      log.info("Action interrupted: {}", name);
      throw new RuntimeException("Execution interrupted", e);
    }
  }
}
