package org.aspectj.lang.reflect;

import java.lang.reflect.Constructor;

public abstract interface ConstructorSignature
  extends CodeSignature
{
  public abstract Constructor getConstructor();
}
