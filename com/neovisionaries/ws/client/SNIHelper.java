package com.neovisionaries.ws.client;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;

















class SNIHelper
{
  private static Constructor<?> sSNIHostNameConstructor;
  private static Method sSetServerNamesMethod;
  
  static
  {
    try
    {
      
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  


  private static void initialize()
    throws Exception
  {
    sSNIHostNameConstructor = Misc.getConstructor("javax.net.ssl.SNIHostName", new Class[] { String.class });
    



    sSetServerNamesMethod = Misc.getMethod("javax.net.ssl.SSLParameters", "setServerNames", new Class[] { List.class });
  }
  



  private static Object createSNIHostName(String hostname)
  {
    return Misc.newInstance(sSNIHostNameConstructor, new Object[] { hostname });
  }
  

  private static List<Object> createSNIHostNames(String[] hostnames)
  {
    List<Object> list = new ArrayList(hostnames.length);
    

    for (String hostname : hostnames)
    {

      list.add(createSNIHostName(hostname));
    }
    
    return list;
  }
  


  private static void setServerNames(SSLParameters parameters, String[] hostnames)
  {
    Misc.invoke(sSetServerNamesMethod, parameters, new Object[] { createSNIHostNames(hostnames) });
  }
  

  static void setServerNames(Socket socket, String[] hostnames)
  {
    if (!(socket instanceof SSLSocket))
    {
      return;
    }
    
    if (hostnames == null)
    {
      return;
    }
    


    int androidSDKVersion = getAndroidSDKVersion();
    if ((androidSDKVersion > 0) && (androidSDKVersion < 24))
    {
      try
      {
        Method method = socket.getClass().getMethod("setHostname", new Class[] { String.class });
        method.invoke(socket, new Object[] { hostnames[0] });
      }
      catch (Exception e)
      {
        System.err.println("SNI configuration failed: " + e.getMessage());
      }
      return;
    }
    
    SSLParameters parameters = ((SSLSocket)socket).getSSLParameters();
    if (parameters == null)
    {
      return;
    }
    

    setServerNames(parameters, hostnames);
  }
  
  public static int getAndroidSDKVersion()
  {
    try
    {
      return Class.forName("android.os.Build$VERSION").getField("SDK_INT").getInt(null);
    }
    catch (Exception ex)
    {
      try
      {
        return Integer.parseInt((String)Class.forName("android.os.Build$VERSION").getField("SDK").get(null));
      }
      catch (Exception ex1) {}
    }
    return 0;
  }
  
  SNIHelper() {}
}
