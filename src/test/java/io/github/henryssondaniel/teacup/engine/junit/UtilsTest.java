package io.github.henryssondaniel.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.henryssondaniel.teacup.engine.Fixture;
import io.github.henryssondaniel.teacup.engine.Setup;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;

class UtilsTest {
  private final TestDescriptor testDescriptor = new TestTestDescriptor();
  private final TestDescriptor testDescriptorNotClass = mock(TestDescriptor.class);
  private final TestDescriptor testDescriptorWithFixture = mock(TestDescriptor.class);
  private final TestDescriptor testDescriptorWithSameFixture = mock(TestDescriptor.class);
  private final TestDescriptor testDescriptorWithoutFixture = mock(TestDescriptor.class);

  @BeforeEach
  void beforeEach() {
    when(testDescriptorWithFixture.getSource())
        .thenReturn(Optional.of(ClassSource.from(TestWithFixture.class)));
    when(testDescriptorWithSameFixture.getSource())
        .thenReturn(Optional.of(ClassSource.from(TestWithFixture.class)));
    when(testDescriptorWithoutFixture.getSource())
        .thenReturn(Optional.of(ClassSource.from(TestWithoutFixture.class)));

    testDescriptor.addChild(testDescriptorWithFixture);
    testDescriptor.addChild(testDescriptorNotClass);
    testDescriptor.addChild(testDescriptorWithoutFixture);
  }

  @Test
  void groupFixtures() {
    testDescriptor.addChild(testDescriptorWithSameFixture);

    Utils.group(testDescriptor);

    var iterator = testDescriptor.getChildren().iterator();

    assertThat(iterator.next()).isSameAs(testDescriptorWithFixture);
    assertThat(iterator.next()).isSameAs(testDescriptorWithSameFixture);
    assertThat(iterator.next()).isSameAs(testDescriptorNotClass);
    assertThat(iterator.next()).isSameAs(testDescriptorWithoutFixture);
  }

  @Test
  void groupFixturesWhenLast() {
    testDescriptor.removeChild(testDescriptorWithFixture);
    testDescriptor.addChild(testDescriptorWithFixture);
    testDescriptor.addChild(testDescriptorWithSameFixture);

    Utils.group(testDescriptor);

    var iterator = testDescriptor.getChildren().iterator();

    assertThat(iterator.next()).isSameAs(testDescriptorNotClass);
    assertThat(iterator.next()).isSameAs(testDescriptorWithoutFixture);
    assertThat(iterator.next()).isSameAs(testDescriptorWithFixture);
    assertThat(iterator.next()).isSameAs(testDescriptorWithSameFixture);
  }

  @Test
  void groupWhenNoFixtures() {
    Utils.group(testDescriptor);

    var iterator = testDescriptor.getChildren().iterator();

    assertThat(iterator.next()).isSameAs(testDescriptorWithFixture);
    assertThat(iterator.next()).isSameAs(testDescriptorNotClass);
    assertThat(iterator.next()).isSameAs(testDescriptorWithoutFixture);
  }

  @Fixture(Setup.class)
  private enum TestWithFixture {
  // Empty
  }

  private enum TestWithoutFixture {
  // Empty
  }

  private static final class TestTestDescriptor extends AbstractTestDescriptor {
    private static final Type TYPE = Type.CONTAINER;

    private TestTestDescriptor() {
      super(UniqueId.forEngine("junit-jupiter"), "displayName");
    }

    @Override
    public Type getType() {
      return TYPE;
    }
  }
}
