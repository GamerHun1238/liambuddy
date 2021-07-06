package com.neovisionaries.ws.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































public class WebSocket
{
  private static final long DEFAULT_CLOSE_DELAY = 10000L;
  private final WebSocketFactory mWebSocketFactory;
  private final SocketConnector mSocketConnector;
  private final StateManager mStateManager;
  private HandshakeBuilder mHandshakeBuilder;
  private final ListenerManager mListenerManager;
  private final PingSender mPingSender;
  private final PongSender mPongSender;
  private final Object mThreadsLock = new Object();
  private WebSocketInputStream mInput;
  private WebSocketOutputStream mOutput;
  private ReadingThread mReadingThread;
  private WritingThread mWritingThread;
  private Map<String, List<String>> mServerHeaders;
  private List<WebSocketExtension> mAgreedExtensions;
  private String mAgreedProtocol;
  private boolean mExtended;
  private boolean mAutoFlush = true;
  private boolean mMissingCloseFrameAllowed = true;
  private boolean mDirectTextMessage;
  private int mFrameQueueSize;
  private int mMaxPayloadSize;
  private boolean mOnConnectedCalled;
  private Object mOnConnectedCalledLock = new Object();
  
  private boolean mReadingThreadStarted;
  
  private boolean mWritingThreadStarted;
  private boolean mReadingThreadFinished;
  private boolean mWritingThreadFinished;
  private WebSocketFrame mServerCloseFrame;
  private WebSocketFrame mClientCloseFrame;
  private PerMessageCompressionExtension mPerMessageCompressionExtension;
  
  WebSocket(WebSocketFactory factory, boolean secure, String userInfo, String host, String path, SocketConnector connector)
  {
    mWebSocketFactory = factory;
    mSocketConnector = connector;
    mStateManager = new StateManager();
    mHandshakeBuilder = new HandshakeBuilder(secure, userInfo, host, path);
    mListenerManager = new ListenerManager(this);
    mPingSender = new PingSender(this, new CounterPayloadGenerator());
    mPongSender = new PongSender(this, new CounterPayloadGenerator());
  }
  

























  public WebSocket recreate()
    throws IOException
  {
    return recreate(mSocketConnector.getConnectionTimeout());
  }
  

























  public WebSocket recreate(int timeout)
    throws IOException
  {
    if (timeout < 0)
    {
      throw new IllegalArgumentException("The given timeout value is negative.");
    }
    
    WebSocket instance = mWebSocketFactory.createSocket(getURI(), timeout);
    

    mHandshakeBuilder = new HandshakeBuilder(mHandshakeBuilder);
    instance.setPingInterval(getPingInterval());
    instance.setPongInterval(getPongInterval());
    instance.setPingPayloadGenerator(getPingPayloadGenerator());
    instance.setPongPayloadGenerator(getPongPayloadGenerator());
    mExtended = mExtended;
    mAutoFlush = mAutoFlush;
    mMissingCloseFrameAllowed = mMissingCloseFrameAllowed;
    mDirectTextMessage = mDirectTextMessage;
    mFrameQueueSize = mFrameQueueSize;
    

    List<WebSocketListener> listeners = mListenerManager.getListeners();
    synchronized (listeners)
    {
      instance.addListeners(listeners);
    }
    
    return instance;
  }
  

  protected void finalize()
    throws Throwable
  {
    if (isInState(WebSocketState.CREATED))
    {

      finish();
    }
    
    super.finalize();
  }
  
























  public WebSocketState getState()
  {
    synchronized (mStateManager)
    {
      return mStateManager.getState();
    }
  }
  










  public boolean isOpen()
  {
    return isInState(WebSocketState.OPEN);
  }
  




  private boolean isInState(WebSocketState state)
  {
    synchronized (mStateManager)
    {
      return mStateManager.getState() == state;
    }
  }
  















  public WebSocket addProtocol(String protocol)
  {
    mHandshakeBuilder.addProtocol(protocol);
    
    return this;
  }
  












  public WebSocket removeProtocol(String protocol)
  {
    mHandshakeBuilder.removeProtocol(protocol);
    
    return this;
  }
  









