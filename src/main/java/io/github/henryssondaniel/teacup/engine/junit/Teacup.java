package io.github.henryssondaniel.teacup.engine.junit;

import io.github.henryssondaniel.teacup.core.Server;
import io.github.henryssondaniel.teacup.core.logging.Factory;
import io.github.henryssondaniel.teacup.engine.Executor;
import io.github.henryssondaniel.teacup.engine.TeacupException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opentest4j.AssertionFailedError;

/**
 * Class that contains convenience methods to be used from a test.
 *
 * @since 1.0
 */
public enum Teacup {
  ;

  private static final String ERROR = "Could not retrieve the %s";
  private static final Executor EXECUTOR = ExecutorHolder.getExecutor();
  private static final Logger LOGGER = Factory.getLogger(Teacup.class);
  private static final String MESSAGE = "Getting the {0}: {1} with class {2}";

  /**
   * Returns the client.
   *
   * @param clazz the class of the client
   * @param name the name of the client
   * @param <T> the client type
   * @return the client
   * @since 1.0
   */
  public static <T> T getClient(Class<T> clazz, String name) {
    LOGGER.log(Level.FINE, MESSAGE, new Object[] {"client", name, clazz.getName()});

    try {
      return io.github.henryssondaniel.teacup.engine.Teacup.getClient(clazz, EXECUTOR, name);
    } catch (TeacupException e) {
      throw new AssertionFailedError(String.format(ERROR, "client"), e);
    }
  }

  /**
   * Returns the server.
   *
   * @param clazz the class of the server
   * @param name the name of the server
   * @param <T> the server type
   * @return the server
   * @since 1.0
   */
  public static <T extends Server> T getServer(Class<T> clazz, String name) {
    LOGGER.log(Level.FINE, MESSAGE, new Object[] {"server", name, clazz.getName()});

    try {
      return io.github.henryssondaniel.teacup.engine.Teacup.getServer(clazz, EXECUTOR, name);
    } catch (TeacupException e) {
      throw new AssertionFailedError(String.format(ERROR, "server"), e);
    }
  }
}
