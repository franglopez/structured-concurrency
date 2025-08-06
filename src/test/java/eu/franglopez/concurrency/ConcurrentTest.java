package eu.franglopez.concurrency;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public record ConcurrentTest<T>(AtomicReference<T> result, AtomicReference<Throwable> error) {

    public ConcurrentTest() {
        this(new AtomicReference<>(), new AtomicReference<>());
    }

  public void runInDifferentThread(Supplier<T> action) {
    Thread.ofVirtual().start(() -> {
      try {
        result.set(action.get());
      } catch (Throwable t) {
        error.set(t);
      }
    });
  }

  public void assertResult(T expectedResult) {
    assertThat(result.get()).isEqualTo(expectedResult);
  }

  public void assertError(String expectedErrorMessage) {
    assertThat(error.get()).hasRootCauseMessage(expectedErrorMessage);
  }

  public void assertErrorType(Class<?> errorClazz) {
    assertThat(error.get()).isInstanceOf(errorClazz);
  }
}
