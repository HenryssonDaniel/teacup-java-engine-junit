package org.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.UniqueId;

class TeacupTestEngineTest {
  private final TestEngine testEngine = new TeacupTestEngine();

  @Test
  void discoverNothingWithNoSelectors() {
    var engineDiscoveryRequest = mock(EngineDiscoveryRequest.class);
    var uniqueId = mock(UniqueId.class);

    assertThat(testEngine.discover(engineDiscoveryRequest, uniqueId).getChildren()).isEmpty();
  }

  @Test
  void execute() {
    var testDescriptor = mock(TestDescriptor.class);
    var executionRequest = mock(ExecutionRequest.class);
    var engineExecutionListener = mock(EngineExecutionListener.class);

    when(executionRequest.getEngineExecutionListener()).thenReturn(engineExecutionListener);
    when(executionRequest.getRootTestDescriptor()).thenReturn(testDescriptor);

    testEngine.execute(executionRequest);

    verify(executionRequest).getConfigurationParameters();
    verify(executionRequest, times(2)).getEngineExecutionListener();
    verify(executionRequest).getRootTestDescriptor();
  }

  @Test
  void getId() {
    assertThat(testEngine.getId()).isSameAs("teacup");
  }
}
