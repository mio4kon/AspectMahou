package android.support.v4.app;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.v4.os.BuildCompat;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.LogWriter;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class FragmentManagerImpl
  extends FragmentManager
  implements LayoutInflaterFactory
{
  static final Interpolator ACCELERATE_CUBIC = new AccelerateInterpolator(1.5F);
  static final Interpolator ACCELERATE_QUINT;
  static final int ANIM_DUR = 220;
  public static final int ANIM_STYLE_CLOSE_ENTER = 3;
  public static final int ANIM_STYLE_CLOSE_EXIT = 4;
  public static final int ANIM_STYLE_FADE_ENTER = 5;
  public static final int ANIM_STYLE_FADE_EXIT = 6;
  public static final int ANIM_STYLE_OPEN_ENTER = 1;
  public static final int ANIM_STYLE_OPEN_EXIT = 2;
  static boolean DEBUG = false;
  static final Interpolator DECELERATE_CUBIC;
  static final Interpolator DECELERATE_QUINT;
  static final boolean HONEYCOMB = false;
  static final String TAG = "FragmentManager";
  static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
  static final String TARGET_STATE_TAG = "android:target_state";
  static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
  static final String VIEW_STATE_TAG = "android:view_state";
  static Field sAnimationListenerField;
  ArrayList<Fragment> mActive;
  ArrayList<Fragment> mAdded;
  ArrayList<Integer> mAvailBackStackIndices;
  ArrayList<Integer> mAvailIndices;
  ArrayList<BackStackRecord> mBackStack;
  ArrayList<FragmentManager.OnBackStackChangedListener> mBackStackChangeListeners;
  ArrayList<BackStackRecord> mBackStackIndices;
  FragmentContainer mContainer;
  FragmentController mController;
  ArrayList<Fragment> mCreatedMenus;
  int mCurState = 0;
  boolean mDestroyed;
  Runnable mExecCommit = new Runnable()
  {
    public void run()
    {
      execPendingActions();
    }
  };
  boolean mExecutingActions;
  boolean mHavePendingDeferredStart;
  FragmentHostCallback mHost;
  boolean mNeedMenuInvalidate;
  String mNoTransactionsBecause;
  Fragment mParent;
  ArrayList<Runnable> mPendingActions;
  SparseArray<Parcelable> mStateArray = null;
  Bundle mStateBundle = null;
  boolean mStateSaved;
  Runnable[] mTmpActions;
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    boolean bool = false;
    if (i >= 11) {
      bool = true;
    }
    HONEYCOMB = bool;
    sAnimationListenerField = null;
    DECELERATE_QUINT = new DecelerateInterpolator(2.5F);
    DECELERATE_CUBIC = new DecelerateInterpolator(1.5F);
    ACCELERATE_QUINT = new AccelerateInterpolator(2.5F);
  }
  
  FragmentManagerImpl() {}
  
  private void checkStateLoss()
  {
    if (mStateSaved) {
      throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
    }
    if (mNoTransactionsBecause != null) {
      throw new IllegalStateException("Can not perform this action inside of " + mNoTransactionsBecause);
    }
  }
  
  static Animation makeFadeAnimation(Context paramContext, float paramFloat1, float paramFloat2)
  {
    AlphaAnimation localAlphaAnimation = new AlphaAnimation(paramFloat1, paramFloat2);
    localAlphaAnimation.setInterpolator(DECELERATE_CUBIC);
    localAlphaAnimation.setDuration(220L);
    return localAlphaAnimation;
  }
  
  static Animation makeOpenCloseAnimation(Context paramContext, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    AnimationSet localAnimationSet = new AnimationSet(false);
    ScaleAnimation localScaleAnimation = new ScaleAnimation(paramFloat1, paramFloat2, paramFloat1, paramFloat2, 1, 0.5F, 1, 0.5F);
    localScaleAnimation.setInterpolator(DECELERATE_QUINT);
    localScaleAnimation.setDuration(220L);
    localAnimationSet.addAnimation(localScaleAnimation);
    AlphaAnimation localAlphaAnimation = new AlphaAnimation(paramFloat3, paramFloat4);
    localAlphaAnimation.setInterpolator(DECELERATE_CUBIC);
    localAlphaAnimation.setDuration(220L);
    localAnimationSet.addAnimation(localAlphaAnimation);
    return localAnimationSet;
  }
  
  static boolean modifiesAlpha(Animation paramAnimation)
  {
    if ((paramAnimation instanceof AlphaAnimation)) {
      return true;
    }
    if ((paramAnimation instanceof AnimationSet))
    {
      List localList = ((AnimationSet)paramAnimation).getAnimations();
      for (int i = 0;; i++)
      {
        if (i >= localList.size()) {
          break label55;
        }
        if ((localList.get(i) instanceof AlphaAnimation)) {
          break;
        }
      }
    }
    label55:
    return false;
  }
  
  public static int reverseTransit(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 4097: 
      return 8194;
    case 8194: 
      return 4097;
    }
    return 4099;
  }
  
  private void setHWLayerAnimListenerIfAlpha(View paramView, Animation paramAnimation)
  {
    if ((paramView == null) || (paramAnimation == null)) {}
    while (!shouldRunOnHWLayer(paramView, paramAnimation)) {
      return;
    }
    try
    {
      if (sAnimationListenerField == null)
      {
        sAnimationListenerField = Animation.class.getDeclaredField("mListener");
        sAnimationListenerField.setAccessible(true);
      }
      localAnimationListener = (Animation.AnimationListener)sAnimationListenerField.get(paramAnimation);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      for (;;)
      {
        Log.e("FragmentManager", "No field with the name mListener is found in Animation class", localNoSuchFieldException);
        localAnimationListener = null;
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;)
      {
        Log.e("FragmentManager", "Cannot access Animation's mListener field", localIllegalAccessException);
        Animation.AnimationListener localAnimationListener = null;
      }
    }
    ViewCompat.setLayerType(paramView, 2, null);
    paramAnimation.setAnimationListener(new AnimateOnHWLayerIfNeededListener(paramView, paramAnimation, localAnimationListener));
  }
  
  static boolean shouldRunOnHWLayer(View paramView, Animation paramAnimation)
  {
    return (Build.VERSION.SDK_INT >= 19) && (ViewCompat.getLayerType(paramView) == 0) && (ViewCompat.hasOverlappingRendering(paramView)) && (modifiesAlpha(paramAnimation));
  }
  
  private void throwException(RuntimeException paramRuntimeException)
  {
    Log.e("FragmentManager", paramRuntimeException.getMessage());
    Log.e("FragmentManager", "Activity state:");
    PrintWriter localPrintWriter = new PrintWriter(new LogWriter("FragmentManager"));
    if (mHost != null) {}
    for (;;)
    {
      try
      {
        mHost.onDump("  ", null, localPrintWriter, new String[0]);
        throw paramRuntimeException;
      }
      catch (Exception localException2)
      {
        Log.e("FragmentManager", "Failed dumping state", localException2);
        continue;
      }
      try
      {
        dump("  ", null, localPrintWriter, new String[0]);
      }
      catch (Exception localException1)
      {
        Log.e("FragmentManager", "Failed dumping state", localException1);
      }
    }
  }
  
  public static int transitToStyleIndex(int paramInt, boolean paramBoolean)
  {
    switch (paramInt)
    {
    default: 
      return -1;
    case 4097: 
      if (paramBoolean) {
        return 1;
      }
      return 2;
    case 8194: 
      if (paramBoolean) {
        return 3;
      }
      return 4;
    }
    if (paramBoolean) {
      return 5;
    }
    return 6;
  }
  
  void addBackStackState(BackStackRecord paramBackStackRecord)
  {
    if (mBackStack == null) {
      mBackStack = new ArrayList();
    }
    mBackStack.add(paramBackStackRecord);
    reportBackStackChanged();
  }
  
  public void addFragment(Fragment paramFragment, boolean paramBoolean)
  {
    if (mAdded == null) {
      mAdded = new ArrayList();
    }
    if (DEBUG) {
      Log.v("FragmentManager", "add: " + paramFragment);
    }
    makeActive(paramFragment);
    if (!mDetached)
    {
      if (mAdded.contains(paramFragment)) {
        throw new IllegalStateException("Fragment already added: " + paramFragment);
      }
      mAdded.add(paramFragment);
      mAdded = true;
      mRemoving = false;
      if ((mHasMenu) && (mMenuVisible)) {
        mNeedMenuInvalidate = true;
      }
      if (paramBoolean) {
        moveToState(paramFragment);
      }
    }
  }
  
  public void addOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener paramOnBackStackChangedListener)
  {
    if (mBackStackChangeListeners == null) {
      mBackStackChangeListeners = new ArrayList();
    }
    mBackStackChangeListeners.add(paramOnBackStackChangedListener);
  }
  
  public int allocBackStackIndex(BackStackRecord paramBackStackRecord)
  {
    try
    {
      if ((mAvailBackStackIndices == null) || (mAvailBackStackIndices.size() <= 0))
      {
        if (mBackStackIndices == null) {
          mBackStackIndices = new ArrayList();
        }
        int i = mBackStackIndices.size();
        if (DEBUG) {
          Log.v("FragmentManager", "Setting back stack index " + i + " to " + paramBackStackRecord);
        }
        mBackStackIndices.add(paramBackStackRecord);
        return i;
      }
      int j = ((Integer)mAvailBackStackIndices.remove(-1 + mAvailBackStackIndices.size())).intValue();
      if (DEBUG) {
        Log.v("FragmentManager", "Adding back stack index " + j + " with " + paramBackStackRecord);
      }
      mBackStackIndices.set(j, paramBackStackRecord);
      return j;
    }
    finally {}
  }
  
  public void attachController(FragmentHostCallback paramFragmentHostCallback, FragmentContainer paramFragmentContainer, Fragment paramFragment)
  {
    if (mHost != null) {
      throw new IllegalStateException("Already attached");
    }
    mHost = paramFragmentHostCallback;
    mContainer = paramFragmentContainer;
    mParent = paramFragment;
  }
  
  public void attachFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.v("FragmentManager", "attach: " + paramFragment);
    }
    if (mDetached)
    {
      mDetached = false;
      if (!mAdded)
      {
        if (mAdded == null) {
          mAdded = new ArrayList();
        }
        if (mAdded.contains(paramFragment)) {
          throw new IllegalStateException("Fragment already added: " + paramFragment);
        }
        if (DEBUG) {
          Log.v("FragmentManager", "add from attach: " + paramFragment);
        }
        mAdded.add(paramFragment);
        mAdded = true;
        if ((mHasMenu) && (mMenuVisible)) {
          mNeedMenuInvalidate = true;
        }
        moveToState(paramFragment, mCurState, paramInt1, paramInt2, false);
      }
    }
  }
  
  public FragmentTransaction beginTransaction()
  {
    return new BackStackRecord(this);
  }
  
  public void detachFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.v("FragmentManager", "detach: " + paramFragment);
    }
    if (!mDetached)
    {
      mDetached = true;
      if (mAdded)
      {
        if (mAdded != null)
        {
          if (DEBUG) {
            Log.v("FragmentManager", "remove from detach: " + paramFragment);
          }
          mAdded.remove(paramFragment);
        }
        if ((mHasMenu) && (mMenuVisible)) {
          mNeedMenuInvalidate = true;
        }
        mAdded = false;
        moveToState(paramFragment, 1, paramInt1, paramInt2, false);
      }
    }
  }
  
  public void dispatchActivityCreated()
  {
    mStateSaved = false;
    moveToState(2, false);
  }
  
  public void dispatchConfigurationChanged(Configuration paramConfiguration)
  {
    if (mAdded != null) {
      for (int i = 0; i < mAdded.size(); i++)
      {
        Fragment localFragment = (Fragment)mAdded.get(i);
        if (localFragment != null) {
          localFragment.performConfigurationChanged(paramConfiguration);
        }
      }
    }
  }
  
  public boolean dispatchContextItemSelected(MenuItem paramMenuItem)
  {
    if (mAdded != null) {
      for (int i = 0; i < mAdded.size(); i++)
      {
        Fragment localFragment = (Fragment)mAdded.get(i);
        if ((localFragment != null) && (localFragment.performContextItemSelected(paramMenuItem))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public void dispatchCreate()
  {
    mStateSaved = false;
    moveToState(1, false);
  }
  
  public boolean dispatchCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    ArrayList localArrayList1 = mAdded;
    ArrayList localArrayList2 = null;
    boolean bool = false;
    if (localArrayList1 != null) {
      for (int j = 0; j < mAdded.size(); j++)
      {
        Fragment localFragment2 = (Fragment)mAdded.get(j);
        if ((localFragment2 != null) && (localFragment2.performCreateOptionsMenu(paramMenu, paramMenuInflater)))
        {
          bool = true;
          if (localArrayList2 == null) {
            localArrayList2 = new ArrayList();
          }
          localArrayList2.add(localFragment2);
        }
      }
    }
    if (mCreatedMenus != null) {
      for (int i = 0; i < mCreatedMenus.size(); i++)
      {
        Fragment localFragment1 = (Fragment)mCreatedMenus.get(i);
        if ((localArrayList2 == null) || (!localArrayList2.contains(localFragment1))) {
          localFragment1.onDestroyOptionsMenu();
        }
      }
    }
    mCreatedMenus = localArrayList2;
    return bool;
  }
  
  public void dispatchDestroy()
  {
    mDestroyed = true;
    execPendingActions();
    moveToState(0, false);
    mHost = null;
    mContainer = null;
    mParent = null;
  }
  
  public void dispatchDestroyView()
  {
    moveToState(1, false);
  }
  
  public void dispatchLowMemory()
  {
    if (mAdded != null) {
      for (int i = 0; i < mAdded.size(); i++)
      {
        Fragment localFragment = (Fragment)mAdded.get(i);
        if (localFragment != null) {
          localFragment.performLowMemory();
        }
      }
    }
  }
  
  public void dispatchMultiWindowModeChanged(boolean paramBoolean)
  {
    if (mAdded == null) {}
    for (;;)
    {
      return;
      for (int i = -1 + mAdded.size(); i >= 0; i--)
      {
        Fragment localFragment = (Fragment)mAdded.get(i);
        if (localFragment != null) {
          localFragment.performMultiWindowModeChanged(paramBoolean);
        }
      }
    }
  }
  
  public boolean dispatchOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (mAdded != null) {
      for (int i = 0; i < mAdded.size(); i++)
      {
        Fragment localFragment = (Fragment)mAdded.get(i);
        if ((localFragment != null) && (localFragment.performOptionsItemSelected(paramMenuItem))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public void dispatchOptionsMenuClosed(Menu paramMenu)
  {
    if (mAdded != null) {
      for (int i = 0; i < mAdded.size(); i++)
      {
        Fragment localFragment = (Fragment)mAdded.get(i);
        if (localFragment != null) {
          localFragment.performOptionsMenuClosed(paramMenu);
        }
      }
    }
  }
  
  public void dispatchPause()
  {
    moveToState(4, false);
  }
  
  public void dispatchPictureInPictureModeChanged(boolean paramBoolean)
  {
    if (mAdded == null) {}
    for (;;)
    {
      return;
      for (int i = -1 + mAdded.size(); i >= 0; i--)
      {
        Fragment localFragment = (Fragment)mAdded.get(i);
        if (localFragment != null) {
          localFragment.performPictureInPictureModeChanged(paramBoolean);
        }
      }
    }
  }
  
  public boolean dispatchPrepareOptionsMenu(Menu paramMenu)
  {
    ArrayList localArrayList = mAdded;
    boolean bool = false;
    if (localArrayList != null) {
      for (int i = 0; i < mAdded.size(); i++)
      {
        Fragment localFragment = (Fragment)mAdded.get(i);
        if ((localFragment != null) && (localFragment.performPrepareOptionsMenu(paramMenu))) {
          bool = true;
        }
      }
    }
    return bool;
  }
  
  public void dispatchReallyStop()
  {
    moveToState(2, false);
  }
  
  public void dispatchResume()
  {
    mStateSaved = false;
    moveToState(5, false);
  }
  
  public void dispatchStart()
  {
    mStateSaved = false;
    moveToState(4, false);
  }
  
  public void dispatchStop()
  {
    mStateSaved = true;
    moveToState(3, false);
  }
  
  void doPendingDeferredStart()
  {
    if (mHavePendingDeferredStart)
    {
      boolean bool = false;
      for (int i = 0; i < mActive.size(); i++)
      {
        Fragment localFragment = (Fragment)mActive.get(i);
        if ((localFragment != null) && (mLoaderManager != null)) {
          bool |= mLoaderManager.hasRunningLoaders();
        }
      }
      if (!bool)
      {
        mHavePendingDeferredStart = false;
        startPendingDeferredFragments();
      }
    }
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    String str = paramString + "    ";
    if (mActive != null)
    {
      int i6 = mActive.size();
      if (i6 > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("Active Fragments in ");
        paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this)));
        paramPrintWriter.println(":");
        for (int i7 = 0; i7 < i6; i7++)
        {
          Fragment localFragment3 = (Fragment)mActive.get(i7);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i7);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(localFragment3);
          if (localFragment3 != null) {
            localFragment3.dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
          }
        }
      }
    }
    if (mAdded != null)
    {
      int i4 = mAdded.size();
      if (i4 > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Added Fragments:");
        for (int i5 = 0; i5 < i4; i5++)
        {
          Fragment localFragment2 = (Fragment)mAdded.get(i5);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i5);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(localFragment2.toString());
        }
      }
    }
    if (mCreatedMenus != null)
    {
      int i2 = mCreatedMenus.size();
      if (i2 > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Fragments Created Menus:");
        for (int i3 = 0; i3 < i2; i3++)
        {
          Fragment localFragment1 = (Fragment)mCreatedMenus.get(i3);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i3);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(localFragment1.toString());
        }
      }
    }
    if (mBackStack != null)
    {
      int n = mBackStack.size();
      if (n > 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("Back Stack:");
        for (int i1 = 0; i1 < n; i1++)
        {
          BackStackRecord localBackStackRecord2 = (BackStackRecord)mBackStack.get(i1);
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(i1);
          paramPrintWriter.print(": ");
          paramPrintWriter.println(localBackStackRecord2.toString());
          localBackStackRecord2.dump(str, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
        }
      }
    }
    try
    {
      if (mBackStackIndices != null)
      {
        int k = mBackStackIndices.size();
        if (k > 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Back Stack Indices:");
          for (int m = 0; m < k; m++)
          {
            BackStackRecord localBackStackRecord1 = (BackStackRecord)mBackStackIndices.get(m);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(m);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(localBackStackRecord1);
          }
        }
      }
      if ((mAvailBackStackIndices != null) && (mAvailBackStackIndices.size() > 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mAvailBackStackIndices: ");
        paramPrintWriter.println(Arrays.toString(mAvailBackStackIndices.toArray()));
      }
      if (mPendingActions != null)
      {
        int i = mPendingActions.size();
        if (i > 0)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.println("Pending Actions:");
          for (int j = 0; j < i; j++)
          {
            Runnable localRunnable = (Runnable)mPendingActions.get(j);
            paramPrintWriter.print(paramString);
            paramPrintWriter.print("  #");
            paramPrintWriter.print(j);
            paramPrintWriter.print(": ");
            paramPrintWriter.println(localRunnable);
          }
        }
      }
      paramPrintWriter.print(paramString);
    }
    finally {}
    paramPrintWriter.println("FragmentManager misc state:");
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mHost=");
    paramPrintWriter.println(mHost);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mContainer=");
    paramPrintWriter.println(mContainer);
    if (mParent != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mParent=");
      paramPrintWriter.println(mParent);
    }
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("  mCurState=");
    paramPrintWriter.print(mCurState);
    paramPrintWriter.print(" mStateSaved=");
    paramPrintWriter.print(mStateSaved);
    paramPrintWriter.print(" mDestroyed=");
    paramPrintWriter.println(mDestroyed);
    if (mNeedMenuInvalidate)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mNeedMenuInvalidate=");
      paramPrintWriter.println(mNeedMenuInvalidate);
    }
    if (mNoTransactionsBecause != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mNoTransactionsBecause=");
      paramPrintWriter.println(mNoTransactionsBecause);
    }
    if ((mAvailIndices != null) && (mAvailIndices.size() > 0))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("  mAvailIndices: ");
      paramPrintWriter.println(Arrays.toString(mAvailIndices.toArray()));
    }
  }
  
  public void enqueueAction(Runnable paramRunnable, boolean paramBoolean)
  {
    if (!paramBoolean) {
      checkStateLoss();
    }
    try
    {
      if ((mDestroyed) || (mHost == null)) {
        throw new IllegalStateException("Activity has been destroyed");
      }
    }
    finally
    {
      throw localObject;
      if (mPendingActions == null) {
        mPendingActions = new ArrayList();
      }
      mPendingActions.add(paramRunnable);
      if (mPendingActions.size() == 1) {
        mHost.getHandler().removeCallbacks(mExecCommit);
      }
    }
  }
  
  public boolean execPendingActions()
  {
    if (mExecutingActions) {
      throw new IllegalStateException("FragmentManager is already executing transactions");
    }
    if (Looper.myLooper() != mHost.getHandler().getLooper()) {
      throw new IllegalStateException("Must be called from main thread of fragment host");
    }
    for (boolean bool = false;; bool = true) {
      try
      {
        if ((mPendingActions == null) || (mPendingActions.size() == 0))
        {
          doPendingDeferredStart();
          return bool;
        }
        int i = mPendingActions.size();
        if ((mTmpActions == null) || (mTmpActions.length < i)) {
          mTmpActions = new Runnable[i];
        }
        mPendingActions.toArray(mTmpActions);
        mPendingActions.clear();
        mHost.getHandler().removeCallbacks(mExecCommit);
        mExecutingActions = true;
        for (int j = 0; j < i; j++)
        {
          mTmpActions[j].run();
          mTmpActions[j] = null;
        }
        mExecutingActions = false;
      }
      finally {}
    }
  }
  
  public void execSingleAction(Runnable paramRunnable, boolean paramBoolean)
  {
    if (mExecutingActions) {
      throw new IllegalStateException("FragmentManager is already executing transactions");
    }
    if (Looper.myLooper() != mHost.getHandler().getLooper()) {
      throw new IllegalStateException("Must be called from main thread of fragment host");
    }
    if (!paramBoolean) {
      checkStateLoss();
    }
    mExecutingActions = true;
    paramRunnable.run();
    mExecutingActions = false;
    doPendingDeferredStart();
  }
  
  public boolean executePendingTransactions()
  {
    return execPendingActions();
  }
  
  public Fragment findFragmentById(int paramInt)
  {
    Fragment localFragment;
    if (mAdded != null) {
      for (int j = -1 + mAdded.size(); j >= 0; j--)
      {
        localFragment = (Fragment)mAdded.get(j);
        if ((localFragment != null) && (mFragmentId == paramInt)) {
          return localFragment;
        }
      }
    }
    if (mActive != null) {
      for (int i = -1 + mActive.size();; i--)
      {
        if (i < 0) {
          break label107;
        }
        localFragment = (Fragment)mActive.get(i);
        if ((localFragment != null) && (mFragmentId == paramInt)) {
          break;
        }
      }
    }
    label107:
    return null;
  }
  
  public Fragment findFragmentByTag(String paramString)
  {
    Fragment localFragment;
    if ((mAdded != null) && (paramString != null)) {
      for (int j = -1 + mAdded.size(); j >= 0; j--)
      {
        localFragment = (Fragment)mAdded.get(j);
        if ((localFragment != null) && (paramString.equals(mTag))) {
          return localFragment;
        }
      }
    }
    if ((mActive != null) && (paramString != null)) {
      for (int i = -1 + mActive.size();; i--)
      {
        if (i < 0) {
          break label121;
        }
        localFragment = (Fragment)mActive.get(i);
        if ((localFragment != null) && (paramString.equals(mTag))) {
          break;
        }
      }
    }
    label121:
    return null;
  }
  
  public Fragment findFragmentByWho(String paramString)
  {
    if ((mActive != null) && (paramString != null)) {
      for (int i = -1 + mActive.size(); i >= 0; i--)
      {
        Fragment localFragment1 = (Fragment)mActive.get(i);
        if (localFragment1 != null)
        {
          Fragment localFragment2 = localFragment1.findFragmentByWho(paramString);
          if (localFragment2 != null) {
            return localFragment2;
          }
        }
      }
    }
    return null;
  }
  
  public void freeBackStackIndex(int paramInt)
  {
    try
    {
      mBackStackIndices.set(paramInt, null);
      if (mAvailBackStackIndices == null) {
        mAvailBackStackIndices = new ArrayList();
      }
      if (DEBUG) {
        Log.v("FragmentManager", "Freeing back stack index " + paramInt);
      }
      mAvailBackStackIndices.add(Integer.valueOf(paramInt));
      return;
    }
    finally {}
  }
  
  public FragmentManager.BackStackEntry getBackStackEntryAt(int paramInt)
  {
    return (FragmentManager.BackStackEntry)mBackStack.get(paramInt);
  }
  
  public int getBackStackEntryCount()
  {
    if (mBackStack != null) {
      return mBackStack.size();
    }
    return 0;
  }
  
  public Fragment getFragment(Bundle paramBundle, String paramString)
  {
    int i = paramBundle.getInt(paramString, -1);
    Fragment localFragment;
    if (i == -1) {
      localFragment = null;
    }
    do
    {
      return localFragment;
      if (i >= mActive.size()) {
        throwException(new IllegalStateException("Fragment no longer exists for key " + paramString + ": index " + i));
      }
      localFragment = (Fragment)mActive.get(i);
    } while (localFragment != null);
    throwException(new IllegalStateException("Fragment no longer exists for key " + paramString + ": index " + i));
    return localFragment;
  }
  
  public List<Fragment> getFragments()
  {
    return mActive;
  }
  
  LayoutInflaterFactory getLayoutInflaterFactory()
  {
    return this;
  }
  
  public void hideFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.v("FragmentManager", "hide: " + paramFragment);
    }
    if (!mHidden)
    {
      mHidden = true;
      if (mView != null)
      {
        Animation localAnimation = loadAnimation(paramFragment, paramInt1, false, paramInt2);
        if (localAnimation != null)
        {
          setHWLayerAnimListenerIfAlpha(mView, localAnimation);
          mView.startAnimation(localAnimation);
        }
        mView.setVisibility(8);
      }
      if ((mAdded) && (mHasMenu) && (mMenuVisible)) {
        mNeedMenuInvalidate = true;
      }
      paramFragment.onHiddenChanged(true);
    }
  }
  
  public boolean isDestroyed()
  {
    return mDestroyed;
  }
  
  boolean isStateAtLeast(int paramInt)
  {
    return mCurState >= paramInt;
  }
  
  Animation loadAnimation(Fragment paramFragment, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    Animation localAnimation1 = paramFragment.onCreateAnimation(paramInt1, paramBoolean, mNextAnim);
    if (localAnimation1 != null) {
      return localAnimation1;
    }
    if (mNextAnim != 0)
    {
      Animation localAnimation2 = AnimationUtils.loadAnimation(mHost.getContext(), mNextAnim);
      if (localAnimation2 != null) {
        return localAnimation2;
      }
    }
    if (paramInt1 == 0) {
      return null;
    }
    int i = transitToStyleIndex(paramInt1, paramBoolean);
    if (i < 0) {
      return null;
    }
    switch (i)
    {
    default: 
      if ((paramInt2 == 0) && (mHost.onHasWindowAnimations())) {
        paramInt2 = mHost.onGetWindowAnimations();
      }
      if (paramInt2 == 0) {
        return null;
      }
      break;
    case 1: 
      return makeOpenCloseAnimation(mHost.getContext(), 1.125F, 1.0F, 0.0F, 1.0F);
    case 2: 
      return makeOpenCloseAnimation(mHost.getContext(), 1.0F, 0.975F, 1.0F, 0.0F);
    case 3: 
      return makeOpenCloseAnimation(mHost.getContext(), 0.975F, 1.0F, 0.0F, 1.0F);
    case 4: 
      return makeOpenCloseAnimation(mHost.getContext(), 1.0F, 1.075F, 1.0F, 0.0F);
    case 5: 
      return makeFadeAnimation(mHost.getContext(), 0.0F, 1.0F);
    case 6: 
      return makeFadeAnimation(mHost.getContext(), 1.0F, 0.0F);
    }
    return null;
  }
  
  void makeActive(Fragment paramFragment)
  {
    if (mIndex >= 0) {}
    for (;;)
    {
      return;
      if ((mAvailIndices == null) || (mAvailIndices.size() <= 0))
      {
        if (mActive == null) {
          mActive = new ArrayList();
        }
        paramFragment.setIndex(mActive.size(), mParent);
        mActive.add(paramFragment);
      }
      while (DEBUG)
      {
        Log.v("FragmentManager", "Allocated fragment index " + paramFragment);
        return;
        paramFragment.setIndex(((Integer)mAvailIndices.remove(-1 + mAvailIndices.size())).intValue(), mParent);
        mActive.set(mIndex, paramFragment);
      }
    }
  }
  
  void makeInactive(Fragment paramFragment)
  {
    if (mIndex < 0) {
      return;
    }
    if (DEBUG) {
      Log.v("FragmentManager", "Freeing fragment index " + paramFragment);
    }
    mActive.set(mIndex, null);
    if (mAvailIndices == null) {
      mAvailIndices = new ArrayList();
    }
    mAvailIndices.add(Integer.valueOf(mIndex));
    mHost.inactivateFragment(mWho);
    paramFragment.initState();
  }
  
  void moveToState(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if ((mHost == null) && (paramInt1 != 0)) {
      throw new IllegalStateException("No host");
    }
    if ((!paramBoolean) && (mCurState == paramInt1)) {}
    do
    {
      do
      {
        return;
        mCurState = paramInt1;
      } while (mActive == null);
      boolean bool = false;
      for (int i = 0; i < mActive.size(); i++)
      {
        Fragment localFragment = (Fragment)mActive.get(i);
        if (localFragment != null)
        {
          moveToState(localFragment, paramInt1, paramInt2, paramInt3, false);
          if (mLoaderManager != null) {
            bool |= mLoaderManager.hasRunningLoaders();
          }
        }
      }
      if (!bool) {
        startPendingDeferredFragments();
      }
    } while ((!mNeedMenuInvalidate) || (mHost == null) || (mCurState != 5));
    mHost.onSupportInvalidateOptionsMenu();
    mNeedMenuInvalidate = false;
  }
  
  void moveToState(int paramInt, boolean paramBoolean)
  {
    moveToState(paramInt, 0, 0, paramBoolean);
  }
  
  void moveToState(Fragment paramFragment)
  {
    moveToState(paramFragment, mCurState, 0, 0, false);
  }
  
  void moveToState(final Fragment paramFragment, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if (((!mAdded) || (mDetached)) && (paramInt1 > 1)) {
      paramInt1 = 1;
    }
    if (mRemoving)
    {
      int k = mState;
      if (paramInt1 > k) {
        paramInt1 = mState;
      }
    }
    if ((mDeferStart) && (mState < 4) && (paramInt1 > 3)) {
      paramInt1 = 3;
    }
    if (mState < paramInt1) {
      if ((!mFromLayout) || (mInLayout)) {}
    }
    for (;;)
    {
      return;
      if (mAnimatingAway != null)
      {
        mAnimatingAway = null;
        moveToState(paramFragment, mStateAfterAnimating, 0, 0, true);
      }
      switch (mState)
      {
      }
      while (mState != paramInt1)
      {
        Log.w("FragmentManager", "moveToState: Fragment state for " + paramFragment + " not updated inline; " + "expected state " + paramInt1 + " found " + mState);
        mState = paramInt1;
        return;
        if (DEBUG) {
          Log.v("FragmentManager", "moveto CREATED: " + paramFragment);
        }
        if (mSavedFragmentState != null)
        {
          mSavedFragmentState.setClassLoader(mHost.getContext().getClassLoader());
          mSavedViewState = mSavedFragmentState.getSparseParcelableArray("android:view_state");
          mTarget = getFragment(mSavedFragmentState, "android:target_state");
          if (mTarget != null) {
            mTargetRequestCode = mSavedFragmentState.getInt("android:target_req_state", 0);
          }
          mUserVisibleHint = mSavedFragmentState.getBoolean("android:user_visible_hint", true);
          if (!mUserVisibleHint)
          {
            mDeferStart = true;
            if (paramInt1 > 3) {
              paramInt1 = 3;
            }
          }
        }
        mHost = mHost;
        mParentFragment = mParent;
        if (mParent != null) {}
        for (FragmentManagerImpl localFragmentManagerImpl = mParent.mChildFragmentManager;; localFragmentManagerImpl = mHost.getFragmentManagerImpl())
        {
          mFragmentManager = localFragmentManagerImpl;
          mCalled = false;
          paramFragment.onAttach(mHost.getContext());
          if (mCalled) {
            break;
          }
          throw new SuperNotCalledException("Fragment " + paramFragment + " did not call through to super.onAttach()");
        }
        if (mParentFragment == null)
        {
          mHost.onAttachFragment(paramFragment);
          label480:
          if (mRetaining) {
            break label1073;
          }
          paramFragment.performCreate(mSavedFragmentState);
          label495:
          mRetaining = false;
          if (mFromLayout)
          {
            mView = paramFragment.performCreateView(paramFragment.getLayoutInflater(mSavedFragmentState), null, mSavedFragmentState);
            if (mView == null) {
              break label1103;
            }
            mInnerView = mView;
            if (Build.VERSION.SDK_INT < 11) {
              break label1089;
            }
            ViewCompat.setSaveFromParentEnabled(mView, false);
            label559:
            if (mHidden) {
              mView.setVisibility(8);
            }
            paramFragment.onViewCreated(mView, mSavedFragmentState);
          }
        }
        for (;;)
        {
          ViewGroup localViewGroup;
          if (paramInt1 > 1)
          {
            if (DEBUG) {
              Log.v("FragmentManager", "moveto ACTIVITY_CREATED: " + paramFragment);
            }
            if (!mFromLayout)
            {
              int j = mContainerId;
              localViewGroup = null;
              if (j != 0)
              {
                if (mContainerId == -1) {
                  throwException(new IllegalArgumentException("Cannot create fragment " + paramFragment + " for a container view with no id"));
                }
                localViewGroup = (ViewGroup)mContainer.onFindViewById(mContainerId);
                if ((localViewGroup != null) || (mRestored)) {}
              }
            }
          }
          try
          {
            String str2 = paramFragment.getResources().getResourceName(mContainerId);
            str1 = str2;
          }
          catch (Resources.NotFoundException localNotFoundException)
          {
            for (;;)
            {
              Animation localAnimation2;
              String str1 = "unknown";
              continue;
              mView = NoSaveStateFrameLayout.wrap(mView);
              continue;
              mInnerView = null;
            }
          }
          throwException(new IllegalArgumentException("No view found for id 0x" + Integer.toHexString(mContainerId) + " (" + str1 + ") for fragment " + paramFragment));
          mContainer = localViewGroup;
          mView = paramFragment.performCreateView(paramFragment.getLayoutInflater(mSavedFragmentState), localViewGroup, mSavedFragmentState);
          if (mView == null) {
            break label1135;
          }
          mInnerView = mView;
          if (Build.VERSION.SDK_INT < 11) {
            break label1121;
          }
          ViewCompat.setSaveFromParentEnabled(mView, false);
          if (localViewGroup != null)
          {
            localAnimation2 = loadAnimation(paramFragment, paramInt2, true, paramInt3);
            if (localAnimation2 != null)
            {
              setHWLayerAnimListenerIfAlpha(mView, localAnimation2);
              mView.startAnimation(localAnimation2);
            }
            localViewGroup.addView(mView);
          }
          if (mHidden) {
            mView.setVisibility(8);
          }
          paramFragment.onViewCreated(mView, mSavedFragmentState);
          paramFragment.performActivityCreated(mSavedFragmentState);
          if (mView != null) {
            paramFragment.restoreViewState(mSavedFragmentState);
          }
          mSavedFragmentState = null;
          if (paramInt1 > 2) {
            mState = 3;
          }
          if (paramInt1 > 3)
          {
            if (DEBUG) {
              Log.v("FragmentManager", "moveto STARTED: " + paramFragment);
            }
            paramFragment.performStart();
          }
          if (paramInt1 <= 4) {
            break;
          }
          if (DEBUG) {
            Log.v("FragmentManager", "moveto RESUMED: " + paramFragment);
          }
          paramFragment.performResume();
          mSavedFragmentState = null;
          mSavedViewState = null;
          break;
          mParentFragment.onAttachFragment(paramFragment);
          break label480;
          label1073:
          paramFragment.restoreChildFragmentState(mSavedFragmentState);
          mState = 1;
          break label495;
          label1089:
          mView = NoSaveStateFrameLayout.wrap(mView);
          break label559;
          label1103:
          mInnerView = null;
        }
        label1121:
        label1135:
        if (mState > paramInt1) {
          switch (mState)
          {
          default: 
            break;
          case 1: 
          case 5: 
          case 4: 
          case 3: 
          case 2: 
            while (paramInt1 < 1)
            {
              if ((mDestroyed) && (mAnimatingAway != null))
              {
                View localView = mAnimatingAway;
                mAnimatingAway = null;
                localView.clearAnimation();
              }
              if (mAnimatingAway == null) {
                break label1566;
              }
              mStateAfterAnimating = paramInt1;
              paramInt1 = 1;
              break;
              if (paramInt1 < 5)
              {
                if (DEBUG) {
                  Log.v("FragmentManager", "movefrom RESUMED: " + paramFragment);
                }
                paramFragment.performPause();
              }
              if (paramInt1 < 4)
              {
                if (DEBUG) {
                  Log.v("FragmentManager", "movefrom STARTED: " + paramFragment);
                }
                paramFragment.performStop();
              }
              if (paramInt1 < 3)
              {
                if (DEBUG) {
                  Log.v("FragmentManager", "movefrom STOPPED: " + paramFragment);
                }
                paramFragment.performReallyStop();
              }
              if (paramInt1 < 2)
              {
                if (DEBUG) {
                  Log.v("FragmentManager", "movefrom ACTIVITY_CREATED: " + paramFragment);
                }
                if ((mView != null) && (mHost.onShouldSaveFragmentState(paramFragment)) && (mSavedViewState == null)) {
                  saveFragmentViewState(paramFragment);
                }
                paramFragment.performDestroyView();
                if ((mView != null) && (mContainer != null))
                {
                  int i = mCurState;
                  Animation localAnimation1 = null;
                  if (i > 0)
                  {
                    boolean bool = mDestroyed;
                    localAnimation1 = null;
                    if (!bool) {
                      localAnimation1 = loadAnimation(paramFragment, paramInt2, false, paramInt3);
                    }
                  }
                  if (localAnimation1 != null)
                  {
                    mAnimatingAway = mView;
                    mStateAfterAnimating = paramInt1;
                    localAnimation1.setAnimationListener(new AnimateOnHWLayerIfNeededListener(mView, localAnimation1)
                    {
                      public void onAnimationEnd(Animation paramAnonymousAnimation)
                      {
                        super.onAnimationEnd(paramAnonymousAnimation);
                        if (paramFragmentmAnimatingAway != null)
                        {
                          paramFragmentmAnimatingAway = null;
                          moveToState(paramFragment, paramFragmentmStateAfterAnimating, 0, 0, false);
                        }
                      }
                    });
                    mView.startAnimation(localAnimation1);
                  }
                  mContainer.removeView(mView);
                }
                mContainer = null;
                mView = null;
                mInnerView = null;
              }
            }
            label1566:
            if (DEBUG) {
              Log.v("FragmentManager", "movefrom CREATED: " + paramFragment);
            }
            if (!mRetaining) {
              paramFragment.performDestroy();
            }
            for (;;)
            {
              paramFragment.performDetach();
              if (paramBoolean) {
                break;
              }
              if (mRetaining) {
                break label1641;
              }
              makeInactive(paramFragment);
              break;
              mState = 0;
            }
            label1641:
            mHost = null;
            mParentFragment = null;
            mFragmentManager = null;
          }
        }
      }
    }
  }
  
  public void noteStateNotSaved()
  {
    mStateSaved = false;
  }
  
  public View onCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    if (!"fragment".equals(paramString)) {}
    String str1;
    int i;
    String str2;
    do
    {
      return null;
      str1 = paramAttributeSet.getAttributeValue(null, "class");
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, FragmentTag.Fragment);
      if (str1 == null) {
        str1 = localTypedArray.getString(0);
      }
      i = localTypedArray.getResourceId(1, -1);
      str2 = localTypedArray.getString(2);
      localTypedArray.recycle();
    } while (!Fragment.isSupportFragmentClass(mHost.getContext(), str1));
    if (paramView != null) {}
    for (int j = paramView.getId(); (j == -1) && (i == -1) && (str2 == null); j = 0) {
      throw new IllegalArgumentException(paramAttributeSet.getPositionDescription() + ": Must specify unique android:id, android:tag, or have a parent with an id for " + str1);
    }
    Fragment localFragment;
    int k;
    if (i != -1)
    {
      localFragment = findFragmentById(i);
      if ((localFragment == null) && (str2 != null)) {
        localFragment = findFragmentByTag(str2);
      }
      if ((localFragment == null) && (j != -1)) {
        localFragment = findFragmentById(j);
      }
      if (DEBUG) {
        Log.v("FragmentManager", "onCreateView: id=0x" + Integer.toHexString(i) + " fname=" + str1 + " existing=" + localFragment);
      }
      if (localFragment != null) {
        break label445;
      }
      localFragment = Fragment.instantiate(paramContext, str1);
      mFromLayout = true;
      if (i == 0) {
        break label438;
      }
      k = i;
      label295:
      mFragmentId = k;
      mContainerId = j;
      mTag = str2;
      mInLayout = true;
      mFragmentManager = this;
      mHost = mHost;
      localFragment.onInflate(mHost.getContext(), paramAttributeSet, mSavedFragmentState);
      addFragment(localFragment, true);
      label363:
      if ((mCurState >= 1) || (!mFromLayout)) {
        break label576;
      }
      moveToState(localFragment, 1, 0, 0, false);
    }
    for (;;)
    {
      if (mView != null) {
        break label585;
      }
      throw new IllegalStateException("Fragment " + str1 + " did not create a view.");
      localFragment = null;
      break;
      label438:
      k = j;
      break label295;
      label445:
      if (mInLayout) {
        throw new IllegalArgumentException(paramAttributeSet.getPositionDescription() + ": Duplicate id 0x" + Integer.toHexString(i) + ", tag " + str2 + ", or parent id 0x" + Integer.toHexString(j) + " with another fragment for " + str1);
      }
      mInLayout = true;
      mHost = mHost;
      if (mRetaining) {
        break label363;
      }
      localFragment.onInflate(mHost.getContext(), paramAttributeSet, mSavedFragmentState);
      break label363;
      label576:
      moveToState(localFragment);
    }
    label585:
    if (i != 0) {
      mView.setId(i);
    }
    if (mView.getTag() == null) {
      mView.setTag(str2);
    }
    return mView;
  }
  
  public void performPendingDeferredStart(Fragment paramFragment)
  {
    if (mDeferStart)
    {
      if (mExecutingActions) {
        mHavePendingDeferredStart = true;
      }
    }
    else {
      return;
    }
    mDeferStart = false;
    moveToState(paramFragment, mCurState, 0, 0, false);
  }
  
  public void popBackStack()
  {
    enqueueAction(new Runnable()
    {
      public void run()
      {
        popBackStackState(mHost.getHandler(), null, -1, 0);
      }
    }, false);
  }
  
  public void popBackStack(final int paramInt1, final int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("Bad id: " + paramInt1);
    }
    enqueueAction(new Runnable()
    {
      public void run()
      {
        popBackStackState(mHost.getHandler(), null, paramInt1, paramInt2);
      }
    }, false);
  }
  
  public void popBackStack(final String paramString, final int paramInt)
  {
    enqueueAction(new Runnable()
    {
      public void run()
      {
        popBackStackState(mHost.getHandler(), paramString, -1, paramInt);
      }
    }, false);
  }
  
  public boolean popBackStackImmediate()
  {
    checkStateLoss();
    executePendingTransactions();
    return popBackStackState(mHost.getHandler(), null, -1, 0);
  }
  
  public boolean popBackStackImmediate(int paramInt1, int paramInt2)
  {
    checkStateLoss();
    executePendingTransactions();
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("Bad id: " + paramInt1);
    }
    return popBackStackState(mHost.getHandler(), null, paramInt1, paramInt2);
  }
  
  public boolean popBackStackImmediate(String paramString, int paramInt)
  {
    checkStateLoss();
    executePendingTransactions();
    return popBackStackState(mHost.getHandler(), paramString, -1, paramInt);
  }
  
  boolean popBackStackState(Handler paramHandler, String paramString, int paramInt1, int paramInt2)
  {
    if (mBackStack == null) {
      return false;
    }
    if ((paramString == null) && (paramInt1 < 0) && ((paramInt2 & 0x1) == 0))
    {
      int i1 = -1 + mBackStack.size();
      if (i1 < 0) {
        return false;
      }
      BackStackRecord localBackStackRecord4 = (BackStackRecord)mBackStack.remove(i1);
      SparseArray localSparseArray3 = new SparseArray();
      SparseArray localSparseArray4 = new SparseArray();
      if (mCurState >= 1) {
        localBackStackRecord4.calculateBackFragments(localSparseArray3, localSparseArray4);
      }
      localBackStackRecord4.popFromBackStack(true, null, localSparseArray3, localSparseArray4);
      reportBackStackChanged();
    }
    for (;;)
    {
      return true;
      int i = -1;
      if ((paramString != null) || (paramInt1 >= 0))
      {
        for (i = -1 + mBackStack.size();; i--)
        {
          BackStackRecord localBackStackRecord3;
          if (i >= 0)
          {
            localBackStackRecord3 = (BackStackRecord)mBackStack.get(i);
            if ((paramString == null) || (!paramString.equals(localBackStackRecord3.getName()))) {
              break label173;
            }
          }
          label173:
          while ((paramInt1 >= 0) && (paramInt1 == mIndex))
          {
            if (i >= 0) {
              break;
            }
            return false;
          }
        }
        if ((paramInt2 & 0x1) != 0)
        {
          i--;
          while (i >= 0)
          {
            BackStackRecord localBackStackRecord2 = (BackStackRecord)mBackStack.get(i);
            if (((paramString == null) || (!paramString.equals(localBackStackRecord2.getName()))) && ((paramInt1 < 0) || (paramInt1 != mIndex))) {
              break;
            }
            i--;
          }
        }
      }
      if (i == -1 + mBackStack.size()) {
        return false;
      }
      ArrayList localArrayList = new ArrayList();
      for (int j = -1 + mBackStack.size(); j > i; j--) {
        localArrayList.add(mBackStack.remove(j));
      }
      int k = -1 + localArrayList.size();
      SparseArray localSparseArray1 = new SparseArray();
      SparseArray localSparseArray2 = new SparseArray();
      if (mCurState >= 1) {
        for (int n = 0; n <= k; n++) {
          ((BackStackRecord)localArrayList.get(n)).calculateBackFragments(localSparseArray1, localSparseArray2);
        }
      }
      BackStackRecord.TransitionState localTransitionState = null;
      int m = 0;
      if (m <= k)
      {
        if (DEBUG) {
          Log.v("FragmentManager", "Popping back stack state: " + localArrayList.get(m));
        }
        BackStackRecord localBackStackRecord1 = (BackStackRecord)localArrayList.get(m);
        if (m == k) {}
        for (boolean bool = true;; bool = false)
        {
          localTransitionState = localBackStackRecord1.popFromBackStack(bool, localTransitionState, localSparseArray1, localSparseArray2);
          m++;
          break;
        }
      }
      reportBackStackChanged();
    }
  }
  
  public void putFragment(Bundle paramBundle, String paramString, Fragment paramFragment)
  {
    if (mIndex < 0) {
      throwException(new IllegalStateException("Fragment " + paramFragment + " is not currently in the FragmentManager"));
    }
    paramBundle.putInt(paramString, mIndex);
  }
  
  public void removeFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.v("FragmentManager", "remove: " + paramFragment + " nesting=" + mBackStackNesting);
    }
    int i;
    if (!paramFragment.isInBackStack())
    {
      i = 1;
      if ((!mDetached) || (i != 0))
      {
        if (mAdded != null) {
          mAdded.remove(paramFragment);
        }
        if ((mHasMenu) && (mMenuVisible)) {
          mNeedMenuInvalidate = true;
        }
        mAdded = false;
        mRemoving = true;
        if (i == 0) {
          break label137;
        }
      }
    }
    label137:
    for (int j = 0;; j = 1)
    {
      moveToState(paramFragment, j, paramInt1, paramInt2, false);
      return;
      i = 0;
      break;
    }
  }
  
  public void removeOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener paramOnBackStackChangedListener)
  {
    if (mBackStackChangeListeners != null) {
      mBackStackChangeListeners.remove(paramOnBackStackChangedListener);
    }
  }
  
  void reportBackStackChanged()
  {
    if (mBackStackChangeListeners != null) {
      for (int i = 0; i < mBackStackChangeListeners.size(); i++) {
        ((FragmentManager.OnBackStackChangedListener)mBackStackChangeListeners.get(i)).onBackStackChanged();
      }
    }
  }
  
  void restoreAllState(Parcelable paramParcelable, FragmentManagerNonConfig paramFragmentManagerNonConfig)
  {
    if (paramParcelable == null) {}
    for (;;)
    {
      return;
      FragmentManagerState localFragmentManagerState = (FragmentManagerState)paramParcelable;
      if (mActive != null)
      {
        List localList1 = null;
        if (paramFragmentManagerNonConfig != null)
        {
          List localList3 = paramFragmentManagerNonConfig.getFragments();
          localList1 = paramFragmentManagerNonConfig.getChildNonConfigs();
          if (localList3 != null) {}
          for (int i2 = localList3.size();; i2 = 0) {
            for (int i3 = 0; i3 < i2; i3++)
            {
              Fragment localFragment4 = (Fragment)localList3.get(i3);
              if (DEBUG) {
                Log.v("FragmentManager", "restoreAllState: re-attaching retained " + localFragment4);
              }
              FragmentState localFragmentState2 = mActive[mIndex];
              mInstance = localFragment4;
              mSavedViewState = null;
              mBackStackNesting = 0;
              mInLayout = false;
              mAdded = false;
              mTarget = null;
              if (mSavedFragmentState != null)
              {
                mSavedFragmentState.setClassLoader(mHost.getContext().getClassLoader());
                mSavedViewState = mSavedFragmentState.getSparseParcelableArray("android:view_state");
                mSavedFragmentState = mSavedFragmentState;
              }
            }
          }
        }
        mActive = new ArrayList(mActive.length);
        if (mAvailIndices != null) {
          mAvailIndices.clear();
        }
        int i = 0;
        if (i < mActive.length)
        {
          FragmentState localFragmentState1 = mActive[i];
          if (localFragmentState1 != null)
          {
            FragmentManagerNonConfig localFragmentManagerNonConfig = null;
            if (localList1 != null)
            {
              int i1 = localList1.size();
              localFragmentManagerNonConfig = null;
              if (i < i1) {
                localFragmentManagerNonConfig = (FragmentManagerNonConfig)localList1.get(i);
              }
            }
            Fragment localFragment3 = localFragmentState1.instantiate(mHost, mParent, localFragmentManagerNonConfig);
            if (DEBUG) {
              Log.v("FragmentManager", "restoreAllState: active #" + i + ": " + localFragment3);
            }
            mActive.add(localFragment3);
            mInstance = null;
          }
          for (;;)
          {
            i++;
            break;
            mActive.add(null);
            if (mAvailIndices == null) {
              mAvailIndices = new ArrayList();
            }
            if (DEBUG) {
              Log.v("FragmentManager", "restoreAllState: avail #" + i);
            }
            mAvailIndices.add(Integer.valueOf(i));
          }
        }
        if (paramFragmentManagerNonConfig != null)
        {
          List localList2 = paramFragmentManagerNonConfig.getFragments();
          int m;
          int n;
          label503:
          Fragment localFragment2;
          if (localList2 != null)
          {
            m = localList2.size();
            n = 0;
            if (n >= m) {
              break label629;
            }
            localFragment2 = (Fragment)localList2.get(n);
            if (mTargetIndex >= 0) {
              if (mTargetIndex >= mActive.size()) {
                break label579;
              }
            }
          }
          for (mTarget = ((Fragment)mActive.get(mTargetIndex));; mTarget = null)
          {
            n++;
            break label503;
            m = 0;
            break;
            label579:
            Log.w("FragmentManager", "Re-attaching retained fragment " + localFragment2 + " target no longer exists: " + mTargetIndex);
          }
        }
        label629:
        if (mAdded != null)
        {
          mAdded = new ArrayList(mAdded.length);
          for (int k = 0; k < mAdded.length; k++)
          {
            Fragment localFragment1 = (Fragment)mActive.get(mAdded[k]);
            if (localFragment1 == null) {
              throwException(new IllegalStateException("No instantiated fragment for index #" + mAdded[k]));
            }
            mAdded = true;
            if (DEBUG) {
              Log.v("FragmentManager", "restoreAllState: added #" + k + ": " + localFragment1);
            }
            if (mAdded.contains(localFragment1)) {
              throw new IllegalStateException("Already added!");
            }
            mAdded.add(localFragment1);
          }
        }
        mAdded = null;
        if (mBackStack == null) {
          break;
        }
        mBackStack = new ArrayList(mBackStack.length);
        for (int j = 0; j < mBackStack.length; j++)
        {
          BackStackRecord localBackStackRecord = mBackStack[j].instantiate(this);
          if (DEBUG)
          {
            Log.v("FragmentManager", "restoreAllState: back stack #" + j + " (index " + mIndex + "): " + localBackStackRecord);
            localBackStackRecord.dump("  ", new PrintWriter(new LogWriter("FragmentManager")), false);
          }
          mBackStack.add(localBackStackRecord);
          if (mIndex >= 0) {
            setBackStackIndex(mIndex, localBackStackRecord);
          }
        }
      }
    }
    mBackStack = null;
  }
  
  FragmentManagerNonConfig retainNonConfig()
  {
    ArrayList localArrayList1 = mActive;
    ArrayList localArrayList2 = null;
    ArrayList localArrayList3 = null;
    if (localArrayList1 != null) {
      for (int i = 0; i < mActive.size(); i++)
      {
        Fragment localFragment = (Fragment)mActive.get(i);
        if (localFragment != null)
        {
          if (mRetainInstance)
          {
            if (localArrayList3 == null) {
              localArrayList3 = new ArrayList();
            }
            localArrayList3.add(localFragment);
            mRetaining = true;
            if (mTarget == null) {
              break label205;
            }
          }
          FragmentManagerNonConfig localFragmentManagerNonConfig;
          label205:
          for (int m = mTarget.mIndex;; m = -1)
          {
            mTargetIndex = m;
            if (DEBUG) {
              Log.v("FragmentManager", "retainNonConfig: keeping retained " + localFragment);
            }
            FragmentManagerImpl localFragmentManagerImpl = mChildFragmentManager;
            j = 0;
            if (localFragmentManagerImpl == null) {
              break label221;
            }
            localFragmentManagerNonConfig = mChildFragmentManager.retainNonConfig();
            j = 0;
            if (localFragmentManagerNonConfig == null) {
              break label221;
            }
            if (localArrayList2 != null) {
              break;
            }
            localArrayList2 = new ArrayList();
            for (int k = 0; k < i; k++) {
              localArrayList2.add(null);
            }
          }
          localArrayList2.add(localFragmentManagerNonConfig);
          int j = 1;
          label221:
          if ((localArrayList2 != null) && (j == 0)) {
            localArrayList2.add(null);
          }
        }
      }
    }
    if ((localArrayList3 == null) && (localArrayList2 == null)) {
      return null;
    }
    return new FragmentManagerNonConfig(localArrayList3, localArrayList2);
  }
  
  Parcelable saveAllState()
  {
    execPendingActions();
    if (HONEYCOMB) {
      mStateSaved = true;
    }
    if ((mActive == null) || (mActive.size() <= 0)) {}
    FragmentState[] arrayOfFragmentState;
    label357:
    do
    {
      return null;
      int i = mActive.size();
      arrayOfFragmentState = new FragmentState[i];
      int j = 0;
      int k = 0;
      if (k < i)
      {
        Fragment localFragment = (Fragment)mActive.get(k);
        FragmentState localFragmentState;
        if (localFragment != null)
        {
          if (mIndex < 0) {
            throwException(new IllegalStateException("Failure saving state: active " + localFragment + " has cleared index: " + mIndex));
          }
          j = 1;
          localFragmentState = new FragmentState(localFragment);
          arrayOfFragmentState[k] = localFragmentState;
          if ((mState <= 0) || (mSavedFragmentState != null)) {
            break label357;
          }
          mSavedFragmentState = saveFragmentBasicState(localFragment);
          if (mTarget != null)
          {
            if (mTarget.mIndex < 0) {
              throwException(new IllegalStateException("Failure saving state: " + localFragment + " has target not in fragment manager: " + mTarget));
            }
            if (mSavedFragmentState == null) {
              mSavedFragmentState = new Bundle();
            }
            putFragment(mSavedFragmentState, "android:target_state", mTarget);
            if (mTargetRequestCode != 0) {
              mSavedFragmentState.putInt("android:target_req_state", mTargetRequestCode);
            }
          }
        }
        for (;;)
        {
          if (DEBUG) {
            Log.v("FragmentManager", "Saved state of " + localFragment + ": " + mSavedFragmentState);
          }
          k++;
          break;
          mSavedFragmentState = mSavedFragmentState;
        }
      }
      if (j != 0) {
        break;
      }
    } while (!DEBUG);
    Log.v("FragmentManager", "saveAllState: no fragments!");
    return null;
    ArrayList localArrayList1 = mAdded;
    int[] arrayOfInt = null;
    if (localArrayList1 != null)
    {
      int i1 = mAdded.size();
      arrayOfInt = null;
      if (i1 > 0)
      {
        arrayOfInt = new int[i1];
        for (int i2 = 0; i2 < i1; i2++)
        {
          arrayOfInt[i2] = mAdded.get(i2)).mIndex;
          if (arrayOfInt[i2] < 0) {
            throwException(new IllegalStateException("Failure saving state: active " + mAdded.get(i2) + " has cleared index: " + arrayOfInt[i2]));
          }
          if (DEBUG) {
            Log.v("FragmentManager", "saveAllState: adding fragment #" + i2 + ": " + mAdded.get(i2));
          }
        }
      }
    }
    ArrayList localArrayList2 = mBackStack;
    BackStackState[] arrayOfBackStackState = null;
    if (localArrayList2 != null)
    {
      int m = mBackStack.size();
      arrayOfBackStackState = null;
      if (m > 0)
      {
        arrayOfBackStackState = new BackStackState[m];
        for (int n = 0; n < m; n++)
        {
          arrayOfBackStackState[n] = new BackStackState((BackStackRecord)mBackStack.get(n));
          if (DEBUG) {
            Log.v("FragmentManager", "saveAllState: adding back stack #" + n + ": " + mBackStack.get(n));
          }
        }
      }
    }
    FragmentManagerState localFragmentManagerState = new FragmentManagerState();
    mActive = arrayOfFragmentState;
    mAdded = arrayOfInt;
    mBackStack = arrayOfBackStackState;
    return localFragmentManagerState;
  }
  
  Bundle saveFragmentBasicState(Fragment paramFragment)
  {
    if (mStateBundle == null) {
      mStateBundle = new Bundle();
    }
    paramFragment.performSaveInstanceState(mStateBundle);
    boolean bool = mStateBundle.isEmpty();
    Bundle localBundle = null;
    if (!bool)
    {
      localBundle = mStateBundle;
      mStateBundle = null;
    }
    if (mView != null) {
      saveFragmentViewState(paramFragment);
    }
    if (mSavedViewState != null)
    {
      if (localBundle == null) {
        localBundle = new Bundle();
      }
      localBundle.putSparseParcelableArray("android:view_state", mSavedViewState);
    }
    if (!mUserVisibleHint)
    {
      if (localBundle == null) {
        localBundle = new Bundle();
      }
      localBundle.putBoolean("android:user_visible_hint", mUserVisibleHint);
    }
    return localBundle;
  }
  
  public Fragment.SavedState saveFragmentInstanceState(Fragment paramFragment)
  {
    if (mIndex < 0) {
      throwException(new IllegalStateException("Fragment " + paramFragment + " is not currently in the FragmentManager"));
    }
    int i = mState;
    Fragment.SavedState localSavedState = null;
    if (i > 0)
    {
      Bundle localBundle = saveFragmentBasicState(paramFragment);
      localSavedState = null;
      if (localBundle != null) {
        localSavedState = new Fragment.SavedState(localBundle);
      }
    }
    return localSavedState;
  }
  
  void saveFragmentViewState(Fragment paramFragment)
  {
    if (mInnerView == null) {
      return;
    }
    if (mStateArray == null) {
      mStateArray = new SparseArray();
    }
    for (;;)
    {
      mInnerView.saveHierarchyState(mStateArray);
      if (mStateArray.size() <= 0) {
        break;
      }
      mSavedViewState = mStateArray;
      mStateArray = null;
      return;
      mStateArray.clear();
    }
  }
  
  /* Error */
  public void setBackStackIndex(int paramInt, BackStackRecord paramBackStackRecord)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 386	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   6: ifnonnull +14 -> 20
    //   9: aload_0
    //   10: new 325	java/util/ArrayList
    //   13: dup
    //   14: invokespecial 326	java/util/ArrayList:<init>	()V
    //   17: putfield 386	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   20: aload_0
    //   21: getfield 386	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   24: invokevirtual 384	java/util/ArrayList:size	()I
    //   27: istore 4
    //   29: iload_1
    //   30: iload 4
    //   32: if_icmpge +58 -> 90
    //   35: getstatic 91	android/support/v4/app/FragmentManagerImpl:DEBUG	Z
    //   38: ifeq +39 -> 77
    //   41: ldc 34
    //   43: new 148	java/lang/StringBuilder
    //   46: dup
    //   47: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   50: ldc_w 388
    //   53: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: iload_1
    //   57: invokevirtual 391	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   60: ldc_w 393
    //   63: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   66: aload_2
    //   67: invokevirtual 342	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   70: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   73: invokestatic 345	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   76: pop
    //   77: aload_0
    //   78: getfield 386	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   81: iload_1
    //   82: aload_2
    //   83: invokevirtual 409	java/util/ArrayList:set	(ILjava/lang/Object;)Ljava/lang/Object;
    //   86: pop
    //   87: aload_0
    //   88: monitorexit
    //   89: return
    //   90: iload 4
    //   92: iload_1
    //   93: if_icmpge +82 -> 175
    //   96: aload_0
    //   97: getfield 386	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   100: aconst_null
    //   101: invokevirtual 330	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   104: pop
    //   105: aload_0
    //   106: getfield 383	android/support/v4/app/FragmentManagerImpl:mAvailBackStackIndices	Ljava/util/ArrayList;
    //   109: ifnonnull +14 -> 123
    //   112: aload_0
    //   113: new 325	java/util/ArrayList
    //   116: dup
    //   117: invokespecial 326	java/util/ArrayList:<init>	()V
    //   120: putfield 383	android/support/v4/app/FragmentManagerImpl:mAvailBackStackIndices	Ljava/util/ArrayList;
    //   123: getstatic 91	android/support/v4/app/FragmentManagerImpl:DEBUG	Z
    //   126: ifeq +30 -> 156
    //   129: ldc 34
    //   131: new 148	java/lang/StringBuilder
    //   134: dup
    //   135: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   138: ldc_w 1334
    //   141: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   144: iload 4
    //   146: invokevirtual 391	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   149: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   152: invokestatic 345	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   155: pop
    //   156: aload_0
    //   157: getfield 383	android/support/v4/app/FragmentManagerImpl:mAvailBackStackIndices	Ljava/util/ArrayList;
    //   160: iload 4
    //   162: invokestatic 680	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   165: invokevirtual 330	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   168: pop
    //   169: iinc 4 1
    //   172: goto -82 -> 90
    //   175: getstatic 91	android/support/v4/app/FragmentManagerImpl:DEBUG	Z
    //   178: ifeq +39 -> 217
    //   181: ldc 34
    //   183: new 148	java/lang/StringBuilder
    //   186: dup
    //   187: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   190: ldc_w 403
    //   193: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   196: iload_1
    //   197: invokevirtual 391	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   200: ldc_w 405
    //   203: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   206: aload_2
    //   207: invokevirtual 342	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   210: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   213: invokestatic 345	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   216: pop
    //   217: aload_0
    //   218: getfield 386	android/support/v4/app/FragmentManagerImpl:mBackStackIndices	Ljava/util/ArrayList;
    //   221: aload_2
    //   222: invokevirtual 330	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   225: pop
    //   226: goto -139 -> 87
    //   229: astore_3
    //   230: aload_0
    //   231: monitorexit
    //   232: aload_3
    //   233: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	234	0	this	FragmentManagerImpl
    //   0	234	1	paramInt	int
    //   0	234	2	paramBackStackRecord	BackStackRecord
    //   229	4	3	localObject	Object
    //   27	143	4	i	int
    // Exception table:
    //   from	to	target	type
    //   2	20	229	finally
    //   20	29	229	finally
    //   35	77	229	finally
    //   77	87	229	finally
    //   87	89	229	finally
    //   96	123	229	finally
    //   123	156	229	finally
    //   156	169	229	finally
    //   175	217	229	finally
    //   217	226	229	finally
    //   230	232	229	finally
  }
  
  public void showFragment(Fragment paramFragment, int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Log.v("FragmentManager", "show: " + paramFragment);
    }
    if (mHidden)
    {
      mHidden = false;
      if (mView != null)
      {
        Animation localAnimation = loadAnimation(paramFragment, paramInt1, true, paramInt2);
        if (localAnimation != null)
        {
          setHWLayerAnimListenerIfAlpha(mView, localAnimation);
          mView.startAnimation(localAnimation);
        }
        mView.setVisibility(0);
      }
      if ((mAdded) && (mHasMenu) && (mMenuVisible)) {
        mNeedMenuInvalidate = true;
      }
      paramFragment.onHiddenChanged(false);
    }
  }
  
  void startPendingDeferredFragments()
  {
    if (mActive == null) {}
    for (;;)
    {
      return;
      for (int i = 0; i < mActive.size(); i++)
      {
        Fragment localFragment = (Fragment)mActive.get(i);
        if (localFragment != null) {
          performPendingDeferredStart(localFragment);
        }
      }
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("FragmentManager{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" in ");
    if (mParent != null) {
      DebugUtils.buildShortClassTag(mParent, localStringBuilder);
    }
    for (;;)
    {
      localStringBuilder.append("}}");
      return localStringBuilder.toString();
      DebugUtils.buildShortClassTag(mHost, localStringBuilder);
    }
  }
  
  static class AnimateOnHWLayerIfNeededListener
    implements Animation.AnimationListener
  {
    private Animation.AnimationListener mOriginalListener;
    private boolean mShouldRunOnHWLayer;
    View mView;
    
    public AnimateOnHWLayerIfNeededListener(View paramView, Animation paramAnimation)
    {
      if ((paramView == null) || (paramAnimation == null)) {
        return;
      }
      mView = paramView;
    }
    
    public AnimateOnHWLayerIfNeededListener(View paramView, Animation paramAnimation, Animation.AnimationListener paramAnimationListener)
    {
      if ((paramView == null) || (paramAnimation == null)) {
        return;
      }
      mOriginalListener = paramAnimationListener;
      mView = paramView;
      mShouldRunOnHWLayer = true;
    }
    
    @CallSuper
    public void onAnimationEnd(Animation paramAnimation)
    {
      if ((mView != null) && (mShouldRunOnHWLayer))
      {
        if ((!ViewCompat.isAttachedToWindow(mView)) && (!BuildCompat.isAtLeastN())) {
          break label64;
        }
        mView.post(new Runnable()
        {
          public void run()
          {
            ViewCompat.setLayerType(mView, 0, null);
          }
        });
      }
      for (;;)
      {
        if (mOriginalListener != null) {
          mOriginalListener.onAnimationEnd(paramAnimation);
        }
        return;
        label64:
        ViewCompat.setLayerType(mView, 0, null);
      }
    }
    
    public void onAnimationRepeat(Animation paramAnimation)
    {
      if (mOriginalListener != null) {
        mOriginalListener.onAnimationRepeat(paramAnimation);
      }
    }
    
    @CallSuper
    public void onAnimationStart(Animation paramAnimation)
    {
      if (mOriginalListener != null) {
        mOriginalListener.onAnimationStart(paramAnimation);
      }
    }
  }
  
  static class FragmentTag
  {
    public static final int[] Fragment = { 16842755, 16842960, 16842961 };
    public static final int Fragment_id = 1;
    public static final int Fragment_name = 0;
    public static final int Fragment_tag = 2;
    
    FragmentTag() {}
  }
}
