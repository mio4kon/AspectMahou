package android.support.v4.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.BaseSavedState;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import java.util.ArrayList;

public class FragmentTabHost
  extends TabHost
  implements TabHost.OnTabChangeListener
{
  private boolean mAttached;
  private int mContainerId;
  private Context mContext;
  private FragmentManager mFragmentManager;
  private TabInfo mLastTab;
  private TabHost.OnTabChangeListener mOnTabChangeListener;
  private FrameLayout mRealTabContent;
  private final ArrayList<TabInfo> mTabs = new ArrayList();
  
  public FragmentTabHost(Context paramContext)
  {
    super(paramContext, null);
    initFragmentTabHost(paramContext, null);
  }
  
  public FragmentTabHost(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initFragmentTabHost(paramContext, paramAttributeSet);
  }
  
  private FragmentTransaction doTabChanged(String paramString, FragmentTransaction paramFragmentTransaction)
  {
    Object localObject = null;
    for (int i = 0; i < mTabs.size(); i++)
    {
      TabInfo localTabInfo = (TabInfo)mTabs.get(i);
      if (tag.equals(paramString)) {
        localObject = localTabInfo;
      }
    }
    if (localObject == null) {
      throw new IllegalStateException("No tab known for tag " + paramString);
    }
    if (mLastTab != localObject)
    {
      if (paramFragmentTransaction == null) {
        paramFragmentTransaction = mFragmentManager.beginTransaction();
      }
      if ((mLastTab != null) && (mLastTab.fragment != null)) {
        paramFragmentTransaction.detach(mLastTab.fragment);
      }
      if (localObject != null)
      {
        if (fragment != null) {
          break label189;
        }
        fragment = Fragment.instantiate(mContext, clss.getName(), args);
        paramFragmentTransaction.add(mContainerId, fragment, tag);
      }
    }
    for (;;)
    {
      mLastTab = localObject;
      return paramFragmentTransaction;
      label189:
      paramFragmentTransaction.attach(fragment);
    }
  }
  
  private void ensureContent()
  {
    if (mRealTabContent == null)
    {
      mRealTabContent = ((FrameLayout)findViewById(mContainerId));
      if (mRealTabContent == null) {
        throw new IllegalStateException("No tab content FrameLayout found for id " + mContainerId);
      }
    }
  }
  
  private void ensureHierarchy(Context paramContext)
  {
    if (findViewById(16908307) == null)
    {
      LinearLayout localLinearLayout = new LinearLayout(paramContext);
      localLinearLayout.setOrientation(1);
      addView(localLinearLayout, new FrameLayout.LayoutParams(-1, -1));
      TabWidget localTabWidget = new TabWidget(paramContext);
      localTabWidget.setId(16908307);
      localTabWidget.setOrientation(0);
      localLinearLayout.addView(localTabWidget, new LinearLayout.LayoutParams(-1, -2, 0.0F));
      FrameLayout localFrameLayout1 = new FrameLayout(paramContext);
      localFrameLayout1.setId(16908305);
      localLinearLayout.addView(localFrameLayout1, new LinearLayout.LayoutParams(0, 0, 0.0F));
      FrameLayout localFrameLayout2 = new FrameLayout(paramContext);
      mRealTabContent = localFrameLayout2;
      mRealTabContent.setId(mContainerId);
      localLinearLayout.addView(localFrameLayout2, new LinearLayout.LayoutParams(-1, 0, 1.0F));
    }
  }
  
  private void initFragmentTabHost(Context paramContext, AttributeSet paramAttributeSet)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, new int[] { 16842995 }, 0, 0);
    mContainerId = localTypedArray.getResourceId(0, 0);
    localTypedArray.recycle();
    super.setOnTabChangedListener(this);
  }
  
  public void addTab(TabHost.TabSpec paramTabSpec, Class<?> paramClass, Bundle paramBundle)
  {
    paramTabSpec.setContent(new DummyTabFactory(mContext));
    String str = paramTabSpec.getTag();
    TabInfo localTabInfo = new TabInfo(str, paramClass, paramBundle);
    if (mAttached)
    {
      fragment = mFragmentManager.findFragmentByTag(str);
      if ((fragment != null) && (!fragment.isDetached()))
      {
        FragmentTransaction localFragmentTransaction = mFragmentManager.beginTransaction();
        localFragmentTransaction.detach(fragment);
        localFragmentTransaction.commit();
      }
    }
    mTabs.add(localTabInfo);
    addTab(paramTabSpec);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    String str = getCurrentTabTag();
    FragmentTransaction localFragmentTransaction1 = null;
    int i = 0;
    if (i < mTabs.size())
    {
      TabInfo localTabInfo = (TabInfo)mTabs.get(i);
      fragment = mFragmentManager.findFragmentByTag(tag);
      if ((fragment != null) && (!fragment.isDetached()))
      {
        if (!tag.equals(str)) {
          break label97;
        }
        mLastTab = localTabInfo;
      }
      for (;;)
      {
        i++;
        break;
        label97:
        if (localFragmentTransaction1 == null) {
          localFragmentTransaction1 = mFragmentManager.beginTransaction();
        }
        localFragmentTransaction1.detach(fragment);
      }
    }
    mAttached = true;
    FragmentTransaction localFragmentTransaction2 = doTabChanged(str, localFragmentTransaction1);
    if (localFragmentTransaction2 != null)
    {
      localFragmentTransaction2.commit();
      mFragmentManager.executePendingTransactions();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    mAttached = false;
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(localSavedState.getSuperState());
    setCurrentTabByTag(curTab);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    curTab = getCurrentTabTag();
    return localSavedState;
  }
  
  public void onTabChanged(String paramString)
  {
    if (mAttached)
    {
      FragmentTransaction localFragmentTransaction = doTabChanged(paramString, null);
      if (localFragmentTransaction != null) {
        localFragmentTransaction.commit();
      }
    }
    if (mOnTabChangeListener != null) {
      mOnTabChangeListener.onTabChanged(paramString);
    }
  }
  
  public void setOnTabChangedListener(TabHost.OnTabChangeListener paramOnTabChangeListener)
  {
    mOnTabChangeListener = paramOnTabChangeListener;
  }
  
  @Deprecated
  public void setup()
  {
    throw new IllegalStateException("Must call setup() that takes a Context and FragmentManager");
  }
  
  public void setup(Context paramContext, FragmentManager paramFragmentManager)
  {
    ensureHierarchy(paramContext);
    super.setup();
    mContext = paramContext;
    mFragmentManager = paramFragmentManager;
    ensureContent();
  }
  
  public void setup(Context paramContext, FragmentManager paramFragmentManager, int paramInt)
  {
    ensureHierarchy(paramContext);
    super.setup();
    mContext = paramContext;
    mFragmentManager = paramFragmentManager;
    mContainerId = paramInt;
    ensureContent();
    mRealTabContent.setId(paramInt);
    if (getId() == -1) {
      setId(16908306);
    }
  }
  
  static class DummyTabFactory
    implements TabHost.TabContentFactory
  {
    private final Context mContext;
    
    public DummyTabFactory(Context paramContext)
    {
      mContext = paramContext;
    }
    
    public View createTabContent(String paramString)
    {
      View localView = new View(mContext);
      localView.setMinimumWidth(0);
      localView.setMinimumHeight(0);
      return localView;
    }
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public FragmentTabHost.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new FragmentTabHost.SavedState(paramAnonymousParcel);
      }
      
      public FragmentTabHost.SavedState[] newArray(int paramAnonymousInt)
      {
        return new FragmentTabHost.SavedState[paramAnonymousInt];
      }
    };
    String curTab;
    
    SavedState(Parcel paramParcel)
    {
      super();
      curTab = paramParcel.readString();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      return "FragmentTabHost.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " curTab=" + curTab + "}";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeString(curTab);
    }
  }
  
  static final class TabInfo
  {
    final Bundle args;
    final Class<?> clss;
    Fragment fragment;
    final String tag;
    
    TabInfo(String paramString, Class<?> paramClass, Bundle paramBundle)
    {
      tag = paramString;
      clss = paramClass;
      args = paramBundle;
    }
  }
}
