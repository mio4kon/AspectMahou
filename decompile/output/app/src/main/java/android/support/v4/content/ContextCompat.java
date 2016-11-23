package android.support.v4.content;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.os.BuildCompat;
import android.util.TypedValue;
import java.io.File;

public class ContextCompat
{
  private static final String DIR_ANDROID = "Android";
  private static final String DIR_OBB = "obb";
  private static final String TAG = "ContextCompat";
  private static final Object sLock = new Object();
  private static TypedValue sTempValue;
  
  @Deprecated
  public ContextCompat() {}
  
  private static File buildPath(File paramFile, String... paramVarArgs)
  {
    int i = paramVarArgs.length;
    int j = 0;
    Object localObject1 = paramFile;
    String str;
    Object localObject2;
    if (j < i)
    {
      str = paramVarArgs[j];
      if (localObject1 == null) {
        localObject2 = new File(str);
      }
    }
    for (;;)
    {
      j++;
      localObject1 = localObject2;
      break;
      if (str != null)
      {
        localObject2 = new File((File)localObject1, str);
        continue;
        return localObject1;
      }
      else
      {
        localObject2 = localObject1;
      }
    }
  }
  
  public static int checkSelfPermission(@NonNull Context paramContext, @NonNull String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("permission is null");
    }
    return paramContext.checkPermission(paramString, Process.myPid(), Process.myUid());
  }
  
  @Deprecated
  public static Context createDeviceEncryptedStorageContext(Context paramContext)
  {
    return createDeviceProtectedStorageContext(paramContext);
  }
  
  public static Context createDeviceProtectedStorageContext(Context paramContext)
  {
    if (BuildCompat.isAtLeastN()) {
      return ContextCompatApi24.createDeviceProtectedStorageContext(paramContext);
    }
    return null;
  }
  
  /* Error */
  private static File createFilesDir(File paramFile)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: aload_0
    //   4: invokevirtual 78	java/io/File:exists	()Z
    //   7: ifne +19 -> 26
    //   10: aload_0
    //   11: invokevirtual 81	java/io/File:mkdirs	()Z
    //   14: ifne +12 -> 26
    //   17: aload_0
    //   18: invokevirtual 78	java/io/File:exists	()Z
    //   21: istore_2
    //   22: iload_2
    //   23: ifeq +8 -> 31
    //   26: ldc 2
    //   28: monitorexit
    //   29: aload_0
    //   30: areturn
    //   31: ldc 14
    //   33: new 83	java/lang/StringBuilder
    //   36: dup
    //   37: invokespecial 84	java/lang/StringBuilder:<init>	()V
    //   40: ldc 86
    //   42: invokevirtual 90	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   45: aload_0
    //   46: invokevirtual 94	java/io/File:getPath	()Ljava/lang/String;
    //   49: invokevirtual 90	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   52: invokevirtual 97	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   55: invokestatic 103	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   58: pop
    //   59: aconst_null
    //   60: astore_0
    //   61: goto -35 -> 26
    //   64: astore_1
    //   65: ldc 2
    //   67: monitorexit
    //   68: aload_1
    //   69: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	70	0	paramFile	File
    //   64	5	1	localObject	Object
    //   21	2	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   3	22	64	finally
    //   31	59	64	finally
  }
  
  public static File getCodeCacheDir(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return ContextCompatApi21.getCodeCacheDir(paramContext);
    }
    return createFilesDir(new File(getApplicationInfodataDir, "code_cache"));
  }
  
  @ColorInt
  public static final int getColor(Context paramContext, @ColorRes int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return ContextCompatApi23.getColor(paramContext, paramInt);
    }
    return paramContext.getResources().getColor(paramInt);
  }
  
  public static final ColorStateList getColorStateList(Context paramContext, @ColorRes int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return ContextCompatApi23.getColorStateList(paramContext, paramInt);
    }
    return paramContext.getResources().getColorStateList(paramInt);
  }
  
  public static File getDataDir(Context paramContext)
  {
    if (BuildCompat.isAtLeastN()) {
      return ContextCompatApi24.getDataDir(paramContext);
    }
    String str = getApplicationInfodataDir;
    if (str != null) {
      return new File(str);
    }
    return null;
  }
  
  public static final Drawable getDrawable(Context paramContext, @DrawableRes int paramInt)
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 21) {
      return ContextCompatApi21.getDrawable(paramContext, paramInt);
    }
    if (i >= 16) {
      return paramContext.getResources().getDrawable(paramInt);
    }
    synchronized (sLock)
    {
      if (sTempValue == null) {
        sTempValue = new TypedValue();
      }
      paramContext.getResources().getValue(paramInt, sTempValue, true);
      int j = sTempValueresourceId;
      return paramContext.getResources().getDrawable(j);
    }
  }
  
  public static File[] getExternalCacheDirs(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return ContextCompatKitKat.getExternalCacheDirs(paramContext);
    }
    File[] arrayOfFile = new File[1];
    arrayOfFile[0] = paramContext.getExternalCacheDir();
    return arrayOfFile;
  }
  
  public static File[] getExternalFilesDirs(Context paramContext, String paramString)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return ContextCompatKitKat.getExternalFilesDirs(paramContext, paramString);
    }
    File[] arrayOfFile = new File[1];
    arrayOfFile[0] = paramContext.getExternalFilesDir(paramString);
    return arrayOfFile;
  }
  
  public static final File getNoBackupFilesDir(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return ContextCompatApi21.getNoBackupFilesDir(paramContext);
    }
    return createFilesDir(new File(getApplicationInfodataDir, "no_backup"));
  }
  
  public static File[] getObbDirs(Context paramContext)
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 19) {
      return ContextCompatKitKat.getObbDirs(paramContext);
    }
    if (i >= 11) {}
    File localFile1;
    String[] arrayOfString;
    for (File localFile2 = ContextCompatHoneycomb.getObbDir(paramContext);; localFile2 = buildPath(localFile1, arrayOfString))
    {
      return new File[] { localFile2 };
      localFile1 = Environment.getExternalStorageDirectory();
      arrayOfString = new String[3];
      arrayOfString[0] = "Android";
      arrayOfString[1] = "obb";
      arrayOfString[2] = paramContext.getPackageName();
    }
  }
  
  @Deprecated
  public static boolean isDeviceEncryptedStorage(Context paramContext)
  {
    return isDeviceProtectedStorage(paramContext);
  }
  
  public static boolean isDeviceProtectedStorage(Context paramContext)
  {
    if (BuildCompat.isAtLeastN()) {
      return ContextCompatApi24.isDeviceProtectedStorage(paramContext);
    }
    return false;
  }
  
  public static boolean startActivities(Context paramContext, Intent[] paramArrayOfIntent)
  {
    return startActivities(paramContext, paramArrayOfIntent, null);
  }
  
  public static boolean startActivities(Context paramContext, Intent[] paramArrayOfIntent, Bundle paramBundle)
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 16)
    {
      ContextCompatJellybean.startActivities(paramContext, paramArrayOfIntent, paramBundle);
      return true;
    }
    if (i >= 11)
    {
      ContextCompatHoneycomb.startActivities(paramContext, paramArrayOfIntent);
      return true;
    }
    return false;
  }
}
