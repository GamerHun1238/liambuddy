package net.dv8tion.jda.api.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;




































public abstract interface WebhookAction
  extends AuditableRestAction<Webhook>
{
  @Nonnull
  public abstract WebhookAction setCheck(@Nullable BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract WebhookAction timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  public abstract WebhookAction deadline(long paramLong);
  
  @Nonnull
  public abstract TextChannel getChannel();
  
  @Nonnull
  public Guild getGuild()
  {
    return getChannel().getGuild();
  }
  
  @Nonnull
  @CheckReturnValue
  public abstract WebhookAction setName(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract WebhookAction setAvatar(@Nullable Icon paramIcon);
}
