package okhttp3.internal.platform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Nullable;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.Protocol;














final class Jdk9Platform
  extends Platform
{
  final Method setProtocolMethod;
  final Method getProtocolMethod;
  
  Jdk9Platform(Method setProtocolMethod, Method getProtocolMethod)
  {
    this.setProtocolMethod = setProtocolMethod;
    this.getProtocolMethod = getProtocolMethod;
  }
  
  public void configureTlsExtensions(SSLSocket sslSocket, String hostname, List<Protocol> protocols)
  {
    try
    {
      SSLParameters sslParameters = sslSocket.getSSLParameters();
      
      List<String> names = alpnProtocolNames(protocols);
      
      setProtocolMethod.invoke(sslParameters, new Object[] {names
        .toArray(new String[names.size()]) });
      
      sslSocket.setSSLParameters(sslParameters);
    } catch (IllegalAccessException|InvocationTargetException e) {
      throw new AssertionError("failed to set SSL parameters", e);
    }
  }
  
  @Nullable
  public String getSelectedProtocol(SSLSocket socket) {
    try {
      String protocol = (String)getProtocolMethod.invoke(socket, new Object[0]);
      


      if ((protocol == null) || (protocol.equals(""))) {
        return null;
      }
      
      return protocol;
    } catch (IllegalAccessException|InvocationTargetException e) {
      throw new AssertionError("failed to get ALPN selected protocol", e);
    }
  }
  



  public X509TrustManager trustManager(SSLSocketFactory sslSocketFactory)
  {
    throw new UnsupportedOperationException("clientBuilder.sslSocketFactory(SSLSocketFactory) not supported on JDK 9+");
  }
  

  public static Jdk9Platform buildIfSupported()
  {
    try
    {
      Method setProtocolMethod = SSLParameters.class.getMethod("setApplicationProtocols", new Class[] { [Ljava.lang.String.class });
      Method getProtocolMethod = SSLSocket.class.getMethod("getApplicationProtocol", new Class[0]);
      
      return new Jdk9Platform(setProtocolMethod, getProtocolMethod);
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    

    return null;
  }
}
