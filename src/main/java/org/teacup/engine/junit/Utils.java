package org.teacup.engine.junit;

import io.github.henryssondaniel.teacup.core.Fixture;
import io.github.henryssondaniel.teacup.core.Setup;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;

enum Utils {
  ;

  static void group(TestDescriptor testDescriptor) {
    List<TestDescriptor> testDescriptors = new LinkedList<>();
    group(testDescriptor.getChildren(), testDescriptors);

    testDescriptors.forEach(testDescriptor::removeChild);
    testDescriptors.forEach(testDescriptor::addChild);
  }

  private static void group(
      Iterable<? extends TestDescriptor> children, List<? super TestDescriptor> testDescriptors) {
    Map<Class<? extends Setup>, TestDescriptor> testDescriptorSetup = new HashMap<>(0);

    for (TestDescriptor descriptor : children) {
      Object testSource = descriptor.getSource().orElse(null);

      if (testSource instanceof ClassSource)
        groupClass(
            testDescriptorSetup,
            ((ClassSource) testSource).getJavaClass().getAnnotation(Fixture.class),
            descriptor,
            testDescriptors);
      else testDescriptors.add(descriptor);
    }
  }

  private static void groupClass(
      Map<? super Class<? extends Setup>, TestDescriptor> annotationMap,
      Fixture fixture,
      TestDescriptor testDescriptor,
      List<? super TestDescriptor> testDescriptors) {
    if (fixture == null) testDescriptors.add(testDescriptor);
    else groupFixture(annotationMap, fixture.value(), testDescriptor, testDescriptors);
  }

  private static void groupExistingFixture(
      TestDescriptor existingTestDescriptor,
      TestDescriptor testDescriptor,
      List<? super TestDescriptor> testDescriptors) {
    var newSize = testDescriptors.indexOf(existingTestDescriptor) + 1;

    if (testDescriptors.size() == newSize) testDescriptors.add(testDescriptor);
    else testDescriptors.add(newSize, testDescriptor);
  }

  private static void groupFixture(
      Map<? super Class<? extends Setup>, TestDescriptor> annotationMap,
      Class<? extends Setup> setup,
      TestDescriptor testDescriptor,
      List<? super TestDescriptor> testDescriptors) {
    if (annotationMap.containsKey(setup))
      groupExistingFixture(annotationMap.get(setup), testDescriptor, testDescriptors);
    else testDescriptors.add(testDescriptor);

    annotationMap.put(setup, testDescriptor);
  }
}
