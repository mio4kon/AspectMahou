package com.mio.aspect.aspect;

import android.util.Log;

import com.mio.aspect.internal.TimeWatcher;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by mio4kon on 16/11/22.
 */

@Aspect
public class TimeAspect {

    public static final String POINTCUT_METHOD = "execution(@com.mio.aspect.annotation.TimeTrace * *(..))";
    public static final String POINTCUT_CONSTRUCTOR = "execution(@com.mio.aspect.annotation.TimeTrace *.new(..))";


    @Pointcut(POINTCUT_METHOD)
    public void methodTimeTrace() {

    }

    @Pointcut(POINTCUT_CONSTRUCTOR)
    public void constructorTimeTrace() {

    }

    @Around("methodTimeTrace() || constructorTimeTrace()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        TimeWatcher timeWatcher = new TimeWatcher();
        timeWatcher.start();
        Object result = joinPoint.proceed();
        timeWatcher.stop();

        Log.d("mio4kon", "类:" + className + ", 方法:" + methodName + ", 耗费时间:" + timeWatcher.getTotalTimeMillis() + "ms");
        return result;
    }


}
