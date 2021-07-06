package net.dv8tion.jda.internal.managers;

import gnu.trove.map.TLongObjectMap;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.MissingAccessException;
import net.dv8tion.jda.api.managers.PermOverrideManager;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.AbstractChannelImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Channels;
import okhttp3.RequestBody;




















public class PermOverrideManagerImpl
  extends ManagerBase<PermOverrideManager>
  implements PermOverrideManager
{
  protected final boolean role;
  protected PermissionOverride override;
  protected long allowed;
  protected long denied;
  
  public PermOverrideManagerImpl(PermissionOverride override)
  {
    super(override.getJDA(), Route.Channels.MODIFY_PERM_OVERRIDE
      .compile(new String[] {override
      .getChannel().getId(), override.getId() }));
    this.override = override;
    role = override.isRoleOverride();
    allowed = override.getAllowedRaw();
    denied = override.getDeniedRaw();
    if (isPermissionChecksEnabled()) {
      checkPermissions();
    }
  }
  
  private void setupValues() {
    if (!shouldUpdate(2L))
      allowed = getPermissionOverride().getAllowedRaw();
    if (!shouldUpdate(1L)) {
      denied = getPermissionOverride().getDeniedRaw();
    }
  }
  
  @Nonnull
  public PermissionOverride getPermissionOverride()
  {
    AbstractChannelImpl<?, ?> channel = (AbstractChannelImpl)override.getChannel();
    PermissionOverride realOverride = (PermissionOverride)channel.getOverrideMap().get(override.getIdLong());
    if (realOverride != null)
      override = realOverride;
    return override;
  }
  

  @Nonnull
  @CheckReturnValue
  public PermOverrideManagerImpl reset(long fields)
  {
    super.reset(fields);
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public PermOverrideManagerImpl reset(long... fields)
  {
    super.reset(fields);
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public PermOverrideManagerImpl reset()
  {
    super.reset();
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public PermOverrideManagerImpl grant(long permissions)
  {
    if (permissions == 0L)
      return this;
    setupValues();
    allowed |= permissions;
    denied &= (permissions ^ 0xFFFFFFFFFFFFFFFF);
    set |= 0x3;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public PermOverrideManagerImpl deny(long permissions)
  {
    if (permissions == 0L)
      return this;
    setupValues();
    denied |= permissions;
    allowed &= (permissions ^ 0xFFFFFFFFFFFFFFFF);
    set |= 0x3;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public PermOverrideManagerImpl clear(long permissions)
  {
    setupValues();
    if ((allowed & permissions) != 0L)
    {
      allowed &= (permissions ^ 0xFFFFFFFFFFFFFFFF);
      set |= 0x2;
    }
    
    if ((denied & permissions) != 0L)
    {
      denied &= (permissions ^ 0xFFFFFFFFFFFFFFFF);
      set |= 1L;
    }
    
    return this;
  }
  

  protected RequestBody finalizeData()
  {
    String targetId = override.getId();
    
    setupValues();
    RequestBody data = getRequestBody(
      DataObject.empty()
      .put("id", targetId)
      .put("type", role ? "role" : "member")
      .put("allow", Long.valueOf(allowed))
      .put("deny", Long.valueOf(denied)));
    reset();
    return data;
  }
  

  protected boolean checkPermissions()
  {
    Member selfMember = getGuild().getSelfMember();
    GuildChannel channel = getChannel();
    if (!selfMember.hasPermission(channel, new Permission[] { Permission.VIEW_CHANNEL }))
      throw new MissingAccessException(channel, Permission.VIEW_CHANNEL);
    if (!selfMember.hasAccess(channel))
      throw new MissingAccessException(channel, Permission.VOICE_CONNECT);
    if (!selfMember.hasPermission(channel, new Permission[] { Permission.MANAGE_PERMISSIONS }))
      throw new InsufficientPermissionException(channel, Permission.MANAGE_PERMISSIONS);
    return super.checkPermissions();
  }
}
