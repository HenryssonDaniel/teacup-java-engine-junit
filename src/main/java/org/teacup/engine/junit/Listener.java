package org.teacup.engine.junit;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.teacup.core.Executor;
import org.teacup.core.Fixture;

/**
 * Teacup test execution listener.
 *
 * @since 1.0
 */
public class Listener implements TestExecutionListener {
  private static final Logger LOGGER = Logger.getLogger(Listener.class.getName());
  private final Executor executor = ExecutorHolder.getExecutor();

  @Override
  public void executionStarted(TestIdentifier testIdentifier) {
    TestExecutionListener.super.executionStarted(testIdentifier);

    LOGGER.log(Level.FINE, "executionStarted: " + testIdentifier.getDisplayName());
    var testSource = testIdentifier.getSource().orElse(null);

    if (testSource instanceof ClassSource)
      executor.executeFixture(
          ((ClassSource) testSource).getJavaClass().getAnnotation(Fixture.class));
  }
}
