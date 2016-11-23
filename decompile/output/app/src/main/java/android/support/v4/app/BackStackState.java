package android.support.v4.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;

final class BackStackState
  implements Parcelable
{
  public static final Parcelable.Creator<BackStackState> CREATOR = new Parcelable.Creator()
  {
    public BackStackState createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BackStackState(paramAnonymousParcel);
    }
    
    public BackStackState[] newArray(int paramAnonymousInt)
    {
      return new BackStackState[paramAnonymousInt];
    }
  };
  final int mBreadCrumbShortTitleRes;
  final CharSequence mBreadCrumbShortTitleText;
  final int mBreadCrumbTitleRes;
  final CharSequence mBreadCrumbTitleText;
  final int mIndex;
  final String mName;
  final int[] mOps;
  final ArrayList<String> mSharedElementSourceNames;
  final ArrayList<String> mSharedElementTargetNames;
  final int mTransition;
  final int mTransitionStyle;
  
  public BackStackState(Parcel paramParcel)
  {
    mOps = paramParcel.createIntArray();
    mTransition = paramParcel.readInt();
    mTransitionStyle = paramParcel.readInt();
    mName = paramParcel.readString();
    mIndex = paramParcel.readInt();
    mBreadCrumbTitleRes = paramParcel.readInt();
    mBreadCrumbTitleText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    mBreadCrumbShortTitleRes = paramParcel.readInt();
    mBreadCrumbShortTitleText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    mSharedElementSourceNames = paramParcel.createStringArrayList();
    mSharedElementTargetNames = paramParcel.createStringArrayList();
  }
  
  public BackStackState(BackStackRecord paramBackStackRecord)
  {
    int i = 0;
    for (BackStackRecord.Op localOp1 = mHead; localOp1 != null; localOp1 = next) {
      if (removed != null) {
        i += removed.size();
      }
    }
    mOps = new int[i + 7 * mNumOp];
    if (!mAddToBackStack) {
      throw new IllegalStateException("Not on back stack");
    }
    BackStackRecord.Op localOp2 = mHead;
    int j = 0;
    if (localOp2 != null)
    {
      int[] arrayOfInt1 = mOps;
      int k = j + 1;
      arrayOfInt1[j] = cmd;
      int[] arrayOfInt2 = mOps;
      int m = k + 1;
      if (fragment != null) {}
      int i4;
      int i9;
      for (int n = fragment.mIndex;; n = -1)
      {
        arrayOfInt2[k] = n;
        int[] arrayOfInt3 = mOps;
        int i1 = m + 1;
        arrayOfInt3[m] = enterAnim;
        int[] arrayOfInt4 = mOps;
        int i2 = i1 + 1;
        arrayOfInt4[i1] = exitAnim;
        int[] arrayOfInt5 = mOps;
        int i3 = i2 + 1;
        arrayOfInt5[i2] = popEnterAnim;
        int[] arrayOfInt6 = mOps;
        i4 = i3 + 1;
        arrayOfInt6[i3] = popExitAnim;
        if (removed == null) {
          break label351;
        }
        int i6 = removed.size();
        int[] arrayOfInt8 = mOps;
        int i7 = i4 + 1;
        arrayOfInt8[i4] = i6;
        int i8 = 0;
        int i10;
        for (i9 = i7; i8 < i6; i9 = i10)
        {
          int[] arrayOfInt9 = mOps;
          i10 = i9 + 1;
          arrayOfInt9[i9] = removed.get(i8)).mIndex;
          i8++;
        }
      }
      int i5 = i9;
      for (;;)
      {
        localOp2 = next;
        j = i5;
        break;
        label351:
        int[] arrayOfInt7 = mOps;
        i5 = i4 + 1;
        arrayOfInt7[i4] = 0;
      }
    }
    mTransition = mTransition;
    mTransitionStyle = mTransitionStyle;
    mName = mName;
    mIndex = mIndex;
    mBreadCrumbTitleRes = mBreadCrumbTitleRes;
    mBreadCrumbTitleText = mBreadCrumbTitleText;
    mBreadCrumbShortTitleRes = mBreadCrumbShortTitleRes;
    mBreadCrumbShortTitleText = mBreadCrumbShortTitleText;
    mSharedElementSourceNames = mSharedElementSourceNames;
    mSharedElementTargetNames = mSharedElementTargetNames;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public BackStackRecord instantiate(FragmentManagerImpl paramFragmentManagerImpl)
  {
    BackStackRecord localBackStackRecord = new BackStackRecord(paramFragmentManagerImpl);
    int i = 0;
    for (int j = 0; i < mOps.length; j++)
    {
      BackStackRecord.Op localOp = new BackStackRecord.Op();
      int[] arrayOfInt1 = mOps;
      int k = i + 1;
      cmd = arrayOfInt1[i];
      if (FragmentManagerImpl.DEBUG) {
        Log.v("FragmentManager", "Instantiate " + localBackStackRecord + " op #" + j + " base fragment #" + mOps[k]);
      }
      int[] arrayOfInt2 = mOps;
      int m = k + 1;
      int n = arrayOfInt2[k];
      if (n >= 0) {}
      int i5;
      for (fragment = ((Fragment)mActive.get(n));; fragment = null)
      {
        int[] arrayOfInt3 = mOps;
        int i1 = m + 1;
        enterAnim = arrayOfInt3[m];
        int[] arrayOfInt4 = mOps;
        int i2 = i1 + 1;
        exitAnim = arrayOfInt4[i1];
        int[] arrayOfInt5 = mOps;
        int i3 = i2 + 1;
        popEnterAnim = arrayOfInt5[i2];
        int[] arrayOfInt6 = mOps;
        int i4 = i3 + 1;
        popExitAnim = arrayOfInt6[i3];
        int[] arrayOfInt7 = mOps;
        i5 = i4 + 1;
        int i6 = arrayOfInt7[i4];
        if (i6 <= 0) {
          break;
        }
        removed = new ArrayList(i6);
        int i7 = 0;
        while (i7 < i6)
        {
          if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "Instantiate " + localBackStackRecord + " set remove fragment #" + mOps[i5]);
          }
          ArrayList localArrayList = mActive;
          int[] arrayOfInt8 = mOps;
          int i8 = i5 + 1;
          Fragment localFragment = (Fragment)localArrayList.get(arrayOfInt8[i5]);
          removed.add(localFragment);
          i7++;
          i5 = i8;
        }
      }
      i = i5;
      mEnterAnim = enterAnim;
      mExitAnim = exitAnim;
      mPopEnterAnim = popEnterAnim;
      mPopExitAnim = popExitAnim;
      localBackStackRecord.addOp(localOp);
    }
    mTransition = mTransition;
    mTransitionStyle = mTransitionStyle;
    mName = mName;
    mIndex = mIndex;
    mAddToBackStack = true;
    mBreadCrumbTitleRes = mBreadCrumbTitleRes;
    mBreadCrumbTitleText = mBreadCrumbTitleText;
    mBreadCrumbShortTitleRes = mBreadCrumbShortTitleRes;
    mBreadCrumbShortTitleText = mBreadCrumbShortTitleText;
    mSharedElementSourceNames = mSharedElementSourceNames;
    mSharedElementTargetNames = mSharedElementTargetNames;
    localBackStackRecord.bumpBackStackNesting(1);
    return localBackStackRecord;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeIntArray(mOps);
    paramParcel.writeInt(mTransition);
    paramParcel.writeInt(mTransitionStyle);
    paramParcel.writeString(mName);
    paramParcel.writeInt(mIndex);
    paramParcel.writeInt(mBreadCrumbTitleRes);
    TextUtils.writeToParcel(mBreadCrumbTitleText, paramParcel, 0);
    paramParcel.writeInt(mBreadCrumbShortTitleRes);
    TextUtils.writeToParcel(mBreadCrumbShortTitleText, paramParcel, 0);
    paramParcel.writeStringList(mSharedElementSourceNames);
    paramParcel.writeStringList(mSharedElementTargetNames);
  }
}
