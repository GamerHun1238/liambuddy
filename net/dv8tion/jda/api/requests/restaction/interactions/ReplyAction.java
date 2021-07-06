package net.dv8tion.jda.api.requests.restaction.interactions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.utils.AllowedMentions;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.dv8tion.jda.internal.utils.Checks;































public abstract interface ReplyAction
  extends InteractionCallbackAction, AllowedMentions<ReplyAction>
{
  @Nonnull
  public abstract ReplyAction setCheck(@Nullable BooleanSupplier paramBooleanSupplier);
  
  @Nonnull
  public abstract ReplyAction timeout(long paramLong, @Nonnull TimeUnit paramTimeUnit);
  
  @Nonnull
  public abstract ReplyAction deadline(long paramLong);
  
  @Nonnull
  @CheckReturnValue
  public ReplyAction addEmbeds(@Nonnull MessageEmbed... embeds)
  {
    Checks.noneNull(embeds, "MessageEmbed");
    return addEmbeds(Arrays.asList(embeds));
  }
  












  @Nonnull
  @CheckReturnValue
  public abstract ReplyAction addEmbeds(@Nonnull Collection<? extends MessageEmbed> paramCollection);
  












  @Nonnull
  @CheckReturnValue
  public ReplyAction addActionRow(@Nonnull Component... components)
  {
    return addActionRows(new ActionRow[] { ActionRow.of(components) });
  }
  













  @Nonnull
  @CheckReturnValue
  public ReplyAction addActionRow(@Nonnull Collection<? extends Component> components)
  {
    return addActionRows(new ActionRow[] { ActionRow.of(components) });
  }
  











  @Nonnull
  @CheckReturnValue
  public ReplyAction addActionRows(@Nonnull Collection<? extends ActionRow> rows)
  {
    Checks.noneNull(rows, "ActionRows");
    return addActionRows((ActionRow[])rows.toArray(new ActionRow[0]));
  }
  













  @Nonnull
  @CheckReturnValue
  public abstract ReplyAction addActionRows(@Nonnull ActionRow... paramVarArgs);
  













  @Nonnull
  public abstract ReplyAction setContent(@Nullable String paramString);
  













  @Nonnull
  public abstract ReplyAction setTTS(boolean paramBoolean);
  













  @Nonnull
  @CheckReturnValue
  public abstract ReplyAction setEphemeral(boolean paramBoolean);
  












  @Nonnull
  @CheckReturnValue
  public ReplyAction addFile(@Nonnull File file, @Nonnull AttachmentOption... options)
  {
    Checks.notNull(file, "File");
    return addFile(file, file.getName(), options);
  }
  





























  @Nonnull
  @CheckReturnValue
  public ReplyAction addFile(@Nonnull File file, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    try
    {
      Checks.notNull(file, "File");
      Checks.check((file.exists()) && (file.canRead()), "Provided file either does not exist or cannot be read from!");
      return addFile(new FileInputStream(file), name, options);
    }
    catch (FileNotFoundException e)
    {
      throw new IllegalArgumentException(e);
    }
  }
  


















  @Nonnull
  @CheckReturnValue
  public ReplyAction addFile(@Nonnull byte[] data, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    Checks.notNull(data, "Data");
    return addFile(new ByteArrayInputStream(data), name, options);
  }
  
  @Nonnull
  @CheckReturnValue
  public abstract ReplyAction addFile(@Nonnull InputStream paramInputStream, @Nonnull String paramString, @Nonnull AttachmentOption... paramVarArgs);
}
