package android.support.v7.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.TintContextWrapper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.view.View.OnClickListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

class AppCompatViewInflater
{
  private static final String LOG_TAG = "AppCompatViewInflater";
  private static final String[] sClassPrefixList = { "android.widget.", "android.view.", "android.webkit." };
  private static final Map<String, Constructor<? extends View>> sConstructorMap = new ArrayMap();
  private static final Class<?>[] sConstructorSignature = { Context.class, AttributeSet.class };
  private static final int[] sOnClickAttrs = { 16843375 };
  private final Object[] mConstructorArgs = new Object[2];
  
  AppCompatViewInflater() {}
  
  private void checkOnClickListener(View paramView, AttributeSet paramAttributeSet)
  {
    Context localContext = paramView.getContext();
    if ((!(localContext instanceof ContextWrapper)) || ((Build.VERSION.SDK_INT >= 15) && (!ViewCompat.hasOnClickListeners(paramView)))) {
      return;
    }
    TypedArray localTypedArray = localContext.obtainStyledAttributes(paramAttributeSet, sOnClickAttrs);
    String str = localTypedArray.getString(0);
    if (str != null) {
      paramView.setOnClickListener(new DeclaredOnClickListener(paramView, str));
    }
    localTypedArray.recycle();
  }
  
  private View createView(Context paramContext, String paramString1, String paramString2)
    throws ClassNotFoundException, InflateException
  {
    Constructor localConstructor = (Constructor)sConstructorMap.get(paramString1);
    if (localConstructor == null) {}
    try
    {
      ClassLoader localClassLoader = paramContext.getClassLoader();
      if (paramString2 != null) {}
      for (String str = paramString2 + paramString1;; str = paramString1)
      {
        localConstructor = localClassLoader.loadClass(str).asSubclass(View.class).getConstructor(sConstructorSignature);
        sConstructorMap.put(paramString1, localConstructor);
        localConstructor.setAccessible(true);
        View localView = (View)localConstructor.newInstance(mConstructorArgs);
        return localView;
      }
      return null;
    }
    catch (Exception localException) {}
  }
  
