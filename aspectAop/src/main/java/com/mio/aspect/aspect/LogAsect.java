package com.mio.aspect.aspect;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by mio4kon on 16/11/23.
 */

@Aspect
public class LogAsect {

    public static final String POINTCUT_LOG_ACTIVITY = "execution(* com.mio.aop.aspectdemo.MainActivity.on*(..))";
    public static final String POINTCUT_LOG_ACTIVITY_BASE = "execution(* android.support.v7.app.AppCompatActivity.on*(..))";


    @Pointcut(POINTCUT_LOG_ACTIVITY)
    public void logForActivity() {
    }

    //no use
    @Pointcut(POINTCUT_LOG_ACTIVITY_BASE)
    public void logForActivityBase() {
    }


    @Before("logForActivity() || logForActivityBase()")
    public void logBefore(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Log.d("mio4kon", "类:" + className + " ,方法:" + methodName);
    }
}
