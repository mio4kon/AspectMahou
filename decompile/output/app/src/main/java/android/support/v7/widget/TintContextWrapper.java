package android.support.v7.widget;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class TintContextWrapper
  extends ContextWrapper
{
  private static final ArrayList<WeakReference<TintContextWrapper>> sCache = new ArrayList();
  private final Resources mResources;
  private final Resources.Theme mTheme;
  
  private TintContextWrapper(@NonNull Context paramContext)
  {
    super(paramContext);
    if (VectorEnabledTintResources.shouldBeUsed())
    {
      mResources = new VectorEnabledTintResources(this, paramContext.getResources());
      mTheme = mResources.newTheme();
      mTheme.setTo(paramContext.getTheme());
      return;
    }
    mResources = new TintResources(this, paramContext.getResources());
    mTheme = null;
  }
  
  private static boolean shouldWrap(@NonNull Context paramContext)
  {
    if (((paramContext instanceof TintContextWrapper)) || ((paramContext.getResources() instanceof TintResources)) || ((paramContext.getResources() instanceof VectorEnabledTintResources))) {}
    while ((AppCompatDelegate.isCompatVectorFromResourcesEnabled()) && (Build.VERSION.SDK_INT > 20)) {
      return false;
    }
    return true;
  }
  
  public static Context wrap(@NonNull Context paramContext)
  {
    if (shouldWrap(paramContext))
    {
      int i = 0;
      int j = sCache.size();
      while (i < j)
      {
        WeakReference localWeakReference = (WeakReference)sCache.get(i);
        if (localWeakReference != null) {}
        for (TintContextWrapper localTintContextWrapper2 = (TintContextWrapper)localWeakReference.get(); (localTintContextWrapper2 != null) && (localTintContextWrapper2.getBaseContext() == paramContext); localTintContextWrapper2 = null) {
          return localTintContextWrapper2;
        }
        i++;
      }
      TintContextWrapper localTintContextWrapper1 = new TintContextWrapper(paramContext);
      sCache.add(new WeakReference(localTintContextWrapper1));
      return localTintContextWrapper1;
    }
    return paramContext;
  }
  
  public Resources getResources()
  {
    return mResources;
  }
  
  public Resources.Theme getTheme()
  {
    if (mTheme == null) {
      return super.getTheme();
    }
    return mTheme;
  }
  
  public void setTheme(int paramInt)
  {
    if (mTheme == null)
    {
      super.setTheme(paramInt);
      return;
    }
    mTheme.applyStyle(paramInt, true);
  }
}