  /* Error */
  private View createViewFromTag(Context paramContext, String paramString, AttributeSet paramAttributeSet)
  {
    // Byte code:
    //   0: aload_2
    //   1: ldc -98
    //   3: invokevirtual 162	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   6: ifeq +13 -> 19
    //   9: aload_3
    //   10: aconst_null
    //   11: ldc -92
    //   13: invokeinterface 168 3 0
    //   18: astore_2
    //   19: aload_0
    //   20: getfield 53	android/support/v7/app/AppCompatViewInflater:mConstructorArgs	[Ljava/lang/Object;
    //   23: iconst_0
    //   24: aload_1
    //   25: aastore
    //   26: aload_0
    //   27: getfield 53	android/support/v7/app/AppCompatViewInflater:mConstructorArgs	[Ljava/lang/Object;
    //   30: iconst_1
    //   31: aload_3
    //   32: aastore
    //   33: iconst_m1
    //   34: aload_2
    //   35: bipush 46
    //   37: invokevirtual 172	java/lang/String:indexOf	(I)I
    //   40: if_icmpne +73 -> 113
    //   43: iconst_0
    //   44: istore 6
    //   46: iload 6
    //   48: getstatic 43	android/support/v7/app/AppCompatViewInflater:sClassPrefixList	[Ljava/lang/String;
    //   51: arraylength
    //   52: if_icmpge +45 -> 97
    //   55: aload_0
    //   56: aload_1
    //   57: aload_2
    //   58: getstatic 43	android/support/v7/app/AppCompatViewInflater:sClassPrefixList	[Ljava/lang/String;
    //   61: iload 6
    //   63: aaload
    //   64: invokespecial 174	android/support/v7/app/AppCompatViewInflater:createView	(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Landroid/view/View;
    //   67: astore 7
    //   69: aload 7
    //   71: ifnull +20 -> 91
    //   74: aload_0
    //   75: getfield 53	android/support/v7/app/AppCompatViewInflater:mConstructorArgs	[Ljava/lang/Object;
    //   78: iconst_0
    //   79: aconst_null
    //   80: aastore
    //   81: aload_0
    //   82: getfield 53	android/support/v7/app/AppCompatViewInflater:mConstructorArgs	[Ljava/lang/Object;
    //   85: iconst_1
    //   86: aconst_null
    //   87: aastore
    //   88: aload 7
    //   90: areturn
    //   91: iinc 6 1
    //   94: goto -48 -> 46
    //   97: aload_0
    //   98: getfield 53	android/support/v7/app/AppCompatViewInflater:mConstructorArgs	[Ljava/lang/Object;
    //   101: iconst_0
    //   102: aconst_null
    //   103: aastore
    //   104: aload_0
    //   105: getfield 53	android/support/v7/app/AppCompatViewInflater:mConstructorArgs	[Ljava/lang/Object;
    //   108: iconst_1
    //   109: aconst_null
    //   110: aastore
    //   111: aconst_null
    //   112: areturn
    //   113: aload_0
    //   114: aload_1
    //   115: aload_2
    //   116: aconst_null
    //   117: invokespecial 174	android/support/v7/app/AppCompatViewInflater:createView	(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Landroid/view/View;
    //   120: astore 8
    //   122: aload_0
    //   123: getfield 53	android/support/v7/app/AppCompatViewInflater:mConstructorArgs	[Ljava/lang/Object;
    //   126: iconst_0
    //   127: aconst_null
    //   128: aastore
    //   129: aload_0
    //   130: getfield 53	android/support/v7/app/AppCompatViewInflater:mConstructorArgs	[Ljava/lang/Object;
    //   133: iconst_1
    //   134: aconst_null
    //   135: aastore
    //   136: aload 8
    //   138: areturn
    //   139: astore 5
    //   141: aload_0
    //   142: getfield 53	android/support/v7/app/AppCompatViewInflater:mConstructorArgs	[Ljava/lang/Object;
    //   145: iconst_0
    //   146: aconst_null
    //   147: aastore
    //   148: aload_0
    //   149: getfield 53	android/support/v7/app/AppCompatViewInflater:mConstructorArgs	[Ljava/lang/Object;
    //   152: iconst_1
    //   153: aconst_null
    //   154: aastore
    //   155: aconst_null
    //   156: areturn
    //   157: astore 4
    //   159: aload_0
    //   160: getfield 53	android/support/v7/app/AppCompatViewInflater:mConstructorArgs	[Ljava/lang/Object;
    //   163: iconst_0
    //   164: aconst_null
    //   165: aastore
    //   166: aload_0
    //   167: getfield 53	android/support/v7/app/AppCompatViewInflater:mConstructorArgs	[Ljava/lang/Object;
    //   170: iconst_1
    //   171: aconst_null
    //   172: aastore
    //   173: aload 4
    //   175: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	176	0	this	AppCompatViewInflater
    //   0	176	1	paramContext	Context
    //   0	176	2	paramString	String
    //   0	176	3	paramAttributeSet	AttributeSet
    //   157	17	4	localObject	Object
    //   139	1	5	localException	Exception
    //   44	48	6	i	int
    //   67	22	7	localView1	View
    //   120	17	8	localView2	View
    // Exception table:
    //   from	to	target	type
    //   19	43	139	java/lang/Exception
    //   46	69	139	java/lang/Exception
    //   113	122	139	java/lang/Exception
    //   19	43	157	finally
    //   46	69	157	finally
    //   113	122	157	finally
  }
  
