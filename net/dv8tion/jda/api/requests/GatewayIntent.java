package net.dv8tion.jda.api.requests;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.emote.GenericEmoteEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.invite.GenericGuildInviteEvent;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.priv.GenericPrivateMessageEvent;
import net.dv8tion.jda.api.events.message.priv.react.GenericPrivateMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserUpdateEvent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.utils.Checks;




























































public enum GatewayIntent
{
  GUILD_MEMBERS(1), 
  


  GUILD_BANS(2), 
  


  GUILD_EMOJIS(3), 
  






  GUILD_WEBHOOKS(5), 
  


  GUILD_INVITES(6), 
  


  GUILD_VOICE_STATES(7), 
  





  GUILD_PRESENCES(8), 
  


  GUILD_MESSAGES(9), 
  


  GUILD_MESSAGE_REACTIONS(10), 
  


  GUILD_MESSAGE_TYPING(11), 
  


  DIRECT_MESSAGES(12), 
  


  DIRECT_MESSAGE_REACTIONS(13), 
  


  DIRECT_MESSAGE_TYPING(14);
  








  public static final int ALL_INTENTS = 0x1 | getRaw(EnumSet.allOf(GatewayIntent.class));
  
















  public static final int DEFAULT = ALL_INTENTS & (getRaw(GUILD_MEMBERS, new GatewayIntent[] { GUILD_PRESENCES, GUILD_WEBHOOKS, GUILD_MESSAGE_TYPING, DIRECT_MESSAGE_TYPING }) ^ 0xFFFFFFFF);
  
  private final int rawValue;
  private final int offset;
  
  private GatewayIntent(int offset)
  {
    this.offset = offset;
    rawValue = (1 << offset);
  }
  





  public int getRawValue()
  {
    return rawValue;
  }
  






  public int getOffset()
  {
    return offset;
  }
  








  @Nonnull
  public static EnumSet<GatewayIntent> getIntents(int raw)
  {
    EnumSet<GatewayIntent> set = EnumSet.noneOf(GatewayIntent.class);
    for (GatewayIntent intent : values())
    {
      if ((intent.getRawValue() & raw) != 0)
        set.add(intent);
    }
    return set;
  }
  











  public static int getRaw(@Nonnull Collection<GatewayIntent> set)
  {
    int raw = 0;
    for (GatewayIntent intent : set)
      raw |= rawValue;
    return raw;
  }
  













  public static int getRaw(@Nonnull GatewayIntent intent, @Nonnull GatewayIntent... set)
  {
    Checks.notNull(intent, "Intent");
    Checks.notNull(set, "Intent");
    return getRaw(EnumSet.of(intent, set));
  }
  














  @Nonnull
  public static EnumSet<GatewayIntent> fromCacheFlags(@Nonnull CacheFlag flag, @Nonnull CacheFlag... other)
  {
    Checks.notNull(flag, "CacheFlag");
    Checks.noneNull(other, "CacheFlag");
    return fromCacheFlags(EnumSet.of(flag, other));
  }
  












  @Nonnull
  public static EnumSet<GatewayIntent> fromCacheFlags(@Nonnull Collection<CacheFlag> flags)
  {
    EnumSet<GatewayIntent> intents = EnumSet.noneOf(GatewayIntent.class);
    for (CacheFlag flag : flags)
    {
      Checks.notNull(flag, "CacheFlag");
      GatewayIntent intent = flag.getRequiredIntent();
      if (intent != null) {
        intents.add(intent);
      }
    }
    return intents;
  }
  











  @Nonnull
  @SafeVarargs
  public static EnumSet<GatewayIntent> fromEvents(@Nonnull Class<? extends GenericEvent>... events)
  {
    Checks.noneNull(events, "Event");
    return fromEvents(Arrays.asList(events));
  }
  











  @Nonnull
  public static EnumSet<GatewayIntent> fromEvents(@Nonnull Collection<Class<? extends GenericEvent>> events)
  {
    EnumSet<GatewayIntent> intents = EnumSet.noneOf(GatewayIntent.class);
    for (Class<? extends GenericEvent> event : events)
    {
      Checks.notNull(event, "Event");
      
      if (GenericUserPresenceEvent.class.isAssignableFrom(event)) {
        intents.add(GUILD_PRESENCES);
      } else if ((GenericUserUpdateEvent.class.isAssignableFrom(event)) || (GenericGuildMemberEvent.class.isAssignableFrom(event)) || (GuildMemberRemoveEvent.class.isAssignableFrom(event))) {
        intents.add(GUILD_MEMBERS);
      }
      else if ((GuildBanEvent.class.isAssignableFrom(event)) || (GuildUnbanEvent.class.isAssignableFrom(event))) {
        intents.add(GUILD_BANS);
      } else if (GenericEmoteEvent.class.isAssignableFrom(event)) {
        intents.add(GUILD_EMOJIS);
      } else if (GenericGuildInviteEvent.class.isAssignableFrom(event)) {
        intents.add(GUILD_INVITES);
      } else if (GenericGuildVoiceEvent.class.isAssignableFrom(event)) {
        intents.add(GUILD_VOICE_STATES);
      }
      else if (GenericGuildMessageReactionEvent.class.isAssignableFrom(event)) {
        intents.add(GUILD_MESSAGE_REACTIONS);
      } else if ((GenericGuildMessageEvent.class.isAssignableFrom(event)) || (MessageBulkDeleteEvent.class.isAssignableFrom(event))) {
        intents.add(GUILD_MESSAGES);
      }
      else if (GenericPrivateMessageReactionEvent.class.isAssignableFrom(event)) {
        intents.add(DIRECT_MESSAGE_REACTIONS);
      } else if (GenericPrivateMessageEvent.class.isAssignableFrom(event)) {
        intents.add(DIRECT_MESSAGES);
      }
      else if (GenericMessageReactionEvent.class.isAssignableFrom(event)) {
        Collections.addAll(intents, new GatewayIntent[] { GUILD_MESSAGE_REACTIONS, DIRECT_MESSAGE_REACTIONS });
      }
      else if (GenericMessageEvent.class.isAssignableFrom(event)) {
        Collections.addAll(intents, new GatewayIntent[] { GUILD_MESSAGES, DIRECT_MESSAGES });
      }
      else if (UserTypingEvent.class.isAssignableFrom(event))
        Collections.addAll(intents, new GatewayIntent[] { GUILD_MESSAGE_TYPING, DIRECT_MESSAGE_TYPING });
    }
    return intents;
  }
  













  @Nonnull
  public static EnumSet<GatewayIntent> from(@Nonnull Collection<Class<? extends GenericEvent>> events, @Nonnull Collection<CacheFlag> flags)
  {
    EnumSet<GatewayIntent> intents = fromEvents(events);
    intents.addAll(fromCacheFlags(flags));
    return intents;
  }
}
