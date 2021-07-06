package net.dv8tion.jda.api.events.message.guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;


























public class GuildMessageReceivedEvent
  extends GenericGuildMessageEvent
{
  private final Message message;
  
  public GuildMessageReceivedEvent(@Nonnull JDA api, long responseNumber, @Nonnull Message message)
  {
    super(api, responseNumber, message.getIdLong(), message.getTextChannel());
    this.message = message;
  }
  





  @Nonnull
  public Message getMessage()
  {
    return message;
  }
  









  @Nonnull
  public User getAuthor()
  {
    return message.getAuthor();
  }
  








  @Nullable
  public Member getMember()
  {
    return message.getMember();
  }
  






  public boolean isWebhookMessage()
  {
    return getMessage().isWebhookMessage();
  }
}
