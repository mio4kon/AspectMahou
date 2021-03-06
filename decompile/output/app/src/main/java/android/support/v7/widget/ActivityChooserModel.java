package android.support.v7.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.DataSetObservable;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class ActivityChooserModel
  extends DataSetObservable
{
  static final String ATTRIBUTE_ACTIVITY = "activity";
  static final String ATTRIBUTE_TIME = "time";
  static final String ATTRIBUTE_WEIGHT = "weight";
  static final boolean DEBUG = false;
  private static final int DEFAULT_ACTIVITY_INFLATION = 5;
  private static final float DEFAULT_HISTORICAL_RECORD_WEIGHT = 1.0F;
  public static final String DEFAULT_HISTORY_FILE_NAME = "activity_choser_model_history.xml";
  public static final int DEFAULT_HISTORY_MAX_LENGTH = 50;
  private static final String HISTORY_FILE_EXTENSION = ".xml";
  private static final int INVALID_INDEX = -1;
  static final String LOG_TAG = ActivityChooserModel.class.getSimpleName();
  static final String TAG_HISTORICAL_RECORD = "historical-record";
  static final String TAG_HISTORICAL_RECORDS = "historical-records";
  private static final Map<String, ActivityChooserModel> sDataModelRegistry = new HashMap();
  private static final Object sRegistryLock = new Object();
  private final List<ActivityResolveInfo> mActivities = new ArrayList();
  private OnChooseActivityListener mActivityChoserModelPolicy;
  private ActivitySorter mActivitySorter = new DefaultSorter();
  boolean mCanReadHistoricalData = true;
  final Context mContext;
  private final List<HistoricalRecord> mHistoricalRecords = new ArrayList();
  private boolean mHistoricalRecordsChanged = true;
  final String mHistoryFileName;
  private int mHistoryMaxSize = 50;
  private final Object mInstanceLock = new Object();
  private Intent mIntent;
  private boolean mReadShareHistoryCalled = false;
  private boolean mReloadActivities = false;
  
  private ActivityChooserModel(Context paramContext, String paramString)
  {
    mContext = paramContext.getApplicationContext();
    if ((!TextUtils.isEmpty(paramString)) && (!paramString.endsWith(".xml")))
    {
      mHistoryFileName = (paramString + ".xml");
      return;
    }
    mHistoryFileName = paramString;
  }
  
  private boolean addHistoricalRecord(HistoricalRecord paramHistoricalRecord)
  {
    boolean bool = mHistoricalRecords.add(paramHistoricalRecord);
    if (bool)
    {
      mHistoricalRecordsChanged = true;
      pruneExcessiveHistoricalRecordsIfNeeded();
      persistHistoricalDataIfNeeded();
      sortActivitiesIfNeeded();
      notifyChanged();
    }
    return bool;
  }
  
  private void ensureConsistentState()
  {
    boolean bool = loadActivitiesIfNeeded() | readHistoricalDataIfNeeded();
    pruneExcessiveHistoricalRecordsIfNeeded();
    if (bool)
    {
      sortActivitiesIfNeeded();
      notifyChanged();
    }
  }
  
  public static ActivityChooserModel get(Context paramContext, String paramString)
  {
    synchronized (sRegistryLock)
    {
      ActivityChooserModel localActivityChooserModel = (ActivityChooserModel)sDataModelRegistry.get(paramString);
      if (localActivityChooserModel == null)
      {
        localActivityChooserModel = new ActivityChooserModel(paramContext, paramString);
        sDataModelRegistry.put(paramString, localActivityChooserModel);
      }
      return localActivityChooserModel;
    }
  }
  
  private boolean loadActivitiesIfNeeded()
  {
    boolean bool1 = mReloadActivities;
    boolean bool2 = false;
    if (bool1)
    {
      Intent localIntent = mIntent;
      bool2 = false;
      if (localIntent != null)
      {
        mReloadActivities = false;
        mActivities.clear();
        List localList = mContext.getPackageManager().queryIntentActivities(mIntent, 0);
        int i = localList.size();
        for (int j = 0; j < i; j++)
        {
          ResolveInfo localResolveInfo = (ResolveInfo)localList.get(j);
          mActivities.add(new ActivityResolveInfo(localResolveInfo));
        }
        bool2 = true;
      }
    }
    return bool2;
  }
  
  private void persistHistoricalDataIfNeeded()
  {
    if (!mReadShareHistoryCalled) {
      throw new IllegalStateException("No preceding call to #readHistoricalData");
    }
    if (!mHistoricalRecordsChanged) {}
    do
    {
      return;
      mHistoricalRecordsChanged = false;
    } while (TextUtils.isEmpty(mHistoryFileName));
    PersistHistoryAsyncTask localPersistHistoryAsyncTask = new PersistHistoryAsyncTask();
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = new ArrayList(mHistoricalRecords);
    arrayOfObject[1] = mHistoryFileName;
    AsyncTaskCompat.executeParallel(localPersistHistoryAsyncTask, arrayOfObject);
  }
  
  private void pruneExcessiveHistoricalRecordsIfNeeded()
  {
    int i = mHistoricalRecords.size() - mHistoryMaxSize;
    if (i <= 0) {}
    for (;;)
    {
      return;
      mHistoricalRecordsChanged = true;
      for (int j = 0; j < i; j++) {
        ((HistoricalRecord)mHistoricalRecords.remove(0));
      }
    }
  }
  
  private boolean readHistoricalDataIfNeeded()
  {
    if ((mCanReadHistoricalData) && (mHistoricalRecordsChanged) && (!TextUtils.isEmpty(mHistoryFileName)))
    {
      mCanReadHistoricalData = false;
      mReadShareHistoryCalled = true;
      readHistoricalDataImpl();
      return true;
    }
    return false;
  }
  
  private void readHistoricalDataImpl()
  {
    try
    {
      FileInputStream localFileInputStream = mContext.openFileInput(mHistoryFileName);
      try
      {
        localXmlPullParser = Xml.newPullParser();
        localXmlPullParser.setInput(localFileInputStream, "UTF-8");
        for (int i = 0; (i != 1) && (i != 2); i = localXmlPullParser.next()) {}
        if (!"historical-records".equals(localXmlPullParser.getName())) {
          throw new XmlPullParserException("Share records file does not start with historical-records tag.");
        }
      }
      catch (XmlPullParserException localXmlPullParserException)
      {
        Log.e(LOG_TAG, "Error reading historical recrod file: " + mHistoryFileName, localXmlPullParserException);
        if (localFileInputStream != null)
        {
          try
          {
            localFileInputStream.close();
            return;
          }
          catch (IOException localIOException4)
          {
            return;
          }
          localList = mHistoricalRecords;
          localList.clear();
          int j;
          do
          {
            j = localXmlPullParser.next();
            if (j == 1)
            {
              if (localFileInputStream == null) {
                break;
              }
              try
              {
                localFileInputStream.close();
                return;
              }
              catch (IOException localIOException5)
              {
                return;
              }
            }
          } while ((j == 3) || (j == 4));
          if (!"historical-record".equals(localXmlPullParser.getName())) {
            throw new XmlPullParserException("Share records file not well-formed.");
          }
        }
      }
      catch (IOException localIOException2)
      {
        for (;;)
        {
          XmlPullParser localXmlPullParser;
          List localList;
          Log.e(LOG_TAG, "Error reading historical recrod file: " + mHistoryFileName, localIOException2);
          if (localFileInputStream == null) {
            break;
          }
          try
          {
            localFileInputStream.close();
            return;
          }
          catch (IOException localIOException3)
          {
            return;
          }
          localList.add(new HistoricalRecord(localXmlPullParser.getAttributeValue(null, "activity"), Long.parseLong(localXmlPullParser.getAttributeValue(null, "time")), Float.parseFloat(localXmlPullParser.getAttributeValue(null, "weight"))));
        }
      }
      finally
      {
        if (localFileInputStream != null) {}
        try
        {
          localFileInputStream.close();
          throw localObject;
        }
        catch (IOException localIOException1)
        {
          for (;;) {}
        }
      }
      return;
    }
    catch (FileNotFoundException localFileNotFoundException) {}
  }
  
  private boolean sortActivitiesIfNeeded()
  {
    if ((mActivitySorter != null) && (mIntent != null) && (!mActivities.isEmpty()) && (!mHistoricalRecords.isEmpty()))
    {
      mActivitySorter.sort(mIntent, mActivities, Collections.unmodifiableList(mHistoricalRecords));
      return true;
    }
    return false;
  }
  
  public Intent chooseActivity(int paramInt)
  {
    synchronized (mInstanceLock)
    {
      if (mIntent == null) {
        return null;
      }
      ensureConsistentState();
      ActivityResolveInfo localActivityResolveInfo = (ActivityResolveInfo)mActivities.get(paramInt);
      ComponentName localComponentName = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
      Intent localIntent1 = new Intent(mIntent);
      localIntent1.setComponent(localComponentName);
      if (mActivityChoserModelPolicy != null)
      {
        Intent localIntent2 = new Intent(localIntent1);
        if (mActivityChoserModelPolicy.onChooseActivity(this, localIntent2)) {
          return null;
        }
      }
      addHistoricalRecord(new HistoricalRecord(localComponentName, System.currentTimeMillis(), 1.0F));
      return localIntent1;
    }
  }
  
  public ResolveInfo getActivity(int paramInt)
  {
    synchronized (mInstanceLock)
    {
      ensureConsistentState();
      ResolveInfo localResolveInfo = mActivities.get(paramInt)).resolveInfo;
      return localResolveInfo;
    }
  }
  
  public int getActivityCount()
  {
    synchronized (mInstanceLock)
    {
      ensureConsistentState();
      int i = mActivities.size();
      return i;
    }
  }
  
  public int getActivityIndex(ResolveInfo paramResolveInfo)
  {
    for (;;)
    {
      int j;
      synchronized (mInstanceLock)
      {
        ensureConsistentState();
        List localList = mActivities;
        int i = localList.size();
        j = 0;
        if (j < i)
        {
          if (getresolveInfo == paramResolveInfo) {
            return j;
          }
        }
        else {
          return -1;
        }
      }
      j++;
    }
  }
  
  public ResolveInfo getDefaultActivity()
  {
    synchronized (mInstanceLock)
    {
      ensureConsistentState();
      if (!mActivities.isEmpty())
      {
        ResolveInfo localResolveInfo = mActivities.get(0)).resolveInfo;
        return localResolveInfo;
      }
      return null;
    }
  }
  
  public int getHistoryMaxSize()
  {
    synchronized (mInstanceLock)
    {
      int i = mHistoryMaxSize;
      return i;
    }
  }
  
  public int getHistorySize()
  {
    synchronized (mInstanceLock)
    {
      ensureConsistentState();
      int i = mHistoricalRecords.size();
      return i;
    }
  }
  
  public Intent getIntent()
  {
    synchronized (mInstanceLock)
    {
      Intent localIntent = mIntent;
      return localIntent;
    }
  }
  
  public void setActivitySorter(ActivitySorter paramActivitySorter)
  {
    synchronized (mInstanceLock)
    {
      if (mActivitySorter == paramActivitySorter) {
        return;
      }
      mActivitySorter = paramActivitySorter;
      if (sortActivitiesIfNeeded()) {
        notifyChanged();
      }
      return;
    }
  }
  
  public void setDefaultActivity(int paramInt)
  {
    for (;;)
    {
      synchronized (mInstanceLock)
      {
        ensureConsistentState();
        ActivityResolveInfo localActivityResolveInfo1 = (ActivityResolveInfo)mActivities.get(paramInt);
        ActivityResolveInfo localActivityResolveInfo2 = (ActivityResolveInfo)mActivities.get(0);
        if (localActivityResolveInfo2 != null)
        {
          f = 5.0F + (weight - weight);
          addHistoricalRecord(new HistoricalRecord(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name), System.currentTimeMillis(), f));
          return;
        }
      }
      float f = 1.0F;
    }
  }
  
  public void setHistoryMaxSize(int paramInt)
  {
    synchronized (mInstanceLock)
    {
      if (mHistoryMaxSize == paramInt) {
        return;
      }
      mHistoryMaxSize = paramInt;
      pruneExcessiveHistoricalRecordsIfNeeded();
      if (sortActivitiesIfNeeded()) {
        notifyChanged();
      }
      return;
    }
  }
  
  public void setIntent(Intent paramIntent)
  {
    synchronized (mInstanceLock)
    {
      if (mIntent == paramIntent) {
        return;
      }
      mIntent = paramIntent;
      mReloadActivities = true;
      ensureConsistentState();
      return;
    }
  }
  
  public void setOnChooseActivityListener(OnChooseActivityListener paramOnChooseActivityListener)
  {
    synchronized (mInstanceLock)
    {
      mActivityChoserModelPolicy = paramOnChooseActivityListener;
      return;
    }
  }
  
  public static abstract interface ActivityChooserModelClient
  {
    public abstract void setActivityChooserModel(ActivityChooserModel paramActivityChooserModel);
  }
  
  public final class ActivityResolveInfo
    implements Comparable<ActivityResolveInfo>
  {
    public final ResolveInfo resolveInfo;
    public float weight;
    
    public ActivityResolveInfo(ResolveInfo paramResolveInfo)
    {
      resolveInfo = paramResolveInfo;
    }
    
    public int compareTo(ActivityResolveInfo paramActivityResolveInfo)
    {
      return Float.floatToIntBits(weight) - Float.floatToIntBits(weight);
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      ActivityResolveInfo localActivityResolveInfo;
      do
      {
        return true;
        if (paramObject == null) {
          return false;
        }
        if (getClass() != paramObject.getClass()) {
          return false;
        }
        localActivityResolveInfo = (ActivityResolveInfo)paramObject;
      } while (Float.floatToIntBits(weight) == Float.floatToIntBits(weight));
      return false;
    }
    
    public int hashCode()
    {
      return 31 + Float.floatToIntBits(weight);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      localStringBuilder.append("resolveInfo:").append(resolveInfo.toString());
      localStringBuilder.append("; weight:").append(new BigDecimal(weight));
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  public static abstract interface ActivitySorter
  {
    public abstract void sort(Intent paramIntent, List<ActivityChooserModel.ActivityResolveInfo> paramList, List<ActivityChooserModel.HistoricalRecord> paramList1);
  }
  
  private final class DefaultSorter
    implements ActivityChooserModel.ActivitySorter
  {
    private static final float WEIGHT_DECAY_COEFFICIENT = 0.95F;
    private final Map<ComponentName, ActivityChooserModel.ActivityResolveInfo> mPackageNameToActivityMap = new HashMap();
    
    DefaultSorter() {}
    
    public void sort(Intent paramIntent, List<ActivityChooserModel.ActivityResolveInfo> paramList, List<ActivityChooserModel.HistoricalRecord> paramList1)
    {
      Map localMap = mPackageNameToActivityMap;
      localMap.clear();
      int i = paramList.size();
      for (int j = 0; j < i; j++)
      {
        ActivityChooserModel.ActivityResolveInfo localActivityResolveInfo2 = (ActivityChooserModel.ActivityResolveInfo)paramList.get(j);
        weight = 0.0F;
        localMap.put(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name), localActivityResolveInfo2);
      }
      int k = -1 + paramList1.size();
      float f = 1.0F;
      for (int m = k; m >= 0; m--)
      {
        ActivityChooserModel.HistoricalRecord localHistoricalRecord = (ActivityChooserModel.HistoricalRecord)paramList1.get(m);
        ActivityChooserModel.ActivityResolveInfo localActivityResolveInfo1 = (ActivityChooserModel.ActivityResolveInfo)localMap.get(activity);
        if (localActivityResolveInfo1 != null)
        {
          weight += f * weight;
          f *= 0.95F;
        }
      }
      Collections.sort(paramList);
    }
  }
  
  public static final class HistoricalRecord
  {
    public final ComponentName activity;
    public final long time;
    public final float weight;
    
    public HistoricalRecord(ComponentName paramComponentName, long paramLong, float paramFloat)
    {
      activity = paramComponentName;
      time = paramLong;
      weight = paramFloat;
    }
    
    public HistoricalRecord(String paramString, long paramLong, float paramFloat)
    {
      this(ComponentName.unflattenFromString(paramString), paramLong, paramFloat);
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      HistoricalRecord localHistoricalRecord;
      do
      {
        return true;
        if (paramObject == null) {
          return false;
        }
        if (getClass() != paramObject.getClass()) {
          return false;
        }
        localHistoricalRecord = (HistoricalRecord)paramObject;
        if (activity == null)
        {
          if (activity != null) {
            return false;
          }
        }
        else if (!activity.equals(activity)) {
          return false;
        }
        if (time != time) {
          return false;
        }
      } while (Float.floatToIntBits(weight) == Float.floatToIntBits(weight));
      return false;
    }
    
    public int hashCode()
    {
      if (activity == null) {}
      for (int i = 0;; i = activity.hashCode()) {
        return 31 * (31 * (i + 31) + (int)(time ^ time >>> 32)) + Float.floatToIntBits(weight);
      }
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      localStringBuilder.append("; activity:").append(activity);
      localStringBuilder.append("; time:").append(time);
      localStringBuilder.append("; weight:").append(new BigDecimal(weight));
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  public static abstract interface OnChooseActivityListener
  {
    public abstract boolean onChooseActivity(ActivityChooserModel paramActivityChooserModel, Intent paramIntent);
  }
  
  private final class PersistHistoryAsyncTask
    extends AsyncTask<Object, Void, Void>
  {
    PersistHistoryAsyncTask() {}
    
    /* Error */
    public Void doInBackground(Object... paramVarArgs)
    {
      // Byte code:
      //   0: aload_1
      //   1: iconst_0
      //   2: aaload
      //   3: checkcast 29	java/util/List
      //   6: astore_2
      //   7: aload_1
      //   8: iconst_1
      //   9: aaload
      //   10: checkcast 31	java/lang/String
      //   13: astore_3
      //   14: aload_0
      //   15: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   18: getfield 37	android/support/v7/widget/ActivityChooserModel:mContext	Landroid/content/Context;
      //   21: aload_3
      //   22: iconst_0
      //   23: invokevirtual 43	android/content/Context:openFileOutput	(Ljava/lang/String;I)Ljava/io/FileOutputStream;
      //   26: astore 6
      //   28: invokestatic 49	android/util/Xml:newSerializer	()Lorg/xmlpull/v1/XmlSerializer;
      //   31: astore 7
      //   33: aload 7
      //   35: aload 6
      //   37: aconst_null
      //   38: invokeinterface 55 3 0
      //   43: aload 7
      //   45: ldc 57
      //   47: iconst_1
      //   48: invokestatic 63	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   51: invokeinterface 67 3 0
      //   56: aload 7
      //   58: aconst_null
      //   59: ldc 69
      //   61: invokeinterface 73 3 0
      //   66: pop
      //   67: aload_2
      //   68: invokeinterface 77 1 0
      //   73: istore 20
      //   75: iconst_0
      //   76: istore 21
      //   78: iload 21
      //   80: iload 20
      //   82: if_icmpge +132 -> 214
      //   85: aload_2
      //   86: iconst_0
      //   87: invokeinterface 81 2 0
      //   92: checkcast 83	android/support/v7/widget/ActivityChooserModel$HistoricalRecord
      //   95: astore 22
      //   97: aload 7
      //   99: aconst_null
      //   100: ldc 85
      //   102: invokeinterface 73 3 0
      //   107: pop
      //   108: aload 7
      //   110: aconst_null
      //   111: ldc 87
      //   113: aload 22
      //   115: getfield 90	android/support/v7/widget/ActivityChooserModel$HistoricalRecord:activity	Landroid/content/ComponentName;
      //   118: invokevirtual 96	android/content/ComponentName:flattenToString	()Ljava/lang/String;
      //   121: invokeinterface 100 4 0
      //   126: pop
      //   127: aload 7
      //   129: aconst_null
      //   130: ldc 102
      //   132: aload 22
      //   134: getfield 105	android/support/v7/widget/ActivityChooserModel$HistoricalRecord:time	J
      //   137: invokestatic 108	java/lang/String:valueOf	(J)Ljava/lang/String;
      //   140: invokeinterface 100 4 0
      //   145: pop
      //   146: aload 7
      //   148: aconst_null
      //   149: ldc 110
      //   151: aload 22
      //   153: getfield 113	android/support/v7/widget/ActivityChooserModel$HistoricalRecord:weight	F
      //   156: invokestatic 116	java/lang/String:valueOf	(F)Ljava/lang/String;
      //   159: invokeinterface 100 4 0
      //   164: pop
      //   165: aload 7
      //   167: aconst_null
      //   168: ldc 85
      //   170: invokeinterface 119 3 0
      //   175: pop
      //   176: iinc 21 1
      //   179: goto -101 -> 78
      //   182: astore 4
      //   184: getstatic 123	android/support/v7/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
      //   187: new 125	java/lang/StringBuilder
      //   190: dup
      //   191: invokespecial 126	java/lang/StringBuilder:<init>	()V
      //   194: ldc -128
      //   196: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   199: aload_3
      //   200: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   203: invokevirtual 135	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   206: aload 4
      //   208: invokestatic 141	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   211: pop
      //   212: aconst_null
      //   213: areturn
      //   214: aload 7
      //   216: aconst_null
      //   217: ldc 69
      //   219: invokeinterface 119 3 0
      //   224: pop
      //   225: aload 7
      //   227: invokeinterface 144 1 0
      //   232: aload_0
      //   233: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   236: iconst_1
      //   237: putfield 148	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   240: aload 6
      //   242: ifnull +8 -> 250
      //   245: aload 6
      //   247: invokevirtual 153	java/io/FileOutputStream:close	()V
      //   250: aconst_null
      //   251: areturn
      //   252: astore 16
      //   254: getstatic 123	android/support/v7/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
      //   257: new 125	java/lang/StringBuilder
      //   260: dup
      //   261: invokespecial 126	java/lang/StringBuilder:<init>	()V
      //   264: ldc -128
      //   266: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   269: aload_0
      //   270: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   273: getfield 156	android/support/v7/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
      //   276: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   279: invokevirtual 135	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   282: aload 16
      //   284: invokestatic 141	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   287: pop
      //   288: aload_0
      //   289: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   292: iconst_1
      //   293: putfield 148	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   296: aload 6
      //   298: ifnull -48 -> 250
      //   301: aload 6
      //   303: invokevirtual 153	java/io/FileOutputStream:close	()V
      //   306: goto -56 -> 250
      //   309: astore 18
      //   311: goto -61 -> 250
      //   314: astore 13
      //   316: getstatic 123	android/support/v7/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
      //   319: new 125	java/lang/StringBuilder
      //   322: dup
      //   323: invokespecial 126	java/lang/StringBuilder:<init>	()V
      //   326: ldc -128
      //   328: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   331: aload_0
      //   332: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   335: getfield 156	android/support/v7/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
      //   338: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   341: invokevirtual 135	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   344: aload 13
      //   346: invokestatic 141	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   349: pop
      //   350: aload_0
      //   351: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   354: iconst_1
      //   355: putfield 148	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   358: aload 6
      //   360: ifnull -110 -> 250
      //   363: aload 6
      //   365: invokevirtual 153	java/io/FileOutputStream:close	()V
      //   368: goto -118 -> 250
      //   371: astore 15
      //   373: goto -123 -> 250
      //   376: astore 10
      //   378: getstatic 123	android/support/v7/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
      //   381: new 125	java/lang/StringBuilder
      //   384: dup
      //   385: invokespecial 126	java/lang/StringBuilder:<init>	()V
      //   388: ldc -128
      //   390: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   393: aload_0
      //   394: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   397: getfield 156	android/support/v7/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
      //   400: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   403: invokevirtual 135	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   406: aload 10
      //   408: invokestatic 141	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   411: pop
      //   412: aload_0
      //   413: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   416: iconst_1
      //   417: putfield 148	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   420: aload 6
      //   422: ifnull -172 -> 250
      //   425: aload 6
      //   427: invokevirtual 153	java/io/FileOutputStream:close	()V
      //   430: goto -180 -> 250
      //   433: astore 12
      //   435: goto -185 -> 250
      //   438: astore 8
      //   440: aload_0
      //   441: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   444: iconst_1
      //   445: putfield 148	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   448: aload 6
      //   450: ifnull +8 -> 458
      //   453: aload 6
      //   455: invokevirtual 153	java/io/FileOutputStream:close	()V
      //   458: aload 8
      //   460: athrow
      //   461: astore 29
      //   463: goto -213 -> 250
      //   466: astore 9
      //   468: goto -10 -> 458
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	471	0	this	PersistHistoryAsyncTask
      //   0	471	1	paramVarArgs	Object[]
      //   6	80	2	localList	List
      //   13	187	3	str	String
      //   182	25	4	localFileNotFoundException	FileNotFoundException
      //   26	428	6	localFileOutputStream	java.io.FileOutputStream
      //   31	195	7	localXmlSerializer	org.xmlpull.v1.XmlSerializer
      //   438	21	8	localObject	Object
      //   466	1	9	localIOException1	IOException
      //   376	31	10	localIOException2	IOException
      //   433	1	12	localIOException3	IOException
      //   314	31	13	localIllegalStateException	IllegalStateException
      //   371	1	15	localIOException4	IOException
      //   252	31	16	localIllegalArgumentException	IllegalArgumentException
      //   309	1	18	localIOException5	IOException
      //   73	10	20	i	int
      //   76	101	21	j	int
      //   95	57	22	localHistoricalRecord	ActivityChooserModel.HistoricalRecord
      //   461	1	29	localIOException6	IOException
      // Exception table:
      //   from	to	target	type
      //   14	28	182	java/io/FileNotFoundException
      //   33	75	252	java/lang/IllegalArgumentException
      //   85	176	252	java/lang/IllegalArgumentException
      //   214	232	252	java/lang/IllegalArgumentException
      //   301	306	309	java/io/IOException
      //   33	75	314	java/lang/IllegalStateException
      //   85	176	314	java/lang/IllegalStateException
      //   214	232	314	java/lang/IllegalStateException
      //   363	368	371	java/io/IOException
      //   33	75	376	java/io/IOException
      //   85	176	376	java/io/IOException
      //   214	232	376	java/io/IOException
      //   425	430	433	java/io/IOException
      //   33	75	438	finally
      //   85	176	438	finally
      //   214	232	438	finally
      //   254	288	438	finally
      //   316	350	438	finally
      //   378	412	438	finally
      //   245	250	461	java/io/IOException
      //   453	458	466	java/io/IOException
    }
  }
}
