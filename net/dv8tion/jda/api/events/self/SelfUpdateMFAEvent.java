package net.dv8tion.jda.api.events.self;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;

























public class SelfUpdateMFAEvent
  extends GenericSelfUpdateEvent<Boolean>
{
  public static final String IDENTIFIER = "mfa_enabled";
  
  public SelfUpdateMFAEvent(@Nonnull JDA api, long responseNumber, boolean wasMfaEnabled)
  {
    super(api, responseNumber, Boolean.valueOf(wasMfaEnabled), Boolean.valueOf(!wasMfaEnabled), "mfa_enabled");
  }
  





  public boolean wasMfaEnabled()
  {
    return getOldValue().booleanValue();
  }
  

  @Nonnull
  public Boolean getOldValue()
  {
    return (Boolean)super.getOldValue();
  }
  

  @Nonnull
  public Boolean getNewValue()
  {
    return (Boolean)super.getNewValue();
  }
}
