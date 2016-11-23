package android.support.v4.app;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class FragmentStatePagerAdapter
  extends PagerAdapter
{
  private static final boolean DEBUG = false;
  private static final String TAG = "FragmentStatePagerAdapter";
  private FragmentTransaction mCurTransaction = null;
  private Fragment mCurrentPrimaryItem = null;
  private final FragmentManager mFragmentManager;
  private ArrayList<Fragment> mFragments = new ArrayList();
  private ArrayList<Fragment.SavedState> mSavedState = new ArrayList();
  
  public FragmentStatePagerAdapter(FragmentManager paramFragmentManager)
  {
    mFragmentManager = paramFragmentManager;
  }
  
  public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
  {
    Fragment localFragment = (Fragment)paramObject;
    if (mCurTransaction == null) {
      mCurTransaction = mFragmentManager.beginTransaction();
    }
    while (mSavedState.size() <= paramInt) {
      mSavedState.add(null);
    }
    ArrayList localArrayList = mSavedState;
    if (localFragment.isAdded()) {}
    for (Fragment.SavedState localSavedState = mFragmentManager.saveFragmentInstanceState(localFragment);; localSavedState = null)
    {
      localArrayList.set(paramInt, localSavedState);
      mFragments.set(paramInt, null);
      mCurTransaction.remove(localFragment);
      return;
    }
  }
  
  public void finishUpdate(ViewGroup paramViewGroup)
  {
    if (mCurTransaction != null)
    {
      mCurTransaction.commitNowAllowingStateLoss();
      mCurTransaction = null;
    }
  }
  
  public abstract Fragment getItem(int paramInt);
  
  public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
  {
    if (mFragments.size() > paramInt)
    {
      Fragment localFragment2 = (Fragment)mFragments.get(paramInt);
      if (localFragment2 != null) {
        return localFragment2;
      }
    }
    if (mCurTransaction == null) {
      mCurTransaction = mFragmentManager.beginTransaction();
    }
    Fragment localFragment1 = getItem(paramInt);
    if (mSavedState.size() > paramInt)
    {
      Fragment.SavedState localSavedState = (Fragment.SavedState)mSavedState.get(paramInt);
      if (localSavedState != null) {
        localFragment1.setInitialSavedState(localSavedState);
      }
    }
    while (mFragments.size() <= paramInt) {
      mFragments.add(null);
    }
    localFragment1.setMenuVisibility(false);
    localFragment1.setUserVisibleHint(false);
    mFragments.set(paramInt, localFragment1);
    mCurTransaction.add(paramViewGroup.getId(), localFragment1);
    return localFragment1;
  }
  
  public boolean isViewFromObject(View paramView, Object paramObject)
  {
    return ((Fragment)paramObject).getView() == paramView;
  }
  
  public void restoreState(Parcelable paramParcelable, ClassLoader paramClassLoader)
  {
    if (paramParcelable != null)
    {
      Bundle localBundle = (Bundle)paramParcelable;
      localBundle.setClassLoader(paramClassLoader);
      Parcelable[] arrayOfParcelable = localBundle.getParcelableArray("states");
      mSavedState.clear();
      mFragments.clear();
      if (arrayOfParcelable != null) {
        for (int j = 0; j < arrayOfParcelable.length; j++) {
          mSavedState.add((Fragment.SavedState)arrayOfParcelable[j]);
        }
      }
      Iterator localIterator = localBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (str.startsWith("f"))
        {
          int i = Integer.parseInt(str.substring(1));
          Fragment localFragment = mFragmentManager.getFragment(localBundle, str);
          if (localFragment != null)
          {
            while (mFragments.size() <= i) {
              mFragments.add(null);
            }
            localFragment.setMenuVisibility(false);
            mFragments.set(i, localFragment);
          }
          else
          {
            Log.w("FragmentStatePagerAdapter", "Bad fragment at key " + str);
          }
        }
      }
    }
  }
  
  public Parcelable saveState()
  {
    int i = mSavedState.size();
    Bundle localBundle = null;
    if (i > 0)
    {
      localBundle = new Bundle();
      Fragment.SavedState[] arrayOfSavedState = new Fragment.SavedState[mSavedState.size()];
      mSavedState.toArray(arrayOfSavedState);
      localBundle.putParcelableArray("states", arrayOfSavedState);
    }
    for (int j = 0; j < mFragments.size(); j++)
    {
      Fragment localFragment = (Fragment)mFragments.get(j);
      if ((localFragment != null) && (localFragment.isAdded()))
      {
        if (localBundle == null) {
          localBundle = new Bundle();
        }
        String str = "f" + j;
        mFragmentManager.putFragment(localBundle, str, localFragment);
      }
    }
    return localBundle;
  }
  
  public void setPrimaryItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
  {
    Fragment localFragment = (Fragment)paramObject;
    if (localFragment != mCurrentPrimaryItem)
    {
      if (mCurrentPrimaryItem != null)
      {
        mCurrentPrimaryItem.setMenuVisibility(false);
        mCurrentPrimaryItem.setUserVisibleHint(false);
      }
      if (localFragment != null)
      {
        localFragment.setMenuVisibility(true);
        localFragment.setUserVisibleHint(true);
      }
      mCurrentPrimaryItem = localFragment;
    }
  }
  
  public void startUpdate(ViewGroup paramViewGroup)
  {
    if (paramViewGroup.getId() == -1) {
      throw new IllegalStateException("ViewPager with adapter " + this + " requires a view id");
    }
  }
}
