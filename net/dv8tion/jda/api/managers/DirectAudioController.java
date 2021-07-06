package net.dv8tion.jda.api.managers;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

public abstract interface DirectAudioController
{
  @Nonnull
  public abstract JDA getJDA();
  
  public abstract void connect(@Nonnull VoiceChannel paramVoiceChannel);
  
  public abstract void disconnect(@Nonnull Guild paramGuild);
  
  public abstract void reconnect(@Nonnull VoiceChannel paramVoiceChannel);
}
