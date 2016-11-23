package android.support.v4.graphics.drawable;

import android.graphics.drawable.Drawable;

class DrawableCompatHoneycomb
{
  DrawableCompatHoneycomb() {}
  
  public static void jumpToCurrentState(Drawable paramDrawable)
  {
    paramDrawable.jumpToCurrentState();
  }
  
  public static Drawable wrapForTinting(Drawable paramDrawable)
  {
    if (!(paramDrawable instanceof TintAwareDrawable)) {
      paramDrawable = new DrawableWrapperHoneycomb(paramDrawable);
    }
    return paramDrawable;
  }
}
