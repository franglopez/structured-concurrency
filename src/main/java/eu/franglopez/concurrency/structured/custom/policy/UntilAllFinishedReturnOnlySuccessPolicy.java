package eu.franglopez.concurrency.structured.custom.policy;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Stream;

public class UntilAllFinishedReturnOnlySuccessPolicy<T> implements StructuredTaskScope.Joiner<T, Stream<T>> {
  Queue<T> queue = new ConcurrentLinkedQueue<>();

  @Override
  public boolean onComplete(StructuredTaskScope.Subtask<? extends T> subtask) {

    if (subtask.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
      // gather only successful results
      queue.offer(subtask.get());
    }
    //Do not stop even if one subtask fails
    return false;
  }

  @Override
  public Stream<T> result() throws Throwable {
    return queue.stream();
  }
}
