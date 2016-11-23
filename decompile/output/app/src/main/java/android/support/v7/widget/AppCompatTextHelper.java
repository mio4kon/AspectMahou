package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.text.AllCapsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.widget.TextView;

class AppCompatTextHelper
{
  private TintInfo mDrawableBottomTint;
  private TintInfo mDrawableLeftTint;
  private TintInfo mDrawableRightTint;
  private TintInfo mDrawableTopTint;
  final TextView mView;
  
  AppCompatTextHelper(TextView paramTextView)
  {
    mView = paramTextView;
  }
  
  static AppCompatTextHelper create(TextView paramTextView)
  {
    if (Build.VERSION.SDK_INT >= 17) {
      return new AppCompatTextHelperV17(paramTextView);
    }
    return new AppCompatTextHelper(paramTextView);
  }
  
  protected static TintInfo createTintInfo(Context paramContext, AppCompatDrawableManager paramAppCompatDrawableManager, int paramInt)
  {
    ColorStateList localColorStateList = paramAppCompatDrawableManager.getTintList(paramContext, paramInt);
    if (localColorStateList != null)
    {
      TintInfo localTintInfo = new TintInfo();
      mHasTintList = true;
      mTintList = localColorStateList;
      return localTintInfo;
    }
    return null;
  }
  
  final void applyCompoundDrawableTint(Drawable paramDrawable, TintInfo paramTintInfo)
  {
    if ((paramDrawable != null) && (paramTintInfo != null)) {
      AppCompatDrawableManager.tintDrawable(paramDrawable, paramTintInfo, mView.getDrawableState());
    }
  }
  
  void applyCompoundDrawablesTints()
  {
    if ((mDrawableLeftTint != null) || (mDrawableTopTint != null) || (mDrawableRightTint != null) || (mDrawableBottomTint != null))
    {
      Drawable[] arrayOfDrawable = mView.getCompoundDrawables();
      applyCompoundDrawableTint(arrayOfDrawable[0], mDrawableLeftTint);
      applyCompoundDrawableTint(arrayOfDrawable[1], mDrawableTopTint);
      applyCompoundDrawableTint(arrayOfDrawable[2], mDrawableRightTint);
      applyCompoundDrawableTint(arrayOfDrawable[3], mDrawableBottomTint);
    }
  }
  
  void loadFromAttributes(AttributeSet paramAttributeSet, int paramInt)
  {
    Context localContext = mView.getContext();
    AppCompatDrawableManager localAppCompatDrawableManager = AppCompatDrawableManager.get();
    TintTypedArray localTintTypedArray1 = TintTypedArray.obtainStyledAttributes(localContext, paramAttributeSet, R.styleable.AppCompatTextHelper, paramInt, 0);
    int i = localTintTypedArray1.getResourceId(R.styleable.AppCompatTextHelper_android_textAppearance, -1);
    if (localTintTypedArray1.hasValue(R.styleable.AppCompatTextHelper_android_drawableLeft)) {
      mDrawableLeftTint = createTintInfo(localContext, localAppCompatDrawableManager, localTintTypedArray1.getResourceId(R.styleable.AppCompatTextHelper_android_drawableLeft, 0));
    }
    if (localTintTypedArray1.hasValue(R.styleable.AppCompatTextHelper_android_drawableTop)) {
      mDrawableTopTint = createTintInfo(localContext, localAppCompatDrawableManager, localTintTypedArray1.getResourceId(R.styleable.AppCompatTextHelper_android_drawableTop, 0));
    }
    if (localTintTypedArray1.hasValue(R.styleable.AppCompatTextHelper_android_drawableRight)) {
      mDrawableRightTint = createTintInfo(localContext, localAppCompatDrawableManager, localTintTypedArray1.getResourceId(R.styleable.AppCompatTextHelper_android_drawableRight, 0));
    }
    if (localTintTypedArray1.hasValue(R.styleable.AppCompatTextHelper_android_drawableBottom)) {
      mDrawableBottomTint = createTintInfo(localContext, localAppCompatDrawableManager, localTintTypedArray1.getResourceId(R.styleable.AppCompatTextHelper_android_drawableBottom, 0));
    }
    localTintTypedArray1.recycle();
    boolean bool1 = mView.getTransformationMethod() instanceof PasswordTransformationMethod;
    boolean bool2 = false;
    int j = 0;
    ColorStateList localColorStateList = null;
    if (i != -1)
    {
      TintTypedArray localTintTypedArray3 = TintTypedArray.obtainStyledAttributes(localContext, i, R.styleable.TextAppearance);
      bool2 = false;
      j = 0;
      if (!bool1)
      {
        boolean bool4 = localTintTypedArray3.hasValue(R.styleable.TextAppearance_textAllCaps);
        bool2 = false;
        j = 0;
        if (bool4)
        {
          j = 1;
          bool2 = localTintTypedArray3.getBoolean(R.styleable.TextAppearance_textAllCaps, false);
        }
      }
      int k = Build.VERSION.SDK_INT;
      localColorStateList = null;
      if (k < 23)
      {
        boolean bool3 = localTintTypedArray3.hasValue(R.styleable.TextAppearance_android_textColor);
        localColorStateList = null;
        if (bool3) {
          localColorStateList = localTintTypedArray3.getColorStateList(R.styleable.TextAppearance_android_textColor);
        }
      }
      localTintTypedArray3.recycle();
    }
    TintTypedArray localTintTypedArray2 = TintTypedArray.obtainStyledAttributes(localContext, paramAttributeSet, R.styleable.TextAppearance, paramInt, 0);
    if ((!bool1) && (localTintTypedArray2.hasValue(R.styleable.TextAppearance_textAllCaps)))
    {
      j = 1;
      bool2 = localTintTypedArray2.getBoolean(R.styleable.TextAppearance_textAllCaps, false);
    }
    if ((Build.VERSION.SDK_INT < 23) && (localTintTypedArray2.hasValue(R.styleable.TextAppearance_android_textColor))) {
      localColorStateList = localTintTypedArray2.getColorStateList(R.styleable.TextAppearance_android_textColor);
    }
    localTintTypedArray2.recycle();
    if (localColorStateList != null) {
      mView.setTextColor(localColorStateList);
    }
    if ((!bool1) && (j != 0)) {
      setAllCaps(bool2);
    }
  }
  
  void onSetTextAppearance(Context paramContext, int paramInt)
  {
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramInt, R.styleable.TextAppearance);
    if (localTintTypedArray.hasValue(R.styleable.TextAppearance_textAllCaps)) {
      setAllCaps(localTintTypedArray.getBoolean(R.styleable.TextAppearance_textAllCaps, false));
    }
    if ((Build.VERSION.SDK_INT < 23) && (localTintTypedArray.hasValue(R.styleable.TextAppearance_android_textColor)))
    {
      ColorStateList localColorStateList = localTintTypedArray.getColorStateList(R.styleable.TextAppearance_android_textColor);
      if (localColorStateList != null) {
        mView.setTextColor(localColorStateList);
      }
    }
    localTintTypedArray.recycle();
  }
  
  void setAllCaps(boolean paramBoolean)
  {
    TextView localTextView = mView;
    if (paramBoolean) {}
    for (AllCapsTransformationMethod localAllCapsTransformationMethod = new AllCapsTransformationMethod(mView.getContext());; localAllCapsTransformationMethod = null)
    {
      localTextView.setTransformationMethod(localAllCapsTransformationMethod);
      return;
    }
  }
}
