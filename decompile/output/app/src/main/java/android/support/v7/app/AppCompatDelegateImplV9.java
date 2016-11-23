package android.support.v7.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.appcompat.R.color;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.layout;
import android.support.v7.appcompat.R.style;
import android.support.v7.appcompat.R.styleable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.StandaloneActionMode;
import android.support.v7.view.menu.ListMenuPresenter;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.ActionBarContextView;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.ContentFrameLayout.OnAttachListener;
import android.support.v7.widget.DecorContentParent;
import android.support.v7.widget.FitWindowsViewGroup;
import android.support.v7.widget.FitWindowsViewGroup.OnFitSystemWindowsListener;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.VectorEnabledTintResources;
import android.support.v7.widget.ViewStubCompat;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.Window;
import android.view.Window.Callback;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

class AppCompatDelegateImplV9
  extends AppCompatDelegateImplBase
  implements MenuBuilder.Callback, LayoutInflaterFactory
{
  private ActionMenuPresenterCallback mActionMenuPresenterCallback;
  ActionMode mActionMode;
  PopupWindow mActionModePopup;
  ActionBarContextView mActionModeView;
  private AppCompatViewInflater mAppCompatViewInflater;
  private boolean mClosingActionMenu;
  private DecorContentParent mDecorContentParent;
  private boolean mEnableDefaultActionBarUp;
  ViewPropertyAnimatorCompat mFadeAnim = null;
  private boolean mFeatureIndeterminateProgress;
  private boolean mFeatureProgress;
  int mInvalidatePanelMenuFeatures;
  boolean mInvalidatePanelMenuPosted;
  private final Runnable mInvalidatePanelMenuRunnable = new Runnable()
  {
    public void run()
    {
      if ((0x1 & mInvalidatePanelMenuFeatures) != 0) {
        doInvalidatePanelMenu(0);
      }
      if ((0x1000 & mInvalidatePanelMenuFeatures) != 0) {
        doInvalidatePanelMenu(108);
      }
      mInvalidatePanelMenuPosted = false;
      mInvalidatePanelMenuFeatures = 0;
    }
  };
  private boolean mLongPressBackDown;
  private PanelMenuPresenterCallback mPanelMenuPresenterCallback;
  private PanelFeatureState[] mPanels;
  private PanelFeatureState mPreparedPanel;
  Runnable mShowActionModePopup;
  private View mStatusGuard;
  private ViewGroup mSubDecor;
  private boolean mSubDecorInstalled;
  private Rect mTempRect1;
  private Rect mTempRect2;
  private TextView mTitleView;
  
  AppCompatDelegateImplV9(Context paramContext, Window paramWindow, AppCompatCallback paramAppCompatCallback)
  {
    super(paramContext, paramWindow, paramAppCompatCallback);
  }
  
  private void applyFixedSizeWindow()
  {
    ContentFrameLayout localContentFrameLayout = (ContentFrameLayout)mSubDecor.findViewById(16908290);
    View localView = mWindow.getDecorView();
    localContentFrameLayout.setDecorPadding(localView.getPaddingLeft(), localView.getPaddingTop(), localView.getPaddingRight(), localView.getPaddingBottom());
    TypedArray localTypedArray = mContext.obtainStyledAttributes(R.styleable.AppCompatTheme);
    localTypedArray.getValue(R.styleable.AppCompatTheme_windowMinWidthMajor, localContentFrameLayout.getMinWidthMajor());
    localTypedArray.getValue(R.styleable.AppCompatTheme_windowMinWidthMinor, localContentFrameLayout.getMinWidthMinor());
    if (localTypedArray.hasValue(R.styleable.AppCompatTheme_windowFixedWidthMajor)) {
      localTypedArray.getValue(R.styleable.AppCompatTheme_windowFixedWidthMajor, localContentFrameLayout.getFixedWidthMajor());
    }
    if (localTypedArray.hasValue(R.styleable.AppCompatTheme_windowFixedWidthMinor)) {
      localTypedArray.getValue(R.styleable.AppCompatTheme_windowFixedWidthMinor, localContentFrameLayout.getFixedWidthMinor());
    }
    if (localTypedArray.hasValue(R.styleable.AppCompatTheme_windowFixedHeightMajor)) {
      localTypedArray.getValue(R.styleable.AppCompatTheme_windowFixedHeightMajor, localContentFrameLayout.getFixedHeightMajor());
    }
    if (localTypedArray.hasValue(R.styleable.AppCompatTheme_windowFixedHeightMinor)) {
      localTypedArray.getValue(R.styleable.AppCompatTheme_windowFixedHeightMinor, localContentFrameLayout.getFixedHeightMinor());
    }
    localTypedArray.recycle();
    localContentFrameLayout.requestLayout();
  }
  
  private ViewGroup createSubDecor()
  {
    TypedArray localTypedArray = mContext.obtainStyledAttributes(R.styleable.AppCompatTheme);
    if (!localTypedArray.hasValue(R.styleable.AppCompatTheme_windowActionBar))
    {
      localTypedArray.recycle();
      throw new IllegalStateException("You need to use a Theme.AppCompat theme (or descendant) with this activity.");
    }
    LayoutInflater localLayoutInflater;
    ViewGroup localViewGroup1;
    if (localTypedArray.getBoolean(R.styleable.AppCompatTheme_windowNoTitle, false))
    {
      requestWindowFeature(1);
      if (localTypedArray.getBoolean(R.styleable.AppCompatTheme_windowActionBarOverlay, false)) {
        requestWindowFeature(109);
      }
      if (localTypedArray.getBoolean(R.styleable.AppCompatTheme_windowActionModeOverlay, false)) {
        requestWindowFeature(10);
      }
      mIsFloating = localTypedArray.getBoolean(R.styleable.AppCompatTheme_android_windowIsFloating, false);
      localTypedArray.recycle();
      mWindow.getDecorView();
      localLayoutInflater = LayoutInflater.from(mContext);
      if (mWindowNoTitle) {
        break label445;
      }
      if (!mIsFloating) {
        break label270;
      }
      localViewGroup1 = (ViewGroup)localLayoutInflater.inflate(R.layout.abc_dialog_title_material, null);
      mOverlayActionBar = false;
      mHasActionBar = false;
    }
    for (;;)
    {
      if (localViewGroup1 != null) {
        break label528;
      }
      throw new IllegalArgumentException("AppCompat does not support the current theme features: { windowActionBar: " + mHasActionBar + ", windowActionBarOverlay: " + mOverlayActionBar + ", android:windowIsFloating: " + mIsFloating + ", windowActionModeOverlay: " + mOverlayActionMode + ", windowNoTitle: " + mWindowNoTitle + " }");
      if (!localTypedArray.getBoolean(R.styleable.AppCompatTheme_windowActionBar, false)) {
        break;
      }
      requestWindowFeature(108);
      break;
      label270:
      boolean bool = mHasActionBar;
      localViewGroup1 = null;
      if (bool)
      {
        TypedValue localTypedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.actionBarTheme, localTypedValue, true);
        if (resourceId != 0) {}
        for (Object localObject = new ContextThemeWrapper(mContext, resourceId);; localObject = mContext)
        {
          localViewGroup1 = (ViewGroup)LayoutInflater.from((Context)localObject).inflate(R.layout.abc_screen_toolbar, null);
          mDecorContentParent = ((DecorContentParent)localViewGroup1.findViewById(R.id.decor_content_parent));
          mDecorContentParent.setWindowCallback(getWindowCallback());
          if (mOverlayActionBar) {
            mDecorContentParent.initFeature(109);
          }
          if (mFeatureProgress) {
            mDecorContentParent.initFeature(2);
          }
          if (!mFeatureIndeterminateProgress) {
            break;
          }
          mDecorContentParent.initFeature(5);
          break;
        }
        label445:
        if (mOverlayActionMode) {}
        for (localViewGroup1 = (ViewGroup)localLayoutInflater.inflate(R.layout.abc_screen_simple_overlay_action_mode, null);; localViewGroup1 = (ViewGroup)localLayoutInflater.inflate(R.layout.abc_screen_simple, null))
        {
          if (Build.VERSION.SDK_INT < 21) {
            break label507;
          }
          ViewCompat.setOnApplyWindowInsetsListener(localViewGroup1, new OnApplyWindowInsetsListener()
          {
            public WindowInsetsCompat onApplyWindowInsets(View paramAnonymousView, WindowInsetsCompat paramAnonymousWindowInsetsCompat)
            {
              int i = paramAnonymousWindowInsetsCompat.getSystemWindowInsetTop();
              int j = updateStatusGuard(i);
              if (i != j) {
                paramAnonymousWindowInsetsCompat = paramAnonymousWindowInsetsCompat.replaceSystemWindowInsets(paramAnonymousWindowInsetsCompat.getSystemWindowInsetLeft(), j, paramAnonymousWindowInsetsCompat.getSystemWindowInsetRight(), paramAnonymousWindowInsetsCompat.getSystemWindowInsetBottom());
              }
              return ViewCompat.onApplyWindowInsets(paramAnonymousView, paramAnonymousWindowInsetsCompat);
            }
          });
          break;
        }
        label507:
        ((FitWindowsViewGroup)localViewGroup1).setOnFitSystemWindowsListener(new FitWindowsViewGroup.OnFitSystemWindowsListener()
        {
          public void onFitSystemWindows(Rect paramAnonymousRect)
          {
            top = updateStatusGuard(top);
          }
        });
      }
    }
    label528:
    if (mDecorContentParent == null) {
      mTitleView = ((TextView)localViewGroup1.findViewById(R.id.title));
    }
    ViewUtils.makeOptionalFitsSystemWindows(localViewGroup1);
    ContentFrameLayout localContentFrameLayout = (ContentFrameLayout)localViewGroup1.findViewById(R.id.action_bar_activity_content);
    ViewGroup localViewGroup2 = (ViewGroup)mWindow.findViewById(16908290);
    if (localViewGroup2 != null)
    {
      while (localViewGroup2.getChildCount() > 0)
      {
        View localView = localViewGroup2.getChildAt(0);
        localViewGroup2.removeViewAt(0);
        localContentFrameLayout.addView(localView);
      }
      localViewGroup2.setId(-1);
      localContentFrameLayout.setId(16908290);
      if ((localViewGroup2 instanceof FrameLayout)) {
        ((FrameLayout)localViewGroup2).setForeground(null);
      }
    }
    mWindow.setContentView(localViewGroup1);
    localContentFrameLayout.setAttachListener(new ContentFrameLayout.OnAttachListener()
    {
      public void onAttachedFromWindow() {}
      
      public void onDetachedFromWindow()
      {
        dismissPopups();
      }
    });
    return localViewGroup1;
  }
  
  private void ensureSubDecor()
  {
    if (!mSubDecorInstalled)
    {
      mSubDecor = createSubDecor();
      CharSequence localCharSequence = getTitle();
      if (!TextUtils.isEmpty(localCharSequence)) {
        onTitleChanged(localCharSequence);
      }
      applyFixedSizeWindow();
      onSubDecorInstalled(mSubDecor);
      mSubDecorInstalled = true;
      PanelFeatureState localPanelFeatureState = getPanelState(0, false);
      if ((!isDestroyed()) && ((localPanelFeatureState == null) || (menu == null))) {
        invalidatePanelMenu(108);
      }
    }
  }
  
  private boolean initializePanelContent(PanelFeatureState paramPanelFeatureState)
  {
    if (createdPanelView != null) {
      shownPanelView = createdPanelView;
    }
    do
    {
      return true;
      if (menu == null) {
        return false;
      }
      if (mPanelMenuPresenterCallback == null) {
        mPanelMenuPresenterCallback = new PanelMenuPresenterCallback();
      }
      shownPanelView = ((View)paramPanelFeatureState.getListMenuView(mPanelMenuPresenterCallback));
    } while (shownPanelView != null);
    return false;
  }
  
  private boolean initializePanelDecor(PanelFeatureState paramPanelFeatureState)
  {
    paramPanelFeatureState.setStyle(getActionBarThemedContext());
    decorView = new ListMenuDecorView(listPresenterContext);
    gravity = 81;
    return true;
  }
  
  private boolean initializePanelMenu(PanelFeatureState paramPanelFeatureState)
  {
    Object localObject = mContext;
    TypedValue localTypedValue;
    Resources.Theme localTheme1;
    Resources.Theme localTheme2;
    if (((featureId == 0) || (featureId == 108)) && (mDecorContentParent != null))
    {
      localTypedValue = new TypedValue();
      localTheme1 = ((Context)localObject).getTheme();
      localTheme1.resolveAttribute(R.attr.actionBarTheme, localTypedValue, true);
      if (resourceId == 0) {
        break label188;
      }
      localTheme2 = ((Context)localObject).getResources().newTheme();
      localTheme2.setTo(localTheme1);
      localTheme2.applyStyle(resourceId, true);
      localTheme2.resolveAttribute(R.attr.actionBarWidgetTheme, localTypedValue, true);
    }
    for (;;)
    {
      if (resourceId != 0)
      {
        if (localTheme2 == null)
        {
          localTheme2 = ((Context)localObject).getResources().newTheme();
          localTheme2.setTo(localTheme1);
        }
        localTheme2.applyStyle(resourceId, true);
      }
      if (localTheme2 != null)
      {
        ContextThemeWrapper localContextThemeWrapper = new ContextThemeWrapper((Context)localObject, 0);
        localContextThemeWrapper.getTheme().setTo(localTheme2);
        localObject = localContextThemeWrapper;
      }
      MenuBuilder localMenuBuilder = new MenuBuilder((Context)localObject);
      localMenuBuilder.setCallback(this);
      paramPanelFeatureState.setMenu(localMenuBuilder);
      return true;
      label188:
      localTheme1.resolveAttribute(R.attr.actionBarWidgetTheme, localTypedValue, true);
      localTheme2 = null;
    }
  }
  
  private void invalidatePanelMenu(int paramInt)
  {
    mInvalidatePanelMenuFeatures |= 1 << paramInt;
    if (!mInvalidatePanelMenuPosted)
    {
      ViewCompat.postOnAnimation(mWindow.getDecorView(), mInvalidatePanelMenuRunnable);
      mInvalidatePanelMenuPosted = true;
    }
  }
  
  private boolean onKeyDownPanel(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getRepeatCount() == 0)
    {
      PanelFeatureState localPanelFeatureState = getPanelState(paramInt, true);
      if (!isOpen) {
        return preparePanel(localPanelFeatureState, paramKeyEvent);
      }
    }
    return false;
  }
  
  private boolean onKeyUpPanel(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool1;
    if (mActionMode != null) {
      bool1 = false;
    }
    for (;;)
    {
      return bool1;
      PanelFeatureState localPanelFeatureState = getPanelState(paramInt, true);
      if ((paramInt == 0) && (mDecorContentParent != null) && (mDecorContentParent.canShowOverflowMenu()) && (!ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(mContext)))) {
        if (!mDecorContentParent.isOverflowMenuShowing())
        {
          boolean bool4 = isDestroyed();
          bool1 = false;
          if (!bool4)
          {
            boolean bool5 = preparePanel(localPanelFeatureState, paramKeyEvent);
            bool1 = false;
            if (bool5) {
              bool1 = mDecorContentParent.showOverflowMenu();
            }
          }
        }
      }
      while (bool1)
      {
        AudioManager localAudioManager = (AudioManager)mContext.getSystemService("audio");
        if (localAudioManager == null) {
          break label243;
        }
        localAudioManager.playSoundEffect(0);
        return bool1;
        bool1 = mDecorContentParent.hideOverflowMenu();
        continue;
        if ((isOpen) || (isHandled))
        {
          bool1 = isOpen;
          closePanel(localPanelFeatureState, true);
        }
        else
        {
          boolean bool2 = isPrepared;
          bool1 = false;
          if (bool2)
          {
            boolean bool3 = true;
            if (refreshMenuContent)
            {
              isPrepared = false;
              bool3 = preparePanel(localPanelFeatureState, paramKeyEvent);
            }
            bool1 = false;
            if (bool3)
            {
              openPanel(localPanelFeatureState, paramKeyEvent);
              bool1 = true;
            }
          }
        }
      }
    }
    label243:
    Log.w("AppCompatDelegate", "Couldn't get audio manager");
    return bool1;
  }
  
  private void openPanel(PanelFeatureState paramPanelFeatureState, KeyEvent paramKeyEvent)
  {
    if ((isOpen) || (isDestroyed())) {}
    label108:
    label114:
    label118:
    label120:
    WindowManager localWindowManager;
    int i;
    do
    {
      do
      {
        for (;;)
        {
          return;
          int k;
          if (featureId == 0)
          {
            Context localContext = mContext;
            if ((0xF & getResourcesgetConfigurationscreenLayout) != 4) {
              break label108;
            }
            k = 1;
            if (getApplicationInfotargetSdkVersion < 11) {
              break label114;
            }
          }
          for (int m = 1;; m = 0)
          {
            if ((k != 0) && (m != 0)) {
              break label118;
            }
            Window.Callback localCallback = getWindowCallback();
            if ((localCallback == null) || (localCallback.onMenuOpened(featureId, menu))) {
              break label120;
            }
            closePanel(paramPanelFeatureState, true);
            return;
            k = 0;
            break;
          }
        }
        localWindowManager = (WindowManager)mContext.getSystemService("window");
      } while ((localWindowManager == null) || (!preparePanel(paramPanelFeatureState, paramKeyEvent)));
      i = -2;
      if ((decorView != null) && (!refreshDecorView)) {
        break label409;
      }
      if (decorView != null) {
        break;
      }
    } while ((!initializePanelDecor(paramPanelFeatureState)) || (decorView == null));
    label189:
    if ((initializePanelContent(paramPanelFeatureState)) && (paramPanelFeatureState.hasPanelItems()))
    {
      ViewGroup.LayoutParams localLayoutParams1 = shownPanelView.getLayoutParams();
      if (localLayoutParams1 == null) {
        localLayoutParams1 = new ViewGroup.LayoutParams(-2, -2);
      }
      int j = background;
      decorView.setBackgroundResource(j);
      ViewParent localViewParent = shownPanelView.getParent();
      if ((localViewParent != null) && ((localViewParent instanceof ViewGroup))) {
        ((ViewGroup)localViewParent).removeView(shownPanelView);
      }
      decorView.addView(shownPanelView, localLayoutParams1);
      if (!shownPanelView.hasFocus()) {
        shownPanelView.requestFocus();
      }
    }
    for (;;)
    {
      isHandled = false;
      WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(i, -2, x, y, 1002, 8519680, -3);
      gravity = gravity;
      windowAnimations = windowAnimations;
      localWindowManager.addView(decorView, localLayoutParams);
      isOpen = true;
      return;
      if ((!refreshDecorView) || (decorView.getChildCount() <= 0)) {
        break label189;
      }
      decorView.removeAllViews();
      break label189;
      break;
      label409:
      if (createdPanelView != null)
      {
        ViewGroup.LayoutParams localLayoutParams2 = createdPanelView.getLayoutParams();
        if ((localLayoutParams2 != null) && (width == -1)) {
          i = -1;
        }
      }
    }
  }
  
  private boolean performPanelShortcut(PanelFeatureState paramPanelFeatureState, int paramInt1, KeyEvent paramKeyEvent, int paramInt2)
  {
    boolean bool1;
    if (paramKeyEvent.isSystem()) {
      bool1 = false;
    }
    do
    {
      return bool1;
      if (!isPrepared)
      {
        boolean bool2 = preparePanel(paramPanelFeatureState, paramKeyEvent);
        bool1 = false;
        if (!bool2) {}
      }
      else
      {
        MenuBuilder localMenuBuilder = menu;
        bool1 = false;
        if (localMenuBuilder != null) {
          bool1 = menu.performShortcut(paramInt1, paramKeyEvent, paramInt2);
        }
      }
    } while ((!bool1) || ((paramInt2 & 0x1) != 0) || (mDecorContentParent != null));
    closePanel(paramPanelFeatureState, true);
    return bool1;
  }
  
  private boolean preparePanel(PanelFeatureState paramPanelFeatureState, KeyEvent paramKeyEvent)
  {
    if (isDestroyed()) {
      return false;
    }
    if (isPrepared) {
      return true;
    }
    if ((mPreparedPanel != null) && (mPreparedPanel != paramPanelFeatureState)) {
      closePanel(mPreparedPanel, false);
    }
    Window.Callback localCallback = getWindowCallback();
    if (localCallback != null) {
      createdPanelView = localCallback.onCreatePanelView(featureId);
    }
    if ((featureId == 0) || (featureId == 108)) {}
    for (int i = 1;; i = 0)
    {
      if ((i != 0) && (mDecorContentParent != null)) {
        mDecorContentParent.setMenuPrepared();
      }
      if ((createdPanelView != null) || ((i != 0) && ((peekSupportActionBar() instanceof ToolbarActionBar)))) {
        break label411;
      }
      if ((menu != null) && (!refreshMenuContent)) {
        break label279;
      }
      if ((menu == null) && ((!initializePanelMenu(paramPanelFeatureState)) || (menu == null))) {
        break;
      }
      if ((i != 0) && (mDecorContentParent != null))
      {
        if (mActionMenuPresenterCallback == null) {
          mActionMenuPresenterCallback = new ActionMenuPresenterCallback();
        }
        mDecorContentParent.setMenu(menu, mActionMenuPresenterCallback);
      }
      menu.stopDispatchingItemsChanged();
      if (localCallback.onCreatePanelMenu(featureId, menu)) {
        break label274;
      }
      paramPanelFeatureState.setMenu(null);
      if ((i == 0) || (mDecorContentParent == null)) {
        break;
      }
      mDecorContentParent.setMenu(null, mActionMenuPresenterCallback);
      return false;
    }
    label274:
    refreshMenuContent = false;
    label279:
    menu.stopDispatchingItemsChanged();
    if (frozenActionViewState != null)
    {
      menu.restoreActionViewStates(frozenActionViewState);
      frozenActionViewState = null;
    }
    if (!localCallback.onPreparePanel(0, createdPanelView, menu))
    {
      if ((i != 0) && (mDecorContentParent != null)) {
        mDecorContentParent.setMenu(null, mActionMenuPresenterCallback);
      }
      menu.startDispatchingItemsChanged();
      return false;
    }
    int j;
    if (paramKeyEvent != null)
    {
      j = paramKeyEvent.getDeviceId();
      if (KeyCharacterMap.load(j).getKeyboardType() == 1) {
        break label434;
      }
    }
    label411:
    label434:
    for (boolean bool = true;; bool = false)
    {
      qwertyMode = bool;
      menu.setQwertyMode(qwertyMode);
      menu.startDispatchingItemsChanged();
      isPrepared = true;
      isHandled = false;
      mPreparedPanel = paramPanelFeatureState;
      return true;
      j = -1;
      break;
    }
  }
  
  private void reopenMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
  {
    if ((mDecorContentParent != null) && (mDecorContentParent.canShowOverflowMenu()) && ((!ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(mContext))) || (mDecorContentParent.isOverflowMenuShowPending())))
    {
      Window.Callback localCallback = getWindowCallback();
      if ((!mDecorContentParent.isOverflowMenuShowing()) || (!paramBoolean)) {
        if ((localCallback != null) && (!isDestroyed()))
        {
          if ((mInvalidatePanelMenuPosted) && ((0x1 & mInvalidatePanelMenuFeatures) != 0))
          {
            mWindow.getDecorView().removeCallbacks(mInvalidatePanelMenuRunnable);
            mInvalidatePanelMenuRunnable.run();
          }
          PanelFeatureState localPanelFeatureState2 = getPanelState(0, true);
          if ((menu != null) && (!refreshMenuContent) && (localCallback.onPreparePanel(0, createdPanelView, menu)))
          {
            localCallback.onMenuOpened(108, menu);
            mDecorContentParent.showOverflowMenu();
          }
        }
      }
      do
      {
        return;
        mDecorContentParent.hideOverflowMenu();
      } while (isDestroyed());
      localCallback.onPanelClosed(108, getPanelState0menu);
      return;
    }
    PanelFeatureState localPanelFeatureState1 = getPanelState(0, true);
    refreshDecorView = true;
    closePanel(localPanelFeatureState1, false);
    openPanel(localPanelFeatureState1, null);
  }
  
  private int sanitizeWindowFeatureId(int paramInt)
  {
    if (paramInt == 8)
    {
      Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR id when requesting this feature.");
      paramInt = 108;
    }
    while (paramInt != 9) {
      return paramInt;
    }
    Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY id when requesting this feature.");
    return 109;
  }
  
  private boolean shouldInheritContext(ViewParent paramViewParent)
  {
    if (paramViewParent == null) {
      return false;
    }
    View localView = mWindow.getDecorView();
    for (;;)
    {
      if (paramViewParent == null) {
        return true;
      }
      if ((paramViewParent == localView) || (!(paramViewParent instanceof View)) || (ViewCompat.isAttachedToWindow((View)paramViewParent))) {
        return false;
      }
      paramViewParent = paramViewParent.getParent();
    }
  }
  
  private void throwFeatureRequestIfSubDecorInstalled()
  {
    if (mSubDecorInstalled) {
      throw new AndroidRuntimeException("Window feature must be requested before adding content");
    }
  }
  
  public void addContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    ensureSubDecor();
    ((ViewGroup)mSubDecor.findViewById(16908290)).addView(paramView, paramLayoutParams);
    mOriginalWindowCallback.onContentChanged();
  }
  
  View callActivityOnCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    if ((mOriginalWindowCallback instanceof LayoutInflater.Factory))
    {
      View localView = ((LayoutInflater.Factory)mOriginalWindowCallback).onCreateView(paramString, paramContext, paramAttributeSet);
      if (localView != null) {
        return localView;
      }
    }
    return null;
  }
  
  void callOnPanelClosed(int paramInt, PanelFeatureState paramPanelFeatureState, Menu paramMenu)
  {
    if (paramMenu == null)
    {
      if ((paramPanelFeatureState == null) && (paramInt >= 0) && (paramInt < mPanels.length)) {
        paramPanelFeatureState = mPanels[paramInt];
      }
      if (paramPanelFeatureState != null) {
        paramMenu = menu;
      }
    }
    if ((paramPanelFeatureState != null) && (!isOpen)) {}
    while (isDestroyed()) {
      return;
    }
    mOriginalWindowCallback.onPanelClosed(paramInt, paramMenu);
  }
  
  void checkCloseActionMenu(MenuBuilder paramMenuBuilder)
  {
    if (mClosingActionMenu) {
      return;
    }
    mClosingActionMenu = true;
    mDecorContentParent.dismissPopups();
    Window.Callback localCallback = getWindowCallback();
    if ((localCallback != null) && (!isDestroyed())) {
      localCallback.onPanelClosed(108, paramMenuBuilder);
    }
    mClosingActionMenu = false;
  }
  
  void closePanel(int paramInt)
  {
    closePanel(getPanelState(paramInt, true), true);
  }
  
  void closePanel(PanelFeatureState paramPanelFeatureState, boolean paramBoolean)
  {
    if ((paramBoolean) && (featureId == 0) && (mDecorContentParent != null) && (mDecorContentParent.isOverflowMenuShowing())) {
      checkCloseActionMenu(menu);
    }
    do
    {
      return;
      WindowManager localWindowManager = (WindowManager)mContext.getSystemService("window");
      if ((localWindowManager != null) && (isOpen) && (decorView != null))
      {
        localWindowManager.removeView(decorView);
        if (paramBoolean) {
          callOnPanelClosed(featureId, paramPanelFeatureState, null);
        }
      }
      isPrepared = false;
      isHandled = false;
      isOpen = false;
      shownPanelView = null;
      refreshDecorView = true;
    } while (mPreparedPanel != paramPanelFeatureState);
    mPreparedPanel = null;
  }
  
  public View createView(View paramView, String paramString, @NonNull Context paramContext, @NonNull AttributeSet paramAttributeSet)
  {
    boolean bool1;
    if (Build.VERSION.SDK_INT < 21)
    {
      bool1 = true;
      if (mAppCompatViewInflater == null) {
        mAppCompatViewInflater = new AppCompatViewInflater();
      }
      if ((!bool1) || (!shouldInheritContext((ViewParent)paramView))) {
        break label75;
      }
    }
    label75:
    for (boolean bool2 = true;; bool2 = false)
    {
      return mAppCompatViewInflater.createView(paramView, paramString, paramContext, paramAttributeSet, bool2, bool1, true, VectorEnabledTintResources.shouldBeUsed());
      bool1 = false;
      break;
    }
  }
  
  void dismissPopups()
  {
    if (mDecorContentParent != null) {
      mDecorContentParent.dismissPopups();
    }
    if (mActionModePopup != null)
    {
      mWindow.getDecorView().removeCallbacks(mShowActionModePopup);
      if (!mActionModePopup.isShowing()) {}
    }
    try
    {
      mActionModePopup.dismiss();
      mActionModePopup = null;
      endOnGoingFadeAnimation();
      PanelFeatureState localPanelFeatureState = getPanelState(0, false);
      if ((localPanelFeatureState != null) && (menu != null)) {
        menu.close();
      }
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
    }
  }
  
  boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if ((paramKeyEvent.getKeyCode() == 82) && (mOriginalWindowCallback.dispatchKeyEvent(paramKeyEvent))) {
      return true;
    }
    int i = paramKeyEvent.getKeyCode();
    if (paramKeyEvent.getAction() == 0) {}
    for (int j = 1; j != 0; j = 0) {
      return onKeyDown(i, paramKeyEvent);
    }
    return onKeyUp(i, paramKeyEvent);
  }
  
  void doInvalidatePanelMenu(int paramInt)
  {
    PanelFeatureState localPanelFeatureState1 = getPanelState(paramInt, true);
    if (menu != null)
    {
      Bundle localBundle = new Bundle();
      menu.saveActionViewStates(localBundle);
      if (localBundle.size() > 0) {
        frozenActionViewState = localBundle;
      }
      menu.stopDispatchingItemsChanged();
      menu.clear();
    }
    refreshMenuContent = true;
    refreshDecorView = true;
    if (((paramInt == 108) || (paramInt == 0)) && (mDecorContentParent != null))
    {
      PanelFeatureState localPanelFeatureState2 = getPanelState(0, false);
      if (localPanelFeatureState2 != null)
      {
        isPrepared = false;
        preparePanel(localPanelFeatureState2, null);
      }
    }
  }
  
  void endOnGoingFadeAnimation()
  {
    if (mFadeAnim != null) {
      mFadeAnim.cancel();
    }
  }
  
  PanelFeatureState findMenuPanel(Menu paramMenu)
  {
    PanelFeatureState[] arrayOfPanelFeatureState = mPanels;
    int i;
    if (arrayOfPanelFeatureState != null) {
      i = arrayOfPanelFeatureState.length;
    }
    for (int j = 0;; j++)
    {
      if (j >= i) {
        break label55;
      }
      PanelFeatureState localPanelFeatureState = arrayOfPanelFeatureState[j];
      if ((localPanelFeatureState != null) && (menu == paramMenu))
      {
        return localPanelFeatureState;
        i = 0;
        break;
      }
    }
    label55:
    return null;
  }
  
  @Nullable
  public View findViewById(@IdRes int paramInt)
  {
    ensureSubDecor();
    return mWindow.findViewById(paramInt);
  }
  
  protected PanelFeatureState getPanelState(int paramInt, boolean paramBoolean)
  {
    Object localObject = mPanels;
    if ((localObject == null) || (localObject.length <= paramInt))
    {
      PanelFeatureState[] arrayOfPanelFeatureState = new PanelFeatureState[paramInt + 1];
      if (localObject != null) {
        System.arraycopy(localObject, 0, arrayOfPanelFeatureState, 0, localObject.length);
      }
      localObject = arrayOfPanelFeatureState;
      mPanels = arrayOfPanelFeatureState;
    }
    PanelFeatureState localPanelFeatureState = localObject[paramInt];
    if (localPanelFeatureState == null)
    {
      localPanelFeatureState = new PanelFeatureState(paramInt);
      localObject[paramInt] = localPanelFeatureState;
    }
    return localPanelFeatureState;
  }
  
  ViewGroup getSubDecor()
  {
    return mSubDecor;
  }
  
  public boolean hasWindowFeature(int paramInt)
  {
    int i = sanitizeWindowFeatureId(paramInt);
    switch (i)
    {
    default: 
      return mWindow.hasFeature(i);
    case 108: 
      return mHasActionBar;
    case 109: 
      return mOverlayActionBar;
    case 10: 
      return mOverlayActionMode;
    case 2: 
      return mFeatureProgress;
    case 5: 
      return mFeatureIndeterminateProgress;
    }
    return mWindowNoTitle;
  }
  
  public void initWindowDecorActionBar()
  {
    ensureSubDecor();
    if ((!mHasActionBar) || (mActionBar != null)) {}
    for (;;)
    {
      return;
      if ((mOriginalWindowCallback instanceof Activity)) {
        mActionBar = new WindowDecorActionBar((Activity)mOriginalWindowCallback, mOverlayActionBar);
      }
      while (mActionBar != null)
      {
        mActionBar.setDefaultDisplayHomeAsUpEnabled(mEnableDefaultActionBarUp);
        return;
        if ((mOriginalWindowCallback instanceof Dialog)) {
          mActionBar = new WindowDecorActionBar((Dialog)mOriginalWindowCallback);
        }
      }
    }
  }
  
  public void installViewFactory()
  {
    LayoutInflater localLayoutInflater = LayoutInflater.from(mContext);
    if (localLayoutInflater.getFactory() == null) {
      LayoutInflaterCompat.setFactory(localLayoutInflater, this);
    }
    while ((LayoutInflaterCompat.getFactory(localLayoutInflater) instanceof AppCompatDelegateImplV9)) {
      return;
    }
    Log.i("AppCompatDelegate", "The Activity's LayoutInflater already has a Factory installed so we can not install AppCompat's");
  }
  
  public void invalidateOptionsMenu()
  {
    ActionBar localActionBar = getSupportActionBar();
    if ((localActionBar != null) && (localActionBar.invalidateOptionsMenu())) {
      return;
    }
    invalidatePanelMenu(0);
  }
  
  boolean onBackPressed()
  {
    if (mActionMode != null) {
      mActionMode.finish();
    }
    ActionBar localActionBar;
    do
    {
      return true;
      localActionBar = getSupportActionBar();
    } while ((localActionBar != null) && (localActionBar.collapseActionView()));
    return false;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if ((mHasActionBar) && (mSubDecorInstalled))
    {
      ActionBar localActionBar = getSupportActionBar();
      if (localActionBar != null) {
        localActionBar.onConfigurationChanged(paramConfiguration);
      }
    }
    AppCompatDrawableManager.get().onConfigurationChanged(mContext);
    applyDayNight();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    ActionBar localActionBar;
    if (((mOriginalWindowCallback instanceof Activity)) && (NavUtils.getParentActivityName((Activity)mOriginalWindowCallback) != null))
    {
      localActionBar = peekSupportActionBar();
      if (localActionBar == null) {
        mEnableDefaultActionBarUp = true;
      }
    }
    else
    {
      return;
    }
    localActionBar.setDefaultDisplayHomeAsUpEnabled(true);
  }
  
  public final View onCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    View localView = callActivityOnCreateView(paramView, paramString, paramContext, paramAttributeSet);
    if (localView != null) {
      return localView;
    }
    return createView(paramView, paramString, paramContext, paramAttributeSet);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (mActionBar != null) {
      mActionBar.onDestroy();
    }
  }
  
  boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool = true;
    switch (paramInt)
    {
    default: 
      if (Build.VERSION.SDK_INT < 11) {
        onKeyShortcut(paramInt, paramKeyEvent);
      }
      return false;
    case 82: 
      onKeyDownPanel(0, paramKeyEvent);
      return bool;
    }
    if ((0x80 & paramKeyEvent.getFlags()) != 0) {}
    for (;;)
    {
      mLongPressBackDown = bool;
      break;
      bool = false;
    }
  }
  
  boolean onKeyShortcut(int paramInt, KeyEvent paramKeyEvent)
  {
    ActionBar localActionBar = getSupportActionBar();
    if ((localActionBar != null) && (localActionBar.onKeyShortcut(paramInt, paramKeyEvent))) {}
    boolean bool;
    do
    {
      do
      {
        return true;
        if ((mPreparedPanel == null) || (!performPanelShortcut(mPreparedPanel, paramKeyEvent.getKeyCode(), paramKeyEvent, 1))) {
          break;
        }
      } while (mPreparedPanel == null);
      mPreparedPanel.isHandled = true;
      return true;
      if (mPreparedPanel != null) {
        break;
      }
      PanelFeatureState localPanelFeatureState = getPanelState(0, true);
      preparePanel(localPanelFeatureState, paramKeyEvent);
      bool = performPanelShortcut(localPanelFeatureState, paramKeyEvent.getKeyCode(), paramKeyEvent, 1);
      isPrepared = false;
    } while (bool);
    return false;
  }
  
  boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool1 = true;
    switch (paramInt)
    {
    }
    do
    {
      bool1 = false;
      boolean bool2;
      PanelFeatureState localPanelFeatureState;
      do
      {
        return bool1;
        onKeyUpPanel(0, paramKeyEvent);
        return bool1;
        bool2 = mLongPressBackDown;
        mLongPressBackDown = false;
        localPanelFeatureState = getPanelState(0, false);
        if ((localPanelFeatureState == null) || (!isOpen)) {
          break;
        }
      } while (bool2);
      closePanel(localPanelFeatureState, bool1);
      return bool1;
    } while (!onBackPressed());
    return bool1;
  }
  
  public boolean onMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem)
  {
    Window.Callback localCallback = getWindowCallback();
    if ((localCallback != null) && (!isDestroyed()))
    {
      PanelFeatureState localPanelFeatureState = findMenuPanel(paramMenuBuilder.getRootMenu());
      if (localPanelFeatureState != null) {
        return localCallback.onMenuItemSelected(featureId, paramMenuItem);
      }
    }
    return false;
  }
  
  public void onMenuModeChange(MenuBuilder paramMenuBuilder)
  {
    reopenMenu(paramMenuBuilder, true);
  }
  
  boolean onMenuOpened(int paramInt, Menu paramMenu)
  {
    if (paramInt == 108)
    {
      ActionBar localActionBar = getSupportActionBar();
      if (localActionBar != null) {
        localActionBar.dispatchMenuVisibilityChanged(true);
      }
      return true;
    }
    return false;
  }
  
  void onPanelClosed(int paramInt, Menu paramMenu)
  {
    if (paramInt == 108)
    {
      ActionBar localActionBar = getSupportActionBar();
      if (localActionBar != null) {
        localActionBar.dispatchMenuVisibilityChanged(false);
      }
    }
    PanelFeatureState localPanelFeatureState;
    do
    {
      do
      {
        return;
      } while (paramInt != 0);
      localPanelFeatureState = getPanelState(paramInt, true);
    } while (!isOpen);
    closePanel(localPanelFeatureState, false);
  }
  
  public void onPostCreate(Bundle paramBundle)
  {
    ensureSubDecor();
  }
  
  public void onPostResume()
  {
    ActionBar localActionBar = getSupportActionBar();
    if (localActionBar != null) {
      localActionBar.setShowHideAnimationEnabled(true);
    }
  }
  
  public void onStop()
  {
    ActionBar localActionBar = getSupportActionBar();
    if (localActionBar != null) {
      localActionBar.setShowHideAnimationEnabled(false);
    }
  }
  
  void onSubDecorInstalled(ViewGroup paramViewGroup) {}
  
  void onTitleChanged(CharSequence paramCharSequence)
  {
    if (mDecorContentParent != null) {
      mDecorContentParent.setWindowTitle(paramCharSequence);
    }
    do
    {
      return;
      if (peekSupportActionBar() != null)
      {
        peekSupportActionBar().setWindowTitle(paramCharSequence);
        return;
      }
    } while (mTitleView == null);
    mTitleView.setText(paramCharSequence);
  }
  
  public boolean requestWindowFeature(int paramInt)
  {
    int i = sanitizeWindowFeatureId(paramInt);
    if ((mWindowNoTitle) && (i == 108)) {
      return false;
    }
    if ((mHasActionBar) && (i == 1)) {
      mHasActionBar = false;
    }
    switch (i)
    {
    default: 
      return mWindow.requestFeature(i);
    case 108: 
      throwFeatureRequestIfSubDecorInstalled();
      mHasActionBar = true;
      return true;
    case 109: 
      throwFeatureRequestIfSubDecorInstalled();
      mOverlayActionBar = true;
      return true;
    case 10: 
      throwFeatureRequestIfSubDecorInstalled();
      mOverlayActionMode = true;
      return true;
    case 2: 
      throwFeatureRequestIfSubDecorInstalled();
      mFeatureProgress = true;
      return true;
    case 5: 
      throwFeatureRequestIfSubDecorInstalled();
      mFeatureIndeterminateProgress = true;
      return true;
    }
    throwFeatureRequestIfSubDecorInstalled();
    mWindowNoTitle = true;
    return true;
  }
  
  public void setContentView(int paramInt)
  {
    ensureSubDecor();
    ViewGroup localViewGroup = (ViewGroup)mSubDecor.findViewById(16908290);
    localViewGroup.removeAllViews();
    LayoutInflater.from(mContext).inflate(paramInt, localViewGroup);
    mOriginalWindowCallback.onContentChanged();
  }
  
  public void setContentView(View paramView)
  {
    ensureSubDecor();
    ViewGroup localViewGroup = (ViewGroup)mSubDecor.findViewById(16908290);
    localViewGroup.removeAllViews();
    localViewGroup.addView(paramView);
    mOriginalWindowCallback.onContentChanged();
  }
  
  public void setContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    ensureSubDecor();
    ViewGroup localViewGroup = (ViewGroup)mSubDecor.findViewById(16908290);
    localViewGroup.removeAllViews();
    localViewGroup.addView(paramView, paramLayoutParams);
    mOriginalWindowCallback.onContentChanged();
  }
  
  public void setSupportActionBar(Toolbar paramToolbar)
  {
    if (!(mOriginalWindowCallback instanceof Activity)) {
      return;
    }
    ActionBar localActionBar = getSupportActionBar();
    if ((localActionBar instanceof WindowDecorActionBar)) {
      throw new IllegalStateException("This Activity already has an action bar supplied by the window decor. Do not request Window.FEATURE_SUPPORT_ACTION_BAR and set windowActionBar to false in your theme to use a Toolbar instead.");
    }
    mMenuInflater = null;
    if (localActionBar != null) {
      localActionBar.onDestroy();
    }
    if (paramToolbar != null)
    {
      ToolbarActionBar localToolbarActionBar = new ToolbarActionBar(paramToolbar, ((Activity)mContext).getTitle(), mAppCompatWindowCallback);
      mActionBar = localToolbarActionBar;
      mWindow.setCallback(localToolbarActionBar.getWrappedWindowCallback());
    }
    for (;;)
    {
      invalidateOptionsMenu();
      return;
      mActionBar = null;
      mWindow.setCallback(mAppCompatWindowCallback);
    }
  }
  
  final boolean shouldAnimateActionModeView()
  {
    return (mSubDecorInstalled) && (mSubDecor != null) && (ViewCompat.isLaidOut(mSubDecor));
  }
  
  public ActionMode startSupportActionMode(@NonNull ActionMode.Callback paramCallback)
  {
    if (paramCallback == null) {
      throw new IllegalArgumentException("ActionMode callback can not be null.");
    }
    if (mActionMode != null) {
      mActionMode.finish();
    }
    ActionModeCallbackWrapperV9 localActionModeCallbackWrapperV9 = new ActionModeCallbackWrapperV9(paramCallback);
    ActionBar localActionBar = getSupportActionBar();
    if (localActionBar != null)
    {
      mActionMode = localActionBar.startActionMode(localActionModeCallbackWrapperV9);
      if ((mActionMode != null) && (mAppCompatCallback != null)) {
        mAppCompatCallback.onSupportActionModeStarted(mActionMode);
      }
    }
    if (mActionMode == null) {
      mActionMode = startSupportActionModeFromWindow(localActionModeCallbackWrapperV9);
    }
    return mActionMode;
  }
  
  ActionMode startSupportActionModeFromWindow(@NonNull ActionMode.Callback paramCallback)
  {
    endOnGoingFadeAnimation();
    if (mActionMode != null) {
      mActionMode.finish();
    }
    if (!(paramCallback instanceof ActionModeCallbackWrapperV9)) {
      paramCallback = new ActionModeCallbackWrapperV9(paramCallback);
    }
    AppCompatCallback localAppCompatCallback = mAppCompatCallback;
    localObject1 = null;
    if (localAppCompatCallback != null)
    {
      boolean bool2 = isDestroyed();
      localObject1 = null;
      if (bool2) {}
    }
    try
    {
      ActionMode localActionMode = mAppCompatCallback.onWindowStartingSupportActionMode(paramCallback);
      localObject1 = localActionMode;
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      for (;;)
      {
        label221:
        label341:
        label384:
        label516:
        label561:
        label563:
        label569:
        label618:
        label620:
        localObject1 = null;
      }
    }
    if (localObject1 != null) {
      mActionMode = localObject1;
    }
    for (;;)
    {
      if ((mActionMode != null) && (mAppCompatCallback != null)) {
        mAppCompatCallback.onSupportActionModeStarted(mActionMode);
      }
      return mActionMode;
      Object localObject2;
      boolean bool1;
      if (mActionModeView == null)
      {
        if (!mIsFloating) {
          break label516;
        }
        TypedValue localTypedValue = new TypedValue();
        Resources.Theme localTheme1 = mContext.getTheme();
        localTheme1.resolveAttribute(R.attr.actionBarTheme, localTypedValue, true);
        if (resourceId != 0)
        {
          Resources.Theme localTheme2 = mContext.getResources().newTheme();
          localTheme2.setTo(localTheme1);
          localTheme2.applyStyle(resourceId, true);
          localObject2 = new ContextThemeWrapper(mContext, 0);
          ((Context)localObject2).getTheme().setTo(localTheme2);
          mActionModeView = new ActionBarContextView((Context)localObject2);
          mActionModePopup = new PopupWindow((Context)localObject2, null, R.attr.actionModePopupWindowStyle);
          PopupWindowCompat.setWindowLayoutType(mActionModePopup, 2);
          mActionModePopup.setContentView(mActionModeView);
          mActionModePopup.setWidth(-1);
          ((Context)localObject2).getTheme().resolveAttribute(R.attr.actionBarSize, localTypedValue, true);
          int i = TypedValue.complexToDimensionPixelSize(data, ((Context)localObject2).getResources().getDisplayMetrics());
          mActionModeView.setContentHeight(i);
          mActionModePopup.setHeight(-2);
          mShowActionModePopup = new Runnable()
          {
            public void run()
            {
              mActionModePopup.showAtLocation(mActionModeView, 55, 0, 0);
              endOnGoingFadeAnimation();
              if (shouldAnimateActionModeView())
              {
                ViewCompat.setAlpha(mActionModeView, 0.0F);
                mFadeAnim = ViewCompat.animate(mActionModeView).alpha(1.0F);
                mFadeAnim.setListener(new ViewPropertyAnimatorListenerAdapter()
                {
                  public void onAnimationEnd(View paramAnonymous2View)
                  {
                    ViewCompat.setAlpha(mActionModeView, 1.0F);
                    mFadeAnim.setListener(null);
                    mFadeAnim = null;
                  }
                  
                  public void onAnimationStart(View paramAnonymous2View)
                  {
                    mActionModeView.setVisibility(0);
                  }
                });
                return;
              }
              ViewCompat.setAlpha(mActionModeView, 1.0F);
              mActionModeView.setVisibility(0);
            }
          };
        }
      }
      else
      {
        if (mActionModeView == null) {
          break label561;
        }
        endOnGoingFadeAnimation();
        mActionModeView.killMode();
        Context localContext = mActionModeView.getContext();
        ActionBarContextView localActionBarContextView = mActionModeView;
        if (mActionModePopup != null) {
          break label563;
        }
        bool1 = true;
        StandaloneActionMode localStandaloneActionMode = new StandaloneActionMode(localContext, localActionBarContextView, paramCallback, bool1);
        if (!paramCallback.onCreateActionMode(localStandaloneActionMode, localStandaloneActionMode.getMenu())) {
          break label620;
        }
        localStandaloneActionMode.invalidate();
        mActionModeView.initForMode(localStandaloneActionMode);
        mActionMode = localStandaloneActionMode;
        if (!shouldAnimateActionModeView()) {
          break label569;
        }
        ViewCompat.setAlpha(mActionModeView, 0.0F);
        mFadeAnim = ViewCompat.animate(mActionModeView).alpha(1.0F);
        mFadeAnim.setListener(new ViewPropertyAnimatorListenerAdapter()
        {
          public void onAnimationEnd(View paramAnonymousView)
          {
            ViewCompat.setAlpha(mActionModeView, 1.0F);
            mFadeAnim.setListener(null);
            mFadeAnim = null;
          }
          
          public void onAnimationStart(View paramAnonymousView)
          {
            mActionModeView.setVisibility(0);
            mActionModeView.sendAccessibilityEvent(32);
            if (mActionModeView.getParent() != null) {
              ViewCompat.requestApplyInsets((View)mActionModeView.getParent());
            }
          }
        });
      }
      for (;;)
      {
        if (mActionModePopup == null) {
          break label618;
        }
        mWindow.getDecorView().post(mShowActionModePopup);
        break;
        localObject2 = mContext;
        break label221;
        ViewStubCompat localViewStubCompat = (ViewStubCompat)mSubDecor.findViewById(R.id.action_mode_bar_stub);
        if (localViewStubCompat == null) {
          break label341;
        }
        localViewStubCompat.setLayoutInflater(LayoutInflater.from(getActionBarThemedContext()));
        mActionModeView = ((ActionBarContextView)localViewStubCompat.inflate());
        break label341;
        break;
        bool1 = false;
        break label384;
        ViewCompat.setAlpha(mActionModeView, 1.0F);
        mActionModeView.setVisibility(0);
        mActionModeView.sendAccessibilityEvent(32);
        if (mActionModeView.getParent() != null) {
          ViewCompat.requestApplyInsets((View)mActionModeView.getParent());
        }
      }
      continue;
      mActionMode = null;
    }
  }
  
  int updateStatusGuard(int paramInt)
  {
    ActionBarContextView localActionBarContextView = mActionModeView;
    int i = 0;
    ViewGroup.MarginLayoutParams localMarginLayoutParams;
    int n;
    int m;
    if (localActionBarContextView != null)
    {
      boolean bool = mActionModeView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams;
      i = 0;
      if (bool)
      {
        localMarginLayoutParams = (ViewGroup.MarginLayoutParams)mActionModeView.getLayoutParams();
        if (!mActionModeView.isShown()) {
          break label325;
        }
        if (mTempRect1 == null)
        {
          mTempRect1 = new Rect();
          mTempRect2 = new Rect();
        }
        Rect localRect1 = mTempRect1;
        Rect localRect2 = mTempRect2;
        localRect1.set(0, paramInt, 0, 0);
        ViewUtils.computeFitSystemWindows(mSubDecor, localRect1, localRect2);
        if (top != 0) {
          break label278;
        }
        n = paramInt;
        int i1 = topMargin;
        m = 0;
        if (i1 != n)
        {
          m = 1;
          topMargin = paramInt;
          if (mStatusGuard != null) {
            break label284;
          }
          mStatusGuard = new View(mContext);
          mStatusGuard.setBackgroundColor(mContext.getResources().getColor(R.color.abc_input_method_navigation_guard));
          mSubDecor.addView(mStatusGuard, -1, new ViewGroup.LayoutParams(-1, paramInt));
        }
        label213:
        if (mStatusGuard == null) {
          break label320;
        }
        i = 1;
        label222:
        if ((!mOverlayActionMode) && (i != 0)) {
          paramInt = 0;
        }
        label235:
        if (m != 0) {
          mActionModeView.setLayoutParams(localMarginLayoutParams);
        }
      }
    }
    View localView;
    int j;
    if (mStatusGuard != null)
    {
      localView = mStatusGuard;
      j = 0;
      if (i == 0) {
        break label356;
      }
    }
    for (;;)
    {
      localView.setVisibility(j);
      return paramInt;
      label278:
      n = 0;
      break;
      label284:
      ViewGroup.LayoutParams localLayoutParams = mStatusGuard.getLayoutParams();
      if (height == paramInt) {
        break label213;
      }
      height = paramInt;
      mStatusGuard.setLayoutParams(localLayoutParams);
      break label213;
      label320:
      i = 0;
      break label222;
      label325:
      int k = topMargin;
      m = 0;
      i = 0;
      if (k == 0) {
        break label235;
      }
      m = 1;
      topMargin = 0;
      i = 0;
      break label235;
      label356:
      j = 8;
    }
  }
  
  private final class ActionMenuPresenterCallback
    implements MenuPresenter.Callback
  {
    ActionMenuPresenterCallback() {}
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
    {
      checkCloseActionMenu(paramMenuBuilder);
    }
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      Window.Callback localCallback = getWindowCallback();
      if (localCallback != null) {
        localCallback.onMenuOpened(108, paramMenuBuilder);
      }
      return true;
    }
  }
  
  class ActionModeCallbackWrapperV9
    implements ActionMode.Callback
  {
    private ActionMode.Callback mWrapped;
    
    public ActionModeCallbackWrapperV9(ActionMode.Callback paramCallback)
    {
      mWrapped = paramCallback;
    }
    
    public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
    {
      return mWrapped.onActionItemClicked(paramActionMode, paramMenuItem);
    }
    
    public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      return mWrapped.onCreateActionMode(paramActionMode, paramMenu);
    }
    
    public void onDestroyActionMode(ActionMode paramActionMode)
    {
      mWrapped.onDestroyActionMode(paramActionMode);
      if (mActionModePopup != null) {
        mWindow.getDecorView().removeCallbacks(mShowActionModePopup);
      }
      if (mActionModeView != null)
      {
        endOnGoingFadeAnimation();
        mFadeAnim = ViewCompat.animate(mActionModeView).alpha(0.0F);
        mFadeAnim.setListener(new ViewPropertyAnimatorListenerAdapter()
        {
          public void onAnimationEnd(View paramAnonymousView)
          {
            mActionModeView.setVisibility(8);
            if (mActionModePopup != null) {
              mActionModePopup.dismiss();
            }
            for (;;)
            {
              mActionModeView.removeAllViews();
              mFadeAnim.setListener(null);
              mFadeAnim = null;
              return;
              if ((mActionModeView.getParent() instanceof View)) {
                ViewCompat.requestApplyInsets((View)mActionModeView.getParent());
              }
            }
          }
        });
      }
      if (mAppCompatCallback != null) {
        mAppCompatCallback.onSupportActionModeFinished(mActionMode);
      }
      mActionMode = null;
    }
    
    public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      return mWrapped.onPrepareActionMode(paramActionMode, paramMenu);
    }
  }
  
  private class ListMenuDecorView
    extends ContentFrameLayout
  {
    public ListMenuDecorView(Context paramContext)
    {
      super();
    }
    
    private boolean isOutOfBounds(int paramInt1, int paramInt2)
    {
      return (paramInt1 < -5) || (paramInt2 < -5) || (paramInt1 > 5 + getWidth()) || (paramInt2 > 5 + getHeight());
    }
    
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
    {
      return (AppCompatDelegateImplV9.this.dispatchKeyEvent(paramKeyEvent)) || (super.dispatchKeyEvent(paramKeyEvent));
    }
    
    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
    {
      if ((paramMotionEvent.getAction() == 0) && (isOutOfBounds((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY())))
      {
        closePanel(0);
        return true;
      }
      return super.onInterceptTouchEvent(paramMotionEvent);
    }
    
    public void setBackgroundResource(int paramInt)
    {
      setBackgroundDrawable(AppCompatResources.getDrawable(getContext(), paramInt));
    }
  }
  
  protected static final class PanelFeatureState
  {
    int background;
    View createdPanelView;
    ViewGroup decorView;
    int featureId;
    Bundle frozenActionViewState;
    Bundle frozenMenuState;
    int gravity;
    boolean isHandled;
    boolean isOpen;
    boolean isPrepared;
    ListMenuPresenter listMenuPresenter;
    Context listPresenterContext;
    MenuBuilder menu;
    public boolean qwertyMode;
    boolean refreshDecorView;
    boolean refreshMenuContent;
    View shownPanelView;
    boolean wasLastOpen;
    int windowAnimations;
    int x;
    int y;
    
    PanelFeatureState(int paramInt)
    {
      featureId = paramInt;
      refreshDecorView = false;
    }
    
    void applyFrozenState()
    {
      if ((menu != null) && (frozenMenuState != null))
      {
        menu.restorePresenterStates(frozenMenuState);
        frozenMenuState = null;
      }
    }
    
    public void clearMenuPresenters()
    {
      if (menu != null) {
        menu.removeMenuPresenter(listMenuPresenter);
      }
      listMenuPresenter = null;
    }
    
    MenuView getListMenuView(MenuPresenter.Callback paramCallback)
    {
      if (menu == null) {
        return null;
      }
      if (listMenuPresenter == null)
      {
        listMenuPresenter = new ListMenuPresenter(listPresenterContext, R.layout.abc_list_menu_item_layout);
        listMenuPresenter.setCallback(paramCallback);
        menu.addMenuPresenter(listMenuPresenter);
      }
      return listMenuPresenter.getMenuView(decorView);
    }
    
    public boolean hasPanelItems()
    {
      boolean bool = true;
      if (shownPanelView == null) {
        bool = false;
      }
      while ((createdPanelView != null) || (listMenuPresenter.getAdapter().getCount() > 0)) {
        return bool;
      }
      return false;
    }
    
    void onRestoreInstanceState(Parcelable paramParcelable)
    {
      SavedState localSavedState = (SavedState)paramParcelable;
      featureId = featureId;
      wasLastOpen = isOpen;
      frozenMenuState = menuState;
      shownPanelView = null;
      decorView = null;
    }
    
    Parcelable onSaveInstanceState()
    {
      SavedState localSavedState = new SavedState();
      featureId = featureId;
      isOpen = isOpen;
      if (menu != null)
      {
        menuState = new Bundle();
        menu.savePresenterStates(menuState);
      }
      return localSavedState;
    }
    
    void setMenu(MenuBuilder paramMenuBuilder)
    {
      if (paramMenuBuilder == menu) {}
      do
      {
        return;
        if (menu != null) {
          menu.removeMenuPresenter(listMenuPresenter);
        }
        menu = paramMenuBuilder;
      } while ((paramMenuBuilder == null) || (listMenuPresenter == null));
      paramMenuBuilder.addMenuPresenter(listMenuPresenter);
    }
    
    void setStyle(Context paramContext)
    {
      TypedValue localTypedValue = new TypedValue();
      Resources.Theme localTheme = paramContext.getResources().newTheme();
      localTheme.setTo(paramContext.getTheme());
      localTheme.resolveAttribute(R.attr.actionBarPopupTheme, localTypedValue, true);
      if (resourceId != 0) {
        localTheme.applyStyle(resourceId, true);
      }
      localTheme.resolveAttribute(R.attr.panelMenuListTheme, localTypedValue, true);
      if (resourceId != 0) {
        localTheme.applyStyle(resourceId, true);
      }
      for (;;)
      {
        ContextThemeWrapper localContextThemeWrapper = new ContextThemeWrapper(paramContext, 0);
        localContextThemeWrapper.getTheme().setTo(localTheme);
        listPresenterContext = localContextThemeWrapper;
        TypedArray localTypedArray = localContextThemeWrapper.obtainStyledAttributes(R.styleable.AppCompatTheme);
        background = localTypedArray.getResourceId(R.styleable.AppCompatTheme_panelBackground, 0);
        windowAnimations = localTypedArray.getResourceId(R.styleable.AppCompatTheme_android_windowAnimationStyle, 0);
        localTypedArray.recycle();
        return;
        localTheme.applyStyle(R.style.Theme_AppCompat_CompactMenu, true);
      }
    }
    
    private static class SavedState
      implements Parcelable
    {
      public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
      {
        public AppCompatDelegateImplV9.PanelFeatureState.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
        {
          return AppCompatDelegateImplV9.PanelFeatureState.SavedState.readFromParcel(paramAnonymousParcel, paramAnonymousClassLoader);
        }
        
        public AppCompatDelegateImplV9.PanelFeatureState.SavedState[] newArray(int paramAnonymousInt)
        {
          return new AppCompatDelegateImplV9.PanelFeatureState.SavedState[paramAnonymousInt];
        }
      });
      int featureId;
      boolean isOpen;
      Bundle menuState;
      
      SavedState() {}
      
      static SavedState readFromParcel(Parcel paramParcel, ClassLoader paramClassLoader)
      {
        int i = 1;
        SavedState localSavedState = new SavedState();
        featureId = paramParcel.readInt();
        if (paramParcel.readInt() == i) {}
        for (;;)
        {
          isOpen = i;
          if (isOpen) {
            menuState = paramParcel.readBundle(paramClassLoader);
          }
          return localSavedState;
          i = 0;
        }
      }
      
      public int describeContents()
      {
        return 0;
      }
      
      public void writeToParcel(Parcel paramParcel, int paramInt)
      {
        paramParcel.writeInt(featureId);
        if (isOpen) {}
        for (int i = 1;; i = 0)
        {
          paramParcel.writeInt(i);
          if (isOpen) {
            paramParcel.writeBundle(menuState);
          }
          return;
        }
      }
    }
  }
  
  private final class PanelMenuPresenterCallback
    implements MenuPresenter.Callback
  {
    PanelMenuPresenterCallback() {}
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
    {
      MenuBuilder localMenuBuilder = paramMenuBuilder.getRootMenu();
      if (localMenuBuilder != paramMenuBuilder) {}
      AppCompatDelegateImplV9.PanelFeatureState localPanelFeatureState;
      for (int i = 1;; i = 0)
      {
        AppCompatDelegateImplV9 localAppCompatDelegateImplV9 = AppCompatDelegateImplV9.this;
        if (i != 0) {
          paramMenuBuilder = localMenuBuilder;
        }
        localPanelFeatureState = localAppCompatDelegateImplV9.findMenuPanel(paramMenuBuilder);
        if (localPanelFeatureState != null)
        {
          if (i == 0) {
            break;
          }
          callOnPanelClosed(featureId, localPanelFeatureState, localMenuBuilder);
          closePanel(localPanelFeatureState, true);
        }
        return;
      }
      closePanel(localPanelFeatureState, paramBoolean);
    }
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      if ((paramMenuBuilder == null) && (mHasActionBar))
      {
        Window.Callback localCallback = getWindowCallback();
        if ((localCallback != null) && (!isDestroyed())) {
          localCallback.onMenuOpened(108, paramMenuBuilder);
        }
      }
      return true;
    }
  }
}
