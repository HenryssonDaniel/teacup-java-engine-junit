package org.teacup.engine.junit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestDescriptor.Type;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.teacup.core.Executor;

class ListenerTest {
  private final Executor executor = mock(Executor.class);
  private final TestDescriptor testDescriptor = mock(TestDescriptor.class);
  private final TestExecutionListener testExecutionListener = new Listener();

  @BeforeEach
  void beforeEach() throws IllegalAccessException, NoSuchFieldException {
    org.teacup.core.Utils.setField(Listener.class, testExecutionListener, "executor", executor);

    var uniqueId = mock(UniqueId.class);

    when(testDescriptor.getType()).thenReturn(Type.CONTAINER);
    when(testDescriptor.getUniqueId()).thenReturn(uniqueId);
  }

  @Test
  void executionStarted() {
    when(testDescriptor.getSource()).thenReturn(Optional.of(ClassSource.from(getClass())));
    testExecutionListener.executionStarted(TestIdentifier.from(testDescriptor));
    verify(executor).executeFixture(null);
  }

  @Test
  void executionStartedWhenNotClass() {
    testExecutionListener.executionStarted(TestIdentifier.from(testDescriptor));
    verifyZeroInteractions(executor);
  }
}
