package okhttp3.internal.platform;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import javax.annotation.Nullable;
import javax.net.ssl.SSLSocket;
import okhttp3.Protocol;
import okhttp3.internal.Util;















class Jdk8WithJettyBootPlatform
  extends Platform
{
  private final Method putMethod;
  private final Method getMethod;
  private final Method removeMethod;
  private final Class<?> clientProviderClass;
  private final Class<?> serverProviderClass;
  
  Jdk8WithJettyBootPlatform(Method putMethod, Method getMethod, Method removeMethod, Class<?> clientProviderClass, Class<?> serverProviderClass)
  {
    this.putMethod = putMethod;
    this.getMethod = getMethod;
    this.removeMethod = removeMethod;
    this.clientProviderClass = clientProviderClass;
    this.serverProviderClass = serverProviderClass;
  }
  
  public void configureTlsExtensions(SSLSocket sslSocket, String hostname, List<Protocol> protocols)
  {
    List<String> names = alpnProtocolNames(protocols);
    try
    {
      Object alpnProvider = Proxy.newProxyInstance(Platform.class.getClassLoader(), new Class[] { clientProviderClass, serverProviderClass }, new AlpnProvider(names));
      
      putMethod.invoke(null, new Object[] { sslSocket, alpnProvider });
    } catch (InvocationTargetException|IllegalAccessException e) {
      throw new AssertionError("failed to set ALPN", e);
    }
  }
  
  public void afterHandshake(SSLSocket sslSocket) {
    try {
      removeMethod.invoke(null, new Object[] { sslSocket });
    } catch (IllegalAccessException|InvocationTargetException e) {
      throw new AssertionError("failed to remove ALPN", e);
    }
  }
  
  @Nullable
  public String getSelectedProtocol(SSLSocket socket) {
    try {
      AlpnProvider provider = (AlpnProvider)Proxy.getInvocationHandler(getMethod.invoke(null, new Object[] { socket }));
      if ((!unsupported) && (selected == null)) {
        Platform.get().log(4, "ALPN callback dropped: HTTP/2 is disabled. Is alpn-boot on the boot class path?", null);
        
        return null;
      }
      return unsupported ? null : selected;
    } catch (InvocationTargetException|IllegalAccessException e) {
      throw new AssertionError("failed to get ALPN selected protocol", e);
    }
  }
  
  public static Platform buildIfSupported()
  {
    try {
      String alpnClassName = "org.eclipse.jetty.alpn.ALPN";
      Class<?> alpnClass = Class.forName(alpnClassName);
      Class<?> providerClass = Class.forName(alpnClassName + "$Provider");
      Class<?> clientProviderClass = Class.forName(alpnClassName + "$ClientProvider");
      Class<?> serverProviderClass = Class.forName(alpnClassName + "$ServerProvider");
      Method putMethod = alpnClass.getMethod("put", new Class[] { SSLSocket.class, providerClass });
      Method getMethod = alpnClass.getMethod("get", new Class[] { SSLSocket.class });
      Method removeMethod = alpnClass.getMethod("remove", new Class[] { SSLSocket.class });
      return new Jdk8WithJettyBootPlatform(putMethod, getMethod, removeMethod, clientProviderClass, serverProviderClass);
    }
    catch (ClassNotFoundException|NoSuchMethodException localClassNotFoundException) {}
    

    return null;
  }
  

  private static class AlpnProvider
    implements InvocationHandler
  {
    private final List<String> protocols;
    
    boolean unsupported;
    
    String selected;
    

    AlpnProvider(List<String> protocols)
    {
      this.protocols = protocols;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      String methodName = method.getName();
      Class<?> returnType = method.getReturnType();
      if (args == null) {
        args = Util.EMPTY_STRING_ARRAY;
      }
      if ((methodName.equals("supports")) && (Boolean.TYPE == returnType))
        return Boolean.valueOf(true);
      if ((methodName.equals("unsupported")) && (Void.TYPE == returnType)) {
        unsupported = true;
        return null; }
      if ((methodName.equals("protocols")) && (args.length == 0))
        return protocols;
      if (((methodName.equals("selectProtocol")) || (methodName.equals("select"))) && (String.class == returnType) && (args.length == 1) && ((args[0] instanceof List)))
      {
        List<?> peerProtocols = (List)args[0];
        
        int i = 0; for (int size = peerProtocols.size(); i < size; i++) {
          String protocol = (String)peerProtocols.get(i);
          if (protocols.contains(protocol)) {
            return this.selected = protocol;
          }
        }
        return this.selected = (String)protocols.get(0); }
      if (((methodName.equals("protocolSelected")) || (methodName.equals("selected"))) && (args.length == 1))
      {
        selected = ((String)args[0]);
        return null;
      }
      return method.invoke(this, args);
    }
  }
}
