package net.dv8tion.jda.api.events.user.update;

import java.util.Collection;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.User.UserFlag;
































public class UserUpdateFlagsEvent
  extends GenericUserUpdateEvent<EnumSet<User.UserFlag>>
{
  public static final String IDENTIFIER = "public_flags";
  
  public UserUpdateFlagsEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull EnumSet<User.UserFlag> oldFlags)
  {
    super(api, responseNumber, user, oldFlags, user.getFlags(), "public_flags");
  }
  





  @Nonnull
  public EnumSet<User.UserFlag> getOldFlags()
  {
    return (EnumSet)getOldValue();
  }
  





  public int getOldFlagsRaw()
  {
    return User.UserFlag.getRaw((Collection)previous);
  }
  





  @Nonnull
  public EnumSet<User.UserFlag> getNewFlags()
  {
    return (EnumSet)getNewValue();
  }
  





  public int getNewFlagsRaw()
  {
    return User.UserFlag.getRaw((Collection)next);
  }
}
