package android.support.v4.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import java.util.List;
import java.util.Map;

public class ActivityCompat
  extends ContextCompat
{
  @Deprecated
  public ActivityCompat() {}
  
  private static ActivityCompat21.SharedElementCallback21 createCallback(SharedElementCallback paramSharedElementCallback)
  {
    SharedElementCallback21Impl localSharedElementCallback21Impl = null;
    if (paramSharedElementCallback != null) {
      localSharedElementCallback21Impl = new SharedElementCallback21Impl(paramSharedElementCallback);
    }
    return localSharedElementCallback21Impl;
  }
  
  private static ActivityCompatApi23.SharedElementCallback23 createCallback23(SharedElementCallback paramSharedElementCallback)
  {
    SharedElementCallback23Impl localSharedElementCallback23Impl = null;
    if (paramSharedElementCallback != null) {
      localSharedElementCallback23Impl = new SharedElementCallback23Impl(paramSharedElementCallback);
    }
    return localSharedElementCallback23Impl;
  }
  
  public static void finishAffinity(Activity paramActivity)
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      ActivityCompatJB.finishAffinity(paramActivity);
      return;
    }
    paramActivity.finish();
  }
  
  public static void finishAfterTransition(Activity paramActivity)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      ActivityCompat21.finishAfterTransition(paramActivity);
      return;
    }
    paramActivity.finish();
  }
  
  public static boolean invalidateOptionsMenu(Activity paramActivity)
  {
    if (Build.VERSION.SDK_INT >= 11)
    {
      ActivityCompatHoneycomb.invalidateOptionsMenu(paramActivity);
      return true;
    }
    return false;
  }
  
  public static void postponeEnterTransition(Activity paramActivity)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      ActivityCompat21.postponeEnterTransition(paramActivity);
    }
  }
  
  public static void requestPermissions(@NonNull final Activity paramActivity, @NonNull String[] paramArrayOfString, final int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      ActivityCompatApi23.requestPermissions(paramActivity, paramArrayOfString, paramInt);
    }
    while (!(paramActivity instanceof OnRequestPermissionsResultCallback)) {
      return;
    }
    new Handler(Looper.getMainLooper()).post(new Runnable()
    {
      public void run()
      {
        int[] arrayOfInt = new int[val$permissions.length];
        PackageManager localPackageManager = paramActivity.getPackageManager();
        String str = paramActivity.getPackageName();
        int i = val$permissions.length;
        for (int j = 0; j < i; j++) {
          arrayOfInt[j] = localPackageManager.checkPermission(val$permissions[j], str);
        }
        ((ActivityCompat.OnRequestPermissionsResultCallback)paramActivity).onRequestPermissionsResult(paramInt, val$permissions, arrayOfInt);
      }
    });
  }
  
  public static void setEnterSharedElementCallback(Activity paramActivity, SharedElementCallback paramSharedElementCallback)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      ActivityCompatApi23.setEnterSharedElementCallback(paramActivity, createCallback23(paramSharedElementCallback));
    }
    while (Build.VERSION.SDK_INT < 21) {
      return;
    }
    ActivityCompat21.setEnterSharedElementCallback(paramActivity, createCallback(paramSharedElementCallback));
  }
  
  public static void setExitSharedElementCallback(Activity paramActivity, SharedElementCallback paramSharedElementCallback)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      ActivityCompatApi23.setExitSharedElementCallback(paramActivity, createCallback23(paramSharedElementCallback));
    }
    while (Build.VERSION.SDK_INT < 21) {
      return;
    }
    ActivityCompat21.setExitSharedElementCallback(paramActivity, createCallback(paramSharedElementCallback));
  }
  
  public static boolean shouldShowRequestPermissionRationale(@NonNull Activity paramActivity, @NonNull String paramString)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return ActivityCompatApi23.shouldShowRequestPermissionRationale(paramActivity, paramString);
    }
    return false;
  }
  
  public static void startActivity(Activity paramActivity, Intent paramIntent, @Nullable Bundle paramBundle)
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      ActivityCompatJB.startActivity(paramActivity, paramIntent, paramBundle);
      return;
    }
    paramActivity.startActivity(paramIntent);
  }
  
  public static void startActivityForResult(Activity paramActivity, Intent paramIntent, int paramInt, @Nullable Bundle paramBundle)
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      ActivityCompatJB.startActivityForResult(paramActivity, paramIntent, paramInt, paramBundle);
      return;
    }
    paramActivity.startActivityForResult(paramIntent, paramInt);
  }
  
  public static void startIntentSenderForResult(Activity paramActivity, IntentSender paramIntentSender, int paramInt1, Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, @Nullable Bundle paramBundle)
    throws IntentSender.SendIntentException
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      ActivityCompatJB.startIntentSenderForResult(paramActivity, paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
      return;
    }
    paramActivity.startIntentSenderForResult(paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4);
  }
  
  public static void startPostponedEnterTransition(Activity paramActivity)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      ActivityCompat21.startPostponedEnterTransition(paramActivity);
    }
  }
  
  @Deprecated
  public Uri getReferrer(Activity paramActivity)
  {
    Uri localUri;
    if (Build.VERSION.SDK_INT >= 22) {
      localUri = ActivityCompat22.getReferrer(paramActivity);
    }
    Intent localIntent;
    do
    {
      return localUri;
      localIntent = paramActivity.getIntent();
      localUri = (Uri)localIntent.getParcelableExtra("android.intent.extra.REFERRER");
    } while (localUri != null);
    String str = localIntent.getStringExtra("android.intent.extra.REFERRER_NAME");
    if (str != null) {
      return Uri.parse(str);
    }
    return null;
  }
  
  public static abstract interface OnRequestPermissionsResultCallback
  {
    public abstract void onRequestPermissionsResult(int paramInt, @NonNull String[] paramArrayOfString, @NonNull int[] paramArrayOfInt);
  }
  
  private static class SharedElementCallback21Impl
    extends ActivityCompat21.SharedElementCallback21
  {
    private SharedElementCallback mCallback;
    
    public SharedElementCallback21Impl(SharedElementCallback paramSharedElementCallback)
    {
      mCallback = paramSharedElementCallback;
    }
    
    public Parcelable onCaptureSharedElementSnapshot(View paramView, Matrix paramMatrix, RectF paramRectF)
    {
      return mCallback.onCaptureSharedElementSnapshot(paramView, paramMatrix, paramRectF);
    }
    
    public View onCreateSnapshotView(Context paramContext, Parcelable paramParcelable)
    {
      return mCallback.onCreateSnapshotView(paramContext, paramParcelable);
    }
    
    public void onMapSharedElements(List<String> paramList, Map<String, View> paramMap)
    {
      mCallback.onMapSharedElements(paramList, paramMap);
    }
    
    public void onRejectSharedElements(List<View> paramList)
    {
      mCallback.onRejectSharedElements(paramList);
    }
    
    public void onSharedElementEnd(List<String> paramList, List<View> paramList1, List<View> paramList2)
    {
      mCallback.onSharedElementEnd(paramList, paramList1, paramList2);
    }
    
    public void onSharedElementStart(List<String> paramList, List<View> paramList1, List<View> paramList2)
    {
      mCallback.onSharedElementStart(paramList, paramList1, paramList2);
    }
  }
  
  private static class SharedElementCallback23Impl
    extends ActivityCompatApi23.SharedElementCallback23
  {
    private SharedElementCallback mCallback;
    
    public SharedElementCallback23Impl(SharedElementCallback paramSharedElementCallback)
    {
      mCallback = paramSharedElementCallback;
    }
    
    public Parcelable onCaptureSharedElementSnapshot(View paramView, Matrix paramMatrix, RectF paramRectF)
    {
      return mCallback.onCaptureSharedElementSnapshot(paramView, paramMatrix, paramRectF);
    }
    
    public View onCreateSnapshotView(Context paramContext, Parcelable paramParcelable)
    {
      return mCallback.onCreateSnapshotView(paramContext, paramParcelable);
    }
    
    public void onMapSharedElements(List<String> paramList, Map<String, View> paramMap)
    {
      mCallback.onMapSharedElements(paramList, paramMap);
    }
    
    public void onRejectSharedElements(List<View> paramList)
    {
      mCallback.onRejectSharedElements(paramList);
    }
    
    public void onSharedElementEnd(List<String> paramList, List<View> paramList1, List<View> paramList2)
    {
      mCallback.onSharedElementEnd(paramList, paramList1, paramList2);
    }
    
    public void onSharedElementStart(List<String> paramList, List<View> paramList1, List<View> paramList2)
    {
      mCallback.onSharedElementStart(paramList, paramList1, paramList2);
    }
    
    public void onSharedElementsArrived(List<String> paramList, List<View> paramList1, final ActivityCompatApi23.OnSharedElementsReadyListenerBridge paramOnSharedElementsReadyListenerBridge)
    {
      mCallback.onSharedElementsArrived(paramList, paramList1, new SharedElementCallback.OnSharedElementsReadyListener()
      {
        public void onSharedElementsReady()
        {
          paramOnSharedElementsReadyListenerBridge.onSharedElementsReady();
        }
      });
    }
  }
}
