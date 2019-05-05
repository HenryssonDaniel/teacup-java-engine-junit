package io.github.henryssondaniel.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import io.github.henryssondaniel.teacup.core.Executor;
import io.github.henryssondaniel.teacup.core.Fixture;
import io.github.henryssondaniel.teacup.core.testing.Node;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestDescriptor.Type;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

class ListenerTest {
  private static final String NAME = "displayName";

  private final Executor executor = ExecutorHolder.getExecutor();
  private final TestDescriptor testDescriptor = mock(TestDescriptor.class);
  private final TestExecutionListener testExecutionListener = new Listener();
  private final TestExecutionResult testExecutionResult = mock(TestExecutionResult.class);
  private final TestPlan testPlan = mock(TestPlan.class);

  @BeforeEach
  void beforeEach() {
    var uniqueId = mock(UniqueId.class);

    when(testDescriptor.getDisplayName()).thenReturn(NAME);
    when(testDescriptor.getType()).thenReturn(Type.CONTAINER);
    when(testDescriptor.getUniqueId()).thenReturn(uniqueId);

    testExecutionListener.testPlanExecutionStarted(testPlan);
  }

  @Test
  void dynamicTestRegistered() throws IllegalAccessException, NoSuchFieldException {
    var testIdentifier = TestIdentifier.from(testDescriptor);
    testExecutionListener.dynamicTestRegistered(testIdentifier);
    verifyTestIdentifier(NAME, testIdentifier);
  }

  @Test
  void dynamicTestRegisteredWhenClass() throws IllegalAccessException, NoSuchFieldException {
    Class<?> clazz = getClass();
    when(testDescriptor.getSource()).thenReturn(Optional.of(ClassSource.from(clazz)));

    var testIdentifier = TestIdentifier.from(testDescriptor);

    var rootName = "root";

    var uniqueId = mock(UniqueId.class);
    when(testDescriptor.getDisplayName()).thenReturn(rootName);
    when(testDescriptor.getUniqueId()).thenReturn(uniqueId);

    var root = TestIdentifier.from(testDescriptor);

    when(testPlan.getParent(testIdentifier))
        .thenReturn(Optional.of(root))
        .thenReturn(Optional.empty());

    testExecutionListener.dynamicTestRegistered(testIdentifier);
    verifyTestIdentifier(rootName + getName(clazz), testIdentifier);
  }

  @Test
  void executionFinishedWhenAborted() throws IllegalAccessException, NoSuchFieldException {
    executionFinished(Status.ABORTED);
  }

  @Test
  void executionFinishedWhenFailed() throws IllegalAccessException, NoSuchFieldException {
    executionFinished(Status.FAILED);
  }

  @Test
  void executionFinishedWhenNotInTestPlan() {
    testExecutionListener.executionFinished(
        TestIdentifier.from(testDescriptor), testExecutionResult);
    verifyZeroInteractions(testExecutionResult);
  }

  @Test
  void executionFinishedWhenSuccessful() throws IllegalAccessException, NoSuchFieldException {
    executionFinished(Status.SUCCESSFUL);
  }

  @Test
  void executionSkipped() throws IllegalAccessException, NoSuchFieldException {
    var testIdentifier = TestIdentifier.from(testDescriptor);
    testExecutionListener.dynamicTestRegistered(testIdentifier);

    verifyTestIdentifier(NAME, testIdentifier);

    testExecutionListener.executionSkipped(testIdentifier, "reason");
    assertThat(getMap()).doesNotContainKey(testIdentifier);
  }

  @Test
  void executionSkippedWhenNotInTestPlan() throws IllegalAccessException, NoSuchFieldException {
    testExecutionListener.executionSkipped(TestIdentifier.from(testDescriptor), "reason");
    assertThat(getMap()).isEmpty();
  }

