package eu.franglopez.concurrency.structured;

import eu.franglopez.concurrency.Action;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Stream;

public class UntilFailOrAllSuccessDefaultPolicyProcess {

  static <T,U> String run(Action<T> [] actions, Action<U> [] action) {
    if (actions == null || actions.length == 0) {
      throw new IllegalArgumentException("At least one action must be provided");
    }
    try (var scope = StructuredTaskScope.open()) {
      // Each action will be executed in its own virtual thread
      List<StructuredTaskScope.Subtask<T>> results = Arrays.stream(actions).map(act -> scope.fork(act::run))
          .toList();
      List<StructuredTaskScope.Subtask<U>> otherResults = Arrays.stream(action).map(act -> scope.fork(act::run)).toList();


      // Wait for all actions to complete
      scope.join();
      return Stream.concat(
          results.stream().map(StructuredTaskScope.Subtask::get).map(Object::toString),
          otherResults.stream().map(StructuredTaskScope.Subtask::get).map(Object::toString))
          .collect(java.util.stream.Collectors.joining(","));

    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

  }
}
