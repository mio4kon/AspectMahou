package android.support.v7.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat.BuilderExtender;
import android.support.v4.app.NotificationCompat.Style;
import android.support.v4.media.session.MediaSessionCompat.Token;

public class NotificationCompat
  extends android.support.v4.app.NotificationCompat
{
  public NotificationCompat() {}
  
  static void addBigMediaStyleToBuilderJellybean(Notification paramNotification, android.support.v4.app.NotificationCompat.Builder paramBuilder)
  {
    if ((mStyle instanceof MediaStyle))
    {
      MediaStyle localMediaStyle = (MediaStyle)mStyle;
      NotificationCompatImplBase.overrideBigContentView(paramNotification, mContext, mContentTitle, mContentText, mContentInfo, mNumber, mLargeIcon, mSubText, mUseChronometer, mNotification.when, mActions, mShowCancelButton, mCancelButtonIntent);
      Bundle localBundle = getExtras(paramNotification);
      if (mToken != null) {
        BundleCompat.putBinder(localBundle, "android.mediaSession", (IBinder)mToken.getToken());
      }
      if (mActionsToShowInCompact != null) {
        localBundle.putIntArray("android.compactActions", mActionsToShowInCompact);
      }
    }
  }
  
  static void addMediaStyleToBuilderIcs(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, android.support.v4.app.NotificationCompat.Builder paramBuilder)
  {
    if ((mStyle instanceof MediaStyle))
    {
      MediaStyle localMediaStyle = (MediaStyle)mStyle;
      NotificationCompatImplBase.overrideContentView(paramNotificationBuilderWithBuilderAccessor, mContext, mContentTitle, mContentText, mContentInfo, mNumber, mLargeIcon, mSubText, mUseChronometer, mNotification.when, mActions, mActionsToShowInCompact, mShowCancelButton, mCancelButtonIntent);
    }
  }
  
  static void addMediaStyleToBuilderLollipop(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, NotificationCompat.Style paramStyle)
  {
    MediaStyle localMediaStyle;
    int[] arrayOfInt;
    if ((paramStyle instanceof MediaStyle))
    {
      localMediaStyle = (MediaStyle)paramStyle;
      arrayOfInt = mActionsToShowInCompact;
      if (mToken == null) {
        break label41;
      }
    }
    label41:
    for (Object localObject = mToken.getToken();; localObject = null)
    {
      NotificationCompatImpl21.addMediaStyle(paramNotificationBuilderWithBuilderAccessor, arrayOfInt, localObject);
      return;
    }
  }
  
  public static MediaSessionCompat.Token getMediaSession(Notification paramNotification)
  {
    Bundle localBundle = getExtras(paramNotification);
    if (localBundle != null) {
      if (Build.VERSION.SDK_INT >= 21)
      {
        Parcelable localParcelable = localBundle.getParcelable("android.mediaSession");
        if (localParcelable != null) {
          return MediaSessionCompat.Token.fromToken(localParcelable);
        }
      }
      else
      {
        IBinder localIBinder = BundleCompat.getBinder(localBundle, "android.mediaSession");
        if (localIBinder != null)
        {
          Parcel localParcel = Parcel.obtain();
          localParcel.writeStrongBinder(localIBinder);
          localParcel.setDataPosition(0);
          MediaSessionCompat.Token localToken = (MediaSessionCompat.Token)MediaSessionCompat.Token.CREATOR.createFromParcel(localParcel);
          localParcel.recycle();
          return localToken;
        }
      }
    }
    return null;
  }
  
  public static class Builder
    extends android.support.v4.app.NotificationCompat.Builder
  {
    public Builder(Context paramContext)
    {
      super();
    }
    
    protected NotificationCompat.BuilderExtender getExtender()
    {
      if (Build.VERSION.SDK_INT >= 21) {
        return new NotificationCompat.LollipopExtender();
      }
      if (Build.VERSION.SDK_INT >= 16) {
        return new NotificationCompat.JellybeanExtender();
      }
      if (Build.VERSION.SDK_INT >= 14) {
        return new NotificationCompat.IceCreamSandwichExtender();
      }
      return super.getExtender();
    }
  }
  
  private static class IceCreamSandwichExtender
    extends NotificationCompat.BuilderExtender
  {
    IceCreamSandwichExtender() {}
    
    public Notification build(android.support.v4.app.NotificationCompat.Builder paramBuilder, NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      NotificationCompat.addMediaStyleToBuilderIcs(paramNotificationBuilderWithBuilderAccessor, paramBuilder);
      return paramNotificationBuilderWithBuilderAccessor.build();
    }
  }
  
  private static class JellybeanExtender
    extends NotificationCompat.BuilderExtender
  {
    JellybeanExtender() {}
    
    public Notification build(android.support.v4.app.NotificationCompat.Builder paramBuilder, NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      NotificationCompat.addMediaStyleToBuilderIcs(paramNotificationBuilderWithBuilderAccessor, paramBuilder);
      Notification localNotification = paramNotificationBuilderWithBuilderAccessor.build();
      NotificationCompat.addBigMediaStyleToBuilderJellybean(localNotification, paramBuilder);
      return localNotification;
    }
  }
  
  private static class LollipopExtender
    extends NotificationCompat.BuilderExtender
  {
    LollipopExtender() {}
    
    public Notification build(android.support.v4.app.NotificationCompat.Builder paramBuilder, NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      NotificationCompat.addMediaStyleToBuilderLollipop(paramNotificationBuilderWithBuilderAccessor, mStyle);
      return paramNotificationBuilderWithBuilderAccessor.build();
    }
  }
  
  public static class MediaStyle
    extends NotificationCompat.Style
  {
    int[] mActionsToShowInCompact = null;
    PendingIntent mCancelButtonIntent;
    boolean mShowCancelButton;
    MediaSessionCompat.Token mToken;
    
    public MediaStyle() {}
    
    public MediaStyle(android.support.v4.app.NotificationCompat.Builder paramBuilder)
    {
      setBuilder(paramBuilder);
    }
    
    public MediaStyle setCancelButtonIntent(PendingIntent paramPendingIntent)
    {
      mCancelButtonIntent = paramPendingIntent;
      return this;
    }
    
    public MediaStyle setMediaSession(MediaSessionCompat.Token paramToken)
    {
      mToken = paramToken;
      return this;
    }
    
    public MediaStyle setShowActionsInCompactView(int... paramVarArgs)
    {
      mActionsToShowInCompact = paramVarArgs;
      return this;
    }
    
    public MediaStyle setShowCancelButton(boolean paramBoolean)
    {
      mShowCancelButton = paramBoolean;
      return this;
    }
  }
}
