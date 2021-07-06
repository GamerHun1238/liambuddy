package net.dv8tion.jda.internal.requests.restaction.pagination;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.CompiledRoute;
import net.dv8tion.jda.internal.requests.Route.Guilds;
import net.dv8tion.jda.internal.utils.Checks;
import org.slf4j.Logger;













public class AuditLogPaginationActionImpl
  extends PaginationActionImpl<AuditLogEntry, AuditLogPaginationAction>
  implements AuditLogPaginationAction
{
  protected final Guild guild;
  protected ActionType type = null;
  protected String userId = null;
  
  public AuditLogPaginationActionImpl(Guild guild)
  {
    super(guild.getJDA(), Route.Guilds.GET_AUDIT_LOGS.compile(new String[] { guild.getId() }), 1, 100, 100);
    if (!guild.getSelfMember().hasPermission(new Permission[] { Permission.VIEW_AUDIT_LOGS }))
      throw new InsufficientPermissionException(guild, Permission.VIEW_AUDIT_LOGS);
    this.guild = guild;
  }
  

  @Nonnull
  public AuditLogPaginationActionImpl type(ActionType type)
  {
    this.type = type;
    return this;
  }
  

  @Nonnull
  public AuditLogPaginationActionImpl user(User user)
  {
    return user(user == null ? null : user.getId());
  }
  

  @Nonnull
  public AuditLogPaginationActionImpl user(String userId)
  {
    if (userId != null)
      Checks.isSnowflake(userId, "User ID");
    this.userId = userId;
    return this;
  }
  

  @Nonnull
  public AuditLogPaginationActionImpl user(long userId)
  {
    return user(Long.toUnsignedString(userId));
  }
  

  @Nonnull
  public Guild getGuild()
  {
    return guild;
  }
  

  protected Route.CompiledRoute finalizeRoute()
  {
    Route.CompiledRoute route = super.finalizeRoute();
    
    String limit = String.valueOf(this.limit.get());
    long last = lastKey;
    
    route = route.withQueryParams(new String[] { "limit", limit });
    
    if (type != null) {
      route = route.withQueryParams(new String[] { "action_type", String.valueOf(type.getKey()) });
    }
    if (userId != null) {
      route = route.withQueryParams(new String[] { "user_id", userId });
    }
    if (last != 0L) {
      route = route.withQueryParams(new String[] { "before", Long.toUnsignedString(last) });
    }
    return route;
  }
  

  protected void handleSuccess(Response response, Request<List<AuditLogEntry>> request)
  {
    DataObject obj = response.getObject();
    DataArray users = obj.getArray("users");
    DataArray webhooks = obj.getArray("webhooks");
    DataArray entries = obj.getArray("audit_log_entries");
    
    List<AuditLogEntry> list = new ArrayList(entries.length());
    EntityBuilder builder = api.getEntityBuilder();
    
    TLongObjectMap<DataObject> userMap = new TLongObjectHashMap();
    for (int i = 0; i < users.length(); i++)
    {
      DataObject user = users.getObject(i);
      userMap.put(user.getLong("id"), user);
    }
    
    TLongObjectMap<DataObject> webhookMap = new TLongObjectHashMap();
    for (int i = 0; i < webhooks.length(); i++)
    {
      DataObject webhook = webhooks.getObject(i);
      webhookMap.put(webhook.getLong("id"), webhook);
    }
    
    for (int i = 0; i < entries.length(); i++)
    {
      try
      {
        DataObject entry = entries.getObject(i);
        DataObject user = (DataObject)userMap.get(entry.getLong("user_id", 0L));
        DataObject webhook = (DataObject)webhookMap.get(entry.getLong("target_id", 0L));
        AuditLogEntry result = builder.createAuditLogEntry((GuildImpl)guild, entry, user, webhook);
        list.add(result);
        if (useCache)
          cached.add(result);
        last = result;
        lastKey = ((AuditLogEntry)last).getIdLong();
      }
      catch (ParsingException|NullPointerException e)
      {
        LOG.warn("Encountered exception in AuditLogPagination", e);
      }
    }
    
    request.onSuccess(list);
  }
  

  protected long getKey(AuditLogEntry it)
  {
    return it.getIdLong();
  }
}
