package android.support.v7.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.NestedScrollView.OnScrollChangeListener;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.styleable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.lang.ref.WeakReference;

class AlertController
{
  ListAdapter mAdapter;
  private int mAlertDialogLayout;
  private final View.OnClickListener mButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      Message localMessage;
      if ((paramAnonymousView == mButtonPositive) && (mButtonPositiveMessage != null)) {
        localMessage = Message.obtain(mButtonPositiveMessage);
      }
      for (;;)
      {
        if (localMessage != null) {
          localMessage.sendToTarget();
        }
        mHandler.obtainMessage(1, mDialog).sendToTarget();
        return;
        if ((paramAnonymousView == mButtonNegative) && (mButtonNegativeMessage != null)) {
          localMessage = Message.obtain(mButtonNegativeMessage);
        } else if ((paramAnonymousView == mButtonNeutral) && (mButtonNeutralMessage != null)) {
          localMessage = Message.obtain(mButtonNeutralMessage);
        } else {
          localMessage = null;
        }
      }
    }
  };
  Button mButtonNegative;
  Message mButtonNegativeMessage;
  private CharSequence mButtonNegativeText;
  Button mButtonNeutral;
  Message mButtonNeutralMessage;
  private CharSequence mButtonNeutralText;
  private int mButtonPanelLayoutHint = 0;
  private int mButtonPanelSideLayout;
  Button mButtonPositive;
  Message mButtonPositiveMessage;
  private CharSequence mButtonPositiveText;
  int mCheckedItem = -1;
  private final Context mContext;
  private View mCustomTitleView;
  final AppCompatDialog mDialog;
  Handler mHandler;
  private Drawable mIcon;
  private int mIconId = 0;
  private ImageView mIconView;
  int mListItemLayout;
  int mListLayout;
  ListView mListView;
  private CharSequence mMessage;
  private TextView mMessageView;
  int mMultiChoiceItemLayout;
  NestedScrollView mScrollView;
  int mSingleChoiceItemLayout;
  private CharSequence mTitle;
  private TextView mTitleView;
  private View mView;
  private int mViewLayoutResId;
  private int mViewSpacingBottom;
  private int mViewSpacingLeft;
  private int mViewSpacingRight;
  private boolean mViewSpacingSpecified = false;
  private int mViewSpacingTop;
  private final Window mWindow;
  
  public AlertController(Context paramContext, AppCompatDialog paramAppCompatDialog, Window paramWindow)
  {
    mContext = paramContext;
    mDialog = paramAppCompatDialog;
    mWindow = paramWindow;
    mHandler = new ButtonHandler(paramAppCompatDialog);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(null, R.styleable.AlertDialog, R.attr.alertDialogStyle, 0);
    mAlertDialogLayout = localTypedArray.getResourceId(R.styleable.AlertDialog_android_layout, 0);
    mButtonPanelSideLayout = localTypedArray.getResourceId(R.styleable.AlertDialog_buttonPanelSideLayout, 0);
    mListLayout = localTypedArray.getResourceId(R.styleable.AlertDialog_listLayout, 0);
    mMultiChoiceItemLayout = localTypedArray.getResourceId(R.styleable.AlertDialog_multiChoiceItemLayout, 0);
    mSingleChoiceItemLayout = localTypedArray.getResourceId(R.styleable.AlertDialog_singleChoiceItemLayout, 0);
    mListItemLayout = localTypedArray.getResourceId(R.styleable.AlertDialog_listItemLayout, 0);
    localTypedArray.recycle();
    paramAppCompatDialog.supportRequestWindowFeature(1);
  }
  
  static boolean canTextInput(View paramView)
  {
    if (paramView.onCheckIsTextEditor()) {
      return true;
    }
    if (!(paramView instanceof ViewGroup)) {
      return false;
    }
    ViewGroup localViewGroup = (ViewGroup)paramView;
    int i = localViewGroup.getChildCount();
    while (i > 0)
    {
      i--;
      if (canTextInput(localViewGroup.getChildAt(i))) {
        return true;
      }
    }
    return false;
  }
  
  static void manageScrollIndicators(View paramView1, View paramView2, View paramView3)
  {
    int j;
    int i;
    if (paramView2 != null)
    {
      if (ViewCompat.canScrollVertically(paramView1, -1))
      {
        j = 0;
        paramView2.setVisibility(j);
      }
    }
    else if (paramView3 != null)
    {
      boolean bool = ViewCompat.canScrollVertically(paramView1, 1);
      i = 0;
      if (!bool) {
        break label51;
      }
    }
    for (;;)
    {
      paramView3.setVisibility(i);
      return;
      j = 4;
      break;
      label51:
      i = 4;
    }
  }
  
  @Nullable
  private ViewGroup resolvePanel(@Nullable View paramView1, @Nullable View paramView2)
  {
    if (paramView1 == null)
    {
      if ((paramView2 instanceof ViewStub)) {
        paramView2 = ((ViewStub)paramView2).inflate();
      }
      return (ViewGroup)paramView2;
    }
    if (paramView2 != null)
    {
      ViewParent localViewParent = paramView2.getParent();
      if ((localViewParent instanceof ViewGroup)) {
        ((ViewGroup)localViewParent).removeView(paramView2);
      }
    }
    if ((paramView1 instanceof ViewStub)) {
      paramView1 = ((ViewStub)paramView1).inflate();
    }
    return (ViewGroup)paramView1;
  }
  
  private int selectContentView()
  {
    if (mButtonPanelSideLayout == 0) {
      return mAlertDialogLayout;
    }
    if (mButtonPanelLayoutHint == 1) {
      return mButtonPanelSideLayout;
    }
    return mAlertDialogLayout;
  }
  
  private void setScrollIndicators(ViewGroup paramViewGroup, View paramView, int paramInt1, int paramInt2)
  {
    View localView1 = mWindow.findViewById(R.id.scrollIndicatorUp);
    View localView2 = mWindow.findViewById(R.id.scrollIndicatorDown);
    if (Build.VERSION.SDK_INT >= 23)
    {
      ViewCompat.setScrollIndicators(paramView, paramInt1, paramInt2);
      if (localView1 != null) {
        paramViewGroup.removeView(localView1);
      }
      if (localView2 != null) {
        paramViewGroup.removeView(localView2);
      }
    }
    final View localView4;
    do
    {
      do
      {
        return;
        if ((localView1 != null) && ((paramInt1 & 0x1) == 0))
        {
          paramViewGroup.removeView(localView1);
          localView1 = null;
        }
        if ((localView2 != null) && ((paramInt1 & 0x2) == 0))
        {
          paramViewGroup.removeView(localView2);
          localView2 = null;
        }
      } while ((localView1 == null) && (localView2 == null));
      final View localView3 = localView1;
      localView4 = localView2;
      if (mMessage != null)
      {
        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
        {
          public void onScrollChange(NestedScrollView paramAnonymousNestedScrollView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
          {
            AlertController.manageScrollIndicators(paramAnonymousNestedScrollView, localView3, localView4);
          }
        });
        mScrollView.post(new Runnable()
        {
          public void run()
          {
            AlertController.manageScrollIndicators(mScrollView, localView3, localView4);
          }
        });
        return;
      }
      if (mListView != null)
      {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
          public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            AlertController.manageScrollIndicators(paramAnonymousAbsListView, localView3, localView4);
          }
          
          public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt) {}
        });
        mListView.post(new Runnable()
        {
          public void run()
          {
            AlertController.manageScrollIndicators(mListView, localView3, localView4);
          }
        });
        return;
      }
      if (localView3 != null) {
        paramViewGroup.removeView(localView3);
      }
    } while (localView4 == null);
    paramViewGroup.removeView(localView4);
  }
  
  private void setupButtons(ViewGroup paramViewGroup)
  {
    int i = 0;
    mButtonPositive = ((Button)paramViewGroup.findViewById(16908313));
    mButtonPositive.setOnClickListener(mButtonHandler);
    if (TextUtils.isEmpty(mButtonPositiveText))
    {
      mButtonPositive.setVisibility(8);
      mButtonNegative = ((Button)paramViewGroup.findViewById(16908314));
      mButtonNegative.setOnClickListener(mButtonHandler);
      if (!TextUtils.isEmpty(mButtonNegativeText)) {
        break label179;
      }
      mButtonNegative.setVisibility(8);
      label90:
      mButtonNeutral = ((Button)paramViewGroup.findViewById(16908315));
      mButtonNeutral.setOnClickListener(mButtonHandler);
      if (!TextUtils.isEmpty(mButtonNeutralText)) {
        break label205;
      }
      mButtonNeutral.setVisibility(8);
    }
    for (;;)
    {
      int j = 0;
      if (i != 0) {
        j = 1;
      }
      if (j == 0) {
        paramViewGroup.setVisibility(8);
      }
      return;
      mButtonPositive.setText(mButtonPositiveText);
      mButtonPositive.setVisibility(0);
      i = 0x0 | 0x1;
      break;
      label179:
      mButtonNegative.setText(mButtonNegativeText);
      mButtonNegative.setVisibility(0);
      i |= 0x2;
      break label90;
      label205:
      mButtonNeutral.setText(mButtonNeutralText);
      mButtonNeutral.setVisibility(0);
      i |= 0x4;
    }
  }
  
  private void setupContent(ViewGroup paramViewGroup)
  {
    mScrollView = ((NestedScrollView)mWindow.findViewById(R.id.scrollView));
    mScrollView.setFocusable(false);
    mScrollView.setNestedScrollingEnabled(false);
    mMessageView = ((TextView)paramViewGroup.findViewById(16908299));
    if (mMessageView == null) {
      return;
    }
    if (mMessage != null)
    {
      mMessageView.setText(mMessage);
      return;
    }
    mMessageView.setVisibility(8);
    mScrollView.removeView(mMessageView);
    if (mListView != null)
    {
      ViewGroup localViewGroup = (ViewGroup)mScrollView.getParent();
      int i = localViewGroup.indexOfChild(mScrollView);
      localViewGroup.removeViewAt(i);
      localViewGroup.addView(mListView, i, new ViewGroup.LayoutParams(-1, -1));
      return;
    }
    paramViewGroup.setVisibility(8);
  }
  
  private void setupCustomContent(ViewGroup paramViewGroup)
  {
    View localView;
    if (mView != null) {
      localView = mView;
    }
    for (;;)
    {
      int i = 0;
      if (localView != null) {
        i = 1;
      }
      if ((i == 0) || (!canTextInput(localView))) {
        mWindow.setFlags(131072, 131072);
      }
      if (i == 0) {
        break;
      }
      FrameLayout localFrameLayout = (FrameLayout)mWindow.findViewById(R.id.custom);
      localFrameLayout.addView(localView, new ViewGroup.LayoutParams(-1, -1));
      if (mViewSpacingSpecified) {
        localFrameLayout.setPadding(mViewSpacingLeft, mViewSpacingTop, mViewSpacingRight, mViewSpacingBottom);
      }
      if (mListView != null) {
        getLayoutParamsweight = 0.0F;
      }
      return;
      if (mViewLayoutResId != 0) {
        localView = LayoutInflater.from(mContext).inflate(mViewLayoutResId, paramViewGroup, false);
      } else {
        localView = null;
      }
    }
    paramViewGroup.setVisibility(8);
  }
  
  private void setupTitle(ViewGroup paramViewGroup)
  {
    if (mCustomTitleView != null)
    {
      ViewGroup.LayoutParams localLayoutParams = new ViewGroup.LayoutParams(-1, -2);
      paramViewGroup.addView(mCustomTitleView, 0, localLayoutParams);
      mWindow.findViewById(R.id.title_template).setVisibility(8);
      return;
    }
    mIconView = ((ImageView)mWindow.findViewById(16908294));
    boolean bool = TextUtils.isEmpty(mTitle);
    int i = 0;
    if (!bool) {
      i = 1;
    }
    if (i != 0)
    {
      mTitleView = ((TextView)mWindow.findViewById(R.id.alertTitle));
      mTitleView.setText(mTitle);
      if (mIconId != 0)
      {
        mIconView.setImageResource(mIconId);
        return;
      }
      if (mIcon != null)
      {
        mIconView.setImageDrawable(mIcon);
        return;
      }
      mTitleView.setPadding(mIconView.getPaddingLeft(), mIconView.getPaddingTop(), mIconView.getPaddingRight(), mIconView.getPaddingBottom());
      mIconView.setVisibility(8);
      return;
    }
    mWindow.findViewById(R.id.title_template).setVisibility(8);
    mIconView.setVisibility(8);
    paramViewGroup.setVisibility(8);
  }
  
  private void setupView()
  {
    View localView1 = mWindow.findViewById(R.id.parentPanel);
    View localView2 = localView1.findViewById(R.id.topPanel);
    View localView3 = localView1.findViewById(R.id.contentPanel);
    View localView4 = localView1.findViewById(R.id.buttonPanel);
    ViewGroup localViewGroup1 = (ViewGroup)localView1.findViewById(R.id.customPanel);
    setupCustomContent(localViewGroup1);
    View localView5 = localViewGroup1.findViewById(R.id.topPanel);
    View localView6 = localViewGroup1.findViewById(R.id.contentPanel);
    View localView7 = localViewGroup1.findViewById(R.id.buttonPanel);
    ViewGroup localViewGroup2 = resolvePanel(localView5, localView2);
    ViewGroup localViewGroup3 = resolvePanel(localView6, localView3);
    ViewGroup localViewGroup4 = resolvePanel(localView7, localView4);
    setupContent(localViewGroup3);
    setupButtons(localViewGroup4);
    setupTitle(localViewGroup2);
    int i;
    int j;
    label166:
    int k;
    label184:
    Object localObject;
    label253:
    int n;
    if ((localViewGroup1 != null) && (localViewGroup1.getVisibility() != 8))
    {
      i = 1;
      if ((localViewGroup2 == null) || (localViewGroup2.getVisibility() == 8)) {
        break label349;
      }
      j = 1;
      if ((localViewGroup4 == null) || (localViewGroup4.getVisibility() == 8)) {
        break label355;
      }
      k = 1;
      if ((k == 0) && (localViewGroup3 != null))
      {
        View localView8 = localViewGroup3.findViewById(R.id.textSpacerNoButtons);
        if (localView8 != null) {
          localView8.setVisibility(0);
        }
      }
      if ((j != 0) && (mScrollView != null)) {
        mScrollView.setClipToPadding(true);
      }
      if (i == 0)
      {
        if (mListView == null) {
          break label361;
        }
        localObject = mListView;
        if (localObject != null)
        {
          if (j == 0) {
            break label370;
          }
          n = 1;
          label266:
          if (k == 0) {
            break label376;
          }
        }
      }
    }
    label349:
    label355:
    label361:
    label370:
    label376:
    for (int i1 = 2;; i1 = 0)
    {
      setScrollIndicators(localViewGroup3, (View)localObject, n | i1, 3);
      ListView localListView = mListView;
      if ((localListView != null) && (mAdapter != null))
      {
        localListView.setAdapter(mAdapter);
        int m = mCheckedItem;
        if (m > -1)
        {
          localListView.setItemChecked(m, true);
          localListView.setSelection(m);
        }
      }
      return;
      i = 0;
      break;
      j = 0;
      break label166;
      k = 0;
      break label184;
      localObject = mScrollView;
      break label253;
      n = 0;
      break label266;
    }
  }
  
  public Button getButton(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case -1: 
      return mButtonPositive;
    case -2: 
      return mButtonNegative;
    }
    return mButtonNeutral;
  }
  
  public int getIconAttributeResId(int paramInt)
  {
    TypedValue localTypedValue = new TypedValue();
    mContext.getTheme().resolveAttribute(paramInt, localTypedValue, true);
    return resourceId;
  }
  
  public ListView getListView()
  {
    return mListView;
  }
  
  public void installContent()
  {
    int i = selectContentView();
    mDialog.setContentView(i);
    setupView();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    return (mScrollView != null) && (mScrollView.executeKeyEvent(paramKeyEvent));
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    return (mScrollView != null) && (mScrollView.executeKeyEvent(paramKeyEvent));
  }
  
  public void setButton(int paramInt, CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener, Message paramMessage)
  {
    if ((paramMessage == null) && (paramOnClickListener != null)) {
      paramMessage = mHandler.obtainMessage(paramInt, paramOnClickListener);
    }
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Button does not exist");
    case -1: 
      mButtonPositiveText = paramCharSequence;
      mButtonPositiveMessage = paramMessage;
      return;
    case -2: 
      mButtonNegativeText = paramCharSequence;
      mButtonNegativeMessage = paramMessage;
      return;
    }
    mButtonNeutralText = paramCharSequence;
    mButtonNeutralMessage = paramMessage;
  }
  
  public void setButtonPanelLayoutHint(int paramInt)
  {
    mButtonPanelLayoutHint = paramInt;
  }
  
  public void setCustomTitle(View paramView)
  {
    mCustomTitleView = paramView;
  }
  
  public void setIcon(int paramInt)
  {
    mIcon = null;
    mIconId = paramInt;
    if (mIconView != null)
    {
      if (paramInt != 0)
      {
        mIconView.setVisibility(0);
        mIconView.setImageResource(mIconId);
      }
    }
    else {
      return;
    }
    mIconView.setVisibility(8);
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    mIcon = paramDrawable;
    mIconId = 0;
    if (mIconView != null)
    {
      if (paramDrawable != null)
      {
        mIconView.setVisibility(0);
        mIconView.setImageDrawable(paramDrawable);
      }
    }
    else {
      return;
    }
    mIconView.setVisibility(8);
  }
  
  public void setMessage(CharSequence paramCharSequence)
  {
    mMessage = paramCharSequence;
    if (mMessageView != null) {
      mMessageView.setText(paramCharSequence);
    }
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    mTitle = paramCharSequence;
    if (mTitleView != null) {
      mTitleView.setText(paramCharSequence);
    }
  }
  
  public void setView(int paramInt)
  {
    mView = null;
    mViewLayoutResId = paramInt;
    mViewSpacingSpecified = false;
  }
  
  public void setView(View paramView)
  {
    mView = paramView;
    mViewLayoutResId = 0;
    mViewSpacingSpecified = false;
  }
  
  public void setView(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    mView = paramView;
    mViewLayoutResId = 0;
    mViewSpacingSpecified = true;
    mViewSpacingLeft = paramInt1;
    mViewSpacingTop = paramInt2;
    mViewSpacingRight = paramInt3;
    mViewSpacingBottom = paramInt4;
  }
  
  public static class AlertParams
  {
    public ListAdapter mAdapter;
    public boolean mCancelable;
    public int mCheckedItem = -1;
    public boolean[] mCheckedItems;
    public final Context mContext;
    public Cursor mCursor;
    public View mCustomTitleView;
    public boolean mForceInverseBackground;
    public Drawable mIcon;
    public int mIconAttrId = 0;
    public int mIconId = 0;
    public final LayoutInflater mInflater;
    public String mIsCheckedColumn;
    public boolean mIsMultiChoice;
    public boolean mIsSingleChoice;
    public CharSequence[] mItems;
    public String mLabelColumn;
    public CharSequence mMessage;
    public DialogInterface.OnClickListener mNegativeButtonListener;
    public CharSequence mNegativeButtonText;
    public DialogInterface.OnClickListener mNeutralButtonListener;
    public CharSequence mNeutralButtonText;
    public DialogInterface.OnCancelListener mOnCancelListener;
    public DialogInterface.OnMultiChoiceClickListener mOnCheckboxClickListener;
    public DialogInterface.OnClickListener mOnClickListener;
    public DialogInterface.OnDismissListener mOnDismissListener;
    public AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    public DialogInterface.OnKeyListener mOnKeyListener;
    public OnPrepareListViewListener mOnPrepareListViewListener;
    public DialogInterface.OnClickListener mPositiveButtonListener;
    public CharSequence mPositiveButtonText;
    public boolean mRecycleOnMeasure = true;
    public CharSequence mTitle;
    public View mView;
    public int mViewLayoutResId;
    public int mViewSpacingBottom;
    public int mViewSpacingLeft;
    public int mViewSpacingRight;
    public boolean mViewSpacingSpecified = false;
    public int mViewSpacingTop;
    
    public AlertParams(Context paramContext)
    {
      mContext = paramContext;
      mCancelable = true;
      mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
    }
    
    private void createListView(final AlertController paramAlertController)
    {
      final ListView localListView = (ListView)mInflater.inflate(mListLayout, null);
      Object localObject;
      if (mIsMultiChoice) {
        if (mCursor == null)
        {
          localObject = new ArrayAdapter(mContext, mMultiChoiceItemLayout, 16908308, mItems)
          {
            public View getView(int paramAnonymousInt, View paramAnonymousView, ViewGroup paramAnonymousViewGroup)
            {
              View localView = super.getView(paramAnonymousInt, paramAnonymousView, paramAnonymousViewGroup);
              if ((mCheckedItems != null) && (mCheckedItems[paramAnonymousInt] != 0)) {
                localListView.setItemChecked(paramAnonymousInt, true);
              }
              return localView;
            }
          };
          if (mOnPrepareListViewListener != null) {
            mOnPrepareListViewListener.onPrepareListView(localListView);
          }
          mAdapter = ((ListAdapter)localObject);
          mCheckedItem = mCheckedItem;
          if (mOnClickListener == null) {
            break label290;
          }
          localListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
          {
            public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
            {
              mOnClickListener.onClick(paramAlertControllermDialog, paramAnonymousInt);
              if (!mIsSingleChoice) {
                paramAlertControllermDialog.dismiss();
              }
            }
          });
          label106:
          if (mOnItemSelectedListener != null) {
            localListView.setOnItemSelectedListener(mOnItemSelectedListener);
          }
          if (!mIsSingleChoice) {
            break label314;
          }
          localListView.setChoiceMode(1);
        }
      }
      for (;;)
      {
        mListView = localListView;
        return;
        Context localContext2 = mContext;
        Cursor localCursor2 = mCursor;
        localObject = new CursorAdapter(localContext2, localCursor2, false)
        {
          private final int mIsCheckedIndex;
          private final int mLabelIndex;
          
          public void bindView(View paramAnonymousView, Context paramAnonymousContext, Cursor paramAnonymousCursor)
          {
            int i = 1;
            ((CheckedTextView)paramAnonymousView.findViewById(16908308)).setText(paramAnonymousCursor.getString(mLabelIndex));
            ListView localListView = localListView;
            int k = paramAnonymousCursor.getPosition();
            if (paramAnonymousCursor.getInt(mIsCheckedIndex) == i) {}
            for (;;)
            {
              localListView.setItemChecked(k, i);
              return;
              int j = 0;
            }
          }
          
          public View newView(Context paramAnonymousContext, Cursor paramAnonymousCursor, ViewGroup paramAnonymousViewGroup)
          {
            return mInflater.inflate(paramAlertControllermMultiChoiceItemLayout, paramAnonymousViewGroup, false);
          }
        };
        break;
        if (mIsSingleChoice) {}
        for (int i = mSingleChoiceItemLayout;; i = mListItemLayout)
        {
          if (mCursor == null) {
            break label251;
          }
          Context localContext1 = mContext;
          Cursor localCursor1 = mCursor;
          String[] arrayOfString = new String[1];
          arrayOfString[0] = mLabelColumn;
          localObject = new SimpleCursorAdapter(localContext1, i, localCursor1, arrayOfString, new int[] { 16908308 });
          break;
        }
        label251:
        if (mAdapter != null)
        {
          localObject = mAdapter;
          break;
        }
        localObject = new AlertController.CheckedItemAdapter(mContext, i, 16908308, mItems);
        break;
        label290:
        if (mOnCheckboxClickListener == null) {
          break label106;
        }
        localListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
          public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
          {
            if (mCheckedItems != null) {
              mCheckedItems[paramAnonymousInt] = localListView.isItemChecked(paramAnonymousInt);
            }
            mOnCheckboxClickListener.onClick(paramAlertControllermDialog, paramAnonymousInt, localListView.isItemChecked(paramAnonymousInt));
          }
        });
        break label106;
        label314:
        if (mIsMultiChoice) {
          localListView.setChoiceMode(2);
        }
      }
    }
    
    public void apply(AlertController paramAlertController)
    {
      if (mCustomTitleView != null)
      {
        paramAlertController.setCustomTitle(mCustomTitleView);
        if (mMessage != null) {
          paramAlertController.setMessage(mMessage);
        }
        if (mPositiveButtonText != null) {
          paramAlertController.setButton(-1, mPositiveButtonText, mPositiveButtonListener, null);
        }
        if (mNegativeButtonText != null) {
          paramAlertController.setButton(-2, mNegativeButtonText, mNegativeButtonListener, null);
        }
        if (mNeutralButtonText != null) {
          paramAlertController.setButton(-3, mNeutralButtonText, mNeutralButtonListener, null);
        }
        if ((mItems != null) || (mCursor != null) || (mAdapter != null)) {
          createListView(paramAlertController);
        }
        if (mView == null) {
          break label236;
        }
        if (!mViewSpacingSpecified) {
          break label227;
        }
        paramAlertController.setView(mView, mViewSpacingLeft, mViewSpacingTop, mViewSpacingRight, mViewSpacingBottom);
      }
      label227:
      label236:
      while (mViewLayoutResId == 0)
      {
        return;
        if (mTitle != null) {
          paramAlertController.setTitle(mTitle);
        }
        if (mIcon != null) {
          paramAlertController.setIcon(mIcon);
        }
        if (mIconId != 0) {
          paramAlertController.setIcon(mIconId);
        }
        if (mIconAttrId == 0) {
          break;
        }
        paramAlertController.setIcon(paramAlertController.getIconAttributeResId(mIconAttrId));
        break;
        paramAlertController.setView(mView);
        return;
      }
      paramAlertController.setView(mViewLayoutResId);
    }
    
    public static abstract interface OnPrepareListViewListener
    {
      public abstract void onPrepareListView(ListView paramListView);
    }
  }
  
  private static final class ButtonHandler
    extends Handler
  {
    private static final int MSG_DISMISS_DIALOG = 1;
    private WeakReference<DialogInterface> mDialog;
    
    public ButtonHandler(DialogInterface paramDialogInterface)
    {
      mDialog = new WeakReference(paramDialogInterface);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (what)
      {
      case 0: 
      default: 
        return;
      case -3: 
      case -2: 
      case -1: 
        ((DialogInterface.OnClickListener)obj).onClick((DialogInterface)mDialog.get(), what);
        return;
      }
      ((DialogInterface)obj).dismiss();
    }
  }
  
  private static class CheckedItemAdapter
    extends ArrayAdapter<CharSequence>
  {
    public CheckedItemAdapter(Context paramContext, int paramInt1, int paramInt2, CharSequence[] paramArrayOfCharSequence)
    {
      super(paramInt1, paramInt2, paramArrayOfCharSequence);
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public boolean hasStableIds()
    {
      return true;
    }
  }
}
