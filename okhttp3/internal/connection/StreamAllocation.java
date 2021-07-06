package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.List;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.ConnectionPool;
import okhttp3.EventListener;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http2.ConnectionShutdownException;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.StreamResetException;


























































public final class StreamAllocation
{
  public final Address address;
  private RouteSelector.Selection routeSelection;
  private Route route;
  private final ConnectionPool connectionPool;
  public final Call call;
  public final EventListener eventListener;
  private final Object callStackTrace;
  private final RouteSelector routeSelector;
  private int refusedStreamCount;
  private RealConnection connection;
  private boolean reportedAcquired;
  private boolean released;
  private boolean canceled;
  private HttpCodec codec;
  
  public StreamAllocation(ConnectionPool connectionPool, Address address, Call call, EventListener eventListener, Object callStackTrace)
  {
    this.connectionPool = connectionPool;
    this.address = address;
    this.call = call;
    this.eventListener = eventListener;
    routeSelector = new RouteSelector(address, routeDatabase(), call, eventListener);
    this.callStackTrace = callStackTrace;
  }
  
  /* Error */
  public HttpCodec newStream(okhttp3.OkHttpClient client, okhttp3.Interceptor.Chain chain, boolean doExtensiveHealthChecks)
  {
    // Byte code:
    //   0: aload_2
    //   1: invokeinterface 11 1 0
    //   6: istore 4
    //   8: aload_2
    //   9: invokeinterface 12 1 0
    //   14: istore 5
    //   16: aload_2
    //   17: invokeinterface 13 1 0
    //   22: istore 6
    //   24: aload_1
    //   25: invokevirtual 14	okhttp3/OkHttpClient:pingIntervalMillis	()I
    //   28: istore 7
    //   30: aload_1
    //   31: invokevirtual 15	okhttp3/OkHttpClient:retryOnConnectionFailure	()Z
    //   34: istore 8
    //   36: aload_0
    //   37: iload 4
    //   39: iload 5
    //   41: iload 6
    //   43: iload 7
    //   45: iload 8
    //   47: iload_3
    //   48: invokespecial 16	okhttp3/internal/connection/StreamAllocation:findHealthyConnection	(IIIIZZ)Lokhttp3/internal/connection/RealConnection;
    //   51: astore 9
    //   53: aload 9
    //   55: aload_1
    //   56: aload_2
    //   57: aload_0
    //   58: invokevirtual 17	okhttp3/internal/connection/RealConnection:newCodec	(Lokhttp3/OkHttpClient;Lokhttp3/Interceptor$Chain;Lokhttp3/internal/connection/StreamAllocation;)Lokhttp3/internal/http/HttpCodec;
    //   61: astore 10
    //   63: aload_0
    //   64: getfield 2	okhttp3/internal/connection/StreamAllocation:connectionPool	Lokhttp3/ConnectionPool;
    //   67: dup
    //   68: astore 11
    //   70: monitorenter
    //   71: aload_0
    //   72: aload 10
    //   74: putfield 18	okhttp3/internal/connection/StreamAllocation:codec	Lokhttp3/internal/http/HttpCodec;
    //   77: aload 10
    //   79: aload 11
    //   81: monitorexit
    //   82: areturn
    //   83: astore 12
    //   85: aload 11
    //   87: monitorexit
    //   88: aload 12
    //   90: athrow
    //   91: astore 9
    //   93: new 20	okhttp3/internal/connection/RouteException
    //   96: dup
    //   97: aload 9
    //   99: invokespecial 21	okhttp3/internal/connection/RouteException:<init>	(Ljava/io/IOException;)V
    //   102: athrow
    // Line number table:
    //   Java source line #107	-> byte code offset #0
    //   Java source line #108	-> byte code offset #8
    //   Java source line #109	-> byte code offset #16
    //   Java source line #110	-> byte code offset #24
    //   Java source line #111	-> byte code offset #30
    //   Java source line #114	-> byte code offset #36
    //   Java source line #116	-> byte code offset #53
    //   Java source line #118	-> byte code offset #63
    //   Java source line #119	-> byte code offset #71
    //   Java source line #120	-> byte code offset #77
    //   Java source line #121	-> byte code offset #83
    //   Java source line #122	-> byte code offset #91
    //   Java source line #123	-> byte code offset #93
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	103	0	this	StreamAllocation
    //   0	103	1	client	okhttp3.OkHttpClient
    //   0	103	2	chain	okhttp3.Interceptor.Chain
    //   0	103	3	doExtensiveHealthChecks	boolean
    //   6	32	4	connectTimeout	int
    //   14	26	5	readTimeout	int
    //   22	20	6	writeTimeout	int
    //   28	16	7	pingIntervalMillis	int
    //   34	12	8	connectionRetryEnabled	boolean
    //   51	3	9	resultConnection	RealConnection
    //   91	7	9	e	IOException
    //   61	17	10	resultCodec	HttpCodec
    //   83	6	12	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   71	82	83	finally
    //   83	88	83	finally
    //   36	82	91	java/io/IOException
    //   83	91	91	java/io/IOException
  }
  
