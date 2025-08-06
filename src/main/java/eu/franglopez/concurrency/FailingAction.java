package eu.franglopez.concurrency;

import java.time.Duration;
import org.slf4j.Logger;

public record FailingAction<R>(String name, Duration delay, R ignoredResultByException)
    implements Action<R> {
  private static final Logger log = org.slf4j.LoggerFactory.getLogger(FailingAction.class);

  public FailingAction {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name must not be null or blank");
    }
    if (delay == null || delay.isNegative()) {
      throw new IllegalArgumentException("Delay must not be negative");
    }
  }

  public FailingAction(String name, Duration delay) {
    this(name, delay, null);
  }

  @Override
  public R run() {
    log.info("Starting failing action: {}", name);
    try {
      Thread.sleep(delay);
      log.error("Action {} failed", name);
      throw new RuntimeException(name + " failed");
    } catch (InterruptedException e) {
      log.info("Action {} interrupted", name);
      throw new RuntimeException("Execution interrupted");
    }
  }
}
