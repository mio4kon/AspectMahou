package org.aspectj.lang;

public class NoAspectBoundException
  extends RuntimeException
{
  Throwable cause;
  
  public NoAspectBoundException() {}
  
  public NoAspectBoundException(String paramString, Throwable paramThrowable) {}
  
  public Throwable getCause()
  {
    return cause;
  }
}
