package org.aspectj.lang.reflect;

public enum AdviceKind
{
  static
  {
    AFTER = new AdviceKind("AFTER", 1);
    AFTER_RETURNING = new AdviceKind("AFTER_RETURNING", 2);
    AFTER_THROWING = new AdviceKind("AFTER_THROWING", 3);
    AROUND = new AdviceKind("AROUND", 4);
    AdviceKind[] arrayOfAdviceKind = new AdviceKind[5];
    arrayOfAdviceKind[0] = BEFORE;
    arrayOfAdviceKind[1] = AFTER;
    arrayOfAdviceKind[2] = AFTER_RETURNING;
    arrayOfAdviceKind[3] = AFTER_THROWING;
    arrayOfAdviceKind[4] = AROUND;
    $VALUES = arrayOfAdviceKind;
  }
  
  private AdviceKind() {}
}
