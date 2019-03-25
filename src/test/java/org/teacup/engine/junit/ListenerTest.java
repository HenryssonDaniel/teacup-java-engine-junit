package org.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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
import org.teacup.core.Fixture;

class ListenerTest {
  private final Executor executor = ExecutorHolder.getExecutor();
  private final Fixture fixture = mock(Fixture.class);
  private final TestDescriptor testDescriptor = mock(TestDescriptor.class);
  private final TestExecutionListener testExecutionListener = new Listener();

  @BeforeEach
  @SuppressWarnings("unchecked")
  void beforeEach() {
    when(fixture.value()).thenReturn((Class) TestSetup.class);

    executor.executeFixture(fixture);

    var uniqueId = mock(UniqueId.class);

    when(testDescriptor.getType()).thenReturn(Type.CONTAINER);
    when(testDescriptor.getUniqueId()).thenReturn(uniqueId);
  }

  @Test
  void executionStarted() {
    when(testDescriptor.getSource()).thenReturn(Optional.of(ClassSource.from(getClass())));
    testExecutionListener.executionStarted(TestIdentifier.from(testDescriptor));
    assertThat(executor.getCurrentSetup()).isEmpty();
  }

  @Test
  void executionStartedWhenNotClass() {
    testExecutionListener.executionStarted(TestIdentifier.from(testDescriptor));
    assertThat(executor.getCurrentSetup()).containsInstanceOf(TestSetup.class);
  }
}