  private static Context themifyContext(Context paramContext, AttributeSet paramAttributeSet, boolean paramBoolean1, boolean paramBoolean2)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.View, 0, 0);
    int i = 0;
    if (paramBoolean1) {
      i = localTypedArray.getResourceId(R.styleable.View_android_theme, 0);
    }
    if ((paramBoolean2) && (i == 0))
    {
      i = localTypedArray.getResourceId(R.styleable.View_theme, 0);
      if (i != 0) {
        Log.i("AppCompatViewInflater", "app:theme is now deprecated. Please move to using android:theme instead.");
      }
    }
    localTypedArray.recycle();
    if ((i != 0) && ((!(paramContext instanceof ContextThemeWrapper)) || (((ContextThemeWrapper)paramContext).getThemeResId() != i))) {
      paramContext = new ContextThemeWrapper(paramContext, i);
    }
    return paramContext;
  }
  
  public final View createView(View paramView, String paramString, @NonNull Context paramContext, @NonNull AttributeSet paramAttributeSet, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    Context localContext = paramContext;
    if ((paramBoolean1) && (paramView != null)) {
      paramContext = paramView.getContext();
    }
    if ((paramBoolean2) || (paramBoolean3)) {
      paramContext = themifyContext(paramContext, paramAttributeSet, paramBoolean2, paramBoolean3);
    }
    if (paramBoolean4) {
      paramContext = TintContextWrapper.wrap(paramContext);
    }
    int i = -1;
    Object localObject;
    switch (paramString.hashCode())
    {
    default: 
      localObject = null;
      switch (i)
      {
      }
      break;
    }
    for (;;)
    {
      if ((localObject == null) && (localContext != paramContext)) {
        localObject = createViewFromTag(paramContext, paramString, paramAttributeSet);
      }
      if (localObject != null) {
        checkOnClickListener((View)localObject, paramAttributeSet);
      }
      return localObject;
      if (!paramString.equals("TextView")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("ImageView")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("Button")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("EditText")) {
        break;
      }
      i = 3;
      break;
      if (!paramString.equals("Spinner")) {
        break;
      }
      i = 4;
      break;
      if (!paramString.equals("ImageButton")) {
        break;
      }
      i = 5;
      break;
      if (!paramString.equals("CheckBox")) {
        break;
      }
      i = 6;
      break;
      if (!paramString.equals("RadioButton")) {
        break;
      }
      i = 7;
      break;
      if (!paramString.equals("CheckedTextView")) {
        break;
      }
      i = 8;
      break;
      if (!paramString.equals("AutoCompleteTextView")) {
        break;
      }
      i = 9;
      break;
      if (!paramString.equals("MultiAutoCompleteTextView")) {
        break;
      }
      i = 10;
      break;
      if (!paramString.equals("RatingBar")) {
        break;
      }
      i = 11;
      break;
      if (!paramString.equals("SeekBar")) {
        break;
      }
      i = 12;
      break;
      localObject = new AppCompatTextView(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatImageView(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatButton(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatEditText(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatSpinner(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatImageButton(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatCheckBox(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatRadioButton(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatCheckedTextView(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatAutoCompleteTextView(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatMultiAutoCompleteTextView(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatRatingBar(paramContext, paramAttributeSet);
      continue;
      localObject = new AppCompatSeekBar(paramContext, paramAttributeSet);
    }
  }
  
  private static class DeclaredOnClickListener
    implements View.OnClickListener
  {
    private final View mHostView;
    private final String mMethodName;
    private Context mResolvedContext;
    private Method mResolvedMethod;
    
    public DeclaredOnClickListener(@NonNull View paramView, @NonNull String paramString)
    {
      mHostView = paramView;
      mMethodName = paramString;
    }
    
    @NonNull
    private void resolveMethod(@Nullable Context paramContext, @NonNull String paramString)
    {
      while (paramContext != null) {
        try
        {
          if (!paramContext.isRestricted())
          {
            Method localMethod = paramContext.getClass().getMethod(mMethodName, new Class[] { View.class });
            if (localMethod != null)
            {
              mResolvedMethod = localMethod;
              mResolvedContext = paramContext;
              return;
            }
          }
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          if ((paramContext instanceof ContextWrapper)) {
            paramContext = ((ContextWrapper)paramContext).getBaseContext();
          } else {
            paramContext = null;
          }
        }
      }
      int i = mHostView.getId();
      if (i == -1) {}
      for (String str = "";; str = " with id '" + mHostView.getContext().getResources().getResourceEntryName(i) + "'") {
        throw new IllegalStateException("Could not find method " + mMethodName + "(View) in a parent or ancestor Context for android:onClick " + "attribute defined on view " + mHostView.getClass() + str);
      }
    }
    
    public void onClick(@NonNull View paramView)
    {
      if (mResolvedMethod == null) {
        resolveMethod(mHostView.getContext(), mMethodName);
      }
      try
      {
        mResolvedMethod.invoke(mResolvedContext, new Object[] { paramView });
        return;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new IllegalStateException("Could not execute non-public method for android:onClick", localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw new IllegalStateException("Could not execute method for android:onClick", localInvocationTargetException);
      }
    }
  }
}
