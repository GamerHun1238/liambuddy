package org.apache.commons.collections4.functors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.Transformer;








































public class InvokerTransformer<I, O>
  implements Transformer<I, O>
{
  private final String iMethodName;
  private final Class<?>[] iParamTypes;
  private final Object[] iArgs;
  
  public static <I, O> Transformer<I, O> invokerTransformer(String methodName)
  {
    if (methodName == null) {
      throw new NullPointerException("The method to invoke must not be null");
    }
    return new InvokerTransformer(methodName);
  }
  












  public static <I, O> Transformer<I, O> invokerTransformer(String methodName, Class<?>[] paramTypes, Object[] args)
  {
    if (methodName == null) {
      throw new NullPointerException("The method to invoke must not be null");
    }
    if (((paramTypes == null) && (args != null)) || ((paramTypes != null) && (args == null)) || ((paramTypes != null) && (args != null) && (paramTypes.length != args.length)))
    {

      throw new IllegalArgumentException("The parameter types must match the arguments");
    }
    if ((paramTypes == null) || (paramTypes.length == 0)) {
      return new InvokerTransformer(methodName);
    }
    return new InvokerTransformer(methodName, paramTypes, args);
  }
  





  private InvokerTransformer(String methodName)
  {
    iMethodName = methodName;
    iParamTypes = null;
    iArgs = null;
  }
  










  public InvokerTransformer(String methodName, Class<?>[] paramTypes, Object[] args)
  {
    iMethodName = methodName;
    iParamTypes = (paramTypes != null ? (Class[])paramTypes.clone() : null);
    iArgs = (args != null ? (Object[])args.clone() : null);
  }
  







  public O transform(Object input)
  {
    if (input == null) {
      return null;
    }
    try {
      Class<?> cls = input.getClass();
      Method method = cls.getMethod(iMethodName, iParamTypes);
      return method.invoke(input, iArgs);
    } catch (NoSuchMethodException ex) {
      throw new FunctorException("InvokerTransformer: The method '" + iMethodName + "' on '" + input.getClass() + "' does not exist");
    }
    catch (IllegalAccessException ex) {
      throw new FunctorException("InvokerTransformer: The method '" + iMethodName + "' on '" + input.getClass() + "' cannot be accessed");
    }
    catch (InvocationTargetException ex) {
      throw new FunctorException("InvokerTransformer: The method '" + iMethodName + "' on '" + input.getClass() + "' threw an exception", ex);
    }
  }
}
