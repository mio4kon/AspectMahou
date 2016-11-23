package android.support.v7.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.styleable;
import android.util.AttributeSet;
import android.view.View;

class AppCompatBackgroundHelper
{
  private int mBackgroundResId = -1;
  private BackgroundTintInfo mBackgroundTint;
  private final AppCompatDrawableManager mDrawableManager;
  private BackgroundTintInfo mInternalBackgroundTint;
  private BackgroundTintInfo mTmpInfo;
  private final View mView;
  
  AppCompatBackgroundHelper(View paramView)
  {
    mView = paramView;
    mDrawableManager = AppCompatDrawableManager.get();
  }
  
  private boolean applyFrameworkTintUsingColorFilter(@NonNull Drawable paramDrawable)
  {
    if (mTmpInfo == null) {
      mTmpInfo = new BackgroundTintInfo();
    }
    BackgroundTintInfo localBackgroundTintInfo = mTmpInfo;
    localBackgroundTintInfo.clear();
    ColorStateList localColorStateList = ViewCompat.getBackgroundTintList(mView);
    if (localColorStateList != null)
    {
      mHasTintList = true;
      mTintList = localColorStateList;
    }
    PorterDuff.Mode localMode = ViewCompat.getBackgroundTintMode(mView);
    if (localMode != null)
    {
      mHasTintMode = true;
      mTintMode = localMode;
    }
    if ((mHasTintList) || (mHasTintMode))
    {
      AppCompatDrawableManager.tintDrawable(paramDrawable, localBackgroundTintInfo, mView.getDrawableState());
      return true;
    }
    return false;
  }
  
  private boolean updateBackgroundTint()
  {
    if ((mBackgroundTint != null) && (mBackgroundTint.mHasTintList))
    {
      if (mBackgroundResId >= 0)
      {
        ColorStateList localColorStateList = mDrawableManager.getTintList(mView.getContext(), mBackgroundResId, mBackgroundTint.mOriginalTintList);
        if (localColorStateList != null)
        {
          mBackgroundTint.mTintList = localColorStateList;
          return true;
        }
      }
      if (mBackgroundTint.mTintList != mBackgroundTint.mOriginalTintList)
      {
        mBackgroundTint.mTintList = mBackgroundTint.mOriginalTintList;
        return true;
      }
    }
    return false;
  }
  
  void applySupportBackgroundTint()
  {
    Drawable localDrawable = mView.getBackground();
    if ((localDrawable == null) || ((Build.VERSION.SDK_INT == 21) && (applyFrameworkTintUsingColorFilter(localDrawable)))) {}
    do
    {
      return;
      if (mBackgroundTint != null)
      {
        AppCompatDrawableManager.tintDrawable(localDrawable, mBackgroundTint, mView.getDrawableState());
        return;
      }
    } while (mInternalBackgroundTint == null);
    AppCompatDrawableManager.tintDrawable(localDrawable, mInternalBackgroundTint, mView.getDrawableState());
  }
  
  ColorStateList getSupportBackgroundTintList()
  {
    if (mBackgroundTint != null) {
      return mBackgroundTint.mTintList;
    }
    return null;
  }
  
  PorterDuff.Mode getSupportBackgroundTintMode()
  {
    if (mBackgroundTint != null) {
      return mBackgroundTint.mTintMode;
    }
    return null;
  }
  
  void loadFromAttributes(AttributeSet paramAttributeSet, int paramInt)
  {
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(mView.getContext(), paramAttributeSet, R.styleable.ViewBackgroundHelper, paramInt, 0);
    try
    {
      if (localTintTypedArray.hasValue(R.styleable.ViewBackgroundHelper_android_background))
      {
        mBackgroundResId = localTintTypedArray.getResourceId(R.styleable.ViewBackgroundHelper_android_background, -1);
        ColorStateList localColorStateList = mDrawableManager.getTintList(mView.getContext(), mBackgroundResId);
        if (localColorStateList != null) {
          setInternalBackgroundTint(localColorStateList);
        }
      }
      if (localTintTypedArray.hasValue(R.styleable.ViewBackgroundHelper_backgroundTint)) {
        ViewCompat.setBackgroundTintList(mView, localTintTypedArray.getColorStateList(R.styleable.ViewBackgroundHelper_backgroundTint));
      }
      if (localTintTypedArray.hasValue(R.styleable.ViewBackgroundHelper_backgroundTintMode)) {
        ViewCompat.setBackgroundTintMode(mView, DrawableUtils.parseTintMode(localTintTypedArray.getInt(R.styleable.ViewBackgroundHelper_backgroundTintMode, -1), null));
      }
      return;
    }
    finally
    {
      localTintTypedArray.recycle();
    }
  }
  
  void onSetBackgroundDrawable(Drawable paramDrawable)
  {
    mBackgroundResId = -1;
    setInternalBackgroundTint(null);
    if (updateBackgroundTint()) {
      applySupportBackgroundTint();
    }
  }
  
  void onSetBackgroundResource(int paramInt)
  {
    mBackgroundResId = paramInt;
    if (mDrawableManager != null) {}
    for (ColorStateList localColorStateList = mDrawableManager.getTintList(mView.getContext(), paramInt);; localColorStateList = null)
    {
      setInternalBackgroundTint(localColorStateList);
      if (updateBackgroundTint()) {
        applySupportBackgroundTint();
      }
      return;
    }
  }
  
  void setInternalBackgroundTint(ColorStateList paramColorStateList)
  {
    if (paramColorStateList != null)
    {
      if (mInternalBackgroundTint == null) {
        mInternalBackgroundTint = new BackgroundTintInfo();
      }
      mInternalBackgroundTint.mTintList = paramColorStateList;
      mInternalBackgroundTint.mHasTintList = true;
    }
    for (;;)
    {
      applySupportBackgroundTint();
      return;
      mInternalBackgroundTint = null;
    }
  }
  
  void setSupportBackgroundTintList(ColorStateList paramColorStateList)
  {
    if (mBackgroundTint == null) {
      mBackgroundTint = new BackgroundTintInfo();
    }
    mBackgroundTint.mOriginalTintList = paramColorStateList;
    mBackgroundTint.mTintList = null;
    mBackgroundTint.mHasTintList = true;
    if (updateBackgroundTint()) {
      applySupportBackgroundTint();
    }
  }
  
  void setSupportBackgroundTintMode(PorterDuff.Mode paramMode)
  {
    if (mBackgroundTint == null) {
      mBackgroundTint = new BackgroundTintInfo();
    }
    mBackgroundTint.mTintMode = paramMode;
    mBackgroundTint.mHasTintMode = true;
    applySupportBackgroundTint();
  }
  
  private static class BackgroundTintInfo
    extends TintInfo
  {
    public ColorStateList mOriginalTintList;
    
    BackgroundTintInfo() {}
    
    void clear()
    {
      super.clear();
      mOriginalTintList = null;
    }
  }
}
