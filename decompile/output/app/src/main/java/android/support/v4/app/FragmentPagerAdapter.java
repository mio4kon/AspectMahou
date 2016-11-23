package android.support.v4.app;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public abstract class FragmentPagerAdapter
  extends PagerAdapter
{
  private static final boolean DEBUG = false;
  private static final String TAG = "FragmentPagerAdapter";
  private FragmentTransaction mCurTransaction = null;
  private Fragment mCurrentPrimaryItem = null;
  private final FragmentManager mFragmentManager;
  
  public FragmentPagerAdapter(FragmentManager paramFragmentManager)
  {
    mFragmentManager = paramFragmentManager;
  }
  
  private static String makeFragmentName(int paramInt, long paramLong)
  {
    return "android:switcher:" + paramInt + ":" + paramLong;
  }
  
  public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
  {
    if (mCurTransaction == null) {
      mCurTransaction = mFragmentManager.beginTransaction();
    }
    mCurTransaction.detach((Fragment)paramObject);
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
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
  {
    if (mCurTransaction == null) {
      mCurTransaction = mFragmentManager.beginTransaction();
    }
    long l = getItemId(paramInt);
    String str = makeFragmentName(paramViewGroup.getId(), l);
    Fragment localFragment = mFragmentManager.findFragmentByTag(str);
    if (localFragment != null) {
      mCurTransaction.attach(localFragment);
    }
    for (;;)
    {
      if (localFragment != mCurrentPrimaryItem)
      {
        localFragment.setMenuVisibility(false);
        localFragment.setUserVisibleHint(false);
      }
      return localFragment;
      localFragment = getItem(paramInt);
      mCurTransaction.add(paramViewGroup.getId(), localFragment, makeFragmentName(paramViewGroup.getId(), l));
    }
  }
  
  public boolean isViewFromObject(View paramView, Object paramObject)
  {
    return ((Fragment)paramObject).getView() == paramView;
  }
  
  public void restoreState(Parcelable paramParcelable, ClassLoader paramClassLoader) {}
  
  public Parcelable saveState()
  {
    return null;
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
