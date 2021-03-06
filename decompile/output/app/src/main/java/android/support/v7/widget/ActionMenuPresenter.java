package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.ActionProvider.SubUiVisibilityListener;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.layout;
import android.support.v7.transition.ActionBarTransition;
import android.support.v7.view.ActionBarPolicy;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.ActionMenuItemView.PopupCallback;
import android.support.v7.view.menu.BaseMenuPresenter;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.MenuView.ItemView;
import android.support.v7.view.menu.ShowableListMenu;
import android.support.v7.view.menu.SubMenuBuilder;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import java.util.ArrayList;

class ActionMenuPresenter
  extends BaseMenuPresenter
  implements ActionProvider.SubUiVisibilityListener
{
  private static final String TAG = "ActionMenuPresenter";
  private final SparseBooleanArray mActionButtonGroups = new SparseBooleanArray();
  ActionButtonSubmenu mActionButtonPopup;
  private int mActionItemWidthLimit;
  private boolean mExpandedActionViewsExclusive;
  private int mMaxItems;
  private boolean mMaxItemsSet;
  private int mMinCellSize;
  int mOpenSubMenuId;
  OverflowMenuButton mOverflowButton;
  OverflowPopup mOverflowPopup;
  private Drawable mPendingOverflowIcon;
  private boolean mPendingOverflowIconSet;
  private ActionMenuPopupCallback mPopupCallback;
  final PopupPresenterCallback mPopupPresenterCallback = new PopupPresenterCallback();
  OpenOverflowRunnable mPostedOpenRunnable;
  private boolean mReserveOverflow;
  private boolean mReserveOverflowSet;
  private View mScrapActionButtonView;
  private boolean mStrictWidthLimit;
  private int mWidthLimit;
  private boolean mWidthLimitSet;
  
  public ActionMenuPresenter(Context paramContext)
  {
    super(paramContext, R.layout.abc_action_menu_layout, R.layout.abc_action_menu_item_layout);
  }
  
  private View findViewForItem(MenuItem paramMenuItem)
  {
    ViewGroup localViewGroup = (ViewGroup)mMenuView;
    View localView;
    if (localViewGroup == null)
    {
      localView = null;
      return localView;
    }
    int i = localViewGroup.getChildCount();
    for (int j = 0;; j++)
    {
      if (j >= i) {
        break label68;
      }
      localView = localViewGroup.getChildAt(j);
      if (((localView instanceof MenuView.ItemView)) && (((MenuView.ItemView)localView).getItemData() == paramMenuItem)) {
        break;
      }
    }
    label68:
    return null;
  }
  
  public void bindItemView(MenuItemImpl paramMenuItemImpl, MenuView.ItemView paramItemView)
  {
    paramItemView.initialize(paramMenuItemImpl, 0);
    ActionMenuView localActionMenuView = (ActionMenuView)mMenuView;
    ActionMenuItemView localActionMenuItemView = (ActionMenuItemView)paramItemView;
    localActionMenuItemView.setItemInvoker(localActionMenuView);
    if (mPopupCallback == null) {
      mPopupCallback = new ActionMenuPopupCallback();
    }
    localActionMenuItemView.setPopupCallback(mPopupCallback);
  }
  
  public boolean dismissPopupMenus()
  {
    return hideOverflowMenu() | hideSubMenus();
  }
  
  public boolean filterLeftoverView(ViewGroup paramViewGroup, int paramInt)
  {
    if (paramViewGroup.getChildAt(paramInt) == mOverflowButton) {
      return false;
    }
    return super.filterLeftoverView(paramViewGroup, paramInt);
  }
  
  public boolean flagActionItems()
  {
    ArrayList localArrayList;
    int i;
    int j;
    int k;
    int m;
    ViewGroup localViewGroup;
    int n;
    int i1;
    int i2;
    int i3;
    int i4;
    label62:
    MenuItemImpl localMenuItemImpl3;
    if (mMenu != null)
    {
      localArrayList = mMenu.getVisibleItems();
      i = localArrayList.size();
      j = mMaxItems;
      k = mActionItemWidthLimit;
      m = View.MeasureSpec.makeMeasureSpec(0, 0);
      localViewGroup = (ViewGroup)mMenuView;
      n = 0;
      i1 = 0;
      i2 = 0;
      i3 = 0;
      i4 = 0;
      if (i4 >= i) {
        break label140;
      }
      localMenuItemImpl3 = (MenuItemImpl)localArrayList.get(i4);
      if (!localMenuItemImpl3.requiresActionButton()) {
        break label120;
      }
      n++;
    }
    for (;;)
    {
      if ((mExpandedActionViewsExclusive) && (localMenuItemImpl3.isActionViewExpanded())) {
        j = 0;
      }
      i4++;
      break label62;
      i = 0;
      localArrayList = null;
      break;
      label120:
      if (localMenuItemImpl3.requestsActionButton()) {
        i1++;
      } else {
        i3 = 1;
      }
    }
    label140:
    if ((mReserveOverflow) && ((i3 != 0) || (n + i1 > j))) {
      j--;
    }
    int i5 = j - n;
    SparseBooleanArray localSparseBooleanArray = mActionButtonGroups;
    localSparseBooleanArray.clear();
    boolean bool1 = mStrictWidthLimit;
    int i6 = 0;
    int i7 = 0;
    if (bool1)
    {
      i7 = k / mMinCellSize;
      int i15 = k % mMinCellSize;
      i6 = mMinCellSize + i15 / i7;
    }
    int i8 = 0;
    if (i8 < i)
    {
      MenuItemImpl localMenuItemImpl1 = (MenuItemImpl)localArrayList.get(i8);
      View localView2;
      if (localMenuItemImpl1.requiresActionButton())
      {
        localView2 = getItemView(localMenuItemImpl1, mScrapActionButtonView, localViewGroup);
        if (mScrapActionButtonView == null) {
          mScrapActionButtonView = localView2;
        }
        if (mStrictWidthLimit)
        {
          i7 -= ActionMenuView.measureChildForCells(localView2, i6, i7, m, 0);
          label307:
          int i13 = localView2.getMeasuredWidth();
          k -= i13;
          if (i2 == 0) {
            i2 = i13;
          }
          int i14 = localMenuItemImpl1.getGroupId();
          if (i14 != 0) {
            localSparseBooleanArray.put(i14, true);
          }
          localMenuItemImpl1.setIsActionButton(true);
        }
      }
      for (;;)
      {
        i8++;
        break;
        localView2.measure(m, m);
        break label307;
        if (localMenuItemImpl1.requestsActionButton())
        {
          int i9 = localMenuItemImpl1.getGroupId();
          boolean bool2 = localSparseBooleanArray.get(i9);
          boolean bool3;
          label428:
          View localView1;
          label496:
          boolean bool5;
          if (((i5 > 0) || (bool2)) && (k > 0) && ((!mStrictWidthLimit) || (i7 > 0)))
          {
            bool3 = true;
            if (bool3)
            {
              localView1 = getItemView(localMenuItemImpl1, mScrapActionButtonView, localViewGroup);
              if (mScrapActionButtonView == null) {
                mScrapActionButtonView = localView1;
              }
              if (!mStrictWidthLimit) {
                break label583;
              }
              int i12 = ActionMenuView.measureChildForCells(localView1, i6, i7, m, 0);
              i7 -= i12;
              if (i12 == 0) {
                bool3 = false;
              }
              int i11 = localView1.getMeasuredWidth();
              k -= i11;
              if (i2 == 0) {
                i2 = i11;
              }
              if (!mStrictWidthLimit) {
                break label601;
              }
              if (k < 0) {
                break label595;
              }
              bool5 = true;
              label534:
              bool3 &= bool5;
            }
            if ((!bool3) || (i9 == 0)) {
              break label628;
            }
            localSparseBooleanArray.put(i9, true);
          }
          for (;;)
          {
            if (bool3) {
              i5--;
            }
            localMenuItemImpl1.setIsActionButton(bool3);
            break;
            bool3 = false;
            break label428;
            label583:
            localView1.measure(m, m);
            break label496;
            label595:
            bool5 = false;
            break label534;
            label601:
            if (k + i2 > 0) {}
            for (boolean bool4 = true;; bool4 = false)
            {
              bool3 &= bool4;
              break;
            }
            label628:
            if (bool2)
            {
              localSparseBooleanArray.put(i9, false);
              for (int i10 = 0; i10 < i8; i10++)
              {
                MenuItemImpl localMenuItemImpl2 = (MenuItemImpl)localArrayList.get(i10);
                if (localMenuItemImpl2.getGroupId() == i9)
                {
                  if (localMenuItemImpl2.isActionButton()) {
                    i5++;
                  }
                  localMenuItemImpl2.setIsActionButton(false);
                }
              }
            }
          }
        }
        localMenuItemImpl1.setIsActionButton(false);
      }
    }
    return true;
  }
  
  public View getItemView(MenuItemImpl paramMenuItemImpl, View paramView, ViewGroup paramViewGroup)
  {
    View localView = paramMenuItemImpl.getActionView();
    if ((localView == null) || (paramMenuItemImpl.hasCollapsibleActionView())) {
      localView = super.getItemView(paramMenuItemImpl, paramView, paramViewGroup);
    }
    if (paramMenuItemImpl.isActionViewExpanded()) {}
    for (int i = 8;; i = 0)
    {
      localView.setVisibility(i);
      ActionMenuView localActionMenuView = (ActionMenuView)paramViewGroup;
      ViewGroup.LayoutParams localLayoutParams = localView.getLayoutParams();
      if (!localActionMenuView.checkLayoutParams(localLayoutParams)) {
        localView.setLayoutParams(localActionMenuView.generateLayoutParams(localLayoutParams));
      }
      return localView;
    }
  }
  
  public MenuView getMenuView(ViewGroup paramViewGroup)
  {
    MenuView localMenuView1 = mMenuView;
    MenuView localMenuView2 = super.getMenuView(paramViewGroup);
    if (localMenuView1 != localMenuView2) {
      ((ActionMenuView)localMenuView2).setPresenter(this);
    }
    return localMenuView2;
  }
  
  public Drawable getOverflowIcon()
  {
    if (mOverflowButton != null) {
      return mOverflowButton.getDrawable();
    }
    if (mPendingOverflowIconSet) {
      return mPendingOverflowIcon;
    }
    return null;
  }
  
  public boolean hideOverflowMenu()
  {
    if ((mPostedOpenRunnable != null) && (mMenuView != null))
    {
      ((View)mMenuView).removeCallbacks(mPostedOpenRunnable);
      mPostedOpenRunnable = null;
      return true;
    }
    OverflowPopup localOverflowPopup = mOverflowPopup;
    if (localOverflowPopup != null)
    {
      localOverflowPopup.dismiss();
      return true;
    }
    return false;
  }
  
  public boolean hideSubMenus()
  {
    if (mActionButtonPopup != null)
    {
      mActionButtonPopup.dismiss();
      return true;
    }
    return false;
  }
  
  public void initForMenu(@NonNull Context paramContext, @Nullable MenuBuilder paramMenuBuilder)
  {
    super.initForMenu(paramContext, paramMenuBuilder);
    Resources localResources = paramContext.getResources();
    ActionBarPolicy localActionBarPolicy = ActionBarPolicy.get(paramContext);
    if (!mReserveOverflowSet) {
      mReserveOverflow = localActionBarPolicy.showsOverflowMenuButton();
    }
    if (!mWidthLimitSet) {
      mWidthLimit = localActionBarPolicy.getEmbeddedMenuWidthLimit();
    }
    if (!mMaxItemsSet) {
      mMaxItems = localActionBarPolicy.getMaxActionButtons();
    }
    int i = mWidthLimit;
    if (mReserveOverflow)
    {
      if (mOverflowButton == null)
      {
        mOverflowButton = new OverflowMenuButton(mSystemContext);
        if (mPendingOverflowIconSet)
        {
          mOverflowButton.setImageDrawable(mPendingOverflowIcon);
          mPendingOverflowIcon = null;
          mPendingOverflowIconSet = false;
        }
        int j = View.MeasureSpec.makeMeasureSpec(0, 0);
        mOverflowButton.measure(j, j);
      }
      i -= mOverflowButton.getMeasuredWidth();
    }
    for (;;)
    {
      mActionItemWidthLimit = i;
      mMinCellSize = ((int)(56.0F * getDisplayMetricsdensity));
      mScrapActionButtonView = null;
      return;
      mOverflowButton = null;
    }
  }
  
  public boolean isOverflowMenuShowPending()
  {
    return (mPostedOpenRunnable != null) || (isOverflowMenuShowing());
  }
  
  public boolean isOverflowMenuShowing()
  {
    return (mOverflowPopup != null) && (mOverflowPopup.isShowing());
  }
  
  public boolean isOverflowReserved()
  {
    return mReserveOverflow;
  }
  
  public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
  {
    dismissPopupMenus();
    super.onCloseMenu(paramMenuBuilder, paramBoolean);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (!mMaxItemsSet) {
      mMaxItems = ActionBarPolicy.get(mContext).getMaxActionButtons();
    }
    if (mMenu != null) {
      mMenu.onItemsChanged(true);
    }
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState)) {}
    MenuItem localMenuItem;
    do
    {
      SavedState localSavedState;
      do
      {
        return;
        localSavedState = (SavedState)paramParcelable;
      } while (openSubMenuId <= 0);
      localMenuItem = mMenu.findItem(openSubMenuId);
    } while (localMenuItem == null);
    onSubMenuSelected((SubMenuBuilder)localMenuItem.getSubMenu());
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState();
    openSubMenuId = mOpenSubMenuId;
    return localSavedState;
  }
  
  public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder)
  {
    if (!paramSubMenuBuilder.hasVisibleItems()) {}
    View localView;
    do
    {
      return false;
      for (SubMenuBuilder localSubMenuBuilder = paramSubMenuBuilder; localSubMenuBuilder.getParentMenu() != mMenu; localSubMenuBuilder = (SubMenuBuilder)localSubMenuBuilder.getParentMenu()) {}
      localView = findViewForItem(localSubMenuBuilder.getItem());
    } while (localView == null);
    mOpenSubMenuId = paramSubMenuBuilder.getItem().getItemId();
    int i = paramSubMenuBuilder.size();
    for (int j = 0;; j++)
    {
      boolean bool = false;
      if (j < i)
      {
        MenuItem localMenuItem = paramSubMenuBuilder.getItem(j);
        if ((localMenuItem.isVisible()) && (localMenuItem.getIcon() != null)) {
          bool = true;
        }
      }
      else
      {
        mActionButtonPopup = new ActionButtonSubmenu(mContext, paramSubMenuBuilder, localView);
        mActionButtonPopup.setForceShowIcon(bool);
        mActionButtonPopup.show();
        super.onSubMenuSelected(paramSubMenuBuilder);
        return true;
      }
    }
  }
  
  public void onSubUiVisibilityChanged(boolean paramBoolean)
  {
    if (paramBoolean) {
      super.onSubMenuSelected(null);
    }
    while (mMenu == null) {
      return;
    }
    mMenu.close(false);
  }
  
  public void setExpandedActionViewsExclusive(boolean paramBoolean)
  {
    mExpandedActionViewsExclusive = paramBoolean;
  }
  
  public void setItemLimit(int paramInt)
  {
    mMaxItems = paramInt;
    mMaxItemsSet = true;
  }
  
  public void setMenuView(ActionMenuView paramActionMenuView)
  {
    mMenuView = paramActionMenuView;
    paramActionMenuView.initialize(mMenu);
  }
  
  public void setOverflowIcon(Drawable paramDrawable)
  {
    if (mOverflowButton != null)
    {
      mOverflowButton.setImageDrawable(paramDrawable);
      return;
    }
    mPendingOverflowIconSet = true;
    mPendingOverflowIcon = paramDrawable;
  }
  
  public void setReserveOverflow(boolean paramBoolean)
  {
    mReserveOverflow = paramBoolean;
    mReserveOverflowSet = true;
  }
  
  public void setWidthLimit(int paramInt, boolean paramBoolean)
  {
    mWidthLimit = paramInt;
    mStrictWidthLimit = paramBoolean;
    mWidthLimitSet = true;
  }
  
  public boolean shouldIncludeItem(int paramInt, MenuItemImpl paramMenuItemImpl)
  {
    return paramMenuItemImpl.isActionButton();
  }
  
  public boolean showOverflowMenu()
  {
    if ((mReserveOverflow) && (!isOverflowMenuShowing()) && (mMenu != null) && (mMenuView != null) && (mPostedOpenRunnable == null) && (!mMenu.getNonActionItems().isEmpty()))
    {
      mPostedOpenRunnable = new OpenOverflowRunnable(new OverflowPopup(mContext, mMenu, mOverflowButton, true));
      ((View)mMenuView).post(mPostedOpenRunnable);
      super.onSubMenuSelected(null);
      return true;
    }
    return false;
  }
  
  public void updateMenuView(boolean paramBoolean)
  {
    ViewGroup localViewGroup1 = (ViewGroup)((View)mMenuView).getParent();
    if (localViewGroup1 != null) {
      ActionBarTransition.beginDelayedTransition(localViewGroup1);
    }
    super.updateMenuView(paramBoolean);
    ((View)mMenuView).requestLayout();
    if (mMenu != null)
    {
      ArrayList localArrayList2 = mMenu.getActionItems();
      int k = localArrayList2.size();
      for (int m = 0; m < k; m++)
      {
        ActionProvider localActionProvider = ((MenuItemImpl)localArrayList2.get(m)).getSupportActionProvider();
        if (localActionProvider != null) {
          localActionProvider.setSubUiVisibilityListener(this);
        }
      }
    }
    ArrayList localArrayList1;
    int i;
    int j;
    if (mMenu != null)
    {
      localArrayList1 = mMenu.getNonActionItems();
      boolean bool = mReserveOverflow;
      i = 0;
      if (bool)
      {
        i = 0;
        if (localArrayList1 != null)
        {
          j = localArrayList1.size();
          if (j != 1) {
            break label279;
          }
          if (((MenuItemImpl)localArrayList1.get(0)).isActionViewExpanded()) {
            break label273;
          }
          i = 1;
        }
      }
      label167:
      if (i == 0) {
        break label296;
      }
      if (mOverflowButton == null) {
        mOverflowButton = new OverflowMenuButton(mSystemContext);
      }
      ViewGroup localViewGroup2 = (ViewGroup)mOverflowButton.getParent();
      if (localViewGroup2 != mMenuView)
      {
        if (localViewGroup2 != null) {
          localViewGroup2.removeView(mOverflowButton);
        }
        ActionMenuView localActionMenuView = (ActionMenuView)mMenuView;
        localActionMenuView.addView(mOverflowButton, localActionMenuView.generateOverflowButtonLayoutParams());
      }
    }
    for (;;)
    {
      ((ActionMenuView)mMenuView).setOverflowReserved(mReserveOverflow);
      return;
      localArrayList1 = null;
      break;
      label273:
      i = 0;
      break label167;
      label279:
      if (j > 0) {}
      for (i = 1;; i = 0) {
        break;
      }
      label296:
      if ((mOverflowButton != null) && (mOverflowButton.getParent() == mMenuView)) {
        ((ViewGroup)mMenuView).removeView(mOverflowButton);
      }
    }
  }
  
  private class ActionButtonSubmenu
    extends MenuPopupHelper
  {
    public ActionButtonSubmenu(Context paramContext, SubMenuBuilder paramSubMenuBuilder, View paramView)
    {
      super(paramSubMenuBuilder, paramView, false, R.attr.actionOverflowMenuStyle);
      if (!((MenuItemImpl)paramSubMenuBuilder.getItem()).isActionButton()) {
        if (mOverflowButton != null) {
          break label61;
        }
      }
      label61:
      for (Object localObject = (View)mMenuView;; localObject = mOverflowButton)
      {
        setAnchorView((View)localObject);
        setPresenterCallback(mPopupPresenterCallback);
        return;
      }
    }
    
    protected void onDismiss()
    {
      mActionButtonPopup = null;
      mOpenSubMenuId = 0;
      super.onDismiss();
    }
  }
  
  private class ActionMenuPopupCallback
    extends ActionMenuItemView.PopupCallback
  {
    ActionMenuPopupCallback() {}
    
    public ShowableListMenu getPopup()
    {
      if (mActionButtonPopup != null) {
        return mActionButtonPopup.getPopup();
      }
      return null;
    }
  }
  
  private class OpenOverflowRunnable
    implements Runnable
  {
    private ActionMenuPresenter.OverflowPopup mPopup;
    
    public OpenOverflowRunnable(ActionMenuPresenter.OverflowPopup paramOverflowPopup)
    {
      mPopup = paramOverflowPopup;
    }
    
    public void run()
    {
      if (mMenu != null) {
        mMenu.changeMenuMode();
      }
      View localView = (View)mMenuView;
      if ((localView != null) && (localView.getWindowToken() != null) && (mPopup.tryShow())) {
        mOverflowPopup = mPopup;
      }
      mPostedOpenRunnable = null;
    }
  }
  
  private class OverflowMenuButton
    extends AppCompatImageView
    implements ActionMenuView.ActionMenuChildView
  {
    private final float[] mTempPts = new float[2];
    
    public OverflowMenuButton(Context paramContext)
    {
      super(null, R.attr.actionOverflowButtonStyle);
      setClickable(true);
      setFocusable(true);
      setVisibility(0);
      setEnabled(true);
      setOnTouchListener(new ForwardingListener(this)
      {
        public ShowableListMenu getPopup()
        {
          if (mOverflowPopup == null) {
            return null;
          }
          return mOverflowPopup.getPopup();
        }
        
        public boolean onForwardingStarted()
        {
          showOverflowMenu();
          return true;
        }
        
        public boolean onForwardingStopped()
        {
          if (mPostedOpenRunnable != null) {
            return false;
          }
          hideOverflowMenu();
          return true;
        }
      });
    }
    
    public boolean needsDividerAfter()
    {
      return false;
    }
    
    public boolean needsDividerBefore()
    {
      return false;
    }
    
    public boolean performClick()
    {
      if (super.performClick()) {
        return true;
      }
      playSoundEffect(0);
      showOverflowMenu();
      return true;
    }
    
    protected boolean setFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      boolean bool = super.setFrame(paramInt1, paramInt2, paramInt3, paramInt4);
      Drawable localDrawable1 = getDrawable();
      Drawable localDrawable2 = getBackground();
      if ((localDrawable1 != null) && (localDrawable2 != null))
      {
        int i = getWidth();
        int j = getHeight();
        int k = Math.max(i, j) / 2;
        int m = getPaddingLeft() - getPaddingRight();
        int n = getPaddingTop() - getPaddingBottom();
        int i1 = (i + m) / 2;
        int i2 = (j + n) / 2;
        DrawableCompat.setHotspotBounds(localDrawable2, i1 - k, i2 - k, i1 + k, i2 + k);
      }
      return bool;
    }
  }
  
  private class OverflowPopup
    extends MenuPopupHelper
  {
    public OverflowPopup(Context paramContext, MenuBuilder paramMenuBuilder, View paramView, boolean paramBoolean)
    {
      super(paramMenuBuilder, paramView, paramBoolean, R.attr.actionOverflowMenuStyle);
      setGravity(8388613);
      setPresenterCallback(mPopupPresenterCallback);
    }
    
    protected void onDismiss()
    {
      if (mMenu != null) {
        mMenu.close();
      }
      mOverflowPopup = null;
      super.onDismiss();
    }
  }
  
  private class PopupPresenterCallback
    implements MenuPresenter.Callback
  {
    PopupPresenterCallback() {}
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
    {
      if ((paramMenuBuilder instanceof SubMenuBuilder)) {
        paramMenuBuilder.getRootMenu().close(false);
      }
      MenuPresenter.Callback localCallback = getCallback();
      if (localCallback != null) {
        localCallback.onCloseMenu(paramMenuBuilder, paramBoolean);
      }
    }
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      if (paramMenuBuilder == null) {
        return false;
      }
      mOpenSubMenuId = ((SubMenuBuilder)paramMenuBuilder).getItem().getItemId();
      MenuPresenter.Callback localCallback = getCallback();
      if (localCallback != null) {}
      for (boolean bool = localCallback.onOpenSubMenu(paramMenuBuilder);; bool = false) {
        return bool;
      }
    }
  }
  
  private static class SavedState
    implements Parcelable
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public ActionMenuPresenter.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActionMenuPresenter.SavedState(paramAnonymousParcel);
      }
      
      public ActionMenuPresenter.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ActionMenuPresenter.SavedState[paramAnonymousInt];
      }
    };
    public int openSubMenuId;
    
    SavedState() {}
    
    SavedState(Parcel paramParcel)
    {
      openSubMenuId = paramParcel.readInt();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(openSubMenuId);
    }
  }
}
