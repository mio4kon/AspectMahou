package org.aspectj.internal.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.aspectj.internal.lang.annotation.ajcDeclareEoW;
import org.aspectj.internal.lang.annotation.ajcDeclareParents;
import org.aspectj.internal.lang.annotation.ajcDeclarePrecedence;
import org.aspectj.internal.lang.annotation.ajcDeclareSoft;
import org.aspectj.internal.lang.annotation.ajcITD;
import org.aspectj.internal.lang.annotation.ajcPrivileged;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareError;
import org.aspectj.lang.annotation.DeclareWarning;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.AdviceKind;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.DeclareAnnotation;
import org.aspectj.lang.reflect.DeclareErrorOrWarning;
import org.aspectj.lang.reflect.DeclareSoft;
import org.aspectj.lang.reflect.InterTypeConstructorDeclaration;
import org.aspectj.lang.reflect.InterTypeFieldDeclaration;
import org.aspectj.lang.reflect.InterTypeMethodDeclaration;
import org.aspectj.lang.reflect.NoSuchAdviceException;
import org.aspectj.lang.reflect.NoSuchPointcutException;
import org.aspectj.lang.reflect.PerClause;
import org.aspectj.lang.reflect.PerClauseKind;

public class AjTypeImpl<T>
  implements AjType<T>
{
  private static final String ajcMagic = "ajc$";
  private Advice[] advice = null;
  private Class<T> clazz;
  private Advice[] declaredAdvice = null;
  private InterTypeConstructorDeclaration[] declaredITDCons = null;
  private InterTypeFieldDeclaration[] declaredITDFields = null;
  private InterTypeMethodDeclaration[] declaredITDMethods = null;
  private org.aspectj.lang.reflect.Pointcut[] declaredPointcuts = null;
  private InterTypeConstructorDeclaration[] itdCons = null;
  private InterTypeFieldDeclaration[] itdFields = null;
  private InterTypeMethodDeclaration[] itdMethods = null;
  private org.aspectj.lang.reflect.Pointcut[] pointcuts = null;
  
  public AjTypeImpl(Class<T> paramClass)
  {
    clazz = paramClass;
  }
  
  private void addAnnotationStyleDeclareParents(List<org.aspectj.lang.reflect.DeclareParents> paramList)
  {
    Field[] arrayOfField = clazz.getDeclaredFields();
    int i = arrayOfField.length;
    int j = 0;
    if (j < i)
    {
      Field localField = arrayOfField[j];
      if ((!localField.isAnnotationPresent(org.aspectj.lang.annotation.DeclareParents.class)) || (!localField.getType().isInterface())) {}
      for (;;)
      {
        j++;
        break;
        org.aspectj.lang.annotation.DeclareParents localDeclareParents = (org.aspectj.lang.annotation.DeclareParents)localField.getAnnotation(org.aspectj.lang.annotation.DeclareParents.class);
        String str = localField.getType().getName();
        paramList.add(new DeclareParentsImpl(localDeclareParents.value(), str, false, this));
      }
    }
  }
  
  private void addAnnotationStyleITDFields(List<InterTypeFieldDeclaration> paramList, boolean paramBoolean) {}
  
  private void addAnnotationStyleITDMethods(List<InterTypeMethodDeclaration> paramList, boolean paramBoolean)
  {
    if (isAspect())
    {
      Field[] arrayOfField = clazz.getDeclaredFields();
      int i = arrayOfField.length;
      int j = 0;
      if (j < i)
      {
        Field localField = arrayOfField[j];
        if (!localField.getType().isInterface()) {}
        while ((!localField.isAnnotationPresent(org.aspectj.lang.annotation.DeclareParents.class)) || (((org.aspectj.lang.annotation.DeclareParents)localField.getAnnotation(org.aspectj.lang.annotation.DeclareParents.class)).defaultImpl() == org.aspectj.lang.annotation.DeclareParents.class))
        {
          j++;
          break;
        }
        Method[] arrayOfMethod = localField.getType().getDeclaredMethods();
        int k = arrayOfMethod.length;
        int m = 0;
        label100:
        Method localMethod;
        if (m < k)
        {
          localMethod = arrayOfMethod[m];
          if ((Modifier.isPublic(localMethod.getModifiers())) || (!paramBoolean)) {
            break label135;
          }
        }
        for (;;)
        {
          m++;
          break label100;
          break;
          label135:
          paramList.add(new InterTypeMethodDeclarationImpl(this, AjTypeSystem.getAjType(localField.getType()), localMethod, 1));
        }
      }
    }
  }
  
  private Advice asAdvice(Method paramMethod)
  {
    if (paramMethod.getAnnotations().length == 0) {}
    Around localAround;
    do
    {
      return null;
      Before localBefore = (Before)paramMethod.getAnnotation(Before.class);
      if (localBefore != null) {
        return new AdviceImpl(paramMethod, localBefore.value(), AdviceKind.BEFORE);
      }
      After localAfter = (After)paramMethod.getAnnotation(After.class);
      if (localAfter != null) {
        return new AdviceImpl(paramMethod, localAfter.value(), AdviceKind.AFTER);
      }
      AfterReturning localAfterReturning = (AfterReturning)paramMethod.getAnnotation(AfterReturning.class);
      if (localAfterReturning != null)
      {
        String str2 = localAfterReturning.pointcut();
        if (str2.equals("")) {
          str2 = localAfterReturning.value();
        }
        return new AdviceImpl(paramMethod, str2, AdviceKind.AFTER_RETURNING, localAfterReturning.returning());
      }
      AfterThrowing localAfterThrowing = (AfterThrowing)paramMethod.getAnnotation(AfterThrowing.class);
      if (localAfterThrowing != null)
      {
        String str1 = localAfterThrowing.pointcut();
        if (str1 == null) {
          str1 = localAfterThrowing.value();
        }
        return new AdviceImpl(paramMethod, str1, AdviceKind.AFTER_THROWING, localAfterThrowing.throwing());
      }
      localAround = (Around)paramMethod.getAnnotation(Around.class);
    } while (localAround == null);
    return new AdviceImpl(paramMethod, localAround.value(), AdviceKind.AROUND);
  }
  
  private org.aspectj.lang.reflect.Pointcut asPointcut(Method paramMethod)
  {
    org.aspectj.lang.annotation.Pointcut localPointcut = (org.aspectj.lang.annotation.Pointcut)paramMethod.getAnnotation(org.aspectj.lang.annotation.Pointcut.class);
    if (localPointcut != null)
    {
      String str = paramMethod.getName();
      if (str.startsWith("ajc$"))
      {
        str = str.substring(2 + str.indexOf("$$"), str.length());
        int i = str.indexOf("$");
        if (i != -1) {
          str = str.substring(0, i);
        }
      }
      return new PointcutImpl(str, localPointcut.value(), paramMethod, AjTypeSystem.getAjType(paramMethod.getDeclaringClass()), localPointcut.argNames());
    }
    return null;
  }
  
  private Advice[] getAdvice(Set paramSet)
  {
    if (advice == null) {
      initAdvice();
    }
    ArrayList localArrayList = new ArrayList();
    for (Advice localAdvice : advice) {
      if (paramSet.contains(localAdvice.getKind())) {
        localArrayList.add(localAdvice);
      }
    }
    Advice[] arrayOfAdvice2 = new Advice[localArrayList.size()];
    localArrayList.toArray(arrayOfAdvice2);
    return arrayOfAdvice2;
  }
  
  private Advice[] getDeclaredAdvice(Set paramSet)
  {
    if (declaredAdvice == null) {
      initDeclaredAdvice();
    }
    ArrayList localArrayList = new ArrayList();
    for (Advice localAdvice : declaredAdvice) {
      if (paramSet.contains(localAdvice.getKind())) {
        localArrayList.add(localAdvice);
      }
    }
    Advice[] arrayOfAdvice2 = new Advice[localArrayList.size()];
    localArrayList.toArray(arrayOfAdvice2);
    return arrayOfAdvice2;
  }
  
  private void initAdvice()
  {
    Method[] arrayOfMethod = clazz.getMethods();
    ArrayList localArrayList = new ArrayList();
    int i = arrayOfMethod.length;
    for (int j = 0; j < i; j++)
    {
      Advice localAdvice = asAdvice(arrayOfMethod[j]);
      if (localAdvice != null) {
        localArrayList.add(localAdvice);
      }
    }
    advice = new Advice[localArrayList.size()];
    localArrayList.toArray(advice);
  }
  
  private void initDeclaredAdvice()
  {
    Method[] arrayOfMethod = clazz.getDeclaredMethods();
    ArrayList localArrayList = new ArrayList();
    int i = arrayOfMethod.length;
    for (int j = 0; j < i; j++)
    {
      Advice localAdvice = asAdvice(arrayOfMethod[j]);
      if (localAdvice != null) {
        localArrayList.add(localAdvice);
      }
    }
    declaredAdvice = new Advice[localArrayList.size()];
    localArrayList.toArray(declaredAdvice);
  }
  
  private boolean isReallyAMethod(Method paramMethod)
  {
    if (paramMethod.getName().startsWith("ajc$")) {}
    do
    {
      return false;
      if (paramMethod.getAnnotations().length == 0) {
        return true;
      }
    } while ((paramMethod.isAnnotationPresent(org.aspectj.lang.annotation.Pointcut.class)) || (paramMethod.isAnnotationPresent(Before.class)) || (paramMethod.isAnnotationPresent(After.class)) || (paramMethod.isAnnotationPresent(AfterReturning.class)) || (paramMethod.isAnnotationPresent(AfterThrowing.class)) || (paramMethod.isAnnotationPresent(Around.class)));
    return true;
  }
  
  private AjType<?>[] toAjTypeArray(Class<?>[] paramArrayOfClass)
  {
    AjType[] arrayOfAjType = new AjType[paramArrayOfClass.length];
    for (int i = 0; i < arrayOfAjType.length; i++) {
      arrayOfAjType[i] = AjTypeSystem.getAjType(paramArrayOfClass[i]);
    }
    return arrayOfAjType;
  }
  
  private Class<?>[] toClassArray(AjType<?>[] paramArrayOfAjType)
  {
    Class[] arrayOfClass = new Class[paramArrayOfAjType.length];
    for (int i = 0; i < arrayOfClass.length; i++) {
      arrayOfClass[i] = paramArrayOfAjType[i].getJavaClass();
    }
    return arrayOfClass;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof AjTypeImpl)) {
      return false;
    }
    return clazz.equals(clazz);
  }
  
  public Advice getAdvice(String paramString)
    throws NoSuchAdviceException
  {
    if (paramString.equals("")) {
      throw new IllegalArgumentException("use getAdvice(AdviceType...) instead for un-named advice");
    }
    if (advice == null) {
      initAdvice();
    }
    for (Advice localAdvice : advice) {
      if (localAdvice.getName().equals(paramString)) {
        return localAdvice;
      }
    }
    throw new NoSuchAdviceException(paramString);
  }
  
  public Advice[] getAdvice(AdviceKind... paramVarArgs)
  {
    EnumSet localEnumSet;
    if (paramVarArgs.length == 0) {
      localEnumSet = EnumSet.allOf(AdviceKind.class);
    }
    for (;;)
    {
      return getAdvice(localEnumSet);
      localEnumSet = EnumSet.noneOf(AdviceKind.class);
      localEnumSet.addAll(Arrays.asList(paramVarArgs));
    }
  }
  
  public AjType<?>[] getAjTypes()
  {
    return toAjTypeArray(clazz.getClasses());
  }
  
  public <A extends Annotation> A getAnnotation(Class<A> paramClass)
  {
    return clazz.getAnnotation(paramClass);
  }
  
  public Annotation[] getAnnotations()
  {
    return clazz.getAnnotations();
  }
  
  public Constructor getConstructor(AjType<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    return clazz.getConstructor(toClassArray(paramVarArgs));
  }
  
  public Constructor[] getConstructors()
  {
    return clazz.getConstructors();
  }
  
  public DeclareAnnotation[] getDeclareAnnotations()
  {
    ArrayList localArrayList = new ArrayList();
    Method[] arrayOfMethod = clazz.getDeclaredMethods();
    int i = arrayOfMethod.length;
    int j = 0;
    if (j < i)
    {
      Method localMethod = arrayOfMethod[j];
      ajcDeclareAnnotation localAjcDeclareAnnotation;
      Annotation[] arrayOfAnnotation;
      int k;
      if (localMethod.isAnnotationPresent(ajcDeclareAnnotation.class))
      {
        localAjcDeclareAnnotation = (ajcDeclareAnnotation)localMethod.getAnnotation(ajcDeclareAnnotation.class);
        arrayOfAnnotation = localMethod.getAnnotations();
        k = arrayOfAnnotation.length;
      }
      for (int m = 0;; m++)
      {
        Object localObject = null;
        if (m < k)
        {
          Annotation localAnnotation = arrayOfAnnotation[m];
          if (localAnnotation.annotationType() != ajcDeclareAnnotation.class) {
            localObject = localAnnotation;
          }
        }
        else
        {
          localArrayList.add(new DeclareAnnotationImpl(this, localAjcDeclareAnnotation.kind(), localAjcDeclareAnnotation.pattern(), localObject, localAjcDeclareAnnotation.annotation()));
          j++;
          break;
        }
      }
    }
    if (getSupertype().isAspect()) {
      localArrayList.addAll(Arrays.asList(getSupertype().getDeclareAnnotations()));
    }
    DeclareAnnotation[] arrayOfDeclareAnnotation = new DeclareAnnotation[localArrayList.size()];
    localArrayList.toArray(arrayOfDeclareAnnotation);
    return arrayOfDeclareAnnotation;
  }
  
  public DeclareErrorOrWarning[] getDeclareErrorOrWarnings()
  {
    int i = 0;
    ArrayList localArrayList = new ArrayList();
    Field[] arrayOfField = clazz.getDeclaredFields();
    int j = arrayOfField.length;
    for (int k = 0;; k++)
    {
      Field localField;
      if (k < j) {
        localField = arrayOfField[k];
      }
      try
      {
        if (localField.isAnnotationPresent(DeclareWarning.class))
        {
          DeclareWarning localDeclareWarning = (DeclareWarning)localField.getAnnotation(DeclareWarning.class);
          if ((Modifier.isPublic(localField.getModifiers())) && (Modifier.isStatic(localField.getModifiers())))
          {
            String str2 = (String)localField.get(null);
            localArrayList.add(new DeclareErrorOrWarningImpl(localDeclareWarning.value(), str2, false, this));
          }
        }
        else if (localField.isAnnotationPresent(DeclareError.class))
        {
          DeclareError localDeclareError = (DeclareError)localField.getAnnotation(DeclareError.class);
          if ((Modifier.isPublic(localField.getModifiers())) && (Modifier.isStatic(localField.getModifiers())))
          {
            String str1 = (String)localField.get(null);
            localArrayList.add(new DeclareErrorOrWarningImpl(localDeclareError.value(), str1, true, this));
          }
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        continue;
        Method[] arrayOfMethod = clazz.getDeclaredMethods();
        int m = arrayOfMethod.length;
        while (i < m)
        {
          Method localMethod = arrayOfMethod[i];
          if (localMethod.isAnnotationPresent(ajcDeclareEoW.class))
          {
            ajcDeclareEoW localAjcDeclareEoW = (ajcDeclareEoW)localMethod.getAnnotation(ajcDeclareEoW.class);
            localArrayList.add(new DeclareErrorOrWarningImpl(localAjcDeclareEoW.pointcut(), localAjcDeclareEoW.message(), localAjcDeclareEoW.isError(), this));
          }
          i++;
        }
        DeclareErrorOrWarning[] arrayOfDeclareErrorOrWarning = new DeclareErrorOrWarning[localArrayList.size()];
        localArrayList.toArray(arrayOfDeclareErrorOrWarning);
        return arrayOfDeclareErrorOrWarning;
      }
      catch (IllegalAccessException localIllegalAccessException) {}
    }
  }
  
  public org.aspectj.lang.reflect.DeclareParents[] getDeclareParents()
  {
    ArrayList localArrayList = new ArrayList();
    for (Method localMethod : clazz.getDeclaredMethods()) {
      if (localMethod.isAnnotationPresent(ajcDeclareParents.class))
      {
        ajcDeclareParents localAjcDeclareParents = (ajcDeclareParents)localMethod.getAnnotation(ajcDeclareParents.class);
        localArrayList.add(new DeclareParentsImpl(localAjcDeclareParents.targetTypePattern(), localAjcDeclareParents.parentTypes(), localAjcDeclareParents.isExtends(), this));
      }
    }
    addAnnotationStyleDeclareParents(localArrayList);
    if (getSupertype().isAspect()) {
      localArrayList.addAll(Arrays.asList(getSupertype().getDeclareParents()));
    }
    org.aspectj.lang.reflect.DeclareParents[] arrayOfDeclareParents = new org.aspectj.lang.reflect.DeclareParents[localArrayList.size()];
    localArrayList.toArray(arrayOfDeclareParents);
    return arrayOfDeclareParents;
  }
  
  public org.aspectj.lang.reflect.DeclarePrecedence[] getDeclarePrecedence()
  {
    ArrayList localArrayList = new ArrayList();
    if (clazz.isAnnotationPresent(org.aspectj.lang.annotation.DeclarePrecedence.class)) {
      localArrayList.add(new DeclarePrecedenceImpl(((org.aspectj.lang.annotation.DeclarePrecedence)clazz.getAnnotation(org.aspectj.lang.annotation.DeclarePrecedence.class)).value(), this));
    }
    for (Method localMethod : clazz.getDeclaredMethods()) {
      if (localMethod.isAnnotationPresent(ajcDeclarePrecedence.class)) {
        localArrayList.add(new DeclarePrecedenceImpl(((ajcDeclarePrecedence)localMethod.getAnnotation(ajcDeclarePrecedence.class)).value(), this));
      }
    }
    if (getSupertype().isAspect()) {
      localArrayList.addAll(Arrays.asList(getSupertype().getDeclarePrecedence()));
    }
    org.aspectj.lang.reflect.DeclarePrecedence[] arrayOfDeclarePrecedence = new org.aspectj.lang.reflect.DeclarePrecedence[localArrayList.size()];
    localArrayList.toArray(arrayOfDeclarePrecedence);
    return arrayOfDeclarePrecedence;
  }
  
  public DeclareSoft[] getDeclareSofts()
  {
    ArrayList localArrayList = new ArrayList();
    for (Method localMethod : clazz.getDeclaredMethods()) {
      if (localMethod.isAnnotationPresent(ajcDeclareSoft.class))
      {
        ajcDeclareSoft localAjcDeclareSoft = (ajcDeclareSoft)localMethod.getAnnotation(ajcDeclareSoft.class);
        localArrayList.add(new DeclareSoftImpl(this, localAjcDeclareSoft.pointcut(), localAjcDeclareSoft.exceptionType()));
      }
    }
    if (getSupertype().isAspect()) {
      localArrayList.addAll(Arrays.asList(getSupertype().getDeclareSofts()));
    }
    DeclareSoft[] arrayOfDeclareSoft = new DeclareSoft[localArrayList.size()];
    localArrayList.toArray(arrayOfDeclareSoft);
    return arrayOfDeclareSoft;
  }
  
  public Advice getDeclaredAdvice(String paramString)
    throws NoSuchAdviceException
  {
    if (paramString.equals("")) {
      throw new IllegalArgumentException("use getAdvice(AdviceType...) instead for un-named advice");
    }
    if (declaredAdvice == null) {
      initDeclaredAdvice();
    }
    for (Advice localAdvice : declaredAdvice) {
      if (localAdvice.getName().equals(paramString)) {
        return localAdvice;
      }
    }
    throw new NoSuchAdviceException(paramString);
  }
  
  public Advice[] getDeclaredAdvice(AdviceKind... paramVarArgs)
  {
    EnumSet localEnumSet;
    if (paramVarArgs.length == 0) {
      localEnumSet = EnumSet.allOf(AdviceKind.class);
    }
    for (;;)
    {
      return getDeclaredAdvice(localEnumSet);
      localEnumSet = EnumSet.noneOf(AdviceKind.class);
      localEnumSet.addAll(Arrays.asList(paramVarArgs));
    }
  }
  
  public AjType<?>[] getDeclaredAjTypes()
  {
    return toAjTypeArray(clazz.getDeclaredClasses());
  }
  
  public Annotation[] getDeclaredAnnotations()
  {
    return clazz.getDeclaredAnnotations();
  }
  
  public Constructor getDeclaredConstructor(AjType<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    return clazz.getDeclaredConstructor(toClassArray(paramVarArgs));
  }
  
  public Constructor[] getDeclaredConstructors()
  {
    return clazz.getDeclaredConstructors();
  }
  
  public Field getDeclaredField(String paramString)
    throws NoSuchFieldException
  {
    Field localField = clazz.getDeclaredField(paramString);
    if (localField.getName().startsWith("ajc$")) {
      throw new NoSuchFieldException(paramString);
    }
    return localField;
  }
  
  public Field[] getDeclaredFields()
  {
    Field[] arrayOfField1 = clazz.getDeclaredFields();
    ArrayList localArrayList = new ArrayList();
    int i = arrayOfField1.length;
    for (int j = 0; j < i; j++)
    {
      Field localField = arrayOfField1[j];
      if ((!localField.getName().startsWith("ajc$")) && (!localField.isAnnotationPresent(DeclareWarning.class)) && (!localField.isAnnotationPresent(DeclareError.class))) {
        localArrayList.add(localField);
      }
    }
    Field[] arrayOfField2 = new Field[localArrayList.size()];
    localArrayList.toArray(arrayOfField2);
    return arrayOfField2;
  }
  
  public InterTypeConstructorDeclaration getDeclaredITDConstructor(AjType<?> paramAjType, AjType<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    InterTypeConstructorDeclaration[] arrayOfInterTypeConstructorDeclaration = getDeclaredITDConstructors();
    int i = arrayOfInterTypeConstructorDeclaration.length;
    int j = 0;
    InterTypeConstructorDeclaration localInterTypeConstructorDeclaration;
    if (j < i)
    {
      localInterTypeConstructorDeclaration = arrayOfInterTypeConstructorDeclaration[j];
      for (;;)
      {
        try
        {
          if (localInterTypeConstructorDeclaration.getTargetType().equals(paramAjType))
          {
            AjType[] arrayOfAjType = localInterTypeConstructorDeclaration.getParameterTypes();
            if (arrayOfAjType.length == paramVarArgs.length)
            {
              k = 0;
              if (k >= arrayOfAjType.length) {
                continue;
              }
              boolean bool = arrayOfAjType[k].equals(paramVarArgs[k]);
              if (bool) {
                continue;
              }
            }
          }
          j++;
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          int k;
          continue;
        }
        break;
        k++;
      }
    }
    throw new NoSuchMethodException();
    return localInterTypeConstructorDeclaration;
  }
  
  public InterTypeConstructorDeclaration[] getDeclaredITDConstructors()
  {
    if (declaredITDCons == null)
    {
      ArrayList localArrayList = new ArrayList();
      Method[] arrayOfMethod = clazz.getDeclaredMethods();
      int i = arrayOfMethod.length;
      int j = 0;
      if (j < i)
      {
        Method localMethod = arrayOfMethod[j];
        if (!localMethod.getName().contains("ajc$postInterConstructor")) {}
        for (;;)
        {
          j++;
          break;
          if (localMethod.isAnnotationPresent(ajcITD.class))
          {
            ajcITD localAjcITD = (ajcITD)localMethod.getAnnotation(ajcITD.class);
            localArrayList.add(new InterTypeConstructorDeclarationImpl(this, localAjcITD.targetType(), localAjcITD.modifiers(), localMethod));
          }
        }
      }
      declaredITDCons = new InterTypeConstructorDeclaration[localArrayList.size()];
      localArrayList.toArray(declaredITDCons);
    }
    return declaredITDCons;
  }
  
  public InterTypeFieldDeclaration getDeclaredITDField(String paramString, AjType<?> paramAjType)
    throws NoSuchFieldException
  {
    for (InterTypeFieldDeclaration localInterTypeFieldDeclaration : getDeclaredITDFields()) {
      if (localInterTypeFieldDeclaration.getName().equals(paramString)) {
        try
        {
          boolean bool = localInterTypeFieldDeclaration.getTargetType().equals(paramAjType);
          if (bool) {
            return localInterTypeFieldDeclaration;
          }
        }
        catch (ClassNotFoundException localClassNotFoundException) {}
      }
    }
    throw new NoSuchFieldException(paramString);
  }
  
  public InterTypeFieldDeclaration[] getDeclaredITDFields()
  {
    ArrayList localArrayList = new ArrayList();
    if (declaredITDFields == null)
    {
      Method[] arrayOfMethod = clazz.getDeclaredMethods();
      int i = arrayOfMethod.length;
      int j = 0;
      if (j < i)
      {
        Method localMethod1 = arrayOfMethod[j];
        if ((!localMethod1.isAnnotationPresent(ajcITD.class)) || (!localMethod1.getName().contains("ajc$interFieldInit"))) {}
        for (;;)
        {
          j++;
          break;
          ajcITD localAjcITD = (ajcITD)localMethod1.getAnnotation(ajcITD.class);
          String str = localMethod1.getName().replace("FieldInit", "FieldGetDispatch");
          try
          {
            Method localMethod2 = clazz.getDeclaredMethod(str, localMethod1.getParameterTypes());
            localArrayList.add(new InterTypeFieldDeclarationImpl(this, localAjcITD.targetType(), localAjcITD.modifiers(), localAjcITD.name(), AjTypeSystem.getAjType(localMethod2.getReturnType()), localMethod2.getGenericReturnType()));
          }
          catch (NoSuchMethodException localNoSuchMethodException)
          {
            throw new IllegalStateException("Can't find field get dispatch method for " + localMethod1.getName());
          }
        }
      }
      addAnnotationStyleITDFields(localArrayList, false);
      declaredITDFields = new InterTypeFieldDeclaration[localArrayList.size()];
      localArrayList.toArray(declaredITDFields);
    }
    return declaredITDFields;
  }
  
  public InterTypeMethodDeclaration getDeclaredITDMethod(String paramString, AjType<?> paramAjType, AjType<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    InterTypeMethodDeclaration[] arrayOfInterTypeMethodDeclaration = getDeclaredITDMethods();
    int i = arrayOfInterTypeMethodDeclaration.length;
    int j = 0;
    for (;;)
    {
      InterTypeMethodDeclaration localInterTypeMethodDeclaration;
      if (j < i) {
        localInterTypeMethodDeclaration = arrayOfInterTypeMethodDeclaration[j];
      }
      try
      {
        if ((localInterTypeMethodDeclaration.getName().equals(paramString)) && (localInterTypeMethodDeclaration.getTargetType().equals(paramAjType)))
        {
          AjType[] arrayOfAjType = localInterTypeMethodDeclaration.getParameterTypes();
          if (arrayOfAjType.length == paramVarArgs.length)
          {
            int k = 0;
            while (k < arrayOfAjType.length)
            {
              boolean bool = arrayOfAjType[k].equals(paramVarArgs[k]);
              if (!bool) {
                break label126;
              }
              k++;
              continue;
              throw new NoSuchMethodException(paramString);
            }
            return localInterTypeMethodDeclaration;
          }
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        label126:
        j++;
      }
    }
  }
  
  public InterTypeMethodDeclaration[] getDeclaredITDMethods()
  {
    if (declaredITDMethods == null)
    {
      ArrayList localArrayList = new ArrayList();
      Method[] arrayOfMethod = clazz.getDeclaredMethods();
      int i = arrayOfMethod.length;
      int j = 0;
      if (j < i)
      {
        Method localMethod = arrayOfMethod[j];
        if (!localMethod.getName().contains("ajc$interMethodDispatch1$")) {}
        for (;;)
        {
          j++;
          break;
          if (localMethod.isAnnotationPresent(ajcITD.class))
          {
            ajcITD localAjcITD = (ajcITD)localMethod.getAnnotation(ajcITD.class);
            localArrayList.add(new InterTypeMethodDeclarationImpl(this, localAjcITD.targetType(), localAjcITD.modifiers(), localAjcITD.name(), localMethod));
          }
        }
      }
      addAnnotationStyleITDMethods(localArrayList, false);
      declaredITDMethods = new InterTypeMethodDeclaration[localArrayList.size()];
      localArrayList.toArray(declaredITDMethods);
    }
    return declaredITDMethods;
  }
  
  public Method getDeclaredMethod(String paramString, AjType<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    Method localMethod = clazz.getDeclaredMethod(paramString, toClassArray(paramVarArgs));
    if (!isReallyAMethod(localMethod)) {
      throw new NoSuchMethodException(paramString);
    }
    return localMethod;
  }
  
  public Method[] getDeclaredMethods()
  {
    Method[] arrayOfMethod1 = clazz.getDeclaredMethods();
    ArrayList localArrayList = new ArrayList();
    int i = arrayOfMethod1.length;
    for (int j = 0; j < i; j++)
    {
      Method localMethod = arrayOfMethod1[j];
      if (isReallyAMethod(localMethod)) {
        localArrayList.add(localMethod);
      }
    }
    Method[] arrayOfMethod2 = new Method[localArrayList.size()];
    localArrayList.toArray(arrayOfMethod2);
    return arrayOfMethod2;
  }
  
  public org.aspectj.lang.reflect.Pointcut getDeclaredPointcut(String paramString)
    throws NoSuchPointcutException
  {
    for (org.aspectj.lang.reflect.Pointcut localPointcut : getDeclaredPointcuts()) {
      if (localPointcut.getName().equals(paramString)) {
        return localPointcut;
      }
    }
    throw new NoSuchPointcutException(paramString);
  }
  
  public org.aspectj.lang.reflect.Pointcut[] getDeclaredPointcuts()
  {
    if (declaredPointcuts != null) {
      return declaredPointcuts;
    }
    ArrayList localArrayList = new ArrayList();
    Method[] arrayOfMethod = clazz.getDeclaredMethods();
    int i = arrayOfMethod.length;
    for (int j = 0; j < i; j++)
    {
      org.aspectj.lang.reflect.Pointcut localPointcut = asPointcut(arrayOfMethod[j]);
      if (localPointcut != null) {
        localArrayList.add(localPointcut);
      }
    }
    org.aspectj.lang.reflect.Pointcut[] arrayOfPointcut = new org.aspectj.lang.reflect.Pointcut[localArrayList.size()];
    localArrayList.toArray(arrayOfPointcut);
    declaredPointcuts = arrayOfPointcut;
    return arrayOfPointcut;
  }
  
  public AjType<?> getDeclaringType()
  {
    Class localClass = clazz.getDeclaringClass();
    if (localClass != null) {
      return new AjTypeImpl(localClass);
    }
    return null;
  }
  
  public Constructor getEnclosingConstructor()
  {
    return clazz.getEnclosingConstructor();
  }
  
  public Method getEnclosingMethod()
  {
    return clazz.getEnclosingMethod();
  }
  
  public AjType<?> getEnclosingType()
  {
    Class localClass = clazz.getEnclosingClass();
    if (localClass != null) {
      return new AjTypeImpl(localClass);
    }
    return null;
  }
  
  public T[] getEnumConstants()
  {
    return clazz.getEnumConstants();
  }
  
  public Field getField(String paramString)
    throws NoSuchFieldException
  {
    Field localField = clazz.getField(paramString);
    if (localField.getName().startsWith("ajc$")) {
      throw new NoSuchFieldException(paramString);
    }
    return localField;
  }
  
  public Field[] getFields()
  {
    Field[] arrayOfField1 = clazz.getFields();
    ArrayList localArrayList = new ArrayList();
    int i = arrayOfField1.length;
    for (int j = 0; j < i; j++)
    {
      Field localField = arrayOfField1[j];
      if ((!localField.getName().startsWith("ajc$")) && (!localField.isAnnotationPresent(DeclareWarning.class)) && (!localField.isAnnotationPresent(DeclareError.class))) {
        localArrayList.add(localField);
      }
    }
    Field[] arrayOfField2 = new Field[localArrayList.size()];
    localArrayList.toArray(arrayOfField2);
    return arrayOfField2;
  }
  
  public Type getGenericSupertype()
  {
    return clazz.getGenericSuperclass();
  }
  
  public InterTypeConstructorDeclaration getITDConstructor(AjType<?> paramAjType, AjType<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    InterTypeConstructorDeclaration[] arrayOfInterTypeConstructorDeclaration = getITDConstructors();
    int i = arrayOfInterTypeConstructorDeclaration.length;
    int j = 0;
    InterTypeConstructorDeclaration localInterTypeConstructorDeclaration;
    if (j < i)
    {
      localInterTypeConstructorDeclaration = arrayOfInterTypeConstructorDeclaration[j];
      for (;;)
      {
        try
        {
          if (localInterTypeConstructorDeclaration.getTargetType().equals(paramAjType))
          {
            AjType[] arrayOfAjType = localInterTypeConstructorDeclaration.getParameterTypes();
            if (arrayOfAjType.length == paramVarArgs.length)
            {
              k = 0;
              if (k >= arrayOfAjType.length) {
                continue;
              }
              boolean bool = arrayOfAjType[k].equals(paramVarArgs[k]);
              if (bool) {
                continue;
              }
            }
          }
          j++;
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          int k;
          continue;
        }
        break;
        k++;
      }
    }
    throw new NoSuchMethodException();
    return localInterTypeConstructorDeclaration;
  }
  
  public InterTypeConstructorDeclaration[] getITDConstructors()
  {
    if (itdCons == null)
    {
      ArrayList localArrayList = new ArrayList();
      Method[] arrayOfMethod = clazz.getMethods();
      int i = arrayOfMethod.length;
      int j = 0;
      if (j < i)
      {
        Method localMethod = arrayOfMethod[j];
        if (!localMethod.getName().contains("ajc$postInterConstructor")) {}
        for (;;)
        {
          j++;
          break;
          if (localMethod.isAnnotationPresent(ajcITD.class))
          {
            ajcITD localAjcITD = (ajcITD)localMethod.getAnnotation(ajcITD.class);
            if (Modifier.isPublic(localAjcITD.modifiers())) {
              localArrayList.add(new InterTypeConstructorDeclarationImpl(this, localAjcITD.targetType(), localAjcITD.modifiers(), localMethod));
            }
          }
        }
      }
      itdCons = new InterTypeConstructorDeclaration[localArrayList.size()];
      localArrayList.toArray(itdCons);
    }
    return itdCons;
  }
  
  public InterTypeFieldDeclaration getITDField(String paramString, AjType<?> paramAjType)
    throws NoSuchFieldException
  {
    for (InterTypeFieldDeclaration localInterTypeFieldDeclaration : getITDFields()) {
      if (localInterTypeFieldDeclaration.getName().equals(paramString)) {
        try
        {
          boolean bool = localInterTypeFieldDeclaration.getTargetType().equals(paramAjType);
          if (bool) {
            return localInterTypeFieldDeclaration;
          }
        }
        catch (ClassNotFoundException localClassNotFoundException) {}
      }
    }
    throw new NoSuchFieldException(paramString);
  }
  
  public InterTypeFieldDeclaration[] getITDFields()
  {
    ArrayList localArrayList = new ArrayList();
    if (itdFields == null)
    {
      Method[] arrayOfMethod = clazz.getMethods();
      int i = arrayOfMethod.length;
      int j = 0;
      if (j < i)
      {
        Method localMethod1 = arrayOfMethod[j];
        ajcITD localAjcITD;
        if (localMethod1.isAnnotationPresent(ajcITD.class))
        {
          localAjcITD = (ajcITD)localMethod1.getAnnotation(ajcITD.class);
          if (localMethod1.getName().contains("ajc$interFieldInit")) {
            break label85;
          }
        }
        for (;;)
        {
          j++;
          break;
          label85:
          if (Modifier.isPublic(localAjcITD.modifiers()))
          {
            String str = localMethod1.getName().replace("FieldInit", "FieldGetDispatch");
            try
            {
              Method localMethod2 = localMethod1.getDeclaringClass().getDeclaredMethod(str, localMethod1.getParameterTypes());
              localArrayList.add(new InterTypeFieldDeclarationImpl(this, localAjcITD.targetType(), localAjcITD.modifiers(), localAjcITD.name(), AjTypeSystem.getAjType(localMethod2.getReturnType()), localMethod2.getGenericReturnType()));
            }
            catch (NoSuchMethodException localNoSuchMethodException)
            {
              throw new IllegalStateException("Can't find field get dispatch method for " + localMethod1.getName());
            }
          }
        }
      }
      addAnnotationStyleITDFields(localArrayList, true);
      itdFields = new InterTypeFieldDeclaration[localArrayList.size()];
      localArrayList.toArray(itdFields);
    }
    return itdFields;
  }
  
  public InterTypeMethodDeclaration getITDMethod(String paramString, AjType<?> paramAjType, AjType<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    InterTypeMethodDeclaration[] arrayOfInterTypeMethodDeclaration = getITDMethods();
    int i = arrayOfInterTypeMethodDeclaration.length;
    int j = 0;
    for (;;)
    {
      InterTypeMethodDeclaration localInterTypeMethodDeclaration;
      if (j < i) {
        localInterTypeMethodDeclaration = arrayOfInterTypeMethodDeclaration[j];
      }
      try
      {
        if ((localInterTypeMethodDeclaration.getName().equals(paramString)) && (localInterTypeMethodDeclaration.getTargetType().equals(paramAjType)))
        {
          AjType[] arrayOfAjType = localInterTypeMethodDeclaration.getParameterTypes();
          if (arrayOfAjType.length == paramVarArgs.length)
          {
            int k = 0;
            while (k < arrayOfAjType.length)
            {
              boolean bool = arrayOfAjType[k].equals(paramVarArgs[k]);
              if (!bool) {
                break label126;
              }
              k++;
              continue;
              throw new NoSuchMethodException(paramString);
            }
            return localInterTypeMethodDeclaration;
          }
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        label126:
        j++;
      }
    }
  }
  
  public InterTypeMethodDeclaration[] getITDMethods()
  {
    if (itdMethods == null)
    {
      ArrayList localArrayList = new ArrayList();
      Method[] arrayOfMethod = clazz.getDeclaredMethods();
      int i = arrayOfMethod.length;
      int j = 0;
      if (j < i)
      {
        Method localMethod = arrayOfMethod[j];
        if (!localMethod.getName().contains("ajc$interMethod$")) {}
        for (;;)
        {
          j++;
          break;
          if (localMethod.isAnnotationPresent(ajcITD.class))
          {
            ajcITD localAjcITD = (ajcITD)localMethod.getAnnotation(ajcITD.class);
            if (Modifier.isPublic(localAjcITD.modifiers())) {
              localArrayList.add(new InterTypeMethodDeclarationImpl(this, localAjcITD.targetType(), localAjcITD.modifiers(), localAjcITD.name(), localMethod));
            }
          }
        }
      }
      addAnnotationStyleITDMethods(localArrayList, true);
      itdMethods = new InterTypeMethodDeclaration[localArrayList.size()];
      localArrayList.toArray(itdMethods);
    }
    return itdMethods;
  }
  
  public AjType<?>[] getInterfaces()
  {
    return toAjTypeArray(clazz.getInterfaces());
  }
  
  public Class<T> getJavaClass()
  {
    return clazz;
  }
  
  public Method getMethod(String paramString, AjType<?>... paramVarArgs)
    throws NoSuchMethodException
  {
    Method localMethod = clazz.getMethod(paramString, toClassArray(paramVarArgs));
    if (!isReallyAMethod(localMethod)) {
      throw new NoSuchMethodException(paramString);
    }
    return localMethod;
  }
  
  public Method[] getMethods()
  {
    Method[] arrayOfMethod1 = clazz.getMethods();
    ArrayList localArrayList = new ArrayList();
    int i = arrayOfMethod1.length;
    for (int j = 0; j < i; j++)
    {
      Method localMethod = arrayOfMethod1[j];
      if (isReallyAMethod(localMethod)) {
        localArrayList.add(localMethod);
      }
    }
    Method[] arrayOfMethod2 = new Method[localArrayList.size()];
    localArrayList.toArray(arrayOfMethod2);
    return arrayOfMethod2;
  }
  
  public int getModifiers()
  {
    return clazz.getModifiers();
  }
  
  public String getName()
  {
    return clazz.getName();
  }
  
  public Package getPackage()
  {
    return clazz.getPackage();
  }
  
  public PerClause getPerClause()
  {
    if (isAspect())
    {
      String str = ((Aspect)clazz.getAnnotation(Aspect.class)).value();
      if (str.equals(""))
      {
        if (getSupertype().isAspect()) {
          return getSupertype().getPerClause();
        }
        return new PerClauseImpl(PerClauseKind.SINGLETON);
      }
      if (str.startsWith("perthis(")) {
        return new PointcutBasedPerClauseImpl(PerClauseKind.PERTHIS, str.substring("perthis(".length(), -1 + str.length()));
      }
      if (str.startsWith("pertarget(")) {
        return new PointcutBasedPerClauseImpl(PerClauseKind.PERTARGET, str.substring("pertarget(".length(), -1 + str.length()));
      }
      if (str.startsWith("percflow(")) {
        return new PointcutBasedPerClauseImpl(PerClauseKind.PERCFLOW, str.substring("percflow(".length(), -1 + str.length()));
      }
      if (str.startsWith("percflowbelow(")) {
        return new PointcutBasedPerClauseImpl(PerClauseKind.PERCFLOWBELOW, str.substring("percflowbelow(".length(), -1 + str.length()));
      }
      if (str.startsWith("pertypewithin")) {
        return new TypePatternBasedPerClauseImpl(PerClauseKind.PERTYPEWITHIN, str.substring("pertypewithin(".length(), -1 + str.length()));
      }
      throw new IllegalStateException("Per-clause not recognized: " + str);
    }
    return null;
  }
  
  public org.aspectj.lang.reflect.Pointcut getPointcut(String paramString)
    throws NoSuchPointcutException
  {
    for (org.aspectj.lang.reflect.Pointcut localPointcut : getPointcuts()) {
      if (localPointcut.getName().equals(paramString)) {
        return localPointcut;
      }
    }
    throw new NoSuchPointcutException(paramString);
  }
  
  public org.aspectj.lang.reflect.Pointcut[] getPointcuts()
  {
    if (pointcuts != null) {
      return pointcuts;
    }
    ArrayList localArrayList = new ArrayList();
    Method[] arrayOfMethod = clazz.getMethods();
    int i = arrayOfMethod.length;
    for (int j = 0; j < i; j++)
    {
      org.aspectj.lang.reflect.Pointcut localPointcut = asPointcut(arrayOfMethod[j]);
      if (localPointcut != null) {
        localArrayList.add(localPointcut);
      }
    }
    org.aspectj.lang.reflect.Pointcut[] arrayOfPointcut = new org.aspectj.lang.reflect.Pointcut[localArrayList.size()];
    localArrayList.toArray(arrayOfPointcut);
    pointcuts = arrayOfPointcut;
    return arrayOfPointcut;
  }
  
  public AjType<? super T> getSupertype()
  {
    Class localClass = clazz.getSuperclass();
    if (localClass == null) {
      return null;
    }
    return new AjTypeImpl(localClass);
  }
  
  public TypeVariable<Class<T>>[] getTypeParameters()
  {
    return clazz.getTypeParameters();
  }
  
  public int hashCode()
  {
    return clazz.hashCode();
  }
  
  public boolean isAnnotationPresent(Class<? extends Annotation> paramClass)
  {
    return clazz.isAnnotationPresent(paramClass);
  }
  
  public boolean isArray()
  {
    return clazz.isArray();
  }
  
  public boolean isAspect()
  {
    return clazz.getAnnotation(Aspect.class) != null;
  }
  
  public boolean isEnum()
  {
    return clazz.isEnum();
  }
  
  public boolean isInstance(Object paramObject)
  {
    return clazz.isInstance(paramObject);
  }
  
  public boolean isInterface()
  {
    return clazz.isInterface();
  }
  
  public boolean isLocalClass()
  {
    return (clazz.isLocalClass()) && (!isAspect());
  }
  
  public boolean isMemberAspect()
  {
    return (clazz.isMemberClass()) && (isAspect());
  }
  
  public boolean isMemberClass()
  {
    return (clazz.isMemberClass()) && (!isAspect());
  }
  
  public boolean isPrimitive()
  {
    return clazz.isPrimitive();
  }
  
  public boolean isPrivileged()
  {
    return (isAspect()) && (clazz.isAnnotationPresent(ajcPrivileged.class));
  }
  
  public String toString()
  {
    return getName();
  }
}
