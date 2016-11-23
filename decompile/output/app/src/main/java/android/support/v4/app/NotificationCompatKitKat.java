package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.List;

class NotificationCompatKitKat
{
  NotificationCompatKitKat() {}
  
  public static NotificationCompatBase.Action getAction(Notification paramNotification, int paramInt, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    Notification.Action localAction = actions[paramInt];
    SparseArray localSparseArray = extras.getSparseParcelableArray("android.support.actionExtras");
    Bundle localBundle = null;
    if (localSparseArray != null) {
      localBundle = (Bundle)localSparseArray.get(paramInt);
    }
    return NotificationCompatJellybean.readAction(paramFactory, paramFactory1, icon, title, actionIntent, localBundle);
  }
  
  public static int getActionCount(Notification paramNotification)
  {
    if (actions != null) {
      return actions.length;
    }
    return 0;
  }
  
  public static Bundle getExtras(Notification paramNotification)
  {
    return extras;
  }
  
  public static String getGroup(Notification paramNotification)
  {
    return extras.getString("android.support.groupKey");
  }
  
  public static boolean getLocalOnly(Notification paramNotification)
  {
    return extras.getBoolean("android.support.localOnly");
  }
  
  public static String getSortKey(Notification paramNotification)
  {
    return extras.getString("android.support.sortKey");
  }
  
  public static boolean isGroupSummary(Notification paramNotification)
  {
    return extras.getBoolean("android.support.isGroupSummary");
  }
  
  public static class Builder
    implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
  {
    private Notification.Builder b;
    private List<Bundle> mActionExtrasList = new ArrayList();
    private RemoteViews mBigContentView;
    private RemoteViews mContentView;
    private Bundle mExtras;
    
    public Builder(Context paramContext, Notification paramNotification, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews1, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt4, CharSequence paramCharSequence4, boolean paramBoolean4, ArrayList<String> paramArrayList, Bundle paramBundle, String paramString1, boolean paramBoolean5, String paramString2, RemoteViews paramRemoteViews2, RemoteViews paramRemoteViews3)
    {
      Notification.Builder localBuilder1 = new Notification.Builder(paramContext).setWhen(when).setShowWhen(paramBoolean2).setSmallIcon(icon, iconLevel).setContent(contentView).setTicker(tickerText, paramRemoteViews1).setSound(sound, audioStreamType).setVibrate(vibrate).setLights(ledARGB, ledOnMS, ledOffMS);
      boolean bool1;
      boolean bool2;
      label131:
      boolean bool3;
      label153:
      boolean bool4;
      if ((0x2 & flags) != 0)
      {
        bool1 = true;
        Notification.Builder localBuilder2 = localBuilder1.setOngoing(bool1);
        if ((0x8 & flags) == 0) {
          break label400;
        }
        bool2 = true;
        Notification.Builder localBuilder3 = localBuilder2.setOnlyAlertOnce(bool2);
        if ((0x10 & flags) == 0) {
          break label406;
        }
        bool3 = true;
        Notification.Builder localBuilder4 = localBuilder3.setAutoCancel(bool3).setDefaults(defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence4).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(deleteIntent);
        if ((0x80 & flags) == 0) {
          break label412;
        }
        bool4 = true;
        label214:
        b = localBuilder4.setFullScreenIntent(paramPendingIntent2, bool4).setLargeIcon(paramBitmap).setNumber(paramInt1).setUsesChronometer(paramBoolean3).setPriority(paramInt4).setProgress(paramInt2, paramInt3, paramBoolean1);
        mExtras = new Bundle();
        if (paramBundle != null) {
          mExtras.putAll(paramBundle);
        }
        if ((paramArrayList != null) && (!paramArrayList.isEmpty())) {
          mExtras.putStringArray("android.people", (String[])paramArrayList.toArray(new String[paramArrayList.size()]));
        }
        if (paramBoolean4) {
          mExtras.putBoolean("android.support.localOnly", true);
        }
        if (paramString1 != null)
        {
          mExtras.putString("android.support.groupKey", paramString1);
          if (!paramBoolean5) {
            break label418;
          }
          mExtras.putBoolean("android.support.isGroupSummary", true);
        }
      }
      for (;;)
      {
        if (paramString2 != null) {
          mExtras.putString("android.support.sortKey", paramString2);
        }
        mContentView = paramRemoteViews2;
        mBigContentView = paramRemoteViews3;
        return;
        bool1 = false;
        break;
        label400:
        bool2 = false;
        break label131;
        label406:
        bool3 = false;
        break label153;
        label412:
        bool4 = false;
        break label214;
        label418:
        mExtras.putBoolean("android.support.useSideChannel", true);
      }
    }
    
    public void addAction(NotificationCompatBase.Action paramAction)
    {
      mActionExtrasList.add(NotificationCompatJellybean.writeActionAndGetExtras(b, paramAction));
    }
    
    public Notification build()
    {
      SparseArray localSparseArray = NotificationCompatJellybean.buildActionExtrasMap(mActionExtrasList);
      if (localSparseArray != null) {
        mExtras.putSparseParcelableArray("android.support.actionExtras", localSparseArray);
      }
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
