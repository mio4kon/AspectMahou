package android.support.v4.app;

import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class Fragment
  implements ComponentCallbacks, View.OnCreateContextMenuListener
{
  static final int ACTIVITY_CREATED = 2;
  static final int CREATED = 1;
  static final int INITIALIZING = 0;
  static final int RESUMED = 5;
  static final int STARTED = 4;
  static final int STOPPED = 3;
  static final Object USE_DEFAULT_TRANSITION = new Object();
  private static final SimpleArrayMap<String, Class<?>> sClassMap = new SimpleArrayMap();
  boolean mAdded;
  Boolean mAllowEnterTransitionOverlap;
  Boolean mAllowReturnTransitionOverlap;
  View mAnimatingAway;
  Bundle mArguments;
  int mBackStackNesting;
  boolean mCalled;
  boolean mCheckedForLoaderManager;
  FragmentManagerImpl mChildFragmentManager;
  FragmentManagerNonConfig mChildNonConfig;
  ViewGroup mContainer;
  int mContainerId;
  boolean mDeferStart;
  boolean mDetached;
  Object mEnterTransition = null;
  SharedElementCallback mEnterTransitionCallback = null;
  Object mExitTransition = null;
  SharedElementCallback mExitTransitionCallback = null;
  int mFragmentId;
  FragmentManagerImpl mFragmentManager;
  boolean mFromLayout;
  boolean mHasMenu;
  boolean mHidden;
  FragmentHostCallback mHost;
  boolean mInLayout;
  int mIndex = -1;
  View mInnerView;
  LoaderManagerImpl mLoaderManager;
  boolean mLoadersStarted;
  boolean mMenuVisible = true;
  int mNextAnim;
  Fragment mParentFragment;
  Object mReenterTransition = USE_DEFAULT_TRANSITION;
  boolean mRemoving;
  boolean mRestored;
  boolean mRetainInstance;
  boolean mRetaining;
  Object mReturnTransition = USE_DEFAULT_TRANSITION;
  Bundle mSavedFragmentState;
  SparseArray<Parcelable> mSavedViewState;
  Object mSharedElementEnterTransition = null;
  Object mSharedElementReturnTransition = USE_DEFAULT_TRANSITION;
  int mState = 0;
  int mStateAfterAnimating;
  String mTag;
  Fragment mTarget;
  int mTargetIndex = -1;
  int mTargetRequestCode;
  boolean mUserVisibleHint = true;
  View mView;
  String mWho;
  
  public Fragment() {}
  
  public static Fragment instantiate(Context paramContext, String paramString)
  {
    return instantiate(paramContext, paramString, null);
  }
  
  public static Fragment instantiate(Context paramContext, String paramString, @Nullable Bundle paramBundle)
  {
    try
    {
      Class localClass = (Class)sClassMap.get(paramString);
      if (localClass == null)
      {
        localClass = paramContext.getClassLoader().loadClass(paramString);
        sClassMap.put(paramString, localClass);
      }
      Fragment localFragment = (Fragment)localClass.newInstance();
      if (paramBundle != null)
      {
        paramBundle.setClassLoader(localFragment.getClass().getClassLoader());
        mArguments = paramBundle;
      }
      return localFragment;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new InstantiationException("Unable to instantiate fragment " + paramString + ": make sure class name exists, is public, and has an" + " empty constructor that is public", localClassNotFoundException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new InstantiationException("Unable to instantiate fragment " + paramString + ": make sure class name exists, is public, and has an" + " empty constructor that is public", localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new InstantiationException("Unable to instantiate fragment " + paramString + ": make sure class name exists, is public, and has an" + " empty constructor that is public", localIllegalAccessException);
    }
  }
  
  static boolean isSupportFragmentClass(Context paramContext, String paramString)
  {
    try
    {
      Class localClass = (Class)sClassMap.get(paramString);
      if (localClass == null)
      {
        localClass = paramContext.getClassLoader().loadClass(paramString);
        sClassMap.put(paramString, localClass);
      }
      boolean bool = Fragment.class.isAssignableFrom(localClass);
      return bool;
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
    return false;
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mFragmentId=#");
    paramPrintWriter.print(Integer.toHexString(mFragmentId));
    paramPrintWriter.print(" mContainerId=#");
    paramPrintWriter.print(Integer.toHexString(mContainerId));
    paramPrintWriter.print(" mTag=");
    paramPrintWriter.println(mTag);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mState=");
    paramPrintWriter.print(mState);
    paramPrintWriter.print(" mIndex=");
    paramPrintWriter.print(mIndex);
    paramPrintWriter.print(" mWho=");
    paramPrintWriter.print(mWho);
    paramPrintWriter.print(" mBackStackNesting=");
    paramPrintWriter.println(mBackStackNesting);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mAdded=");
    paramPrintWriter.print(mAdded);
    paramPrintWriter.print(" mRemoving=");
    paramPrintWriter.print(mRemoving);
    paramPrintWriter.print(" mFromLayout=");
    paramPrintWriter.print(mFromLayout);
    paramPrintWriter.print(" mInLayout=");
    paramPrintWriter.println(mInLayout);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mHidden=");
    paramPrintWriter.print(mHidden);
    paramPrintWriter.print(" mDetached=");
    paramPrintWriter.print(mDetached);
    paramPrintWriter.print(" mMenuVisible=");
    paramPrintWriter.print(mMenuVisible);
    paramPrintWriter.print(" mHasMenu=");
    paramPrintWriter.println(mHasMenu);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mRetainInstance=");
    paramPrintWriter.print(mRetainInstance);
    paramPrintWriter.print(" mRetaining=");
    paramPrintWriter.print(mRetaining);
    paramPrintWriter.print(" mUserVisibleHint=");
    paramPrintWriter.println(mUserVisibleHint);
    if (mFragmentManager != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mFragmentManager=");
      paramPrintWriter.println(mFragmentManager);
    }
    if (mHost != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mHost=");
      paramPrintWriter.println(mHost);
    }
    if (mParentFragment != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mParentFragment=");
      paramPrintWriter.println(mParentFragment);
    }
    if (mArguments != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mArguments=");
      paramPrintWriter.println(mArguments);
    }
    if (mSavedFragmentState != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSavedFragmentState=");
      paramPrintWriter.println(mSavedFragmentState);
    }
    if (mSavedViewState != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mSavedViewState=");
      paramPrintWriter.println(mSavedViewState);
    }
    if (mTarget != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mTarget=");
      paramPrintWriter.print(mTarget);
      paramPrintWriter.print(" mTargetRequestCode=");
      paramPrintWriter.println(mTargetRequestCode);
    }
    if (mNextAnim != 0)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mNextAnim=");
      paramPrintWriter.println(mNextAnim);
    }
    if (mContainer != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mContainer=");
      paramPrintWriter.println(mContainer);
    }
    if (mView != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mView=");
      paramPrintWriter.println(mView);
    }
    if (mInnerView != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mInnerView=");
      paramPrintWriter.println(mView);
    }
    if (mAnimatingAway != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mAnimatingAway=");
      paramPrintWriter.println(mAnimatingAway);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mStateAfterAnimating=");
      paramPrintWriter.println(mStateAfterAnimating);
    }
    if (mLoaderManager != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Loader Manager:");
      mLoaderManager.dump(paramString + "  ", paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
    if (mChildFragmentManager != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Child " + mChildFragmentManager + ":");
      mChildFragmentManager.dump(paramString + "  ", paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    return super.equals(paramObject);
  }
  
  Fragment findFragmentByWho(String paramString)
  {
    if (paramString.equals(mWho)) {
      return this;
    }
    if (mChildFragmentManager != null) {
      return mChildFragmentManager.findFragmentByWho(paramString);
    }
    return null;
  }
  
  public final FragmentActivity getActivity()
  {
    if (mHost == null) {
      return null;
    }
    return (FragmentActivity)mHost.getActivity();
  }
  
  public boolean getAllowEnterTransitionOverlap()
  {
    if (mAllowEnterTransitionOverlap == null) {
      return true;
    }
    return mAllowEnterTransitionOverlap.booleanValue();
  }
  
  public boolean getAllowReturnTransitionOverlap()
  {
    if (mAllowReturnTransitionOverlap == null) {
      return true;
    }
    return mAllowReturnTransitionOverlap.booleanValue();
  }
  
  public final Bundle getArguments()
  {
    return mArguments;
  }
  
  public final FragmentManager getChildFragmentManager()
  {
    if (mChildFragmentManager == null)
    {
      instantiateChildFragmentManager();
      if (mState < 5) {
        break label31;
      }
      mChildFragmentManager.dispatchResume();
    }
    for (;;)
    {
      return mChildFragmentManager;
      label31:
      if (mState >= 4) {
        mChildFragmentManager.dispatchStart();
      } else if (mState >= 2) {
        mChildFragmentManager.dispatchActivityCreated();
      } else if (mState >= 1) {
        mChildFragmentManager.dispatchCreate();
      }
    }
  }
  
  public Context getContext()
  {
    if (mHost == null) {
      return null;
    }
    return mHost.getContext();
  }
  
  public Object getEnterTransition()
  {
    return mEnterTransition;
  }
  
  public Object getExitTransition()
  {
    return mExitTransition;
  }
  
  public final FragmentManager getFragmentManager()
  {
    return mFragmentManager;
  }
  
  public final Object getHost()
  {
    if (mHost == null) {
      return null;
    }
    return mHost.onGetHost();
  }
  
  public final int getId()
  {
    return mFragmentId;
  }
  
  public LayoutInflater getLayoutInflater(Bundle paramBundle)
  {
    LayoutInflater localLayoutInflater = mHost.onGetLayoutInflater();
    getChildFragmentManager();
    LayoutInflaterCompat.setFactory(localLayoutInflater, mChildFragmentManager.getLayoutInflaterFactory());
    return localLayoutInflater;
  }
  
  public LoaderManager getLoaderManager()
  {
    if (mLoaderManager != null) {
      return mLoaderManager;
    }
    if (mHost == null) {
      throw new IllegalStateException("Fragment " + this + " not attached to Activity");
    }
    mCheckedForLoaderManager = true;
    mLoaderManager = mHost.getLoaderManager(mWho, mLoadersStarted, true);
    return mLoaderManager;
  }
  
  public final Fragment getParentFragment()
  {
    return mParentFragment;
  }
  
  public Object getReenterTransition()
  {
    if (mReenterTransition == USE_DEFAULT_TRANSITION) {
      return getExitTransition();
    }
    return mReenterTransition;
  }
  
  public final Resources getResources()
  {
    if (mHost == null) {
      throw new IllegalStateException("Fragment " + this + " not attached to Activity");
    }
    return mHost.getContext().getResources();
  }
  
  public final boolean getRetainInstance()
  {
    return mRetainInstance;
  }
  
  public Object getReturnTransition()
  {
    if (mReturnTransition == USE_DEFAULT_TRANSITION) {
      return getEnterTransition();
    }
    return mReturnTransition;
  }
  
  public Object getSharedElementEnterTransition()
  {
    return mSharedElementEnterTransition;
  }
  
  public Object getSharedElementReturnTransition()
  {
    if (mSharedElementReturnTransition == USE_DEFAULT_TRANSITION) {
      return getSharedElementEnterTransition();
    }
    return mSharedElementReturnTransition;
  }
  
  public final String getString(@StringRes int paramInt)
  {
    return getResources().getString(paramInt);
  }
  
  public final String getString(@StringRes int paramInt, Object... paramVarArgs)
  {
    return getResources().getString(paramInt, paramVarArgs);
  }
  
  public final String getTag()
  {
    return mTag;
  }
  
  public final Fragment getTargetFragment()
  {
    return mTarget;
  }
  
  public final int getTargetRequestCode()
  {
    return mTargetRequestCode;
  }
  
  public final CharSequence getText(@StringRes int paramInt)
  {
    return getResources().getText(paramInt);
  }
  
  public boolean getUserVisibleHint()
  {
    return mUserVisibleHint;
  }
  
  @Nullable
  public View getView()
  {
    return mView;
  }
  
  public final boolean hasOptionsMenu()
  {
    return mHasMenu;
  }
  
  public final int hashCode()
  {
    return super.hashCode();
  }
  
  void initState()
  {
    mIndex = -1;
    mWho = null;
    mAdded = false;
    mRemoving = false;
    mFromLayout = false;
    mInLayout = false;
    mRestored = false;
    mBackStackNesting = 0;
    mFragmentManager = null;
    mChildFragmentManager = null;
    mHost = null;
    mFragmentId = 0;
    mContainerId = 0;
    mTag = null;
    mHidden = false;
    mDetached = false;
    mRetaining = false;
    mLoaderManager = null;
    mLoadersStarted = false;
    mCheckedForLoaderManager = false;
  }
  
  void instantiateChildFragmentManager()
  {
    mChildFragmentManager = new FragmentManagerImpl();
    mChildFragmentManager.attachController(mHost, new FragmentContainer()
    {
      @Nullable
      public View onFindViewById(int paramAnonymousInt)
      {
        if (mView == null) {
          throw new IllegalStateException("Fragment does not have a view");
        }
        return mView.findViewById(paramAnonymousInt);
      }
      
      public boolean onHasView()
      {
        return mView != null;
      }
    }, this);
  }
  
  public final boolean isAdded()
  {
    return (mHost != null) && (mAdded);
  }
  
  public final boolean isDetached()
  {
    return mDetached;
  }
  
  public final boolean isHidden()
  {
    return mHidden;
  }
  
  final boolean isInBackStack()
  {
    return mBackStackNesting > 0;
  }
  
  public final boolean isInLayout()
  {
    return mInLayout;
  }
  
  public final boolean isMenuVisible()
  {
    return mMenuVisible;
  }
  
  public final boolean isRemoving()
  {
    return mRemoving;
  }
  
  public final boolean isResumed()
  {
    return mState >= 5;
  }
  
  public final boolean isVisible()
  {
    return (isAdded()) && (!isHidden()) && (mView != null) && (mView.getWindowToken() != null) && (mView.getVisibility() == 0);
  }
  
  @CallSuper
  public void onActivityCreated(@Nullable Bundle paramBundle)
  {
    mCalled = true;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {}
  
  @Deprecated
  @CallSuper
  public void onAttach(Activity paramActivity)
  {
    mCalled = true;
  }
  
  @CallSuper
  public void onAttach(Context paramContext)
  {
    mCalled = true;
    if (mHost == null) {}
    for (Activity localActivity = null;; localActivity = mHost.getActivity())
    {
      if (localActivity != null)
      {
        mCalled = false;
        onAttach(localActivity);
      }
      return;
    }
  }
  
  public void onAttachFragment(Fragment paramFragment) {}
  
  @CallSuper
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    mCalled = true;
  }
  
  public boolean onContextItemSelected(MenuItem paramMenuItem)
  {
    return false;
  }
  
  @CallSuper
  public void onCreate(@Nullable Bundle paramBundle)
  {
    mCalled = true;
    restoreChildFragmentState(paramBundle);
    if ((mChildFragmentManager != null) && (!mChildFragmentManager.isStateAtLeast(1))) {
      mChildFragmentManager.dispatchCreate();
    }
  }
  
  public Animation onCreateAnimation(int paramInt1, boolean paramBoolean, int paramInt2)
  {
    return null;
  }
  
  public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo)
  {
    getActivity().onCreateContextMenu(paramContextMenu, paramView, paramContextMenuInfo);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater) {}
  
  @Nullable
  public View onCreateView(LayoutInflater paramLayoutInflater, @Nullable ViewGroup paramViewGroup, @Nullable Bundle paramBundle)
  {
    return null;
  }
  
  @CallSuper
  public void onDestroy()
  {
    mCalled = true;
    if (!mCheckedForLoaderManager)
    {
      mCheckedForLoaderManager = true;
      mLoaderManager = mHost.getLoaderManager(mWho, mLoadersStarted, false);
    }
    if (mLoaderManager != null) {
      mLoaderManager.doDestroy();
    }
  }
  
  public void onDestroyOptionsMenu() {}
  
  @CallSuper
  public void onDestroyView()
  {
    mCalled = true;
  }
  
  @CallSuper
  public void onDetach()
  {
    mCalled = true;
  }
  
  public void onHiddenChanged(boolean paramBoolean) {}
  
  @Deprecated
  @CallSuper
  public void onInflate(Activity paramActivity, AttributeSet paramAttributeSet, Bundle paramBundle)
  {
    mCalled = true;
  }
  
  @CallSuper
  public void onInflate(Context paramContext, AttributeSet paramAttributeSet, Bundle paramBundle)
  {
    mCalled = true;
    if (mHost == null) {}
    for (Activity localActivity = null;; localActivity = mHost.getActivity())
    {
      if (localActivity != null)
      {
        mCalled = false;
        onInflate(localActivity, paramAttributeSet, paramBundle);
      }
      return;
    }
  }
  
  @CallSuper
  public void onLowMemory()
  {
    mCalled = true;
  }
  
  public void onMultiWindowModeChanged(boolean paramBoolean) {}
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    return false;
  }
  
  public void onOptionsMenuClosed(Menu paramMenu) {}
  
  @CallSuper
  public void onPause()
  {
    mCalled = true;
  }
  
  public void onPictureInPictureModeChanged(boolean paramBoolean) {}
  
  public void onPrepareOptionsMenu(Menu paramMenu) {}
  
  public void onRequestPermissionsResult(int paramInt, @NonNull String[] paramArrayOfString, @NonNull int[] paramArrayOfInt) {}
  
  @CallSuper
  public void onResume()
  {
    mCalled = true;
  }
  
  public void onSaveInstanceState(Bundle paramBundle) {}
  
  @CallSuper
  public void onStart()
  {
    mCalled = true;
    if (!mLoadersStarted)
    {
      mLoadersStarted = true;
      if (!mCheckedForLoaderManager)
      {
        mCheckedForLoaderManager = true;
        mLoaderManager = mHost.getLoaderManager(mWho, mLoadersStarted, false);
      }
      if (mLoaderManager != null) {
        mLoaderManager.doStart();
      }
    }
  }
  
  @CallSuper
  public void onStop()
  {
    mCalled = true;
  }
  
  public void onViewCreated(View paramView, @Nullable Bundle paramBundle) {}
  
  @CallSuper
  public void onViewStateRestored(@Nullable Bundle paramBundle)
  {
    mCalled = true;
  }
  
  void performActivityCreated(Bundle paramBundle)
  {
    if (mChildFragmentManager != null) {
      mChildFragmentManager.noteStateNotSaved();
    }
    mState = 2;
    mCalled = false;
    onActivityCreated(paramBundle);
    if (!mCalled) {
      throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onActivityCreated()");
    }
    if (mChildFragmentManager != null) {
      mChildFragmentManager.dispatchActivityCreated();
    }
  }
  
  void performConfigurationChanged(Configuration paramConfiguration)
  {
    onConfigurationChanged(paramConfiguration);
    if (mChildFragmentManager != null) {
      mChildFragmentManager.dispatchConfigurationChanged(paramConfiguration);
    }
  }
  
  boolean performContextItemSelected(MenuItem paramMenuItem)
  {
    if (!mHidden)
    {
      if (onContextItemSelected(paramMenuItem)) {}
      while ((mChildFragmentManager != null) && (mChildFragmentManager.dispatchContextItemSelected(paramMenuItem))) {
        return true;
      }
    }
    return false;
  }
  
  void performCreate(Bundle paramBundle)
  {
    if (mChildFragmentManager != null) {
      mChildFragmentManager.noteStateNotSaved();
    }
    mState = 1;
    mCalled = false;
    onCreate(paramBundle);
    if (!mCalled) {
      throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onCreate()");
    }
  }
  
  boolean performCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    boolean bool1 = mHidden;
    boolean bool2 = false;
    if (!bool1)
    {
      boolean bool3 = mHasMenu;
      bool2 = false;
      if (bool3)
      {
        boolean bool4 = mMenuVisible;
        bool2 = false;
        if (bool4)
        {
          bool2 = true;
          onCreateOptionsMenu(paramMenu, paramMenuInflater);
        }
      }
      if (mChildFragmentManager != null) {
        bool2 |= mChildFragmentManager.dispatchCreateOptionsMenu(paramMenu, paramMenuInflater);
      }
    }
    return bool2;
  }
  
  View performCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    if (mChildFragmentManager != null) {
      mChildFragmentManager.noteStateNotSaved();
    }
    return onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
  }
  
  void performDestroy()
  {
    if (mChildFragmentManager != null) {
      mChildFragmentManager.dispatchDestroy();
    }
    mState = 0;
    mCalled = false;
    onDestroy();
    if (!mCalled) {
      throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onDestroy()");
    }
    mChildFragmentManager = null;
  }
  
  void performDestroyView()
  {
    if (mChildFragmentManager != null) {
      mChildFragmentManager.dispatchDestroyView();
    }
    mState = 1;
    mCalled = false;
    onDestroyView();
    if (!mCalled) {
      throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onDestroyView()");
    }
    if (mLoaderManager != null) {
      mLoaderManager.doReportNextStart();
    }
  }
  
  void performDetach()
  {
    mCalled = false;
    onDetach();
    if (!mCalled) {
      throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onDetach()");
    }
    if (mChildFragmentManager != null)
    {
      if (!mRetaining) {
        throw new IllegalStateException("Child FragmentManager of " + this + " was not " + " destroyed and this fragment is not retaining instance");
      }
      mChildFragmentManager.dispatchDestroy();
      mChildFragmentManager = null;
    }
  }
  
  void performLowMemory()
  {
    onLowMemory();
    if (mChildFragmentManager != null) {
      mChildFragmentManager.dispatchLowMemory();
    }
  }
  
  void performMultiWindowModeChanged(boolean paramBoolean)
  {
    onMultiWindowModeChanged(paramBoolean);
    if (mChildFragmentManager != null) {
      mChildFragmentManager.dispatchMultiWindowModeChanged(paramBoolean);
    }
  }
  
  boolean performOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (!mHidden)
    {
      if ((mHasMenu) && (mMenuVisible) && (onOptionsItemSelected(paramMenuItem))) {}
      while ((mChildFragmentManager != null) && (mChildFragmentManager.dispatchOptionsItemSelected(paramMenuItem))) {
        return true;
      }
    }
    return false;
  }
  
  void performOptionsMenuClosed(Menu paramMenu)
  {
    if (!mHidden)
    {
      if ((mHasMenu) && (mMenuVisible)) {
        onOptionsMenuClosed(paramMenu);
      }
      if (mChildFragmentManager != null) {
        mChildFragmentManager.dispatchOptionsMenuClosed(paramMenu);
      }
    }
  }
  
  void performPause()
  {
    if (mChildFragmentManager != null) {
      mChildFragmentManager.dispatchPause();
    }
    mState = 4;
    mCalled = false;
    onPause();
    if (!mCalled) {
      throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onPause()");
    }
  }
  
  void performPictureInPictureModeChanged(boolean paramBoolean)
  {
    onPictureInPictureModeChanged(paramBoolean);
    if (mChildFragmentManager != null) {
      mChildFragmentManager.dispatchPictureInPictureModeChanged(paramBoolean);
    }
  }
  
  boolean performPrepareOptionsMenu(Menu paramMenu)
  {
    boolean bool1 = mHidden;
    boolean bool2 = false;
    if (!bool1)
    {
      boolean bool3 = mHasMenu;
      bool2 = false;
      if (bool3)
      {
        boolean bool4 = mMenuVisible;
        bool2 = false;
        if (bool4)
        {
          bool2 = true;
          onPrepareOptionsMenu(paramMenu);
        }
      }
      if (mChildFragmentManager != null) {
        bool2 |= mChildFragmentManager.dispatchPrepareOptionsMenu(paramMenu);
      }
    }
    return bool2;
  }
  
  void performReallyStop()
  {
    if (mChildFragmentManager != null) {
      mChildFragmentManager.dispatchReallyStop();
    }
    mState = 2;
    if (mLoadersStarted)
    {
      mLoadersStarted = false;
      if (!mCheckedForLoaderManager)
      {
        mCheckedForLoaderManager = true;
        mLoaderManager = mHost.getLoaderManager(mWho, mLoadersStarted, false);
      }
      if (mLoaderManager != null)
      {
        if (!mHost.getRetainLoaders()) {
          break label88;
        }
        mLoaderManager.doRetain();
      }
    }
    return;
    label88:
    mLoaderManager.doStop();
  }
  
  void performResume()
  {
    if (mChildFragmentManager != null)
    {
      mChildFragmentManager.noteStateNotSaved();
      mChildFragmentManager.execPendingActions();
    }
    mState = 5;
    mCalled = false;
    onResume();
    if (!mCalled) {
      throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onResume()");
    }
    if (mChildFragmentManager != null)
    {
      mChildFragmentManager.dispatchResume();
      mChildFragmentManager.execPendingActions();
    }
  }
  
  void performSaveInstanceState(Bundle paramBundle)
  {
    onSaveInstanceState(paramBundle);
    if (mChildFragmentManager != null)
    {
      Parcelable localParcelable = mChildFragmentManager.saveAllState();
      if (localParcelable != null) {
        paramBundle.putParcelable("android:support:fragments", localParcelable);
      }
    }
  }
  
  void performStart()
  {
    if (mChildFragmentManager != null)
    {
      mChildFragmentManager.noteStateNotSaved();
      mChildFragmentManager.execPendingActions();
    }
    mState = 4;
    mCalled = false;
    onStart();
    if (!mCalled) {
      throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onStart()");
    }
    if (mChildFragmentManager != null) {
      mChildFragmentManager.dispatchStart();
    }
    if (mLoaderManager != null) {
      mLoaderManager.doReportStart();
    }
  }
  
  void performStop()
  {
    if (mChildFragmentManager != null) {
      mChildFragmentManager.dispatchStop();
    }
    mState = 3;
    mCalled = false;
    onStop();
    if (!mCalled) {
      throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onStop()");
    }
  }
  
  public void registerForContextMenu(View paramView)
  {
    paramView.setOnCreateContextMenuListener(this);
  }
  
  public final void requestPermissions(@NonNull String[] paramArrayOfString, int paramInt)
  {
    if (mHost == null) {
      throw new IllegalStateException("Fragment " + this + " not attached to Activity");
    }
    mHost.onRequestPermissionsFromFragment(this, paramArrayOfString, paramInt);
  }
  
  void restoreChildFragmentState(@Nullable Bundle paramBundle)
  {
    if (paramBundle != null)
    {
      Parcelable localParcelable = paramBundle.getParcelable("android:support:fragments");
      if (localParcelable != null)
      {
        if (mChildFragmentManager == null) {
          instantiateChildFragmentManager();
        }
        mChildFragmentManager.restoreAllState(localParcelable, mChildNonConfig);
        mChildNonConfig = null;
        mChildFragmentManager.dispatchCreate();
      }
    }
  }
  
  final void restoreViewState(Bundle paramBundle)
  {
    if (mSavedViewState != null)
    {
      mInnerView.restoreHierarchyState(mSavedViewState);
      mSavedViewState = null;
    }
    mCalled = false;
    onViewStateRestored(paramBundle);
    if (!mCalled) {
      throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onViewStateRestored()");
    }
  }
  
  public void setAllowEnterTransitionOverlap(boolean paramBoolean)
  {
    mAllowEnterTransitionOverlap = Boolean.valueOf(paramBoolean);
  }
  
  public void setAllowReturnTransitionOverlap(boolean paramBoolean)
  {
    mAllowReturnTransitionOverlap = Boolean.valueOf(paramBoolean);
  }
  
  public void setArguments(Bundle paramBundle)
  {
    if (mIndex >= 0) {
      throw new IllegalStateException("Fragment already active");
    }
    mArguments = paramBundle;
  }
  
  public void setEnterSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    mEnterTransitionCallback = paramSharedElementCallback;
  }
  
  public void setEnterTransition(Object paramObject)
  {
    mEnterTransition = paramObject;
  }
  
  public void setExitSharedElementCallback(SharedElementCallback paramSharedElementCallback)
  {
    mExitTransitionCallback = paramSharedElementCallback;
  }
  
  public void setExitTransition(Object paramObject)
  {
    mExitTransition = paramObject;
  }
  
  public void setHasOptionsMenu(boolean paramBoolean)
  {
    if (mHasMenu != paramBoolean)
    {
      mHasMenu = paramBoolean;
      if ((isAdded()) && (!isHidden())) {
        mHost.onSupportInvalidateOptionsMenu();
      }
    }
  }
  
  final void setIndex(int paramInt, Fragment paramFragment)
  {
    mIndex = paramInt;
    if (paramFragment != null)
    {
      mWho = (mWho + ":" + mIndex);
      return;
    }
    mWho = ("android:fragment:" + mIndex);
  }
  
  public void setInitialSavedState(SavedState paramSavedState)
  {
    if (mIndex >= 0) {
      throw new IllegalStateException("Fragment already active");
    }
    if ((paramSavedState != null) && (mState != null)) {}
    for (Bundle localBundle = mState;; localBundle = null)
    {
      mSavedFragmentState = localBundle;
      return;
    }
  }
  
  public void setMenuVisibility(boolean paramBoolean)
  {
    if (mMenuVisible != paramBoolean)
    {
      mMenuVisible = paramBoolean;
      if ((mHasMenu) && (isAdded()) && (!isHidden())) {
        mHost.onSupportInvalidateOptionsMenu();
      }
    }
  }
  
  public void setReenterTransition(Object paramObject)
  {
    mReenterTransition = paramObject;
  }
  
  public void setRetainInstance(boolean paramBoolean)
  {
    mRetainInstance = paramBoolean;
  }
  
  public void setReturnTransition(Object paramObject)
  {
    mReturnTransition = paramObject;
  }
  
  public void setSharedElementEnterTransition(Object paramObject)
  {
    mSharedElementEnterTransition = paramObject;
  }
  
  public void setSharedElementReturnTransition(Object paramObject)
  {
    mSharedElementReturnTransition = paramObject;
  }
  
  public void setTargetFragment(Fragment paramFragment, int paramInt)
  {
    mTarget = paramFragment;
    mTargetRequestCode = paramInt;
  }
  
  public void setUserVisibleHint(boolean paramBoolean)
  {
    if ((!mUserVisibleHint) && (paramBoolean) && (mState < 4) && (mFragmentManager != null) && (isAdded())) {
      mFragmentManager.performPendingDeferredStart(this);
    }
    mUserVisibleHint = paramBoolean;
    if ((mState < 4) && (!paramBoolean)) {}
    for (boolean bool = true;; bool = false)
    {
      mDeferStart = bool;
      return;
    }
  }
  
  public boolean shouldShowRequestPermissionRationale(@NonNull String paramString)
  {
    if (mHost != null) {
      return mHost.onShouldShowRequestPermissionRationale(paramString);
    }
    return false;
  }
  
  public void startActivity(Intent paramIntent)
  {
    startActivity(paramIntent, null);
  }
  
  public void startActivity(Intent paramIntent, @Nullable Bundle paramBundle)
  {
    if (mHost == null) {
      throw new IllegalStateException("Fragment " + this + " not attached to Activity");
    }
    mHost.onStartActivityFromFragment(this, paramIntent, -1, paramBundle);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    startActivityForResult(paramIntent, paramInt, null);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt, @Nullable Bundle paramBundle)
  {
    if (mHost == null) {
      throw new IllegalStateException("Fragment " + this + " not attached to Activity");
    }
    mHost.onStartActivityFromFragment(this, paramIntent, paramInt, paramBundle);
  }
  
  public void startIntentSenderForResult(IntentSender paramIntentSender, int paramInt1, @Nullable Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle)
    throws IntentSender.SendIntentException
  {
    if (mHost == null) {
      throw new IllegalStateException("Fragment " + this + " not attached to Activity");
    }
    mHost.onStartIntentSenderFromFragment(this, paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    DebugUtils.buildShortClassTag(this, localStringBuilder);
    if (mIndex >= 0)
    {
      localStringBuilder.append(" #");
      localStringBuilder.append(mIndex);
    }
    if (mFragmentId != 0)
    {
      localStringBuilder.append(" id=0x");
      localStringBuilder.append(Integer.toHexString(mFragmentId));
    }
    if (mTag != null)
    {
      localStringBuilder.append(" ");
      localStringBuilder.append(mTag);
    }
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void unregisterForContextMenu(View paramView)
  {
    paramView.setOnCreateContextMenuListener(null);
  }
  
  public static class InstantiationException
    extends RuntimeException
  {
    public InstantiationException(String paramString, Exception paramException)
    {
      super(paramException);
    }
  }
  
  public static class SavedState
    implements Parcelable
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public Fragment.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new Fragment.SavedState(paramAnonymousParcel, null);
      }
      
      public Fragment.SavedState[] newArray(int paramAnonymousInt)
      {
        return new Fragment.SavedState[paramAnonymousInt];
      }
    };
    final Bundle mState;
    
    SavedState(Bundle paramBundle)
    {
      mState = paramBundle;
    }
    
    SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      mState = paramParcel.readBundle();
      if ((paramClassLoader != null) && (mState != null)) {
        mState.setClassLoader(paramClassLoader);
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeBundle(mState);
    }
  }
}
