package android.support.graphics.drawable;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.VectorDrawable;
import android.os.Build.VERSION;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@TargetApi(21)
public class VectorDrawableCompat
  extends VectorDrawableCommon
{
  private static final boolean DBG_VECTOR_DRAWABLE = false;
  static final PorterDuff.Mode DEFAULT_TINT_MODE = PorterDuff.Mode.SRC_IN;
  private static final int LINECAP_BUTT = 0;
  private static final int LINECAP_ROUND = 1;
  private static final int LINECAP_SQUARE = 2;
  private static final int LINEJOIN_BEVEL = 2;
  private static final int LINEJOIN_MITER = 0;
  private static final int LINEJOIN_ROUND = 1;
  static final String LOGTAG = "VectorDrawableCompat";
  private static final int MAX_CACHED_BITMAP_SIZE = 2048;
  private static final String SHAPE_CLIP_PATH = "clip-path";
  private static final String SHAPE_GROUP = "group";
  private static final String SHAPE_PATH = "path";
  private static final String SHAPE_VECTOR = "vector";
  private boolean mAllowCaching = true;
  private Drawable.ConstantState mCachedConstantStateDelegate;
  private ColorFilter mColorFilter;
  private boolean mMutated;
  private PorterDuffColorFilter mTintFilter;
  private final Rect mTmpBounds = new Rect();
  private final float[] mTmpFloats = new float[9];
  private final Matrix mTmpMatrix = new Matrix();
  private VectorDrawableCompatState mVectorState;
  
  VectorDrawableCompat()
  {
    mVectorState = new VectorDrawableCompatState();
  }
  
  VectorDrawableCompat(@NonNull VectorDrawableCompatState paramVectorDrawableCompatState)
  {
    mVectorState = paramVectorDrawableCompatState;
    mTintFilter = updateTintFilter(mTintFilter, mTint, mTintMode);
  }
  
  static int applyAlpha(int paramInt, float paramFloat)
  {
    int i = Color.alpha(paramInt);
    return paramInt & 0xFFFFFF | (int)(paramFloat * i) << 24;
  }
  
  @Nullable
  public static VectorDrawableCompat create(@NonNull Resources paramResources, @DrawableRes int paramInt, @Nullable Resources.Theme paramTheme)
  {
    if (Build.VERSION.SDK_INT >= 23)
    {
      VectorDrawableCompat localVectorDrawableCompat1 = new VectorDrawableCompat();
      mDelegateDrawable = ResourcesCompat.getDrawable(paramResources, paramInt, paramTheme);
      mCachedConstantStateDelegate = new VectorDrawableDelegateState(mDelegateDrawable.getConstantState());
      return localVectorDrawableCompat1;
    }
    try
    {
      localXmlResourceParser = paramResources.getXml(paramInt);
      localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
      int i;
      do
      {
        i = localXmlResourceParser.next();
      } while ((i != 2) && (i != 1));
      if (i != 2) {
        throw new XmlPullParserException("No start tag found");
      }
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      XmlResourceParser localXmlResourceParser;
      AttributeSet localAttributeSet;
      Log.e("VectorDrawableCompat", "parser error", localXmlPullParserException);
      return null;
      VectorDrawableCompat localVectorDrawableCompat2 = createFromXmlInner(paramResources, localXmlResourceParser, localAttributeSet, paramTheme);
      return localVectorDrawableCompat2;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Log.e("VectorDrawableCompat", "parser error", localIOException);
      }
    }
  }
  
  public static VectorDrawableCompat createFromXmlInner(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    VectorDrawableCompat localVectorDrawableCompat = new VectorDrawableCompat();
    localVectorDrawableCompat.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    return localVectorDrawableCompat;
  }
  
  private void inflateInternal(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    VectorDrawableCompatState localVectorDrawableCompatState = mVectorState;
    VPathRenderer localVPathRenderer = mVPathRenderer;
    int i = 1;
    Stack localStack = new Stack();
    localStack.push(mRootGroup);
    int j = paramXmlPullParser.getEventType();
    if (j != 1)
    {
      String str;
      VGroup localVGroup1;
      if (j == 2)
      {
        str = paramXmlPullParser.getName();
        localVGroup1 = (VGroup)localStack.peek();
        if ("path".equals(str))
        {
          VFullPath localVFullPath = new VFullPath();
          localVFullPath.inflate(paramResources, paramAttributeSet, paramTheme, paramXmlPullParser);
          mChildren.add(localVFullPath);
          if (localVFullPath.getPathName() != null) {
            mVGTargetsMap.put(localVFullPath.getPathName(), localVFullPath);
          }
          i = 0;
          mChangingConfigurations |= mChangingConfigurations;
        }
      }
      for (;;)
      {
        j = paramXmlPullParser.next();
        break;
        if ("clip-path".equals(str))
        {
          VClipPath localVClipPath = new VClipPath();
          localVClipPath.inflate(paramResources, paramAttributeSet, paramTheme, paramXmlPullParser);
          mChildren.add(localVClipPath);
          if (localVClipPath.getPathName() != null) {
            mVGTargetsMap.put(localVClipPath.getPathName(), localVClipPath);
          }
          mChangingConfigurations |= mChangingConfigurations;
        }
        else if ("group".equals(str))
        {
          VGroup localVGroup2 = new VGroup();
          localVGroup2.inflate(paramResources, paramAttributeSet, paramTheme, paramXmlPullParser);
          mChildren.add(localVGroup2);
          localStack.push(localVGroup2);
          if (localVGroup2.getGroupName() != null) {
            mVGTargetsMap.put(localVGroup2.getGroupName(), localVGroup2);
          }
          mChangingConfigurations |= mChangingConfigurations;
          continue;
          if ((j == 3) && ("group".equals(paramXmlPullParser.getName()))) {
            localStack.pop();
          }
        }
      }
    }
    if (i != 0)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      if (localStringBuffer.length() > 0) {
        localStringBuffer.append(" or ");
      }
      localStringBuffer.append("path");
      throw new XmlPullParserException("no " + localStringBuffer + " defined");
    }
  }
  
  private boolean needMirroring()
  {
    return false;
  }
  
  private static PorterDuff.Mode parseTintModeCompat(int paramInt, PorterDuff.Mode paramMode)
  {
    switch (paramInt)
    {
    case 4: 
    case 6: 
    case 7: 
    case 8: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    default: 
      return paramMode;
    case 3: 
      return PorterDuff.Mode.SRC_OVER;
    case 5: 
      return PorterDuff.Mode.SRC_IN;
    case 9: 
      return PorterDuff.Mode.SRC_ATOP;
    case 14: 
      return PorterDuff.Mode.MULTIPLY;
    case 15: 
      return PorterDuff.Mode.SCREEN;
    }
    return PorterDuff.Mode.ADD;
  }
  
  private void printGroupTree(VGroup paramVGroup, int paramInt)
  {
    String str = "";
    for (int i = 0; i < paramInt; i++) {
      str = str + "    ";
    }
    Log.v("VectorDrawableCompat", str + "current group is :" + paramVGroup.getGroupName() + " rotation is " + mRotate);
    Log.v("VectorDrawableCompat", str + "matrix is :" + paramVGroup.getLocalMatrix().toString());
    int j = 0;
    if (j < mChildren.size())
    {
      Object localObject = mChildren.get(j);
      if ((localObject instanceof VGroup)) {
        printGroupTree((VGroup)localObject, paramInt + 1);
      }
      for (;;)
      {
        j++;
        break;
        ((VPath)localObject).printVPath(paramInt + 1);
      }
    }
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray, XmlPullParser paramXmlPullParser)
    throws XmlPullParserException
  {
    VectorDrawableCompatState localVectorDrawableCompatState = mVectorState;
    VPathRenderer localVPathRenderer = mVPathRenderer;
    mTintMode = parseTintModeCompat(TypedArrayUtils.getNamedInt(paramTypedArray, paramXmlPullParser, "tintMode", 6, -1), PorterDuff.Mode.SRC_IN);
    ColorStateList localColorStateList = paramTypedArray.getColorStateList(1);
    if (localColorStateList != null) {
      mTint = localColorStateList;
    }
    mAutoMirrored = TypedArrayUtils.getNamedBoolean(paramTypedArray, paramXmlPullParser, "autoMirrored", 5, mAutoMirrored);
    mViewportWidth = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "viewportWidth", 7, mViewportWidth);
    mViewportHeight = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "viewportHeight", 8, mViewportHeight);
    if (mViewportWidth <= 0.0F) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + "<vector> tag requires viewportWidth > 0");
    }
    if (mViewportHeight <= 0.0F) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + "<vector> tag requires viewportHeight > 0");
    }
    mBaseWidth = paramTypedArray.getDimension(3, mBaseWidth);
    mBaseHeight = paramTypedArray.getDimension(2, mBaseHeight);
    if (mBaseWidth <= 0.0F) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + "<vector> tag requires width > 0");
    }
    if (mBaseHeight <= 0.0F) {
      throw new XmlPullParserException(paramTypedArray.getPositionDescription() + "<vector> tag requires height > 0");
    }
    localVPathRenderer.setAlpha(TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "alpha", 4, localVPathRenderer.getAlpha()));
    String str = paramTypedArray.getString(0);
    if (str != null)
    {
      mRootName = str;
      mVGTargetsMap.put(str, localVPathRenderer);
    }
  }
  
  public boolean canApplyTheme()
  {
    if (mDelegateDrawable != null) {
      DrawableCompat.canApplyTheme(mDelegateDrawable);
    }
    return false;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (mDelegateDrawable != null) {
      mDelegateDrawable.draw(paramCanvas);
    }
    Object localObject;
    int k;
    int m;
    do
    {
      do
      {
        return;
        copyBounds(mTmpBounds);
      } while ((mTmpBounds.width() <= 0) || (mTmpBounds.height() <= 0));
      if (mColorFilter != null) {
        break;
      }
      localObject = mTintFilter;
      paramCanvas.getMatrix(mTmpMatrix);
      mTmpMatrix.getValues(mTmpFloats);
      float f1 = Math.abs(mTmpFloats[0]);
      float f2 = Math.abs(mTmpFloats[4]);
      float f3 = Math.abs(mTmpFloats[1]);
      float f4 = Math.abs(mTmpFloats[3]);
      if ((f3 != 0.0F) || (f4 != 0.0F))
      {
        f1 = 1.0F;
        f2 = 1.0F;
      }
      int i = (int)(f1 * mTmpBounds.width());
      int j = (int)(f2 * mTmpBounds.height());
      k = Math.min(2048, i);
      m = Math.min(2048, j);
    } while ((k <= 0) || (m <= 0));
    int n = paramCanvas.save();
    paramCanvas.translate(mTmpBounds.left, mTmpBounds.top);
    if (needMirroring())
    {
      paramCanvas.translate(mTmpBounds.width(), 0.0F);
      paramCanvas.scale(-1.0F, 1.0F);
    }
    mTmpBounds.offsetTo(0, 0);
    mVectorState.createCachedBitmapIfNeeded(k, m);
    if (!mAllowCaching) {
      mVectorState.updateCachedBitmap(k, m);
    }
    for (;;)
    {
      mVectorState.drawCachedBitmapWithRootAlpha(paramCanvas, (ColorFilter)localObject, mTmpBounds);
      paramCanvas.restoreToCount(n);
      return;
      localObject = mColorFilter;
      break;
      if (!mVectorState.canReuseCache())
      {
        mVectorState.updateCachedBitmap(k, m);
        mVectorState.updateCacheStates();
      }
    }
  }
  
  public int getAlpha()
  {
    if (mDelegateDrawable != null) {
      return DrawableCompat.getAlpha(mDelegateDrawable);
    }
    return mVectorState.mVPathRenderer.getRootAlpha();
  }
  
  public int getChangingConfigurations()
  {
    if (mDelegateDrawable != null) {
      return mDelegateDrawable.getChangingConfigurations();
    }
    return super.getChangingConfigurations() | mVectorState.getChangingConfigurations();
  }
  
  public Drawable.ConstantState getConstantState()
  {
    if (mDelegateDrawable != null) {
      return new VectorDrawableDelegateState(mDelegateDrawable.getConstantState());
    }
    mVectorState.mChangingConfigurations = getChangingConfigurations();
    return mVectorState;
  }
  
  public int getIntrinsicHeight()
  {
    if (mDelegateDrawable != null) {
      return mDelegateDrawable.getIntrinsicHeight();
    }
    return (int)mVectorState.mVPathRenderer.mBaseHeight;
  }
  
  public int getIntrinsicWidth()
  {
    if (mDelegateDrawable != null) {
      return mDelegateDrawable.getIntrinsicWidth();
    }
    return (int)mVectorState.mVPathRenderer.mBaseWidth;
  }
  
  public int getOpacity()
  {
    if (mDelegateDrawable != null) {
      return mDelegateDrawable.getOpacity();
    }
    return -3;
  }
  
  public float getPixelSize()
  {
    if (((mVectorState == null) && (mVectorState.mVPathRenderer == null)) || (mVectorState.mVPathRenderer.mBaseWidth == 0.0F) || (mVectorState.mVPathRenderer.mBaseHeight == 0.0F) || (mVectorState.mVPathRenderer.mViewportHeight == 0.0F) || (mVectorState.mVPathRenderer.mViewportWidth == 0.0F)) {
      return 1.0F;
    }
    float f1 = mVectorState.mVPathRenderer.mBaseWidth;
    float f2 = mVectorState.mVPathRenderer.mBaseHeight;
    float f3 = mVectorState.mVPathRenderer.mViewportWidth;
    float f4 = mVectorState.mVPathRenderer.mViewportHeight;
    return Math.min(f3 / f1, f4 / f2);
  }
  
  Object getTargetByName(String paramString)
  {
    return mVectorState.mVPathRenderer.mVGTargetsMap.get(paramString);
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet)
    throws XmlPullParserException, IOException
  {
    if (mDelegateDrawable != null)
    {
      mDelegateDrawable.inflate(paramResources, paramXmlPullParser, paramAttributeSet);
      return;
    }
    inflate(paramResources, paramXmlPullParser, paramAttributeSet, null);
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    if (mDelegateDrawable != null)
    {
      DrawableCompat.inflate(mDelegateDrawable, paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
      return;
    }
    VectorDrawableCompatState localVectorDrawableCompatState = mVectorState;
    mVPathRenderer = new VPathRenderer();
    TypedArray localTypedArray = obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.styleable_VectorDrawableTypeArray);
    updateStateFromTypedArray(localTypedArray, paramXmlPullParser);
    localTypedArray.recycle();
    mChangingConfigurations = getChangingConfigurations();
    mCacheDirty = true;
    inflateInternal(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    mTintFilter = updateTintFilter(mTintFilter, mTint, mTintMode);
  }
  
  public void invalidateSelf()
  {
    if (mDelegateDrawable != null)
    {
      mDelegateDrawable.invalidateSelf();
      return;
    }
    super.invalidateSelf();
  }
  
  public boolean isStateful()
  {
    if (mDelegateDrawable != null) {
      return mDelegateDrawable.isStateful();
    }
    return (super.isStateful()) || ((mVectorState != null) && (mVectorState.mTint != null) && (mVectorState.mTint.isStateful()));
  }
  
  public Drawable mutate()
  {
    if (mDelegateDrawable != null) {
      mDelegateDrawable.mutate();
    }
    while ((mMutated) || (super.mutate() != this)) {
      return this;
    }
    mVectorState = new VectorDrawableCompatState(mVectorState);
    mMutated = true;
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    if (mDelegateDrawable != null) {
      mDelegateDrawable.setBounds(paramRect);
    }
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    if (mDelegateDrawable != null) {
      return mDelegateDrawable.setState(paramArrayOfInt);
    }
    VectorDrawableCompatState localVectorDrawableCompatState = mVectorState;
    if ((mTint != null) && (mTintMode != null))
    {
      mTintFilter = updateTintFilter(mTintFilter, mTint, mTintMode);
      invalidateSelf();
      return true;
    }
    return false;
  }
  
  public void scheduleSelf(Runnable paramRunnable, long paramLong)
  {
    if (mDelegateDrawable != null)
    {
      mDelegateDrawable.scheduleSelf(paramRunnable, paramLong);
      return;
    }
    super.scheduleSelf(paramRunnable, paramLong);
  }
  
  void setAllowCaching(boolean paramBoolean)
  {
    mAllowCaching = paramBoolean;
  }
  
  public void setAlpha(int paramInt)
  {
    if (mDelegateDrawable != null) {
      mDelegateDrawable.setAlpha(paramInt);
    }
    while (mVectorState.mVPathRenderer.getRootAlpha() == paramInt) {
      return;
    }
    mVectorState.mVPathRenderer.setRootAlpha(paramInt);
    invalidateSelf();
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    if (mDelegateDrawable != null)
    {
      mDelegateDrawable.setColorFilter(paramColorFilter);
      return;
    }
    mColorFilter = paramColorFilter;
    invalidateSelf();
  }
  
  public void setTint(int paramInt)
  {
    if (mDelegateDrawable != null)
    {
      DrawableCompat.setTint(mDelegateDrawable, paramInt);
      return;
    }
    setTintList(ColorStateList.valueOf(paramInt));
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    if (mDelegateDrawable != null) {
      DrawableCompat.setTintList(mDelegateDrawable, paramColorStateList);
    }
    VectorDrawableCompatState localVectorDrawableCompatState;
    do
    {
      return;
      localVectorDrawableCompatState = mVectorState;
    } while (mTint == paramColorStateList);
    mTint = paramColorStateList;
    mTintFilter = updateTintFilter(mTintFilter, paramColorStateList, mTintMode);
    invalidateSelf();
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    if (mDelegateDrawable != null) {
      DrawableCompat.setTintMode(mDelegateDrawable, paramMode);
    }
    VectorDrawableCompatState localVectorDrawableCompatState;
    do
    {
      return;
      localVectorDrawableCompatState = mVectorState;
    } while (mTintMode == paramMode);
    mTintMode = paramMode;
    mTintFilter = updateTintFilter(mTintFilter, mTint, paramMode);
    invalidateSelf();
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (mDelegateDrawable != null) {
      return mDelegateDrawable.setVisible(paramBoolean1, paramBoolean2);
    }
    return super.setVisible(paramBoolean1, paramBoolean2);
  }
  
  public void unscheduleSelf(Runnable paramRunnable)
  {
    if (mDelegateDrawable != null)
    {
      mDelegateDrawable.unscheduleSelf(paramRunnable);
      return;
    }
    super.unscheduleSelf(paramRunnable);
  }
  
  PorterDuffColorFilter updateTintFilter(PorterDuffColorFilter paramPorterDuffColorFilter, ColorStateList paramColorStateList, PorterDuff.Mode paramMode)
  {
    if ((paramColorStateList == null) || (paramMode == null)) {
      return null;
    }
    return new PorterDuffColorFilter(paramColorStateList.getColorForState(getState(), 0), paramMode);
  }
  
  private static class VClipPath
    extends VectorDrawableCompat.VPath
  {
    public VClipPath() {}
    
    public VClipPath(VClipPath paramVClipPath)
    {
      super();
    }
    
    private void updateStateFromTypedArray(TypedArray paramTypedArray)
    {
      String str1 = paramTypedArray.getString(0);
      if (str1 != null) {
        mPathName = str1;
      }
      String str2 = paramTypedArray.getString(1);
      if (str2 != null) {
        mNodes = PathParser.createNodesFromPathData(str2);
      }
    }
    
    public void inflate(Resources paramResources, AttributeSet paramAttributeSet, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser)
    {
      if (!TypedArrayUtils.hasAttribute(paramXmlPullParser, "pathData")) {
        return;
      }
      TypedArray localTypedArray = VectorDrawableCommon.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.styleable_VectorDrawableClipPath);
      updateStateFromTypedArray(localTypedArray);
      localTypedArray.recycle();
    }
    
    public boolean isClipPath()
    {
      return true;
    }
  }
  
  private static class VFullPath
    extends VectorDrawableCompat.VPath
  {
    float mFillAlpha = 1.0F;
    int mFillColor = 0;
    int mFillRule;
    float mStrokeAlpha = 1.0F;
    int mStrokeColor = 0;
    Paint.Cap mStrokeLineCap = Paint.Cap.BUTT;
    Paint.Join mStrokeLineJoin = Paint.Join.MITER;
    float mStrokeMiterlimit = 4.0F;
    float mStrokeWidth = 0.0F;
    private int[] mThemeAttrs;
    float mTrimPathEnd = 1.0F;
    float mTrimPathOffset = 0.0F;
    float mTrimPathStart = 0.0F;
    
    public VFullPath() {}
    
    public VFullPath(VFullPath paramVFullPath)
    {
      super();
      mThemeAttrs = mThemeAttrs;
      mStrokeColor = mStrokeColor;
      mStrokeWidth = mStrokeWidth;
      mStrokeAlpha = mStrokeAlpha;
      mFillColor = mFillColor;
      mFillRule = mFillRule;
      mFillAlpha = mFillAlpha;
      mTrimPathStart = mTrimPathStart;
      mTrimPathEnd = mTrimPathEnd;
      mTrimPathOffset = mTrimPathOffset;
      mStrokeLineCap = mStrokeLineCap;
      mStrokeLineJoin = mStrokeLineJoin;
      mStrokeMiterlimit = mStrokeMiterlimit;
    }
    
    private Paint.Cap getStrokeLineCap(int paramInt, Paint.Cap paramCap)
    {
      switch (paramInt)
      {
      default: 
        return paramCap;
      case 0: 
        return Paint.Cap.BUTT;
      case 1: 
        return Paint.Cap.ROUND;
      }
      return Paint.Cap.SQUARE;
    }
    
    private Paint.Join getStrokeLineJoin(int paramInt, Paint.Join paramJoin)
    {
      switch (paramInt)
      {
      default: 
        return paramJoin;
      case 0: 
        return Paint.Join.MITER;
      case 1: 
        return Paint.Join.ROUND;
      }
      return Paint.Join.BEVEL;
    }
    
    private void updateStateFromTypedArray(TypedArray paramTypedArray, XmlPullParser paramXmlPullParser)
    {
      mThemeAttrs = null;
      if (!TypedArrayUtils.hasAttribute(paramXmlPullParser, "pathData")) {
        return;
      }
      String str1 = paramTypedArray.getString(0);
      if (str1 != null) {
        mPathName = str1;
      }
      String str2 = paramTypedArray.getString(2);
      if (str2 != null) {
        mNodes = PathParser.createNodesFromPathData(str2);
      }
      mFillColor = TypedArrayUtils.getNamedColor(paramTypedArray, paramXmlPullParser, "fillColor", 1, mFillColor);
      mFillAlpha = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "fillAlpha", 12, mFillAlpha);
      mStrokeLineCap = getStrokeLineCap(TypedArrayUtils.getNamedInt(paramTypedArray, paramXmlPullParser, "strokeLineCap", 8, -1), mStrokeLineCap);
      mStrokeLineJoin = getStrokeLineJoin(TypedArrayUtils.getNamedInt(paramTypedArray, paramXmlPullParser, "strokeLineJoin", 9, -1), mStrokeLineJoin);
      mStrokeMiterlimit = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "strokeMiterLimit", 10, mStrokeMiterlimit);
      mStrokeColor = TypedArrayUtils.getNamedColor(paramTypedArray, paramXmlPullParser, "strokeColor", 3, mStrokeColor);
      mStrokeAlpha = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "strokeAlpha", 11, mStrokeAlpha);
      mStrokeWidth = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "strokeWidth", 4, mStrokeWidth);
      mTrimPathEnd = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "trimPathEnd", 6, mTrimPathEnd);
      mTrimPathOffset = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "trimPathOffset", 7, mTrimPathOffset);
      mTrimPathStart = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "trimPathStart", 5, mTrimPathStart);
    }
    
    public void applyTheme(Resources.Theme paramTheme)
    {
      if (mThemeAttrs == null) {}
    }
    
    public boolean canApplyTheme()
    {
      return mThemeAttrs != null;
    }
    
    float getFillAlpha()
    {
      return mFillAlpha;
    }
    
    int getFillColor()
    {
      return mFillColor;
    }
    
    float getStrokeAlpha()
    {
      return mStrokeAlpha;
    }
    
    int getStrokeColor()
    {
      return mStrokeColor;
    }
    
    float getStrokeWidth()
    {
      return mStrokeWidth;
    }
    
    float getTrimPathEnd()
    {
      return mTrimPathEnd;
    }
    
    float getTrimPathOffset()
    {
      return mTrimPathOffset;
    }
    
    float getTrimPathStart()
    {
      return mTrimPathStart;
    }
    
    public void inflate(Resources paramResources, AttributeSet paramAttributeSet, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser)
    {
      TypedArray localTypedArray = VectorDrawableCommon.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.styleable_VectorDrawablePath);
      updateStateFromTypedArray(localTypedArray, paramXmlPullParser);
      localTypedArray.recycle();
    }
    
    void setFillAlpha(float paramFloat)
    {
      mFillAlpha = paramFloat;
    }
    
    void setFillColor(int paramInt)
    {
      mFillColor = paramInt;
    }
    
    void setStrokeAlpha(float paramFloat)
    {
      mStrokeAlpha = paramFloat;
    }
    
    void setStrokeColor(int paramInt)
    {
      mStrokeColor = paramInt;
    }
    
    void setStrokeWidth(float paramFloat)
    {
      mStrokeWidth = paramFloat;
    }
    
    void setTrimPathEnd(float paramFloat)
    {
      mTrimPathEnd = paramFloat;
    }
    
    void setTrimPathOffset(float paramFloat)
    {
      mTrimPathOffset = paramFloat;
    }
    
    void setTrimPathStart(float paramFloat)
    {
      mTrimPathStart = paramFloat;
    }
  }
  
  private static class VGroup
  {
    int mChangingConfigurations;
    final ArrayList<Object> mChildren = new ArrayList();
    private String mGroupName = null;
    private final Matrix mLocalMatrix = new Matrix();
    private float mPivotX = 0.0F;
    private float mPivotY = 0.0F;
    float mRotate = 0.0F;
    private float mScaleX = 1.0F;
    private float mScaleY = 1.0F;
    private final Matrix mStackedMatrix = new Matrix();
    private int[] mThemeAttrs;
    private float mTranslateX = 0.0F;
    private float mTranslateY = 0.0F;
    
    public VGroup() {}
    
    public VGroup(VGroup paramVGroup, ArrayMap<String, Object> paramArrayMap)
    {
      mRotate = mRotate;
      mPivotX = mPivotX;
      mPivotY = mPivotY;
      mScaleX = mScaleX;
      mScaleY = mScaleY;
      mTranslateX = mTranslateX;
      mTranslateY = mTranslateY;
      mThemeAttrs = mThemeAttrs;
      mGroupName = mGroupName;
      mChangingConfigurations = mChangingConfigurations;
      if (mGroupName != null) {
        paramArrayMap.put(mGroupName, this);
      }
      mLocalMatrix.set(mLocalMatrix);
      ArrayList localArrayList = mChildren;
      int i = 0;
      while (i < localArrayList.size())
      {
        Object localObject1 = localArrayList.get(i);
        if ((localObject1 instanceof VGroup))
        {
          VGroup localVGroup = (VGroup)localObject1;
          mChildren.add(new VGroup(localVGroup, paramArrayMap));
          i++;
        }
        else
        {
          if ((localObject1 instanceof VectorDrawableCompat.VFullPath)) {}
          for (Object localObject2 = new VectorDrawableCompat.VFullPath((VectorDrawableCompat.VFullPath)localObject1);; localObject2 = new VectorDrawableCompat.VClipPath((VectorDrawableCompat.VClipPath)localObject1))
          {
            mChildren.add(localObject2);
            if (mPathName == null) {
              break;
            }
            paramArrayMap.put(mPathName, localObject2);
            break;
            if (!(localObject1 instanceof VectorDrawableCompat.VClipPath)) {
              break label329;
            }
          }
          label329:
          throw new IllegalStateException("Unknown object in the tree!");
        }
      }
    }
    
    private void updateLocalMatrix()
    {
      mLocalMatrix.reset();
      mLocalMatrix.postTranslate(-mPivotX, -mPivotY);
      mLocalMatrix.postScale(mScaleX, mScaleY);
      mLocalMatrix.postRotate(mRotate, 0.0F, 0.0F);
      mLocalMatrix.postTranslate(mTranslateX + mPivotX, mTranslateY + mPivotY);
    }
    
    private void updateStateFromTypedArray(TypedArray paramTypedArray, XmlPullParser paramXmlPullParser)
    {
      mThemeAttrs = null;
      mRotate = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "rotation", 5, mRotate);
      mPivotX = paramTypedArray.getFloat(1, mPivotX);
      mPivotY = paramTypedArray.getFloat(2, mPivotY);
      mScaleX = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "scaleX", 3, mScaleX);
      mScaleY = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "scaleY", 4, mScaleY);
      mTranslateX = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "translateX", 6, mTranslateX);
      mTranslateY = TypedArrayUtils.getNamedFloat(paramTypedArray, paramXmlPullParser, "translateY", 7, mTranslateY);
      String str = paramTypedArray.getString(0);
      if (str != null) {
        mGroupName = str;
      }
      updateLocalMatrix();
    }
    
    public String getGroupName()
    {
      return mGroupName;
    }
    
    public Matrix getLocalMatrix()
    {
      return mLocalMatrix;
    }
    
    public float getPivotX()
    {
      return mPivotX;
    }
    
    public float getPivotY()
    {
      return mPivotY;
    }
    
    public float getRotation()
    {
      return mRotate;
    }
    
    public float getScaleX()
    {
      return mScaleX;
    }
    
    public float getScaleY()
    {
      return mScaleY;
    }
    
    public float getTranslateX()
    {
      return mTranslateX;
    }
    
    public float getTranslateY()
    {
      return mTranslateY;
    }
    
    public void inflate(Resources paramResources, AttributeSet paramAttributeSet, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser)
    {
      TypedArray localTypedArray = VectorDrawableCommon.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.styleable_VectorDrawableGroup);
      updateStateFromTypedArray(localTypedArray, paramXmlPullParser);
      localTypedArray.recycle();
    }
    
    public void setPivotX(float paramFloat)
    {
      if (paramFloat != mPivotX)
      {
        mPivotX = paramFloat;
        updateLocalMatrix();
      }
    }
    
    public void setPivotY(float paramFloat)
    {
      if (paramFloat != mPivotY)
      {
        mPivotY = paramFloat;
        updateLocalMatrix();
      }
    }
    
    public void setRotation(float paramFloat)
    {
      if (paramFloat != mRotate)
      {
        mRotate = paramFloat;
        updateLocalMatrix();
      }
    }
    
    public void setScaleX(float paramFloat)
    {
      if (paramFloat != mScaleX)
      {
        mScaleX = paramFloat;
        updateLocalMatrix();
      }
    }
    
    public void setScaleY(float paramFloat)
    {
      if (paramFloat != mScaleY)
      {
        mScaleY = paramFloat;
        updateLocalMatrix();
      }
    }
    
    public void setTranslateX(float paramFloat)
    {
      if (paramFloat != mTranslateX)
      {
        mTranslateX = paramFloat;
        updateLocalMatrix();
      }
    }
    
    public void setTranslateY(float paramFloat)
    {
      if (paramFloat != mTranslateY)
      {
        mTranslateY = paramFloat;
        updateLocalMatrix();
      }
    }
  }
  
  private static class VPath
  {
    int mChangingConfigurations;
    protected PathParser.PathDataNode[] mNodes = null;
    String mPathName;
    
    public VPath() {}
    
    public VPath(VPath paramVPath)
    {
      mPathName = mPathName;
      mChangingConfigurations = mChangingConfigurations;
      mNodes = PathParser.deepCopyNodes(mNodes);
    }
    
    public String NodesToString(PathParser.PathDataNode[] paramArrayOfPathDataNode)
    {
      String str = " ";
      for (int i = 0; i < paramArrayOfPathDataNode.length; i++)
      {
        str = str + type + ":";
        float[] arrayOfFloat = params;
        for (int j = 0; j < arrayOfFloat.length; j++) {
          str = str + arrayOfFloat[j] + ",";
        }
      }
      return str;
    }
    
    public void applyTheme(Resources.Theme paramTheme) {}
    
    public boolean canApplyTheme()
    {
      return false;
    }
    
    public PathParser.PathDataNode[] getPathData()
    {
      return mNodes;
    }
    
    public String getPathName()
    {
      return mPathName;
    }
    
    public boolean isClipPath()
    {
      return false;
    }
    
    public void printVPath(int paramInt)
    {
      String str = "";
      for (int i = 0; i < paramInt; i++) {
        str = str + "    ";
      }
      Log.v("VectorDrawableCompat", str + "current path is :" + mPathName + " pathData is " + NodesToString(mNodes));
    }
    
    public void setPathData(PathParser.PathDataNode[] paramArrayOfPathDataNode)
    {
      if (!PathParser.canMorph(mNodes, paramArrayOfPathDataNode))
      {
        mNodes = PathParser.deepCopyNodes(paramArrayOfPathDataNode);
        return;
      }
      PathParser.updateNodes(mNodes, paramArrayOfPathDataNode);
    }
    
    public void toPath(Path paramPath)
    {
      paramPath.reset();
      if (mNodes != null) {
        PathParser.PathDataNode.nodesToPath(mNodes, paramPath);
      }
    }
  }
  
  private static class VPathRenderer
  {
    private static final Matrix IDENTITY_MATRIX = new Matrix();
    float mBaseHeight = 0.0F;
    float mBaseWidth = 0.0F;
    private int mChangingConfigurations;
    private Paint mFillPaint;
    private final Matrix mFinalPathMatrix = new Matrix();
    private final Path mPath;
    private PathMeasure mPathMeasure;
    private final Path mRenderPath;
    int mRootAlpha = 255;
    final VectorDrawableCompat.VGroup mRootGroup;
    String mRootName = null;
    private Paint mStrokePaint;
    final ArrayMap<String, Object> mVGTargetsMap = new ArrayMap();
    float mViewportHeight = 0.0F;
    float mViewportWidth = 0.0F;
    
    public VPathRenderer()
    {
      mRootGroup = new VectorDrawableCompat.VGroup();
      mPath = new Path();
      mRenderPath = new Path();
    }
    
    public VPathRenderer(VPathRenderer paramVPathRenderer)
    {
      mRootGroup = new VectorDrawableCompat.VGroup(mRootGroup, mVGTargetsMap);
      mPath = new Path(mPath);
      mRenderPath = new Path(mRenderPath);
      mBaseWidth = mBaseWidth;
      mBaseHeight = mBaseHeight;
      mViewportWidth = mViewportWidth;
      mViewportHeight = mViewportHeight;
      mChangingConfigurations = mChangingConfigurations;
      mRootAlpha = mRootAlpha;
      mRootName = mRootName;
      if (mRootName != null) {
        mVGTargetsMap.put(mRootName, this);
      }
    }
    
    private static float cross(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      return paramFloat1 * paramFloat4 - paramFloat2 * paramFloat3;
    }
    
    private void drawGroupTree(VectorDrawableCompat.VGroup paramVGroup, Matrix paramMatrix, Canvas paramCanvas, int paramInt1, int paramInt2, ColorFilter paramColorFilter)
    {
      mStackedMatrix.set(paramMatrix);
      mStackedMatrix.preConcat(mLocalMatrix);
      paramCanvas.save();
      int i = 0;
      if (i < mChildren.size())
      {
        Object localObject = mChildren.get(i);
        if ((localObject instanceof VectorDrawableCompat.VGroup)) {
          drawGroupTree((VectorDrawableCompat.VGroup)localObject, mStackedMatrix, paramCanvas, paramInt1, paramInt2, paramColorFilter);
        }
        for (;;)
        {
          i++;
          break;
          if ((localObject instanceof VectorDrawableCompat.VPath)) {
            drawPath(paramVGroup, (VectorDrawableCompat.VPath)localObject, paramCanvas, paramInt1, paramInt2, paramColorFilter);
          }
        }
      }
      paramCanvas.restore();
    }
    
    private void drawPath(VectorDrawableCompat.VGroup paramVGroup, VectorDrawableCompat.VPath paramVPath, Canvas paramCanvas, int paramInt1, int paramInt2, ColorFilter paramColorFilter)
    {
      float f1 = paramInt1 / mViewportWidth;
      float f2 = paramInt2 / mViewportHeight;
      float f3 = Math.min(f1, f2);
      Matrix localMatrix = mStackedMatrix;
      mFinalPathMatrix.set(localMatrix);
      mFinalPathMatrix.postScale(f1, f2);
      float f4 = getMatrixScale(localMatrix);
      if (f4 == 0.0F) {
        return;
      }
      paramVPath.toPath(mPath);
      Path localPath = mPath;
      mRenderPath.reset();
      if (paramVPath.isClipPath())
      {
        mRenderPath.addPath(localPath, mFinalPathMatrix);
        paramCanvas.clipPath(mRenderPath);
        return;
      }
      VectorDrawableCompat.VFullPath localVFullPath = (VectorDrawableCompat.VFullPath)paramVPath;
      float f8;
      float f9;
      if ((mTrimPathStart != 0.0F) || (mTrimPathEnd != 1.0F))
      {
        float f5 = (mTrimPathStart + mTrimPathOffset) % 1.0F;
        float f6 = (mTrimPathEnd + mTrimPathOffset) % 1.0F;
        if (mPathMeasure == null) {
          mPathMeasure = new PathMeasure();
        }
        mPathMeasure.setPath(mPath, false);
        float f7 = mPathMeasure.getLength();
        f8 = f5 * f7;
        f9 = f6 * f7;
        localPath.reset();
        if (f8 <= f9) {
          break label529;
        }
        mPathMeasure.getSegment(f8, f7, localPath, true);
        mPathMeasure.getSegment(0.0F, f9, localPath, true);
      }
      for (;;)
      {
        localPath.rLineTo(0.0F, 0.0F);
        mRenderPath.addPath(localPath, mFinalPathMatrix);
        if (mFillColor != 0)
        {
          if (mFillPaint == null)
          {
            mFillPaint = new Paint();
            mFillPaint.setStyle(Paint.Style.FILL);
            mFillPaint.setAntiAlias(true);
          }
          Paint localPaint2 = mFillPaint;
          localPaint2.setColor(VectorDrawableCompat.applyAlpha(mFillColor, mFillAlpha));
          localPaint2.setColorFilter(paramColorFilter);
          paramCanvas.drawPath(mRenderPath, localPaint2);
        }
        if (mStrokeColor == 0) {
          break;
        }
        if (mStrokePaint == null)
        {
          mStrokePaint = new Paint();
          mStrokePaint.setStyle(Paint.Style.STROKE);
          mStrokePaint.setAntiAlias(true);
        }
        Paint localPaint1 = mStrokePaint;
        if (mStrokeLineJoin != null) {
          localPaint1.setStrokeJoin(mStrokeLineJoin);
        }
        if (mStrokeLineCap != null) {
          localPaint1.setStrokeCap(mStrokeLineCap);
        }
        localPaint1.setStrokeMiter(mStrokeMiterlimit);
        localPaint1.setColor(VectorDrawableCompat.applyAlpha(mStrokeColor, mStrokeAlpha));
        localPaint1.setColorFilter(paramColorFilter);
        localPaint1.setStrokeWidth(f3 * f4 * mStrokeWidth);
        paramCanvas.drawPath(mRenderPath, localPaint1);
        return;
        label529:
        mPathMeasure.getSegment(f8, f9, localPath, true);
      }
    }
    
    private float getMatrixScale(Matrix paramMatrix)
    {
      float[] arrayOfFloat = { 0.0F, 1.0F, 1.0F, 0.0F };
      paramMatrix.mapVectors(arrayOfFloat);
      float f1 = (float)Math.hypot(arrayOfFloat[0], arrayOfFloat[1]);
      float f2 = (float)Math.hypot(arrayOfFloat[2], arrayOfFloat[3]);
      float f3 = cross(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
      float f4 = Math.max(f1, f2);
      boolean bool = f4 < 0.0F;
      float f5 = 0.0F;
      if (bool) {
        f5 = Math.abs(f3) / f4;
      }
      return f5;
    }
    
    public void draw(Canvas paramCanvas, int paramInt1, int paramInt2, ColorFilter paramColorFilter)
    {
      drawGroupTree(mRootGroup, IDENTITY_MATRIX, paramCanvas, paramInt1, paramInt2, paramColorFilter);
    }
    
    public float getAlpha()
    {
      return getRootAlpha() / 255.0F;
    }
    
    public int getRootAlpha()
    {
      return mRootAlpha;
    }
    
    public void setAlpha(float paramFloat)
    {
      setRootAlpha((int)(255.0F * paramFloat));
    }
    
    public void setRootAlpha(int paramInt)
    {
      mRootAlpha = paramInt;
    }
  }
  
  private static class VectorDrawableCompatState
    extends Drawable.ConstantState
  {
    boolean mAutoMirrored;
    boolean mCacheDirty;
    boolean mCachedAutoMirrored;
    Bitmap mCachedBitmap;
    int mCachedRootAlpha;
    int[] mCachedThemeAttrs;
    ColorStateList mCachedTint;
    PorterDuff.Mode mCachedTintMode;
    int mChangingConfigurations;
    Paint mTempPaint;
    ColorStateList mTint = null;
    PorterDuff.Mode mTintMode = VectorDrawableCompat.DEFAULT_TINT_MODE;
    VectorDrawableCompat.VPathRenderer mVPathRenderer;
    
    public VectorDrawableCompatState()
    {
      mVPathRenderer = new VectorDrawableCompat.VPathRenderer();
    }
    
    public VectorDrawableCompatState(VectorDrawableCompatState paramVectorDrawableCompatState)
    {
      if (paramVectorDrawableCompatState != null)
      {
        mChangingConfigurations = mChangingConfigurations;
        mVPathRenderer = new VectorDrawableCompat.VPathRenderer(mVPathRenderer);
        if (mVPathRenderer.mFillPaint != null) {
          VectorDrawableCompat.VPathRenderer.access$002(mVPathRenderer, new Paint(mVPathRenderer.mFillPaint));
        }
        if (mVPathRenderer.mStrokePaint != null) {
          VectorDrawableCompat.VPathRenderer.access$102(mVPathRenderer, new Paint(mVPathRenderer.mStrokePaint));
        }
        mTint = mTint;
        mTintMode = mTintMode;
        mAutoMirrored = mAutoMirrored;
      }
    }
    
    public boolean canReuseBitmap(int paramInt1, int paramInt2)
    {
      return (paramInt1 == mCachedBitmap.getWidth()) && (paramInt2 == mCachedBitmap.getHeight());
    }
    
    public boolean canReuseCache()
    {
      return (!mCacheDirty) && (mCachedTint == mTint) && (mCachedTintMode == mTintMode) && (mCachedAutoMirrored == mAutoMirrored) && (mCachedRootAlpha == mVPathRenderer.getRootAlpha());
    }
    
    public void createCachedBitmapIfNeeded(int paramInt1, int paramInt2)
    {
      if ((mCachedBitmap == null) || (!canReuseBitmap(paramInt1, paramInt2)))
      {
        mCachedBitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
        mCacheDirty = true;
      }
    }
    
    public void drawCachedBitmapWithRootAlpha(Canvas paramCanvas, ColorFilter paramColorFilter, Rect paramRect)
    {
      Paint localPaint = getPaint(paramColorFilter);
      paramCanvas.drawBitmap(mCachedBitmap, null, paramRect, localPaint);
    }
    
    public int getChangingConfigurations()
    {
      return mChangingConfigurations;
    }
    
    public Paint getPaint(ColorFilter paramColorFilter)
    {
      if ((!hasTranslucentRoot()) && (paramColorFilter == null)) {
        return null;
      }
      if (mTempPaint == null)
      {
        mTempPaint = new Paint();
        mTempPaint.setFilterBitmap(true);
      }
      mTempPaint.setAlpha(mVPathRenderer.getRootAlpha());
      mTempPaint.setColorFilter(paramColorFilter);
      return mTempPaint;
    }
    
    public boolean hasTranslucentRoot()
    {
      return mVPathRenderer.getRootAlpha() < 255;
    }
    
    public Drawable newDrawable()
    {
      return new VectorDrawableCompat(this);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new VectorDrawableCompat(this);
    }
    
    public void updateCacheStates()
    {
      mCachedTint = mTint;
      mCachedTintMode = mTintMode;
      mCachedRootAlpha = mVPathRenderer.getRootAlpha();
      mCachedAutoMirrored = mAutoMirrored;
      mCacheDirty = false;
    }
    
    public void updateCachedBitmap(int paramInt1, int paramInt2)
    {
      mCachedBitmap.eraseColor(0);
      Canvas localCanvas = new Canvas(mCachedBitmap);
      mVPathRenderer.draw(localCanvas, paramInt1, paramInt2, null);
    }
  }
  
  private static class VectorDrawableDelegateState
    extends Drawable.ConstantState
  {
    private final Drawable.ConstantState mDelegateState;
    
    public VectorDrawableDelegateState(Drawable.ConstantState paramConstantState)
    {
      mDelegateState = paramConstantState;
    }
    
    public boolean canApplyTheme()
    {
      return mDelegateState.canApplyTheme();
    }
    
    public int getChangingConfigurations()
    {
      return mDelegateState.getChangingConfigurations();
    }
    
    public Drawable newDrawable()
    {
      VectorDrawableCompat localVectorDrawableCompat = new VectorDrawableCompat();
      mDelegateDrawable = ((VectorDrawable)mDelegateState.newDrawable());
      return localVectorDrawableCompat;
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      VectorDrawableCompat localVectorDrawableCompat = new VectorDrawableCompat();
      mDelegateDrawable = ((VectorDrawable)mDelegateState.newDrawable(paramResources));
      return localVectorDrawableCompat;
    }
    
    public Drawable newDrawable(Resources paramResources, Resources.Theme paramTheme)
    {
      VectorDrawableCompat localVectorDrawableCompat = new VectorDrawableCompat();
      mDelegateDrawable = ((VectorDrawable)mDelegateState.newDrawable(paramResources, paramTheme));
      return localVectorDrawableCompat;
    }
  }
}
