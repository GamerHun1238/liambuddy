package net.dv8tion.jda.api.entities;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.components.ComponentLayout;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.dv8tion.jda.internal.utils.Checks;














































































public abstract interface WebhookClient<T>
{
  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageAction<T> sendMessage(@Nonnull String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageAction<T> sendMessage(@Nonnull Message paramMessage);
  
  @Nonnull
  @CheckReturnValue
  public WebhookMessageAction<T> sendMessageFormat(@Nonnull String format, @Nonnull Object... args)
  {
    Checks.notNull(format, "Format String");
    return sendMessage(String.format(format, args));
  }
  




















  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageAction<T> sendMessageEmbeds(@Nonnull Collection<? extends MessageEmbed> paramCollection);
  




















  @Nonnull
  @CheckReturnValue
  public WebhookMessageAction<T> sendMessageEmbeds(@Nonnull MessageEmbed embed, @Nonnull MessageEmbed... embeds)
  {
    Checks.notNull(embed, "MessageEmbeds");
    Checks.noneNull(embeds, "MessageEmbeds");
    List<MessageEmbed> embedList = new ArrayList();
    embedList.add(embed);
    Collections.addAll(embedList, embeds);
    return sendMessageEmbeds(embedList);
  }
  







































  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageAction<T> sendFile(@Nonnull InputStream paramInputStream, @Nonnull String paramString, @Nonnull AttachmentOption... paramVarArgs);
  







































  @Nonnull
  @CheckReturnValue
  public WebhookMessageAction<T> sendFile(@Nonnull File file, @Nonnull AttachmentOption... options)
  {
    Checks.notNull(file, "File");
    return sendFile(file, file.getName(), options);
  }
  


















































  @Nonnull
  @CheckReturnValue
  public WebhookMessageAction<T> sendFile(@Nonnull File file, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    Checks.notNull(file, "File");
    Checks.check((file.exists()) && (file.canRead()), "Provided file doesn't exist or cannot be read!");
    
    Checks.notNull(name, "Name");
    
    try
    {
      return sendFile(new FileInputStream(file), name, options);
    }
    catch (FileNotFoundException ex)
    {
      throw new IllegalArgumentException(ex);
    }
  }
  







































  @Nonnull
  @CheckReturnValue
  public WebhookMessageAction<T> sendFile(@Nonnull byte[] data, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    Checks.notNull(data, "Data");
    Checks.notNull(name, "Name");
    return sendFile(new ByteArrayInputStream(data), name, options);
  }
  























  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageUpdateAction<T> editMessageById(@Nonnull String paramString1, @Nonnull String paramString2);
  























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageById(long messageId, @Nonnull String content)
  {
    return editMessageById(Long.toUnsignedString(messageId), content);
  }
  























  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageUpdateAction<T> editMessageById(@Nonnull String paramString, @Nonnull Message paramMessage);
  























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageById(long messageId, Message message)
  {
    return editMessageById(Long.toUnsignedString(messageId), message);
  }
  

























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageFormatById(@Nonnull String messageId, @Nonnull String format, @Nonnull Object... args)
  {
    Checks.notNull(format, "Format String");
    return editMessageById(messageId, String.format(format, args));
  }
  

























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageFormatById(long messageId, @Nonnull String format, @Nonnull Object... args)
  {
    return editMessageFormatById(Long.toUnsignedString(messageId), format, args);
  }
  























  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageUpdateAction<T> editMessageEmbedsById(@Nonnull String paramString, @Nonnull Collection<? extends MessageEmbed> paramCollection);
  























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageEmbedsById(long messageId, @Nonnull Collection<? extends MessageEmbed> embeds)
  {
    return editMessageEmbedsById(Long.toUnsignedString(messageId), embeds);
  }
  























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageEmbedsById(@Nonnull String messageId, @Nonnull MessageEmbed... embeds)
  {
    Checks.noneNull(embeds, "MessageEmbeds");
    return editMessageEmbedsById(messageId, Arrays.asList(embeds));
  }
  























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageEmbedsById(long messageId, @Nonnull MessageEmbed... embeds)
  {
    return editMessageEmbedsById(Long.toUnsignedString(messageId), embeds);
  }
  























  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageUpdateAction<T> editMessageComponentsById(@Nonnull String paramString, @Nonnull Collection<? extends ComponentLayout> paramCollection);
  























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageComponentsById(long messageId, @Nonnull Collection<? extends ComponentLayout> components)
  {
    return editMessageComponentsById(Long.toUnsignedString(messageId), components);
  }
  























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageComponentsById(@Nonnull String messageId, @Nonnull ComponentLayout... components)
  {
    Checks.noneNull(components, "ComponentLayouts");
    return editMessageComponentsById(messageId, Arrays.asList(components));
  }
  























  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageComponentsById(long messageId, @Nonnull ComponentLayout... components)
  {
    return editMessageComponentsById(Long.toUnsignedString(messageId), components);
  }
  











































  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageUpdateAction<T> editMessageById(@Nonnull String paramString1, @Nonnull InputStream paramInputStream, @Nonnull String paramString2, @Nonnull AttachmentOption... paramVarArgs);
  










































  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageById(@Nonnull String messageId, @Nonnull File file, @Nonnull AttachmentOption... options)
  {
    Checks.notNull(file, "File");
    return editMessageById(messageId, file, file.getName(), options);
  }
  










































  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageById(@Nonnull String messageId, @Nonnull File file, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    Checks.notNull(file, "File");
    Checks.check((file.exists()) && (file.canRead()), "Provided file doesn't exist or cannot be read!");
    
    Checks.notNull(name, "Name");
    
    try
    {
      return editMessageById(messageId, new FileInputStream(file), name, options);
    }
    catch (FileNotFoundException ex)
    {
      throw new IllegalArgumentException(ex);
    }
  }
  










































  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageById(@Nonnull String messageId, @Nonnull byte[] data, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    Checks.notNull(data, "Data");
    Checks.notNull(name, "Name");
    
    return editMessageById(messageId, new ByteArrayInputStream(data), name, options);
  }
  










































  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageById(long messageId, @Nonnull InputStream data, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    return editMessageById(Long.toUnsignedString(messageId), data, name, options);
  }
  










































  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageById(long messageId, @Nonnull File file, @Nonnull AttachmentOption... options)
  {
    return editMessageById(Long.toUnsignedString(messageId), file, options);
  }
  










































  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageById(long messageId, @Nonnull File file, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    return editMessageById(Long.toUnsignedString(messageId), file, name, options);
  }
  










































  @Nonnull
  @CheckReturnValue
  public WebhookMessageUpdateAction<T> editMessageById(long messageId, @Nonnull byte[] data, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    return editMessageById(Long.toUnsignedString(messageId), data, name, options);
  }
  




















  @Nonnull
  @CheckReturnValue
  public abstract RestAction<Void> deleteMessageById(@Nonnull String paramString);
  




















  @Nonnull
  @CheckReturnValue
  public RestAction<Void> deleteMessageById(long messageId)
  {
    return deleteMessageById(Long.toUnsignedString(messageId));
  }
}
