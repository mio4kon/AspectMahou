package android.support.v4.widget;

import android.view.View;
import android.widget.PopupWindow;

class PopupWindowCompatKitKat
{
  PopupWindowCompatKitKat() {}
  
  public static void showAsDropDown(PopupWindow paramPopupWindow, View paramView, int paramInt1, int paramInt2, int paramInt3)
  {
    paramPopupWindow.showAsDropDown(paramView, paramInt1, paramInt2, paramInt3);
  }
}
