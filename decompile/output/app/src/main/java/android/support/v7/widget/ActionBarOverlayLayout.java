package android.support.v7.widget;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.id;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window.Callback;

public class ActionBarOverlayLayout
  extends ViewGroup
  implements DecorContentParent, NestedScrollingParent
{
  static final int[] ATTRS;
  private static final String TAG = "ActionBarOverlayLayout";
  private final int ACTION_BAR_ANIMATE_DELAY = 600;
  private int mActionBarHeight;
  ActionBarContainer mActionBarTop;
  private ActionBarVisibilityCallback mActionBarVisibilityCallback;
  private final Runnable mAddActionBarHideOffset = new Runnable()
  {
    public void run()
    {
      haltActionBarHideOffsetAnimations();
      mCurrentActionBarTopAnimator = ViewCompat.animate(mActionBarTop).translationY(-mActionBarTop.getHeight()).setListener(mTopAnimatorListener);
    }
  };
  boolean mAnimatingForFling;
  private final Rect mBaseContentInsets = new Rect();
  private final Rect mBaseInnerInsets = new Rect();
  private ContentFrameLayout mContent;
  private final Rect mContentInsets = new Rect();
  ViewPropertyAnimatorCompat mCurrentActionBarTopAnimator;
  private DecorToolbar mDecorToolbar;
  private ScrollerCompat mFlingEstimator;
  private boolean mHasNonEmbeddedTabs;
  private boolean mHideOnContentScroll;
  private int mHideOnContentScrollReference;
  private boolean mIgnoreWindowContentOverlay;
  private final Rect mInnerInsets = new Rect();
  private final Rect mLastBaseContentInsets = new Rect();
  private final Rect mLastInnerInsets = new Rect();
  private int mLastSystemUiVisibility;
  private boolean mOverlayMode;
  private final NestedScrollingParentHelper mParentHelper;
  private final Runnable mRemoveActionBarHideOffset = new Runnable()
  {
    public void run()
    {
      haltActionBarHideOffsetAnimations();
      mCurrentActionBarTopAnimator = ViewCompat.animate(mActionBarTop).translationY(0.0F).setListener(mTopAnimatorListener);
    }
  };
  final ViewPropertyAnimatorListener mTopAnimatorListener = new ViewPropertyAnimatorListenerAdapter()
  {
    public void onAnimationCancel(View paramAnonymousView)
    {
      mCurrentActionBarTopAnimator = null;
      mAnimatingForFling = false;
    }
    
    public void onAnimationEnd(View paramAnonymousView)
    {
      mCurrentActionBarTopAnimator = null;
      mAnimatingForFling = false;
    }
  };
  private Drawable mWindowContentOverlay;
  private int mWindowVisibility = 0;
  
  static
  {
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = R.attr.actionBarSize;
    arrayOfInt[1] = 16842841;
    ATTRS = arrayOfInt;
  }
  
  public ActionBarOverlayLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ActionBarOverlayLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext);
    mParentHelper = new NestedScrollingParentHelper(this);
  }
  
  private void addActionBarHideOffset()
  {
    haltActionBarHideOffsetAnimations();
    mAddActionBarHideOffset.run();
  }
  
  private boolean applyInsets(View paramView, Rect paramRect, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    boolean bool = false;
    if (paramBoolean1)
    {
      int i = leftMargin;
      int j = left;
      bool = false;
      if (i != j)
      {
        bool = true;
        leftMargin = left;
      }
    }
    if ((paramBoolean2) && (topMargin != top))
    {
      bool = true;
      topMargin = top;
    }
    if ((paramBoolean4) && (rightMargin != right))
    {
      bool = true;
      rightMargin = right;
    }
    if ((paramBoolean3) && (bottomMargin != bottom))
    {
      bool = true;
      bottomMargin = bottom;
    }
    return bool;
  }
  
  private DecorToolbar getDecorToolbar(View paramView)
  {
    if ((paramView instanceof DecorToolbar)) {
      return (DecorToolbar)paramView;
    }
    if ((paramView instanceof Toolbar)) {
      return ((Toolbar)paramView).getWrapper();
    }
    throw new IllegalStateException("Can't make a decor toolbar out of " + paramView.getClass().getSimpleName());
  }
  
  private void init(Context paramContext)
  {
    int i = 1;
    TypedArray localTypedArray = getContext().getTheme().obtainStyledAttributes(ATTRS);
    mActionBarHeight = localTypedArray.getDimensionPixelSize(0, 0);
    mWindowContentOverlay = localTypedArray.getDrawable(i);
    if (mWindowContentOverlay == null)
    {
      int j = i;
      setWillNotDraw(j);
      localTypedArray.recycle();
      if (getApplicationInfotargetSdkVersion >= 19) {
        break label87;
      }
    }
    for (;;)
    {
      mIgnoreWindowContentOverlay = i;
      mFlingEstimator = ScrollerCompat.create(paramContext);
      return;
      int k = 0;
      break;
      label87:
      i = 0;
    }
  }
  
  private void postAddActionBarHideOffset()
  {
    haltActionBarHideOffsetAnimations();
    postDelayed(mAddActionBarHideOffset, 600L);
  }
  
  private void postRemoveActionBarHideOffset()
  {
    haltActionBarHideOffsetAnimations();
    postDelayed(mRemoveActionBarHideOffset, 600L);
  }
  
  private void removeActionBarHideOffset()
  {
    haltActionBarHideOffsetAnimations();
    mRemoveActionBarHideOffset.run();
  }
  
  private boolean shouldHideActionBarOnFling(float paramFloat1, float paramFloat2)
  {
    mFlingEstimator.fling(0, 0, 0, (int)paramFloat2, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    int i = mFlingEstimator.getFinalY();
    int j = mActionBarTop.getHeight();
    boolean bool = false;
    if (i > j) {
      bool = true;
    }
    return bool;
  }
  
  public boolean canShowOverflowMenu()
  {
    pullChildren();
    return mDecorToolbar.canShowOverflowMenu();
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  public void dismissPopups()
  {
    pullChildren();
    mDecorToolbar.dismissPopupMenus();
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if ((mWindowContentOverlay != null) && (!mIgnoreWindowContentOverlay)) {
      if (mActionBarTop.getVisibility() != 0) {
        break label82;
      }
    }
    label82:
    for (int i = (int)(0.5F + (mActionBarTop.getBottom() + ViewCompat.getTranslationY(mActionBarTop)));; i = 0)
    {
      mWindowContentOverlay.setBounds(0, i, getWidth(), i + mWindowContentOverlay.getIntrinsicHeight());
      mWindowContentOverlay.draw(paramCanvas);
      return;
    }
  }
  
  protected boolean fitSystemWindows(Rect paramRect)
  {
    pullChildren();
    if ((0x100 & ViewCompat.getWindowSystemUiVisibility(this)) != 0) {}
    for (;;)
    {
      boolean bool = applyInsets(mActionBarTop, paramRect, true, true, false, true);
      mBaseInnerInsets.set(paramRect);
      ViewUtils.computeFitSystemWindows(this, mBaseInnerInsets, mBaseContentInsets);
      if (!mLastBaseContentInsets.equals(mBaseContentInsets))
      {
        bool = true;
        mLastBaseContentInsets.set(mBaseContentInsets);
      }
      if (bool) {
        requestLayout();
      }
      return true;
    }
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-1, -1);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return new LayoutParams(paramLayoutParams);
  }
  
  public int getActionBarHideOffset()
  {
    if (mActionBarTop != null) {
      return -(int)ViewCompat.getTranslationY(mActionBarTop);
    }
    return 0;
  }
  
  public int getNestedScrollAxes()
  {
    return mParentHelper.getNestedScrollAxes();
  }
  
  public CharSequence getTitle()
  {
    pullChildren();
    return mDecorToolbar.getTitle();
  }
  
  void haltActionBarHideOffsetAnimations()
  {
    removeCallbacks(mRemoveActionBarHideOffset);
    removeCallbacks(mAddActionBarHideOffset);
    if (mCurrentActionBarTopAnimator != null) {
      mCurrentActionBarTopAnimator.cancel();
    }
  }
  
  public boolean hasIcon()
  {
    pullChildren();
    return mDecorToolbar.hasIcon();
  }
  
  public boolean hasLogo()
  {
    pullChildren();
    return mDecorToolbar.hasLogo();
  }
  
  public boolean hideOverflowMenu()
  {
    pullChildren();
    return mDecorToolbar.hideOverflowMenu();
  }
  
  public void initFeature(int paramInt)
  {
    pullChildren();
    switch (paramInt)
    {
    default: 
      return;
    case 2: 
      mDecorToolbar.initProgress();
      return;
    case 5: 
      mDecorToolbar.initIndeterminateProgress();
      return;
    }
    setOverlayMode(true);
  }
  
  public boolean isHideOnContentScrollEnabled()
  {
    return mHideOnContentScroll;
  }
  
  public boolean isInOverlayMode()
  {
    return mOverlayMode;
  }
  
  public boolean isOverflowMenuShowPending()
  {
    pullChildren();
    return mDecorToolbar.isOverflowMenuShowPending();
  }
  
  public boolean isOverflowMenuShowing()
  {
    pullChildren();
    return mDecorToolbar.isOverflowMenuShowing();
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    init(getContext());
    ViewCompat.requestApplyInsets(this);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    haltActionBarHideOffsetAnimations();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = getChildCount();
    int j = getPaddingLeft();
    (paramInt3 - paramInt1 - getPaddingRight());
    int k = getPaddingTop();
    (paramInt4 - paramInt2 - getPaddingBottom());
    for (int m = 0; m < i; m++)
    {
      View localView = getChildAt(m);
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        int n = localView.getMeasuredWidth();
        int i1 = localView.getMeasuredHeight();
        int i2 = j + leftMargin;
        int i3 = k + topMargin;
        localView.layout(i2, i3, i2 + n, i3 + i1);
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    pullChildren();
    measureChildWithMargins(mActionBarTop, paramInt1, 0, paramInt2, 0);
    LayoutParams localLayoutParams1 = (LayoutParams)mActionBarTop.getLayoutParams();
    int i = Math.max(0, mActionBarTop.getMeasuredWidth() + leftMargin + rightMargin);
    int j = Math.max(0, mActionBarTop.getMeasuredHeight() + topMargin + bottomMargin);
    int k = ViewUtils.combineMeasuredStates(0, ViewCompat.getMeasuredState(mActionBarTop));
    int m;
    int i1;
    label137:
    Rect localRect4;
    if ((0x100 & ViewCompat.getWindowSystemUiVisibility(this)) != 0)
    {
      m = 1;
      if (m == 0) {
        break label423;
      }
      i1 = mActionBarHeight;
      if ((mHasNonEmbeddedTabs) && (mActionBarTop.getTabContainer() != null)) {
        i1 += mActionBarHeight;
      }
      mContentInsets.set(mBaseContentInsets);
      mInnerInsets.set(mBaseInnerInsets);
      if ((mOverlayMode) || (m != 0)) {
        break label454;
      }
      Rect localRect3 = mContentInsets;
      top = (i1 + top);
      localRect4 = mContentInsets;
    }
    label423:
    label454:
    Rect localRect2;
    for (bottom = (0 + bottom);; bottom = (0 + bottom))
    {
      applyInsets(mContent, mContentInsets, true, true, true, true);
      if (!mLastInnerInsets.equals(mInnerInsets))
      {
        mLastInnerInsets.set(mInnerInsets);
        mContent.dispatchFitSystemWindows(mInnerInsets);
      }
      measureChildWithMargins(mContent, paramInt1, 0, paramInt2, 0);
      LayoutParams localLayoutParams2 = (LayoutParams)mContent.getLayoutParams();
      int i2 = Math.max(i, mContent.getMeasuredWidth() + leftMargin + rightMargin);
      int i3 = Math.max(j, mContent.getMeasuredHeight() + topMargin + bottomMargin);
      int i4 = ViewUtils.combineMeasuredStates(k, ViewCompat.getMeasuredState(mContent));
      int i5 = i2 + (getPaddingLeft() + getPaddingRight());
      int i6 = Math.max(i3 + (getPaddingTop() + getPaddingBottom()), getSuggestedMinimumHeight());
      setMeasuredDimension(ViewCompat.resolveSizeAndState(Math.max(i5, getSuggestedMinimumWidth()), paramInt1, i4), ViewCompat.resolveSizeAndState(i6, paramInt2, i4 << 16));
      return;
      m = 0;
      break;
      int n = mActionBarTop.getVisibility();
      i1 = 0;
      if (n == 8) {
        break label137;
      }
      i1 = mActionBarTop.getMeasuredHeight();
      break label137;
      Rect localRect1 = mInnerInsets;
      top = (i1 + top);
      localRect2 = mInnerInsets;
    }
  }
  
  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if ((!mHideOnContentScroll) || (!paramBoolean)) {
      return false;
    }
    if (shouldHideActionBarOnFling(paramFloat1, paramFloat2)) {
      addActionBarHideOffset();
    }
    for (;;)
    {
      mAnimatingForFling = true;
      return true;
      removeActionBarHideOffset();
    }
  }
  
  public boolean onNestedPreFling(View paramView, float paramFloat1, float paramFloat2)
  {
    return false;
  }
  
  public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt) {}
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    mHideOnContentScrollReference = (paramInt2 + mHideOnContentScrollReference);
    setActionBarHideOffset(mHideOnContentScrollReference);
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt)
  {
    mParentHelper.onNestedScrollAccepted(paramView1, paramView2, paramInt);
    mHideOnContentScrollReference = getActionBarHideOffset();
    haltActionBarHideOffsetAnimations();
    if (mActionBarVisibilityCallback != null) {
      mActionBarVisibilityCallback.onContentScrollStarted();
    }
  }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt)
  {
    if (((paramInt & 0x2) == 0) || (mActionBarTop.getVisibility() != 0)) {
      return false;
    }
    return mHideOnContentScroll;
  }
  
  public void onStopNestedScroll(View paramView)
  {
    if ((mHideOnContentScroll) && (!mAnimatingForFling))
    {
      if (mHideOnContentScrollReference > mActionBarTop.getHeight()) {
        break label49;
      }
      postRemoveActionBarHideOffset();
    }
    for (;;)
    {
      if (mActionBarVisibilityCallback != null) {
        mActionBarVisibilityCallback.onContentScrollStopped();
      }
      return;
      label49:
      postAddActionBarHideOffset();
    }
  }
  
  public void onWindowSystemUiVisibilityChanged(int paramInt)
  {
    boolean bool1 = true;
    if (Build.VERSION.SDK_INT >= 16) {
      super.onWindowSystemUiVisibilityChanged(paramInt);
    }
    pullChildren();
    int i = paramInt ^ mLastSystemUiVisibility;
    mLastSystemUiVisibility = paramInt;
    boolean bool2;
    boolean bool3;
    if ((paramInt & 0x4) == 0)
    {
      bool2 = bool1;
      if ((paramInt & 0x100) == 0) {
        break label122;
      }
      bool3 = bool1;
      label51:
      if (mActionBarVisibilityCallback != null)
      {
        ActionBarVisibilityCallback localActionBarVisibilityCallback = mActionBarVisibilityCallback;
        if (bool3) {
          break label128;
        }
        label69:
        localActionBarVisibilityCallback.enableContentAnimations(bool1);
        if ((!bool2) && (bool3)) {
          break label133;
        }
        mActionBarVisibilityCallback.showForSystem();
      }
    }
    for (;;)
    {
      if (((i & 0x100) != 0) && (mActionBarVisibilityCallback != null)) {
        ViewCompat.requestApplyInsets(this);
      }
      return;
      bool2 = false;
      break;
      label122:
      bool3 = false;
      break label51;
      label128:
      bool1 = false;
      break label69;
      label133:
      mActionBarVisibilityCallback.hideForSystem();
    }
  }
  
  protected void onWindowVisibilityChanged(int paramInt)
  {
    super.onWindowVisibilityChanged(paramInt);
    mWindowVisibility = paramInt;
    if (mActionBarVisibilityCallback != null) {
      mActionBarVisibilityCallback.onWindowVisibilityChanged(paramInt);
    }
  }
  
  void pullChildren()
  {
    if (mContent == null)
    {
      mContent = ((ContentFrameLayout)findViewById(R.id.action_bar_activity_content));
      mActionBarTop = ((ActionBarContainer)findViewById(R.id.action_bar_container));
      mDecorToolbar = getDecorToolbar(findViewById(R.id.action_bar));
    }
  }
  
  public void restoreToolbarHierarchyState(SparseArray<Parcelable> paramSparseArray)
  {
    pullChildren();
    mDecorToolbar.restoreHierarchyState(paramSparseArray);
  }
  
  public void saveToolbarHierarchyState(SparseArray<Parcelable> paramSparseArray)
  {
    pullChildren();
    mDecorToolbar.saveHierarchyState(paramSparseArray);
  }
  
  public void setActionBarHideOffset(int paramInt)
  {
    haltActionBarHideOffsetAnimations();
    int i = Math.max(0, Math.min(paramInt, mActionBarTop.getHeight()));
    ViewCompat.setTranslationY(mActionBarTop, -i);
  }
  
  public void setActionBarVisibilityCallback(ActionBarVisibilityCallback paramActionBarVisibilityCallback)
  {
    mActionBarVisibilityCallback = paramActionBarVisibilityCallback;
    if (getWindowToken() != null)
    {
      mActionBarVisibilityCallback.onWindowVisibilityChanged(mWindowVisibility);
      if (mLastSystemUiVisibility != 0)
      {
        onWindowSystemUiVisibilityChanged(mLastSystemUiVisibility);
        ViewCompat.requestApplyInsets(this);
      }
    }
  }
  
  public void setHasNonEmbeddedTabs(boolean paramBoolean)
  {
    mHasNonEmbeddedTabs = paramBoolean;
  }
  
  public void setHideOnContentScrollEnabled(boolean paramBoolean)
  {
    if (paramBoolean != mHideOnContentScroll)
    {
      mHideOnContentScroll = paramBoolean;
      if (!paramBoolean)
      {
        haltActionBarHideOffsetAnimations();
        setActionBarHideOffset(0);
      }
    }
  }
  
  public void setIcon(int paramInt)
  {
    pullChildren();
    mDecorToolbar.setIcon(paramInt);
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    pullChildren();
    mDecorToolbar.setIcon(paramDrawable);
  }
  
  public void setLogo(int paramInt)
  {
    pullChildren();
    mDecorToolbar.setLogo(paramInt);
  }
  
  public void setMenu(Menu paramMenu, MenuPresenter.Callback paramCallback)
  {
    pullChildren();
    mDecorToolbar.setMenu(paramMenu, paramCallback);
  }
  
  public void setMenuPrepared()
  {
    pullChildren();
    mDecorToolbar.setMenuPrepared();
  }
  
  public void setOverlayMode(boolean paramBoolean)
  {
    mOverlayMode = paramBoolean;
    if ((paramBoolean) && (getContextgetApplicationInfotargetSdkVersion < 19)) {}
    for (boolean bool = true;; bool = false)
    {
      mIgnoreWindowContentOverlay = bool;
      return;
    }
  }
  
  public void setShowingForActionMode(boolean paramBoolean) {}
  
  public void setUiOptions(int paramInt) {}
  
  public void setWindowCallback(Window.Callback paramCallback)
  {
    pullChildren();
    mDecorToolbar.setWindowCallback(paramCallback);
  }
  
  public void setWindowTitle(CharSequence paramCharSequence)
  {
    pullChildren();
    mDecorToolbar.setWindowTitle(paramCharSequence);
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
  
  public boolean showOverflowMenu()
  {
    pullChildren();
    return mDecorToolbar.showOverflowMenu();
  }
  
  public static abstract interface ActionBarVisibilityCallback
  {
    public abstract void enableContentAnimations(boolean paramBoolean);
    
    public abstract void hideForSystem();
    
    public abstract void onContentScrollStarted();
    
    public abstract void onContentScrollStopped();
    
    public abstract void onWindowVisibilityChanged(int paramInt);
    
    public abstract void showForSystem();
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
  }
}