  private RealConnection findHealthyConnection(int connectTimeout, int readTimeout, int writeTimeout, int pingIntervalMillis, boolean connectionRetryEnabled, boolean doExtensiveHealthChecks)
    throws IOException
  {
    RealConnection candidate;
    for (;;)
    {
      candidate = findConnection(connectTimeout, readTimeout, writeTimeout, pingIntervalMillis, connectionRetryEnabled);
      


      synchronized (connectionPool) {
        if (successCount == 0) {
          return candidate;
        }
      }
      


      if (candidate.isHealthy(doExtensiveHealthChecks)) break;
      noNewStreams();
    }
    

    return candidate;
  }
  




  private RealConnection findConnection(int connectTimeout, int readTimeout, int writeTimeout, int pingIntervalMillis, boolean connectionRetryEnabled)
    throws IOException
  {
    boolean foundPooledConnection = false;
    RealConnection result = null;
    Route selectedRoute = null;
    

    synchronized (connectionPool) {
      if (released) throw new IllegalStateException("released");
      if (codec != null) throw new IllegalStateException("codec != null");
      if (canceled) { throw new IOException("Canceled");
      }
      

      Connection releasedConnection = connection;
      Socket toClose = releaseIfNoNewStreams();
      if (connection != null)
      {
        result = connection;
        releasedConnection = null;
      }
      if (!reportedAcquired)
      {
        releasedConnection = null;
      }
      
      if (result == null)
      {
        Internal.instance.acquire(connectionPool, address, this, null);
        if (connection != null) {
          foundPooledConnection = true;
          result = connection;
        } else {
          selectedRoute = this.route;
        } } }
    Socket toClose;
    Connection releasedConnection;
    Util.closeQuietly(toClose);
    
    if (releasedConnection != null) {
      eventListener.connectionReleased(call, releasedConnection);
    }
    if (foundPooledConnection) {
      eventListener.connectionAcquired(call, result);
    }
    if (result != null)
    {
      return result;
    }
    

    boolean newRouteSelection = false;
    if ((selectedRoute == null) && ((routeSelection == null) || (!routeSelection.hasNext()))) {
      newRouteSelection = true;
      routeSelection = routeSelector.next();
    }
    
    synchronized (connectionPool) {
      if (canceled) { throw new IOException("Canceled");
      }
      if (newRouteSelection)
      {

        List<Route> routes = routeSelection.getAll();
        int i = 0; for (int size = routes.size(); i < size; i++) {
          Route route = (Route)routes.get(i);
          Internal.instance.acquire(connectionPool, address, this, route);
          if (connection != null) {
            foundPooledConnection = true;
            result = connection;
            this.route = route;
            break;
          }
        }
      }
      
      if (!foundPooledConnection) {
        if (selectedRoute == null) {
          selectedRoute = routeSelection.next();
        }
        


        this.route = selectedRoute;
        refusedStreamCount = 0;
        result = new RealConnection(connectionPool, selectedRoute);
        acquire(result, false);
      }
    }
    

    if (foundPooledConnection) {
      eventListener.connectionAcquired(call, result);
      return result;
    }
    

    result.connect(connectTimeout, readTimeout, writeTimeout, pingIntervalMillis, connectionRetryEnabled, call, eventListener);
    
    routeDatabase().connected(result.route());
    
    Socket socket = null;
    synchronized (connectionPool) {
      reportedAcquired = true;
      

      Internal.instance.put(connectionPool, result);
      


      if (result.isMultiplexed()) {
        socket = Internal.instance.deduplicate(connectionPool, address, this);
        result = connection;
      }
    }
    Util.closeQuietly(socket);
    
    eventListener.connectionAcquired(call, result);
    return result;
  }
  





