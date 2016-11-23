package org.aspectj.runtime.internal.cflowstack;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class ThreadStackImpl11
  implements ThreadStack
{
  private static final int COLLECT_AT = 20000;
  private static final int MIN_COLLECT_AT = 100;
  private Stack cached_stack;
  private Thread cached_thread;
  private int change_count = 0;
  private Hashtable stacks = new Hashtable();
  
  public ThreadStackImpl11() {}
  
  public Stack getThreadStack()
  {
    try
    {
      if (Thread.currentThread() == cached_thread) {
        break label221;
      }
      cached_thread = Thread.currentThread();
      cached_stack = ((Stack)stacks.get(cached_thread));
      if (cached_stack == null)
      {
        cached_stack = new Stack();
        stacks.put(cached_thread, cached_stack);
      }
      change_count = (1 + change_count);
      int i = Math.max(1, stacks.size());
      if (change_count <= Math.max(100, 20000 / i)) {
        break label221;
      }
      Stack localStack2 = new Stack();
      Enumeration localEnumeration1 = stacks.keys();
      while (localEnumeration1.hasMoreElements())
      {
        Thread localThread2 = (Thread)localEnumeration1.nextElement();
        if (!localThread2.isAlive()) {
          localStack2.push(localThread2);
        }
      }
      localEnumeration2 = localStack2.elements();
    }
    finally {}
    Enumeration localEnumeration2;
    while (localEnumeration2.hasMoreElements())
    {
      Thread localThread1 = (Thread)localEnumeration2.nextElement();
      stacks.remove(localThread1);
    }
    change_count = 0;
    label221:
    Stack localStack1 = cached_stack;
    return localStack1;
  }
  
  public void removeThreadStack() {}
}
