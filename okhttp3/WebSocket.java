package okhttp3;

import javax.annotation.Nullable;
import okio.ByteString;

public abstract interface WebSocket
{
  public abstract Request request();
  
  public abstract long queueSize();
  
  public abstract boolean send(String paramString);
  
  public abstract boolean send(ByteString paramByteString);
  
  public abstract boolean close(int paramInt, @Nullable String paramString);
  
  public abstract void cancel();
  
  public static abstract interface Factory
  {
    public abstract WebSocket newWebSocket(Request paramRequest, WebSocketListener paramWebSocketListener);
  }
}
