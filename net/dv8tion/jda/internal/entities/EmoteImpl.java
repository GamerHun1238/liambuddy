package net.dv8tion.jda.internal.entities;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ListedEmote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.EmoteManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.managers.EmoteManagerImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.CompiledRoute;
import net.dv8tion.jda.internal.requests.Route.Emotes;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;





















public class EmoteImpl
  implements ListedEmote
{
  private final long id;
  private final JDAImpl api;
  private final Set<Role> roles;
  private EmoteManager manager;
  private GuildImpl guild;
  private boolean managed = false;
  private boolean available = true;
  private boolean animated = false;
  private String name;
  private User user;
  
  public EmoteImpl(long id, GuildImpl guild)
  {
    this.id = id;
    api = guild.getJDA();
    this.guild = guild;
    roles = ConcurrentHashMap.newKeySet();
  }
  
  public EmoteImpl(long id, JDAImpl api)
  {
    this.id = id;
    this.api = api;
    guild = null;
    roles = null;
  }
  

  public GuildImpl getGuild()
  {
    if (guild == null)
      return null;
    GuildImpl realGuild = (GuildImpl)api.getGuildById(guild.getIdLong());
    if (realGuild != null)
      guild = realGuild;
    return guild;
  }
  

  @Nonnull
  public List<Role> getRoles()
  {
    if (!canProvideRoles())
      throw new IllegalStateException("Unable to return roles because this emote is from a message. (We do not know the origin Guild of this emote)");
    return Collections.unmodifiableList(new LinkedList(roles));
  }
  

  public boolean canProvideRoles()
  {
    return roles != null;
  }
  

  @Nonnull
  public String getName()
  {
    return name;
  }
  

  public boolean isManaged()
  {
    return managed;
  }
  

  public boolean isAvailable()
  {
    return available;
  }
  

  public long getIdLong()
  {
    return id;
  }
  

  @Nonnull
  public JDAImpl getJDA()
  {
    return api;
  }
  

  @Nonnull
  public User getUser()
  {
    if (!hasUser())
      throw new IllegalStateException("This emote does not have a user");
    return user;
  }
  

  public boolean hasUser()
  {
    return user != null;
  }
  

  @Nonnull
  public EmoteManager getManager()
  {
    if (manager == null)
      return this.manager = new EmoteManagerImpl(this);
    return manager;
  }
  

  public boolean isAnimated()
  {
    return animated;
  }
  

  @Nonnull
  public AuditableRestAction<Void> delete()
  {
    if (getGuild() == null)
      throw new IllegalStateException("The emote you are trying to delete is not an actual emote we have access to (it is from a message)!");
    if (managed)
      throw new UnsupportedOperationException("You cannot delete a managed emote!");
    if (!getGuild().getSelfMember().hasPermission(new Permission[] { Permission.MANAGE_EMOTES })) {
      throw new InsufficientPermissionException(getGuild(), Permission.MANAGE_EMOTES);
    }
    Route.CompiledRoute route = Route.Emotes.DELETE_EMOTE.compile(new String[] { getGuild().getId(), getId() });
    return new AuditableRestActionImpl(getJDA(), route);
  }
  


  public EmoteImpl setName(String name)
  {
    this.name = name;
    return this;
  }
  
  public EmoteImpl setAnimated(boolean animated)
  {
    this.animated = animated;
    return this;
  }
  
  public EmoteImpl setManaged(boolean val)
  {
    managed = val;
    return this;
  }
  
  public EmoteImpl setAvailable(boolean available)
  {
    this.available = available;
    return this;
  }
  
  public EmoteImpl setUser(User user)
  {
    this.user = user;
    return this;
  }
  


  public Set<Role> getRoleSet()
  {
    return roles;
  }
  



  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (!(obj instanceof EmoteImpl)) {
      return false;
    }
    EmoteImpl oEmote = (EmoteImpl)obj;
    return (id == id) && (getName().equals(oEmote.getName()));
  }
  


  public int hashCode()
  {
    return Long.hashCode(id);
  }
  

  public String toString()
  {
    return "E:" + getName() + '(' + getIdLong() + ')';
  }
  

  public EmoteImpl clone()
  {
    EmoteImpl copy = new EmoteImpl(id, getGuild()).setUser(user).setManaged(managed).setAnimated(animated).setName(name);
    roles.addAll(roles);
    return copy;
  }
}