  public WebSocket clearProtocols()
  {
    mHandshakeBuilder.clearProtocols();
    
    return this;
  }
  










  public WebSocket addExtension(WebSocketExtension extension)
  {
    mHandshakeBuilder.addExtension(extension);
    
    return this;
  }
  


















  public WebSocket addExtension(String extension)
  {
    mHandshakeBuilder.addExtension(extension);
    
    return this;
  }
  












  public WebSocket removeExtension(WebSocketExtension extension)
  {
    mHandshakeBuilder.removeExtension(extension);
    
    return this;
  }
  













  public WebSocket removeExtensions(String name)
  {
    mHandshakeBuilder.removeExtensions(name);
    
    return this;
  }
  









  public WebSocket clearExtensions()
  {
    mHandshakeBuilder.clearExtensions();
    
    return this;
  }
  














  public WebSocket addHeader(String name, String value)
  {
    mHandshakeBuilder.addHeader(name, value);
    
    return this;
  }
  












  public WebSocket removeHeaders(String name)
  {
    mHandshakeBuilder.removeHeaders(name);
    
    return this;
  }
  









  public WebSocket clearHeaders()
  {
    mHandshakeBuilder.clearHeaders();
    
    return this;
  }
  











  public WebSocket setUserInfo(String userInfo)
  {
    mHandshakeBuilder.setUserInfo(userInfo);
    
    return this;
  }
  













  public WebSocket setUserInfo(String id, String password)
  {
    mHandshakeBuilder.setUserInfo(id, password);
    
    return this;
  }
  









  public WebSocket clearUserInfo()
  {
    mHandshakeBuilder.clearUserInfo();
    
    return this;
  }
  


















  public boolean isExtended()
  {
    return mExtended;
  }
  










  public WebSocket setExtended(boolean extended)
  {
    mExtended = extended;
    
    return this;
  }
  











  public boolean isAutoFlush()
  {
    return mAutoFlush;
  }
  













  public WebSocket setAutoFlush(boolean auto)
  {
    mAutoFlush = auto;
    
    return this;
  }
  



















  public boolean isMissingCloseFrameAllowed()
  {
    return mMissingCloseFrameAllowed;
  }
  



















  public WebSocket setMissingCloseFrameAllowed(boolean allowed)
  {
    mMissingCloseFrameAllowed = allowed;
    
    return this;
  }
  




















  public boolean isDirectTextMessage()
  {
    return mDirectTextMessage;
  }
  























  public WebSocket setDirectTextMessage(boolean direct)
  {
    mDirectTextMessage = direct;
    
    return this;
  }
  









  public WebSocket flush()
  {
    synchronized (mStateManager)
    {
      WebSocketState state = mStateManager.getState();
      
      if ((state != WebSocketState.OPEN) && (state != WebSocketState.CLOSING))
      {
        return this;
      }
    }
    

    WritingThread wt = mWritingThread;
    

    if (wt != null)
    {

      wt.queueFlush();
    }
    
    return this;
  }
  










  public int getFrameQueueSize()
  {
    return mFrameQueueSize;
  }
  
































  public WebSocket setFrameQueueSize(int size)
    throws IllegalArgumentException
  {
    if (size < 0)
    {
      throw new IllegalArgumentException("size must not be negative.");
    }
    
    mFrameQueueSize = size;
    
    return this;
  }
  











  public int getMaxPayloadSize()
  {
    return mMaxPayloadSize;
  }
  





















  public WebSocket setMaxPayloadSize(int size)
    throws IllegalArgumentException
  {
    if (size < 0)
    {
      throw new IllegalArgumentException("size must not be negative.");
    }
    
    mMaxPayloadSize = size;
    
    return this;
  }
  











  public long getPingInterval()
  {
    return mPingSender.getInterval();
  }
  





















  public WebSocket setPingInterval(long interval)
  {
    mPingSender.setInterval(interval);
    
    return this;
  }
  











  public long getPongInterval()
  {
    return mPongSender.getInterval();
  }
  






































  public WebSocket setPongInterval(long interval)
  {
    mPongSender.setInterval(interval);
    
    return this;
  }
  









