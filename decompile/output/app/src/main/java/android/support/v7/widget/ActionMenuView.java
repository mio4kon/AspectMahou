package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.view.menu.MenuBuilder.ItemInvoker;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;

public class ActionMenuView
  extends LinearLayoutCompat
  implements MenuBuilder.ItemInvoker, MenuView
{
  static final int GENERATED_ITEM_PADDING = 4;
  static final int MIN_CELL_SIZE = 56;
  private static final String TAG = "ActionMenuView";
  private MenuPresenter.Callback mActionMenuPresenterCallback;
  private boolean mFormatItems;
  private int mFormatItemsWidth;
  private int mGeneratedItemPadding;
  private MenuBuilder mMenu;
  MenuBuilder.Callback mMenuBuilderCallback;
  private int mMinCellSize;
  OnMenuItemClickListener mOnMenuItemClickListener;
  private Context mPopupContext;
  private int mPopupTheme;
  private ActionMenuPresenter mPresenter;
  private boolean mReserveOverflow;
  
  public ActionMenuView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ActionMenuView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setBaselineAligned(false);
    float f = getResourcesgetDisplayMetricsdensity;
    mMinCellSize = ((int)(56.0F * f));
    mGeneratedItemPadding = ((int)(4.0F * f));
    mPopupContext = paramContext;
    mPopupTheme = 0;
  }
  
  static int measureChildForCells(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt3) - paramInt4, View.MeasureSpec.getMode(paramInt3));
    ActionMenuItemView localActionMenuItemView;
    int j;
    label54:
    int k;
    if ((paramView instanceof ActionMenuItemView))
    {
      localActionMenuItemView = (ActionMenuItemView)paramView;
      if ((localActionMenuItemView == null) || (!localActionMenuItemView.hasText())) {
        break label178;
      }
      j = 1;
      k = 0;
      if (paramInt2 > 0) {
        if (j != 0)
        {
          k = 0;
          if (paramInt2 < 2) {}
        }
        else
        {
          paramView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 * paramInt2, Integer.MIN_VALUE), i);
          int m = paramView.getMeasuredWidth();
          k = m / paramInt1;
          if (m % paramInt1 != 0) {
            k++;
          }
          if ((j != 0) && (k < 2)) {
            k = 2;
          }
        }
      }
      if ((isOverflowButton) || (j == 0)) {
        break label184;
      }
    }
    label178:
    label184:
    for (boolean bool = true;; bool = false)
    {
      expandable = bool;
      cellsUsed = k;
      paramView.measure(View.MeasureSpec.makeMeasureSpec(k * paramInt1, 1073741824), i);
      return k;
      localActionMenuItemView = null;
      break;
      j = 0;
      break label54;
    }
  }
  
  private void onMeasureExactFormat(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt2);
    int j = View.MeasureSpec.getSize(paramInt1);
    int k = View.MeasureSpec.getSize(paramInt2);
    int m = getPaddingLeft() + getPaddingRight();
    int n = getPaddingTop() + getPaddingBottom();
    int i1 = getChildMeasureSpec(paramInt2, n, -2);
    int i2 = j - m;
    int i3 = i2 / mMinCellSize;
    int i4 = i2 % mMinCellSize;
    if (i3 == 0)
    {
      setMeasuredDimension(i2, 0);
      return;
    }
    int i5 = mMinCellSize + i4 / i3;
    int i6 = i3;
    int i7 = 0;
    int i8 = 0;
    int i9 = 0;
    int i10 = 0;
    int i11 = 0;
    long l1 = 0L;
    int i12 = getChildCount();
    int i13 = 0;
    while (i13 < i12)
    {
      View localView4 = getChildAt(i13);
      if (localView4.getVisibility() == 8)
      {
        i13++;
      }
      else
      {
        boolean bool1 = localView4 instanceof ActionMenuItemView;
        i10++;
        if (bool1) {
          localView4.setPadding(mGeneratedItemPadding, 0, mGeneratedItemPadding, 0);
        }
        LayoutParams localLayoutParams5 = (LayoutParams)localView4.getLayoutParams();
        expanded = false;
        extraPixels = 0;
        cellsUsed = 0;
        expandable = false;
        leftMargin = 0;
        rightMargin = 0;
        boolean bool2;
        if ((bool1) && (((ActionMenuItemView)localView4).hasText()))
        {
          bool2 = true;
          label256:
          preventEdgeOffset = bool2;
          if (!isOverflowButton) {
            break label368;
          }
        }
        label368:
        for (int i26 = 1;; i26 = i6)
        {
          int i27 = measureChildForCells(localView4, i5, i26, i1, n);
          i8 = Math.max(i8, i27);
          if (expandable) {
            i9++;
          }
          if (isOverflowButton) {
            i11 = 1;
          }
          i6 -= i27;
          int i28 = localView4.getMeasuredHeight();
          i7 = Math.max(i7, i28);
          if (i27 != 1) {
            break;
          }
          l1 |= 1 << i13;
          break;
          bool2 = false;
          break label256;
        }
      }
    }
    int i14;
    int i15;
    int i21;
    long l2;
    int i22;
    int i23;
    label415:
    LayoutParams localLayoutParams4;
    if ((i11 != 0) && (i10 == 2))
    {
      i14 = 1;
      i15 = 0;
      if ((i9 <= 0) || (i6 <= 0)) {
        break label526;
      }
      i21 = Integer.MAX_VALUE;
      l2 = 0L;
      i22 = 0;
      i23 = 0;
      if (i23 >= i12) {
        break label512;
      }
      localLayoutParams4 = (LayoutParams)getChildAt(i23).getLayoutParams();
      if (expandable) {
        break label456;
      }
    }
    for (;;)
    {
      i23++;
      break label415;
      i14 = 0;
      break;
      label456:
      if (cellsUsed < i21)
      {
        i21 = cellsUsed;
        l2 = 1 << i23;
        i22 = 1;
      }
      else if (cellsUsed == i21)
      {
        l2 |= 1 << i23;
        i22++;
      }
    }
    label512:
    l1 |= l2;
    label526:
    int i16;
    label540:
    int i18;
    label679:
    int i19;
    if (i22 > i6)
    {
      if ((i11 != 0) || (i10 != 1)) {
        break label848;
      }
      i16 = 1;
      if ((i6 <= 0) || (l1 == 0L) || ((i6 >= i10 - 1) && (i16 == 0) && (i8 <= 1))) {
        break label1004;
      }
      float f = Long.bitCount(l1);
      if (i16 == 0)
      {
        if (((1L & l1) != 0L) && (!getChildAt0getLayoutParamspreventEdgeOffset)) {
          f -= 0.5F;
        }
        if (((l1 & 1 << i12 - 1) != 0L) && (!getChildAt1getLayoutParamspreventEdgeOffset)) {
          f -= 0.5F;
        }
      }
      if (f <= 0.0F) {
        break label854;
      }
      i18 = (int)(i6 * i5 / f);
      i19 = 0;
      label682:
      if (i19 >= i12) {
        break label1004;
      }
      if ((l1 & 1 << i19) != 0L) {
        break label860;
      }
    }
    for (;;)
    {
      i19++;
      break label682;
      int i24 = i21 + 1;
      int i25 = 0;
      if (i25 < i12)
      {
        View localView3 = getChildAt(i25);
        LayoutParams localLayoutParams3 = (LayoutParams)localView3.getLayoutParams();
        if ((l2 & 1 << i25) == 0L) {
          if (cellsUsed == i24) {
            l1 |= 1 << i25;
          }
        }
        for (;;)
        {
          i25++;
          break;
          if ((i14 != 0) && (preventEdgeOffset) && (i6 == 1)) {
            localView3.setPadding(i5 + mGeneratedItemPadding, 0, mGeneratedItemPadding, 0);
          }
          cellsUsed = (1 + cellsUsed);
          expanded = true;
          i6--;
        }
      }
      i15 = 1;
      break;
      label848:
      i16 = 0;
      break label540;
      label854:
      i18 = 0;
      break label679;
      label860:
      View localView2 = getChildAt(i19);
      LayoutParams localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
      if ((localView2 instanceof ActionMenuItemView))
      {
        extraPixels = i18;
        expanded = true;
        if ((i19 == 0) && (!preventEdgeOffset)) {
          leftMargin = (-i18 / 2);
        }
        i15 = 1;
      }
      else if (isOverflowButton)
      {
        extraPixels = i18;
        expanded = true;
        rightMargin = (-i18 / 2);
        i15 = 1;
      }
      else
      {
        if (i19 != 0) {
          leftMargin = (i18 / 2);
        }
        int i20 = i12 - 1;
        if (i19 != i20) {
          rightMargin = (i18 / 2);
        }
      }
    }
    label1004:
    if (i15 != 0)
    {
      int i17 = 0;
      if (i17 < i12)
      {
        View localView1 = getChildAt(i17);
        LayoutParams localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
        if (!expanded) {}
        for (;;)
        {
          i17++;
          break;
          localView1.measure(View.MeasureSpec.makeMeasureSpec(i5 * cellsUsed + extraPixels, 1073741824), i1);
        }
      }
    }
    if (i != 1073741824) {
      k = i7;
    }
    setMeasuredDimension(i2, k);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return (paramLayoutParams != null) && ((paramLayoutParams instanceof LayoutParams));
  }
  
  public void dismissPopupMenus()
  {
    if (mPresenter != null) {
      mPresenter.dismissPopupMenus();
    }
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    return false;
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    LayoutParams localLayoutParams = new LayoutParams(-2, -2);
    gravity = 16;
    return localLayoutParams;
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (paramLayoutParams != null)
    {
      if ((paramLayoutParams instanceof LayoutParams)) {}
      for (LayoutParams localLayoutParams = new LayoutParams((LayoutParams)paramLayoutParams);; localLayoutParams = new LayoutParams(paramLayoutParams))
      {
        if (gravity <= 0) {
          gravity = 16;
        }
        return localLayoutParams;
      }
    }
    return generateDefaultLayoutParams();
  }
  
  public LayoutParams generateOverflowButtonLayoutParams()
  {
    LayoutParams localLayoutParams = generateDefaultLayoutParams();
    isOverflowButton = true;
    return localLayoutParams;
  }
  
  public Menu getMenu()
  {
    ActionMenuPresenter localActionMenuPresenter;
    if (mMenu == null)
    {
      Context localContext = getContext();
      mMenu = new MenuBuilder(localContext);
      mMenu.setCallback(new MenuBuilderCallback());
      mPresenter = new ActionMenuPresenter(localContext);
      mPresenter.setReserveOverflow(true);
      localActionMenuPresenter = mPresenter;
      if (mActionMenuPresenterCallback == null) {
        break label109;
      }
    }
    label109:
    for (Object localObject = mActionMenuPresenterCallback;; localObject = new ActionMenuPresenterCallback())
    {
      localActionMenuPresenter.setCallback((MenuPresenter.Callback)localObject);
      mMenu.addMenuPresenter(mPresenter, mPopupContext);
      mPresenter.setMenuView(this);
      return mMenu;
    }
  }
  
  @Nullable
  public Drawable getOverflowIcon()
  {
    getMenu();
    return mPresenter.getOverflowIcon();
  }
  
  public int getPopupTheme()
  {
    return mPopupTheme;
  }
  
  public int getWindowAnimations()
  {
    return 0;
  }
  
  protected boolean hasSupportDividerBeforeChildAt(int paramInt)
  {
    boolean bool1;
    if (paramInt == 0) {
      bool1 = false;
    }
    View localView2;
    do
    {
      return bool1;
      View localView1 = getChildAt(paramInt - 1);
      localView2 = getChildAt(paramInt);
      int i = getChildCount();
      bool1 = false;
      if (paramInt < i)
      {
        boolean bool2 = localView1 instanceof ActionMenuChildView;
        bool1 = false;
        if (bool2) {
          bool1 = false | ((ActionMenuChildView)localView1).needsDividerAfter();
        }
      }
    } while ((paramInt <= 0) || (!(localView2 instanceof ActionMenuChildView)));
    return bool1 | ((ActionMenuChildView)localView2).needsDividerBefore();
  }
  
  public boolean hideOverflowMenu()
  {
    return (mPresenter != null) && (mPresenter.hideOverflowMenu());
  }
  
  public void initialize(MenuBuilder paramMenuBuilder)
  {
    mMenu = paramMenuBuilder;
  }
  
  public boolean invokeItem(MenuItemImpl paramMenuItemImpl)
  {
    return mMenu.performItemAction(paramMenuItemImpl, 0);
  }
  
  public boolean isOverflowMenuShowPending()
  {
    return (mPresenter != null) && (mPresenter.isOverflowMenuShowPending());
  }
  
  public boolean isOverflowMenuShowing()
  {
    return (mPresenter != null) && (mPresenter.isOverflowMenuShowing());
  }
  
  public boolean isOverflowReserved()
  {
    return mReserveOverflow;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (mPresenter != null)
    {
      mPresenter.updateMenuView(false);
      if (mPresenter.isOverflowMenuShowing())
      {
        mPresenter.hideOverflowMenu();
        mPresenter.showOverflowMenu();
      }
    }
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    dismissPopupMenus();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!mFormatItems)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    int i = getChildCount();
    int j = (paramInt4 - paramInt2) / 2;
    int k = getDividerWidth();
    int m = 0;
    int n = 0;
    int i1 = paramInt3 - paramInt1 - getPaddingRight() - getPaddingLeft();
    int i2 = 0;
    boolean bool = ViewUtils.isLayoutRtl(this);
    int i3 = 0;
    if (i3 < i)
    {
      View localView4 = getChildAt(i3);
      if (localView4.getVisibility() == 8) {}
      for (;;)
      {
        i3++;
        break;
        LayoutParams localLayoutParams3 = (LayoutParams)localView4.getLayoutParams();
        if (isOverflowButton)
        {
          int i25 = localView4.getMeasuredWidth();
          if (hasSupportDividerBeforeChildAt(i3)) {
            i25 += k;
          }
          int i26 = localView4.getMeasuredHeight();
          int i28;
          int i27;
          if (bool)
          {
            i28 = getPaddingLeft() + leftMargin;
            i27 = i28 + i25;
          }
          for (;;)
          {
            int i29 = j - i26 / 2;
            int i30 = i29 + i26;
            localView4.layout(i28, i29, i27, i30);
            i1 -= i25;
            i2 = 1;
            break;
            i27 = getWidth() - getPaddingRight() - rightMargin;
            i28 = i27 - i25;
          }
        }
        int i24 = localView4.getMeasuredWidth() + leftMargin + rightMargin;
        m += i24;
        i1 -= i24;
        if (hasSupportDividerBeforeChildAt(i3)) {
          m += k;
        }
        n++;
      }
    }
    if ((i == 1) && (i2 == 0))
    {
      View localView3 = getChildAt(0);
      int i20 = localView3.getMeasuredWidth();
      int i21 = localView3.getMeasuredHeight();
      int i22 = (paramInt3 - paramInt1) / 2 - i20 / 2;
      int i23 = j - i21 / 2;
      localView3.layout(i22, i23, i22 + i20, i23 + i21);
      return;
    }
    int i4;
    label382:
    int i6;
    label401:
    int i7;
    int i14;
    int i15;
    label428:
    View localView2;
    LayoutParams localLayoutParams2;
    if (i2 != 0)
    {
      i4 = 0;
      int i5 = n - i4;
      if (i5 <= 0) {
        break label483;
      }
      i6 = i1 / i5;
      i7 = Math.max(0, i6);
      if (!bool) {
        break label560;
      }
      i14 = getWidth() - getPaddingRight();
      i15 = 0;
      if (i15 < i)
      {
        localView2 = getChildAt(i15);
        localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
        if ((localView2.getVisibility() != 8) && (!isOverflowButton)) {
          break label489;
        }
      }
    }
    for (;;)
    {
      i15++;
      break label428;
      break;
      i4 = 1;
      break label382;
      label483:
      i6 = 0;
      break label401;
      label489:
      int i16 = i14 - rightMargin;
      int i17 = localView2.getMeasuredWidth();
      int i18 = localView2.getMeasuredHeight();
      int i19 = j - i18 / 2;
      localView2.layout(i16 - i17, i19, i16, i19 + i18);
      i14 = i16 - (i7 + (i17 + leftMargin));
    }
    label560:
    int i8 = getPaddingLeft();
    int i9 = 0;
    label569:
    View localView1;
    LayoutParams localLayoutParams1;
    if (i9 < i)
    {
      localView1 = getChildAt(i9);
      localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
      if ((localView1.getVisibility() != 8) && (!isOverflowButton)) {
        break label618;
      }
    }
    for (;;)
    {
      i9++;
      break label569;
      break;
      label618:
      int i10 = i8 + leftMargin;
      int i11 = localView1.getMeasuredWidth();
      int i12 = localView1.getMeasuredHeight();
      int i13 = j - i12 / 2;
      localView1.layout(i10, i13, i10 + i11, i13 + i12);
      i8 = i10 + (i7 + (i11 + rightMargin));
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    boolean bool1 = mFormatItems;
    if (View.MeasureSpec.getMode(paramInt1) == 1073741824) {}
    int j;
    for (boolean bool2 = true;; bool2 = false)
    {
      mFormatItems = bool2;
      if (bool1 != mFormatItems) {
        mFormatItemsWidth = 0;
      }
      int i = View.MeasureSpec.getSize(paramInt1);
      if ((mFormatItems) && (mMenu != null) && (i != mFormatItemsWidth))
      {
        mFormatItemsWidth = i;
        mMenu.onItemsChanged(true);
      }
      j = getChildCount();
      if ((!mFormatItems) || (j <= 0)) {
        break;
      }
      onMeasureExactFormat(paramInt1, paramInt2);
      return;
    }
    for (int k = 0; k < j; k++)
    {
      LayoutParams localLayoutParams = (LayoutParams)getChildAt(k).getLayoutParams();
      rightMargin = 0;
      leftMargin = 0;
    }
    super.onMeasure(paramInt1, paramInt2);
  }
  
  public MenuBuilder peekMenu()
  {
    return mMenu;
  }
  
  public void setExpandedActionViewsExclusive(boolean paramBoolean)
  {
    mPresenter.setExpandedActionViewsExclusive(paramBoolean);
  }
  
  public void setMenuCallbacks(MenuPresenter.Callback paramCallback, MenuBuilder.Callback paramCallback1)
  {
    mActionMenuPresenterCallback = paramCallback;
    mMenuBuilderCallback = paramCallback1;
  }
  
  public void setOnMenuItemClickListener(OnMenuItemClickListener paramOnMenuItemClickListener)
  {
    mOnMenuItemClickListener = paramOnMenuItemClickListener;
  }
  
  public void setOverflowIcon(@Nullable Drawable paramDrawable)
  {
    getMenu();
    mPresenter.setOverflowIcon(paramDrawable);
  }
  
  public void setOverflowReserved(boolean paramBoolean)
  {
    mReserveOverflow = paramBoolean;
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
  
  public void setPresenter(ActionMenuPresenter paramActionMenuPresenter)
  {
    mPresenter = paramActionMenuPresenter;
    mPresenter.setMenuView(this);
  }
  
  public boolean showOverflowMenu()
  {
    return (mPresenter != null) && (mPresenter.showOverflowMenu());
  }
  
  public static abstract interface ActionMenuChildView
  {
    public abstract boolean needsDividerAfter();
    
    public abstract boolean needsDividerBefore();
  }
  
  private class ActionMenuPresenterCallback
    implements MenuPresenter.Callback
  {
    ActionMenuPresenterCallback() {}
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {}
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      return false;
    }
  }
  
  public static class LayoutParams
    extends LinearLayoutCompat.LayoutParams
  {
    @ViewDebug.ExportedProperty
    public int cellsUsed;
    @ViewDebug.ExportedProperty
    public boolean expandable;
    boolean expanded;
    @ViewDebug.ExportedProperty
    public int extraPixels;
    @ViewDebug.ExportedProperty
    public boolean isOverflowButton;
    @ViewDebug.ExportedProperty
    public boolean preventEdgeOffset;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      isOverflowButton = false;
    }
    
    LayoutParams(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      super(paramInt2);
      isOverflowButton = paramBoolean;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      isOverflowButton = isOverflowButton;
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
  }
  
  private class MenuBuilderCallback
    implements MenuBuilder.Callback
  {
    MenuBuilderCallback() {}
    
    public boolean onMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem)
    {
      return (mOnMenuItemClickListener != null) && (mOnMenuItemClickListener.onMenuItemClick(paramMenuItem));
    }
    
    public void onMenuModeChange(MenuBuilder paramMenuBuilder)
    {
      if (mMenuBuilderCallback != null) {
        mMenuBuilderCallback.onMenuModeChange(paramMenuBuilder);
      }
    }
  }
  
  public static abstract interface OnMenuItemClickListener
  {
    public abstract boolean onMenuItemClick(MenuItem paramMenuItem);
  }
}
