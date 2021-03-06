package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.Action.Builder;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.RemoteViews;
import java.util.ArrayList;

class NotificationCompatApi20
{
  NotificationCompatApi20() {}
  
  public static void addAction(Notification.Builder paramBuilder, NotificationCompatBase.Action paramAction)
  {
    Notification.Action.Builder localBuilder = new Notification.Action.Builder(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent());
    if (paramAction.getRemoteInputs() != null)
    {
      RemoteInput[] arrayOfRemoteInput = RemoteInputCompatApi20.fromCompat(paramAction.getRemoteInputs());
      int i = arrayOfRemoteInput.length;
      for (int j = 0; j < i; j++) {
        localBuilder.addRemoteInput(arrayOfRemoteInput[j]);
      }
    }
    if (paramAction.getExtras() != null) {}
    for (Bundle localBundle = new Bundle(paramAction.getExtras());; localBundle = new Bundle())
    {
      localBundle.putBoolean("android.support.allowGeneratedReplies", paramAction.getAllowGeneratedReplies());
      localBuilder.addExtras(localBundle);
      paramBuilder.addAction(localBuilder.build());
      return;
    }
  }
  
  public static NotificationCompatBase.Action getAction(Notification paramNotification, int paramInt, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    return getActionCompatFromAction(actions[paramInt], paramFactory, paramFactory1);
  }
  
  private static NotificationCompatBase.Action getActionCompatFromAction(Notification.Action paramAction, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    RemoteInputCompatBase.RemoteInput[] arrayOfRemoteInput = RemoteInputCompatApi20.toCompat(paramAction.getRemoteInputs(), paramFactory1);
    boolean bool = paramAction.getExtras().getBoolean("android.support.allowGeneratedReplies");
    return paramFactory.build(icon, title, actionIntent, paramAction.getExtras(), arrayOfRemoteInput, bool);
  }
  
  private static Notification.Action getActionFromActionCompat(NotificationCompatBase.Action paramAction)
  {
    Notification.Action.Builder localBuilder = new Notification.Action.Builder(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent()).addExtras(paramAction.getExtras());
    RemoteInputCompatBase.RemoteInput[] arrayOfRemoteInput = paramAction.getRemoteInputs();
    if (arrayOfRemoteInput != null)
    {
      RemoteInput[] arrayOfRemoteInput1 = RemoteInputCompatApi20.fromCompat(arrayOfRemoteInput);
      int i = arrayOfRemoteInput1.length;
      for (int j = 0; j < i; j++) {
        localBuilder.addRemoteInput(arrayOfRemoteInput1[j]);
      }
    }
    return localBuilder.build();
  }
  
  public static NotificationCompatBase.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    NotificationCompatBase.Action[] arrayOfAction;
    if (paramArrayList == null) {
      arrayOfAction = null;
    }
    for (;;)
    {
      return arrayOfAction;
      arrayOfAction = paramFactory.newArray(paramArrayList.size());
      for (int i = 0; i < arrayOfAction.length; i++) {
        arrayOfAction[i] = getActionCompatFromAction((Notification.Action)paramArrayList.get(i), paramFactory, paramFactory1);
      }
    }
  }
  
  public static String getGroup(Notification paramNotification)
  {
    return paramNotification.getGroup();
  }
  
  public static boolean getLocalOnly(Notification paramNotification)
  {
    return (0x100 & flags) != 0;
  }
  
  public static ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompatBase.Action[] paramArrayOfAction)
  {
    Object localObject;
    if (paramArrayOfAction == null) {
      localObject = null;
    }
    for (;;)
    {
      return localObject;
      localObject = new ArrayList(paramArrayOfAction.length);
      int i = paramArrayOfAction.length;
      for (int j = 0; j < i; j++) {
        ((ArrayList)localObject).add(getActionFromActionCompat(paramArrayOfAction[j]));
      }
    }
  }
  
  public static String getSortKey(Notification paramNotification)
  {
    return paramNotification.getSortKey();
  }
  
  public static boolean isGroupSummary(Notification paramNotification)
  {
    return (0x200 & flags) != 0;
  }
  
  public static class Builder
    implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
  {
    private Notification.Builder b;
    private RemoteViews mBigContentView;
    private RemoteViews mContentView;
    private Bundle mExtras;
    
    public Builder(Context paramContext, Notification paramNotification, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews1, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt4, CharSequence paramCharSequence4, boolean paramBoolean4, ArrayList<String> paramArrayList, Bundle paramBundle, String paramString1, boolean paramBoolean5, String paramString2, RemoteViews paramRemoteViews2, RemoteViews paramRemoteViews3)
    {
      Notification.Builder localBuilder1 = new Notification.Builder(paramContext).setWhen(when).setShowWhen(paramBoolean2).setSmallIcon(icon, iconLevel).setContent(contentView).setTicker(tickerText, paramRemoteViews1).setSound(sound, audioStreamType).setVibrate(vibrate).setLights(ledARGB, ledOnMS, ledOffMS);
      boolean bool1;
      boolean bool2;
      label120:
      boolean bool3;
      label142:
      Notification.Builder localBuilder4;
      if ((0x2 & flags) != 0)
      {
        bool1 = true;
        Notification.Builder localBuilder2 = localBuilder1.setOngoing(bool1);
        if ((0x8 & flags) == 0) {
          break label347;
        }
        bool2 = true;
        Notification.Builder localBuilder3 = localBuilder2.setOnlyAlertOnce(bool2);
        if ((0x10 & flags) == 0) {
          break label353;
        }
        bool3 = true;
        localBuilder4 = localBuilder3.setAutoCancel(bool3).setDefaults(defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence4).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(deleteIntent);
        if ((0x80 & flags) == 0) {
          break label359;
        }
      }
      label347:
      label353:
      label359:
      for (boolean bool4 = true;; bool4 = false)
      {
        b = localBuilder4.setFullScreenIntent(paramPendingIntent2, bool4).setLargeIcon(paramBitmap).setNumber(paramInt1).setUsesChronometer(paramBoolean3).setPriority(paramInt4).setProgress(paramInt2, paramInt3, paramBoolean1).setLocalOnly(paramBoolean4).setGroup(paramString1).setGroupSummary(paramBoolean5).setSortKey(paramString2);
        mExtras = new Bundle();
        if (paramBundle != null) {
          mExtras.putAll(paramBundle);
        }
        if ((paramArrayList != null) && (!paramArrayList.isEmpty())) {
          mExtras.putStringArray("android.people", (String[])paramArrayList.toArray(new String[paramArrayList.size()]));
        }
        mContentView = paramRemoteViews2;
        mBigContentView = paramRemoteViews3;
        return;
        bool1 = false;
        break;
        bool2 = false;
        break label120;
        bool3 = false;
        break label142;
      }
    }
    
    public void addAction(NotificationCompatBase.Action paramAction)
    {
      NotificationCompatApi20.addAction(b, paramAction);
    }
    
    public Notification build()
    {
      b.setExtras(mExtras);
      Notification localNotification = b.build();
      if (mContentView != null) {
        contentView = mContentView;
      }
      if (mBigContentView != null) {
        bigContentView = mBigContentView;
      }
      return localNotification;
    }
    
    public Notification.Builder getBuilder()
    {
      return b;
    }
  }
}
