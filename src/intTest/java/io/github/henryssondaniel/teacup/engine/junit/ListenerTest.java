package io.github.henryssondaniel.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.henryssondaniel.teacup.core.Executor;
import io.github.henryssondaniel.teacup.core.Fixture;
import io.github.henryssondaniel.teacup.core.testing.Node;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.engine.config.CachingJupiterConfiguration;
import org.junit.jupiter.engine.config.DefaultJupiterConfiguration;
import org.junit.jupiter.engine.config.JupiterConfiguration;
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

@Fixture(TestSetup.class)
class ListenerTest {
  private static final Executor EXECUTOR = ExecutorHolder.getExecutor();

  private final JupiterConfiguration jupiterConfiguration =
      new CachingJupiterConfiguration(
          new DefaultJupiterConfiguration(new TestConfigurationParameters()));
  private final TestExecutionListener testExecutionListener = new Listener();
  private final UniqueId uniqueId = UniqueId.parse("[test:test]");

  @Test
  void dynamicTestRegistered() throws IllegalAccessException, NoSuchFieldException {
    var testDescriptor = createTestDescriptor();

    testExecutionListener.testPlanExecutionStarted(
        TestPlan.from(Collections.singletonList(testDescriptor)));
    testExecutionListener.dynamicTestRegistered(TestIdentifier.from(testDescriptor));

    assertThat(getMap()).hasSize(1);
  }

  @Test
  void executionFinishedAborted() throws IllegalAccessException, NoSuchFieldException {
    var testDescriptor = createTestDescriptor();

    testExecutionListener.testPlanExecutionStarted(
        TestPlan.from(Collections.singletonList(testDescriptor)));
    testExecutionListener.executionFinished(
        TestIdentifier.from(testDescriptor), TestExecutionResult.aborted(null));

    assertThat(getMap()).isEmpty();
  }

  @Test
  void executionFinishedFailed() throws IllegalAccessException, NoSuchFieldException {
    var testDescriptor = createTestDescriptor();

    testExecutionListener.testPlanExecutionStarted(
        TestPlan.from(Collections.singletonList(testDescriptor)));
    testExecutionListener.executionFinished(
        TestIdentifier.from(testDescriptor), TestExecutionResult.failed(null));

    assertThat(getMap()).isEmpty();
  }

  @Test
  void executionFinishedSuccessful() throws IllegalAccessException, NoSuchFieldException {
    var testDescriptor = createTestDescriptor();

    testExecutionListener.testPlanExecutionStarted(
        TestPlan.from(Collections.singletonList(testDescriptor)));
    testExecutionListener.executionFinished(
        TestIdentifier.from(testDescriptor), TestExecutionResult.successful());

    assertThat(getMap()).isEmpty();
  }

  @Test
  void executionFinishedWHenNoPlan() throws IllegalAccessException, NoSuchFieldException {
    testExecutionListener.executionFinished(
        TestIdentifier.from(
            new ClassTestDescriptor(uniqueId, ListenerTest.class, jupiterConfiguration)),
        TestExecutionResult.successful());

    assertThat(getMap()).isEmpty();
  }

  @Test
  void executionSkipped() throws IllegalAccessException, NoSuchFieldException {
    var testDescriptor = createTestDescriptor();

    testExecutionListener.testPlanExecutionStarted(
        TestPlan.from(Collections.singletonList(testDescriptor)));
    testExecutionListener.executionSkipped(TestIdentifier.from(testDescriptor), "reason");

    assertThat(getMap()).isEmpty();
  }

  @Test
  void executionSkippedWhenNoPlan() throws IllegalAccessException, NoSuchFieldException {
    testExecutionListener.executionSkipped(
        TestIdentifier.from(
            new ClassTestDescriptor(uniqueId, ListenerTest.class, jupiterConfiguration)),
        "reason");

    assertThat(getMap()).isEmpty();
  }

