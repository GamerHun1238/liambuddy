package com.neovisionaries.ws.client;

import java.io.IOException;
import java.util.LinkedList;





















class WritingThread
  extends WebSocketThread
{
  private static final int SHOULD_SEND = 0;
  private static final int SHOULD_STOP = 1;
  private static final int SHOULD_CONTINUE = 2;
  private static final int SHOULD_FLUSH = 3;
  private static final int FLUSH_THRESHOLD = 1000;
  private final LinkedList<WebSocketFrame> mFrames;
  private final PerMessageCompressionExtension mPMCE;
  private boolean mStopRequested;
  private WebSocketFrame mCloseFrame;
  private boolean mFlushNeeded;
  private boolean mStopped;
  
  public WritingThread(WebSocket websocket)
  {
    super("WritingThread", websocket, ThreadType.WRITING_THREAD);
    
    mFrames = new LinkedList();
    mPMCE = websocket.getPerMessageCompressionExtension();
  }
  


  public void runMain()
  {
    try
    {
      main();


    }
    catch (Throwable t)
    {

      WebSocketException cause = new WebSocketException(WebSocketError.UNEXPECTED_ERROR_IN_WRITING_THREAD, "An uncaught throwable was detected in the writing thread: " + t.getMessage(), t);
      

      ListenerManager manager = mWebSocket.getListenerManager();
      manager.callOnError(cause);
      manager.callOnUnexpectedError(cause);
    }
    
    synchronized (this)
    {

      mStopped = true;
      notifyAll();
    }
    

    notifyFinished();
  }
  

  private void main()
  {
    mWebSocket.onWritingThreadStarted();
    

    for (;;)
    {
      int result = waitForFrames();
      
      if (result == 1) {
        break;
      }
      
      if (result == 3)
      {
        flushIgnoreError();

      }
      else if (result != 2)
      {


        try
        {


          sendFrames(false);
        }
        catch (WebSocketException e)
        {
          break;
        }
      }
    }
    

    try
    {
      sendFrames(true);
    }
    catch (WebSocketException localWebSocketException1) {}
  }
  




  public void requestStop()
  {
    synchronized (this)
    {

      mStopRequested = true;
      

      notifyAll();
    }
  }
  

  public boolean queueFrame(WebSocketFrame frame)
  {
    synchronized (this)
    {

      for (;;)
      {
        if (mStopped)
        {

          return false;
        }
        


        if ((mStopRequested) || (mCloseFrame != null)) {
          break;
        }
        



        if (frame.isControlFrame()) {
          break;
        }
        



        int queueSize = mWebSocket.getFrameQueueSize();
        

        if (queueSize == 0) {
          break;
        }
        



        if (mFrames.size() < queueSize) {
          break;
        }
        



        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
      



      if (isHighPriorityFrame(frame))
      {

        addHighPriorityFrame(frame);

      }
      else
      {
        mFrames.addLast(frame);
      }
      

      notifyAll();
    }
    

    return true;
  }
  

  private static boolean isHighPriorityFrame(WebSocketFrame frame)
  {
    return (frame.isPingFrame()) || (frame.isPongFrame());
  }
  

  private void addHighPriorityFrame(WebSocketFrame frame)
  {
    int index = 0;
    


    for (WebSocketFrame f : mFrames)
    {

      if (!isHighPriorityFrame(f)) {
        break;
      }
      

      index++;
    }
    
    mFrames.add(index, frame);
  }
  

  public void queueFlush()
  {
    synchronized (this)
    {
      mFlushNeeded = true;
      

      notifyAll();
    }
  }
  

  private void flushIgnoreError()
  {
    try
    {
      flush();
    }
    catch (IOException localIOException) {}
  }
  


  private void flush()
    throws IOException
  {
    mWebSocket.getOutput().flush();
  }
  

  private int waitForFrames()
  {
    synchronized (this)
    {

      if (mStopRequested)
      {
        return 1;
      }
      

      if (mCloseFrame != null)
      {
        return 1;
      }
      

      if (mFrames.size() == 0)
      {

        if (mFlushNeeded)
        {
          mFlushNeeded = false;
          return 3;
        }
        


        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
      


      if (mStopRequested)
      {
        return 1;
      }
      
      if (mFrames.size() == 0)
      {
        if (mFlushNeeded)
        {
          mFlushNeeded = false;
          return 3;
        }
        

        return 2;
      }
    }
    
    return 0;
  }
  

  private void sendFrames(boolean last)
    throws WebSocketException
  {
    long lastFlushAt = System.currentTimeMillis();
    


    for (;;)
    {
      synchronized (this)
      {

        WebSocketFrame frame = (WebSocketFrame)mFrames.poll();
        

        notifyAll();
        

        if (frame == null) {
          break;
        }
      }
      

      WebSocketFrame frame;
      
      sendFrame(frame);
      

      if ((frame.isPingFrame()) || (frame.isPongFrame()))
      {

        doFlush();
        lastFlushAt = System.currentTimeMillis();



      }
      else if (isFlushNeeded(last))
      {





        lastFlushAt = flushIfLongInterval(lastFlushAt);
      }
    }
    if (isFlushNeeded(last))
    {
      doFlush();
    }
  }
  

  private boolean isFlushNeeded(boolean last)
  {
    return (last) || (mWebSocket.isAutoFlush()) || (mFlushNeeded) || (mCloseFrame != null);
  }
  

  private long flushIfLongInterval(long lastFlushAt)
    throws WebSocketException
  {
    long current = System.currentTimeMillis();
    

    if (1000L < current - lastFlushAt)
    {

      doFlush();
      

      return current;
    }
    


    return lastFlushAt;
  }
  


  private void doFlush()
    throws WebSocketException
  {
    try
    {
      flush();
      
      synchronized (this)
      {
        mFlushNeeded = false;
      }
      

    }
    catch (IOException e)
    {

      WebSocketException cause = new WebSocketException(WebSocketError.FLUSH_ERROR, "Flushing frames to the server failed: " + e.getMessage(), e);
      

      ListenerManager manager = mWebSocket.getListenerManager();
      manager.callOnError(cause);
      manager.callOnSendError(cause, null);
      
      throw cause;
    }
  }
  

  private void sendFrame(WebSocketFrame frame)
    throws WebSocketException
  {
    frame = WebSocketFrame.compressFrame(frame, mPMCE);
    

    mWebSocket.getListenerManager().callOnSendingFrame(frame);
    
    boolean unsent = false;
    

    if (mCloseFrame != null)
    {

      unsent = true;

    }
    else if (frame.isCloseFrame())
    {
      mCloseFrame = frame;
    }
    
    if (unsent)
    {

      mWebSocket.getListenerManager().callOnFrameUnsent(frame);
      return;
    }
    

    if (frame.isCloseFrame())
    {


      changeToClosing();
    }
    

    try
    {
      mWebSocket.getOutput().write(frame);


    }
    catch (IOException e)
    {

      WebSocketException cause = new WebSocketException(WebSocketError.IO_ERROR_IN_WRITING, "An I/O error occurred when a frame was tried to be sent: " + e.getMessage(), e);
      

      ListenerManager manager = mWebSocket.getListenerManager();
      manager.callOnError(cause);
      manager.callOnSendError(cause, frame);
      
      throw cause;
    }
    

    mWebSocket.getListenerManager().callOnFrameSent(frame);
  }
  

  private void changeToClosing()
  {
    StateManager manager = mWebSocket.getStateManager();
    
    boolean stateChanged = false;
    
    synchronized (manager)
    {

      WebSocketState state = manager.getState();
      

      if ((state != WebSocketState.CLOSING) && (state != WebSocketState.CLOSED))
      {

        manager.changeToClosing(StateManager.CloseInitiator.CLIENT);
        
        stateChanged = true;
      }
    }
    
    if (stateChanged)
    {

      mWebSocket.getListenerManager().callOnStateChanged(WebSocketState.CLOSING);
    }
  }
  

  private void notifyFinished()
  {
    mWebSocket.onWritingThreadFinished(mCloseFrame);
  }
}
