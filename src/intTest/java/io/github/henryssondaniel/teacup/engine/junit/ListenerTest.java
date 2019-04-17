package io.github.henryssondaniel.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.henryssondaniel.teacup.core.Executor;
import io.github.henryssondaniel.teacup.core.Fixture;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.engine.config.CachingJupiterConfiguration;
import org.junit.jupiter.engine.config.DefaultJupiterConfiguration;
import org.junit.jupiter.engine.config.JupiterConfiguration;
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

@Fixture(TestSetup.class)
class ListenerTest {
  private static final Executor EXECUTOR = ExecutorHolder.getExecutor();

  private final JupiterConfiguration jupiterConfiguration =
      new CachingJupiterConfiguration(
          new DefaultJupiterConfiguration(new TestConfigurationParameters()));
  private final TestExecutionListener testExecutionListener = new Listener();
  private final UniqueId uniqueId = UniqueId.parse("[test:test]");

  @Test
  void executionStarted() {
    EXECUTOR.executeFixture(null);
    testExecutionListener.executionStarted(
        TestIdentifier.from(
            new ClassTestDescriptor(uniqueId, ListenerTest.class, jupiterConfiguration)));

    assertThat(EXECUTOR.getCurrentSetup()).containsInstanceOf(TestSetup.class);
  }

  @Test
  void executionStartedWhenNotClass() {
    EXECUTOR.executeFixture(null);
    testExecutionListener.executionStarted(
        TestIdentifier.from(
            new TestMethodTestDescriptor(
                uniqueId,
                ListenerTest.class,
                ListenerTest.class.getMethods()[0],
                jupiterConfiguration)));

    assertThat(ExecutorHolder.getExecutor().getCurrentSetup()).isEmpty();
  }

  private static final class TestConfigurationParameters implements ConfigurationParameters {
    private static final Logger LOGGER =
        Logger.getLogger(TestConfigurationParameters.class.getName());

    @Override
    public Optional<String> get(String key) {
      LOGGER.log(Level.FINE, "Get");
      return Optional.empty();
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
      LOGGER.log(Level.FINE, "Get boolean");
      return Optional.empty();
    }

    @Override
    public int size() {
      LOGGER.log(Level.FINE, "Size");
      return 0;
    }
  }
}
