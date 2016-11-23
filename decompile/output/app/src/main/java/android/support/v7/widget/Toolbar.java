package android.support.v7.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.CollapsibleActionView;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.SubMenuBuilder;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class Toolbar
  extends ViewGroup
{
  private static final String TAG = "Toolbar";
  private MenuPresenter.Callback mActionMenuPresenterCallback;
  int mButtonGravity;
  ImageButton mCollapseButtonView;
  private CharSequence mCollapseDescription;
  private Drawable mCollapseIcon;
  private boolean mCollapsible;
  private int mContentInsetEndWithActions;
  private int mContentInsetStartWithNavigation;
  private RtlSpacingHelper mContentInsets;
  private boolean mEatingHover;
  private boolean mEatingTouch;
  View mExpandedActionView;
  private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
  private int mGravity = 8388627;
  private final ArrayList<View> mHiddenViews = new ArrayList();
  private ImageView mLogoView;
  private int mMaxButtonHeight;
  private MenuBuilder.Callback mMenuBuilderCallback;
  private ActionMenuView mMenuView;
  private final ActionMenuView.OnMenuItemClickListener mMenuViewItemClickListener;
  private ImageButton mNavButtonView;
  OnMenuItemClickListener mOnMenuItemClickListener;
  private ActionMenuPresenter mOuterActionMenuPresenter;
  private Context mPopupContext;
  private int mPopupTheme;
  private final Runnable mShowOverflowMenuRunnable;
  private CharSequence mSubtitleText;
  private int mSubtitleTextAppearance;
  private int mSubtitleTextColor;
  private TextView mSubtitleTextView;
  private final int[] mTempMargins = new int[2];
  private final ArrayList<View> mTempViews = new ArrayList();
  private int mTitleMarginBottom;
  private int mTitleMarginEnd;
  private int mTitleMarginStart;
  private int mTitleMarginTop;
  private CharSequence mTitleText;
  private int mTitleTextAppearance;
  private int mTitleTextColor;
  private TextView mTitleTextView;
  private ToolbarWidgetWrapper mWrapper;
  
  public Toolbar(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Toolbar(Context paramContext, @Nullable AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.toolbarStyle);
  }
  
  public Toolbar(Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    ActionMenuView.OnMenuItemClickListener local1 = new ActionMenuView.OnMenuItemClickListener()
    {
      public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
      {
        if (mOnMenuItemClickListener != null) {
          return mOnMenuItemClickListener.onMenuItemClick(paramAnonymousMenuItem);
        }
        return false;
      }
    };
    mMenuViewItemClickListener = local1;
    Runnable local2 = new Runnable()
    {
      public void run()
      {
        showOverflowMenu();
      }
    };
    mShowOverflowMenuRunnable = local2;
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(getContext(), paramAttributeSet, R.styleable.Toolbar, paramInt, 0);
    mTitleTextAppearance = localTintTypedArray.getResourceId(R.styleable.Toolbar_titleTextAppearance, 0);
    mSubtitleTextAppearance = localTintTypedArray.getResourceId(R.styleable.Toolbar_subtitleTextAppearance, 0);
    mGravity = localTintTypedArray.getInteger(R.styleable.Toolbar_android_gravity, mGravity);
    mButtonGravity = localTintTypedArray.getInteger(R.styleable.Toolbar_buttonGravity, 48);
    int i = localTintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_titleMargin, 0);
    if (localTintTypedArray.hasValue(R.styleable.Toolbar_titleMargins)) {
      i = localTintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_titleMargins, i);
    }
    mTitleMarginBottom = i;
    mTitleMarginTop = i;
    mTitleMarginEnd = i;
    mTitleMarginStart = i;
    int j = localTintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginStart, -1);
    if (j >= 0) {
      mTitleMarginStart = j;
    }
    int k = localTintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginEnd, -1);
    if (k >= 0) {
      mTitleMarginEnd = k;
    }
    int m = localTintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginTop, -1);
    if (m >= 0) {
      mTitleMarginTop = m;
    }
    int n = localTintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginBottom, -1);
    if (n >= 0) {
      mTitleMarginBottom = n;
    }
    mMaxButtonHeight = localTintTypedArray.getDimensionPixelSize(R.styleable.Toolbar_maxButtonHeight, -1);
    int i1 = localTintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetStart, Integer.MIN_VALUE);
    int i2 = localTintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetEnd, Integer.MIN_VALUE);
    int i3 = localTintTypedArray.getDimensionPixelSize(R.styleable.Toolbar_contentInsetLeft, 0);
    int i4 = localTintTypedArray.getDimensionPixelSize(R.styleable.Toolbar_contentInsetRight, 0);
    ensureContentInsets();
    mContentInsets.setAbsolute(i3, i4);
    if ((i1 != Integer.MIN_VALUE) || (i2 != Integer.MIN_VALUE)) {
      mContentInsets.setRelative(i1, i2);
    }
    mContentInsetStartWithNavigation = localTintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetStartWithNavigation, Integer.MIN_VALUE);
    mContentInsetEndWithActions = localTintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetEndWithActions, Integer.MIN_VALUE);
    mCollapseIcon = localTintTypedArray.getDrawable(R.styleable.Toolbar_collapseIcon);
    mCollapseDescription = localTintTypedArray.getText(R.styleable.Toolbar_collapseContentDescription);
    CharSequence localCharSequence1 = localTintTypedArray.getText(R.styleable.Toolbar_title);
    if (!TextUtils.isEmpty(localCharSequence1)) {
      setTitle(localCharSequence1);
    }
    CharSequence localCharSequence2 = localTintTypedArray.getText(R.styleable.Toolbar_subtitle);
    if (!TextUtils.isEmpty(localCharSequence2)) {
      setSubtitle(localCharSequence2);
    }
    mPopupContext = getContext();
    setPopupTheme(localTintTypedArray.getResourceId(R.styleable.Toolbar_popupTheme, 0));
    Drawable localDrawable1 = localTintTypedArray.getDrawable(R.styleable.Toolbar_navigationIcon);
    if (localDrawable1 != null) {
      setNavigationIcon(localDrawable1);
    }
    CharSequence localCharSequence3 = localTintTypedArray.getText(R.styleable.Toolbar_navigationContentDescription);
    if (!TextUtils.isEmpty(localCharSequence3)) {
      setNavigationContentDescription(localCharSequence3);
    }
    Drawable localDrawable2 = localTintTypedArray.getDrawable(R.styleable.Toolbar_logo);
    if (localDrawable2 != null) {
      setLogo(localDrawable2);
    }
    CharSequence localCharSequence4 = localTintTypedArray.getText(R.styleable.Toolbar_logoDescription);
    if (!TextUtils.isEmpty(localCharSequence4)) {
      setLogoDescription(localCharSequence4);
    }
    if (localTintTypedArray.hasValue(R.styleable.Toolbar_titleTextColor)) {
      setTitleTextColor(localTintTypedArray.getColor(R.styleable.Toolbar_titleTextColor, -1));
    }
    if (localTintTypedArray.hasValue(R.styleable.Toolbar_subtitleTextColor)) {
      setSubtitleTextColor(localTintTypedArray.getColor(R.styleable.Toolbar_subtitleTextColor, -1));
    }
    localTintTypedArray.recycle();
  }
  
  private void addCustomViewsWithGravity(List<View> paramList, int paramInt)
  {
    int i = 1;
    if (ViewCompat.getLayoutDirection(this) == i) {}
    int j;
    int k;
    for (;;)
    {
      j = getChildCount();
      k = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
      paramList.clear();
      if (i == 0) {
        break;
      }
      for (int n = j - 1; n >= 0; n--)
      {
        View localView2 = getChildAt(n);
        LayoutParams localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
        if ((mViewType == 0) && (shouldLayout(localView2)) && (getChildHorizontalGravity(gravity) == k)) {
          paramList.add(localView2);
        }
      }
      i = 0;
    }
    for (int m = 0; m < j; m++)
    {
      View localView1 = getChildAt(m);
      LayoutParams localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
      if ((mViewType == 0) && (shouldLayout(localView1)) && (getChildHorizontalGravity(gravity) == k)) {
        paramList.add(localView1);
      }
    }
  }
  
  private void addSystemView(View paramView, boolean paramBoolean)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    LayoutParams localLayoutParams1;
    if (localLayoutParams == null) {
      localLayoutParams1 = generateDefaultLayoutParams();
    }
    for (;;)
    {
      mViewType = 1;
      if ((!paramBoolean) || (mExpandedActionView == null)) {
        break;
      }
      paramView.setLayoutParams(localLayoutParams1);
      mHiddenViews.add(paramView);
      return;
      if (!checkLayoutParams(localLayoutParams)) {
        localLayoutParams1 = generateLayoutParams(localLayoutParams);
      } else {
        localLayoutParams1 = (LayoutParams)localLayoutParams;
      }
    }
    addView(paramView, localLayoutParams1);
  }
  
  private void ensureContentInsets()
  {
    if (mContentInsets == null) {
      mContentInsets = new RtlSpacingHelper();
    }
  }
  
  private void ensureLogoView()
  {
    if (mLogoView == null) {
      mLogoView = new AppCompatImageView(getContext());
    }
  }
  
  private void ensureMenu()
  {
    ensureMenuView();
    if (mMenuView.peekMenu() == null)
    {
      MenuBuilder localMenuBuilder = (MenuBuilder)mMenuView.getMenu();
      if (mExpandedMenuPresenter == null) {
        mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
      }
      mMenuView.setExpandedActionViewsExclusive(true);
      localMenuBuilder.addMenuPresenter(mExpandedMenuPresenter, mPopupContext);
    }
  }
  
  private void ensureMenuView()
  {
    if (mMenuView == null)
    {
      mMenuView = new ActionMenuView(getContext());
      mMenuView.setPopupTheme(mPopupTheme);
      mMenuView.setOnMenuItemClickListener(mMenuViewItemClickListener);
      mMenuView.setMenuCallbacks(mActionMenuPresenterCallback, mMenuBuilderCallback);
      LayoutParams localLayoutParams = generateDefaultLayoutParams();
      gravity = (0x800005 | 0x70 & mButtonGravity);
      mMenuView.setLayoutParams(localLayoutParams);
      addSystemView(mMenuView, false);
    }
  }
  
  private void ensureNavButtonView()
  {
    if (mNavButtonView == null)
    {
      mNavButtonView = new AppCompatImageButton(getContext(), null, R.attr.toolbarNavigationButtonStyle);
      LayoutParams localLayoutParams = generateDefaultLayoutParams();
      gravity = (0x800003 | 0x70 & mButtonGravity);
      mNavButtonView.setLayoutParams(localLayoutParams);
    }
  }
  
  private int getChildHorizontalGravity(int paramInt)
  {
    int i = ViewCompat.getLayoutDirection(this);
    int j = 0x7 & GravityCompat.getAbsoluteGravity(paramInt, i);
    switch (j)
    {
    case 2: 
    case 4: 
    default: 
      if (i != 1) {
        break;
      }
    }
    for (int k = 5;; k = 3)
    {
      j = k;
      return j;
    }
  }
  
  private int getChildTop(View paramView, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = paramView.getMeasuredHeight();
    int j;
    int k;
    int m;
    int n;
    int i1;
    if (paramInt > 0)
    {
      j = (i - paramInt) / 2;
      switch (getChildVerticalGravity(gravity))
      {
      default: 
        k = getPaddingTop();
        m = getPaddingBottom();
        n = getHeight();
        i1 = (n - k - m - i) / 2;
        if (i1 < topMargin) {
          i1 = topMargin;
        }
        break;
      }
    }
    for (;;)
    {
      return k + i1;
      j = 0;
      break;
      return getPaddingTop() - j;
      return getHeight() - getPaddingBottom() - i - bottomMargin - j;
      int i2 = n - m - i - i1 - k;
      if (i2 < bottomMargin) {
        i1 = Math.max(0, i1 - (bottomMargin - i2));
      }
    }
  }
  
  private int getChildVerticalGravity(int paramInt)
  {
    int i = paramInt & 0x70;
    switch (i)
    {
    default: 
      i = 0x70 & mGravity;
    }
    return i;
  }
  
  private int getHorizontalMargins(View paramView)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    return MarginLayoutParamsCompat.getMarginStart(localMarginLayoutParams) + MarginLayoutParamsCompat.getMarginEnd(localMarginLayoutParams);
  }
  
  private MenuInflater getMenuInflater()
  {
    return new SupportMenuInflater(getContext());
  }
  
  private int getVerticalMargins(View paramView)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    return topMargin + bottomMargin;
  }
  
  private int getViewListMeasuredWidth(List<View> paramList, int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt[0];
    int j = paramArrayOfInt[1];
    int k = 0;
    int m = paramList.size();
    for (int n = 0; n < m; n++)
    {
      View localView = (View)paramList.get(n);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      int i1 = leftMargin - i;
      int i2 = rightMargin - j;
      int i3 = Math.max(0, i1);
      int i4 = Math.max(0, i2);
      i = Math.max(0, -i1);
      j = Math.max(0, -i2);
      k += i4 + (i3 + localView.getMeasuredWidth());
    }
    return k;
  }
  
  private boolean isChildOrHidden(View paramView)
  {
    return (paramView.getParent() == this) || (mHiddenViews.contains(paramView));
  }
  
  private static boolean isCustomView(View paramView)
  {
    return getLayoutParamsmViewType == 0;
  }
  
  private int layoutChildLeft(View paramView, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = leftMargin - paramArrayOfInt[0];
    int j = paramInt1 + Math.max(0, i);
    paramArrayOfInt[0] = Math.max(0, -i);
    int k = getChildTop(paramView, paramInt2);
    int m = paramView.getMeasuredWidth();
    paramView.layout(j, k, j + m, k + paramView.getMeasuredHeight());
    return j + (m + rightMargin);
  }
  
  private int layoutChildRight(View paramView, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = rightMargin - paramArrayOfInt[1];
    int j = paramInt1 - Math.max(0, i);
    paramArrayOfInt[1] = Math.max(0, -i);
    int k = getChildTop(paramView, paramInt2);
    int m = paramView.getMeasuredWidth();
    paramView.layout(j - m, k, j, k + paramView.getMeasuredHeight());
    return j - (m + leftMargin);
  }
  
  private int measureChildCollapseMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    int i = leftMargin - paramArrayOfInt[0];
    int j = rightMargin - paramArrayOfInt[1];
    int k = Math.max(0, i) + Math.max(0, j);
    paramArrayOfInt[0] = Math.max(0, -i);
    paramArrayOfInt[1] = Math.max(0, -j);
    paramView.measure(getChildMeasureSpec(paramInt1, paramInt2 + (k + (getPaddingLeft() + getPaddingRight())), width), getChildMeasureSpec(paramInt3, paramInt4 + (getPaddingTop() + getPaddingBottom() + topMargin + bottomMargin), height));
    return k + paramView.getMeasuredWidth();
  }
  
  private void measureChildConstrained(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    int i = getChildMeasureSpec(paramInt1, paramInt2 + (getPaddingLeft() + getPaddingRight() + leftMargin + rightMargin), width);
    int j = getChildMeasureSpec(paramInt3, paramInt4 + (getPaddingTop() + getPaddingBottom() + topMargin + bottomMargin), height);
    int k = View.MeasureSpec.getMode(j);
    if ((k != 1073741824) && (paramInt5 >= 0)) {
      if (k == 0) {
        break label135;
      }
    }
    label135:
    for (int m = Math.min(View.MeasureSpec.getSize(j), paramInt5);; m = paramInt5)
    {
      j = View.MeasureSpec.makeMeasureSpec(m, 1073741824);
      paramView.measure(i, j);
      return;
    }
  }
  
  private void postShowOverflowMenu()
  {
    removeCallbacks(mShowOverflowMenuRunnable);
    post(mShowOverflowMenuRunnable);
  }
  
  private boolean shouldCollapse()
  {
    if (!mCollapsible) {
      return false;
    }
    int i = getChildCount();
    for (int j = 0;; j++)
    {
      if (j >= i) {
        break label55;
      }
      View localView = getChildAt(j);
      if ((shouldLayout(localView)) && (localView.getMeasuredWidth() > 0) && (localView.getMeasuredHeight() > 0)) {
        break;
      }
    }
    label55:
    return true;
  }
  
  private boolean shouldLayout(View paramView)
  {
    return (paramView != null) && (paramView.getParent() == this) && (paramView.getVisibility() != 8);
  }
  
  void addChildrenForExpandedActionView()
  {
    for (int i = -1 + mHiddenViews.size(); i >= 0; i--) {
      addView((View)mHiddenViews.get(i));
    }
    mHiddenViews.clear();
  }
  
  public boolean canShowOverflowMenu()
  {
    return (getVisibility() == 0) && (mMenuView != null) && (mMenuView.isOverflowReserved());
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return (super.checkLayoutParams(paramLayoutParams)) && ((paramLayoutParams instanceof LayoutParams));
  }
  
  public void collapseActionView()
  {
    if (mExpandedMenuPresenter == null) {}
    for (MenuItemImpl localMenuItemImpl = null;; localMenuItemImpl = mExpandedMenuPresenter.mCurrentExpandedItem)
    {
      if (localMenuItemImpl != null) {
        localMenuItemImpl.collapseActionView();
      }
      return;
    }
  }
  
  public void dismissPopupMenus()
  {
    if (mMenuView != null) {
      mMenuView.dismissPopupMenus();
    }
  }
  
  void ensureCollapseButtonView()
  {
    if (mCollapseButtonView == null)
    {
      mCollapseButtonView = new AppCompatImageButton(getContext(), null, R.attr.toolbarNavigationButtonStyle);
      mCollapseButtonView.setImageDrawable(mCollapseIcon);
      mCollapseButtonView.setContentDescription(mCollapseDescription);
      LayoutParams localLayoutParams = generateDefaultLayoutParams();
      gravity = (0x800003 | 0x70 & mButtonGravity);
      mViewType = 2;
      mCollapseButtonView.setLayoutParams(localLayoutParams);
      mCollapseButtonView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          collapseActionView();
        }
      });
    }
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof LayoutParams)) {
      return new LayoutParams((LayoutParams)paramLayoutParams);
    }
    if ((paramLayoutParams instanceof ActionBar.LayoutParams)) {
      return new LayoutParams((ActionBar.LayoutParams)paramLayoutParams);
    }
    if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  public int getContentInsetEnd()
  {
    if (mContentInsets != null) {
      return mContentInsets.getEnd();
    }
    return 0;
  }
  
  public int getContentInsetEndWithActions()
  {
    if (mContentInsetEndWithActions != Integer.MIN_VALUE) {
      return mContentInsetEndWithActions;
    }
    return getContentInsetEnd();
  }
  
  public int getContentInsetLeft()
  {
    if (mContentInsets != null) {
      return mContentInsets.getLeft();
    }
    return 0;
  }
  
  public int getContentInsetRight()
  {
    if (mContentInsets != null) {
      return mContentInsets.getRight();
    }
    return 0;
  }
  
  public int getContentInsetStart()
  {
    if (mContentInsets != null) {
      return mContentInsets.getStart();
    }
    return 0;
  }
  
  public int getContentInsetStartWithNavigation()
  {
    if (mContentInsetStartWithNavigation != Integer.MIN_VALUE) {
      return mContentInsetStartWithNavigation;
    }
    return getContentInsetStart();
  }
  
  public int getCurrentContentInsetEnd()
  {
    ActionMenuView localActionMenuView = mMenuView;
    int i = 0;
    if (localActionMenuView != null)
    {
      MenuBuilder localMenuBuilder = mMenuView.peekMenu();
      if ((localMenuBuilder == null) || (!localMenuBuilder.hasVisibleItems())) {
        break label52;
      }
    }
    label52:
    for (i = 1; i != 0; i = 0) {
      return Math.max(getContentInsetEnd(), Math.max(mContentInsetEndWithActions, 0));
    }
    return getContentInsetEnd();
  }
  
  public int getCurrentContentInsetLeft()
  {
    if (ViewCompat.getLayoutDirection(this) == 1) {
      return getCurrentContentInsetEnd();
    }
    return getCurrentContentInsetStart();
  }
  
  public int getCurrentContentInsetRight()
  {
    if (ViewCompat.getLayoutDirection(this) == 1) {
      return getCurrentContentInsetStart();
    }
    return getCurrentContentInsetEnd();
  }
  
  public int getCurrentContentInsetStart()
  {
    if (getNavigationIcon() != null) {
      return Math.max(getContentInsetStart(), Math.max(mContentInsetStartWithNavigation, 0));
    }
    return getContentInsetStart();
  }
  
  public Drawable getLogo()
  {
    if (mLogoView != null) {
      return mLogoView.getDrawable();
    }
    return null;
  }
  
  public CharSequence getLogoDescription()
  {
    if (mLogoView != null) {
      return mLogoView.getContentDescription();
    }
    return null;
  }
  
  public Menu getMenu()
  {
    ensureMenu();
    return mMenuView.getMenu();
  }
  
  @Nullable
  public CharSequence getNavigationContentDescription()
  {
    if (mNavButtonView != null) {
      return mNavButtonView.getContentDescription();
    }
    return null;
  }
  
  @Nullable
  public Drawable getNavigationIcon()
  {
    if (mNavButtonView != null) {
      return mNavButtonView.getDrawable();
    }
    return null;
  }
  
  @Nullable
  public Drawable getOverflowIcon()
  {
    ensureMenu();
    return mMenuView.getOverflowIcon();
  }
  
  public int getPopupTheme()
  {
    return mPopupTheme;
  }
  
  public CharSequence getSubtitle()
  {
    return mSubtitleText;
  }
  
  public CharSequence getTitle()
  {
    return mTitleText;
  }
  
  public int getTitleMarginBottom()
  {
    return mTitleMarginBottom;
  }
  
  public int getTitleMarginEnd()
  {
    return mTitleMarginEnd;
  }
  
  public int getTitleMarginStart()
  {
    return mTitleMarginStart;
  }
  
  public int getTitleMarginTop()
  {
    return mTitleMarginTop;
  }
  
  public DecorToolbar getWrapper()
  {
    if (mWrapper == null) {
      mWrapper = new ToolbarWidgetWrapper(this, true);
    }
    return mWrapper;
  }
  
  public boolean hasExpandedActionView()
  {
    return (mExpandedMenuPresenter != null) && (mExpandedMenuPresenter.mCurrentExpandedItem != null);
  }
  
  public boolean hideOverflowMenu()
  {
    return (mMenuView != null) && (mMenuView.hideOverflowMenu());
  }
  
  public void inflateMenu(@MenuRes int paramInt)
  {
    getMenuInflater().inflate(paramInt, getMenu());
  }
  
  public boolean isOverflowMenuShowPending()
  {
    return (mMenuView != null) && (mMenuView.isOverflowMenuShowPending());
  }
  
  public boolean isOverflowMenuShowing()
  {
    return (mMenuView != null) && (mMenuView.isOverflowMenuShowing());
  }
  
  public boolean isTitleTruncated()
  {
    if (mTitleTextView == null) {}
    for (;;)
    {
      return false;
      Layout localLayout = mTitleTextView.getLayout();
      if (localLayout != null)
      {
        int i = localLayout.getLineCount();
        for (int j = 0; j < i; j++) {
          if (localLayout.getEllipsisCount(j) > 0) {
            return true;
          }
        }
      }
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    removeCallbacks(mShowOverflowMenuRunnable);
  }
  
  public boolean onHoverEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if (i == 9) {
      mEatingHover = false;
    }
    if (!mEatingHover)
    {
      boolean bool = super.onHoverEvent(paramMotionEvent);
      if ((i == 9) && (!bool)) {
        mEatingHover = true;
      }
    }
    if ((i == 10) || (i == 3)) {
      mEatingHover = false;
    }
    return true;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i;
    int j;
    int k;
    int m;
    int n;
    int i1;
    int i2;
    int i3;
    int i4;
    int[] arrayOfInt;
    int i6;
    label96:
    label128:
    label160:
    label192:
    int i9;
    int i11;
    label296:
    label328:
    boolean bool1;
    boolean bool2;
    int i12;
    TextView localTextView1;
    label453:
    TextView localTextView2;
    label464:
    LayoutParams localLayoutParams1;
    LayoutParams localLayoutParams2;
    int i13;
    label517:
    int i42;
    label598:
    int i14;
    if (ViewCompat.getLayoutDirection(this) == 1)
    {
      i = 1;
      j = getWidth();
      k = getHeight();
      m = getPaddingLeft();
      n = getPaddingRight();
      i1 = getPaddingTop();
      i2 = getPaddingBottom();
      i3 = m;
      i4 = j - n;
      arrayOfInt = mTempMargins;
      arrayOfInt[1] = 0;
      arrayOfInt[0] = 0;
      int i5 = ViewCompat.getMinimumHeight(this);
      if (i5 < 0) {
        break label897;
      }
      i6 = Math.min(i5, paramInt4 - paramInt2);
      if (shouldLayout(mNavButtonView))
      {
        if (i == 0) {
          break label903;
        }
        i4 = layoutChildRight(mNavButtonView, i4, arrayOfInt, i6);
      }
      if (shouldLayout(mCollapseButtonView))
      {
        if (i == 0) {
          break label922;
        }
        i4 = layoutChildRight(mCollapseButtonView, i4, arrayOfInt, i6);
      }
      if (shouldLayout(mMenuView))
      {
        if (i == 0) {
          break label941;
        }
        i3 = layoutChildLeft(mMenuView, i3, arrayOfInt, i6);
      }
      int i7 = getCurrentContentInsetLeft();
      int i8 = getCurrentContentInsetRight();
      arrayOfInt[0] = Math.max(0, i7 - i3);
      arrayOfInt[1] = Math.max(0, i8 - (j - n - i4));
      i9 = Math.max(i3, i7);
      int i10 = j - n - i8;
      i11 = Math.min(i4, i10);
      if (shouldLayout(mExpandedActionView))
      {
        if (i == 0) {
          break label960;
        }
        i11 = layoutChildRight(mExpandedActionView, i11, arrayOfInt, i6);
      }
      if (shouldLayout(mLogoView))
      {
        if (i == 0) {
          break label979;
        }
        i11 = layoutChildRight(mLogoView, i11, arrayOfInt, i6);
      }
      bool1 = shouldLayout(mTitleTextView);
      bool2 = shouldLayout(mSubtitleTextView);
      i12 = 0;
      if (bool1)
      {
        LayoutParams localLayoutParams8 = (LayoutParams)mTitleTextView.getLayoutParams();
        i12 = 0 + (topMargin + mTitleTextView.getMeasuredHeight() + bottomMargin);
      }
      if (bool2)
      {
        LayoutParams localLayoutParams7 = (LayoutParams)mSubtitleTextView.getLayoutParams();
        i12 += topMargin + mSubtitleTextView.getMeasuredHeight() + bottomMargin;
      }
      if ((bool1) || (bool2))
      {
        if (!bool1) {
          break label998;
        }
        localTextView1 = mTitleTextView;
        if (!bool2) {
          break label1007;
        }
        localTextView2 = mSubtitleTextView;
        localLayoutParams1 = (LayoutParams)localTextView1.getLayoutParams();
        localLayoutParams2 = (LayoutParams)localTextView2.getLayoutParams();
        if (((!bool1) || (mTitleTextView.getMeasuredWidth() <= 0)) && ((!bool2) || (mSubtitleTextView.getMeasuredWidth() <= 0))) {
          break label1016;
        }
        i13 = 1;
        switch (0x70 & mGravity)
        {
        default: 
          i42 = (k - i1 - i2 - i12) / 2;
          int i43 = topMargin + mTitleMarginTop;
          if (i42 < i43)
          {
            i42 = topMargin + mTitleMarginTop;
            i14 = i1 + i42;
            label605:
            if (i == 0) {
              break label1128;
            }
            if (i13 == 0) {
              break label1122;
            }
          }
          break;
        }
      }
    }
    label897:
    label903:
    label922:
    label941:
    label960:
    label979:
    label998:
    label1007:
    label1016:
    label1122:
    for (int i33 = mTitleMarginStart;; i33 = 0)
    {
      int i34 = i33 - arrayOfInt[1];
      i11 -= Math.max(0, i34);
      arrayOfInt[1] = Math.max(0, -i34);
      int i35 = i11;
      int i36 = i11;
      if (bool1)
      {
        LayoutParams localLayoutParams6 = (LayoutParams)mTitleTextView.getLayoutParams();
        int i40 = i35 - mTitleTextView.getMeasuredWidth();
        int i41 = i14 + mTitleTextView.getMeasuredHeight();
        mTitleTextView.layout(i40, i14, i35, i41);
        i35 = i40 - mTitleMarginEnd;
        i14 = i41 + bottomMargin;
      }
      if (bool2)
      {
        LayoutParams localLayoutParams5 = (LayoutParams)mSubtitleTextView.getLayoutParams();
        int i37 = i14 + topMargin;
        int i38 = i36 - mSubtitleTextView.getMeasuredWidth();
        int i39 = i37 + mSubtitleTextView.getMeasuredHeight();
        mSubtitleTextView.layout(i38, i37, i36, i39);
        i36 -= mTitleMarginEnd;
        (i39 + bottomMargin);
      }
      if (i13 != 0) {
        i11 = Math.min(i35, i36);
      }
      addCustomViewsWithGravity(mTempViews, 3);
      int i19 = mTempViews.size();
      for (int i20 = 0; i20 < i19; i20++) {
        i9 = layoutChildLeft((View)mTempViews.get(i20), i9, arrayOfInt, i6);
      }
      i = 0;
      break;
      i6 = 0;
      break label96;
      i3 = layoutChildLeft(mNavButtonView, i3, arrayOfInt, i6);
      break label128;
      i3 = layoutChildLeft(mCollapseButtonView, i3, arrayOfInt, i6);
      break label160;
      i4 = layoutChildRight(mMenuView, i4, arrayOfInt, i6);
      break label192;
      i9 = layoutChildLeft(mExpandedActionView, i9, arrayOfInt, i6);
      break label296;
      i9 = layoutChildLeft(mLogoView, i9, arrayOfInt, i6);
      break label328;
      localTextView1 = mSubtitleTextView;
      break label453;
      localTextView2 = mTitleTextView;
      break label464;
      i13 = 0;
      break label517;
      i14 = getPaddingTop() + topMargin + mTitleMarginTop;
      break label605;
      int i44 = k - i2 - i12 - i42 - i1;
      if (i44 >= bottomMargin + mTitleMarginBottom) {
        break label598;
      }
      i42 = Math.max(0, i42 - (bottomMargin + mTitleMarginBottom - i44));
      break label598;
      i14 = k - i2 - bottomMargin - mTitleMarginBottom - i12;
      break label605;
    }
    label1128:
    if (i13 != 0) {}
    for (int i15 = mTitleMarginStart;; i15 = 0)
    {
      int i16 = i15 - arrayOfInt[0];
      i9 += Math.max(0, i16);
      arrayOfInt[0] = Math.max(0, -i16);
      int i17 = i9;
      int i18 = i9;
      if (bool1)
      {
        LayoutParams localLayoutParams4 = (LayoutParams)mTitleTextView.getLayoutParams();
        int i31 = i17 + mTitleTextView.getMeasuredWidth();
        int i32 = i14 + mTitleTextView.getMeasuredHeight();
        mTitleTextView.layout(i17, i14, i31, i32);
        i17 = i31 + mTitleMarginEnd;
        i14 = i32 + bottomMargin;
      }
      if (bool2)
      {
        LayoutParams localLayoutParams3 = (LayoutParams)mSubtitleTextView.getLayoutParams();
        int i28 = i14 + topMargin;
        int i29 = i18 + mSubtitleTextView.getMeasuredWidth();
        int i30 = i28 + mSubtitleTextView.getMeasuredHeight();
        mSubtitleTextView.layout(i18, i28, i29, i30);
        i18 = i29 + mTitleMarginEnd;
        (i30 + bottomMargin);
      }
      if (i13 == 0) {
        break;
      }
      i9 = Math.max(i17, i18);
      break;
    }
    addCustomViewsWithGravity(mTempViews, 5);
    int i21 = mTempViews.size();
    for (int i22 = 0; i22 < i21; i22++) {
      i11 = layoutChildRight((View)mTempViews.get(i22), i11, arrayOfInt, i6);
    }
    addCustomViewsWithGravity(mTempViews, 1);
    int i23 = getViewListMeasuredWidth(mTempViews, arrayOfInt);
    int i24 = m + (j - m - n) / 2 - i23 / 2;
    int i25 = i24 + i23;
    if (i24 < i9) {
      i24 = i9;
    }
    for (;;)
    {
      int i26 = mTempViews.size();
      for (int i27 = 0; i27 < i26; i27++) {
        i24 = layoutChildLeft((View)mTempViews.get(i27), i24, arrayOfInt, i6);
      }
      if (i25 > i11) {
        i24 -= i25 - i11;
      }
    }
    mTempViews.clear();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = mTempMargins;
    int j;
    int i;
    int k;
    int m;
    int i5;
    int i7;
    label519:
    View localView;
    if (ViewUtils.isLayoutRtl(this))
    {
      j = 1;
      i = 0;
      boolean bool1 = shouldLayout(mNavButtonView);
      k = 0;
      m = 0;
      int n = 0;
      if (bool1)
      {
        measureChildConstrained(mNavButtonView, paramInt1, 0, paramInt2, 0, mMaxButtonHeight);
        n = mNavButtonView.getMeasuredWidth() + getHorizontalMargins(mNavButtonView);
        m = Math.max(0, mNavButtonView.getMeasuredHeight() + getVerticalMargins(mNavButtonView));
        k = ViewUtils.combineMeasuredStates(0, ViewCompat.getMeasuredState(mNavButtonView));
      }
      if (shouldLayout(mCollapseButtonView))
      {
        measureChildConstrained(mCollapseButtonView, paramInt1, 0, paramInt2, 0, mMaxButtonHeight);
        n = mCollapseButtonView.getMeasuredWidth() + getHorizontalMargins(mCollapseButtonView);
        int i29 = mCollapseButtonView.getMeasuredHeight() + getVerticalMargins(mCollapseButtonView);
        m = Math.max(m, i29);
        int i30 = ViewCompat.getMeasuredState(mCollapseButtonView);
        k = ViewUtils.combineMeasuredStates(k, i30);
      }
      int i1 = getCurrentContentInsetStart();
      int i2 = 0 + Math.max(i1, n);
      arrayOfInt[j] = Math.max(0, i1 - n);
      boolean bool2 = shouldLayout(mMenuView);
      int i3 = 0;
      if (bool2)
      {
        measureChildConstrained(mMenuView, paramInt1, i2, paramInt2, 0, mMaxButtonHeight);
        i3 = mMenuView.getMeasuredWidth() + getHorizontalMargins(mMenuView);
        int i27 = mMenuView.getMeasuredHeight() + getVerticalMargins(mMenuView);
        m = Math.max(m, i27);
        int i28 = ViewCompat.getMeasuredState(mMenuView);
        k = ViewUtils.combineMeasuredStates(k, i28);
      }
      int i4 = getCurrentContentInsetEnd();
      i5 = i2 + Math.max(i4, i3);
      arrayOfInt[i] = Math.max(0, i4 - i3);
      if (shouldLayout(mExpandedActionView))
      {
        i5 += measureChildCollapseMargins(mExpandedActionView, paramInt1, i5, paramInt2, 0, arrayOfInt);
        int i25 = mExpandedActionView.getMeasuredHeight() + getVerticalMargins(mExpandedActionView);
        m = Math.max(m, i25);
        int i26 = ViewCompat.getMeasuredState(mExpandedActionView);
        k = ViewUtils.combineMeasuredStates(k, i26);
      }
      if (shouldLayout(mLogoView))
      {
        i5 += measureChildCollapseMargins(mLogoView, paramInt1, i5, paramInt2, 0, arrayOfInt);
        int i23 = mLogoView.getMeasuredHeight() + getVerticalMargins(mLogoView);
        m = Math.max(m, i23);
        int i24 = ViewCompat.getMeasuredState(mLogoView);
        k = ViewUtils.combineMeasuredStates(k, i24);
      }
      int i6 = getChildCount();
      i7 = 0;
      if (i7 >= i6) {
        break label631;
      }
      localView = getChildAt(i7);
      if ((getLayoutParamsmViewType == 0) && (shouldLayout(localView))) {
        break label572;
      }
    }
    for (;;)
    {
      i7++;
      break label519;
      i = 1;
      j = 0;
      break;
      label572:
      i5 += measureChildCollapseMargins(localView, paramInt1, i5, paramInt2, 0, arrayOfInt);
      int i21 = localView.getMeasuredHeight() + getVerticalMargins(localView);
      m = Math.max(m, i21);
      int i22 = ViewCompat.getMeasuredState(localView);
      k = ViewUtils.combineMeasuredStates(k, i22);
    }
    label631:
    int i8 = mTitleMarginTop + mTitleMarginBottom;
    int i9 = mTitleMarginStart + mTitleMarginEnd;
    boolean bool3 = shouldLayout(mTitleTextView);
    int i10 = 0;
    int i11 = 0;
    if (bool3)
    {
      measureChildCollapseMargins(mTitleTextView, paramInt1, i5 + i9, paramInt2, i8, arrayOfInt);
      i11 = mTitleTextView.getMeasuredWidth() + getHorizontalMargins(mTitleTextView);
      i10 = mTitleTextView.getMeasuredHeight() + getVerticalMargins(mTitleTextView);
      int i20 = ViewCompat.getMeasuredState(mTitleTextView);
      k = ViewUtils.combineMeasuredStates(k, i20);
    }
    if (shouldLayout(mSubtitleTextView))
    {
      int i18 = measureChildCollapseMargins(mSubtitleTextView, paramInt1, i5 + i9, paramInt2, i10 + i8, arrayOfInt);
      i11 = Math.max(i11, i18);
      i10 += mSubtitleTextView.getMeasuredHeight() + getVerticalMargins(mSubtitleTextView);
      int i19 = ViewCompat.getMeasuredState(mSubtitleTextView);
      k = ViewUtils.combineMeasuredStates(k, i19);
    }
    int i12 = i5 + i11;
    int i13 = Math.max(m, i10);
    int i14 = i12 + (getPaddingLeft() + getPaddingRight());
    int i15 = i13 + (getPaddingTop() + getPaddingBottom());
    int i16 = ViewCompat.resolveSizeAndState(Math.max(i14, getSuggestedMinimumWidth()), paramInt1, 0xFF000000 & k);
    int i17 = ViewCompat.resolveSizeAndState(Math.max(i15, getSuggestedMinimumHeight()), paramInt2, k << 16);
    if (shouldCollapse()) {
      i17 = 0;
    }
    setMeasuredDimension(i16, i17);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(localSavedState.getSuperState());
    if (mMenuView != null) {}
    for (MenuBuilder localMenuBuilder = mMenuView.peekMenu();; localMenuBuilder = null)
    {
      if ((expandedMenuItemId != 0) && (mExpandedMenuPresenter != null) && (localMenuBuilder != null))
      {
        MenuItem localMenuItem = localMenuBuilder.findItem(expandedMenuItemId);
        if (localMenuItem != null) {
          MenuItemCompat.expandActionView(localMenuItem);
        }
      }
      if (!isOverflowOpen) {
        break;
      }
      postShowOverflowMenu();
      return;
    }
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    int i = 1;
    if (Build.VERSION.SDK_INT >= 17) {
      super.onRtlPropertiesChanged(paramInt);
    }
    ensureContentInsets();
    RtlSpacingHelper localRtlSpacingHelper = mContentInsets;
    if (paramInt == i) {}
    for (;;)
    {
      localRtlSpacingHelper.setDirection(i);
      return;
      i = 0;
    }
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    if ((mExpandedMenuPresenter != null) && (mExpandedMenuPresenter.mCurrentExpandedItem != null)) {
      expandedMenuItemId = mExpandedMenuPresenter.mCurrentExpandedItem.getItemId();
    }
    isOverflowOpen = isOverflowMenuShowing();
    return localSavedState;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if (i == 0) {
      mEatingTouch = false;
    }
    if (!mEatingTouch)
    {
      boolean bool = super.onTouchEvent(paramMotionEvent);
      if ((i == 0) && (!bool)) {
        mEatingTouch = true;
      }
    }
    if ((i == 1) || (i == 3)) {
      mEatingTouch = false;
    }
    return true;
  }
  
  void removeChildrenForExpandedActionView()
  {
    for (int i = -1 + getChildCount(); i >= 0; i--)
    {
      View localView = getChildAt(i);
      if ((getLayoutParamsmViewType != 2) && (localView != mMenuView))
      {
        removeViewAt(i);
        mHiddenViews.add(localView);
      }
    }
  }
  
  public void setCollapsible(boolean paramBoolean)
  {
    mCollapsible = paramBoolean;
    requestLayout();
  }
  
  public void setContentInsetEndWithActions(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = Integer.MIN_VALUE;
    }
    if (paramInt != mContentInsetEndWithActions)
    {
      mContentInsetEndWithActions = paramInt;
      if (getNavigationIcon() != null) {
        requestLayout();
      }
    }
  }
  
  public void setContentInsetStartWithNavigation(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = Integer.MIN_VALUE;
    }
    if (paramInt != mContentInsetStartWithNavigation)
    {
      mContentInsetStartWithNavigation = paramInt;
      if (getNavigationIcon() != null) {
        requestLayout();
      }
    }
  }
  
  public void setContentInsetsAbsolute(int paramInt1, int paramInt2)
  {
    ensureContentInsets();
    mContentInsets.setAbsolute(paramInt1, paramInt2);
  }
  
  public void setContentInsetsRelative(int paramInt1, int paramInt2)
  {
    ensureContentInsets();
    mContentInsets.setRelative(paramInt1, paramInt2);
  }
  
  public void setLogo(@DrawableRes int paramInt)
  {
    setLogo(AppCompatResources.getDrawable(getContext(), paramInt));
  }
  
  public void setLogo(Drawable paramDrawable)
  {
    if (paramDrawable != null)
    {
      ensureLogoView();
      if (!isChildOrHidden(mLogoView)) {
        addSystemView(mLogoView, true);
      }
    }
    for (;;)
    {
      if (mLogoView != null) {
        mLogoView.setImageDrawable(paramDrawable);
      }
      return;
      if ((mLogoView != null) && (isChildOrHidden(mLogoView)))
      {
        removeView(mLogoView);
        mHiddenViews.remove(mLogoView);
      }
    }
  }
  
  public void setLogoDescription(@StringRes int paramInt)
  {
    setLogoDescription(getContext().getText(paramInt));
  }
  
  public void setLogoDescription(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence)) {
      ensureLogoView();
    }
    if (mLogoView != null) {
      mLogoView.setContentDescription(paramCharSequence);
    }
  }
  
  public void setMenu(MenuBuilder paramMenuBuilder, ActionMenuPresenter paramActionMenuPresenter)
  {
    if ((paramMenuBuilder == null) && (mMenuView == null)) {}
    MenuBuilder localMenuBuilder;
    do
    {
      return;
      ensureMenuView();
      localMenuBuilder = mMenuView.peekMenu();
    } while (localMenuBuilder == paramMenuBuilder);
    if (localMenuBuilder != null)
    {
      localMenuBuilder.removeMenuPresenter(mOuterActionMenuPresenter);
      localMenuBuilder.removeMenuPresenter(mExpandedMenuPresenter);
    }
    if (mExpandedMenuPresenter == null) {
      mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
    }
    paramActionMenuPresenter.setExpandedActionViewsExclusive(true);
    if (paramMenuBuilder != null)
    {
      paramMenuBuilder.addMenuPresenter(paramActionMenuPresenter, mPopupContext);
      paramMenuBuilder.addMenuPresenter(mExpandedMenuPresenter, mPopupContext);
    }
    for (;;)
    {
      mMenuView.setPopupTheme(mPopupTheme);
      mMenuView.setPresenter(paramActionMenuPresenter);
      mOuterActionMenuPresenter = paramActionMenuPresenter;
      return;
      paramActionMenuPresenter.initForMenu(mPopupContext, null);
      mExpandedMenuPresenter.initForMenu(mPopupContext, null);
      paramActionMenuPresenter.updateMenuView(true);
      mExpandedMenuPresenter.updateMenuView(true);
    }
  }
  
  public void setMenuCallbacks(MenuPresenter.Callback paramCallback, MenuBuilder.Callback paramCallback1)
  {
    mActionMenuPresenterCallback = paramCallback;
    mMenuBuilderCallback = paramCallback1;
    if (mMenuView != null) {
      mMenuView.setMenuCallbacks(paramCallback, paramCallback1);
    }
  }
  
  public void setNavigationContentDescription(@StringRes int paramInt)
  {
    if (paramInt != 0) {}
    for (CharSequence localCharSequence = getContext().getText(paramInt);; localCharSequence = null)
    {
      setNavigationContentDescription(localCharSequence);
      return;
    }
  }
  
  public void setNavigationContentDescription(@Nullable CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence)) {
      ensureNavButtonView();
    }
    if (mNavButtonView != null) {
      mNavButtonView.setContentDescription(paramCharSequence);
    }
  }
  
  public void setNavigationIcon(@DrawableRes int paramInt)
  {
    setNavigationIcon(AppCompatResources.getDrawable(getContext(), paramInt));
  }
  
  public void setNavigationIcon(@Nullable Drawable paramDrawable)
  {
    if (paramDrawable != null)
    {
      ensureNavButtonView();
      if (!isChildOrHidden(mNavButtonView)) {
        addSystemView(mNavButtonView, true);
      }
    }
    for (;;)
    {
      if (mNavButtonView != null) {
        mNavButtonView.setImageDrawable(paramDrawable);
      }
      return;
      if ((mNavButtonView != null) && (isChildOrHidden(mNavButtonView)))
      {
        removeView(mNavButtonView);
        mHiddenViews.remove(mNavButtonView);
      }
    }
  }
  
  public void setNavigationOnClickListener(View.OnClickListener paramOnClickListener)
  {
    ensureNavButtonView();
    mNavButtonView.setOnClickListener(paramOnClickListener);
  }
  
  public void setOnMenuItemClickListener(OnMenuItemClickListener paramOnMenuItemClickListener)
  {
    mOnMenuItemClickListener = paramOnMenuItemClickListener;
  }
  
  public void setOverflowIcon(@Nullable Drawable paramDrawable)
  {
    ensureMenu();
    mMenuView.setOverflowIcon(paramDrawable);
  }
  
  public void setPopupTheme(@StyleRes int paramInt)
  {
    if (mPopupTheme != paramInt)
    {
      mPopupTheme = paramInt;
      if (paramInt == 0) {
        mPopupContext = getContext();
      }
    }
    else
    {
      return;
    }
    mPopupContext = new ContextThemeWrapper(getContext(), paramInt);
  }
  
  public void setSubtitle(@StringRes int paramInt)
  {
    setSubtitle(getContext().getText(paramInt));
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence))
    {
      if (mSubtitleTextView == null)
      {
        Context localContext = getContext();
        mSubtitleTextView = new AppCompatTextView(localContext);
        mSubtitleTextView.setSingleLine();
        mSubtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        if (mSubtitleTextAppearance != 0) {
          mSubtitleTextView.setTextAppearance(localContext, mSubtitleTextAppearance);
        }
        if (mSubtitleTextColor != 0) {
          mSubtitleTextView.setTextColor(mSubtitleTextColor);
        }
      }
      if (!isChildOrHidden(mSubtitleTextView)) {
        addSystemView(mSubtitleTextView, true);
      }
    }
    for (;;)
    {
      if (mSubtitleTextView != null) {
        mSubtitleTextView.setText(paramCharSequence);
      }
      mSubtitleText = paramCharSequence;
      return;
      if ((mSubtitleTextView != null) && (isChildOrHidden(mSubtitleTextView)))
      {
        removeView(mSubtitleTextView);
        mHiddenViews.remove(mSubtitleTextView);
      }
    }
  }
  
  public void setSubtitleTextAppearance(Context paramContext, @StyleRes int paramInt)
  {
    mSubtitleTextAppearance = paramInt;
    if (mSubtitleTextView != null) {
      mSubtitleTextView.setTextAppearance(paramContext, paramInt);
    }
  }
  
  public void setSubtitleTextColor(@ColorInt int paramInt)
  {
    mSubtitleTextColor = paramInt;
    if (mSubtitleTextView != null) {
      mSubtitleTextView.setTextColor(paramInt);
    }
  }
  
  public void setTitle(@StringRes int paramInt)
  {
    setTitle(getContext().getText(paramInt));
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence))
    {
      if (mTitleTextView == null)
      {
        Context localContext = getContext();
        mTitleTextView = new AppCompatTextView(localContext);
        mTitleTextView.setSingleLine();
        mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        if (mTitleTextAppearance != 0) {
          mTitleTextView.setTextAppearance(localContext, mTitleTextAppearance);
        }
        if (mTitleTextColor != 0) {
          mTitleTextView.setTextColor(mTitleTextColor);
        }
      }
      if (!isChildOrHidden(mTitleTextView)) {
        addSystemView(mTitleTextView, true);
      }
    }
    for (;;)
    {
      if (mTitleTextView != null) {
        mTitleTextView.setText(paramCharSequence);
      }
      mTitleText = paramCharSequence;
      return;
      if ((mTitleTextView != null) && (isChildOrHidden(mTitleTextView)))
      {
        removeView(mTitleTextView);
        mHiddenViews.remove(mTitleTextView);
      }
    }
  }
  
  public void setTitleMargin(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    mTitleMarginStart = paramInt1;
    mTitleMarginTop = paramInt2;
    mTitleMarginEnd = paramInt3;
    mTitleMarginBottom = paramInt4;
    requestLayout();
  }
  
  public void setTitleMarginBottom(int paramInt)
  {
    mTitleMarginBottom = paramInt;
    requestLayout();
  }
  
  public void setTitleMarginEnd(int paramInt)
  {
    mTitleMarginEnd = paramInt;
    requestLayout();
  }
  
  public void setTitleMarginStart(int paramInt)
  {
    mTitleMarginStart = paramInt;
    requestLayout();
  }
  
  public void setTitleMarginTop(int paramInt)
  {
    mTitleMarginTop = paramInt;
    requestLayout();
  }
  
  public void setTitleTextAppearance(Context paramContext, @StyleRes int paramInt)
  {
    mTitleTextAppearance = paramInt;
    if (mTitleTextView != null) {
      mTitleTextView.setTextAppearance(paramContext, paramInt);
    }
  }
  
  public void setTitleTextColor(@ColorInt int paramInt)
  {
    mTitleTextColor = paramInt;
    if (mTitleTextView != null) {
      mTitleTextView.setTextColor(paramInt);
    }
  }
  
  public boolean showOverflowMenu()
  {
    return (mMenuView != null) && (mMenuView.showOverflowMenu());
  }
  
  private class ExpandedActionViewMenuPresenter
    implements MenuPresenter
  {
    MenuItemImpl mCurrentExpandedItem;
    MenuBuilder mMenu;
    
    ExpandedActionViewMenuPresenter() {}
    
    public boolean collapseItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl)
    {
      if ((mExpandedActionView instanceof CollapsibleActionView)) {
        ((CollapsibleActionView)mExpandedActionView).onActionViewCollapsed();
      }
      removeView(mExpandedActionView);
      removeView(mCollapseButtonView);
      mExpandedActionView = null;
      addChildrenForExpandedActionView();
      mCurrentExpandedItem = null;
      requestLayout();
      paramMenuItemImpl.setActionViewExpanded(false);
      return true;
    }
    
    public boolean expandItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl)
    {
      ensureCollapseButtonView();
      if (mCollapseButtonView.getParent() != Toolbar.this) {
        addView(mCollapseButtonView);
      }
      mExpandedActionView = paramMenuItemImpl.getActionView();
      mCurrentExpandedItem = paramMenuItemImpl;
      if (mExpandedActionView.getParent() != Toolbar.this)
      {
        Toolbar.LayoutParams localLayoutParams = generateDefaultLayoutParams();
        gravity = (0x800003 | 0x70 & mButtonGravity);
        mViewType = 2;
        mExpandedActionView.setLayoutParams(localLayoutParams);
        addView(mExpandedActionView);
      }
      removeChildrenForExpandedActionView();
      requestLayout();
      paramMenuItemImpl.setActionViewExpanded(true);
      if ((mExpandedActionView instanceof CollapsibleActionView)) {
        ((CollapsibleActionView)mExpandedActionView).onActionViewExpanded();
      }
      return true;
    }
    
    public boolean flagActionItems()
    {
      return false;
    }
    
    public int getId()
    {
      return 0;
    }
    
    public MenuView getMenuView(ViewGroup paramViewGroup)
    {
      return null;
    }
    
    public void initForMenu(Context paramContext, MenuBuilder paramMenuBuilder)
    {
      if ((mMenu != null) && (mCurrentExpandedItem != null)) {
        mMenu.collapseItemActionView(mCurrentExpandedItem);
      }
      mMenu = paramMenuBuilder;
    }
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {}
    
    public void onRestoreInstanceState(Parcelable paramParcelable) {}
    
    public Parcelable onSaveInstanceState()
    {
      return null;
    }
    
    public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder)
    {
      return false;
    }
    
    public void setCallback(MenuPresenter.Callback paramCallback) {}
    
    public void updateMenuView(boolean paramBoolean)
    {
      int i;
      int j;
      if (mCurrentExpandedItem != null)
      {
        MenuBuilder localMenuBuilder = mMenu;
        i = 0;
        if (localMenuBuilder != null) {
          j = mMenu.size();
        }
      }
      for (int k = 0;; k++)
      {
        i = 0;
        if (k < j)
        {
          if (mMenu.getItem(k) == mCurrentExpandedItem) {
            i = 1;
          }
        }
        else
        {
          if (i == 0) {
            collapseItemActionView(mMenu, mCurrentExpandedItem);
          }
          return;
        }
      }
    }
  }
  
  public static class LayoutParams
    extends ActionBar.LayoutParams
  {
    static final int CUSTOM = 0;
    static final int EXPANDED = 2;
    static final int SYSTEM = 1;
    int mViewType = 0;
    
    public LayoutParams(int paramInt)
    {
      this(-2, -1, paramInt);
    }
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      gravity = 8388627;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3)
    {
      super(paramInt2);
      gravity = paramInt3;
    }
    
    public LayoutParams(@NonNull Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ActionBar.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      mViewType = mViewType;
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
      copyMarginsFromCompat(paramMarginLayoutParams);
    }
    
    void copyMarginsFromCompat(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      leftMargin = leftMargin;
      topMargin = topMargin;
      rightMargin = rightMargin;
      bottomMargin = bottomMargin;
    }
  }
  
  public static abstract interface OnMenuItemClickListener
  {
    public abstract boolean onMenuItemClick(MenuItem paramMenuItem);
  }
  
  public static class SavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public Toolbar.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new Toolbar.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public Toolbar.SavedState[] newArray(int paramAnonymousInt)
      {
        return new Toolbar.SavedState[paramAnonymousInt];
      }
    });
    int expandedMenuItemId;
    boolean isOverflowOpen;
    
    public SavedState(Parcel paramParcel)
    {
      this(paramParcel, null);
    }
    
    public SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      expandedMenuItemId = paramParcel.readInt();
      if (paramParcel.readInt() != 0) {}
      for (boolean bool = true;; bool = false)
      {
        isOverflowOpen = bool;
        return;
      }
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(expandedMenuItemId);
      if (isOverflowOpen) {}
      for (int i = 1;; i = 0)
      {
        paramParcel.writeInt(i);
        return;
      }
    }
  }
}
