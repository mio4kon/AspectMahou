package android.support.v4.provider;

import android.content.Context;
import android.net.Uri;

class TreeDocumentFile
  extends DocumentFile
{
  private Context mContext;
  private Uri mUri;
  
  TreeDocumentFile(DocumentFile paramDocumentFile, Context paramContext, Uri paramUri)
  {
    super(paramDocumentFile);
    mContext = paramContext;
    mUri = paramUri;
  }
  
  public boolean canRead()
  {
    return DocumentsContractApi19.canRead(mContext, mUri);
  }
  
  public boolean canWrite()
  {
    return DocumentsContractApi19.canWrite(mContext, mUri);
  }
  
  public DocumentFile createDirectory(String paramString)
  {
    Uri localUri = DocumentsContractApi21.createDirectory(mContext, mUri, paramString);
    if (localUri != null) {
      return new TreeDocumentFile(this, mContext, localUri);
    }
    return null;
  }
  
  public DocumentFile createFile(String paramString1, String paramString2)
  {
    Uri localUri = DocumentsContractApi21.createFile(mContext, mUri, paramString1, paramString2);
    if (localUri != null) {
      return new TreeDocumentFile(this, mContext, localUri);
    }
    return null;
  }
  
  public boolean delete()
  {
    return DocumentsContractApi19.delete(mContext, mUri);
  }
  
  public boolean exists()
  {
    return DocumentsContractApi19.exists(mContext, mUri);
  }
  
  public String getName()
  {
    return DocumentsContractApi19.getName(mContext, mUri);
  }
  
  public String getType()
  {
    return DocumentsContractApi19.getType(mContext, mUri);
  }
  
  public Uri getUri()
  {
    return mUri;
  }
  
  public boolean isDirectory()
  {
    return DocumentsContractApi19.isDirectory(mContext, mUri);
  }
  
  public boolean isFile()
  {
    return DocumentsContractApi19.isFile(mContext, mUri);
  }
  
  public long lastModified()
  {
    return DocumentsContractApi19.lastModified(mContext, mUri);
  }
  
  public long length()
  {
    return DocumentsContractApi19.length(mContext, mUri);
  }
  
  public DocumentFile[] listFiles()
  {
    Uri[] arrayOfUri = DocumentsContractApi21.listFiles(mContext, mUri);
    DocumentFile[] arrayOfDocumentFile = new DocumentFile[arrayOfUri.length];
    for (int i = 0; i < arrayOfUri.length; i++) {
      arrayOfDocumentFile[i] = new TreeDocumentFile(this, mContext, arrayOfUri[i]);
    }
    return arrayOfDocumentFile;
  }
  
  public boolean renameTo(String paramString)
  {
    Uri localUri = DocumentsContractApi21.renameTo(mContext, mUri, paramString);
    if (localUri != null)
    {
      mUri = localUri;
      return true;
    }
    return false;
  }
}
