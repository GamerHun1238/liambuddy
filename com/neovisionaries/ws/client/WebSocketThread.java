package com.neovisionaries.ws.client;







abstract class WebSocketThread
  extends Thread
{
  protected final WebSocket mWebSocket;
  




  private final ThreadType mThreadType;
  





  WebSocketThread(String name, WebSocket ws, ThreadType type)
  {
    super(name);
    
    mWebSocket = ws;
    mThreadType = type;
  }
  


  public void run()
  {
    ListenerManager lm = mWebSocket.getListenerManager();
    
    if (lm != null)
    {

      lm.callOnThreadStarted(mThreadType, this);
    }
    
    runMain();
    
    if (lm != null)
    {

      lm.callOnThreadStopping(mThreadType, this);
    }
  }
  

  public void callOnThreadCreated()
  {
    ListenerManager lm = mWebSocket.getListenerManager();
    
    if (lm != null)
    {
      lm.callOnThreadCreated(mThreadType, this);
    }
  }
  
  protected abstract void runMain();
}
