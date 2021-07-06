package com.neovisionaries.ws.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


























class ReadingThread
  extends WebSocketThread
{
  private boolean mStopRequested;
  private WebSocketFrame mCloseFrame;
  private List<WebSocketFrame> mContinuation = new ArrayList();
  private final PerMessageCompressionExtension mPMCE;
  private Object mCloseLock = new Object();
  
  private Timer mCloseTimer;
  private CloseTask mCloseTask;
  private long mCloseDelay;
  private boolean mNotWaitForCloseFrame;
  
  public ReadingThread(WebSocket websocket)
  {
    super("ReadingThread", websocket, ThreadType.READING_THREAD);
    
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

      WebSocketException cause = new WebSocketException(WebSocketError.UNEXPECTED_ERROR_IN_READING_THREAD, "An uncaught throwable was detected in the reading thread: " + t.getMessage(), t);
      

      ListenerManager manager = mWebSocket.getListenerManager();
      manager.callOnError(cause);
      manager.callOnUnexpectedError(cause);
    }
    

    notifyFinished();
  }
  

  private void main()
  {
    mWebSocket.onReadingThreadStarted();
    
    for (;;)
    {
      synchronized (this)
      {
        if (mStopRequested) {
          break;
        }
      }
      


      WebSocketFrame frame = readFrame();
      
      if (frame == null) {
        break;
      }
      



      boolean keepReading = handleFrame(frame);
      
      if (!keepReading) {
        break;
      }
    }
    


    waitForCloseFrame();
    

    cancelClose();
  }
  

  void requestStop(long closeDelay)
  {
    synchronized (this)
    {
      if (mStopRequested)
      {
        return;
      }
      
      mStopRequested = true;
    }
    




    interrupt();
    








    mCloseDelay = closeDelay;
    scheduleClose();
  }
  





  private void callOnFrame(WebSocketFrame frame)
  {
    mWebSocket.getListenerManager().callOnFrame(frame);
  }
  





  private void callOnContinuationFrame(WebSocketFrame frame)
  {
    mWebSocket.getListenerManager().callOnContinuationFrame(frame);
  }
  





  private void callOnTextFrame(WebSocketFrame frame)
  {
    mWebSocket.getListenerManager().callOnTextFrame(frame);
  }
  





  private void callOnBinaryFrame(WebSocketFrame frame)
  {
    mWebSocket.getListenerManager().callOnBinaryFrame(frame);
  }
  





  private void callOnCloseFrame(WebSocketFrame frame)
  {
    mWebSocket.getListenerManager().callOnCloseFrame(frame);
  }
  





  private void callOnPingFrame(WebSocketFrame frame)
  {
    mWebSocket.getListenerManager().callOnPingFrame(frame);
  }
  





  private void callOnPongFrame(WebSocketFrame frame)
  {
    mWebSocket.getListenerManager().callOnPongFrame(frame);
  }
  





  private void callOnTextMessage(byte[] data)
  {
    if (mWebSocket.isDirectTextMessage())
    {
      mWebSocket.getListenerManager().callOnTextMessage(data);
      return;
    }
    


    try
    {
      String message = Misc.toStringUTF8(data);
      

      callOnTextMessage(message);


    }
    catch (Throwable t)
    {

      WebSocketException wse = new WebSocketException(WebSocketError.TEXT_MESSAGE_CONSTRUCTION_ERROR, "Failed to convert payload data into a string: " + t.getMessage(), t);
      

      callOnError(wse);
      callOnTextMessageError(wse, data);
    }
  }
  





  private void callOnTextMessage(String message)
  {
    mWebSocket.getListenerManager().callOnTextMessage(message);
  }
  





  private void callOnBinaryMessage(byte[] message)
  {
    mWebSocket.getListenerManager().callOnBinaryMessage(message);
  }
  





  private void callOnError(WebSocketException cause)
  {
    mWebSocket.getListenerManager().callOnError(cause);
  }
  





  private void callOnFrameError(WebSocketException cause, WebSocketFrame frame)
  {
    mWebSocket.getListenerManager().callOnFrameError(cause, frame);
  }
  





  private void callOnMessageError(WebSocketException cause, List<WebSocketFrame> frames)
  {
    mWebSocket.getListenerManager().callOnMessageError(cause, frames);
  }
  





  private void callOnMessageDecompressionError(WebSocketException cause, byte[] compressed)
  {
    mWebSocket.getListenerManager().callOnMessageDecompressionError(cause, compressed);
  }
  





  private void callOnTextMessageError(WebSocketException cause, byte[] data)
  {
    mWebSocket.getListenerManager().callOnTextMessageError(cause, data);
  }
  

  private WebSocketFrame readFrame()
  {
    WebSocketFrame frame = null;
    WebSocketException wse = null;
    

    try
    {
      frame = mWebSocket.getInput().readFrame();
      

      verifyFrame(frame);
      

      return frame;
    }
    catch (InterruptedIOException e)
    {
      if (mStopRequested)
      {


        return null;
      }
      




      wse = new WebSocketException(WebSocketError.INTERRUPTED_IN_READING, "Interruption occurred while a frame was being read from the web socket: " + e.getMessage(), e);

    }
    catch (IOException e)
    {
      if ((mStopRequested) && (isInterrupted()))
      {


        return null;
      }
      




      wse = new WebSocketException(WebSocketError.IO_ERROR_IN_READING, "An I/O error occurred while a frame was being read from the web socket: " + e.getMessage(), e);

    }
    catch (WebSocketException e)
    {

      wse = e;
    }
    
    boolean error = true;
    


    if ((wse instanceof NoMoreFrameException))
    {

      mNotWaitForCloseFrame = true;
      

      if (mWebSocket.isMissingCloseFrameAllowed())
      {
        error = false;
      }
    }
    
    if (error)
    {

      callOnError(wse);
      callOnFrameError(wse, frame);
    }
    

    WebSocketFrame closeFrame = createCloseFrame(wse);
    

    mWebSocket.sendFrame(closeFrame);
    

    return null;
  }
  

  private void verifyFrame(WebSocketFrame frame)
    throws WebSocketException
  {
    verifyReservedBits(frame);
    

    verifyFrameOpcode(frame);
    

    verifyFrameMask(frame);
    

    verifyFrameFragmentation(frame);
    

    verifyFrameSize(frame);
  }
  

  private void verifyReservedBits(WebSocketFrame frame)
    throws WebSocketException
  {
    if (mWebSocket.isExtended())
    {

      return;
    }
    





    verifyReservedBit1(frame);
    verifyReservedBit2(frame);
    verifyReservedBit3(frame);
  }
  




  private void verifyReservedBit1(WebSocketFrame frame)
    throws WebSocketException
  {
    if (mPMCE != null)
    {

      boolean verified = verifyReservedBit1ForPMCE(frame);
      
      if (verified)
      {
        return;
      }
    }
    
    if (!frame.getRsv1())
    {

      return;
    }
    

    throw new WebSocketException(WebSocketError.UNEXPECTED_RESERVED_BIT, "The RSV1 bit of a frame is set unexpectedly.");
  }
  






  private boolean verifyReservedBit1ForPMCE(WebSocketFrame frame)
    throws WebSocketException
  {
    if ((frame.isTextFrame()) || (frame.isBinaryFrame()))
    {



      return true;
    }
    

    return false;
  }
  



  private void verifyReservedBit2(WebSocketFrame frame)
    throws WebSocketException
  {
    if (!frame.getRsv2())
    {

      return;
    }
    

    throw new WebSocketException(WebSocketError.UNEXPECTED_RESERVED_BIT, "The RSV2 bit of a frame is set unexpectedly.");
  }
  




  private void verifyReservedBit3(WebSocketFrame frame)
    throws WebSocketException
  {
    if (!frame.getRsv3())
    {

      return;
    }
    

    throw new WebSocketException(WebSocketError.UNEXPECTED_RESERVED_BIT, "The RSV3 bit of a frame is set unexpectedly.");
  }
  












  private void verifyFrameOpcode(WebSocketFrame frame)
    throws WebSocketException
  {
    switch (frame.getOpcode())
    {

    case 0: 
    case 1: 
    case 2: 
    case 8: 
    case 9: 
    case 10: 
      return;
    }
    
    



    if (mWebSocket.isExtended())
    {

      return;
    }
    



    throw new WebSocketException(WebSocketError.UNKNOWN_OPCODE, "A frame has an unknown opcode: 0x" + Integer.toHexString(frame.getOpcode()));
  }
  












  private void verifyFrameMask(WebSocketFrame frame)
    throws WebSocketException
  {
    if (frame.getMask())
    {

      throw new WebSocketException(WebSocketError.FRAME_MASKED, "A frame from the server is masked.");
    }
  }
  





  private void verifyFrameFragmentation(WebSocketFrame frame)
    throws WebSocketException
  {
    if (frame.isControlFrame())
    {

      if (!frame.getFin())
      {

        throw new WebSocketException(WebSocketError.FRAGMENTED_CONTROL_FRAME, "A control frame is fragmented.");
      }
      



      return;
    }
    

    boolean continuationExists = mContinuation.size() != 0;
    

    if (frame.isContinuationFrame())
    {

      if (!continuationExists)
      {

        throw new WebSocketException(WebSocketError.UNEXPECTED_CONTINUATION_FRAME, "A continuation frame was detected although a continuation had not started.");
      }
      



      return;
    }
    


    if (continuationExists)
    {

      throw new WebSocketException(WebSocketError.CONTINUATION_NOT_CLOSED, "A non-control frame was detected although the existing continuation had not been closed.");
    }
  }
  



  private void verifyFrameSize(WebSocketFrame frame)
    throws WebSocketException
  {
    if (!frame.isControlFrame())
    {

      return;
    }
    






    byte[] payload = frame.getPayload();
    
    if (payload == null)
    {

      return;
    }
    
    if (125 < payload.length)
    {

      throw new WebSocketException(WebSocketError.TOO_LONG_CONTROL_FRAME_PAYLOAD, "The payload size of a control frame exceeds the maximum size (125 bytes): " + payload.length);
    }
  }
  
  private WebSocketFrame createCloseFrame(WebSocketException wse)
  {
    int closeCode;
    int closeCode;
    int closeCode;
    int closeCode;
    int closeCode;
    switch (1.$SwitchMap$com$neovisionaries$ws$client$WebSocketError[wse.getError().ordinal()])
    {


    case 1: 
    case 2: 
    case 3: 
      closeCode = 1002;
      break;
    
    case 4: 
    case 5: 
      closeCode = 1009;
      break;
    


    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
      closeCode = 1002;
      break;
    


    case 14: 
    case 15: 
      closeCode = 1008;
      break;
    


    default: 
      closeCode = 1008;
    }
    
    
    return WebSocketFrame.createCloseFrame(closeCode, wse.getMessage());
  }
  


  private boolean handleFrame(WebSocketFrame frame)
  {
    callOnFrame(frame);
    

    switch (frame.getOpcode())
    {
    case 0: 
      return handleContinuationFrame(frame);
    
    case 1: 
      return handleTextFrame(frame);
    
    case 2: 
      return handleBinaryFrame(frame);
    
    case 8: 
      return handleCloseFrame(frame);
    
    case 9: 
      return handlePingFrame(frame);
    
    case 10: 
      return handlePongFrame(frame);
    }
    
    
    return true;
  }
  



  private boolean handleContinuationFrame(WebSocketFrame frame)
  {
    callOnContinuationFrame(frame);
    

    mContinuation.add(frame);
    

    if (!frame.getFin())
    {

      return true;
    }
    


    byte[] data = getMessage(mContinuation);
    

    if (data == null)
    {

      return false;
    }
    

    if (((WebSocketFrame)mContinuation.get(0)).isTextFrame())
    {

      callOnTextMessage(data);

    }
    else
    {
      callOnBinaryMessage(data);
    }
    

    mContinuation.clear();
    

    return true;
  }
  


  private byte[] getMessage(List<WebSocketFrame> frames)
  {
    byte[] data = concatenatePayloads(mContinuation);
    

    if (data == null)
    {

      return null;
    }
    


    if ((mPMCE != null) && (((WebSocketFrame)frames.get(0)).getRsv1()))
    {

      data = decompress(data);
    }
    
    return data;
  }
  

  private byte[] concatenatePayloads(List<WebSocketFrame> frames)
  {
    Throwable cause;
    
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      

      for (WebSocketFrame frame : frames)
      {

        byte[] payload = frame.getPayload();
        

        if ((payload != null) && (payload.length != 0))
        {




          baos.write(payload);
        }
      }
      
      return baos.toByteArray();
    }
    catch (IOException e)
    {
      cause = e;
    }
    catch (OutOfMemoryError e) {
      Throwable cause;
      cause = e;
    }
    



    WebSocketException wse = new WebSocketException(WebSocketError.MESSAGE_CONSTRUCTION_ERROR, "Failed to concatenate payloads of multiple frames to construct a message: " + cause.getMessage(), cause);
    

    callOnError(wse);
    callOnMessageError(wse, frames);
    



    WebSocketFrame frame = WebSocketFrame.createCloseFrame(1009, wse.getMessage());
    

    mWebSocket.sendFrame(frame);
    

    return null;
  }
  


  private byte[] getMessage(WebSocketFrame frame)
  {
    byte[] payload = frame.getPayload();
    


    if ((mPMCE != null) && (frame.getRsv1()))
    {

      payload = decompress(payload);
    }
    
    return payload;
  }
  




  private byte[] decompress(byte[] input)
  {
    try
    {
      return mPMCE.decompress(input);
    }
    catch (WebSocketException e)
    {
      WebSocketException wse = e;
      


      callOnError(wse);
      callOnMessageDecompressionError(wse, input);
      



      WebSocketFrame frame = WebSocketFrame.createCloseFrame(1003, wse.getMessage());
      

      mWebSocket.sendFrame(frame);
    }
    
    return null;
  }
  


  private boolean handleTextFrame(WebSocketFrame frame)
  {
    callOnTextFrame(frame);
    

    if (!frame.getFin())
    {

      mContinuation.add(frame);
      

      return true;
    }
    


    byte[] payload = getMessage(frame);
    

    callOnTextMessage(payload);
    

    return true;
  }
  


  private boolean handleBinaryFrame(WebSocketFrame frame)
  {
    callOnBinaryFrame(frame);
    

    if (!frame.getFin())
    {

      mContinuation.add(frame);
      

      return true;
    }
    


    byte[] payload = getMessage(frame);
    

    callOnBinaryMessage(payload);
    

    return true;
  }
  


  private boolean handleCloseFrame(WebSocketFrame frame)
  {
    StateManager manager = mWebSocket.getStateManager();
    

    mCloseFrame = frame;
    
    boolean stateChanged = false;
    
    synchronized (manager)
    {

      WebSocketState state = manager.getState();
      

      if ((state != WebSocketState.CLOSING) && (state != WebSocketState.CLOSED))
      {

        manager.changeToClosing(StateManager.CloseInitiator.SERVER);
        










        mWebSocket.sendFrame(frame);
        
        stateChanged = true;
      }
    }
    
    if (stateChanged)
    {

      mWebSocket.getListenerManager().callOnStateChanged(WebSocketState.CLOSING);
    }
    

    callOnCloseFrame(frame);
    

    return false;
  }
  


  private boolean handlePingFrame(WebSocketFrame frame)
  {
    callOnPingFrame(frame);
    









    WebSocketFrame pong = WebSocketFrame.createPongFrame(frame.getPayload());
    

    mWebSocket.sendFrame(pong);
    

    return true;
  }
  


  private boolean handlePongFrame(WebSocketFrame frame)
  {
    callOnPongFrame(frame);
    

    return true;
  }
  

  private void waitForCloseFrame()
  {
    if (mNotWaitForCloseFrame)
    {
      return;
    }
    

    if (mCloseFrame != null)
    {
      return;
    }
    
    WebSocketFrame frame = null;
    


    scheduleClose();
    

    for (;;)
    {
      try
      {
        frame = mWebSocket.getInput().readFrame();
      }
      catch (Throwable t)
      {
        break;
      }
      


      if (frame.isCloseFrame())
      {

        mCloseFrame = frame;


      }
      else if (isInterrupted()) {
        break;
      }
    }
  }
  


  private void notifyFinished()
  {
    mWebSocket.onReadingThreadFinished(mCloseFrame);
  }
  

  private void scheduleClose()
  {
    synchronized (mCloseLock)
    {
      cancelCloseTask();
      scheduleCloseTask();
    }
  }
  

  private void scheduleCloseTask()
  {
    mCloseTask = new CloseTask(null);
    mCloseTimer = new Timer("ReadingThreadCloseTimer");
    mCloseTimer.schedule(mCloseTask, mCloseDelay);
  }
  

  private void cancelClose()
  {
    synchronized (mCloseLock)
    {
      cancelCloseTask();
    }
  }
  

  private void cancelCloseTask()
  {
    if (mCloseTimer != null)
    {
      mCloseTimer.cancel();
      mCloseTimer = null;
    }
    
    if (mCloseTask != null)
    {
      mCloseTask.cancel();
      mCloseTask = null;
    }
  }
  
  private class CloseTask extends TimerTask
  {
    private CloseTask() {}
    
    public void run()
    {
      try
      {
        Socket socket = mWebSocket.getSocket();
        if (socket != null)
        {
          socket.close();
        }
      }
      catch (Throwable localThrowable) {}
    }
  }
}
