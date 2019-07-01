package io.github.henryssondaniel.teacup.engine.junit;

import io.github.henryssondaniel.teacup.core.logging.Factory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.engine.config.CachingJupiterConfiguration;
import org.junit.jupiter.engine.config.DefaultJupiterConfiguration;
import org.junit.jupiter.engine.config.JupiterConfiguration;
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.jupiter.engine.discovery.DiscoverySelectorResolver;
import org.junit.jupiter.engine.execution.JupiterEngineExecutionContext;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;

/**
 * The teacup test engine.
 *
 * @since 1.0
 */
public class TeacupTestEngine extends HierarchicalTestEngine<JupiterEngineExecutionContext> {
  private static final String ID = "teacup";
  private static final Logger LOGGER = Factory.getLogger(TeacupTestEngine.class);

  @Override
  public TestDescriptor discover(EngineDiscoveryRequest engineDiscoveryRequest, UniqueId uniqueId) {
    LOGGER.log(Level.SEVERE, "Discover using unique ID: {0}", uniqueId);

    JupiterConfiguration jupiterConfiguration =
        new CachingJupiterConfiguration(
            new DefaultJupiterConfiguration(engineDiscoveryRequest.getConfigurationParameters()));

    var testDescriptor = new JupiterEngineDescriptor(uniqueId, jupiterConfiguration);

    new DiscoverySelectorResolver().resolveSelectors(engineDiscoveryRequest, testDescriptor);
    Utils.group(testDescriptor);

    return testDescriptor;
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  protected JupiterEngineExecutionContext createExecutionContext(
      ExecutionRequest executionRequest) {
    return new JupiterEngineExecutionContext(
        executionRequest.getEngineExecutionListener(),
        ((JupiterEngineDescriptor) executionRequest.getRootTestDescriptor()).getConfiguration());
  }
}
