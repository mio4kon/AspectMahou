package android.support.v4.content.res;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.AnyRes;
import android.support.annotation.StyleableRes;
import android.util.TypedValue;

public class TypedArrayUtils
{
  public TypedArrayUtils() {}
  
  public static int getAttr(Context paramContext, int paramInt1, int paramInt2)
  {
    TypedValue localTypedValue = new TypedValue();
    paramContext.getTheme().resolveAttribute(paramInt1, localTypedValue, true);
    if (resourceId != 0) {
      return paramInt1;
    }
    return paramInt2;
  }
  
  public static boolean getBoolean(TypedArray paramTypedArray, @StyleableRes int paramInt1, @StyleableRes int paramInt2, boolean paramBoolean)
  {
    return paramTypedArray.getBoolean(paramInt1, paramTypedArray.getBoolean(paramInt2, paramBoolean));
  }
  
  public static Drawable getDrawable(TypedArray paramTypedArray, @StyleableRes int paramInt1, @StyleableRes int paramInt2)
  {
    Drawable localDrawable = paramTypedArray.getDrawable(paramInt1);
    if (localDrawable == null) {
      localDrawable = paramTypedArray.getDrawable(paramInt2);
    }
    return localDrawable;
  }
  
  public static int getInt(TypedArray paramTypedArray, @StyleableRes int paramInt1, @StyleableRes int paramInt2, int paramInt3)
  {
    return paramTypedArray.getInt(paramInt1, paramTypedArray.getInt(paramInt2, paramInt3));
  }
  
  @AnyRes
  public static int getResourceId(TypedArray paramTypedArray, @StyleableRes int paramInt1, @StyleableRes int paramInt2, @AnyRes int paramInt3)
  {
    return paramTypedArray.getResourceId(paramInt1, paramTypedArray.getResourceId(paramInt2, paramInt3));
  }
  
  public static String getString(TypedArray paramTypedArray, @StyleableRes int paramInt1, @StyleableRes int paramInt2)
  {
    String str = paramTypedArray.getString(paramInt1);
    if (str == null) {
      str = paramTypedArray.getString(paramInt2);
    }
    return str;
  }
  
  public static CharSequence[] getTextArray(TypedArray paramTypedArray, @StyleableRes int paramInt1, @StyleableRes int paramInt2)
  {
    CharSequence[] arrayOfCharSequence = paramTypedArray.getTextArray(paramInt1);
    if (arrayOfCharSequence == null) {
      arrayOfCharSequence = paramTypedArray.getTextArray(paramInt2);
    }
    return arrayOfCharSequence;
  }
}
