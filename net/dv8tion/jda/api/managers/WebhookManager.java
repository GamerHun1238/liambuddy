package net.dv8tion.jda.api.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;





















































































public abstract interface WebhookManager
  extends Manager<WebhookManager>
{
  public static final long NAME = 1L;
  public static final long CHANNEL = 2L;
  public static final long AVATAR = 4L;
  
  @Nonnull
  public abstract WebhookManager reset(long paramLong);
  
  @Nonnull
  public abstract WebhookManager reset(long... paramVarArgs);
  
  @Nonnull
  public abstract Webhook getWebhook();
  
  @Nonnull
  public TextChannel getChannel()
  {
    return getWebhook().getChannel();
  }
  







  @Nonnull
  public Guild getGuild()
  {
    return getWebhook().getGuild();
  }
  
  @Nonnull
  @CheckReturnValue
  public abstract WebhookManager setName(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract WebhookManager setAvatar(@Nullable Icon paramIcon);
  
  @Nonnull
  @CheckReturnValue
  public abstract WebhookManager setChannel(@Nonnull TextChannel paramTextChannel);
}
