package com.mio.aop.aspectdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.mio.aspect.annotation.TimeTrace;
import com.mio.aspect.aspect.LogAsect;
import com.mio.aspect.aspect.TimeAspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.internal.AroundClosure;
import org.aspectj.runtime.reflect.Factory;

public class MainActivity
  extends AppCompatActivity
{
  private static final JoinPoint.StaticPart ajc$tjp_0;
  private static final JoinPoint.StaticPart ajc$tjp_1;
  private static final JoinPoint.StaticPart ajc$tjp_2;
  private static final JoinPoint.StaticPart ajc$tjp_3;
  private static final JoinPoint.StaticPart ajc$tjp_4;
  
  static {}
  
  public MainActivity() {}
  
  private static void ajc$preClinit()
  {
    Factory localFactory = new Factory("MainActivity.java", MainActivity.class);
    ajc$tjp_0 = localFactory.makeSJP("method-execution", localFactory.makeMethodSig("4", "onCreate", "com.mio.aop.aspectdemo.MainActivity", "android.os.Bundle", "savedInstanceState", "", "void"), 14);
    ajc$tjp_1 = localFactory.makeSJP("method-execution", localFactory.makeMethodSig("4", "onResume", "com.mio.aop.aspectdemo.MainActivity", "", "", "", "void"), 21);
    ajc$tjp_2 = localFactory.makeSJP("method-execution", localFactory.makeMethodSig("4", "onDestroy", "com.mio.aop.aspectdemo.MainActivity", "", "", "", "void"), 31);
    ajc$tjp_3 = localFactory.makeSJP("method-execution", localFactory.makeMethodSig("4", "onStop", "com.mio.aop.aspectdemo.MainActivity", "", "", "", "void"), 36);
    ajc$tjp_4 = localFactory.makeSJP("method-execution", localFactory.makeMethodSig("4", "onStart", "com.mio.aop.aspectdemo.MainActivity", "", "", "", "void"), 41);
  }
  
  static final void onCreate_aroundBody0(MainActivity paramMainActivity, Bundle paramBundle, JoinPoint paramJoinPoint)
  {
    LogAsect.aspectOf().logBefore(paramJoinPoint);
    paramMainActivity.onCreate(paramBundle);
    paramMainActivity.setContentView(2130968602);
  }
  
  static final void onResume_aroundBody2(MainActivity paramMainActivity, JoinPoint paramJoinPoint)
  {
    LogAsect.aspectOf().logBefore(paramJoinPoint);
    paramMainActivity.onResume();
    try
    {
      Thread.sleep(50L);
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      localInterruptedException.printStackTrace();
    }
  }
  
  @TimeTrace
  protected void onCreate(Bundle paramBundle)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this, paramBundle);
    TimeAspect.aspectOf().weaveJoinPoint(new MainActivity.AjcClosure1(new Object[] { this, paramBundle, localJoinPoint }).linkClosureAndJoinPoint(69648));
  }
  
  protected void onDestroy()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    LogAsect.aspectOf().logBefore(localJoinPoint);
    super.onDestroy();
  }
  
  @TimeTrace
  protected void onResume()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    TimeAspect.aspectOf().weaveJoinPoint(new MainActivity.AjcClosure3(new Object[] { this, localJoinPoint }).linkClosureAndJoinPoint(69648));
  }
  
  protected void onStart()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    LogAsect.aspectOf().logBefore(localJoinPoint);
    super.onStart();
  }
  
  protected void onStop()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    LogAsect.aspectOf().logBefore(localJoinPoint);
    super.onStop();
  }
}