  @Test
  void executionStarted() throws IllegalAccessException, NoSuchFieldException {
    var setup = executor.getCurrentSetup();

    var testIdentifier = TestIdentifier.from(testDescriptor);
    testExecutionListener.dynamicTestRegistered(testIdentifier);

    var node = verifyTestIdentifier(NAME, testIdentifier);

    testExecutionListener.executionStarted(testIdentifier);

    assertThat(executor.getCurrentSetup()).isSameAs(setup);
    assertThat(node.getTimeStarted()).isLessThanOrEqualTo(System.currentTimeMillis());
  }

  @SuppressWarnings("unchecked")
  @Test
  void executionStartedWhenClass() {
    var fixture = mock(Fixture.class);
    when(fixture.value()).thenReturn((Class) TestSetup.class);

    executor.executeFixture(fixture);

    when(testDescriptor.getSource()).thenReturn(Optional.of(ClassSource.from(getClass())));

    testExecutionListener.executionStarted(TestIdentifier.from(testDescriptor));
    assertThat(executor.getCurrentSetup()).isEmpty();
  }

  @Test
  void reportingEntryPublished() {
    var setup = executor.getCurrentSetup();
    testExecutionListener.reportingEntryPublished(
        TestIdentifier.from(testDescriptor), ReportEntry.from("key", "value"));
    assertThat(executor.getCurrentSetup()).isSameAs(setup);
  }

  @Test
  void testPlanExecutionFinished() throws IllegalAccessException, NoSuchFieldException {
    var testIdentifier = TestIdentifier.from(testDescriptor);
    testExecutionListener.dynamicTestRegistered(testIdentifier);

    verifyTestIdentifier(NAME, TestIdentifier.from(testDescriptor));

    testExecutionListener.testPlanExecutionFinished(testPlan);
    assertThat(getMap()).isEmpty();
  }

  @Test
  void testPlanExecutionStarted() throws IllegalAccessException, NoSuchFieldException {
    var testIdentifier = TestIdentifier.from(testDescriptor);
    when(testPlan.getRoots()).thenReturn(Collections.singleton(testIdentifier));

    testExecutionListener.testPlanExecutionStarted(testPlan);

    verifyTestIdentifier(NAME, testIdentifier);
  }

  private void executionFinished(Status status)
      throws IllegalAccessException, NoSuchFieldException {
    var testIdentifier = TestIdentifier.from(testDescriptor);
    testExecutionListener.dynamicTestRegistered(testIdentifier);

    var node = verifyTestIdentifier(NAME, testIdentifier);

    when(testExecutionResult.getStatus()).thenReturn(status);

    testExecutionListener.executionFinished(testIdentifier, testExecutionResult);

    assertThat(getMap()).doesNotContainKey(testIdentifier);
    assertThat(node.getTimeFinished()).isLessThanOrEqualTo(System.currentTimeMillis());

    verify(testExecutionResult).getStatus();
    verify(testExecutionResult).getThrowable();
  }

  @SuppressWarnings("unchecked")
  private Map<TestIdentifier, Node> getMap() throws IllegalAccessException, NoSuchFieldException {
    var field = Listener.class.getDeclaredField("map");
    field.setAccessible(true);

    return (Map<TestIdentifier, Node>) field.get(testExecutionListener);
  }

  private static String getName(Class<?> clazz) {
    return new File(clazz.getResource(".").getFile())
        .toPath()
        .resolve(clazz.getSimpleName())
        .toString()
        .replaceFirst(Pattern.quote(System.getProperty("user.dir")), "");
  }

  private Node verifyTestIdentifier(String name, TestIdentifier testIdentifier)
      throws IllegalAccessException, NoSuchFieldException {
    var map = getMap();
    assertThat(map).containsOnlyKeys(testIdentifier);

    var node = map.get(testIdentifier);
    assertThat(node.getName()).isEqualTo(name);
    assertThat(node.getNodes()).isEmpty();
    assertThat(node.getTimeFinished()).isZero();
    assertThat(node.getTimeStarted()).isZero();

    verify(testPlan).getChildren(testIdentifier);

    return node;
  }
}