  public PayloadGenerator getPingPayloadGenerator()
  {
    return mPingSender.getPayloadGenerator();
  }
  









  public WebSocket setPingPayloadGenerator(PayloadGenerator generator)
  {
    mPingSender.setPayloadGenerator(generator);
    
    return this;
  }
  









  public PayloadGenerator getPongPayloadGenerator()
  {
    return mPongSender.getPayloadGenerator();
  }
  









  public WebSocket setPongPayloadGenerator(PayloadGenerator generator)
  {
    mPongSender.setPayloadGenerator(generator);
    
    return this;
  }
  









  public String getPingSenderName()
  {
    return mPingSender.getTimerName();
  }
  












  public WebSocket setPingSenderName(String name)
  {
    mPingSender.setTimerName(name);
    
    return this;
  }
  









  public String getPongSenderName()
  {
    return mPongSender.getTimerName();
  }
  












  public WebSocket setPongSenderName(String name)
  {
    mPongSender.setTimerName(name);
    
    return this;
  }
  










  public WebSocket addListener(WebSocketListener listener)
  {
    mListenerManager.addListener(listener);
    
    return this;
  }
  













  public WebSocket addListeners(List<WebSocketListener> listeners)
  {
    mListenerManager.addListeners(listeners);
    
    return this;
  }
  












  public WebSocket removeListener(WebSocketListener listener)
  {
    mListenerManager.removeListener(listener);
    
    return this;
  }
  













  public WebSocket removeListeners(List<WebSocketListener> listeners)
  {
    mListenerManager.removeListeners(listeners);
    
    return this;
  }
  









  public WebSocket clearListeners()
  {
    mListenerManager.clearListeners();
    
    return this;
  }
  


















  public Socket getSocket()
  {
    return mSocketConnector.getSocket();
  }
  









  public Socket getConnectedSocket()
    throws WebSocketException
  {
    return mSocketConnector.getConnectedSocket();
  }
  










  public URI getURI()
  {
    return mHandshakeBuilder.getURI();
  }
  

































































  public WebSocket connect()
    throws WebSocketException
  {
    changeStateOnConnect();
    




    try
    {
      Socket socket = mSocketConnector.connect();
      

      headers = shakeHands(socket);
    }
    catch (WebSocketException e)
    {
      Map<String, List<String>> headers;
      mSocketConnector.closeSilently();
      

      mStateManager.setState(WebSocketState.CLOSED);
      

      mListenerManager.callOnStateChanged(WebSocketState.CLOSED);
      

      throw e;
    }
    
    Map<String, List<String>> headers;
    mServerHeaders = headers;
    

    mPerMessageCompressionExtension = findAgreedPerMessageCompressionExtension();
    

    mStateManager.setState(WebSocketState.OPEN);
    

    mListenerManager.callOnStateChanged(WebSocketState.OPEN);
    

    startThreads();
    
    return this;
  }
  



























  public Future<WebSocket> connect(ExecutorService executorService)
  {
    return executorService.submit(connectable());
  }
  














  public Callable<WebSocket> connectable()
  {
    return new Connectable(this);
  }
  












  public WebSocket connectAsynchronously()
  {
    Thread thread = new ConnectThread(this);
    

    ListenerManager lm = mListenerManager;
    
    if (lm != null)
    {
      lm.callOnThreadCreated(ThreadType.CONNECT_THREAD, thread);
    }
    
    thread.start();
    
    return this;
  }
  












  public WebSocket disconnect()
  {
    return disconnect(1000, null);
  }
  



















  public WebSocket disconnect(int closeCode)
  {
    return disconnect(closeCode, null);
  }
  























  public WebSocket disconnect(String reason)
  {
    return disconnect(1000, reason);
  }
  
































  public WebSocket disconnect(int closeCode, String reason)
  {
    return disconnect(closeCode, reason, 10000L);
  }
  











































