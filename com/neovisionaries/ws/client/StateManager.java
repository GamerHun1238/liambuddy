package com.neovisionaries.ws.client;










class StateManager
{
  private WebSocketState mState;
  









  static enum CloseInitiator
  {
    NONE, 
    SERVER, 
    CLIENT;
    
    private CloseInitiator() {}
  }
  
  private CloseInitiator mCloseInitiator = CloseInitiator.NONE;
  

  public StateManager()
  {
    mState = WebSocketState.CREATED;
  }
  

  public WebSocketState getState()
  {
    return mState;
  }
  

  public void setState(WebSocketState state)
  {
    mState = state;
  }
  

  public void changeToClosing(CloseInitiator closeInitiator)
  {
    mState = WebSocketState.CLOSING;
    

    if (mCloseInitiator == CloseInitiator.NONE)
    {
      mCloseInitiator = closeInitiator;
    }
  }
  

  public boolean getClosedByServer()
  {
    return mCloseInitiator == CloseInitiator.SERVER;
  }
}
