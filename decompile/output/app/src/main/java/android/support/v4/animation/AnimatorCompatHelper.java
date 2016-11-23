package android.support.v4.animation;

import android.os.Build.VERSION;
import android.view.View;

public final class AnimatorCompatHelper
{
  private static final AnimatorProvider IMPL = new GingerbreadAnimatorCompatProvider();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 12)
    {
      IMPL = new HoneycombMr1AnimatorCompatProvider();
      return;
    }
  }
  
  private AnimatorCompatHelper() {}
  
  public static void clearInterpolator(View paramView)
  {
    IMPL.clearInterpolator(paramView);
  }
  
  public static ValueAnimatorCompat emptyValueAnimator()
  {
    return IMPL.emptyValueAnimator();
  }
}
