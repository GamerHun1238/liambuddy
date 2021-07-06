package net.dv8tion.jda.api.managers;

import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IPermissionHolder;







































































































public abstract interface ChannelManager
  extends Manager<ChannelManager>
{
  public static final long NAME = 1L;
  public static final long PARENT = 2L;
  public static final long TOPIC = 4L;
  public static final long POSITION = 8L;
  public static final long NSFW = 16L;
  public static final long USERLIMIT = 32L;
  public static final long BITRATE = 64L;
  public static final long PERMISSION = 128L;
  public static final long SLOWMODE = 256L;
  public static final long NEWS = 512L;
  public static final long REGION = 1024L;
  
  @Nonnull
  public abstract ChannelManager reset(long paramLong);
  
  @Nonnull
  public abstract ChannelManager reset(long... paramVarArgs);
  
  @Nonnull
  public abstract GuildChannel getChannel();
  
  @Nonnull
  public ChannelType getType()
  {
    return getChannel().getType();
  }
  







  @Nonnull
  public Guild getGuild()
  {
    return getChannel().getGuild();
  }
  















  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager clearOverridesAdded();
  














  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager clearOverridesRemoved();
  














  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager putPermissionOverride(@Nonnull IPermissionHolder paramIPermissionHolder, long paramLong1, long paramLong2);
  














  @Nonnull
  @CheckReturnValue
  public ChannelManager putPermissionOverride(@Nonnull IPermissionHolder permHolder, @Nullable Collection<Permission> allow, @Nullable Collection<Permission> deny)
  {
    long allowRaw = allow == null ? 0L : Permission.getRaw(allow);
    long denyRaw = deny == null ? 0L : Permission.getRaw(deny);
    return putPermissionOverride(permHolder, allowRaw, denyRaw);
  }
  


















  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager removePermissionOverride(@Nonnull IPermissionHolder paramIPermissionHolder);
  


















  @Nonnull
  @CheckReturnValue
  public ChannelManager sync()
  {
    if (getChannel().getParent() == null)
      throw new IllegalStateException("sync() requires a parent category");
    return sync(getChannel().getParent());
  }
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager sync(@Nonnull GuildChannel paramGuildChannel);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager setName(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager setParent(@Nullable Category paramCategory);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager setPosition(int paramInt);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager setTopic(@Nullable String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager setNSFW(boolean paramBoolean);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager setSlowmode(int paramInt);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager setUserLimit(int paramInt);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager setBitrate(int paramInt);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager setRegion(Region paramRegion);
  
  @Nonnull
  @CheckReturnValue
  public abstract ChannelManager setNews(boolean paramBoolean);
}
