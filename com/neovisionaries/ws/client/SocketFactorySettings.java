package com.neovisionaries.ws.client;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;



















class SocketFactorySettings
{
  private SocketFactory mSocketFactory;
  private SSLSocketFactory mSSLSocketFactory;
  private SSLContext mSSLContext;
  
  public SocketFactorySettings() {}
  
  public SocketFactorySettings(SocketFactorySettings settings)
  {
    mSocketFactory = mSocketFactory;
    mSSLSocketFactory = mSSLSocketFactory;
    mSSLContext = mSSLContext;
  }
  

  public SocketFactory getSocketFactory()
  {
    return mSocketFactory;
  }
  

  public void setSocketFactory(SocketFactory factory)
  {
    mSocketFactory = factory;
  }
  

  public SSLSocketFactory getSSLSocketFactory()
  {
    return mSSLSocketFactory;
  }
  

  public void setSSLSocketFactory(SSLSocketFactory factory)
  {
    mSSLSocketFactory = factory;
  }
  

  public SSLContext getSSLContext()
  {
    return mSSLContext;
  }
  

  public void setSSLContext(SSLContext context)
  {
    mSSLContext = context;
  }
  

  public SocketFactory selectSocketFactory(boolean secure)
  {
    if (secure)
    {
      if (mSSLContext != null)
      {
        return mSSLContext.getSocketFactory();
      }
      
      if (mSSLSocketFactory != null)
      {
        return mSSLSocketFactory;
      }
      
      return SSLSocketFactory.getDefault();
    }
    
    if (mSocketFactory != null)
    {
      return mSocketFactory;
    }
    
    return SocketFactory.getDefault();
  }
}
