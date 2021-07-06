package net.dv8tion.jda.internal.requests.restaction.pagination;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.CompiledRoute;
import net.dv8tion.jda.internal.requests.Route.Messages;
import net.dv8tion.jda.internal.utils.EncodingUtil;
import org.slf4j.Logger;



















public class ReactionPaginationActionImpl
  extends PaginationActionImpl<User, ReactionPaginationAction>
  implements ReactionPaginationAction
{
  protected final MessageReaction reaction;
  
  public ReactionPaginationActionImpl(MessageReaction reaction)
  {
    super(reaction.getJDA(), Route.Messages.GET_REACTION_USERS.compile(new String[] { reaction.getChannel().getId(), reaction.getMessageId(), getCode(reaction) }), 1, 100, 100);
    this.reaction = reaction;
  }
  
  public ReactionPaginationActionImpl(Message message, String code)
  {
    super(message.getJDA(), Route.Messages.GET_REACTION_USERS.compile(new String[] { message.getChannel().getId(), message.getId(), code }), 1, 100, 100);
    reaction = null;
  }
  
  public ReactionPaginationActionImpl(MessageChannel channel, String messageId, String code)
  {
    super(channel.getJDA(), Route.Messages.GET_REACTION_USERS.compile(new String[] { channel.getId(), messageId, code }), 1, 100, 100);
    reaction = null;
  }
  
  protected static String getCode(MessageReaction reaction)
  {
    MessageReaction.ReactionEmote emote = reaction.getReactionEmote();
    
    return emote.isEmote() ? 
      emote.getName() + ":" + emote.getId() : 
      EncodingUtil.encodeUTF8(emote.getName());
  }
  

  @Nonnull
  public MessageReaction getReaction()
  {
    if (reaction == null)
      throw new IllegalStateException("Cannot get reaction for this action");
    return reaction;
  }
  

  protected Route.CompiledRoute finalizeRoute()
  {
    Route.CompiledRoute route = super.finalizeRoute();
    
    String after = null;
    String limit = String.valueOf(getLimit());
    long last = lastKey;
    if (last != 0L) {
      after = Long.toUnsignedString(last);
    }
    route = route.withQueryParams(new String[] { "limit", limit });
    
    if (after != null) {
      route = route.withQueryParams(new String[] { "after", after });
    }
    return route;
  }
  

  protected void handleSuccess(Response response, Request<List<User>> request)
  {
    EntityBuilder builder = api.getEntityBuilder();
    DataArray array = response.getArray();
    List<User> users = new LinkedList();
    for (int i = 0; i < array.length(); i++)
    {
      try
      {
        User user = builder.createUser(array.getObject(i));
        users.add(user);
        if (useCache)
          cached.add(user);
        last = user;
        lastKey = ((User)last).getIdLong();
      }
      catch (ParsingException|NullPointerException e)
      {
        LOG.warn("Encountered exception in ReactionPagination", e);
      }
    }
    
    request.onSuccess(users);
  }
  

  protected long getKey(User it)
  {
    return it.getIdLong();
  }
}
