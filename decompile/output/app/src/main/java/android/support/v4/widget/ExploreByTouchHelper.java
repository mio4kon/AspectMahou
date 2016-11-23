package android.support.v4.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewParentCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import java.util.ArrayList;
import java.util.List;

public abstract class ExploreByTouchHelper
  extends AccessibilityDelegateCompat
{
  private static final String DEFAULT_CLASS_NAME = "android.view.View";
  public static final int HOST_ID = -1;
  public static final int INVALID_ID = Integer.MIN_VALUE;
  private static final Rect INVALID_PARENT_BOUNDS = new Rect(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
  private static final FocusStrategy.BoundsAdapter<AccessibilityNodeInfoCompat> NODE_ADAPTER = new FocusStrategy.BoundsAdapter()
  {
    public void obtainBounds(AccessibilityNodeInfoCompat paramAnonymousAccessibilityNodeInfoCompat, Rect paramAnonymousRect)
    {
      paramAnonymousAccessibilityNodeInfoCompat.getBoundsInParent(paramAnonymousRect);
    }
  };
  private static final FocusStrategy.CollectionAdapter<SparseArrayCompat<AccessibilityNodeInfoCompat>, AccessibilityNodeInfoCompat> SPARSE_VALUES_ADAPTER = new FocusStrategy.CollectionAdapter()
  {
    public AccessibilityNodeInfoCompat get(SparseArrayCompat<AccessibilityNodeInfoCompat> paramAnonymousSparseArrayCompat, int paramAnonymousInt)
    {
      return (AccessibilityNodeInfoCompat)paramAnonymousSparseArrayCompat.valueAt(paramAnonymousInt);
    }
    
    public int size(SparseArrayCompat<AccessibilityNodeInfoCompat> paramAnonymousSparseArrayCompat)
    {
      return paramAnonymousSparseArrayCompat.size();
    }
  };
  private int mAccessibilityFocusedVirtualViewId = Integer.MIN_VALUE;
  private final View mHost;
  private int mHoveredVirtualViewId = Integer.MIN_VALUE;
  private int mKeyboardFocusedVirtualViewId = Integer.MIN_VALUE;
  private final AccessibilityManager mManager;
  private MyNodeProvider mNodeProvider;
  private final int[] mTempGlobalRect = new int[2];
  private final Rect mTempParentRect = new Rect();
  private final Rect mTempScreenRect = new Rect();
  private final Rect mTempVisibleRect = new Rect();
  
  public ExploreByTouchHelper(View paramView)
  {
    if (paramView == null) {
      throw new IllegalArgumentException("View may not be null");
    }
    mHost = paramView;
    mManager = ((AccessibilityManager)paramView.getContext().getSystemService("accessibility"));
    paramView.setFocusable(true);
    if (ViewCompat.getImportantForAccessibility(paramView) == 0) {
      ViewCompat.setImportantForAccessibility(paramView, 1);
    }
  }
  
  private boolean clearAccessibilityFocus(int paramInt)
  {
    if (mAccessibilityFocusedVirtualViewId == paramInt)
    {
      mAccessibilityFocusedVirtualViewId = Integer.MIN_VALUE;
      mHost.invalidate();
      sendEventForVirtualView(paramInt, 65536);
      return true;
    }
    return false;
  }
  
  private boolean clickKeyboardFocusedVirtualView()
  {
    return (mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE) && (onPerformActionForVirtualView(mKeyboardFocusedVirtualViewId, 16, null));
  }
  
  private AccessibilityEvent createEvent(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      return createEventForChild(paramInt1, paramInt2);
    }
    return createEventForHost(paramInt2);
  }
  
  private AccessibilityEvent createEventForChild(int paramInt1, int paramInt2)
  {
    AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(paramInt2);
    AccessibilityRecordCompat localAccessibilityRecordCompat = AccessibilityEventCompat.asRecord(localAccessibilityEvent);
    AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat = obtainAccessibilityNodeInfo(paramInt1);
    localAccessibilityRecordCompat.getText().add(localAccessibilityNodeInfoCompat.getText());
    localAccessibilityRecordCompat.setContentDescription(localAccessibilityNodeInfoCompat.getContentDescription());
    localAccessibilityRecordCompat.setScrollable(localAccessibilityNodeInfoCompat.isScrollable());
    localAccessibilityRecordCompat.setPassword(localAccessibilityNodeInfoCompat.isPassword());
    localAccessibilityRecordCompat.setEnabled(localAccessibilityNodeInfoCompat.isEnabled());
    localAccessibilityRecordCompat.setChecked(localAccessibilityNodeInfoCompat.isChecked());
    onPopulateEventForVirtualView(paramInt1, localAccessibilityEvent);
    if ((localAccessibilityEvent.getText().isEmpty()) && (localAccessibilityEvent.getContentDescription() == null)) {
      throw new RuntimeException("Callbacks must add text or a content description in populateEventForVirtualViewId()");
    }
    localAccessibilityRecordCompat.setClassName(localAccessibilityNodeInfoCompat.getClassName());
    localAccessibilityRecordCompat.setSource(mHost, paramInt1);
    localAccessibilityEvent.setPackageName(mHost.getContext().getPackageName());
    return localAccessibilityEvent;
  }
  
  private AccessibilityEvent createEventForHost(int paramInt)
  {
    AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(paramInt);
    ViewCompat.onInitializeAccessibilityEvent(mHost, localAccessibilityEvent);
    return localAccessibilityEvent;
  }
  
  @NonNull
  private AccessibilityNodeInfoCompat createNodeForChild(int paramInt)
  {
    AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.obtain();
    localAccessibilityNodeInfoCompat.setEnabled(true);
    localAccessibilityNodeInfoCompat.setFocusable(true);
    localAccessibilityNodeInfoCompat.setClassName("android.view.View");
    localAccessibilityNodeInfoCompat.setBoundsInParent(INVALID_PARENT_BOUNDS);
    localAccessibilityNodeInfoCompat.setBoundsInScreen(INVALID_PARENT_BOUNDS);
    onPopulateNodeForVirtualView(paramInt, localAccessibilityNodeInfoCompat);
    if ((localAccessibilityNodeInfoCompat.getText() == null) && (localAccessibilityNodeInfoCompat.getContentDescription() == null)) {
      throw new RuntimeException("Callbacks must add text or a content description in populateNodeForVirtualViewId()");
    }
    localAccessibilityNodeInfoCompat.getBoundsInParent(mTempParentRect);
    if (mTempParentRect.equals(INVALID_PARENT_BOUNDS)) {
      throw new RuntimeException("Callbacks must set parent bounds in populateNodeForVirtualViewId()");
    }
    int i = localAccessibilityNodeInfoCompat.getActions();
    if ((i & 0x40) != 0) {
      throw new RuntimeException("Callbacks must not add ACTION_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
    }
    if ((i & 0x80) != 0) {
      throw new RuntimeException("Callbacks must not add ACTION_CLEAR_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
    }
    localAccessibilityNodeInfoCompat.setPackageName(mHost.getContext().getPackageName());
    localAccessibilityNodeInfoCompat.setSource(mHost, paramInt);
    localAccessibilityNodeInfoCompat.setParent(mHost);
    boolean bool;
    if (mAccessibilityFocusedVirtualViewId == paramInt)
    {
      localAccessibilityNodeInfoCompat.setAccessibilityFocused(true);
      localAccessibilityNodeInfoCompat.addAction(128);
      if (mKeyboardFocusedVirtualViewId != paramInt) {
        break label340;
      }
      bool = true;
      label201:
      if (!bool) {
        break label346;
      }
      localAccessibilityNodeInfoCompat.addAction(2);
    }
    for (;;)
    {
      localAccessibilityNodeInfoCompat.setFocused(bool);
      if (intersectVisibleToUser(mTempParentRect))
      {
        localAccessibilityNodeInfoCompat.setVisibleToUser(true);
        localAccessibilityNodeInfoCompat.setBoundsInParent(mTempParentRect);
      }
      localAccessibilityNodeInfoCompat.getBoundsInScreen(mTempScreenRect);
      if (mTempScreenRect.equals(INVALID_PARENT_BOUNDS))
      {
        mHost.getLocationOnScreen(mTempGlobalRect);
        localAccessibilityNodeInfoCompat.getBoundsInParent(mTempScreenRect);
        mTempScreenRect.offset(mTempGlobalRect[0] - mHost.getScrollX(), mTempGlobalRect[1] - mHost.getScrollY());
        localAccessibilityNodeInfoCompat.setBoundsInScreen(mTempScreenRect);
      }
      return localAccessibilityNodeInfoCompat;
      localAccessibilityNodeInfoCompat.setAccessibilityFocused(false);
      localAccessibilityNodeInfoCompat.addAction(64);
      break;
      label340:
      bool = false;
      break label201;
      label346:
      if (localAccessibilityNodeInfoCompat.isFocusable()) {
        localAccessibilityNodeInfoCompat.addAction(1);
      }
    }
  }
  
  @NonNull
  private AccessibilityNodeInfoCompat createNodeForHost()
  {
    AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.obtain(mHost);
    ViewCompat.onInitializeAccessibilityNodeInfo(mHost, localAccessibilityNodeInfoCompat);
    ArrayList localArrayList = new ArrayList();
    getVisibleVirtualViews(localArrayList);
    if ((localAccessibilityNodeInfoCompat.getChildCount() > 0) && (localArrayList.size() > 0)) {
      throw new RuntimeException("Views cannot have both real and virtual children");
    }
    int i = 0;
    int j = localArrayList.size();
    while (i < j)
    {
      localAccessibilityNodeInfoCompat.addChild(mHost, ((Integer)localArrayList.get(i)).intValue());
      i++;
    }
    return localAccessibilityNodeInfoCompat;
  }
  
  private SparseArrayCompat<AccessibilityNodeInfoCompat> getAllNodes()
  {
    ArrayList localArrayList = new ArrayList();
    getVisibleVirtualViews(localArrayList);
    SparseArrayCompat localSparseArrayCompat = new SparseArrayCompat();
    for (int i = 0; i < localArrayList.size(); i++) {
      localSparseArrayCompat.put(i, createNodeForChild(i));
    }
    return localSparseArrayCompat;
  }
  
  private void getBoundsInParent(int paramInt, Rect paramRect)
  {
    obtainAccessibilityNodeInfo(paramInt).getBoundsInParent(paramRect);
  }
  
  private static Rect guessPreviouslyFocusedRect(@NonNull View paramView, int paramInt, @NonNull Rect paramRect)
  {
    int i = paramView.getWidth();
    int j = paramView.getHeight();
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
      paramRect.set(i, 0, i, j);
      return paramRect;
    case 33: 
      paramRect.set(0, j, i, j);
      return paramRect;
    case 66: 
      paramRect.set(-1, 0, -1, j);
      return paramRect;
    }
    paramRect.set(0, -1, i, -1);
    return paramRect;
  }
  
  private boolean intersectVisibleToUser(Rect paramRect)
  {
    if ((paramRect == null) || (paramRect.isEmpty())) {}
    ViewParent localViewParent;
    label67:
    do
    {
      do
      {
        return false;
      } while (mHost.getWindowVisibility() != 0);
      View localView;
      for (localViewParent = mHost.getParent();; localViewParent = localView.getParent())
      {
        if (!(localViewParent instanceof View)) {
          break label67;
        }
        localView = (View)localViewParent;
        if ((ViewCompat.getAlpha(localView) <= 0.0F) || (localView.getVisibility() != 0)) {
          break;
        }
      }
    } while ((localViewParent == null) || (!mHost.getLocalVisibleRect(mTempVisibleRect)));
    return paramRect.intersect(mTempVisibleRect);
  }
  
  private static int keyToDirection(int paramInt)
  {
    switch (paramInt)
    {
    case 20: 
    default: 
      return 130;
    case 21: 
      return 17;
    case 19: 
      return 33;
    }
    return 66;
  }
  
  private boolean moveFocus(int paramInt, @Nullable Rect paramRect)
  {
    SparseArrayCompat localSparseArrayCompat = getAllNodes();
    int i = mKeyboardFocusedVirtualViewId;
    if (i == Integer.MIN_VALUE) {}
    for (AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat1 = null;; localAccessibilityNodeInfoCompat1 = (AccessibilityNodeInfoCompat)localSparseArrayCompat.get(i)) {
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException("direction must be one of {FOCUS_FORWARD, FOCUS_BACKWARD, FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
      }
    }
    boolean bool;
    AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat2;
    if (ViewCompat.getLayoutDirection(mHost) == 1)
    {
      bool = true;
      localAccessibilityNodeInfoCompat2 = (AccessibilityNodeInfoCompat)FocusStrategy.findNextFocusInRelativeDirection(localSparseArrayCompat, SPARSE_VALUES_ADAPTER, NODE_ADAPTER, localAccessibilityNodeInfoCompat1, paramInt, bool, false);
      if (localAccessibilityNodeInfoCompat2 != null) {
        break label240;
      }
    }
    label240:
    for (int j = Integer.MIN_VALUE;; j = localSparseArrayCompat.keyAt(localSparseArrayCompat.indexOfValue(localAccessibilityNodeInfoCompat2)))
    {
      return requestKeyboardFocusForVirtualView(j);
      bool = false;
      break;
      Rect localRect = new Rect();
      if (mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE) {
        getBoundsInParent(mKeyboardFocusedVirtualViewId, localRect);
      }
      for (;;)
      {
        localAccessibilityNodeInfoCompat2 = (AccessibilityNodeInfoCompat)FocusStrategy.findNextFocusInAbsoluteDirection(localSparseArrayCompat, SPARSE_VALUES_ADAPTER, NODE_ADAPTER, localAccessibilityNodeInfoCompat1, localRect, paramInt);
        break;
        if (paramRect != null) {
          localRect.set(paramRect);
        } else {
          guessPreviouslyFocusedRect(mHost, paramInt, localRect);
        }
      }
    }
  }
  
  private boolean performActionForChild(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    switch (paramInt2)
    {
    default: 
      return onPerformActionForVirtualView(paramInt1, paramInt2, paramBundle);
    case 64: 
      return requestAccessibilityFocus(paramInt1);
    case 128: 
      return clearAccessibilityFocus(paramInt1);
    case 1: 
      return requestKeyboardFocusForVirtualView(paramInt1);
    }
    return clearKeyboardFocusForVirtualView(paramInt1);
  }
  
  private boolean performActionForHost(int paramInt, Bundle paramBundle)
  {
    return ViewCompat.performAccessibilityAction(mHost, paramInt, paramBundle);
  }
  
  private boolean requestAccessibilityFocus(int paramInt)
  {
    if ((!mManager.isEnabled()) || (!AccessibilityManagerCompat.isTouchExplorationEnabled(mManager))) {}
    while (mAccessibilityFocusedVirtualViewId == paramInt) {
      return false;
    }
    if (mAccessibilityFocusedVirtualViewId != Integer.MIN_VALUE) {
      clearAccessibilityFocus(mAccessibilityFocusedVirtualViewId);
    }
    mAccessibilityFocusedVirtualViewId = paramInt;
    mHost.invalidate();
    sendEventForVirtualView(paramInt, 32768);
    return true;
  }
  
  private void updateHoveredVirtualView(int paramInt)
  {
    if (mHoveredVirtualViewId == paramInt) {
      return;
    }
    int i = mHoveredVirtualViewId;
    mHoveredVirtualViewId = paramInt;
    sendEventForVirtualView(paramInt, 128);
    sendEventForVirtualView(i, 256);
  }
  
  public final boolean clearKeyboardFocusForVirtualView(int paramInt)
  {
    if (mKeyboardFocusedVirtualViewId != paramInt) {
      return false;
    }
    mKeyboardFocusedVirtualViewId = Integer.MIN_VALUE;
    onVirtualViewKeyboardFocusChanged(paramInt, false);
    sendEventForVirtualView(paramInt, 8);
    return true;
  }
  
  public final boolean dispatchHoverEvent(@NonNull MotionEvent paramMotionEvent)
  {
    boolean bool = true;
    if ((!mManager.isEnabled()) || (!AccessibilityManagerCompat.isTouchExplorationEnabled(mManager))) {}
    do
    {
      return false;
      switch (paramMotionEvent.getAction())
      {
      case 8: 
      default: 
        return false;
      case 7: 
      case 9: 
        int i = getVirtualViewAt(paramMotionEvent.getX(), paramMotionEvent.getY());
        updateHoveredVirtualView(i);
        if (i != Integer.MIN_VALUE) {}
        for (;;)
        {
          return bool;
          bool = false;
        }
      }
    } while (mAccessibilityFocusedVirtualViewId == Integer.MIN_VALUE);
    updateHoveredVirtualView(Integer.MIN_VALUE);
    return bool;
  }
  
  public final boolean dispatchKeyEvent(@NonNull KeyEvent paramKeyEvent)
  {
    int i = paramKeyEvent.getAction();
    boolean bool1 = false;
    int j;
    if (i != 1)
    {
      j = paramKeyEvent.getKeyCode();
      bool1 = false;
      switch (j)
      {
      }
    }
    boolean bool2;
    do
    {
      int k;
      do
      {
        boolean bool3;
        do
        {
          boolean bool4;
          do
          {
            return bool1;
            bool4 = KeyEventCompat.hasNoModifiers(paramKeyEvent);
            bool1 = false;
          } while (!bool4);
          int m = keyToDirection(j);
          int n = 1 + paramKeyEvent.getRepeatCount();
          for (int i1 = 0; (i1 < n) && (moveFocus(m, null)); i1++) {
            bool1 = true;
          }
          bool3 = KeyEventCompat.hasNoModifiers(paramKeyEvent);
          bool1 = false;
        } while (!bool3);
        k = paramKeyEvent.getRepeatCount();
        bool1 = false;
      } while (k != 0);
      clickKeyboardFocusedVirtualView();
      return true;
      if (KeyEventCompat.hasNoModifiers(paramKeyEvent)) {
        return moveFocus(2, null);
      }
      bool2 = KeyEventCompat.hasModifiers(paramKeyEvent, 1);
      bool1 = false;
    } while (!bool2);
    return moveFocus(1, null);
  }
  
  public final int getAccessibilityFocusedVirtualViewId()
  {
    return mAccessibilityFocusedVirtualViewId;
  }
  
  public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View paramView)
  {
    if (mNodeProvider == null) {
      mNodeProvider = new MyNodeProvider();
    }
    return mNodeProvider;
  }
  
  @Deprecated
  public int getFocusedVirtualView()
  {
    return getAccessibilityFocusedVirtualViewId();
  }
  
  public final int getKeyboardFocusedVirtualViewId()
  {
    return mKeyboardFocusedVirtualViewId;
  }
  
  protected abstract int getVirtualViewAt(float paramFloat1, float paramFloat2);
  
  protected abstract void getVisibleVirtualViews(List<Integer> paramList);
  
  public final void invalidateRoot()
  {
    invalidateVirtualView(-1, 1);
  }
  
  public final void invalidateVirtualView(int paramInt)
  {
    invalidateVirtualView(paramInt, 0);
  }
  
  public final void invalidateVirtualView(int paramInt1, int paramInt2)
  {
    if ((paramInt1 != Integer.MIN_VALUE) && (mManager.isEnabled()))
    {
      ViewParent localViewParent = mHost.getParent();
      if (localViewParent != null)
      {
        AccessibilityEvent localAccessibilityEvent = createEvent(paramInt1, 2048);
        AccessibilityEventCompat.setContentChangeTypes(localAccessibilityEvent, paramInt2);
        ViewParentCompat.requestSendAccessibilityEvent(localViewParent, mHost, localAccessibilityEvent);
      }
    }
  }
  
  @NonNull
  AccessibilityNodeInfoCompat obtainAccessibilityNodeInfo(int paramInt)
  {
    if (paramInt == -1) {
      return createNodeForHost();
    }
    return createNodeForChild(paramInt);
  }
  
  public final void onFocusChanged(boolean paramBoolean, int paramInt, @Nullable Rect paramRect)
  {
    if (mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE) {
      clearKeyboardFocusForVirtualView(mKeyboardFocusedVirtualViewId);
    }
    if (paramBoolean) {
      moveFocus(paramInt, paramRect);
    }
  }
  
  public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
    onPopulateEventForHost(paramAccessibilityEvent);
  }
  
  public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
    onPopulateNodeForHost(paramAccessibilityNodeInfoCompat);
  }
  
  protected abstract boolean onPerformActionForVirtualView(int paramInt1, int paramInt2, Bundle paramBundle);
  
  protected void onPopulateEventForHost(AccessibilityEvent paramAccessibilityEvent) {}
  
  protected void onPopulateEventForVirtualView(int paramInt, AccessibilityEvent paramAccessibilityEvent) {}
  
  protected void onPopulateNodeForHost(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat) {}
  
  protected abstract void onPopulateNodeForVirtualView(int paramInt, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat);
  
  protected void onVirtualViewKeyboardFocusChanged(int paramInt, boolean paramBoolean) {}
  
  boolean performAction(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    switch (paramInt1)
    {
    default: 
      return performActionForChild(paramInt1, paramInt2, paramBundle);
    }
    return performActionForHost(paramInt2, paramBundle);
  }
  
  public final boolean requestKeyboardFocusForVirtualView(int paramInt)
  {
    if ((!mHost.isFocused()) && (!mHost.requestFocus())) {}
    while (mKeyboardFocusedVirtualViewId == paramInt) {
      return false;
    }
    if (mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE) {
      clearKeyboardFocusForVirtualView(mKeyboardFocusedVirtualViewId);
    }
    mKeyboardFocusedVirtualViewId = paramInt;
    onVirtualViewKeyboardFocusChanged(paramInt, true);
    sendEventForVirtualView(paramInt, 8);
    return true;
  }
  
  public final boolean sendEventForVirtualView(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == Integer.MIN_VALUE) || (!mManager.isEnabled())) {}
    ViewParent localViewParent;
    do
    {
      return false;
      localViewParent = mHost.getParent();
    } while (localViewParent == null);
    AccessibilityEvent localAccessibilityEvent = createEvent(paramInt1, paramInt2);
    return ViewParentCompat.requestSendAccessibilityEvent(localViewParent, mHost, localAccessibilityEvent);
  }
  
  private class MyNodeProvider
    extends AccessibilityNodeProviderCompat
  {
    MyNodeProvider() {}
    
    public AccessibilityNodeInfoCompat createAccessibilityNodeInfo(int paramInt)
    {
      return AccessibilityNodeInfoCompat.obtain(obtainAccessibilityNodeInfo(paramInt));
    }
    
    public boolean performAction(int paramInt1, int paramInt2, Bundle paramBundle)
    {
      return ExploreByTouchHelper.this.performAction(paramInt1, paramInt2, paramBundle);
    }
  }
}
