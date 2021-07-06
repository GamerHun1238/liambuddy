package okhttp3.internal.http2;

import java.io.IOException;
import java.util.List;
import okio.BufferedSource;




































































public abstract interface PushObserver
{
  public static final PushObserver CANCEL = new PushObserver()
  {
    public boolean onRequest(int streamId, List<Header> requestHeaders) {
      return true;
    }
    
    public boolean onHeaders(int streamId, List<Header> responseHeaders, boolean last) {
      return true;
    }
    
    public boolean onData(int streamId, BufferedSource source, int byteCount, boolean last) throws IOException
    {
      source.skip(byteCount);
      return true;
    }
    
    public void onReset(int streamId, ErrorCode errorCode) {}
  };
  
  public abstract boolean onRequest(int paramInt, List<Header> paramList);
  
  public abstract boolean onHeaders(int paramInt, List<Header> paramList, boolean paramBoolean);
  
  public abstract boolean onData(int paramInt1, BufferedSource paramBufferedSource, int paramInt2, boolean paramBoolean)
    throws IOException;
  
  public abstract void onReset(int paramInt, ErrorCode paramErrorCode);
}
