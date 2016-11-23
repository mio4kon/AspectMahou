package android.support.v7.view.menu;

import android.support.v7.appcompat.R.layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;

public class MenuAdapter
  extends BaseAdapter
{
  static final int ITEM_LAYOUT = R.layout.abc_popup_menu_item_layout;
  MenuBuilder mAdapterMenu;
  private int mExpandedIndex = -1;
  private boolean mForceShowIcon;
  private final LayoutInflater mInflater;
  private final boolean mOverflowOnly;
  
  public MenuAdapter(MenuBuilder paramMenuBuilder, LayoutInflater paramLayoutInflater, boolean paramBoolean)
  {
    mOverflowOnly = paramBoolean;
    mInflater = paramLayoutInflater;
    mAdapterMenu = paramMenuBuilder;
    findExpandedIndex();
  }
  
  void findExpandedIndex()
  {
    MenuItemImpl localMenuItemImpl = mAdapterMenu.getExpandedItem();
    if (localMenuItemImpl != null)
    {
      ArrayList localArrayList = mAdapterMenu.getNonActionItems();
      int i = localArrayList.size();
      for (int j = 0; j < i; j++) {
        if ((MenuItemImpl)localArrayList.get(j) == localMenuItemImpl)
        {
          mExpandedIndex = j;
          return;
        }
      }
    }
    mExpandedIndex = -1;
  }
  
  public MenuBuilder getAdapterMenu()
  {
    return mAdapterMenu;
  }
  
  public int getCount()
  {
    if (mOverflowOnly) {}
    for (ArrayList localArrayList = mAdapterMenu.getNonActionItems(); mExpandedIndex < 0; localArrayList = mAdapterMenu.getVisibleItems()) {
      return localArrayList.size();
    }
    return -1 + localArrayList.size();
  }
  
  public boolean getForceShowIcon()
  {
    return mForceShowIcon;
  }
  
  public MenuItemImpl getItem(int paramInt)
  {
    if (mOverflowOnly) {}
    for (ArrayList localArrayList = mAdapterMenu.getNonActionItems();; localArrayList = mAdapterMenu.getVisibleItems())
    {
      if ((mExpandedIndex >= 0) && (paramInt >= mExpandedIndex)) {
        paramInt++;
      }
      return (MenuItemImpl)localArrayList.get(paramInt);
    }
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    if (paramView == null) {
      paramView = mInflater.inflate(ITEM_LAYOUT, paramViewGroup, false);
    }
    MenuView.ItemView localItemView = (MenuView.ItemView)paramView;
    if (mForceShowIcon) {
      ((ListMenuItemView)paramView).setForceShowIcon(true);
    }
    localItemView.initialize(getItem(paramInt), 0);
    return paramView;
  }
  
  public void notifyDataSetChanged()
  {
    findExpandedIndex();
    super.notifyDataSetChanged();
  }
  
  public void setForceShowIcon(boolean paramBoolean)
  {
    mForceShowIcon = paramBoolean;
  }
}
