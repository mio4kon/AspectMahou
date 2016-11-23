package android.support.v4.media.session;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.MediaMetadataCompat.Builder;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.VolumeProviderCompat.Callback;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MediaSessionCompat
{
  static final String ACTION_ARGUMENT_EXTRAS = "android.support.v4.media.session.action.ARGUMENT_EXTRAS";
  static final String ACTION_ARGUMENT_MEDIA_ID = "android.support.v4.media.session.action.ARGUMENT_MEDIA_ID";
  static final String ACTION_ARGUMENT_QUERY = "android.support.v4.media.session.action.ARGUMENT_QUERY";
  static final String ACTION_ARGUMENT_URI = "android.support.v4.media.session.action.ARGUMENT_URI";
  static final String ACTION_PLAY_FROM_URI = "android.support.v4.media.session.action.PLAY_FROM_URI";
  static final String ACTION_PREPARE = "android.support.v4.media.session.action.PREPARE";
  static final String ACTION_PREPARE_FROM_MEDIA_ID = "android.support.v4.media.session.action.PREPARE_FROM_MEDIA_ID";
  static final String ACTION_PREPARE_FROM_SEARCH = "android.support.v4.media.session.action.PREPARE_FROM_SEARCH";
  static final String ACTION_PREPARE_FROM_URI = "android.support.v4.media.session.action.PREPARE_FROM_URI";
  public static final int FLAG_HANDLES_MEDIA_BUTTONS = 1;
  public static final int FLAG_HANDLES_TRANSPORT_CONTROLS = 2;
  private static final int MAX_BITMAP_SIZE_IN_DP = 320;
  static final String TAG = "MediaSessionCompat";
  static int sMaxBitmapSize;
  private final ArrayList<OnActiveChangeListener> mActiveListeners = new ArrayList();
  private final MediaControllerCompat mController;
  private final MediaSessionImpl mImpl;
  
  private MediaSessionCompat(Context paramContext, MediaSessionImpl paramMediaSessionImpl)
  {
    mImpl = paramMediaSessionImpl;
    mController = new MediaControllerCompat(paramContext, this);
  }
  
  public MediaSessionCompat(Context paramContext, String paramString)
  {
    this(paramContext, paramString, null, null);
  }
  
  public MediaSessionCompat(Context paramContext, String paramString, ComponentName paramComponentName, PendingIntent paramPendingIntent)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("context must not be null");
    }
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("tag must not be null or empty");
    }
    if (Build.VERSION.SDK_INT >= 21) {}
    for (mImpl = new MediaSessionImplApi21(paramContext, paramString);; mImpl = new MediaSessionImplBase(paramContext, paramString, paramComponentName, paramPendingIntent))
    {
      mController = new MediaControllerCompat(paramContext, this);
      if (sMaxBitmapSize == 0) {
        sMaxBitmapSize = (int)TypedValue.applyDimension(1, 320.0F, paramContext.getResources().getDisplayMetrics());
      }
      return;
    }
  }
  
  public static MediaSessionCompat fromMediaSession(Context paramContext, Object paramObject)
  {
    if ((paramContext == null) || (paramObject == null) || (Build.VERSION.SDK_INT < 21)) {
      return null;
    }
    return new MediaSessionCompat(paramContext, new MediaSessionImplApi21(paramObject));
  }
  
  @Deprecated
  public static MediaSessionCompat obtain(Context paramContext, Object paramObject)
  {
    return fromMediaSession(paramContext, paramObject);
  }
  
  public void addOnActiveChangeListener(OnActiveChangeListener paramOnActiveChangeListener)
  {
    if (paramOnActiveChangeListener == null) {
      throw new IllegalArgumentException("Listener may not be null");
    }
    mActiveListeners.add(paramOnActiveChangeListener);
  }
  
  public String getCallingPackage()
  {
    return mImpl.getCallingPackage();
  }
  
  public MediaControllerCompat getController()
  {
    return mController;
  }
  
  public Object getMediaSession()
  {
    return mImpl.getMediaSession();
  }
  
  public Object getRemoteControlClient()
  {
    return mImpl.getRemoteControlClient();
  }
  
  public Token getSessionToken()
  {
    return mImpl.getSessionToken();
  }
  
  public boolean isActive()
  {
    return mImpl.isActive();
  }
  
  public void release()
  {
    mImpl.release();
  }
  
  public void removeOnActiveChangeListener(OnActiveChangeListener paramOnActiveChangeListener)
  {
    if (paramOnActiveChangeListener == null) {
      throw new IllegalArgumentException("Listener may not be null");
    }
    mActiveListeners.remove(paramOnActiveChangeListener);
  }
  
  public void sendSessionEvent(String paramString, Bundle paramBundle)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("event cannot be null or empty");
    }
    mImpl.sendSessionEvent(paramString, paramBundle);
  }
  
  public void setActive(boolean paramBoolean)
  {
    mImpl.setActive(paramBoolean);
    Iterator localIterator = mActiveListeners.iterator();
    while (localIterator.hasNext()) {
      ((OnActiveChangeListener)localIterator.next()).onActiveChanged();
    }
  }
  
  public void setCallback(Callback paramCallback)
  {
    setCallback(paramCallback, null);
  }
  
  public void setCallback(Callback paramCallback, Handler paramHandler)
  {
    MediaSessionImpl localMediaSessionImpl = mImpl;
    if (paramHandler != null) {}
    for (;;)
    {
      localMediaSessionImpl.setCallback(paramCallback, paramHandler);
      return;
      paramHandler = new Handler();
    }
  }
  
  public void setExtras(Bundle paramBundle)
  {
    mImpl.setExtras(paramBundle);
  }
  
  public void setFlags(int paramInt)
  {
    mImpl.setFlags(paramInt);
  }
  
  public void setMediaButtonReceiver(PendingIntent paramPendingIntent)
  {
    mImpl.setMediaButtonReceiver(paramPendingIntent);
  }
  
  public void setMetadata(MediaMetadataCompat paramMediaMetadataCompat)
  {
    mImpl.setMetadata(paramMediaMetadataCompat);
  }
  
  public void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat)
  {
    mImpl.setPlaybackState(paramPlaybackStateCompat);
  }
  
  public void setPlaybackToLocal(int paramInt)
  {
    mImpl.setPlaybackToLocal(paramInt);
  }
  
  public void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat)
  {
    if (paramVolumeProviderCompat == null) {
      throw new IllegalArgumentException("volumeProvider may not be null!");
    }
    mImpl.setPlaybackToRemote(paramVolumeProviderCompat);
  }
  
  public void setQueue(List<QueueItem> paramList)
  {
    mImpl.setQueue(paramList);
  }
  
  public void setQueueTitle(CharSequence paramCharSequence)
  {
    mImpl.setQueueTitle(paramCharSequence);
  }
  
  public void setRatingType(int paramInt)
  {
    mImpl.setRatingType(paramInt);
  }
  
  public void setSessionActivity(PendingIntent paramPendingIntent)
  {
    mImpl.setSessionActivity(paramPendingIntent);
  }
  
  public static abstract class Callback
  {
    final Object mCallbackObj;
    
    public Callback()
    {
      if (Build.VERSION.SDK_INT >= 24)
      {
        mCallbackObj = MediaSessionCompatApi24.createCallback(new StubApi24());
        return;
      }
      if (Build.VERSION.SDK_INT >= 23)
      {
        mCallbackObj = MediaSessionCompatApi23.createCallback(new StubApi23());
        return;
      }
      if (Build.VERSION.SDK_INT >= 21)
      {
        mCallbackObj = MediaSessionCompatApi21.createCallback(new StubApi21());
        return;
      }
      mCallbackObj = null;
    }
    
    public void onCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver) {}
    
    public void onCustomAction(String paramString, Bundle paramBundle) {}
    
    public void onFastForward() {}
    
    public boolean onMediaButtonEvent(Intent paramIntent)
    {
      return false;
    }
    
    public void onPause() {}
    
    public void onPlay() {}
    
    public void onPlayFromMediaId(String paramString, Bundle paramBundle) {}
    
    public void onPlayFromSearch(String paramString, Bundle paramBundle) {}
    
    public void onPlayFromUri(Uri paramUri, Bundle paramBundle) {}
    
    public void onPrepare() {}
    
    public void onPrepareFromMediaId(String paramString, Bundle paramBundle) {}
    
    public void onPrepareFromSearch(String paramString, Bundle paramBundle) {}
    
    public void onPrepareFromUri(Uri paramUri, Bundle paramBundle) {}
    
    public void onRewind() {}
    
    public void onSeekTo(long paramLong) {}
    
    public void onSetRating(RatingCompat paramRatingCompat) {}
    
    public void onSkipToNext() {}
    
    public void onSkipToPrevious() {}
    
    public void onSkipToQueueItem(long paramLong) {}
    
    public void onStop() {}
    
    private class StubApi21
      implements MediaSessionCompatApi21.Callback
    {
      StubApi21() {}
      
      public void onCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
      {
        MediaSessionCompat.Callback.this.onCommand(paramString, paramBundle, paramResultReceiver);
      }
      
      public void onCustomAction(String paramString, Bundle paramBundle)
      {
        if (paramString.equals("android.support.v4.media.session.action.PLAY_FROM_URI"))
        {
          Uri localUri2 = (Uri)paramBundle.getParcelable("android.support.v4.media.session.action.ARGUMENT_URI");
          Bundle localBundle4 = (Bundle)paramBundle.getParcelable("android.support.v4.media.session.action.ARGUMENT_EXTRAS");
          onPlayFromUri(localUri2, localBundle4);
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.PREPARE"))
        {
          onPrepare();
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.PREPARE_FROM_MEDIA_ID"))
        {
          String str2 = paramBundle.getString("android.support.v4.media.session.action.ARGUMENT_MEDIA_ID");
          Bundle localBundle3 = paramBundle.getBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS");
          onPrepareFromMediaId(str2, localBundle3);
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.PREPARE_FROM_SEARCH"))
        {
          String str1 = paramBundle.getString("android.support.v4.media.session.action.ARGUMENT_QUERY");
          Bundle localBundle2 = paramBundle.getBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS");
          onPrepareFromSearch(str1, localBundle2);
          return;
        }
        if (paramString.equals("android.support.v4.media.session.action.PREPARE_FROM_URI"))
        {
          Uri localUri1 = (Uri)paramBundle.getParcelable("android.support.v4.media.session.action.ARGUMENT_URI");
          Bundle localBundle1 = paramBundle.getBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS");
          onPrepareFromUri(localUri1, localBundle1);
          return;
        }
        MediaSessionCompat.Callback.this.onCustomAction(paramString, paramBundle);
      }
      
      public void onFastForward()
      {
        MediaSessionCompat.Callback.this.onFastForward();
      }
      
      public boolean onMediaButtonEvent(Intent paramIntent)
      {
        return MediaSessionCompat.Callback.this.onMediaButtonEvent(paramIntent);
      }
      
      public void onPause()
      {
        MediaSessionCompat.Callback.this.onPause();
      }
      
      public void onPlay()
      {
        MediaSessionCompat.Callback.this.onPlay();
      }
      
      public void onPlayFromMediaId(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPlayFromMediaId(paramString, paramBundle);
      }
      
      public void onPlayFromSearch(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPlayFromSearch(paramString, paramBundle);
      }
      
      public void onRewind()
      {
        MediaSessionCompat.Callback.this.onRewind();
      }
      
      public void onSeekTo(long paramLong)
      {
        MediaSessionCompat.Callback.this.onSeekTo(paramLong);
      }
      
      public void onSetRating(Object paramObject)
      {
        onSetRating(RatingCompat.fromRating(paramObject));
      }
      
      public void onSkipToNext()
      {
        MediaSessionCompat.Callback.this.onSkipToNext();
      }
      
      public void onSkipToPrevious()
      {
        MediaSessionCompat.Callback.this.onSkipToPrevious();
      }
      
      public void onSkipToQueueItem(long paramLong)
      {
        MediaSessionCompat.Callback.this.onSkipToQueueItem(paramLong);
      }
      
      public void onStop()
      {
        MediaSessionCompat.Callback.this.onStop();
      }
    }
    
    private class StubApi23
      extends MediaSessionCompat.Callback.StubApi21
      implements MediaSessionCompatApi23.Callback
    {
      StubApi23()
      {
        super();
      }
      
      public void onPlayFromUri(Uri paramUri, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPlayFromUri(paramUri, paramBundle);
      }
    }
    
    private class StubApi24
      extends MediaSessionCompat.Callback.StubApi23
      implements MediaSessionCompatApi24.Callback
    {
      StubApi24()
      {
        super();
      }
      
      public void onPrepare()
      {
        MediaSessionCompat.Callback.this.onPrepare();
      }
      
      public void onPrepareFromMediaId(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPrepareFromMediaId(paramString, paramBundle);
      }
      
      public void onPrepareFromSearch(String paramString, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPrepareFromSearch(paramString, paramBundle);
      }
      
      public void onPrepareFromUri(Uri paramUri, Bundle paramBundle)
      {
        MediaSessionCompat.Callback.this.onPrepareFromUri(paramUri, paramBundle);
      }
    }
  }
  
  static abstract interface MediaSessionImpl
  {
    public abstract String getCallingPackage();
    
    public abstract Object getMediaSession();
    
    public abstract Object getRemoteControlClient();
    
    public abstract MediaSessionCompat.Token getSessionToken();
    
    public abstract boolean isActive();
    
    public abstract void release();
    
    public abstract void sendSessionEvent(String paramString, Bundle paramBundle);
    
    public abstract void setActive(boolean paramBoolean);
    
    public abstract void setCallback(MediaSessionCompat.Callback paramCallback, Handler paramHandler);
    
    public abstract void setExtras(Bundle paramBundle);
    
    public abstract void setFlags(int paramInt);
    
    public abstract void setMediaButtonReceiver(PendingIntent paramPendingIntent);
    
    public abstract void setMetadata(MediaMetadataCompat paramMediaMetadataCompat);
    
    public abstract void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat);
    
    public abstract void setPlaybackToLocal(int paramInt);
    
    public abstract void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat);
    
    public abstract void setQueue(List<MediaSessionCompat.QueueItem> paramList);
    
    public abstract void setQueueTitle(CharSequence paramCharSequence);
    
    public abstract void setRatingType(int paramInt);
    
    public abstract void setSessionActivity(PendingIntent paramPendingIntent);
  }
  
  static class MediaSessionImplApi21
    implements MediaSessionCompat.MediaSessionImpl
  {
    private PendingIntent mMediaButtonIntent;
    private final Object mSessionObj;
    private final MediaSessionCompat.Token mToken;
    
    public MediaSessionImplApi21(Context paramContext, String paramString)
    {
      mSessionObj = MediaSessionCompatApi21.createSession(paramContext, paramString);
      mToken = new MediaSessionCompat.Token(MediaSessionCompatApi21.getSessionToken(mSessionObj));
    }
    
    public MediaSessionImplApi21(Object paramObject)
    {
      mSessionObj = MediaSessionCompatApi21.verifySession(paramObject);
      mToken = new MediaSessionCompat.Token(MediaSessionCompatApi21.getSessionToken(mSessionObj));
    }
    
    public String getCallingPackage()
    {
      if (Build.VERSION.SDK_INT < 24) {
        return null;
      }
      return MediaSessionCompatApi24.getCallingPackage(mSessionObj);
    }
    
    public Object getMediaSession()
    {
      return mSessionObj;
    }
    
    public Object getRemoteControlClient()
    {
      return null;
    }
    
    public MediaSessionCompat.Token getSessionToken()
    {
      return mToken;
    }
    
    public boolean isActive()
    {
      return MediaSessionCompatApi21.isActive(mSessionObj);
    }
    
    public void release()
    {
      MediaSessionCompatApi21.release(mSessionObj);
    }
    
    public void sendSessionEvent(String paramString, Bundle paramBundle)
    {
      MediaSessionCompatApi21.sendSessionEvent(mSessionObj, paramString, paramBundle);
    }
    
    public void setActive(boolean paramBoolean)
    {
      MediaSessionCompatApi21.setActive(mSessionObj, paramBoolean);
    }
    
    public void setCallback(MediaSessionCompat.Callback paramCallback, Handler paramHandler)
    {
      Object localObject1 = mSessionObj;
      if (paramCallback == null) {}
      for (Object localObject2 = null;; localObject2 = mCallbackObj)
      {
        MediaSessionCompatApi21.setCallback(localObject1, localObject2, paramHandler);
        return;
      }
    }
    
    public void setExtras(Bundle paramBundle)
    {
      MediaSessionCompatApi21.setExtras(mSessionObj, paramBundle);
    }
    
    public void setFlags(int paramInt)
    {
      MediaSessionCompatApi21.setFlags(mSessionObj, paramInt);
    }
    
    public void setMediaButtonReceiver(PendingIntent paramPendingIntent)
    {
      mMediaButtonIntent = paramPendingIntent;
      MediaSessionCompatApi21.setMediaButtonReceiver(mSessionObj, paramPendingIntent);
    }
    
    public void setMetadata(MediaMetadataCompat paramMediaMetadataCompat)
    {
      Object localObject1 = mSessionObj;
      if (paramMediaMetadataCompat == null) {}
      for (Object localObject2 = null;; localObject2 = paramMediaMetadataCompat.getMediaMetadata())
      {
        MediaSessionCompatApi21.setMetadata(localObject1, localObject2);
        return;
      }
    }
    
    public void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat)
    {
      Object localObject1 = mSessionObj;
      if (paramPlaybackStateCompat == null) {}
      for (Object localObject2 = null;; localObject2 = paramPlaybackStateCompat.getPlaybackState())
      {
        MediaSessionCompatApi21.setPlaybackState(localObject1, localObject2);
        return;
      }
    }
    
    public void setPlaybackToLocal(int paramInt)
    {
      MediaSessionCompatApi21.setPlaybackToLocal(mSessionObj, paramInt);
    }
    
    public void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat)
    {
      MediaSessionCompatApi21.setPlaybackToRemote(mSessionObj, paramVolumeProviderCompat.getVolumeProvider());
    }
    
    public void setQueue(List<MediaSessionCompat.QueueItem> paramList)
    {
      ArrayList localArrayList = null;
      if (paramList != null)
      {
        localArrayList = new ArrayList();
        Iterator localIterator = paramList.iterator();
        while (localIterator.hasNext()) {
          localArrayList.add(((MediaSessionCompat.QueueItem)localIterator.next()).getQueueItem());
        }
      }
      MediaSessionCompatApi21.setQueue(mSessionObj, localArrayList);
    }
    
    public void setQueueTitle(CharSequence paramCharSequence)
    {
      MediaSessionCompatApi21.setQueueTitle(mSessionObj, paramCharSequence);
    }
    
    public void setRatingType(int paramInt)
    {
      if (Build.VERSION.SDK_INT < 22) {
        return;
      }
      MediaSessionCompatApi22.setRatingType(mSessionObj, paramInt);
    }
    
    public void setSessionActivity(PendingIntent paramPendingIntent)
    {
      MediaSessionCompatApi21.setSessionActivity(mSessionObj, paramPendingIntent);
    }
  }
  
  static class MediaSessionImplBase
    implements MediaSessionCompat.MediaSessionImpl
  {
    final AudioManager mAudioManager;
    volatile MediaSessionCompat.Callback mCallback;
    private final Context mContext;
    final RemoteCallbackList<IMediaControllerCallback> mControllerCallbacks = new RemoteCallbackList();
    boolean mDestroyed = false;
    Bundle mExtras;
    int mFlags;
    private MessageHandler mHandler;
    private boolean mIsActive = false;
    private boolean mIsMbrRegistered = false;
    private boolean mIsRccRegistered = false;
    int mLocalStream;
    final Object mLock = new Object();
    private final ComponentName mMediaButtonReceiverComponentName;
    private final PendingIntent mMediaButtonReceiverIntent;
    MediaMetadataCompat mMetadata;
    final String mPackageName;
    List<MediaSessionCompat.QueueItem> mQueue;
    CharSequence mQueueTitle;
    int mRatingType;
    private final Object mRccObj;
    PendingIntent mSessionActivity;
    PlaybackStateCompat mState;
    private final MediaSessionStub mStub;
    final String mTag;
    private final MediaSessionCompat.Token mToken;
    private VolumeProviderCompat.Callback mVolumeCallback = new VolumeProviderCompat.Callback()
    {
      public void onVolumeChanged(VolumeProviderCompat paramAnonymousVolumeProviderCompat)
      {
        if (mVolumeProvider != paramAnonymousVolumeProviderCompat) {
          return;
        }
        ParcelableVolumeInfo localParcelableVolumeInfo = new ParcelableVolumeInfo(mVolumeType, mLocalStream, paramAnonymousVolumeProviderCompat.getVolumeControl(), paramAnonymousVolumeProviderCompat.getMaxVolume(), paramAnonymousVolumeProviderCompat.getCurrentVolume());
        sendVolumeInfoChanged(localParcelableVolumeInfo);
      }
    };
    VolumeProviderCompat mVolumeProvider;
    int mVolumeType;
    
    public MediaSessionImplBase(Context paramContext, String paramString, ComponentName paramComponentName, PendingIntent paramPendingIntent)
    {
      if (paramComponentName == null)
      {
        paramComponentName = MediaButtonReceiver.getMediaButtonReceiverComponent(paramContext);
        if (paramComponentName == null) {
          Log.w("MediaSessionCompat", "Couldn't find a unique registered media button receiver in the given context.");
        }
      }
      if ((paramComponentName != null) && (paramPendingIntent == null))
      {
        Intent localIntent = new Intent("android.intent.action.MEDIA_BUTTON");
        localIntent.setComponent(paramComponentName);
        paramPendingIntent = PendingIntent.getBroadcast(paramContext, 0, localIntent, 0);
      }
      if (paramComponentName == null) {
        throw new IllegalArgumentException("MediaButtonReceiver component may not be null.");
      }
      mContext = paramContext;
      mPackageName = paramContext.getPackageName();
      mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
      mTag = paramString;
      mMediaButtonReceiverComponentName = paramComponentName;
      mMediaButtonReceiverIntent = paramPendingIntent;
      mStub = new MediaSessionStub();
      mToken = new MediaSessionCompat.Token(mStub);
      mRatingType = 0;
      mVolumeType = 1;
      mLocalStream = 3;
      if (Build.VERSION.SDK_INT >= 14)
      {
        mRccObj = MediaSessionCompatApi14.createRemoteControlClient(paramPendingIntent);
        return;
      }
      mRccObj = null;
    }
    
    private void sendEvent(String paramString, Bundle paramBundle)
    {
      int i = -1 + mControllerCallbacks.beginBroadcast();
      for (;;)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0) {
          localIMediaControllerCallback = (IMediaControllerCallback)mControllerCallbacks.getBroadcastItem(i);
        }
        try
        {
          localIMediaControllerCallback.onEvent(paramString, paramBundle);
          i--;
          continue;
          mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private void sendExtras(Bundle paramBundle)
    {
      int i = -1 + mControllerCallbacks.beginBroadcast();
      for (;;)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0) {
          localIMediaControllerCallback = (IMediaControllerCallback)mControllerCallbacks.getBroadcastItem(i);
        }
        try
        {
          localIMediaControllerCallback.onExtrasChanged(paramBundle);
          i--;
          continue;
          mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private void sendMetadata(MediaMetadataCompat paramMediaMetadataCompat)
    {
      int i = -1 + mControllerCallbacks.beginBroadcast();
      for (;;)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0) {
          localIMediaControllerCallback = (IMediaControllerCallback)mControllerCallbacks.getBroadcastItem(i);
        }
        try
        {
          localIMediaControllerCallback.onMetadataChanged(paramMediaMetadataCompat);
          i--;
          continue;
          mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private void sendQueue(List<MediaSessionCompat.QueueItem> paramList)
    {
      int i = -1 + mControllerCallbacks.beginBroadcast();
      for (;;)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0) {
          localIMediaControllerCallback = (IMediaControllerCallback)mControllerCallbacks.getBroadcastItem(i);
        }
        try
        {
          localIMediaControllerCallback.onQueueChanged(paramList);
          i--;
          continue;
          mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private void sendQueueTitle(CharSequence paramCharSequence)
    {
      int i = -1 + mControllerCallbacks.beginBroadcast();
      for (;;)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0) {
          localIMediaControllerCallback = (IMediaControllerCallback)mControllerCallbacks.getBroadcastItem(i);
        }
        try
        {
          localIMediaControllerCallback.onQueueTitleChanged(paramCharSequence);
          i--;
          continue;
          mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private void sendSessionDestroyed()
    {
      int i = -1 + mControllerCallbacks.beginBroadcast();
      for (;;)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0) {
          localIMediaControllerCallback = (IMediaControllerCallback)mControllerCallbacks.getBroadcastItem(i);
        }
        try
        {
          localIMediaControllerCallback.onSessionDestroyed();
          i--;
          continue;
          mControllerCallbacks.finishBroadcast();
          mControllerCallbacks.kill();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private void sendState(PlaybackStateCompat paramPlaybackStateCompat)
    {
      int i = -1 + mControllerCallbacks.beginBroadcast();
      for (;;)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0) {
          localIMediaControllerCallback = (IMediaControllerCallback)mControllerCallbacks.getBroadcastItem(i);
        }
        try
        {
          localIMediaControllerCallback.onPlaybackStateChanged(paramPlaybackStateCompat);
          i--;
          continue;
          mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    private boolean update()
    {
      label51:
      boolean bool2;
      if (mIsActive)
      {
        if ((!mIsMbrRegistered) && ((0x1 & mFlags) != 0)) {
          if (Build.VERSION.SDK_INT >= 18)
          {
            MediaSessionCompatApi18.registerMediaButtonEventReceiver(mContext, mMediaButtonReceiverIntent, mMediaButtonReceiverComponentName);
            mIsMbrRegistered = true;
            int i = Build.VERSION.SDK_INT;
            bool2 = false;
            if (i >= 14)
            {
              if ((mIsRccRegistered) || ((0x2 & mFlags) == 0)) {
                break label190;
              }
              MediaSessionCompatApi14.registerRemoteControlClient(mContext, mRccObj);
              mIsRccRegistered = true;
              bool2 = true;
            }
          }
        }
        label190:
        int j;
        do
        {
          boolean bool3;
          do
          {
            return bool2;
            ((AudioManager)mContext.getSystemService("audio")).registerMediaButtonEventReceiver(mMediaButtonReceiverComponentName);
            break;
            if ((!mIsMbrRegistered) || ((0x1 & mFlags) != 0)) {
              break label51;
            }
            if (Build.VERSION.SDK_INT >= 18) {
              MediaSessionCompatApi18.unregisterMediaButtonEventReceiver(mContext, mMediaButtonReceiverIntent, mMediaButtonReceiverComponentName);
            }
            for (;;)
            {
              mIsMbrRegistered = false;
              break;
              ((AudioManager)mContext.getSystemService("audio")).unregisterMediaButtonEventReceiver(mMediaButtonReceiverComponentName);
            }
            bool3 = mIsRccRegistered;
            bool2 = false;
          } while (!bool3);
          j = 0x2 & mFlags;
          bool2 = false;
        } while (j != 0);
        MediaSessionCompatApi14.setState(mRccObj, 0);
        MediaSessionCompatApi14.unregisterRemoteControlClient(mContext, mRccObj);
        mIsRccRegistered = false;
        return false;
      }
      if (mIsMbrRegistered)
      {
        if (Build.VERSION.SDK_INT < 18) {
          break label316;
        }
        MediaSessionCompatApi18.unregisterMediaButtonEventReceiver(mContext, mMediaButtonReceiverIntent, mMediaButtonReceiverComponentName);
      }
      for (;;)
      {
        mIsMbrRegistered = false;
        boolean bool1 = mIsRccRegistered;
        bool2 = false;
        if (!bool1) {
          break;
        }
        MediaSessionCompatApi14.setState(mRccObj, 0);
        MediaSessionCompatApi14.unregisterRemoteControlClient(mContext, mRccObj);
        mIsRccRegistered = false;
        return false;
        label316:
        ((AudioManager)mContext.getSystemService("audio")).unregisterMediaButtonEventReceiver(mMediaButtonReceiverComponentName);
      }
    }
    
    void adjustVolume(int paramInt1, int paramInt2)
    {
      if (mVolumeType == 2)
      {
        if (mVolumeProvider != null) {
          mVolumeProvider.onAdjustVolume(paramInt1);
        }
        return;
      }
      mAudioManager.adjustStreamVolume(mLocalStream, paramInt1, paramInt2);
    }
    
    public String getCallingPackage()
    {
      return null;
    }
    
    public Object getMediaSession()
    {
      return null;
    }
    
    public Object getRemoteControlClient()
    {
      return mRccObj;
    }
    
    public MediaSessionCompat.Token getSessionToken()
    {
      return mToken;
    }
    
    PlaybackStateCompat getStateWithUpdatedPosition()
    {
      long l1 = -1L;
      PlaybackStateCompat localPlaybackStateCompat2;
      for (;;)
      {
        long l4;
        synchronized (mLock)
        {
          PlaybackStateCompat localPlaybackStateCompat1 = mState;
          if ((mMetadata != null) && (mMetadata.containsKey("android.media.metadata.DURATION"))) {
            l1 = mMetadata.getLong("android.media.metadata.DURATION");
          }
          localPlaybackStateCompat2 = null;
          if (localPlaybackStateCompat1 != null) {
            if ((localPlaybackStateCompat1.getState() != 3) && (localPlaybackStateCompat1.getState() != 4))
            {
              int i = localPlaybackStateCompat1.getState();
              localPlaybackStateCompat2 = null;
              if (i != 5) {}
            }
            else
            {
              long l2 = localPlaybackStateCompat1.getLastPositionUpdateTime();
              long l3 = SystemClock.elapsedRealtime();
              boolean bool = l2 < 0L;
              localPlaybackStateCompat2 = null;
              if (bool)
              {
                l4 = (localPlaybackStateCompat1.getPlaybackSpeed() * (float)(l3 - l2)) + localPlaybackStateCompat1.getPosition();
                if ((l1 < 0L) || (l4 <= l1)) {
                  break label208;
                }
                l4 = l1;
                PlaybackStateCompat.Builder localBuilder = new PlaybackStateCompat.Builder(localPlaybackStateCompat1);
                localBuilder.setState(localPlaybackStateCompat1.getState(), l4, localPlaybackStateCompat1.getPlaybackSpeed(), l3);
                localPlaybackStateCompat2 = localBuilder.build();
              }
            }
          }
          if (localPlaybackStateCompat2 != null) {
            break;
          }
          return localPlaybackStateCompat1;
        }
        label208:
        if (l4 < 0L) {
          l4 = 0L;
        }
      }
      return localPlaybackStateCompat2;
    }
    
    public boolean isActive()
    {
      return mIsActive;
    }
    
    void postToHandler(int paramInt)
    {
      postToHandler(paramInt, null);
    }
    
    void postToHandler(int paramInt, Object paramObject)
    {
      postToHandler(paramInt, paramObject, null);
    }
    
    void postToHandler(int paramInt, Object paramObject, Bundle paramBundle)
    {
      synchronized (mLock)
      {
        if (mHandler != null) {
          mHandler.post(paramInt, paramObject, paramBundle);
        }
        return;
      }
    }
    
    public void release()
    {
      mIsActive = false;
      mDestroyed = true;
      update();
      sendSessionDestroyed();
    }
    
    public void sendSessionEvent(String paramString, Bundle paramBundle)
    {
      sendEvent(paramString, paramBundle);
    }
    
    void sendVolumeInfoChanged(ParcelableVolumeInfo paramParcelableVolumeInfo)
    {
      int i = -1 + mControllerCallbacks.beginBroadcast();
      for (;;)
      {
        IMediaControllerCallback localIMediaControllerCallback;
        if (i >= 0) {
          localIMediaControllerCallback = (IMediaControllerCallback)mControllerCallbacks.getBroadcastItem(i);
        }
        try
        {
          localIMediaControllerCallback.onVolumeInfoChanged(paramParcelableVolumeInfo);
          i--;
          continue;
          mControllerCallbacks.finishBroadcast();
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
    }
    
    public void setActive(boolean paramBoolean)
    {
      if (paramBoolean == mIsActive) {}
      do
      {
        return;
        mIsActive = paramBoolean;
      } while (!update());
      setMetadata(mMetadata);
      setPlaybackState(mState);
    }
    
    public void setCallback(MediaSessionCompat.Callback paramCallback, Handler paramHandler)
    {
      mCallback = paramCallback;
      if (paramCallback == null)
      {
        if (Build.VERSION.SDK_INT >= 18) {
          MediaSessionCompatApi18.setOnPlaybackPositionUpdateListener(mRccObj, null);
        }
        if (Build.VERSION.SDK_INT >= 19) {
          MediaSessionCompatApi19.setOnMetadataUpdateListener(mRccObj, null);
        }
      }
      for (;;)
      {
        return;
        if (paramHandler == null) {
          paramHandler = new Handler();
        }
        synchronized (mLock)
        {
          mHandler = new MessageHandler(paramHandler.getLooper());
          MediaSessionCompatApi19.Callback local2 = new MediaSessionCompatApi19.Callback()
          {
            public void onSeekTo(long paramAnonymousLong)
            {
              postToHandler(18, Long.valueOf(paramAnonymousLong));
            }
            
            public void onSetRating(Object paramAnonymousObject)
            {
              postToHandler(19, RatingCompat.fromRating(paramAnonymousObject));
            }
          };
          if (Build.VERSION.SDK_INT >= 18)
          {
            Object localObject4 = MediaSessionCompatApi18.createPlaybackPositionUpdateListener(local2);
            MediaSessionCompatApi18.setOnPlaybackPositionUpdateListener(mRccObj, localObject4);
          }
          if (Build.VERSION.SDK_INT < 19) {
            continue;
          }
          Object localObject3 = MediaSessionCompatApi19.createMetadataUpdateListener(local2);
          MediaSessionCompatApi19.setOnMetadataUpdateListener(mRccObj, localObject3);
          return;
        }
      }
    }
    
    public void setExtras(Bundle paramBundle)
    {
      mExtras = paramBundle;
      sendExtras(paramBundle);
    }
    
    public void setFlags(int paramInt)
    {
      synchronized (mLock)
      {
        mFlags = paramInt;
        update();
        return;
      }
    }
    
    public void setMediaButtonReceiver(PendingIntent paramPendingIntent) {}
    
    public void setMetadata(MediaMetadataCompat paramMediaMetadataCompat)
    {
      if (paramMediaMetadataCompat != null) {
        paramMediaMetadataCompat = new MediaMetadataCompat.Builder(paramMediaMetadataCompat, MediaSessionCompat.sMaxBitmapSize).build();
      }
      label101:
      do
      {
        synchronized (mLock)
        {
          mMetadata = paramMediaMetadataCompat;
          sendMetadata(paramMediaMetadataCompat);
          if (!mIsActive) {
            return;
          }
        }
        if (Build.VERSION.SDK_INT >= 19)
        {
          Object localObject4 = mRccObj;
          Bundle localBundle2 = null;
          if (paramMediaMetadataCompat == null) {
            if (mState != null) {
              break label101;
            }
          }
          for (long l = 0L;; l = mState.getActions())
          {
            MediaSessionCompatApi19.setMetadata(localObject4, localBundle2, l);
            return;
            localBundle2 = paramMediaMetadataCompat.getBundle();
            break;
          }
        }
      } while (Build.VERSION.SDK_INT < 14);
      Object localObject3 = mRccObj;
      Bundle localBundle1 = null;
      if (paramMediaMetadataCompat == null) {}
      for (;;)
      {
        MediaSessionCompatApi14.setMetadata(localObject3, localBundle1);
        return;
        localBundle1 = paramMediaMetadataCompat.getBundle();
      }
    }
    
    public void setPlaybackState(PlaybackStateCompat paramPlaybackStateCompat)
    {
      do
      {
        do
        {
          synchronized (mLock)
          {
            mState = paramPlaybackStateCompat;
            sendState(paramPlaybackStateCompat);
            if (!mIsActive) {
              return;
            }
          }
          if (paramPlaybackStateCompat != null) {
            break;
          }
        } while (Build.VERSION.SDK_INT < 14);
        MediaSessionCompatApi14.setState(mRccObj, 0);
        MediaSessionCompatApi14.setTransportControlFlags(mRccObj, 0L);
        return;
        if (Build.VERSION.SDK_INT >= 18) {
          MediaSessionCompatApi18.setState(mRccObj, paramPlaybackStateCompat.getState(), paramPlaybackStateCompat.getPosition(), paramPlaybackStateCompat.getPlaybackSpeed(), paramPlaybackStateCompat.getLastPositionUpdateTime());
        }
        while (Build.VERSION.SDK_INT >= 19)
        {
          MediaSessionCompatApi19.setTransportControlFlags(mRccObj, paramPlaybackStateCompat.getActions());
          return;
          if (Build.VERSION.SDK_INT >= 14) {
            MediaSessionCompatApi14.setState(mRccObj, paramPlaybackStateCompat.getState());
          }
        }
        if (Build.VERSION.SDK_INT >= 18)
        {
          MediaSessionCompatApi18.setTransportControlFlags(mRccObj, paramPlaybackStateCompat.getActions());
          return;
        }
      } while (Build.VERSION.SDK_INT < 14);
      MediaSessionCompatApi14.setTransportControlFlags(mRccObj, paramPlaybackStateCompat.getActions());
    }
    
    public void setPlaybackToLocal(int paramInt)
    {
      if (mVolumeProvider != null) {
        mVolumeProvider.setCallback(null);
      }
      mVolumeType = 1;
      sendVolumeInfoChanged(new ParcelableVolumeInfo(mVolumeType, mLocalStream, 2, mAudioManager.getStreamMaxVolume(mLocalStream), mAudioManager.getStreamVolume(mLocalStream)));
    }
    
    public void setPlaybackToRemote(VolumeProviderCompat paramVolumeProviderCompat)
    {
      if (paramVolumeProviderCompat == null) {
        throw new IllegalArgumentException("volumeProvider may not be null");
      }
      if (mVolumeProvider != null) {
        mVolumeProvider.setCallback(null);
      }
      mVolumeType = 2;
      mVolumeProvider = paramVolumeProviderCompat;
      sendVolumeInfoChanged(new ParcelableVolumeInfo(mVolumeType, mLocalStream, mVolumeProvider.getVolumeControl(), mVolumeProvider.getMaxVolume(), mVolumeProvider.getCurrentVolume()));
      paramVolumeProviderCompat.setCallback(mVolumeCallback);
    }
    
    public void setQueue(List<MediaSessionCompat.QueueItem> paramList)
    {
      mQueue = paramList;
      sendQueue(paramList);
    }
    
    public void setQueueTitle(CharSequence paramCharSequence)
    {
      mQueueTitle = paramCharSequence;
      sendQueueTitle(paramCharSequence);
    }
    
    public void setRatingType(int paramInt)
    {
      mRatingType = paramInt;
    }
    
    public void setSessionActivity(PendingIntent paramPendingIntent)
    {
      synchronized (mLock)
      {
        mSessionActivity = paramPendingIntent;
        return;
      }
    }
    
    void setVolumeTo(int paramInt1, int paramInt2)
    {
      if (mVolumeType == 2)
      {
        if (mVolumeProvider != null) {
          mVolumeProvider.onSetVolumeTo(paramInt1);
        }
        return;
      }
      mAudioManager.setStreamVolume(mLocalStream, paramInt1, paramInt2);
    }
    
    private static final class Command
    {
      public final String command;
      public final Bundle extras;
      public final ResultReceiver stub;
      
      public Command(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
      {
        command = paramString;
        extras = paramBundle;
        stub = paramResultReceiver;
      }
    }
    
    class MediaSessionStub
      extends IMediaSession.Stub
    {
      MediaSessionStub() {}
      
      public void adjustVolume(int paramInt1, int paramInt2, String paramString)
      {
        adjustVolume(paramInt1, paramInt2);
      }
      
      public void fastForward()
        throws RemoteException
      {
        postToHandler(16);
      }
      
      public Bundle getExtras()
      {
        synchronized (mLock)
        {
          Bundle localBundle = mExtras;
          return localBundle;
        }
      }
      
      public long getFlags()
      {
        synchronized (mLock)
        {
          long l = mFlags;
          return l;
        }
      }
      
      public PendingIntent getLaunchPendingIntent()
      {
        synchronized (mLock)
        {
          PendingIntent localPendingIntent = mSessionActivity;
          return localPendingIntent;
        }
      }
      
      public MediaMetadataCompat getMetadata()
      {
        return mMetadata;
      }
      
      public String getPackageName()
      {
        return mPackageName;
      }
      
      public PlaybackStateCompat getPlaybackState()
      {
        return getStateWithUpdatedPosition();
      }
      
      public List<MediaSessionCompat.QueueItem> getQueue()
      {
        synchronized (mLock)
        {
          List localList = mQueue;
          return localList;
        }
      }
      
      public CharSequence getQueueTitle()
      {
        return mQueueTitle;
      }
      
      public int getRatingType()
      {
        return mRatingType;
      }
      
      public String getTag()
      {
        return mTag;
      }
      
      public ParcelableVolumeInfo getVolumeAttributes()
      {
        synchronized (mLock)
        {
          int i = mVolumeType;
          int j = mLocalStream;
          VolumeProviderCompat localVolumeProviderCompat = mVolumeProvider;
          if (i == 2)
          {
            k = localVolumeProviderCompat.getVolumeControl();
            m = localVolumeProviderCompat.getMaxVolume();
            n = localVolumeProviderCompat.getCurrentVolume();
            return new ParcelableVolumeInfo(i, j, k, m, n);
          }
          int k = 2;
          int m = mAudioManager.getStreamMaxVolume(j);
          int n = mAudioManager.getStreamVolume(j);
        }
      }
      
      public boolean isTransportControlEnabled()
      {
        return (0x2 & mFlags) != 0;
      }
      
      public void next()
        throws RemoteException
      {
        postToHandler(14);
      }
      
      public void pause()
        throws RemoteException
      {
        postToHandler(12);
      }
      
      public void play()
        throws RemoteException
      {
        postToHandler(7);
      }
      
      public void playFromMediaId(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        postToHandler(8, paramString, paramBundle);
      }
      
      public void playFromSearch(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        postToHandler(9, paramString, paramBundle);
      }
      
      public void playFromUri(Uri paramUri, Bundle paramBundle)
        throws RemoteException
      {
        postToHandler(10, paramUri, paramBundle);
      }
      
      public void prepare()
        throws RemoteException
      {
        postToHandler(3);
      }
      
      public void prepareFromMediaId(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        postToHandler(4, paramString, paramBundle);
      }
      
      public void prepareFromSearch(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        postToHandler(5, paramString, paramBundle);
      }
      
      public void prepareFromUri(Uri paramUri, Bundle paramBundle)
        throws RemoteException
      {
        postToHandler(6, paramUri, paramBundle);
      }
      
      public void previous()
        throws RemoteException
      {
        postToHandler(15);
      }
      
      public void rate(RatingCompat paramRatingCompat)
        throws RemoteException
      {
        postToHandler(19, paramRatingCompat);
      }
      
      public void registerCallbackListener(IMediaControllerCallback paramIMediaControllerCallback)
      {
        if (mDestroyed) {}
        try
        {
          paramIMediaControllerCallback.onSessionDestroyed();
          return;
        }
        catch (Exception localException) {}
        mControllerCallbacks.register(paramIMediaControllerCallback);
        return;
      }
      
      public void rewind()
        throws RemoteException
      {
        postToHandler(17);
      }
      
      public void seekTo(long paramLong)
        throws RemoteException
      {
        postToHandler(18, Long.valueOf(paramLong));
      }
      
      public void sendCommand(String paramString, Bundle paramBundle, MediaSessionCompat.ResultReceiverWrapper paramResultReceiverWrapper)
      {
        postToHandler(1, new MediaSessionCompat.MediaSessionImplBase.Command(paramString, paramBundle, MediaSessionCompat.ResultReceiverWrapper.access$000(paramResultReceiverWrapper)));
      }
      
      public void sendCustomAction(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        postToHandler(20, paramString, paramBundle);
      }
      
      public boolean sendMediaButton(KeyEvent paramKeyEvent)
      {
        if ((0x1 & mFlags) != 0) {}
        for (boolean bool = true;; bool = false)
        {
          if (bool) {
            postToHandler(21, paramKeyEvent);
          }
          return bool;
        }
      }
      
      public void setVolumeTo(int paramInt1, int paramInt2, String paramString)
      {
        setVolumeTo(paramInt1, paramInt2);
      }
      
      public void skipToQueueItem(long paramLong)
      {
        postToHandler(11, Long.valueOf(paramLong));
      }
      
      public void stop()
        throws RemoteException
      {
        postToHandler(13);
      }
      
      public void unregisterCallbackListener(IMediaControllerCallback paramIMediaControllerCallback)
      {
        mControllerCallbacks.unregister(paramIMediaControllerCallback);
      }
    }
    
    private class MessageHandler
      extends Handler
    {
      private static final int KEYCODE_MEDIA_PAUSE = 127;
      private static final int KEYCODE_MEDIA_PLAY = 126;
      private static final int MSG_ADJUST_VOLUME = 2;
      private static final int MSG_COMMAND = 1;
      private static final int MSG_CUSTOM_ACTION = 20;
      private static final int MSG_FAST_FORWARD = 16;
      private static final int MSG_MEDIA_BUTTON = 21;
      private static final int MSG_NEXT = 14;
      private static final int MSG_PAUSE = 12;
      private static final int MSG_PLAY = 7;
      private static final int MSG_PLAY_MEDIA_ID = 8;
      private static final int MSG_PLAY_SEARCH = 9;
      private static final int MSG_PLAY_URI = 10;
      private static final int MSG_PREPARE = 3;
      private static final int MSG_PREPARE_MEDIA_ID = 4;
      private static final int MSG_PREPARE_SEARCH = 5;
      private static final int MSG_PREPARE_URI = 6;
      private static final int MSG_PREVIOUS = 15;
      private static final int MSG_RATE = 19;
      private static final int MSG_REWIND = 17;
      private static final int MSG_SEEK_TO = 18;
      private static final int MSG_SET_VOLUME = 22;
      private static final int MSG_SKIP_TO_ITEM = 11;
      private static final int MSG_STOP = 13;
      
      public MessageHandler(Looper paramLooper)
      {
        super();
      }
      
      private void onMediaButtonEvent(KeyEvent paramKeyEvent, MediaSessionCompat.Callback paramCallback)
      {
        int i = 1;
        if ((paramKeyEvent == null) || (paramKeyEvent.getAction() != 0)) {}
        label27:
        int j;
        label140:
        int k;
        label154:
        label310:
        label316:
        label321:
        do
        {
          return;
          long l;
          if (mState == null)
          {
            l = 0L;
            switch (paramKeyEvent.getKeyCode())
            {
            default: 
              return;
            case 79: 
            case 85: 
              if ((mState != null) && (mState.getState() == 3))
              {
                j = i;
                if ((0x204 & l) == 0L) {
                  break label310;
                }
                k = i;
                if ((0x202 & l) == 0L) {
                  break label316;
                }
              }
              break;
            }
          }
          for (;;)
          {
            if ((j == 0) || (i == 0)) {
              break label321;
            }
            paramCallback.onPause();
            return;
            l = mState.getActions();
            break label27;
            if ((0x4 & l) == 0L) {
              break;
            }
            paramCallback.onPlay();
            return;
            if ((0x2 & l) == 0L) {
              break;
            }
            paramCallback.onPause();
            return;
            if ((0x20 & l) == 0L) {
              break;
            }
            paramCallback.onSkipToNext();
            return;
            if ((0x10 & l) == 0L) {
              break;
            }
            paramCallback.onSkipToPrevious();
            return;
            if ((1L & l) == 0L) {
              break;
            }
            paramCallback.onStop();
            return;
            if ((0x40 & l) == 0L) {
              break;
            }
            paramCallback.onFastForward();
            return;
            if ((0x8 & l) == 0L) {
              break;
            }
            paramCallback.onRewind();
            return;
            j = 0;
            break label140;
            k = 0;
            break label154;
            i = 0;
          }
        } while ((j != 0) || (k == 0));
        paramCallback.onPlay();
      }
      
      public void handleMessage(Message paramMessage)
      {
        MediaSessionCompat.Callback localCallback = mCallback;
        if (localCallback == null) {}
        KeyEvent localKeyEvent;
        Intent localIntent;
        do
        {
          return;
          switch (what)
          {
          default: 
            return;
          case 1: 
            MediaSessionCompat.MediaSessionImplBase.Command localCommand = (MediaSessionCompat.MediaSessionImplBase.Command)obj;
            localCallback.onCommand(command, extras, stub);
            return;
          case 21: 
            localKeyEvent = (KeyEvent)obj;
            localIntent = new Intent("android.intent.action.MEDIA_BUTTON");
            localIntent.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent);
          }
        } while (localCallback.onMediaButtonEvent(localIntent));
        onMediaButtonEvent(localKeyEvent, localCallback);
        return;
        localCallback.onPrepare();
        return;
        localCallback.onPrepareFromMediaId((String)obj, paramMessage.getData());
        return;
        localCallback.onPrepareFromSearch((String)obj, paramMessage.getData());
        return;
        localCallback.onPrepareFromUri((Uri)obj, paramMessage.getData());
        return;
        localCallback.onPlay();
        return;
        localCallback.onPlayFromMediaId((String)obj, paramMessage.getData());
        return;
        localCallback.onPlayFromSearch((String)obj, paramMessage.getData());
        return;
        localCallback.onPlayFromUri((Uri)obj, paramMessage.getData());
        return;
        localCallback.onSkipToQueueItem(((Long)obj).longValue());
        return;
        localCallback.onPause();
        return;
        localCallback.onStop();
        return;
        localCallback.onSkipToNext();
        return;
        localCallback.onSkipToPrevious();
        return;
        localCallback.onFastForward();
        return;
        localCallback.onRewind();
        return;
        localCallback.onSeekTo(((Long)obj).longValue());
        return;
        localCallback.onSetRating((RatingCompat)obj);
        return;
        localCallback.onCustomAction((String)obj, paramMessage.getData());
        return;
        adjustVolume(((Integer)obj).intValue(), 0);
        return;
        setVolumeTo(((Integer)obj).intValue(), 0);
      }
      
      public void post(int paramInt)
      {
        post(paramInt, null);
      }
      
      public void post(int paramInt, Object paramObject)
      {
        obtainMessage(paramInt, paramObject).sendToTarget();
      }
      
      public void post(int paramInt1, Object paramObject, int paramInt2)
      {
        obtainMessage(paramInt1, paramInt2, 0, paramObject).sendToTarget();
      }
      
      public void post(int paramInt, Object paramObject, Bundle paramBundle)
      {
        Message localMessage = obtainMessage(paramInt, paramObject);
        localMessage.setData(paramBundle);
        localMessage.sendToTarget();
      }
    }
  }
  
  public static abstract interface OnActiveChangeListener
  {
    public abstract void onActiveChanged();
  }
  
  public static final class QueueItem
    implements Parcelable
  {
    public static final Parcelable.Creator<QueueItem> CREATOR = new Parcelable.Creator()
    {
      public MediaSessionCompat.QueueItem createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MediaSessionCompat.QueueItem(paramAnonymousParcel);
      }
      
      public MediaSessionCompat.QueueItem[] newArray(int paramAnonymousInt)
      {
        return new MediaSessionCompat.QueueItem[paramAnonymousInt];
      }
    };
    public static final int UNKNOWN_ID = -1;
    private final MediaDescriptionCompat mDescription;
    private final long mId;
    private Object mItem;
    
    QueueItem(Parcel paramParcel)
    {
      mDescription = ((MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(paramParcel));
      mId = paramParcel.readLong();
    }
    
    public QueueItem(MediaDescriptionCompat paramMediaDescriptionCompat, long paramLong)
    {
      this(null, paramMediaDescriptionCompat, paramLong);
    }
    
    private QueueItem(Object paramObject, MediaDescriptionCompat paramMediaDescriptionCompat, long paramLong)
    {
      if (paramMediaDescriptionCompat == null) {
        throw new IllegalArgumentException("Description cannot be null.");
      }
      if (paramLong == -1L) {
        throw new IllegalArgumentException("Id cannot be QueueItem.UNKNOWN_ID");
      }
      mDescription = paramMediaDescriptionCompat;
      mId = paramLong;
      mItem = paramObject;
    }
    
    public static QueueItem fromQueueItem(Object paramObject)
    {
      if ((paramObject == null) || (Build.VERSION.SDK_INT < 21)) {
        return null;
      }
      return new QueueItem(paramObject, MediaDescriptionCompat.fromMediaDescription(MediaSessionCompatApi21.QueueItem.getDescription(paramObject)), MediaSessionCompatApi21.QueueItem.getQueueId(paramObject));
    }
    
    public static List<QueueItem> fromQueueItemList(List<?> paramList)
    {
      Object localObject;
      if ((paramList == null) || (Build.VERSION.SDK_INT < 21)) {
        localObject = null;
      }
      for (;;)
      {
        return localObject;
        localObject = new ArrayList();
        Iterator localIterator = paramList.iterator();
        while (localIterator.hasNext()) {
          ((List)localObject).add(fromQueueItem(localIterator.next()));
        }
      }
    }
    
    @Deprecated
    public static QueueItem obtain(Object paramObject)
    {
      return fromQueueItem(paramObject);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public MediaDescriptionCompat getDescription()
    {
      return mDescription;
    }
    
    public long getQueueId()
    {
      return mId;
    }
    
    public Object getQueueItem()
    {
      if ((mItem != null) || (Build.VERSION.SDK_INT < 21)) {
        return mItem;
      }
      mItem = MediaSessionCompatApi21.QueueItem.createItem(mDescription.getMediaDescription(), mId);
      return mItem;
    }
    
    public String toString()
    {
      return "MediaSession.QueueItem {Description=" + mDescription + ", Id=" + mId + " }";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      mDescription.writeToParcel(paramParcel, paramInt);
      paramParcel.writeLong(mId);
    }
  }
  
  static final class ResultReceiverWrapper
    implements Parcelable
  {
    public static final Parcelable.Creator<ResultReceiverWrapper> CREATOR = new Parcelable.Creator()
    {
      public MediaSessionCompat.ResultReceiverWrapper createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MediaSessionCompat.ResultReceiverWrapper(paramAnonymousParcel);
      }
      
      public MediaSessionCompat.ResultReceiverWrapper[] newArray(int paramAnonymousInt)
      {
        return new MediaSessionCompat.ResultReceiverWrapper[paramAnonymousInt];
      }
    };
    private ResultReceiver mResultReceiver;
    
    ResultReceiverWrapper(Parcel paramParcel)
    {
      mResultReceiver = ((ResultReceiver)ResultReceiver.CREATOR.createFromParcel(paramParcel));
    }
    
    public ResultReceiverWrapper(ResultReceiver paramResultReceiver)
    {
      mResultReceiver = paramResultReceiver;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      mResultReceiver.writeToParcel(paramParcel, paramInt);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface SessionFlags {}
  
  public static final class Token
    implements Parcelable
  {
    public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator()
    {
      public MediaSessionCompat.Token createFromParcel(Parcel paramAnonymousParcel)
      {
        if (Build.VERSION.SDK_INT >= 21) {}
        for (Object localObject = paramAnonymousParcel.readParcelable(null);; localObject = paramAnonymousParcel.readStrongBinder()) {
          return new MediaSessionCompat.Token(localObject);
        }
      }
      
      public MediaSessionCompat.Token[] newArray(int paramAnonymousInt)
      {
        return new MediaSessionCompat.Token[paramAnonymousInt];
      }
    };
    private final Object mInner;
    
    Token(Object paramObject)
    {
      mInner = paramObject;
    }
    
    public static Token fromToken(Object paramObject)
    {
      if ((paramObject == null) || (Build.VERSION.SDK_INT < 21)) {
        return null;
      }
      return new Token(MediaSessionCompatApi21.verifyToken(paramObject));
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      Token localToken;
      do
      {
        return true;
        if (!(paramObject instanceof Token)) {
          return false;
        }
        localToken = (Token)paramObject;
        if (mInner != null) {
          break;
        }
      } while (mInner == null);
      return false;
      if (mInner == null) {
        return false;
      }
      return mInner.equals(mInner);
    }
    
    public Object getToken()
    {
      return mInner;
    }
    
    public int hashCode()
    {
      if (mInner == null) {
        return 0;
      }
      return mInner.hashCode();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      if (Build.VERSION.SDK_INT >= 21)
      {
        paramParcel.writeParcelable((Parcelable)mInner, paramInt);
        return;
      }
      paramParcel.writeStrongBinder((IBinder)mInner);
    }
  }
}
