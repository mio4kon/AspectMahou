package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v7.appcompat.R.styleable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import java.lang.ref.WeakReference;

public final class ViewStubCompat
  extends View
{
  private OnInflateListener mInflateListener;
  private int mInflatedId;
  private WeakReference<View> mInflatedViewRef;
  private LayoutInflater mInflater;
  private int mLayoutResource = 0;
  
  public ViewStubCompat(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ViewStubCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ViewStubCompat, paramInt, 0);
    mInflatedId = localTypedArray.getResourceId(R.styleable.ViewStubCompat_android_inflatedId, -1);
    mLayoutResource = localTypedArray.getResourceId(R.styleable.ViewStubCompat_android_layout, 0);
    setId(localTypedArray.getResourceId(R.styleable.ViewStubCompat_android_id, -1));
    localTypedArray.recycle();
    setVisibility(8);
    setWillNotDraw(true);
  }
  
  protected void dispatchDraw(Canvas paramCanvas) {}
  
  public void draw(Canvas paramCanvas) {}
  
  public int getInflatedId()
  {
    return mInflatedId;
  }
  
  public LayoutInflater getLayoutInflater()
  {
    return mInflater;
  }
  
  public int getLayoutResource()
  {
    return mLayoutResource;
  }
  
  public View inflate()
  {
    ViewParent localViewParent = getParent();
    if ((localViewParent != null) && ((localViewParent instanceof ViewGroup)))
    {
      if (mLayoutResource != 0)
      {
        ViewGroup localViewGroup = (ViewGroup)localViewParent;
        LayoutInflater localLayoutInflater;
        View localView;
        int i;
        if (mInflater != null)
        {
          localLayoutInflater = mInflater;
          localView = localLayoutInflater.inflate(mLayoutResource, localViewGroup, false);
          if (mInflatedId != -1) {
            localView.setId(mInflatedId);
          }
          i = localViewGroup.indexOfChild(this);
          localViewGroup.removeViewInLayout(this);
          ViewGroup.LayoutParams localLayoutParams = getLayoutParams();
          if (localLayoutParams == null) {
            break label148;
          }
          localViewGroup.addView(localView, i, localLayoutParams);
        }
        for (;;)
        {
          mInflatedViewRef = new WeakReference(localView);
          if (mInflateListener != null) {
            mInflateListener.onInflate(this, localView);
          }
          return localView;
          localLayoutInflater = LayoutInflater.from(getContext());
          break;
          label148:
          localViewGroup.addView(localView, i);
        }
      }
      throw new IllegalArgumentException("ViewStub must have a valid layoutResource");
    }
    throw new IllegalStateException("ViewStub must have a non-null ViewGroup viewParent");
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(0, 0);
  }
  
  public void setInflatedId(int paramInt)
  {
    mInflatedId = paramInt;
  }
  
  public void setLayoutInflater(LayoutInflater paramLayoutInflater)
  {
    mInflater = paramLayoutInflater;
  }
  
  public void setLayoutResource(int paramInt)
  {
    mLayoutResource = paramInt;
  }
  
  public void setOnInflateListener(OnInflateListener paramOnInflateListener)
  {
    mInflateListener = paramOnInflateListener;
  }
  
  public void setVisibility(int paramInt)
  {
    if (mInflatedViewRef != null)
    {
      View localView = (View)mInflatedViewRef.get();
      if (localView != null) {
        localView.setVisibility(paramInt);
      }
    }
    do
    {
      return;
      throw new IllegalStateException("setVisibility called on un-referenced view");
      super.setVisibility(paramInt);
    } while ((paramInt != 0) && (paramInt != 4));
    inflate();
  }
  
  public static abstract interface OnInflateListener
  {
    public abstract void onInflate(ViewStubCompat paramViewStubCompat, View paramView);
  }
}
