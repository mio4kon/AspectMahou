package org.aspectj.lang;

import java.io.PrintStream;
import java.io.PrintWriter;

public class SoftException
  extends RuntimeException
{
  private static final boolean HAVE_JAVA_14;
  Throwable inner;
  
  static
  {
    try
    {
      Class.forName("java.nio.Buffer");
      bool = true;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        boolean bool = false;
      }
    }
    HAVE_JAVA_14 = bool;
  }
  
  public SoftException(Throwable paramThrowable)
  {
    inner = paramThrowable;
  }
  
  public Throwable getCause()
  {
    return inner;
  }
  
  public Throwable getWrappedThrowable()
  {
    return inner;
  }
  
  public void printStackTrace()
  {
    printStackTrace(System.err);
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    super.printStackTrace(paramPrintStream);
    Throwable localThrowable = inner;
    if ((!HAVE_JAVA_14) && (localThrowable != null))
    {
      paramPrintStream.print("Caused by: ");
      localThrowable.printStackTrace(paramPrintStream);
    }
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    super.printStackTrace(paramPrintWriter);
    Throwable localThrowable = inner;
    if ((!HAVE_JAVA_14) && (localThrowable != null))
    {
      paramPrintWriter.print("Caused by: ");
      localThrowable.printStackTrace(paramPrintWriter);
    }
  }
}
