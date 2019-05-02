package io.github.henryssondaniel.teacup.engine.junit;

import io.github.henryssondaniel.teacup.core.Executor;
import io.github.henryssondaniel.teacup.core.Fixture;
import io.github.henryssondaniel.teacup.core.reporting.Factory;
import io.github.henryssondaniel.teacup.core.reporting.Reporter;
import io.github.henryssondaniel.teacup.core.testing.Node;
import java.io.File;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

/**
 * Teacup test execution listener.
 *
 * @since 1.0
 */
public class Listener implements TestExecutionListener {
  private static final Executor EXECUTOR = ExecutorHolder.getExecutor();
  private static final Logger LOGGER =
      io.github.henryssondaniel.teacup.core.logging.Factory.getLogger(Listener.class);
  private static final String MISSING = "{0} {1} is not part of the test plan.";
  private static final Reporter REPORTER = Factory.getReporter();

  private final Map<TestIdentifier, Node> map = new HashMap<>(0);

  private TestPlan plan;

  @Override
  public void dynamicTestRegistered(TestIdentifier testIdentifier) {
    LOGGER.log(Level.FINE, "Dynamic test {0} registered", testIdentifier.getDisplayName());

    REPORTER.added(createNode(testIdentifier));
  }

  @Override
  public void executionFinished(
      TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
    var displayName = testIdentifier.getDisplayName();

    LOGGER.log(Level.FINE, "Execution of {0} finished", displayName);

    var node = map.remove(testIdentifier);

    if (node == null) LOGGER.log(Level.WARNING, MISSING, new Object[] {"Ended", displayName});
    else {
      node.setTimeFinished(System.currentTimeMillis());

      REPORTER.finished(
          node,
          io.github.henryssondaniel.teacup.core.testing.Factory.createResult(
              getStatus(testExecutionResult.getStatus()),
              testExecutionResult.getThrowable().orElse(null)));
    }
  }

  @Override
  public void executionSkipped(TestIdentifier testIdentifier, String reason) {
    var displayName = testIdentifier.getDisplayName();

    LOGGER.log(Level.FINE, "Execution of {0} skipped", displayName);

    var node = map.remove(testIdentifier);
    if (node == null) LOGGER.log(Level.WARNING, MISSING, new Object[] {"Skipped", displayName});
    else REPORTER.skipped(node, reason);
  }

  @Override
  public void executionStarted(TestIdentifier testIdentifier) {
    var displayName = testIdentifier.getDisplayName();

    LOGGER.log(Level.FINE, "Execution of {0} started", displayName);

    var testSource = getClassSource(testIdentifier);
    if (testSource != null)
      EXECUTOR.executeFixture(testSource.getJavaClass().getAnnotation(Fixture.class));

    var node = map.get(testIdentifier);
    if (node == null) LOGGER.log(Level.WARNING, MISSING, new Object[] {"Started", displayName});
    else {
      node.setTimeStarted(System.currentTimeMillis());
      REPORTER.started(node);
    }
  }

  @Override
  public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry reportEntry) {
    LOGGER.log(Level.FINE, "Reporting entry published for {0}", testIdentifier.getDisplayName());

    var builder = new StringBuilder(0);

    reportEntry
        .getKeyValuePairs()
        .forEach((key, value) -> builder.append(key).append(" = ").append(value));

    var logRecord = new LogRecord(Level.INFO, builder.toString());
    logRecord.setInstant(reportEntry.getTimestamp().toInstant(ZoneOffset.UTC));

    REPORTER.log(logRecord, map.get(testIdentifier));
  }

  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    LOGGER.log(Level.FINE, "Execution of test plan started");

    map.clear();
    plan = null;

    REPORTER.terminated();
  }

  @Override
  public void testPlanExecutionStarted(TestPlan testPlan) {
    LOGGER.log(Level.FINE, "Execution of test plan started");

    plan = testPlan;
    REPORTER.initialized(createChildren(testPlan.getRoots()));
  }

  private Collection<Node> createChildren(Collection<TestIdentifier> testIdentifiers) {
    return testIdentifiers.stream().map(this::createNode).collect(Collectors.toSet());
  }

  private Node createNode(TestIdentifier testIdentifier) {
    var testNode =
        io.github.henryssondaniel.teacup.core.testing.Factory.createNode(
            getName(testIdentifier), createChildren(plan.getChildren(testIdentifier)));
    map.put(testIdentifier, testNode);
    return testNode;
  }

  private static ClassSource getClassSource(TestIdentifier testIdentifier) {
    return testIdentifier
        .getSource()
        .filter(ClassSource.class::isInstance)
        .map(ClassSource.class::cast)
        .orElse(null);
  }

  private String getName(TestIdentifier testIdentifier) {
    var path = Path.of("");

    var identifier = testIdentifier;
    var shouldContinue = true;

    while (identifier != null && shouldContinue) {
      var classSource = getClassSource(identifier);

      var temp = path;

      if (classSource == null) {
        path = Path.of(identifier.getDisplayName());
        identifier = getParent(identifier, plan);
      } else {
        path = getPath(classSource.getJavaClass());
        shouldContinue = false;
      }

      path = path.resolve(temp);
    }

    return path.toString().replaceFirst(Pattern.quote(System.getProperty("user.dir")), "");
  }

  private static TestIdentifier getParent(TestIdentifier testIdentifier, TestPlan testPlan) {
    return testPlan.getParent(testIdentifier).orElse(null);
  }

  private static Path getPath(Class<?> clazz) {
    return new File(clazz.getResource(".").getFile()).toPath().resolve(clazz.getSimpleName());
  }

  private static io.github.henryssondaniel.teacup.core.testing.Status getStatus(Status status) {
    io.github.henryssondaniel.teacup.core.testing.Status testStatus;

    switch (status) {
      case ABORTED:
        testStatus = io.github.henryssondaniel.teacup.core.testing.Status.ABORTED;
        break;
      case FAILED:
        testStatus = io.github.henryssondaniel.teacup.core.testing.Status.FAILED;
        break;
      default:
        testStatus = io.github.henryssondaniel.teacup.core.testing.Status.SUCCESSFUL;
    }

    return testStatus;
  }
}
