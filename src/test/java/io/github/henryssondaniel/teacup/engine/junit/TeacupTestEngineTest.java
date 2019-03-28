package io.github.henryssondaniel.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.engine.config.CachingJupiterConfiguration;
import org.junit.jupiter.engine.config.DefaultJupiterConfiguration;
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.UniqueId;

class TeacupTestEngineTest {
  private final TestEngine testEngine = new TeacupTestEngine();
  private final UniqueId uniqueId = mock(UniqueId.class);

  @Test
  void discoverNothingWithNoSelectors() {
    var configurationParameters = mock(ConfigurationParameters.class);

    var engineDiscoveryRequest = mock(EngineDiscoveryRequest.class);
    when(engineDiscoveryRequest.getConfigurationParameters()).thenReturn(configurationParameters);

    assertThat(testEngine.discover(engineDiscoveryRequest, uniqueId).getChildren()).isEmpty();
  }

  @Test
  void execute() {
    var configurationParameters = mock(ConfigurationParameters.class);

    TestDescriptor testDescriptor =
        new JupiterEngineDescriptor(
            uniqueId,
            new CachingJupiterConfiguration(
                new DefaultJupiterConfiguration(configurationParameters)));

    var executionRequest = mock(ExecutionRequest.class);
    var engineExecutionListener = mock(EngineExecutionListener.class);

    when(executionRequest.getEngineExecutionListener()).thenReturn(engineExecutionListener);
    when(executionRequest.getRootTestDescriptor()).thenReturn(testDescriptor);

    testEngine.execute(executionRequest);

    verify(executionRequest, times(2)).getEngineExecutionListener();
    verify(executionRequest, times(2)).getRootTestDescriptor();
  }

  @Test
  void getId() {
    assertThat(testEngine.getId()).isSameAs("teacup");
  }
}
