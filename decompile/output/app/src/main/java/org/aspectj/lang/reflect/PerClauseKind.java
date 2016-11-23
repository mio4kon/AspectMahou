package org.aspectj.lang.reflect;

public enum PerClauseKind
{
  static
  {
    PERTHIS = new PerClauseKind("PERTHIS", 1);
    PERTARGET = new PerClauseKind("PERTARGET", 2);
    PERCFLOW = new PerClauseKind("PERCFLOW", 3);
    PERCFLOWBELOW = new PerClauseKind("PERCFLOWBELOW", 4);
    PERTYPEWITHIN = new PerClauseKind("PERTYPEWITHIN", 5);
    PerClauseKind[] arrayOfPerClauseKind = new PerClauseKind[6];
    arrayOfPerClauseKind[0] = SINGLETON;
    arrayOfPerClauseKind[1] = PERTHIS;
    arrayOfPerClauseKind[2] = PERTARGET;
    arrayOfPerClauseKind[3] = PERCFLOW;
    arrayOfPerClauseKind[4] = PERCFLOWBELOW;
    arrayOfPerClauseKind[5] = PERTYPEWITHIN;
    $VALUES = arrayOfPerClauseKind;
  }
  
  private PerClauseKind() {}
}
