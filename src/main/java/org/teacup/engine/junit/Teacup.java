package org.teacup.engine.junit;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.teacup.core.Executor;
import org.teacup.core.TeacupException;

/**
 * Class that contains convenience methods to be used from a test.
 *
 * @since 1.0
 */
public enum Teacup {
  ;

  private static final Executor EXECUTOR = ExecutorHolder.getExecutor();
  private static final Logger LOGGER = Logger.getLogger(Teacup.class.getName());

  /**
   * Returns the client.
   *
   * @param clientClass the class of the client
   * @param name the name of the client
   * @param <T> the client type
   * @return the client
   */
  public static <T> T getClient(Class<T> clientClass, String name) {
    LOGGER.log(Level.FINE, "Getting the client: " + name + " with class: " + clientClass.getName());
    T client = null;

    try {
      client = org.teacup.core.Teacup.getClient(clientClass, EXECUTOR, name);
    } catch (TeacupException e) {
      Assertions.fail("Could not retrieve the client", e);
    }

    return client;
  }
}
