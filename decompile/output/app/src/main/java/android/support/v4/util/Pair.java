package android.support.v4.util;

public class Pair<F, S>
{
  public final F first;
  public final S second;
  
  public Pair(F paramF, S paramS)
  {
    first = paramF;
    second = paramS;
  }
  
  public static <A, B> Pair<A, B> create(A paramA, B paramB)
  {
    return new Pair(paramA, paramB);
  }
  
  private static boolean objectsEqual(Object paramObject1, Object paramObject2)
  {
    return (paramObject1 == paramObject2) || ((paramObject1 != null) && (paramObject1.equals(paramObject2)));
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Pair)) {}
    Pair localPair;
    do
    {
      return false;
      localPair = (Pair)paramObject;
    } while ((!objectsEqual(first, first)) || (!objectsEqual(second, second)));
    return true;
  }
  
  public int hashCode()
  {
    int i;
    int j;
    if (first == null)
    {
      i = 0;
      Object localObject = second;
      j = 0;
      if (localObject != null) {
        break label35;
      }
    }
    for (;;)
    {
      return i ^ j;
      i = first.hashCode();
      break;
      label35:
      j = second.hashCode();
    }
  }
}
