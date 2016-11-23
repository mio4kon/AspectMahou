package org.aspectj.lang.reflect;

public class NoSuchAdviceException
  extends Exception
{
  private static final long serialVersionUID = 3256444698657634352L;
  private String name;
  
  public NoSuchAdviceException(String paramString)
  {
    name = paramString;
  }
  
  public String getName()
  {
    return name;
  }
}
