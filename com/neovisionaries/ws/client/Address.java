package com.neovisionaries.ws.client;






class Address
{
  private final String mHost;
  




  private final int mPort;
  




  private transient String mString;
  





  Address(String host, int port)
  {
    mHost = host;
    mPort = port;
  }
  

  String getHostname()
  {
    return mHost;
  }
  
  int getPort()
  {
    return mPort;
  }
  


  public String toString()
  {
    if (mString == null)
    {
      mString = String.format("%s:%d", new Object[] { mHost, Integer.valueOf(mPort) });
    }
    
    return mString;
  }
}
