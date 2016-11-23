package android.support.v4.app;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LogWriter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

final class BackStackRecord
  extends FragmentTransaction
  implements FragmentManager.BackStackEntry, Runnable
{
  static final int OP_ADD = 1;
  static final int OP_ATTACH = 7;
  static final int OP_DETACH = 6;
  static final int OP_HIDE = 4;
  static final int OP_NULL = 0;
  static final int OP_REMOVE = 3;
  static final int OP_REPLACE = 2;
  static final int OP_SHOW = 5;
  static final boolean SUPPORTS_TRANSITIONS = false;
  static final String TAG = "FragmentManager";
  boolean mAddToBackStack;
  boolean mAllowAddToBackStack = true;
  int mBreadCrumbShortTitleRes;
  CharSequence mBreadCrumbShortTitleText;
  int mBreadCrumbTitleRes;
  CharSequence mBreadCrumbTitleText;
  boolean mCommitted;
  int mEnterAnim;
  int mExitAnim;
  Op mHead;
  int mIndex = -1;
  final FragmentManagerImpl mManager;
  String mName;
  int mNumOp;
  int mPopEnterAnim;
  int mPopExitAnim;
  ArrayList<String> mSharedElementSourceNames;
  ArrayList<String> mSharedElementTargetNames;
  Op mTail;
  int mTransition;
  int mTransitionStyle;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 21) {}
    for (boolean bool = true;; bool = false)
    {
      SUPPORTS_TRANSITIONS = bool;
      return;
    }
  }
  
  public BackStackRecord(FragmentManagerImpl paramFragmentManagerImpl)
  {
    mManager = paramFragmentManagerImpl;
  }
  
  private TransitionState beginTransition(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2, boolean paramBoolean)
  {
    TransitionState localTransitionState = new TransitionState();
    nonExistentView = new View(mManager.mHost.getContext());
    int i = 0;
    for (int j = 0; j < paramSparseArray1.size(); j++) {
      if (configureTransitions(paramSparseArray1.keyAt(j), localTransitionState, paramBoolean, paramSparseArray1, paramSparseArray2)) {
        i = 1;
      }
    }
    for (int k = 0; k < paramSparseArray2.size(); k++)
    {
      int m = paramSparseArray2.keyAt(k);
      if ((paramSparseArray1.get(m) == null) && (configureTransitions(m, localTransitionState, paramBoolean, paramSparseArray1, paramSparseArray2))) {
        i = 1;
      }
    }
    if (i == 0) {
      localTransitionState = null;
    }
    return localTransitionState;
  }
  
  private void calculateFragments(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    if (!mManager.mContainer.onHasView()) {}
    Op localOp;
    do
    {
      return;
      localOp = mHead;
    } while (localOp == null);
    switch (cmd)
    {
    }
    for (;;)
    {
      localOp = next;
      break;
      setLastIn(paramSparseArray1, paramSparseArray2, fragment);
      continue;
      Fragment localFragment1 = fragment;
      if (mManager.mAdded != null)
      {
        int i = 0;
        if (i < mManager.mAdded.size())
        {
          Fragment localFragment2 = (Fragment)mManager.mAdded.get(i);
          if ((localFragment1 == null) || (mContainerId == mContainerId))
          {
            if (localFragment2 != localFragment1) {
              break label183;
            }
            localFragment1 = null;
            paramSparseArray2.remove(mContainerId);
          }
          for (;;)
          {
            i++;
            break;
            label183:
            setFirstOut(paramSparseArray1, paramSparseArray2, localFragment2);
          }
        }
      }
      setLastIn(paramSparseArray1, paramSparseArray2, fragment);
      continue;
      setFirstOut(paramSparseArray1, paramSparseArray2, fragment);
      continue;
      setFirstOut(paramSparseArray1, paramSparseArray2, fragment);
      continue;
      setLastIn(paramSparseArray1, paramSparseArray2, fragment);
      continue;
      setFirstOut(paramSparseArray1, paramSparseArray2, fragment);
      continue;
      setLastIn(paramSparseArray1, paramSparseArray2, fragment);
    }
  }
  
  private static Object captureExitingViews(Object paramObject, Fragment paramFragment, ArrayList<View> paramArrayList, ArrayMap<String, View> paramArrayMap, View paramView)
  {
    if (paramObject != null) {
      paramObject = FragmentTransitionCompat21.captureExitingViews(paramObject, paramFragment.getView(), paramArrayList, paramArrayMap, paramView);
    }
    return paramObject;
  }
  
  private boolean configureTransitions(int paramInt, TransitionState paramTransitionState, boolean paramBoolean, SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    ViewGroup localViewGroup = (ViewGroup)mManager.mContainer.onFindViewById(paramInt);
    if (localViewGroup == null) {
      return false;
    }
    final Fragment localFragment1 = (Fragment)paramSparseArray2.get(paramInt);
    Fragment localFragment2 = (Fragment)paramSparseArray1.get(paramInt);
    Object localObject1 = getEnterTransition(localFragment1, paramBoolean);
    Object localObject2 = getSharedElementTransition(localFragment1, localFragment2, paramBoolean);
    Object localObject3 = getExitTransition(localFragment2, paramBoolean);
    ArrayList localArrayList1 = new ArrayList();
    ArrayMap localArrayMap1 = null;
    if (localObject2 != null)
    {
      localArrayMap1 = remapSharedElements(paramTransitionState, localFragment2, paramBoolean);
      if (localArrayMap1.isEmpty())
      {
        localObject2 = null;
        localArrayMap1 = null;
      }
    }
    else
    {
      if ((localObject1 != null) || (localObject2 != null) || (localObject3 != null)) {
        break label216;
      }
      return false;
    }
    if (paramBoolean) {}
    for (SharedElementCallback localSharedElementCallback = mEnterTransitionCallback;; localSharedElementCallback = mEnterTransitionCallback)
    {
      if (localSharedElementCallback != null)
      {
        ArrayList localArrayList5 = new ArrayList(localArrayMap1.keySet());
        ArrayList localArrayList6 = new ArrayList(localArrayMap1.values());
        localSharedElementCallback.onSharedElementStart(localArrayList5, localArrayList6, null);
      }
      prepareSharedElementTransition(paramTransitionState, localViewGroup, localObject2, localFragment1, localFragment2, paramBoolean, localArrayList1, localObject1, localObject3);
      break;
    }
    label216:
    ArrayList localArrayList2 = new ArrayList();
    View localView1 = nonExistentView;
    Object localObject4 = captureExitingViews(localObject3, localFragment2, localArrayList2, localArrayMap1, localView1);
    if ((mSharedElementTargetNames != null) && (localArrayMap1 != null))
    {
      Object localObject6 = mSharedElementTargetNames.get(0);
      View localView4 = (View)localArrayMap1.get(localObject6);
      if (localView4 != null)
      {
        if (localObject4 != null) {
          FragmentTransitionCompat21.setEpicenter(localObject4, localView4);
        }
        if (localObject2 != null) {
          FragmentTransitionCompat21.setEpicenter(localObject2, localView4);
        }
      }
    }
    FragmentTransitionCompat21.ViewRetriever local1 = new FragmentTransitionCompat21.ViewRetriever()
    {
      public View getView()
      {
        return localFragment1.getView();
      }
    };
    ArrayList localArrayList3 = new ArrayList();
    ArrayMap localArrayMap2 = new ArrayMap();
    boolean bool = true;
    if (localFragment1 != null) {
      if (!paramBoolean) {
        break label502;
      }
    }
    label502:
    for (bool = localFragment1.getAllowReturnTransitionOverlap();; bool = localFragment1.getAllowEnterTransitionOverlap())
    {
      Object localObject5 = FragmentTransitionCompat21.mergeTransitions(localObject1, localObject4, localObject2, bool);
      if (localObject5 != null)
      {
        View localView2 = nonExistentView;
        FragmentTransitionCompat21.EpicenterView localEpicenterView = enteringEpicenterView;
        ArrayMap localArrayMap3 = nameOverrides;
        FragmentTransitionCompat21.addTransitionTargets(localObject1, localObject2, localObject4, localViewGroup, local1, localView2, localEpicenterView, localArrayMap3, localArrayList3, localArrayList2, localArrayMap1, localArrayMap2, localArrayList1);
        excludeHiddenFragmentsAfterEnter(localViewGroup, paramTransitionState, paramInt, localObject5);
        FragmentTransitionCompat21.excludeTarget(localObject5, nonExistentView, true);
        excludeHiddenFragments(paramTransitionState, paramInt, localObject5);
        FragmentTransitionCompat21.beginDelayedTransition(localViewGroup, localObject5);
        View localView3 = nonExistentView;
        ArrayList localArrayList4 = hiddenFragmentViews;
        FragmentTransitionCompat21.cleanupTransitions(localViewGroup, localView3, localObject1, localArrayList3, localObject4, localArrayList2, localObject2, localArrayList1, localObject5, localArrayList4, localArrayMap2);
      }
      if (localObject5 == null) {
        break;
      }
      return true;
    }
    return false;
  }
  
  private void doAddOp(int paramInt1, Fragment paramFragment, String paramString, int paramInt2)
  {
    Class localClass = paramFragment.getClass();
    int i = localClass.getModifiers();
    if ((localClass.isAnonymousClass()) || (!Modifier.isPublic(i)) || ((localClass.isMemberClass()) && (!Modifier.isStatic(i)))) {
      throw new IllegalStateException("Fragment " + localClass.getCanonicalName() + " must be a public static class to be  properly recreated from" + " instance state.");
    }
    mFragmentManager = mManager;
    if (paramString != null)
    {
      if ((mTag != null) && (!paramString.equals(mTag))) {
        throw new IllegalStateException("Can't change tag of fragment " + paramFragment + ": was " + mTag + " now " + paramString);
      }
      mTag = paramString;
    }
    if (paramInt1 != 0)
    {
      if (paramInt1 == -1) {
        throw new IllegalArgumentException("Can't add fragment " + paramFragment + " with tag " + paramString + " to container view with no id");
      }
      if ((mFragmentId != 0) && (mFragmentId != paramInt1)) {
        throw new IllegalStateException("Can't change container ID of fragment " + paramFragment + ": was " + mFragmentId + " now " + paramInt1);
      }
      mFragmentId = paramInt1;
      mContainerId = paramInt1;
    }
    Op localOp = new Op();
    cmd = paramInt2;
    fragment = paramFragment;
    addOp(localOp);
  }
  
  private void excludeHiddenFragmentsAfterEnter(final View paramView, final TransitionState paramTransitionState, final int paramInt, final Object paramObject)
  {
    paramView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        paramView.getViewTreeObserver().removeOnPreDrawListener(this);
        excludeHiddenFragments(paramTransitionState, paramInt, paramObject);
        return true;
      }
    });
  }
  
  private static Object getEnterTransition(Fragment paramFragment, boolean paramBoolean)
  {
    if (paramFragment == null) {
      return null;
    }
    if (paramBoolean) {}
    for (Object localObject = paramFragment.getReenterTransition();; localObject = paramFragment.getEnterTransition()) {
      return FragmentTransitionCompat21.cloneTransition(localObject);
    }
  }
  
  private static Object getExitTransition(Fragment paramFragment, boolean paramBoolean)
  {
    if (paramFragment == null) {
      return null;
    }
    if (paramBoolean) {}
    for (Object localObject = paramFragment.getReturnTransition();; localObject = paramFragment.getExitTransition()) {
      return FragmentTransitionCompat21.cloneTransition(localObject);
    }
  }
  
  private static Object getSharedElementTransition(Fragment paramFragment1, Fragment paramFragment2, boolean paramBoolean)
  {
    if ((paramFragment1 == null) || (paramFragment2 == null)) {
      return null;
    }
    if (paramBoolean) {}
    for (Object localObject = paramFragment2.getSharedElementReturnTransition();; localObject = paramFragment1.getSharedElementEnterTransition()) {
      return FragmentTransitionCompat21.wrapSharedElementTransition(localObject);
    }
  }
  
  private ArrayMap<String, View> mapEnteringSharedElements(TransitionState paramTransitionState, Fragment paramFragment, boolean paramBoolean)
  {
    ArrayMap localArrayMap = new ArrayMap();
    View localView = paramFragment.getView();
    if ((localView != null) && (mSharedElementSourceNames != null))
    {
      FragmentTransitionCompat21.findNamedViews(localArrayMap, localView);
      if (paramBoolean) {
        localArrayMap = remapNames(mSharedElementSourceNames, mSharedElementTargetNames, localArrayMap);
      }
    }
    else
    {
      return localArrayMap;
    }
    localArrayMap.retainAll(mSharedElementTargetNames);
    return localArrayMap;
  }
  
  private void prepareSharedElementTransition(final TransitionState paramTransitionState, final View paramView, final Object paramObject1, final Fragment paramFragment1, final Fragment paramFragment2, final boolean paramBoolean, final ArrayList<View> paramArrayList, final Object paramObject2, final Object paramObject3)
  {
    if (paramObject1 != null) {
      paramView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          paramView.getViewTreeObserver().removeOnPreDrawListener(this);
          FragmentTransitionCompat21.removeTargets(paramObject1, paramArrayList);
          paramArrayList.remove(paramTransitionStatenonExistentView);
          FragmentTransitionCompat21.excludeSharedElementViews(paramObject2, paramObject3, paramObject1, paramArrayList, false);
          paramArrayList.clear();
          ArrayMap localArrayMap = mapSharedElementsIn(paramTransitionState, paramBoolean, paramFragment1);
          FragmentTransitionCompat21.setSharedElementTargets(paramObject1, paramTransitionStatenonExistentView, localArrayMap, paramArrayList);
          setEpicenterIn(localArrayMap, paramTransitionState);
          callSharedElementEnd(paramTransitionState, paramFragment1, paramFragment2, paramBoolean, localArrayMap);
          FragmentTransitionCompat21.excludeSharedElementViews(paramObject2, paramObject3, paramObject1, paramArrayList, true);
          return true;
        }
      });
    }
  }
  
  private static ArrayMap<String, View> remapNames(ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2, ArrayMap<String, View> paramArrayMap)
  {
    if (paramArrayMap.isEmpty()) {
      return paramArrayMap;
    }
    ArrayMap localArrayMap = new ArrayMap();
    int i = paramArrayList1.size();
    for (int j = 0; j < i; j++)
    {
      View localView = (View)paramArrayMap.get(paramArrayList1.get(j));
      if (localView != null) {
        localArrayMap.put(paramArrayList2.get(j), localView);
      }
    }
    return localArrayMap;
  }
  
  private ArrayMap<String, View> remapSharedElements(TransitionState paramTransitionState, Fragment paramFragment, boolean paramBoolean)
  {
    ArrayMap localArrayMap = new ArrayMap();
    if (mSharedElementSourceNames != null)
    {
      FragmentTransitionCompat21.findNamedViews(localArrayMap, paramFragment.getView());
      if (!paramBoolean) {
        break label74;
      }
      localArrayMap.retainAll(mSharedElementTargetNames);
    }
    while (paramBoolean)
    {
      if (mEnterTransitionCallback != null) {
        mEnterTransitionCallback.onMapSharedElements(mSharedElementTargetNames, localArrayMap);
      }
      setBackNameOverrides(paramTransitionState, localArrayMap, false);
      return localArrayMap;
      label74:
      localArrayMap = remapNames(mSharedElementSourceNames, mSharedElementTargetNames, localArrayMap);
    }
    if (mExitTransitionCallback != null) {
      mExitTransitionCallback.onMapSharedElements(mSharedElementTargetNames, localArrayMap);
    }
    setNameOverrides(paramTransitionState, localArrayMap, false);
    return localArrayMap;
  }
  
  private void setBackNameOverrides(TransitionState paramTransitionState, ArrayMap<String, View> paramArrayMap, boolean paramBoolean)
  {
    int i;
    int j;
    label13:
    String str1;
    String str2;
    if (mSharedElementTargetNames == null)
    {
      i = 0;
      j = 0;
      if (j >= i) {
        return;
      }
      str1 = (String)mSharedElementSourceNames.get(j);
      View localView = (View)paramArrayMap.get((String)mSharedElementTargetNames.get(j));
      if (localView != null)
      {
        str2 = FragmentTransitionCompat21.getTransitionName(localView);
        if (!paramBoolean) {
          break label100;
        }
        setNameOverride(nameOverrides, str1, str2);
      }
    }
    for (;;)
    {
      j++;
      break label13;
      i = mSharedElementTargetNames.size();
      break;
      label100:
      setNameOverride(nameOverrides, str2, str1);
    }
  }
  
  private static void setFirstOut(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2, Fragment paramFragment)
  {
    if (paramFragment != null)
    {
      int i = mContainerId;
      if ((i != 0) && (!paramFragment.isHidden()))
      {
        if ((paramFragment.isAdded()) && (paramFragment.getView() != null) && (paramSparseArray1.get(i) == null)) {
          paramSparseArray1.put(i, paramFragment);
        }
        if (paramSparseArray2.get(i) == paramFragment) {
          paramSparseArray2.remove(i);
        }
      }
    }
  }
  
  private void setLastIn(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2, Fragment paramFragment)
  {
    if (paramFragment != null)
    {
      int i = mContainerId;
      if (i != 0)
      {
        if (!paramFragment.isAdded()) {
          paramSparseArray2.put(i, paramFragment);
        }
        if (paramSparseArray1.get(i) == paramFragment) {
          paramSparseArray1.remove(i);
        }
      }
      if ((mState < 1) && (mManager.mCurState >= 1))
      {
        mManager.makeActive(paramFragment);
        mManager.moveToState(paramFragment, 1, 0, 0, false);
      }
    }
  }
  
  private static void setNameOverride(ArrayMap<String, String> paramArrayMap, String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString2 != null)) {}
    for (int i = 0; i < paramArrayMap.size(); i++) {
      if (paramString1.equals(paramArrayMap.valueAt(i)))
      {
        paramArrayMap.setValueAt(i, paramString2);
        return;
      }
    }
    paramArrayMap.put(paramString1, paramString2);
  }
  
  private void setNameOverrides(TransitionState paramTransitionState, ArrayMap<String, View> paramArrayMap, boolean paramBoolean)
  {
    int i = paramArrayMap.size();
    int j = 0;
    if (j < i)
    {
      String str1 = (String)paramArrayMap.keyAt(j);
      String str2 = FragmentTransitionCompat21.getTransitionName((View)paramArrayMap.valueAt(j));
      if (paramBoolean) {
        setNameOverride(nameOverrides, str1, str2);
      }
      for (;;)
      {
        j++;
        break;
        setNameOverride(nameOverrides, str2, str1);
      }
    }
  }
  
  private static void setNameOverrides(TransitionState paramTransitionState, ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2)
  {
    if (paramArrayList1 != null) {
      for (int i = 0; i < paramArrayList1.size(); i++)
      {
        String str1 = (String)paramArrayList1.get(i);
        String str2 = (String)paramArrayList2.get(i);
        setNameOverride(nameOverrides, str1, str2);
      }
    }
  }
  
  public FragmentTransaction add(int paramInt, Fragment paramFragment)
  {
    doAddOp(paramInt, paramFragment, null, 1);
    return this;
  }
  
  public FragmentTransaction add(int paramInt, Fragment paramFragment, String paramString)
  {
    doAddOp(paramInt, paramFragment, paramString, 1);
    return this;
  }
  
  public FragmentTransaction add(Fragment paramFragment, String paramString)
  {
    doAddOp(0, paramFragment, paramString, 1);
    return this;
  }
  
  void addOp(Op paramOp)
  {
    if (mHead == null)
    {
      mTail = paramOp;
      mHead = paramOp;
    }
    for (;;)
    {
      enterAnim = mEnterAnim;
      exitAnim = mExitAnim;
      popEnterAnim = mPopEnterAnim;
      popExitAnim = mPopExitAnim;
      mNumOp = (1 + mNumOp);
      return;
      prev = mTail;
      mTail.next = paramOp;
      mTail = paramOp;
    }
  }
  
  public FragmentTransaction addSharedElement(View paramView, String paramString)
  {
    if (SUPPORTS_TRANSITIONS)
    {
      String str = FragmentTransitionCompat21.getTransitionName(paramView);
      if (str == null) {
        throw new IllegalArgumentException("Unique transitionNames are required for all sharedElements");
      }
      if (mSharedElementSourceNames == null)
      {
        mSharedElementSourceNames = new ArrayList();
        mSharedElementTargetNames = new ArrayList();
      }
      mSharedElementSourceNames.add(str);
      mSharedElementTargetNames.add(paramString);
    }
    return this;
  }
  
  public FragmentTransaction addToBackStack(String paramString)
  {
    if (!mAllowAddToBackStack) {
      throw new IllegalStateException("This FragmentTransaction is not allowed to be added to the back stack.");
    }
    mAddToBackStack = true;
    mName = paramString;
    return this;
  }
  
  public FragmentTransaction attach(Fragment paramFragment)
  {
    Op localOp = new Op();
    cmd = 7;
    fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  void bumpBackStackNesting(int paramInt)
  {
    if (!mAddToBackStack) {}
    for (;;)
    {
      return;
      if (FragmentManagerImpl.DEBUG) {
        Log.v("FragmentManager", "Bump nesting in " + this + " by " + paramInt);
      }
      for (Op localOp = mHead; localOp != null; localOp = next)
      {
        if (fragment != null)
        {
          Fragment localFragment2 = fragment;
          mBackStackNesting = (paramInt + mBackStackNesting);
          if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "Bump nesting of " + fragment + " to " + fragment.mBackStackNesting);
          }
        }
        if (removed != null) {
          for (int i = -1 + removed.size(); i >= 0; i--)
          {
            Fragment localFragment1 = (Fragment)removed.get(i);
            mBackStackNesting = (paramInt + mBackStackNesting);
            if (FragmentManagerImpl.DEBUG) {
              Log.v("FragmentManager", "Bump nesting of " + localFragment1 + " to " + mBackStackNesting);
            }
          }
        }
      }
    }
  }
  
  public void calculateBackFragments(SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    if (!mManager.mContainer.onHasView()) {}
    Op localOp;
    do
    {
      return;
      localOp = mTail;
    } while (localOp == null);
    switch (cmd)
    {
    }
    for (;;)
    {
      localOp = prev;
      break;
      setFirstOut(paramSparseArray1, paramSparseArray2, fragment);
      continue;
      if (removed != null) {
        for (int i = -1 + removed.size(); i >= 0; i--) {
          setLastIn(paramSparseArray1, paramSparseArray2, (Fragment)removed.get(i));
        }
      }
      setFirstOut(paramSparseArray1, paramSparseArray2, fragment);
      continue;
      setLastIn(paramSparseArray1, paramSparseArray2, fragment);
      continue;
      setLastIn(paramSparseArray1, paramSparseArray2, fragment);
      continue;
      setFirstOut(paramSparseArray1, paramSparseArray2, fragment);
      continue;
      setLastIn(paramSparseArray1, paramSparseArray2, fragment);
      continue;
      setFirstOut(paramSparseArray1, paramSparseArray2, fragment);
    }
  }
  
  void callSharedElementEnd(TransitionState paramTransitionState, Fragment paramFragment1, Fragment paramFragment2, boolean paramBoolean, ArrayMap<String, View> paramArrayMap)
  {
    if (paramBoolean) {}
    for (SharedElementCallback localSharedElementCallback = mEnterTransitionCallback;; localSharedElementCallback = mEnterTransitionCallback)
    {
      if (localSharedElementCallback != null) {
        localSharedElementCallback.onSharedElementEnd(new ArrayList(paramArrayMap.keySet()), new ArrayList(paramArrayMap.values()), null);
      }
      return;
    }
  }
  
  public int commit()
  {
    return commitInternal(false);
  }
  
  public int commitAllowingStateLoss()
  {
    return commitInternal(true);
  }
  
  int commitInternal(boolean paramBoolean)
  {
    if (mCommitted) {
      throw new IllegalStateException("commit already called");
    }
    if (FragmentManagerImpl.DEBUG)
    {
      Log.v("FragmentManager", "Commit: " + this);
      dump("  ", null, new PrintWriter(new LogWriter("FragmentManager")), null);
    }
    mCommitted = true;
    if (mAddToBackStack) {}
    for (mIndex = mManager.allocBackStackIndex(this);; mIndex = -1)
    {
      mManager.enqueueAction(this, paramBoolean);
      return mIndex;
    }
  }
  
  public void commitNow()
  {
    disallowAddToBackStack();
    mManager.execSingleAction(this, false);
  }
  
  public void commitNowAllowingStateLoss()
  {
    disallowAddToBackStack();
    mManager.execSingleAction(this, true);
  }
  
  public FragmentTransaction detach(Fragment paramFragment)
  {
    Op localOp = new Op();
    cmd = 6;
    fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  public FragmentTransaction disallowAddToBackStack()
  {
    if (mAddToBackStack) {
      throw new IllegalStateException("This transaction is already being added to the back stack");
    }
    mAllowAddToBackStack = false;
    return this;
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    dump(paramString, paramPrintWriter, true);
  }
  
  public void dump(String paramString, PrintWriter paramPrintWriter, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mName=");
      paramPrintWriter.print(mName);
      paramPrintWriter.print(" mIndex=");
      paramPrintWriter.print(mIndex);
      paramPrintWriter.print(" mCommitted=");
      paramPrintWriter.println(mCommitted);
      if (mTransition != 0)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mTransition=#");
        paramPrintWriter.print(Integer.toHexString(mTransition));
        paramPrintWriter.print(" mTransitionStyle=#");
        paramPrintWriter.println(Integer.toHexString(mTransitionStyle));
      }
      if ((mEnterAnim != 0) || (mExitAnim != 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mEnterAnim=#");
        paramPrintWriter.print(Integer.toHexString(mEnterAnim));
        paramPrintWriter.print(" mExitAnim=#");
        paramPrintWriter.println(Integer.toHexString(mExitAnim));
      }
      if ((mPopEnterAnim != 0) || (mPopExitAnim != 0))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mPopEnterAnim=#");
        paramPrintWriter.print(Integer.toHexString(mPopEnterAnim));
        paramPrintWriter.print(" mPopExitAnim=#");
        paramPrintWriter.println(Integer.toHexString(mPopExitAnim));
      }
      if ((mBreadCrumbTitleRes != 0) || (mBreadCrumbTitleText != null))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mBreadCrumbTitleRes=#");
        paramPrintWriter.print(Integer.toHexString(mBreadCrumbTitleRes));
        paramPrintWriter.print(" mBreadCrumbTitleText=");
        paramPrintWriter.println(mBreadCrumbTitleText);
      }
      if ((mBreadCrumbShortTitleRes != 0) || (mBreadCrumbShortTitleText != null))
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mBreadCrumbShortTitleRes=#");
        paramPrintWriter.print(Integer.toHexString(mBreadCrumbShortTitleRes));
        paramPrintWriter.print(" mBreadCrumbShortTitleText=");
        paramPrintWriter.println(mBreadCrumbShortTitleText);
      }
    }
    if (mHead != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("Operations:");
      String str1 = paramString + "    ";
      Op localOp = mHead;
      for (int i = 0; localOp != null; i++)
      {
        String str2;
        int j;
        switch (cmd)
        {
        default: 
          str2 = "cmd=" + cmd;
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  Op #");
          paramPrintWriter.print(i);
          paramPrintWriter.print(": ");
          paramPrintWriter.print(str2);
          paramPrintWriter.print(" ");
          paramPrintWriter.println(fragment);
          if (paramBoolean)
          {
            if ((enterAnim != 0) || (exitAnim != 0))
            {
              paramPrintWriter.print(paramString);
              paramPrintWriter.print("enterAnim=#");
              paramPrintWriter.print(Integer.toHexString(enterAnim));
              paramPrintWriter.print(" exitAnim=#");
              paramPrintWriter.println(Integer.toHexString(exitAnim));
            }
            if ((popEnterAnim != 0) || (popExitAnim != 0))
            {
              paramPrintWriter.print(paramString);
              paramPrintWriter.print("popEnterAnim=#");
              paramPrintWriter.print(Integer.toHexString(popEnterAnim));
              paramPrintWriter.print(" popExitAnim=#");
              paramPrintWriter.println(Integer.toHexString(popExitAnim));
            }
          }
          if ((removed == null) || (removed.size() <= 0)) {
            break label804;
          }
          j = 0;
          label641:
          if (j >= removed.size()) {
            break label804;
          }
          paramPrintWriter.print(str1);
          if (removed.size() == 1) {
            paramPrintWriter.print("Removed: ");
          }
          break;
        }
        for (;;)
        {
          paramPrintWriter.println(removed.get(j));
          j++;
          break label641;
          str2 = "NULL";
          break;
          str2 = "ADD";
          break;
          str2 = "REPLACE";
          break;
          str2 = "REMOVE";
          break;
          str2 = "HIDE";
          break;
          str2 = "SHOW";
          break;
          str2 = "DETACH";
          break;
          str2 = "ATTACH";
          break;
          if (j == 0) {
            paramPrintWriter.println("Removed:");
          }
          paramPrintWriter.print(str1);
          paramPrintWriter.print("  #");
          paramPrintWriter.print(j);
          paramPrintWriter.print(": ");
        }
        label804:
        localOp = next;
      }
    }
  }
  
  void excludeHiddenFragments(TransitionState paramTransitionState, int paramInt, Object paramObject)
  {
    if (mManager.mAdded != null)
    {
      int i = 0;
      if (i < mManager.mAdded.size())
      {
        Fragment localFragment = (Fragment)mManager.mAdded.get(i);
        if ((mView != null) && (mContainer != null) && (mContainerId == paramInt))
        {
          if (!mHidden) {
            break label122;
          }
          if (!hiddenFragmentViews.contains(mView))
          {
            FragmentTransitionCompat21.excludeTarget(paramObject, mView, true);
            hiddenFragmentViews.add(mView);
          }
        }
        for (;;)
        {
          i++;
          break;
          label122:
          FragmentTransitionCompat21.excludeTarget(paramObject, mView, false);
          hiddenFragmentViews.remove(mView);
        }
      }
    }
  }
  
  public CharSequence getBreadCrumbShortTitle()
  {
    if (mBreadCrumbShortTitleRes != 0) {
      return mManager.mHost.getContext().getText(mBreadCrumbShortTitleRes);
    }
    return mBreadCrumbShortTitleText;
  }
  
  public int getBreadCrumbShortTitleRes()
  {
    return mBreadCrumbShortTitleRes;
  }
  
  public CharSequence getBreadCrumbTitle()
  {
    if (mBreadCrumbTitleRes != 0) {
      return mManager.mHost.getContext().getText(mBreadCrumbTitleRes);
    }
    return mBreadCrumbTitleText;
  }
  
  public int getBreadCrumbTitleRes()
  {
    return mBreadCrumbTitleRes;
  }
  
  public int getId()
  {
    return mIndex;
  }
  
  public String getName()
  {
    return mName;
  }
  
  public int getTransition()
  {
    return mTransition;
  }
  
  public int getTransitionStyle()
  {
    return mTransitionStyle;
  }
  
  public FragmentTransaction hide(Fragment paramFragment)
  {
    Op localOp = new Op();
    cmd = 4;
    fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  public boolean isAddToBackStackAllowed()
  {
    return mAllowAddToBackStack;
  }
  
  public boolean isEmpty()
  {
    return mNumOp == 0;
  }
  
  ArrayMap<String, View> mapSharedElementsIn(TransitionState paramTransitionState, boolean paramBoolean, Fragment paramFragment)
  {
    ArrayMap localArrayMap = mapEnteringSharedElements(paramTransitionState, paramFragment, paramBoolean);
    if (paramBoolean)
    {
      if (mExitTransitionCallback != null) {
        mExitTransitionCallback.onMapSharedElements(mSharedElementTargetNames, localArrayMap);
      }
      setBackNameOverrides(paramTransitionState, localArrayMap, true);
      return localArrayMap;
    }
    if (mEnterTransitionCallback != null) {
      mEnterTransitionCallback.onMapSharedElements(mSharedElementTargetNames, localArrayMap);
    }
    setNameOverrides(paramTransitionState, localArrayMap, true);
    return localArrayMap;
  }
  
  public TransitionState popFromBackStack(boolean paramBoolean, TransitionState paramTransitionState, SparseArray<Fragment> paramSparseArray1, SparseArray<Fragment> paramSparseArray2)
  {
    if (FragmentManagerImpl.DEBUG)
    {
      Log.v("FragmentManager", "popFromBackStack: " + this);
      dump("  ", null, new PrintWriter(new LogWriter("FragmentManager")), null);
    }
    if ((SUPPORTS_TRANSITIONS) && (mManager.mCurState >= 1))
    {
      if (paramTransitionState != null) {
        break label224;
      }
      if ((paramSparseArray1.size() != 0) || (paramSparseArray2.size() != 0)) {
        paramTransitionState = beginTransition(paramSparseArray1, paramSparseArray2, true);
      }
    }
    label102:
    bumpBackStackNesting(-1);
    int i;
    label114:
    int j;
    label121:
    Op localOp;
    int k;
    if (paramTransitionState != null)
    {
      i = 0;
      if (paramTransitionState == null) {
        break label260;
      }
      j = 0;
      localOp = mTail;
      if (localOp == null) {
        break label585;
      }
      if (paramTransitionState == null) {
        break label269;
      }
      k = 0;
      label139:
      if (paramTransitionState == null) {
        break label279;
      }
    }
    label224:
    label260:
    label269:
    label279:
    for (int m = 0;; m = popExitAnim) {
      switch (cmd)
      {
      default: 
        throw new IllegalArgumentException("Unknown cmd: " + cmd);
        if (paramBoolean) {
          break label102;
        }
        ArrayList localArrayList1 = mSharedElementTargetNames;
        ArrayList localArrayList2 = mSharedElementSourceNames;
        setNameOverrides(paramTransitionState, localArrayList1, localArrayList2);
        break label102;
        i = mTransitionStyle;
        break label114;
        j = mTransition;
        break label121;
        k = popEnterAnim;
        break label139;
      }
    }
    Fragment localFragment8 = fragment;
    mNextAnim = m;
    mManager.removeFragment(localFragment8, FragmentManagerImpl.reverseTransit(j), i);
    for (;;)
    {
      localOp = prev;
      break;
      Fragment localFragment6 = fragment;
      if (localFragment6 != null)
      {
        mNextAnim = m;
        mManager.removeFragment(localFragment6, FragmentManagerImpl.reverseTransit(j), i);
      }
      if (removed != null)
      {
        for (int n = 0; n < removed.size(); n++)
        {
          Fragment localFragment7 = (Fragment)removed.get(n);
          mNextAnim = k;
          mManager.addFragment(localFragment7, false);
        }
        Fragment localFragment5 = fragment;
        mNextAnim = k;
        mManager.addFragment(localFragment5, false);
        continue;
        Fragment localFragment4 = fragment;
        mNextAnim = k;
        mManager.showFragment(localFragment4, FragmentManagerImpl.reverseTransit(j), i);
        continue;
        Fragment localFragment3 = fragment;
        mNextAnim = m;
        mManager.hideFragment(localFragment3, FragmentManagerImpl.reverseTransit(j), i);
        continue;
        Fragment localFragment2 = fragment;
        mNextAnim = k;
        mManager.attachFragment(localFragment2, FragmentManagerImpl.reverseTransit(j), i);
        continue;
        Fragment localFragment1 = fragment;
        mNextAnim = k;
        mManager.detachFragment(localFragment1, FragmentManagerImpl.reverseTransit(j), i);
      }
    }
    label585:
    if (paramBoolean)
    {
      mManager.moveToState(mManager.mCurState, FragmentManagerImpl.reverseTransit(j), i, true);
      paramTransitionState = null;
    }
    if (mIndex >= 0)
    {
      mManager.freeBackStackIndex(mIndex);
      mIndex = -1;
    }
    return paramTransitionState;
  }
  
  public FragmentTransaction remove(Fragment paramFragment)
  {
    Op localOp = new Op();
    cmd = 3;
    fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  public FragmentTransaction replace(int paramInt, Fragment paramFragment)
  {
    return replace(paramInt, paramFragment, null);
  }
  
  public FragmentTransaction replace(int paramInt, Fragment paramFragment, String paramString)
  {
    if (paramInt == 0) {
      throw new IllegalArgumentException("Must use non-zero containerViewId");
    }
    doAddOp(paramInt, paramFragment, paramString, 2);
    return this;
  }
  
  public void run()
  {
    if (FragmentManagerImpl.DEBUG) {
      Log.v("FragmentManager", "Run: " + this);
    }
    if ((mAddToBackStack) && (mIndex < 0)) {
      throw new IllegalStateException("addToBackStack() called after commit()");
    }
    bumpBackStackNesting(1);
    boolean bool = SUPPORTS_TRANSITIONS;
    TransitionState localTransitionState = null;
    if (bool)
    {
      int i2 = mManager.mCurState;
      localTransitionState = null;
      if (i2 >= 1)
      {
        SparseArray localSparseArray1 = new SparseArray();
        SparseArray localSparseArray2 = new SparseArray();
        calculateFragments(localSparseArray1, localSparseArray2);
        localTransitionState = beginTransition(localSparseArray1, localSparseArray2, false);
      }
    }
    int i;
    label131:
    int j;
    label138:
    Op localOp;
    int k;
    if (localTransitionState != null)
    {
      i = 0;
      if (localTransitionState == null) {
        break label252;
      }
      j = 0;
      localOp = mHead;
      if (localOp == null) {
        break label740;
      }
      if (localTransitionState == null) {
        break label261;
      }
      k = 0;
      label156:
      if (localTransitionState == null) {
        break label271;
      }
    }
    label252:
    label261:
    label271:
    for (int m = 0;; m = exitAnim) {
      switch (cmd)
      {
      default: 
        throw new IllegalArgumentException("Unknown cmd: " + cmd);
        i = mTransitionStyle;
        break label131;
        j = mTransition;
        break label138;
        k = enterAnim;
        break label156;
      }
    }
    Fragment localFragment8 = fragment;
    mNextAnim = k;
    mManager.addFragment(localFragment8, false);
    for (;;)
    {
      localOp = next;
      break;
      Fragment localFragment6 = fragment;
      int n = mContainerId;
      if (mManager.mAdded != null)
      {
        int i1 = -1 + mManager.mAdded.size();
        if (i1 >= 0)
        {
          Fragment localFragment7 = (Fragment)mManager.mAdded.get(i1);
          if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "OP_REPLACE: adding=" + localFragment6 + " old=" + localFragment7);
          }
          if (mContainerId == n)
          {
            if (localFragment7 != localFragment6) {
              break label451;
            }
            localFragment6 = null;
            fragment = null;
          }
          for (;;)
          {
            i1--;
            break;
            label451:
            if (removed == null) {
              removed = new ArrayList();
            }
            removed.add(localFragment7);
            mNextAnim = m;
            if (mAddToBackStack)
            {
              mBackStackNesting = (1 + mBackStackNesting);
              if (FragmentManagerImpl.DEBUG) {
                Log.v("FragmentManager", "Bump nesting of " + localFragment7 + " to " + mBackStackNesting);
              }
            }
            mManager.removeFragment(localFragment7, j, i);
          }
        }
      }
      if (localFragment6 != null)
      {
        mNextAnim = k;
        mManager.addFragment(localFragment6, false);
        continue;
        Fragment localFragment5 = fragment;
        mNextAnim = m;
        mManager.removeFragment(localFragment5, j, i);
        continue;
        Fragment localFragment4 = fragment;
        mNextAnim = m;
        mManager.hideFragment(localFragment4, j, i);
        continue;
        Fragment localFragment3 = fragment;
        mNextAnim = k;
        mManager.showFragment(localFragment3, j, i);
        continue;
        Fragment localFragment2 = fragment;
        mNextAnim = m;
        mManager.detachFragment(localFragment2, j, i);
        continue;
        Fragment localFragment1 = fragment;
        mNextAnim = k;
        mManager.attachFragment(localFragment1, j, i);
      }
    }
    label740:
    mManager.moveToState(mManager.mCurState, j, i, true);
    if (mAddToBackStack) {
      mManager.addBackStackState(this);
    }
  }
  
  public FragmentTransaction setBreadCrumbShortTitle(int paramInt)
  {
    mBreadCrumbShortTitleRes = paramInt;
    mBreadCrumbShortTitleText = null;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbShortTitle(CharSequence paramCharSequence)
  {
    mBreadCrumbShortTitleRes = 0;
    mBreadCrumbShortTitleText = paramCharSequence;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbTitle(int paramInt)
  {
    mBreadCrumbTitleRes = paramInt;
    mBreadCrumbTitleText = null;
    return this;
  }
  
  public FragmentTransaction setBreadCrumbTitle(CharSequence paramCharSequence)
  {
    mBreadCrumbTitleRes = 0;
    mBreadCrumbTitleText = paramCharSequence;
    return this;
  }
  
  public FragmentTransaction setCustomAnimations(int paramInt1, int paramInt2)
  {
    return setCustomAnimations(paramInt1, paramInt2, 0, 0);
  }
  
  public FragmentTransaction setCustomAnimations(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    mEnterAnim = paramInt1;
    mExitAnim = paramInt2;
    mPopEnterAnim = paramInt3;
    mPopExitAnim = paramInt4;
    return this;
  }
  
  void setEpicenterIn(ArrayMap<String, View> paramArrayMap, TransitionState paramTransitionState)
  {
    if ((mSharedElementTargetNames != null) && (!paramArrayMap.isEmpty()))
    {
      View localView = (View)paramArrayMap.get(mSharedElementTargetNames.get(0));
      if (localView != null) {
        enteringEpicenterView.epicenter = localView;
      }
    }
  }
  
  public FragmentTransaction setTransition(int paramInt)
  {
    mTransition = paramInt;
    return this;
  }
  
  public FragmentTransaction setTransitionStyle(int paramInt)
  {
    mTransitionStyle = paramInt;
    return this;
  }
  
  public FragmentTransaction show(Fragment paramFragment)
  {
    Op localOp = new Op();
    cmd = 5;
    fragment = paramFragment;
    addOp(localOp);
    return this;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("BackStackEntry{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    if (mIndex >= 0)
    {
      localStringBuilder.append(" #");
      localStringBuilder.append(mIndex);
    }
    if (mName != null)
    {
      localStringBuilder.append(" ");
      localStringBuilder.append(mName);
    }
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  static final class Op
  {
    int cmd;
    int enterAnim;
    int exitAnim;
    Fragment fragment;
    Op next;
    int popEnterAnim;
    int popExitAnim;
    Op prev;
    ArrayList<Fragment> removed;
    
    Op() {}
  }
  
  public class TransitionState
  {
    public FragmentTransitionCompat21.EpicenterView enteringEpicenterView = new FragmentTransitionCompat21.EpicenterView();
    public ArrayList<View> hiddenFragmentViews = new ArrayList();
    public ArrayMap<String, String> nameOverrides = new ArrayMap();
    public View nonExistentView;
    
    public TransitionState() {}
  }
}
