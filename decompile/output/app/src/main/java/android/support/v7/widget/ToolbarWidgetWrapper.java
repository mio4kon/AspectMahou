package android.support.v7.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.drawable;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.string;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window.Callback;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class ToolbarWidgetWrapper
  implements DecorToolbar
{
  private static final int AFFECTS_LOGO_MASK = 3;
  private static final long DEFAULT_FADE_DURATION_MS = 200L;
  private static final String TAG = "ToolbarWidgetWrapper";
  private ActionMenuPresenter mActionMenuPresenter;
  private View mCustomView;
  private int mDefaultNavigationContentDescription = 0;
  private Drawable mDefaultNavigationIcon;
  private int mDisplayOpts;
  private CharSequence mHomeDescription;
  private Drawable mIcon;
  private Drawable mLogo;
  boolean mMenuPrepared;
  private Drawable mNavIcon;
  private int mNavigationMode = 0;
  private Spinner mSpinner;
  private CharSequence mSubtitle;
  private View mTabView;
  CharSequence mTitle;
  private boolean mTitleSet;
  Toolbar mToolbar;
  Window.Callback mWindowCallback;
  
  public ToolbarWidgetWrapper(Toolbar paramToolbar, boolean paramBoolean)
  {
    this(paramToolbar, paramBoolean, R.string.abc_action_bar_up_description, R.drawable.abc_ic_ab_back_material);
  }
  
  public ToolbarWidgetWrapper(Toolbar paramToolbar, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    mToolbar = paramToolbar;
    mTitle = paramToolbar.getTitle();
    mSubtitle = paramToolbar.getSubtitle();
    boolean bool;
    TintTypedArray localTintTypedArray;
    if (mTitle != null)
    {
      bool = true;
      mTitleSet = bool;
      mNavIcon = paramToolbar.getNavigationIcon();
      localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramToolbar.getContext(), null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
      mDefaultNavigationIcon = localTintTypedArray.getDrawable(R.styleable.ActionBar_homeAsUpIndicator);
      if (!paramBoolean) {
        break label500;
      }
      CharSequence localCharSequence1 = localTintTypedArray.getText(R.styleable.ActionBar_title);
      if (!TextUtils.isEmpty(localCharSequence1)) {
        setTitle(localCharSequence1);
      }
      CharSequence localCharSequence2 = localTintTypedArray.getText(R.styleable.ActionBar_subtitle);
      if (!TextUtils.isEmpty(localCharSequence2)) {
        setSubtitle(localCharSequence2);
      }
      Drawable localDrawable1 = localTintTypedArray.getDrawable(R.styleable.ActionBar_logo);
      if (localDrawable1 != null) {
        setLogo(localDrawable1);
      }
      Drawable localDrawable2 = localTintTypedArray.getDrawable(R.styleable.ActionBar_icon);
      if (localDrawable2 != null) {
        setIcon(localDrawable2);
      }
      if ((mNavIcon == null) && (mDefaultNavigationIcon != null)) {
        setNavigationIcon(mDefaultNavigationIcon);
      }
      setDisplayOptions(localTintTypedArray.getInt(R.styleable.ActionBar_displayOptions, 0));
      int i = localTintTypedArray.getResourceId(R.styleable.ActionBar_customNavigationLayout, 0);
      if (i != 0)
      {
        setCustomView(LayoutInflater.from(mToolbar.getContext()).inflate(i, mToolbar, false));
        setDisplayOptions(0x10 | mDisplayOpts);
      }
      int j = localTintTypedArray.getLayoutDimension(R.styleable.ActionBar_height, 0);
      if (j > 0)
      {
        ViewGroup.LayoutParams localLayoutParams = mToolbar.getLayoutParams();
        height = j;
        mToolbar.setLayoutParams(localLayoutParams);
      }
      int k = localTintTypedArray.getDimensionPixelOffset(R.styleable.ActionBar_contentInsetStart, -1);
      int m = localTintTypedArray.getDimensionPixelOffset(R.styleable.ActionBar_contentInsetEnd, -1);
      if ((k >= 0) || (m >= 0)) {
        mToolbar.setContentInsetsRelative(Math.max(k, 0), Math.max(m, 0));
      }
      int n = localTintTypedArray.getResourceId(R.styleable.ActionBar_titleTextStyle, 0);
      if (n != 0) {
        mToolbar.setTitleTextAppearance(mToolbar.getContext(), n);
      }
      int i1 = localTintTypedArray.getResourceId(R.styleable.ActionBar_subtitleTextStyle, 0);
      if (i1 != 0) {
        mToolbar.setSubtitleTextAppearance(mToolbar.getContext(), i1);
      }
      int i2 = localTintTypedArray.getResourceId(R.styleable.ActionBar_popupTheme, 0);
      if (i2 != 0) {
        mToolbar.setPopupTheme(i2);
      }
    }
    for (;;)
    {
      localTintTypedArray.recycle();
      setDefaultNavigationContentDescription(paramInt1);
      mHomeDescription = mToolbar.getNavigationContentDescription();
      Toolbar localToolbar = mToolbar;
      View.OnClickListener local1 = new View.OnClickListener()
      {
        final ActionMenuItem mNavItem = new ActionMenuItem(mToolbar.getContext(), 0, 16908332, 0, 0, mTitle);
        
        public void onClick(View paramAnonymousView)
        {
          if ((mWindowCallback != null) && (mMenuPrepared)) {
            mWindowCallback.onMenuItemSelected(0, mNavItem);
          }
        }
      };
      localToolbar.setNavigationOnClickListener(local1);
      return;
      bool = false;
      break;
      label500:
      mDisplayOpts = detectDisplayOptions();
    }
  }
  
  private int detectDisplayOptions()
  {
    int i = 11;
    if (mToolbar.getNavigationIcon() != null)
    {
      i |= 0x4;
      mDefaultNavigationIcon = mToolbar.getNavigationIcon();
    }
    return i;
  }
  
  private void ensureSpinner()
  {
    if (mSpinner == null)
    {
      mSpinner = new AppCompatSpinner(getContext(), null, R.attr.actionDropDownStyle);
      Toolbar.LayoutParams localLayoutParams = new Toolbar.LayoutParams(-2, -2, 8388627);
      mSpinner.setLayoutParams(localLayoutParams);
    }
  }
  
  private void setTitleInt(CharSequence paramCharSequence)
  {
    mTitle = paramCharSequence;
    if ((0x8 & mDisplayOpts) != 0) {
      mToolbar.setTitle(paramCharSequence);
    }
  }
  
  private void updateHomeAccessibility()
  {
    if ((0x4 & mDisplayOpts) != 0)
    {
      if (TextUtils.isEmpty(mHomeDescription)) {
        mToolbar.setNavigationContentDescription(mDefaultNavigationContentDescription);
      }
    }
    else {
      return;
    }
    mToolbar.setNavigationContentDescription(mHomeDescription);
  }
  
  private void updateNavigationIcon()
  {
    if ((0x4 & mDisplayOpts) != 0)
    {
      Toolbar localToolbar = mToolbar;
      if (mNavIcon != null) {}
      for (Drawable localDrawable = mNavIcon;; localDrawable = mDefaultNavigationIcon)
      {
        localToolbar.setNavigationIcon(localDrawable);
        return;
      }
    }
    mToolbar.setNavigationIcon(null);
  }
  
  private void updateToolbarLogo()
  {
    int i = 0x2 & mDisplayOpts;
    Drawable localDrawable = null;
    if (i != 0)
    {
      if ((0x1 & mDisplayOpts) == 0) {
        break label51;
      }
      if (mLogo == null) {
        break label43;
      }
      localDrawable = mLogo;
    }
    for (;;)
    {
      mToolbar.setLogo(localDrawable);
      return;
      label43:
      localDrawable = mIcon;
      continue;
      label51:
      localDrawable = mIcon;
    }
  }
  
  public void animateToVisibility(int paramInt)
  {
    ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat = setupAnimatorToVisibility(paramInt, 200L);
    if (localViewPropertyAnimatorCompat != null) {
      localViewPropertyAnimatorCompat.start();
    }
  }
  
  public boolean canShowOverflowMenu()
  {
    return mToolbar.canShowOverflowMenu();
  }
  
  public void collapseActionView()
  {
    mToolbar.collapseActionView();
  }
  
  public void dismissPopupMenus()
  {
    mToolbar.dismissPopupMenus();
  }
  
  public Context getContext()
  {
    return mToolbar.getContext();
  }
  
  public View getCustomView()
  {
    return mCustomView;
  }
  
  public int getDisplayOptions()
  {
    return mDisplayOpts;
  }
  
  public int getDropdownItemCount()
  {
    if (mSpinner != null) {
      return mSpinner.getCount();
    }
    return 0;
  }
  
  public int getDropdownSelectedPosition()
  {
    if (mSpinner != null) {
      return mSpinner.getSelectedItemPosition();
    }
    return 0;
  }
  
  public int getHeight()
  {
    return mToolbar.getHeight();
  }
  
  public Menu getMenu()
  {
    return mToolbar.getMenu();
  }
  
  public int getNavigationMode()
  {
    return mNavigationMode;
  }
  
  public CharSequence getSubtitle()
  {
    return mToolbar.getSubtitle();
  }
  
  public CharSequence getTitle()
  {
    return mToolbar.getTitle();
  }
  
  public ViewGroup getViewGroup()
  {
    return mToolbar;
  }
  
  public int getVisibility()
  {
    return mToolbar.getVisibility();
  }
  
  public boolean hasEmbeddedTabs()
  {
    return mTabView != null;
  }
  
  public boolean hasExpandedActionView()
  {
    return mToolbar.hasExpandedActionView();
  }
  
  public boolean hasIcon()
  {
    return mIcon != null;
  }
  
  public boolean hasLogo()
  {
    return mLogo != null;
  }
  
  public boolean hideOverflowMenu()
  {
    return mToolbar.hideOverflowMenu();
  }
  
  public void initIndeterminateProgress()
  {
    Log.i("ToolbarWidgetWrapper", "Progress display unsupported");
  }
  
  public void initProgress()
  {
    Log.i("ToolbarWidgetWrapper", "Progress display unsupported");
  }
  
  public boolean isOverflowMenuShowPending()
  {
    return mToolbar.isOverflowMenuShowPending();
  }
  
  public boolean isOverflowMenuShowing()
  {
    return mToolbar.isOverflowMenuShowing();
  }
  
  public boolean isTitleTruncated()
  {
    return mToolbar.isTitleTruncated();
  }
  
  public void restoreHierarchyState(SparseArray<Parcelable> paramSparseArray)
  {
    mToolbar.restoreHierarchyState(paramSparseArray);
  }
  
  public void saveHierarchyState(SparseArray<Parcelable> paramSparseArray)
  {
    mToolbar.saveHierarchyState(paramSparseArray);
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    mToolbar.setBackgroundDrawable(paramDrawable);
  }
  
  public void setCollapsible(boolean paramBoolean)
  {
    mToolbar.setCollapsible(paramBoolean);
  }
  
  public void setCustomView(View paramView)
  {
    if ((mCustomView != null) && ((0x10 & mDisplayOpts) != 0)) {
      mToolbar.removeView(mCustomView);
    }
    mCustomView = paramView;
    if ((paramView != null) && ((0x10 & mDisplayOpts) != 0)) {
      mToolbar.addView(mCustomView);
    }
  }
  
  public void setDefaultNavigationContentDescription(int paramInt)
  {
    if (paramInt == mDefaultNavigationContentDescription) {}
    do
    {
      return;
      mDefaultNavigationContentDescription = paramInt;
    } while (!TextUtils.isEmpty(mToolbar.getNavigationContentDescription()));
    setNavigationContentDescription(mDefaultNavigationContentDescription);
  }
  
  public void setDefaultNavigationIcon(Drawable paramDrawable)
  {
    if (mDefaultNavigationIcon != paramDrawable)
    {
      mDefaultNavigationIcon = paramDrawable;
      updateNavigationIcon();
    }
  }
  
  public void setDisplayOptions(int paramInt)
  {
    int i = paramInt ^ mDisplayOpts;
    mDisplayOpts = paramInt;
    if (i != 0)
    {
      if ((i & 0x4) != 0)
      {
        if ((paramInt & 0x4) != 0) {
          updateHomeAccessibility();
        }
        updateNavigationIcon();
      }
      if ((i & 0x3) != 0) {
        updateToolbarLogo();
      }
      if ((i & 0x8) != 0)
      {
        if ((paramInt & 0x8) == 0) {
          break label115;
        }
        mToolbar.setTitle(mTitle);
        mToolbar.setSubtitle(mSubtitle);
      }
    }
    for (;;)
    {
      if (((i & 0x10) != 0) && (mCustomView != null))
      {
        if ((paramInt & 0x10) == 0) {
          break;
        }
        mToolbar.addView(mCustomView);
      }
      return;
      label115:
      mToolbar.setTitle(null);
      mToolbar.setSubtitle(null);
    }
    mToolbar.removeView(mCustomView);
  }
  
  public void setDropdownParams(SpinnerAdapter paramSpinnerAdapter, AdapterView.OnItemSelectedListener paramOnItemSelectedListener)
  {
    ensureSpinner();
    mSpinner.setAdapter(paramSpinnerAdapter);
    mSpinner.setOnItemSelectedListener(paramOnItemSelectedListener);
  }
  
  public void setDropdownSelectedPosition(int paramInt)
  {
    if (mSpinner == null) {
      throw new IllegalStateException("Can't set dropdown selected position without an adapter");
    }
    mSpinner.setSelection(paramInt);
  }
  
  public void setEmbeddedTabView(ScrollingTabContainerView paramScrollingTabContainerView)
  {
    if ((mTabView != null) && (mTabView.getParent() == mToolbar)) {
      mToolbar.removeView(mTabView);
    }
    mTabView = paramScrollingTabContainerView;
    if ((paramScrollingTabContainerView != null) && (mNavigationMode == 2))
    {
      mToolbar.addView(mTabView, 0);
      Toolbar.LayoutParams localLayoutParams = (Toolbar.LayoutParams)mTabView.getLayoutParams();
      width = -2;
      height = -2;
      gravity = 8388691;
      paramScrollingTabContainerView.setAllowCollapse(true);
    }
  }
  
  public void setHomeButtonEnabled(boolean paramBoolean) {}
  
  public void setIcon(int paramInt)
  {
    if (paramInt != 0) {}
    for (Drawable localDrawable = AppCompatResources.getDrawable(getContext(), paramInt);; localDrawable = null)
    {
      setIcon(localDrawable);
      return;
    }
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    mIcon = paramDrawable;
    updateToolbarLogo();
  }
  
  public void setLogo(int paramInt)
  {
    if (paramInt != 0) {}
    for (Drawable localDrawable = AppCompatResources.getDrawable(getContext(), paramInt);; localDrawable = null)
    {
      setLogo(localDrawable);
      return;
    }
  }
  
  public void setLogo(Drawable paramDrawable)
  {
    mLogo = paramDrawable;
    updateToolbarLogo();
  }
  
  public void setMenu(Menu paramMenu, MenuPresenter.Callback paramCallback)
  {
    if (mActionMenuPresenter == null)
    {
      mActionMenuPresenter = new ActionMenuPresenter(mToolbar.getContext());
      mActionMenuPresenter.setId(R.id.action_menu_presenter);
    }
    mActionMenuPresenter.setCallback(paramCallback);
    mToolbar.setMenu((MenuBuilder)paramMenu, mActionMenuPresenter);
  }
  
  public void setMenuCallbacks(MenuPresenter.Callback paramCallback, MenuBuilder.Callback paramCallback1)
  {
    mToolbar.setMenuCallbacks(paramCallback, paramCallback1);
  }
  
  public void setMenuPrepared()
  {
    mMenuPrepared = true;
  }
  
  public void setNavigationContentDescription(int paramInt)
  {
    if (paramInt == 0) {}
    for (Object localObject = null;; localObject = getContext().getString(paramInt))
    {
      setNavigationContentDescription((CharSequence)localObject);
      return;
    }
  }
  
  public void setNavigationContentDescription(CharSequence paramCharSequence)
  {
    mHomeDescription = paramCharSequence;
    updateHomeAccessibility();
  }
  
  public void setNavigationIcon(int paramInt)
  {
    if (paramInt != 0) {}
    for (Drawable localDrawable = AppCompatResources.getDrawable(getContext(), paramInt);; localDrawable = null)
    {
      setNavigationIcon(localDrawable);
      return;
    }
  }
  
  public void setNavigationIcon(Drawable paramDrawable)
  {
    mNavIcon = paramDrawable;
    updateNavigationIcon();
  }
  
  public void setNavigationMode(int paramInt)
  {
    int i = mNavigationMode;
    if (paramInt != i)
    {
      switch (i)
      {
      }
      for (;;)
      {
        mNavigationMode = paramInt;
        switch (paramInt)
        {
        default: 
          throw new IllegalArgumentException("Invalid navigation mode " + paramInt);
          if ((mSpinner != null) && (mSpinner.getParent() == mToolbar))
          {
            mToolbar.removeView(mSpinner);
            continue;
            if ((mTabView != null) && (mTabView.getParent() == mToolbar)) {
              mToolbar.removeView(mTabView);
            }
          }
          break;
        }
      }
      ensureSpinner();
      mToolbar.addView(mSpinner, 0);
    }
    do
    {
      return;
    } while (mTabView == null);
    mToolbar.addView(mTabView, 0);
    Toolbar.LayoutParams localLayoutParams = (Toolbar.LayoutParams)mTabView.getLayoutParams();
    width = -2;
    height = -2;
    gravity = 8388691;
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    mSubtitle = paramCharSequence;
    if ((0x8 & mDisplayOpts) != 0) {
      mToolbar.setSubtitle(paramCharSequence);
    }
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    mTitleSet = true;
    setTitleInt(paramCharSequence);
  }
  
  public void setVisibility(int paramInt)
  {
    mToolbar.setVisibility(paramInt);
  }
  
  public void setWindowCallback(Window.Callback paramCallback)
  {
    mWindowCallback = paramCallback;
  }
  
  public void setWindowTitle(CharSequence paramCharSequence)
  {
    if (!mTitleSet) {
      setTitleInt(paramCharSequence);
    }
  }
  
  public ViewPropertyAnimatorCompat setupAnimatorToVisibility(final int paramInt, long paramLong)
  {
    ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat = ViewCompat.animate(mToolbar);
    if (paramInt == 0) {}
    for (float f = 1.0F;; f = 0.0F) {
      localViewPropertyAnimatorCompat.alpha(f).setDuration(paramLong).setListener(new ViewPropertyAnimatorListenerAdapter()
      {
        private boolean mCanceled = false;
        
        public void onAnimationCancel(View paramAnonymousView)
        {
          mCanceled = true;
        }
        
        public void onAnimationEnd(View paramAnonymousView)
        {
          if (!mCanceled) {
            mToolbar.setVisibility(paramInt);
          }
        }
        
        public void onAnimationStart(View paramAnonymousView)
        {
          mToolbar.setVisibility(0);
        }
      });
    }
  }
  
  public boolean showOverflowMenu()
  {
    return mToolbar.showOverflowMenu();
  }
}
