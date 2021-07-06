package okhttp3.internal.connection;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http.RealInterceptorChain;














public final class ConnectInterceptor
  implements Interceptor
{
  public final OkHttpClient client;
  
  public ConnectInterceptor(OkHttpClient client)
  {
    this.client = client;
  }
  
  public Response intercept(Interceptor.Chain chain) throws IOException {
    RealInterceptorChain realChain = (RealInterceptorChain)chain;
    Request request = realChain.request();
    StreamAllocation streamAllocation = realChain.streamAllocation();
    

    boolean doExtensiveHealthChecks = !request.method().equals("GET");
    HttpCodec httpCodec = streamAllocation.newStream(client, chain, doExtensiveHealthChecks);
    RealConnection connection = streamAllocation.connection();
    
    return realChain.proceed(request, streamAllocation, httpCodec, connection);
  }
}
