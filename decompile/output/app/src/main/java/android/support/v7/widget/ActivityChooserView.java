package android.support.v7.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.dimen;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.layout;
import android.support.v7.appcompat.R.string;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.view.menu.ShowableListMenu;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

public class ActivityChooserView
  extends ViewGroup
  implements ActivityChooserModel.ActivityChooserModelClient
{
  private static final String LOG_TAG = "ActivityChooserView";
  private final LinearLayoutCompat mActivityChooserContent;
  private final Drawable mActivityChooserContentBackground;
  final ActivityChooserViewAdapter mAdapter;
  private final Callbacks mCallbacks;
  private int mDefaultActionButtonContentDescription;
  final FrameLayout mDefaultActivityButton;
  private final ImageView mDefaultActivityButtonImage;
  final FrameLayout mExpandActivityOverflowButton;
  private final ImageView mExpandActivityOverflowButtonImage;
  int mInitialActivityCount = 4;
  private boolean mIsAttachedToWindow;
  boolean mIsSelectingDefaultActivity;
  private final int mListPopupMaxWidth;
  private ListPopupWindow mListPopupWindow;
  final DataSetObserver mModelDataSetObserver = new DataSetObserver()
  {
    public void onChanged()
    {
      super.onChanged();
      mAdapter.notifyDataSetChanged();
    }
    
    public void onInvalidated()
    {
      super.onInvalidated();
      mAdapter.notifyDataSetInvalidated();
    }
  };
  PopupWindow.OnDismissListener mOnDismissListener;
  private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
  {
    public void onGlobalLayout()
    {
      if (isShowingPopup())
      {
        if (isShown()) {
          break label31;
        }
        getListPopupWindow().dismiss();
      }
      label31:
      do
      {
        return;
        getListPopupWindow().show();
      } while (mProvider == null);
      mProvider.subUiVisibilityChanged(true);
    }
  };
  ActionProvider mProvider;
  
  public ActivityChooserView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ActivityChooserView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ActivityChooserView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ActivityChooserView, paramInt, 0);
    mInitialActivityCount = localTypedArray.getInt(R.styleable.ActivityChooserView_initialActivityCount, 4);
    Drawable localDrawable = localTypedArray.getDrawable(R.styleable.ActivityChooserView_expandActivityOverflowButtonDrawable);
    localTypedArray.recycle();
    LayoutInflater.from(getContext()).inflate(R.layout.abc_activity_chooser_view, this, true);
    mCallbacks = new Callbacks();
    mActivityChooserContent = ((LinearLayoutCompat)findViewById(R.id.activity_chooser_view_content));
    mActivityChooserContentBackground = mActivityChooserContent.getBackground();
    mDefaultActivityButton = ((FrameLayout)findViewById(R.id.default_activity_button));
    mDefaultActivityButton.setOnClickListener(mCallbacks);
    mDefaultActivityButton.setOnLongClickListener(mCallbacks);
    mDefaultActivityButtonImage = ((ImageView)mDefaultActivityButton.findViewById(R.id.image));
    FrameLayout localFrameLayout = (FrameLayout)findViewById(R.id.expand_activities_button);
    localFrameLayout.setOnClickListener(mCallbacks);
    localFrameLayout.setOnTouchListener(new ForwardingListener(localFrameLayout)
    {
      public ShowableListMenu getPopup()
      {
        return getListPopupWindow();
      }
      
      protected boolean onForwardingStarted()
      {
        showPopup();
        return true;
      }
      
      protected boolean onForwardingStopped()
      {
        dismissPopup();
        return true;
      }
    });
    mExpandActivityOverflowButton = localFrameLayout;
    mExpandActivityOverflowButtonImage = ((ImageView)localFrameLayout.findViewById(R.id.image));
    mExpandActivityOverflowButtonImage.setImageDrawable(localDrawable);
    mAdapter = new ActivityChooserViewAdapter();
    mAdapter.registerDataSetObserver(new DataSetObserver()
    {
      public void onChanged()
      {
        super.onChanged();
        updateAppearance();
      }
    });
    Resources localResources = paramContext.getResources();
    mListPopupMaxWidth = Math.max(getDisplayMetricswidthPixels / 2, localResources.getDimensionPixelSize(R.dimen.abc_config_prefDialogWidth));
  }
  
  public boolean dismissPopup()
  {
    if (isShowingPopup())
    {
      getListPopupWindow().dismiss();
      ViewTreeObserver localViewTreeObserver = getViewTreeObserver();
      if (localViewTreeObserver.isAlive()) {
        localViewTreeObserver.removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
      }
    }
    return true;
  }
  
  public ActivityChooserModel getDataModel()
  {
    return mAdapter.getDataModel();
  }
  
  ListPopupWindow getListPopupWindow()
  {
    if (mListPopupWindow == null)
    {
      mListPopupWindow = new ListPopupWindow(getContext());
      mListPopupWindow.setAdapter(mAdapter);
      mListPopupWindow.setAnchorView(this);
      mListPopupWindow.setModal(true);
      mListPopupWindow.setOnItemClickListener(mCallbacks);
      mListPopupWindow.setOnDismissListener(mCallbacks);
    }
    return mListPopupWindow;
  }
  
  public boolean isShowingPopup()
  {
    return getListPopupWindow().isShowing();
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    ActivityChooserModel localActivityChooserModel = mAdapter.getDataModel();
    if (localActivityChooserModel != null) {
      localActivityChooserModel.registerObserver(mModelDataSetObserver);
    }
    mIsAttachedToWindow = true;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    ActivityChooserModel localActivityChooserModel = mAdapter.getDataModel();
    if (localActivityChooserModel != null) {
      localActivityChooserModel.unregisterObserver(mModelDataSetObserver);
    }
    ViewTreeObserver localViewTreeObserver = getViewTreeObserver();
    if (localViewTreeObserver.isAlive()) {
      localViewTreeObserver.removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
    }
    if (isShowingPopup()) {
      dismissPopup();
    }
    mIsAttachedToWindow = false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    mActivityChooserContent.layout(0, 0, paramInt3 - paramInt1, paramInt4 - paramInt2);
    if (!isShowingPopup()) {
      dismissPopup();
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    LinearLayoutCompat localLinearLayoutCompat = mActivityChooserContent;
    if (mDefaultActivityButton.getVisibility() != 0) {
      paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt2), 1073741824);
    }
    measureChild(localLinearLayoutCompat, paramInt1, paramInt2);
    setMeasuredDimension(localLinearLayoutCompat.getMeasuredWidth(), localLinearLayoutCompat.getMeasuredHeight());
  }
  
  public void setActivityChooserModel(ActivityChooserModel paramActivityChooserModel)
  {
    mAdapter.setDataModel(paramActivityChooserModel);
    if (isShowingPopup())
    {
      dismissPopup();
      showPopup();
    }
  }
  
  public void setDefaultActionButtonContentDescription(int paramInt)
  {
    mDefaultActionButtonContentDescription = paramInt;
  }
  
  public void setExpandActivityOverflowButtonContentDescription(int paramInt)
  {
    String str = getContext().getString(paramInt);
    mExpandActivityOverflowButtonImage.setContentDescription(str);
  }
  
  public void setExpandActivityOverflowButtonDrawable(Drawable paramDrawable)
  {
    mExpandActivityOverflowButtonImage.setImageDrawable(paramDrawable);
  }
  
  public void setInitialActivityCount(int paramInt)
  {
    mInitialActivityCount = paramInt;
  }
  
  public void setOnDismissListener(PopupWindow.OnDismissListener paramOnDismissListener)
  {
    mOnDismissListener = paramOnDismissListener;
  }
  
  public void setProvider(ActionProvider paramActionProvider)
  {
    mProvider = paramActionProvider;
  }
  
  public boolean showPopup()
  {
    if ((isShowingPopup()) || (!mIsAttachedToWindow)) {
      return false;
    }
    mIsSelectingDefaultActivity = false;
    showPopupUnchecked(mInitialActivityCount);
    return true;
  }
  
  void showPopupUnchecked(int paramInt)
  {
    if (mAdapter.getDataModel() == null) {
      throw new IllegalStateException("No data model. Did you call #setDataModel?");
    }
    getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    boolean bool;
    int j;
    label59:
    label92:
    ListPopupWindow localListPopupWindow;
    if (mDefaultActivityButton.getVisibility() == 0)
    {
      bool = true;
      int i = mAdapter.getActivityCount();
      if (!bool) {
        break label189;
      }
      j = 1;
      if ((paramInt == Integer.MAX_VALUE) || (i <= paramInt + j)) {
        break label195;
      }
      mAdapter.setShowFooterView(true);
      mAdapter.setMaxActivityCount(paramInt - 1);
      localListPopupWindow = getListPopupWindow();
      if (!localListPopupWindow.isShowing())
      {
        if ((!mIsSelectingDefaultActivity) && (bool)) {
          break label214;
        }
        mAdapter.setShowDefaultActivity(true, bool);
      }
    }
    for (;;)
    {
      localListPopupWindow.setContentWidth(Math.min(mAdapter.measureContentWidth(), mListPopupMaxWidth));
      localListPopupWindow.show();
      if (mProvider != null) {
        mProvider.subUiVisibilityChanged(true);
      }
      localListPopupWindow.getListView().setContentDescription(getContext().getString(R.string.abc_activitychooserview_choose_application));
      return;
      bool = false;
      break;
      label189:
      j = 0;
      break label59;
      label195:
      mAdapter.setShowFooterView(false);
      mAdapter.setMaxActivityCount(paramInt);
      break label92;
      label214:
      mAdapter.setShowDefaultActivity(false, false);
    }
  }
  
  void updateAppearance()
  {
    if (mAdapter.getCount() > 0)
    {
      mExpandActivityOverflowButton.setEnabled(true);
      int i = mAdapter.getActivityCount();
      int j = mAdapter.getHistorySize();
      if ((i != 1) && ((i <= 1) || (j <= 0))) {
        break label165;
      }
      mDefaultActivityButton.setVisibility(0);
      ResolveInfo localResolveInfo = mAdapter.getDefaultActivity();
      PackageManager localPackageManager = getContext().getPackageManager();
      mDefaultActivityButtonImage.setImageDrawable(localResolveInfo.loadIcon(localPackageManager));
      if (mDefaultActionButtonContentDescription != 0)
      {
        CharSequence localCharSequence = localResolveInfo.loadLabel(localPackageManager);
        String str = getContext().getString(mDefaultActionButtonContentDescription, new Object[] { localCharSequence });
        mDefaultActivityButton.setContentDescription(str);
      }
    }
    for (;;)
    {
      if (mDefaultActivityButton.getVisibility() != 0) {
        break label177;
      }
      mActivityChooserContent.setBackgroundDrawable(mActivityChooserContentBackground);
      return;
      mExpandActivityOverflowButton.setEnabled(false);
      break;
      label165:
      mDefaultActivityButton.setVisibility(8);
    }
    label177:
    mActivityChooserContent.setBackgroundDrawable(null);
  }
  
  private class ActivityChooserViewAdapter
    extends BaseAdapter
  {
    private static final int ITEM_VIEW_TYPE_ACTIVITY = 0;
    private static final int ITEM_VIEW_TYPE_COUNT = 3;
    private static final int ITEM_VIEW_TYPE_FOOTER = 1;
    public static final int MAX_ACTIVITY_COUNT_DEFAULT = 4;
    public static final int MAX_ACTIVITY_COUNT_UNLIMITED = Integer.MAX_VALUE;
    private ActivityChooserModel mDataModel;
    private boolean mHighlightDefaultActivity;
    private int mMaxActivityCount = 4;
    private boolean mShowDefaultActivity;
    private boolean mShowFooterView;
    
    ActivityChooserViewAdapter() {}
    
    public int getActivityCount()
    {
      return mDataModel.getActivityCount();
    }
    
    public int getCount()
    {
      int i = mDataModel.getActivityCount();
      if ((!mShowDefaultActivity) && (mDataModel.getDefaultActivity() != null)) {
        i--;
      }
      int j = Math.min(i, mMaxActivityCount);
      if (mShowFooterView) {
        j++;
      }
      return j;
    }
    
    public ActivityChooserModel getDataModel()
    {
      return mDataModel;
    }
    
    public ResolveInfo getDefaultActivity()
    {
      return mDataModel.getDefaultActivity();
    }
    
    public int getHistorySize()
    {
      return mDataModel.getHistorySize();
    }
    
    public Object getItem(int paramInt)
    {
      switch (getItemViewType(paramInt))
      {
      default: 
        throw new IllegalArgumentException();
      case 1: 
        return null;
      }
      if ((!mShowDefaultActivity) && (mDataModel.getDefaultActivity() != null)) {
        paramInt++;
      }
      return mDataModel.getActivity(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((mShowFooterView) && (paramInt == -1 + getCount())) {
        return 1;
      }
      return 0;
    }
    
    public boolean getShowDefaultActivity()
    {
      return mShowDefaultActivity;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      switch (getItemViewType(paramInt))
      {
      default: 
        throw new IllegalArgumentException();
      case 1: 
        if ((paramView == null) || (paramView.getId() != 1))
        {
          paramView = LayoutInflater.from(getContext()).inflate(R.layout.abc_activity_chooser_view_list_item, paramViewGroup, false);
          paramView.setId(1);
          ((TextView)paramView.findViewById(R.id.title)).setText(getContext().getString(R.string.abc_activity_chooser_view_see_all));
        }
        return paramView;
      }
      if ((paramView == null) || (paramView.getId() != R.id.list_item)) {
        paramView = LayoutInflater.from(getContext()).inflate(R.layout.abc_activity_chooser_view_list_item, paramViewGroup, false);
      }
      PackageManager localPackageManager = getContext().getPackageManager();
      ImageView localImageView = (ImageView)paramView.findViewById(R.id.icon);
      ResolveInfo localResolveInfo = (ResolveInfo)getItem(paramInt);
      localImageView.setImageDrawable(localResolveInfo.loadIcon(localPackageManager));
      ((TextView)paramView.findViewById(R.id.title)).setText(localResolveInfo.loadLabel(localPackageManager));
      if ((mShowDefaultActivity) && (paramInt == 0) && (mHighlightDefaultActivity)) {
        ViewCompat.setActivated(paramView, true);
      }
      for (;;)
      {
        return paramView;
        ViewCompat.setActivated(paramView, false);
      }
    }
    
    public int getViewTypeCount()
    {
      return 3;
    }
    
    public int measureContentWidth()
    {
      int i = mMaxActivityCount;
      mMaxActivityCount = Integer.MAX_VALUE;
      int j = 0;
      View localView = null;
      int k = View.MeasureSpec.makeMeasureSpec(0, 0);
      int m = View.MeasureSpec.makeMeasureSpec(0, 0);
      int n = getCount();
      for (int i1 = 0; i1 < n; i1++)
      {
        localView = getView(i1, localView, null);
        localView.measure(k, m);
        j = Math.max(j, localView.getMeasuredWidth());
      }
      mMaxActivityCount = i;
      return j;
    }
    
    public void setDataModel(ActivityChooserModel paramActivityChooserModel)
    {
      ActivityChooserModel localActivityChooserModel = mAdapter.getDataModel();
      if ((localActivityChooserModel != null) && (isShown())) {
        localActivityChooserModel.unregisterObserver(mModelDataSetObserver);
      }
      mDataModel = paramActivityChooserModel;
      if ((paramActivityChooserModel != null) && (isShown())) {
        paramActivityChooserModel.registerObserver(mModelDataSetObserver);
      }
      notifyDataSetChanged();
    }
    
    public void setMaxActivityCount(int paramInt)
    {
      if (mMaxActivityCount != paramInt)
      {
        mMaxActivityCount = paramInt;
        notifyDataSetChanged();
      }
    }
    
    public void setShowDefaultActivity(boolean paramBoolean1, boolean paramBoolean2)
    {
      if ((mShowDefaultActivity != paramBoolean1) || (mHighlightDefaultActivity != paramBoolean2))
      {
        mShowDefaultActivity = paramBoolean1;
        mHighlightDefaultActivity = paramBoolean2;
        notifyDataSetChanged();
      }
    }
    
    public void setShowFooterView(boolean paramBoolean)
    {
      if (mShowFooterView != paramBoolean)
      {
        mShowFooterView = paramBoolean;
        notifyDataSetChanged();
      }
    }
  }
  
  private class Callbacks
    implements AdapterView.OnItemClickListener, View.OnClickListener, View.OnLongClickListener, PopupWindow.OnDismissListener
  {
    Callbacks() {}
    
    private void notifyOnDismissListener()
    {
      if (mOnDismissListener != null) {
        mOnDismissListener.onDismiss();
      }
    }
    
    public void onClick(View paramView)
    {
      if (paramView == mDefaultActivityButton)
      {
        dismissPopup();
        ResolveInfo localResolveInfo = mAdapter.getDefaultActivity();
        int i = mAdapter.getDataModel().getActivityIndex(localResolveInfo);
        Intent localIntent = mAdapter.getDataModel().chooseActivity(i);
        if (localIntent != null)
        {
          localIntent.addFlags(524288);
          getContext().startActivity(localIntent);
        }
        return;
      }
      if (paramView == mExpandActivityOverflowButton)
      {
        mIsSelectingDefaultActivity = false;
        showPopupUnchecked(mInitialActivityCount);
        return;
      }
      throw new IllegalArgumentException();
    }
    
    public void onDismiss()
    {
      notifyOnDismissListener();
      if (mProvider != null) {
        mProvider.subUiVisibilityChanged(false);
      }
    }
    
    public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
      switch (((ActivityChooserView.ActivityChooserViewAdapter)paramAdapterView.getAdapter()).getItemViewType(paramInt))
      {
      default: 
        throw new IllegalArgumentException();
      case 1: 
        showPopupUnchecked(Integer.MAX_VALUE);
      }
      do
      {
        return;
        dismissPopup();
        if (!mIsSelectingDefaultActivity) {
          break;
        }
      } while (paramInt <= 0);
      mAdapter.getDataModel().setDefaultActivity(paramInt);
      return;
      if (mAdapter.getShowDefaultActivity()) {}
      for (;;)
      {
        Intent localIntent = mAdapter.getDataModel().chooseActivity(paramInt);
        if (localIntent == null) {
          break;
        }
        localIntent.addFlags(524288);
        getContext().startActivity(localIntent);
        return;
        paramInt++;
      }
    }
    
    public boolean onLongClick(View paramView)
    {
      if (paramView == mDefaultActivityButton)
      {
        if (mAdapter.getCount() > 0)
        {
          mIsSelectingDefaultActivity = true;
          showPopupUnchecked(mInitialActivityCount);
        }
        return true;
      }
      throw new IllegalArgumentException();
    }
  }
  
  public static class InnerLayout
    extends LinearLayoutCompat
  {
    private static final int[] TINT_ATTRS = { 16842964 };
    
    public InnerLayout(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, TINT_ATTRS);
      setBackgroundDrawable(localTintTypedArray.getDrawable(0));
      localTintTypedArray.recycle();
    }
  }
}
