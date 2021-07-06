package net.dv8tion.jda.api.events;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.CloseCode;




















public class ShutdownEvent
  extends Event
{
  protected final OffsetDateTime shutdownTime;
  protected final int code;
  
  public ShutdownEvent(@Nonnull JDA api, @Nonnull OffsetDateTime shutdownTime, int code)
  {
    super(api);
    this.shutdownTime = shutdownTime;
    this.code = code;
  }
  






  @Nonnull
  public OffsetDateTime getTimeShutdown()
  {
    return shutdownTime;
  }
  








  @Nullable
  public CloseCode getCloseCode()
  {
    return CloseCode.from(code);
  }
  






  public int getCode()
  {
    return code;
  }
}
