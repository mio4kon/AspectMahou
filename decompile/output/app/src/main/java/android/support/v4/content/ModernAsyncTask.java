package android.support.v4.content;

import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

abstract class ModernAsyncTask<Params, Progress, Result>
{
  private static final int CORE_POOL_SIZE = 5;
  private static final int KEEP_ALIVE = 1;
  private static final String LOG_TAG = "AsyncTask";
  private static final int MAXIMUM_POOL_SIZE = 128;
  private static final int MESSAGE_POST_PROGRESS = 2;
  private static final int MESSAGE_POST_RESULT = 1;
  public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(5, 128, 1L, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
  private static volatile Executor sDefaultExecutor = THREAD_POOL_EXECUTOR;
  private static InternalHandler sHandler;
  private static final BlockingQueue<Runnable> sPoolWorkQueue;
  private static final ThreadFactory sThreadFactory = new ThreadFactory()
  {
    private final AtomicInteger mCount = new AtomicInteger(1);
    
    public Thread newThread(Runnable paramAnonymousRunnable)
    {
      return new Thread(paramAnonymousRunnable, "ModernAsyncTask #" + mCount.getAndIncrement());
    }
  };
  private final FutureTask<Result> mFuture = new FutureTask(mWorker)
  {
    protected void done()
    {
      try
      {
        Object localObject = get();
        postResultIfNotInvoked(localObject);
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        Log.w("AsyncTask", localInterruptedException);
        return;
      }
      catch (ExecutionException localExecutionException)
      {
        throw new RuntimeException("An error occurred while executing doInBackground()", localExecutionException.getCause());
      }
      catch (CancellationException localCancellationException)
      {
        postResultIfNotInvoked(null);
        return;
      }
      catch (Throwable localThrowable)
      {
        throw new RuntimeException("An error occurred while executing doInBackground()", localThrowable);
      }
    }
  };
  private volatile Status mStatus = Status.PENDING;
  final AtomicBoolean mTaskInvoked = new AtomicBoolean();
  private final WorkerRunnable<Params, Result> mWorker = new WorkerRunnable()
  {
    public Result call()
      throws Exception
    {
      mTaskInvoked.set(true);
      Object localObject1 = null;
      try
      {
        Process.setThreadPriority(10);
        localObject1 = doInBackground(mParams);
        Binder.flushPendingCommands();
        return localObject1;
      }
      finally
      {
        postResult(localObject1);
      }
    }
  };
  
  static
  {
    sPoolWorkQueue = new LinkedBlockingQueue(10);
  }
  
  public ModernAsyncTask() {}
  
  public static void execute(Runnable paramRunnable)
  {
    sDefaultExecutor.execute(paramRunnable);
  }
  
  private static Handler getHandler()
  {
    try
    {
      if (sHandler == null) {
        sHandler = new InternalHandler();
      }
      InternalHandler localInternalHandler = sHandler;
      return localInternalHandler;
    }
    finally {}
  }
  
  public static void setDefaultExecutor(Executor paramExecutor)
  {
    sDefaultExecutor = paramExecutor;
  }
  
  public final boolean cancel(boolean paramBoolean)
  {
    return mFuture.cancel(paramBoolean);
  }
  
  protected abstract Result doInBackground(Params... paramVarArgs);
  
  public final ModernAsyncTask<Params, Progress, Result> execute(Params... paramVarArgs)
  {
    return executeOnExecutor(sDefaultExecutor, paramVarArgs);
  }
  
  public final ModernAsyncTask<Params, Progress, Result> executeOnExecutor(Executor paramExecutor, Params... paramVarArgs)
  {
    if (mStatus != Status.PENDING) {}
    switch (4.$SwitchMap$android$support$v4$content$ModernAsyncTask$Status[mStatus.ordinal()])
    {
    default: 
      mStatus = Status.RUNNING;
      onPreExecute();
      mWorker.mParams = paramVarArgs;
      paramExecutor.execute(mFuture);
      return this;
    case 1: 
      throw new IllegalStateException("Cannot execute task: the task is already running.");
    }
    throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
  }
  
  void finish(Result paramResult)
  {
    if (isCancelled()) {
      onCancelled(paramResult);
    }
    for (;;)
    {
      mStatus = Status.FINISHED;
      return;
      onPostExecute(paramResult);
    }
  }
  
  public final Result get()
    throws InterruptedException, ExecutionException
  {
    return mFuture.get();
  }
  
  public final Result get(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    return mFuture.get(paramLong, paramTimeUnit);
  }
  
  public final Status getStatus()
  {
    return mStatus;
  }
  
  public final boolean isCancelled()
  {
    return mFuture.isCancelled();
  }
  
  protected void onCancelled() {}
  
  protected void onCancelled(Result paramResult)
  {
    onCancelled();
  }
  
  protected void onPostExecute(Result paramResult) {}
  
  protected void onPreExecute() {}
  
  protected void onProgressUpdate(Progress... paramVarArgs) {}
  
  Result postResult(Result paramResult)
  {
    getHandler().obtainMessage(1, new AsyncTaskResult(this, new Object[] { paramResult })).sendToTarget();
    return paramResult;
  }
  
  void postResultIfNotInvoked(Result paramResult)
  {
    if (!mTaskInvoked.get()) {
      postResult(paramResult);
    }
  }
  
  protected final void publishProgress(Progress... paramVarArgs)
  {
    if (!isCancelled()) {
      getHandler().obtainMessage(2, new AsyncTaskResult(this, paramVarArgs)).sendToTarget();
    }
  }
  
  private static class AsyncTaskResult<Data>
  {
    final Data[] mData;
    final ModernAsyncTask mTask;
    
    AsyncTaskResult(ModernAsyncTask paramModernAsyncTask, Data... paramVarArgs)
    {
      mTask = paramModernAsyncTask;
      mData = paramVarArgs;
    }
  }
  
  private static class InternalHandler
    extends Handler
  {
    public InternalHandler()
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      ModernAsyncTask.AsyncTaskResult localAsyncTaskResult = (ModernAsyncTask.AsyncTaskResult)obj;
      switch (what)
      {
      default: 
        return;
      case 1: 
        mTask.finish(mData[0]);
        return;
      }
      mTask.onProgressUpdate(mData);
    }
  }
  
  public static enum Status
  {
    static
    {
      FINISHED = new Status("FINISHED", 2);
      Status[] arrayOfStatus = new Status[3];
      arrayOfStatus[0] = PENDING;
      arrayOfStatus[1] = RUNNING;
      arrayOfStatus[2] = FINISHED;
      $VALUES = arrayOfStatus;
    }
    
    private Status() {}
  }
  
  private static abstract class WorkerRunnable<Params, Result>
    implements Callable<Result>
  {
    Params[] mParams;
    
    WorkerRunnable() {}
  }
}
