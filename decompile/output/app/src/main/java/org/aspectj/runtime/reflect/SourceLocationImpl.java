package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.SourceLocation;

class SourceLocationImpl
  implements SourceLocation
{
  String fileName;
  int line;
  Class withinType;
  
  SourceLocationImpl(Class paramClass, String paramString, int paramInt)
  {
    withinType = paramClass;
    fileName = paramString;
    line = paramInt;
  }
  
  public int getColumn()
  {
    return -1;
  }
  
  public String getFileName()
  {
    return fileName;
  }
  
  public int getLine()
  {
    return line;
  }
  
  public Class getWithinType()
  {
    return withinType;
  }
  
  public String toString()
  {
    return getFileName() + ":" + getLine();
  }
}