  public WebSocket disconnect(int closeCode, String reason, long closeDelay)
  {
    synchronized (mStateManager)
    {
      switch (1.$SwitchMap$com$neovisionaries$ws$client$WebSocketState[mStateManager.getState().ordinal()])
      {
      case 1: 
        finishAsynchronously();
        return this;
      




      case 2: 
        break;
      





      default: 
        return this;
      }
      
      
      mStateManager.changeToClosing(StateManager.CloseInitiator.CLIENT);
      

      WebSocketFrame frame = WebSocketFrame.createCloseFrame(closeCode, reason);
      

      sendFrame(frame);
    }
    

    mListenerManager.callOnStateChanged(WebSocketState.CLOSING);
    

    if (closeDelay < 0L)
    {

      closeDelay = 10000L;
    }
    

    stopThreads(closeDelay);
    
    return this;
  }
  












  public List<WebSocketExtension> getAgreedExtensions()
  {
    return mAgreedExtensions;
  }
  












  public String getAgreedProtocol()
  {
    return mAgreedProtocol;
  }
  





































  public WebSocket sendFrame(WebSocketFrame frame)
  {
    if (frame == null)
    {
      return this;
    }
    
    synchronized (mStateManager)
    {
      WebSocketState state = mStateManager.getState();
      
      if ((state != WebSocketState.OPEN) && (state != WebSocketState.CLOSING))
      {
        return this;
      }
    }
    



    WritingThread wt = mWritingThread;
    






    if (wt == null)
    {

      return this;
    }
    

    List<WebSocketFrame> frames = splitIfNecessary(frame);
    




    if (frames == null)
    {

      wt.queueFrame(frame);
    }
    else
    {
      for (WebSocketFrame f : frames)
      {

        wt.queueFrame(f);
      }
    }
    
    return this;
  }
  

  private List<WebSocketFrame> splitIfNecessary(WebSocketFrame frame)
  {
    return WebSocketFrame.splitIfNecessary(frame, mMaxPayloadSize, mPerMessageCompressionExtension);
  }
  




















  public WebSocket sendContinuation()
  {
    return sendFrame(WebSocketFrame.createContinuationFrame());
  }
  


















  public WebSocket sendContinuation(boolean fin)
  {
    return sendFrame(WebSocketFrame.createContinuationFrame().setFin(fin));
  }
  
























  public WebSocket sendContinuation(String payload)
  {
    return sendFrame(WebSocketFrame.createContinuationFrame(payload));
  }
  





















  public WebSocket sendContinuation(String payload, boolean fin)
  {
    return sendFrame(WebSocketFrame.createContinuationFrame(payload).setFin(fin));
  }
  
























  public WebSocket sendContinuation(byte[] payload)
  {
    return sendFrame(WebSocketFrame.createContinuationFrame(payload));
  }
  





















  public WebSocket sendContinuation(byte[] payload, boolean fin)
  {
    return sendFrame(WebSocketFrame.createContinuationFrame(payload).setFin(fin));
  }
  























  public WebSocket sendText(String message)
  {
    return sendFrame(WebSocketFrame.createTextFrame(message));
  }
  





















  public WebSocket sendText(String payload, boolean fin)
  {
    return sendFrame(WebSocketFrame.createTextFrame(payload).setFin(fin));
  }
  























  public WebSocket sendBinary(byte[] message)
  {
    return sendFrame(WebSocketFrame.createBinaryFrame(message));
  }
  





















  public WebSocket sendBinary(byte[] payload, boolean fin)
  {
    return sendFrame(WebSocketFrame.createBinaryFrame(payload).setFin(fin));
  }
  













  public WebSocket sendClose()
  {
    return sendFrame(WebSocketFrame.createCloseFrame());
  }
  



















  public WebSocket sendClose(int closeCode)
  {
    return sendFrame(WebSocketFrame.createCloseFrame(closeCode));
  }
  

























  public WebSocket sendClose(int closeCode, String reason)
  {
    return sendFrame(WebSocketFrame.createCloseFrame(closeCode, reason));
  }
  













  public WebSocket sendPing()
  {
    return sendFrame(WebSocketFrame.createPingFrame());
  }
  




















  public WebSocket sendPing(byte[] payload)
  {
    return sendFrame(WebSocketFrame.createPingFrame(payload));
  }
  




















