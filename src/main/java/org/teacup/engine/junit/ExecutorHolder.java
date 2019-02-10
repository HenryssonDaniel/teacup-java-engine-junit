package org.teacup.engine.junit;

import org.teacup.core.Executor;
import org.teacup.core.ExecutorFactory;

enum ExecutorHolder {
  ;

  private static final Executor EXECUTOR = ExecutorFactory.create();

  static Executor getExecutor() {
    return EXECUTOR;
  }
}
