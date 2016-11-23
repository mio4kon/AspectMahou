package android.support.v4.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;

class DrawerLayoutCompatApi21
{
  private static final int[] THEME_ATTRS = { 16843828 };
  
  DrawerLayoutCompatApi21() {}
  
  public static void applyMarginInsets(ViewGroup.MarginLayoutParams paramMarginLayoutParams, Object paramObject, int paramInt)
  {
    WindowInsets localWindowInsets = (WindowInsets)paramObject;
    if (paramInt == 3) {
      localWindowInsets = localWindowInsets.replaceSystemWindowInsets(localWindowInsets.getSystemWindowInsetLeft(), localWindowInsets.getSystemWindowInsetTop(), 0, localWindowInsets.getSystemWindowInsetBottom());
    }
    for (;;)
    {
      leftMargin = localWindowInsets.getSystemWindowInsetLeft();
      topMargin = localWindowInsets.getSystemWindowInsetTop();
      rightMargin = localWindowInsets.getSystemWindowInsetRight();
      bottomMargin = localWindowInsets.getSystemWindowInsetBottom();
      return;
      if (paramInt == 5) {
        localWindowInsets = localWindowInsets.replaceSystemWindowInsets(0, localWindowInsets.getSystemWindowInsetTop(), localWindowInsets.getSystemWindowInsetRight(), localWindowInsets.getSystemWindowInsetBottom());
      }
    }
  }
  
  public static void configureApplyInsets(View paramView)
  {
    if ((paramView instanceof DrawerLayoutImpl))
    {
      paramView.setOnApplyWindowInsetsListener(new InsetsListener());
      paramView.setSystemUiVisibility(1280);
    }
  }
  
  public static void dispatchChildInsets(View paramView, Object paramObject, int paramInt)
  {
    WindowInsets localWindowInsets = (WindowInsets)paramObject;
    if (paramInt == 3) {
      localWindowInsets = localWindowInsets.replaceSystemWindowInsets(localWindowInsets.getSystemWindowInsetLeft(), localWindowInsets.getSystemWindowInsetTop(), 0, localWindowInsets.getSystemWindowInsetBottom());
    }
    for (;;)
    {
      paramView.dispatchApplyWindowInsets(localWindowInsets);
      return;
      if (paramInt == 5) {
        localWindowInsets = localWindowInsets.replaceSystemWindowInsets(0, localWindowInsets.getSystemWindowInsetTop(), localWindowInsets.getSystemWindowInsetRight(), localWindowInsets.getSystemWindowInsetBottom());
      }
    }
  }
  
  public static Drawable getDefaultStatusBarBackground(Context paramContext)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(THEME_ATTRS);
    try
    {
      Drawable localDrawable = localTypedArray.getDrawable(0);
      return localDrawable;
    }
    finally
    {
      localTypedArray.recycle();
    }
  }
  
  public static int getTopInset(Object paramObject)
  {
    if (paramObject != null) {
      return ((WindowInsets)paramObject).getSystemWindowInsetTop();
    }
    return 0;
  }
  
  static class InsetsListener
    implements View.OnApplyWindowInsetsListener
  {
    InsetsListener() {}
    
    public WindowInsets onApplyWindowInsets(View paramView, WindowInsets paramWindowInsets)
    {
      DrawerLayoutImpl localDrawerLayoutImpl = (DrawerLayoutImpl)paramView;
      if (paramWindowInsets.getSystemWindowInsetTop() > 0) {}
      for (boolean bool = true;; bool = false)
      {
        localDrawerLayoutImpl.setChildInsets(paramWindowInsets, bool);
        return paramWindowInsets.consumeSystemWindowInsets();
      }
    }
  }
}
