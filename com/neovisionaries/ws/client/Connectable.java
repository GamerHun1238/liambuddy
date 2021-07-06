package com.neovisionaries.ws.client;

import java.util.concurrent.Callable;
























class Connectable
  implements Callable<WebSocket>
{
  private final WebSocket mWebSocket;
  
  public Connectable(WebSocket ws)
  {
    mWebSocket = ws;
  }
  

  public WebSocket call()
    throws WebSocketException
  {
    return mWebSocket.connect();
  }
}
