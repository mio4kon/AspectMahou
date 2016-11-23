package android.support.v7.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.ActionBarPolicy;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.ViewPropertyAnimatorCompatSet;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.view.menu.SubMenuBuilder;
import android.support.v7.widget.ActionBarContainer;
import android.support.v7.widget.ActionBarContextView;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.ActionBarOverlayLayout.ActionBarVisibilityCallback;
import android.support.v7.widget.DecorToolbar;
import android.support.v7.widget.ScrollingTabContainerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.SpinnerAdapter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class WindowDecorActionBar
  extends ActionBar
  implements ActionBarOverlayLayout.ActionBarVisibilityCallback
{
  private static final boolean ALLOW_SHOW_HIDE_ANIMATIONS = false;
  private static final long FADE_IN_DURATION_MS = 200L;
  private static final long FADE_OUT_DURATION_MS = 100L;
  private static final int INVALID_POSITION = -1;
  private static final String TAG = "WindowDecorActionBar";
  private static final Interpolator sHideInterpolator;
  private static final Interpolator sShowInterpolator;
  ActionModeImpl mActionMode;
  private Activity mActivity;
  ActionBarContainer mContainerView;
  boolean mContentAnimations = true;
  View mContentView;
  Context mContext;
  ActionBarContextView mContextView;
  private int mCurWindowVisibility = 0;
  ViewPropertyAnimatorCompatSet mCurrentShowAnim;
  DecorToolbar mDecorToolbar;
  ActionMode mDeferredDestroyActionMode;
  ActionMode.Callback mDeferredModeDestroyCallback;
  private Dialog mDialog;
  private boolean mDisplayHomeAsUpSet;
  private boolean mHasEmbeddedTabs;
  boolean mHiddenByApp;
  boolean mHiddenBySystem;
  final ViewPropertyAnimatorListener mHideListener = new ViewPropertyAnimatorListenerAdapter()
  {
    public void onAnimationEnd(View paramAnonymousView)
    {
      if ((mContentAnimations) && (mContentView != null))
      {
        ViewCompat.setTranslationY(mContentView, 0.0F);
        ViewCompat.setTranslationY(mContainerView, 0.0F);
      }
      mContainerView.setVisibility(8);
      mContainerView.setTransitioning(false);
      mCurrentShowAnim = null;
      completeDeferredDestroyActionMode();
      if (mOverlayLayout != null) {
        ViewCompat.requestApplyInsets(mOverlayLayout);
      }
    }
  };
  boolean mHideOnContentScroll;
  private boolean mLastMenuVisibility;
  private ArrayList<ActionBar.OnMenuVisibilityListener> mMenuVisibilityListeners = new ArrayList();
  private boolean mNowShowing = true;
  ActionBarOverlayLayout mOverlayLayout;
  private int mSavedTabPosition = -1;
  private TabImpl mSelectedTab;
  private boolean mShowHideAnimationEnabled;
  final ViewPropertyAnimatorListener mShowListener = new ViewPropertyAnimatorListenerAdapter()
  {
    public void onAnimationEnd(View paramAnonymousView)
    {
      mCurrentShowAnim = null;
      mContainerView.requestLayout();
    }
  };
  private boolean mShowingForMode;
  ScrollingTabContainerView mTabScrollView;
  private ArrayList<TabImpl> mTabs = new ArrayList();
  private Context mThemedContext;
  final ViewPropertyAnimatorUpdateListener mUpdateListener = new ViewPropertyAnimatorUpdateListener()
  {
    public void onAnimationUpdate(View paramAnonymousView)
    {
      ((View)mContainerView.getParent()).invalidate();
    }
  };
  
  static
  {
    boolean bool1 = true;
    boolean bool2;
    if (!WindowDecorActionBar.class.desiredAssertionStatus())
    {
      bool2 = bool1;
      $assertionsDisabled = bool2;
      sHideInterpolator = new AccelerateInterpolator();
      sShowInterpolator = new DecelerateInterpolator();
      if (Build.VERSION.SDK_INT < 14) {
        break label54;
      }
    }
    for (;;)
    {
      ALLOW_SHOW_HIDE_ANIMATIONS = bool1;
      return;
      bool2 = false;
      break;
      label54:
      bool1 = false;
    }
  }
  
  public WindowDecorActionBar(Activity paramActivity, boolean paramBoolean)
  {
    mActivity = paramActivity;
    View localView = paramActivity.getWindow().getDecorView();
    init(localView);
    if (!paramBoolean) {
      mContentView = localView.findViewById(16908290);
    }
  }
  
  public WindowDecorActionBar(Dialog paramDialog)
  {
    mDialog = paramDialog;
    init(paramDialog.getWindow().getDecorView());
  }
  
  public WindowDecorActionBar(View paramView)
  {
    assert (paramView.isInEditMode());
    init(paramView);
  }
  
  static boolean checkShowingFlags(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (paramBoolean3) {}
    while ((!paramBoolean1) && (!paramBoolean2)) {
      return true;
    }
    return false;
  }
  
  private void cleanupTabs()
  {
    if (mSelectedTab != null) {
      selectTab(null);
    }
    mTabs.clear();
    if (mTabScrollView != null) {
      mTabScrollView.removeAllTabs();
    }
    mSavedTabPosition = -1;
  }
  
  private void configureTab(ActionBar.Tab paramTab, int paramInt)
  {
    TabImpl localTabImpl = (TabImpl)paramTab;
    if (localTabImpl.getCallback() == null) {
      throw new IllegalStateException("Action Bar Tab must have a Callback");
    }
    localTabImpl.setPosition(paramInt);
    mTabs.add(paramInt, localTabImpl);
    int i = mTabs.size();
    for (int j = paramInt + 1; j < i; j++) {
      ((TabImpl)mTabs.get(j)).setPosition(j);
    }
  }
  
  private void ensureTabsExist()
  {
    if (mTabScrollView != null) {
      return;
    }
    ScrollingTabContainerView localScrollingTabContainerView = new ScrollingTabContainerView(mContext);
    if (mHasEmbeddedTabs)
    {
      localScrollingTabContainerView.setVisibility(0);
      mDecorToolbar.setEmbeddedTabView(localScrollingTabContainerView);
      mTabScrollView = localScrollingTabContainerView;
      return;
    }
    if (getNavigationMode() == 2)
    {
      localScrollingTabContainerView.setVisibility(0);
      if (mOverlayLayout != null) {
        ViewCompat.requestApplyInsets(mOverlayLayout);
      }
    }
    for (;;)
    {
      mContainerView.setTabContainer(localScrollingTabContainerView);
      break;
      localScrollingTabContainerView.setVisibility(8);
    }
  }
  
  private DecorToolbar getDecorToolbar(View paramView)
  {
    if ((paramView instanceof DecorToolbar)) {
      return (DecorToolbar)paramView;
    }
    if ((paramView instanceof Toolbar)) {
      return ((Toolbar)paramView).getWrapper();
    }
    if ("Can't make a decor toolbar out of " + paramView != null) {}
    for (String str = paramView.getClass().getSimpleName();; str = "null") {
      throw new IllegalStateException(str);
    }
  }
  
  private void hideForActionMode()
  {
    if (mShowingForMode)
    {
      mShowingForMode = false;
      if (mOverlayLayout != null) {
        mOverlayLayout.setShowingForActionMode(false);
      }
      updateVisibility(false);
    }
  }
  
  private void init(View paramView)
  {
    mOverlayLayout = ((ActionBarOverlayLayout)paramView.findViewById(R.id.decor_content_parent));
    if (mOverlayLayout != null) {
      mOverlayLayout.setActionBarVisibilityCallback(this);
    }
    mDecorToolbar = getDecorToolbar(paramView.findViewById(R.id.action_bar));
    mContextView = ((ActionBarContextView)paramView.findViewById(R.id.action_context_bar));
    mContainerView = ((ActionBarContainer)paramView.findViewById(R.id.action_bar_container));
    if ((mDecorToolbar == null) || (mContextView == null) || (mContainerView == null)) {
      throw new IllegalStateException(getClass().getSimpleName() + " can only be used " + "with a compatible window decor layout");
    }
    mContext = mDecorToolbar.getContext();
    int i;
    ActionBarPolicy localActionBarPolicy;
    if ((0x4 & mDecorToolbar.getDisplayOptions()) != 0)
    {
      i = 1;
      if (i != 0) {
        mDisplayHomeAsUpSet = true;
      }
      localActionBarPolicy = ActionBarPolicy.get(mContext);
      if ((!localActionBarPolicy.enableHomeButtonByDefault()) && (i == 0)) {
        break label275;
      }
    }
    label275:
    for (boolean bool = true;; bool = false)
    {
      setHomeButtonEnabled(bool);
      setHasEmbeddedTabs(localActionBarPolicy.hasEmbeddedTabs());
      TypedArray localTypedArray = mContext.obtainStyledAttributes(null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
      if (localTypedArray.getBoolean(R.styleable.ActionBar_hideOnContentScroll, false)) {
        setHideOnContentScrollEnabled(true);
      }
      int j = localTypedArray.getDimensionPixelSize(R.styleable.ActionBar_elevation, 0);
      if (j != 0) {
        setElevation(j);
      }
      localTypedArray.recycle();
      return;
      i = 0;
      break;
    }
  }
  
  private void setHasEmbeddedTabs(boolean paramBoolean)
  {
    boolean bool1 = true;
    mHasEmbeddedTabs = paramBoolean;
    boolean bool2;
    label45:
    label78:
    boolean bool3;
    label98:
    ActionBarOverlayLayout localActionBarOverlayLayout;
    if (!mHasEmbeddedTabs)
    {
      mDecorToolbar.setEmbeddedTabView(null);
      mContainerView.setTabContainer(mTabScrollView);
      if (getNavigationMode() != 2) {
        break label155;
      }
      bool2 = bool1;
      if (mTabScrollView != null)
      {
        if (!bool2) {
          break label160;
        }
        mTabScrollView.setVisibility(0);
        if (mOverlayLayout != null) {
          ViewCompat.requestApplyInsets(mOverlayLayout);
        }
      }
      DecorToolbar localDecorToolbar = mDecorToolbar;
      if ((mHasEmbeddedTabs) || (!bool2)) {
        break label172;
      }
      bool3 = bool1;
      localDecorToolbar.setCollapsible(bool3);
      localActionBarOverlayLayout = mOverlayLayout;
      if ((mHasEmbeddedTabs) || (!bool2)) {
        break label178;
      }
    }
    for (;;)
    {
      localActionBarOverlayLayout.setHasNonEmbeddedTabs(bool1);
      return;
      mContainerView.setTabContainer(null);
      mDecorToolbar.setEmbeddedTabView(mTabScrollView);
      break;
      label155:
      bool2 = false;
      break label45;
      label160:
      mTabScrollView.setVisibility(8);
      break label78;
      label172:
      bool3 = false;
      break label98;
      label178:
      bool1 = false;
    }
  }
  
  private boolean shouldAnimateContextView()
  {
    return ViewCompat.isLaidOut(mContainerView);
  }
  
  private void showForActionMode()
  {
    if (!mShowingForMode)
    {
      mShowingForMode = true;
      if (mOverlayLayout != null) {
        mOverlayLayout.setShowingForActionMode(true);
      }
      updateVisibility(false);
    }
  }
  
  private void updateVisibility(boolean paramBoolean)
  {
    if (checkShowingFlags(mHiddenByApp, mHiddenBySystem, mShowingForMode)) {
      if (!mNowShowing)
      {
        mNowShowing = true;
        doShow(paramBoolean);
      }
    }
    while (!mNowShowing) {
      return;
    }
    mNowShowing = false;
    doHide(paramBoolean);
  }
  
  public void addOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener paramOnMenuVisibilityListener)
  {
    mMenuVisibilityListeners.add(paramOnMenuVisibilityListener);
  }
  
  public void addTab(ActionBar.Tab paramTab)
  {
    addTab(paramTab, mTabs.isEmpty());
  }
  
  public void addTab(ActionBar.Tab paramTab, int paramInt)
  {
    addTab(paramTab, paramInt, mTabs.isEmpty());
  }
  
  public void addTab(ActionBar.Tab paramTab, int paramInt, boolean paramBoolean)
  {
    ensureTabsExist();
    mTabScrollView.addTab(paramTab, paramInt, paramBoolean);
    configureTab(paramTab, paramInt);
    if (paramBoolean) {
      selectTab(paramTab);
    }
  }
  
  public void addTab(ActionBar.Tab paramTab, boolean paramBoolean)
  {
    ensureTabsExist();
    mTabScrollView.addTab(paramTab, paramBoolean);
    configureTab(paramTab, mTabs.size());
    if (paramBoolean) {
      selectTab(paramTab);
    }
  }
  
  public void animateToMode(boolean paramBoolean)
  {
    ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat2;
    ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat1;
    if (paramBoolean)
    {
      showForActionMode();
      if (!shouldAnimateContextView()) {
        break label105;
      }
      if (!paramBoolean) {
        break label75;
      }
      localViewPropertyAnimatorCompat2 = mDecorToolbar.setupAnimatorToVisibility(4, 100L);
      localViewPropertyAnimatorCompat1 = mContextView.setupAnimatorToVisibility(0, 200L);
    }
    for (;;)
    {
      ViewPropertyAnimatorCompatSet localViewPropertyAnimatorCompatSet = new ViewPropertyAnimatorCompatSet();
      localViewPropertyAnimatorCompatSet.playSequentially(localViewPropertyAnimatorCompat2, localViewPropertyAnimatorCompat1);
      localViewPropertyAnimatorCompatSet.start();
      return;
      hideForActionMode();
      break;
      label75:
      localViewPropertyAnimatorCompat1 = mDecorToolbar.setupAnimatorToVisibility(0, 200L);
      localViewPropertyAnimatorCompat2 = mContextView.setupAnimatorToVisibility(8, 100L);
    }
    label105:
    if (paramBoolean)
    {
      mDecorToolbar.setVisibility(4);
      mContextView.setVisibility(0);
      return;
    }
    mDecorToolbar.setVisibility(0);
    mContextView.setVisibility(8);
  }
  
  public boolean collapseActionView()
  {
    if ((mDecorToolbar != null) && (mDecorToolbar.hasExpandedActionView()))
    {
      mDecorToolbar.collapseActionView();
      return true;
    }
    return false;
  }
  
  void completeDeferredDestroyActionMode()
  {
    if (mDeferredModeDestroyCallback != null)
    {
      mDeferredModeDestroyCallback.onDestroyActionMode(mDeferredDestroyActionMode);
      mDeferredDestroyActionMode = null;
      mDeferredModeDestroyCallback = null;
    }
  }
  
  public void dispatchMenuVisibilityChanged(boolean paramBoolean)
  {
    if (paramBoolean == mLastMenuVisibility) {}
    for (;;)
    {
      return;
      mLastMenuVisibility = paramBoolean;
      int i = mMenuVisibilityListeners.size();
      for (int j = 0; j < i; j++) {
        ((ActionBar.OnMenuVisibilityListener)mMenuVisibilityListeners.get(j)).onMenuVisibilityChanged(paramBoolean);
      }
    }
  }
  
  public void doHide(boolean paramBoolean)
  {
    if (mCurrentShowAnim != null) {
      mCurrentShowAnim.cancel();
    }
    if ((mCurWindowVisibility == 0) && (ALLOW_SHOW_HIDE_ANIMATIONS) && ((mShowHideAnimationEnabled) || (paramBoolean)))
    {
      ViewCompat.setAlpha(mContainerView, 1.0F);
      mContainerView.setTransitioning(true);
      ViewPropertyAnimatorCompatSet localViewPropertyAnimatorCompatSet = new ViewPropertyAnimatorCompatSet();
      float f = -mContainerView.getHeight();
      if (paramBoolean)
      {
        int[] arrayOfInt = { 0, 0 };
        mContainerView.getLocationInWindow(arrayOfInt);
        f -= arrayOfInt[1];
      }
      ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat = ViewCompat.animate(mContainerView).translationY(f);
      localViewPropertyAnimatorCompat.setUpdateListener(mUpdateListener);
      localViewPropertyAnimatorCompatSet.play(localViewPropertyAnimatorCompat);
      if ((mContentAnimations) && (mContentView != null)) {
        localViewPropertyAnimatorCompatSet.play(ViewCompat.animate(mContentView).translationY(f));
      }
      localViewPropertyAnimatorCompatSet.setInterpolator(sHideInterpolator);
      localViewPropertyAnimatorCompatSet.setDuration(250L);
      localViewPropertyAnimatorCompatSet.setListener(mHideListener);
      mCurrentShowAnim = localViewPropertyAnimatorCompatSet;
      localViewPropertyAnimatorCompatSet.start();
      return;
    }
    mHideListener.onAnimationEnd(null);
  }
  
  public void doShow(boolean paramBoolean)
  {
    if (mCurrentShowAnim != null) {
      mCurrentShowAnim.cancel();
    }
    mContainerView.setVisibility(0);
    if ((mCurWindowVisibility == 0) && (ALLOW_SHOW_HIDE_ANIMATIONS) && ((mShowHideAnimationEnabled) || (paramBoolean)))
    {
      ViewCompat.setTranslationY(mContainerView, 0.0F);
      float f = -mContainerView.getHeight();
      if (paramBoolean)
      {
        int[] arrayOfInt = { 0, 0 };
        mContainerView.getLocationInWindow(arrayOfInt);
        f -= arrayOfInt[1];
      }
      ViewCompat.setTranslationY(mContainerView, f);
      ViewPropertyAnimatorCompatSet localViewPropertyAnimatorCompatSet = new ViewPropertyAnimatorCompatSet();
      ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat = ViewCompat.animate(mContainerView).translationY(0.0F);
      localViewPropertyAnimatorCompat.setUpdateListener(mUpdateListener);
      localViewPropertyAnimatorCompatSet.play(localViewPropertyAnimatorCompat);
      if ((mContentAnimations) && (mContentView != null))
      {
        ViewCompat.setTranslationY(mContentView, f);
        localViewPropertyAnimatorCompatSet.play(ViewCompat.animate(mContentView).translationY(0.0F));
      }
      localViewPropertyAnimatorCompatSet.setInterpolator(sShowInterpolator);
      localViewPropertyAnimatorCompatSet.setDuration(250L);
      localViewPropertyAnimatorCompatSet.setListener(mShowListener);
      mCurrentShowAnim = localViewPropertyAnimatorCompatSet;
      localViewPropertyAnimatorCompatSet.start();
    }
    for (;;)
    {
      if (mOverlayLayout != null) {
        ViewCompat.requestApplyInsets(mOverlayLayout);
      }
      return;
      ViewCompat.setAlpha(mContainerView, 1.0F);
      ViewCompat.setTranslationY(mContainerView, 0.0F);
      if ((mContentAnimations) && (mContentView != null)) {
        ViewCompat.setTranslationY(mContentView, 0.0F);
      }
      mShowListener.onAnimationEnd(null);
    }
  }
  
  public void enableContentAnimations(boolean paramBoolean)
  {
    mContentAnimations = paramBoolean;
  }
  
  public View getCustomView()
  {
    return mDecorToolbar.getCustomView();
  }
  
  public int getDisplayOptions()
  {
    return mDecorToolbar.getDisplayOptions();
  }
  
  public float getElevation()
  {
    return ViewCompat.getElevation(mContainerView);
  }
  
  public int getHeight()
  {
    return mContainerView.getHeight();
  }
  
  public int getHideOffset()
  {
    return mOverlayLayout.getActionBarHideOffset();
  }
  
  public int getNavigationItemCount()
  {
    switch (mDecorToolbar.getNavigationMode())
    {
    default: 
      return 0;
    case 2: 
      return mTabs.size();
    }
    return mDecorToolbar.getDropdownItemCount();
  }
  
  public int getNavigationMode()
  {
    return mDecorToolbar.getNavigationMode();
  }
  
  public int getSelectedNavigationIndex()
  {
    switch (mDecorToolbar.getNavigationMode())
    {
    default: 
    case 2: 
      do
      {
        return -1;
      } while (mSelectedTab == null);
      return mSelectedTab.getPosition();
    }
    return mDecorToolbar.getDropdownSelectedPosition();
  }
  
  public ActionBar.Tab getSelectedTab()
  {
    return mSelectedTab;
  }
  
  public CharSequence getSubtitle()
  {
    return mDecorToolbar.getSubtitle();
  }
  
  public ActionBar.Tab getTabAt(int paramInt)
  {
    return (ActionBar.Tab)mTabs.get(paramInt);
  }
  
  public int getTabCount()
  {
    return mTabs.size();
  }
  
  public Context getThemedContext()
  {
    int i;
    if (mThemedContext == null)
    {
      TypedValue localTypedValue = new TypedValue();
      mContext.getTheme().resolveAttribute(R.attr.actionBarWidgetTheme, localTypedValue, true);
      i = resourceId;
      if (i == 0) {
        break label61;
      }
    }
    label61:
    for (mThemedContext = new ContextThemeWrapper(mContext, i);; mThemedContext = mContext) {
      return mThemedContext;
    }
  }
  
  public CharSequence getTitle()
  {
    return mDecorToolbar.getTitle();
  }
  
  public boolean hasIcon()
  {
    return mDecorToolbar.hasIcon();
  }
  
  public boolean hasLogo()
  {
    return mDecorToolbar.hasLogo();
  }
  
  public void hide()
  {
    if (!mHiddenByApp)
    {
      mHiddenByApp = true;
      updateVisibility(false);
    }
  }
  
  public void hideForSystem()
  {
    if (!mHiddenBySystem)
    {
      mHiddenBySystem = true;
      updateVisibility(true);
    }
  }
  
  public boolean isHideOnContentScrollEnabled()
  {
    return mOverlayLayout.isHideOnContentScrollEnabled();
  }
  
  public boolean isShowing()
  {
    int i = getHeight();
    return (mNowShowing) && ((i == 0) || (getHideOffset() < i));
  }
  
  public boolean isTitleTruncated()
  {
    return (mDecorToolbar != null) && (mDecorToolbar.isTitleTruncated());
  }
  
  public ActionBar.Tab newTab()
  {
    return new TabImpl();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    setHasEmbeddedTabs(ActionBarPolicy.get(mContext).hasEmbeddedTabs());
  }
  
  public void onContentScrollStarted()
  {
    if (mCurrentShowAnim != null)
    {
      mCurrentShowAnim.cancel();
      mCurrentShowAnim = null;
    }
  }
  
  public void onContentScrollStopped() {}
  
  public void onWindowVisibilityChanged(int paramInt)
  {
    mCurWindowVisibility = paramInt;
  }
  
  public void removeAllTabs()
  {
    cleanupTabs();
  }
  
  public void removeOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener paramOnMenuVisibilityListener)
  {
    mMenuVisibilityListeners.remove(paramOnMenuVisibilityListener);
  }
  
  public void removeTab(ActionBar.Tab paramTab)
  {
    removeTabAt(paramTab.getPosition());
  }
  
  public void removeTabAt(int paramInt)
  {
    if (mTabScrollView == null) {}
    int i;
    do
    {
      return;
      if (mSelectedTab != null) {}
      for (i = mSelectedTab.getPosition();; i = mSavedTabPosition)
      {
        mTabScrollView.removeTabAt(paramInt);
        TabImpl localTabImpl = (TabImpl)mTabs.remove(paramInt);
        if (localTabImpl != null) {
          localTabImpl.setPosition(-1);
        }
        int j = mTabs.size();
        for (int k = paramInt; k < j; k++) {
          ((TabImpl)mTabs.get(k)).setPosition(k);
        }
      }
    } while (i != paramInt);
    if (mTabs.isEmpty()) {}
    for (Object localObject = null;; localObject = (TabImpl)mTabs.get(Math.max(0, paramInt - 1)))
    {
      selectTab((ActionBar.Tab)localObject);
      return;
    }
  }
  
  public boolean requestFocus()
  {
    ViewGroup localViewGroup = mDecorToolbar.getViewGroup();
    if ((localViewGroup != null) && (!localViewGroup.hasFocus()))
    {
      localViewGroup.requestFocus();
      return true;
    }
    return false;
  }
  
  public void selectTab(ActionBar.Tab paramTab)
  {
    int i = -1;
    int j;
    if (getNavigationMode() != 2) {
      if (paramTab != null)
      {
        j = paramTab.getPosition();
        mSavedTabPosition = j;
      }
    }
    label140:
    label218:
    for (;;)
    {
      return;
      j = i;
      break;
      FragmentTransaction localFragmentTransaction;
      if (((mActivity instanceof FragmentActivity)) && (!mDecorToolbar.getViewGroup().isInEditMode()))
      {
        localFragmentTransaction = ((FragmentActivity)mActivity).getSupportFragmentManager().beginTransaction().disallowAddToBackStack();
        if (mSelectedTab != paramTab) {
          break label140;
        }
        if (mSelectedTab != null)
        {
          mSelectedTab.getCallback().onTabReselected(mSelectedTab, localFragmentTransaction);
          mTabScrollView.animateToTab(paramTab.getPosition());
        }
      }
      for (;;)
      {
        if ((localFragmentTransaction == null) || (localFragmentTransaction.isEmpty())) {
          break label218;
        }
        localFragmentTransaction.commit();
        return;
        localFragmentTransaction = null;
        break;
        ScrollingTabContainerView localScrollingTabContainerView = mTabScrollView;
        if (paramTab != null) {
          i = paramTab.getPosition();
        }
        localScrollingTabContainerView.setTabSelected(i);
        if (mSelectedTab != null) {
          mSelectedTab.getCallback().onTabUnselected(mSelectedTab, localFragmentTransaction);
        }
        mSelectedTab = ((TabImpl)paramTab);
        if (mSelectedTab != null) {
          mSelectedTab.getCallback().onTabSelected(mSelectedTab, localFragmentTransaction);
        }
      }
    }
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    mContainerView.setPrimaryBackground(paramDrawable);
  }
  
  public void setCustomView(int paramInt)
  {
    setCustomView(LayoutInflater.from(getThemedContext()).inflate(paramInt, mDecorToolbar.getViewGroup(), false));
  }
  
  public void setCustomView(View paramView)
  {
    mDecorToolbar.setCustomView(paramView);
  }
  
  public void setCustomView(View paramView, ActionBar.LayoutParams paramLayoutParams)
  {
    paramView.setLayoutParams(paramLayoutParams);
    mDecorToolbar.setCustomView(paramView);
  }
  
  public void setDefaultDisplayHomeAsUpEnabled(boolean paramBoolean)
  {
    if (!mDisplayHomeAsUpSet) {
      setDisplayHomeAsUpEnabled(paramBoolean);
    }
  }
  
  public void setDisplayHomeAsUpEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 4;; i = 0)
    {
      setDisplayOptions(i, 4);
      return;
    }
  }
  
  public void setDisplayOptions(int paramInt)
  {
    if ((paramInt & 0x4) != 0) {
      mDisplayHomeAsUpSet = true;
    }
    mDecorToolbar.setDisplayOptions(paramInt);
  }
  
  public void setDisplayOptions(int paramInt1, int paramInt2)
  {
    int i = mDecorToolbar.getDisplayOptions();
    if ((paramInt2 & 0x4) != 0) {
      mDisplayHomeAsUpSet = true;
    }
    mDecorToolbar.setDisplayOptions(paramInt1 & paramInt2 | i & (paramInt2 ^ 0xFFFFFFFF));
  }
  
  public void setDisplayShowCustomEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 16;; i = 0)
    {
      setDisplayOptions(i, 16);
      return;
    }
  }
  
  public void setDisplayShowHomeEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 2;; i = 0)
    {
      setDisplayOptions(i, 2);
      return;
    }
  }
  
  public void setDisplayShowTitleEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 8;; i = 0)
    {
      setDisplayOptions(i, 8);
      return;
    }
  }
  
  public void setDisplayUseLogoEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      setDisplayOptions(i, 1);
      return;
    }
  }
  
  public void setElevation(float paramFloat)
  {
    ViewCompat.setElevation(mContainerView, paramFloat);
  }
  
  public void setHideOffset(int paramInt)
  {
    if ((paramInt != 0) && (!mOverlayLayout.isInOverlayMode())) {
      throw new IllegalStateException("Action bar must be in overlay mode (Window.FEATURE_OVERLAY_ACTION_BAR) to set a non-zero hide offset");
    }
    mOverlayLayout.setActionBarHideOffset(paramInt);
  }
  
  public void setHideOnContentScrollEnabled(boolean paramBoolean)
  {
    if ((paramBoolean) && (!mOverlayLayout.isInOverlayMode())) {
      throw new IllegalStateException("Action bar must be in overlay mode (Window.FEATURE_OVERLAY_ACTION_BAR) to enable hide on content scroll");
    }
    mHideOnContentScroll = paramBoolean;
    mOverlayLayout.setHideOnContentScrollEnabled(paramBoolean);
  }
  
  public void setHomeActionContentDescription(int paramInt)
  {
    mDecorToolbar.setNavigationContentDescription(paramInt);
  }
  
  public void setHomeActionContentDescription(CharSequence paramCharSequence)
  {
    mDecorToolbar.setNavigationContentDescription(paramCharSequence);
  }
  
  public void setHomeAsUpIndicator(int paramInt)
  {
    mDecorToolbar.setNavigationIcon(paramInt);
  }
  
  public void setHomeAsUpIndicator(Drawable paramDrawable)
  {
    mDecorToolbar.setNavigationIcon(paramDrawable);
  }
  
  public void setHomeButtonEnabled(boolean paramBoolean)
  {
    mDecorToolbar.setHomeButtonEnabled(paramBoolean);
  }
  
  public void setIcon(int paramInt)
  {
    mDecorToolbar.setIcon(paramInt);
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    mDecorToolbar.setIcon(paramDrawable);
  }
  
  public void setListNavigationCallbacks(SpinnerAdapter paramSpinnerAdapter, ActionBar.OnNavigationListener paramOnNavigationListener)
  {
    mDecorToolbar.setDropdownParams(paramSpinnerAdapter, new NavItemSelectedListener(paramOnNavigationListener));
  }
  
  public void setLogo(int paramInt)
  {
    mDecorToolbar.setLogo(paramInt);
  }
  
  public void setLogo(Drawable paramDrawable)
  {
    mDecorToolbar.setLogo(paramDrawable);
  }
  
  public void setNavigationMode(int paramInt)
  {
    boolean bool1 = true;
    int i = mDecorToolbar.getNavigationMode();
    label88:
    boolean bool2;
    label109:
    ActionBarOverlayLayout localActionBarOverlayLayout;
    switch (i)
    {
    default: 
      if ((i != paramInt) && (!mHasEmbeddedTabs) && (mOverlayLayout != null)) {
        ViewCompat.requestApplyInsets(mOverlayLayout);
      }
      mDecorToolbar.setNavigationMode(paramInt);
      switch (paramInt)
      {
      default: 
        DecorToolbar localDecorToolbar = mDecorToolbar;
        if ((paramInt == 2) && (!mHasEmbeddedTabs))
        {
          bool2 = bool1;
          localDecorToolbar.setCollapsible(bool2);
          localActionBarOverlayLayout = mOverlayLayout;
          if ((paramInt != 2) || (mHasEmbeddedTabs)) {
            break label210;
          }
        }
        break;
      }
      break;
    }
    for (;;)
    {
      localActionBarOverlayLayout.setHasNonEmbeddedTabs(bool1);
      return;
      mSavedTabPosition = getSelectedNavigationIndex();
      selectTab(null);
      mTabScrollView.setVisibility(8);
      break;
      ensureTabsExist();
      mTabScrollView.setVisibility(0);
      if (mSavedTabPosition == -1) {
        break label88;
      }
      setSelectedNavigationItem(mSavedTabPosition);
      mSavedTabPosition = -1;
      break label88;
      bool2 = false;
      break label109;
      label210:
      bool1 = false;
    }
  }
  
  public void setSelectedNavigationItem(int paramInt)
  {
    switch (mDecorToolbar.getNavigationMode())
    {
    default: 
      throw new IllegalStateException("setSelectedNavigationIndex not valid for current navigation mode");
    case 2: 
      selectTab((ActionBar.Tab)mTabs.get(paramInt));
      return;
    }
    mDecorToolbar.setDropdownSelectedPosition(paramInt);
  }
  
  public void setShowHideAnimationEnabled(boolean paramBoolean)
  {
    mShowHideAnimationEnabled = paramBoolean;
    if ((!paramBoolean) && (mCurrentShowAnim != null)) {
      mCurrentShowAnim.cancel();
    }
  }
  
  public void setSplitBackgroundDrawable(Drawable paramDrawable) {}
  
  public void setStackedBackgroundDrawable(Drawable paramDrawable)
  {
    mContainerView.setStackedBackground(paramDrawable);
  }
  
  public void setSubtitle(int paramInt)
  {
    setSubtitle(mContext.getString(paramInt));
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    mDecorToolbar.setSubtitle(paramCharSequence);
  }
  
  public void setTitle(int paramInt)
  {
    setTitle(mContext.getString(paramInt));
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    mDecorToolbar.setTitle(paramCharSequence);
  }
  
  public void setWindowTitle(CharSequence paramCharSequence)
  {
    mDecorToolbar.setWindowTitle(paramCharSequence);
  }
  
  public void show()
  {
    if (mHiddenByApp)
    {
      mHiddenByApp = false;
      updateVisibility(false);
    }
  }
  
  public void showForSystem()
  {
    if (mHiddenBySystem)
    {
      mHiddenBySystem = false;
      updateVisibility(true);
    }
  }
  
  public ActionMode startActionMode(ActionMode.Callback paramCallback)
  {
    if (mActionMode != null) {
      mActionMode.finish();
    }
    mOverlayLayout.setHideOnContentScrollEnabled(false);
    mContextView.killMode();
    ActionModeImpl localActionModeImpl = new ActionModeImpl(mContextView.getContext(), paramCallback);
    if (localActionModeImpl.dispatchOnCreate())
    {
      mActionMode = localActionModeImpl;
      localActionModeImpl.invalidate();
      mContextView.initForMode(localActionModeImpl);
      animateToMode(true);
      mContextView.sendAccessibilityEvent(32);
      return localActionModeImpl;
    }
    return null;
  }
  
  public class ActionModeImpl
    extends ActionMode
    implements MenuBuilder.Callback
  {
    private final Context mActionModeContext;
    private ActionMode.Callback mCallback;
    private WeakReference<View> mCustomView;
    private final MenuBuilder mMenu;
    
    public ActionModeImpl(Context paramContext, ActionMode.Callback paramCallback)
    {
      mActionModeContext = paramContext;
      mCallback = paramCallback;
      mMenu = new MenuBuilder(paramContext).setDefaultShowAsAction(1);
      mMenu.setCallback(this);
    }
    
    public boolean dispatchOnCreate()
    {
      mMenu.stopDispatchingItemsChanged();
      try
      {
        boolean bool = mCallback.onCreateActionMode(this, mMenu);
        return bool;
      }
      finally
      {
        mMenu.startDispatchingItemsChanged();
      }
    }
    
    public void finish()
    {
      if (mActionMode != this) {
        return;
      }
      if (!WindowDecorActionBar.checkShowingFlags(mHiddenByApp, mHiddenBySystem, false))
      {
        mDeferredDestroyActionMode = this;
        mDeferredModeDestroyCallback = mCallback;
      }
      for (;;)
      {
        mCallback = null;
        animateToMode(false);
        mContextView.closeMode();
        mDecorToolbar.getViewGroup().sendAccessibilityEvent(32);
        mOverlayLayout.setHideOnContentScrollEnabled(mHideOnContentScroll);
        mActionMode = null;
        return;
        mCallback.onDestroyActionMode(this);
      }
    }
    
    public View getCustomView()
    {
      if (mCustomView != null) {
        return (View)mCustomView.get();
      }
      return null;
    }
    
    public Menu getMenu()
    {
      return mMenu;
    }
    
    public MenuInflater getMenuInflater()
    {
      return new SupportMenuInflater(mActionModeContext);
    }
    
    public CharSequence getSubtitle()
    {
      return mContextView.getSubtitle();
    }
    
    public CharSequence getTitle()
    {
      return mContextView.getTitle();
    }
    
    public void invalidate()
    {
      if (mActionMode != this) {
        return;
      }
      mMenu.stopDispatchingItemsChanged();
      try
      {
        mCallback.onPrepareActionMode(this, mMenu);
        return;
      }
      finally
      {
        mMenu.startDispatchingItemsChanged();
      }
    }
    
    public boolean isTitleOptional()
    {
      return mContextView.isTitleOptional();
    }
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {}
    
    public void onCloseSubMenu(SubMenuBuilder paramSubMenuBuilder) {}
    
    public boolean onMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem)
    {
      if (mCallback != null) {
        return mCallback.onActionItemClicked(this, paramMenuItem);
      }
      return false;
    }
    
    public void onMenuModeChange(MenuBuilder paramMenuBuilder)
    {
      if (mCallback == null) {
        return;
      }
      invalidate();
      mContextView.showOverflowMenu();
    }
    
    public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder)
    {
      boolean bool = true;
      if (mCallback == null) {
        bool = false;
      }
      while (!paramSubMenuBuilder.hasVisibleItems()) {
        return bool;
      }
      new MenuPopupHelper(getThemedContext(), paramSubMenuBuilder).show();
      return bool;
    }
    
    public void setCustomView(View paramView)
    {
      mContextView.setCustomView(paramView);
      mCustomView = new WeakReference(paramView);
    }
    
    public void setSubtitle(int paramInt)
    {
      setSubtitle(mContext.getResources().getString(paramInt));
    }
    
    public void setSubtitle(CharSequence paramCharSequence)
    {
      mContextView.setSubtitle(paramCharSequence);
    }
    
    public void setTitle(int paramInt)
    {
      setTitle(mContext.getResources().getString(paramInt));
    }
    
    public void setTitle(CharSequence paramCharSequence)
    {
      mContextView.setTitle(paramCharSequence);
    }
    
    public void setTitleOptionalHint(boolean paramBoolean)
    {
      super.setTitleOptionalHint(paramBoolean);
      mContextView.setTitleOptional(paramBoolean);
    }
  }
  
  public class TabImpl
    extends ActionBar.Tab
  {
    private ActionBar.TabListener mCallback;
    private CharSequence mContentDesc;
    private View mCustomView;
    private Drawable mIcon;
    private int mPosition = -1;
    private Object mTag;
    private CharSequence mText;
    
    public TabImpl() {}
    
    public ActionBar.TabListener getCallback()
    {
      return mCallback;
    }
    
    public CharSequence getContentDescription()
    {
      return mContentDesc;
    }
    
    public View getCustomView()
    {
      return mCustomView;
    }
    
    public Drawable getIcon()
    {
      return mIcon;
    }
    
    public int getPosition()
    {
      return mPosition;
    }
    
    public Object getTag()
    {
      return mTag;
    }
    
    public CharSequence getText()
    {
      return mText;
    }
    
    public void select()
    {
      selectTab(this);
    }
    
    public ActionBar.Tab setContentDescription(int paramInt)
    {
      return setContentDescription(mContext.getResources().getText(paramInt));
    }
    
    public ActionBar.Tab setContentDescription(CharSequence paramCharSequence)
    {
      mContentDesc = paramCharSequence;
      if (mPosition >= 0) {
        mTabScrollView.updateTab(mPosition);
      }
      return this;
    }
    
    public ActionBar.Tab setCustomView(int paramInt)
    {
      return setCustomView(LayoutInflater.from(getThemedContext()).inflate(paramInt, null));
    }
    
    public ActionBar.Tab setCustomView(View paramView)
    {
      mCustomView = paramView;
      if (mPosition >= 0) {
        mTabScrollView.updateTab(mPosition);
      }
      return this;
    }
    
    public ActionBar.Tab setIcon(int paramInt)
    {
      return setIcon(AppCompatResources.getDrawable(mContext, paramInt));
    }
    
    public ActionBar.Tab setIcon(Drawable paramDrawable)
    {
      mIcon = paramDrawable;
      if (mPosition >= 0) {
        mTabScrollView.updateTab(mPosition);
      }
      return this;
    }
    
    public void setPosition(int paramInt)
    {
      mPosition = paramInt;
    }
    
    public ActionBar.Tab setTabListener(ActionBar.TabListener paramTabListener)
    {
      mCallback = paramTabListener;
      return this;
    }
    
    public ActionBar.Tab setTag(Object paramObject)
    {
      mTag = paramObject;
      return this;
    }
    
    public ActionBar.Tab setText(int paramInt)
    {
      return setText(mContext.getResources().getText(paramInt));
    }
    
    public ActionBar.Tab setText(CharSequence paramCharSequence)
    {
      mText = paramCharSequence;
      if (mPosition >= 0) {
        mTabScrollView.updateTab(mPosition);
      }
      return this;
    }
  }
}
