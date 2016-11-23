package org.aspectj.lang.reflect;

import java.lang.reflect.Constructor;

public abstract interface InitializerSignature
  extends CodeSignature
{
  public abstract Constructor getInitializer();
}
