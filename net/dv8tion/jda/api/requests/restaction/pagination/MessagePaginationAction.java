package net.dv8tion.jda.api.requests.restaction.pagination;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;



























































public abstract interface MessagePaginationAction
  extends PaginationAction<Message, MessagePaginationAction>
{
  @Nonnull
  public ChannelType getType()
  {
    return getChannel().getType();
  }
  
  @Nonnull
  public abstract MessageChannel getChannel();
}
