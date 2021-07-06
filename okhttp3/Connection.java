package okhttp3;

import java.net.Socket;
import javax.annotation.Nullable;

public abstract interface Connection
{
  public abstract Route route();
  
  public abstract Socket socket();
  
  @Nullable
  public abstract Handshake handshake();
  
  public abstract Protocol protocol();
}
