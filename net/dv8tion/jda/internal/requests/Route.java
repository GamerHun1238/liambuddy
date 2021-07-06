package net.dv8tion.jda.internal.requests;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
















public class Route
{
  private static final String majorParameters = "guild_id:channel_id:webhook_id:interaction_token";
  private final String route;
  private final Method method;
  private final int paramCount;
  
  public static class Misc
  {
    public static final Route TRACK = new Route(Method.POST, "track", null);
    public static final Route GET_VOICE_REGIONS = new Route(Method.GET, "voice/regions", null);
    public static final Route GATEWAY = new Route(Method.GET, "gateway", null);
    public static final Route GATEWAY_BOT = new Route(Method.GET, "gateway/bot", null);
    
    public Misc() {}
  }
  
  public static class Applications {
    public static final Route GET_BOT_APPLICATION = new Route(Method.GET, "oauth2/applications/@me", null);
    

    public static final Route GET_APPLICATIONS = new Route(Method.GET, "oauth2/applications", null);
    public static final Route CREATE_APPLICATION = new Route(Method.POST, "oauth2/applications", null);
    public static final Route GET_APPLICATION = new Route(Method.GET, "oauth2/applications/{application_id}", null);
    public static final Route MODIFY_APPLICATION = new Route(Method.PUT, "oauth2/applications/{application_id}", null);
    public static final Route DELETE_APPLICATION = new Route(Method.DELETE, "oauth2/applications/{application_id}", null);
    
    public static final Route CREATE_BOT = new Route(Method.POST, "oauth2/applications/{application_id}/bot", null);
    
    public static final Route RESET_APPLICATION_SECRET = new Route(Method.POST, "oauth2/applications/{application_id}/reset", null);
    public static final Route RESET_BOT_TOKEN = new Route(Method.POST, "oauth2/applications/{application_id}/bot/reset", null);
    
    public static final Route GET_AUTHORIZED_APPLICATIONS = new Route(Method.GET, "oauth2/tokens", null);
    public static final Route GET_AUTHORIZED_APPLICATION = new Route(Method.GET, "oauth2/tokens/{auth_id}", null);
    public static final Route DELETE_AUTHORIZED_APPLICATION = new Route(Method.DELETE, "oauth2/tokens/{auth_id}", null);
    
    public Applications() {}
  }
  
  public static class Interactions { public static final Route GET_COMMANDS = new Route(Method.GET, "applications/{application_id}/commands", null);
    public static final Route GET_COMMAND = new Route(Method.GET, "applications/{application_id}/commands/{command_id}", null);
    public static final Route CREATE_COMMAND = new Route(Method.POST, "applications/{application_id}/commands", null);
    public static final Route UPDATE_COMMANDS = new Route(Method.PUT, "applications/{application_id}/commands", null);
    public static final Route EDIT_COMMAND = new Route(Method.PATCH, "applications/{application_id}/commands/{command_id}", null);
    public static final Route DELETE_COMMAND = new Route(Method.DELETE, "applications/{application_id}/commands/{command_id}", null);
    
    public static final Route GET_GUILD_COMMANDS = new Route(Method.GET, "applications/{application_id}/guilds/{guild_id}/commands", null);
    public static final Route GET_GUILD_COMMAND = new Route(Method.GET, "applications/{application_id}/guilds/{guild_id}/commands/{command_id}", null);
    public static final Route CREATE_GUILD_COMMAND = new Route(Method.POST, "applications/{application_id}/guilds/{guild_id}/commands", null);
    public static final Route UPDATE_GUILD_COMMANDS = new Route(Method.PUT, "applications/{application_id}/guilds/{guild_id}/commands", null);
    public static final Route EDIT_GUILD_COMMAND = new Route(Method.PATCH, "applications/{application_id}/guilds/{guild_id}/commands/{command_id}", null);
    public static final Route DELETE_GUILD_COMMAND = new Route(Method.DELETE, "applications/{application_id}/guilds/{guild_id}/commands/{command_id}", null);
    
    public static final Route GET_ALL_COMMAND_PERMISSIONS = new Route(Method.GET, "applications/{application_id}/guilds/{guild_id}/commands/permissions", null);
    public static final Route EDIT_ALL_COMMAND_PERMISSIONS = new Route(Method.PUT, "applications/{application_id}/guilds/{guild_id}/commands/permissions", null);
    public static final Route GET_COMMAND_PERMISSIONS = new Route(Method.GET, "applications/{application_id}/guilds/{guild_id}/commands/{command_id}/permissions", null);
    public static final Route EDIT_COMMAND_PERMISSIONS = new Route(Method.PUT, "applications/{application_id}/guilds/{guild_id}/commands/{command_id}/permissions", null);
    
