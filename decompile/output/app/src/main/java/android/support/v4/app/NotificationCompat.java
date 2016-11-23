package android.support.v4.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.v4.os.BuildCompat;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class NotificationCompat
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
  public static final String CATEGORY_REMINDER = "reminder";
  public static final String CATEGORY_SERVICE = "service";
  public static final String CATEGORY_SOCIAL = "social";
  public static final String CATEGORY_STATUS = "status";
  public static final String CATEGORY_SYSTEM = "sys";
  public static final String CATEGORY_TRANSPORT = "transport";
  @ColorInt
  public static final int COLOR_DEFAULT = 0;
  public static final int DEFAULT_ALL = -1;
  public static final int DEFAULT_LIGHTS = 4;
  public static final int DEFAULT_SOUND = 1;
  public static final int DEFAULT_VIBRATE = 2;
  public static final String EXTRA_BACKGROUND_IMAGE_URI = "android.backgroundImageUri";
  public static final String EXTRA_BIG_TEXT = "android.bigText";
  public static final String EXTRA_COMPACT_ACTIONS = "android.compactActions";
  public static final String EXTRA_CONVERSATION_TITLE = "android.conversationTitle";
  public static final String EXTRA_INFO_TEXT = "android.infoText";
  public static final String EXTRA_LARGE_ICON = "android.largeIcon";
  public static final String EXTRA_LARGE_ICON_BIG = "android.largeIcon.big";
  public static final String EXTRA_MEDIA_SESSION = "android.mediaSession";
  public static final String EXTRA_MESSAGES = "android.messages";
  public static final String EXTRA_PEOPLE = "android.people";
  public static final String EXTRA_PICTURE = "android.picture";
  public static final String EXTRA_PROGRESS = "android.progress";
  public static final String EXTRA_PROGRESS_INDETERMINATE = "android.progressIndeterminate";
  public static final String EXTRA_PROGRESS_MAX = "android.progressMax";
  public static final String EXTRA_REMOTE_INPUT_HISTORY = "android.remoteInputHistory";
  public static final String EXTRA_SELF_DISPLAY_NAME = "android.selfDisplayName";
  public static final String EXTRA_SHOW_CHRONOMETER = "android.showChronometer";
  public static final String EXTRA_SHOW_WHEN = "android.showWhen";
  public static final String EXTRA_SMALL_ICON = "android.icon";
  public static final String EXTRA_SUB_TEXT = "android.subText";
  public static final String EXTRA_SUMMARY_TEXT = "android.summaryText";
  public static final String EXTRA_TEMPLATE = "android.template";
  public static final String EXTRA_TEXT = "android.text";
  public static final String EXTRA_TEXT_LINES = "android.textLines";
  public static final String EXTRA_TITLE = "android.title";
  public static final String EXTRA_TITLE_BIG = "android.title.big";
  public static final int FLAG_AUTO_CANCEL = 16;
  public static final int FLAG_FOREGROUND_SERVICE = 64;
  public static final int FLAG_GROUP_SUMMARY = 512;
  @Deprecated
  public static final int FLAG_HIGH_PRIORITY = 128;
  public static final int FLAG_INSISTENT = 4;
  public static final int FLAG_LOCAL_ONLY = 256;
  public static final int FLAG_NO_CLEAR = 32;
  public static final int FLAG_ONGOING_EVENT = 2;
  public static final int FLAG_ONLY_ALERT_ONCE = 8;
  public static final int FLAG_SHOW_LIGHTS = 1;
  static final NotificationCompatImpl IMPL = new NotificationCompatImplBase();
  public static final int PRIORITY_DEFAULT = 0;
  public static final int PRIORITY_HIGH = 1;
  public static final int PRIORITY_LOW = -1;
  public static final int PRIORITY_MAX = 2;
  public static final int PRIORITY_MIN = -2;
  public static final int STREAM_DEFAULT = -1;
  public static final int VISIBILITY_PRIVATE = 0;
  public static final int VISIBILITY_PUBLIC = 1;
  public static final int VISIBILITY_SECRET = -1;
  
  static
  {
    if (BuildCompat.isAtLeastN())
    {
      IMPL = new NotificationCompatImplApi24();
      return;
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      IMPL = new NotificationCompatImplApi21();
      return;
    }
    if (Build.VERSION.SDK_INT >= 20)
    {
      IMPL = new NotificationCompatImplApi20();
      return;
    }
    if (Build.VERSION.SDK_INT >= 19)
    {
      IMPL = new NotificationCompatImplKitKat();
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      IMPL = new NotificationCompatImplJellybean();
      return;
    }
    if (Build.VERSION.SDK_INT >= 14)
    {
      IMPL = new NotificationCompatImplIceCreamSandwich();
      return;
    }
    if (Build.VERSION.SDK_INT >= 11)
    {
      IMPL = new NotificationCompatImplHoneycomb();
      return;
    }
  }
  
  public NotificationCompat() {}
  
  static void addActionsToBuilder(NotificationBuilderWithActions paramNotificationBuilderWithActions, ArrayList<Action> paramArrayList)
  {
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext()) {
      paramNotificationBuilderWithActions.addAction((Action)localIterator.next());
    }
  }
  
  static void addStyleToBuilderApi24(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, Style paramStyle)
  {
    if (paramStyle != null)
    {
      if ((paramStyle instanceof MessagingStyle))
      {
        MessagingStyle localMessagingStyle = (MessagingStyle)paramStyle;
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        ArrayList localArrayList3 = new ArrayList();
        ArrayList localArrayList4 = new ArrayList();
        ArrayList localArrayList5 = new ArrayList();
        Iterator localIterator = mMessages.iterator();
        while (localIterator.hasNext())
        {
          NotificationCompat.MessagingStyle.Message localMessage = (NotificationCompat.MessagingStyle.Message)localIterator.next();
          localArrayList1.add(localMessage.getText());
          localArrayList2.add(Long.valueOf(localMessage.getTimestamp()));
          localArrayList3.add(localMessage.getSender());
          localArrayList4.add(localMessage.getDataMimeType());
          localArrayList5.add(localMessage.getDataUri());
        }
        NotificationCompatApi24.addMessagingStyle(paramNotificationBuilderWithBuilderAccessor, mUserDisplayName, mConversationTitle, localArrayList1, localArrayList2, localArrayList3, localArrayList4, localArrayList5);
      }
    }
    else {
      return;
    }
    addStyleToBuilderJellybean(paramNotificationBuilderWithBuilderAccessor, paramStyle);
  }
  
  static void addStyleToBuilderJellybean(NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor, Style paramStyle)
  {
    if (paramStyle != null)
    {
      if (!(paramStyle instanceof BigTextStyle)) {
        break label42;
      }
      BigTextStyle localBigTextStyle = (BigTextStyle)paramStyle;
      NotificationCompatJellybean.addBigTextStyle(paramNotificationBuilderWithBuilderAccessor, mBigContentTitle, mSummaryTextSet, mSummaryText, mBigText);
    }
    label42:
    do
    {
      return;
      if ((paramStyle instanceof InboxStyle))
      {
        InboxStyle localInboxStyle = (InboxStyle)paramStyle;
        NotificationCompatJellybean.addInboxStyle(paramNotificationBuilderWithBuilderAccessor, mBigContentTitle, mSummaryTextSet, mSummaryText, mTexts);
        return;
      }
      if ((paramStyle instanceof BigPictureStyle))
      {
        BigPictureStyle localBigPictureStyle = (BigPictureStyle)paramStyle;
        NotificationCompatJellybean.addBigPictureStyle(paramNotificationBuilderWithBuilderAccessor, mBigContentTitle, mSummaryTextSet, mSummaryText, mPicture, mBigLargeIcon, mBigLargeIconSet);
        return;
      }
    } while (!(paramStyle instanceof MessagingStyle));
  }
  
  public static Action getAction(Notification paramNotification, int paramInt)
  {
    return IMPL.getAction(paramNotification, paramInt);
  }
  
  public static int getActionCount(Notification paramNotification)
  {
    return IMPL.getActionCount(paramNotification);
  }
  
  public static String getCategory(Notification paramNotification)
  {
    return IMPL.getCategory(paramNotification);
  }
  
  public static Bundle getExtras(Notification paramNotification)
  {
    return IMPL.getExtras(paramNotification);
  }
  
  public static String getGroup(Notification paramNotification)
  {
    return IMPL.getGroup(paramNotification);
  }
  
  public static boolean getLocalOnly(Notification paramNotification)
  {
    return IMPL.getLocalOnly(paramNotification);
  }
  
  static Notification[] getNotificationArrayFromBundle(Bundle paramBundle, String paramString)
  {
    Parcelable[] arrayOfParcelable = paramBundle.getParcelableArray(paramString);
    if (((arrayOfParcelable instanceof Notification[])) || (arrayOfParcelable == null)) {
      return (Notification[])arrayOfParcelable;
    }
    Notification[] arrayOfNotification = new Notification[arrayOfParcelable.length];
    for (int i = 0; i < arrayOfParcelable.length; i++) {
      arrayOfNotification[i] = ((Notification)arrayOfParcelable[i]);
    }
    paramBundle.putParcelableArray(paramString, arrayOfNotification);
    return arrayOfNotification;
  }
  
  public static String getSortKey(Notification paramNotification)
  {
    return IMPL.getSortKey(paramNotification);
  }
  
  public static boolean isGroupSummary(Notification paramNotification)
  {
    return IMPL.isGroupSummary(paramNotification);
  }
  
  public static class Action
    extends NotificationCompatBase.Action
  {
    public static final NotificationCompatBase.Action.Factory FACTORY = new NotificationCompatBase.Action.Factory()
    {
      public NotificationCompatBase.Action build(int paramAnonymousInt, CharSequence paramAnonymousCharSequence, PendingIntent paramAnonymousPendingIntent, Bundle paramAnonymousBundle, RemoteInputCompatBase.RemoteInput[] paramAnonymousArrayOfRemoteInput, boolean paramAnonymousBoolean)
      {
        return new NotificationCompat.Action(paramAnonymousInt, paramAnonymousCharSequence, paramAnonymousPendingIntent, paramAnonymousBundle, (RemoteInput[])paramAnonymousArrayOfRemoteInput, paramAnonymousBoolean);
      }
      
      public NotificationCompat.Action[] newArray(int paramAnonymousInt)
      {
        return new NotificationCompat.Action[paramAnonymousInt];
      }
    };
    public PendingIntent actionIntent;
    public int icon;
    private boolean mAllowGeneratedReplies = false;
    final Bundle mExtras;
    private final RemoteInput[] mRemoteInputs;
    public CharSequence title;
    
    public Action(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent)
    {
      this(paramInt, paramCharSequence, paramPendingIntent, new Bundle(), null, false);
    }
    
    Action(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent, Bundle paramBundle, RemoteInput[] paramArrayOfRemoteInput, boolean paramBoolean)
    {
      icon = paramInt;
      title = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      actionIntent = paramPendingIntent;
      if (paramBundle != null) {}
      for (;;)
      {
        mExtras = paramBundle;
        mRemoteInputs = paramArrayOfRemoteInput;
        mAllowGeneratedReplies = paramBoolean;
        return;
        paramBundle = new Bundle();
      }
    }
    
    public PendingIntent getActionIntent()
    {
      return actionIntent;
    }
    
    public boolean getAllowGeneratedReplies()
    {
      return mAllowGeneratedReplies;
    }
    
    public Bundle getExtras()
    {
      return mExtras;
    }
    
    public int getIcon()
    {
      return icon;
    }
    
    public RemoteInput[] getRemoteInputs()
    {
      return mRemoteInputs;
    }
    
    public CharSequence getTitle()
    {
      return title;
    }
    
    public static final class Builder
    {
      private boolean mAllowGeneratedReplies;
      private final Bundle mExtras;
      private final int mIcon;
      private final PendingIntent mIntent;
      private ArrayList<RemoteInput> mRemoteInputs;
      private final CharSequence mTitle;
      
      public Builder(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent)
      {
        this(paramInt, paramCharSequence, paramPendingIntent, new Bundle());
      }
      
      private Builder(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent, Bundle paramBundle)
      {
        mIcon = paramInt;
        mTitle = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
        mIntent = paramPendingIntent;
        mExtras = paramBundle;
      }
      
      public Builder(NotificationCompat.Action paramAction)
      {
        this(icon, title, actionIntent, new Bundle(mExtras));
      }
      
      public Builder addExtras(Bundle paramBundle)
      {
        if (paramBundle != null) {
          mExtras.putAll(paramBundle);
        }
        return this;
      }
      
      public Builder addRemoteInput(RemoteInput paramRemoteInput)
      {
        if (mRemoteInputs == null) {
          mRemoteInputs = new ArrayList();
        }
        mRemoteInputs.add(paramRemoteInput);
        return this;
      }
      
      public NotificationCompat.Action build()
      {
        if (mRemoteInputs != null) {}
        for (RemoteInput[] arrayOfRemoteInput = (RemoteInput[])mRemoteInputs.toArray(new RemoteInput[mRemoteInputs.size()]);; arrayOfRemoteInput = null) {
          return new NotificationCompat.Action(mIcon, mTitle, mIntent, mExtras, arrayOfRemoteInput, mAllowGeneratedReplies);
        }
      }
      
      public Builder extend(NotificationCompat.Action.Extender paramExtender)
      {
        paramExtender.extend(this);
        return this;
      }
      
      public Bundle getExtras()
      {
        return mExtras;
      }
      
      public Builder setAllowGeneratedReplies(boolean paramBoolean)
      {
        mAllowGeneratedReplies = paramBoolean;
        return this;
      }
    }
    
    public static abstract interface Extender
    {
      public abstract NotificationCompat.Action.Builder extend(NotificationCompat.Action.Builder paramBuilder);
    }
    
    public static final class WearableExtender
      implements NotificationCompat.Action.Extender
    {
      private static final int DEFAULT_FLAGS = 1;
      private static final String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
      private static final int FLAG_AVAILABLE_OFFLINE = 1;
      private static final int FLAG_HINT_DISPLAY_INLINE = 4;
      private static final int FLAG_HINT_LAUNCHES_ACTIVITY = 2;
      private static final String KEY_CANCEL_LABEL = "cancelLabel";
      private static final String KEY_CONFIRM_LABEL = "confirmLabel";
      private static final String KEY_FLAGS = "flags";
      private static final String KEY_IN_PROGRESS_LABEL = "inProgressLabel";
      private CharSequence mCancelLabel;
      private CharSequence mConfirmLabel;
      private int mFlags = 1;
      private CharSequence mInProgressLabel;
      
      public WearableExtender() {}
      
      public WearableExtender(NotificationCompat.Action paramAction)
      {
        Bundle localBundle = paramAction.getExtras().getBundle("android.wearable.EXTENSIONS");
        if (localBundle != null)
        {
          mFlags = localBundle.getInt("flags", 1);
          mInProgressLabel = localBundle.getCharSequence("inProgressLabel");
          mConfirmLabel = localBundle.getCharSequence("confirmLabel");
          mCancelLabel = localBundle.getCharSequence("cancelLabel");
        }
      }
      
      private void setFlag(int paramInt, boolean paramBoolean)
      {
        if (paramBoolean)
        {
          mFlags = (paramInt | mFlags);
          return;
        }
        mFlags &= (paramInt ^ 0xFFFFFFFF);
      }
      
      public WearableExtender clone()
      {
        WearableExtender localWearableExtender = new WearableExtender();
        mFlags = mFlags;
        mInProgressLabel = mInProgressLabel;
        mConfirmLabel = mConfirmLabel;
        mCancelLabel = mCancelLabel;
        return localWearableExtender;
      }
      
      public NotificationCompat.Action.Builder extend(NotificationCompat.Action.Builder paramBuilder)
      {
        Bundle localBundle = new Bundle();
        if (mFlags != 1) {
          localBundle.putInt("flags", mFlags);
        }
        if (mInProgressLabel != null) {
          localBundle.putCharSequence("inProgressLabel", mInProgressLabel);
        }
        if (mConfirmLabel != null) {
          localBundle.putCharSequence("confirmLabel", mConfirmLabel);
        }
        if (mCancelLabel != null) {
          localBundle.putCharSequence("cancelLabel", mCancelLabel);
        }
        paramBuilder.getExtras().putBundle("android.wearable.EXTENSIONS", localBundle);
        return paramBuilder;
      }
      
      public CharSequence getCancelLabel()
      {
        return mCancelLabel;
      }
      
      public CharSequence getConfirmLabel()
      {
        return mConfirmLabel;
      }
      
      public boolean getHintDisplayActionInline()
      {
        return (0x4 & mFlags) != 0;
      }
      
      public boolean getHintLaunchesActivity()
      {
        return (0x2 & mFlags) != 0;
      }
      
      public CharSequence getInProgressLabel()
      {
        return mInProgressLabel;
      }
      
      public boolean isAvailableOffline()
      {
        return (0x1 & mFlags) != 0;
      }
      
      public WearableExtender setAvailableOffline(boolean paramBoolean)
      {
        setFlag(1, paramBoolean);
        return this;
      }
      
      public WearableExtender setCancelLabel(CharSequence paramCharSequence)
      {
        mCancelLabel = paramCharSequence;
        return this;
      }
      
      public WearableExtender setConfirmLabel(CharSequence paramCharSequence)
      {
        mConfirmLabel = paramCharSequence;
        return this;
      }
      
      public WearableExtender setHintDisplayActionInline(boolean paramBoolean)
      {
        setFlag(4, paramBoolean);
        return this;
      }
      
      public WearableExtender setHintLaunchesActivity(boolean paramBoolean)
      {
        setFlag(2, paramBoolean);
        return this;
      }
      
      public WearableExtender setInProgressLabel(CharSequence paramCharSequence)
      {
        mInProgressLabel = paramCharSequence;
        return this;
      }
    }
  }
  
  public static class BigPictureStyle
    extends NotificationCompat.Style
  {
    Bitmap mBigLargeIcon;
    boolean mBigLargeIconSet;
    Bitmap mPicture;
    
    public BigPictureStyle() {}
    
    public BigPictureStyle(NotificationCompat.Builder paramBuilder)
    {
      setBuilder(paramBuilder);
    }
    
    public BigPictureStyle bigLargeIcon(Bitmap paramBitmap)
    {
      mBigLargeIcon = paramBitmap;
      mBigLargeIconSet = true;
      return this;
    }
    
    public BigPictureStyle bigPicture(Bitmap paramBitmap)
    {
      mPicture = paramBitmap;
      return this;
    }
    
    public BigPictureStyle setBigContentTitle(CharSequence paramCharSequence)
    {
      mBigContentTitle = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      return this;
    }
    
    public BigPictureStyle setSummaryText(CharSequence paramCharSequence)
    {
      mSummaryText = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      mSummaryTextSet = true;
      return this;
    }
  }
  
  public static class BigTextStyle
    extends NotificationCompat.Style
  {
    CharSequence mBigText;
    
    public BigTextStyle() {}
    
    public BigTextStyle(NotificationCompat.Builder paramBuilder)
    {
      setBuilder(paramBuilder);
    }
    
    public BigTextStyle bigText(CharSequence paramCharSequence)
    {
      mBigText = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      return this;
    }
    
    public BigTextStyle setBigContentTitle(CharSequence paramCharSequence)
    {
      mBigContentTitle = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      return this;
    }
    
    public BigTextStyle setSummaryText(CharSequence paramCharSequence)
    {
      mSummaryText = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      mSummaryTextSet = true;
      return this;
    }
  }
  
  public static class Builder
  {
    private static final int MAX_CHARSEQUENCE_LENGTH = 5120;
    public ArrayList<NotificationCompat.Action> mActions = new ArrayList();
    RemoteViews mBigContentView;
    String mCategory;
    int mColor = 0;
    public CharSequence mContentInfo;
    PendingIntent mContentIntent;
    public CharSequence mContentText;
    public CharSequence mContentTitle;
    RemoteViews mContentView;
    public Context mContext;
    Bundle mExtras;
    PendingIntent mFullScreenIntent;
    String mGroupKey;
    boolean mGroupSummary;
    RemoteViews mHeadsUpContentView;
    public Bitmap mLargeIcon;
    boolean mLocalOnly = false;
    public Notification mNotification = new Notification();
    public int mNumber;
    public ArrayList<String> mPeople;
    int mPriority;
    int mProgress;
    boolean mProgressIndeterminate;
    int mProgressMax;
    Notification mPublicVersion;
    public CharSequence[] mRemoteInputHistory;
    boolean mShowWhen = true;
    String mSortKey;
    public NotificationCompat.Style mStyle;
    public CharSequence mSubText;
    RemoteViews mTickerView;
    public boolean mUseChronometer;
    int mVisibility = 0;
    
    public Builder(Context paramContext)
    {
      mContext = paramContext;
      mNotification.when = System.currentTimeMillis();
      mNotification.audioStreamType = -1;
      mPriority = 0;
      mPeople = new ArrayList();
    }
    
    protected static CharSequence limitCharSequenceLength(CharSequence paramCharSequence)
    {
      if (paramCharSequence == null) {}
      while (paramCharSequence.length() <= 5120) {
        return paramCharSequence;
      }
      return paramCharSequence.subSequence(0, 5120);
    }
    
    private void setFlag(int paramInt, boolean paramBoolean)
    {
      if (paramBoolean)
      {
        Notification localNotification2 = mNotification;
        flags = (paramInt | flags);
        return;
      }
      Notification localNotification1 = mNotification;
      flags &= (paramInt ^ 0xFFFFFFFF);
    }
    
    public Builder addAction(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent)
    {
      mActions.add(new NotificationCompat.Action(paramInt, paramCharSequence, paramPendingIntent));
      return this;
    }
    
    public Builder addAction(NotificationCompat.Action paramAction)
    {
      mActions.add(paramAction);
      return this;
    }
    
    public Builder addExtras(Bundle paramBundle)
    {
      if (paramBundle != null)
      {
        if (mExtras == null) {
          mExtras = new Bundle(paramBundle);
        }
      }
      else {
        return this;
      }
      mExtras.putAll(paramBundle);
      return this;
    }
    
    public Builder addPerson(String paramString)
    {
      mPeople.add(paramString);
      return this;
    }
    
    public Notification build()
    {
      return NotificationCompat.IMPL.build(this, getExtender());
    }
    
    public Builder extend(NotificationCompat.Extender paramExtender)
    {
      paramExtender.extend(this);
      return this;
    }
    
    protected NotificationCompat.BuilderExtender getExtender()
    {
      return new NotificationCompat.BuilderExtender();
    }
    
    public Bundle getExtras()
    {
      if (mExtras == null) {
        mExtras = new Bundle();
      }
      return mExtras;
    }
    
    @Deprecated
    public Notification getNotification()
    {
      return build();
    }
    
    public Builder setAutoCancel(boolean paramBoolean)
    {
      setFlag(16, paramBoolean);
      return this;
    }
    
    public Builder setCategory(String paramString)
    {
      mCategory = paramString;
      return this;
    }
    
    public Builder setColor(@ColorInt int paramInt)
    {
      mColor = paramInt;
      return this;
    }
    
    public Builder setContent(RemoteViews paramRemoteViews)
    {
      mNotification.contentView = paramRemoteViews;
      return this;
    }
    
    public Builder setContentInfo(CharSequence paramCharSequence)
    {
      mContentInfo = limitCharSequenceLength(paramCharSequence);
      return this;
    }
    
    public Builder setContentIntent(PendingIntent paramPendingIntent)
    {
      mContentIntent = paramPendingIntent;
      return this;
    }
    
    public Builder setContentText(CharSequence paramCharSequence)
    {
      mContentText = limitCharSequenceLength(paramCharSequence);
      return this;
    }
    
    public Builder setContentTitle(CharSequence paramCharSequence)
    {
      mContentTitle = limitCharSequenceLength(paramCharSequence);
      return this;
    }
    
    public Builder setCustomBigContentView(RemoteViews paramRemoteViews)
    {
      mBigContentView = paramRemoteViews;
      return this;
    }
    
    public Builder setCustomContentView(RemoteViews paramRemoteViews)
    {
      mContentView = paramRemoteViews;
      return this;
    }
    
    public Builder setCustomHeadsUpContentView(RemoteViews paramRemoteViews)
    {
      mHeadsUpContentView = paramRemoteViews;
      return this;
    }
    
    public Builder setDefaults(int paramInt)
    {
      mNotification.defaults = paramInt;
      if ((paramInt & 0x4) != 0)
      {
        Notification localNotification = mNotification;
        flags = (0x1 | flags);
      }
      return this;
    }
    
    public Builder setDeleteIntent(PendingIntent paramPendingIntent)
    {
      mNotification.deleteIntent = paramPendingIntent;
      return this;
    }
    
    public Builder setExtras(Bundle paramBundle)
    {
      mExtras = paramBundle;
      return this;
    }
    
    public Builder setFullScreenIntent(PendingIntent paramPendingIntent, boolean paramBoolean)
    {
      mFullScreenIntent = paramPendingIntent;
      setFlag(128, paramBoolean);
      return this;
    }
    
    public Builder setGroup(String paramString)
    {
      mGroupKey = paramString;
      return this;
    }
    
    public Builder setGroupSummary(boolean paramBoolean)
    {
      mGroupSummary = paramBoolean;
      return this;
    }
    
    public Builder setLargeIcon(Bitmap paramBitmap)
    {
      mLargeIcon = paramBitmap;
      return this;
    }
    
    public Builder setLights(@ColorInt int paramInt1, int paramInt2, int paramInt3)
    {
      int i = 1;
      mNotification.ledARGB = paramInt1;
      mNotification.ledOnMS = paramInt2;
      mNotification.ledOffMS = paramInt3;
      int j;
      Notification localNotification;
      int k;
      if ((mNotification.ledOnMS != 0) && (mNotification.ledOffMS != 0))
      {
        j = i;
        localNotification = mNotification;
        k = 0xFFFFFFFE & mNotification.flags;
        if (j == 0) {
          break label92;
        }
      }
      for (;;)
      {
        flags = (i | k);
        return this;
        j = 0;
        break;
        label92:
        i = 0;
      }
    }
    
    public Builder setLocalOnly(boolean paramBoolean)
    {
      mLocalOnly = paramBoolean;
      return this;
    }
    
    public Builder setNumber(int paramInt)
    {
      mNumber = paramInt;
      return this;
    }
    
    public Builder setOngoing(boolean paramBoolean)
    {
      setFlag(2, paramBoolean);
      return this;
    }
    
    public Builder setOnlyAlertOnce(boolean paramBoolean)
    {
      setFlag(8, paramBoolean);
      return this;
    }
    
    public Builder setPriority(int paramInt)
    {
      mPriority = paramInt;
      return this;
    }
    
    public Builder setProgress(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      mProgressMax = paramInt1;
      mProgress = paramInt2;
      mProgressIndeterminate = paramBoolean;
      return this;
    }
    
    public Builder setPublicVersion(Notification paramNotification)
    {
      mPublicVersion = paramNotification;
      return this;
    }
    
    public Builder setRemoteInputHistory(CharSequence[] paramArrayOfCharSequence)
    {
      mRemoteInputHistory = paramArrayOfCharSequence;
      return this;
    }
    
    public Builder setShowWhen(boolean paramBoolean)
    {
      mShowWhen = paramBoolean;
      return this;
    }
    
    public Builder setSmallIcon(int paramInt)
    {
      mNotification.icon = paramInt;
      return this;
    }
    
    public Builder setSmallIcon(int paramInt1, int paramInt2)
    {
      mNotification.icon = paramInt1;
      mNotification.iconLevel = paramInt2;
      return this;
    }
    
    public Builder setSortKey(String paramString)
    {
      mSortKey = paramString;
      return this;
    }
    
    public Builder setSound(Uri paramUri)
    {
      mNotification.sound = paramUri;
      mNotification.audioStreamType = -1;
      return this;
    }
    
    public Builder setSound(Uri paramUri, int paramInt)
    {
      mNotification.sound = paramUri;
      mNotification.audioStreamType = paramInt;
      return this;
    }
    
    public Builder setStyle(NotificationCompat.Style paramStyle)
    {
      if (mStyle != paramStyle)
      {
        mStyle = paramStyle;
        if (mStyle != null) {
          mStyle.setBuilder(this);
        }
      }
      return this;
    }
    
    public Builder setSubText(CharSequence paramCharSequence)
    {
      mSubText = limitCharSequenceLength(paramCharSequence);
      return this;
    }
    
    public Builder setTicker(CharSequence paramCharSequence)
    {
      mNotification.tickerText = limitCharSequenceLength(paramCharSequence);
      return this;
    }
    
    public Builder setTicker(CharSequence paramCharSequence, RemoteViews paramRemoteViews)
    {
      mNotification.tickerText = limitCharSequenceLength(paramCharSequence);
      mTickerView = paramRemoteViews;
      return this;
    }
    
    public Builder setUsesChronometer(boolean paramBoolean)
    {
      mUseChronometer = paramBoolean;
      return this;
    }
    
    public Builder setVibrate(long[] paramArrayOfLong)
    {
      mNotification.vibrate = paramArrayOfLong;
      return this;
    }
    
    public Builder setVisibility(int paramInt)
    {
      mVisibility = paramInt;
      return this;
    }
    
    public Builder setWhen(long paramLong)
    {
      mNotification.when = paramLong;
      return this;
    }
  }
  
  protected static class BuilderExtender
  {
    protected BuilderExtender() {}
    
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationBuilderWithBuilderAccessor paramNotificationBuilderWithBuilderAccessor)
    {
      return paramNotificationBuilderWithBuilderAccessor.build();
    }
  }
  
  public static final class CarExtender
    implements NotificationCompat.Extender
  {
    private static final String EXTRA_CAR_EXTENDER = "android.car.EXTENSIONS";
    private static final String EXTRA_COLOR = "app_color";
    private static final String EXTRA_CONVERSATION = "car_conversation";
    private static final String EXTRA_LARGE_ICON = "large_icon";
    private static final String TAG = "CarExtender";
    private int mColor = 0;
    private Bitmap mLargeIcon;
    private UnreadConversation mUnreadConversation;
    
    public CarExtender() {}
    
    public CarExtender(Notification paramNotification)
    {
      if (Build.VERSION.SDK_INT < 21) {}
      for (;;)
      {
        return;
        if (NotificationCompat.getExtras(paramNotification) == null) {}
        for (Bundle localBundle1 = null; localBundle1 != null; localBundle1 = NotificationCompat.getExtras(paramNotification).getBundle("android.car.EXTENSIONS"))
        {
          mLargeIcon = ((Bitmap)localBundle1.getParcelable("large_icon"));
          mColor = localBundle1.getInt("app_color", 0);
          Bundle localBundle2 = localBundle1.getBundle("car_conversation");
          mUnreadConversation = ((UnreadConversation)NotificationCompat.IMPL.getUnreadConversationFromBundle(localBundle2, UnreadConversation.FACTORY, RemoteInput.FACTORY));
          return;
        }
      }
    }
    
    public NotificationCompat.Builder extend(NotificationCompat.Builder paramBuilder)
    {
      if (Build.VERSION.SDK_INT < 21) {
        return paramBuilder;
      }
      Bundle localBundle = new Bundle();
      if (mLargeIcon != null) {
        localBundle.putParcelable("large_icon", mLargeIcon);
      }
      if (mColor != 0) {
        localBundle.putInt("app_color", mColor);
      }
      if (mUnreadConversation != null) {
        localBundle.putBundle("car_conversation", NotificationCompat.IMPL.getBundleForUnreadConversation(mUnreadConversation));
      }
      paramBuilder.getExtras().putBundle("android.car.EXTENSIONS", localBundle);
      return paramBuilder;
    }
    
    @ColorInt
    public int getColor()
    {
      return mColor;
    }
    
    public Bitmap getLargeIcon()
    {
      return mLargeIcon;
    }
    
    public UnreadConversation getUnreadConversation()
    {
      return mUnreadConversation;
    }
    
    public CarExtender setColor(@ColorInt int paramInt)
    {
      mColor = paramInt;
      return this;
    }
    
    public CarExtender setLargeIcon(Bitmap paramBitmap)
    {
      mLargeIcon = paramBitmap;
      return this;
    }
    
    public CarExtender setUnreadConversation(UnreadConversation paramUnreadConversation)
    {
      mUnreadConversation = paramUnreadConversation;
      return this;
    }
    
    public static class UnreadConversation
      extends NotificationCompatBase.UnreadConversation
    {
      static final NotificationCompatBase.UnreadConversation.Factory FACTORY = new NotificationCompatBase.UnreadConversation.Factory()
      {
        public NotificationCompat.CarExtender.UnreadConversation build(String[] paramAnonymousArrayOfString1, RemoteInputCompatBase.RemoteInput paramAnonymousRemoteInput, PendingIntent paramAnonymousPendingIntent1, PendingIntent paramAnonymousPendingIntent2, String[] paramAnonymousArrayOfString2, long paramAnonymousLong)
        {
          return new NotificationCompat.CarExtender.UnreadConversation(paramAnonymousArrayOfString1, (RemoteInput)paramAnonymousRemoteInput, paramAnonymousPendingIntent1, paramAnonymousPendingIntent2, paramAnonymousArrayOfString2, paramAnonymousLong);
        }
      };
      private final long mLatestTimestamp;
      private final String[] mMessages;
      private final String[] mParticipants;
      private final PendingIntent mReadPendingIntent;
      private final RemoteInput mRemoteInput;
      private final PendingIntent mReplyPendingIntent;
      
      UnreadConversation(String[] paramArrayOfString1, RemoteInput paramRemoteInput, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2, String[] paramArrayOfString2, long paramLong)
      {
        mMessages = paramArrayOfString1;
        mRemoteInput = paramRemoteInput;
        mReadPendingIntent = paramPendingIntent2;
        mReplyPendingIntent = paramPendingIntent1;
        mParticipants = paramArrayOfString2;
        mLatestTimestamp = paramLong;
      }
      
      public long getLatestTimestamp()
      {
        return mLatestTimestamp;
      }
      
      public String[] getMessages()
      {
        return mMessages;
      }
      
      public String getParticipant()
      {
        if (mParticipants.length > 0) {
          return mParticipants[0];
        }
        return null;
      }
      
      public String[] getParticipants()
      {
        return mParticipants;
      }
      
      public PendingIntent getReadPendingIntent()
      {
        return mReadPendingIntent;
      }
      
      public RemoteInput getRemoteInput()
      {
        return mRemoteInput;
      }
      
      public PendingIntent getReplyPendingIntent()
      {
        return mReplyPendingIntent;
      }
      
      public static class Builder
      {
        private long mLatestTimestamp;
        private final List<String> mMessages = new ArrayList();
        private final String mParticipant;
        private PendingIntent mReadPendingIntent;
        private RemoteInput mRemoteInput;
        private PendingIntent mReplyPendingIntent;
        
        public Builder(String paramString)
        {
          mParticipant = paramString;
        }
        
        public Builder addMessage(String paramString)
        {
          mMessages.add(paramString);
          return this;
        }
        
        public NotificationCompat.CarExtender.UnreadConversation build()
        {
          String[] arrayOfString1 = (String[])mMessages.toArray(new String[mMessages.size()]);
          String[] arrayOfString2 = new String[1];
          arrayOfString2[0] = mParticipant;
          return new NotificationCompat.CarExtender.UnreadConversation(arrayOfString1, mRemoteInput, mReplyPendingIntent, mReadPendingIntent, arrayOfString2, mLatestTimestamp);
        }
        
        public Builder setLatestTimestamp(long paramLong)
        {
          mLatestTimestamp = paramLong;
          return this;
        }
        
        public Builder setReadPendingIntent(PendingIntent paramPendingIntent)
        {
          mReadPendingIntent = paramPendingIntent;
          return this;
        }
        
        public Builder setReplyAction(PendingIntent paramPendingIntent, RemoteInput paramRemoteInput)
        {
          mRemoteInput = paramRemoteInput;
          mReplyPendingIntent = paramPendingIntent;
          return this;
        }
      }
    }
  }
  
  public static abstract interface Extender
  {
    public abstract NotificationCompat.Builder extend(NotificationCompat.Builder paramBuilder);
  }
  
  public static class InboxStyle
    extends NotificationCompat.Style
  {
    ArrayList<CharSequence> mTexts = new ArrayList();
    
    public InboxStyle() {}
    
    public InboxStyle(NotificationCompat.Builder paramBuilder)
    {
      setBuilder(paramBuilder);
    }
    
    public InboxStyle addLine(CharSequence paramCharSequence)
    {
      mTexts.add(NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence));
      return this;
    }
    
    public InboxStyle setBigContentTitle(CharSequence paramCharSequence)
    {
      mBigContentTitle = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      return this;
    }
    
    public InboxStyle setSummaryText(CharSequence paramCharSequence)
    {
      mSummaryText = NotificationCompat.Builder.limitCharSequenceLength(paramCharSequence);
      mSummaryTextSet = true;
      return this;
    }
  }
  
  public static class MessagingStyle
    extends NotificationCompat.Style
  {
    public static final int MAXIMUM_RETAINED_MESSAGES = 25;
    CharSequence mConversationTitle;
    List<Message> mMessages = new ArrayList();
    CharSequence mUserDisplayName;
    
    MessagingStyle() {}
    
    public MessagingStyle(CharSequence paramCharSequence)
    {
      mUserDisplayName = paramCharSequence;
    }
    
    public static MessagingStyle extractMessagingStyleFromNotification(Notification paramNotification)
    {
      Bundle localBundle = NotificationCompat.IMPL.getExtras(paramNotification);
      if (!localBundle.containsKey("android.selfDisplayName")) {
        return null;
      }
      try
      {
        MessagingStyle localMessagingStyle = new MessagingStyle();
        localMessagingStyle.restoreFromCompatExtras(localBundle);
        return localMessagingStyle;
      }
      catch (ClassCastException localClassCastException) {}
      return null;
    }
    
    public void addCompatExtras(Bundle paramBundle)
    {
      super.addCompatExtras(paramBundle);
      if (mUserDisplayName != null) {
        paramBundle.putCharSequence("android.selfDisplayName", mUserDisplayName);
      }
      if (mConversationTitle != null) {
        paramBundle.putCharSequence("android.conversationTitle", mConversationTitle);
      }
      if (!mMessages.isEmpty()) {
        paramBundle.putParcelableArray("android.messages", Message.getBundleArrayForMessages(mMessages));
      }
    }
    
    public MessagingStyle addMessage(Message paramMessage)
    {
      mMessages.add(paramMessage);
      if (mMessages.size() > 25) {
        mMessages.remove(0);
      }
      return this;
    }
    
    public MessagingStyle addMessage(CharSequence paramCharSequence1, long paramLong, CharSequence paramCharSequence2)
    {
      mMessages.add(new Message(paramCharSequence1, paramLong, paramCharSequence2));
      if (mMessages.size() > 25) {
        mMessages.remove(0);
      }
      return this;
    }
    
    public CharSequence getConversationTitle()
    {
      return mConversationTitle;
    }
    
    public List<Message> getMessages()
    {
      return mMessages;
    }
    
    public CharSequence getUserDisplayName()
    {
      return mUserDisplayName;
    }
    
    protected void restoreFromCompatExtras(Bundle paramBundle)
    {
      mMessages.clear();
      mUserDisplayName = paramBundle.getString("android.selfDisplayName");
      mConversationTitle = paramBundle.getString("android.conversationTitle");
      Parcelable[] arrayOfParcelable = paramBundle.getParcelableArray("android.messages");
      if (arrayOfParcelable != null) {
        mMessages = Message.getMessagesFromBundleArray(arrayOfParcelable);
      }
    }
    
    public MessagingStyle setConversationTitle(CharSequence paramCharSequence)
    {
      mConversationTitle = paramCharSequence;
      return this;
    }
    
    public static final class Message
    {
      static final String KEY_DATA_MIME_TYPE = "type";
      static final String KEY_DATA_URI = "uri";
      static final String KEY_SENDER = "sender";
      static final String KEY_TEXT = "text";
      static final String KEY_TIMESTAMP = "time";
      private String mDataMimeType;
      private Uri mDataUri;
      private final CharSequence mSender;
      private final CharSequence mText;
      private final long mTimestamp;
      
      public Message(CharSequence paramCharSequence1, long paramLong, CharSequence paramCharSequence2)
      {
        mText = paramCharSequence1;
        mTimestamp = paramLong;
        mSender = paramCharSequence2;
      }
      
      static Bundle[] getBundleArrayForMessages(List<Message> paramList)
      {
        Bundle[] arrayOfBundle = new Bundle[paramList.size()];
        int i = paramList.size();
        for (int j = 0; j < i; j++) {
          arrayOfBundle[j] = ((Message)paramList.get(j)).toBundle();
        }
        return arrayOfBundle;
      }
      
      static Message getMessageFromBundle(Bundle paramBundle)
      {
        try
        {
          if ((paramBundle.containsKey("text")) && (paramBundle.containsKey("time")))
          {
            localMessage = new Message(paramBundle.getCharSequence("text"), paramBundle.getLong("time"), paramBundle.getCharSequence("sender"));
            if ((!paramBundle.containsKey("type")) || (!paramBundle.containsKey("uri"))) {
              return localMessage;
            }
            localMessage.setData(paramBundle.getString("type"), (Uri)paramBundle.getParcelable("uri"));
            return localMessage;
          }
        }
        catch (ClassCastException localClassCastException)
        {
          return null;
        }
        Message localMessage = null;
        return localMessage;
      }
      
      static List<Message> getMessagesFromBundleArray(Parcelable[] paramArrayOfParcelable)
      {
        ArrayList localArrayList = new ArrayList(paramArrayOfParcelable.length);
        for (int i = 0; i < paramArrayOfParcelable.length; i++) {
          if ((paramArrayOfParcelable[i] instanceof Bundle))
          {
            Message localMessage = getMessageFromBundle((Bundle)paramArrayOfParcelable[i]);
            if (localMessage != null) {
              localArrayList.add(localMessage);
            }
          }
        }
        return localArrayList;
      }
      
      private Bundle toBundle()
      {
        Bundle localBundle = new Bundle();
        if (mText != null) {
          localBundle.putCharSequence("text", mText);
        }
        localBundle.putLong("time", mTimestamp);
        if (mSender != null) {
          localBundle.putCharSequence("sender", mSender);
        }
        if (mDataMimeType != null) {
          localBundle.putString("type", mDataMimeType);
        }
        if (mDataUri != null) {
          localBundle.putParcelable("uri", mDataUri);
        }
        return localBundle;
      }
      
      public String getDataMimeType()
      {
        return mDataMimeType;
      }
      
      public Uri getDataUri()
      {
        return mDataUri;
      }
      
      public CharSequence getSender()
      {
        return mSender;
      }
      
      public CharSequence getText()
      {
        return mText;
      }
      
      public long getTimestamp()
      {
        return mTimestamp;
      }
      
      public Message setData(String paramString, Uri paramUri)
      {
        mDataMimeType = paramString;
        mDataUri = paramUri;
        return this;
      }
    }
  }
  
  static abstract interface NotificationCompatImpl
  {
    public abstract Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender);
    
    public abstract NotificationCompat.Action getAction(Notification paramNotification, int paramInt);
    
    public abstract int getActionCount(Notification paramNotification);
    
    public abstract NotificationCompat.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList);
    
    public abstract Bundle getBundleForUnreadConversation(NotificationCompatBase.UnreadConversation paramUnreadConversation);
    
    public abstract String getCategory(Notification paramNotification);
    
    public abstract Bundle getExtras(Notification paramNotification);
    
    public abstract String getGroup(Notification paramNotification);
    
    public abstract boolean getLocalOnly(Notification paramNotification);
    
    public abstract ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompat.Action[] paramArrayOfAction);
    
    public abstract String getSortKey(Notification paramNotification);
    
    public abstract NotificationCompatBase.UnreadConversation getUnreadConversationFromBundle(Bundle paramBundle, NotificationCompatBase.UnreadConversation.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1);
    
    public abstract boolean isGroupSummary(Notification paramNotification);
  }
  
  static class NotificationCompatImplApi20
    extends NotificationCompat.NotificationCompatImplKitKat
  {
    NotificationCompatImplApi20() {}
    
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      NotificationCompatApi20.Builder localBuilder = new NotificationCompatApi20.Builder(mContext, mNotification, mContentTitle, mContentText, mContentInfo, mTickerView, mNumber, mContentIntent, mFullScreenIntent, mLargeIcon, mProgressMax, mProgress, mProgressIndeterminate, mShowWhen, mUseChronometer, mPriority, mSubText, mLocalOnly, mPeople, mExtras, mGroupKey, mGroupSummary, mSortKey, mContentView, mBigContentView);
      NotificationCompat.addActionsToBuilder(localBuilder, mActions);
      NotificationCompat.addStyleToBuilderJellybean(localBuilder, mStyle);
      Notification localNotification = paramBuilderExtender.build(paramBuilder, localBuilder);
      if (mStyle != null) {
        mStyle.addCompatExtras(getExtras(localNotification));
      }
      return localNotification;
    }
    
    public NotificationCompat.Action getAction(Notification paramNotification, int paramInt)
    {
      return (NotificationCompat.Action)NotificationCompatApi20.getAction(paramNotification, paramInt, NotificationCompat.Action.FACTORY, RemoteInput.FACTORY);
    }
    
    public NotificationCompat.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList)
    {
      return (NotificationCompat.Action[])NotificationCompatApi20.getActionsFromParcelableArrayList(paramArrayList, NotificationCompat.Action.FACTORY, RemoteInput.FACTORY);
    }
    
    public String getGroup(Notification paramNotification)
    {
      return NotificationCompatApi20.getGroup(paramNotification);
    }
    
    public boolean getLocalOnly(Notification paramNotification)
    {
      return NotificationCompatApi20.getLocalOnly(paramNotification);
    }
    
    public ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompat.Action[] paramArrayOfAction)
    {
      return NotificationCompatApi20.getParcelableArrayListForActions(paramArrayOfAction);
    }
    
    public String getSortKey(Notification paramNotification)
    {
      return NotificationCompatApi20.getSortKey(paramNotification);
    }
    
    public boolean isGroupSummary(Notification paramNotification)
    {
      return NotificationCompatApi20.isGroupSummary(paramNotification);
    }
  }
  
  static class NotificationCompatImplApi21
    extends NotificationCompat.NotificationCompatImplApi20
  {
    NotificationCompatImplApi21() {}
    
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      NotificationCompatApi21.Builder localBuilder = new NotificationCompatApi21.Builder(mContext, mNotification, mContentTitle, mContentText, mContentInfo, mTickerView, mNumber, mContentIntent, mFullScreenIntent, mLargeIcon, mProgressMax, mProgress, mProgressIndeterminate, mShowWhen, mUseChronometer, mPriority, mSubText, mLocalOnly, mCategory, mPeople, mExtras, mColor, mVisibility, mPublicVersion, mGroupKey, mGroupSummary, mSortKey, mContentView, mBigContentView, mHeadsUpContentView);
      NotificationCompat.addActionsToBuilder(localBuilder, mActions);
      NotificationCompat.addStyleToBuilderJellybean(localBuilder, mStyle);
      Notification localNotification = paramBuilderExtender.build(paramBuilder, localBuilder);
      if (mStyle != null) {
        mStyle.addCompatExtras(getExtras(localNotification));
      }
      return localNotification;
    }
    
    public Bundle getBundleForUnreadConversation(NotificationCompatBase.UnreadConversation paramUnreadConversation)
    {
      return NotificationCompatApi21.getBundleForUnreadConversation(paramUnreadConversation);
    }
    
    public String getCategory(Notification paramNotification)
    {
      return NotificationCompatApi21.getCategory(paramNotification);
    }
    
    public NotificationCompatBase.UnreadConversation getUnreadConversationFromBundle(Bundle paramBundle, NotificationCompatBase.UnreadConversation.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
    {
      return NotificationCompatApi21.getUnreadConversationFromBundle(paramBundle, paramFactory, paramFactory1);
    }
  }
  
  static class NotificationCompatImplApi24
    extends NotificationCompat.NotificationCompatImplApi21
  {
    NotificationCompatImplApi24() {}
    
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      NotificationCompatApi24.Builder localBuilder = new NotificationCompatApi24.Builder(mContext, mNotification, mContentTitle, mContentText, mContentInfo, mTickerView, mNumber, mContentIntent, mFullScreenIntent, mLargeIcon, mProgressMax, mProgress, mProgressIndeterminate, mShowWhen, mUseChronometer, mPriority, mSubText, mLocalOnly, mCategory, mPeople, mExtras, mColor, mVisibility, mPublicVersion, mGroupKey, mGroupSummary, mSortKey, mRemoteInputHistory, mContentView, mBigContentView, mHeadsUpContentView);
      NotificationCompat.addActionsToBuilder(localBuilder, mActions);
      NotificationCompat.addStyleToBuilderApi24(localBuilder, mStyle);
      Notification localNotification = paramBuilderExtender.build(paramBuilder, localBuilder);
      if (mStyle != null) {
        mStyle.addCompatExtras(getExtras(localNotification));
      }
      return localNotification;
    }
  }
  
  static class NotificationCompatImplBase
    implements NotificationCompat.NotificationCompatImpl
  {
    NotificationCompatImplBase() {}
    
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      Notification localNotification = NotificationCompatBase.add(mNotification, mContext, mContentTitle, mContentText, mContentIntent, mFullScreenIntent);
      if (mPriority > 0) {
        flags = (0x80 | flags);
      }
      if (mContentView != null) {
        contentView = mContentView;
      }
      return localNotification;
    }
    
    public NotificationCompat.Action getAction(Notification paramNotification, int paramInt)
    {
      return null;
    }
    
    public int getActionCount(Notification paramNotification)
    {
      return 0;
    }
    
    public NotificationCompat.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList)
    {
      return null;
    }
    
    public Bundle getBundleForUnreadConversation(NotificationCompatBase.UnreadConversation paramUnreadConversation)
    {
      return null;
    }
    
    public String getCategory(Notification paramNotification)
    {
      return null;
    }
    
    public Bundle getExtras(Notification paramNotification)
    {
      return null;
    }
    
    public String getGroup(Notification paramNotification)
    {
      return null;
    }
    
    public boolean getLocalOnly(Notification paramNotification)
    {
      return false;
    }
    
    public ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompat.Action[] paramArrayOfAction)
    {
      return null;
    }
    
    public String getSortKey(Notification paramNotification)
    {
      return null;
    }
    
    public NotificationCompatBase.UnreadConversation getUnreadConversationFromBundle(Bundle paramBundle, NotificationCompatBase.UnreadConversation.Factory paramFactory, RemoteInputCompatBase.RemoteInput.Factory paramFactory1)
    {
      return null;
    }
    
    public boolean isGroupSummary(Notification paramNotification)
    {
      return false;
    }
  }
  
  static class NotificationCompatImplHoneycomb
    extends NotificationCompat.NotificationCompatImplBase
  {
    NotificationCompatImplHoneycomb() {}
    
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      Notification localNotification = NotificationCompatHoneycomb.add(mContext, mNotification, mContentTitle, mContentText, mContentInfo, mTickerView, mNumber, mContentIntent, mFullScreenIntent, mLargeIcon);
      if (mContentView != null) {
        contentView = mContentView;
      }
      return localNotification;
    }
  }
  
  static class NotificationCompatImplIceCreamSandwich
    extends NotificationCompat.NotificationCompatImplBase
  {
    NotificationCompatImplIceCreamSandwich() {}
    
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      Notification localNotification = paramBuilderExtender.build(paramBuilder, new NotificationCompatIceCreamSandwich.Builder(mContext, mNotification, mContentTitle, mContentText, mContentInfo, mTickerView, mNumber, mContentIntent, mFullScreenIntent, mLargeIcon, mProgressMax, mProgress, mProgressIndeterminate));
      if (mContentView != null) {
        contentView = mContentView;
      }
      return localNotification;
    }
  }
  
  static class NotificationCompatImplJellybean
    extends NotificationCompat.NotificationCompatImplBase
  {
    NotificationCompatImplJellybean() {}
    
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      NotificationCompatJellybean.Builder localBuilder = new NotificationCompatJellybean.Builder(mContext, mNotification, mContentTitle, mContentText, mContentInfo, mTickerView, mNumber, mContentIntent, mFullScreenIntent, mLargeIcon, mProgressMax, mProgress, mProgressIndeterminate, mUseChronometer, mPriority, mSubText, mLocalOnly, mExtras, mGroupKey, mGroupSummary, mSortKey, mContentView, mBigContentView);
      NotificationCompat.addActionsToBuilder(localBuilder, mActions);
      NotificationCompat.addStyleToBuilderJellybean(localBuilder, mStyle);
      Notification localNotification = paramBuilderExtender.build(paramBuilder, localBuilder);
      if (mStyle != null) {
        mStyle.addCompatExtras(getExtras(localNotification));
      }
      return localNotification;
    }
    
    public NotificationCompat.Action getAction(Notification paramNotification, int paramInt)
    {
      return (NotificationCompat.Action)NotificationCompatJellybean.getAction(paramNotification, paramInt, NotificationCompat.Action.FACTORY, RemoteInput.FACTORY);
    }
    
    public int getActionCount(Notification paramNotification)
    {
      return NotificationCompatJellybean.getActionCount(paramNotification);
    }
    
    public NotificationCompat.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> paramArrayList)
    {
      return (NotificationCompat.Action[])NotificationCompatJellybean.getActionsFromParcelableArrayList(paramArrayList, NotificationCompat.Action.FACTORY, RemoteInput.FACTORY);
    }
    
    public Bundle getExtras(Notification paramNotification)
    {
      return NotificationCompatJellybean.getExtras(paramNotification);
    }
    
    public String getGroup(Notification paramNotification)
    {
      return NotificationCompatJellybean.getGroup(paramNotification);
    }
    
    public boolean getLocalOnly(Notification paramNotification)
    {
      return NotificationCompatJellybean.getLocalOnly(paramNotification);
    }
    
    public ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompat.Action[] paramArrayOfAction)
    {
      return NotificationCompatJellybean.getParcelableArrayListForActions(paramArrayOfAction);
    }
    
    public String getSortKey(Notification paramNotification)
    {
      return NotificationCompatJellybean.getSortKey(paramNotification);
    }
    
    public boolean isGroupSummary(Notification paramNotification)
    {
      return NotificationCompatJellybean.isGroupSummary(paramNotification);
    }
  }
  
  static class NotificationCompatImplKitKat
    extends NotificationCompat.NotificationCompatImplJellybean
  {
    NotificationCompatImplKitKat() {}
    
    public Notification build(NotificationCompat.Builder paramBuilder, NotificationCompat.BuilderExtender paramBuilderExtender)
    {
      NotificationCompatKitKat.Builder localBuilder = new NotificationCompatKitKat.Builder(mContext, mNotification, mContentTitle, mContentText, mContentInfo, mTickerView, mNumber, mContentIntent, mFullScreenIntent, mLargeIcon, mProgressMax, mProgress, mProgressIndeterminate, mShowWhen, mUseChronometer, mPriority, mSubText, mLocalOnly, mPeople, mExtras, mGroupKey, mGroupSummary, mSortKey, mContentView, mBigContentView);
      NotificationCompat.addActionsToBuilder(localBuilder, mActions);
      NotificationCompat.addStyleToBuilderJellybean(localBuilder, mStyle);
      return paramBuilderExtender.build(paramBuilder, localBuilder);
    }
    
    public NotificationCompat.Action getAction(Notification paramNotification, int paramInt)
    {
      return (NotificationCompat.Action)NotificationCompatKitKat.getAction(paramNotification, paramInt, NotificationCompat.Action.FACTORY, RemoteInput.FACTORY);
    }
    
    public int getActionCount(Notification paramNotification)
    {
      return NotificationCompatKitKat.getActionCount(paramNotification);
    }
    
    public Bundle getExtras(Notification paramNotification)
    {
      return NotificationCompatKitKat.getExtras(paramNotification);
    }
    
    public String getGroup(Notification paramNotification)
    {
      return NotificationCompatKitKat.getGroup(paramNotification);
    }
    
    public boolean getLocalOnly(Notification paramNotification)
    {
      return NotificationCompatKitKat.getLocalOnly(paramNotification);
    }
    
    public String getSortKey(Notification paramNotification)
    {
      return NotificationCompatKitKat.getSortKey(paramNotification);
    }
    
    public boolean isGroupSummary(Notification paramNotification)
    {
      return NotificationCompatKitKat.isGroupSummary(paramNotification);
    }
  }
  
  public static abstract class Style
  {
    CharSequence mBigContentTitle;
    NotificationCompat.Builder mBuilder;
    CharSequence mSummaryText;
    boolean mSummaryTextSet = false;
    
    public Style() {}
    
    public void addCompatExtras(Bundle paramBundle) {}
    
    public Notification build()
    {
      NotificationCompat.Builder localBuilder = mBuilder;
      Notification localNotification = null;
      if (localBuilder != null) {
        localNotification = mBuilder.build();
      }
      return localNotification;
    }
    
    protected void restoreFromCompatExtras(Bundle paramBundle) {}
    
    public void setBuilder(NotificationCompat.Builder paramBuilder)
    {
      if (mBuilder != paramBuilder)
      {
        mBuilder = paramBuilder;
        if (mBuilder != null) {
          mBuilder.setStyle(this);
        }
      }
    }
  }
  
  public static final class WearableExtender
    implements NotificationCompat.Extender
  {
    private static final int DEFAULT_CONTENT_ICON_GRAVITY = 8388613;
    private static final int DEFAULT_FLAGS = 1;
    private static final int DEFAULT_GRAVITY = 80;
    private static final String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
    private static final int FLAG_BIG_PICTURE_AMBIENT = 32;
    private static final int FLAG_CONTENT_INTENT_AVAILABLE_OFFLINE = 1;
    private static final int FLAG_HINT_AVOID_BACKGROUND_CLIPPING = 16;
    private static final int FLAG_HINT_CONTENT_INTENT_LAUNCHES_ACTIVITY = 64;
    private static final int FLAG_HINT_HIDE_ICON = 2;
    private static final int FLAG_HINT_SHOW_BACKGROUND_ONLY = 4;
    private static final int FLAG_START_SCROLL_BOTTOM = 8;
    private static final String KEY_ACTIONS = "actions";
    private static final String KEY_BACKGROUND = "background";
    private static final String KEY_CONTENT_ACTION_INDEX = "contentActionIndex";
    private static final String KEY_CONTENT_ICON = "contentIcon";
    private static final String KEY_CONTENT_ICON_GRAVITY = "contentIconGravity";
    private static final String KEY_CUSTOM_CONTENT_HEIGHT = "customContentHeight";
    private static final String KEY_CUSTOM_SIZE_PRESET = "customSizePreset";
    private static final String KEY_DISMISSAL_ID = "dismissalId";
    private static final String KEY_DISPLAY_INTENT = "displayIntent";
    private static final String KEY_FLAGS = "flags";
    private static final String KEY_GRAVITY = "gravity";
    private static final String KEY_HINT_SCREEN_TIMEOUT = "hintScreenTimeout";
    private static final String KEY_PAGES = "pages";
    public static final int SCREEN_TIMEOUT_LONG = -1;
    public static final int SCREEN_TIMEOUT_SHORT = 0;
    public static final int SIZE_DEFAULT = 0;
    public static final int SIZE_FULL_SCREEN = 5;
    public static final int SIZE_LARGE = 4;
    public static final int SIZE_MEDIUM = 3;
    public static final int SIZE_SMALL = 2;
    public static final int SIZE_XSMALL = 1;
    public static final int UNSET_ACTION_INDEX = -1;
    private ArrayList<NotificationCompat.Action> mActions = new ArrayList();
    private Bitmap mBackground;
    private int mContentActionIndex = -1;
    private int mContentIcon;
    private int mContentIconGravity = 8388613;
    private int mCustomContentHeight;
    private int mCustomSizePreset = 0;
    private String mDismissalId;
    private PendingIntent mDisplayIntent;
    private int mFlags = 1;
    private int mGravity = 80;
    private int mHintScreenTimeout;
    private ArrayList<Notification> mPages = new ArrayList();
    
    public WearableExtender() {}
    
    public WearableExtender(Notification paramNotification)
    {
      Bundle localBundle1 = NotificationCompat.getExtras(paramNotification);
      if (localBundle1 != null) {}
      for (Bundle localBundle2 = localBundle1.getBundle("android.wearable.EXTENSIONS");; localBundle2 = null)
      {
        if (localBundle2 != null)
        {
          NotificationCompat.Action[] arrayOfAction = NotificationCompat.IMPL.getActionsFromParcelableArrayList(localBundle2.getParcelableArrayList("actions"));
          if (arrayOfAction != null) {
            Collections.addAll(mActions, arrayOfAction);
          }
          mFlags = localBundle2.getInt("flags", 1);
          mDisplayIntent = ((PendingIntent)localBundle2.getParcelable("displayIntent"));
          Notification[] arrayOfNotification = NotificationCompat.getNotificationArrayFromBundle(localBundle2, "pages");
          if (arrayOfNotification != null) {
            Collections.addAll(mPages, arrayOfNotification);
          }
          mBackground = ((Bitmap)localBundle2.getParcelable("background"));
          mContentIcon = localBundle2.getInt("contentIcon");
          mContentIconGravity = localBundle2.getInt("contentIconGravity", 8388613);
          mContentActionIndex = localBundle2.getInt("contentActionIndex", -1);
          mCustomSizePreset = localBundle2.getInt("customSizePreset", 0);
          mCustomContentHeight = localBundle2.getInt("customContentHeight");
          mGravity = localBundle2.getInt("gravity", 80);
          mHintScreenTimeout = localBundle2.getInt("hintScreenTimeout");
          mDismissalId = localBundle2.getString("dismissalId");
        }
        return;
      }
    }
    
    private void setFlag(int paramInt, boolean paramBoolean)
    {
      if (paramBoolean)
      {
        mFlags = (paramInt | mFlags);
        return;
      }
      mFlags &= (paramInt ^ 0xFFFFFFFF);
    }
    
    public WearableExtender addAction(NotificationCompat.Action paramAction)
    {
      mActions.add(paramAction);
      return this;
    }
    
    public WearableExtender addActions(List<NotificationCompat.Action> paramList)
    {
      mActions.addAll(paramList);
      return this;
    }
    
    public WearableExtender addPage(Notification paramNotification)
    {
      mPages.add(paramNotification);
      return this;
    }
    
    public WearableExtender addPages(List<Notification> paramList)
    {
      mPages.addAll(paramList);
      return this;
    }
    
    public WearableExtender clearActions()
    {
      mActions.clear();
      return this;
    }
    
    public WearableExtender clearPages()
    {
      mPages.clear();
      return this;
    }
    
    public WearableExtender clone()
    {
      WearableExtender localWearableExtender = new WearableExtender();
      mActions = new ArrayList(mActions);
      mFlags = mFlags;
      mDisplayIntent = mDisplayIntent;
      mPages = new ArrayList(mPages);
      mBackground = mBackground;
      mContentIcon = mContentIcon;
      mContentIconGravity = mContentIconGravity;
      mContentActionIndex = mContentActionIndex;
      mCustomSizePreset = mCustomSizePreset;
      mCustomContentHeight = mCustomContentHeight;
      mGravity = mGravity;
      mHintScreenTimeout = mHintScreenTimeout;
      mDismissalId = mDismissalId;
      return localWearableExtender;
    }
    
    public NotificationCompat.Builder extend(NotificationCompat.Builder paramBuilder)
    {
      Bundle localBundle = new Bundle();
      if (!mActions.isEmpty()) {
        localBundle.putParcelableArrayList("actions", NotificationCompat.IMPL.getParcelableArrayListForActions((NotificationCompat.Action[])mActions.toArray(new NotificationCompat.Action[mActions.size()])));
      }
      if (mFlags != 1) {
        localBundle.putInt("flags", mFlags);
      }
      if (mDisplayIntent != null) {
        localBundle.putParcelable("displayIntent", mDisplayIntent);
      }
      if (!mPages.isEmpty()) {
        localBundle.putParcelableArray("pages", (Parcelable[])mPages.toArray(new Notification[mPages.size()]));
      }
      if (mBackground != null) {
        localBundle.putParcelable("background", mBackground);
      }
      if (mContentIcon != 0) {
        localBundle.putInt("contentIcon", mContentIcon);
      }
      if (mContentIconGravity != 8388613) {
        localBundle.putInt("contentIconGravity", mContentIconGravity);
      }
      if (mContentActionIndex != -1) {
        localBundle.putInt("contentActionIndex", mContentActionIndex);
      }
      if (mCustomSizePreset != 0) {
        localBundle.putInt("customSizePreset", mCustomSizePreset);
      }
      if (mCustomContentHeight != 0) {
        localBundle.putInt("customContentHeight", mCustomContentHeight);
      }
      if (mGravity != 80) {
        localBundle.putInt("gravity", mGravity);
      }
      if (mHintScreenTimeout != 0) {
        localBundle.putInt("hintScreenTimeout", mHintScreenTimeout);
      }
      if (mDismissalId != null) {
        localBundle.putString("dismissalId", mDismissalId);
      }
      paramBuilder.getExtras().putBundle("android.wearable.EXTENSIONS", localBundle);
      return paramBuilder;
    }
    
    public List<NotificationCompat.Action> getActions()
    {
      return mActions;
    }
    
    public Bitmap getBackground()
    {
      return mBackground;
    }
    
    public int getContentAction()
    {
      return mContentActionIndex;
    }
    
    public int getContentIcon()
    {
      return mContentIcon;
    }
    
    public int getContentIconGravity()
    {
      return mContentIconGravity;
    }
    
    public boolean getContentIntentAvailableOffline()
    {
      return (0x1 & mFlags) != 0;
    }
    
    public int getCustomContentHeight()
    {
      return mCustomContentHeight;
    }
    
    public int getCustomSizePreset()
    {
      return mCustomSizePreset;
    }
    
    public String getDismissalId()
    {
      return mDismissalId;
    }
    
    public PendingIntent getDisplayIntent()
    {
      return mDisplayIntent;
    }
    
    public int getGravity()
    {
      return mGravity;
    }
    
    public boolean getHintAmbientBigPicture()
    {
      return (0x20 & mFlags) != 0;
    }
    
    public boolean getHintAvoidBackgroundClipping()
    {
      return (0x10 & mFlags) != 0;
    }
    
    public boolean getHintContentIntentLaunchesActivity()
    {
      return (0x40 & mFlags) != 0;
    }
    
    public boolean getHintHideIcon()
    {
      return (0x2 & mFlags) != 0;
    }
    
    public int getHintScreenTimeout()
    {
      return mHintScreenTimeout;
    }
    
    public boolean getHintShowBackgroundOnly()
    {
      return (0x4 & mFlags) != 0;
    }
    
    public List<Notification> getPages()
    {
      return mPages;
    }
    
    public boolean getStartScrollBottom()
    {
      return (0x8 & mFlags) != 0;
    }
    
    public WearableExtender setBackground(Bitmap paramBitmap)
    {
      mBackground = paramBitmap;
      return this;
    }
    
    public WearableExtender setContentAction(int paramInt)
    {
      mContentActionIndex = paramInt;
      return this;
    }
    
    public WearableExtender setContentIcon(int paramInt)
    {
      mContentIcon = paramInt;
      return this;
    }
    
    public WearableExtender setContentIconGravity(int paramInt)
    {
      mContentIconGravity = paramInt;
      return this;
    }
    
    public WearableExtender setContentIntentAvailableOffline(boolean paramBoolean)
    {
      setFlag(1, paramBoolean);
      return this;
    }
    
    public WearableExtender setCustomContentHeight(int paramInt)
    {
      mCustomContentHeight = paramInt;
      return this;
    }
    
    public WearableExtender setCustomSizePreset(int paramInt)
    {
      mCustomSizePreset = paramInt;
      return this;
    }
    
    public WearableExtender setDismissalId(String paramString)
    {
      mDismissalId = paramString;
      return this;
    }
    
    public WearableExtender setDisplayIntent(PendingIntent paramPendingIntent)
    {
      mDisplayIntent = paramPendingIntent;
      return this;
    }
    
    public WearableExtender setGravity(int paramInt)
    {
      mGravity = paramInt;
      return this;
    }
    
    public WearableExtender setHintAmbientBigPicture(boolean paramBoolean)
    {
      setFlag(32, paramBoolean);
      return this;
    }
    
    public WearableExtender setHintAvoidBackgroundClipping(boolean paramBoolean)
    {
      setFlag(16, paramBoolean);
      return this;
    }
    
    public WearableExtender setHintContentIntentLaunchesActivity(boolean paramBoolean)
    {
      setFlag(64, paramBoolean);
      return this;
    }
    
    public WearableExtender setHintHideIcon(boolean paramBoolean)
    {
      setFlag(2, paramBoolean);
      return this;
    }
    
    public WearableExtender setHintScreenTimeout(int paramInt)
    {
      mHintScreenTimeout = paramInt;
      return this;
    }
    
    public WearableExtender setHintShowBackgroundOnly(boolean paramBoolean)
    {
      setFlag(4, paramBoolean);
      return this;
    }
    
    public WearableExtender setStartScrollBottom(boolean paramBoolean)
    {
      setFlag(8, paramBoolean);
      return this;
    }
  }
}
