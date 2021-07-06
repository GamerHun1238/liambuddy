package org.apache.commons.collections4.functors;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.FunctorException;
































public class InstantiateFactory<T>
  implements Factory<T>
{
  private final Class<T> iClassToInstantiate;
  private final Class<?>[] iParamTypes;
  private final Object[] iArgs;
  private transient Constructor<T> iConstructor = null;
  












  public static <T> Factory<T> instantiateFactory(Class<T> classToInstantiate, Class<?>[] paramTypes, Object[] args)
  {
    if (classToInstantiate == null) {
      throw new NullPointerException("Class to instantiate must not be null");
    }
    if (((paramTypes == null) && (args != null)) || ((paramTypes != null) && (args == null)) || ((paramTypes != null) && (args != null) && (paramTypes.length != args.length)))
    {

      throw new IllegalArgumentException("Parameter types must match the arguments");
    }
    
    if ((paramTypes == null) || (paramTypes.length == 0)) {
      return new InstantiateFactory(classToInstantiate);
    }
    return new InstantiateFactory(classToInstantiate, paramTypes, args);
  }
  






  public InstantiateFactory(Class<T> classToInstantiate)
  {
    iClassToInstantiate = classToInstantiate;
    iParamTypes = null;
    iArgs = null;
    findConstructor();
  }
  








  public InstantiateFactory(Class<T> classToInstantiate, Class<?>[] paramTypes, Object[] args)
  {
    iClassToInstantiate = classToInstantiate;
    iParamTypes = ((Class[])paramTypes.clone());
    iArgs = ((Object[])args.clone());
    findConstructor();
  }
  

  private void findConstructor()
  {
    try
    {
      iConstructor = iClassToInstantiate.getConstructor(iParamTypes);
    } catch (NoSuchMethodException ex) {
      throw new IllegalArgumentException("InstantiateFactory: The constructor must exist and be public ");
    }
  }
  






  public T create()
  {
    if (iConstructor == null) {
      findConstructor();
    }
    try
    {
      return iConstructor.newInstance(iArgs);
    } catch (InstantiationException ex) {
      throw new FunctorException("InstantiateFactory: InstantiationException", ex);
    } catch (IllegalAccessException ex) {
      throw new FunctorException("InstantiateFactory: Constructor must be public", ex);
    } catch (InvocationTargetException ex) {
      throw new FunctorException("InstantiateFactory: Constructor threw an exception", ex);
    }
  }
}
