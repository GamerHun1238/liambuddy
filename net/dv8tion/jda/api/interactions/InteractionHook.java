package net.dv8tion.jda.api.interactions;

import java.io.File;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.WebhookClient;
import net.dv8tion.jda.api.interactions.components.ComponentLayout;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.dv8tion.jda.internal.utils.Checks;




















































public abstract interface InteractionHook
  extends WebhookClient<Message>
{
  @Nonnull
  public abstract Interaction getInteraction();
  
  public long getExpirationTimestamp()
  {
    return getInteraction().getTimeCreated().plus(15L, ChronoUnit.MINUTES).toEpochSecond() * 1000L;
  }
  








  public boolean isExpired()
  {
    return System.currentTimeMillis() > getExpirationTimestamp();
  }
  














  @Nonnull
  public abstract InteractionHook setEphemeral(boolean paramBoolean);
  













  @Nonnull
  public abstract JDA getJDA();
  













  @Nonnull
  @CheckReturnValue
  public abstract RestAction<Message> retrieveOriginal();
  













  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<Message> editOriginal(@Nonnull String content)
  {
    return editMessageById("@original", content);
  }
  























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<Message> editOriginalComponents(@Nonnull Collection<? extends ComponentLayout> components)
  {
    return editMessageComponentsById("@original", components);
  }
  























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<Message> editOriginalComponents(@Nonnull ComponentLayout... components)
  {
    return editMessageComponentsById("@original", components);
  }
  























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<Message> editOriginalEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds)
  {
    return editMessageEmbedsById("@original", embeds);
  }
  























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<Message> editOriginalEmbeds(@Nonnull MessageEmbed... embeds)
  {
    return editMessageEmbedsById("@original", embeds);
  }
  























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<Message> editOriginal(@Nonnull Message message)
  {
    return editMessageById("@original", message);
  }
  

























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<Message> editOriginalFormat(@Nonnull String format, @Nonnull Object... args)
  {
    Checks.notNull(format, "Format String");
    return editOriginal(String.format(format, args));
  }
  











































  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<Message> editOriginal(@Nonnull InputStream data, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    return editMessageById("@original", data, name, options);
  }
  










































  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<Message> editOriginal(@Nonnull File file, @Nonnull AttachmentOption... options)
  {
    return editMessageById("@original", file, options);
  }
  










































  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<Message> editOriginal(@Nonnull File file, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    return editMessageById("@original", file, name, options);
  }
  










































  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<Message> editOriginal(@Nonnull byte[] data, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    return editMessageById("@original", data, name, options);
  }
  






  @Nonnull
  @CheckReturnValue
  public RestAction<Void> deleteOriginal()
  {
    return deleteMessageById("@original");
  }
}
