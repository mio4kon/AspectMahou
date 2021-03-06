package android.support.v7.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.view.menu.MenuWrapperFactory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;

@TargetApi(11)
public class SupportActionModeWrapper
  extends android.view.ActionMode
{
  final Context mContext;
  final ActionMode mWrappedObject;
  
  public SupportActionModeWrapper(Context paramContext, ActionMode paramActionMode)
  {
    mContext = paramContext;
    mWrappedObject = paramActionMode;
  }
  
  public void finish()
  {
    mWrappedObject.finish();
  }
  
  public View getCustomView()
  {
    return mWrappedObject.getCustomView();
  }
  
  public Menu getMenu()
  {
    return MenuWrapperFactory.wrapSupportMenu(mContext, (SupportMenu)mWrappedObject.getMenu());
  }
  
  public MenuInflater getMenuInflater()
  {
    return mWrappedObject.getMenuInflater();
  }
  
  public CharSequence getSubtitle()
  {
    return mWrappedObject.getSubtitle();
  }
  
  public Object getTag()
  {
    return mWrappedObject.getTag();
  }
  
  public CharSequence getTitle()
  {
    return mWrappedObject.getTitle();
  }
  
  public boolean getTitleOptionalHint()
  {
    return mWrappedObject.getTitleOptionalHint();
  }
  
  public void invalidate()
  {
    mWrappedObject.invalidate();
  }
  
  public boolean isTitleOptional()
  {
    return mWrappedObject.isTitleOptional();
  }
  
  public void setCustomView(View paramView)
  {
    mWrappedObject.setCustomView(paramView);
  }
  
  public void setSubtitle(int paramInt)
  {
    mWrappedObject.setSubtitle(paramInt);
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    mWrappedObject.setSubtitle(paramCharSequence);
  }
  
  public void setTag(Object paramObject)
  {
    mWrappedObject.setTag(paramObject);
  }
  
  public void setTitle(int paramInt)
  {
    mWrappedObject.setTitle(paramInt);
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    mWrappedObject.setTitle(paramCharSequence);
  }
  
  public void setTitleOptionalHint(boolean paramBoolean)
  {
    mWrappedObject.setTitleOptionalHint(paramBoolean);
  }
  
  public static class CallbackWrapper
    implements ActionMode.Callback
  {
    final ArrayList<SupportActionModeWrapper> mActionModes;
    final Context mContext;
    final SimpleArrayMap<Menu, Menu> mMenus;
    final android.view.ActionMode.Callback mWrappedCallback;
    
    public CallbackWrapper(Context paramContext, android.view.ActionMode.Callback paramCallback)
    {
      mContext = paramContext;
      mWrappedCallback = paramCallback;
      mActionModes = new ArrayList();
      mMenus = new SimpleArrayMap();
    }
    
    private Menu getMenuWrapper(Menu paramMenu)
    {
      Menu localMenu = (Menu)mMenus.get(paramMenu);
      if (localMenu == null)
      {
        localMenu = MenuWrapperFactory.wrapSupportMenu(mContext, (SupportMenu)paramMenu);
        mMenus.put(paramMenu, localMenu);
      }
      return localMenu;
    }
    
    public android.view.ActionMode getActionModeWrapper(ActionMode paramActionMode)
    {
      int i = 0;
      int j = mActionModes.size();
      while (i < j)
      {
        SupportActionModeWrapper localSupportActionModeWrapper2 = (SupportActionModeWrapper)mActionModes.get(i);
        if ((localSupportActionModeWrapper2 != null) && (mWrappedObject == paramActionMode)) {
          return localSupportActionModeWrapper2;
        }
        i++;
      }
      SupportActionModeWrapper localSupportActionModeWrapper1 = new SupportActionModeWrapper(mContext, paramActionMode);
      mActionModes.add(localSupportActionModeWrapper1);
      return localSupportActionModeWrapper1;
    }
    
    public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
    {
      return mWrappedCallback.onActionItemClicked(getActionModeWrapper(paramActionMode), MenuWrapperFactory.wrapSupportMenuItem(mContext, (SupportMenuItem)paramMenuItem));
    }
    
    public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      return mWrappedCallback.onCreateActionMode(getActionModeWrapper(paramActionMode), getMenuWrapper(paramMenu));
    }
    
    public void onDestroyActionMode(ActionMode paramActionMode)
    {
      mWrappedCallback.onDestroyActionMode(getActionModeWrapper(paramActionMode));
    }
    
    public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      return mWrappedCallback.onPrepareActionMode(getActionModeWrapper(paramActionMode), getMenuWrapper(paramMenu));
    }
  }
}
