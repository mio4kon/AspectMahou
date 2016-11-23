package android.support.v4.print;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Builder;
import android.print.PrintAttributes.Margins;
import android.print.PrintAttributes.MediaSize;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentAdapter.LayoutResultCallback;
import android.print.PrintDocumentAdapter.WriteResultCallback;
import android.print.PrintDocumentInfo;
import android.print.PrintDocumentInfo.Builder;
import android.print.PrintManager;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

class PrintHelperKitkat
{
  public static final int COLOR_MODE_COLOR = 2;
  public static final int COLOR_MODE_MONOCHROME = 1;
  private static final String LOG_TAG = "PrintHelperKitkat";
  private static final int MAX_PRINT_SIZE = 3500;
  public static final int ORIENTATION_LANDSCAPE = 1;
  public static final int ORIENTATION_PORTRAIT = 2;
  public static final int SCALE_MODE_FILL = 2;
  public static final int SCALE_MODE_FIT = 1;
  int mColorMode = 2;
  final Context mContext;
  BitmapFactory.Options mDecodeOptions = null;
  protected boolean mIsMinMarginsHandlingCorrect = true;
  private final Object mLock = new Object();
  int mOrientation;
  protected boolean mPrintActivityRespectsOrientation = true;
  int mScaleMode = 2;
  
  PrintHelperKitkat(Context paramContext)
  {
    mContext = paramContext;
  }
  
