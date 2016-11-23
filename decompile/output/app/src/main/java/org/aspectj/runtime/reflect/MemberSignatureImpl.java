package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.MemberSignature;

abstract class MemberSignatureImpl
  extends SignatureImpl
  implements MemberSignature
{
  MemberSignatureImpl(int paramInt, String paramString, Class paramClass)
  {
    super(paramInt, paramString, paramClass);
  }
  
  public MemberSignatureImpl(String paramString)
  {
    super(paramString);
  }
}
