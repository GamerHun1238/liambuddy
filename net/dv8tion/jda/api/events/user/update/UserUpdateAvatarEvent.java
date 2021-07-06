package net.dv8tion.jda.api.events.user.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;



































public class UserUpdateAvatarEvent
  extends GenericUserUpdateEvent<String>
{
  public static final String IDENTIFIER = "avatar";
  
  public UserUpdateAvatarEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nullable String oldAvatar)
  {
    super(api, responseNumber, user, oldAvatar, user.getAvatarId(), "avatar");
  }
  





  @Nullable
  public String getOldAvatarId()
  {
    return (String)getOldValue();
  }
  





  @Nullable
  public String getOldAvatarUrl()
  {
    return previous == null ? null : String.format("https://cdn.discordapp.com/avatars/%s/%s.%s", new Object[] { getUser().getId(), previous, ((String)previous).startsWith("a_") ? "gif" : "png" });
  }
  





  @Nullable
  public String getNewAvatarId()
  {
    return (String)getNewValue();
  }
  





  @Nullable
  public String getNewAvatarUrl()
  {
    return next == null ? null : String.format("https://cdn.discordapp.com/avatars/%s/%s.%s", new Object[] { getUser().getId(), next, ((String)next).startsWith("a_") ? "gif" : "png" });
  }
}