  public WebSocket sendPing(String payload)
  {
    return sendFrame(WebSocketFrame.createPingFrame(payload));
  }
  













  public WebSocket sendPong()
  {
    return sendFrame(WebSocketFrame.createPongFrame());
  }
  




















  public WebSocket sendPong(byte[] payload)
  {
    return sendFrame(WebSocketFrame.createPongFrame(payload));
  }
  




















  public WebSocket sendPong(String payload)
  {
    return sendFrame(WebSocketFrame.createPongFrame(payload));
  }
  
  private void changeStateOnConnect()
    throws WebSocketException
  {
    synchronized (mStateManager)
    {

      if (mStateManager.getState() != WebSocketState.CREATED)
      {
        throw new WebSocketException(WebSocketError.NOT_IN_CREATED_STATE, "The current state of the WebSocket is not CREATED.");
      }
      



      mStateManager.setState(WebSocketState.CONNECTING);
    }
    

    mListenerManager.callOnStateChanged(WebSocketState.CONNECTING);
  }
  




  private Map<String, List<String>> shakeHands(Socket socket)
    throws WebSocketException
  {
    WebSocketInputStream input = openInputStream(socket);
    

    WebSocketOutputStream output = openOutputStream(socket);
    

    String key = generateWebSocketKey();
    

    writeHandshake(output, key);
    

    Map<String, List<String>> headers = readHandshake(input, key);
    


    mInput = input;
    mOutput = output;
    

    return headers;
  }
  






  private WebSocketInputStream openInputStream(Socket socket)
    throws WebSocketException
  {
    try
    {
      return new WebSocketInputStream(new BufferedInputStream(socket
        .getInputStream()));


    }
    catch (IOException e)
    {

      throw new WebSocketException(WebSocketError.SOCKET_INPUT_STREAM_FAILURE, "Failed to get the input stream of the raw socket: " + e.getMessage(), e);
    }
  }
  






  private WebSocketOutputStream openOutputStream(Socket socket)
    throws WebSocketException
  {
    try
    {
      return new WebSocketOutputStream(new BufferedOutputStream(socket
        .getOutputStream()));


    }
    catch (IOException e)
    {

      throw new WebSocketException(WebSocketError.SOCKET_OUTPUT_STREAM_FAILURE, "Failed to get the output stream from the raw socket: " + e.getMessage(), e);
    }
  }
  

















  private static String generateWebSocketKey()
  {
    byte[] data = new byte[16];
    

    Misc.nextBytes(data);
    

    return Base64.encode(data);
  }
  




  private void writeHandshake(WebSocketOutputStream output, String key)
    throws WebSocketException
  {
    mHandshakeBuilder.setKey(key);
    String requestLine = mHandshakeBuilder.buildRequestLine();
    List<String[]> headers = mHandshakeBuilder.buildHeaders();
    String handshake = HandshakeBuilder.build(requestLine, headers);
    

    mListenerManager.callOnSendingHandshake(requestLine, headers);
    

    try
    {
      output.write(handshake);
      output.flush();


    }
    catch (IOException e)
    {

      throw new WebSocketException(WebSocketError.OPENING_HAHDSHAKE_REQUEST_FAILURE, "Failed to send an opening handshake request to the server: " + e.getMessage(), e);
    }
  }
  



  private Map<String, List<String>> readHandshake(WebSocketInputStream input, String key)
    throws WebSocketException
  {
    return new HandshakeReader(this).readHandshake(input, key);
  }
  












  private void startThreads()
  {
    ReadingThread readingThread = new ReadingThread(this);
    WritingThread writingThread = new WritingThread(this);
    
    synchronized (mThreadsLock)
    {
      mReadingThread = readingThread;
      mWritingThread = writingThread;
    }
    

    readingThread.callOnThreadCreated();
    writingThread.callOnThreadCreated();
    
    readingThread.start();
    writingThread.start();
  }
  















  private void stopThreads(long closeDelay)
  {
    synchronized (mThreadsLock)
    {
      ReadingThread readingThread = mReadingThread;
      WritingThread writingThread = mWritingThread;
      
      mReadingThread = null;
      mWritingThread = null; }
    WritingThread writingThread;
    ReadingThread readingThread;
    if (readingThread != null)
    {
      readingThread.requestStop(closeDelay);
    }
    
    if (writingThread != null)
    {
      writingThread.requestStop();
    }
  }
  




