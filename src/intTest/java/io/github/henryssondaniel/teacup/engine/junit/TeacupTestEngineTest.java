package io.github.henryssondaniel.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import io.github.henryssondaniel.teacup.engine.Fixture;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.engine.config.DefaultJupiterConfiguration;
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

@Fixture(TestSetup.class)
class TeacupTestEngineTest {
  private final UniqueId uniqueId = UniqueId.parse("[test:test]");

  @Test
  void createExecutionContext() {
    ConfigurationParameters configurationParameters = new TestConfigurationParameters();
    var defaultJupiterConfiguration = new DefaultJupiterConfiguration(configurationParameters);
    EngineExecutionListener engineExecutionListener = new TestEngineExecutionListener();

    var jupiterEngineExecutionContext =
        new TeacupTestEngine()
            .createExecutionContext(
                new ExecutionRequest(
                    new JupiterEngineDescriptor(uniqueId, defaultJupiterConfiguration),
                    engineExecutionListener,
                    configurationParameters));

    assertThat(jupiterEngineExecutionContext.getConfiguration())
        .isSameAs(defaultJupiterConfiguration);
    assertThat(jupiterEngineExecutionContext.getExecutionListener())
        .isSameAs(engineExecutionListener);
  }

  @Test
  void discover() {
    var testDescriptor =
        new TeacupTestEngine()
            .discover(
                LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectPackage("io"))
                    .build(),
                uniqueId);

    List<? extends TestDescriptor> children = new ArrayList<>(testDescriptor.getChildren());
    assertThat(children).hasSize(3);

    if (((ClassSource) children.get(1).getSource().orElseThrow()).getJavaClass()
        == TeacupTest.class)
      fail("The test does not have a fixture and can thereby not be between two classes who does.");
  }

  @Test
  void getId() {
    assertThat(new TeacupTestEngine().getId()).isEqualTo("teacup");
  }

  private static final class TestEngineExecutionListener implements EngineExecutionListener {
    private static final Logger LOGGER =
        Logger.getLogger(TestEngineExecutionListener.class.getName());

    @Override
    public void dynamicTestRegistered(TestDescriptor testDescriptor) {
      LOGGER.log(Level.FINE, "Dynamic test registered");
    }

    @Override
    public void executionFinished(
        TestDescriptor testDescriptor, TestExecutionResult testExecutionResult) {
      LOGGER.log(Level.FINE, "Execution finished");
    }

    @Override
    public void executionSkipped(TestDescriptor testDescriptor, String reason) {
      LOGGER.log(Level.FINE, "Execution skipped");
    }

    @Override
    public void executionStarted(TestDescriptor testDescriptor) {
      LOGGER.log(Level.FINE, "Execution started");
    }

    @Override
    public void reportingEntryPublished(TestDescriptor testDescriptor, ReportEntry entry) {
      LOGGER.log(Level.FINE, "Report entry published");
    }
  }
}
