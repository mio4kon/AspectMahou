package android.support.v7.view.menu;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;

public class ActionMenuItem
  implements SupportMenuItem
{
  private static final int CHECKABLE = 1;
  private static final int CHECKED = 2;
  private static final int ENABLED = 16;
  private static final int EXCLUSIVE = 4;
  private static final int HIDDEN = 8;
  private static final int NO_ICON;
  private final int mCategoryOrder;
  private MenuItem.OnMenuItemClickListener mClickListener;
  private Context mContext;
  private int mFlags = 16;
  private final int mGroup;
  private Drawable mIconDrawable;
  private int mIconResId = 0;
  private final int mId;
  private Intent mIntent;
  private final int mOrdering;
  private char mShortcutAlphabeticChar;
  private char mShortcutNumericChar;
  private CharSequence mTitle;
  private CharSequence mTitleCondensed;
  
  public ActionMenuItem(Context paramContext, int paramInt1, int paramInt2, int paramInt3, int paramInt4, CharSequence paramCharSequence)
  {
    mContext = paramContext;
    mId = paramInt2;
    mGroup = paramInt1;
    mCategoryOrder = paramInt3;
    mOrdering = paramInt4;
    mTitle = paramCharSequence;
  }
  
  public boolean collapseActionView()
  {
    return false;
  }
  
  public boolean expandActionView()
  {
    return false;
  }
  
  public android.view.ActionProvider getActionProvider()
  {
    throw new UnsupportedOperationException();
  }
  
  public View getActionView()
  {
    return null;
  }
  
  public char getAlphabeticShortcut()
  {
    return mShortcutAlphabeticChar;
  }
  
  public int getGroupId()
  {
    return mGroup;
  }
  
  public Drawable getIcon()
  {
    return mIconDrawable;
  }
  
  public Intent getIntent()
  {
    return mIntent;
  }
  
  public int getItemId()
  {
    return mId;
  }
  
  public ContextMenu.ContextMenuInfo getMenuInfo()
  {
    return null;
  }
  
  public char getNumericShortcut()
  {
    return mShortcutNumericChar;
  }
  
  public int getOrder()
  {
    return mOrdering;
  }
  
  public SubMenu getSubMenu()
  {
    return null;
  }
  
  public android.support.v4.view.ActionProvider getSupportActionProvider()
  {
    return null;
  }
  
  public CharSequence getTitle()
  {
    return mTitle;
  }
  
  public CharSequence getTitleCondensed()
  {
    if (mTitleCondensed != null) {
      return mTitleCondensed;
    }
    return mTitle;
  }
  
  public boolean hasSubMenu()
  {
    return false;
  }
  
  public boolean invoke()
  {
    if ((mClickListener != null) && (mClickListener.onMenuItemClick(this))) {
      return true;
    }
    if (mIntent != null)
    {
      mContext.startActivity(mIntent);
      return true;
    }
    return false;
  }
  
  public boolean isActionViewExpanded()
  {
    return false;
  }
  
  public boolean isCheckable()
  {
    return (0x1 & mFlags) != 0;
  }
  
  public boolean isChecked()
  {
    return (0x2 & mFlags) != 0;
  }
  
  public boolean isEnabled()
  {
    return (0x10 & mFlags) != 0;
  }
  
  public boolean isVisible()
  {
    return (0x8 & mFlags) == 0;
  }
  
  public MenuItem setActionProvider(android.view.ActionProvider paramActionProvider)
  {
    throw new UnsupportedOperationException();
  }
  
  public SupportMenuItem setActionView(int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  public SupportMenuItem setActionView(View paramView)
  {
    throw new UnsupportedOperationException();
  }
  
  public MenuItem setAlphabeticShortcut(char paramChar)
  {
    mShortcutAlphabeticChar = paramChar;
    return this;
  }
  
  public MenuItem setCheckable(boolean paramBoolean)
  {
    int i = 0xFFFFFFFE & mFlags;
    if (paramBoolean) {}
    for (int j = 1;; j = 0)
    {
      mFlags = (j | i);
      return this;
    }
  }
  
  public MenuItem setChecked(boolean paramBoolean)
  {
    int i = 0xFFFFFFFD & mFlags;
    if (paramBoolean) {}
    for (int j = 2;; j = 0)
    {
      mFlags = (j | i);
      return this;
    }
  }
  
  public MenuItem setEnabled(boolean paramBoolean)
  {
    int i = 0xFFFFFFEF & mFlags;
    if (paramBoolean) {}
    for (int j = 16;; j = 0)
    {
      mFlags = (j | i);
      return this;
    }
  }
  
  public ActionMenuItem setExclusiveCheckable(boolean paramBoolean)
  {
    int i = 0xFFFFFFFB & mFlags;
    if (paramBoolean) {}
    for (int j = 4;; j = 0)
    {
      mFlags = (j | i);
      return this;
    }
  }
  
  public MenuItem setIcon(int paramInt)
  {
    mIconResId = paramInt;
    mIconDrawable = ContextCompat.getDrawable(mContext, paramInt);
    return this;
  }
  
  public MenuItem setIcon(Drawable paramDrawable)
  {
    mIconDrawable = paramDrawable;
    mIconResId = 0;
    return this;
  }
  
  public MenuItem setIntent(Intent paramIntent)
  {
    mIntent = paramIntent;
    return this;
  }
  
  public MenuItem setNumericShortcut(char paramChar)
  {
    mShortcutNumericChar = paramChar;
    return this;
  }
  
  public MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener paramOnActionExpandListener)
  {
    throw new UnsupportedOperationException();
  }
  
  public MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener paramOnMenuItemClickListener)
  {
    mClickListener = paramOnMenuItemClickListener;
    return this;
  }
  
  public MenuItem setShortcut(char paramChar1, char paramChar2)
  {
    mShortcutNumericChar = paramChar1;
    mShortcutAlphabeticChar = paramChar2;
    return this;
  }
  
  public void setShowAsAction(int paramInt) {}
  
  public SupportMenuItem setShowAsActionFlags(int paramInt)
  {
    setShowAsAction(paramInt);
    return this;
  }
  
  public SupportMenuItem setSupportActionProvider(android.support.v4.view.ActionProvider paramActionProvider)
  {
    throw new UnsupportedOperationException();
  }
  
  public SupportMenuItem setSupportOnActionExpandListener(MenuItemCompat.OnActionExpandListener paramOnActionExpandListener)
  {
    return this;
  }
  
  public MenuItem setTitle(int paramInt)
  {
    mTitle = mContext.getResources().getString(paramInt);
    return this;
  }
  
  public MenuItem setTitle(CharSequence paramCharSequence)
  {
    mTitle = paramCharSequence;
    return this;
  }
  
  public MenuItem setTitleCondensed(CharSequence paramCharSequence)
  {
    mTitleCondensed = paramCharSequence;
    return this;
  }
  
  public MenuItem setVisible(boolean paramBoolean)
  {
    int i = 0x8 & mFlags;
    if (paramBoolean) {}
    for (int j = 0;; j = 8)
    {
      mFlags = (j | i);
      return this;
    }
  }
}