  private Socket releaseIfNoNewStreams()
  {
    assert (Thread.holdsLock(connectionPool));
    RealConnection allocatedConnection = connection;
    if ((allocatedConnection != null) && (noNewStreams)) {
      return deallocate(false, false, true);
    }
    return null;
  }
  
  public void streamFinished(boolean noNewStreams, HttpCodec codec, long bytesRead, IOException e) {
    eventListener.responseBodyEnd(call, bytesRead);
    

    boolean callEnd;
    
    synchronized (connectionPool) {
      if ((codec == null) || (codec != this.codec)) {
        throw new IllegalStateException("expected " + this.codec + " but was " + codec);
      }
      if (!noNewStreams) {
        connection.successCount += 1;
      }
      Connection releasedConnection = connection;
      Socket socket = deallocate(noNewStreams, false, true);
      if (connection != null) releasedConnection = null;
      callEnd = released; }
    boolean callEnd;
    Connection releasedConnection; Socket socket; Util.closeQuietly(socket);
    if (releasedConnection != null) {
      eventListener.connectionReleased(call, releasedConnection);
    }
    
    if (e != null) {
      e = Internal.instance.timeoutExit(call, e);
      eventListener.callFailed(call, e);
    } else if (callEnd) {
      Internal.instance.timeoutExit(call, null);
      eventListener.callEnd(call);
    }
  }
  
  public HttpCodec codec() {
    synchronized (connectionPool) {
      return codec;
    }
  }
  
  private RouteDatabase routeDatabase() {
    return Internal.instance.routeDatabase(connectionPool);
  }
  
  public Route route() {
    return route;
  }
  
  public synchronized RealConnection connection() {
    return connection;
  }
  

  public void release(boolean callEnd)
  {
    synchronized (connectionPool) {
      Connection releasedConnection = connection;
      Socket socket = deallocate(false, true, false);
      if (connection != null) releasedConnection = null; }
    Connection releasedConnection;
    Socket socket; Util.closeQuietly(socket);
    if (releasedConnection != null) {
      if (callEnd) {
        Internal.instance.timeoutExit(call, null);
      }
      eventListener.connectionReleased(call, releasedConnection);
      if (callEnd) {
        eventListener.callEnd(call);
      }
    }
  }
  


  public void noNewStreams()
  {
    synchronized (connectionPool) {
      Connection releasedConnection = connection;
      Socket socket = deallocate(true, false, false);
      if (connection != null) releasedConnection = null; }
    Connection releasedConnection;
    Socket socket; Util.closeQuietly(socket);
    if (releasedConnection != null) {
      eventListener.connectionReleased(call, releasedConnection);
    }
  }
  






