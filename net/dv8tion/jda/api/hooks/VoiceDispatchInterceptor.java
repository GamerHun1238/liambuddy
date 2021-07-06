package net.dv8tion.jda.api.hooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.ShardInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.DirectAudioController;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;





























































public abstract interface VoiceDispatchInterceptor
{
  public abstract void onVoiceServerUpdate(@Nonnull VoiceServerUpdate paramVoiceServerUpdate);
  
  public abstract boolean onVoiceStateUpdate(@Nonnull VoiceStateUpdate paramVoiceStateUpdate);
  
  public static abstract interface VoiceUpdate
    extends SerializableData
  {
    @Nonnull
    public abstract Guild getGuild();
    
    @Nonnull
    public abstract DataObject toData();
    
    @Nonnull
    public DirectAudioController getAudioController()
    {
      return getJDA().getDirectAudioController();
    }
    





    public long getGuildIdLong()
    {
      return getGuild().getIdLong();
    }
    





    @Nonnull
    public String getGuildId()
    {
      return Long.toUnsignedString(getGuildIdLong());
    }
    





    @Nonnull
    public JDA getJDA()
    {
      return getGuild().getJDA();
    }
    





    @Nullable
    public JDA.ShardInfo getShardInfo()
    {
      return getJDA().getShardInfo();
    }
  }
  

  public static class VoiceServerUpdate
    implements VoiceDispatchInterceptor.VoiceUpdate
  {
    private final Guild guild;
    
    private final String endpoint;
    private final String token;
    private final String sessionId;
    private final DataObject json;
    
    public VoiceServerUpdate(Guild guild, String endpoint, String token, String sessionId, DataObject json)
    {
      this.guild = guild;
      this.endpoint = endpoint;
      this.token = token;
      this.sessionId = sessionId;
      this.json = json;
    }
    

    @Nonnull
    public Guild getGuild()
    {
      return guild;
    }
    

    @Nonnull
    public DataObject toData()
    {
      return json;
    }
    





    @Nonnull
    public String getEndpoint()
    {
      return endpoint;
    }
    





    @Nonnull
    public String getToken()
    {
      return token;
    }
    





    @Nonnull
    public String getSessionId()
    {
      return sessionId;
    }
  }
  

  public static class VoiceStateUpdate
    implements VoiceDispatchInterceptor.VoiceUpdate
  {
    private final VoiceChannel channel;
    
    private final GuildVoiceState voiceState;
    private final DataObject json;
    
    public VoiceStateUpdate(VoiceChannel channel, GuildVoiceState voiceState, DataObject json)
    {
      this.channel = channel;
      this.voiceState = voiceState;
      this.json = json;
    }
    

    @Nonnull
    public Guild getGuild()
    {
      return voiceState.getGuild();
    }
    

    @Nonnull
    public DataObject toData()
    {
      return json;
    }
    





    @Nullable
    public VoiceChannel getChannel()
    {
      return channel;
    }
    





    @Nonnull
    public GuildVoiceState getVoiceState()
    {
      return voiceState;
    }
  }
}
