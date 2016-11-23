package android.support.v4.view.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener;
import java.util.List;

class AccessibilityManagerCompatIcs
{
  AccessibilityManagerCompatIcs() {}
  
  public static boolean addAccessibilityStateChangeListener(AccessibilityManager paramAccessibilityManager, AccessibilityStateChangeListenerWrapper paramAccessibilityStateChangeListenerWrapper)
  {
    return paramAccessibilityManager.addAccessibilityStateChangeListener(paramAccessibilityStateChangeListenerWrapper);
  }
  
  public static List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(AccessibilityManager paramAccessibilityManager, int paramInt)
  {
    return paramAccessibilityManager.getEnabledAccessibilityServiceList(paramInt);
  }
  
  public static List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(AccessibilityManager paramAccessibilityManager)
  {
    return paramAccessibilityManager.getInstalledAccessibilityServiceList();
  }
  
  public static boolean isTouchExplorationEnabled(AccessibilityManager paramAccessibilityManager)
  {
    return paramAccessibilityManager.isTouchExplorationEnabled();
  }
  
  public static boolean removeAccessibilityStateChangeListener(AccessibilityManager paramAccessibilityManager, AccessibilityStateChangeListenerWrapper paramAccessibilityStateChangeListenerWrapper)
  {
    return paramAccessibilityManager.removeAccessibilityStateChangeListener(paramAccessibilityStateChangeListenerWrapper);
  }
  
  static abstract interface AccessibilityStateChangeListenerBridge
  {
    public abstract void onAccessibilityStateChanged(boolean paramBoolean);
  }
  
  public static class AccessibilityStateChangeListenerWrapper
    implements AccessibilityManager.AccessibilityStateChangeListener
  {
    Object mListener;
    AccessibilityManagerCompatIcs.AccessibilityStateChangeListenerBridge mListenerBridge;
    
    public AccessibilityStateChangeListenerWrapper(Object paramObject, AccessibilityManagerCompatIcs.AccessibilityStateChangeListenerBridge paramAccessibilityStateChangeListenerBridge)
    {
      mListener = paramObject;
      mListenerBridge = paramAccessibilityStateChangeListenerBridge;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      AccessibilityStateChangeListenerWrapper localAccessibilityStateChangeListenerWrapper;
      do
      {
        return true;
        if ((paramObject == null) || (getClass() != paramObject.getClass())) {
          return false;
        }
        localAccessibilityStateChangeListenerWrapper = (AccessibilityStateChangeListenerWrapper)paramObject;
        if (mListener != null) {
          break;
        }
      } while (mListener == null);
      return false;
      return mListener.equals(mListener);
    }
    
    public int hashCode()
    {
      if (mListener == null) {
        return 0;
      }
      return mListener.hashCode();
    }
    
    public void onAccessibilityStateChanged(boolean paramBoolean)
    {
      mListenerBridge.onAccessibilityStateChanged(paramBoolean);
    }
  }
}
