package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.appcompat.R.attr;
import android.support.v7.view.ActionBarPolicy;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ScrollingTabContainerView
  extends HorizontalScrollView
  implements AdapterView.OnItemSelectedListener
{
  private static final int FADE_DURATION = 200;
  private static final String TAG = "ScrollingTabContainerView";
  private static final Interpolator sAlphaInterpolator = new DecelerateInterpolator();
  private boolean mAllowCollapse;
  private int mContentHeight;
  int mMaxTabWidth;
  private int mSelectedTabIndex;
  int mStackedTabMaxWidth;
  private TabClickListener mTabClickListener;
  LinearLayoutCompat mTabLayout;
  Runnable mTabSelector;
  private Spinner mTabSpinner;
  protected final VisibilityAnimListener mVisAnimListener = new VisibilityAnimListener();
  protected ViewPropertyAnimatorCompat mVisibilityAnim;
  
  public ScrollingTabContainerView(Context paramContext)
  {
    super(paramContext);
    setHorizontalScrollBarEnabled(false);
    ActionBarPolicy localActionBarPolicy = ActionBarPolicy.get(paramContext);
    setContentHeight(localActionBarPolicy.getTabContainerHeight());
    mStackedTabMaxWidth = localActionBarPolicy.getStackedTabMaxWidth();
    mTabLayout = createTabLayout();
    addView(mTabLayout, new ViewGroup.LayoutParams(-2, -1));
  }
  
  private Spinner createSpinner()
  {
    AppCompatSpinner localAppCompatSpinner = new AppCompatSpinner(getContext(), null, R.attr.actionDropDownStyle);
    localAppCompatSpinner.setLayoutParams(new LinearLayoutCompat.LayoutParams(-2, -1));
    localAppCompatSpinner.setOnItemSelectedListener(this);
    return localAppCompatSpinner;
  }
  
  private LinearLayoutCompat createTabLayout()
  {
    LinearLayoutCompat localLinearLayoutCompat = new LinearLayoutCompat(getContext(), null, R.attr.actionBarTabBarStyle);
    localLinearLayoutCompat.setMeasureWithLargestChildEnabled(true);
    localLinearLayoutCompat.setGravity(17);
    localLinearLayoutCompat.setLayoutParams(new LinearLayoutCompat.LayoutParams(-2, -1));
    return localLinearLayoutCompat;
  }
  
  private boolean isCollapsed()
  {
    return (mTabSpinner != null) && (mTabSpinner.getParent() == this);
  }
  
  private void performCollapse()
  {
    if (isCollapsed()) {
      return;
    }
    if (mTabSpinner == null) {
      mTabSpinner = createSpinner();
    }
    removeView(mTabLayout);
    addView(mTabSpinner, new ViewGroup.LayoutParams(-2, -1));
    if (mTabSpinner.getAdapter() == null) {
      mTabSpinner.setAdapter(new TabAdapter());
    }
    if (mTabSelector != null)
    {
      removeCallbacks(mTabSelector);
      mTabSelector = null;
    }
    mTabSpinner.setSelection(mSelectedTabIndex);
  }
  
  private boolean performExpand()
  {
    if (!isCollapsed()) {
      return false;
    }
    removeView(mTabSpinner);
    addView(mTabLayout, new ViewGroup.LayoutParams(-2, -1));
    setTabSelected(mTabSpinner.getSelectedItemPosition());
    return false;
  }
  
  public void addTab(ActionBar.Tab paramTab, int paramInt, boolean paramBoolean)
  {
    TabView localTabView = createTabView(paramTab, false);
    mTabLayout.addView(localTabView, paramInt, new LinearLayoutCompat.LayoutParams(0, -1, 1.0F));
    if (mTabSpinner != null) {
      ((TabAdapter)mTabSpinner.getAdapter()).notifyDataSetChanged();
    }
    if (paramBoolean) {
      localTabView.setSelected(true);
    }
    if (mAllowCollapse) {
      requestLayout();
    }
  }
  
  public void addTab(ActionBar.Tab paramTab, boolean paramBoolean)
  {
    TabView localTabView = createTabView(paramTab, false);
    mTabLayout.addView(localTabView, new LinearLayoutCompat.LayoutParams(0, -1, 1.0F));
    if (mTabSpinner != null) {
      ((TabAdapter)mTabSpinner.getAdapter()).notifyDataSetChanged();
    }
    if (paramBoolean) {
      localTabView.setSelected(true);
    }
    if (mAllowCollapse) {
      requestLayout();
    }
  }
  
  public void animateToTab(int paramInt)
  {
    final View localView = mTabLayout.getChildAt(paramInt);
    if (mTabSelector != null) {
      removeCallbacks(mTabSelector);
    }
    mTabSelector = new Runnable()
    {
      public void run()
      {
        int i = localView.getLeft() - (getWidth() - localView.getWidth()) / 2;
        smoothScrollTo(i, 0);
        mTabSelector = null;
      }
    };
    post(mTabSelector);
  }
  
  public void animateToVisibility(int paramInt)
  {
    if (mVisibilityAnim != null) {
      mVisibilityAnim.cancel();
    }
    if (paramInt == 0)
    {
      if (getVisibility() != 0) {
        ViewCompat.setAlpha(this, 0.0F);
      }
      ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat2 = ViewCompat.animate(this).alpha(1.0F);
      localViewPropertyAnimatorCompat2.setDuration(200L);
      localViewPropertyAnimatorCompat2.setInterpolator(sAlphaInterpolator);
      localViewPropertyAnimatorCompat2.setListener(mVisAnimListener.withFinalVisibility(localViewPropertyAnimatorCompat2, paramInt));
      localViewPropertyAnimatorCompat2.start();
      return;
    }
    ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat1 = ViewCompat.animate(this).alpha(0.0F);
    localViewPropertyAnimatorCompat1.setDuration(200L);
    localViewPropertyAnimatorCompat1.setInterpolator(sAlphaInterpolator);
    localViewPropertyAnimatorCompat1.setListener(mVisAnimListener.withFinalVisibility(localViewPropertyAnimatorCompat1, paramInt));
    localViewPropertyAnimatorCompat1.start();
  }
  
  TabView createTabView(ActionBar.Tab paramTab, boolean paramBoolean)
  {
    TabView localTabView = new TabView(getContext(), paramTab, paramBoolean);
    if (paramBoolean)
    {
      localTabView.setBackgroundDrawable(null);
      localTabView.setLayoutParams(new AbsListView.LayoutParams(-1, mContentHeight));
      return localTabView;
    }
    localTabView.setFocusable(true);
    if (mTabClickListener == null) {
      mTabClickListener = new TabClickListener();
    }
    localTabView.setOnClickListener(mTabClickListener);
    return localTabView;
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (mTabSelector != null) {
      post(mTabSelector);
    }
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    ActionBarPolicy localActionBarPolicy = ActionBarPolicy.get(getContext());
    setContentHeight(localActionBarPolicy.getTabContainerHeight());
    mStackedTabMaxWidth = localActionBarPolicy.getStackedTabMaxWidth();
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (mTabSelector != null) {
      removeCallbacks(mTabSelector);
    }
  }
  
  public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    ((TabView)paramView).getTab().select();
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt1);
    boolean bool;
    label70:
    label85:
    int k;
    int m;
    if (i == 1073741824)
    {
      bool = true;
      setFillViewport(bool);
      int j = mTabLayout.getChildCount();
      if ((j <= 1) || ((i != 1073741824) && (i != Integer.MIN_VALUE))) {
        break label204;
      }
      if (j <= 2) {
        break label191;
      }
      mMaxTabWidth = ((int)(0.4F * View.MeasureSpec.getSize(paramInt1)));
      mMaxTabWidth = Math.min(mMaxTabWidth, mStackedTabMaxWidth);
      k = View.MeasureSpec.makeMeasureSpec(mContentHeight, 1073741824);
      if ((bool) || (!mAllowCollapse)) {
        break label212;
      }
      m = 1;
      label112:
      if (m == 0) {
        break label226;
      }
      mTabLayout.measure(0, k);
      if (mTabLayout.getMeasuredWidth() <= View.MeasureSpec.getSize(paramInt1)) {
        break label218;
      }
      performCollapse();
    }
    for (;;)
    {
      int n = getMeasuredWidth();
      super.onMeasure(paramInt1, k);
      int i1 = getMeasuredWidth();
      if ((bool) && (n != i1)) {
        setTabSelected(mSelectedTabIndex);
      }
      return;
      bool = false;
      break;
      label191:
      mMaxTabWidth = (View.MeasureSpec.getSize(paramInt1) / 2);
      break label70;
      label204:
      mMaxTabWidth = -1;
      break label85;
      label212:
      m = 0;
      break label112;
      label218:
      performExpand();
      continue;
      label226:
      performExpand();
    }
  }
  
  public void onNothingSelected(AdapterView<?> paramAdapterView) {}
  
  public void removeAllTabs()
  {
    mTabLayout.removeAllViews();
    if (mTabSpinner != null) {
      ((TabAdapter)mTabSpinner.getAdapter()).notifyDataSetChanged();
    }
    if (mAllowCollapse) {
      requestLayout();
    }
  }
  
  public void removeTabAt(int paramInt)
  {
    mTabLayout.removeViewAt(paramInt);
    if (mTabSpinner != null) {
      ((TabAdapter)mTabSpinner.getAdapter()).notifyDataSetChanged();
    }
    if (mAllowCollapse) {
      requestLayout();
    }
  }
  
  public void setAllowCollapse(boolean paramBoolean)
  {
    mAllowCollapse = paramBoolean;
  }
  
  public void setContentHeight(int paramInt)
  {
    mContentHeight = paramInt;
    requestLayout();
  }
  
  public void setTabSelected(int paramInt)
  {
    mSelectedTabIndex = paramInt;
    int i = mTabLayout.getChildCount();
    int j = 0;
    if (j < i)
    {
      View localView = mTabLayout.getChildAt(j);
      if (j == paramInt) {}
      for (boolean bool = true;; bool = false)
      {
        localView.setSelected(bool);
        if (bool) {
          animateToTab(paramInt);
        }
        j++;
        break;
      }
    }
    if ((mTabSpinner != null) && (paramInt >= 0)) {
      mTabSpinner.setSelection(paramInt);
    }
  }
  
  public void updateTab(int paramInt)
  {
    ((TabView)mTabLayout.getChildAt(paramInt)).update();
    if (mTabSpinner != null) {
      ((TabAdapter)mTabSpinner.getAdapter()).notifyDataSetChanged();
    }
    if (mAllowCollapse) {
      requestLayout();
    }
  }
  
  private class TabAdapter
    extends BaseAdapter
  {
    TabAdapter() {}
    
    public int getCount()
    {
      return mTabLayout.getChildCount();
    }
    
    public Object getItem(int paramInt)
    {
      return ((ScrollingTabContainerView.TabView)mTabLayout.getChildAt(paramInt)).getTab();
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null) {
        return createTabView((ActionBar.Tab)getItem(paramInt), true);
      }
      ((ScrollingTabContainerView.TabView)paramView).bindTab((ActionBar.Tab)getItem(paramInt));
      return paramView;
    }
  }
  
  private class TabClickListener
    implements View.OnClickListener
  {
    TabClickListener() {}
    
    public void onClick(View paramView)
    {
      ((ScrollingTabContainerView.TabView)paramView).getTab().select();
      int i = mTabLayout.getChildCount();
      int j = 0;
      if (j < i)
      {
        View localView = mTabLayout.getChildAt(j);
        if (localView == paramView) {}
        for (boolean bool = true;; bool = false)
        {
          localView.setSelected(bool);
          j++;
          break;
        }
      }
    }
  }
  
  private class TabView
    extends LinearLayoutCompat
    implements View.OnLongClickListener
  {
    private final int[] BG_ATTRS = { 16842964 };
    private View mCustomView;
    private ImageView mIconView;
    private ActionBar.Tab mTab;
    private TextView mTextView;
    
    public TabView(Context paramContext, ActionBar.Tab paramTab, boolean paramBoolean)
    {
      super(null, R.attr.actionBarTabStyle);
      mTab = paramTab;
      TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, null, BG_ATTRS, R.attr.actionBarTabStyle, 0);
      if (localTintTypedArray.hasValue(0)) {
        setBackgroundDrawable(localTintTypedArray.getDrawable(0));
      }
      localTintTypedArray.recycle();
      if (paramBoolean) {
        setGravity(8388627);
      }
      update();
    }
    
    public void bindTab(ActionBar.Tab paramTab)
    {
      mTab = paramTab;
      update();
    }
    
    public ActionBar.Tab getTab()
    {
      return mTab;
    }
    
    public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(ActionBar.Tab.class.getName());
    }
    
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
      if (Build.VERSION.SDK_INT >= 14) {
        paramAccessibilityNodeInfo.setClassName(ActionBar.Tab.class.getName());
      }
    }
    
    public boolean onLongClick(View paramView)
    {
      int[] arrayOfInt = new int[2];
      getLocationOnScreen(arrayOfInt);
      Context localContext = getContext();
      int i = getWidth();
      int j = getHeight();
      int k = getResourcesgetDisplayMetricswidthPixels;
      Toast localToast = Toast.makeText(localContext, mTab.getContentDescription(), 0);
      localToast.setGravity(49, arrayOfInt[0] + i / 2 - k / 2, j);
      localToast.show();
      return true;
    }
    
    public void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, paramInt2);
      if ((mMaxTabWidth > 0) && (getMeasuredWidth() > mMaxTabWidth)) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(mMaxTabWidth, 1073741824), paramInt2);
      }
    }
    
    public void setSelected(boolean paramBoolean)
    {
      if (isSelected() != paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        super.setSelected(paramBoolean);
        if ((i != 0) && (paramBoolean)) {
          sendAccessibilityEvent(4);
        }
        return;
      }
    }
    
    public void update()
    {
      ActionBar.Tab localTab = mTab;
      View localView = localTab.getCustomView();
      if (localView != null)
      {
        ViewParent localViewParent = localView.getParent();
        if (localViewParent != this)
        {
          if (localViewParent != null) {
            ((ViewGroup)localViewParent).removeView(localView);
          }
          addView(localView);
        }
        mCustomView = localView;
        if (mTextView != null) {
          mTextView.setVisibility(8);
        }
        if (mIconView != null)
        {
          mIconView.setVisibility(8);
          mIconView.setImageDrawable(null);
        }
        return;
      }
      if (mCustomView != null)
      {
        removeView(mCustomView);
        mCustomView = null;
      }
      Drawable localDrawable = localTab.getIcon();
      CharSequence localCharSequence = localTab.getText();
      int i;
      if (localDrawable != null)
      {
        if (mIconView == null)
        {
          AppCompatImageView localAppCompatImageView = new AppCompatImageView(getContext());
          LinearLayoutCompat.LayoutParams localLayoutParams2 = new LinearLayoutCompat.LayoutParams(-2, -2);
          gravity = 16;
          localAppCompatImageView.setLayoutParams(localLayoutParams2);
          addView(localAppCompatImageView, 0);
          mIconView = localAppCompatImageView;
        }
        mIconView.setImageDrawable(localDrawable);
        mIconView.setVisibility(0);
        if (TextUtils.isEmpty(localCharSequence)) {
          break label372;
        }
        i = 1;
        label213:
        if (i == 0) {
          break label378;
        }
        if (mTextView == null)
        {
          AppCompatTextView localAppCompatTextView = new AppCompatTextView(getContext(), null, R.attr.actionBarTabTextStyle);
          localAppCompatTextView.setEllipsize(TextUtils.TruncateAt.END);
          LinearLayoutCompat.LayoutParams localLayoutParams1 = new LinearLayoutCompat.LayoutParams(-2, -2);
          gravity = 16;
          localAppCompatTextView.setLayoutParams(localLayoutParams1);
          addView(localAppCompatTextView);
          mTextView = localAppCompatTextView;
        }
        mTextView.setText(localCharSequence);
        mTextView.setVisibility(0);
      }
      for (;;)
      {
        if (mIconView != null) {
          mIconView.setContentDescription(localTab.getContentDescription());
        }
        if ((i != 0) || (TextUtils.isEmpty(localTab.getContentDescription()))) {
          break label405;
        }
        setOnLongClickListener(this);
        return;
        if (mIconView == null) {
          break;
        }
        mIconView.setVisibility(8);
        mIconView.setImageDrawable(null);
        break;
        label372:
        i = 0;
        break label213;
        label378:
        if (mTextView != null)
        {
          mTextView.setVisibility(8);
          mTextView.setText(null);
        }
      }
      label405:
      setOnLongClickListener(null);
      setLongClickable(false);
    }
  }
  
  protected class VisibilityAnimListener
    implements ViewPropertyAnimatorListener
  {
    private boolean mCanceled = false;
    private int mFinalVisibility;
    
    protected VisibilityAnimListener() {}
    
    public void onAnimationCancel(View paramView)
    {
      mCanceled = true;
    }
    
    public void onAnimationEnd(View paramView)
    {
      if (mCanceled) {
        return;
      }
      mVisibilityAnim = null;
      setVisibility(mFinalVisibility);
    }
    
    public void onAnimationStart(View paramView)
    {
      setVisibility(0);
      mCanceled = false;
    }
    
    public VisibilityAnimListener withFinalVisibility(ViewPropertyAnimatorCompat paramViewPropertyAnimatorCompat, int paramInt)
    {
      mFinalVisibility = paramInt;
      mVisibilityAnim = paramViewPropertyAnimatorCompat;
      return this;
    }
  }
}
