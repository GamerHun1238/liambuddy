package net.dv8tion.jda.api.events.guild.voice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.UpdateEvent;

public abstract interface GuildVoiceUpdateEvent
  extends UpdateEvent<Member, VoiceChannel>
{
  public static final String IDENTIFIER = "voice-channel";
  
  @Nonnull
  public abstract Member getMember();
  
  @Nonnull
  public abstract Guild getGuild();
  
  @Nullable
  public abstract VoiceChannel getChannelLeft();
  
  @Nullable
  public abstract VoiceChannel getChannelJoined();
}
