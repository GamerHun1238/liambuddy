package com.neovisionaries.ws.client;

import java.util.Timer;
import java.util.TimerTask;




















abstract class PeriodicalFrameSender
{
  private final WebSocket mWebSocket;
  private String mTimerName;
  private Timer mTimer;
  private boolean mScheduled;
  private long mInterval;
  private PayloadGenerator mGenerator;
  
  public PeriodicalFrameSender(WebSocket webSocket, String timerName, PayloadGenerator generator)
  {
    mWebSocket = webSocket;
    mTimerName = timerName;
    mGenerator = generator;
  }
  

  public void start()
  {
    setInterval(getInterval());
  }
  

  public void stop()
  {
    synchronized (this)
    {
      if (mTimer == null)
      {
        return;
      }
      
      mScheduled = false;
      mTimer.cancel();
    }
  }
  

  public long getInterval()
  {
    synchronized (this)
    {
      return mInterval;
    }
  }
  

  public void setInterval(long interval)
  {
    if (interval < 0L)
    {
      interval = 0L;
    }
    
    synchronized (this)
    {
      mInterval = interval;
    }
    
    if (interval == 0L)
    {
      return;
    }
    
    if (!mWebSocket.isOpen())
    {
      return;
    }
    
    synchronized (this)
    {
      if (mTimer == null)
      {
        if (mTimerName == null)
        {
          mTimer = new Timer();
        }
        else
        {
          mTimer = new Timer(mTimerName);
        }
      }
      
      if (!mScheduled)
      {
        mScheduled = schedule(mTimer, new Task(null), interval);
      }
    }
  }
  

  public PayloadGenerator getPayloadGenerator()
  {
    synchronized (this)
    {
      return mGenerator;
    }
  }
  

  public void setPayloadGenerator(PayloadGenerator generator)
  {
    synchronized (this)
    {
      mGenerator = generator;
    }
  }
  

  public String getTimerName()
  {
    return mTimerName;
  }
  

  public void setTimerName(String timerName)
  {
    synchronized (this)
    {
      mTimerName = timerName;
    }
  }
  
  private final class Task extends TimerTask
  {
    private Task() {}
    
    public void run()
    {
      PeriodicalFrameSender.this.doTask();
    }
  }
  

  private void doTask()
  {
    synchronized (this)
    {
      if ((mInterval == 0L) || (!mWebSocket.isOpen()))
      {
        mScheduled = false;
        

        return;
      }
      

      mWebSocket.sendFrame(createFrame());
      

      mScheduled = schedule(mTimer, new Task(null), mInterval);
    }
  }
  


  private WebSocketFrame createFrame()
  {
    byte[] payload = generatePayload();
    

    return createFrame(payload);
  }
  

  private byte[] generatePayload()
  {
    if (mGenerator == null)
    {
      return null;
    }
    

    try
    {
      return mGenerator.generate();
    }
    catch (Throwable t) {}
    

    return null;
  }
  



  private static boolean schedule(Timer timer, Task task, long interval)
  {
    try
    {
      timer.schedule(task, interval);
      

      return true;
    }
    catch (RuntimeException e) {}
    















    return false;
  }
  
  protected abstract WebSocketFrame createFrame(byte[] paramArrayOfByte);
}
