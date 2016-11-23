package com.mio.aspect.aspect;

import android.util.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LogAsect
{
  public static final String POINTCUT_LOG_ACTIVITY = "execution(* com.mio.aop.aspectdemo.MainActivity.on*(..))";
  public static final String POINTCUT_LOG_ACTIVITY_BASE = "execution(* android.support.v7.app.AppCompatActivity.on*(..))";
  private static Throwable ajc$initFailureCause;
  public static final LogAsect ajc$perSingletonInstance;
  
  static
  {
    try
    {
      ajc$postClinit();
      return;
    }
    catch (Throwable localThrowable)
    {
      ajc$initFailureCause = localThrowable;
    }
  }
  
  public LogAsect() {}
  
  private static void ajc$postClinit()
  {
    ajc$perSingletonInstance = new LogAsect();
  }
  
  public static LogAsect aspectOf()
  {
    if (ajc$perSingletonInstance == null) {
      throw new NoAspectBoundException("com.mio.aspect.aspect.LogAsect", ajc$initFailureCause);
    }
    return ajc$perSingletonInstance;
  }
  
  public static boolean hasAspect()
  {
    return ajc$perSingletonInstance != null;
  }
  
  @Before("logForActivity() || logForActivityBase()")
  public void logBefore(JoinPoint paramJoinPoint)
  {
    Signature localSignature = paramJoinPoint.getSignature();
    String str1 = localSignature.getDeclaringType().getSimpleName();
    String str2 = localSignature.getName();
    Log.d("mio4kon", "类:" + str1 + " ,方法:" + str2);
  }
  
  @Pointcut("execution(* com.mio.aop.aspectdemo.MainActivity.on*(..))")
  public void logForActivity() {}
  
  @Pointcut("execution(* android.support.v7.app.AppCompatActivity.on*(..))")
  public void logForActivityBase() {}
}
