package android.support.v7.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuItemWrapperICS;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SupportMenuInflater
  extends MenuInflater
{
  static final Class<?>[] ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE = ACTION_VIEW_CONSTRUCTOR_SIGNATURE;
  static final Class<?>[] ACTION_VIEW_CONSTRUCTOR_SIGNATURE = { Context.class };
  static final String LOG_TAG = "SupportMenuInflater";
  static final int NO_ID = 0;
  private static final String XML_GROUP = "group";
  private static final String XML_ITEM = "item";
  private static final String XML_MENU = "menu";
  final Object[] mActionProviderConstructorArguments;
  final Object[] mActionViewConstructorArguments;
  Context mContext;
  private Object mRealOwner;
  
  public SupportMenuInflater(Context paramContext)
  {
    super(paramContext);
    mContext = paramContext;
    mActionViewConstructorArguments = new Object[] { paramContext };
    mActionProviderConstructorArguments = mActionViewConstructorArguments;
  }
  
  private Object findRealOwner(Object paramObject)
  {
    if ((paramObject instanceof Activity)) {}
    while (!(paramObject instanceof ContextWrapper)) {
      return paramObject;
    }
    return findRealOwner(((ContextWrapper)paramObject).getBaseContext());
  }
  
  private void parseMenu(XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Menu paramMenu)
    throws XmlPullParserException, IOException
  {
    MenuState localMenuState = new MenuState(paramMenu);
    int i = paramXmlPullParser.getEventType();
    int j = 0;
    Object localObject = null;
    String str3;
    label57:
    int k;
    if (i == 2)
    {
      str3 = paramXmlPullParser.getName();
      if (str3.equals("menu"))
      {
        i = paramXmlPullParser.next();
        k = 0;
        label60:
        if (k != 0) {
          return;
        }
      }
    }
    switch (i)
    {
    default: 
    case 2: 
    case 3: 
      for (;;)
      {
        i = paramXmlPullParser.next();
        break label60;
        throw new RuntimeException("Expecting menu, got " + str3);
        i = paramXmlPullParser.next();
        if (i != 1) {
          break;
        }
        break label57;
        if (j == 0)
        {
          String str2 = paramXmlPullParser.getName();
          if (str2.equals("group"))
          {
            localMenuState.readGroup(paramAttributeSet);
          }
          else if (str2.equals("item"))
          {
            localMenuState.readItem(paramAttributeSet);
          }
          else if (str2.equals("menu"))
          {
            parseMenu(paramXmlPullParser, paramAttributeSet, localMenuState.addSubMenuItem());
          }
          else
          {
            j = 1;
            localObject = str2;
            continue;
            String str1 = paramXmlPullParser.getName();
            if ((j != 0) && (str1.equals(localObject)))
            {
              j = 0;
              localObject = null;
            }
            else if (str1.equals("group"))
            {
              localMenuState.resetGroup();
            }
            else if (str1.equals("item"))
            {
              if (!localMenuState.hasAddedItem()) {
                if ((itemActionProvider != null) && (itemActionProvider.hasSubMenu())) {
                  localMenuState.addSubMenuItem();
                } else {
                  localMenuState.addItem();
                }
              }
            }
            else if (str1.equals("menu"))
            {
              k = 1;
            }
          }
        }
      }
    }
    throw new RuntimeException("Unexpected end of document");
  }
  
  Object getRealOwner()
  {
    if (mRealOwner == null) {
      mRealOwner = findRealOwner(mContext);
    }
    return mRealOwner;
  }
  
  /* Error */
  public void inflate(int paramInt, Menu paramMenu)
  {
    // Byte code:
    //   0: aload_2
    //   1: instanceof 155
    //   4: ifne +10 -> 14
    //   7: aload_0
    //   8: iload_1
    //   9: aload_2
    //   10: invokespecial 157	android/view/MenuInflater:inflate	(ILandroid/view/Menu;)V
    //   13: return
    //   14: aconst_null
    //   15: astore_3
    //   16: aload_0
    //   17: getfield 47	android/support/v7/view/SupportMenuInflater:mContext	Landroid/content/Context;
    //   20: invokevirtual 161	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   23: iload_1
    //   24: invokevirtual 167	android/content/res/Resources:getLayout	(I)Landroid/content/res/XmlResourceParser;
    //   27: astore_3
    //   28: aload_0
    //   29: aload_3
    //   30: aload_3
    //   31: invokestatic 173	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    //   34: aload_2
    //   35: invokespecial 126	android/support/v7/view/SupportMenuInflater:parseMenu	(Lorg/xmlpull/v1/XmlPullParser;Landroid/util/AttributeSet;Landroid/view/Menu;)V
    //   38: aload_3
    //   39: ifnull -26 -> 13
    //   42: aload_3
    //   43: invokeinterface 178 1 0
    //   48: return
    //   49: astore 6
    //   51: new 180	android/view/InflateException
    //   54: dup
    //   55: ldc -74
    //   57: aload 6
    //   59: invokespecial 185	android/view/InflateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   62: athrow
    //   63: astore 5
    //   65: aload_3
    //   66: ifnull +9 -> 75
    //   69: aload_3
    //   70: invokeinterface 178 1 0
    //   75: aload 5
    //   77: athrow
    //   78: astore 4
    //   80: new 180	android/view/InflateException
    //   83: dup
    //   84: ldc -74
    //   86: aload 4
    //   88: invokespecial 185	android/view/InflateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   91: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	92	0	this	SupportMenuInflater
    //   0	92	1	paramInt	int
    //   0	92	2	paramMenu	Menu
    //   15	55	3	localXmlResourceParser	android.content.res.XmlResourceParser
    //   78	9	4	localIOException	IOException
    //   63	13	5	localObject	Object
    //   49	9	6	localXmlPullParserException	XmlPullParserException
    // Exception table:
    //   from	to	target	type
    //   16	38	49	org/xmlpull/v1/XmlPullParserException
    //   16	38	63	finally
    //   51	63	63	finally
    //   80	92	63	finally
    //   16	38	78	java/io/IOException
  }
  
  private static class InflatedOnMenuItemClickListener
    implements MenuItem.OnMenuItemClickListener
  {
    private static final Class<?>[] PARAM_TYPES = { MenuItem.class };
    private Method mMethod;
    private Object mRealOwner;
    
    public InflatedOnMenuItemClickListener(Object paramObject, String paramString)
    {
      mRealOwner = paramObject;
      Class localClass = paramObject.getClass();
      try
      {
        mMethod = localClass.getMethod(paramString, PARAM_TYPES);
        return;
      }
      catch (Exception localException)
      {
        InflateException localInflateException = new InflateException("Couldn't resolve menu item onClick handler " + paramString + " in class " + localClass.getName());
        localInflateException.initCause(localException);
        throw localInflateException;
      }
    }
    
    public boolean onMenuItemClick(MenuItem paramMenuItem)
    {
      try
      {
        if (mMethod.getReturnType() == Boolean.TYPE) {
          return ((Boolean)mMethod.invoke(mRealOwner, new Object[] { paramMenuItem })).booleanValue();
        }
        mMethod.invoke(mRealOwner, new Object[] { paramMenuItem });
        return true;
      }
      catch (Exception localException)
      {
        throw new RuntimeException(localException);
      }
    }
  }
  
  private class MenuState
  {
    private static final int defaultGroupId = 0;
    private static final int defaultItemCategory = 0;
    private static final int defaultItemCheckable = 0;
    private static final boolean defaultItemChecked = false;
    private static final boolean defaultItemEnabled = true;
    private static final int defaultItemId = 0;
    private static final int defaultItemOrder = 0;
    private static final boolean defaultItemVisible = true;
    private int groupCategory;
    private int groupCheckable;
    private boolean groupEnabled;
    private int groupId;
    private int groupOrder;
    private boolean groupVisible;
    ActionProvider itemActionProvider;
    private String itemActionProviderClassName;
    private String itemActionViewClassName;
    private int itemActionViewLayout;
    private boolean itemAdded;
    private char itemAlphabeticShortcut;
    private int itemCategoryOrder;
    private int itemCheckable;
    private boolean itemChecked;
    private boolean itemEnabled;
    private int itemIconResId;
    private int itemId;
    private String itemListenerMethodName;
    private char itemNumericShortcut;
    private int itemShowAsAction;
    private CharSequence itemTitle;
    private CharSequence itemTitleCondensed;
    private boolean itemVisible;
    private Menu menu;
    
    public MenuState(Menu paramMenu)
    {
      menu = paramMenu;
      resetGroup();
    }
    
    private char getShortcut(String paramString)
    {
      if (paramString == null) {
        return '\000';
      }
      return paramString.charAt(0);
    }
    
    private <T> T newInstance(String paramString, Class<?>[] paramArrayOfClass, Object[] paramArrayOfObject)
    {
      try
      {
        Constructor localConstructor = mContext.getClassLoader().loadClass(paramString).getConstructor(paramArrayOfClass);
        localConstructor.setAccessible(true);
        Object localObject = localConstructor.newInstance(paramArrayOfObject);
        return localObject;
      }
      catch (Exception localException)
      {
        Log.w("SupportMenuInflater", "Cannot instantiate class: " + paramString, localException);
      }
      return null;
    }
    
    private void setItem(MenuItem paramMenuItem)
    {
      MenuItem localMenuItem = paramMenuItem.setChecked(itemChecked).setVisible(itemVisible).setEnabled(itemEnabled);
      if (itemCheckable >= 1) {}
      for (boolean bool = true;; bool = false)
      {
        localMenuItem.setCheckable(bool).setTitleCondensed(itemTitleCondensed).setIcon(itemIconResId).setAlphabeticShortcut(itemAlphabeticShortcut).setNumericShortcut(itemNumericShortcut);
        if (itemShowAsAction >= 0) {
          MenuItemCompat.setShowAsAction(paramMenuItem, itemShowAsAction);
        }
        if (itemListenerMethodName == null) {
          break label158;
        }
        if (!mContext.isRestricted()) {
          break;
        }
        throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context");
      }
      paramMenuItem.setOnMenuItemClickListener(new SupportMenuInflater.InflatedOnMenuItemClickListener(getRealOwner(), itemListenerMethodName));
      label158:
      if ((paramMenuItem instanceof MenuItemImpl))
      {
        ((MenuItemImpl)paramMenuItem);
        if (itemCheckable >= 2)
        {
          if (!(paramMenuItem instanceof MenuItemImpl)) {
            break label277;
          }
          ((MenuItemImpl)paramMenuItem).setExclusiveCheckable(true);
        }
        label193:
        String str = itemActionViewClassName;
        int i = 0;
        if (str != null)
        {
          MenuItemCompat.setActionView(paramMenuItem, (View)newInstance(itemActionViewClassName, SupportMenuInflater.ACTION_VIEW_CONSTRUCTOR_SIGNATURE, mActionViewConstructorArguments));
          i = 1;
        }
        if (itemActionViewLayout > 0)
        {
          if (i != 0) {
            break label295;
          }
          MenuItemCompat.setActionView(paramMenuItem, itemActionViewLayout);
        }
      }
      for (;;)
      {
        if (itemActionProvider != null) {
          MenuItemCompat.setActionProvider(paramMenuItem, itemActionProvider);
        }
        return;
        break;
        label277:
        if (!(paramMenuItem instanceof MenuItemWrapperICS)) {
          break label193;
        }
        ((MenuItemWrapperICS)paramMenuItem).setExclusiveCheckable(true);
        break label193;
        label295:
        Log.w("SupportMenuInflater", "Ignoring attribute 'itemActionViewLayout'. Action view already specified.");
      }
    }
    
    public void addItem()
    {
      itemAdded = true;
      setItem(menu.add(groupId, itemId, itemCategoryOrder, itemTitle));
    }
    
    public SubMenu addSubMenuItem()
    {
      itemAdded = true;
      SubMenu localSubMenu = menu.addSubMenu(groupId, itemId, itemCategoryOrder, itemTitle);
      setItem(localSubMenu.getItem());
      return localSubMenu;
    }
    
    public boolean hasAddedItem()
    {
      return itemAdded;
    }
    
    public void readGroup(AttributeSet paramAttributeSet)
    {
      TypedArray localTypedArray = mContext.obtainStyledAttributes(paramAttributeSet, R.styleable.MenuGroup);
      groupId = localTypedArray.getResourceId(R.styleable.MenuGroup_android_id, 0);
      groupCategory = localTypedArray.getInt(R.styleable.MenuGroup_android_menuCategory, 0);
      groupOrder = localTypedArray.getInt(R.styleable.MenuGroup_android_orderInCategory, 0);
      groupCheckable = localTypedArray.getInt(R.styleable.MenuGroup_android_checkableBehavior, 0);
      groupVisible = localTypedArray.getBoolean(R.styleable.MenuGroup_android_visible, true);
      groupEnabled = localTypedArray.getBoolean(R.styleable.MenuGroup_android_enabled, true);
      localTypedArray.recycle();
    }
    
    public void readItem(AttributeSet paramAttributeSet)
    {
      TypedArray localTypedArray = mContext.obtainStyledAttributes(paramAttributeSet, R.styleable.MenuItem);
      itemId = localTypedArray.getResourceId(R.styleable.MenuItem_android_id, 0);
      int i = localTypedArray.getInt(R.styleable.MenuItem_android_menuCategory, groupCategory);
      int j = localTypedArray.getInt(R.styleable.MenuItem_android_orderInCategory, groupOrder);
      itemCategoryOrder = (0xFFFF0000 & i | 0xFFFF & j);
      itemTitle = localTypedArray.getText(R.styleable.MenuItem_android_title);
      itemTitleCondensed = localTypedArray.getText(R.styleable.MenuItem_android_titleCondensed);
      itemIconResId = localTypedArray.getResourceId(R.styleable.MenuItem_android_icon, 0);
      itemAlphabeticShortcut = getShortcut(localTypedArray.getString(R.styleable.MenuItem_android_alphabeticShortcut));
      itemNumericShortcut = getShortcut(localTypedArray.getString(R.styleable.MenuItem_android_numericShortcut));
      int m;
      label162:
      int k;
      if (localTypedArray.hasValue(R.styleable.MenuItem_android_checkable)) {
        if (localTypedArray.getBoolean(R.styleable.MenuItem_android_checkable, false))
        {
          m = 1;
          itemCheckable = m;
          itemChecked = localTypedArray.getBoolean(R.styleable.MenuItem_android_checked, false);
          itemVisible = localTypedArray.getBoolean(R.styleable.MenuItem_android_visible, groupVisible);
          itemEnabled = localTypedArray.getBoolean(R.styleable.MenuItem_android_enabled, groupEnabled);
          itemShowAsAction = localTypedArray.getInt(R.styleable.MenuItem_showAsAction, -1);
          itemListenerMethodName = localTypedArray.getString(R.styleable.MenuItem_android_onClick);
          itemActionViewLayout = localTypedArray.getResourceId(R.styleable.MenuItem_actionLayout, 0);
          itemActionViewClassName = localTypedArray.getString(R.styleable.MenuItem_actionViewClass);
          itemActionProviderClassName = localTypedArray.getString(R.styleable.MenuItem_actionProviderClass);
          if (itemActionProviderClassName == null) {
            break label342;
          }
          k = 1;
          label271:
          if ((k == 0) || (itemActionViewLayout != 0) || (itemActionViewClassName != null)) {
            break label348;
          }
        }
      }
      for (itemActionProvider = ((ActionProvider)newInstance(itemActionProviderClassName, SupportMenuInflater.ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE, mActionProviderConstructorArguments));; itemActionProvider = null)
      {
        localTypedArray.recycle();
        itemAdded = false;
        return;
        m = 0;
        break;
        itemCheckable = groupCheckable;
        break label162;
        label342:
        k = 0;
        break label271;
        label348:
        if (k != 0) {
          Log.w("SupportMenuInflater", "Ignoring attribute 'actionProviderClass'. Action view already specified.");
        }
      }
    }
    
    public void resetGroup()
    {
      groupId = 0;
      groupCategory = 0;
      groupOrder = 0;
      groupCheckable = 0;
      groupVisible = true;
      groupEnabled = true;
    }
  }
}
