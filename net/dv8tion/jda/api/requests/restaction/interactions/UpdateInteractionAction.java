package net.dv8tion.jda.api.requests.restaction.interactions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.dv8tion.jda.internal.utils.Checks;






































public abstract interface UpdateInteractionAction
  extends InteractionCallbackAction
{
  @Nonnull
  public abstract UpdateInteractionAction setContent(@Nullable String paramString);
  
  @Nonnull
  @CheckReturnValue
  public UpdateInteractionAction setEmbeds(@Nonnull MessageEmbed... embeds)
  {
    Checks.noneNull(embeds, "MessageEmbed");
    return setEmbeds(Arrays.asList(embeds));
  }
  











  @Nonnull
  @CheckReturnValue
  public abstract UpdateInteractionAction setEmbeds(@Nonnull Collection<? extends MessageEmbed> paramCollection);
  











  @Nonnull
  @CheckReturnValue
  public UpdateInteractionAction setActionRows(@Nonnull Collection<? extends ActionRow> rows)
  {
    Checks.noneNull(rows, "ActionRows");
    return setActionRows((ActionRow[])rows.toArray(new ActionRow[0]));
  }
  












  @Nonnull
  @CheckReturnValue
  public abstract UpdateInteractionAction setActionRows(@Nonnull ActionRow... paramVarArgs);
  












  @Nonnull
  @CheckReturnValue
  public UpdateInteractionAction setActionRow(@Nonnull Component... components)
  {
    return setActionRows(new ActionRow[] { ActionRow.of(components) });
  }
  













  @Nonnull
  @CheckReturnValue
  public UpdateInteractionAction setActionRow(@Nonnull Collection<? extends Component> components)
  {
    return setActionRows(new ActionRow[] { ActionRow.of(components) });
  }
  















  @Nonnull
  @CheckReturnValue
  public UpdateInteractionAction addFile(@Nonnull File file, @Nonnull AttachmentOption... options)
  {
    Checks.notNull(file, "File");
    return addFile(file, file.getName(), options);
  }
  





























  @Nonnull
  @CheckReturnValue
  public UpdateInteractionAction addFile(@Nonnull File file, @Nonnull String name, @Nonnull AttachmentOption... options)
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
  public UpdateInteractionAction addFile(@Nonnull byte[] data, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    Checks.notNull(data, "Data");
    return addFile(new ByteArrayInputStream(data), name, options);
  }
  
  @Nonnull
  @CheckReturnValue
  public abstract UpdateInteractionAction addFile(@Nonnull InputStream paramInputStream, @Nonnull String paramString, @Nonnull AttachmentOption... paramVarArgs);
}
