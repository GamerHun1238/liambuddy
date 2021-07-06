package net.dv8tion.jda.api.events.guild.override;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.StoreChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;














public class GenericPermissionOverrideEvent
  extends GenericGuildEvent
{
  protected final GuildChannel channel;
  protected final PermissionOverride override;
  
  public GenericPermissionOverrideEvent(@Nonnull JDA api, long responseNumber, @Nonnull GuildChannel channel, @Nonnull PermissionOverride override)
  {
    super(api, responseNumber, channel.getGuild());
    this.channel = channel;
    this.override = override;
  }
  





  @Nonnull
  public ChannelType getChannelType()
  {
    return channel.getType();
  }
  





  @Nonnull
  public GuildChannel getChannel()
  {
    return channel;
  }
  











  @Nonnull
  public TextChannel getTextChannel()
  {
    if ((channel instanceof TextChannel))
      return (TextChannel)channel;
    throw new IllegalStateException("This override is for a channel of type " + getChannelType());
  }
  











  @Nonnull
  public VoiceChannel getVoiceChannel()
  {
    if ((channel instanceof VoiceChannel))
      return (VoiceChannel)channel;
    throw new IllegalStateException("This override is for a channel of type " + getChannelType());
  }
  











  @Nonnull
  public StoreChannel getStoreChannel()
  {
    if ((channel instanceof StoreChannel))
      return (StoreChannel)channel;
    throw new IllegalStateException("This override is for a channel of type " + getChannelType());
  }
  












  @Nonnull
  public Category getCategory()
  {
    if ((channel instanceof Category))
      return (Category)channel;
    throw new IllegalStateException("This override is for a channel of type " + getChannelType());
  }
  





  @Nonnull
  public PermissionOverride getPermissionOverride()
  {
    return override;
  }
  






  public boolean isRoleOverride()
  {
    return override.isRoleOverride();
  }
  






  public boolean isMemberOverride()
  {
    return override.isMemberOverride();
  }
  






  @Nullable
  public IPermissionHolder getPermissionHolder()
  {
    return isMemberOverride() ? override.getMember() : override.getRole();
  }
  






  @Nullable
  public Member getMember()
  {
    return override.getMember();
  }
  





  @Nullable
  public Role getRole()
  {
    return override.getRole();
  }
}