    public static final Route CALLBACK = new Route(Method.POST, "interactions/{interaction_id}/{interaction_token}/callback", null);
    public static final Route CREATE_FOLLOWUP = new Route(Method.POST, "webhooks/{application_id}/{interaction_token}", null);
    public static final Route EDIT_FOLLOWUP = new Route(Method.PATCH, "webhooks/{application_id}/{interaction_token}/messages/{message_id}", null);
    public static final Route DELETE_FOLLOWUP = new Route(Method.DELETE, "webhooks/{application_id}/{interaction_token}/messages/{message_id}", null);
    public static final Route GET_ORIGINAL = new Route(Method.GET, "webhooks/{application_id}/{interaction_token}/messages/@original", null);
    
    public Interactions() {}
  }
  
  public static class Self { public static final Route GET_SELF = new Route(Method.GET, "users/@me", null);
    public static final Route MODIFY_SELF = new Route(Method.PATCH, "users/@me", null);
    public static final Route GET_GUILDS = new Route(Method.GET, "users/@me/guilds", null);
    public static final Route LEAVE_GUILD = new Route(Method.DELETE, "users/@me/guilds/{guild_id}", null);
    public static final Route GET_PRIVATE_CHANNELS = new Route(Method.GET, "users/@me/channels", null);
    public static final Route CREATE_PRIVATE_CHANNEL = new Route(Method.POST, "users/@me/channels", null);
    

    public static final Route USER_SETTINGS = new Route(Method.GET, "users/@me/settings", null);
    public static final Route GET_CONNECTIONS = new Route(Method.GET, "users/@me/connections", null);
    public static final Route FRIEND_SUGGESTIONS = new Route(Method.GET, "friend-suggestions", null);
    public static final Route GET_RECENT_MENTIONS = new Route(Method.GET, "users/@me/mentions", null);
    
    public Self() {}
  }
  
  public static class Users { public static final Route GET_USER = new Route(Method.GET, "users/{user_id}", null);
    public static final Route GET_PROFILE = new Route(Method.GET, "users/{user_id}/profile", null);
    public static final Route GET_NOTE = new Route(Method.GET, "users/@me/notes/{user_id}", null);
    public static final Route SET_NOTE = new Route(Method.PUT, "users/@me/notes/{user_id}", null);
    
    public Users() {}
  }
  
  public static class Relationships { public static final Route GET_RELATIONSHIPS = new Route(Method.GET, "users/@me/relationships", null);
    public static final Route GET_RELATIONSHIP = new Route(Method.GET, "users/@me/relationships/{user_id}", null);
    public static final Route ADD_RELATIONSHIP = new Route(Method.PUT, "users/@me/relationships/{user_id}", null);
    public static final Route DELETE_RELATIONSHIP = new Route(Method.DELETE, "users/@me/relationships/{user_id}", null);
    
    public Relationships() {}
  }
  
