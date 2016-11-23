package android.support.v4.provider;

import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class RawDocumentFile
  extends DocumentFile
{
  private File mFile;
  
  RawDocumentFile(DocumentFile paramDocumentFile, File paramFile)
  {
    super(paramDocumentFile);
    mFile = paramFile;
  }
  
  private static boolean deleteContents(File paramFile)
  {
    File[] arrayOfFile = paramFile.listFiles();
    boolean bool = true;
    if (arrayOfFile != null)
    {
      int i = arrayOfFile.length;
      for (int j = 0; j < i; j++)
      {
        File localFile = arrayOfFile[j];
        if (localFile.isDirectory()) {
          bool &= deleteContents(localFile);
        }
        if (!localFile.delete())
        {
          Log.w("DocumentFile", "Failed to delete " + localFile);
          bool = false;
        }
      }
    }
    return bool;
  }
  
  private static String getTypeForName(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    if (i >= 0)
    {
      String str1 = paramString.substring(i + 1).toLowerCase();
      String str2 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str1);
      if (str2 != null) {
        return str2;
      }
    }
    return "application/octet-stream";
  }
  
  public boolean canRead()
  {
    return mFile.canRead();
  }
  
  public boolean canWrite()
  {
    return mFile.canWrite();
  }
  
  public DocumentFile createDirectory(String paramString)
  {
    File localFile = new File(mFile, paramString);
    if ((localFile.isDirectory()) || (localFile.mkdir())) {
      return new RawDocumentFile(this, localFile);
    }
    return null;
  }
  
  public DocumentFile createFile(String paramString1, String paramString2)
  {
    String str = MimeTypeMap.getSingleton().getExtensionFromMimeType(paramString1);
    if (str != null) {
      paramString2 = paramString2 + "." + str;
    }
    File localFile = new File(mFile, paramString2);
    try
    {
      localFile.createNewFile();
      RawDocumentFile localRawDocumentFile = new RawDocumentFile(this, localFile);
      return localRawDocumentFile;
    }
    catch (IOException localIOException)
    {
      Log.w("DocumentFile", "Failed to createFile: " + localIOException);
    }
    return null;
  }
  
  public boolean delete()
  {
    deleteContents(mFile);
    return mFile.delete();
  }
  
  public boolean exists()
  {
    return mFile.exists();
  }
  
  public String getName()
  {
    return mFile.getName();
  }
  
  public String getType()
  {
    if (mFile.isDirectory()) {
      return null;
    }
    return getTypeForName(mFile.getName());
  }
  
  public Uri getUri()
  {
    return Uri.fromFile(mFile);
  }
  
  public boolean isDirectory()
  {
    return mFile.isDirectory();
  }
  
  public boolean isFile()
  {
    return mFile.isFile();
  }
  
  public long lastModified()
  {
    return mFile.lastModified();
  }
  
  public long length()
  {
    return mFile.length();
  }
  
  public DocumentFile[] listFiles()
  {
    ArrayList localArrayList = new ArrayList();
    File[] arrayOfFile = mFile.listFiles();
    if (arrayOfFile != null)
    {
      int i = arrayOfFile.length;
      for (int j = 0; j < i; j++) {
        localArrayList.add(new RawDocumentFile(this, arrayOfFile[j]));
      }
    }
    return (DocumentFile[])localArrayList.toArray(new DocumentFile[localArrayList.size()]);
  }
  
  public boolean renameTo(String paramString)
  {
    File localFile = new File(mFile.getParentFile(), paramString);
    if (mFile.renameTo(localFile))
    {
      mFile = localFile;
      return true;
    }
    return false;
  }
}
