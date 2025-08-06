package eu.franglopez.concurrency;

public interface Action<R> {
    /**
     * Executes the action and returns a result.
     *
     * @return the result of the action
     * @throws RuntimeException if the action fails or is interrupted
     */
    R run() throws RuntimeException;
}
