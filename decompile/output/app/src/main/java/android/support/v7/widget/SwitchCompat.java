package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.text.AllCapsTransformationMethod;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.CompoundButton;
import java.util.List;

public class SwitchCompat
  extends CompoundButton
{
  private static final String ACCESSIBILITY_EVENT_CLASS_NAME = "android.widget.Switch";
  private static final int[] CHECKED_STATE_SET = { 16842912 };
  private static final int MONOSPACE = 3;
  private static final int SANS = 1;
  private static final int SERIF = 2;
  private static final int THUMB_ANIMATION_DURATION = 250;
  private static final int TOUCH_MODE_DOWN = 1;
  private static final int TOUCH_MODE_DRAGGING = 2;
  private static final int TOUCH_MODE_IDLE;
  private boolean mHasThumbTint = false;
  private boolean mHasThumbTintMode = false;
  private boolean mHasTrackTint = false;
  private boolean mHasTrackTintMode = false;
  private int mMinFlingVelocity;
  private Layout mOffLayout;
  private Layout mOnLayout;
  ThumbAnimation mPositionAnimator;
  private boolean mShowText;
  private boolean mSplitTrack;
  private int mSwitchBottom;
  private int mSwitchHeight;
  private int mSwitchLeft;
  private int mSwitchMinWidth;
  private int mSwitchPadding;
  private int mSwitchRight;
  private int mSwitchTop;
  private TransformationMethod mSwitchTransformationMethod;
  private int mSwitchWidth;
  private final Rect mTempRect = new Rect();
  private ColorStateList mTextColors;
  private CharSequence mTextOff;
  private CharSequence mTextOn;
  private TextPaint mTextPaint = new TextPaint(1);
  private Drawable mThumbDrawable;
  private float mThumbPosition;
  private int mThumbTextPadding;
  private ColorStateList mThumbTintList = null;
  private PorterDuff.Mode mThumbTintMode = null;
  private int mThumbWidth;
  private int mTouchMode;
  private int mTouchSlop;
  private float mTouchX;
  private float mTouchY;
  private Drawable mTrackDrawable;
  private ColorStateList mTrackTintList = null;
  private PorterDuff.Mode mTrackTintMode = null;
  private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
  
  public SwitchCompat(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SwitchCompat(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.switchStyle);
  }
  
  public SwitchCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    Resources localResources = getResources();
    mTextPaint.density = getDisplayMetricsdensity;
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.SwitchCompat, paramInt, 0);
    mThumbDrawable = localTintTypedArray.getDrawable(R.styleable.SwitchCompat_android_thumb);
    if (mThumbDrawable != null) {
      mThumbDrawable.setCallback(this);
    }
    mTrackDrawable = localTintTypedArray.getDrawable(R.styleable.SwitchCompat_track);
    if (mTrackDrawable != null) {
      mTrackDrawable.setCallback(this);
    }
    mTextOn = localTintTypedArray.getText(R.styleable.SwitchCompat_android_textOn);
    mTextOff = localTintTypedArray.getText(R.styleable.SwitchCompat_android_textOff);
    mShowText = localTintTypedArray.getBoolean(R.styleable.SwitchCompat_showText, true);
    mThumbTextPadding = localTintTypedArray.getDimensionPixelSize(R.styleable.SwitchCompat_thumbTextPadding, 0);
    mSwitchMinWidth = localTintTypedArray.getDimensionPixelSize(R.styleable.SwitchCompat_switchMinWidth, 0);
    mSwitchPadding = localTintTypedArray.getDimensionPixelSize(R.styleable.SwitchCompat_switchPadding, 0);
    mSplitTrack = localTintTypedArray.getBoolean(R.styleable.SwitchCompat_splitTrack, false);
    ColorStateList localColorStateList1 = localTintTypedArray.getColorStateList(R.styleable.SwitchCompat_thumbTint);
    if (localColorStateList1 != null)
    {
      mThumbTintList = localColorStateList1;
      mHasThumbTint = true;
    }
    PorterDuff.Mode localMode1 = DrawableUtils.parseTintMode(localTintTypedArray.getInt(R.styleable.SwitchCompat_thumbTintMode, -1), null);
    if (mThumbTintMode != localMode1)
    {
      mThumbTintMode = localMode1;
      mHasThumbTintMode = true;
    }
    if ((mHasThumbTint) || (mHasThumbTintMode)) {
      applyThumbTint();
    }
    ColorStateList localColorStateList2 = localTintTypedArray.getColorStateList(R.styleable.SwitchCompat_trackTint);
    if (localColorStateList2 != null)
    {
      mTrackTintList = localColorStateList2;
      mHasTrackTint = true;
    }
    PorterDuff.Mode localMode2 = DrawableUtils.parseTintMode(localTintTypedArray.getInt(R.styleable.SwitchCompat_trackTintMode, -1), null);
    if (mTrackTintMode != localMode2)
    {
      mTrackTintMode = localMode2;
      mHasTrackTintMode = true;
    }
    if ((mHasTrackTint) || (mHasTrackTintMode)) {
      applyTrackTint();
    }
    int i = localTintTypedArray.getResourceId(R.styleable.SwitchCompat_switchTextAppearance, 0);
    if (i != 0) {
      setSwitchTextAppearance(paramContext, i);
    }
    localTintTypedArray.recycle();
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(paramContext);
    mTouchSlop = localViewConfiguration.getScaledTouchSlop();
    mMinFlingVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
    refreshDrawableState();
    setChecked(isChecked());
  }
  
  private void animateThumbToCheckedState(final boolean paramBoolean)
  {
    if (mPositionAnimator != null) {
      cancelPositionAnimator();
    }
    float f1 = mThumbPosition;
    if (paramBoolean) {}
    for (float f2 = 1.0F;; f2 = 0.0F)
    {
      mPositionAnimator = new ThumbAnimation(f1, f2);
      mPositionAnimator.setDuration(250L);
      mPositionAnimator.setAnimationListener(new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnonymousAnimation)
        {
          SwitchCompat localSwitchCompat;
          if (mPositionAnimator == paramAnonymousAnimation)
          {
            localSwitchCompat = SwitchCompat.this;
            if (!paramBoolean) {
              break label39;
            }
          }
          label39:
          for (float f = 1.0F;; f = 0.0F)
          {
            localSwitchCompat.setThumbPosition(f);
            mPositionAnimator = null;
            return;
          }
        }
        
        public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
        
        public void onAnimationStart(Animation paramAnonymousAnimation) {}
      });
      startAnimation(mPositionAnimator);
      return;
    }
  }
  
  private void applyThumbTint()
  {
    if ((mThumbDrawable != null) && ((mHasThumbTint) || (mHasThumbTintMode)))
    {
      mThumbDrawable = mThumbDrawable.mutate();
      if (mHasThumbTint) {
        DrawableCompat.setTintList(mThumbDrawable, mThumbTintList);
      }
      if (mHasThumbTintMode) {
        DrawableCompat.setTintMode(mThumbDrawable, mThumbTintMode);
      }
      if (mThumbDrawable.isStateful()) {
        mThumbDrawable.setState(getDrawableState());
      }
    }
  }
  
  private void applyTrackTint()
  {
    if ((mTrackDrawable != null) && ((mHasTrackTint) || (mHasTrackTintMode)))
    {
      mTrackDrawable = mTrackDrawable.mutate();
      if (mHasTrackTint) {
        DrawableCompat.setTintList(mTrackDrawable, mTrackTintList);
      }
      if (mHasTrackTintMode) {
        DrawableCompat.setTintMode(mTrackDrawable, mTrackTintMode);
      }
      if (mTrackDrawable.isStateful()) {
        mTrackDrawable.setState(getDrawableState());
      }
    }
  }
  
  private void cancelPositionAnimator()
  {
    if (mPositionAnimator != null)
    {
      clearAnimation();
      mPositionAnimator = null;
    }
  }
  
  private void cancelSuperTouch(MotionEvent paramMotionEvent)
  {
    MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
    localMotionEvent.setAction(3);
    super.onTouchEvent(localMotionEvent);
    localMotionEvent.recycle();
  }
  
  private static float constrain(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 < paramFloat2) {
      return paramFloat2;
    }
    if (paramFloat1 > paramFloat3) {
      return paramFloat3;
    }
    return paramFloat1;
  }
  
  private boolean getTargetCheckedState()
  {
    return mThumbPosition > 0.5F;
  }
  
  private int getThumbOffset()
  {
    if (ViewUtils.isLayoutRtl(this)) {}
    for (float f = 1.0F - mThumbPosition;; f = mThumbPosition) {
      return (int)(0.5F + f * getThumbScrollRange());
    }
  }
  
  private int getThumbScrollRange()
  {
    if (mTrackDrawable != null)
    {
      Rect localRect1 = mTempRect;
      mTrackDrawable.getPadding(localRect1);
      if (mThumbDrawable != null) {}
      for (Rect localRect2 = DrawableUtils.getOpticalBounds(mThumbDrawable);; localRect2 = DrawableUtils.INSETS_NONE) {
        return mSwitchWidth - mThumbWidth - left - right - left - right;
      }
    }
    return 0;
  }
  
  private boolean hitThumb(float paramFloat1, float paramFloat2)
  {
    if (mThumbDrawable == null) {}
    int j;
    int k;
    int m;
    int n;
    do
    {
      return false;
      int i = getThumbOffset();
      mThumbDrawable.getPadding(mTempRect);
      j = mSwitchTop - mTouchSlop;
      k = i + mSwitchLeft - mTouchSlop;
      m = k + mThumbWidth + mTempRect.left + mTempRect.right + mTouchSlop;
      n = mSwitchBottom + mTouchSlop;
    } while ((paramFloat1 <= k) || (paramFloat1 >= m) || (paramFloat2 <= j) || (paramFloat2 >= n));
    return true;
  }
  
  private Layout makeLayout(CharSequence paramCharSequence)
  {
    CharSequence localCharSequence;
    TextPaint localTextPaint;
    if (mSwitchTransformationMethod != null)
    {
      localCharSequence = mSwitchTransformationMethod.getTransformation(paramCharSequence, this);
      localTextPaint = mTextPaint;
      if (localCharSequence == null) {
        break label66;
      }
    }
    label66:
    for (int i = (int)Math.ceil(Layout.getDesiredWidth(localCharSequence, mTextPaint));; i = 0)
    {
      return new StaticLayout(localCharSequence, localTextPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
      localCharSequence = paramCharSequence;
      break;
    }
  }
  
  private void setSwitchTypefaceByIndex(int paramInt1, int paramInt2)
  {
    Typeface localTypeface = null;
    switch (paramInt1)
    {
    }
    for (;;)
    {
      setSwitchTypeface(localTypeface, paramInt2);
      return;
      localTypeface = Typeface.SANS_SERIF;
      continue;
      localTypeface = Typeface.SERIF;
      continue;
      localTypeface = Typeface.MONOSPACE;
    }
  }
  
  private void stopDrag(MotionEvent paramMotionEvent)
  {
    mTouchMode = 0;
    int i;
    boolean bool1;
    float f;
    boolean bool2;
    if ((paramMotionEvent.getAction() == 1) && (isEnabled()))
    {
      i = 1;
      bool1 = isChecked();
      if (i == 0) {
        break label143;
      }
      mVelocityTracker.computeCurrentVelocity(1000);
      f = mVelocityTracker.getXVelocity();
      if (Math.abs(f) <= mMinFlingVelocity) {
        break label134;
      }
      if (!ViewUtils.isLayoutRtl(this)) {
        break label115;
      }
      if (f >= 0.0F) {
        break label109;
      }
      bool2 = true;
    }
    for (;;)
    {
      if (bool2 != bool1) {
        playSoundEffect(0);
      }
      setChecked(bool2);
      cancelSuperTouch(paramMotionEvent);
      return;
      i = 0;
      break;
      label109:
      bool2 = false;
      continue;
      label115:
      if (f > 0.0F)
      {
        bool2 = true;
      }
      else
      {
        bool2 = false;
        continue;
        label134:
        bool2 = getTargetCheckedState();
        continue;
        label143:
        bool2 = bool1;
      }
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect1 = mTempRect;
    int i = mSwitchLeft;
    int j = mSwitchTop;
    int k = mSwitchRight;
    int m = mSwitchBottom;
    int n = i + getThumbOffset();
    if (mThumbDrawable != null) {}
    for (Rect localRect2 = DrawableUtils.getOpticalBounds(mThumbDrawable);; localRect2 = DrawableUtils.INSETS_NONE)
    {
      if (mTrackDrawable != null)
      {
        mTrackDrawable.getPadding(localRect1);
        n += left;
        int i3 = i;
        int i4 = j;
        int i5 = k;
        int i6 = m;
        if (localRect2 != null)
        {
          if (left > left) {
            i3 += left - left;
          }
          if (top > top) {
            i4 += top - top;
          }
          if (right > right) {
            i5 -= right - right;
          }
          if (bottom > bottom) {
            i6 -= bottom - bottom;
          }
        }
        mTrackDrawable.setBounds(i3, i4, i5, i6);
      }
      if (mThumbDrawable != null)
      {
        mThumbDrawable.getPadding(localRect1);
        int i1 = n - left;
        int i2 = n + mThumbWidth + right;
        mThumbDrawable.setBounds(i1, j, i2, m);
        Drawable localDrawable = getBackground();
        if (localDrawable != null) {
          DrawableCompat.setHotspotBounds(localDrawable, i1, j, i2, m);
        }
      }
      super.draw(paramCanvas);
      return;
    }
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      super.drawableHotspotChanged(paramFloat1, paramFloat2);
    }
    if (mThumbDrawable != null) {
      DrawableCompat.setHotspot(mThumbDrawable, paramFloat1, paramFloat2);
    }
    if (mTrackDrawable != null) {
      DrawableCompat.setHotspot(mTrackDrawable, paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    Drawable localDrawable1 = mThumbDrawable;
    boolean bool1 = false;
    if (localDrawable1 != null)
    {
      boolean bool2 = localDrawable1.isStateful();
      bool1 = false;
      if (bool2) {
        bool1 = false | localDrawable1.setState(arrayOfInt);
      }
    }
    Drawable localDrawable2 = mTrackDrawable;
    if ((localDrawable2 != null) && (localDrawable2.isStateful())) {
      bool1 |= localDrawable2.setState(arrayOfInt);
    }
    if (bool1) {
      invalidate();
    }
  }
  
  public int getCompoundPaddingLeft()
  {
    int i;
    if (!ViewUtils.isLayoutRtl(this)) {
      i = super.getCompoundPaddingLeft();
    }
    do
    {
      return i;
      i = super.getCompoundPaddingLeft() + mSwitchWidth;
    } while (TextUtils.isEmpty(getText()));
    return i + mSwitchPadding;
  }
  
  public int getCompoundPaddingRight()
  {
    int i;
    if (ViewUtils.isLayoutRtl(this)) {
      i = super.getCompoundPaddingRight();
    }
    do
    {
      return i;
      i = super.getCompoundPaddingRight() + mSwitchWidth;
    } while (TextUtils.isEmpty(getText()));
    return i + mSwitchPadding;
  }
  
  public boolean getShowText()
  {
    return mShowText;
  }
  
  public boolean getSplitTrack()
  {
    return mSplitTrack;
  }
  
  public int getSwitchMinWidth()
  {
    return mSwitchMinWidth;
  }
  
  public int getSwitchPadding()
  {
    return mSwitchPadding;
  }
  
  public CharSequence getTextOff()
  {
    return mTextOff;
  }
  
  public CharSequence getTextOn()
  {
    return mTextOn;
  }
  
  public Drawable getThumbDrawable()
  {
    return mThumbDrawable;
  }
  
  public int getThumbTextPadding()
  {
    return mThumbTextPadding;
  }
  
  @Nullable
  public ColorStateList getThumbTintList()
  {
    return mThumbTintList;
  }
  
  @Nullable
  public PorterDuff.Mode getThumbTintMode()
  {
    return mThumbTintMode;
  }
  
  public Drawable getTrackDrawable()
  {
    return mTrackDrawable;
  }
  
  @Nullable
  public ColorStateList getTrackTintList()
  {
    return mTrackTintList;
  }
  
  @Nullable
  public PorterDuff.Mode getTrackTintMode()
  {
    return mTrackTintMode;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    if (Build.VERSION.SDK_INT >= 11)
    {
      super.jumpDrawablesToCurrentState();
      if (mThumbDrawable != null) {
        mThumbDrawable.jumpToCurrentState();
      }
      if (mTrackDrawable != null) {
        mTrackDrawable.jumpToCurrentState();
      }
      cancelPositionAnimator();
      if (!isChecked()) {
        break label59;
      }
    }
    label59:
    for (float f = 1.0F;; f = 0.0F)
    {
      setThumbPosition(f);
      return;
    }
  }
  
  protected int[] onCreateDrawableState(int paramInt)
  {
    int[] arrayOfInt = super.onCreateDrawableState(paramInt + 1);
    if (isChecked()) {
      mergeDrawableStates(arrayOfInt, CHECKED_STATE_SET);
    }
    return arrayOfInt;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    Rect localRect1 = mTempRect;
    Drawable localDrawable1 = mTrackDrawable;
    int k;
    int m;
    label144:
    int n;
    Layout localLayout;
    label174:
    Rect localRect2;
    if (localDrawable1 != null)
    {
      localDrawable1.getPadding(localRect1);
      int i = mSwitchTop;
      int j = mSwitchBottom;
      k = i + top;
      m = j - bottom;
      Drawable localDrawable2 = mThumbDrawable;
      if (localDrawable1 != null)
      {
        if ((!mSplitTrack) || (localDrawable2 == null)) {
          break label304;
        }
        Rect localRect3 = DrawableUtils.getOpticalBounds(localDrawable2);
        localDrawable2.copyBounds(localRect1);
        left += left;
        right -= right;
        int i4 = paramCanvas.save();
        paramCanvas.clipRect(localRect1, Region.Op.DIFFERENCE);
        localDrawable1.draw(paramCanvas);
        paramCanvas.restoreToCount(i4);
      }
      n = paramCanvas.save();
      if (localDrawable2 != null) {
        localDrawable2.draw(paramCanvas);
      }
      if (!getTargetCheckedState()) {
        break label312;
      }
      localLayout = mOnLayout;
      if (localLayout != null)
      {
        int[] arrayOfInt = getDrawableState();
        if (mTextColors != null) {
          mTextPaint.setColor(mTextColors.getColorForState(arrayOfInt, 0));
        }
        mTextPaint.drawableState = arrayOfInt;
        if (localDrawable2 == null) {
          break label321;
        }
        localRect2 = localDrawable2.getBounds();
      }
    }
    label304:
    label312:
    label321:
    for (int i1 = left + right;; i1 = getWidth())
    {
      int i2 = i1 / 2 - localLayout.getWidth() / 2;
      int i3 = (k + m) / 2 - localLayout.getHeight() / 2;
      paramCanvas.translate(i2, i3);
      localLayout.draw(paramCanvas);
      paramCanvas.restoreToCount(n);
      return;
      localRect1.setEmpty();
      break;
      localDrawable1.draw(paramCanvas);
      break label144;
      localLayout = mOffLayout;
      break label174;
    }
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    paramAccessibilityEvent.setClassName("android.widget.Switch");
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
      paramAccessibilityNodeInfo.setClassName("android.widget.Switch");
      if (!isChecked()) {
        break label56;
      }
    }
    CharSequence localCharSequence2;
    label56:
    for (CharSequence localCharSequence1 = mTextOn;; localCharSequence1 = mTextOff)
    {
      if (!TextUtils.isEmpty(localCharSequence1))
      {
        localCharSequence2 = paramAccessibilityNodeInfo.getText();
        if (!TextUtils.isEmpty(localCharSequence2)) {
          break;
        }
        paramAccessibilityNodeInfo.setText(localCharSequence1);
      }
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(localCharSequence2).append(' ').append(localCharSequence1);
    paramAccessibilityNodeInfo.setText(localStringBuilder);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    Drawable localDrawable = mThumbDrawable;
    int i = 0;
    int j = 0;
    Rect localRect1;
    int m;
    int k;
    label125:
    int i1;
    int n;
    if (localDrawable != null)
    {
      localRect1 = mTempRect;
      if (mTrackDrawable != null)
      {
        mTrackDrawable.getPadding(localRect1);
        Rect localRect2 = DrawableUtils.getOpticalBounds(mThumbDrawable);
        i = Math.max(0, left - left);
        j = Math.max(0, right - right);
      }
    }
    else
    {
      if (!ViewUtils.isLayoutRtl(this)) {
        break label208;
      }
      m = i + getPaddingLeft();
      k = m + mSwitchWidth - i - j;
      switch (0x70 & getGravity())
      {
      default: 
        i1 = getPaddingTop();
        n = i1 + mSwitchHeight;
      }
    }
    for (;;)
    {
      mSwitchLeft = m;
      mSwitchTop = i1;
      mSwitchBottom = n;
      mSwitchRight = k;
      return;
      localRect1.setEmpty();
      break;
      label208:
      k = getWidth() - getPaddingRight() - j;
      m = j + (i + (k - mSwitchWidth));
      break label125;
      i1 = (getPaddingTop() + getHeight() - getPaddingBottom()) / 2 - mSwitchHeight / 2;
      n = i1 + mSwitchHeight;
      continue;
      n = getHeight() - getPaddingBottom();
      i1 = n - mSwitchHeight;
    }
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    if (mShowText)
    {
      if (mOnLayout == null) {
        mOnLayout = makeLayout(mTextOn);
      }
      if (mOffLayout == null) {
        mOffLayout = makeLayout(mTextOff);
      }
    }
    Rect localRect1 = mTempRect;
    int j;
    int i;
    int k;
    if (mThumbDrawable != null)
    {
      mThumbDrawable.getPadding(localRect1);
      j = mThumbDrawable.getIntrinsicWidth() - left - right;
      i = mThumbDrawable.getIntrinsicHeight();
      if (!mShowText) {
        break label292;
      }
      k = Math.max(mOnLayout.getWidth(), mOffLayout.getWidth()) + 2 * mThumbTextPadding;
      label127:
      mThumbWidth = Math.max(k, j);
      if (mTrackDrawable == null) {
        break label298;
      }
      mTrackDrawable.getPadding(localRect1);
    }
    for (int m = mTrackDrawable.getIntrinsicHeight();; m = 0)
    {
      int n = left;
      int i1 = right;
      if (mThumbDrawable != null)
      {
        Rect localRect2 = DrawableUtils.getOpticalBounds(mThumbDrawable);
        n = Math.max(n, left);
        i1 = Math.max(i1, right);
      }
      int i2 = Math.max(mSwitchMinWidth, i1 + (n + 2 * mThumbWidth));
      int i3 = Math.max(m, i);
      mSwitchWidth = i2;
      mSwitchHeight = i3;
      super.onMeasure(paramInt1, paramInt2);
      if (getMeasuredHeight() < i3) {
        setMeasuredDimension(ViewCompat.getMeasuredWidthAndState(this), i3);
      }
      return;
      i = 0;
      j = 0;
      break;
      label292:
      k = 0;
      break label127;
      label298:
      localRect1.setEmpty();
    }
  }
  
  public void onPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onPopulateAccessibilityEvent(paramAccessibilityEvent);
    if (isChecked()) {}
    for (CharSequence localCharSequence = mTextOn;; localCharSequence = mTextOff)
    {
      if (localCharSequence != null) {
        paramAccessibilityEvent.getText().add(localCharSequence);
      }
      return;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    mVelocityTracker.addMovement(paramMotionEvent);
    switch (MotionEventCompat.getActionMasked(paramMotionEvent))
    {
    }
    for (;;)
    {
      return super.onTouchEvent(paramMotionEvent);
      float f7 = paramMotionEvent.getX();
      float f8 = paramMotionEvent.getY();
      if ((isEnabled()) && (hitThumb(f7, f8)))
      {
        mTouchMode = 1;
        mTouchX = f7;
        mTouchY = f8;
        continue;
        switch (mTouchMode)
        {
        case 0: 
        default: 
          break;
        case 1: 
          float f5 = paramMotionEvent.getX();
          float f6 = paramMotionEvent.getY();
          if ((Math.abs(f5 - mTouchX) > mTouchSlop) || (Math.abs(f6 - mTouchY) > mTouchSlop))
          {
            mTouchMode = 2;
            getParent().requestDisallowInterceptTouchEvent(true);
            mTouchX = f5;
            mTouchY = f6;
            return true;
          }
          break;
        case 2: 
          float f1 = paramMotionEvent.getX();
          int i = getThumbScrollRange();
          float f2 = f1 - mTouchX;
          if (i != 0)
          {
            f3 = f2 / i;
            if (ViewUtils.isLayoutRtl(this)) {
              f3 = -f3;
            }
            float f4 = constrain(f3 + mThumbPosition, 0.0F, 1.0F);
            if (f4 != mThumbPosition)
            {
              mTouchX = f1;
              setThumbPosition(f4);
            }
            return true;
          }
          if (f2 > 0.0F) {}
          for (float f3 = 1.0F;; f3 = -1.0F) {
            break;
          }
          if (mTouchMode == 2)
          {
            stopDrag(paramMotionEvent);
            super.onTouchEvent(paramMotionEvent);
            return true;
          }
          mTouchMode = 0;
          mVelocityTracker.clear();
        }
      }
    }
  }
  
  public void setChecked(boolean paramBoolean)
  {
    super.setChecked(paramBoolean);
    boolean bool = isChecked();
    if ((getWindowToken() != null) && (ViewCompat.isLaidOut(this)) && (isShown()))
    {
      animateThumbToCheckedState(bool);
      return;
    }
    cancelPositionAnimator();
    if (bool) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      setThumbPosition(f);
      return;
    }
  }
  
  public void setShowText(boolean paramBoolean)
  {
    if (mShowText != paramBoolean)
    {
      mShowText = paramBoolean;
      requestLayout();
    }
  }
  
  public void setSplitTrack(boolean paramBoolean)
  {
    mSplitTrack = paramBoolean;
    invalidate();
  }
  
  public void setSwitchMinWidth(int paramInt)
  {
    mSwitchMinWidth = paramInt;
    requestLayout();
  }
  
  public void setSwitchPadding(int paramInt)
  {
    mSwitchPadding = paramInt;
    requestLayout();
  }
  
  public void setSwitchTextAppearance(Context paramContext, int paramInt)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramInt, R.styleable.TextAppearance);
    ColorStateList localColorStateList = localTypedArray.getColorStateList(R.styleable.TextAppearance_android_textColor);
    if (localColorStateList != null)
    {
      mTextColors = localColorStateList;
      int i = localTypedArray.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, 0);
      if ((i != 0) && (i != mTextPaint.getTextSize()))
      {
        mTextPaint.setTextSize(i);
        requestLayout();
      }
      setSwitchTypefaceByIndex(localTypedArray.getInt(R.styleable.TextAppearance_android_typeface, -1), localTypedArray.getInt(R.styleable.TextAppearance_android_textStyle, -1));
      if (!localTypedArray.getBoolean(R.styleable.TextAppearance_textAllCaps, false)) {
        break label134;
      }
    }
    label134:
    for (mSwitchTransformationMethod = new AllCapsTransformationMethod(getContext());; mSwitchTransformationMethod = null)
    {
      localTypedArray.recycle();
      return;
      mTextColors = getTextColors();
      break;
    }
  }
  
  public void setSwitchTypeface(Typeface paramTypeface)
  {
    if (mTextPaint.getTypeface() != paramTypeface)
    {
      mTextPaint.setTypeface(paramTypeface);
      requestLayout();
      invalidate();
    }
  }
  
  public void setSwitchTypeface(Typeface paramTypeface, int paramInt)
  {
    if (paramInt > 0)
    {
      Typeface localTypeface;
      int i;
      label28:
      TextPaint localTextPaint2;
      if (paramTypeface == null)
      {
        localTypeface = Typeface.defaultFromStyle(paramInt);
        setSwitchTypeface(localTypeface);
        if (localTypeface == null) {
          break label101;
        }
        i = localTypeface.getStyle();
        int j = paramInt & (i ^ 0xFFFFFFFF);
        TextPaint localTextPaint1 = mTextPaint;
        int k = j & 0x1;
        boolean bool = false;
        if (k != 0) {
          bool = true;
        }
        localTextPaint1.setFakeBoldText(bool);
        localTextPaint2 = mTextPaint;
        if ((j & 0x2) == 0) {
          break label107;
        }
      }
      label101:
      label107:
      for (float f = -0.25F;; f = 0.0F)
      {
        localTextPaint2.setTextSkewX(f);
        return;
        localTypeface = Typeface.create(paramTypeface, paramInt);
        break;
        i = 0;
        break label28;
      }
    }
    mTextPaint.setFakeBoldText(false);
    mTextPaint.setTextSkewX(0.0F);
    setSwitchTypeface(paramTypeface);
  }
  
  public void setTextOff(CharSequence paramCharSequence)
  {
    mTextOff = paramCharSequence;
    requestLayout();
  }
  
  public void setTextOn(CharSequence paramCharSequence)
  {
    mTextOn = paramCharSequence;
    requestLayout();
  }
  
  public void setThumbDrawable(Drawable paramDrawable)
  {
    if (mThumbDrawable != null) {
      mThumbDrawable.setCallback(null);
    }
    mThumbDrawable = paramDrawable;
    if (paramDrawable != null) {
      paramDrawable.setCallback(this);
    }
    requestLayout();
  }
  
  void setThumbPosition(float paramFloat)
  {
    mThumbPosition = paramFloat;
    invalidate();
  }
  
  public void setThumbResource(int paramInt)
  {
    setThumbDrawable(AppCompatResources.getDrawable(getContext(), paramInt));
  }
  
  public void setThumbTextPadding(int paramInt)
  {
    mThumbTextPadding = paramInt;
    requestLayout();
  }
  
  public void setThumbTintList(@Nullable ColorStateList paramColorStateList)
  {
    mThumbTintList = paramColorStateList;
    mHasThumbTint = true;
    applyThumbTint();
  }
  
  public void setThumbTintMode(@Nullable PorterDuff.Mode paramMode)
  {
    mThumbTintMode = paramMode;
    mHasThumbTintMode = true;
    applyThumbTint();
  }
  
  public void setTrackDrawable(Drawable paramDrawable)
  {
    if (mTrackDrawable != null) {
      mTrackDrawable.setCallback(null);
    }
    mTrackDrawable = paramDrawable;
    if (paramDrawable != null) {
      paramDrawable.setCallback(this);
    }
    requestLayout();
  }
  
  public void setTrackResource(int paramInt)
  {
    setTrackDrawable(AppCompatResources.getDrawable(getContext(), paramInt));
  }
  
  public void setTrackTintList(@Nullable ColorStateList paramColorStateList)
  {
    mTrackTintList = paramColorStateList;
    mHasTrackTint = true;
    applyTrackTint();
  }
  
  public void setTrackTintMode(@Nullable PorterDuff.Mode paramMode)
  {
    mTrackTintMode = paramMode;
    mHasTrackTintMode = true;
    applyTrackTint();
  }
  
  public void toggle()
  {
    if (!isChecked()) {}
    for (boolean bool = true;; bool = false)
    {
      setChecked(bool);
      return;
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (super.verifyDrawable(paramDrawable)) || (paramDrawable == mThumbDrawable) || (paramDrawable == mTrackDrawable);
  }
  
  private class ThumbAnimation
    extends Animation
  {
    final float mDiff;
    final float mEndPosition;
    final float mStartPosition;
    
    ThumbAnimation(float paramFloat1, float paramFloat2)
    {
      mStartPosition = paramFloat1;
      mEndPosition = paramFloat2;
      mDiff = (paramFloat2 - paramFloat1);
    }
    
    protected void applyTransformation(float paramFloat, Transformation paramTransformation)
    {
      setThumbPosition(mStartPosition + paramFloat * mDiff);
    }
  }
}
