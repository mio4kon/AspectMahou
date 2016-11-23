package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.styleable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class LinearLayoutCompat
  extends ViewGroup
{
  public static final int HORIZONTAL = 0;
  private static final int INDEX_BOTTOM = 2;
  private static final int INDEX_CENTER_VERTICAL = 0;
  private static final int INDEX_FILL = 3;
  private static final int INDEX_TOP = 1;
  public static final int SHOW_DIVIDER_BEGINNING = 1;
  public static final int SHOW_DIVIDER_END = 4;
  public static final int SHOW_DIVIDER_MIDDLE = 2;
  public static final int SHOW_DIVIDER_NONE = 0;
  public static final int VERTICAL = 1;
  private static final int VERTICAL_GRAVITY_COUNT = 4;
  private boolean mBaselineAligned = true;
  private int mBaselineAlignedChildIndex = -1;
  private int mBaselineChildTop = 0;
  private Drawable mDivider;
  private int mDividerHeight;
  private int mDividerPadding;
  private int mDividerWidth;
  private int mGravity = 8388659;
  private int[] mMaxAscent;
  private int[] mMaxDescent;
  private int mOrientation;
  private int mShowDividers;
  private int mTotalLength;
  private boolean mUseLargestChild;
  private float mWeightSum;
  
  public LinearLayoutCompat(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public LinearLayoutCompat(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public LinearLayoutCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.LinearLayoutCompat, paramInt, 0);
    int i = localTintTypedArray.getInt(R.styleable.LinearLayoutCompat_android_orientation, -1);
    if (i >= 0) {
      setOrientation(i);
    }
    int j = localTintTypedArray.getInt(R.styleable.LinearLayoutCompat_android_gravity, -1);
    if (j >= 0) {
      setGravity(j);
    }
    boolean bool = localTintTypedArray.getBoolean(R.styleable.LinearLayoutCompat_android_baselineAligned, true);
    if (!bool) {
      setBaselineAligned(bool);
    }
    mWeightSum = localTintTypedArray.getFloat(R.styleable.LinearLayoutCompat_android_weightSum, -1.0F);
    mBaselineAlignedChildIndex = localTintTypedArray.getInt(R.styleable.LinearLayoutCompat_android_baselineAlignedChildIndex, -1);
    mUseLargestChild = localTintTypedArray.getBoolean(R.styleable.LinearLayoutCompat_measureWithLargestChild, false);
    setDividerDrawable(localTintTypedArray.getDrawable(R.styleable.LinearLayoutCompat_divider));
    mShowDividers = localTintTypedArray.getInt(R.styleable.LinearLayoutCompat_showDividers, 0);
    mDividerPadding = localTintTypedArray.getDimensionPixelSize(R.styleable.LinearLayoutCompat_dividerPadding, 0);
    localTintTypedArray.recycle();
  }
  
  private void forceUniformHeight(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
    for (int j = 0; j < paramInt1; j++)
    {
      View localView = getVirtualChildAt(j);
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (height == -1)
        {
          int k = width;
          width = localView.getMeasuredWidth();
          measureChildWithMargins(localView, paramInt2, 0, i, 0);
          width = k;
        }
      }
    }
  }
  
  private void forceUniformWidth(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
    for (int j = 0; j < paramInt1; j++)
    {
      View localView = getVirtualChildAt(j);
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (width == -1)
        {
          int k = height;
          height = localView.getMeasuredHeight();
          measureChildWithMargins(localView, i, 0, paramInt2, 0);
          height = k;
        }
      }
    }
  }
  
  private void setChildFrame(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramView.layout(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  void drawDividersHorizontal(Canvas paramCanvas)
  {
    int i = getVirtualChildCount();
    boolean bool = ViewUtils.isLayoutRtl(this);
    int j = 0;
    if (j < i)
    {
      View localView2 = getVirtualChildAt(j);
      LayoutParams localLayoutParams2;
      if ((localView2 != null) && (localView2.getVisibility() != 8) && (hasDividerBeforeChildAt(j)))
      {
        localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
        if (!bool) {
          break label91;
        }
      }
      label91:
      for (int m = localView2.getRight() + rightMargin;; m = localView2.getLeft() - leftMargin - mDividerWidth)
      {
        drawVerticalDivider(paramCanvas, m);
        j++;
        break;
      }
    }
    View localView1;
    int k;
    if (hasDividerBeforeChildAt(i))
    {
      localView1 = getVirtualChildAt(i - 1);
      if (localView1 != null) {
        break label171;
      }
      if (!bool) {
        break label152;
      }
      k = getPaddingLeft();
    }
    for (;;)
    {
      drawVerticalDivider(paramCanvas, k);
      return;
      label152:
      k = getWidth() - getPaddingRight() - mDividerWidth;
      continue;
      label171:
      LayoutParams localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
      if (bool) {
        k = localView1.getLeft() - leftMargin - mDividerWidth;
      } else {
        k = localView1.getRight() + rightMargin;
      }
    }
  }
  
  void drawDividersVertical(Canvas paramCanvas)
  {
    int i = getVirtualChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView2 = getVirtualChildAt(j);
      if ((localView2 != null) && (localView2.getVisibility() != 8) && (hasDividerBeforeChildAt(j)))
      {
        LayoutParams localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
        drawHorizontalDivider(paramCanvas, localView2.getTop() - topMargin - mDividerHeight);
      }
    }
    View localView1;
    if (hasDividerBeforeChildAt(i))
    {
      localView1 = getVirtualChildAt(i - 1);
      if (localView1 != null) {
        break label125;
      }
    }
    label125:
    LayoutParams localLayoutParams1;
    for (int k = getHeight() - getPaddingBottom() - mDividerHeight;; k = localView1.getBottom() + bottomMargin)
    {
      drawHorizontalDivider(paramCanvas, k);
      return;
      localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
    }
  }
  
  void drawHorizontalDivider(Canvas paramCanvas, int paramInt)
  {
    mDivider.setBounds(getPaddingLeft() + mDividerPadding, paramInt, getWidth() - getPaddingRight() - mDividerPadding, paramInt + mDividerHeight);
    mDivider.draw(paramCanvas);
  }
  
  void drawVerticalDivider(Canvas paramCanvas, int paramInt)
  {
    mDivider.setBounds(paramInt, getPaddingTop() + mDividerPadding, paramInt + mDividerWidth, getHeight() - getPaddingBottom() - mDividerPadding);
    mDivider.draw(paramCanvas);
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    if (mOrientation == 0) {
      return new LayoutParams(-2, -2);
    }
    if (mOrientation == 1) {
      return new LayoutParams(-1, -2);
    }
    return null;
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return new LayoutParams(paramLayoutParams);
  }
  
  public int getBaseline()
  {
    int i = -1;
    if (mBaselineAlignedChildIndex < 0) {
      i = super.getBaseline();
    }
    View localView;
    int j;
    do
    {
      return i;
      if (getChildCount() <= mBaselineAlignedChildIndex) {
        throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
      }
      localView = getChildAt(mBaselineAlignedChildIndex);
      j = localView.getBaseline();
      if (j != i) {
        break;
      }
    } while (mBaselineAlignedChildIndex == 0);
    throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
    int k = mBaselineChildTop;
    if (mOrientation == 1)
    {
      int m = 0x70 & mGravity;
      if (m != 48) {
        switch (m)
        {
        }
      }
    }
    for (;;)
    {
      return j + (k + getLayoutParamstopMargin);
      k = getBottom() - getTop() - getPaddingBottom() - mTotalLength;
      continue;
      k += (getBottom() - getTop() - getPaddingTop() - getPaddingBottom() - mTotalLength) / 2;
    }
  }
  
  public int getBaselineAlignedChildIndex()
  {
    return mBaselineAlignedChildIndex;
  }
  
  int getChildrenSkipCount(View paramView, int paramInt)
  {
    return 0;
  }
  
  public Drawable getDividerDrawable()
  {
    return mDivider;
  }
  
  public int getDividerPadding()
  {
    return mDividerPadding;
  }
  
  public int getDividerWidth()
  {
    return mDividerWidth;
  }
  
  int getLocationOffset(View paramView)
  {
    return 0;
  }
  
  int getNextLocationOffset(View paramView)
  {
    return 0;
  }
  
  public int getOrientation()
  {
    return mOrientation;
  }
  
  public int getShowDividers()
  {
    return mShowDividers;
  }
  
  View getVirtualChildAt(int paramInt)
  {
    return getChildAt(paramInt);
  }
  
  int getVirtualChildCount()
  {
    return getChildCount();
  }
  
  public float getWeightSum()
  {
    return mWeightSum;
  }
  
  protected boolean hasDividerBeforeChildAt(int paramInt)
  {
    if (paramInt == 0) {
      if ((0x1 & mShowDividers) == 0) {}
    }
    do
    {
      return true;
      return false;
      if (paramInt != getChildCount()) {
        break;
      }
    } while ((0x4 & mShowDividers) != 0);
    return false;
    if ((0x2 & mShowDividers) != 0) {
      for (int i = paramInt - 1;; i--)
      {
        boolean bool = false;
        if (i >= 0)
        {
          if (getChildAt(i).getVisibility() != 8) {
            bool = true;
          }
        }
        else {
          return bool;
        }
      }
    }
    return false;
  }
  
  public boolean isBaselineAligned()
  {
    return mBaselineAligned;
  }
  
  public boolean isMeasureWithLargestChildEnabled()
  {
    return mUseLargestChild;
  }
  
  void layoutHorizontal(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool1 = ViewUtils.isLayoutRtl(this);
    int i = getPaddingTop();
    int j = paramInt4 - paramInt2;
    int k = j - getPaddingBottom();
    int m = j - i - getPaddingBottom();
    int n = getVirtualChildCount();
    int i1 = 0x800007 & mGravity;
    int i2 = 0x70 & mGravity;
    boolean bool2 = mBaselineAligned;
    int[] arrayOfInt1 = mMaxAscent;
    int[] arrayOfInt2 = mMaxDescent;
    int i3;
    int i6;
    label145:
    int i7;
    View localView;
    switch (GravityCompat.getAbsoluteGravity(i1, ViewCompat.getLayoutDirection(this)))
    {
    default: 
      i3 = getPaddingLeft();
      int i4 = 1;
      int i5 = 0;
      if (bool1)
      {
        i5 = n - 1;
        i4 = -1;
      }
      i6 = 0;
      if (i6 >= n) {
        return;
      }
      i7 = i5 + i4 * i6;
      localView = getVirtualChildAt(i7);
      if (localView == null) {
        i3 += measureNullChild(i7);
      }
      break;
    }
    while (localView.getVisibility() == 8)
    {
      i6++;
      break label145;
      i3 = paramInt3 + getPaddingLeft() - paramInt1 - mTotalLength;
      break;
      i3 = getPaddingLeft() + (paramInt3 - paramInt1 - mTotalLength) / 2;
      break;
    }
    int i8 = localView.getMeasuredWidth();
    int i9 = localView.getMeasuredHeight();
    int i10 = -1;
    LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
    if ((bool2) && (height != -1)) {
      i10 = localView.getBaseline();
    }
    int i11 = gravity;
    if (i11 < 0) {
      i11 = i2;
    }
    int i12;
    switch (i11 & 0x70)
    {
    default: 
      i12 = i;
    }
    for (;;)
    {
      if (hasDividerBeforeChildAt(i7)) {
        i3 += mDividerWidth;
      }
      int i14 = i3 + leftMargin;
      setChildFrame(localView, i14 + getLocationOffset(localView), i12, i8, i9);
      i3 = i14 + (i8 + rightMargin + getNextLocationOffset(localView));
      i6 += getChildrenSkipCount(localView, i7);
      break;
      i12 = i + topMargin;
      if (i10 != -1)
      {
        i12 += arrayOfInt1[1] - i10;
        continue;
        i12 = i + (m - i9) / 2 + topMargin - bottomMargin;
        continue;
        i12 = k - i9 - bottomMargin;
        if (i10 != -1)
        {
          int i13 = localView.getMeasuredHeight() - i10;
          i12 -= arrayOfInt2[2] - i13;
        }
      }
    }
  }
  
  void layoutVertical(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = getPaddingLeft();
    int j = paramInt3 - paramInt1;
    int k = j - getPaddingRight();
    int m = j - i - getPaddingRight();
    int n = getVirtualChildCount();
    int i1 = 0x70 & mGravity;
    int i2 = 0x800007 & mGravity;
    int i3;
    int i4;
    label93:
    View localView;
    switch (i1)
    {
    default: 
      i3 = getPaddingTop();
      i4 = 0;
      if (i4 >= n) {
        return;
      }
      localView = getVirtualChildAt(i4);
      if (localView == null) {
        i3 += measureNullChild(i4);
      }
      break;
    }
    while (localView.getVisibility() == 8)
    {
      i4++;
      break label93;
      i3 = paramInt4 + getPaddingTop() - paramInt2 - mTotalLength;
      break;
      i3 = getPaddingTop() + (paramInt4 - paramInt2 - mTotalLength) / 2;
      break;
    }
    int i5 = localView.getMeasuredWidth();
    int i6 = localView.getMeasuredHeight();
    LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
    int i7 = gravity;
    if (i7 < 0) {
      i7 = i2;
    }
    int i8;
    switch (0x7 & GravityCompat.getAbsoluteGravity(i7, ViewCompat.getLayoutDirection(this)))
    {
    default: 
      i8 = i + leftMargin;
    }
    for (;;)
    {
      if (hasDividerBeforeChildAt(i4)) {
        i3 += mDividerHeight;
      }
      int i9 = i3 + topMargin;
      setChildFrame(localView, i8, i9 + getLocationOffset(localView), i5, i6);
      i3 = i9 + (i6 + bottomMargin + getNextLocationOffset(localView));
      i4 += getChildrenSkipCount(localView, i4);
      break;
      i8 = i + (m - i5) / 2 + leftMargin - rightMargin;
      continue;
      i8 = k - i5 - rightMargin;
    }
  }
  
  void measureChildBeforeLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    measureChildWithMargins(paramView, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  void measureHorizontal(int paramInt1, int paramInt2)
  {
    mTotalLength = 0;
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 1;
    float f1 = 0.0F;
    int i1 = getVirtualChildCount();
    int i2 = View.MeasureSpec.getMode(paramInt1);
    int i3 = View.MeasureSpec.getMode(paramInt2);
    int i4 = 0;
    int i5 = 0;
    if ((mMaxAscent == null) || (mMaxDescent == null))
    {
      mMaxAscent = new int[4];
      mMaxDescent = new int[4];
    }
    int[] arrayOfInt1 = mMaxAscent;
    int[] arrayOfInt2 = mMaxDescent;
    arrayOfInt1[3] = -1;
    arrayOfInt1[2] = -1;
    arrayOfInt1[1] = -1;
    arrayOfInt1[0] = -1;
    arrayOfInt2[3] = -1;
    arrayOfInt2[2] = -1;
    arrayOfInt2[1] = -1;
    arrayOfInt2[0] = -1;
    boolean bool1 = mBaselineAligned;
    boolean bool2 = mUseLargestChild;
    int i6;
    int i7;
    int i8;
    label156:
    View localView4;
    if (i2 == 1073741824)
    {
      i6 = 1;
      i7 = Integer.MIN_VALUE;
      i8 = 0;
      if (i8 >= i1) {
        break label822;
      }
      localView4 = getVirtualChildAt(i8);
      if (localView4 != null) {
        break label203;
      }
      mTotalLength += measureNullChild(i8);
    }
    for (;;)
    {
      i8++;
      break label156;
      i6 = 0;
      break;
      label203:
      if (localView4.getVisibility() != 8) {
        break label229;
      }
      i8 += getChildrenSkipCount(localView4, i8);
    }
    label229:
    if (hasDividerBeforeChildAt(i8)) {
      mTotalLength += mDividerWidth;
    }
    LayoutParams localLayoutParams3 = (LayoutParams)localView4.getLayoutParams();
    f1 += weight;
    label321:
    label342:
    int i33;
    int i34;
    int i35;
    int i38;
    if ((i2 == 1073741824) && (width == 0) && (weight > 0.0F)) {
      if (i6 != 0)
      {
        mTotalLength += leftMargin + rightMargin;
        if (!bool1) {
          break label588;
        }
        int i42 = View.MeasureSpec.makeMeasureSpec(0, 0);
        localView4.measure(i42, i42);
        i33 = 0;
        if (i3 != 1073741824)
        {
          int i40 = height;
          i33 = 0;
          if (i40 == -1)
          {
            i4 = 1;
            i33 = 1;
          }
        }
        i34 = topMargin + bottomMargin;
        i35 = i34 + localView4.getMeasuredHeight();
        int i36 = ViewCompat.getMeasuredState(localView4);
        j = ViewUtils.combineMeasuredStates(j, i36);
        if (bool1)
        {
          int i37 = localView4.getBaseline();
          if (i37 != -1)
          {
            if (gravity >= 0) {
              break label775;
            }
            i38 = mGravity;
            label445:
            int i39 = (0xFFFFFFFE & (i38 & 0x70) >> 4) >> 1;
            arrayOfInt1[i39] = Math.max(arrayOfInt1[i39], i37);
            arrayOfInt2[i39] = Math.max(arrayOfInt2[i39], i35 - i37);
          }
        }
        i = Math.max(i, i35);
        if ((n == 0) || (height != -1)) {
          break label785;
        }
        n = 1;
        label516:
        if (weight <= 0.0F) {
          break label798;
        }
        if (i33 == 0) {
          break label791;
        }
      }
    }
    for (;;)
    {
      m = Math.max(m, i34);
      i8 += getChildrenSkipCount(localView4, i8);
      break;
      int i41 = mTotalLength;
      mTotalLength = Math.max(i41, i41 + leftMargin + rightMargin);
      break label321;
      label588:
      i5 = 1;
      break label342;
      int i29 = Integer.MIN_VALUE;
      if ((width == 0) && (weight > 0.0F))
      {
        i29 = 0;
        width = -2;
      }
      int i30;
      label640:
      int i31;
      if (f1 == 0.0F)
      {
        i30 = mTotalLength;
        measureChildBeforeLayout(localView4, i8, paramInt1, i30, paramInt2, 0);
        if (i29 != Integer.MIN_VALUE) {
          width = i29;
        }
        i31 = localView4.getMeasuredWidth();
        if (i6 == 0) {
          break label733;
        }
      }
      label733:
      int i32;
      for (mTotalLength += i31 + leftMargin + rightMargin + getNextLocationOffset(localView4);; mTotalLength = Math.max(i32, i32 + i31 + leftMargin + rightMargin + getNextLocationOffset(localView4)))
      {
        if (!bool2) {
          break label773;
        }
        i7 = Math.max(i31, i7);
        break;
        i30 = 0;
        break label640;
        i32 = mTotalLength;
      }
      label773:
      break label342;
      label775:
      i38 = gravity;
      break label445;
      label785:
      n = 0;
      break label516;
      label791:
      i34 = i35;
    }
    label798:
    if (i33 != 0) {}
    for (;;)
    {
      k = Math.max(k, i34);
      break;
      i34 = i35;
    }
    label822:
    if ((mTotalLength > 0) && (hasDividerBeforeChildAt(i1))) {
      mTotalLength += mDividerWidth;
    }
    if ((arrayOfInt1[1] != -1) || (arrayOfInt1[0] != -1) || (arrayOfInt1[2] != -1) || (arrayOfInt1[3] != -1))
    {
      int i9 = Math.max(arrayOfInt1[3], Math.max(arrayOfInt1[0], Math.max(arrayOfInt1[1], arrayOfInt1[2]))) + Math.max(arrayOfInt2[3], Math.max(arrayOfInt2[0], Math.max(arrayOfInt2[1], arrayOfInt2[2])));
      i = Math.max(i, i9);
    }
    if ((bool2) && ((i2 == Integer.MIN_VALUE) || (i2 == 0)))
    {
      mTotalLength = 0;
      int i27 = 0;
      if (i27 < i1)
      {
        View localView3 = getVirtualChildAt(i27);
        if (localView3 == null) {
          mTotalLength += measureNullChild(i27);
        }
        for (;;)
        {
          i27++;
          break;
          if (localView3.getVisibility() == 8)
          {
            i27 += getChildrenSkipCount(localView3, i27);
          }
          else
          {
            LayoutParams localLayoutParams2 = (LayoutParams)localView3.getLayoutParams();
            if (i6 != 0)
            {
              mTotalLength += i7 + leftMargin + rightMargin + getNextLocationOffset(localView3);
            }
            else
            {
              int i28 = mTotalLength;
              mTotalLength = Math.max(i28, i28 + i7 + leftMargin + rightMargin + getNextLocationOffset(localView3));
            }
          }
        }
      }
    }
    mTotalLength += getPaddingLeft() + getPaddingRight();
    int i10 = ViewCompat.resolveSizeAndState(Math.max(mTotalLength, getSuggestedMinimumWidth()), paramInt1, 0);
    int i11 = (i10 & 0xFFFFFF) - mTotalLength;
    if ((i5 != 0) || ((i11 != 0) && (f1 > 0.0F)))
    {
      float f2;
      int i12;
      label1257:
      View localView1;
      if (mWeightSum > 0.0F)
      {
        f2 = mWeightSum;
        arrayOfInt1[3] = -1;
        arrayOfInt1[2] = -1;
        arrayOfInt1[1] = -1;
        arrayOfInt1[0] = -1;
        arrayOfInt2[3] = -1;
        arrayOfInt2[2] = -1;
        arrayOfInt2[1] = -1;
        arrayOfInt2[0] = -1;
        i = -1;
        mTotalLength = 0;
        i12 = 0;
        if (i12 >= i1) {
          break label1751;
        }
        localView1 = getVirtualChildAt(i12);
        if ((localView1 != null) && (localView1.getVisibility() != 8)) {
          break label1300;
        }
      }
      label1300:
      LayoutParams localLayoutParams1;
      int i22;
      int i23;
      label1487:
      int i16;
      label1506:
      int i17;
      int i18;
      label1541:
      label1567:
      int i19;
      do
      {
        i12++;
        break label1257;
        f2 = f1;
        break;
        localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
        float f3 = weight;
        if (f3 > 0.0F)
        {
          i22 = (int)(f3 * i11 / f2);
          f2 -= f3;
          i11 -= i22;
          i23 = getChildMeasureSpec(paramInt2, getPaddingTop() + getPaddingBottom() + topMargin + bottomMargin, height);
          if ((width == 0) && (i2 == 1073741824)) {
            break label1649;
          }
          int i24 = i22 + localView1.getMeasuredWidth();
          if (i24 < 0) {
            i24 = 0;
          }
          localView1.measure(View.MeasureSpec.makeMeasureSpec(i24, 1073741824), i23);
          int i25 = 0xFF000000 & ViewCompat.getMeasuredState(localView1);
          j = ViewUtils.combineMeasuredStates(j, i25);
        }
        if (i6 == 0) {
          break label1677;
        }
        mTotalLength += localView1.getMeasuredWidth() + leftMargin + rightMargin + getNextLocationOffset(localView1);
        if ((i3 == 1073741824) || (height != -1)) {
          break label1722;
        }
        i16 = 1;
        i17 = topMargin + bottomMargin;
        i18 = i17 + localView1.getMeasuredHeight();
        i = Math.max(i, i18);
        if (i16 == 0) {
          break label1728;
        }
        k = Math.max(k, i17);
        if ((n == 0) || (height != -1)) {
          break label1735;
        }
        n = 1;
        if (!bool1) {
          break label1739;
        }
        i19 = localView1.getBaseline();
      } while (i19 == -1);
      if (gravity < 0) {}
      for (int i20 = mGravity;; i20 = gravity)
      {
        int i21 = (0xFFFFFFFE & (i20 & 0x70) >> 4) >> 1;
        arrayOfInt1[i21] = Math.max(arrayOfInt1[i21], i19);
        arrayOfInt2[i21] = Math.max(arrayOfInt2[i21], i18 - i19);
        break;
        label1649:
        if (i22 > 0) {}
        for (;;)
        {
          localView1.measure(View.MeasureSpec.makeMeasureSpec(i22, 1073741824), i23);
          break;
          i22 = 0;
        }
        label1677:
        int i15 = mTotalLength;
        mTotalLength = Math.max(i15, i15 + localView1.getMeasuredWidth() + leftMargin + rightMargin + getNextLocationOffset(localView1));
        break label1487;
        label1722:
        i16 = 0;
        break label1506;
        label1728:
        i17 = i18;
        break label1541;
        label1735:
        n = 0;
        break label1567;
        label1739:
        break;
      }
      label1751:
      mTotalLength += getPaddingLeft() + getPaddingRight();
      if ((arrayOfInt1[1] != -1) || (arrayOfInt1[0] != -1) || (arrayOfInt1[2] != -1) || (arrayOfInt1[3] != -1))
      {
        int i13 = Math.max(arrayOfInt1[3], Math.max(arrayOfInt1[0], Math.max(arrayOfInt1[1], arrayOfInt1[2]))) + Math.max(arrayOfInt2[3], Math.max(arrayOfInt2[0], Math.max(arrayOfInt2[1], arrayOfInt2[2])));
        i = Math.max(i, i13);
      }
    }
    do
    {
      if ((n == 0) && (i3 != 1073741824)) {
        i = k;
      }
      int i14 = Math.max(i + (getPaddingTop() + getPaddingBottom()), getSuggestedMinimumHeight());
      setMeasuredDimension(i10 | 0xFF000000 & j, ViewCompat.resolveSizeAndState(i14, paramInt2, j << 16));
      if (i4 != 0) {
        forceUniformHeight(i1, paramInt1);
      }
      return;
      k = Math.max(k, m);
    } while ((!bool2) || (i2 == 1073741824));
    int i26 = 0;
    label1957:
    View localView2;
    if (i26 < i1)
    {
      localView2 = getVirtualChildAt(i26);
      if ((localView2 != null) && (localView2.getVisibility() != 8)) {
        break label1993;
      }
    }
    for (;;)
    {
      i26++;
      break label1957;
      break;
      label1993:
      if (getLayoutParamsweight > 0.0F) {
        localView2.measure(View.MeasureSpec.makeMeasureSpec(i7, 1073741824), View.MeasureSpec.makeMeasureSpec(localView2.getMeasuredHeight(), 1073741824));
      }
    }
  }
  
  int measureNullChild(int paramInt)
  {
    return 0;
  }
  
  void measureVertical(int paramInt1, int paramInt2)
  {
    mTotalLength = 0;
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 1;
    float f1 = 0.0F;
    int i1 = getVirtualChildCount();
    int i2 = View.MeasureSpec.getMode(paramInt1);
    int i3 = View.MeasureSpec.getMode(paramInt2);
    int i4 = 0;
    int i5 = 0;
    int i6 = mBaselineAlignedChildIndex;
    boolean bool = mUseLargestChild;
    int i7 = Integer.MIN_VALUE;
    int i8 = 0;
    if (i8 < i1)
    {
      View localView4 = getVirtualChildAt(i8);
      if (localView4 == null) {
        mTotalLength += measureNullChild(i8);
      }
      for (;;)
      {
        i8++;
        break;
        if (localView4.getVisibility() != 8) {
          break label133;
        }
        i8 += getChildrenSkipCount(localView4, i8);
      }
      label133:
      if (hasDividerBeforeChildAt(i8)) {
        mTotalLength += mDividerHeight;
      }
      LayoutParams localLayoutParams3 = (LayoutParams)localView4.getLayoutParams();
      f1 += weight;
      if ((i3 == 1073741824) && (height == 0) && (weight > 0.0F))
      {
        int i30 = mTotalLength;
        mTotalLength = Math.max(i30, i30 + topMargin + bottomMargin);
        i5 = 1;
        if ((i6 >= 0) && (i6 == i8 + 1)) {
          mBaselineChildTop = mTotalLength;
        }
        if ((i8 < i6) && (weight > 0.0F)) {
          throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
        }
      }
      else
      {
        int i22 = Integer.MIN_VALUE;
        if ((height == 0) && (weight > 0.0F))
        {
          i22 = 0;
          height = -2;
        }
        if (f1 == 0.0F) {}
        for (int i23 = mTotalLength;; i23 = 0)
        {
          measureChildBeforeLayout(localView4, i8, paramInt1, 0, paramInt2, i23);
          if (i22 != Integer.MIN_VALUE) {
            height = i22;
          }
          int i24 = localView4.getMeasuredHeight();
          int i25 = mTotalLength;
          mTotalLength = Math.max(i25, i25 + i24 + topMargin + bottomMargin + getNextLocationOffset(localView4));
          if (!bool) {
            break;
          }
          i7 = Math.max(i24, i7);
          break;
        }
      }
      int i26 = 0;
      if (i2 != 1073741824)
      {
        int i29 = width;
        i26 = 0;
        if (i29 == -1)
        {
          i4 = 1;
          i26 = 1;
        }
      }
      int i27 = leftMargin + rightMargin;
      int i28 = i27 + localView4.getMeasuredWidth();
      i = Math.max(i, i28);
      j = ViewUtils.combineMeasuredStates(j, ViewCompat.getMeasuredState(localView4));
      if ((n != 0) && (width == -1))
      {
        n = 1;
        label516:
        if (weight <= 0.0F) {
          break label569;
        }
        if (i26 == 0) {
          break label562;
        }
      }
      for (;;)
      {
        m = Math.max(m, i27);
        i8 += getChildrenSkipCount(localView4, i8);
        break;
        n = 0;
        break label516;
        label562:
        i27 = i28;
      }
      label569:
      if (i26 != 0) {}
      for (;;)
      {
        k = Math.max(k, i27);
        break;
        i27 = i28;
      }
    }
    if ((mTotalLength > 0) && (hasDividerBeforeChildAt(i1))) {
      mTotalLength += mDividerHeight;
    }
    if ((bool) && ((i3 == Integer.MIN_VALUE) || (i3 == 0)))
    {
      mTotalLength = 0;
      int i20 = 0;
      if (i20 < i1)
      {
        View localView3 = getVirtualChildAt(i20);
        if (localView3 == null) {
          mTotalLength += measureNullChild(i20);
        }
        for (;;)
        {
          i20++;
          break;
          if (localView3.getVisibility() == 8)
          {
            i20 += getChildrenSkipCount(localView3, i20);
          }
          else
          {
            LayoutParams localLayoutParams2 = (LayoutParams)localView3.getLayoutParams();
            int i21 = mTotalLength;
            mTotalLength = Math.max(i21, i21 + i7 + topMargin + bottomMargin + getNextLocationOffset(localView3));
          }
        }
      }
    }
    mTotalLength += getPaddingTop() + getPaddingBottom();
    int i9 = ViewCompat.resolveSizeAndState(Math.max(mTotalLength, getSuggestedMinimumHeight()), paramInt2, 0);
    int i10 = (i9 & 0xFFFFFF) - mTotalLength;
    if ((i5 != 0) || ((i10 != 0) && (f1 > 0.0F)))
    {
      if (mWeightSum > 0.0F) {}
      View localView1;
      for (float f2 = mWeightSum;; f2 = f1)
      {
        mTotalLength = 0;
        for (int i11 = 0;; i11++)
        {
          if (i11 >= i1) {
            break label1211;
          }
          localView1 = getVirtualChildAt(i11);
          if (localView1.getVisibility() != 8) {
            break;
          }
        }
      }
      LayoutParams localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
      float f3 = weight;
      int i16;
      int i17;
      int i12;
      int i13;
      int i14;
      if (f3 > 0.0F)
      {
        i16 = (int)(f3 * i10 / f2);
        f2 -= f3;
        i10 -= i16;
        i17 = getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight() + leftMargin + rightMargin, width);
        if ((height != 0) || (i3 != 1073741824))
        {
          int i18 = i16 + localView1.getMeasuredHeight();
          if (i18 < 0) {
            i18 = 0;
          }
          localView1.measure(i17, View.MeasureSpec.makeMeasureSpec(i18, 1073741824));
          j = ViewUtils.combineMeasuredStates(j, 0xFF00 & ViewCompat.getMeasuredState(localView1));
        }
      }
      else
      {
        i12 = leftMargin + rightMargin;
        i13 = i12 + localView1.getMeasuredWidth();
        i = Math.max(i, i13);
        if ((i2 == 1073741824) || (width != -1)) {
          break label1192;
        }
        i14 = 1;
        label1088:
        if (i14 == 0) {
          break label1198;
        }
        label1093:
        k = Math.max(k, i12);
        if ((n == 0) || (width != -1)) {
          break label1205;
        }
      }
      label1192:
      label1198:
      label1205:
      for (n = 1;; n = 0)
      {
        int i15 = mTotalLength;
        mTotalLength = Math.max(i15, i15 + localView1.getMeasuredHeight() + topMargin + bottomMargin + getNextLocationOffset(localView1));
        break;
        if (i16 > 0) {}
        for (;;)
        {
          localView1.measure(i17, View.MeasureSpec.makeMeasureSpec(i16, 1073741824));
          break;
          i16 = 0;
        }
        i14 = 0;
        break label1088;
        i12 = i13;
        break label1093;
      }
      label1211:
      mTotalLength += getPaddingTop() + getPaddingBottom();
    }
    do
    {
      if ((n == 0) && (i2 != 1073741824)) {
        i = k;
      }
      setMeasuredDimension(ViewCompat.resolveSizeAndState(Math.max(i + (getPaddingLeft() + getPaddingRight()), getSuggestedMinimumWidth()), paramInt1, j), i9);
      if (i4 != 0) {
        forceUniformWidth(i1, paramInt2);
      }
      return;
      k = Math.max(k, m);
    } while ((!bool) || (i3 == 1073741824));
    int i19 = 0;
    label1311:
    View localView2;
    if (i19 < i1)
    {
      localView2 = getVirtualChildAt(i19);
      if ((localView2 != null) && (localView2.getVisibility() != 8)) {
        break label1347;
      }
    }
    for (;;)
    {
      i19++;
      break label1311;
      break;
      label1347:
      if (getLayoutParamsweight > 0.0F) {
        localView2.measure(View.MeasureSpec.makeMeasureSpec(localView2.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(i7, 1073741824));
      }
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (mDivider == null) {
      return;
    }
    if (mOrientation == 1)
    {
      drawDividersVertical(paramCanvas);
      return;
    }
    drawDividersHorizontal(paramCanvas);
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(LinearLayoutCompat.class.getName());
    }
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
      paramAccessibilityNodeInfo.setClassName(LinearLayoutCompat.class.getName());
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (mOrientation == 1)
    {
      layoutVertical(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    layoutHorizontal(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (mOrientation == 1)
    {
      measureVertical(paramInt1, paramInt2);
      return;
    }
    measureHorizontal(paramInt1, paramInt2);
  }
  
  public void setBaselineAligned(boolean paramBoolean)
  {
    mBaselineAligned = paramBoolean;
  }
  
  public void setBaselineAlignedChildIndex(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getChildCount())) {
      throw new IllegalArgumentException("base aligned child index out of range (0, " + getChildCount() + ")");
    }
    mBaselineAlignedChildIndex = paramInt;
  }
  
  public void setDividerDrawable(Drawable paramDrawable)
  {
    if (paramDrawable == mDivider) {
      return;
    }
    mDivider = paramDrawable;
    if (paramDrawable != null) {
      mDividerWidth = paramDrawable.getIntrinsicWidth();
    }
    for (mDividerHeight = paramDrawable.getIntrinsicHeight();; mDividerHeight = 0)
    {
      boolean bool = false;
      if (paramDrawable == null) {
        bool = true;
      }
      setWillNotDraw(bool);
      requestLayout();
      return;
      mDividerWidth = 0;
    }
  }
  
  public void setDividerPadding(int paramInt)
  {
    mDividerPadding = paramInt;
  }
  
  public void setGravity(int paramInt)
  {
    if (mGravity != paramInt)
    {
      if ((0x800007 & paramInt) == 0) {
        paramInt |= 0x800003;
      }
      if ((paramInt & 0x70) == 0) {
        paramInt |= 0x30;
      }
      mGravity = paramInt;
      requestLayout();
    }
  }
  
  public void setHorizontalGravity(int paramInt)
  {
    int i = paramInt & 0x800007;
    if ((0x800007 & mGravity) != i)
    {
      mGravity = (i | 0xFF7FFFF8 & mGravity);
      requestLayout();
    }
  }
  
  public void setMeasureWithLargestChildEnabled(boolean paramBoolean)
  {
    mUseLargestChild = paramBoolean;
  }
  
  public void setOrientation(int paramInt)
  {
    if (mOrientation != paramInt)
    {
      mOrientation = paramInt;
      requestLayout();
    }
  }
  
  public void setShowDividers(int paramInt)
  {
    if (paramInt != mShowDividers) {
      requestLayout();
    }
    mShowDividers = paramInt;
  }
  
  public void setVerticalGravity(int paramInt)
  {
    int i = paramInt & 0x70;
    if ((0x70 & mGravity) != i)
    {
      mGravity = (i | 0xFFFFFF8F & mGravity);
      requestLayout();
    }
  }
  
  public void setWeightSum(float paramFloat)
  {
    mWeightSum = Math.max(0.0F, paramFloat);
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface DividerMode {}
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    public int gravity = -1;
    public float weight;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      weight = 0.0F;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, float paramFloat)
    {
      super(paramInt2);
      weight = paramFloat;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.LinearLayoutCompat_Layout);
      weight = localTypedArray.getFloat(R.styleable.LinearLayoutCompat_Layout_android_layout_weight, 0.0F);
      gravity = localTypedArray.getInt(R.styleable.LinearLayoutCompat_Layout_android_layout_gravity, -1);
      localTypedArray.recycle();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      weight = weight;
      gravity = gravity;
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface OrientationMode {}
}
