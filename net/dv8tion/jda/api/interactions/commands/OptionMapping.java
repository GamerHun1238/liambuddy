package net.dv8tion.jda.api.interactions.commands;

import gnu.trove.map.TLongObjectMap;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.AbstractChannel;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.data.DataObject;




















public class OptionMapping
{
  private final DataObject data;
  private final OptionType type;
  private final String name;
  private final TLongObjectMap<Object> resolved;
  
  public OptionMapping(DataObject data, TLongObjectMap<Object> resolved)
  {
    this.data = data;
    type = OptionType.fromKey(data.getInt("type", -1));
    name = data.getString("name");
    this.resolved = resolved;
  }
  





  @Nonnull
  public OptionType getType()
  {
    return type;
  }
  





  @Nonnull
  public String getName()
  {
    return name;
  }
  







  @Nonnull
  public String getAsString()
  {
    return data.getString("value");
  }
  








  public boolean getAsBoolean()
  {
    if (type != OptionType.BOOLEAN)
      throw new IllegalStateException("Cannot convert option of type " + type + " to boolean");
    return data.getBoolean("value");
  }
  











  public long getAsLong()
  {
    switch (1.$SwitchMap$net$dv8tion$jda$api$interactions$commands$OptionType[type.ordinal()])
    {
    default: 
      throw new IllegalStateException("Cannot convert option of type " + type + " to long");
    }
    
    



    return data.getLong("value");
  }
  









  @Nonnull
  public IMentionable getAsMentionable()
  {
    Object entity = resolved.get(getAsLong());
    if ((entity instanceof IMentionable))
      return (IMentionable)entity;
    throw new IllegalStateException("Cannot resolve option of type " + type + " to IMentionable");
  }
  









  @Nullable
  public Member getAsMember()
  {
    if (type != OptionType.USER)
      throw new IllegalStateException("Cannot resolve Member for option " + getName() + " of type " + type);
    Object object = resolved.get(getAsLong());
    if ((object instanceof Member))
      return (Member)object;
    return null;
  }
  








  @Nonnull
  public User getAsUser()
  {
    if (type != OptionType.USER)
      throw new IllegalStateException("Cannot resolve User for option " + getName() + " of type " + type);
    Object object = resolved.get(getAsLong());
    if ((object instanceof Member))
      return ((Member)object).getUser();
    if ((object instanceof User))
      return (User)object;
    throw new IllegalStateException("Could not resolve user!");
  }
  








  @Nonnull
  public Role getAsRole()
  {
    if (type != OptionType.ROLE)
      throw new IllegalStateException("Cannot resolve Role for option " + getName() + " of type " + type);
    Object role = resolved.get(getAsLong());
    if ((role instanceof Role))
      return (Role)role;
    throw new IllegalStateException("Could not resolve role!");
  }
  










  @Nonnull
  public GuildChannel getAsGuildChannel()
  {
    AbstractChannel value = getAsChannel();
    if ((value instanceof GuildChannel))
      return (GuildChannel)value;
    throw new IllegalStateException("Could not resolve GuildChannel!");
  }
  









  @Nullable
  public MessageChannel getAsMessageChannel()
  {
    AbstractChannel value = getAsChannel();
    return (value instanceof MessageChannel) ? (MessageChannel)value : null;
  }
  








  @Nonnull
  public ChannelType getChannelType()
  {
    AbstractChannel channel = getAsChannel();
    return channel == null ? ChannelType.UNKNOWN : channel.getType();
  }
  

  public String toString()
  {
    return "Option[" + getType() + "](" + getName() + "=" + getAsString() + ")";
  }
  

  public int hashCode()
  {
    return Objects.hash(new Object[] { getType(), getName() });
  }
  

  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (!(obj instanceof OptionMapping))
      return false;
    OptionMapping data = (OptionMapping)obj;
    return (getType() == data.getType()) && (getName().equals(data.getName()));
  }
  
  @Nullable
  private AbstractChannel getAsChannel()
  {
    if (type != OptionType.CHANNEL)
      throw new IllegalStateException("Cannot resolve AbstractChannel for option " + getName() + " of type " + type);
    return (AbstractChannel)resolved.get(getAsLong());
  }
}
