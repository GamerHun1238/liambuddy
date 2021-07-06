package net.dv8tion.jda.api.requests.restaction;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.utils.Checks;





















































































































































































public abstract interface ChannelAction<T extends GuildChannel>
  extends AuditableRestAction<T>
{
  @Nonnull
  public abstract ChannelAction<T> setCheck(@Nullable BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract ChannelAction<T> timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  public abstract ChannelAction<T> deadline(long paramLong);
  
  @Nonnull
  public abstract Guild getGuild();
  
  @Nonnull
  public abstract ChannelType getType();
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<T> setName(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<T> setParent(@Nullable Category paramCategory);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<T> setPosition(@Nullable Integer paramInteger);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<T> setTopic(@Nullable String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<T> setNSFW(boolean paramBoolean);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<T> setSlowmode(int paramInt);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<T> setNews(boolean paramBoolean);
  
  @Nonnull
  @CheckReturnValue
  public ChannelAction<T> addPermissionOverride(@Nonnull IPermissionHolder target, @Nullable Collection<Permission> allow, @Nullable Collection<Permission> deny)
  {
    long allowRaw = allow != null ? Permission.getRaw(allow) : 0L;
    long denyRaw = deny != null ? Permission.getRaw(deny) : 0L;
    
    return addPermissionOverride(target, allowRaw, denyRaw);
  }
  







































  @Nonnull
  @CheckReturnValue
  public ChannelAction<T> addPermissionOverride(@Nonnull IPermissionHolder target, long allow, long deny)
  {
    Checks.notNull(target, "Override Role/Member");
    if ((target instanceof Role))
      return addRolePermissionOverride(target.getIdLong(), allow, deny);
    if ((target instanceof Member))
      return addMemberPermissionOverride(target.getIdLong(), allow, deny);
    throw new IllegalArgumentException("Cannot add override for " + target.getClass().getSimpleName());
  }
  





























  @Nonnull
  @CheckReturnValue
  public ChannelAction<T> addMemberPermissionOverride(long memberId, @Nullable Collection<Permission> allow, @Nullable Collection<Permission> deny)
  {
    long allowRaw = allow != null ? Permission.getRaw(allow) : 0L;
    long denyRaw = deny != null ? Permission.getRaw(deny) : 0L;
    
    return addMemberPermissionOverride(memberId, allowRaw, denyRaw);
  }
  





























  @Nonnull
  @CheckReturnValue
  public ChannelAction<T> addRolePermissionOverride(long roleId, @Nullable Collection<Permission> allow, @Nullable Collection<Permission> deny)
  {
    long allowRaw = allow != null ? Permission.getRaw(allow) : 0L;
    long denyRaw = deny != null ? Permission.getRaw(deny) : 0L;
    
    return addRolePermissionOverride(roleId, allowRaw, denyRaw);
  }
  























  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<T> addMemberPermissionOverride(long paramLong1, long paramLong2, long paramLong3);
  






















  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<T> addRolePermissionOverride(long paramLong1, long paramLong2, long paramLong3);
  






















  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<T> removePermissionOverride(long paramLong);
  






















  @Nonnull
  @CheckReturnValue
  public ChannelAction<T> removePermissionOverride(@Nonnull String id)
  {
    return removePermissionOverride(MiscUtil.parseSnowflake(id));
  }
  












  @Nonnull
  @CheckReturnValue
  public ChannelAction<T> removePermissionOverride(@Nonnull IPermissionHolder holder)
  {
    Checks.notNull(holder, "PermissionHolder");
    return removePermissionOverride(holder.getIdLong());
  }
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<T> clearPermissionOverrides();
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<T> syncPermissionOverrides();
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<T> setBitrate(@Nullable Integer paramInteger);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelAction<T> setUserlimit(@Nullable Integer paramInteger);
}
