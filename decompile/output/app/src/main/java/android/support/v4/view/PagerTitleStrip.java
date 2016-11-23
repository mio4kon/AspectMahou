package android.support.v4.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import java.lang.ref.WeakReference;

@ViewPager.DecorView
public class PagerTitleStrip
  extends ViewGroup
{
  private static final int[] ATTRS = { 16842804, 16842901, 16842904, 16842927 };
  private static final PagerTitleStripImpl IMPL = new PagerTitleStripImplBase();
  private static final float SIDE_ALPHA = 0.6F;
  private static final String TAG = "PagerTitleStrip";
  private static final int[] TEXT_ATTRS = { 16843660 };
  private static final int TEXT_SPACING = 16;
  TextView mCurrText;
  private int mGravity;
  private int mLastKnownCurrentPage = -1;
  float mLastKnownPositionOffset = -1.0F;
  TextView mNextText;
  private int mNonPrimaryAlpha;
  private final PageListener mPageListener = new PageListener();
  ViewPager mPager;
  TextView mPrevText;
  private int mScaledTextSpacing;
  int mTextColor;
  private boolean mUpdatingPositions;
  private boolean mUpdatingText;
  private WeakReference<PagerAdapter> mWatchingAdapter;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      IMPL = new PagerTitleStripImplIcs();
      return;
    }
  }
  
  public PagerTitleStrip(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public PagerTitleStrip(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TextView localTextView1 = new TextView(paramContext);
    mPrevText = localTextView1;
    addView(localTextView1);
    TextView localTextView2 = new TextView(paramContext);
    mCurrText = localTextView2;
    addView(localTextView2);
    TextView localTextView3 = new TextView(paramContext);
    mNextText = localTextView3;
    addView(localTextView3);
    TypedArray localTypedArray1 = paramContext.obtainStyledAttributes(paramAttributeSet, ATTRS);
    int i = localTypedArray1.getResourceId(0, 0);
    if (i != 0)
    {
      mPrevText.setTextAppearance(paramContext, i);
      mCurrText.setTextAppearance(paramContext, i);
      mNextText.setTextAppearance(paramContext, i);
    }
    int j = localTypedArray1.getDimensionPixelSize(1, 0);
    if (j != 0) {
      setTextSize(0, j);
    }
    if (localTypedArray1.hasValue(2))
    {
      int k = localTypedArray1.getColor(2, 0);
      mPrevText.setTextColor(k);
      mCurrText.setTextColor(k);
      mNextText.setTextColor(k);
    }
    mGravity = localTypedArray1.getInteger(3, 80);
    localTypedArray1.recycle();
    mTextColor = mCurrText.getTextColors().getDefaultColor();
    setNonPrimaryAlpha(0.6F);
    mPrevText.setEllipsize(TextUtils.TruncateAt.END);
    mCurrText.setEllipsize(TextUtils.TruncateAt.END);
    mNextText.setEllipsize(TextUtils.TruncateAt.END);
    boolean bool = false;
    if (i != 0)
    {
      TypedArray localTypedArray2 = paramContext.obtainStyledAttributes(i, TEXT_ATTRS);
      bool = localTypedArray2.getBoolean(0, false);
      localTypedArray2.recycle();
    }
    if (bool)
    {
      setSingleLineAllCaps(mPrevText);
      setSingleLineAllCaps(mCurrText);
      setSingleLineAllCaps(mNextText);
    }
    for (;;)
    {
      mScaledTextSpacing = ((int)(16.0F * getResourcesgetDisplayMetricsdensity));
      return;
      mPrevText.setSingleLine();
      mCurrText.setSingleLine();
      mNextText.setSingleLine();
    }
  }
  
  private static void setSingleLineAllCaps(TextView paramTextView)
  {
    IMPL.setSingleLineAllCaps(paramTextView);
  }
  
  int getMinHeight()
  {
    Drawable localDrawable = getBackground();
    int i = 0;
    if (localDrawable != null) {
      i = localDrawable.getIntrinsicHeight();
    }
    return i;
  }
  
  public int getTextSpacing()
  {
    return mScaledTextSpacing;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    ViewParent localViewParent = getParent();
    if (!(localViewParent instanceof ViewPager)) {
      throw new IllegalStateException("PagerTitleStrip must be a direct child of a ViewPager.");
    }
    ViewPager localViewPager = (ViewPager)localViewParent;
    PagerAdapter localPagerAdapter1 = localViewPager.getAdapter();
    localViewPager.setInternalPageChangeListener(mPageListener);
    localViewPager.addOnAdapterChangeListener(mPageListener);
    mPager = localViewPager;
    if (mWatchingAdapter != null) {}
    for (PagerAdapter localPagerAdapter2 = (PagerAdapter)mWatchingAdapter.get();; localPagerAdapter2 = null)
    {
      updateAdapter(localPagerAdapter2, localPagerAdapter1);
      return;
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (mPager != null)
    {
      updateAdapter(mPager.getAdapter(), null);
      mPager.setInternalPageChangeListener(null);
      mPager.removeOnAdapterChangeListener(mPageListener);
      mPager = null;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (mPager != null)
    {
      boolean bool = mLastKnownPositionOffset < 0.0F;
      float f = 0.0F;
      if (!bool) {
        f = mLastKnownPositionOffset;
      }
      updateTextPositions(mLastKnownCurrentPage, f, true);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (View.MeasureSpec.getMode(paramInt1) != 1073741824) {
      throw new IllegalStateException("Must measure with an exact width");
    }
    int i = getPaddingTop() + getPaddingBottom();
    int j = getChildMeasureSpec(paramInt2, i, -2);
    int k = View.MeasureSpec.getSize(paramInt1);
    int m = getChildMeasureSpec(paramInt1, (int)(0.2F * k), -2);
    mPrevText.measure(m, j);
    mCurrText.measure(m, j);
    mNextText.measure(m, j);
    if (View.MeasureSpec.getMode(paramInt2) == 1073741824) {}
    int n;
    for (int i1 = View.MeasureSpec.getSize(paramInt2);; i1 = Math.max(getMinHeight(), n + i))
    {
      setMeasuredDimension(k, ViewCompat.resolveSizeAndState(i1, paramInt2, ViewCompat.getMeasuredState(mCurrText) << 16));
      return;
      n = mCurrText.getMeasuredHeight();
    }
  }
  
  public void requestLayout()
  {
    if (!mUpdatingText) {
      super.requestLayout();
    }
  }
  
  public void setGravity(int paramInt)
  {
    mGravity = paramInt;
    requestLayout();
  }
  
  public void setNonPrimaryAlpha(@FloatRange(from=0.0D, to=1.0D) float paramFloat)
  {
    mNonPrimaryAlpha = (0xFF & (int)(255.0F * paramFloat));
    int i = mNonPrimaryAlpha << 24 | 0xFFFFFF & mTextColor;
    mPrevText.setTextColor(i);
    mNextText.setTextColor(i);
  }
  
  public void setTextColor(@ColorInt int paramInt)
  {
    mTextColor = paramInt;
    mCurrText.setTextColor(paramInt);
    int i = mNonPrimaryAlpha << 24 | 0xFFFFFF & mTextColor;
    mPrevText.setTextColor(i);
    mNextText.setTextColor(i);
  }
  
  public void setTextSize(int paramInt, float paramFloat)
  {
    mPrevText.setTextSize(paramInt, paramFloat);
    mCurrText.setTextSize(paramInt, paramFloat);
    mNextText.setTextSize(paramInt, paramFloat);
  }
  
  public void setTextSpacing(int paramInt)
  {
    mScaledTextSpacing = paramInt;
    requestLayout();
  }
  
  void updateAdapter(PagerAdapter paramPagerAdapter1, PagerAdapter paramPagerAdapter2)
  {
    if (paramPagerAdapter1 != null)
    {
      paramPagerAdapter1.unregisterDataSetObserver(mPageListener);
      mWatchingAdapter = null;
    }
    if (paramPagerAdapter2 != null)
    {
      paramPagerAdapter2.registerDataSetObserver(mPageListener);
      mWatchingAdapter = new WeakReference(paramPagerAdapter2);
    }
    if (mPager != null)
    {
      mLastKnownCurrentPage = -1;
      mLastKnownPositionOffset = -1.0F;
      updateText(mPager.getCurrentItem(), paramPagerAdapter2);
      requestLayout();
    }
  }
  
  void updateText(int paramInt, PagerAdapter paramPagerAdapter)
  {
    int i;
    TextView localTextView;
    if (paramPagerAdapter != null)
    {
      i = paramPagerAdapter.getCount();
      mUpdatingText = true;
      CharSequence localCharSequence1 = null;
      if (paramInt >= 1)
      {
        localCharSequence1 = null;
        if (paramPagerAdapter != null) {
          localCharSequence1 = paramPagerAdapter.getPageTitle(paramInt - 1);
        }
      }
      mPrevText.setText(localCharSequence1);
      localTextView = mCurrText;
      if ((paramPagerAdapter == null) || (paramInt >= i)) {
        break label239;
      }
    }
    label239:
    for (CharSequence localCharSequence2 = paramPagerAdapter.getPageTitle(paramInt);; localCharSequence2 = null)
    {
      localTextView.setText(localCharSequence2);
      int j = paramInt + 1;
      CharSequence localCharSequence3 = null;
      if (j < i)
      {
        localCharSequence3 = null;
        if (paramPagerAdapter != null) {
          localCharSequence3 = paramPagerAdapter.getPageTitle(paramInt + 1);
        }
      }
      mNextText.setText(localCharSequence3);
      int k = View.MeasureSpec.makeMeasureSpec(Math.max(0, (int)(0.8F * (getWidth() - getPaddingLeft() - getPaddingRight()))), Integer.MIN_VALUE);
      int m = View.MeasureSpec.makeMeasureSpec(Math.max(0, getHeight() - getPaddingTop() - getPaddingBottom()), Integer.MIN_VALUE);
      mPrevText.measure(k, m);
      mCurrText.measure(k, m);
      mNextText.measure(k, m);
      mLastKnownCurrentPage = paramInt;
      if (!mUpdatingPositions) {
        updateTextPositions(paramInt, mLastKnownPositionOffset, false);
      }
      mUpdatingText = false;
      return;
      i = 0;
      break;
    }
  }
  
  void updateTextPositions(int paramInt, float paramFloat, boolean paramBoolean)
  {
    int i;
    int k;
    int n;
    int i1;
    int i2;
    int i3;
    int i4;
    int i5;
    int i9;
    int i10;
    int i15;
    int i16;
    int i17;
    int i21;
    int i23;
    int i24;
    int i25;
    if (paramInt != mLastKnownCurrentPage)
    {
      updateText(paramInt, mPager.getAdapter());
      mUpdatingPositions = true;
      i = mPrevText.getMeasuredWidth();
      int j = mCurrText.getMeasuredWidth();
      k = mNextText.getMeasuredWidth();
      int m = j / 2;
      n = getWidth();
      i1 = getHeight();
      i2 = getPaddingLeft();
      i3 = getPaddingRight();
      i4 = getPaddingTop();
      i5 = getPaddingBottom();
      int i6 = i2 + m;
      int i7 = i3 + m;
      int i8 = n - i6 - i7;
      float f = paramFloat + 0.5F;
      if (f > 1.0F) {
        f -= 1.0F;
      }
      i9 = n - i7 - (int)(f * i8) - j / 2;
      i10 = i9 + j;
      int i11 = mPrevText.getBaseline();
      int i12 = mCurrText.getBaseline();
      int i13 = mNextText.getBaseline();
      int i14 = Math.max(Math.max(i11, i12), i13);
      i15 = i14 - i11;
      i16 = i14 - i12;
      i17 = i14 - i13;
      int i18 = i15 + mPrevText.getMeasuredHeight();
      int i19 = i16 + mCurrText.getMeasuredHeight();
      int i20 = i17 + mNextText.getMeasuredHeight();
      i21 = Math.max(Math.max(i18, i19), i20);
      switch (0x70 & mGravity)
      {
      default: 
        i23 = i4 + i15;
        i24 = i4 + i16;
        i25 = i4 + i17;
      }
    }
    for (;;)
    {
      TextView localTextView1 = mCurrText;
      int i26 = i24 + mCurrText.getMeasuredHeight();
      localTextView1.layout(i9, i24, i10, i26);
      int i27 = Math.min(i2, i9 - mScaledTextSpacing - i);
      TextView localTextView2 = mPrevText;
      int i28 = i27 + i;
      int i29 = i23 + mPrevText.getMeasuredHeight();
      localTextView2.layout(i27, i23, i28, i29);
      int i30 = Math.max(n - i3 - k, i10 + mScaledTextSpacing);
      TextView localTextView3 = mNextText;
      int i31 = i30 + k;
      int i32 = i25 + mNextText.getMeasuredHeight();
      localTextView3.layout(i30, i25, i31, i32);
      mLastKnownPositionOffset = paramFloat;
      mUpdatingPositions = false;
      return;
      if ((paramBoolean) || (paramFloat != mLastKnownPositionOffset)) {
        break;
      }
      return;
      int i33 = (i1 - i4 - i5 - i21) / 2;
      i23 = i33 + i15;
      i24 = i33 + i16;
      i25 = i33 + i17;
      continue;
      int i22 = i1 - i5 - i21;
      i23 = i22 + i15;
      i24 = i22 + i16;
      i25 = i22 + i17;
    }
  }
  
  private class PageListener
    extends DataSetObserver
    implements ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener
  {
    private int mScrollState;
    
    PageListener() {}
    
    public void onAdapterChanged(ViewPager paramViewPager, PagerAdapter paramPagerAdapter1, PagerAdapter paramPagerAdapter2)
    {
      updateAdapter(paramPagerAdapter1, paramPagerAdapter2);
    }
    
    public void onChanged()
    {
      updateText(mPager.getCurrentItem(), mPager.getAdapter());
      boolean bool = mLastKnownPositionOffset < 0.0F;
      float f = 0.0F;
      if (!bool) {
        f = mLastKnownPositionOffset;
      }
      updateTextPositions(mPager.getCurrentItem(), f, true);
    }
    
    public void onPageScrollStateChanged(int paramInt)
    {
      mScrollState = paramInt;
    }
    
    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
    {
      if (paramFloat > 0.5F) {
        paramInt1++;
      }
      updateTextPositions(paramInt1, paramFloat, false);
    }
    
    public void onPageSelected(int paramInt)
    {
      if (mScrollState == 0)
      {
        updateText(mPager.getCurrentItem(), mPager.getAdapter());
        boolean bool = mLastKnownPositionOffset < 0.0F;
        float f = 0.0F;
        if (!bool) {
          f = mLastKnownPositionOffset;
        }
        updateTextPositions(mPager.getCurrentItem(), f, true);
      }
    }
  }
  
  static abstract interface PagerTitleStripImpl
  {
    public abstract void setSingleLineAllCaps(TextView paramTextView);
  }
  
  static class PagerTitleStripImplBase
    implements PagerTitleStrip.PagerTitleStripImpl
  {
    PagerTitleStripImplBase() {}
    
    public void setSingleLineAllCaps(TextView paramTextView)
    {
      paramTextView.setSingleLine();
    }
  }
  
  static class PagerTitleStripImplIcs
    implements PagerTitleStrip.PagerTitleStripImpl
  {
    PagerTitleStripImplIcs() {}
    
    public void setSingleLineAllCaps(TextView paramTextView)
    {
      PagerTitleStripIcs.setSingleLineAllCaps(paramTextView);
    }
  }
}
