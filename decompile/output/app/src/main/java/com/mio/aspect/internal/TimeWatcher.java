package com.mio.aspect.internal;

import java.util.concurrent.TimeUnit;

public class TimeWatcher
{
  private long elapsedTime;
  private long endTime;
  private long startTime;
  
  public TimeWatcher() {}
  
  private void reset()
  {
    startTime = 0L;
    endTime = 0L;
    elapsedTime = 0L;
  }
  
  public long getTotalTimeMillis()
  {
    long l = 0L;
    if (elapsedTime != l) {
      l = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
    }
    return l;
  }
  
  public void start()
  {
    reset();
    startTime = System.nanoTime();
  }
  
  public void stop()
  {
    if (startTime != 0L)
    {
      endTime = System.nanoTime();
      elapsedTime = (endTime - startTime);
      return;
    }
    reset();
  }
}