  public static class Guilds { public static final Route GET_GUILD = new Route(Method.GET, "guilds/{guild_id}", null);
    public static final Route MODIFY_GUILD = new Route(Method.PATCH, "guilds/{guild_id}", null);
    public static final Route GET_VANITY_URL = new Route(Method.GET, "guilds/{guild_id}/vanity-url", null);
    public static final Route CREATE_CHANNEL = new Route(Method.POST, "guilds/{guild_id}/channels", null);
    public static final Route GET_CHANNELS = new Route(Method.GET, "guilds/{guild_id}/channels", null);
    public static final Route MODIFY_CHANNELS = new Route(Method.PATCH, "guilds/{guild_id}/channels", null);
    public static final Route MODIFY_ROLES = new Route(Method.PATCH, "guilds/{guild_id}/roles", null);
    public static final Route GET_BANS = new Route(Method.GET, "guilds/{guild_id}/bans", null);
    public static final Route GET_BAN = new Route(Method.GET, "guilds/{guild_id}/bans/{user_id}", null);
    public static final Route UNBAN = new Route(Method.DELETE, "guilds/{guild_id}/bans/{user_id}", null);
    public static final Route BAN = new Route(Method.PUT, "guilds/{guild_id}/bans/{user_id}", null);
    public static final Route KICK_MEMBER = new Route(Method.DELETE, "guilds/{guild_id}/members/{user_id}", null);
    public static final Route MODIFY_MEMBER = new Route(Method.PATCH, "guilds/{guild_id}/members/{user_id}", null);
    public static final Route ADD_MEMBER = new Route(Method.PUT, "guilds/{guild_id}/members/{user_id}", null);
    public static final Route GET_MEMBER = new Route(Method.GET, "guilds/{guild_id}/members/{user_id}", null);
    public static final Route MODIFY_SELF_NICK = new Route(Method.PATCH, "guilds/{guild_id}/members/@me/nick", null);
    public static final Route PRUNABLE_COUNT = new Route(Method.GET, "guilds/{guild_id}/prune", null);
    public static final Route PRUNE_MEMBERS = new Route(Method.POST, "guilds/{guild_id}/prune", null);
    public static final Route GET_WEBHOOKS = new Route(Method.GET, "guilds/{guild_id}/webhooks", null);
    public static final Route GET_GUILD_EMBED = new Route(Method.GET, "guilds/{guild_id}/embed", null);
    public static final Route MODIFY_GUILD_EMBED = new Route(Method.PATCH, "guilds/{guild_id}/embed", null);
    public static final Route GET_GUILD_EMOTES = new Route(Method.GET, "guilds/{guild_id}/emojis", null);
    public static final Route GET_AUDIT_LOGS = new Route(Method.GET, "guilds/{guild_id}/audit-logs", null);
    public static final Route GET_VOICE_REGIONS = new Route(Method.GET, "guilds/{guild_id}/regions", null);
    public static final Route UPDATE_VOICE_STATE = new Route(Method.PATCH, "guilds/{guild_id}/voice-states/{user_id}", null);
    
    public static final Route GET_INTEGRATIONS = new Route(Method.GET, "guilds/{guild_id}/integrations", null);
    public static final Route CREATE_INTEGRATION = new Route(Method.POST, "guilds/{guild_id}/integrations", null);
    public static final Route DELETE_INTEGRATION = new Route(Method.DELETE, "guilds/{guild_id}/integrations/{integration_id}", null);
    public static final Route MODIFY_INTEGRATION = new Route(Method.PATCH, "guilds/{guild_id}/integrations/{integration_id}", null);
    public static final Route SYNC_INTEGRATION = new Route(Method.POST, "guilds/{guild_id}/integrations/{integration_id}/sync", null);
    
    public static final Route ADD_MEMBER_ROLE = new Route(Method.PUT, "guilds/{guild_id}/members/{user_id}/roles/{role_id}", null);
    public static final Route REMOVE_MEMBER_ROLE = new Route(Method.DELETE, "guilds/{guild_id}/members/{user_id}/roles/{role_id}", null);
    


    public static final Route CREATE_GUILD = new Route(Method.POST, "guilds", null);
    public static final Route DELETE_GUILD = new Route(Method.POST, "guilds/{guild_id}/delete", null);
    public static final Route ACK_GUILD = new Route(Method.POST, "guilds/{guild_id}/ack", null);
    
    public static final Route MODIFY_NOTIFICATION_SETTINGS = new Route(Method.PATCH, "users/@me/guilds/{guild_id}/settings", null);
    
    public Guilds() {}
  }
  
  public static class Emotes {
    public static final Route MODIFY_EMOTE = new Route(Method.PATCH, "guilds/{guild_id}/emojis/{emote_id}", null);
    public static final Route DELETE_EMOTE = new Route(Method.DELETE, "guilds/{guild_id}/emojis/{emote_id}", null);
    public static final Route CREATE_EMOTE = new Route(Method.POST, "guilds/{guild_id}/emojis", null);
    
    public static final Route GET_EMOTES = new Route(Method.GET, "guilds/{guild_id}/emojis", null);
    public static final Route GET_EMOTE = new Route(Method.GET, "guilds/{guild_id}/emojis/{emoji_id}", null);
    
    public Emotes() {}
  }
  
  public static class Webhooks { public static final Route GET_WEBHOOK = new Route(Method.GET, "webhooks/{webhook_id}", null);
    public static final Route GET_TOKEN_WEBHOOK = new Route(Method.GET, "webhooks/{webhook_id}/{token}", null);
    public static final Route DELETE_WEBHOOK = new Route(Method.DELETE, "webhooks/{webhook_id}", null);
    public static final Route DELETE_TOKEN_WEBHOOK = new Route(Method.DELETE, "webhooks/{webhook_id}/{token}", null);
    public static final Route MODIFY_WEBHOOK = new Route(Method.PATCH, "webhooks/{webhook_id}", null);
    public static final Route MODIFY_TOKEN_WEBHOOK = new Route(Method.PATCH, "webhooks/{webhook_id}/{token}", null);
    

