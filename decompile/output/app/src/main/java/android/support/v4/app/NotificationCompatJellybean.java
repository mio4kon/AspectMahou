package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.BigPictureStyle;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.Notification.InboxStyle;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class NotificationCompatJellybean
{
  static final String EXTRA_ACTION_EXTRAS = "android.support.actionExtras";
  static final String EXTRA_ALLOW_GENERATED_REPLIES = "android.support.allowGeneratedReplies";
  static final String EXTRA_GROUP_KEY = "android.support.groupKey";
  static final String EXTRA_GROUP_SUMMARY = "android.support.isGroupSummary";
  static final String EXTRA_LOCAL_ONLY = "android.support.localOnly";
  static final String EXTRA_REMOTE_INPUTS = "android.support.remoteInputs";
  static final String EXTRA_SORT_KEY = "android.support.sortKey";
  static final String EXTRA_USE_SIDE_CHANNEL = "android.support.useSideChannel";
  private static final String KEY_ACTION_INTENT = "actionIntent";
  private static final String KEY_ALLOW_GENERATED_REPLIES = "allowGeneratedReplies";
  private static final String KEY_EXTRAS = "extras";
  private static final String KEY_ICON = "icon";
  private static final String KEY_REMOTE_INPUTS = "remoteInputs";
  private static final String KEY_TITLE = "title";
  public static final String TAG = "NotificationCompat";
  private static Class<?> sActionClass;
  private static Field sActionIconField;
  private static Field sActionIntentField;
  private static Field sActionTitleField;
  private static boolean sActionsAccessFailed;
  private static Field sActionsField;
  private static final Object sActionsLock = new Object();
  private static Field sExtrasField;
  private static boolean sExtrasFieldAccessFailed;
  private static final Object sExtrasLock = new Object();
  
  NotificationCompatJellybean() {}
  
  public static void addBigPictureStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, CharSequence paramCharSequence1, boolean paramBoolean1, CharSequence paramCharSequence2, Bitmap paramBitmap1, Bitmap paramBitmap2, boolean paramBoolean2)
  {
    Notification.BigPictureStyle localBigPictureStyle = new Notification.BigPictureStyle(paramNotificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(paramCharSequence1).bigPicture(paramBitmap1);
    if (paramBoolean2) {
      localBigPictureStyle.bigLargeIcon(paramBitmap2);
    }
    if (paramBoolean1) {
      localBigPictureStyle.setSummaryText(paramCharSequence2);
    }
  }
  
  public static void addBigTextStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, CharSequence paramCharSequence1, boolean paramBoolean, CharSequence paramCharSequence2, CharSequence paramCharSequence3)
  {
    Notification.BigTextStyle localBigTextStyle = new Notification.BigTextStyle(paramNotificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(paramCharSequence1).bigText(paramCharSequence3);
    if (paramBoolean) {
      localBigTextStyle.setSummaryText(paramCharSequence2);
    }
  }
  
  public static void addInboxStyle(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, CharSequence paramCharSequence1, boolean paramBoolean, CharSequence paramCharSequence2, ArrayList<CharSequence> paramArrayList)
  {
    Notification.InboxStyle localInboxStyle = new Notification.InboxStyle(paramNotificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(paramCharSequence1);
    if (paramBoolean) {
      localInboxStyle.setSummaryText(paramCharSequence2);
    }
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext()) {
      localInboxStyle.addLine((CharSequence)localIterator.next());
    }
  }
  
  public static SparseArray<Bundle> buildActionExtrasMap(List<Bundle> paramList)
  {
    SparseArray localSparseArray = null;
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      Bundle localBundle = (Bundle)paramList.get(i);
      if (localBundle != null)
      {
        if (localSparseArray == null) {
          localSparseArray = new SparseArray();
        }
        localSparseArray.put(i, localBundle);
      }
      i++;
    }
    return localSparseArray;
  }
  
  private static boolean ensureActionReflectionReadyLocked()
  {
    boolean bool = true;
    if (sActionsAccessFailed) {
      return false;
    }
    try
    {
      if (sActionsField == null)
      {
        sActionClass = Class.forName("android.app.Notification$Action");
        sActionIconField = sActionClass.getDeclaredField("icon");
        sActionTitleField = sActionClass.getDeclaredField("title");
        sActionIntentField = sActionClass.getDeclaredField("actionIntent");
        sActionsField = Notification.class.getDeclaredField("actions");
        sActionsField.setAccessible(true);
      }
      if (!sActionsAccessFailed) {
        return bool;
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      for (;;)
      {
        Log.e("NotificationCompat", "Unable to access notification actions", localClassNotFoundException);
        sActionsAccessFailed = bool;
      }
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      for (;;)
      {
        Log.e("NotificationCompat", "Unable to access notification actions", localNoSuchFieldException);
        sActionsAccessFailed = bool;
        continue;
        bool = false;
      }
    }
  }
  
  public static NotificationCompatBase.Action getAction(Notification paramNotification, int paramInt, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    synchronized (sActionsLock)
    {
      try
      {
        Object localObject3 = getActionObjectsLocked(paramNotification)[paramInt];
        Bundle localBundle1 = getExtras(paramNotification);
        Bundle localBundle2 = null;
        if (localBundle1 != null)
        {
          SparseArray localSparseArray = localBundle1.getSparseParcelableArray("android.support.actionExtras");
          localBundle2 = null;
          if (localSparseArray != null) {
            localBundle2 = (Bundle)localSparseArray.get(paramInt);
          }
        }
        NotificationCompatBase.Action localAction = readAction(paramFactory, paramFactory1, sActionIconField.getInt(localObject3), (CharSequence)sActionTitleField.get(localObject3), (PendingIntent)sActionIntentField.get(localObject3), localBundle2);
        return localAction;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        Log.e("NotificationCompat", "Unable to access notification actions", localIllegalAccessException);
        sActionsAccessFailed = true;
        return null;
      }
    }
  }
  
  public static int getActionCount(Notification paramNotification)
  {
    for (;;)
    {
      synchronized (sActionsLock)
      {
        Object[] arrayOfObject = getActionObjectsLocked(paramNotification);
        if (arrayOfObject != null)
        {
          i = arrayOfObject.length;
          return i;
        }
      }
      int i = 0;
    }
  }
  
  private static NotificationCompatBase.Action getActionFromBundle(Bundle paramBundle, NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    return paramFactory.build(paramBundle.getInt("icon"), paramBundle.getCharSequence("title"), (PendingIntent)paramBundle.getParcelable("actionIntent"), paramBundle.getBundle("extras"), RemoteInputCompatJellybean.fromBundleArray(BundleUtil.getBundleArrayFromBundle(paramBundle, "remoteInputs"), paramFactory1), paramBundle.getBoolean("allowGeneratedReplies"));
  }
  
  private static Object[] getActionObjectsLocked(Notification paramNotification)
  {
    Object[] arrayOfObject;
    synchronized (sActionsLock)
    {
      if (!ensureActionReflectionReadyLocked()) {
        return null;
      }
    }
    return null;
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
        arrayOfAction[i] = getActionFromBundle((Bundle)paramArrayList.get(i), paramFactory, paramFactory1);
      }
    }
  }
  
  private static Bundle getBundleForAction(NotificationCompatBase.Action paramAction)
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("icon", paramAction.getIcon());
    localBundle.putCharSequence("title", paramAction.getTitle());
    localBundle.putParcelable("actionIntent", paramAction.getActionIntent());
    localBundle.putBundle("extras", paramAction.getExtras());
    localBundle.putParcelableArray("remoteInputs", RemoteInputCompatJellybean.toBundleArray(paramAction.getRemoteInputs()));
    return localBundle;
  }
  
  public static Bundle getExtras(Notification paramNotification)
  {
    Field localField;
    Bundle localBundle;
    synchronized (sExtrasLock)
    {
      if (sExtrasFieldAccessFailed) {
        return null;
      }
    }
  }
  
  public static String getGroup(Notification paramNotification)
  {
    return getExtras(paramNotification).getString("android.support.groupKey");
  }
  
  public static boolean getLocalOnly(Notification paramNotification)
  {
    return getExtras(paramNotification).getBoolean("android.support.localOnly");
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
        ((ArrayList)localObject).add(getBundleForAction(paramArrayOfAction[j]));
      }
    }
  }
  
  public static String getSortKey(Notification paramNotification)
  {
    return getExtras(paramNotification).getString("android.support.sortKey");
  }
  
  public static boolean isGroupSummary(Notification paramNotification)
  {
    return getExtras(paramNotification).getBoolean("android.support.isGroupSummary");
  }
  
  public static NotificationCompatBase.Action readAction(NotificationCompatBase.Action.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1, int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent, Bundle paramBundle)
  {
    RemoteInputCompatBase.RemoteInput[] arrayOfRemoteInput = null;
    boolean bool = false;
    if (paramBundle != null)
    {
      arrayOfRemoteInput = RemoteInputCompatJellybean.fromBundleArray(BundleUtil.getBundleArrayFromBundle(paramBundle, "android.support.remoteInputs"), paramFactory1);
      bool = paramBundle.getBoolean("android.support.allowGeneratedReplies");
    }
    return paramFactory.build(paramInt, paramCharSequence, paramPendingIntent, paramBundle, arrayOfRemoteInput, bool);
  }
  
  public static Bundle writeActionAndGetExtras(Notification.Builder paramBuilder, NotificationCompatBase.Action paramAction)
  {
    paramBuilder.addAction(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent());
    Bundle localBundle = new Bundle(paramAction.getExtras());
    if (paramAction.getRemoteInputs() != null) {
      localBundle.putParcelableArray("android.support.remoteInputs", RemoteInputCompatJellybean.toBundleArray(paramAction.getRemoteInputs()));
    }
    localBundle.putBoolean("android.support.allowGeneratedReplies", paramAction.getAllowGeneratedReplies());
    return localBundle;
  }
  
  public static class Builder
    implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
  {
    private Notification.Builder b;
    private List<Bundle> mActionExtrasList = new ArrayList();
    private RemoteViews mBigContentView;
    private RemoteViews mContentView;
    private final Bundle mExtras;
    
    public Builder(Context paramContext, Notification paramNotification, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews1, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, int paramInt4, CharSequence paramCharSequence4, boolean paramBoolean3, Bundle paramBundle, String paramString1, boolean paramBoolean4, String paramString2, RemoteViews paramRemoteViews2, RemoteViews paramRemoteViews3)
    {
      Notification.Builder localBuilder1 = new Notification.Builder(paramContext).setWhen(when).setSmallIcon(icon, iconLevel).setContent(contentView).setTicker(tickerText, paramRemoteViews1).setSound(sound, audioStreamType).setVibrate(vibrate).setLights(ledARGB, ledOnMS, ledOffMS);
      boolean bool1;
      boolean bool2;
      label126:
      boolean bool3;
      label148:
      boolean bool4;
      if ((0x2 & flags) != 0)
      {
        bool1 = true;
        Notification.Builder localBuilder2 = localBuilder1.setOngoing(bool1);
        if ((0x8 & flags) == 0) {
          break label357;
        }
        bool2 = true;
        Notification.Builder localBuilder3 = localBuilder2.setOnlyAlertOnce(bool2);
        if ((0x10 & flags) == 0) {
          break label363;
        }
        bool3 = true;
        Notification.Builder localBuilder4 = localBuilder3.setAutoCancel(bool3).setDefaults(defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence4).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(deleteIntent);
        if ((0x80 & flags) == 0) {
          break label369;
        }
        bool4 = true;
        label209:
        b = localBuilder4.setFullScreenIntent(paramPendingIntent2, bool4).setLargeIcon(paramBitmap).setNumber(paramInt1).setUsesChronometer(paramBoolean2).setPriority(paramInt4).setProgress(paramInt2, paramInt3, paramBoolean1);
        mExtras = new Bundle();
        if (paramBundle != null) {
          mExtras.putAll(paramBundle);
        }
        if (paramBoolean3) {
          mExtras.putBoolean("android.support.localOnly", true);
        }
        if (paramString1 != null)
        {
          mExtras.putString("android.support.groupKey", paramString1);
          if (!paramBoolean4) {
            break label375;
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
        label357:
        bool2 = false;
        break label126;
        label363:
        bool3 = false;
        break label148;
        label369:
        bool4 = false;
        break label209;
        label375:
        mExtras.putBoolean("android.support.useSideChannel", true);
      }
    }
    
    public void addAction(NotificationCompatBase.Action paramAction)
    {
      mActionExtrasList.add(NotificationCompatJellybean.writeActionAndGetExtras(b, paramAction));
    }
    
    public Notification build()
    {
      Notification localNotification = b.build();
      Bundle localBundle1 = NotificationCompatJellybean.getExtras(localNotification);
      Bundle localBundle2 = new Bundle(mExtras);
      Iterator localIterator = mExtras.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (localBundle1.containsKey(str)) {
          localBundle2.remove(str);
        }
      }
      localBundle1.putAll(localBundle2);
      SparseArray localSparseArray = NotificationCompatJellybean.buildActionExtrasMap(mActionExtrasList);
      if (localSparseArray != null) {
        NotificationCompatJellybean.getExtras(localNotification).putSparseParcelableArray("android.support.actionExtras", localSparseArray);
      }
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