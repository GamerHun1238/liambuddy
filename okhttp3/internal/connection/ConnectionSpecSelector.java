package okhttp3.internal.connection;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.net.UnknownServiceException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import okhttp3.ConnectionSpec;
import okhttp3.internal.Internal;




















public final class ConnectionSpecSelector
{
  private final List<ConnectionSpec> connectionSpecs;
  private int nextModeIndex;
  private boolean isFallbackPossible;
  private boolean isFallback;
  
  public ConnectionSpecSelector(List<ConnectionSpec> connectionSpecs)
  {
    nextModeIndex = 0;
    this.connectionSpecs = connectionSpecs;
  }
  




  public ConnectionSpec configureSecureSocket(SSLSocket sslSocket)
    throws IOException
  {
    ConnectionSpec tlsConfiguration = null;
    int i = nextModeIndex; for (int size = connectionSpecs.size(); i < size; i++) {
      ConnectionSpec connectionSpec = (ConnectionSpec)connectionSpecs.get(i);
      if (connectionSpec.isCompatible(sslSocket)) {
        tlsConfiguration = connectionSpec;
        nextModeIndex = (i + 1);
        break;
      }
    }
    
    if (tlsConfiguration == null)
    {





      throw new UnknownServiceException("Unable to find acceptable protocols. isFallback=" + isFallback + ", modes=" + connectionSpecs + ", supported protocols=" + Arrays.toString(sslSocket.getEnabledProtocols()));
    }
    
    isFallbackPossible = isFallbackPossible(sslSocket);
    
    Internal.instance.apply(tlsConfiguration, sslSocket, isFallback);
    
    return tlsConfiguration;
  }
  







  public boolean connectionFailed(IOException e)
  {
    isFallback = true;
    
    if (!isFallbackPossible) {
      return false;
    }
    

    if ((e instanceof ProtocolException)) {
      return false;
    }
    



    if ((e instanceof InterruptedIOException)) {
      return false;
    }
    


    if ((e instanceof SSLHandshakeException))
    {
      if ((e.getCause() instanceof CertificateException)) {
        return false;
      }
    }
    if ((e instanceof SSLPeerUnverifiedException))
    {
      return false;
    }
    

    return e instanceof SSLException;
  }
  




  private boolean isFallbackPossible(SSLSocket socket)
  {
    for (int i = nextModeIndex; i < connectionSpecs.size(); i++) {
      if (((ConnectionSpec)connectionSpecs.get(i)).isCompatible(socket)) {
        return true;
      }
    }
    return false;
  }
}
