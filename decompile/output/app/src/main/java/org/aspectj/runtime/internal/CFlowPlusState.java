package org.aspectj.runtime.internal;

import org.aspectj.runtime.CFlow;

public class CFlowPlusState
  extends CFlow
{
  private Object[] state;
  
  public CFlowPlusState(Object[] paramArrayOfObject)
  {
    state = paramArrayOfObject;
  }
  
  public CFlowPlusState(Object[] paramArrayOfObject, Object paramObject)
  {
    super(paramObject);
    state = paramArrayOfObject;
  }
  
  public Object get(int paramInt)
  {
    return state[paramInt];
  }
}
