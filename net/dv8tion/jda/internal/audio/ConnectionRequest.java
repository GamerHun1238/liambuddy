package net.dv8tion.jda.internal.audio;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

















public class ConnectionRequest
{
  protected final long guildId;
  protected long nextAttemptEpoch;
  protected ConnectionStage stage;
  protected long channelId;
  
  public ConnectionRequest(Guild guild)
  {
    stage = ConnectionStage.DISCONNECT;
    guildId = guild.getIdLong();
  }
  
  public ConnectionRequest(VoiceChannel channel, ConnectionStage stage)
  {
    channelId = channel.getIdLong();
    guildId = channel.getGuild().getIdLong();
    this.stage = stage;
    nextAttemptEpoch = System.currentTimeMillis();
  }
  
  public void setStage(ConnectionStage stage)
  {
    this.stage = stage;
  }
  
  public void setChannel(VoiceChannel channel)
  {
    channelId = channel.getIdLong();
  }
  
  public void setNextAttemptEpoch(long epochMillis)
  {
    nextAttemptEpoch = epochMillis;
  }
  
  public VoiceChannel getChannel(JDA api)
  {
    return api.getVoiceChannelById(channelId);
  }
  
  public long getChannelId()
  {
    return channelId;
  }
  
  public ConnectionStage getStage()
  {
    return stage;
  }
  
  public long getNextAttemptEpoch()
  {
    return nextAttemptEpoch;
  }
  
  public long getGuildIdLong()
  {
    return guildId;
  }
  

  public String toString()
  {
    return stage + "(" + Long.toUnsignedString(guildId) + "#" + Long.toUnsignedString(channelId) + ")";
  }
}
