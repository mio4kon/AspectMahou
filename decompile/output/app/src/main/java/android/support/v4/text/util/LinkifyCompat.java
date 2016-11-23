package android.support.v4.text.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.PatternsCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LinkifyCompat
{
  private static final Comparator<LinkSpec> COMPARATOR = new Comparator()
  {
    public final int compare(LinkifyCompat.LinkSpec paramAnonymousLinkSpec1, LinkifyCompat.LinkSpec paramAnonymousLinkSpec2)
    {
      if (start < start) {}
      do
      {
        return -1;
        if (start > start) {
          return 1;
        }
        if (end < end) {
          return 1;
        }
      } while (end > end);
      return 0;
    }
  };
  private static final String[] EMPTY_STRING = new String[0];
  
  private LinkifyCompat() {}
  
  private static void addLinkMovementMethod(@NonNull TextView paramTextView)
  {
    MovementMethod localMovementMethod = paramTextView.getMovementMethod();
    if (((localMovementMethod == null) || (!(localMovementMethod instanceof LinkMovementMethod))) && (paramTextView.getLinksClickable())) {
      paramTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
  }
  
  public static final void addLinks(@NonNull TextView paramTextView, @NonNull Pattern paramPattern, @Nullable String paramString)
  {
    addLinks(paramTextView, paramPattern, paramString, null, null, null);
  }
  
  public static final void addLinks(@NonNull TextView paramTextView, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    addLinks(paramTextView, paramPattern, paramString, null, paramMatchFilter, paramTransformFilter);
  }
  
  public static final void addLinks(@NonNull TextView paramTextView, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable String[] paramArrayOfString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    SpannableString localSpannableString = SpannableString.valueOf(paramTextView.getText());
    if (addLinks(localSpannableString, paramPattern, paramString, paramArrayOfString, paramMatchFilter, paramTransformFilter))
    {
      paramTextView.setText(localSpannableString);
      addLinkMovementMethod(paramTextView);
    }
  }
  
  public static final boolean addLinks(@NonNull Spannable paramSpannable, int paramInt)
  {
    if (paramInt == 0) {
      return false;
    }
    URLSpan[] arrayOfURLSpan = (URLSpan[])paramSpannable.getSpans(0, paramSpannable.length(), URLSpan.class);
    for (int i = -1 + arrayOfURLSpan.length; i >= 0; i--) {
      paramSpannable.removeSpan(arrayOfURLSpan[i]);
    }
    if ((paramInt & 0x4) != 0) {
      Linkify.addLinks(paramSpannable, 4);
    }
    ArrayList localArrayList = new ArrayList();
    if ((paramInt & 0x1) != 0) {
      gatherLinks(localArrayList, paramSpannable, PatternsCompat.AUTOLINK_WEB_URL, new String[] { "http://", "https://", "rtsp://" }, Linkify.sUrlMatchFilter, null);
    }
    if ((paramInt & 0x2) != 0) {
      gatherLinks(localArrayList, paramSpannable, PatternsCompat.AUTOLINK_EMAIL_ADDRESS, new String[] { "mailto:" }, null, null);
    }
    if ((paramInt & 0x8) != 0) {
      gatherMapLinks(localArrayList, paramSpannable);
    }
    pruneOverlaps(localArrayList, paramSpannable);
    if (localArrayList.size() == 0) {
      return false;
    }
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      LinkSpec localLinkSpec = (LinkSpec)localIterator.next();
      if (frameworkAddedSpan == null) {
        applyLink(url, start, end, paramSpannable);
      }
    }
    return true;
  }
  
  public static final boolean addLinks(@NonNull Spannable paramSpannable, @NonNull Pattern paramPattern, @Nullable String paramString)
  {
    return addLinks(paramSpannable, paramPattern, paramString, null, null, null);
  }
  
  public static final boolean addLinks(@NonNull Spannable paramSpannable, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    return addLinks(paramSpannable, paramPattern, paramString, null, paramMatchFilter, paramTransformFilter);
  }
  
  public static final boolean addLinks(@NonNull Spannable paramSpannable, @NonNull Pattern paramPattern, @Nullable String paramString, @Nullable String[] paramArrayOfString, @Nullable Linkify.MatchFilter paramMatchFilter, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    if (paramString == null) {
      paramString = "";
    }
    if ((paramArrayOfString == null) || (paramArrayOfString.length < 1)) {
      paramArrayOfString = EMPTY_STRING;
    }
    String[] arrayOfString = new String[1 + paramArrayOfString.length];
    arrayOfString[0] = paramString.toLowerCase(Locale.ROOT);
    int i = 0;
    if (i < paramArrayOfString.length)
    {
      String str1 = paramArrayOfString[i];
      int m = i + 1;
      if (str1 == null) {}
      for (String str2 = "";; str2 = str1.toLowerCase(Locale.ROOT))
      {
        arrayOfString[m] = str2;
        i++;
        break;
      }
    }
    boolean bool1 = false;
    Matcher localMatcher = paramPattern.matcher(paramSpannable);
    while (localMatcher.find())
    {
      int j = localMatcher.start();
      int k = localMatcher.end();
      boolean bool2 = true;
      if (paramMatchFilter != null) {
        bool2 = paramMatchFilter.acceptMatch(paramSpannable, j, k);
      }
      if (bool2)
      {
        applyLink(makeUrl(localMatcher.group(0), arrayOfString, localMatcher, paramTransformFilter), j, k, paramSpannable);
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static final boolean addLinks(@NonNull TextView paramTextView, int paramInt)
  {
    if (paramInt == 0) {}
    SpannableString localSpannableString;
    do
    {
      CharSequence localCharSequence;
      do
      {
        return false;
        localCharSequence = paramTextView.getText();
        if (!(localCharSequence instanceof Spannable)) {
          break;
        }
      } while (!addLinks((Spannable)localCharSequence, paramInt));
      addLinkMovementMethod(paramTextView);
      return true;
      localSpannableString = SpannableString.valueOf(localCharSequence);
    } while (!addLinks(localSpannableString, paramInt));
    addLinkMovementMethod(paramTextView);
    paramTextView.setText(localSpannableString);
    return true;
  }
  
  private static void applyLink(String paramString, int paramInt1, int paramInt2, Spannable paramSpannable)
  {
    paramSpannable.setSpan(new URLSpan(paramString), paramInt1, paramInt2, 33);
  }
  
  private static void gatherLinks(ArrayList<LinkSpec> paramArrayList, Spannable paramSpannable, Pattern paramPattern, String[] paramArrayOfString, Linkify.MatchFilter paramMatchFilter, Linkify.TransformFilter paramTransformFilter)
  {
    Matcher localMatcher = paramPattern.matcher(paramSpannable);
    while (localMatcher.find())
    {
      int i = localMatcher.start();
      int j = localMatcher.end();
      if ((paramMatchFilter == null) || (paramMatchFilter.acceptMatch(paramSpannable, i, j)))
      {
        LinkSpec localLinkSpec = new LinkSpec();
        url = makeUrl(localMatcher.group(0), paramArrayOfString, localMatcher, paramTransformFilter);
        start = i;
        end = j;
        paramArrayList.add(localLinkSpec);
      }
    }
  }
  
  /* Error */
  private static final void gatherMapLinks(ArrayList<LinkSpec> paramArrayList, Spannable paramSpannable)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 231	java/lang/Object:toString	()Ljava/lang/String;
    //   4: astore_2
    //   5: iconst_0
    //   6: istore_3
    //   7: aload_2
    //   8: invokestatic 237	android/webkit/WebView:findAddress	(Ljava/lang/String;)Ljava/lang/String;
    //   11: astore 5
    //   13: aload 5
    //   15: ifnull +123 -> 138
    //   18: aload_2
    //   19: aload 5
    //   21: invokevirtual 241	java/lang/String:indexOf	(Ljava/lang/String;)I
    //   24: istore 6
    //   26: iload 6
    //   28: ifge +4 -> 32
    //   31: return
    //   32: new 147	android/support/v4/text/util/LinkifyCompat$LinkSpec
    //   35: dup
    //   36: invokespecial 219	android/support/v4/text/util/LinkifyCompat$LinkSpec:<init>	()V
    //   39: astore 7
    //   41: iload 6
    //   43: aload 5
    //   45: invokevirtual 242	java/lang/String:length	()I
    //   48: iadd
    //   49: istore 8
    //   51: aload 7
    //   53: iload_3
    //   54: iload 6
    //   56: iadd
    //   57: putfield 159	android/support/v4/text/util/LinkifyCompat$LinkSpec:start	I
    //   60: aload 7
    //   62: iload_3
    //   63: iload 8
    //   65: iadd
    //   66: putfield 162	android/support/v4/text/util/LinkifyCompat$LinkSpec:end	I
    //   69: aload_2
    //   70: iload 8
    //   72: invokevirtual 245	java/lang/String:substring	(I)Ljava/lang/String;
    //   75: astore 9
    //   77: aload 9
    //   79: astore_2
    //   80: iload_3
    //   81: iload 8
    //   83: iadd
    //   84: istore_3
    //   85: aload 5
    //   87: ldc -9
    //   89: invokestatic 253	java/net/URLEncoder:encode	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   92: astore 11
    //   94: aload 7
    //   96: new 255	java/lang/StringBuilder
    //   99: dup
    //   100: invokespecial 256	java/lang/StringBuilder:<init>	()V
    //   103: ldc_w 258
    //   106: invokevirtual 262	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   109: aload 11
    //   111: invokevirtual 262	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   114: invokevirtual 263	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   117: putfield 155	android/support/v4/text/util/LinkifyCompat$LinkSpec:url	Ljava/lang/String;
    //   120: aload_0
    //   121: aload 7
    //   123: invokevirtual 223	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   126: pop
    //   127: goto -120 -> 7
    //   130: astore 4
    //   132: return
    //   133: astore 10
    //   135: goto -128 -> 7
    //   138: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	139	0	paramArrayList	ArrayList<LinkSpec>
    //   0	139	1	paramSpannable	Spannable
    //   4	76	2	localObject	Object
    //   6	79	3	i	int
    //   130	1	4	localUnsupportedOperationException	UnsupportedOperationException
    //   11	75	5	str1	String
    //   24	33	6	j	int
    //   39	83	7	localLinkSpec	LinkSpec
    //   49	35	8	k	int
    //   75	3	9	str2	String
    //   133	1	10	localUnsupportedEncodingException	java.io.UnsupportedEncodingException
    //   92	18	11	str3	String
    // Exception table:
    //   from	to	target	type
    //   7	13	130	java/lang/UnsupportedOperationException
    //   18	26	130	java/lang/UnsupportedOperationException
    //   32	77	130	java/lang/UnsupportedOperationException
    //   85	94	130	java/lang/UnsupportedOperationException
    //   94	127	130	java/lang/UnsupportedOperationException
    //   85	94	133	java/io/UnsupportedEncodingException
  }
  
  private static String makeUrl(@NonNull String paramString, @NonNull String[] paramArrayOfString, Matcher paramMatcher, @Nullable Linkify.TransformFilter paramTransformFilter)
  {
    if (paramTransformFilter != null) {
      paramString = paramTransformFilter.transformUrl(paramMatcher, paramString);
    }
    for (int i = 0;; i++)
    {
      int j = paramArrayOfString.length;
      int k = 0;
      if (i < j)
      {
        String str1 = paramArrayOfString[i];
        int m = paramArrayOfString[i].length();
        if (!paramString.regionMatches(true, 0, str1, 0, m)) {
          continue;
        }
        k = 1;
        String str2 = paramArrayOfString[i];
        int n = paramArrayOfString[i].length();
        if (!paramString.regionMatches(false, 0, str2, 0, n)) {
          paramString = paramArrayOfString[i] + paramString.substring(paramArrayOfString[i].length());
        }
      }
      if ((k == 0) && (paramArrayOfString.length > 0)) {
        paramString = paramArrayOfString[0] + paramString;
      }
      return paramString;
    }
  }
  
  private static final void pruneOverlaps(ArrayList<LinkSpec> paramArrayList, Spannable paramSpannable)
  {
    URLSpan[] arrayOfURLSpan = (URLSpan[])paramSpannable.getSpans(0, paramSpannable.length(), URLSpan.class);
    for (int i = 0; i < arrayOfURLSpan.length; i++)
    {
      LinkSpec localLinkSpec1 = new LinkSpec();
      frameworkAddedSpan = arrayOfURLSpan[i];
      start = paramSpannable.getSpanStart(arrayOfURLSpan[i]);
      end = paramSpannable.getSpanEnd(arrayOfURLSpan[i]);
      paramArrayList.add(localLinkSpec1);
    }
    Collections.sort(paramArrayList, COMPARATOR);
    int j = paramArrayList.size();
    int k = 0;
    while (k < j - 1)
    {
      LinkSpec localLinkSpec2 = (LinkSpec)paramArrayList.get(k);
      LinkSpec localLinkSpec3 = (LinkSpec)paramArrayList.get(k + 1);
      int m = -1;
      if ((start <= start) && (end > start))
      {
        if (end <= end) {
          m = k + 1;
        }
        for (;;)
        {
          if (m == -1) {
            break label294;
          }
          URLSpan localURLSpan = getframeworkAddedSpan;
          if (localURLSpan != null) {
            paramSpannable.removeSpan(localURLSpan);
          }
          paramArrayList.remove(m);
          j--;
          break;
          if (end - start > end - start) {
            m = k + 1;
          } else if (end - start < end - start) {
            m = k;
          }
        }
      }
      label294:
      k++;
    }
  }
  
  private static class LinkSpec
  {
    int end;
    URLSpan frameworkAddedSpan;
    int start;
    String url;
    
    LinkSpec() {}
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface LinkifyMask {}
}
