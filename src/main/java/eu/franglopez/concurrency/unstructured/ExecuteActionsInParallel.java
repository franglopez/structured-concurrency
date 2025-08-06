package eu.franglopez.concurrency.unstructured;

import eu.franglopez.concurrency.Action;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ExecuteActionsInParallel {
  @SafeVarargs
  static <T> String run(Action<T> ... actions){
    try (ExecutorService executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory())) {
      var futures = new java.util.ArrayList<java.util.concurrent.CompletableFuture<T>>();
      for (Action<T> action : actions) {
        futures.add(java.util.concurrent.CompletableFuture.supplyAsync(action::run, executor));
      }
      return futures.stream()
          .map(java.util.concurrent.CompletableFuture::join)
          .map(Object::toString)
          .collect(Collectors.joining(","));
    }
  }
}
