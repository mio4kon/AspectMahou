package org.aspectj.runtime.reflect;

import org.aspectj.lang.JoinPoint.EnclosingStaticPart;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;

class JoinPointImpl
  implements ProceedingJoinPoint
{
  Object _this;
  private AroundClosure arc;
  Object[] args;
  JoinPoint.StaticPart staticPart;
  Object target;
  
  public JoinPointImpl(JoinPoint.StaticPart paramStaticPart, Object paramObject1, Object paramObject2, Object[] paramArrayOfObject)
  {
    staticPart = paramStaticPart;
    _this = paramObject1;
    target = paramObject2;
    args = paramArrayOfObject;
  }
  
  public Object[] getArgs()
  {
    if (args == null) {
      args = new Object[0];
    }
    Object[] arrayOfObject = new Object[args.length];
    System.arraycopy(args, 0, arrayOfObject, 0, args.length);
    return arrayOfObject;
  }
  
  public String getKind()
  {
    return staticPart.getKind();
  }
  
  public Signature getSignature()
  {
    return staticPart.getSignature();
  }
  
  public SourceLocation getSourceLocation()
  {
    return staticPart.getSourceLocation();
  }
  
  public JoinPoint.StaticPart getStaticPart()
  {
    return staticPart;
  }
  
  public Object getTarget()
  {
    return target;
  }
  
  public Object getThis()
  {
    return _this;
  }
  
  public Object proceed()
    throws Throwable
  {
    if (arc == null) {
      return null;
    }
    return arc.run(arc.getState());
  }
  
  public Object proceed(Object[] paramArrayOfObject)
    throws Throwable
  {
    int i = 1;
    if (arc == null) {
      return null;
    }
    int j = arc.getFlags();
    int k;
    label38:
    int m;
    label49:
    int n;
    label60:
    int i1;
    label70:
    int i2;
    label79:
    Object[] arrayOfObject;
    int i3;
    label96:
    int i5;
    label115:
    int i6;
    int i7;
    int i11;
    if ((0x100000 & j) != 0)
    {
      if ((0x10000 & j) == 0) {
        break label224;
      }
      k = i;
      if ((j & 0x1000) == 0) {
        break label230;
      }
      m = i;
      if ((j & 0x100) == 0) {
        break label236;
      }
      n = i;
      if ((j & 0x10) == 0) {
        break label242;
      }
      i1 = i;
      if ((j & 0x1) == 0) {
        break label248;
      }
      i2 = i;
      arrayOfObject = arc.getState();
      if (m == 0) {
        break label254;
      }
      i3 = i;
      int i4 = 0 + i3;
      if ((i1 == 0) || (k != 0)) {
        break label260;
      }
      i5 = i;
      i6 = i4 + i5;
      i7 = 0;
      if (m != 0)
      {
        i7 = 0;
        if (n != 0)
        {
          i7 = 1;
          arrayOfObject[0] = paramArrayOfObject[0];
        }
      }
      if ((i1 != 0) && (i2 != 0))
      {
        if (k == 0) {
          break label277;
        }
        if (n == 0) {
          break label266;
        }
        i11 = i;
        label171:
        i7 = i11 + 1;
        if (n == 0) {
          break label272;
        }
      }
    }
    for (;;)
    {
      arrayOfObject[0] = paramArrayOfObject[i];
      for (int i8 = i7; i8 < paramArrayOfObject.length; i8++) {
        arrayOfObject[(i6 + (i8 - i7))] = paramArrayOfObject[i8];
      }
      break;
      label224:
      k = 0;
      break label38;
      label230:
      m = 0;
      break label49;
      label236:
      n = 0;
      break label60;
      label242:
      i1 = 0;
      break label70;
      label248:
      i2 = 0;
      break label79;
      label254:
      i3 = 0;
      break label96;
      label260:
      i5 = 0;
      break label115;
      label266:
      i11 = 0;
      break label171;
      label272:
      i = 0;
    }
    label277:
    int i9;
    label285:
    int i10;
    if (m != 0)
    {
      i9 = i;
      i7 = i9 + 1;
      if (m == 0) {
        break label321;
      }
      i10 = i;
      label299:
      if (m == 0) {
        break label327;
      }
    }
    for (;;)
    {
      arrayOfObject[i10] = paramArrayOfObject[i];
      break;
      i9 = 0;
      break label285;
      label321:
      i10 = 0;
      break label299;
      label327:
      i = 0;
    }
    return arc.run(arrayOfObject);
  }
  
  public void set$AroundClosure(AroundClosure paramAroundClosure)
  {
    arc = paramAroundClosure;
  }
  
  public final String toLongString()
  {
    return staticPart.toLongString();
  }
  
  public final String toShortString()
  {
    return staticPart.toShortString();
  }
  
  public final String toString()
  {
    return staticPart.toString();
  }
  
  static class EnclosingStaticPartImpl
    extends JoinPointImpl.StaticPartImpl
    implements JoinPoint.EnclosingStaticPart
  {
    public EnclosingStaticPartImpl(int paramInt, String paramString, Signature paramSignature, SourceLocation paramSourceLocation)
    {
      super(paramString, paramSignature, paramSourceLocation);
    }
  }
  
  static class StaticPartImpl
    implements JoinPoint.StaticPart
  {
    private int id;
    String kind;
    Signature signature;
    SourceLocation sourceLocation;
    
    public StaticPartImpl(int paramInt, String paramString, Signature paramSignature, SourceLocation paramSourceLocation)
    {
      kind = paramString;
      signature = paramSignature;
      sourceLocation = paramSourceLocation;
      id = paramInt;
    }
    
    public int getId()
    {
      return id;
    }
    
    public String getKind()
    {
      return kind;
    }
    
    public Signature getSignature()
    {
      return signature;
    }
    
    public SourceLocation getSourceLocation()
    {
      return sourceLocation;
    }
    
    public final String toLongString()
    {
      return toString(StringMaker.longStringMaker);
    }
    
    public final String toShortString()
    {
      return toString(StringMaker.shortStringMaker);
    }
    
    public final String toString()
    {
      return toString(StringMaker.middleStringMaker);
    }
    
    String toString(StringMaker paramStringMaker)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append(paramStringMaker.makeKindName(getKind()));
      localStringBuffer.append("(");
      localStringBuffer.append(((SignatureImpl)getSignature()).toString(paramStringMaker));
      localStringBuffer.append(")");
      return localStringBuffer.toString();
    }
  }
}
