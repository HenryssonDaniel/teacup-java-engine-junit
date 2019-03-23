package org.teacup.engine.junit;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.teacup.core.Executor;
import org.teacup.core.Server;
import org.teacup.core.TeacupException;

/**
 * Class that contains convenience methods to be used from a test.
 *
 * @since 1.0
 */
public enum Teacup {
  ;

  private static final String ERROR = "Could not retrieve the %s";
  private static final Executor EXECUTOR = ExecutorHolder.getExecutor();
  private static final Logger LOGGER = Logger.getLogger(Teacup.class.getName());
  private static final String MESSAGE = "Getting the %s: %s with class %s";

  /**
   * Returns the client.
   *
   * @param clazz the class of the client
   * @param name the name of the client
   * @param <T> the client type
   * @return the client
   */
  public static <T> T getClient(Class<T> clazz, String name) {
    LOGGER.log(Level.FINE, String.format(MESSAGE, "client", name, clazz.getName()));
    T client = null;

    try {
      client = org.teacup.core.Teacup.getClient(clazz, EXECUTOR, name);
    } catch (TeacupException e) {
      Assertions.fail(String.format(ERROR, "client"), e);
    }

    return client;
  }

  /**
   * Returns the server.
   *
   * @param clazz the class of the server
   * @param name the name of the server
   * @param <T> the server type
   * @return the server
   */
  public static <T extends Server> T getServer(Class<T> clazz, String name) {
    LOGGER.log(Level.FINE, String.format(MESSAGE, "server", name, clazz.getName()));
    T server = null;

    try {
      server = org.teacup.core.Teacup.getServer(clazz, EXECUTOR, name);
    } catch (TeacupException e) {
      Assertions.fail(String.format(ERROR, "server"), e);
    }

    return server;
  }
}
