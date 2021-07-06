package okhttp3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import okhttp3.internal.Util;






















public final class Dispatcher
{
  private int maxRequests = 64;
  private int maxRequestsPerHost = 5;
  
  @Nullable
  private Runnable idleCallback;
  
  @Nullable
  private ExecutorService executorService;
  private final Deque<RealCall.AsyncCall> readyAsyncCalls = new ArrayDeque();
  

  private final Deque<RealCall.AsyncCall> runningAsyncCalls = new ArrayDeque();
  

  private final Deque<RealCall> runningSyncCalls = new ArrayDeque();
  
  public Dispatcher(ExecutorService executorService) {
    this.executorService = executorService;
  }
  
  public Dispatcher() {}
  
  public synchronized ExecutorService executorService()
  {
    if (executorService == null)
    {
      executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp Dispatcher", false));
    }
    return executorService;
  }
  






  public void setMaxRequests(int maxRequests)
  {
    if (maxRequests < 1) {
      throw new IllegalArgumentException("max < 1: " + maxRequests);
    }
    synchronized (this) {
      this.maxRequests = maxRequests;
    }
    promoteAndExecute();
  }
  
  public synchronized int getMaxRequests() {
    return maxRequests;
  }
  










  public void setMaxRequestsPerHost(int maxRequestsPerHost)
  {
    if (maxRequestsPerHost < 1) {
      throw new IllegalArgumentException("max < 1: " + maxRequestsPerHost);
    }
    synchronized (this) {
      this.maxRequestsPerHost = maxRequestsPerHost;
    }
    promoteAndExecute();
  }
  
  public synchronized int getMaxRequestsPerHost() {
    return maxRequestsPerHost;
  }
  











  public synchronized void setIdleCallback(@Nullable Runnable idleCallback)
  {
    this.idleCallback = idleCallback;
  }
  
  void enqueue(RealCall.AsyncCall call) {
    synchronized (this) {
      readyAsyncCalls.add(call);
      


      if (!getforWebSocket) {
        RealCall.AsyncCall existingCall = findExistingCallWithHost(call.host());
        if (existingCall != null) call.reuseCallsPerHostFrom(existingCall);
      }
    }
    promoteAndExecute();
  }
  
  @Nullable
  private RealCall.AsyncCall findExistingCallWithHost(String host) { for (RealCall.AsyncCall existingCall : runningAsyncCalls) {
      if (existingCall.host().equals(host)) return existingCall;
    }
    for (RealCall.AsyncCall existingCall : readyAsyncCalls) {
      if (existingCall.host().equals(host)) return existingCall;
    }
    return null;
  }
  



  public synchronized void cancelAll()
  {
    for (RealCall.AsyncCall call : readyAsyncCalls) {
      call.get().cancel();
    }
    
    for (RealCall.AsyncCall call : runningAsyncCalls) {
      call.get().cancel();
    }
    
    for (RealCall call : runningSyncCalls) {
      call.cancel();
    }
  }
  






  private boolean promoteAndExecute()
  {
    assert (!Thread.holdsLock(this));
    
    List<RealCall.AsyncCall> executableCalls = new ArrayList();
    boolean isRunning;
    synchronized (this) {
      for (Iterator<RealCall.AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext();) {
        RealCall.AsyncCall asyncCall = (RealCall.AsyncCall)i.next();
        
        if (runningAsyncCalls.size() >= maxRequests) break;
        if (asyncCall.callsPerHost().get() < maxRequestsPerHost)
        {
          i.remove();
          asyncCall.callsPerHost().incrementAndGet();
          executableCalls.add(asyncCall);
          runningAsyncCalls.add(asyncCall);
        } }
      isRunning = runningCallsCount() > 0;
    }
    boolean isRunning;
    int i = 0; for (int size = executableCalls.size(); i < size; i++) {
      RealCall.AsyncCall asyncCall = (RealCall.AsyncCall)executableCalls.get(i);
      asyncCall.executeOn(executorService());
    }
    
    return isRunning;
  }
  
  synchronized void executed(RealCall call)
  {
    runningSyncCalls.add(call);
  }
  
  void finished(RealCall.AsyncCall call)
  {
    call.callsPerHost().decrementAndGet();
    finished(runningAsyncCalls, call);
  }
  
  void finished(RealCall call)
  {
    finished(runningSyncCalls, call);
  }
  
  private <T> void finished(Deque<T> calls, T call) {
    Runnable idleCallback;
    synchronized (this) {
      if (!calls.remove(call)) throw new AssertionError("Call wasn't in-flight!");
      idleCallback = this.idleCallback;
    }
    Runnable idleCallback;
    boolean isRunning = promoteAndExecute();
    
    if ((!isRunning) && (idleCallback != null)) {
      idleCallback.run();
    }
  }
  
  public synchronized List<Call> queuedCalls()
  {
    List<Call> result = new ArrayList();
    for (RealCall.AsyncCall asyncCall : readyAsyncCalls) {
      result.add(asyncCall.get());
    }
    return Collections.unmodifiableList(result);
  }
  
  public synchronized List<Call> runningCalls()
  {
    List<Call> result = new ArrayList();
    result.addAll(runningSyncCalls);
    for (RealCall.AsyncCall asyncCall : runningAsyncCalls) {
      result.add(asyncCall.get());
    }
    return Collections.unmodifiableList(result);
  }
  
  public synchronized int queuedCallsCount() {
    return readyAsyncCalls.size();
  }
  
  public synchronized int runningCallsCount() {
    return runningAsyncCalls.size() + runningSyncCalls.size();
  }
}