  private Bitmap convertBitmapForColorMode(Bitmap paramBitmap, int paramInt)
  {
    if (paramInt != 1) {
      return paramBitmap;
    }
    Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    Paint localPaint = new Paint();
    ColorMatrix localColorMatrix = new ColorMatrix();
    localColorMatrix.setSaturation(0.0F);
    localPaint.setColorFilter(new ColorMatrixColorFilter(localColorMatrix));
    localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint);
    localCanvas.setBitmap(null);
    return localBitmap;
  }
  
  private Matrix getMatrix(int paramInt1, int paramInt2, RectF paramRectF, int paramInt3)
  {
    Matrix localMatrix = new Matrix();
    float f1 = paramRectF.width() / paramInt1;
    if (paramInt3 == 2) {}
    for (float f2 = Math.max(f1, paramRectF.height() / paramInt2);; f2 = Math.min(f1, paramRectF.height() / paramInt2))
    {
      localMatrix.postScale(f2, f2);
      localMatrix.postTranslate((paramRectF.width() - f2 * paramInt1) / 2.0F, (paramRectF.height() - f2 * paramInt2) / 2.0F);
      return localMatrix;
    }
  }
  
  private static boolean isPortrait(Bitmap paramBitmap)
  {
    return paramBitmap.getWidth() <= paramBitmap.getHeight();
  }
  
  private Bitmap loadBitmap(Uri paramUri, BitmapFactory.Options paramOptions)
    throws FileNotFoundException
  {
    if ((paramUri == null) || (mContext == null)) {
      throw new IllegalArgumentException("bad argument to loadBitmap");
    }
    localInputStream = null;
    try
    {
      localInputStream = mContext.getContentResolver().openInputStream(paramUri);
      Bitmap localBitmap = BitmapFactory.decodeStream(localInputStream, null, paramOptions);
      if (localInputStream != null) {}
      try
      {
        localInputStream.close();
        return localBitmap;
      }
      catch (IOException localIOException2)
      {
        Log.w("PrintHelperKitkat", "close fail ", localIOException2);
        return localBitmap;
      }
      try
      {
        localInputStream.close();
        throw localObject;
      }
      catch (IOException localIOException1)
      {
        for (;;)
        {
          Log.w("PrintHelperKitkat", "close fail ", localIOException1);
        }
      }
    }
    finally
    {
      if (localInputStream == null) {}
    }
  }
  
  private Bitmap loadConstrainedBitmap(Uri paramUri, int paramInt)
    throws FileNotFoundException
  {
    if ((paramInt <= 0) || (paramUri == null) || (mContext == null)) {
      throw new IllegalArgumentException("bad argument to getScaledBitmap");
    }
    BitmapFactory.Options localOptions1 = new BitmapFactory.Options();
    inJustDecodeBounds = true;
    loadBitmap(paramUri, localOptions1);
    int i = outWidth;
    int j = outHeight;
    if ((i <= 0) || (j <= 0)) {}
    int m;
    do
    {
      return null;
      int k = Math.max(i, j);
      m = 1;
      while (k > paramInt)
      {
        k >>>= 1;
        m <<= 1;
      }
    } while ((m <= 0) || (Math.min(i, j) / m <= 0));
    BitmapFactory.Options localOptions2;
    synchronized (mLock)
    {
      mDecodeOptions = new BitmapFactory.Options();
      mDecodeOptions.inMutable = true;
      mDecodeOptions.inSampleSize = m;
      localOptions2 = mDecodeOptions;
    }
    try
    {
      Bitmap localBitmap = loadBitmap(paramUri, localOptions2);
      synchronized (mLock)
      {
        mDecodeOptions = null;
        return localBitmap;
      }
      localObject2 = finally;
      throw localObject2;
    }
    finally {}
  }
  
  private void writeBitmap(final PrintAttributes paramPrintAttributes, final int paramInt, final Bitmap paramBitmap, final ParcelFileDescriptor paramParcelFileDescriptor, final CancellationSignal paramCancellationSignal, final PrintDocumentAdapter.WriteResultCallback paramWriteResultCallback)
  {
    if (mIsMinMarginsHandlingCorrect) {}
    for (final PrintAttributes localPrintAttributes = paramPrintAttributes;; localPrintAttributes = copyAttributes(paramPrintAttributes).setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0)).build())
    {
      new AsyncTask()
      {
        /* Error */
        protected Throwable doInBackground(Void... paramAnonymousVarArgs)
        {
          // Byte code:
          //   0: aload_0
          //   1: getfield 31	android/support/v4/print/PrintHelperKitkat$2:val$cancellationSignal	Landroid/os/CancellationSignal;
          //   4: invokevirtual 63	android/os/CancellationSignal:isCanceled	()Z
          //   7: ifeq +5 -> 12
          //   10: aconst_null
          //   11: areturn
          //   12: new 65	android/print/pdf/PrintedPdfDocument
          //   15: dup
          //   16: aload_0
          //   17: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   20: getfield 69	android/support/v4/print/PrintHelperKitkat:mContext	Landroid/content/Context;
          //   23: aload_0
          //   24: getfield 33	android/support/v4/print/PrintHelperKitkat$2:val$pdfAttributes	Landroid/print/PrintAttributes;
          //   27: invokespecial 72	android/print/pdf/PrintedPdfDocument:<init>	(Landroid/content/Context;Landroid/print/PrintAttributes;)V
          //   30: astore_3
          //   31: aload_0
          //   32: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   35: aload_0
          //   36: getfield 35	android/support/v4/print/PrintHelperKitkat$2:val$bitmap	Landroid/graphics/Bitmap;
          //   39: aload_0
          //   40: getfield 33	android/support/v4/print/PrintHelperKitkat$2:val$pdfAttributes	Landroid/print/PrintAttributes;
          //   43: invokevirtual 78	android/print/PrintAttributes:getColorMode	()I
          //   46: invokestatic 82	android/support/v4/print/PrintHelperKitkat:access$100	(Landroid/support/v4/print/PrintHelperKitkat;Landroid/graphics/Bitmap;I)Landroid/graphics/Bitmap;
          //   49: astore 4
          //   51: aload_0
          //   52: getfield 31	android/support/v4/print/PrintHelperKitkat$2:val$cancellationSignal	Landroid/os/CancellationSignal;
          //   55: invokevirtual 63	android/os/CancellationSignal:isCanceled	()Z
          //   58: istore 5
          //   60: iload 5
          //   62: ifne +348 -> 410
          //   65: aload_3
          //   66: iconst_1
          //   67: invokevirtual 86	android/print/pdf/PrintedPdfDocument:startPage	(I)Landroid/graphics/pdf/PdfDocument$Page;
          //   70: astore 9
          //   72: aload_0
          //   73: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   76: getfield 90	android/support/v4/print/PrintHelperKitkat:mIsMinMarginsHandlingCorrect	Z
          //   79: ifeq +129 -> 208
          //   82: new 92	android/graphics/RectF
          //   85: dup
          //   86: aload 9
          //   88: invokevirtual 98	android/graphics/pdf/PdfDocument$Page:getInfo	()Landroid/graphics/pdf/PdfDocument$PageInfo;
          //   91: invokevirtual 104	android/graphics/pdf/PdfDocument$PageInfo:getContentRect	()Landroid/graphics/Rect;
          //   94: invokespecial 107	android/graphics/RectF:<init>	(Landroid/graphics/Rect;)V
          //   97: astore 10
          //   99: aload_0
          //   100: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   103: aload 4
          //   105: invokevirtual 112	android/graphics/Bitmap:getWidth	()I
          //   108: aload 4
          //   110: invokevirtual 115	android/graphics/Bitmap:getHeight	()I
          //   113: aload 10
          //   115: aload_0
          //   116: getfield 39	android/support/v4/print/PrintHelperKitkat$2:val$fittingMode	I
          //   119: invokestatic 119	android/support/v4/print/PrintHelperKitkat:access$200	(Landroid/support/v4/print/PrintHelperKitkat;IILandroid/graphics/RectF;I)Landroid/graphics/Matrix;
          //   122: astore 11
          //   124: aload_0
          //   125: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   128: getfield 90	android/support/v4/print/PrintHelperKitkat:mIsMinMarginsHandlingCorrect	Z
          //   131: ifeq +178 -> 309
          //   134: aload 9
          //   136: invokevirtual 123	android/graphics/pdf/PdfDocument$Page:getCanvas	()Landroid/graphics/Canvas;
          //   139: aload 4
          //   141: aload 11
          //   143: aconst_null
          //   144: invokevirtual 129	android/graphics/Canvas:drawBitmap	(Landroid/graphics/Bitmap;Landroid/graphics/Matrix;Landroid/graphics/Paint;)V
          //   147: aload_3
          //   148: aload 9
          //   150: invokevirtual 133	android/print/pdf/PrintedPdfDocument:finishPage	(Landroid/graphics/pdf/PdfDocument$Page;)V
          //   153: aload_0
          //   154: getfield 31	android/support/v4/print/PrintHelperKitkat$2:val$cancellationSignal	Landroid/os/CancellationSignal;
          //   157: invokevirtual 63	android/os/CancellationSignal:isCanceled	()Z
          //   160: istore 12
          //   162: iload 12
          //   164: ifeq +175 -> 339
          //   167: aload_3
          //   168: invokevirtual 136	android/print/pdf/PrintedPdfDocument:close	()V
          //   171: aload_0
          //   172: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   175: astore 15
          //   177: aload 15
          //   179: ifnull +10 -> 189
          //   182: aload_0
          //   183: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   186: invokevirtual 139	android/os/ParcelFileDescriptor:close	()V
          //   189: aload 4
          //   191: aload_0
          //   192: getfield 35	android/support/v4/print/PrintHelperKitkat$2:val$bitmap	Landroid/graphics/Bitmap;
          //   195: if_acmpeq +215 -> 410
          //   198: aload 4
          //   200: invokevirtual 142	android/graphics/Bitmap:recycle	()V
          //   203: aconst_null
          //   204: areturn
          //   205: astore_2
          //   206: aload_2
          //   207: areturn
          //   208: new 65	android/print/pdf/PrintedPdfDocument
          //   211: dup
          //   212: aload_0
          //   213: getfield 29	android/support/v4/print/PrintHelperKitkat$2:this$0	Landroid/support/v4/print/PrintHelperKitkat;
          //   216: getfield 69	android/support/v4/print/PrintHelperKitkat:mContext	Landroid/content/Context;
          //   219: aload_0
          //   220: getfield 37	android/support/v4/print/PrintHelperKitkat$2:val$attributes	Landroid/print/PrintAttributes;
          //   223: invokespecial 72	android/print/pdf/PrintedPdfDocument:<init>	(Landroid/content/Context;Landroid/print/PrintAttributes;)V
          //   226: astore 19
          //   228: aload 19
          //   230: iconst_1
          //   231: invokevirtual 86	android/print/pdf/PrintedPdfDocument:startPage	(I)Landroid/graphics/pdf/PdfDocument$Page;
          //   234: astore 20
          //   236: new 92	android/graphics/RectF
          //   239: dup
          //   240: aload 20
          //   242: invokevirtual 98	android/graphics/pdf/PdfDocument$Page:getInfo	()Landroid/graphics/pdf/PdfDocument$PageInfo;
          //   245: invokevirtual 104	android/graphics/pdf/PdfDocument$PageInfo:getContentRect	()Landroid/graphics/Rect;
          //   248: invokespecial 107	android/graphics/RectF:<init>	(Landroid/graphics/Rect;)V
          //   251: astore 10
          //   253: aload 19
          //   255: aload 20
          //   257: invokevirtual 133	android/print/pdf/PrintedPdfDocument:finishPage	(Landroid/graphics/pdf/PdfDocument$Page;)V
          //   260: aload 19
          //   262: invokevirtual 136	android/print/pdf/PrintedPdfDocument:close	()V
          //   265: goto -166 -> 99
          //   268: astore 6
          //   270: aload_3
          //   271: invokevirtual 136	android/print/pdf/PrintedPdfDocument:close	()V
          //   274: aload_0
          //   275: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   278: astore 7
          //   280: aload 7
          //   282: ifnull +10 -> 292
          //   285: aload_0
          //   286: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   289: invokevirtual 139	android/os/ParcelFileDescriptor:close	()V
          //   292: aload 4
          //   294: aload_0
          //   295: getfield 35	android/support/v4/print/PrintHelperKitkat$2:val$bitmap	Landroid/graphics/Bitmap;
          //   298: if_acmpeq +8 -> 306
          //   301: aload 4
          //   303: invokevirtual 142	android/graphics/Bitmap:recycle	()V
          //   306: aload 6
          //   308: athrow
          //   309: aload 11
          //   311: aload 10
          //   313: getfield 146	android/graphics/RectF:left	F
          //   316: aload 10
          //   318: getfield 149	android/graphics/RectF:top	F
          //   321: invokevirtual 155	android/graphics/Matrix:postTranslate	(FF)Z
          //   324: pop
          //   325: aload 9
          //   327: invokevirtual 123	android/graphics/pdf/PdfDocument$Page:getCanvas	()Landroid/graphics/Canvas;
          //   330: aload 10
          //   332: invokevirtual 159	android/graphics/Canvas:clipRect	(Landroid/graphics/RectF;)Z
          //   335: pop
          //   336: goto -202 -> 134
          //   339: aload_3
          //   340: new 161	java/io/FileOutputStream
          //   343: dup
          //   344: aload_0
          //   345: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   348: invokevirtual 165	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
          //   351: invokespecial 168	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
          //   354: invokevirtual 172	android/print/pdf/PrintedPdfDocument:writeTo	(Ljava/io/OutputStream;)V
          //   357: aload_3
          //   358: invokevirtual 136	android/print/pdf/PrintedPdfDocument:close	()V
          //   361: aload_0
          //   362: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   365: astore 13
          //   367: aload 13
          //   369: ifnull +10 -> 379
          //   372: aload_0
          //   373: getfield 41	android/support/v4/print/PrintHelperKitkat$2:val$fileDescriptor	Landroid/os/ParcelFileDescriptor;
          //   376: invokevirtual 139	android/os/ParcelFileDescriptor:close	()V
          //   379: aload 4
          //   381: aload_0
          //   382: getfield 35	android/support/v4/print/PrintHelperKitkat$2:val$bitmap	Landroid/graphics/Bitmap;
          //   385: if_acmpeq +25 -> 410
          //   388: aload 4
          //   390: invokevirtual 142	android/graphics/Bitmap:recycle	()V
          //   393: aconst_null
          //   394: areturn
          //   395: astore 8
          //   397: goto -105 -> 292
          //   400: astore 14
          //   402: goto -23 -> 379
          //   405: astore 16
          //   407: goto -218 -> 189
          //   410: aconst_null
          //   411: areturn
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	412	0	this	2
          //   0	412	1	paramAnonymousVarArgs	Void[]
          //   205	2	2	localThrowable	Throwable
          //   30	328	3	localPrintedPdfDocument1	android.print.pdf.PrintedPdfDocument
          //   49	340	4	localBitmap	Bitmap
          //   58	3	5	bool1	boolean
          //   268	39	6	localObject	Object
          //   278	3	7	localParcelFileDescriptor1	ParcelFileDescriptor
          //   395	1	8	localIOException1	IOException
          //   70	256	9	localPage1	android.graphics.pdf.PdfDocument.Page
          //   97	234	10	localRectF	RectF
          //   122	188	11	localMatrix	Matrix
          //   160	3	12	bool2	boolean
          //   365	3	13	localParcelFileDescriptor2	ParcelFileDescriptor
          //   400	1	14	localIOException2	IOException
          //   175	3	15	localParcelFileDescriptor3	ParcelFileDescriptor
          //   405	1	16	localIOException3	IOException
          //   226	35	19	localPrintedPdfDocument2	android.print.pdf.PrintedPdfDocument
          //   234	22	20	localPage2	android.graphics.pdf.PdfDocument.Page
          // Exception table:
          //   from	to	target	type
          //   0	10	205	java/lang/Throwable
          //   12	60	205	java/lang/Throwable
          //   167	177	205	java/lang/Throwable
          //   182	189	205	java/lang/Throwable
          //   189	203	205	java/lang/Throwable
          //   270	280	205	java/lang/Throwable
          //   285	292	205	java/lang/Throwable
          //   292	306	205	java/lang/Throwable
          //   306	309	205	java/lang/Throwable
          //   357	367	205	java/lang/Throwable
          //   372	379	205	java/lang/Throwable
          //   379	393	205	java/lang/Throwable
          //   65	99	268	finally
          //   99	134	268	finally
          //   134	162	268	finally
          //   208	265	268	finally
          //   309	336	268	finally
          //   339	357	268	finally
          //   285	292	395	java/io/IOException
          //   372	379	400	java/io/IOException
          //   182	189	405	java/io/IOException
        }
        
        protected void onPostExecute(Throwable paramAnonymousThrowable)
        {
          if (paramCancellationSignal.isCanceled())
          {
            paramWriteResultCallback.onWriteCancelled();
            return;
          }
          if (paramAnonymousThrowable == null)
          {
            PrintDocumentAdapter.WriteResultCallback localWriteResultCallback = paramWriteResultCallback;
            PageRange[] arrayOfPageRange = new PageRange[1];
            arrayOfPageRange[0] = PageRange.ALL_PAGES;
            localWriteResultCallback.onWriteFinished(arrayOfPageRange);
            return;
          }
          Log.e("PrintHelperKitkat", "Error writing printed content", paramAnonymousThrowable);
          paramWriteResultCallback.onWriteFailed(null);
        }
      }.execute(new Void[0]);
      return;
    }
  }
  
  protected PrintAttributes.Builder copyAttributes(PrintAttributes paramPrintAttributes)
  {
    PrintAttributes.Builder localBuilder = new PrintAttributes.Builder().setMediaSize(paramPrintAttributes.getMediaSize()).setResolution(paramPrintAttributes.getResolution()).setMinMargins(paramPrintAttributes.getMinMargins());
    if (paramPrintAttributes.getColorMode() != 0) {
      localBuilder.setColorMode(paramPrintAttributes.getColorMode());
    }
    return localBuilder;
  }
  
  public int getColorMode()
  {
    return mColorMode;
  }
  
  public int getOrientation()
  {
    if (mOrientation == 0) {
      return 1;
    }
    return mOrientation;
  }
  
  public int getScaleMode()
  {
    return mScaleMode;
  }
  
  public void printBitmap(final String paramString, final Bitmap paramBitmap, final OnPrintFinishCallback paramOnPrintFinishCallback)
  {
    if (paramBitmap == null) {
      return;
    }
    final int i = mScaleMode;
    PrintManager localPrintManager = (PrintManager)mContext.getSystemService("print");
    if (isPortrait(paramBitmap)) {}
    for (PrintAttributes.MediaSize localMediaSize = PrintAttributes.MediaSize.UNKNOWN_PORTRAIT;; localMediaSize = PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE)
    {
      PrintAttributes localPrintAttributes = new PrintAttributes.Builder().setMediaSize(localMediaSize).setColorMode(mColorMode).build();
      localPrintManager.print(paramString, new PrintDocumentAdapter()
      {
        private PrintAttributes mAttributes;
        
        public void onFinish()
        {
          if (paramOnPrintFinishCallback != null) {
            paramOnPrintFinishCallback.onFinish();
          }
        }
        
        public void onLayout(PrintAttributes paramAnonymousPrintAttributes1, PrintAttributes paramAnonymousPrintAttributes2, CancellationSignal paramAnonymousCancellationSignal, PrintDocumentAdapter.LayoutResultCallback paramAnonymousLayoutResultCallback, Bundle paramAnonymousBundle)
        {
          int i = 1;
          mAttributes = paramAnonymousPrintAttributes2;
          PrintDocumentInfo localPrintDocumentInfo = new PrintDocumentInfo.Builder(paramString).setContentType(i).setPageCount(i).build();
          if (!paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1)) {}
          for (;;)
          {
            paramAnonymousLayoutResultCallback.onLayoutFinished(localPrintDocumentInfo, i);
            return;
            int j = 0;
          }
        }
        
        public void onWrite(PageRange[] paramAnonymousArrayOfPageRange, ParcelFileDescriptor paramAnonymousParcelFileDescriptor, CancellationSignal paramAnonymousCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramAnonymousWriteResultCallback)
        {
          PrintHelperKitkat.this.writeBitmap(mAttributes, i, paramBitmap, paramAnonymousParcelFileDescriptor, paramAnonymousCancellationSignal, paramAnonymousWriteResultCallback);
        }
      }, localPrintAttributes);
      return;
    }
  }
  
  public void printBitmap(final String paramString, final Uri paramUri, final OnPrintFinishCallback paramOnPrintFinishCallback)
    throws FileNotFoundException
  {
    PrintDocumentAdapter local3 = new PrintDocumentAdapter()
    {
      private PrintAttributes mAttributes;
      Bitmap mBitmap = null;
      AsyncTask<Uri, Boolean, Bitmap> mLoadBitmap;
      
      private void cancelLoad()
      {
        synchronized (mLock)
        {
          if (mDecodeOptions != null)
          {
            mDecodeOptions.requestCancelDecode();
            mDecodeOptions = null;
          }
          return;
        }
      }
      
      public void onFinish()
      {
        super.onFinish();
        cancelLoad();
        if (mLoadBitmap != null) {
          mLoadBitmap.cancel(true);
        }
        if (paramOnPrintFinishCallback != null) {
          paramOnPrintFinishCallback.onFinish();
        }
        if (mBitmap != null)
        {
          mBitmap.recycle();
          mBitmap = null;
        }
      }
      
      public void onLayout(final PrintAttributes paramAnonymousPrintAttributes1, final PrintAttributes paramAnonymousPrintAttributes2, final CancellationSignal paramAnonymousCancellationSignal, final PrintDocumentAdapter.LayoutResultCallback paramAnonymousLayoutResultCallback, Bundle paramAnonymousBundle)
      {
        int i = 1;
        try
        {
          mAttributes = paramAnonymousPrintAttributes2;
          if (paramAnonymousCancellationSignal.isCanceled())
          {
            paramAnonymousLayoutResultCallback.onLayoutCancelled();
            return;
          }
        }
        finally {}
        if (mBitmap != null)
        {
          PrintDocumentInfo localPrintDocumentInfo = new PrintDocumentInfo.Builder(paramString).setContentType(i).setPageCount(i).build();
          if (!paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1)) {}
          for (;;)
          {
            paramAnonymousLayoutResultCallback.onLayoutFinished(localPrintDocumentInfo, i);
            return;
            int j = 0;
          }
        }
        mLoadBitmap = new AsyncTask()
        {
          protected Bitmap doInBackground(Uri... paramAnonymous2VarArgs)
          {
            try
            {
              Bitmap localBitmap = PrintHelperKitkat.this.loadConstrainedBitmap(val$imageFile, 3500);
              return localBitmap;
            }
            catch (FileNotFoundException localFileNotFoundException) {}
            return null;
          }
          
          protected void onCancelled(Bitmap paramAnonymous2Bitmap)
          {
            paramAnonymousLayoutResultCallback.onLayoutCancelled();
            mLoadBitmap = null;
          }
          
          protected void onPostExecute(Bitmap paramAnonymous2Bitmap)
          {
            super.onPostExecute(paramAnonymous2Bitmap);
            if ((paramAnonymous2Bitmap != null) && ((!mPrintActivityRespectsOrientation) || (mOrientation == 0))) {}
            for (;;)
            {
              try
              {
                PrintAttributes.MediaSize localMediaSize = mAttributes.getMediaSize();
                if ((localMediaSize != null) && (localMediaSize.isPortrait() != PrintHelperKitkat.isPortrait(paramAnonymous2Bitmap)))
                {
                  Matrix localMatrix = new Matrix();
                  localMatrix.postRotate(90.0F);
                  int i = paramAnonymous2Bitmap.getWidth();
                  int j = paramAnonymous2Bitmap.getHeight();
                  paramAnonymous2Bitmap = Bitmap.createBitmap(paramAnonymous2Bitmap, 0, 0, i, j, localMatrix, true);
                }
                mBitmap = paramAnonymous2Bitmap;
                if (paramAnonymous2Bitmap == null) {
                  break label195;
                }
                PrintDocumentInfo localPrintDocumentInfo = new PrintDocumentInfo.Builder(val$jobName).setContentType(1).setPageCount(1).build();
                if (!paramAnonymousPrintAttributes2.equals(paramAnonymousPrintAttributes1))
                {
                  bool = true;
                  paramAnonymousLayoutResultCallback.onLayoutFinished(localPrintDocumentInfo, bool);
                  mLoadBitmap = null;
                  return;
                }
              }
              finally {}
              boolean bool = false;
              continue;
              label195:
              paramAnonymousLayoutResultCallback.onLayoutFailed(null);
            }
          }
          
          protected void onPreExecute()
          {
            paramAnonymousCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener()
            {
              public void onCancel()
              {
                PrintHelperKitkat.3.this.cancelLoad();
                cancel(false);
              }
            });
          }
        }.execute(new Uri[0]);
      }
      
      public void onWrite(PageRange[] paramAnonymousArrayOfPageRange, ParcelFileDescriptor paramAnonymousParcelFileDescriptor, CancellationSignal paramAnonymousCancellationSignal, PrintDocumentAdapter.WriteResultCallback paramAnonymousWriteResultCallback)
      {
        PrintHelperKitkat.this.writeBitmap(mAttributes, val$fittingMode, mBitmap, paramAnonymousParcelFileDescriptor, paramAnonymousCancellationSignal, paramAnonymousWriteResultCallback);
      }
    };
    PrintManager localPrintManager = (PrintManager)mContext.getSystemService("print");
    PrintAttributes.Builder localBuilder = new PrintAttributes.Builder();
    localBuilder.setColorMode(mColorMode);
    if ((mOrientation == 1) || (mOrientation == 0)) {
      localBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE);
    }
    for (;;)
    {
      localPrintManager.print(paramString, local3, localBuilder.build());
      return;
      if (mOrientation == 2) {
        localBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_PORTRAIT);
      }
    }
  }
  
  public void setColorMode(int paramInt)
  {
    mColorMode = paramInt;
  }
  
  public void setOrientation(int paramInt)
  {
    mOrientation = paramInt;
  }
  
  public void setScaleMode(int paramInt)
  {
    mScaleMode = paramInt;
  }
  
  public static abstract interface OnPrintFinishCallback
  {
    public abstract void onFinish();
  }
}
