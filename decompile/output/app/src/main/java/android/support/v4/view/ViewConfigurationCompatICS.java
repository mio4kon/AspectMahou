package android.support.v4.view;

import android.view.ViewConfiguration;

class ViewConfigurationCompatICS
{
  ViewConfigurationCompatICS() {}
  
  static boolean hasPermanentMenuKey(ViewConfiguration paramViewConfiguration)
  {
    return paramViewConfiguration.hasPermanentMenuKey();
  }
}
