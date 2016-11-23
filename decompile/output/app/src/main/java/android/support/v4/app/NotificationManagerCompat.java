package android.support.v4.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.support.v4.os.BuildCompat;
import android.util.Log;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class NotificationManagerCompat
{
  public static final String ACTION_BIND_SIDE_CHANNEL = "android.support.BIND_NOTIFICATION_SIDE_CHANNEL";
  public static final String EXTRA_USE_SIDE_CHANNEL = "android.support.useSideChannel";
  private static final Impl IMPL;
  public static final int IMPORTANCE_DEFAULT = 3;
  public static final int IMPORTANCE_HIGH = 4;
  public static final int IMPORTANCE_LOW = 2;
  public static final int IMPORTANCE_MAX = 5;
  public static final int IMPORTANCE_MIN = 1;
  public static final int IMPORTANCE_NONE = 0;
  public static final int IMPORTANCE_UNSPECIFIED = -1000;
  static final int MAX_SIDE_CHANNEL_SDK_VERSION = 19;
  private static final String SETTING_ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
  static final int SIDE_CHANNEL_BIND_FLAGS = 0;
  private static final int SIDE_CHANNEL_RETRY_BASE_INTERVAL_MS = 1000;
  private static final int SIDE_CHANNEL_RETRY_MAX_COUNT = 6;
  private static final String TAG = "NotifManCompat";
  private static Set<String> sEnabledNotificationListenerPackages;
  private static String sEnabledNotificationListeners;
  private static final Object sEnabledNotificationListenersLock = new Object();
  private static final Object sLock;
  private static SideChannelManager sSideChannelManager;
  private final Context mContext;
  private final NotificationManager mNotificationManager;
  
  static
  {
    sEnabledNotificationListenerPackages = new HashSet();
    sLock = new Object();
    if (BuildCompat.isAtLeastN()) {
      IMPL = new ImplApi24();
    }
    for (;;)
    {
      SIDE_CHANNEL_BIND_FLAGS = IMPL.getSideChannelBindFlags();
      return;
      if (Build.VERSION.SDK_INT >= 19) {
        IMPL = new ImplKitKat();
      } else if (Build.VERSION.SDK_INT >= 14) {
        IMPL = new ImplIceCreamSandwich();
      } else {
        IMPL = new ImplBase();
      }
    }
  }
  
  private NotificationManagerCompat(Context paramContext)
  {
    mContext = paramContext;
    mNotificationManager = ((NotificationManager)mContext.getSystemService("notification"));
  }
  
  public static NotificationManagerCompat from(Context paramContext)
  {
    return new NotificationManagerCompat(paramContext);
  }
  
  public static Set<String> getEnabledListenerPackages(Context paramContext)
  {
    String str = Settings.Secure.getString(paramContext.getContentResolver(), "enabled_notification_listeners");
    Object localObject1 = sEnabledNotificationListenersLock;
    if (str != null) {}
    for (;;)
    {
      int j;
      try
      {
        if (!str.equals(sEnabledNotificationListeners))
        {
          String[] arrayOfString = str.split(":");
          HashSet localHashSet = new HashSet(arrayOfString.length);
          int i = arrayOfString.length;
          j = 0;
          if (j < i)
          {
            ComponentName localComponentName = ComponentName.unflattenFromString(arrayOfString[j]);
            if (localComponentName != null) {
              localHashSet.add(localComponentName.getPackageName());
            }
          }
          else
          {
            sEnabledNotificationListenerPackages = localHashSet;
            sEnabledNotificationListeners = str;
          }
        }
        else
        {
          Set localSet = sEnabledNotificationListenerPackages;
          return localSet;
        }
      }
      finally {}
      j++;
    }
  }
  
  private void pushSideChannelQueue(Task paramTask)
  {
    synchronized (sLock)
    {
      if (sSideChannelManager == null) {
        sSideChannelManager = new SideChannelManager(mContext.getApplicationContext());
      }
      sSideChannelManager.queueTask(paramTask);
      return;
    }
  }
  
  private static boolean useSideChannelForNotification(Notification paramNotification)
  {
    Bundle localBundle = NotificationCompat.getExtras(paramNotification);
    return (localBundle != null) && (localBundle.getBoolean("android.support.useSideChannel"));
  }
  
  public boolean areNotificationsEnabled()
  {
    return IMPL.areNotificationsEnabled(mContext, mNotificationManager);
  }
  
  public void cancel(int paramInt)
  {
    cancel(null, paramInt);
  }
  
  public void cancel(String paramString, int paramInt)
  {
    IMPL.cancelNotification(mNotificationManager, paramString, paramInt);
    if (Build.VERSION.SDK_INT <= 19) {
      pushSideChannelQueue(new CancelTask(mContext.getPackageName(), paramInt, paramString));
    }
  }
  
  public void cancelAll()
  {
    mNotificationManager.cancelAll();
    if (Build.VERSION.SDK_INT <= 19) {
      pushSideChannelQueue(new CancelTask(mContext.getPackageName()));
    }
  }
  
  public int getImportance()
  {
    return IMPL.getImportance(mNotificationManager);
  }
  
  public void notify(int paramInt, Notification paramNotification)
  {
    notify(null, paramInt, paramNotification);
  }
  
  public void notify(String paramString, int paramInt, Notification paramNotification)
  {
    if (useSideChannelForNotification(paramNotification))
    {
      pushSideChannelQueue(new NotifyTask(mContext.getPackageName(), paramInt, paramString, paramNotification));
      IMPL.cancelNotification(mNotificationManager, paramString, paramInt);
      return;
    }
    IMPL.postNotification(mNotificationManager, paramString, paramInt, paramNotification);
  }
  
  private static class CancelTask
    implements NotificationManagerCompat.Task
  {
    final boolean all;
    final int id;
    final String packageName;
    final String tag;
    
    public CancelTask(String paramString)
    {
      packageName = paramString;
      id = 0;
      tag = null;
      all = true;
    }
    
    public CancelTask(String paramString1, int paramInt, String paramString2)
    {
      packageName = paramString1;
      id = paramInt;
      tag = paramString2;
      all = false;
    }
    
    public void send(INotificationSideChannel paramINotificationSideChannel)
      throws RemoteException
    {
      if (all)
      {
        paramINotificationSideChannel.cancelAll(packageName);
        return;
      }
      paramINotificationSideChannel.cancel(packageName, id, tag);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("CancelTask[");
      localStringBuilder.append("packageName:").append(packageName);
      localStringBuilder.append(", id:").append(id);
      localStringBuilder.append(", tag:").append(tag);
      localStringBuilder.append(", all:").append(all);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  static abstract interface Impl
  {
    public abstract boolean areNotificationsEnabled(Context paramContext, NotificationManager paramNotificationManager);
    
    public abstract void cancelNotification(NotificationManager paramNotificationManager, String paramString, int paramInt);
    
    public abstract int getImportance(NotificationManager paramNotificationManager);
    
    public abstract int getSideChannelBindFlags();
    
    public abstract void postNotification(NotificationManager paramNotificationManager, String paramString, int paramInt, Notification paramNotification);
  }
  
  static class ImplApi24
    extends NotificationManagerCompat.ImplKitKat
  {
    ImplApi24() {}
    
    public boolean areNotificationsEnabled(Context paramContext, NotificationManager paramNotificationManager)
    {
      return NotificationManagerCompatApi24.areNotificationsEnabled(paramNotificationManager);
    }
    
    public int getImportance(NotificationManager paramNotificationManager)
    {
      return NotificationManagerCompatApi24.getImportance(paramNotificationManager);
    }
  }
  
  static class ImplBase
    implements NotificationManagerCompat.Impl
  {
    ImplBase() {}
    
    public boolean areNotificationsEnabled(Context paramContext, NotificationManager paramNotificationManager)
    {
      return true;
    }
    
    public void cancelNotification(NotificationManager paramNotificationManager, String paramString, int paramInt)
    {
      paramNotificationManager.cancel(paramString, paramInt);
    }
    
    public int getImportance(NotificationManager paramNotificationManager)
    {
      return 64536;
    }
    
    public int getSideChannelBindFlags()
    {
      return 1;
    }
    
    public void postNotification(NotificationManager paramNotificationManager, String paramString, int paramInt, Notification paramNotification)
    {
      paramNotificationManager.notify(paramString, paramInt, paramNotification);
    }
  }
  
  static class ImplIceCreamSandwich
    extends NotificationManagerCompat.ImplBase
  {
    ImplIceCreamSandwich() {}
    
    public int getSideChannelBindFlags()
    {
      return 33;
    }
  }
  
  static class ImplKitKat
    extends NotificationManagerCompat.ImplIceCreamSandwich
  {
    ImplKitKat() {}
    
    public boolean areNotificationsEnabled(Context paramContext, NotificationManager paramNotificationManager)
    {
      return NotificationManagerCompatKitKat.areNotificationsEnabled(paramContext);
    }
  }
  
  private static class NotifyTask
    implements NotificationManagerCompat.Task
  {
    final int id;
    final Notification notif;
    final String packageName;
    final String tag;
    
    public NotifyTask(String paramString1, int paramInt, String paramString2, Notification paramNotification)
    {
      packageName = paramString1;
      id = paramInt;
      tag = paramString2;
      notif = paramNotification;
    }
    
    public void send(INotificationSideChannel paramINotificationSideChannel)
      throws RemoteException
    {
      paramINotificationSideChannel.notify(packageName, id, tag, notif);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("NotifyTask[");
      localStringBuilder.append("packageName:").append(packageName);
      localStringBuilder.append(", id:").append(id);
      localStringBuilder.append(", tag:").append(tag);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  private static class ServiceConnectedEvent
  {
    final ComponentName componentName;
    final IBinder iBinder;
    
    public ServiceConnectedEvent(ComponentName paramComponentName, IBinder paramIBinder)
    {
      componentName = paramComponentName;
      iBinder = paramIBinder;
    }
  }
  
  private static class SideChannelManager
    implements Handler.Callback, ServiceConnection
  {
    private static final String KEY_BINDER = "binder";
    private static final int MSG_QUEUE_TASK = 0;
    private static final int MSG_RETRY_LISTENER_QUEUE = 3;
    private static final int MSG_SERVICE_CONNECTED = 1;
    private static final int MSG_SERVICE_DISCONNECTED = 2;
    private Set<String> mCachedEnabledPackages = new HashSet();
    private final Context mContext;
    private final Handler mHandler;
    private final HandlerThread mHandlerThread;
    private final Map<ComponentName, ListenerRecord> mRecordMap = new HashMap();
    
    public SideChannelManager(Context paramContext)
    {
      mContext = paramContext;
      mHandlerThread = new HandlerThread("NotificationManagerCompat");
      mHandlerThread.start();
      mHandler = new Handler(mHandlerThread.getLooper(), this);
    }
    
    private boolean ensureServiceBound(ListenerRecord paramListenerRecord)
    {
      if (bound) {
        return true;
      }
      Intent localIntent = new Intent("android.support.BIND_NOTIFICATION_SIDE_CHANNEL").setComponent(componentName);
      bound = mContext.bindService(localIntent, this, NotificationManagerCompat.SIDE_CHANNEL_BIND_FLAGS);
      if (bound) {
        retryCount = 0;
      }
      for (;;)
      {
        return bound;
        Log.w("NotifManCompat", "Unable to bind to listener " + componentName);
        mContext.unbindService(this);
      }
    }
    
    private void ensureServiceUnbound(ListenerRecord paramListenerRecord)
    {
      if (bound)
      {
        mContext.unbindService(this);
        bound = false;
      }
      service = null;
    }
    
    private void handleQueueTask(NotificationManagerCompat.Task paramTask)
    {
      updateListenerMap();
      Iterator localIterator = mRecordMap.values().iterator();
      while (localIterator.hasNext())
      {
        ListenerRecord localListenerRecord = (ListenerRecord)localIterator.next();
        taskQueue.add(paramTask);
        processListenerQueue(localListenerRecord);
      }
    }
    
    private void handleRetryListenerQueue(ComponentName paramComponentName)
    {
      ListenerRecord localListenerRecord = (ListenerRecord)mRecordMap.get(paramComponentName);
      if (localListenerRecord != null) {
        processListenerQueue(localListenerRecord);
      }
    }
    
    private void handleServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      ListenerRecord localListenerRecord = (ListenerRecord)mRecordMap.get(paramComponentName);
      if (localListenerRecord != null)
      {
        service = INotificationSideChannel.Stub.asInterface(paramIBinder);
        retryCount = 0;
        processListenerQueue(localListenerRecord);
      }
    }
    
    private void handleServiceDisconnected(ComponentName paramComponentName)
    {
      ListenerRecord localListenerRecord = (ListenerRecord)mRecordMap.get(paramComponentName);
      if (localListenerRecord != null) {
        ensureServiceUnbound(localListenerRecord);
      }
    }
    
    private void processListenerQueue(ListenerRecord paramListenerRecord)
    {
      if (Log.isLoggable("NotifManCompat", 3)) {
        Log.d("NotifManCompat", "Processing component " + componentName + ", " + taskQueue.size() + " queued tasks");
      }
      if (taskQueue.isEmpty()) {}
      do
      {
        return;
        if ((!ensureServiceBound(paramListenerRecord)) || (service == null))
        {
          scheduleListenerRetry(paramListenerRecord);
          return;
        }
        try
        {
          Object localObject;
          do
          {
            if (Log.isLoggable("NotifManCompat", 3)) {
              Log.d("NotifManCompat", "Sending task " + localObject);
            }
            ((NotificationManagerCompat.Task)localObject).send(service);
            taskQueue.remove();
            localObject = (NotificationManagerCompat.Task)taskQueue.peek();
          } while (localObject != null);
        }
        catch (DeadObjectException localDeadObjectException)
        {
          for (;;)
          {
            if (Log.isLoggable("NotifManCompat", 3)) {
              Log.d("NotifManCompat", "Remote service has died: " + componentName);
            }
          }
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Log.w("NotifManCompat", "RemoteException communicating with " + componentName, localRemoteException);
          }
        }
      } while (taskQueue.isEmpty());
      scheduleListenerRetry(paramListenerRecord);
    }
    
    private void scheduleListenerRetry(ListenerRecord paramListenerRecord)
    {
      if (mHandler.hasMessages(3, componentName)) {
        return;
      }
      retryCount = (1 + retryCount);
      if (retryCount > 6)
      {
        Log.w("NotifManCompat", "Giving up on delivering " + taskQueue.size() + " tasks to " + componentName + " after " + retryCount + " retries");
        taskQueue.clear();
        return;
      }
      int i = 1000 * (1 << -1 + retryCount);
      if (Log.isLoggable("NotifManCompat", 3)) {
        Log.d("NotifManCompat", "Scheduling retry for " + i + " ms");
      }
      Message localMessage = mHandler.obtainMessage(3, componentName);
      mHandler.sendMessageDelayed(localMessage, i);
    }
    
    private void updateListenerMap()
    {
      Set localSet = NotificationManagerCompat.getEnabledListenerPackages(mContext);
      if (localSet.equals(mCachedEnabledPackages)) {}
      for (;;)
      {
        return;
        mCachedEnabledPackages = localSet;
        List localList = mContext.getPackageManager().queryIntentServices(new Intent().setAction("android.support.BIND_NOTIFICATION_SIDE_CHANNEL"), 4);
        HashSet localHashSet = new HashSet();
        Iterator localIterator1 = localList.iterator();
        while (localIterator1.hasNext())
        {
          ResolveInfo localResolveInfo = (ResolveInfo)localIterator1.next();
          if (localSet.contains(serviceInfo.packageName))
          {
            ComponentName localComponentName2 = new ComponentName(serviceInfo.packageName, serviceInfo.name);
            if (serviceInfo.permission != null) {
              Log.w("NotifManCompat", "Permission present on component " + localComponentName2 + ", not adding listener record.");
            } else {
              localHashSet.add(localComponentName2);
            }
          }
        }
        Iterator localIterator2 = localHashSet.iterator();
        while (localIterator2.hasNext())
        {
          ComponentName localComponentName1 = (ComponentName)localIterator2.next();
          if (!mRecordMap.containsKey(localComponentName1))
          {
            if (Log.isLoggable("NotifManCompat", 3)) {
              Log.d("NotifManCompat", "Adding listener record for " + localComponentName1);
            }
            mRecordMap.put(localComponentName1, new ListenerRecord(localComponentName1));
          }
        }
        Iterator localIterator3 = mRecordMap.entrySet().iterator();
        while (localIterator3.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator3.next();
          if (!localHashSet.contains(localEntry.getKey()))
          {
            if (Log.isLoggable("NotifManCompat", 3)) {
              Log.d("NotifManCompat", "Removing listener record for " + localEntry.getKey());
            }
            ensureServiceUnbound((ListenerRecord)localEntry.getValue());
            localIterator3.remove();
          }
        }
      }
    }
    
    public boolean handleMessage(Message paramMessage)
    {
      switch (what)
      {
      default: 
        return false;
      case 0: 
        handleQueueTask((NotificationManagerCompat.Task)obj);
        return true;
      case 1: 
        NotificationManagerCompat.ServiceConnectedEvent localServiceConnectedEvent = (NotificationManagerCompat.ServiceConnectedEvent)obj;
        handleServiceConnected(componentName, iBinder);
        return true;
      case 2: 
        handleServiceDisconnected((ComponentName)obj);
        return true;
      }
      handleRetryListenerQueue((ComponentName)obj);
      return true;
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      if (Log.isLoggable("NotifManCompat", 3)) {
        Log.d("NotifManCompat", "Connected to service " + paramComponentName);
      }
      mHandler.obtainMessage(1, new NotificationManagerCompat.ServiceConnectedEvent(paramComponentName, paramIBinder)).sendToTarget();
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      if (Log.isLoggable("NotifManCompat", 3)) {
        Log.d("NotifManCompat", "Disconnected from service " + paramComponentName);
      }
      mHandler.obtainMessage(2, paramComponentName).sendToTarget();
    }
    
    public void queueTask(NotificationManagerCompat.Task paramTask)
    {
      mHandler.obtainMessage(0, paramTask).sendToTarget();
    }
    
    private static class ListenerRecord
    {
      public boolean bound = false;
      public final ComponentName componentName;
      public int retryCount = 0;
      public INotificationSideChannel service;
      public LinkedList<NotificationManagerCompat.Task> taskQueue = new LinkedList();
      
      public ListenerRecord(ComponentName paramComponentName)
      {
        componentName = paramComponentName;
      }
    }
  }
  
  private static abstract interface Task
  {
    public abstract void send(INotificationSideChannel paramINotificationSideChannel)
      throws RemoteException;
  }
}