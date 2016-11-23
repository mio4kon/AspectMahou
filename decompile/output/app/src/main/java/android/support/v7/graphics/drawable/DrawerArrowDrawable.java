package android.support.v7.graphics.drawable;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.style;
import android.support.v7.appcompat.R.styleable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DrawerArrowDrawable
  extends Drawable
{
  public static final int ARROW_DIRECTION_END = 3;
  public static final int ARROW_DIRECTION_LEFT = 0;
  public static final int ARROW_DIRECTION_RIGHT = 1;
  public static final int ARROW_DIRECTION_START = 2;
  private static final float ARROW_HEAD_ANGLE = (float)Math.toRadians(45.0D);
  private float mArrowHeadLength;
  private float mArrowShaftLength;
  private float mBarGap;
  private float mBarLength;
  private int mDirection = 2;
  private float mMaxCutForBarSize;
  private final Paint mPaint = new Paint();
  private final Path mPath = new Path();
  private float mProgress;
  private final int mSize;
  private boolean mSpin;
  private boolean mVerticalMirror = false;
  
  public DrawerArrowDrawable(Context paramContext)
  {
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setStrokeJoin(Paint.Join.MITER);
    mPaint.setStrokeCap(Paint.Cap.BUTT);
    mPaint.setAntiAlias(true);
    TypedArray localTypedArray = paramContext.getTheme().obtainStyledAttributes(null, R.styleable.DrawerArrowToggle, R.attr.drawerArrowStyle, R.style.Base_Widget_AppCompat_DrawerArrowToggle);
    setColor(localTypedArray.getColor(R.styleable.DrawerArrowToggle_color, 0));
    setBarThickness(localTypedArray.getDimension(R.styleable.DrawerArrowToggle_thickness, 0.0F));
    setSpinEnabled(localTypedArray.getBoolean(R.styleable.DrawerArrowToggle_spinBars, true));
    setGapSize(Math.round(localTypedArray.getDimension(R.styleable.DrawerArrowToggle_gapBetweenBars, 0.0F)));
    mSize = localTypedArray.getDimensionPixelSize(R.styleable.DrawerArrowToggle_drawableSize, 0);
    mBarLength = Math.round(localTypedArray.getDimension(R.styleable.DrawerArrowToggle_barLength, 0.0F));
    mArrowHeadLength = Math.round(localTypedArray.getDimension(R.styleable.DrawerArrowToggle_arrowHeadLength, 0.0F));
    mArrowShaftLength = localTypedArray.getDimension(R.styleable.DrawerArrowToggle_arrowShaftLength, 0.0F);
    localTypedArray.recycle();
  }
  
  private static float lerp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return paramFloat1 + paramFloat3 * (paramFloat2 - paramFloat1);
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect = getBounds();
    int i;
    float f6;
    label138:
    float f7;
    label146:
    int j;
    switch (mDirection)
    {
    case 2: 
    default: 
      if (DrawableCompat.getLayoutDirection(this) == 1)
      {
        i = 1;
        float f1 = (float)Math.sqrt(2.0F * (mArrowHeadLength * mArrowHeadLength));
        float f2 = lerp(mBarLength, f1, mProgress);
        float f3 = lerp(mBarLength, mArrowShaftLength, mProgress);
        float f4 = Math.round(lerp(0.0F, mMaxCutForBarSize, mProgress));
        float f5 = lerp(0.0F, ARROW_HEAD_ANGLE, mProgress);
        if (i == 0) {
          break label462;
        }
        f6 = 0.0F;
        if (i == 0) {
          break label470;
        }
        f7 = 180.0F;
        float f8 = mProgress;
        float f9 = lerp(f6, f7, f8);
        float f10 = (float)Math.round(f2 * Math.cos(f5));
        float f11 = (float)Math.round(f2 * Math.sin(f5));
        mPath.rewind();
        float f12 = lerp(mBarGap + mPaint.getStrokeWidth(), -mMaxCutForBarSize, mProgress);
        float f13 = -f3 / 2.0F;
        mPath.moveTo(f13 + f4, 0.0F);
        mPath.rLineTo(f3 - 2.0F * f4, 0.0F);
        mPath.moveTo(f13, f12);
        mPath.rLineTo(f10, f11);
        mPath.moveTo(f13, -f12);
        mPath.rLineTo(f10, -f11);
        mPath.close();
        paramCanvas.save();
        float f14 = mPaint.getStrokeWidth();
        float f15 = (float)(2 * ((int)(localRect.height() - 3.0F * f14 - 2.0F * mBarGap) / 4) + (1.5D * f14 + mBarGap));
        paramCanvas.translate(localRect.centerX(), f15);
        if (!mSpin) {
          break label482;
        }
        if ((i ^ mVerticalMirror) == 0) {
          break label476;
        }
        j = -1;
        label403:
        paramCanvas.rotate(f9 * j);
      }
      break;
    }
    for (;;)
    {
      paramCanvas.drawPath(mPath, mPaint);
      paramCanvas.restore();
      return;
      i = 0;
      break;
      i = 1;
      break;
      if (DrawableCompat.getLayoutDirection(this) == 0) {}
      for (i = 1;; i = 0) {
        break;
      }
      i = 0;
      break;
      label462:
      f6 = -180.0F;
      break label138;
      label470:
      f7 = 0.0F;
      break label146;
      label476:
      j = 1;
      break label403;
      label482:
      if (i != 0) {
        paramCanvas.rotate(180.0F);
      }
    }
  }
  
  public float getArrowHeadLength()
  {
    return mArrowHeadLength;
  }
  
  public float getArrowShaftLength()
  {
    return mArrowShaftLength;
  }
  
  public float getBarLength()
  {
    return mBarLength;
  }
  
  public float getBarThickness()
  {
    return mPaint.getStrokeWidth();
  }
  
  @ColorInt
  public int getColor()
  {
    return mPaint.getColor();
  }
  
  public int getDirection()
  {
    return mDirection;
  }
  
  public float getGapSize()
  {
    return mBarGap;
  }
  
  public int getIntrinsicHeight()
  {
    return mSize;
  }
  
  public int getIntrinsicWidth()
  {
    return mSize;
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public final Paint getPaint()
  {
    return mPaint;
  }
  
  @FloatRange(from=0.0D, to=1.0D)
  public float getProgress()
  {
    return mProgress;
  }
  
  public boolean isSpinEnabled()
  {
    return mSpin;
  }
  
  public void setAlpha(int paramInt)
  {
    if (paramInt != mPaint.getAlpha())
    {
      mPaint.setAlpha(paramInt);
      invalidateSelf();
    }
  }
  
  public void setArrowHeadLength(float paramFloat)
  {
    if (mArrowHeadLength != paramFloat)
    {
      mArrowHeadLength = paramFloat;
      invalidateSelf();
    }
  }
  
  public void setArrowShaftLength(float paramFloat)
  {
    if (mArrowShaftLength != paramFloat)
    {
      mArrowShaftLength = paramFloat;
      invalidateSelf();
    }
  }
  
  public void setBarLength(float paramFloat)
  {
    if (mBarLength != paramFloat)
    {
      mBarLength = paramFloat;
      invalidateSelf();
    }
  }
  
  public void setBarThickness(float paramFloat)
  {
    if (mPaint.getStrokeWidth() != paramFloat)
    {
      mPaint.setStrokeWidth(paramFloat);
      mMaxCutForBarSize = ((float)(paramFloat / 2.0F * Math.cos(ARROW_HEAD_ANGLE)));
      invalidateSelf();
    }
  }
  
  public void setColor(@ColorInt int paramInt)
  {
    if (paramInt != mPaint.getColor())
    {
      mPaint.setColor(paramInt);
      invalidateSelf();
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    mPaint.setColorFilter(paramColorFilter);
    invalidateSelf();
  }
  
  public void setDirection(int paramInt)
  {
    if (paramInt != mDirection)
    {
      mDirection = paramInt;
      invalidateSelf();
    }
  }
  
  public void setGapSize(float paramFloat)
  {
    if (paramFloat != mBarGap)
    {
      mBarGap = paramFloat;
      invalidateSelf();
    }
  }
  
  public void setProgress(@FloatRange(from=0.0D, to=1.0D) float paramFloat)
  {
    if (mProgress != paramFloat)
    {
      mProgress = paramFloat;
      invalidateSelf();
    }
  }
  
  public void setSpinEnabled(boolean paramBoolean)
  {
    if (mSpin != paramBoolean)
    {
      mSpin = paramBoolean;
      invalidateSelf();
    }
  }
  
  public void setVerticalMirror(boolean paramBoolean)
  {
    if (mVerticalMirror != paramBoolean)
    {
      mVerticalMirror = paramBoolean;
      invalidateSelf();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ArrowDirection {}
}
