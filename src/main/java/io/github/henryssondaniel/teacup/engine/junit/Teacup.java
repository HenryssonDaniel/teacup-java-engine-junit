package io.github.henryssondaniel.teacup.engine.junit;

import io.github.henryssondaniel.teacup.core.Executor;
import io.github.henryssondaniel.teacup.core.Server;
import io.github.henryssondaniel.teacup.core.TeacupException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;

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
  private static final String MESSAGE = "Getting the {0}: {1} with class {2}";

  /**
   * Returns the client.
   *
   * @param clazz the class of the client
   * @param name the name of the client
   * @param <T> the client type
   * @return the client
   */
  public static <T> T getClient(Class<T> clazz, String name) {
    LOGGER.log(Level.FINE, MESSAGE, new Object[] {"client", name, clazz.getName()});
    T client = null;

    try {
      client = io.github.henryssondaniel.teacup.core.Teacup.getClient(clazz, EXECUTOR, name);
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
    LOGGER.log(Level.FINE, MESSAGE, new Object[] {"server", name, clazz.getName()});
    T server = null;

    try {
      server = io.github.henryssondaniel.teacup.core.Teacup.getServer(clazz, EXECUTOR, name);
    } catch (TeacupException e) {
      Assertions.fail(String.format(ERROR, "server"), e);
    }

    return server;
  }
}
