package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.TintableBackgroundView;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.layout;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.menu.ShowableListMenu;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class AppCompatSpinner
  extends Spinner
  implements TintableBackgroundView
{
  private static final int[] ATTRS_ANDROID_SPINNERMODE;
  private static final boolean IS_AT_LEAST_JB = false;
  static final boolean IS_AT_LEAST_M = false;
  private static final int MAX_ITEMS_MEASURED = 15;
  private static final int MODE_DIALOG = 0;
  private static final int MODE_DROPDOWN = 1;
  private static final int MODE_THEME = -1;
  private static final String TAG = "AppCompatSpinner";
  private AppCompatBackgroundHelper mBackgroundTintHelper;
  int mDropDownWidth;
  private ForwardingListener mForwardingListener;
  DropdownPopup mPopup;
  private Context mPopupContext;
  private boolean mPopupSet;
  private SpinnerAdapter mTempAdapter;
  final Rect mTempRect = new Rect();
  
  static
  {
    boolean bool1;
    if (Build.VERSION.SDK_INT >= 23)
    {
      bool1 = true;
      IS_AT_LEAST_M = bool1;
      if (Build.VERSION.SDK_INT < 16) {
        break label45;
      }
    }
    label45:
    for (boolean bool2 = true;; bool2 = false)
    {
      IS_AT_LEAST_JB = bool2;
      ATTRS_ANDROID_SPINNERMODE = new int[] { 16843505 };
      return;
      bool1 = false;
      break;
    }
  }
  
  public AppCompatSpinner(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AppCompatSpinner(Context paramContext, int paramInt)
  {
    this(paramContext, null, R.attr.spinnerStyle, paramInt);
  }
  
  public AppCompatSpinner(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.spinnerStyle);
  }
  
  public AppCompatSpinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, -1);
  }
  
  public AppCompatSpinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    this(paramContext, paramAttributeSet, paramInt1, paramInt2, null);
  }
  
  public AppCompatSpinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2, Resources.Theme paramTheme)
  {
    super(paramContext, paramAttributeSet, paramInt1);
    TintTypedArray localTintTypedArray1 = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.Spinner, paramInt1, 0);
    mBackgroundTintHelper = new AppCompatBackgroundHelper(this);
    TypedArray localTypedArray;
    if (paramTheme != null)
    {
      mPopupContext = new ContextThemeWrapper(paramContext, paramTheme);
      if (mPopupContext != null) {
        if (paramInt2 == -1)
        {
          if (Build.VERSION.SDK_INT < 11) {
            break label412;
          }
          localTypedArray = null;
        }
      }
    }
    for (;;)
    {
      try
      {
        localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, ATTRS_ANDROID_SPINNERMODE, paramInt1, 0);
        if (localTypedArray.hasValue(0))
        {
          int j = localTypedArray.getInt(0, 0);
          paramInt2 = j;
        }
      }
      catch (Exception localException)
      {
        final DropdownPopup localDropdownPopup;
        TintTypedArray localTintTypedArray2;
        CharSequence[] arrayOfCharSequence;
        ArrayAdapter localArrayAdapter;
        int i;
        Context localContext;
        Log.i("AppCompatSpinner", "Could not read android:spinnerMode", localException);
        if (localTypedArray == null) {
          continue;
        }
        localTypedArray.recycle();
        continue;
      }
      finally
      {
        if (localTypedArray == null) {
          continue;
        }
        localTypedArray.recycle();
      }
      if (paramInt2 == 1)
      {
        localDropdownPopup = new DropdownPopup(mPopupContext, paramAttributeSet, paramInt1);
        localTintTypedArray2 = TintTypedArray.obtainStyledAttributes(mPopupContext, paramAttributeSet, R.styleable.Spinner, paramInt1, 0);
        mDropDownWidth = localTintTypedArray2.getLayoutDimension(R.styleable.Spinner_android_dropDownWidth, -2);
        localDropdownPopup.setBackgroundDrawable(localTintTypedArray2.getDrawable(R.styleable.Spinner_android_popupBackground));
        localDropdownPopup.setPromptText(localTintTypedArray1.getString(R.styleable.Spinner_android_prompt));
        localTintTypedArray2.recycle();
        mPopup = localDropdownPopup;
        mForwardingListener = new ForwardingListener(this)
        {
          public ShowableListMenu getPopup()
          {
            return localDropdownPopup;
          }
          
          public boolean onForwardingStarted()
          {
            if (!mPopup.isShowing()) {
              mPopup.show();
            }
            return true;
          }
        };
      }
      arrayOfCharSequence = localTintTypedArray1.getTextArray(R.styleable.Spinner_android_entries);
      if (arrayOfCharSequence != null)
      {
        localArrayAdapter = new ArrayAdapter(paramContext, 17367048, arrayOfCharSequence);
        localArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        setAdapter(localArrayAdapter);
      }
      localTintTypedArray1.recycle();
      mPopupSet = true;
      if (mTempAdapter != null)
      {
        setAdapter(mTempAdapter);
        mTempAdapter = null;
      }
      mBackgroundTintHelper.loadFromAttributes(paramAttributeSet, paramInt1);
      return;
      i = localTintTypedArray1.getResourceId(R.styleable.Spinner_popupTheme, 0);
      if (i != 0)
      {
        mPopupContext = new ContextThemeWrapper(paramContext, i);
        break;
      }
      if (!IS_AT_LEAST_M)
      {
        localContext = paramContext;
        mPopupContext = localContext;
        break;
      }
      localContext = null;
      continue;
      label412:
      paramInt2 = 1;
    }
  }
  
  int compatMeasureContentWidth(SpinnerAdapter paramSpinnerAdapter, Drawable paramDrawable)
  {
    int i;
    if (paramSpinnerAdapter == null) {
      i = 0;
    }
    do
    {
      return i;
      i = 0;
      View localView = null;
      int j = 0;
      int k = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 0);
      int m = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0);
      int n = Math.max(0, getSelectedItemPosition());
      int i1 = Math.min(paramSpinnerAdapter.getCount(), n + 15);
      for (int i2 = Math.max(0, n - (15 - (i1 - n))); i2 < i1; i2++)
      {
        int i3 = paramSpinnerAdapter.getItemViewType(i2);
        if (i3 != j)
        {
          j = i3;
          localView = null;
        }
        localView = paramSpinnerAdapter.getView(i2, localView, this);
        if (localView.getLayoutParams() == null) {
          localView.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        }
        localView.measure(k, m);
        i = Math.max(i, localView.getMeasuredWidth());
      }
    } while (paramDrawable == null);
    paramDrawable.getPadding(mTempRect);
    return i + (mTempRect.left + mTempRect.right);
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.applySupportBackgroundTint();
    }
  }
  
  public int getDropDownHorizontalOffset()
  {
    if (mPopup != null) {
      return mPopup.getHorizontalOffset();
    }
    if (IS_AT_LEAST_JB) {
      return super.getDropDownHorizontalOffset();
    }
    return 0;
  }
  
  public int getDropDownVerticalOffset()
  {
    if (mPopup != null) {
      return mPopup.getVerticalOffset();
    }
    if (IS_AT_LEAST_JB) {
      return super.getDropDownVerticalOffset();
    }
    return 0;
  }
  
  public int getDropDownWidth()
  {
    if (mPopup != null) {
      return mDropDownWidth;
    }
    if (IS_AT_LEAST_JB) {
      return super.getDropDownWidth();
    }
    return 0;
  }
  
  public Drawable getPopupBackground()
  {
    if (mPopup != null) {
      return mPopup.getBackground();
    }
    if (IS_AT_LEAST_JB) {
      return super.getPopupBackground();
    }
    return null;
  }
  
  public Context getPopupContext()
  {
    if (mPopup != null) {
      return mPopupContext;
    }
    if (IS_AT_LEAST_M) {
      return super.getPopupContext();
    }
    return null;
  }
  
  public CharSequence getPrompt()
  {
    if (mPopup != null) {
      return mPopup.getHintText();
    }
    return super.getPrompt();
  }
  
  @Nullable
  public ColorStateList getSupportBackgroundTintList()
  {
    if (mBackgroundTintHelper != null) {
      return mBackgroundTintHelper.getSupportBackgroundTintList();
    }
    return null;
  }
  
  @Nullable
  public PorterDuff.Mode getSupportBackgroundTintMode()
  {
    if (mBackgroundTintHelper != null) {
      return mBackgroundTintHelper.getSupportBackgroundTintMode();
    }
    return null;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if ((mPopup != null) && (mPopup.isShowing())) {
      mPopup.dismiss();
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if ((mPopup != null) && (View.MeasureSpec.getMode(paramInt1) == Integer.MIN_VALUE)) {
      setMeasuredDimension(Math.min(Math.max(getMeasuredWidth(), compatMeasureContentWidth(getAdapter(), getBackground())), View.MeasureSpec.getSize(paramInt1)), getMeasuredHeight());
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((mForwardingListener != null) && (mForwardingListener.onTouch(this, paramMotionEvent))) {
      return true;
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public boolean performClick()
  {
    if (mPopup != null)
    {
      if (!mPopup.isShowing()) {
        mPopup.show();
      }
      return true;
    }
    return super.performClick();
  }
  
  public void setAdapter(SpinnerAdapter paramSpinnerAdapter)
  {
    if (!mPopupSet) {
      mTempAdapter = paramSpinnerAdapter;
    }
    do
    {
      return;
      super.setAdapter(paramSpinnerAdapter);
    } while (mPopup == null);
    if (mPopupContext == null) {}
    for (Context localContext = getContext();; localContext = mPopupContext)
    {
      mPopup.setAdapter(new DropDownAdapter(paramSpinnerAdapter, localContext.getTheme()));
      return;
    }
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    super.setBackgroundDrawable(paramDrawable);
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.onSetBackgroundDrawable(paramDrawable);
    }
  }
  
  public void setBackgroundResource(@DrawableRes int paramInt)
  {
    super.setBackgroundResource(paramInt);
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.onSetBackgroundResource(paramInt);
    }
  }
  
  public void setDropDownHorizontalOffset(int paramInt)
  {
    if (mPopup != null) {
      mPopup.setHorizontalOffset(paramInt);
    }
    while (!IS_AT_LEAST_JB) {
      return;
    }
    super.setDropDownHorizontalOffset(paramInt);
  }
  
  public void setDropDownVerticalOffset(int paramInt)
  {
    if (mPopup != null) {
      mPopup.setVerticalOffset(paramInt);
    }
    while (!IS_AT_LEAST_JB) {
      return;
    }
    super.setDropDownVerticalOffset(paramInt);
  }
  
  public void setDropDownWidth(int paramInt)
  {
    if (mPopup != null) {
      mDropDownWidth = paramInt;
    }
    while (!IS_AT_LEAST_JB) {
      return;
    }
    super.setDropDownWidth(paramInt);
  }
  
  public void setPopupBackgroundDrawable(Drawable paramDrawable)
  {
    if (mPopup != null) {
      mPopup.setBackgroundDrawable(paramDrawable);
    }
    while (!IS_AT_LEAST_JB) {
      return;
    }
    super.setPopupBackgroundDrawable(paramDrawable);
  }
  
  public void setPopupBackgroundResource(@DrawableRes int paramInt)
  {
    setPopupBackgroundDrawable(AppCompatResources.getDrawable(getPopupContext(), paramInt));
  }
  
  public void setPrompt(CharSequence paramCharSequence)
  {
    if (mPopup != null)
    {
      mPopup.setPromptText(paramCharSequence);
      return;
    }
    super.setPrompt(paramCharSequence);
  }
  
  public void setSupportBackgroundTintList(@Nullable ColorStateList paramColorStateList)
  {
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.setSupportBackgroundTintList(paramColorStateList);
    }
  }
  
  public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode paramMode)
  {
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.setSupportBackgroundTintMode(paramMode);
    }
  }
  
  private static class DropDownAdapter
    implements ListAdapter, SpinnerAdapter
  {
    private SpinnerAdapter mAdapter;
    private ListAdapter mListAdapter;
    
    public DropDownAdapter(@Nullable SpinnerAdapter paramSpinnerAdapter, @Nullable Resources.Theme paramTheme)
    {
      mAdapter = paramSpinnerAdapter;
      if ((paramSpinnerAdapter instanceof ListAdapter)) {
        mListAdapter = ((ListAdapter)paramSpinnerAdapter);
      }
      if (paramTheme != null)
      {
        if ((!AppCompatSpinner.IS_AT_LEAST_M) || (!(paramSpinnerAdapter instanceof android.widget.ThemedSpinnerAdapter))) {
          break label67;
        }
        android.widget.ThemedSpinnerAdapter localThemedSpinnerAdapter1 = (android.widget.ThemedSpinnerAdapter)paramSpinnerAdapter;
        if (localThemedSpinnerAdapter1.getDropDownViewTheme() != paramTheme) {
          localThemedSpinnerAdapter1.setDropDownViewTheme(paramTheme);
        }
      }
      label67:
      ThemedSpinnerAdapter localThemedSpinnerAdapter;
      do
      {
        do
        {
          return;
        } while (!(paramSpinnerAdapter instanceof ThemedSpinnerAdapter));
        localThemedSpinnerAdapter = (ThemedSpinnerAdapter)paramSpinnerAdapter;
      } while (localThemedSpinnerAdapter.getDropDownViewTheme() != null);
      localThemedSpinnerAdapter.setDropDownViewTheme(paramTheme);
    }
    
    public boolean areAllItemsEnabled()
    {
      ListAdapter localListAdapter = mListAdapter;
      if (localListAdapter != null) {
        return localListAdapter.areAllItemsEnabled();
      }
      return true;
    }
    
    public int getCount()
    {
      if (mAdapter == null) {
        return 0;
      }
      return mAdapter.getCount();
    }
    
    public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (mAdapter == null) {
        return null;
      }
      return mAdapter.getDropDownView(paramInt, paramView, paramViewGroup);
    }
    
    public Object getItem(int paramInt)
    {
      if (mAdapter == null) {
        return null;
      }
      return mAdapter.getItem(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      if (mAdapter == null) {
        return -1L;
      }
      return mAdapter.getItemId(paramInt);
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      return getDropDownView(paramInt, paramView, paramViewGroup);
    }
    
    public int getViewTypeCount()
    {
      return 1;
    }
    
    public boolean hasStableIds()
    {
      return (mAdapter != null) && (mAdapter.hasStableIds());
    }
    
    public boolean isEmpty()
    {
      return getCount() == 0;
    }
    
    public boolean isEnabled(int paramInt)
    {
      ListAdapter localListAdapter = mListAdapter;
      if (localListAdapter != null) {
        return localListAdapter.isEnabled(paramInt);
      }
      return true;
    }
    
    public void registerDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (mAdapter != null) {
        mAdapter.registerDataSetObserver(paramDataSetObserver);
      }
    }
    
    public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (mAdapter != null) {
        mAdapter.unregisterDataSetObserver(paramDataSetObserver);
      }
    }
  }
  
  private class DropdownPopup
    extends ListPopupWindow
  {
    ListAdapter mAdapter;
    private CharSequence mHintText;
    private final Rect mVisibleRect = new Rect();
    
    public DropdownPopup(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      super(paramAttributeSet, paramInt);
      setAnchorView(AppCompatSpinner.this);
      setModal(true);
      setPromptPosition(0);
      setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          setSelection(paramAnonymousInt);
          if (getOnItemClickListener() != null) {
            performItemClick(paramAnonymousView, paramAnonymousInt, mAdapter.getItemId(paramAnonymousInt));
          }
          dismiss();
        }
      });
    }
    
    void computeContentWidth()
    {
      Drawable localDrawable = getBackground();
      int i;
      int j;
      int k;
      int m;
      if (localDrawable != null)
      {
        localDrawable.getPadding(mTempRect);
        if (ViewUtils.isLayoutRtl(AppCompatSpinner.this))
        {
          i = mTempRect.right;
          j = getPaddingLeft();
          k = getPaddingRight();
          m = getWidth();
          if (mDropDownWidth != -2) {
            break label244;
          }
          int i1 = compatMeasureContentWidth((SpinnerAdapter)mAdapter, getBackground());
          int i2 = getContext().getResources().getDisplayMetrics().widthPixels - mTempRect.left - mTempRect.right;
          if (i1 > i2) {
            i1 = i2;
          }
          setContentWidth(Math.max(i1, m - j - k));
          label169:
          if (!ViewUtils.isLayoutRtl(AppCompatSpinner.this)) {
            break label284;
          }
        }
      }
      label244:
      label284:
      for (int n = i + (m - k - getWidth());; n = i + j)
      {
        setHorizontalOffset(n);
        return;
        i = -mTempRect.left;
        break;
        Rect localRect = mTempRect;
        mTempRect.right = 0;
        left = 0;
        i = 0;
        break;
        if (mDropDownWidth == -1)
        {
          setContentWidth(m - j - k);
          break label169;
        }
        setContentWidth(mDropDownWidth);
        break label169;
      }
    }
    
    public CharSequence getHintText()
    {
      return mHintText;
    }
    
    boolean isVisibleToUser(View paramView)
    {
      return (ViewCompat.isAttachedToWindow(paramView)) && (paramView.getGlobalVisibleRect(mVisibleRect));
    }
    
    public void setAdapter(ListAdapter paramListAdapter)
    {
      super.setAdapter(paramListAdapter);
      mAdapter = paramListAdapter;
    }
    
    public void setPromptText(CharSequence paramCharSequence)
    {
      mHintText = paramCharSequence;
    }
    
    public void show()
    {
      boolean bool = isShowing();
      computeContentWidth();
      setInputMethodMode(2);
      super.show();
      getListView().setChoiceMode(1);
      setSelection(getSelectedItemPosition());
      if (bool) {}
      ViewTreeObserver localViewTreeObserver;
      do
      {
        return;
        localViewTreeObserver = getViewTreeObserver();
      } while (localViewTreeObserver == null);
      final ViewTreeObserver.OnGlobalLayoutListener local2 = new ViewTreeObserver.OnGlobalLayoutListener()
      {
        public void onGlobalLayout()
        {
          if (!isVisibleToUser(AppCompatSpinner.this))
          {
            dismiss();
            return;
          }
          computeContentWidth();
          AppCompatSpinner.DropdownPopup.this.show();
        }
      };
      localViewTreeObserver.addOnGlobalLayoutListener(local2);
      setOnDismissListener(new PopupWindow.OnDismissListener()
      {
        public void onDismiss()
        {
          ViewTreeObserver localViewTreeObserver = getViewTreeObserver();
          if (localViewTreeObserver != null) {
            localViewTreeObserver.removeGlobalOnLayoutListener(local2);
          }
        }
      });
    }
  }
}
