package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.RemoteInput.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Iterator;

class NotificationCompatApi21
{
  public static final String CATEGORY_ALARM = "alarm";
  public static final String CATEGORY_CALL = "call";
  public static final String CATEGORY_EMAIL = "email";
  public static final String CATEGORY_ERROR = "err";
  public static final String CATEGORY_EVENT = "event";
  public static final String CATEGORY_MESSAGE = "msg";
  public static final String CATEGORY_PROGRESS = "progress";
  public static final String CATEGORY_PROMO = "promo";
  public static final String CATEGORY_RECOMMENDATION = "recommendation";
  public static final String CATEGORY_SERVICE = "service";
  public static final String CATEGORY_SOCIAL = "social";
  public static final String CATEGORY_STATUS = "status";
  public static final String CATEGORY_SYSTEM = "sys";
  public static final String CATEGORY_TRANSPORT = "transport";
  private static final String KEY_AUTHOR = "author";
  private static final String KEY_MESSAGES = "messages";
  private static final String KEY_ON_READ = "on_read";
  private static final String KEY_ON_REPLY = "on_reply";
  private static final String KEY_PARTICIPANTS = "participants";
  private static final String KEY_REMOTE_INPUT = "remote_input";
  private static final String KEY_TEXT = "text";
  private static final String KEY_TIMESTAMP = "timestamp";
  
  NotificationCompatApi21() {}
  
  private static RemoteInput fromCompatRemoteInput(RemoteInputCompatBase.RemoteInput paramRemoteInput)
  {
    return new RemoteInput.Builder(paramRemoteInput.getResultKey()).setLabel(paramRemoteInput.getLabel()).setChoices(paramRemoteInput.getChoices()).setAllowFreeFormInput(paramRemoteInput.getAllowFreeFormInput()).addExtras(paramRemoteInput.getExtras()).build();
  }
  
  static Bundle getBundleForUnreadConversation(NotificationCompatBase.UnreadConversation paramUnreadConversation)
  {
    if (paramUnreadConversation == null) {
      return null;
    }
    Bundle localBundle1 = new Bundle();
    String[] arrayOfString = paramUnreadConversation.getParticipants();
    String str = null;
    if (arrayOfString != null)
    {
      int j = paramUnreadConversation.getParticipants().length;
      str = null;
      if (j > 1) {
        str = paramUnreadConversation.getParticipants()[0];
      }
    }
    Parcelable[] arrayOfParcelable = new Parcelable[paramUnreadConversation.getMessages().length];
    for (int i = 0; i < arrayOfParcelable.length; i++)
    {
      Bundle localBundle2 = new Bundle();
      localBundle2.putString("text", paramUnreadConversation.getMessages()[i]);
      localBundle2.putString("author", str);
      arrayOfParcelable[i] = localBundle2;
    }
    localBundle1.putParcelableArray("messages", arrayOfParcelable);
    RemoteInputCompatBase.RemoteInput localRemoteInput = paramUnreadConversation.getRemoteInput();
    if (localRemoteInput != null) {
      localBundle1.putParcelable("remote_input", fromCompatRemoteInput(localRemoteInput));
    }
    localBundle1.putParcelable("on_reply", paramUnreadConversation.getReplyPendingIntent());
    localBundle1.putParcelable("on_read", paramUnreadConversation.getReadPendingIntent());
    localBundle1.putStringArray("participants", paramUnreadConversation.getParticipants());
    localBundle1.putLong("timestamp", paramUnreadConversation.getLatestTimestamp());
    return localBundle1;
  }
  
  public static String getCategory(Notification paramNotification)
  {
    return category;
  }
  
