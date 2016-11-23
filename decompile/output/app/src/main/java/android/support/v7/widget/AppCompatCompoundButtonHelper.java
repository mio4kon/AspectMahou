package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.widget.CompoundButton;

class AppCompatCompoundButtonHelper
{
  private ColorStateList mButtonTintList = null;
  private PorterDuff.Mode mButtonTintMode = null;
  private boolean mHasButtonTint = false;
  private boolean mHasButtonTintMode = false;
  private boolean mSkipNextApply;
  private final CompoundButton mView;
  
  AppCompatCompoundButtonHelper(CompoundButton paramCompoundButton)
  {
    mView = paramCompoundButton;
  }
  
  void applyButtonTint()
  {
    Drawable localDrawable1 = CompoundButtonCompat.getButtonDrawable(mView);
    if ((localDrawable1 != null) && ((mHasButtonTint) || (mHasButtonTintMode)))
    {
      Drawable localDrawable2 = DrawableCompat.wrap(localDrawable1).mutate();
      if (mHasButtonTint) {
        DrawableCompat.setTintList(localDrawable2, mButtonTintList);
      }
      if (mHasButtonTintMode) {
        DrawableCompat.setTintMode(localDrawable2, mButtonTintMode);
      }
      if (localDrawable2.isStateful()) {
        localDrawable2.setState(mView.getDrawableState());
      }
      mView.setButtonDrawable(localDrawable2);
    }
  }
  
  int getCompoundPaddingLeft(int paramInt)
  {
    if (Build.VERSION.SDK_INT < 17)
    {
      Drawable localDrawable = CompoundButtonCompat.getButtonDrawable(mView);
      if (localDrawable != null) {
        paramInt += localDrawable.getIntrinsicWidth();
      }
    }
    return paramInt;
  }
  
  ColorStateList getSupportButtonTintList()
  {
    return mButtonTintList;
  }
  
  PorterDuff.Mode getSupportButtonTintMode()
  {
    return mButtonTintMode;
  }
  
  void loadFromAttributes(AttributeSet paramAttributeSet, int paramInt)
  {
    TypedArray localTypedArray = mView.getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.CompoundButton, paramInt, 0);
    try
    {
      if (localTypedArray.hasValue(R.styleable.CompoundButton_android_button))
      {
        int i = localTypedArray.getResourceId(R.styleable.CompoundButton_android_button, 0);
        if (i != 0) {
          mView.setButtonDrawable(AppCompatResources.getDrawable(mView.getContext(), i));
        }
      }
      if (localTypedArray.hasValue(R.styleable.CompoundButton_buttonTint)) {
        CompoundButtonCompat.setButtonTintList(mView, localTypedArray.getColorStateList(R.styleable.CompoundButton_buttonTint));
      }
      if (localTypedArray.hasValue(R.styleable.CompoundButton_buttonTintMode)) {
        CompoundButtonCompat.setButtonTintMode(mView, DrawableUtils.parseTintMode(localTypedArray.getInt(R.styleable.CompoundButton_buttonTintMode, -1), null));
      }
      return;
    }
    finally
    {
      localTypedArray.recycle();
    }
  }
  
  void onSetButtonDrawable()
  {
    if (mSkipNextApply)
    {
      mSkipNextApply = false;
      return;
    }
    mSkipNextApply = true;
    applyButtonTint();
  }
  
  void setSupportButtonTintList(ColorStateList paramColorStateList)
  {
    mButtonTintList = paramColorStateList;
    mHasButtonTint = true;
    applyButtonTint();
  }
  
  void setSupportButtonTintMode(@Nullable PorterDuff.Mode paramMode)
  {
    mButtonTintMode = paramMode;
    mHasButtonTintMode = true;
    applyButtonTint();
  }
  
  static abstract interface DirectSetButtonDrawableInterface
  {
    public abstract void setButtonDrawable(Drawable paramDrawable);
  }
}
