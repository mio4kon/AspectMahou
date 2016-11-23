package android.support.v4.widget;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;

public abstract class CursorAdapter
  extends BaseAdapter
  implements Filterable, CursorFilter.CursorFilterClient
{
  @Deprecated
  public static final int FLAG_AUTO_REQUERY = 1;
  public static final int FLAG_REGISTER_CONTENT_OBSERVER = 2;
  protected boolean mAutoRequery;
  protected ChangeObserver mChangeObserver;
  protected Context mContext;
  protected Cursor mCursor;
  protected CursorFilter mCursorFilter;
  protected DataSetObserver mDataSetObserver;
  protected boolean mDataValid;
  protected FilterQueryProvider mFilterQueryProvider;
  protected int mRowIDColumn;
  
  @Deprecated
  public CursorAdapter(Context paramContext, Cursor paramCursor)
  {
    init(paramContext, paramCursor, 1);
  }
  
  public CursorAdapter(Context paramContext, Cursor paramCursor, int paramInt)
  {
    init(paramContext, paramCursor, paramInt);
  }
  
  public CursorAdapter(Context paramContext, Cursor paramCursor, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 2)
    {
      init(paramContext, paramCursor, i);
      return;
    }
  }
  
  public abstract void bindView(View paramView, Context paramContext, Cursor paramCursor);
  
  public void changeCursor(Cursor paramCursor)
  {
    Cursor localCursor = swapCursor(paramCursor);
    if (localCursor != null) {
      localCursor.close();
    }
  }
  
  public CharSequence convertToString(Cursor paramCursor)
  {
    if (paramCursor == null) {
      return "";
    }
    return paramCursor.toString();
  }
  
  public int getCount()
  {
    if ((mDataValid) && (mCursor != null)) {
      return mCursor.getCount();
    }
    return 0;
  }
  
  public Cursor getCursor()
  {
    return mCursor;
  }
  
  public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    if (mDataValid)
    {
      mCursor.moveToPosition(paramInt);
      if (paramView == null) {}
      for (View localView = newDropDownView(mContext, mCursor, paramViewGroup);; localView = paramView)
      {
        bindView(localView, mContext, mCursor);
        return localView;
      }
    }
    return null;
  }
  
  public Filter getFilter()
  {
    if (mCursorFilter == null) {
      mCursorFilter = new CursorFilter(this);
    }
    return mCursorFilter;
  }
  
  public FilterQueryProvider getFilterQueryProvider()
  {
    return mFilterQueryProvider;
  }
  
  public Object getItem(int paramInt)
  {
    if ((mDataValid) && (mCursor != null))
    {
      mCursor.moveToPosition(paramInt);
      return mCursor;
    }
    return null;
  }
  
  public long getItemId(int paramInt)
  {
    long l = 0L;
    if ((mDataValid) && (mCursor != null) && (mCursor.moveToPosition(paramInt))) {
      l = mCursor.getLong(mRowIDColumn);
    }
    return l;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    if (!mDataValid) {
      throw new IllegalStateException("this should only be called when the cursor is valid");
    }
    if (!mCursor.moveToPosition(paramInt)) {
      throw new IllegalStateException("couldn't move cursor to position " + paramInt);
    }
    if (paramView == null) {}
    for (View localView = newView(mContext, mCursor, paramViewGroup);; localView = paramView)
    {
      bindView(localView, mContext, mCursor);
      return localView;
    }
  }
  
  public boolean hasStableIds()
  {
    return true;
  }
  
  void init(Context paramContext, Cursor paramCursor, int paramInt)
  {
    int i = 1;
    label25:
    int j;
    if ((paramInt & 0x1) == i)
    {
      paramInt |= 0x2;
      mAutoRequery = i;
      if (paramCursor == null) {
        break label141;
      }
      mCursor = paramCursor;
      mDataValid = i;
      mContext = paramContext;
      if (i == 0) {
        break label147;
      }
      j = paramCursor.getColumnIndexOrThrow("_id");
      label56:
      mRowIDColumn = j;
      if ((paramInt & 0x2) != 2) {
        break label153;
      }
      mChangeObserver = new ChangeObserver();
    }
    for (mDataSetObserver = new MyDataSetObserver();; mDataSetObserver = null)
    {
      if (i != 0)
      {
        if (mChangeObserver != null) {
          paramCursor.registerContentObserver(mChangeObserver);
        }
        if (mDataSetObserver != null) {
          paramCursor.registerDataSetObserver(mDataSetObserver);
        }
      }
      return;
      mAutoRequery = false;
      break;
      label141:
      i = 0;
      break label25;
      label147:
      j = -1;
      break label56;
      label153:
      mChangeObserver = null;
    }
  }
  
  @Deprecated
  protected void init(Context paramContext, Cursor paramCursor, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 1;; i = 2)
    {
      init(paramContext, paramCursor, i);
      return;
    }
  }
  
  public View newDropDownView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup)
  {
    return newView(paramContext, paramCursor, paramViewGroup);
  }
  
  public abstract View newView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup);
  
  protected void onContentChanged()
  {
    if ((mAutoRequery) && (mCursor != null) && (!mCursor.isClosed())) {
      mDataValid = mCursor.requery();
    }
  }
  
  public Cursor runQueryOnBackgroundThread(CharSequence paramCharSequence)
  {
    if (mFilterQueryProvider != null) {
      return mFilterQueryProvider.runQuery(paramCharSequence);
    }
    return mCursor;
  }
  
  public void setFilterQueryProvider(FilterQueryProvider paramFilterQueryProvider)
  {
    mFilterQueryProvider = paramFilterQueryProvider;
  }
  
  public Cursor swapCursor(Cursor paramCursor)
  {
    if (paramCursor == mCursor) {
      return null;
    }
    Cursor localCursor = mCursor;
    if (localCursor != null)
    {
      if (mChangeObserver != null) {
        localCursor.unregisterContentObserver(mChangeObserver);
      }
      if (mDataSetObserver != null) {
        localCursor.unregisterDataSetObserver(mDataSetObserver);
      }
    }
    mCursor = paramCursor;
    if (paramCursor != null)
    {
      if (mChangeObserver != null) {
        paramCursor.registerContentObserver(mChangeObserver);
      }
      if (mDataSetObserver != null) {
        paramCursor.registerDataSetObserver(mDataSetObserver);
      }
      mRowIDColumn = paramCursor.getColumnIndexOrThrow("_id");
      mDataValid = true;
      notifyDataSetChanged();
      return localCursor;
    }
    mRowIDColumn = -1;
    mDataValid = false;
    notifyDataSetInvalidated();
    return localCursor;
  }
  
  private class ChangeObserver
    extends ContentObserver
  {
    ChangeObserver()
    {
      super();
    }
    
    public boolean deliverSelfNotifications()
    {
      return true;
    }
    
    public void onChange(boolean paramBoolean)
    {
      onContentChanged();
    }
  }
  
  private class MyDataSetObserver
    extends DataSetObserver
  {
    MyDataSetObserver() {}
    
    public void onChanged()
    {
      mDataValid = true;
      notifyDataSetChanged();
    }
    
    public void onInvalidated()
    {
      mDataValid = false;
      notifyDataSetInvalidated();
    }
  }
}