  private Socket deallocate(boolean noNewStreams, boolean released, boolean streamFinished)
  {
    assert (Thread.holdsLock(connectionPool));
    
    if (streamFinished) {
      codec = null;
    }
    if (released) {
      this.released = true;
    }
    Socket socket = null;
    if (connection != null) {
      if (noNewStreams) {
        connection.noNewStreams = true;
      }
      if ((codec == null) && ((this.released) || (connection.noNewStreams))) {
        release(connection);
        if (connection.allocations.isEmpty()) {
          connection.idleAtNanos = System.nanoTime();
          if (Internal.instance.connectionBecameIdle(connectionPool, connection)) {
            socket = connection.socket();
          }
        }
        connection = null;
      }
    }
    return socket;
  }
  
  public void cancel()
  {
    RealConnection connectionToCancel;
    synchronized (connectionPool) {
      canceled = true;
      HttpCodec codecToCancel = codec;
      connectionToCancel = connection; }
    RealConnection connectionToCancel;
    HttpCodec codecToCancel; if (codecToCancel != null) {
      codecToCancel.cancel();
    } else if (connectionToCancel != null) {
      connectionToCancel.cancel();
    }
  }
  

  public void streamFailed(IOException e)
  {
    boolean noNewStreams = false;
    
    synchronized (connectionPool) {
      if ((e instanceof StreamResetException)) {
        ErrorCode errorCode = errorCode;
        if (errorCode == ErrorCode.REFUSED_STREAM)
        {
          refusedStreamCount += 1;
          if (refusedStreamCount > 1) {
            noNewStreams = true;
            route = null;
          }
        } else if (errorCode != ErrorCode.CANCEL)
        {
          noNewStreams = true;
          route = null;
        }
      } else if ((connection != null) && (
        (!connection.isMultiplexed()) || ((e instanceof ConnectionShutdownException)))) {
        noNewStreams = true;
        

        if (connection.successCount == 0) {
          if ((route != null) && (e != null)) {
            routeSelector.connectFailed(route, e);
          }
          route = null;
        }
      }
      Connection releasedConnection = connection;
      Socket socket = deallocate(noNewStreams, false, true);
      if ((connection != null) || (!reportedAcquired)) releasedConnection = null; }
    Connection releasedConnection;
    Socket socket;
    Util.closeQuietly(socket);
    if (releasedConnection != null) {
      eventListener.connectionReleased(call, releasedConnection);
    }
  }
  



  public void acquire(RealConnection connection, boolean reportedAcquired)
  {
    assert (Thread.holdsLock(connectionPool));
    if (this.connection != null) { throw new IllegalStateException();
    }
    this.connection = connection;
    this.reportedAcquired = reportedAcquired;
    allocations.add(new StreamAllocationReference(this, callStackTrace));
  }
  
  private void release(RealConnection connection)
  {
    int i = 0; for (int size = allocations.size(); i < size; i++) {
      Reference<StreamAllocation> reference = (Reference)allocations.get(i);
      if (reference.get() == this) {
        allocations.remove(i);
        return;
      }
    }
    throw new IllegalStateException();
  }
  







  public Socket releaseAndAcquire(RealConnection newConnection)
  {
    assert (Thread.holdsLock(connectionPool));
    if ((codec != null) || (connection.allocations.size() != 1)) { throw new IllegalStateException();
    }
    
    Reference<StreamAllocation> onlyAllocation = (Reference)connection.allocations.get(0);
    Socket socket = deallocate(true, false, false);
    

    connection = newConnection;
    allocations.add(onlyAllocation);
    
    return socket;
  }
  
  public boolean hasMoreRoutes() {
    return (route != null) || ((routeSelection != null) && 
      (routeSelection.hasNext())) || 
      (routeSelector.hasNext());
  }
  
  public String toString() {
    RealConnection connection = connection();
    return connection != null ? connection.toString() : address.toString();
  }
  

  public static final class StreamAllocationReference
    extends WeakReference<StreamAllocation>
  {
    public final Object callStackTrace;
    
    StreamAllocationReference(StreamAllocation referent, Object callStackTrace)
    {
      super();
      this.callStackTrace = callStackTrace;
    }
  }
}
