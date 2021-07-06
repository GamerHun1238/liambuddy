package net.dv8tion.jda.internal.requests.restaction.interactions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction.ResponseType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.InteractionHookImpl;
import net.dv8tion.jda.internal.utils.AllowedMentionsImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;













public class ReplyActionImpl
  extends InteractionCallbackActionImpl
  implements ReplyAction
{
  private final List<MessageEmbed> embeds = new ArrayList();
  private final AllowedMentionsImpl allowedMentions = new AllowedMentionsImpl();
  private final List<ActionRow> components = new ArrayList();
  private String content = "";
  
  private int flags;
  private boolean tts;
  
  public ReplyActionImpl(InteractionHookImpl hook)
  {
    super(hook);
  }
  
  public ReplyActionImpl applyMessage(Message message)
  {
    content = message.getContentRaw();
    tts = message.isTTS();
    embeds.addAll(message.getEmbeds());
    components.addAll(message.getActionRows());
    allowedMentions.applyMessage(message);
    return this;
  }
  
  protected DataObject toData()
  {
    DataObject json = DataObject.empty();
    if (isEmpty())
    {
      json.put("type", Integer.valueOf(InteractionCallbackAction.ResponseType.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE.getRaw()));
      if (flags != 0) {
        json.put("data", DataObject.empty().put("flags", Integer.valueOf(flags)));
      }
    }
    else {
      DataObject payload = DataObject.empty();
      payload.put("allowed_mentions", allowedMentions);
      payload.put("content", content);
      payload.put("tts", Boolean.valueOf(tts));
      payload.put("flags", Integer.valueOf(flags));
      if (!embeds.isEmpty())
        payload.put("embeds", DataArray.fromCollection(embeds));
      if (!components.isEmpty())
        payload.put("components", DataArray.fromCollection(components));
      json.put("data", payload);
      
      json.put("type", Integer.valueOf(InteractionCallbackAction.ResponseType.CHANNEL_MESSAGE_WITH_SOURCE.getRaw()));
    }
    return json;
  }
  
  private boolean isEmpty()
  {
    return (Helpers.isEmpty(content)) && (embeds.isEmpty()) && (files.isEmpty()) && (components.isEmpty());
  }
  

  @Nonnull
  public ReplyActionImpl setEphemeral(boolean ephemeral)
  {
    if (ephemeral) {
      flags |= 0x40;
    } else
      flags &= 0xFFFFFFBF;
    return this;
  }
  

  @Nonnull
  public ReplyAction addFile(@Nonnull InputStream data, @Nonnull String name, @Nonnull AttachmentOption... options)
  {
    Checks.notNull(data, "Data");
    Checks.notEmpty(name, "Name");
    Checks.noneNull(options, "Options");
    if (options.length > 0) {
      name = "SPOILER_" + name;
    }
    files.put(name, data);
    return this;
  }
  

  @Nonnull
  public ReplyAction addEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds)
  {
    Checks.noneNull(embeds, "MessageEmbed");
    for (MessageEmbed embed : embeds)
    {
      Checks.check(embed.isSendable(), "Provided Message contains an empty embed or an embed with a length greater than %d characters, which is the max for bot accounts!", 
      
        Integer.valueOf(6000));
    }
    
    if (embeds.size() + this.embeds.size() > 10)
      throw new IllegalStateException("Cannot have more than 10 embeds per message!");
    this.embeds.addAll(embeds);
    return this;
  }
  

  @Nonnull
  public ReplyAction addActionRows(@Nonnull ActionRow... rows)
  {
    Checks.noneNull(rows, "ActionRows");
    Checks.check(components.size() + rows.length <= 5, "Can only have 5 action rows per message!");
    Collections.addAll(components, rows);
    return this;
  }
  

  @Nonnull
  public ReplyAction setCheck(BooleanSupplier checks)
  {
    return (ReplyAction)super.setCheck(checks);
  }
  

  @Nonnull
  public ReplyAction timeout(long timeout, @Nonnull TimeUnit unit)
  {
    return (ReplyAction)super.timeout(timeout, unit);
  }
  

  @Nonnull
  public ReplyAction deadline(long timestamp)
  {
    return (ReplyAction)super.deadline(timestamp);
  }
  

  @Nonnull
  public ReplyActionImpl setTTS(boolean isTTS)
  {
    tts = isTTS;
    return this;
  }
  

  @Nonnull
  public ReplyActionImpl setContent(String content)
  {
    if (content != null)
      Checks.notLonger(content, 2000, "Content");
    this.content = (content == null ? "" : content);
    return this;
  }
  


  @Nonnull
  public ReplyAction mentionRepliedUser(boolean mention)
  {
    allowedMentions.mentionRepliedUser(mention);
    return this;
  }
  


  @Nonnull
  public ReplyAction allowedMentions(@Nullable Collection<Message.MentionType> allowedMentions)
  {
    this.allowedMentions.allowedMentions(allowedMentions);
    return this;
  }
  


  @Nonnull
  public ReplyAction mention(@Nonnull IMentionable... mentions)
  {
    allowedMentions.mention(mentions);
    return this;
  }
  


  @Nonnull
  public ReplyAction mentionUsers(@Nonnull String... userIds)
  {
    allowedMentions.mentionUsers(userIds);
    return this;
  }
  


  @Nonnull
  public ReplyAction mentionRoles(@Nonnull String... roleIds)
  {
    allowedMentions.mentionRoles(roleIds);
    return this;
  }
}
