package android.support.v4.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

final class FragmentState
  implements Parcelable
{
  public static final Parcelable.Creator<FragmentState> CREATOR = new Parcelable.Creator()
  {
    public FragmentState createFromParcel(Parcel paramAnonymousParcel)
    {
      return new FragmentState(paramAnonymousParcel);
    }
    
    public FragmentState[] newArray(int paramAnonymousInt)
    {
      return new FragmentState[paramAnonymousInt];
    }
  };
  final Bundle mArguments;
  final String mClassName;
  final int mContainerId;
  final boolean mDetached;
  final int mFragmentId;
  final boolean mFromLayout;
  final boolean mHidden;
  final int mIndex;
  Fragment mInstance;
  final boolean mRetainInstance;
  Bundle mSavedFragmentState;
  final String mTag;
  
  public FragmentState(Parcel paramParcel)
  {
    mClassName = paramParcel.readString();
    mIndex = paramParcel.readInt();
    boolean bool2;
    boolean bool3;
    label70:
    boolean bool4;
    if (paramParcel.readInt() != 0)
    {
      bool2 = bool1;
      mFromLayout = bool2;
      mFragmentId = paramParcel.readInt();
      mContainerId = paramParcel.readInt();
      mTag = paramParcel.readString();
      if (paramParcel.readInt() == 0) {
        break label126;
      }
      bool3 = bool1;
      mRetainInstance = bool3;
      if (paramParcel.readInt() == 0) {
        break label132;
      }
      bool4 = bool1;
      label86:
      mDetached = bool4;
      mArguments = paramParcel.readBundle();
      if (paramParcel.readInt() == 0) {
        break label138;
      }
    }
    for (;;)
    {
      mHidden = bool1;
      mSavedFragmentState = paramParcel.readBundle();
      return;
      bool2 = false;
      break;
      label126:
      bool3 = false;
      break label70;
      label132:
      bool4 = false;
      break label86;
      label138:
      bool1 = false;
    }
  }
  
  public FragmentState(Fragment paramFragment)
  {
    mClassName = paramFragment.getClass().getName();
    mIndex = mIndex;
    mFromLayout = mFromLayout;
    mFragmentId = mFragmentId;
    mContainerId = mContainerId;
    mTag = mTag;
    mRetainInstance = mRetainInstance;
    mDetached = mDetached;
    mArguments = mArguments;
    mHidden = mHidden;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public Fragment instantiate(FragmentHostCallback paramFragmentHostCallback, Fragment paramFragment, FragmentManagerNonConfig paramFragmentManagerNonConfig)
  {
    if (mInstance == null)
    {
      Context localContext = paramFragmentHostCallback.getContext();
      if (mArguments != null) {
        mArguments.setClassLoader(localContext.getClassLoader());
      }
      mInstance = Fragment.instantiate(localContext, mClassName, mArguments);
      if (mSavedFragmentState != null)
      {
        mSavedFragmentState.setClassLoader(localContext.getClassLoader());
        mInstance.mSavedFragmentState = mSavedFragmentState;
      }
      mInstance.setIndex(mIndex, paramFragment);
      mInstance.mFromLayout = mFromLayout;
      mInstance.mRestored = true;
      mInstance.mFragmentId = mFragmentId;
      mInstance.mContainerId = mContainerId;
      mInstance.mTag = mTag;
      mInstance.mRetainInstance = mRetainInstance;
      mInstance.mDetached = mDetached;
      mInstance.mHidden = mHidden;
      mInstance.mFragmentManager = mFragmentManager;
      if (FragmentManagerImpl.DEBUG) {
        Log.v("FragmentManager", "Instantiated fragment " + mInstance);
      }
    }
    mInstance.mChildNonConfig = paramFragmentManagerNonConfig;
    return mInstance;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeString(mClassName);
    paramParcel.writeInt(mIndex);
    int j;
    int k;
    label68:
    int m;
    if (mFromLayout)
    {
      j = i;
      paramParcel.writeInt(j);
      paramParcel.writeInt(mFragmentId);
      paramParcel.writeInt(mContainerId);
      paramParcel.writeString(mTag);
      if (!mRetainInstance) {
        break label125;
      }
      k = i;
      paramParcel.writeInt(k);
      if (!mDetached) {
        break label131;
      }
      m = i;
      label84:
      paramParcel.writeInt(m);
      paramParcel.writeBundle(mArguments);
      if (!mHidden) {
        break label137;
      }
    }
    for (;;)
    {
      paramParcel.writeInt(i);
      paramParcel.writeBundle(mSavedFragmentState);
      return;
      j = 0;
      break;
      label125:
      k = 0;
      break label68;
      label131:
      m = 0;
      break label84;
      label137:
      i = 0;
    }
  }
}
