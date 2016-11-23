package android.support.v7.view.menu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.dimen;
import android.support.v7.appcompat.R.layout;
import android.support.v7.widget.MenuItemHoverListener;
import android.support.v7.widget.MenuPopupWindow;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

final class CascadingMenuPopup
  extends MenuPopup
  implements MenuPresenter, View.OnKeyListener, PopupWindow.OnDismissListener
{
  static final int HORIZ_POSITION_LEFT = 0;
  static final int HORIZ_POSITION_RIGHT = 1;
  static final int SUBMENU_TIMEOUT_MS = 200;
  private View mAnchorView;
  private final Context mContext;
  private int mDropDownGravity = 0;
  private boolean mForceShowIcon;
  private final ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
  {
    public void onGlobalLayout()
    {
      if ((isShowing()) && (mShowingMenus.size() > 0) && (!mShowingMenus.get(0)).window.isModal()))
      {
        View localView = mShownAnchorView;
        if ((localView != null) && (localView.isShown())) {
          break label77;
        }
        dismiss();
      }
      for (;;)
      {
        return;
        label77:
        Iterator localIterator = mShowingMenus.iterator();
        while (localIterator.hasNext()) {
          nextwindow.show();
        }
      }
    }
  };
  private boolean mHasXOffset;
  private boolean mHasYOffset;
  private int mLastPosition;
  private final MenuItemHoverListener mMenuItemHoverListener = new MenuItemHoverListener()
  {
    public void onItemHoverEnter(@NonNull final MenuBuilder paramAnonymousMenuBuilder, @NonNull final MenuItem paramAnonymousMenuItem)
    {
      mSubMenuHoverHandler.removeCallbacksAndMessages(null);
      int i = -1;
      int j = 0;
      int k = mShowingMenus.size();
      for (;;)
      {
        if (j < k)
        {
          if (paramAnonymousMenuBuilder == mShowingMenus.get(j)).menu) {
            i = j;
          }
        }
        else
        {
          if (i != -1) {
            break;
          }
          return;
        }
        j++;
      }
      int m = i + 1;
      if (m < mShowingMenus.size()) {}
      for (final CascadingMenuPopup.CascadingMenuInfo localCascadingMenuInfo = (CascadingMenuPopup.CascadingMenuInfo)mShowingMenus.get(m);; localCascadingMenuInfo = null)
      {
        Runnable local1 = new Runnable()
        {
          public void run()
          {
            if (localCascadingMenuInfo != null)
            {
              mShouldCloseImmediately = true;
              localCascadingMenuInfomenu.close(false);
              mShouldCloseImmediately = false;
            }
            if ((paramAnonymousMenuItem.isEnabled()) && (paramAnonymousMenuItem.hasSubMenu())) {
              paramAnonymousMenuBuilder.performItemAction(paramAnonymousMenuItem, 0);
            }
          }
        };
        long l = 200L + SystemClock.uptimeMillis();
        mSubMenuHoverHandler.postAtTime(local1, paramAnonymousMenuBuilder, l);
        return;
      }
    }
    
    public void onItemHoverExit(@NonNull MenuBuilder paramAnonymousMenuBuilder, @NonNull MenuItem paramAnonymousMenuItem)
    {
      mSubMenuHoverHandler.removeCallbacksAndMessages(paramAnonymousMenuBuilder);
    }
  };
  private final int mMenuMaxWidth;
  private PopupWindow.OnDismissListener mOnDismissListener;
  private final boolean mOverflowOnly;
  private final List<MenuBuilder> mPendingMenus = new LinkedList();
  private final int mPopupStyleAttr;
  private final int mPopupStyleRes;
  private MenuPresenter.Callback mPresenterCallback;
  private int mRawDropDownGravity = 0;
  boolean mShouldCloseImmediately;
  private boolean mShowTitle;
  final List<CascadingMenuInfo> mShowingMenus = new ArrayList();
  View mShownAnchorView;
  final Handler mSubMenuHoverHandler;
  private ViewTreeObserver mTreeObserver;
  private int mXOffset;
  private int mYOffset;
  
  public CascadingMenuPopup(@NonNull Context paramContext, @NonNull View paramView, @AttrRes int paramInt1, @StyleRes int paramInt2, boolean paramBoolean)
  {
    mContext = paramContext;
    mAnchorView = paramView;
    mPopupStyleAttr = paramInt1;
    mPopupStyleRes = paramInt2;
    mOverflowOnly = paramBoolean;
    mForceShowIcon = false;
    mLastPosition = getInitialMenuPosition();
    Resources localResources = paramContext.getResources();
    mMenuMaxWidth = Math.max(getDisplayMetricswidthPixels / 2, localResources.getDimensionPixelSize(R.dimen.abc_config_prefDialogWidth));
    mSubMenuHoverHandler = new Handler();
  }
  
  private MenuPopupWindow createPopupWindow()
  {
    MenuPopupWindow localMenuPopupWindow = new MenuPopupWindow(mContext, null, mPopupStyleAttr, mPopupStyleRes);
    localMenuPopupWindow.setHoverListener(mMenuItemHoverListener);
    localMenuPopupWindow.setOnItemClickListener(this);
    localMenuPopupWindow.setOnDismissListener(this);
    localMenuPopupWindow.setAnchorView(mAnchorView);
    localMenuPopupWindow.setDropDownGravity(mDropDownGravity);
    localMenuPopupWindow.setModal(true);
    return localMenuPopupWindow;
  }
  
  private int findIndexOfAddedMenu(@NonNull MenuBuilder paramMenuBuilder)
  {
    int i = 0;
    int j = mShowingMenus.size();
    while (i < j)
    {
      if (paramMenuBuilder == mShowingMenus.get(i)).menu) {
        return i;
      }
      i++;
    }
    return -1;
  }
  
  private MenuItem findMenuItemForSubmenu(@NonNull MenuBuilder paramMenuBuilder1, @NonNull MenuBuilder paramMenuBuilder2)
  {
    int i = 0;
    int j = paramMenuBuilder1.size();
    while (i < j)
    {
      MenuItem localMenuItem = paramMenuBuilder1.getItem(i);
      if ((localMenuItem.hasSubMenu()) && (paramMenuBuilder2 == localMenuItem.getSubMenu())) {
        return localMenuItem;
      }
      i++;
    }
    return null;
  }
  
  @Nullable
  private View findParentViewForSubmenu(@NonNull CascadingMenuInfo paramCascadingMenuInfo, @NonNull MenuBuilder paramMenuBuilder)
  {
    MenuItem localMenuItem = findMenuItemForSubmenu(menu, paramMenuBuilder);
    if (localMenuItem == null) {
      return null;
    }
    ListView localListView = paramCascadingMenuInfo.getListView();
    ListAdapter localListAdapter = localListView.getAdapter();
    int i;
    MenuAdapter localMenuAdapter;
    label61:
    int j;
    int k;
    int m;
    if ((localListAdapter instanceof HeaderViewListAdapter))
    {
      HeaderViewListAdapter localHeaderViewListAdapter = (HeaderViewListAdapter)localListAdapter;
      i = localHeaderViewListAdapter.getHeadersCount();
      localMenuAdapter = (MenuAdapter)localHeaderViewListAdapter.getWrappedAdapter();
      j = -1;
      k = 0;
      m = localMenuAdapter.getCount();
    }
    for (;;)
    {
      if (k < m)
      {
        if (localMenuItem == localMenuAdapter.getItem(k)) {
          j = k;
        }
      }
      else
      {
        if (j == -1) {
          break;
        }
        int n = j + i - localListView.getFirstVisiblePosition();
        if ((n < 0) || (n >= localListView.getChildCount())) {
          break;
        }
        return localListView.getChildAt(n);
        localMenuAdapter = (MenuAdapter)localListAdapter;
        i = 0;
        break label61;
      }
      k++;
    }
  }
  
  private int getInitialMenuPosition()
  {
    int i = 1;
    if (ViewCompat.getLayoutDirection(mAnchorView) == i) {
      i = 0;
    }
    return i;
  }
  
  private int getNextMenuPosition(int paramInt)
  {
    ListView localListView = ((CascadingMenuInfo)mShowingMenus.get(-1 + mShowingMenus.size())).getListView();
    int[] arrayOfInt = new int[2];
    localListView.getLocationOnScreen(arrayOfInt);
    Rect localRect = new Rect();
    mShownAnchorView.getWindowVisibleDisplayFrame(localRect);
    if (mLastPosition == 1)
    {
      if (paramInt + (arrayOfInt[0] + localListView.getWidth()) > right) {
        return 0;
      }
      return 1;
    }
    if (arrayOfInt[0] - paramInt < 0) {
      return 1;
    }
    return 0;
  }
  
  private void showMenu(@NonNull MenuBuilder paramMenuBuilder)
  {
    LayoutInflater localLayoutInflater = LayoutInflater.from(mContext);
    MenuAdapter localMenuAdapter = new MenuAdapter(paramMenuBuilder, localLayoutInflater, mOverflowOnly);
    int i;
    MenuPopupWindow localMenuPopupWindow;
    CascadingMenuInfo localCascadingMenuInfo1;
    View localView;
    label130:
    int k;
    label164:
    int m;
    int i1;
    if ((!isShowing()) && (mForceShowIcon))
    {
      localMenuAdapter.setForceShowIcon(true);
      i = measureIndividualMenuWidth(localMenuAdapter, null, mContext, mMenuMaxWidth);
      localMenuPopupWindow = createPopupWindow();
      localMenuPopupWindow.setAdapter(localMenuAdapter);
      localMenuPopupWindow.setContentWidth(i);
      localMenuPopupWindow.setDropDownGravity(mDropDownGravity);
      if (mShowingMenus.size() <= 0) {
        break label383;
      }
      localCascadingMenuInfo1 = (CascadingMenuInfo)mShowingMenus.get(-1 + mShowingMenus.size());
      localView = findParentViewForSubmenu(localCascadingMenuInfo1, paramMenuBuilder);
      if (localView == null) {
        break label439;
      }
      localMenuPopupWindow.setTouchModal(false);
      localMenuPopupWindow.setEnterTransition(null);
      int j = getNextMenuPosition(i);
      if (j != 1) {
        break label392;
      }
      k = 1;
      mLastPosition = j;
      int[] arrayOfInt = new int[2];
      localView.getLocationInWindow(arrayOfInt);
      m = window.getHorizontalOffset() + arrayOfInt[0];
      int n = window.getVerticalOffset() + arrayOfInt[1];
      if ((0x5 & mDropDownGravity) != 5) {
        break label411;
      }
      if (k == 0) {
        break label398;
      }
      i1 = m + i;
      label234:
      localMenuPopupWindow.setHorizontalOffset(i1);
      localMenuPopupWindow.setVerticalOffset(n);
    }
    for (;;)
    {
      CascadingMenuInfo localCascadingMenuInfo2 = new CascadingMenuInfo(localMenuPopupWindow, paramMenuBuilder, mLastPosition);
      mShowingMenus.add(localCascadingMenuInfo2);
      localMenuPopupWindow.show();
      if ((localCascadingMenuInfo1 == null) && (mShowTitle) && (paramMenuBuilder.getHeaderTitle() != null))
      {
        ListView localListView = localMenuPopupWindow.getListView();
        FrameLayout localFrameLayout = (FrameLayout)localLayoutInflater.inflate(R.layout.abc_popup_menu_header_item_layout, localListView, false);
        TextView localTextView = (TextView)localFrameLayout.findViewById(16908310);
        localFrameLayout.setEnabled(false);
        localTextView.setText(paramMenuBuilder.getHeaderTitle());
        localListView.addHeaderView(localFrameLayout, null, false);
        localMenuPopupWindow.show();
      }
      return;
      if (!isShowing()) {
        break;
      }
      localMenuAdapter.setForceShowIcon(MenuPopup.shouldPreserveIconSpacing(paramMenuBuilder));
      break;
      label383:
      localCascadingMenuInfo1 = null;
      localView = null;
      break label130;
      label392:
      k = 0;
      break label164;
      label398:
      i1 = m - localView.getWidth();
      break label234;
      label411:
      if (k != 0)
      {
        i1 = m + localView.getWidth();
        break label234;
      }
      i1 = m - i;
      break label234;
      label439:
      if (mHasXOffset) {
        localMenuPopupWindow.setHorizontalOffset(mXOffset);
      }
      if (mHasYOffset) {
        localMenuPopupWindow.setVerticalOffset(mYOffset);
      }
      localMenuPopupWindow.setEpicenterBounds(getEpicenterBounds());
    }
  }
  
  public void addMenu(MenuBuilder paramMenuBuilder)
  {
    paramMenuBuilder.addMenuPresenter(this, mContext);
    if (isShowing())
    {
      showMenu(paramMenuBuilder);
      return;
    }
    mPendingMenus.add(paramMenuBuilder);
  }
  
  protected boolean closeMenuOnSubMenuOpened()
  {
    return false;
  }
  
  public void dismiss()
  {
    int i = mShowingMenus.size();
    if (i > 0)
    {
      CascadingMenuInfo[] arrayOfCascadingMenuInfo = (CascadingMenuInfo[])mShowingMenus.toArray(new CascadingMenuInfo[i]);
      for (int j = i - 1; j >= 0; j--)
      {
        CascadingMenuInfo localCascadingMenuInfo = arrayOfCascadingMenuInfo[j];
        if (window.isShowing()) {
          window.dismiss();
        }
      }
    }
  }
  
  public boolean flagActionItems()
  {
    return false;
  }
  
  public ListView getListView()
  {
    if (mShowingMenus.isEmpty()) {
      return null;
    }
    return ((CascadingMenuInfo)mShowingMenus.get(-1 + mShowingMenus.size())).getListView();
  }
  
  public boolean isShowing()
  {
    return (mShowingMenus.size() > 0) && (mShowingMenus.get(0)).window.isShowing());
  }
  
  public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
  {
    int i = findIndexOfAddedMenu(paramMenuBuilder);
    if (i < 0) {}
    do
    {
      return;
      int j = i + 1;
      if (j < mShowingMenus.size()) {
        mShowingMenus.get(j)).menu.close(false);
      }
      CascadingMenuInfo localCascadingMenuInfo = (CascadingMenuInfo)mShowingMenus.remove(i);
      menu.removeMenuPresenter(this);
      if (mShouldCloseImmediately)
      {
        window.setExitTransition(null);
        window.setAnimationStyle(0);
      }
      window.dismiss();
      int k = mShowingMenus.size();
      if (k > 0) {}
      for (mLastPosition = mShowingMenus.get(k - 1)).position; k == 0; mLastPosition = getInitialMenuPosition())
      {
        dismiss();
        if (mPresenterCallback != null) {
          mPresenterCallback.onCloseMenu(paramMenuBuilder, true);
        }
        if (mTreeObserver != null)
        {
          if (mTreeObserver.isAlive()) {
            mTreeObserver.removeGlobalOnLayoutListener(mGlobalLayoutListener);
          }
          mTreeObserver = null;
        }
        mOnDismissListener.onDismiss();
        return;
      }
    } while (!paramBoolean);
    mShowingMenus.get(0)).menu.close(false);
  }
  
  public void onDismiss()
  {
    int i = 0;
    int j = mShowingMenus.size();
    for (;;)
    {
      Object localObject = null;
      if (i < j)
      {
        CascadingMenuInfo localCascadingMenuInfo = (CascadingMenuInfo)mShowingMenus.get(i);
        if (!window.isShowing()) {
          localObject = localCascadingMenuInfo;
        }
      }
      else
      {
        if (localObject != null) {
          menu.close(false);
        }
        return;
      }
      i++;
    }
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramKeyEvent.getAction() == 1) && (paramInt == 82))
    {
      dismiss();
      return true;
    }
    return false;
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable) {}
  
  public Parcelable onSaveInstanceState()
  {
    return null;
  }
  
  public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder)
  {
    Iterator localIterator = mShowingMenus.iterator();
    while (localIterator.hasNext())
    {
      CascadingMenuInfo localCascadingMenuInfo = (CascadingMenuInfo)localIterator.next();
      if (paramSubMenuBuilder == menu) {
        localCascadingMenuInfo.getListView().requestFocus();
      }
    }
    do
    {
      return true;
      if (!paramSubMenuBuilder.hasVisibleItems()) {
        break;
      }
      addMenu(paramSubMenuBuilder);
    } while (mPresenterCallback == null);
    mPresenterCallback.onOpenSubMenu(paramSubMenuBuilder);
    return true;
    return false;
  }
  
  public void setAnchorView(@NonNull View paramView)
  {
    if (mAnchorView != paramView)
    {
      mAnchorView = paramView;
      mDropDownGravity = GravityCompat.getAbsoluteGravity(mRawDropDownGravity, ViewCompat.getLayoutDirection(mAnchorView));
    }
  }
  
  public void setCallback(MenuPresenter.Callback paramCallback)
  {
    mPresenterCallback = paramCallback;
  }
  
  public void setForceShowIcon(boolean paramBoolean)
  {
    mForceShowIcon = paramBoolean;
  }
  
  public void setGravity(int paramInt)
  {
    if (mRawDropDownGravity != paramInt)
    {
      mRawDropDownGravity = paramInt;
      mDropDownGravity = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(mAnchorView));
    }
  }
  
  public void setHorizontalOffset(int paramInt)
  {
    mHasXOffset = true;
    mXOffset = paramInt;
  }
  
  public void setOnDismissListener(PopupWindow.OnDismissListener paramOnDismissListener)
  {
    mOnDismissListener = paramOnDismissListener;
  }
  
  public void setShowTitle(boolean paramBoolean)
  {
    mShowTitle = paramBoolean;
  }
  
  public void setVerticalOffset(int paramInt)
  {
    mHasYOffset = true;
    mYOffset = paramInt;
  }
  
  public void show()
  {
    if (isShowing()) {}
    do
    {
      return;
      Iterator localIterator = mPendingMenus.iterator();
      while (localIterator.hasNext()) {
        showMenu((MenuBuilder)localIterator.next());
      }
      mPendingMenus.clear();
      mShownAnchorView = mAnchorView;
    } while (mShownAnchorView == null);
    if (mTreeObserver == null) {}
    for (int i = 1;; i = 0)
    {
      mTreeObserver = mShownAnchorView.getViewTreeObserver();
      if (i == 0) {
        break;
      }
      mTreeObserver.addOnGlobalLayoutListener(mGlobalLayoutListener);
      return;
    }
  }
  
  public void updateMenuView(boolean paramBoolean)
  {
    Iterator localIterator = mShowingMenus.iterator();
    while (localIterator.hasNext()) {
      toMenuAdapter(((CascadingMenuInfo)localIterator.next()).getListView().getAdapter()).notifyDataSetChanged();
    }
  }
  
  private static class CascadingMenuInfo
  {
    public final MenuBuilder menu;
    public final int position;
    public final MenuPopupWindow window;
    
    public CascadingMenuInfo(@NonNull MenuPopupWindow paramMenuPopupWindow, @NonNull MenuBuilder paramMenuBuilder, int paramInt)
    {
      window = paramMenuPopupWindow;
      menu = paramMenuBuilder;
      position = paramInt;
    }
    
    public ListView getListView()
    {
      return window.getListView();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface HorizPosition {}
}
