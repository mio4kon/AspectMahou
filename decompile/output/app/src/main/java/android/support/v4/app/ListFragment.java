package android.support.v4.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ListFragment
  extends Fragment
{
  static final int INTERNAL_EMPTY_ID = 16711681;
  static final int INTERNAL_LIST_CONTAINER_ID = 16711683;
  static final int INTERNAL_PROGRESS_CONTAINER_ID = 16711682;
  ListAdapter mAdapter;
  CharSequence mEmptyText;
  View mEmptyView;
  private final Handler mHandler = new Handler();
  ListView mList;
  View mListContainer;
  boolean mListShown;
  private final AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener()
  {
    public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      onListItemClick((ListView)paramAnonymousAdapterView, paramAnonymousView, paramAnonymousInt, paramAnonymousLong);
    }
  };
  View mProgressContainer;
  private final Runnable mRequestFocus = new Runnable()
  {
    public void run()
    {
      mList.focusableViewAvailable(mList);
    }
  };
  TextView mStandardEmptyView;
  
  public ListFragment() {}
  
  private void ensureList()
  {
    if (mList != null) {
      return;
    }
    View localView1 = getView();
    if (localView1 == null) {
      throw new IllegalStateException("Content view not yet created");
    }
    if ((localView1 instanceof ListView))
    {
      mList = ((ListView)localView1);
      mListShown = true;
      mList.setOnItemClickListener(mOnClickListener);
      if (mAdapter == null) {
        break label256;
      }
      ListAdapter localListAdapter = mAdapter;
      mAdapter = null;
      setListAdapter(localListAdapter);
    }
    for (;;)
    {
      mHandler.post(mRequestFocus);
      return;
      mStandardEmptyView = ((TextView)localView1.findViewById(16711681));
      if (mStandardEmptyView == null) {
        mEmptyView = localView1.findViewById(16908292);
      }
      View localView2;
      for (;;)
      {
        mProgressContainer = localView1.findViewById(16711682);
        mListContainer = localView1.findViewById(16711683);
        localView2 = localView1.findViewById(16908298);
        if ((localView2 instanceof ListView)) {
          break label195;
        }
        if (localView2 != null) {
          break;
        }
        throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
        mStandardEmptyView.setVisibility(8);
      }
      throw new RuntimeException("Content has view with id attribute 'android.R.id.list' that is not a ListView class");
      label195:
      mList = ((ListView)localView2);
      if (mEmptyView != null)
      {
        mList.setEmptyView(mEmptyView);
        break;
      }
      if (mEmptyText == null) {
        break;
      }
      mStandardEmptyView.setText(mEmptyText);
      mList.setEmptyView(mStandardEmptyView);
      break;
      label256:
      if (mProgressContainer != null) {
        setListShown(false, false);
      }
    }
  }
  
  private void setListShown(boolean paramBoolean1, boolean paramBoolean2)
  {
    ensureList();
    if (mProgressContainer == null) {
      throw new IllegalStateException("Can't be used with a custom content view");
    }
    if (mListShown == paramBoolean1) {
      return;
    }
    mListShown = paramBoolean1;
    if (paramBoolean1)
    {
      if (paramBoolean2)
      {
        mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), 17432577));
        mListContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), 17432576));
      }
      for (;;)
      {
        mProgressContainer.setVisibility(8);
        mListContainer.setVisibility(0);
        return;
        mProgressContainer.clearAnimation();
        mListContainer.clearAnimation();
      }
    }
    if (paramBoolean2)
    {
      mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), 17432576));
      mListContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), 17432577));
    }
    for (;;)
    {
      mProgressContainer.setVisibility(0);
      mListContainer.setVisibility(8);
      return;
      mProgressContainer.clearAnimation();
      mListContainer.clearAnimation();
    }
  }
  
  public ListAdapter getListAdapter()
  {
    return mAdapter;
  }
  
  public ListView getListView()
  {
    ensureList();
    return mList;
  }
  
  public long getSelectedItemId()
  {
    ensureList();
    return mList.getSelectedItemId();
  }
  
  public int getSelectedItemPosition()
  {
    ensureList();
    return mList.getSelectedItemPosition();
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    Context localContext = getContext();
    FrameLayout localFrameLayout1 = new FrameLayout(localContext);
    LinearLayout localLinearLayout = new LinearLayout(localContext);
    localLinearLayout.setId(16711682);
    localLinearLayout.setOrientation(1);
    localLinearLayout.setVisibility(8);
    localLinearLayout.setGravity(17);
    localLinearLayout.addView(new ProgressBar(localContext, null, 16842874), new FrameLayout.LayoutParams(-2, -2));
    localFrameLayout1.addView(localLinearLayout, new FrameLayout.LayoutParams(-1, -1));
    FrameLayout localFrameLayout2 = new FrameLayout(localContext);
    localFrameLayout2.setId(16711683);
    TextView localTextView = new TextView(localContext);
    localTextView.setId(16711681);
    localTextView.setGravity(17);
    localFrameLayout2.addView(localTextView, new FrameLayout.LayoutParams(-1, -1));
    ListView localListView = new ListView(localContext);
    localListView.setId(16908298);
    localListView.setDrawSelectorOnTop(false);
    localFrameLayout2.addView(localListView, new FrameLayout.LayoutParams(-1, -1));
    localFrameLayout1.addView(localFrameLayout2, new FrameLayout.LayoutParams(-1, -1));
    localFrameLayout1.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
    return localFrameLayout1;
  }
  
  public void onDestroyView()
  {
    mHandler.removeCallbacks(mRequestFocus);
    mList = null;
    mListShown = false;
    mListContainer = null;
    mProgressContainer = null;
    mEmptyView = null;
    mStandardEmptyView = null;
    super.onDestroyView();
  }
  
  public void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong) {}
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    ensureList();
  }
  
  public void setEmptyText(CharSequence paramCharSequence)
  {
    ensureList();
    if (mStandardEmptyView == null) {
      throw new IllegalStateException("Can't be used with a custom content view");
    }
    mStandardEmptyView.setText(paramCharSequence);
    if (mEmptyText == null) {
      mList.setEmptyView(mStandardEmptyView);
    }
    mEmptyText = paramCharSequence;
  }
  
  public void setListAdapter(ListAdapter paramListAdapter)
  {
    if (mAdapter != null) {}
    for (int i = 1;; i = 0)
    {
      mAdapter = paramListAdapter;
      if (mList != null)
      {
        mList.setAdapter(paramListAdapter);
        if ((!mListShown) && (i == 0))
        {
          IBinder localIBinder = getView().getWindowToken();
          boolean bool = false;
          if (localIBinder != null) {
            bool = true;
          }
          setListShown(true, bool);
        }
      }
      return;
    }
  }
  
  public void setListShown(boolean paramBoolean)
  {
    setListShown(paramBoolean, true);
  }
  
  public void setListShownNoAnimation(boolean paramBoolean)
  {
    setListShown(paramBoolean, false);
  }
  
  public void setSelection(int paramInt)
  {
    ensureList();
    mList.setSelection(paramInt);
  }
}
