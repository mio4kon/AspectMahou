package android.support.v4.media;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.BundleCompat;
import android.support.v4.media.session.MediaSessionCompat.Token;
import android.support.v4.os.BuildCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class MediaBrowserServiceCompat
  extends Service
{
  static final boolean DEBUG = Log.isLoggable("MBServiceCompat", 3);
  public static final String KEY_MEDIA_ITEM = "media_item";
  static final int RESULT_FLAG_ON_LOAD_ITEM_NOT_IMPLEMENTED = 2;
  static final int RESULT_FLAG_OPTION_NOT_HANDLED = 1;
  public static final String SERVICE_INTERFACE = "android.media.browse.MediaBrowserService";
  static final String TAG = "MBServiceCompat";
  final ArrayMap<IBinder, ConnectionRecord> mConnections = new ArrayMap();
  ConnectionRecord mCurConnection;
  final ServiceHandler mHandler = new ServiceHandler();
  private MediaBrowserServiceImpl mImpl;
  MediaSessionCompat.Token mSession;
  
  public MediaBrowserServiceCompat() {}
  
  void addSubscription(String paramString, ConnectionRecord paramConnectionRecord, IBinder paramIBinder, Bundle paramBundle)
  {
    Object localObject = (List)subscriptions.get(paramString);
    if (localObject == null) {
      localObject = new ArrayList();
    }
    Iterator localIterator = ((List)localObject).iterator();
    while (localIterator.hasNext())
    {
      Pair localPair = (Pair)localIterator.next();
      if ((paramIBinder == first) && (MediaBrowserCompatUtils.areSameOptions(paramBundle, (Bundle)second))) {
        return;
      }
    }
    ((List)localObject).add(new Pair(paramIBinder, paramBundle));
    subscriptions.put(paramString, localObject);
    performLoadChildren(paramString, paramConnectionRecord, paramBundle);
  }
  
  List<MediaBrowserCompat.MediaItem> applyOptions(List<MediaBrowserCompat.MediaItem> paramList, Bundle paramBundle)
  {
    if (paramList == null) {
      paramList = null;
    }
    int i;
    int j;
    do
    {
      return paramList;
      i = paramBundle.getInt("android.media.browse.extra.PAGE", -1);
      j = paramBundle.getInt("android.media.browse.extra.PAGE_SIZE", -1);
    } while ((i == -1) && (j == -1));
    int k = j * i;
    int m = k + j;
    if ((i < 0) || (j < 1) || (k >= paramList.size())) {
      return Collections.EMPTY_LIST;
    }
    if (m > paramList.size()) {
      m = paramList.size();
    }
    return paramList.subList(k, m);
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  public final Bundle getBrowserRootHints()
  {
    return mImpl.getBrowserRootHints();
  }
  
  @Nullable
  public MediaSessionCompat.Token getSessionToken()
  {
    return mSession;
  }
  
  boolean isValidPackage(String paramString, int paramInt)
  {
    if (paramString == null) {}
    for (;;)
    {
      return false;
      String[] arrayOfString = getPackageManager().getPackagesForUid(paramInt);
      int i = arrayOfString.length;
      for (int j = 0; j < i; j++) {
        if (arrayOfString[j].equals(paramString)) {
          return true;
        }
      }
    }
  }
  
  public void notifyChildrenChanged(@NonNull String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("parentId cannot be null in notifyChildrenChanged");
    }
    mImpl.notifyChildrenChanged(paramString, null);
  }
  
  public void notifyChildrenChanged(@NonNull String paramString, @NonNull Bundle paramBundle)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("parentId cannot be null in notifyChildrenChanged");
    }
    if (paramBundle == null) {
      throw new IllegalArgumentException("options cannot be null in notifyChildrenChanged");
    }
    mImpl.notifyChildrenChanged(paramString, paramBundle);
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return mImpl.onBind(paramIntent);
  }
  
  public void onCreate()
  {
    super.onCreate();
    if ((Build.VERSION.SDK_INT >= 24) || (BuildCompat.isAtLeastN())) {
      mImpl = new MediaBrowserServiceImplApi24();
    }
    for (;;)
    {
      mImpl.onCreate();
      return;
      if (Build.VERSION.SDK_INT >= 23) {
        mImpl = new MediaBrowserServiceImplApi23();
      } else if (Build.VERSION.SDK_INT >= 21) {
        mImpl = new MediaBrowserServiceImplApi21();
      } else {
        mImpl = new MediaBrowserServiceImplBase();
      }
    }
  }
  
  @Nullable
  public abstract BrowserRoot onGetRoot(@NonNull String paramString, int paramInt, @Nullable Bundle paramBundle);
  
  public abstract void onLoadChildren(@NonNull String paramString, @NonNull Result<List<MediaBrowserCompat.MediaItem>> paramResult);
  
  public void onLoadChildren(@NonNull String paramString, @NonNull Result<List<MediaBrowserCompat.MediaItem>> paramResult, @NonNull Bundle paramBundle)
  {
    paramResult.setFlags(1);
    onLoadChildren(paramString, paramResult);
  }
  
  public void onLoadItem(String paramString, Result<MediaBrowserCompat.MediaItem> paramResult)
  {
    paramResult.setFlags(2);
    paramResult.sendResult(null);
  }
  
  void performLoadChildren(final String paramString, final ConnectionRecord paramConnectionRecord, final Bundle paramBundle)
  {
    Result local1 = new Result(paramString)
    {
      void onResultSent(List<MediaBrowserCompat.MediaItem> paramAnonymousList, int paramAnonymousInt)
      {
        if (mConnections.get(paramConnectionRecordcallbacks.asBinder()) != paramConnectionRecord)
        {
          if (MediaBrowserServiceCompat.DEBUG) {
            Log.d("MBServiceCompat", "Not sending onLoadChildren result for connection that has been disconnected. pkg=" + paramConnectionRecordpkg + " id=" + paramString);
          }
          return;
        }
        if ((paramAnonymousInt & 0x1) != 0) {}
        for (Object localObject = applyOptions(paramAnonymousList, paramBundle);; localObject = paramAnonymousList) {
          try
          {
            paramConnectionRecordcallbacks.onLoadChildren(paramString, (List)localObject, paramBundle);
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("MBServiceCompat", "Calling onLoadChildren() failed for id=" + paramString + " package=" + paramConnectionRecordpkg);
            return;
          }
        }
      }
    };
    mCurConnection = paramConnectionRecord;
    if (paramBundle == null) {
      onLoadChildren(paramString, local1);
    }
    for (;;)
    {
      mCurConnection = null;
      if (local1.isDone()) {
        break;
      }
      throw new IllegalStateException("onLoadChildren must call detach() or sendResult() before returning for package=" + pkg + " id=" + paramString);
      onLoadChildren(paramString, local1, paramBundle);
    }
  }
  
  void performLoadItem(String paramString, ConnectionRecord paramConnectionRecord, final ResultReceiver paramResultReceiver)
  {
    Result local2 = new Result(paramString)
    {
      void onResultSent(MediaBrowserCompat.MediaItem paramAnonymousMediaItem, int paramAnonymousInt)
      {
        if ((paramAnonymousInt & 0x2) != 0)
        {
          paramResultReceiver.send(-1, null);
          return;
        }
        Bundle localBundle = new Bundle();
        localBundle.putParcelable("media_item", paramAnonymousMediaItem);
        paramResultReceiver.send(0, localBundle);
      }
    };
    mCurConnection = paramConnectionRecord;
    onLoadItem(paramString, local2);
    mCurConnection = null;
    if (!local2.isDone()) {
      throw new IllegalStateException("onLoadItem must call detach() or sendResult() before returning for id=" + paramString);
    }
  }
  
  boolean removeSubscription(String paramString, ConnectionRecord paramConnectionRecord, IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      return subscriptions.remove(paramString) != null;
    }
    List localList = (List)subscriptions.get(paramString);
    boolean bool = false;
    if (localList != null)
    {
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext()) {
        if (paramIBinder == nextfirst)
        {
          bool = true;
          localIterator.remove();
        }
      }
      if (localList.size() == 0) {
        subscriptions.remove(paramString);
      }
    }
    return bool;
  }
  
  public void setSessionToken(MediaSessionCompat.Token paramToken)
  {
    if (paramToken == null) {
      throw new IllegalArgumentException("Session token may not be null.");
    }
    if (mSession != null) {
      throw new IllegalStateException("The session token has already been set.");
    }
    mSession = paramToken;
    mImpl.setSessionToken(paramToken);
  }
  
  public static final class BrowserRoot
  {
    public static final String EXTRA_OFFLINE = "android.service.media.extra.OFFLINE";
    public static final String EXTRA_RECENT = "android.service.media.extra.RECENT";
    public static final String EXTRA_SUGGESTED = "android.service.media.extra.SUGGESTED";
    public static final String EXTRA_SUGGESTION_KEYWORDS = "android.service.media.extra.SUGGESTION_KEYWORDS";
    private final Bundle mExtras;
    private final String mRootId;
    
    public BrowserRoot(@NonNull String paramString, @Nullable Bundle paramBundle)
    {
      if (paramString == null) {
        throw new IllegalArgumentException("The root id in BrowserRoot cannot be null. Use null for BrowserRoot instead.");
      }
      mRootId = paramString;
      mExtras = paramBundle;
    }
    
    public Bundle getExtras()
    {
      return mExtras;
    }
    
    public String getRootId()
    {
      return mRootId;
    }
  }
  
  private class ConnectionRecord
  {
    MediaBrowserServiceCompat.ServiceCallbacks callbacks;
    String pkg;
    MediaBrowserServiceCompat.BrowserRoot root;
    Bundle rootHints;
    HashMap<String, List<Pair<IBinder, Bundle>>> subscriptions = new HashMap();
    
    ConnectionRecord() {}
  }
  
  static abstract interface MediaBrowserServiceImpl
  {
    public abstract Bundle getBrowserRootHints();
    
    public abstract void notifyChildrenChanged(String paramString, Bundle paramBundle);
    
    public abstract IBinder onBind(Intent paramIntent);
    
    public abstract void onCreate();
    
    public abstract void setSessionToken(MediaSessionCompat.Token paramToken);
  }
  
  class MediaBrowserServiceImplApi21
    implements MediaBrowserServiceCompat.MediaBrowserServiceImpl, MediaBrowserServiceCompatApi21.ServiceCompatProxy
  {
    Messenger mMessenger;
    Object mServiceObj;
    
    MediaBrowserServiceImplApi21() {}
    
    public Bundle getBrowserRootHints()
    {
      if (mMessenger == null) {}
      do
      {
        return null;
        if (mCurConnection == null) {
          throw new IllegalStateException("This should be called inside of onLoadChildren or onLoadItem methods");
        }
      } while (mCurConnection.rootHints == null);
      return new Bundle(mCurConnection.rootHints);
    }
    
    public void notifyChildrenChanged(final String paramString, final Bundle paramBundle)
    {
      if (mMessenger == null)
      {
        MediaBrowserServiceCompatApi21.notifyChildrenChanged(mServiceObj, paramString);
        return;
      }
      mHandler.post(new Runnable()
      {
        public void run()
        {
          Iterator localIterator1 = mConnections.keySet().iterator();
          while (localIterator1.hasNext())
          {
            IBinder localIBinder = (IBinder)localIterator1.next();
            MediaBrowserServiceCompat.ConnectionRecord localConnectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)mConnections.get(localIBinder);
            List localList = (List)subscriptions.get(paramString);
            if (localList != null)
            {
              Iterator localIterator2 = localList.iterator();
              while (localIterator2.hasNext())
              {
                Pair localPair = (Pair)localIterator2.next();
                if (MediaBrowserCompatUtils.hasDuplicatedItems(paramBundle, (Bundle)second)) {
                  performLoadChildren(paramString, localConnectionRecord, (Bundle)second);
                }
              }
            }
          }
        }
      });
    }
    
    public IBinder onBind(Intent paramIntent)
    {
      return MediaBrowserServiceCompatApi21.onBind(mServiceObj, paramIntent);
    }
    
    public void onCreate()
    {
      mServiceObj = MediaBrowserServiceCompatApi21.createService(MediaBrowserServiceCompat.this, this);
      MediaBrowserServiceCompatApi21.onCreate(mServiceObj);
    }
    
    public MediaBrowserServiceCompatApi21.BrowserRoot onGetRoot(String paramString, int paramInt, Bundle paramBundle)
    {
      Bundle localBundle = null;
      if (paramBundle != null)
      {
        int i = paramBundle.getInt("extra_client_version", 0);
        localBundle = null;
        if (i != 0)
        {
          paramBundle.remove("extra_client_version");
          mMessenger = new Messenger(mHandler);
          localBundle = new Bundle();
          localBundle.putInt("extra_service_version", 1);
          BundleCompat.putBinder(localBundle, "extra_messenger", mMessenger.getBinder());
        }
      }
      MediaBrowserServiceCompat.BrowserRoot localBrowserRoot = onGetRoot(paramString, paramInt, paramBundle);
      if (localBrowserRoot == null) {
        return null;
      }
      if (localBundle == null) {
        localBundle = localBrowserRoot.getExtras();
      }
      for (;;)
      {
        return new MediaBrowserServiceCompatApi21.BrowserRoot(localBrowserRoot.getRootId(), localBundle);
        if (localBrowserRoot.getExtras() != null) {
          localBundle.putAll(localBrowserRoot.getExtras());
        }
      }
    }
    
    public void onLoadChildren(String paramString, final MediaBrowserServiceCompatApi21.ResultWrapper<List<Parcel>> paramResultWrapper)
    {
      MediaBrowserServiceCompat.Result local2 = new MediaBrowserServiceCompat.Result(paramString)
      {
        public void detach()
        {
          paramResultWrapper.detach();
        }
        
        void onResultSent(List<MediaBrowserCompat.MediaItem> paramAnonymousList, int paramAnonymousInt)
        {
          ArrayList localArrayList = null;
          if (paramAnonymousList != null)
          {
            localArrayList = new ArrayList();
            Iterator localIterator = paramAnonymousList.iterator();
            while (localIterator.hasNext())
            {
              MediaBrowserCompat.MediaItem localMediaItem = (MediaBrowserCompat.MediaItem)localIterator.next();
              Parcel localParcel = Parcel.obtain();
              localMediaItem.writeToParcel(localParcel, 0);
              localArrayList.add(localParcel);
            }
          }
          paramResultWrapper.sendResult(localArrayList);
        }
      };
      onLoadChildren(paramString, local2);
    }
    
    public void setSessionToken(MediaSessionCompat.Token paramToken)
    {
      MediaBrowserServiceCompatApi21.setSessionToken(mServiceObj, paramToken.getToken());
    }
  }
  
  class MediaBrowserServiceImplApi23
    extends MediaBrowserServiceCompat.MediaBrowserServiceImplApi21
    implements MediaBrowserServiceCompatApi23.ServiceCompatProxy
  {
    MediaBrowserServiceImplApi23()
    {
      super();
    }
    
    public void onCreate()
    {
      mServiceObj = MediaBrowserServiceCompatApi23.createService(MediaBrowserServiceCompat.this, this);
      MediaBrowserServiceCompatApi21.onCreate(mServiceObj);
    }
    
    public void onLoadItem(String paramString, final MediaBrowserServiceCompatApi21.ResultWrapper<Parcel> paramResultWrapper)
    {
      MediaBrowserServiceCompat.Result local1 = new MediaBrowserServiceCompat.Result(paramString)
      {
        public void detach()
        {
          paramResultWrapper.detach();
        }
        
        void onResultSent(MediaBrowserCompat.MediaItem paramAnonymousMediaItem, int paramAnonymousInt)
        {
          if (paramAnonymousMediaItem == null)
          {
            paramResultWrapper.sendResult(null);
            return;
          }
          Parcel localParcel = Parcel.obtain();
          paramAnonymousMediaItem.writeToParcel(localParcel, 0);
          paramResultWrapper.sendResult(localParcel);
        }
      };
      onLoadItem(paramString, local1);
    }
  }
  
  class MediaBrowserServiceImplApi24
    extends MediaBrowserServiceCompat.MediaBrowserServiceImplApi23
    implements MediaBrowserServiceCompatApi24.ServiceCompatProxy
  {
    MediaBrowserServiceImplApi24()
    {
      super();
    }
    
    public Bundle getBrowserRootHints()
    {
      return MediaBrowserServiceCompatApi24.getBrowserRootHints(mServiceObj);
    }
    
    public void notifyChildrenChanged(String paramString, Bundle paramBundle)
    {
      if (paramBundle == null)
      {
        MediaBrowserServiceCompatApi21.notifyChildrenChanged(mServiceObj, paramString);
        return;
      }
      MediaBrowserServiceCompatApi24.notifyChildrenChanged(mServiceObj, paramString, paramBundle);
    }
    
    public void onCreate()
    {
      mServiceObj = MediaBrowserServiceCompatApi24.createService(MediaBrowserServiceCompat.this, this);
      MediaBrowserServiceCompatApi21.onCreate(mServiceObj);
    }
    
    public void onLoadChildren(String paramString, final MediaBrowserServiceCompatApi24.ResultWrapper paramResultWrapper, Bundle paramBundle)
    {
      MediaBrowserServiceCompat.Result local1 = new MediaBrowserServiceCompat.Result(paramString)
      {
        public void detach()
        {
          paramResultWrapper.detach();
        }
        
        void onResultSent(List<MediaBrowserCompat.MediaItem> paramAnonymousList, int paramAnonymousInt)
        {
          ArrayList localArrayList = null;
          if (paramAnonymousList != null)
          {
            localArrayList = new ArrayList();
            Iterator localIterator = paramAnonymousList.iterator();
            while (localIterator.hasNext())
            {
              MediaBrowserCompat.MediaItem localMediaItem = (MediaBrowserCompat.MediaItem)localIterator.next();
              Parcel localParcel = Parcel.obtain();
              localMediaItem.writeToParcel(localParcel, 0);
              localArrayList.add(localParcel);
            }
          }
          paramResultWrapper.sendResult(localArrayList, paramAnonymousInt);
        }
      };
      onLoadChildren(paramString, local1, paramBundle);
    }
  }
  
  class MediaBrowserServiceImplBase
    implements MediaBrowserServiceCompat.MediaBrowserServiceImpl
  {
    private Messenger mMessenger;
    
    MediaBrowserServiceImplBase() {}
    
    public Bundle getBrowserRootHints()
    {
      if (mCurConnection == null) {
        throw new IllegalStateException("This should be called inside of onLoadChildren or onLoadItem methods");
      }
      if (mCurConnection.rootHints == null) {
        return null;
      }
      return new Bundle(mCurConnection.rootHints);
    }
    
    public void notifyChildrenChanged(@NonNull final String paramString, final Bundle paramBundle)
    {
      mHandler.post(new Runnable()
      {
        public void run()
        {
          Iterator localIterator1 = mConnections.keySet().iterator();
          while (localIterator1.hasNext())
          {
            IBinder localIBinder = (IBinder)localIterator1.next();
            MediaBrowserServiceCompat.ConnectionRecord localConnectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)mConnections.get(localIBinder);
            List localList = (List)subscriptions.get(paramString);
            if (localList != null)
            {
              Iterator localIterator2 = localList.iterator();
              while (localIterator2.hasNext())
              {
                Pair localPair = (Pair)localIterator2.next();
                if (MediaBrowserCompatUtils.hasDuplicatedItems(paramBundle, (Bundle)second)) {
                  performLoadChildren(paramString, localConnectionRecord, (Bundle)second);
                }
              }
            }
          }
        }
      });
    }
    
    public IBinder onBind(Intent paramIntent)
    {
      if ("android.media.browse.MediaBrowserService".equals(paramIntent.getAction())) {
        return mMessenger.getBinder();
      }
      return null;
    }
    
    public void onCreate()
    {
      mMessenger = new Messenger(mHandler);
    }
    
    public void setSessionToken(final MediaSessionCompat.Token paramToken)
    {
      mHandler.post(new Runnable()
      {
        public void run()
        {
          Iterator localIterator = mConnections.values().iterator();
          while (localIterator.hasNext())
          {
            MediaBrowserServiceCompat.ConnectionRecord localConnectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)localIterator.next();
            try
            {
              callbacks.onConnect(root.getRootId(), paramToken, root.getExtras());
            }
            catch (RemoteException localRemoteException)
            {
              Log.w("MBServiceCompat", "Connection for " + pkg + " is no longer valid.");
              localIterator.remove();
            }
          }
        }
      });
    }
  }
  
  public static class Result<T>
  {
    private Object mDebug;
    private boolean mDetachCalled;
    private int mFlags;
    private boolean mSendResultCalled;
    
    Result(Object paramObject)
    {
      mDebug = paramObject;
    }
    
    public void detach()
    {
      if (mDetachCalled) {
        throw new IllegalStateException("detach() called when detach() had already been called for: " + mDebug);
      }
      if (mSendResultCalled) {
        throw new IllegalStateException("detach() called when sendResult() had already been called for: " + mDebug);
      }
      mDetachCalled = true;
    }
    
    boolean isDone()
    {
      return (mDetachCalled) || (mSendResultCalled);
    }
    
    void onResultSent(T paramT, int paramInt) {}
    
    public void sendResult(T paramT)
    {
      if (mSendResultCalled) {
        throw new IllegalStateException("sendResult() called twice for: " + mDebug);
      }
      mSendResultCalled = true;
      onResultSent(paramT, mFlags);
    }
    
    void setFlags(int paramInt)
    {
      mFlags = paramInt;
    }
  }
  
  private class ServiceBinderImpl
  {
    ServiceBinderImpl() {}
    
    public void addSubscription(final String paramString, final IBinder paramIBinder, final Bundle paramBundle, final MediaBrowserServiceCompat.ServiceCallbacks paramServiceCallbacks)
    {
      mHandler.postOrRun(new Runnable()
      {
        public void run()
        {
          IBinder localIBinder = paramServiceCallbacks.asBinder();
          MediaBrowserServiceCompat.ConnectionRecord localConnectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)mConnections.get(localIBinder);
          if (localConnectionRecord == null)
          {
            Log.w("MBServiceCompat", "addSubscription for callback that isn't registered id=" + paramString);
            return;
          }
          addSubscription(paramString, localConnectionRecord, paramIBinder, paramBundle);
        }
      });
    }
    
    public void connect(final String paramString, final int paramInt, final Bundle paramBundle, final MediaBrowserServiceCompat.ServiceCallbacks paramServiceCallbacks)
    {
      if (!isValidPackage(paramString, paramInt)) {
        throw new IllegalArgumentException("Package/uid mismatch: uid=" + paramInt + " package=" + paramString);
      }
      mHandler.postOrRun(new Runnable()
      {
        public void run()
        {
          IBinder localIBinder = paramServiceCallbacks.asBinder();
          mConnections.remove(localIBinder);
          MediaBrowserServiceCompat.ConnectionRecord localConnectionRecord = new MediaBrowserServiceCompat.ConnectionRecord(MediaBrowserServiceCompat.this);
          pkg = paramString;
          rootHints = paramBundle;
          callbacks = paramServiceCallbacks;
          root = onGetRoot(paramString, paramInt, paramBundle);
          if (root == null) {
            Log.i("MBServiceCompat", "No root for client " + paramString + " from service " + getClass().getName());
          }
          for (;;)
          {
            try
            {
              paramServiceCallbacks.onConnectFailed();
              return;
            }
            catch (RemoteException localRemoteException2)
            {
              Log.w("MBServiceCompat", "Calling onConnectFailed() failed. Ignoring. pkg=" + paramString);
              return;
            }
            try
            {
              mConnections.put(localIBinder, localConnectionRecord);
              if (mSession != null)
              {
                paramServiceCallbacks.onConnect(root.getRootId(), mSession, root.getExtras());
                return;
              }
            }
            catch (RemoteException localRemoteException1)
            {
              Log.w("MBServiceCompat", "Calling onConnect() failed. Dropping client. pkg=" + paramString);
              mConnections.remove(localIBinder);
            }
          }
        }
      });
    }
    
    public void disconnect(final MediaBrowserServiceCompat.ServiceCallbacks paramServiceCallbacks)
    {
      mHandler.postOrRun(new Runnable()
      {
        public void run()
        {
          IBinder localIBinder = paramServiceCallbacks.asBinder();
          if ((MediaBrowserServiceCompat.ConnectionRecord)mConnections.remove(localIBinder) != null) {}
        }
      });
    }
    
    public void getMediaItem(final String paramString, final ResultReceiver paramResultReceiver, final MediaBrowserServiceCompat.ServiceCallbacks paramServiceCallbacks)
    {
      if ((TextUtils.isEmpty(paramString)) || (paramResultReceiver == null)) {
        return;
      }
      mHandler.postOrRun(new Runnable()
      {
        public void run()
        {
          IBinder localIBinder = paramServiceCallbacks.asBinder();
          MediaBrowserServiceCompat.ConnectionRecord localConnectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)mConnections.get(localIBinder);
          if (localConnectionRecord == null)
          {
            Log.w("MBServiceCompat", "getMediaItem for callback that isn't registered id=" + paramString);
            return;
          }
          performLoadItem(paramString, localConnectionRecord, paramResultReceiver);
        }
      });
    }
    
    public void registerCallbacks(final MediaBrowserServiceCompat.ServiceCallbacks paramServiceCallbacks, final Bundle paramBundle)
    {
      mHandler.postOrRun(new Runnable()
      {
        public void run()
        {
          IBinder localIBinder = paramServiceCallbacks.asBinder();
          mConnections.remove(localIBinder);
          MediaBrowserServiceCompat.ConnectionRecord localConnectionRecord = new MediaBrowserServiceCompat.ConnectionRecord(MediaBrowserServiceCompat.this);
          callbacks = paramServiceCallbacks;
          rootHints = paramBundle;
          mConnections.put(localIBinder, localConnectionRecord);
        }
      });
    }
    
    public void removeSubscription(final String paramString, final IBinder paramIBinder, final MediaBrowserServiceCompat.ServiceCallbacks paramServiceCallbacks)
    {
      mHandler.postOrRun(new Runnable()
      {
        public void run()
        {
          IBinder localIBinder = paramServiceCallbacks.asBinder();
          MediaBrowserServiceCompat.ConnectionRecord localConnectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)mConnections.get(localIBinder);
          if (localConnectionRecord == null) {
            Log.w("MBServiceCompat", "removeSubscription for callback that isn't registered id=" + paramString);
          }
          while (removeSubscription(paramString, localConnectionRecord, paramIBinder)) {
            return;
          }
          Log.w("MBServiceCompat", "removeSubscription called for " + paramString + " which is not subscribed");
        }
      });
    }
    
    public void unregisterCallbacks(final MediaBrowserServiceCompat.ServiceCallbacks paramServiceCallbacks)
    {
      mHandler.postOrRun(new Runnable()
      {
        public void run()
        {
          IBinder localIBinder = paramServiceCallbacks.asBinder();
          mConnections.remove(localIBinder);
        }
      });
    }
  }
  
  private static abstract interface ServiceCallbacks
  {
    public abstract IBinder asBinder();
    
    public abstract void onConnect(String paramString, MediaSessionCompat.Token paramToken, Bundle paramBundle)
      throws RemoteException;
    
    public abstract void onConnectFailed()
      throws RemoteException;
    
    public abstract void onLoadChildren(String paramString, List<MediaBrowserCompat.MediaItem> paramList, Bundle paramBundle)
      throws RemoteException;
  }
  
  private class ServiceCallbacksCompat
    implements MediaBrowserServiceCompat.ServiceCallbacks
  {
    final Messenger mCallbacks;
    
    ServiceCallbacksCompat(Messenger paramMessenger)
    {
      mCallbacks = paramMessenger;
    }
    
    private void sendRequest(int paramInt, Bundle paramBundle)
      throws RemoteException
    {
      Message localMessage = Message.obtain();
      what = paramInt;
      arg1 = 1;
      localMessage.setData(paramBundle);
      mCallbacks.send(localMessage);
    }
    
    public IBinder asBinder()
    {
      return mCallbacks.getBinder();
    }
    
    public void onConnect(String paramString, MediaSessionCompat.Token paramToken, Bundle paramBundle)
      throws RemoteException
    {
      if (paramBundle == null) {
        paramBundle = new Bundle();
      }
      paramBundle.putInt("extra_service_version", 1);
      Bundle localBundle = new Bundle();
      localBundle.putString("data_media_item_id", paramString);
      localBundle.putParcelable("data_media_session_token", paramToken);
      localBundle.putBundle("data_root_hints", paramBundle);
      sendRequest(1, localBundle);
    }
    
    public void onConnectFailed()
      throws RemoteException
    {
      sendRequest(2, null);
    }
    
    public void onLoadChildren(String paramString, List<MediaBrowserCompat.MediaItem> paramList, Bundle paramBundle)
      throws RemoteException
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("data_media_item_id", paramString);
      localBundle.putBundle("data_options", paramBundle);
      if (paramList != null) {
        if (!(paramList instanceof ArrayList)) {
          break label59;
        }
      }
      label59:
      for (ArrayList localArrayList = (ArrayList)paramList;; localArrayList = new ArrayList(paramList))
      {
        localBundle.putParcelableArrayList("data_media_item_list", localArrayList);
        sendRequest(3, localBundle);
        return;
      }
    }
  }
  
  private final class ServiceHandler
    extends Handler
  {
    private final MediaBrowserServiceCompat.ServiceBinderImpl mServiceBinderImpl = new MediaBrowserServiceCompat.ServiceBinderImpl(MediaBrowserServiceCompat.this);
    
    ServiceHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      Bundle localBundle = paramMessage.getData();
      switch (what)
      {
      default: 
        Log.w("MBServiceCompat", "Unhandled message: " + paramMessage + "\n  Service version: " + 1 + "\n  Client version: " + arg1);
        return;
      case 1: 
        mServiceBinderImpl.connect(localBundle.getString("data_package_name"), localBundle.getInt("data_calling_uid"), localBundle.getBundle("data_root_hints"), new MediaBrowserServiceCompat.ServiceCallbacksCompat(MediaBrowserServiceCompat.this, replyTo));
        return;
      case 2: 
        mServiceBinderImpl.disconnect(new MediaBrowserServiceCompat.ServiceCallbacksCompat(MediaBrowserServiceCompat.this, replyTo));
        return;
      case 3: 
        mServiceBinderImpl.addSubscription(localBundle.getString("data_media_item_id"), BundleCompat.getBinder(localBundle, "data_callback_token"), localBundle.getBundle("data_options"), new MediaBrowserServiceCompat.ServiceCallbacksCompat(MediaBrowserServiceCompat.this, replyTo));
        return;
      case 4: 
        mServiceBinderImpl.removeSubscription(localBundle.getString("data_media_item_id"), BundleCompat.getBinder(localBundle, "data_callback_token"), new MediaBrowserServiceCompat.ServiceCallbacksCompat(MediaBrowserServiceCompat.this, replyTo));
        return;
      case 5: 
        mServiceBinderImpl.getMediaItem(localBundle.getString("data_media_item_id"), (ResultReceiver)localBundle.getParcelable("data_result_receiver"), new MediaBrowserServiceCompat.ServiceCallbacksCompat(MediaBrowserServiceCompat.this, replyTo));
        return;
      case 6: 
        mServiceBinderImpl.registerCallbacks(new MediaBrowserServiceCompat.ServiceCallbacksCompat(MediaBrowserServiceCompat.this, replyTo), localBundle.getBundle("data_root_hints"));
        return;
      }
      mServiceBinderImpl.unregisterCallbacks(new MediaBrowserServiceCompat.ServiceCallbacksCompat(MediaBrowserServiceCompat.this, replyTo));
    }
    
    public void postOrRun(Runnable paramRunnable)
    {
      if (Thread.currentThread() == getLooper().getThread())
      {
        paramRunnable.run();
        return;
      }
      post(paramRunnable);
    }
    
    public boolean sendMessageAtTime(Message paramMessage, long paramLong)
    {
      Bundle localBundle = paramMessage.getData();
      localBundle.setClassLoader(MediaBrowserCompat.class.getClassLoader());
      localBundle.putInt("data_calling_uid", Binder.getCallingUid());
      return super.sendMessageAtTime(paramMessage, paramLong);
    }
  }
}
