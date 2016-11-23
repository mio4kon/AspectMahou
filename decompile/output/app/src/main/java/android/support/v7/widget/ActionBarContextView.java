package android.support.v7.widget;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.layout;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.view.ActionMode;
import android.support.v7.view.menu.MenuBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActionBarContextView
  extends AbsActionBarView
{
  private static final String TAG = "ActionBarContextView";
  private View mClose;
  private int mCloseItemLayout;
  private View mCustomView;
  private CharSequence mSubtitle;
  private int mSubtitleStyleRes;
  private TextView mSubtitleView;
  private CharSequence mTitle;
  private LinearLayout mTitleLayout;
  private boolean mTitleOptional;
  private int mTitleStyleRes;
  private TextView mTitleView;
  
  public ActionBarContextView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ActionBarContextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.actionModeStyle);
  }
  
  public ActionBarContextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.ActionMode, paramInt, 0);
    setBackgroundDrawable(localTintTypedArray.getDrawable(R.styleable.ActionMode_background));
    mTitleStyleRes = localTintTypedArray.getResourceId(R.styleable.ActionMode_titleTextStyle, 0);
    mSubtitleStyleRes = localTintTypedArray.getResourceId(R.styleable.ActionMode_subtitleTextStyle, 0);
    mContentHeight = localTintTypedArray.getLayoutDimension(R.styleable.ActionMode_height, 0);
    mCloseItemLayout = localTintTypedArray.getResourceId(R.styleable.ActionMode_closeItemLayout, R.layout.abc_action_mode_close_item_material);
    localTintTypedArray.recycle();
  }
  
  private void initTitle()
  {
    int i = 8;
    if (mTitleLayout == null)
    {
      LayoutInflater.from(getContext()).inflate(R.layout.abc_action_bar_title_item, this);
      mTitleLayout = ((LinearLayout)getChildAt(-1 + getChildCount()));
      mTitleView = ((TextView)mTitleLayout.findViewById(R.id.action_bar_title));
      mSubtitleView = ((TextView)mTitleLayout.findViewById(R.id.action_bar_subtitle));
      if (mTitleStyleRes != 0) {
        mTitleView.setTextAppearance(getContext(), mTitleStyleRes);
      }
      if (mSubtitleStyleRes != 0) {
        mSubtitleView.setTextAppearance(getContext(), mSubtitleStyleRes);
      }
    }
    mTitleView.setText(mTitle);
    mSubtitleView.setText(mSubtitle);
    int j;
    int k;
    label166:
    TextView localTextView;
    if (!TextUtils.isEmpty(mTitle))
    {
      j = 1;
      if (TextUtils.isEmpty(mSubtitle)) {
        break label232;
      }
      k = 1;
      localTextView = mSubtitleView;
      if (k == 0) {
        break label237;
      }
    }
    label232:
    label237:
    for (int m = 0;; m = i)
    {
      localTextView.setVisibility(m);
      LinearLayout localLinearLayout = mTitleLayout;
      if ((j != 0) || (k != 0)) {
        i = 0;
      }
      localLinearLayout.setVisibility(i);
      if (mTitleLayout.getParent() == null) {
        addView(mTitleLayout);
      }
      return;
      j = 0;
      break;
      k = 0;
      break label166;
    }
  }
  
  public void closeMode()
  {
    if (mClose == null) {
      killMode();
    }
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new ViewGroup.MarginLayoutParams(-1, -2);
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new ViewGroup.MarginLayoutParams(getContext(), paramAttributeSet);
  }
  
  public CharSequence getSubtitle()
  {
    return mSubtitle;
  }
  
  public CharSequence getTitle()
  {
    return mTitle;
  }
  
  public boolean hideOverflowMenu()
  {
    if (mActionMenuPresenter != null) {
      return mActionMenuPresenter.hideOverflowMenu();
    }
    return false;
  }
  
  public void initForMode(final ActionMode paramActionMode)
  {
    if (mClose == null)
    {
      mClose = LayoutInflater.from(getContext()).inflate(mCloseItemLayout, this, false);
      addView(mClose);
    }
    for (;;)
    {
      mClose.findViewById(R.id.action_mode_close_button).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramActionMode.finish();
        }
      });
      MenuBuilder localMenuBuilder = (MenuBuilder)paramActionMode.getMenu();
      if (mActionMenuPresenter != null) {
        mActionMenuPresenter.dismissPopupMenus();
      }
      mActionMenuPresenter = new ActionMenuPresenter(getContext());
      mActionMenuPresenter.setReserveOverflow(true);
      ViewGroup.LayoutParams localLayoutParams = new ViewGroup.LayoutParams(-2, -1);
      localMenuBuilder.addMenuPresenter(mActionMenuPresenter, mPopupContext);
      mMenuView = ((ActionMenuView)mActionMenuPresenter.getMenuView(this));
      mMenuView.setBackgroundDrawable(null);
      addView(mMenuView, localLayoutParams);
      return;
      if (mClose.getParent() == null) {
        addView(mClose);
      }
    }
  }
  
  public boolean isOverflowMenuShowing()
  {
    if (mActionMenuPresenter != null) {
      return mActionMenuPresenter.isOverflowMenuShowing();
    }
    return false;
  }
  
  public boolean isTitleOptional()
  {
    return mTitleOptional;
  }
  
  public void killMode()
  {
    removeAllViews();
    mCustomView = null;
    mMenuView = null;
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (mActionMenuPresenter != null)
    {
      mActionMenuPresenter.hideOverflowMenu();
      mActionMenuPresenter.hideSubMenus();
    }
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      if (paramAccessibilityEvent.getEventType() == 32)
      {
        paramAccessibilityEvent.setSource(this);
        paramAccessibilityEvent.setClassName(getClass().getName());
        paramAccessibilityEvent.setPackageName(getContext().getPackageName());
        paramAccessibilityEvent.setContentDescription(mTitle);
      }
    }
    else {
      return;
    }
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool1 = ViewUtils.isLayoutRtl(this);
    int i;
    int j;
    int k;
    ViewGroup.MarginLayoutParams localMarginLayoutParams;
    int n;
    label87:
    int i1;
    label99:
    int m;
    label223:
    ActionMenuView localActionMenuView;
    if (bool1)
    {
      i = paramInt3 - paramInt1 - getPaddingRight();
      j = getPaddingTop();
      k = paramInt4 - paramInt2 - getPaddingTop() - getPaddingBottom();
      if ((mClose != null) && (mClose.getVisibility() != 8))
      {
        localMarginLayoutParams = (ViewGroup.MarginLayoutParams)mClose.getLayoutParams();
        if (!bool1) {
          break label272;
        }
        n = rightMargin;
        if (!bool1) {
          break label282;
        }
        i1 = leftMargin;
        int i2 = next(i, n, bool1);
        i = next(i2 + positionChild(mClose, i2, j, k, bool1), i1, bool1);
      }
      if ((mTitleLayout != null) && (mCustomView == null) && (mTitleLayout.getVisibility() != 8)) {
        i += positionChild(mTitleLayout, i, j, k, bool1);
      }
      if (mCustomView != null) {
        (i + positionChild(mCustomView, i, j, k, bool1));
      }
      if (!bool1) {
        break label292;
      }
      m = getPaddingLeft();
      if (mMenuView != null)
      {
        localActionMenuView = mMenuView;
        if (bool1) {
          break label306;
        }
      }
    }
    label272:
    label282:
    label292:
    label306:
    for (boolean bool2 = true;; bool2 = false)
    {
      (m + positionChild(localActionMenuView, m, j, k, bool2));
      return;
      i = getPaddingLeft();
      break;
      n = leftMargin;
      break label87;
      i1 = rightMargin;
      break label99;
      m = paramInt3 - paramInt1 - getPaddingRight();
      break label223;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (View.MeasureSpec.getMode(paramInt1) != 1073741824) {
      throw new IllegalStateException(getClass().getSimpleName() + " can only be used " + "with android:layout_width=\"match_parent\" (or fill_parent)");
    }
    if (View.MeasureSpec.getMode(paramInt2) == 0) {
      throw new IllegalStateException(getClass().getSimpleName() + " can only be used " + "with android:layout_height=\"wrap_content\"");
    }
    int i = View.MeasureSpec.getSize(paramInt1);
    int j;
    int k;
    int m;
    int n;
    int i1;
    int i12;
    label297:
    int i13;
    label323:
    label330:
    ViewGroup.LayoutParams localLayoutParams;
    int i6;
    label361:
    int i7;
    label381:
    int i8;
    if (mContentHeight > 0)
    {
      j = mContentHeight;
      k = getPaddingTop() + getPaddingBottom();
      m = i - getPaddingLeft() - getPaddingRight();
      n = j - k;
      i1 = View.MeasureSpec.makeMeasureSpec(n, Integer.MIN_VALUE);
      if (mClose != null)
      {
        int i14 = measureChildView(mClose, m, i1, 0);
        ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)mClose.getLayoutParams();
        m = i14 - (leftMargin + rightMargin);
      }
      if ((mMenuView != null) && (mMenuView.getParent() == this)) {
        m = measureChildView(mMenuView, m, i1, 0);
      }
      if ((mTitleLayout != null) && (mCustomView == null))
      {
        if (!mTitleOptional) {
          break label516;
        }
        int i10 = View.MeasureSpec.makeMeasureSpec(0, 0);
        mTitleLayout.measure(i10, i1);
        int i11 = mTitleLayout.getMeasuredWidth();
        if (i11 > m) {
          break label503;
        }
        i12 = 1;
        if (i12 != 0) {
          m -= i11;
        }
        LinearLayout localLinearLayout = mTitleLayout;
        if (i12 == 0) {
          break label509;
        }
        i13 = 0;
        localLinearLayout.setVisibility(i13);
      }
      if (mCustomView != null)
      {
        localLayoutParams = mCustomView.getLayoutParams();
        if (width == -2) {
          break label534;
        }
        i6 = 1073741824;
        if (width < 0) {
          break label542;
        }
        i7 = Math.min(width, m);
        if (height == -2) {
          break label549;
        }
        i8 = 1073741824;
        label396:
        if (height < 0) {
          break label557;
        }
      }
    }
    int i2;
    label503:
    label509:
    label516:
    label534:
    label542:
    label549:
    label557:
    for (int i9 = Math.min(height, n);; i9 = n)
    {
      mCustomView.measure(View.MeasureSpec.makeMeasureSpec(i7, i6), View.MeasureSpec.makeMeasureSpec(i9, i8));
      if (mContentHeight > 0) {
        break label572;
      }
      i2 = 0;
      int i3 = getChildCount();
      for (int i4 = 0; i4 < i3; i4++)
      {
        int i5 = k + getChildAt(i4).getMeasuredHeight();
        if (i5 > i2) {
          i2 = i5;
        }
      }
      j = View.MeasureSpec.getSize(paramInt2);
      break;
      i12 = 0;
      break label297;
      i13 = 8;
      break label323;
      m = measureChildView(mTitleLayout, m, i1, 0);
      break label330;
      i6 = Integer.MIN_VALUE;
      break label361;
      i7 = m;
      break label381;
      i8 = Integer.MIN_VALUE;
      break label396;
    }
    setMeasuredDimension(i, i2);
    return;
    label572:
    setMeasuredDimension(i, j);
  }
  
  public void setContentHeight(int paramInt)
  {
    mContentHeight = paramInt;
  }
  
  public void setCustomView(View paramView)
  {
    if (mCustomView != null) {
      removeView(mCustomView);
    }
    mCustomView = paramView;
    if ((paramView != null) && (mTitleLayout != null))
    {
      removeView(mTitleLayout);
      mTitleLayout = null;
    }
    if (paramView != null) {
      addView(paramView);
    }
    requestLayout();
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    mSubtitle = paramCharSequence;
    initTitle();
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    mTitle = paramCharSequence;
    initTitle();
  }
  
  public void setTitleOptional(boolean paramBoolean)
  {
    if (paramBoolean != mTitleOptional) {
      requestLayout();
    }
    mTitleOptional = paramBoolean;
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
  
  public boolean showOverflowMenu()
  {
    if (mActionMenuPresenter != null) {
      return mActionMenuPresenter.showOverflowMenu();
    }
    return false;
  }
}
