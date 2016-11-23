package android.support.v4.content.res;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

class ConfigurationHelperGingerbread
{
  ConfigurationHelperGingerbread() {}
  
  static int getDensityDpi(@NonNull Resources paramResources)
  {
    return getDisplayMetricsdensityDpi;
  }
  
  static int getScreenHeightDp(@NonNull Resources paramResources)
  {
    DisplayMetrics localDisplayMetrics = paramResources.getDisplayMetrics();
    return (int)(heightPixels / density);
  }
  
  static int getScreenWidthDp(@NonNull Resources paramResources)
  {
    DisplayMetrics localDisplayMetrics = paramResources.getDisplayMetrics();
    return (int)(widthPixels / density);
  }
  
  static int getSmallestScreenWidthDp(@NonNull Resources paramResources)
  {
    return Math.min(getScreenWidthDp(paramResources), getScreenHeightDp(paramResources));
  }
}
