package net.dv8tion.jda.api.requests.restaction;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.AllowedMentions;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.dv8tion.jda.internal.utils.Checks;
























































































































public abstract interface WebhookMessageAction<T>
  extends RestAction<T>, AllowedMentions<WebhookMessageAction<T>>
{
  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageAction<T> setEphemeral(boolean paramBoolean);
  
  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageAction<T> setContent(@Nullable String paramString);
  
  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageAction<T> setTTS(boolean paramBoolean);
  
  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageAction<T> addEmbeds(@Nonnull Collection<? extends MessageEmbed> paramCollection);
  
  @Nonnull
  @CheckReturnValue
  public WebhookMessageAction<T> addEmbeds(@Nonnull MessageEmbed embed, @Nonnull MessageEmbed... other)
  {
    ArrayList<MessageEmbed> embeds = new ArrayList();
    embeds.add(embed);
    Collections.addAll(embeds, other);
    return addEmbeds(embeds);
  }
  
























  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageAction<T> addFile(@Nonnull InputStream paramInputStream, @Nonnull String paramString, @Nonnull AttachmentOption... paramVarArgs);
  
























  @Nonnull
  @CheckReturnValue
  public WebhookMessageAction<T> addFile(@Nonnull byte[] data, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    Checks.notNull(name, "Name");
    Checks.notNull(data, "Data");
    return addFile(new ByteArrayInputStream(data), name, options);
  }
  




























  @Nonnull
  @CheckReturnValue
  public WebhookMessageAction<T> addFile(@Nonnull File file, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    Checks.notEmpty(name, "Name");
    Checks.notNull(file, "File");
    try
    {
      return addFile(new FileInputStream(file), name, options);
    }
    catch (FileNotFoundException e)
    {
      throw new IllegalArgumentException(e);
    }
  }
  





















  @Nonnull
  @CheckReturnValue
  public WebhookMessageAction<T> addFile(@Nonnull File file, @Nonnull AttachmentOption... options)
  {
    Checks.notNull(file, "File");
    return addFile(file, file.getName(), options);
  }
  











  @Nonnull
  @CheckReturnValue
  public WebhookMessageAction<T> addActionRow(@Nonnull Component... components)
  {
    return addActionRows(new ActionRow[] { ActionRow.of(components) });
  }
  











  @Nonnull
  @CheckReturnValue
  public WebhookMessageAction<T> addActionRow(@Nonnull Collection<? extends Component> components)
  {
    return addActionRows(new ActionRow[] { ActionRow.of(components) });
  }
  











  @Nonnull
  @CheckReturnValue
  public WebhookMessageAction<T> addActionRows(@Nonnull Collection<? extends ActionRow> rows)
  {
    Checks.noneNull(rows, "ActionRows");
    return addActionRows((ActionRow[])rows.toArray(new ActionRow[0]));
  }
  
  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageAction<T> addActionRows(@Nonnull ActionRow... paramVarArgs);
  
  @Nonnull
  @CheckReturnValue
  public abstract WebhookMessageAction<T> applyMessage(@Nonnull Message paramMessage);
}
