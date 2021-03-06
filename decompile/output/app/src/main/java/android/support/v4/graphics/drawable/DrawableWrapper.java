package android.support.v4.graphics.drawable;

import android.graphics.drawable.Drawable;

public abstract interface DrawableWrapper
{
  public abstract Drawable getWrappedDrawable();
  
  public abstract void setWrappedDrawable(Drawable paramDrawable);
}
