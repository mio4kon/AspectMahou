package android.support.v4.app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.util.SparseArrayCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class FragmentActivity
  extends BaseFragmentActivityJB
  implements ActivityCompat.OnRequestPermissionsResultCallback, ActivityCompatApi23.RequestPermissionsRequestCodeValidator
{
  static final String ALLOCATED_REQUEST_INDICIES_TAG = "android:support:request_indicies";
  static final String FRAGMENTS_TAG = "android:support:fragments";
  private static final int HONEYCOMB = 11;
  static final int MAX_NUM_PENDING_FRAGMENT_ACTIVITY_RESULTS = 65534;
  static final int MSG_REALLY_STOPPED = 1;
  static final int MSG_RESUME_PENDING = 2;
  static final String NEXT_CANDIDATE_REQUEST_INDEX_TAG = "android:support:next_request_index";
  static final String REQUEST_FRAGMENT_WHO_TAG = "android:support:request_fragment_who";
  private static final String TAG = "FragmentActivity";
  boolean mCreated;
  final FragmentController mFragments = FragmentController.createController(new HostCallbacks());
  final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (what)
      {
      default: 
        super.handleMessage(paramAnonymousMessage);
      case 1: 
        do
        {
          return;
        } while (!mStopped);
        doReallyStop(false);
        return;
      }
      onResumeFragments();
      mFragments.execPendingActions();
    }
  };
  MediaControllerCompat mMediaController;
  int mNextCandidateRequestIndex;
  boolean mOptionsMenuInvalidated;
  SparseArrayCompat<String> mPendingFragmentActivityResults;
  boolean mReallyStopped;
  boolean mRequestedPermissionsFromFragment;
  boolean mResumed;
  boolean mRetaining;
  boolean mStopped;
  
  public FragmentActivity() {}
  
  private int allocateRequestIndex(Fragment paramFragment)
  {
    if (mPendingFragmentActivityResults.size() >= 65534) {
      throw new IllegalStateException("Too many pending Fragment activity results.");
    }
    while (mPendingFragmentActivityResults.indexOfKey(mNextCandidateRequestIndex) >= 0) {
      mNextCandidateRequestIndex = ((1 + mNextCandidateRequestIndex) % 65534);
    }
    int i = mNextCandidateRequestIndex;
    mPendingFragmentActivityResults.put(i, mWho);
    mNextCandidateRequestIndex = ((1 + mNextCandidateRequestIndex) % 65534);
    return i;
  }
  
  private void dumpViewHierarchy(String paramString, PrintWriter paramPrintWriter, View paramView)
  {
    paramPrintWriter.print(paramString);
    if (paramView == null) {
      paramPrintWriter.println("null");
    }
    for (;;)
    {
      return;
      paramPrintWriter.println(viewToString(paramView));
      if ((paramView instanceof ViewGroup))
      {
        ViewGroup localViewGroup = (ViewGroup)paramView;
        int i = localViewGroup.getChildCount();
        if (i > 0)
        {
          String str = paramString + "  ";
          for (int j = 0; j < i; j++) {
            dumpViewHierarchy(str, paramPrintWriter, localViewGroup.getChildAt(j));
          }
        }
      }
    }
  }
  
  private static String viewToString(View paramView)
  {
    char c1 = 'F';
    char c2 = '.';
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append(paramView.getClass().getName());
    localStringBuilder.append('{');
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(paramView)));
    localStringBuilder.append(' ');
    char c3;
    label108:
    char c4;
    label126:
    char c5;
    label143:
    char c6;
    label161:
    char c7;
    label179:
    char c8;
    label197:
    char c9;
    label215:
    label236:
    char c10;
    label253:
    int i;
    Resources localResources;
    switch (paramView.getVisibility())
    {
    default: 
      localStringBuilder.append(c2);
      if (paramView.isFocusable())
      {
        c3 = c1;
        localStringBuilder.append(c3);
        if (!paramView.isEnabled()) {
          break label534;
        }
        c4 = 'E';
        localStringBuilder.append(c4);
        if (!paramView.willNotDraw()) {
          break label540;
        }
        c5 = c2;
        localStringBuilder.append(c5);
        if (!paramView.isHorizontalScrollBarEnabled()) {
          break label547;
        }
        c6 = 'H';
        localStringBuilder.append(c6);
        if (!paramView.isVerticalScrollBarEnabled()) {
          break label553;
        }
        c7 = 'V';
        localStringBuilder.append(c7);
        if (!paramView.isClickable()) {
          break label559;
        }
        c8 = 'C';
        localStringBuilder.append(c8);
        if (!paramView.isLongClickable()) {
          break label565;
        }
        c9 = 'L';
        localStringBuilder.append(c9);
        localStringBuilder.append(' ');
        if (!paramView.isFocused()) {
          break label571;
        }
        localStringBuilder.append(c1);
        if (!paramView.isSelected()) {
          break label576;
        }
        c10 = 'S';
        localStringBuilder.append(c10);
        if (paramView.isPressed()) {
          c2 = 'P';
        }
        localStringBuilder.append(c2);
        localStringBuilder.append(' ');
        localStringBuilder.append(paramView.getLeft());
        localStringBuilder.append(',');
        localStringBuilder.append(paramView.getTop());
        localStringBuilder.append('-');
        localStringBuilder.append(paramView.getRight());
        localStringBuilder.append(',');
        localStringBuilder.append(paramView.getBottom());
        i = paramView.getId();
        if (i != -1)
        {
          localStringBuilder.append(" #");
          localStringBuilder.append(Integer.toHexString(i));
          localResources = paramView.getResources();
          if ((i != 0) && (localResources != null)) {
            switch (0xFF000000 & i)
            {
            }
          }
        }
      }
      break;
    }
    for (;;)
    {
      try
      {
        str1 = localResources.getResourcePackageName(i);
        String str2 = localResources.getResourceTypeName(i);
        String str3 = localResources.getResourceEntryName(i);
        localStringBuilder.append(" ");
        localStringBuilder.append(str1);
        localStringBuilder.append(":");
        localStringBuilder.append(str2);
        localStringBuilder.append("/");
        localStringBuilder.append(str3);
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        String str1;
        label534:
        label540:
        label547:
        label553:
        label559:
        label565:
        label571:
        label576:
        continue;
      }
      localStringBuilder.append("}");
      return localStringBuilder.toString();
      localStringBuilder.append('V');
      break;
      localStringBuilder.append('I');
      break;
      localStringBuilder.append('G');
      break;
      c3 = c2;
      break label108;
      c4 = c2;
      break label126;
      c5 = 'D';
      break label143;
      c6 = c2;
      break label161;
      c7 = c2;
      break label179;
      c8 = c2;
      break label197;
      c9 = c2;
      break label215;
      c1 = c2;
      break label236;
      c10 = c2;
      break label253;
      str1 = "app";
      continue;
      str1 = "android";
    }
  }
  
  final View dispatchFragmentsOnCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    return mFragments.onCreateView(paramView, paramString, paramContext, paramAttributeSet);
  }
  
  void doReallyStop(boolean paramBoolean)
  {
    if (!mReallyStopped)
    {
      mReallyStopped = true;
      mRetaining = paramBoolean;
      mHandler.removeMessages(1);
      onReallyStop();
    }
    while (!paramBoolean) {
      return;
    }
    mFragments.doLoaderStart();
    mFragments.doLoaderStop(true);
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (Build.VERSION.SDK_INT >= 11) {}
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("Local FragmentActivity ");
    paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this)));
    paramPrintWriter.println(" State:");
    String str = paramString + "  ";
    paramPrintWriter.print(str);
    paramPrintWriter.print("mCreated=");
    paramPrintWriter.print(mCreated);
    paramPrintWriter.print("mResumed=");
    paramPrintWriter.print(mResumed);
    paramPrintWriter.print(" mStopped=");
    paramPrintWriter.print(mStopped);
    paramPrintWriter.print(" mReallyStopped=");
    paramPrintWriter.println(mReallyStopped);
    mFragments.dumpLoaders(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    mFragments.getSupportFragmentManager().dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("View Hierarchy:");
    dumpViewHierarchy(paramString + "  ", paramPrintWriter, getWindow().getDecorView());
  }
  
  public Object getLastCustomNonConfigurationInstance()
  {
    NonConfigurationInstances localNonConfigurationInstances = (NonConfigurationInstances)getLastNonConfigurationInstance();
    if (localNonConfigurationInstances != null) {
      return custom;
    }
    return null;
  }
  
  public FragmentManager getSupportFragmentManager()
  {
    return mFragments.getSupportFragmentManager();
  }
  
  public LoaderManager getSupportLoaderManager()
  {
    return mFragments.getSupportLoaderManager();
  }
  
  public final MediaControllerCompat getSupportMediaController()
  {
    return mMediaController;
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    mFragments.noteStateNotSaved();
    int i = paramInt1 >> 16;
    if (i != 0)
    {
      int j = i - 1;
      String str = (String)mPendingFragmentActivityResults.get(j);
      mPendingFragmentActivityResults.remove(j);
      if (str == null)
      {
        Log.w("FragmentActivity", "Activity result delivered for unknown Fragment.");
        return;
      }
      Fragment localFragment = mFragments.findFragmentByWho(str);
      if (localFragment == null)
      {
        Log.w("FragmentActivity", "Activity result no fragment exists for who: " + str);
        return;
      }
      localFragment.onActivityResult(0xFFFF & paramInt1, paramInt2, paramIntent);
      return;
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void onAttachFragment(Fragment paramFragment) {}
  
  public void onBackPressed()
  {
    if (!mFragments.getSupportFragmentManager().popBackStackImmediate()) {
      super.onBackPressed();
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    mFragments.dispatchConfigurationChanged(paramConfiguration);
  }
  
  protected void onCreate(@Nullable Bundle paramBundle)
  {
    mFragments.attachHost(null);
    super.onCreate(paramBundle);
    NonConfigurationInstances localNonConfigurationInstances = (NonConfigurationInstances)getLastNonConfigurationInstance();
    if (localNonConfigurationInstances != null) {
      mFragments.restoreLoaderNonConfig(loaders);
    }
    int[] arrayOfInt;
    String[] arrayOfString;
    if (paramBundle != null)
    {
      Parcelable localParcelable = paramBundle.getParcelable("android:support:fragments");
      FragmentController localFragmentController = mFragments;
      FragmentManagerNonConfig localFragmentManagerNonConfig = null;
      if (localNonConfigurationInstances != null) {
        localFragmentManagerNonConfig = fragments;
      }
      localFragmentController.restoreAllState(localParcelable, localFragmentManagerNonConfig);
      if (paramBundle.containsKey("android:support:next_request_index"))
      {
        mNextCandidateRequestIndex = paramBundle.getInt("android:support:next_request_index");
        arrayOfInt = paramBundle.getIntArray("android:support:request_indicies");
        arrayOfString = paramBundle.getStringArray("android:support:request_fragment_who");
        if ((arrayOfInt != null) && (arrayOfString != null) && (arrayOfInt.length == arrayOfString.length)) {
          break label168;
        }
        Log.w("FragmentActivity", "Invalid requestCode mapping in savedInstanceState.");
      }
    }
    for (;;)
    {
      if (mPendingFragmentActivityResults == null)
      {
        mPendingFragmentActivityResults = new SparseArrayCompat();
        mNextCandidateRequestIndex = 0;
      }
      mFragments.dispatchCreate();
      return;
      label168:
      mPendingFragmentActivityResults = new SparseArrayCompat(arrayOfInt.length);
      for (int i = 0; i < arrayOfInt.length; i++) {
        mPendingFragmentActivityResults.put(arrayOfInt[i], arrayOfString[i]);
      }
    }
  }
  
  public boolean onCreatePanelMenu(int paramInt, Menu paramMenu)
  {
    if (paramInt == 0)
    {
      boolean bool = super.onCreatePanelMenu(paramInt, paramMenu) | mFragments.dispatchCreateOptionsMenu(paramMenu, getMenuInflater());
      if (Build.VERSION.SDK_INT >= 11) {
        return bool;
      }
      return true;
    }
    return super.onCreatePanelMenu(paramInt, paramMenu);
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    doReallyStop(false);
    mFragments.dispatchDestroy();
    mFragments.doLoaderDestroy();
  }
  
  public void onLowMemory()
  {
    super.onLowMemory();
    mFragments.dispatchLowMemory();
  }
  
  public boolean onMenuItemSelected(int paramInt, MenuItem paramMenuItem)
  {
    if (super.onMenuItemSelected(paramInt, paramMenuItem)) {
      return true;
    }
    switch (paramInt)
    {
    default: 
      return false;
    case 0: 
      return mFragments.dispatchOptionsItemSelected(paramMenuItem);
    }
    return mFragments.dispatchContextItemSelected(paramMenuItem);
  }
  
  @CallSuper
  public void onMultiWindowModeChanged(boolean paramBoolean)
  {
    mFragments.dispatchMultiWindowModeChanged(paramBoolean);
  }
  
  protected void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    mFragments.noteStateNotSaved();
  }
  
  public void onPanelClosed(int paramInt, Menu paramMenu)
  {
    switch (paramInt)
    {
    }
    for (;;)
    {
      super.onPanelClosed(paramInt, paramMenu);
      return;
      mFragments.dispatchOptionsMenuClosed(paramMenu);
    }
  }
  
  protected void onPause()
  {
    super.onPause();
    mResumed = false;
    if (mHandler.hasMessages(2))
    {
      mHandler.removeMessages(2);
      onResumeFragments();
    }
    mFragments.dispatchPause();
  }
  
  @CallSuper
  public void onPictureInPictureModeChanged(boolean paramBoolean)
  {
    mFragments.dispatchPictureInPictureModeChanged(paramBoolean);
  }
  
  protected void onPostResume()
  {
    super.onPostResume();
    mHandler.removeMessages(2);
    onResumeFragments();
    mFragments.execPendingActions();
  }
  
  protected boolean onPrepareOptionsPanel(View paramView, Menu paramMenu)
  {
    return super.onPreparePanel(0, paramView, paramMenu);
  }
  
  public boolean onPreparePanel(int paramInt, View paramView, Menu paramMenu)
  {
    if ((paramInt == 0) && (paramMenu != null))
    {
      if (mOptionsMenuInvalidated)
      {
        mOptionsMenuInvalidated = false;
        paramMenu.clear();
        onCreatePanelMenu(paramInt, paramMenu);
      }
      return onPrepareOptionsPanel(paramView, paramMenu) | mFragments.dispatchPrepareOptionsMenu(paramMenu);
    }
    return super.onPreparePanel(paramInt, paramView, paramMenu);
  }
  
  void onReallyStop()
  {
    mFragments.doLoaderStop(mRetaining);
    mFragments.dispatchReallyStop();
  }
  
  public void onRequestPermissionsResult(int paramInt, @NonNull String[] paramArrayOfString, @NonNull int[] paramArrayOfInt)
  {
    int i = 0xFFFF & paramInt >> 16;
    String str;
    if (i != 0)
    {
      int j = i - 1;
      str = (String)mPendingFragmentActivityResults.get(j);
      mPendingFragmentActivityResults.remove(j);
      if (str == null) {
        Log.w("FragmentActivity", "Activity result delivered for unknown Fragment.");
      }
    }
    else
    {
      return;
    }
    Fragment localFragment = mFragments.findFragmentByWho(str);
    if (localFragment == null)
    {
      Log.w("FragmentActivity", "Activity result no fragment exists for who: " + str);
      return;
    }
    localFragment.onRequestPermissionsResult(paramInt & 0xFFFF, paramArrayOfString, paramArrayOfInt);
  }
  
  protected void onResume()
  {
    super.onResume();
    mHandler.sendEmptyMessage(2);
    mResumed = true;
    mFragments.execPendingActions();
  }
  
  protected void onResumeFragments()
  {
    mFragments.dispatchResume();
  }
  
  public Object onRetainCustomNonConfigurationInstance()
  {
    return null;
  }
  
  public final Object onRetainNonConfigurationInstance()
  {
    if (mStopped) {
      doReallyStop(true);
    }
    Object localObject = onRetainCustomNonConfigurationInstance();
    FragmentManagerNonConfig localFragmentManagerNonConfig = mFragments.retainNestedNonConfig();
    SimpleArrayMap localSimpleArrayMap = mFragments.retainLoaderNonConfig();
    if ((localFragmentManagerNonConfig == null) && (localSimpleArrayMap == null) && (localObject == null)) {
      return null;
    }
    NonConfigurationInstances localNonConfigurationInstances = new NonConfigurationInstances();
    custom = localObject;
    fragments = localFragmentManagerNonConfig;
    loaders = localSimpleArrayMap;
    return localNonConfigurationInstances;
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    Parcelable localParcelable = mFragments.saveAllState();
    if (localParcelable != null) {
      paramBundle.putParcelable("android:support:fragments", localParcelable);
    }
    if (mPendingFragmentActivityResults.size() > 0)
    {
      paramBundle.putInt("android:support:next_request_index", mNextCandidateRequestIndex);
      int[] arrayOfInt = new int[mPendingFragmentActivityResults.size()];
      String[] arrayOfString = new String[mPendingFragmentActivityResults.size()];
      for (int i = 0; i < mPendingFragmentActivityResults.size(); i++)
      {
        arrayOfInt[i] = mPendingFragmentActivityResults.keyAt(i);
        arrayOfString[i] = ((String)mPendingFragmentActivityResults.valueAt(i));
      }
      paramBundle.putIntArray("android:support:request_indicies", arrayOfInt);
      paramBundle.putStringArray("android:support:request_fragment_who", arrayOfString);
    }
  }
  
  protected void onStart()
  {
    super.onStart();
    mStopped = false;
    mReallyStopped = false;
    mHandler.removeMessages(1);
    if (!mCreated)
    {
      mCreated = true;
      mFragments.dispatchActivityCreated();
    }
    mFragments.noteStateNotSaved();
    mFragments.execPendingActions();
    mFragments.doLoaderStart();
    mFragments.dispatchStart();
    mFragments.reportLoaderStart();
  }
  
  public void onStateNotSaved()
  {
    mFragments.noteStateNotSaved();
  }
  
  protected void onStop()
  {
    super.onStop();
    mStopped = true;
    mHandler.sendEmptyMessage(1);
    mFragments.dispatchStop();
  }
  
  void requestPermissionsFromFragment(Fragment paramFragment, String[] paramArrayOfString, int paramInt)
  {
    if (paramInt == -1)
    {
      ActivityCompat.requestPermissions(this, paramArrayOfString, paramInt);
      return;
    }
    checkForValidRequestCode(paramInt);
    try
    {
      mRequestedPermissionsFromFragment = true;
      ActivityCompat.requestPermissions(this, paramArrayOfString, (1 + allocateRequestIndex(paramFragment) << 16) + (0xFFFF & paramInt));
      return;
    }
    finally
    {
      mRequestedPermissionsFromFragment = false;
    }
  }
  
  public void setEnterSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    ActivityCompat.setEnterSharedElementCallback(this, paramSharedElementCallback);
  }
  
  public void setExitSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    ActivityCompat.setExitSharedElementCallback(this, paramSharedElementCallback);
  }
  
  public final void setSupportMediaController(MediaControllerCompat paramMediaControllerCompat)
  {
    mMediaController = paramMediaControllerCompat;
    if (Build.VERSION.SDK_INT >= 21) {
      ActivityCompat21.setMediaController(this, paramMediaControllerCompat.getMediaController());
    }
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    if ((!mStartedActivityFromFragment) && (paramInt != -1)) {
      checkForValidRequestCode(paramInt);
    }
    super.startActivityForResult(paramIntent, paramInt);
  }
  
  public void startActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt)
  {
    startActivityFromFragment(paramFragment, paramIntent, paramInt, null);
  }
  
  public void startActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt, @Nullable Bundle paramBundle)
  {
    mStartedActivityFromFragment = true;
    if (paramInt == -1) {}
    try
    {
      ActivityCompat.startActivityForResult(this, paramIntent, -1, paramBundle);
      return;
    }
    finally
    {
      mStartedActivityFromFragment = false;
    }
    checkForValidRequestCode(paramInt);
    ActivityCompat.startActivityForResult(this, paramIntent, (1 + allocateRequestIndex(paramFragment) << 16) + (0xFFFF & paramInt), paramBundle);
    mStartedActivityFromFragment = false;
  }
  
  public void startIntentSenderFromFragment(Fragment paramFragment, IntentSender paramIntentSender, int paramInt1, @Nullable Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle)
    throws IntentSender.SendIntentException
  {
    mStartedIntentSenderFromFragment = true;
    if (paramInt1 == -1) {}
    try
    {
      ActivityCompat.startIntentSenderForResult(this, paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
      return;
    }
    finally
    {
      mStartedIntentSenderFromFragment = false;
    }
    checkForValidRequestCode(paramInt1);
    ActivityCompat.startIntentSenderForResult(this, paramIntentSender, (1 + allocateRequestIndex(paramFragment) << 16) + (0xFFFF & paramInt1), paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
    mStartedIntentSenderFromFragment = false;
  }
  
  public void supportFinishAfterTransition()
  {
    ActivityCompat.finishAfterTransition(this);
  }
  
  public void supportInvalidateOptionsMenu()
  {
    if (Build.VERSION.SDK_INT >= 11)
    {
      ActivityCompatHoneycomb.invalidateOptionsMenu(this);
      return;
    }
    mOptionsMenuInvalidated = true;
  }
  
  public void supportPostponeEnterTransition()
  {
    ActivityCompat.postponeEnterTransition(this);
  }
  
  public void supportStartPostponedEnterTransition()
  {
    ActivityCompat.startPostponedEnterTransition(this);
  }
  
  public final void validateRequestPermissionsRequestCode(int paramInt)
  {
    if ((!mRequestedPermissionsFromFragment) && (paramInt != -1)) {
      checkForValidRequestCode(paramInt);
    }
  }
  
  class HostCallbacks
    extends FragmentHostCallback<FragmentActivity>
  {
    public HostCallbacks()
    {
      super();
    }
    
    public void onAttachFragment(Fragment paramFragment)
    {
      FragmentActivity.this.onAttachFragment(paramFragment);
    }
    
    public void onDump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    
    @Nullable
    public View onFindViewById(int paramInt)
    {
      return findViewById(paramInt);
    }
    
    public FragmentActivity onGetHost()
    {
      return FragmentActivity.this;
    }
    
    public LayoutInflater onGetLayoutInflater()
    {
      return getLayoutInflater().cloneInContext(FragmentActivity.this);
    }
    
    public int onGetWindowAnimations()
    {
      Window localWindow = getWindow();
      if (localWindow == null) {
        return 0;
      }
      return getAttributeswindowAnimations;
    }
    
    public boolean onHasView()
    {
      Window localWindow = getWindow();
      return (localWindow != null) && (localWindow.peekDecorView() != null);
    }
    
    public boolean onHasWindowAnimations()
    {
      return getWindow() != null;
    }
    
    public void onRequestPermissionsFromFragment(@NonNull Fragment paramFragment, @NonNull String[] paramArrayOfString, int paramInt)
    {
      requestPermissionsFromFragment(paramFragment, paramArrayOfString, paramInt);
    }
    
    public boolean onShouldSaveFragmentState(Fragment paramFragment)
    {
      return !isFinishing();
    }
    
    public boolean onShouldShowRequestPermissionRationale(@NonNull String paramString)
    {
      return ActivityCompat.shouldShowRequestPermissionRationale(FragmentActivity.this, paramString);
    }
    
    public void onStartActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt)
    {
      startActivityFromFragment(paramFragment, paramIntent, paramInt);
    }
    
    public void onStartActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt, @Nullable Bundle paramBundle)
    {
      startActivityFromFragment(paramFragment, paramIntent, paramInt, paramBundle);
    }
    
    public void onStartIntentSenderFromFragment(Fragment paramFragment, IntentSender paramIntentSender, int paramInt1, @Nullable Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle)
      throws IntentSender.SendIntentException
    {
      startIntentSenderFromFragment(paramFragment, paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
    }
    
    public void onSupportInvalidateOptionsMenu()
    {
      supportInvalidateOptionsMenu();
    }
  }
  
  static final class NonConfigurationInstances
  {
    Object custom;
    FragmentManagerNonConfig fragments;
    SimpleArrayMap<String, LoaderManager> loaders;
    
    NonConfigurationInstances() {}
  }
}
