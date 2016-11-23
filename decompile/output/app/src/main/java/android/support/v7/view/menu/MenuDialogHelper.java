package android.support.v7.view.menu;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.appcompat.R.layout;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ListAdapter;

class MenuDialogHelper
  implements DialogInterface.OnKeyListener, DialogInterface.OnClickListener, DialogInterface.OnDismissListener, MenuPresenter.Callback
{
  private AlertDialog mDialog;
  private MenuBuilder mMenu;
  ListMenuPresenter mPresenter;
  private MenuPresenter.Callback mPresenterCallback;
  
  public MenuDialogHelper(MenuBuilder paramMenuBuilder)
  {
    mMenu = paramMenuBuilder;
  }
  
  public void dismiss()
  {
    if (mDialog != null) {
      mDialog.dismiss();
    }
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    mMenu.performItemAction((MenuItemImpl)mPresenter.getAdapter().getItem(paramInt), 0);
  }
  
  public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
  {
    if ((paramBoolean) || (paramMenuBuilder == mMenu)) {
      dismiss();
    }
    if (mPresenterCallback != null) {
      mPresenterCallback.onCloseMenu(paramMenuBuilder, paramBoolean);
    }
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    mPresenter.onCloseMenu(mMenu, true);
  }
  
  public boolean onKey(DialogInterface paramDialogInterface, int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 82) || (paramInt == 4)) {
      if ((paramKeyEvent.getAction() == 0) && (paramKeyEvent.getRepeatCount() == 0))
      {
        Window localWindow2 = mDialog.getWindow();
        if (localWindow2 != null)
        {
          View localView2 = localWindow2.getDecorView();
          if (localView2 != null)
          {
            KeyEvent.DispatcherState localDispatcherState2 = localView2.getKeyDispatcherState();
            if (localDispatcherState2 != null)
            {
              localDispatcherState2.startTracking(paramKeyEvent, this);
              return true;
            }
          }
        }
      }
      else if ((paramKeyEvent.getAction() == 1) && (!paramKeyEvent.isCanceled()))
      {
        Window localWindow1 = mDialog.getWindow();
        if (localWindow1 != null)
        {
          View localView1 = localWindow1.getDecorView();
          if (localView1 != null)
          {
            KeyEvent.DispatcherState localDispatcherState1 = localView1.getKeyDispatcherState();
            if ((localDispatcherState1 != null) && (localDispatcherState1.isTracking(paramKeyEvent)))
            {
              mMenu.close(true);
              paramDialogInterface.dismiss();
              return true;
            }
          }
        }
      }
    }
    return mMenu.performShortcut(paramInt, paramKeyEvent, 0);
  }
  
  public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
  {
    if (mPresenterCallback != null) {
      return mPresenterCallback.onOpenSubMenu(paramMenuBuilder);
    }
    return false;
  }
  
  public void setPresenterCallback(MenuPresenter.Callback paramCallback)
  {
    mPresenterCallback = paramCallback;
  }
  
  public void show(IBinder paramIBinder)
  {
    MenuBuilder localMenuBuilder = mMenu;
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(localMenuBuilder.getContext());
    mPresenter = new ListMenuPresenter(localBuilder.getContext(), R.layout.abc_list_menu_item_layout);
    mPresenter.setCallback(this);
    mMenu.addMenuPresenter(mPresenter);
    localBuilder.setAdapter(mPresenter.getAdapter(), this);
    View localView = localMenuBuilder.getHeaderView();
    if (localView != null) {
      localBuilder.setCustomTitle(localView);
    }
    for (;;)
    {
      localBuilder.setOnKeyListener(this);
      mDialog = localBuilder.create();
      mDialog.setOnDismissListener(this);
      WindowManager.LayoutParams localLayoutParams = mDialog.getWindow().getAttributes();
      type = 1003;
      if (paramIBinder != null) {
        token = paramIBinder;
      }
      flags = (0x20000 | flags);
      mDialog.show();
      return;
      localBuilder.setIcon(localMenuBuilder.getHeaderIcon()).setTitle(localMenuBuilder.getHeaderTitle());
    }
  }
}
