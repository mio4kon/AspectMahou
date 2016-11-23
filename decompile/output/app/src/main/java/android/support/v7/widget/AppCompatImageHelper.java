package android.support.v7.widget;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build.VERSION;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AppCompatImageHelper
{
  private final ImageView mView;
  
  public AppCompatImageHelper(ImageView paramImageView)
  {
    mView = paramImageView;
  }
  
  boolean hasOverlappingRendering()
  {
    Drawable localDrawable = mView.getBackground();
    return (Build.VERSION.SDK_INT < 21) || (!(localDrawable instanceof RippleDrawable));
  }
  
  public void loadFromAttributes(AttributeSet paramAttributeSet, int paramInt)
  {
    TintTypedArray localTintTypedArray = null;
    try
    {
      Drawable localDrawable = mView.getDrawable();
      localTintTypedArray = null;
      if (localDrawable == null)
      {
        localTintTypedArray = TintTypedArray.obtainStyledAttributes(mView.getContext(), paramAttributeSet, R.styleable.AppCompatImageView, paramInt, 0);
        int i = localTintTypedArray.getResourceId(R.styleable.AppCompatImageView_srcCompat, -1);
        if (i != -1)
        {
          localDrawable = AppCompatResources.getDrawable(mView.getContext(), i);
          if (localDrawable != null) {
            mView.setImageDrawable(localDrawable);
          }
        }
      }
      if (localDrawable != null) {
        DrawableUtils.fixDrawable(localDrawable);
      }
      return;
    }
    finally
    {
      if (localTintTypedArray != null) {
        localTintTypedArray.recycle();
      }
    }
  }
  
  public void setImageResource(int paramInt)
  {
    if (paramInt != 0)
    {
      Drawable localDrawable = AppCompatResources.getDrawable(mView.getContext(), paramInt);
      if (localDrawable != null) {
        DrawableUtils.fixDrawable(localDrawable);
      }
      mView.setImageDrawable(localDrawable);
      return;
    }
    mView.setImageDrawable(null);
  }
}
