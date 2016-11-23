package org.aspectj.internal.lang.reflect;

import java.lang.annotation.Annotation;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclareAnnotation;
import org.aspectj.lang.reflect.DeclareAnnotation.Kind;
import org.aspectj.lang.reflect.SignaturePattern;
import org.aspectj.lang.reflect.TypePattern;

public class DeclareAnnotationImpl
  implements DeclareAnnotation
{
  private String annText;
  private AjType<?> declaringType;
  private DeclareAnnotation.Kind kind;
  private SignaturePattern signaturePattern;
  private Annotation theAnnotation;
  private TypePattern typePattern;
  
  public DeclareAnnotationImpl(AjType<?> paramAjType, String paramString1, String paramString2, Annotation paramAnnotation, String paramString3)
  {
    declaringType = paramAjType;
    if (paramString1.equals("at_type"))
    {
      kind = DeclareAnnotation.Kind.Type;
      if (kind != DeclareAnnotation.Kind.Type) {
        break label144;
      }
      typePattern = new TypePatternImpl(paramString2);
    }
    for (;;)
    {
      theAnnotation = paramAnnotation;
      annText = paramString3;
      return;
      if (paramString1.equals("at_field"))
      {
        kind = DeclareAnnotation.Kind.Field;
        break;
      }
      if (paramString1.equals("at_method"))
      {
        kind = DeclareAnnotation.Kind.Method;
        break;
      }
      if (paramString1.equals("at_constructor"))
      {
        kind = DeclareAnnotation.Kind.Constructor;
        break;
      }
      throw new IllegalStateException("Unknown declare annotation kind: " + paramString1);
      label144:
      signaturePattern = new SignaturePatternImpl(paramString2);
    }
  }
  
  public Annotation getAnnotation()
  {
    return theAnnotation;
  }
  
  public String getAnnotationAsText()
  {
    return annText;
  }
  
  public AjType<?> getDeclaringType()
  {
    return declaringType;
  }
  
  public DeclareAnnotation.Kind getKind()
  {
    return kind;
  }
  
  public SignaturePattern getSignaturePattern()
  {
    return signaturePattern;
  }
  
  public TypePattern getTypePattern()
  {
    return typePattern;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("declare @");
    switch (1.$SwitchMap$org$aspectj$lang$reflect$DeclareAnnotation$Kind[getKind().ordinal()])
    {
    }
    for (;;)
    {
      localStringBuffer.append(" : ");
      localStringBuffer.append(getAnnotationAsText());
      return localStringBuffer.toString();
      localStringBuffer.append("type : ");
      localStringBuffer.append(getTypePattern().asString());
      continue;
      localStringBuffer.append("method : ");
      localStringBuffer.append(getSignaturePattern().asString());
      continue;
      localStringBuffer.append("field : ");
      localStringBuffer.append(getSignaturePattern().asString());
      continue;
      localStringBuffer.append("constructor : ");
      localStringBuffer.append(getSignaturePattern().asString());
    }
  }
}
