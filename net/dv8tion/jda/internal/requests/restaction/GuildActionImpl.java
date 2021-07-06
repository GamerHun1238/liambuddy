package net.dv8tion.jda.internal.requests.restaction;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild.ExplicitContentLevel;
import net.dv8tion.jda.api.entities.Guild.NotificationLevel;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.requests.restaction.GuildAction;
import net.dv8tion.jda.api.requests.restaction.GuildAction.ChannelData;
import net.dv8tion.jda.api.requests.restaction.GuildAction.RoleData;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Guilds;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;












public class GuildActionImpl
  extends RestActionImpl<Void>
  implements GuildAction
{
  protected String name;
  protected Region region;
  protected Icon icon;
  protected Guild.VerificationLevel verificationLevel;
  protected Guild.NotificationLevel notificationLevel;
  protected Guild.ExplicitContentLevel explicitContentLevel;
  protected final List<GuildAction.RoleData> roles;
  protected final List<GuildAction.ChannelData> channels;
  
  public GuildActionImpl(JDA api, String name)
  {
    super(api, Route.Guilds.CREATE_GUILD.compile(new String[0]));
    setName(name);
    
    roles = new LinkedList();
    channels = new LinkedList();
    
    roles.add(new GuildAction.RoleData(0L));
  }
  

  @Nonnull
  public GuildActionImpl setCheck(BooleanSupplier checks)
  {
    return (GuildActionImpl)super.setCheck(checks);
  }
  

  @Nonnull
  public GuildActionImpl timeout(long timeout, @Nonnull TimeUnit unit)
  {
    return (GuildActionImpl)super.timeout(timeout, unit);
  }
  

  @Nonnull
  public GuildActionImpl deadline(long timestamp)
  {
    return (GuildActionImpl)super.deadline(timestamp);
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildActionImpl setRegion(Region region)
  {
    Checks.check((region == null) || (!region.isVip()), "Cannot create a Guild with a VIP voice region!");
    this.region = region;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildActionImpl setIcon(Icon icon)
  {
    this.icon = icon;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildActionImpl setName(@Nonnull String name)
  {
    Checks.notBlank(name, "Name");
    name = name.trim();
    Checks.notEmpty(name, "Name");
    Checks.notLonger(name, 100, "Name");
    this.name = name;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildActionImpl setVerificationLevel(Guild.VerificationLevel level)
  {
    verificationLevel = level;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildActionImpl setNotificationLevel(Guild.NotificationLevel level)
  {
    notificationLevel = level;
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildActionImpl setExplicitContentLevel(Guild.ExplicitContentLevel level)
  {
    explicitContentLevel = level;
    return this;
  }
  



  @Nonnull
  @CheckReturnValue
  public GuildActionImpl addChannel(@Nonnull GuildAction.ChannelData channel)
  {
    Checks.notNull(channel, "Channel");
    channels.add(channel);
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildAction.ChannelData getChannel(int index)
  {
    return (GuildAction.ChannelData)channels.get(index);
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildAction.ChannelData removeChannel(int index)
  {
    return (GuildAction.ChannelData)channels.remove(index);
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildActionImpl removeChannel(@Nonnull GuildAction.ChannelData data)
  {
    channels.remove(data);
    return this;
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildAction.ChannelData newChannel(@Nonnull ChannelType type, @Nonnull String name)
  {
    GuildAction.ChannelData data = new GuildAction.ChannelData(type, name);
    addChannel(data);
    return data;
  }
  



  @Nonnull
  @CheckReturnValue
  public GuildAction.RoleData getPublicRole()
  {
    return (GuildAction.RoleData)roles.get(0);
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildAction.RoleData getRole(int index)
  {
    return (GuildAction.RoleData)roles.get(index);
  }
  

  @Nonnull
  @CheckReturnValue
  public GuildAction.RoleData newRole()
  {
    GuildAction.RoleData role = new GuildAction.RoleData(roles.size());
    roles.add(role);
    return role;
  }
  

  protected RequestBody finalizeData()
  {
    DataObject object = DataObject.empty();
    object.put("name", name);
    object.put("roles", DataArray.fromCollection(roles));
    if (!channels.isEmpty())
      object.put("channels", DataArray.fromCollection(channels));
    if (icon != null)
      object.put("icon", icon.getEncoding());
    if (verificationLevel != null)
      object.put("verification_level", Integer.valueOf(verificationLevel.getKey()));
    if (notificationLevel != null)
      object.put("default_message_notifications", Integer.valueOf(notificationLevel.getKey()));
    if (explicitContentLevel != null)
      object.put("explicit_content_filter", Integer.valueOf(explicitContentLevel.getKey()));
    if (region != null)
      object.put("region", region.getKey());
    return getRequestBody(object);
  }
}
