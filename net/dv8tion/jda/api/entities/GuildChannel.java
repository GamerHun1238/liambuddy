package net.dv8tion.jda.api.entities;

import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;































































































































































































public abstract interface GuildChannel
  extends AbstractChannel, IMentionable, Comparable<GuildChannel>
{
  @Nonnull
  public abstract Guild getGuild();
  
  @Nullable
  public abstract Category getParent();
  
  @Nonnull
  public abstract List<Member> getMembers();
  
  public abstract int getPosition();
  
  public abstract int getPositionRaw();
  
  @Nullable
  public abstract PermissionOverride getPermissionOverride(@Nonnull IPermissionHolder paramIPermissionHolder);
  
  @Nonnull
  public abstract List<PermissionOverride> getPermissionOverrides();
  
  @Nonnull
  public abstract List<PermissionOverride> getMemberPermissionOverrides();
  
  @Nonnull
  public abstract List<PermissionOverride> getRolePermissionOverrides();
  
  public abstract boolean isSynced();
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<? extends GuildChannel> createCopy(@Nonnull Guild paramGuild);
  
  @Nonnull
  @CheckReturnValue
  public ChannelAction<? extends GuildChannel> createCopy()
  {
    return createCopy(getGuild());
  }
  




















  @Nonnull
  public abstract ChannelManager getManager();
  



















  @Nonnull
  @CheckReturnValue
  public abstract AuditableRestAction<Void> delete();
  



















  @Nonnull
  @CheckReturnValue
  public abstract PermissionOverrideAction createPermissionOverride(@Nonnull IPermissionHolder paramIPermissionHolder);
  



















  @Nonnull
  @CheckReturnValue
  public abstract PermissionOverrideAction putPermissionOverride(@Nonnull IPermissionHolder paramIPermissionHolder);
  



















  @Nonnull
  @CheckReturnValue
  public PermissionOverrideAction upsertPermissionOverride(@Nonnull IPermissionHolder permissionHolder)
  {
    PermissionOverride override = getPermissionOverride(permissionHolder);
    if (override != null)
      return override.getManager();
    return putPermissionOverride(permissionHolder);
  }
  
  @Nonnull
  @CheckReturnValue
  public abstract InviteAction createInvite();
  
  @Nonnull
  @CheckReturnValue
  public abstract RestAction<List<Invite>> retrieveInvites();
}