    public static final Route EXECUTE_WEBHOOK = new Route(Method.POST, "webhooks/{webhook_id}/{token}", null);
    public static final Route EXECUTE_WEBHOOK_EDIT = new Route(Method.PATCH, "webhooks/{webhook_id}/{token}/messages/{message_id}", null);
    public static final Route EXECUTE_WEBHOOK_DELETE = new Route(Method.DELETE, "webhooks/{webhook_id}/{token}/messages/{message_id}", null);
    public static final Route EXECUTE_WEBHOOK_SLACK = new Route(Method.POST, "webhooks/{webhook_id}/{token}/slack", null);
    public static final Route EXECUTE_WEBHOOK_GITHUB = new Route(Method.POST, "webhooks/{webhook_id}/{token}/github", null);
    
    public Webhooks() {}
  }
  
  public static class Roles { public static final Route GET_ROLES = new Route(Method.GET, "guilds/{guild_id}/roles", null);
    public static final Route CREATE_ROLE = new Route(Method.POST, "guilds/{guild_id}/roles", null);
    public static final Route GET_ROLE = new Route(Method.GET, "guilds/{guild_id}/roles/{role_id}", null);
    public static final Route MODIFY_ROLE = new Route(Method.PATCH, "guilds/{guild_id}/roles/{role_id}", null);
    public static final Route DELETE_ROLE = new Route(Method.DELETE, "guilds/{guild_id}/roles/{role_id}", null);
    
    public Roles() {}
  }
  
  public static class Channels { public static final Route DELETE_CHANNEL = new Route(Method.DELETE, "channels/{channel_id}", null);
    public static final Route MODIFY_CHANNEL = new Route(Method.PATCH, "channels/{channel_id}", null);
    public static final Route GET_WEBHOOKS = new Route(Method.GET, "channels/{channel_id}/webhooks", null);
    public static final Route CREATE_WEBHOOK = new Route(Method.POST, "channels/{channel_id}/webhooks", null);
    public static final Route CREATE_PERM_OVERRIDE = new Route(Method.PUT, "channels/{channel_id}/permissions/{permoverride_id}", null);
    public static final Route MODIFY_PERM_OVERRIDE = new Route(Method.PUT, "channels/{channel_id}/permissions/{permoverride_id}", null);
    public static final Route DELETE_PERM_OVERRIDE = new Route(Method.DELETE, "channels/{channel_id}/permissions/{permoverride_id}", null);
    
    public static final Route SEND_TYPING = new Route(Method.POST, "channels/{channel_id}/typing", null);
    public static final Route GET_PERMISSIONS = new Route(Method.GET, "channels/{channel_id}/permissions", null);
    public static final Route GET_PERM_OVERRIDE = new Route(Method.GET, "channels/{channel_id}/permissions/{permoverride_id}", null);
    public static final Route FOLLOW_CHANNEL = new Route(Method.POST, "channels/{channel_id}/followers", null);
    

    public static final Route GET_RECIPIENTS = new Route(Method.GET, "channels/{channel_id}/recipients", null);
    public static final Route GET_RECIPIENT = new Route(Method.GET, "channels/{channel_id}/recipients/{user_id}", null);
    public static final Route ADD_RECIPIENT = new Route(Method.PUT, "channels/{channel_id}/recipients/{user_id}", null);
    public static final Route REMOVE_RECIPIENT = new Route(Method.DELETE, "channels/{channel_id}/recipients/{user_id}", null);
    public static final Route START_CALL = new Route(Method.POST, "channels/{channel_id}/call/ring", null);
    public static final Route STOP_CALL = new Route(Method.POST, "channels/{channel_id}/call/stop_ringing", null);
    
    public Channels() {}
  }
  
  public static class StageInstances { public static final Route GET_INSTANCE = new Route(Method.GET, "stage-instances/{channel_id}", null);
    public static final Route DELETE_INSTANCE = new Route(Method.DELETE, "stage-instances/{channel_id}", null);
    public static final Route UPDATE_INSTANCE = new Route(Method.PATCH, "stage-instances/{channel_id}", null);
    public static final Route CREATE_INSTANCE = new Route(Method.POST, "stage-instances", null);
    