  WebSocketInputStream getInput()
  {
    return mInput;
  }
  




  WebSocketOutputStream getOutput()
  {
    return mOutput;
  }
  




  StateManager getStateManager()
  {
    return mStateManager;
  }
  




  ListenerManager getListenerManager()
  {
    return mListenerManager;
  }
  




  HandshakeBuilder getHandshakeBuilder()
  {
    return mHandshakeBuilder;
  }
  




  void setAgreedExtensions(List<WebSocketExtension> extensions)
  {
    mAgreedExtensions = extensions;
  }
  




  void setAgreedProtocol(String protocol)
  {
    mAgreedProtocol = protocol;
  }
  




  void onReadingThreadStarted()
  {
    boolean bothStarted = false;
    
    synchronized (mThreadsLock)
    {
      mReadingThreadStarted = true;
      
      if (mWritingThreadStarted)
      {

        bothStarted = true;
      }
    }
    

    callOnConnectedIfNotYet();
    

    if (bothStarted)
    {
      onThreadsStarted();
    }
  }
  




  void onWritingThreadStarted()
  {
    boolean bothStarted = false;
    
    synchronized (mThreadsLock)
    {
      mWritingThreadStarted = true;
      
      if (mReadingThreadStarted)
      {

        bothStarted = true;
      }
    }
    

    callOnConnectedIfNotYet();
    

    if (bothStarted)
    {
      onThreadsStarted();
    }
  }
  






  private void callOnConnectedIfNotYet()
  {
    synchronized (mOnConnectedCalledLock)
    {

      if (mOnConnectedCalled)
      {

        return;
      }
      
      mOnConnectedCalled = true;
    }
    

    mListenerManager.callOnConnected(mServerHeaders);
  }
  








  private void onThreadsStarted()
  {
    mPingSender.start();
    

    mPongSender.start();
  }
  




  void onReadingThreadFinished(WebSocketFrame closeFrame)
  {
    synchronized (mThreadsLock)
    {
      mReadingThreadFinished = true;
      mServerCloseFrame = closeFrame;
      
      if (!mWritingThreadFinished)
      {

        return;
      }
    }
    

    onThreadsFinished();
  }
  




  void onWritingThreadFinished(WebSocketFrame closeFrame)
  {
    synchronized (mThreadsLock)
    {
      mWritingThreadFinished = true;
      mClientCloseFrame = closeFrame;
      
      if (!mReadingThreadFinished)
      {

        return;
      }
    }
    

    onThreadsFinished();
  }
  






  private void onThreadsFinished()
  {
    finish();
  }
  


  void finish()
  {
    mPingSender.stop();
    mPongSender.stop();
    

    Socket socket = mSocketConnector.getSocket();
    if (socket != null)
    {
      try
      {
        socket.close();
      }
      catch (Throwable localThrowable) {}
    }
    



    synchronized (mStateManager)
    {

      mStateManager.setState(WebSocketState.CLOSED);
    }
    

    mListenerManager.callOnStateChanged(WebSocketState.CLOSED);
    

    mListenerManager.callOnDisconnected(mServerCloseFrame, mClientCloseFrame, mStateManager
      .getClosedByServer());
  }
  




  private void finishAsynchronously()
  {
    WebSocketThread thread = new FinishThread(this);
    

    thread.callOnThreadCreated();
    
    thread.start();
  }
  




  private PerMessageCompressionExtension findAgreedPerMessageCompressionExtension()
  {
    if (mAgreedExtensions == null)
    {
      return null;
    }
    
    for (WebSocketExtension extension : mAgreedExtensions)
    {
      if ((extension instanceof PerMessageCompressionExtension))
      {
        return (PerMessageCompressionExtension)extension;
      }
    }
    
    return null;
  }
  






  PerMessageCompressionExtension getPerMessageCompressionExtension()
  {
    return mPerMessageCompressionExtension;
  }
}
