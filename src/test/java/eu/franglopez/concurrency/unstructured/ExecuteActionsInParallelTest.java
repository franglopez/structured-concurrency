package eu.franglopez.concurrency.unstructured;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.awaitility.Awaitility.await;

import eu.franglopez.concurrency.Action;
import eu.franglopez.concurrency.DelayedAction;
import eu.franglopez.concurrency.FailingAction;
import eu.franglopez.concurrency.ConcurrentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class ExecuteActionsInParallelTest {
  ConcurrentTest<String> concurrentTest;

  @BeforeEach
  void setUp() {
    concurrentTest = new ConcurrentTest<>();
  }

  @Test
  @Timeout(5)
  void given2elements_ShouldWaitBothToFinish() {
    //given
    Action<String> pizza = new DelayedAction<>("pizza", "pizza made", ofMillis(500));
    Action<String> sandwich = new DelayedAction<>("sandwich", "sandwich made", ofMillis(200));

    //when
    concurrentTest.runInDifferentThread(() ->
        ExecuteActionsInParallel.run(pizza, sandwich));

    //then
    await().atLeast(ofMillis(1)).atMost(ofSeconds(4))
        .untilAsserted(() -> concurrentTest.assertResult("pizza made,sandwich made"));
  }

  @Test
  @Timeout(5)
  void givenFailingElementFinishLast_ShouldFailWithFailedElement() {
    //given
    Action<String> pizza = new DelayedAction<>("pizza", "pizza made", ofMillis(200));
    Action<String> sandwich = new FailingAction<>("sandwich", ofMillis(1000));


    //when
    concurrentTest.runInDifferentThread(() ->
        ExecuteActionsInParallel.run(pizza, sandwich));

    //then
    await().atLeast(ofMillis(100)).atMost(ofSeconds(4))
        .untilAsserted(() -> concurrentTest.assertError("sandwich failed"));
  }

  @Test
  @Timeout(5)
  void givenFirstElementFailing_ShouldWaitAllToFinish() {
    //given
    Action<String> pizza = new FailingAction<>("pizza", ofMillis(200));
    Action<String> sandwich = new DelayedAction<>("sandwich", "sandwich made", ofMillis(2000));


    //when
    concurrentTest.runInDifferentThread(() ->
        ExecuteActionsInParallel.run(pizza, sandwich));

    //then
    await().atLeast(ofMillis(100)).atMost(ofSeconds(4))
        .untilAsserted(() -> concurrentTest.assertError("pizza failed"));
  }

  @Test
  @Timeout(5)
  void givenTwoElementFailing_ShouldWaitAllToFinishAndReturnFirstElement() {
    //given
    Action<String> pizza = new FailingAction<>("pizza", ofMillis(200));
    Action<String> sandwich = new FailingAction<>("sandwich", ofMillis(2000));


    //when
    concurrentTest.runInDifferentThread(() ->
        ExecuteActionsInParallel.run(pizza, sandwich));

    //then
    await().atLeast(ofMillis(100)).atMost(ofSeconds(4))
        .untilAsserted(() -> concurrentTest.assertError("pizza failed"));
  }
}
