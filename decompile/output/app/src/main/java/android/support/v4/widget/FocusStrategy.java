package android.support.v4.widget;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class FocusStrategy
{
  FocusStrategy() {}
  
  private static boolean beamBeats(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2, @NonNull Rect paramRect3)
  {
    boolean bool1 = true;
    boolean bool2 = beamsOverlap(paramInt, paramRect1, paramRect2);
    if ((beamsOverlap(paramInt, paramRect1, paramRect3)) || (!bool2)) {
      bool1 = false;
    }
    while ((!isToDirectionOf(paramInt, paramRect1, paramRect3)) || (paramInt == 17) || (paramInt == 66) || (majorAxisDistance(paramInt, paramRect1, paramRect2) < majorAxisDistanceToFarEdge(paramInt, paramRect1, paramRect3))) {
      return bool1;
    }
    return false;
  }
  
  private static boolean beamsOverlap(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
    case 66: 
      if ((bottom < top) || (top > bottom)) {
        break;
      }
    }
    do
    {
      return true;
      return false;
    } while ((right >= left) && (left <= right));
    return false;
  }
  
  public static <L, T> T findNextFocusInAbsoluteDirection(@NonNull L paramL, @NonNull CollectionAdapter<L, T> paramCollectionAdapter, @NonNull BoundsAdapter<T> paramBoundsAdapter, @Nullable T paramT, @NonNull Rect paramRect, int paramInt)
  {
    Rect localRect1 = new Rect(paramRect);
    Object localObject1;
    Rect localRect2;
    int j;
    label103:
    Object localObject2;
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
      localRect1.offset(1 + paramRect.width(), 0);
      localObject1 = null;
      int i = paramCollectionAdapter.size(paramL);
      localRect2 = new Rect();
      j = 0;
      if (j >= i) {
        break label221;
      }
      localObject2 = paramCollectionAdapter.get(paramL, j);
      if (localObject2 != paramT) {
        break;
      }
    }
    for (;;)
    {
      j++;
      break label103;
      localRect1.offset(-(1 + paramRect.width()), 0);
      break;
      localRect1.offset(0, 1 + paramRect.height());
      break;
      localRect1.offset(0, -(1 + paramRect.height()));
      break;
      paramBoundsAdapter.obtainBounds(localObject2, localRect2);
      if (isBetterCandidate(paramInt, paramRect, localRect2, localRect1))
      {
        localRect1.set(localRect2);
        localObject1 = localObject2;
      }
    }
    label221:
    return localObject1;
  }
  
  public static <L, T> T findNextFocusInRelativeDirection(@NonNull L paramL, @NonNull CollectionAdapter<L, T> paramCollectionAdapter, @NonNull BoundsAdapter<T> paramBoundsAdapter, @Nullable T paramT, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = paramCollectionAdapter.size(paramL);
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++) {
      localArrayList.add(paramCollectionAdapter.get(paramL, j));
    }
    Collections.sort(localArrayList, new SequentialComparator(paramBoolean1, paramBoundsAdapter));
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_FORWARD, FOCUS_BACKWARD}.");
    case 2: 
      return getNextFocusable(paramT, localArrayList, paramBoolean2);
    }
    return getPreviousFocusable(paramT, localArrayList, paramBoolean2);
  }
  
  private static <T> T getNextFocusable(T paramT, ArrayList<T> paramArrayList, boolean paramBoolean)
  {
    int i = paramArrayList.size();
    if (paramT == null) {}
    for (int j = -1;; j = paramArrayList.lastIndexOf(paramT))
    {
      int k = j + 1;
      if (k >= i) {
        break;
      }
      return paramArrayList.get(k);
    }
    if ((paramBoolean) && (i > 0)) {
      return paramArrayList.get(0);
    }
    return null;
  }
  
  private static <T> T getPreviousFocusable(T paramT, ArrayList<T> paramArrayList, boolean paramBoolean)
  {
    int i = paramArrayList.size();
    if (paramT == null) {}
    for (int j = i;; j = paramArrayList.indexOf(paramT))
    {
      int k = j - 1;
      if (k < 0) {
        break;
      }
      return paramArrayList.get(k);
    }
    if ((paramBoolean) && (i > 0)) {
      return paramArrayList.get(i - 1);
    }
    return null;
  }
  
  private static int getWeightedDistanceFor(int paramInt1, int paramInt2)
  {
    return paramInt1 * (paramInt1 * 13) + paramInt2 * paramInt2;
  }
  
  private static boolean isBetterCandidate(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2, @NonNull Rect paramRect3)
  {
    boolean bool = true;
    if (!isCandidate(paramRect1, paramRect2, paramInt)) {
      bool = false;
    }
    do
    {
      do
      {
        return bool;
      } while ((!isCandidate(paramRect1, paramRect3, paramInt)) || (beamBeats(paramInt, paramRect1, paramRect2, paramRect3)));
      if (beamBeats(paramInt, paramRect1, paramRect3, paramRect2)) {
        return false;
      }
    } while (getWeightedDistanceFor(majorAxisDistance(paramInt, paramRect1, paramRect2), minorAxisDistance(paramInt, paramRect1, paramRect2)) < getWeightedDistanceFor(majorAxisDistance(paramInt, paramRect1, paramRect3), minorAxisDistance(paramInt, paramRect1, paramRect3)));
    return false;
  }
  
  private static boolean isCandidate(@NonNull Rect paramRect1, @NonNull Rect paramRect2, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
      if (((right <= right) && (left < right)) || (left <= left)) {
        break;
      }
    }
    do
    {
      do
      {
        do
        {
          return true;
          return false;
        } while (((left < left) || (right <= left)) && (right < right));
        return false;
      } while (((bottom > bottom) || (top >= bottom)) && (top > top));
      return false;
    } while (((top < top) || (bottom <= top)) && (bottom < bottom));
    return false;
  }
  
  private static boolean isToDirectionOf(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
      if (left < right) {
        break;
      }
    }
    do
    {
      do
      {
        do
        {
          return true;
          return false;
        } while (right <= left);
        return false;
      } while (top >= bottom);
      return false;
    } while (bottom <= top);
    return false;
  }
  
  private static int majorAxisDistance(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2)
  {
    return Math.max(0, majorAxisDistanceRaw(paramInt, paramRect1, paramRect2));
  }
  
  private static int majorAxisDistanceRaw(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
      return left - right;
    case 66: 
      return left - right;
    case 33: 
      return top - bottom;
    }
    return top - bottom;
  }
  
  private static int majorAxisDistanceToFarEdge(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2)
  {
    return Math.max(1, majorAxisDistanceToFarEdgeRaw(paramInt, paramRect1, paramRect2));
  }
  
  private static int majorAxisDistanceToFarEdgeRaw(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
      return left - left;
    case 66: 
      return right - right;
    case 33: 
      return top - top;
    }
    return bottom - bottom;
  }
  
  private static int minorAxisDistance(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
    case 66: 
      return Math.abs(top + paramRect1.height() / 2 - (top + paramRect2.height() / 2));
    }
    return Math.abs(left + paramRect1.width() / 2 - (left + paramRect2.width() / 2));
  }
  
  public static abstract interface BoundsAdapter<T>
  {
    public abstract void obtainBounds(T paramT, Rect paramRect);
  }
  
  public static abstract interface CollectionAdapter<T, V>
  {
    public abstract V get(T paramT, int paramInt);
    
    public abstract int size(T paramT);
  }
  
  private static class SequentialComparator<T>
    implements Comparator<T>
  {
    private final FocusStrategy.BoundsAdapter<T> mAdapter;
    private final boolean mIsLayoutRtl;
    private final Rect mTemp1 = new Rect();
    private final Rect mTemp2 = new Rect();
    
    SequentialComparator(boolean paramBoolean, FocusStrategy.BoundsAdapter<T> paramBoundsAdapter)
    {
      mIsLayoutRtl = paramBoolean;
      mAdapter = paramBoundsAdapter;
    }
    
    public int compare(T paramT1, T paramT2)
    {
      int i = 1;
      Rect localRect1 = mTemp1;
      Rect localRect2 = mTemp2;
      mAdapter.obtainBounds(paramT1, localRect1);
      mAdapter.obtainBounds(paramT2, localRect2);
      if (top < top) {}
      do
      {
        do
        {
          do
          {
            return -1;
            if (top > top) {
              return i;
            }
            if (left < left)
            {
              if (mIsLayoutRtl) {}
              for (;;)
              {
                return i;
                i = -1;
              }
            }
            if (left <= left) {
              break;
            }
          } while (mIsLayoutRtl);
          return i;
        } while (bottom < bottom);
        if (bottom > bottom) {
          return i;
        }
        if (right < right)
        {
          if (mIsLayoutRtl) {}
          for (;;)
          {
            return i;
            i = -1;
          }
        }
        if (right <= right) {
          break;
        }
      } while (mIsLayoutRtl);
      return i;
      return 0;
    }
  }
}
