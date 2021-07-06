package net.dv8tion.jda.internal.entities;

import java.util.Collection;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.ApplicationTeam;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.Checks;


















public class ApplicationInfoImpl
  implements ApplicationInfo
{
  private final JDA api;
  private final boolean doesBotRequireCodeGrant;
  private final boolean isBotPublic;
  private final long id;
  private final String iconId;
  private final String description;
  private final String name;
  private final User owner;
  private final ApplicationTeam team;
  private String scopes = "bot";
  

  public ApplicationInfoImpl(JDA api, String description, boolean doesBotRequireCodeGrant, String iconId, long id, boolean isBotPublic, String name, User owner, ApplicationTeam team)
  {
    this.api = api;
    this.description = description;
    this.doesBotRequireCodeGrant = doesBotRequireCodeGrant;
    this.iconId = iconId;
    this.id = id;
    this.isBotPublic = isBotPublic;
    this.name = name;
    this.owner = owner;
    this.team = team;
  }
  

  public final boolean doesBotRequireCodeGrant()
  {
    return doesBotRequireCodeGrant;
  }
  

  public boolean equals(Object obj)
  {
    return ((obj instanceof ApplicationInfoImpl)) && (id == id);
  }
  

  @Nonnull
  public String getDescription()
  {
    return description;
  }
  

  public String getIconId()
  {
    return iconId;
  }
  

  public String getIconUrl()
  {
    return 
      "https://cdn.discordapp.com/app-icons/" + id + '/' + iconId + ".png";
  }
  

  @Nonnull
  public ApplicationTeam getTeam()
  {
    return team;
  }
  

  @Nonnull
  public ApplicationInfo setRequiredScopes(@Nonnull Collection<String> scopes)
  {
    Checks.noneNull(scopes, "Scopes");
    this.scopes = String.join("+", scopes);
    if (!this.scopes.contains("bot"))
    {
      if (this.scopes.isEmpty()) {
        this.scopes = "bot";
      } else
        this.scopes += "+bot";
    }
    return this;
  }
  

  public long getIdLong()
  {
    return id;
  }
  

  @Nonnull
  public String getInviteUrl(String guildId, Collection<Permission> permissions)
  {
    StringBuilder builder = new StringBuilder("https://discord.com/oauth2/authorize?client_id=");
    builder.append(getId());
    builder.append("&scope=").append(scopes);
    if ((permissions != null) && (!permissions.isEmpty()))
    {
      builder.append("&permissions=");
      builder.append(Permission.getRaw(permissions));
    }
    if (guildId != null)
    {
      builder.append("&guild_id=");
      builder.append(guildId);
    }
    return builder.toString();
  }
  

  @Nonnull
  public JDA getJDA()
  {
    return api;
  }
  

  @Nonnull
  public String getName()
  {
    return name;
  }
  

  @Nonnull
  public User getOwner()
  {
    return owner;
  }
  

  public int hashCode()
  {
    return Long.hashCode(id);
  }
  

  public final boolean isBotPublic()
  {
    return isBotPublic;
  }
  

  public String toString()
  {
    return "ApplicationInfo(" + id + ")";
  }
}
