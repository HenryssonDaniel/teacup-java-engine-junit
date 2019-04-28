package io.github.henryssondaniel.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import io.github.henryssondaniel.teacup.core.Executor;
import io.github.henryssondaniel.teacup.core.Fixture;
import io.github.henryssondaniel.teacup.core.reporting.TestCase;
import io.github.henryssondaniel.teacup.core.reporting.TestSuite;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestDescriptor.Type;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

class ListenerTest {
  private final Executor executor = ExecutorHolder.getExecutor();
  private final Fixture fixture = mock(Fixture.class);
  private final TestDescriptor testDescriptor = mock(TestDescriptor.class);
  private final TestExecutionListener testExecutionListener = new Listener();
  private final TestExecutionResult testExecutionResult = mock(TestExecutionResult.class);
  private final TestPlan testPlan = mock(TestPlan.class);

  @BeforeEach
  @SuppressWarnings("unchecked")
  void beforeEach() {
    when(fixture.value()).thenReturn((Class) TestSetup.class);

    var uniqueId = mock(UniqueId.class);

    when(testDescriptor.getType()).thenReturn(Type.CONTAINER);
    when(testDescriptor.getUniqueId()).thenReturn(uniqueId);

    executor.executeFixture(fixture);
  }

  @Test
  void executionFinished() {
    testExecutionListener.executionFinished(
        TestIdentifier.from(testDescriptor), testExecutionResult);
    verifyZeroInteractions(testExecutionResult);
  }

  @Test
  void executionFinishedWhenMethodAborted() throws IllegalAccessException, NoSuchFieldException {
    when(testDescriptor.getSource())
        .thenReturn(Optional.of(MethodSource.from(getClass().getMethods()[0])));
    when(testExecutionResult.getStatus()).thenReturn(Status.ABORTED);

    testExecutionListener.executionFinished(createTestIdentifier(), testExecutionResult);

    verify(testExecutionResult).getStatus();
    verify(testExecutionResult).getThrowable();
  }

  @Test
  void executionFinishedWhenMethodFailed() throws IllegalAccessException, NoSuchFieldException {
    when(testDescriptor.getSource())
        .thenReturn(Optional.of(MethodSource.from(getClass().getMethods()[0])));
    when(testExecutionResult.getStatus()).thenReturn(Status.FAILED);

    testExecutionListener.executionFinished(createTestIdentifier(), testExecutionResult);

    verify(testExecutionResult).getStatus();
    verify(testExecutionResult).getThrowable();
  }

  @Test
  void executionFinishedWhenMethodFinished() throws IllegalAccessException, NoSuchFieldException {
    when(testDescriptor.getSource())
        .thenReturn(Optional.of(MethodSource.from(getClass().getMethods()[0])));
    when(testExecutionResult.getStatus()).thenReturn(Status.SUCCESSFUL);

    testExecutionListener.executionFinished(createTestIdentifier(), testExecutionResult);

    verify(testExecutionResult).getStatus();
    verify(testExecutionResult).getThrowable();
  }

  @Test
  void executionFinishedWhenMethodNotInTestPlan() {
    when(testDescriptor.getSource())
        .thenReturn(Optional.of(MethodSource.from(getClass().getMethods()[0])));
    testExecutionListener.executionFinished(
        TestIdentifier.from(testDescriptor), testExecutionResult);
    verifyZeroInteractions(testExecutionResult);
  }

  @Test
  void executionSkipped() {
    var setup = executor.getCurrentSetup();
    testExecutionListener.executionSkipped(TestIdentifier.from(testDescriptor), "reason");
    assertThat(executor.getCurrentSetup()).isEqualTo(setup);
  }

  @Test
  void executionSkippedWhenMethod() throws IllegalAccessException, NoSuchFieldException {
    var setup = executor.getCurrentSetup();
    when(testDescriptor.getSource())
        .thenReturn(Optional.of(MethodSource.from(getClass().getMethods()[0])));
    testExecutionListener.executionSkipped(createTestIdentifier(), "reason");
    assertThat(executor.getCurrentSetup()).isEqualTo(setup);
  }

  @Test
  void executionSkippedWhenMethodNotInTestPlan()
      throws IllegalAccessException, NoSuchFieldException {
    when(testDescriptor.getSource())
        .thenReturn(Optional.of(MethodSource.from(getClass().getMethods()[0])));
    testExecutionListener.executionSkipped(TestIdentifier.from(testDescriptor), "reason");
    assertThat(getTestSuites()).isEmpty();
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

  @Test
  void executionStartedWithMethod() throws IllegalAccessException, NoSuchFieldException {
    var testIdentifier = createTestIdentifier();
    testExecutionListener.executionStarted(testIdentifier);
    assertThat(getMap().get(testIdentifier).getTimeStarted())
        .isLessThanOrEqualTo(System.currentTimeMillis());
  }

  @Test
  void executionStartedWithMethodNotInTestPlan() {
    var setup = executor.getCurrentSetup();
    when(testDescriptor.getSource())
        .thenReturn(Optional.of(MethodSource.from(getClass().getMethods()[0])));
    testExecutionListener.executionStarted(TestIdentifier.from(testDescriptor));
    assertThat(executor.getCurrentSetup()).isEqualTo(setup);
  }

  @Test
  void reportingEntryPublished() {
    var setup = executor.getCurrentSetup();
    testExecutionListener.reportingEntryPublished(
        TestIdentifier.from(testDescriptor), ReportEntry.from("key", "value"));
    assertThat(executor.getCurrentSetup()).isEqualTo(setup);
  }

  @Test
  void testPlanExecutionFinished() throws IllegalAccessException, NoSuchFieldException {
    createTestIdentifier();
    testExecutionListener.testPlanExecutionFinished(testPlan);
    assertThat(getTestSuites()).isEmpty();
  }

  @Test
  void testPlanExecutionStarted() throws IllegalAccessException, NoSuchFieldException {
    createTestIdentifier();
  }

  private TestIdentifier createTestIdentifier()
      throws IllegalAccessException, NoSuchFieldException {
    when(testDescriptor.getSource())
        .thenReturn(Optional.of(MethodSource.from(getClass().getMethods()[0])));
    when(testDescriptor.getType()).thenReturn(Type.TEST);

    var testIdentifier = TestIdentifier.from(testDescriptor);

    when(testPlan.getDescendants(testIdentifier)).thenReturn(Collections.singleton(testIdentifier));
    when(testPlan.getRoots()).thenReturn(Collections.singleton(testIdentifier));

    testExecutionListener.testPlanExecutionStarted(testPlan);

    assertThat(getMap()).containsKey(testIdentifier);
    assertThat(getTestSuites()).hasSize(1);

    return testIdentifier;
  }

  @SuppressWarnings("unchecked")
  private Map<TestIdentifier, TestCase> getMap()
      throws IllegalAccessException, NoSuchFieldException {
    var field = Listener.class.getDeclaredField("map");
    field.setAccessible(true);

    return (Map<TestIdentifier, TestCase>) field.get(testExecutionListener);
  }

  @SuppressWarnings("unchecked")
  private Iterable<TestSuite> getTestSuites() throws IllegalAccessException, NoSuchFieldException {
    var field = Listener.class.getDeclaredField("testSuites");
    field.setAccessible(true);

    return (Iterable<TestSuite>) field.get(testExecutionListener);
  }
}
