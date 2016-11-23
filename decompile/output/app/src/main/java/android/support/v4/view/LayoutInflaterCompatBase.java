package android.support.v4.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;

class LayoutInflaterCompatBase
{
  LayoutInflaterCompatBase() {}
  
  static LayoutInflaterFactory getFactory(LayoutInflater paramLayoutInflater)
  {
    LayoutInflater.Factory localFactory = paramLayoutInflater.getFactory();
    if ((localFactory instanceof FactoryWrapper)) {
      return mDelegateFactory;
    }
    return null;
  }
  
  static void setFactory(LayoutInflater paramLayoutInflater, LayoutInflaterFactory paramLayoutInflaterFactory)
  {
    if (paramLayoutInflaterFactory != null) {}
    for (FactoryWrapper localFactoryWrapper = new FactoryWrapper(paramLayoutInflaterFactory);; localFactoryWrapper = null)
    {
      paramLayoutInflater.setFactory(localFactoryWrapper);
      return;
    }
  }
  
  static class FactoryWrapper
    implements LayoutInflater.Factory
  {
    final LayoutInflaterFactory mDelegateFactory;
    
    FactoryWrapper(LayoutInflaterFactory paramLayoutInflaterFactory)
    {
      mDelegateFactory = paramLayoutInflaterFactory;
    }
    
    public View onCreateView(String paramString, Context paramContext, AttributeSet paramAttributeSet)
    {
      return mDelegateFactory.onCreateView(null, paramString, paramContext, paramAttributeSet);
    }
    
    public String toString()
    {
      return getClass().getName() + "{" + mDelegateFactory + "}";
    }
  }
}
