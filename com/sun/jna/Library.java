package com.sun.jna;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;























































































public abstract interface Library
{
  public static final String OPTION_TYPE_MAPPER = "type-mapper";
  public static final String OPTION_FUNCTION_MAPPER = "function-mapper";
  public static final String OPTION_INVOCATION_MAPPER = "invocation-mapper";
  public static final String OPTION_STRUCTURE_ALIGNMENT = "structure-alignment";
  public static final String OPTION_STRING_ENCODING = "string-encoding";
  public static final String OPTION_ALLOW_OBJECTS = "allow-objects";
  public static final String OPTION_CALLING_CONVENTION = "calling-convention";
  public static final String OPTION_OPEN_FLAGS = "open-flags";
  public static final String OPTION_CLASSLOADER = "classloader";
  
  public static class Handler
    implements InvocationHandler
  {
    static final Method OBJECT_TOSTRING;
    static final Method OBJECT_HASHCODE;
    static final Method OBJECT_EQUALS;
    private final NativeLibrary nativeLibrary;
    private final Class<?> interfaceClass;
    private final Map<String, Object> options;
    private final InvocationMapper invocationMapper;
    
    static
    {
      try
      {
        OBJECT_TOSTRING = Object.class.getMethod("toString", new Class[0]);
        OBJECT_HASHCODE = Object.class.getMethod("hashCode", new Class[0]);
        OBJECT_EQUALS = Object.class.getMethod("equals", new Class[] { Object.class });
      } catch (Exception e) {
        throw new Error("Error retrieving Object.toString() method");
      }
    }
    

    private static final class FunctionInfo
    {
      final InvocationHandler handler;
      
      final Function function;
      
      final boolean isVarArgs;
      final Map<String, ?> options;
      final Class<?>[] parameterTypes;
      
      FunctionInfo(InvocationHandler handler, Function function, Class<?>[] parameterTypes, boolean isVarArgs, Map<String, ?> options)
      {
        this.handler = handler;
        this.function = function;
        this.isVarArgs = isVarArgs;
        this.options = options;
        this.parameterTypes = parameterTypes;
      }
    }
    





    private final Map<Method, FunctionInfo> functions = new WeakHashMap();
    
    public Handler(String libname, Class<?> interfaceClass, Map<String, ?> options) {
      if ((libname != null) && ("".equals(libname.trim()))) {
        throw new IllegalArgumentException("Invalid library name \"" + libname + "\"");
      }
      
      if (!interfaceClass.isInterface()) {
        throw new IllegalArgumentException(libname + " does not implement an interface: " + interfaceClass.getName());
      }
      
      this.interfaceClass = interfaceClass;
      this.options = new HashMap(options);
      int callingConvention = AltCallingConvention.class.isAssignableFrom(interfaceClass) ? 63 : 0;
      

      if (this.options.get("calling-convention") == null) {
        this.options.put("calling-convention", Integer.valueOf(callingConvention));
      }
      if (this.options.get("classloader") == null) {
        this.options.put("classloader", interfaceClass.getClassLoader());
      }
      nativeLibrary = NativeLibrary.getInstance(libname, this.options);
      invocationMapper = ((InvocationMapper)this.options.get("invocation-mapper"));
    }
    
    public NativeLibrary getNativeLibrary() {
      return nativeLibrary;
    }
    
    public String getLibraryName() {
      return nativeLibrary.getName();
    }
    
    public Class<?> getInterfaceClass() {
      return interfaceClass;
    }
    


    public Object invoke(Object proxy, Method method, Object[] inArgs)
      throws Throwable
    {
      if (OBJECT_TOSTRING.equals(method))
        return "Proxy interface to " + nativeLibrary;
      if (OBJECT_HASHCODE.equals(method))
        return Integer.valueOf(hashCode());
      if (OBJECT_EQUALS.equals(method)) {
        Object o = inArgs[0];
        if ((o != null) && (Proxy.isProxyClass(o.getClass()))) {
          return Function.valueOf(Proxy.getInvocationHandler(o) == this);
        }
        return Boolean.FALSE;
      }
      

      FunctionInfo f = (FunctionInfo)functions.get(method);
      if (f == null) {
        synchronized (functions) {
          f = (FunctionInfo)functions.get(method);
          if (f == null) {
            boolean isVarArgs = Function.isVarArgs(method);
            InvocationHandler handler = null;
            if (invocationMapper != null) {
              handler = invocationMapper.getInvocationHandler(nativeLibrary, method);
            }
            Function function = null;
            Class<?>[] parameterTypes = null;
            Map<String, Object> options = null;
            if (handler == null)
            {
              function = nativeLibrary.getFunction(method.getName(), method);
              parameterTypes = method.getParameterTypes();
              options = new HashMap(this.options);
              options.put("invoking-method", method);
            }
            f = new FunctionInfo(handler, function, parameterTypes, isVarArgs, options);
            functions.put(method, f);
          }
        }
      }
      if (isVarArgs) {
        inArgs = Function.concatenateVarArgs(inArgs);
      }
      if (handler != null) {
        return handler.invoke(proxy, method, inArgs);
      }
      return function.invoke(method, parameterTypes, method.getReturnType(), inArgs, options);
    }
  }
}
