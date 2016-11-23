package org.aspectj.runtime.internal.cflowstack;

import java.util.Stack;

public abstract interface ThreadStack
{
  public abstract Stack getThreadStack();
  
  public abstract void removeThreadStack();
}
