package com.neovisionaries.ws.client;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;












public class SocketInitiator
{
  private final SocketFactory mSocketFactory;
  private final Address mAddress;
  private final int mConnectTimeout;
  private final String[] mServerNames;
  private final DualStackMode mMode;
  private final int mFallbackDelay;
  
  private class Signal
  {
    private final CountDownLatch mLatch;
    private final int mMaxDelay;
    
    Signal(int maxDelay)
    {
      mLatch = new CountDownLatch(1);
      mMaxDelay = maxDelay;
    }
    

    boolean isDone()
    {
      return mLatch.getCount() == 0L;
    }
    
    void await()
      throws InterruptedException
    {
      mLatch.await(mMaxDelay, TimeUnit.MILLISECONDS);
    }
    

    void done()
    {
      mLatch.countDown();
    }
  }
  


  private class SocketRacer
    extends Thread
  {
    private final SocketInitiator.SocketFuture mFuture;
    

    private final SocketFactory mSocketFactory;
    

    private final SocketAddress mSocketAddress;
    

    private String[] mServerNames;
    

    private final int mConnectTimeout;
    

    private final SocketInitiator.Signal mStartSignal;
    
    private final SocketInitiator.Signal mDoneSignal;
    

    SocketRacer(SocketInitiator.SocketFuture future, SocketFactory socketFactory, SocketAddress socketAddress, String[] serverNames, int connectTimeout, SocketInitiator.Signal startSignal, SocketInitiator.Signal doneSignal)
    {
      mFuture = future;
      mSocketFactory = socketFactory;
      mSocketAddress = socketAddress;
      mServerNames = serverNames;
      mConnectTimeout = connectTimeout;
      mStartSignal = startSignal;
      mDoneSignal = doneSignal;
    }
    
    public void run()
    {
      Socket socket = null;
      
      try
      {
        if (mStartSignal != null)
        {
          mStartSignal.await();
        }
        

        if (mFuture.hasSocket())
        {
          return;
        }
        

        socket = mSocketFactory.createSocket();
        

        SNIHelper.setServerNames(socket, mServerNames);
        

        socket.connect(mSocketAddress, mConnectTimeout);
        

        complete(socket);
      }
      catch (Exception e)
      {
        abort(e);
        
        if (socket != null)
        {
          try
          {
            socket.close();
          }
          catch (IOException localIOException) {}
        }
      }
    }
    




    private void complete(Socket socket)
    {
      synchronized (mFuture)
      {

        if (mDoneSignal.isDone()) {
          return;
        }
        

        mFuture.setSocket(this, socket);
        

        mDoneSignal.done();
      }
    }
    

    void abort(Exception exception)
    {
      synchronized (mFuture)
      {

        if (mDoneSignal.isDone())
        {
          return;
        }
        

        mFuture.setException(exception);
        

        mDoneSignal.done();
      }
    }
  }
  



  private class SocketFuture
  {
    private CountDownLatch mLatch;
    


    private List<SocketInitiator.SocketRacer> mRacers;
    


    private Socket mSocket;
    

    private Exception mException;
    


    private SocketFuture() {}
    


    synchronized boolean hasSocket()
    {
      return mSocket != null;
    }
    


    synchronized void setSocket(SocketInitiator.SocketRacer current, Socket socket)
    {
      if ((mLatch == null) || (mRacers == null))
      {
        throw new IllegalStateException("Cannot set socket before awaiting!");
      }
      

      if (mSocket == null)
      {
        mSocket = socket;
        

        for (SocketInitiator.SocketRacer racer : mRacers)
        {

          if (racer != current)
          {


            racer.abort(new InterruptedException());
            racer.interrupt();
          }
        }
      }
      else
      {
        try {
          socket.close();
        }
        catch (IOException localIOException1) {}
      }
      




      mLatch.countDown();
    }
    


    synchronized void setException(Exception exception)
    {
      if ((mLatch == null) || (mRacers == null))
      {
        throw new IllegalStateException("Cannot set exception before awaiting!");
      }
      

      if (mException == null)
      {
        mException = exception;
      }
      

      mLatch.countDown();
    }
    

    Socket await(List<SocketInitiator.SocketRacer> racers)
      throws Exception
    {
      mRacers = racers;
      

      mLatch = new CountDownLatch(mRacers.size());
      

      for (SocketInitiator.SocketRacer racer : mRacers)
      {
        racer.start();
      }
      

      mLatch.await();
      

      if (mSocket != null)
      {
        return mSocket;
      }
      if (mException != null)
      {
        throw mException;
      }
      

      throw new WebSocketException(WebSocketError.SOCKET_CONNECT_ERROR, "No viable interface to connect");
    }
  }
  













  public SocketInitiator(SocketFactory socketFactory, Address address, int connectTimeout, String[] serverNames, DualStackMode mode, int fallbackDelay)
  {
    mSocketFactory = socketFactory;
    mAddress = address;
    mConnectTimeout = connectTimeout;
    mServerNames = serverNames;
    mMode = mode;
    mFallbackDelay = fallbackDelay;
  }
  

  public Socket establish(InetAddress[] addresses)
    throws Exception
  {
    SocketFuture future = new SocketFuture(null);
    

    List<SocketRacer> racers = new ArrayList(addresses.length);
    int delay = 0;
    Signal startSignal = null;
    for (InetAddress address : addresses)
    {

      if (((mMode != DualStackMode.IPV4_ONLY) || ((address instanceof Inet4Address))) && ((mMode != DualStackMode.IPV6_ONLY) || ((address instanceof Inet6Address))))
      {





        delay += mFallbackDelay;
        

        Signal doneSignal = new Signal(delay);
        

        SocketAddress socketAddress = new InetSocketAddress(address, mAddress.getPort());
        SocketRacer racer = new SocketRacer(future, mSocketFactory, socketAddress, mServerNames, mConnectTimeout, startSignal, doneSignal);
        

        racers.add(racer);
        

        startSignal = doneSignal;
      }
    }
    
    return future.await(racers);
  }
}
