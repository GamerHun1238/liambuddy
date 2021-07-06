package net.dv8tion.jda.internal.entities;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.User.UserFlag;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Contract;
















public class UserById
  implements User
{
  protected final long id;
  
  public UserById(long id)
  {
    this.id = id;
  }
  

  public long getIdLong()
  {
    return id;
  }
  

  @Nonnull
  public String getAsMention()
  {
    return "<@" + getId() + ">";
  }
  

  public int hashCode()
  {
    return Long.hashCode(id);
  }
  

  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (!(obj instanceof User))
      return false;
    return ((User)obj).getIdLong() == id;
  }
  

  public String toString()
  {
    return "U:(" + getId() + ')';
  }
  
  @Contract("->fail")
  private void unsupported()
  {
    throw new UnsupportedOperationException("This User instance only wraps an ID. Other operations are unsupported");
  }
  

  @Nonnull
  public String getName()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public String getDiscriminator()
  {
    unsupported();
    return null;
  }
  

  @Nullable
  public String getAvatarId()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public String getDefaultAvatarId()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public String getAsTag()
  {
    unsupported();
    return null;
  }
  

  public boolean hasPrivateChannel()
  {
    unsupported();
    return false;
  }
  

  @Nonnull
  public RestAction<PrivateChannel> openPrivateChannel()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public List<Guild> getMutualGuilds()
  {
    unsupported();
    return null;
  }
  

  public boolean isBot()
  {
    unsupported();
    return false;
  }
  

  public boolean isSystem()
  {
    unsupported();
    return false;
  }
  

  @Nonnull
  public JDA getJDA()
  {
    unsupported();
    return null;
  }
  

  @Nonnull
  public EnumSet<User.UserFlag> getFlags()
  {
    unsupported();
    return null;
  }
  

  public int getFlagsRaw()
  {
    unsupported();
    return 0;
  }
}
