package net.dv8tion.jda.internal.requests.restaction.pagination;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.MissingAccessException;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.CompiledRoute;
import net.dv8tion.jda.internal.requests.Route.Messages;
import org.slf4j.Logger;









public class MessagePaginationActionImpl
  extends PaginationActionImpl<Message, MessagePaginationAction>
  implements MessagePaginationAction
{
  private final MessageChannel channel;
  
  public MessagePaginationActionImpl(MessageChannel channel)
  {
    super(channel.getJDA(), Route.Messages.GET_MESSAGE_HISTORY.compile(new String[] { channel.getId() }), 1, 100, 100);
    
    if (channel.getType() == ChannelType.TEXT)
    {
      TextChannel textChannel = (TextChannel)channel;
      Member selfMember = textChannel.getGuild().getSelfMember();
      if (!selfMember.hasAccess(textChannel))
        throw new MissingAccessException(textChannel, Permission.VIEW_CHANNEL);
      if (!selfMember.hasPermission(textChannel, new Permission[] { Permission.MESSAGE_HISTORY })) {
        throw new InsufficientPermissionException(textChannel, Permission.MESSAGE_HISTORY);
      }
    }
    this.channel = channel;
  }
  

  @Nonnull
  public MessageChannel getChannel()
  {
    return channel;
  }
  

  protected Route.CompiledRoute finalizeRoute()
  {
    Route.CompiledRoute route = super.finalizeRoute();
    
    String limit = String.valueOf(getLimit());
    long last = lastKey;
    
    route = route.withQueryParams(new String[] { "limit", limit });
    
    if (last != 0L) {
      route = route.withQueryParams(new String[] { "before", Long.toUnsignedString(last) });
    }
    return route;
  }
  

  protected void handleSuccess(Response response, Request<List<Message>> request)
  {
    DataArray array = response.getArray();
    List<Message> messages = new ArrayList(array.length());
    EntityBuilder builder = api.getEntityBuilder();
    for (int i = 0; i < array.length(); i++)
    {
      try
      {
        Message msg = builder.createMessage(array.getObject(i), channel, false);
        messages.add(msg);
        if (useCache)
          cached.add(msg);
        last = msg;
        lastKey = ((Message)last).getIdLong();
      }
      catch (ParsingException|NullPointerException e)
      {
        LOG.warn("Encountered an exception in MessagePagination", e);
      }
      catch (IllegalArgumentException e)
      {
        if ("UNKNOWN_MESSAGE_TYPE".equals(e.getMessage())) {
          LOG.warn("Skipping unknown message type during pagination", e);
        } else {
          LOG.warn("Unexpected issue trying to parse message during pagination", e);
        }
      }
    }
    request.onSuccess(messages);
  }
  

  protected long getKey(Message it)
  {
    return it.getIdLong();
  }
}
