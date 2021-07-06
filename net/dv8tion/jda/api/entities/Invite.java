package net.dv8tion.jda.api.entities;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.entities.InviteImpl;


















































public abstract interface Invite
{
  @Nonnull
  public static RestAction<Invite> resolve(@Nonnull JDA api, @Nonnull String code)
  {
    return resolve(api, code, false);
  }
  




















  @Nonnull
  public static RestAction<Invite> resolve(@Nonnull JDA api, @Nonnull String code, boolean withCounts)
  {
    return InviteImpl.resolve(api, code, withCounts);
  }
  









  @Nonnull
  @CheckReturnValue
  public abstract AuditableRestAction<Void> delete();
  









  @Nonnull
  @CheckReturnValue
  public abstract RestAction<Invite> expand();
  









  @Nonnull
  public abstract InviteType getType();
  









  @Nullable
  public abstract Channel getChannel();
  









  @Nonnull
  public abstract String getCode();
  









  @Nullable
  public abstract Group getGroup();
  








  @Nonnull
  public String getUrl()
  {
    return "https://discord.gg/" + getCode();
  }
  


























  @Nonnull
  @Deprecated
  @DeprecatedSince("4.0.0")
  @ReplaceWith("getTimeCreated()")
  public abstract OffsetDateTime getCreationTime();
  


























  @Nullable
  public abstract Guild getGuild();
  


























  @Nullable
  public abstract User getInviter();
  


























  @Nonnull
  public abstract JDA getJDA();
  


























  public abstract int getMaxAge();
  


























  public abstract int getMaxUses();
  


























  @Nonnull
  public abstract OffsetDateTime getTimeCreated();
  


























  public abstract int getUses();
  


























  public abstract boolean isExpanded();
  


























  public abstract boolean isTemporary();
  

























  public static enum InviteType
  {
    GUILD, 
    GROUP, 
    UNKNOWN;
    
    private InviteType() {}
  }
  
  public static abstract interface Group
    extends ISnowflake
  {
    @Nullable
    public abstract String getIconId();
    
    @Nullable
    public abstract String getIconUrl();
    
    @Nullable
    public abstract String getName();
    
    @Nullable
    public abstract List<String> getUsers();
  }
  
  public static abstract interface Guild
    extends ISnowflake
  {
    @Nullable
    public abstract String getIconId();
    
    @Nullable
    public abstract String getIconUrl();
    
    @Nonnull
    public abstract String getName();
    
    @Nullable
    public abstract String getSplashId();
    
    @Nullable
    public abstract String getSplashUrl();
    
    @Nonnull
    public abstract Guild.VerificationLevel getVerificationLevel();
    
    public abstract int getOnlineCount();
    
    public abstract int getMemberCount();
    
    @Nonnull
    public abstract Set<String> getFeatures();
  }
  
  public static abstract interface Channel
    extends ISnowflake
  {
    @Nonnull
    public abstract String getName();
    
    @Nonnull
    public abstract ChannelType getType();
  }
}
