package com.neovisionaries.ws.client;

import java.security.Principal;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;










































public class HostnameUnverifiedException
  extends WebSocketException
{
  private static final long serialVersionUID = 1L;
  private final SSLSocket mSSLSocket;
  private final String mHostname;
  
  public HostnameUnverifiedException(SSLSocket socket, String hostname)
  {
    super(WebSocketError.HOSTNAME_UNVERIFIED, 
      String.format("The certificate of the peer%s does not match the expected hostname (%s)", new Object[] {
      stringifyPrincipal(socket), hostname }));
    
    mSSLSocket = socket;
    mHostname = hostname;
  }
  

  private static String stringifyPrincipal(SSLSocket socket)
  {
    try
    {
      return String.format(" (%s)", new Object[] { socket.getSession().getPeerPrincipal().toString() });
    }
    catch (Exception e) {}
    

    return "";
  }
  








  public SSLSocket getSSLSocket()
  {
    return mSSLSocket;
  }
  







  public String getHostname()
  {
    return mHostname;
  }
}