  @Test
  void executionStarted() throws IllegalAccessException, NoSuchFieldException {
    TestDescriptor testDescriptor =
        new TestMethodTestDescriptor(
            uniqueId, ListenerTest.class, ListenerTest.class.getMethods()[0], jupiterConfiguration);
    var testIdentifier = TestIdentifier.from(testDescriptor);

    EXECUTOR.executeFixture(null);

    testExecutionListener.testPlanExecutionStarted(
        TestPlan.from(Collections.singletonList(testDescriptor)));
    testExecutionListener.executionStarted(testIdentifier);

    assertThat(EXECUTOR.getCurrentSetup()).isEmpty();
    assertThat(getMap()).containsKey(testIdentifier);
  }

  @Test
  void executionStartedWhenClass() throws IllegalAccessException, NoSuchFieldException {
    EXECUTOR.executeFixture(null);
    testExecutionListener.executionStarted(
        TestIdentifier.from(
            new ClassTestDescriptor(uniqueId, ListenerTest.class, jupiterConfiguration)));

    assertThat(EXECUTOR.getCurrentSetup()).containsInstanceOf(TestSetup.class);
    assertThat(getMap()).isEmpty();
  }

  @Test
  void executionStartedWhenNoPlan() throws IllegalAccessException, NoSuchFieldException {
    EXECUTOR.executeFixture(null);
    testExecutionListener.executionStarted(
        TestIdentifier.from(
            new TestMethodTestDescriptor(
                uniqueId,
                ListenerTest.class,
                ListenerTest.class.getMethods()[0],
                jupiterConfiguration)));

    assertThat(EXECUTOR.getCurrentSetup()).isEmpty();
    assertThat(getMap()).isEmpty();
  }

  @Test
  void executionStartedWhenNotClass() throws IllegalAccessException, NoSuchFieldException {
    EXECUTOR.executeFixture(null);
    testExecutionListener.executionStarted(
        TestIdentifier.from(
            new TestMethodTestDescriptor(
                uniqueId,
                ListenerTest.class,
                ListenerTest.class.getMethods()[0],
                jupiterConfiguration)));

    assertThat(ExecutorHolder.getExecutor().getCurrentSetup()).isEmpty();
    assertThat(getMap()).isEmpty();
  }

  @Test
  void reportingEntryPublished() throws IllegalAccessException, NoSuchFieldException {
    testExecutionListener.reportingEntryPublished(
        TestIdentifier.from(
            new ClassTestDescriptor(uniqueId, ListenerTest.class, jupiterConfiguration)),
        ReportEntry.from("key", "value"));
    assertThat(getMap()).isEmpty();
  }

  @Test
  void testPlanExecutionFinished() throws IllegalAccessException, NoSuchFieldException {
    testExecutionListener.testPlanExecutionFinished(
        TestPlan.from(
            Collections.singletonList(
                new ClassTestDescriptor(uniqueId, ListenerTest.class, jupiterConfiguration))));
    assertThat(getMap()).isEmpty();
  }

  @Test
  void testPlanExecutionStarted() throws IllegalAccessException, NoSuchFieldException {
    TestDescriptor testDescriptor =
        new ClassTestDescriptor(uniqueId, ListenerTest.class, jupiterConfiguration);

    testExecutionListener.testPlanExecutionStarted(
        TestPlan.from(Collections.singletonList(testDescriptor)));

    assertThat(getMap()).containsKey(TestIdentifier.from(testDescriptor));
  }

  private TestDescriptor createTestDescriptor() {
    return new ClassTestDescriptor(uniqueId, ListenerTest.class, jupiterConfiguration);
  }

  @SuppressWarnings("unchecked")
  private Map<TestIdentifier, Node> getMap() throws IllegalAccessException, NoSuchFieldException {
    var field = Listener.class.getDeclaredField("map");
    field.setAccessible(true);

    return (Map<TestIdentifier, Node>) field.get(testExecutionListener);
  }
}
