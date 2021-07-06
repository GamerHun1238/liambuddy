package okhttp3;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

public abstract interface Interceptor
{
  public abstract Response intercept(Chain paramChain)
    throws IOException;
  
  public static abstract interface Chain
  {
    public abstract Request request();
    
    public abstract Response proceed(Request paramRequest)
      throws IOException;
    
    @Nullable
    public abstract Connection connection();
    
    public abstract Call call();
    
    public abstract int connectTimeoutMillis();
    
    public abstract Chain withConnectTimeout(int paramInt, TimeUnit paramTimeUnit);
    
    public abstract int readTimeoutMillis();
    
    public abstract Chain withReadTimeout(int paramInt, TimeUnit paramTimeUnit);
    
    public abstract int writeTimeoutMillis();
    
    public abstract Chain withWriteTimeout(int paramInt, TimeUnit paramTimeUnit);
  }
}
