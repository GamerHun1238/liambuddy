package net.dv8tion.jda.api.events.self;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;























public class SelfUpdateDiscriminatorEvent
  extends GenericSelfUpdateEvent<String>
{
  public static final String IDENTIFIER = "discriminator";
  
  public SelfUpdateDiscriminatorEvent(@Nonnull JDA api, long responseNumber, @Nonnull String oldDiscriminator)
  {
    super(api, responseNumber, oldDiscriminator, api.getSelfUser().getDiscriminator(), "discriminator");
  }
  





  @Nonnull
  public String getOldDiscriminator()
  {
    return getOldValue();
  }
  





  @Nonnull
  public String getNewDiscriminator()
  {
    return getNewValue();
  }
  

  @Nonnull
  public String getOldValue()
  {
    return (String)super.getOldValue();
  }
  

  @Nonnull
  public String getNewValue()
  {
    return (String)super.getNewValue();
  }
}