    public StageInstances() {}
  }
  
  public static class Messages { public static final Route EDIT_MESSAGE = new Route(Method.PATCH, "channels/{channel_id}/messages/{message_id}", null);
    public static final Route SEND_MESSAGE = new Route(Method.POST, "channels/{channel_id}/messages", null);
    public static final Route GET_PINNED_MESSAGES = new Route(Method.GET, "channels/{channel_id}/pins", null);
    public static final Route ADD_PINNED_MESSAGE = new Route(Method.PUT, "channels/{channel_id}/pins/{message_id}", null);
    public static final Route REMOVE_PINNED_MESSAGE = new Route(Method.DELETE, "channels/{channel_id}/pins/{message_id}", null);
    
    public static final Route ADD_REACTION = new Route(Method.PUT, "channels/{channel_id}/messages/{message_id}/reactions/{reaction_code}/{user_id}", null);
    public static final Route REMOVE_REACTION = new Route(Method.DELETE, "channels/{channel_id}/messages/{message_id}/reactions/{reaction_code}/{user_id}", null);
    public static final Route REMOVE_ALL_REACTIONS = new Route(Method.DELETE, "channels/{channel_id}/messages/{message_id}/reactions", null);
    public static final Route GET_REACTION_USERS = new Route(Method.GET, "channels/{channel_id}/messages/{message_id}/reactions/{reaction_code}", null);
    public static final Route CLEAR_EMOTE_REACTIONS = new Route(Method.DELETE, "channels/{channel_id}/messages/{message_id}/reactions/{reaction_code}", null);
    
    public static final Route DELETE_MESSAGE = new Route(Method.DELETE, "channels/{channel_id}/messages/{message_id}", null);
    public static final Route GET_MESSAGE_HISTORY = new Route(Method.GET, "channels/{channel_id}/messages", null);
    public static final Route CROSSPOST_MESSAGE = new Route(Method.POST, "channels/{channel_id}/messages/{message_id}/crosspost", null);
    

    public static final Route GET_MESSAGE = new Route(Method.GET, "channels/{channel_id}/messages/{message_id}", null);
    public static final Route DELETE_MESSAGES = new Route(Method.POST, "channels/{channel_id}/messages/bulk-delete", null);
    

    public static final Route ACK_MESSAGE = new Route(Method.POST, "channels/{channel_id}/messages/{message_id}/ack", null);
    
    public Messages() {}
  }
  
  public static class Invites { public static final Route GET_INVITE = new Route(Method.GET, "invites/{code}", null);
    public static final Route GET_GUILD_INVITES = new Route(Method.GET, "guilds/{guild_id}/invites", null);
    public static final Route GET_CHANNEL_INVITES = new Route(Method.GET, "channels/{channel_id}/invites", null);
    public static final Route CREATE_INVITE = new Route(Method.POST, "channels/{channel_id}/invites", null);
    public static final Route DELETE_INVITE = new Route(Method.DELETE, "invites/{code}", null);
    
    public Invites() {}
  }
  
  public static class Templates { public static final Route GET_TEMPLATE = new Route(Method.GET, "guilds/templates/{code}", null);
    public static final Route SYNC_TEMPLATE = new Route(Method.PUT, "guilds/{guild_id}/templates/{code}", null);
    public static final Route CREATE_TEMPLATE = new Route(Method.POST, "guilds/{guild_id}/templates", null);
    public static final Route MODIFY_TEMPLATE = new Route(Method.PATCH, "guilds/{guild_id}/templates/{code}", null);
    public static final Route DELETE_TEMPLATE = new Route(Method.DELETE, "guilds/{guild_id}/templates/{code}", null);
    public static final Route GET_GUILD_TEMPLATES = new Route(Method.GET, "guilds/{guild_id}/templates", null);
    public static final Route CREATE_GUILD_FROM_TEMPLATE = new Route(Method.POST, "guilds/templates/{code}", null);
    
    public Templates() {}
  }
  
  @Nonnull
  public static Route custom(@Nonnull Method method, @Nonnull String route) { Checks.notNull(method, "Method");
    Checks.notEmpty(route, "Route");
    Checks.noWhitespace(route, "Route");
    return new Route(method, route);
  }
  
  @Nonnull
  public static Route delete(@Nonnull String route)
  {
    return custom(Method.DELETE, route);
  }
  
  @Nonnull
  public static Route post(@Nonnull String route)
  {
    return custom(Method.POST, route);
  }
  
