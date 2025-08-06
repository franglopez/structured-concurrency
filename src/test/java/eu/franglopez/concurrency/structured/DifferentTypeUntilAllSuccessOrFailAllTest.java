package eu.franglopez.concurrency.structured;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.awaitility.Awaitility.await;

import eu.franglopez.concurrency.Action;
import eu.franglopez.concurrency.ConcurrentTest;
import eu.franglopez.concurrency.DelayedAction;
import eu.franglopez.concurrency.FailingAction;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class DifferentTypeUntilAllSuccessOrFailAllTest {


  ConcurrentTest<String> concurrentTest;

  @BeforeEach
  void setUp() {
    concurrentTest = new ConcurrentTest<>();
  }


  @Test
  @Timeout(5)
  void given2elements_ShouldWaitBothToFinish() {
    //given
    Action<String> pizza = new DelayedAction<>("pizza", "pizza made", Duration.ofSeconds(1));
    Action<String> burger = new DelayedAction<>("burger", "burger made", Duration.ofSeconds(3));
    Action<Integer> sandwich = new DelayedAction<>("sandwich", 42, Duration.ofSeconds(2));
    Action<String>[] actions = new Action[] {pizza, burger};
    Action<Integer>[] actions2 = new Action[] {sandwich};

    //when
    concurrentTest.runInDifferentThread(() ->
        UntilFailOrAllSuccessDefaultPolicyProcess.run(actions, actions2));

    //then
    await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> concurrentTest.assertResult(
        "pizza made,burger made,42"
    ));
  }

  @Test
  @Timeout(5)
  void givenFailingElementFinishLast_ShouldFailWithFailedElement() {
    //given
    Action<String> pizza = new DelayedAction<>("pizza", "pizza made", Duration.ofSeconds(1));
    Action<String> burger = new FailingAction<>("burger", Duration.ofSeconds(3));
    Action<Integer> sandwich = new DelayedAction<>("sandwich", 42, Duration.ofSeconds(2));
    Action<String>[] actions = new Action[] {pizza, burger};
    Action<Integer>[] actions2 = new Action[] {sandwich};

    //when
    concurrentTest.runInDifferentThread(() ->
        UntilFailOrAllSuccessDefaultPolicyProcess.run(actions, actions2));

    //then
    await().atLeast(ofMillis(100)).atMost(ofSeconds(4))
        .untilAsserted(() -> concurrentTest.assertError("burger failed"));
  }

    @Test
    @Timeout(2)
    void givenFirstElementFailing_ShouldInterruptRestOfElements() {
    //given
    Action<String> pizza = new FailingAction<>("pizza", Duration.ofSeconds(1));
    Action<String> burger = new DelayedAction<>("burger", "burger made", Duration.ofSeconds(3));
    Action<Integer> sandwich = new DelayedAction<>("sandwich", 42, Duration.ofSeconds(4));
    Action<String>[] actions = new Action[] {pizza, burger};
    Action<Integer>[] actions2 = new Action[] {sandwich};

    //when
    concurrentTest.runInDifferentThread(() ->
        UntilFailOrAllSuccessDefaultPolicyProcess.run(actions, actions2));

    //then
    await().atLeast(ofMillis(1)).atMost(ofSeconds(2))
        .untilAsserted(() -> concurrentTest.assertError("pizza failed"));
      concurrentTest.assertResult(null);
    }

    @Test
    @Timeout(3)
    void givenMiddleElementFailing_ShouldStopExecuting() {
        //given
        Action<String> pizza = new DelayedAction<>("pizza","pizza made", Duration.ofSeconds(1));
        Action<String> burger = new FailingAction<>("burger", Duration.ofSeconds(2));
        Action<Integer> sandwich = new DelayedAction<>("sandwich", 42, Duration.ofSeconds(4));
        Action<String>[] actions = new Action[] {pizza, burger};
        Action<Integer>[] actions2 = new Action[] {sandwich};

        //when
        concurrentTest.runInDifferentThread(() ->
            UntilFailOrAllSuccessDefaultPolicyProcess.run(actions, actions2));

        //then
        await().atLeast(ofMillis(100)).atMost(ofSeconds(4))
            .untilAsserted(() -> concurrentTest.assertError("burger failed"));
        concurrentTest.assertResult(null);
    }

    @Test
    @Timeout(2)
    void givenAllElementsFailing_ShouldReturnFirstElement() {
        //given
        Action<String> pizza = new FailingAction<>("pizza", Duration.ofSeconds(1));
        Action<String> burger = new FailingAction<>("burger", Duration.ofSeconds(3));
        Action<Integer> sandwich = new FailingAction<>("sandwich", Duration.ofSeconds(2));
        Action<String>[] actions = new Action[] {pizza, burger};
        Action<Integer>[] actions2 = new Action[] {sandwich};

        //when
        concurrentTest.runInDifferentThread(() ->
            UntilFailOrAllSuccessDefaultPolicyProcess.run(actions, actions2));

        //then
        await().atLeast(ofMillis(100)).atMost(ofSeconds(4))
            .untilAsserted(() -> concurrentTest.assertError("pizza failed"));
    }

}
