package net.dv8tion.jda.internal.entities;

import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.MissingAccessException;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Channels;
import net.dv8tion.jda.internal.requests.Route.CompiledRoute;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.PermissionOverrideActionImpl;












public class PermissionOverrideImpl
  implements PermissionOverride
{
  private final long id;
  private final boolean isRole;
  private final JDAImpl api;
  private GuildChannel channel;
  protected PermissionOverrideAction manager;
  private long allow;
  private long deny;
  
  public PermissionOverrideImpl(GuildChannel channel, long id, boolean isRole)
  {
    this.isRole = isRole;
    api = ((JDAImpl)channel.getJDA());
    this.channel = channel;
    this.id = id;
  }
  

  public long getAllowedRaw()
  {
    return allow;
  }
  

  public long getInheritRaw()
  {
    return (allow | deny) ^ 0xFFFFFFFFFFFFFFFF;
  }
  

  public long getDeniedRaw()
  {
    return deny;
  }
  

  @Nonnull
  public EnumSet<Permission> getAllowed()
  {
    return Permission.getPermissions(allow);
  }
  

  @Nonnull
  public EnumSet<Permission> getInherit()
  {
    return Permission.getPermissions(getInheritRaw());
  }
  

  @Nonnull
  public EnumSet<Permission> getDenied()
  {
    return Permission.getPermissions(deny);
  }
  

  @Nonnull
  public JDA getJDA()
  {
    return api;
  }
  

  public IPermissionHolder getPermissionHolder()
  {
    return isRole ? getRole() : getMember();
  }
  

  public Member getMember()
  {
    return getGuild().getMemberById(id);
  }
  

  public Role getRole()
  {
    return getGuild().getRoleById(id);
  }
  

  @Nonnull
  public GuildChannel getChannel()
  {
    GuildChannel realChannel = api.getGuildChannelById(channel.getType(), channel.getIdLong());
    if (realChannel != null)
      channel = realChannel;
    return channel;
  }
  

  @Nonnull
  public Guild getGuild()
  {
    return getChannel().getGuild();
  }
  

  public boolean isMemberOverride()
  {
    return !isRole;
  }
  

  public boolean isRoleOverride()
  {
    return isRole;
  }
  

  @Nonnull
  public PermissionOverrideAction getManager()
  {
    Member selfMember = getGuild().getSelfMember();
    GuildChannel channel = getChannel();
    if (!selfMember.hasPermission(channel, new Permission[] { Permission.VIEW_CHANNEL }))
      throw new MissingAccessException(channel, Permission.VIEW_CHANNEL);
    if (!selfMember.hasAccess(channel))
      throw new MissingAccessException(channel, Permission.VOICE_CONNECT);
    if (!selfMember.hasPermission(channel, new Permission[] { Permission.MANAGE_PERMISSIONS }))
      throw new InsufficientPermissionException(channel, Permission.MANAGE_PERMISSIONS);
    if (manager == null)
      return this.manager = new PermissionOverrideActionImpl(this).setOverride(false);
    return manager;
  }
  


  @Nonnull
  public AuditableRestAction<Void> delete()
  {
    Member selfMember = getGuild().getSelfMember();
    GuildChannel channel = getChannel();
    if (!selfMember.hasPermission(channel, new Permission[] { Permission.VIEW_CHANNEL }))
      throw new MissingAccessException(channel, Permission.VIEW_CHANNEL);
    if (!selfMember.hasAccess(channel))
      throw new MissingAccessException(channel, Permission.VOICE_CONNECT);
    if (!selfMember.hasPermission(channel, new Permission[] { Permission.MANAGE_PERMISSIONS })) {
      throw new InsufficientPermissionException(channel, Permission.MANAGE_PERMISSIONS);
    }
    Route.CompiledRoute route = Route.Channels.DELETE_PERM_OVERRIDE.compile(new String[] { this.channel.getId(), getId() });
    return new AuditableRestActionImpl(getJDA(), route);
  }
  

  public long getIdLong()
  {
    return id;
  }
  
  public PermissionOverrideImpl setAllow(long allow)
  {
    this.allow = allow;
    return this;
  }
  
  public PermissionOverrideImpl setDeny(long deny)
  {
    this.deny = deny;
    return this;
  }
  

  public boolean equals(Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof PermissionOverrideImpl))
      return false;
    PermissionOverrideImpl oPerm = (PermissionOverrideImpl)o;
    return (id == id) && (channel.getIdLong() == channel.getIdLong());
  }
  

  public int hashCode()
  {
    return Objects.hash(new Object[] { Long.valueOf(id), Long.valueOf(channel.getIdLong()) });
  }
  

  public String toString()
  {
    return "PermOver:(" + (isMemberOverride() ? "M" : "R") + ")(" + channel.getId() + " | " + getId() + ")";
  }
}
