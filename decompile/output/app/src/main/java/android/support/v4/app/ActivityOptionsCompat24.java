package android.support.v4.app;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;

class ActivityOptionsCompat24
{
  private final ActivityOptions mActivityOptions;
  
  private ActivityOptionsCompat24(ActivityOptions paramActivityOptions)
  {
    mActivityOptions = paramActivityOptions;
  }
  
  public static ActivityOptionsCompat24 makeBasic()
  {
    return new ActivityOptionsCompat24(ActivityOptions.makeBasic());
  }
  
  public static ActivityOptionsCompat24 makeClipRevealAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return new ActivityOptionsCompat24(ActivityOptions.makeClipRevealAnimation(paramView, paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public static ActivityOptionsCompat24 makeCustomAnimation(Context paramContext, int paramInt1, int paramInt2)
  {
    return new ActivityOptionsCompat24(ActivityOptions.makeCustomAnimation(paramContext, paramInt1, paramInt2));
  }
  
  public static ActivityOptionsCompat24 makeScaleUpAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return new ActivityOptionsCompat24(ActivityOptions.makeScaleUpAnimation(paramView, paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public static ActivityOptionsCompat24 makeSceneTransitionAnimation(Activity paramActivity, View paramView, String paramString)
  {
    return new ActivityOptionsCompat24(ActivityOptions.makeSceneTransitionAnimation(paramActivity, paramView, paramString));
  }
  
  public static ActivityOptionsCompat24 makeSceneTransitionAnimation(Activity paramActivity, View[] paramArrayOfView, String[] paramArrayOfString)
  {
    Pair[] arrayOfPair = null;
    if (paramArrayOfView != null)
    {
      arrayOfPair = new Pair[paramArrayOfView.length];
      for (int i = 0; i < arrayOfPair.length; i++) {
        arrayOfPair[i] = Pair.create(paramArrayOfView[i], paramArrayOfString[i]);
      }
    }
    return new ActivityOptionsCompat24(ActivityOptions.makeSceneTransitionAnimation(paramActivity, arrayOfPair));
  }
  
  public static ActivityOptionsCompat24 makeTaskLaunchBehind()
  {
    return new ActivityOptionsCompat24(ActivityOptions.makeTaskLaunchBehind());
  }
  
  public static ActivityOptionsCompat24 makeThumbnailScaleUpAnimation(View paramView, Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    return new ActivityOptionsCompat24(ActivityOptions.makeThumbnailScaleUpAnimation(paramView, paramBitmap, paramInt1, paramInt2));
  }
  
  public Rect getLaunchBounds()
  {
    return mActivityOptions.getLaunchBounds();
  }
  
  public void requestUsageTimeReport(PendingIntent paramPendingIntent)
  {
    mActivityOptions.requestUsageTimeReport(paramPendingIntent);
  }
  
  public ActivityOptionsCompat24 setLaunchBounds(@Nullable Rect paramRect)
  {
    return new ActivityOptionsCompat24(mActivityOptions.setLaunchBounds(paramRect));
  }
  
  public Bundle toBundle()
  {
    return mActivityOptions.toBundle();
  }
  
  public void update(ActivityOptionsCompat24 paramActivityOptionsCompat24)
  {
    mActivityOptions.update(mActivityOptions);
  }
}
