package io.github.henryssondaniel.teacup.engine.junit;

import io.github.henryssondaniel.teacup.core.Executor;
import io.github.henryssondaniel.teacup.core.Fixture;
import io.github.henryssondaniel.teacup.core.reporting.Factory;
import io.github.henryssondaniel.teacup.core.reporting.Reporter;
import io.github.henryssondaniel.teacup.core.reporting.TestCase;
import io.github.henryssondaniel.teacup.core.reporting.TestStatus;
import io.github.henryssondaniel.teacup.core.reporting.TestSuite;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
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
  private static final String METHOD_MISSING = "{0} test method is not part of the test plan.";
  private static final Reporter REPORTER = Factory.getReporter();

  private final Map<TestIdentifier, TestCase> map = new HashMap<>(0);
  private final Collection<TestSuite> testSuites = new LinkedHashSet<>(0);

  @Override
  public void executionFinished(
      TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
    LOGGER.log(Level.FINE, "Execution of " + testIdentifier.getDisplayName() + " finished");

    if (testIdentifier.getSource().orElse(null) instanceof MethodSource) {
      var testCase = map.get(testIdentifier);

      if (testCase == null) LOGGER.log(Level.WARNING, METHOD_MISSING, "Ended");
      else finishTestCase(testExecutionResult, testCase);
    }
  }

  @Override
  public void executionSkipped(TestIdentifier testIdentifier, String reason) {
    if (testIdentifier.getSource().orElse(null) instanceof MethodSource) {
      var testCase = map.get(testIdentifier);

      if (testCase == null) LOGGER.log(Level.WARNING, METHOD_MISSING, "Skipped");
      else REPORTER.skipped(reason, testCase);
    }
  }

  @Override
  public void executionStarted(TestIdentifier testIdentifier) {
    LOGGER.log(Level.FINE, "Execution of " + testIdentifier.getDisplayName() + " started");

    var testSource = testIdentifier.getSource().orElse(null);

    if (testSource instanceof ClassSource)
      EXECUTOR.executeFixture(
          ((ClassSource) testSource).getJavaClass().getAnnotation(Fixture.class));
    else if (testSource instanceof MethodSource) startTestCase(map.get(testIdentifier));
  }

  @Override
  public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry reportEntry) {
    LOGGER.log(Level.FINE, "Reporting entry published for " + testIdentifier.getDisplayName());

    var builder = new StringBuilder(0);

    reportEntry
        .getKeyValuePairs()
        .forEach((key, value) -> builder.append(key).append(" = ").append(value));

    var logRecord = new LogRecord(Level.INFO, builder.toString());
    logRecord.setInstant(reportEntry.getTimestamp().toInstant(ZoneOffset.UTC));

    REPORTER.log(logRecord);
  }

  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    LOGGER.log(Level.FINE, "Execution of test plan started");
    REPORTER.finished(testSuites);
    testSuites.clear();
  }

  @Override
  public void testPlanExecutionStarted(TestPlan testPlan) {
    LOGGER.log(Level.FINE, "Execution of test plan started");

    testSuites.addAll(
        testPlan.getRoots().stream()
            .map(
                testIdentifier ->
                    Factory.createTestSuite(
                        testIdentifier.getDisplayName(),
                        testPlan.getDescendants(testIdentifier).stream()
                            .filter(TestIdentifier::isTest)
                            .map(this::createTestCase)
                            .collect(Collectors.toSet())))
            .collect(Collectors.toSet()));

    REPORTER.started(testSuites);
  }

  private TestCase createTestCase(TestIdentifier testIdentifier) {
    var methodSource = (MethodSource) testIdentifier.getSource().orElseThrow();

    var testCase =
        Factory.createTestCase(methodSource.getMethodName(), Path.of(methodSource.getClassName()));

    map.put(testIdentifier, testCase);

    return testCase;
  }

  private static void finishTestCase(TestExecutionResult testExecutionResult, TestCase testCase) {
    testCase.setTimeFinished(System.currentTimeMillis());

    REPORTER.finished(
        testCase,
        Factory.createTestResult(
            getTestStatus(testExecutionResult.getStatus()),
            testExecutionResult.getThrowable().orElse(null)));
  }

  private static TestStatus getTestStatus(Status status) {
    TestStatus testStatus;

    switch (status) {
      case ABORTED:
        testStatus = TestStatus.ABORTED;
        break;
      case FAILED:
        testStatus = TestStatus.FAILED;
        break;
      default:
        testStatus = TestStatus.SUCCESSFUL;
    }

    return testStatus;
  }

  private static void startTestCase(TestCase testCase) {
    if (testCase == null) LOGGER.log(Level.WARNING, METHOD_MISSING, "Started");
    else {
      testCase.setTimeStarted(System.currentTimeMillis());
      REPORTER.started(testCase);
    }
  }
}