  static NotificationCompatBase.UnreadConversation getUnreadConversationFromBundle(Bundle paramBundle, NotificationCompatBase.UnreadConversation.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
  {
    if (paramBundle == null) {
      return null;
    }
    Parcelable[] arrayOfParcelable = paramBundle.getParcelableArray("messages");
    Object localObject = null;
    String[] arrayOfString2;
    int i;
    if (arrayOfParcelable != null)
    {
      arrayOfString2 = new String[arrayOfParcelable.length];
      i = 1;
    }
    label155:
    label184:
    label186:
    for (int j = 0;; j++)
    {
      if (j < arrayOfString2.length) {
        if ((arrayOfParcelable[j] instanceof Bundle)) {
          break label155;
        }
      }
      for (i = 0;; i = 0)
      {
        if (i == 0) {
          break label184;
        }
        localObject = arrayOfString2;
        PendingIntent localPendingIntent1 = (PendingIntent)paramBundle.getParcelable("on_read");
        PendingIntent localPendingIntent2 = (PendingIntent)paramBundle.getParcelable("on_reply");
        RemoteInput localRemoteInput = (RemoteInput)paramBundle.getParcelable("remote_input");
        String[] arrayOfString1 = paramBundle.getStringArray("participants");
        if ((arrayOfString1 == null) || (arrayOfString1.length != 1)) {
          break;
        }
        RemoteInputCompatBase.RemoteInput localRemoteInput1 = null;
        if (localRemoteInput != null) {
          localRemoteInput1 = toCompatRemoteInput(localRemoteInput, paramFactory1);
        }
        return paramFactory.build(localObject, localRemoteInput1, localPendingIntent2, localPendingIntent1, arrayOfString1, paramBundle.getLong("timestamp"));
        arrayOfString2[j] = ((Bundle)arrayOfParcelable[j]).getString("text");
        if (arrayOfString2[j] != null) {
          break label186;
        }
      }
      break;
    }
  }
  
  private static RemoteInputCompatBase.RemoteInput toCompatRemoteInput(RemoteInput paramRemoteInput, RemoteInputCompatBase.RemoteInput.Factory paramFactory)
  {
    return paramFactory.build(paramRemoteInput.getResultKey(), paramRemoteInput.getLabel(), paramRemoteInput.getChoices(), paramRemoteInput.getAllowFreeFormInput(), paramRemoteInput.getExtras());
  }
  
  public static class Builder
    implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
  {
    private Notification.Builder b;
    private RemoteViews mBigContentView;
    private RemoteViews mContentView;
    private Bundle mExtras;
    private RemoteViews mHeadsUpContentView;
    
    public Builder(Context paramContext, Notification paramNotification1, CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3, RemoteViews paramRemoteViews1, int paramInt1, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, Bitmap paramBitmap, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt4, CharSequence paramCharSequence4, boolean paramBoolean4, String paramString1, ArrayList<String> paramArrayList, Bundle paramBundle, int paramInt5, int paramInt6, Notification paramNotification2, String paramString2, boolean paramBoolean5, String paramString3, RemoteViews paramRemoteViews2, RemoteViews paramRemoteViews3, RemoteViews paramRemoteViews4)
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
          break label358;
        }
        bool2 = true;
        Notification.Builder localBuilder3 = localBuilder2.setOnlyAlertOnce(bool2);
        if ((0x10 & flags) == 0) {
          break label364;
        }
        bool3 = true;
        localBuilder4 = localBuilder3.setAutoCancel(bool3).setDefaults(defaults).setContentTitle(paramCharSequence1).setContentText(paramCharSequence2).setSubText(paramCharSequence4).setContentInfo(paramCharSequence3).setContentIntent(paramPendingIntent1).setDeleteIntent(deleteIntent);
        if ((0x80 & flags) == 0) {
          break label370;
        }
      }
      label358:
      label364:
      label370:
      for (boolean bool4 = true;; bool4 = false)
      {
        b = localBuilder4.setFullScreenIntent(paramPendingIntent2, bool4).setLargeIcon(paramBitmap).setNumber(paramInt1).setUsesChronometer(paramBoolean3).setPriority(paramInt4).setProgress(paramInt2, paramInt3, paramBoolean1).setLocalOnly(paramBoolean4).setGroup(paramString2).setGroupSummary(paramBoolean5).setSortKey(paramString3).setCategory(paramString1).setColor(paramInt5).setVisibility(paramInt6).setPublicVersion(paramNotification2);
        mExtras = new Bundle();
        if (paramBundle != null) {
          mExtras.putAll(paramBundle);
        }
        Iterator localIterator = paramArrayList.iterator();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          b.addPerson(str);
        }
        bool1 = false;
        break;
        bool2 = false;
        break label120;
        bool3 = false;
        break label142;
      }
      mContentView = paramRemoteViews2;
      mBigContentView = paramRemoteViews3;
      mHeadsUpContentView = paramRemoteViews4;
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
      if (mHeadsUpContentView != null) {
        headsUpContentView = mHeadsUpContentView;
      }
      return localNotification;
    }
    
    public Notification.Builder getBuilder()
    {
      return b;
    }
  }
}
