package org.aspectj.lang.reflect;

import java.lang.annotation.Annotation;

public abstract interface DeclareAnnotation
{
  public abstract Annotation getAnnotation();
  
  public abstract String getAnnotationAsText();
  
  public abstract AjType<?> getDeclaringType();
  
  public abstract Kind getKind();
  
  public abstract SignaturePattern getSignaturePattern();
  
  public abstract TypePattern getTypePattern();
  
  public static enum Kind
  {
    static
    {
      Constructor = new Kind("Constructor", 2);
      Type = new Kind("Type", 3);
      Kind[] arrayOfKind = new Kind[4];
      arrayOfKind[0] = Field;
      arrayOfKind[1] = Method;
      arrayOfKind[2] = Constructor;
      arrayOfKind[3] = Type;
      $VALUES = arrayOfKind;
    }
    
    private Kind() {}
  }
}
