package org.aspectj.runtime.internal;

import java.util.Stack;
import java.util.Vector;
import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.runtime.CFlow;
import org.aspectj.runtime.internal.cflowstack.ThreadStack;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactory;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactoryImpl;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactoryImpl11;

public class CFlowStack
{
  private static ThreadStackFactory tsFactory;
  private ThreadStack stackProxy = tsFactory.getNewThreadStack();
  
  static {}
  
  public CFlowStack() {}
  
  private static String getSystemPropertyWithoutSecurityException(String paramString1, String paramString2)
  {
    try
    {
      String str = System.getProperty(paramString1, paramString2);
      return str;
    }
    catch (SecurityException localSecurityException) {}
    return paramString2;
  }
  
  private static ThreadStackFactory getThreadLocalStackFactory()
  {
    return new ThreadStackFactoryImpl();
  }
  
  private static ThreadStackFactory getThreadLocalStackFactoryFor11()
  {
    return new ThreadStackFactoryImpl11();
  }
  
  private Stack getThreadStack()
  {
    return stackProxy.getThreadStack();
  }
  
  public static String getThreadStackFactoryClassName()
  {
    return tsFactory.getClass().getName();
  }
  
  private static void selectFactoryForVMVersion()
  {
    String str = getSystemPropertyWithoutSecurityException("aspectj.runtime.cflowstack.usethreadlocal", "unspecified");
    if (str.equals("unspecified"))
    {
      if (System.getProperty("java.class.version", "0.0").compareTo("46.0") >= 0) {}
      for (i = 1; i != 0; i = 0)
      {
        tsFactory = getThreadLocalStackFactory();
        return;
      }
    }
    if ((str.equals("yes")) || (str.equals("true"))) {}
    for (int i = 1;; i = 0) {
      break;
    }
    tsFactory = getThreadLocalStackFactoryFor11();
  }
  
  public Object get(int paramInt)
  {
    CFlow localCFlow = peekCFlow();
    if (localCFlow == null) {
      return null;
    }
    return localCFlow.get(paramInt);
  }
  
  public boolean isValid()
  {
    return !getThreadStack().isEmpty();
  }
  
  public Object peek()
  {
    Stack localStack = getThreadStack();
    if (localStack.isEmpty()) {
      throw new NoAspectBoundException();
    }
    return localStack.peek();
  }
  
  public CFlow peekCFlow()
  {
    Stack localStack = getThreadStack();
    if (localStack.isEmpty()) {
      return null;
    }
    return (CFlow)localStack.peek();
  }
  
  public Object peekInstance()
  {
    CFlow localCFlow = peekCFlow();
    if (localCFlow != null) {
      return localCFlow.getAspect();
    }
    throw new NoAspectBoundException();
  }
  
  public CFlow peekTopCFlow()
  {
    Stack localStack = getThreadStack();
    if (localStack.isEmpty()) {
      return null;
    }
    return (CFlow)localStack.elementAt(0);
  }
  
  public void pop()
  {
    Stack localStack = getThreadStack();
    localStack.pop();
    if (localStack.isEmpty()) {
      stackProxy.removeThreadStack();
    }
  }
  
  public void push(Object paramObject)
  {
    getThreadStack().push(paramObject);
  }
  
  public void push(Object[] paramArrayOfObject)
  {
    getThreadStack().push(new CFlowPlusState(paramArrayOfObject));
  }
  
  public void pushInstance(Object paramObject)
  {
    getThreadStack().push(new CFlow(paramObject));
  }
}