  @Nonnull
  public static Route put(@Nonnull String route)
  {
    return custom(Method.PUT, route);
  }
  
  @Nonnull
  public static Route patch(@Nonnull String route)
  {
    return custom(Method.PATCH, route);
  }
  
  @Nonnull
  public static Route get(@Nonnull String route)
  {
    return custom(Method.GET, route);
  }
  





  private Route(Method method, String route)
  {
    this.method = method;
    this.route = route;
    paramCount = Helpers.countMatches(route, '{');
    
    if (paramCount != Helpers.countMatches(route, '}')) {
      throw new IllegalArgumentException("An argument does not have both {}'s for route: " + method + "  " + route);
    }
  }
  
  public Method getMethod() {
    return method;
  }
  
  public String getRoute()
  {
    return route;
  }
  
  public int getParamCount()
  {
    return paramCount;
  }
  
  public CompiledRoute compile(String... params)
  {
    if (params.length != paramCount)
    {
      throw new IllegalArgumentException("Error Compiling Route: [" + route + "], incorrect amount of parameters provided.Expected: " + paramCount + ", Provided: " + params.length);
    }
    


    Set<String> major = new HashSet();
    StringBuilder compiledRoute = new StringBuilder(route);
    for (int i = 0; i < paramCount; i++)
    {
      int paramStart = compiledRoute.indexOf("{");
      int paramEnd = compiledRoute.indexOf("}");
      String paramName = compiledRoute.substring(paramStart + 1, paramEnd);
      if ("guild_id:channel_id:webhook_id:interaction_token".contains(paramName))
      {
        if (params[i].length() > 30) {
          major.add(paramName + "=" + Integer.toUnsignedString(params[i].hashCode()));
        } else {
          major.add(paramName + "=" + params[i]);
        }
      }
      compiledRoute.replace(paramStart, paramEnd + 1, params[i]);
    }
    
    return new CompiledRoute(this, compiledRoute.toString(), major.isEmpty() ? "n/a" : String.join(":", major), null);
  }
  

  public int hashCode()
  {
    return (route + method.toString()).hashCode();
  }
  

  public boolean equals(Object o)
  {
    if (!(o instanceof Route)) {
      return false;
    }
    Route oRoute = (Route)o;
    return (method.equals(method)) && (route.equals(route));
  }
  

  public String toString()
  {
    return method + "/" + route;
  }
  
  public class CompiledRoute
  {
    private final Route baseRoute;
    private final String major;
    private final String compiledRoute;
    private final boolean hasQueryParams;
    
    private CompiledRoute(Route baseRoute, String compiledRoute, String major, boolean hasQueryParams)
    {
      this.baseRoute = baseRoute;
      this.compiledRoute = compiledRoute;
      this.major = major;
      this.hasQueryParams = hasQueryParams;
    }
    
    private CompiledRoute(Route baseRoute, String compiledRoute, String major)
    {
      this(baseRoute, compiledRoute, major, false);
    }
    
    @Nonnull
    @CheckReturnValue
    public CompiledRoute withQueryParams(String... params)
    {
      Checks.check(params.length >= 2, "params length must be at least 2");
      Checks.check(params.length % 2 == 0, "params length must be a multiple of 2");
      
      StringBuilder newRoute = new StringBuilder(compiledRoute);
      
      for (int i = 0; i < params.length; i++) {
        newRoute.append((!hasQueryParams) && (i == 0) ? '?' : '&').append(params[i]).append('=').append(params[(++i)]);
      }
      return new CompiledRoute(Route.this, baseRoute, newRoute.toString(), major, true);
    }
    
    public String getMajorParameters()
    {
      return major;
    }
    
    public String getCompiledRoute()
    {
      return compiledRoute;
    }
    
    public Route getBaseRoute()
    {
      return baseRoute;
    }
    
    public Method getMethod()
    {
      return baseRoute.method;
    }
    

    public int hashCode()
    {
      return (compiledRoute + method.toString()).hashCode();
    }
    

    public boolean equals(Object o)
    {
      if (!(o instanceof CompiledRoute)) {
        return false;
      }
      CompiledRoute oCompiled = (CompiledRoute)o;
      
      return (baseRoute.equals(oCompiled.getBaseRoute())) && (compiledRoute.equals(compiledRoute));
    }
    

    public String toString()
    {
      return "CompiledRoute(" + method + ": " + compiledRoute + ")";
    }
  }
}
