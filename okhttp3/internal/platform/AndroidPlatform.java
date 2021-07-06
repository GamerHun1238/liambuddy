package okhttp3.internal.platform;

import android.os.Build.VERSION;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.Protocol;
import okhttp3.internal.Util;
import okhttp3.internal.tls.CertificateChainCleaner;


















class AndroidPlatform
  extends Platform
{
  private static final int MAX_LOG_LENGTH = 4000;
  private final Class<?> sslParametersClass;
  private final Method setUseSessionTickets;
  private final Method setHostname;
  private final Method getAlpnSelectedProtocol;
  private final Method setAlpnProtocols;
  private final CloseGuard closeGuard = CloseGuard.get();
  
  AndroidPlatform(Class<?> sslParametersClass, Method setUseSessionTickets, Method setHostname, Method getAlpnSelectedProtocol, Method setAlpnProtocols)
  {
    this.sslParametersClass = sslParametersClass;
    this.setUseSessionTickets = setUseSessionTickets;
    this.setHostname = setHostname;
    this.getAlpnSelectedProtocol = getAlpnSelectedProtocol;
    this.setAlpnProtocols = setAlpnProtocols;
  }
  
  public void connectSocket(Socket socket, InetSocketAddress address, int connectTimeout) throws IOException
  {
    try {
      socket.connect(address, connectTimeout);
    } catch (AssertionError e) {
      if (Util.isAndroidGetsocknameError(e)) throw new IOException(e);
      throw e;
    }
    catch (ClassCastException e)
    {
      if (Build.VERSION.SDK_INT == 26) {
        throw new IOException("Exception in connect", e);
      }
      throw e;
    }
  }
  
  @Nullable
  protected X509TrustManager trustManager(SSLSocketFactory sslSocketFactory) {
    Object context = readFieldOrNull(sslSocketFactory, sslParametersClass, "sslParameters");
    if (context == null)
    {
      try
      {
        Class<?> gmsSslParametersClass = Class.forName("com.google.android.gms.org.conscrypt.SSLParametersImpl", false, sslSocketFactory
        
          .getClass().getClassLoader());
        context = readFieldOrNull(sslSocketFactory, gmsSslParametersClass, "sslParameters");
      } catch (ClassNotFoundException e) {
        return super.trustManager(sslSocketFactory);
      }
    }
    
    X509TrustManager x509TrustManager = (X509TrustManager)readFieldOrNull(context, X509TrustManager.class, "x509TrustManager");
    
    if (x509TrustManager != null) { return x509TrustManager;
    }
    return (X509TrustManager)readFieldOrNull(context, X509TrustManager.class, "trustManager");
  }
  
  public void configureTlsExtensions(SSLSocket sslSocket, String hostname, List<Protocol> protocols)
  {
    try
    {
      if (hostname != null) {
        setUseSessionTickets.invoke(sslSocket, new Object[] { Boolean.valueOf(true) });
        
        setHostname.invoke(sslSocket, new Object[] { hostname });
      }
      

      setAlpnProtocols.invoke(sslSocket, new Object[] { concatLengthPrefixed(protocols) });
    } catch (IllegalAccessException|InvocationTargetException e) {
      throw new AssertionError(e);
    }
  }
  
  @Nullable
  public String getSelectedProtocol(SSLSocket socket) {
    try { byte[] alpnResult = (byte[])getAlpnSelectedProtocol.invoke(socket, new Object[0]);
      return alpnResult != null ? new String(alpnResult, StandardCharsets.UTF_8) : null;
    } catch (IllegalAccessException|InvocationTargetException e) {
      throw new AssertionError(e);
    }
  }
  
  public void log(int level, String message, @Nullable Throwable t) {
    int logLevel = level == 5 ? 5 : 3;
    if (t != null) { message = message + '\n' + Log.getStackTraceString(t);
    }
    
    int i = 0; for (int length = message.length(); i < length; i++) {
      int newline = message.indexOf('\n', i);
      newline = newline != -1 ? newline : length;
      do {
        int end = Math.min(newline, i + 4000);
        Log.println(logLevel, "OkHttp", message.substring(i, end));
        i = end;
      } while (i < newline);
    }
  }
  
  public Object getStackTraceForCloseable(String closer) {
    return closeGuard.createAndOpen(closer);
  }
  
  public void logCloseableLeak(String message, Object stackTrace) {
    boolean reported = closeGuard.warnIfOpen(stackTrace);
    if (!reported)
    {
      log(5, message, null);
    }
  }
  
  public boolean isCleartextTrafficPermitted(String hostname) {
    try {
      Class<?> networkPolicyClass = Class.forName("android.security.NetworkSecurityPolicy");
      Method getInstanceMethod = networkPolicyClass.getMethod("getInstance", new Class[0]);
      Object networkSecurityPolicy = getInstanceMethod.invoke(null, new Object[0]);
      return api24IsCleartextTrafficPermitted(hostname, networkPolicyClass, networkSecurityPolicy);
    } catch (ClassNotFoundException|NoSuchMethodException e) {
      return super.isCleartextTrafficPermitted(hostname);
    } catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
      throw new AssertionError("unable to determine cleartext support", e);
    }
  }
  
  private boolean api24IsCleartextTrafficPermitted(String hostname, Class<?> networkPolicyClass, Object networkSecurityPolicy) throws InvocationTargetException, IllegalAccessException
  {
    try
    {
      Method isCleartextTrafficPermittedMethod = networkPolicyClass.getMethod("isCleartextTrafficPermitted", new Class[] { String.class });
      return ((Boolean)isCleartextTrafficPermittedMethod.invoke(networkSecurityPolicy, new Object[] { hostname })).booleanValue();
    } catch (NoSuchMethodException e) {}
    return api23IsCleartextTrafficPermitted(hostname, networkPolicyClass, networkSecurityPolicy);
  }
  
  private boolean api23IsCleartextTrafficPermitted(String hostname, Class<?> networkPolicyClass, Object networkSecurityPolicy)
    throws InvocationTargetException, IllegalAccessException
  {
    try
    {
      Method isCleartextTrafficPermittedMethod = networkPolicyClass.getMethod("isCleartextTrafficPermitted", new Class[0]);
      return ((Boolean)isCleartextTrafficPermittedMethod.invoke(networkSecurityPolicy, new Object[0])).booleanValue();
    } catch (NoSuchMethodException e) {}
    return super.isCleartextTrafficPermitted(hostname);
  }
  
  public CertificateChainCleaner buildCertificateChainCleaner(X509TrustManager trustManager)
  {
    try {
      Class<?> extensionsClass = Class.forName("android.net.http.X509TrustManagerExtensions");
      Constructor<?> constructor = extensionsClass.getConstructor(new Class[] { X509TrustManager.class });
      Object extensions = constructor.newInstance(new Object[] { trustManager });
      Method checkServerTrusted = extensionsClass.getMethod("checkServerTrusted", new Class[] { [Ljava.security.cert.X509Certificate.class, String.class, String.class });
      
      return new AndroidCertificateChainCleaner(extensions, checkServerTrusted);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }
  
  @Nullable
  public static Platform buildIfSupported()
  {
    try
    {
      Class<?> sslParametersClass = Class.forName("com.android.org.conscrypt.SSLParametersImpl");
      sslSocketClass = Class.forName("com.android.org.conscrypt.OpenSSLSocketImpl");
    } catch (ClassNotFoundException ignored) { Class<?> sslSocketClass;
      return null; }
    Class<?> sslSocketClass;
    Class<?> sslParametersClass; if (Build.VERSION.SDK_INT >= 21) {
      try {
        Method setUseSessionTickets = sslSocketClass.getDeclaredMethod("setUseSessionTickets", new Class[] { Boolean.TYPE });
        
        Method setHostname = sslSocketClass.getMethod("setHostname", new Class[] { String.class });
        Method getAlpnSelectedProtocol = sslSocketClass.getMethod("getAlpnSelectedProtocol", new Class[0]);
        Method setAlpnProtocols = sslSocketClass.getMethod("setAlpnProtocols", new Class[] { [B.class });
        return new AndroidPlatform(sslParametersClass, setUseSessionTickets, setHostname, getAlpnSelectedProtocol, setAlpnProtocols);
      }
      catch (NoSuchMethodException localNoSuchMethodException) {}
    }
    
    throw new IllegalStateException("Expected Android API level 21+ but was " + Build.VERSION.SDK_INT);
  }
  

  static final class AndroidCertificateChainCleaner
    extends CertificateChainCleaner
  {
    private final Object x509TrustManagerExtensions;
    
    private final Method checkServerTrusted;
    

    AndroidCertificateChainCleaner(Object x509TrustManagerExtensions, Method checkServerTrusted)
    {
      this.x509TrustManagerExtensions = x509TrustManagerExtensions;
      this.checkServerTrusted = checkServerTrusted;
    }
    
    public List<Certificate> clean(List<Certificate> chain, String hostname) throws SSLPeerUnverifiedException
    {
      try
      {
        X509Certificate[] certificates = (X509Certificate[])chain.toArray(new X509Certificate[chain.size()]);
        return (List)checkServerTrusted.invoke(x509TrustManagerExtensions, new Object[] { certificates, "RSA", hostname });
      }
      catch (InvocationTargetException e) {
        SSLPeerUnverifiedException exception = new SSLPeerUnverifiedException(e.getMessage());
        exception.initCause(e);
        throw exception;
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }
    
    public boolean equals(Object other) {
      return other instanceof AndroidCertificateChainCleaner;
    }
    
    public int hashCode() {
      return 0;
    }
  }
  

  static final class CloseGuard
  {
    private final Method getMethod;
    
    private final Method openMethod;
    
    private final Method warnIfOpenMethod;
    
    CloseGuard(Method getMethod, Method openMethod, Method warnIfOpenMethod)
    {
      this.getMethod = getMethod;
      this.openMethod = openMethod;
      this.warnIfOpenMethod = warnIfOpenMethod;
    }
    
    Object createAndOpen(String closer) {
      if (getMethod != null) {
        try {
          Object closeGuardInstance = getMethod.invoke(null, new Object[0]);
          openMethod.invoke(closeGuardInstance, new Object[] { closer });
          return closeGuardInstance;
        }
        catch (Exception localException) {}
      }
      return null;
    }
    
    boolean warnIfOpen(Object closeGuardInstance) {
      boolean reported = false;
      if (closeGuardInstance != null) {
        try {
          warnIfOpenMethod.invoke(closeGuardInstance, new Object[0]);
          reported = true;
        }
        catch (Exception localException) {}
      }
      return reported;
    }
    
    static CloseGuard get()
    {
      Method getMethod;
      Method openMethod;
      Method warnIfOpenMethod;
      try {
        Class<?> closeGuardClass = Class.forName("dalvik.system.CloseGuard");
        Method getMethod = closeGuardClass.getMethod("get", new Class[0]);
        Method openMethod = closeGuardClass.getMethod("open", new Class[] { String.class });
        warnIfOpenMethod = closeGuardClass.getMethod("warnIfOpen", new Class[0]);
      } catch (Exception ignored) { Method warnIfOpenMethod;
        getMethod = null;
        openMethod = null;
        warnIfOpenMethod = null;
      }
      return new CloseGuard(getMethod, openMethod, warnIfOpenMethod);
    }
  }
  
  public SSLContext getSSLContext() {
    boolean tryTls12;
    try {
      tryTls12 = (Build.VERSION.SDK_INT >= 16) && (Build.VERSION.SDK_INT < 22);
    }
    catch (NoClassDefFoundError e) {
      boolean tryTls12;
      tryTls12 = true;
    }
    
    if (tryTls12) {
      try {
        return SSLContext.getInstance("TLSv1.2");
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException1) {}
    }
    
    try
    {
      return SSLContext.getInstance("TLS");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("No TLS provider", e);
    }
  }
}
