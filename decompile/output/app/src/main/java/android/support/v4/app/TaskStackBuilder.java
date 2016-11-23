package android.support.v4.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;

public final class TaskStackBuilder
  implements Iterable<Intent>
{
  private static final TaskStackBuilderImpl IMPL = new TaskStackBuilderImplBase();
  private static final String TAG = "TaskStackBuilder";
  private final ArrayList<Intent> mIntents = new ArrayList();
  private final Context mSourceContext;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 11)
    {
      IMPL = new TaskStackBuilderImplHoneycomb();
      return;
    }
  }
  
  private TaskStackBuilder(Context paramContext)
  {
    mSourceContext = paramContext;
  }
  
  public static TaskStackBuilder create(Context paramContext)
  {
    return new TaskStackBuilder(paramContext);
  }
  
  @Deprecated
  public static TaskStackBuilder from(Context paramContext)
  {
    return create(paramContext);
  }
  
  public TaskStackBuilder addNextIntent(Intent paramIntent)
  {
    mIntents.add(paramIntent);
    return this;
  }
  
  public TaskStackBuilder addNextIntentWithParentStack(Intent paramIntent)
  {
    ComponentName localComponentName = paramIntent.getComponent();
    if (localComponentName == null) {
      localComponentName = paramIntent.resolveActivity(mSourceContext.getPackageManager());
    }
    if (localComponentName != null) {
      addParentStack(localComponentName);
    }
    addNextIntent(paramIntent);
    return this;
  }
  
  public TaskStackBuilder addParentStack(Activity paramActivity)
  {
    boolean bool = paramActivity instanceof SupportParentable;
    Intent localIntent = null;
    if (bool) {
      localIntent = ((SupportParentable)paramActivity).getSupportParentActivityIntent();
    }
    if (localIntent == null) {
      localIntent = NavUtils.getParentActivityIntent(paramActivity);
    }
    if (localIntent != null)
    {
      ComponentName localComponentName = localIntent.getComponent();
      if (localComponentName == null) {
        localComponentName = localIntent.resolveActivity(mSourceContext.getPackageManager());
      }
      addParentStack(localComponentName);
      addNextIntent(localIntent);
    }
    return this;
  }
  
  public TaskStackBuilder addParentStack(ComponentName paramComponentName)
  {
    int i = mIntents.size();
    try
    {
      Intent localIntent;
      for (Object localObject = NavUtils.getParentActivityIntent(mSourceContext, paramComponentName); localObject != null; localObject = localIntent)
      {
        mIntents.add(i, localObject);
        localIntent = NavUtils.getParentActivityIntent(mSourceContext, ((Intent)localObject).getComponent());
      }
      return this;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.e("TaskStackBuilder", "Bad ComponentName while traversing activity parent metadata");
      throw new IllegalArgumentException(localNameNotFoundException);
    }
  }
  
  public TaskStackBuilder addParentStack(Class<?> paramClass)
  {
    return addParentStack(new ComponentName(mSourceContext, paramClass));
  }
  
  public Intent editIntentAt(int paramInt)
  {
    return (Intent)mIntents.get(paramInt);
  }
  
  @Deprecated
  public Intent getIntent(int paramInt)
  {
    return editIntentAt(paramInt);
  }
  
  public int getIntentCount()
  {
    return mIntents.size();
  }
  
  public Intent[] getIntents()
  {
    Intent[] arrayOfIntent = new Intent[mIntents.size()];
    if (arrayOfIntent.length == 0) {}
    for (;;)
    {
      return arrayOfIntent;
      arrayOfIntent[0] = new Intent((Intent)mIntents.get(0)).addFlags(268484608);
      for (int i = 1; i < arrayOfIntent.length; i++) {
        arrayOfIntent[i] = new Intent((Intent)mIntents.get(i));
      }
    }
  }
  
  public PendingIntent getPendingIntent(int paramInt1, int paramInt2)
  {
    return getPendingIntent(paramInt1, paramInt2, null);
  }
  
  public PendingIntent getPendingIntent(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    if (mIntents.isEmpty()) {
      throw new IllegalStateException("No intents added to TaskStackBuilder; cannot getPendingIntent");
    }
    Intent[] arrayOfIntent = (Intent[])mIntents.toArray(new Intent[mIntents.size()]);
    arrayOfIntent[0] = new Intent(arrayOfIntent[0]).addFlags(268484608);
    return IMPL.getPendingIntent(mSourceContext, arrayOfIntent, paramInt1, paramInt2, paramBundle);
  }
  
  @Deprecated
  public Iterator<Intent> iterator()
  {
    return mIntents.iterator();
  }
  
  public void startActivities()
  {
    startActivities(null);
  }
  
  public void startActivities(Bundle paramBundle)
  {
    if (mIntents.isEmpty()) {
      throw new IllegalStateException("No intents added to TaskStackBuilder; cannot startActivities");
    }
    Intent[] arrayOfIntent = (Intent[])mIntents.toArray(new Intent[mIntents.size()]);
    arrayOfIntent[0] = new Intent(arrayOfIntent[0]).addFlags(268484608);
    if (!ContextCompat.startActivities(mSourceContext, arrayOfIntent, paramBundle))
    {
      Intent localIntent = new Intent(arrayOfIntent[(-1 + arrayOfIntent.length)]);
      localIntent.addFlags(268435456);
      mSourceContext.startActivity(localIntent);
    }
  }
  
  public static abstract interface SupportParentable
  {
    public abstract Intent getSupportParentActivityIntent();
  }
  
  static abstract interface TaskStackBuilderImpl
  {
    public abstract PendingIntent getPendingIntent(Context paramContext, Intent[] paramArrayOfIntent, int paramInt1, int paramInt2, Bundle paramBundle);
  }
  
  static class TaskStackBuilderImplBase
    implements TaskStackBuilder.TaskStackBuilderImpl
  {
    TaskStackBuilderImplBase() {}
    
    public PendingIntent getPendingIntent(Context paramContext, Intent[] paramArrayOfIntent, int paramInt1, int paramInt2, Bundle paramBundle)
    {
      Intent localIntent = new Intent(paramArrayOfIntent[(-1 + paramArrayOfIntent.length)]);
      localIntent.addFlags(268435456);
      return PendingIntent.getActivity(paramContext, paramInt1, localIntent, paramInt2);
    }
  }
  
  static class TaskStackBuilderImplHoneycomb
    implements TaskStackBuilder.TaskStackBuilderImpl
  {
    TaskStackBuilderImplHoneycomb() {}
    
    public PendingIntent getPendingIntent(Context paramContext, Intent[] paramArrayOfIntent, int paramInt1, int paramInt2, Bundle paramBundle)
    {
      paramArrayOfIntent[0] = new Intent(paramArrayOfIntent[0]).addFlags(268484608);
      return TaskStackBuilderHoneycomb.getActivitiesPendingIntent(paramContext, paramInt1, paramArrayOfIntent, paramInt2);
    }
  }
  
  static class TaskStackBuilderImplJellybean
    implements TaskStackBuilder.TaskStackBuilderImpl
  {
    TaskStackBuilderImplJellybean() {}
    
    public PendingIntent getPendingIntent(Context paramContext, Intent[] paramArrayOfIntent, int paramInt1, int paramInt2, Bundle paramBundle)
    {
      paramArrayOfIntent[0] = new Intent(paramArrayOfIntent[0]).addFlags(268484608);
      return TaskStackBuilderJellybean.getActivitiesPendingIntent(paramContext, paramInt1, paramArrayOfIntent, paramInt2, paramBundle);
    }
  }
}
