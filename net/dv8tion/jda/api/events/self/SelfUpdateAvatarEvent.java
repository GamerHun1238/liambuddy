package net.dv8tion.jda.api.events.self;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;























public class SelfUpdateAvatarEvent
  extends GenericSelfUpdateEvent<String>
{
  public static final String IDENTIFIER = "avatar";
  private static final String AVATAR_URL = "https://cdn.discordapp.com/avatars/%s/%s%s";
  
  public SelfUpdateAvatarEvent(@Nonnull JDA api, long responseNumber, @Nullable String oldAvatarId)
  {
    super(api, responseNumber, oldAvatarId, api.getSelfUser().getAvatarId(), "avatar");
  }
  





  @Nullable
  public String getOldAvatarId()
  {
    return (String)getOldValue();
  }
  





  @Nullable
  public String getOldAvatarUrl()
  {
    return previous == null ? null : String.format("https://cdn.discordapp.com/avatars/%s/%s%s", new Object[] { getSelfUser().getId(), previous, ((String)previous).startsWith("a_") ? ".gif" : ".png" });
  }
  





  @Nullable
  public String getNewAvatarId()
  {
    return (String)getNewValue();
  }
  





  @Nullable
  public String getNewAvatarUrl()
  {
    return next == null ? null : String.format("https://cdn.discordapp.com/avatars/%s/%s%s", new Object[] { getSelfUser().getId(), next, ((String)next).startsWith("a_") ? ".gif" : ".png" });
  }
}
