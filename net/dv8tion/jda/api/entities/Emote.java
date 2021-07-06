package net.dv8tion.jda.api.entities;

import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.DeprecatedSince;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.managers.EmoteManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.utils.PermissionUtil;




































































public abstract interface Emote
  extends IMentionable
{
  public static final String ICON_URL = "https://cdn.discordapp.com/emojis/%s.%s";
  
  @Nullable
  public abstract Guild getGuild();
  
  @Nonnull
  public abstract List<Role> getRoles();
  
  @Deprecated
  @DeprecatedSince("3.8.0")
  @ReplaceWith("canProvideRoles()")
  public boolean hasRoles()
  {
    return canProvideRoles();
  }
  











  public abstract boolean canProvideRoles();
  











  @Nonnull
  public abstract String getName();
  











  public abstract boolean isManaged();
  










  public abstract boolean isAvailable();
  










  @Nonnull
  public abstract JDA getJDA();
  










  @Nonnull
  @CheckReturnValue
  public abstract AuditableRestAction<Void> delete();
  










  @Nonnull
  public abstract EmoteManager getManager();
  










  public abstract boolean isAnimated();
  










  @Nonnull
  public String getImageUrl()
  {
    return String.format("https://cdn.discordapp.com/emojis/%s.%s", new Object[] { getId(), isAnimated() ? "gif" : "png" });
  }
  









  @Nonnull
  public String getAsMention()
  {
    return (isAnimated() ? "<a:" : "<:") + getName() + ":" + getId() + ">";
  }
  








  public boolean canInteract(Member issuer)
  {
    return PermissionUtil.canInteract(issuer, this);
  }
  











  public boolean canInteract(User issuer, MessageChannel channel)
  {
    return PermissionUtil.canInteract(issuer, this, channel);
  }
  













  public boolean canInteract(User issuer, MessageChannel channel, boolean botOverride)
  {
    return PermissionUtil.canInteract(issuer, this, channel, botOverride);
  }
}
