package android.support.v4.widget;

import android.database.Cursor;
import android.widget.Filter;
import android.widget.Filter.FilterResults;

class CursorFilter
  extends Filter
{
  CursorFilterClient mClient;
  
  CursorFilter(CursorFilterClient paramCursorFilterClient)
  {
    mClient = paramCursorFilterClient;
  }
  
  public CharSequence convertResultToString(Object paramObject)
  {
    return mClient.convertToString((Cursor)paramObject);
  }
  
  protected Filter.FilterResults performFiltering(CharSequence paramCharSequence)
  {
    Cursor localCursor = mClient.runQueryOnBackgroundThread(paramCharSequence);
    Filter.FilterResults localFilterResults = new Filter.FilterResults();
    if (localCursor != null)
    {
      count = localCursor.getCount();
      values = localCursor;
      return localFilterResults;
    }
    count = 0;
    values = null;
    return localFilterResults;
  }
  
  protected void publishResults(CharSequence paramCharSequence, Filter.FilterResults paramFilterResults)
  {
    Cursor localCursor = mClient.getCursor();
    if ((values != null) && (values != localCursor)) {
      mClient.changeCursor((Cursor)values);
    }
  }
  
  static abstract interface CursorFilterClient
  {
    public abstract void changeCursor(Cursor paramCursor);
    
    public abstract CharSequence convertToString(Cursor paramCursor);
    
    public abstract Cursor getCursor();
    
    public abstract Cursor runQueryOnBackgroundThread(CharSequence paramCharSequence);
  }
}
