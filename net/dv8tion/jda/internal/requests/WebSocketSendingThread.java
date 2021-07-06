package net.dv8tion.jda.internal.requests;

import gnu.trove.map.TLongObjectMap;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.audio.ConnectionRequest;
import net.dv8tion.jda.internal.audio.ConnectionStage;
import org.slf4j.Logger;

















class WebSocketSendingThread
  implements Runnable
{
  private static final Logger LOG = WebSocketClient.LOG;
  
  private final WebSocketClient client;
  
  private final JDAImpl api;
  private final ReentrantLock queueLock;
  private final Queue<DataObject> chunkQueue;
  private final Queue<DataObject> ratelimitQueue;
  private final TLongObjectMap<ConnectionRequest> queuedAudioConnections;
  private final ScheduledExecutorService executor;
  private Future<?> handle;
  private boolean needRateLimit = false;
  private boolean attemptedToSend = false;
  private boolean shutdown = false;
  
  WebSocketSendingThread(WebSocketClient client)
  {
    this.client = client;
    api = api;
    queueLock = queueLock;
    chunkQueue = chunkSyncQueue;
    ratelimitQueue = ratelimitQueue;
    queuedAudioConnections = queuedAudioConnections;
    executor = executor;
  }
  
  public void shutdown()
  {
    shutdown = true;
    if (handle != null) {
      handle.cancel(false);
    }
  }
  
  public void start() {
    shutdown = false;
    handle = executor.submit(this);
  }
  
  private void scheduleIdle()
  {
    if (shutdown)
      return;
    handle = executor.schedule(this, 500L, TimeUnit.MILLISECONDS);
  }
  
  private void scheduleSentMessage()
  {
    if (shutdown)
      return;
    handle = executor.schedule(this, 10L, TimeUnit.MILLISECONDS);
  }
  
  private void scheduleRateLimit()
  {
    if (shutdown)
      return;
    handle = executor.schedule(this, 1L, TimeUnit.MINUTES);
  }
  


  public void run()
  {
    if (!client.sentAuthInfo)
    {
      scheduleIdle();
      return;
    }
    
    ConnectionRequest audioRequest = null;
    DataObject chunkRequest = null;
    try
    {
      api.setContext();
      attemptedToSend = false;
      needRateLimit = false;
      
      audioRequest = client.getNextAudioConnectRequest();
      if ((!queueLock.tryLock()) && (!queueLock.tryLock(10L, TimeUnit.SECONDS)))
      {
        scheduleNext();
        return;
      }
      
      chunkRequest = (DataObject)chunkQueue.peek();
      if (chunkRequest != null) {
        handleChunkSync(chunkRequest);
      } else if (audioRequest != null) {
        handleAudioRequest(audioRequest);
      } else {
        handleNormalRequest();
      }
    }
    catch (InterruptedException ignored) {
      LOG.debug("Main WS send thread interrupted. Most likely JDA is disconnecting the websocket.");
      return;

    }
    catch (Throwable ex)
    {
      LOG.error("Encountered error in gateway worker", ex);
      
      if (!attemptedToSend)
      {

        if (chunkRequest != null) {
          client.chunkSyncQueue.remove(chunkRequest);
        } else if (audioRequest != null) {
          client.removeAudioConnection(audioRequest.getGuildIdLong());
        }
      }
      
      if ((ex instanceof Error)) {
        throw ((Error)ex);
      }
    }
    finally
    {
      client.maybeUnlock();
    }
    
    scheduleNext();
  }
  
  private void scheduleNext()
  {
    try
    {
      if (needRateLimit) {
        scheduleRateLimit();
      } else if (!attemptedToSend) {
        scheduleIdle();
      } else {
        scheduleSentMessage();
      }
    }
    catch (RejectedExecutionException ex) {
      if ((api.getStatus() == JDA.Status.SHUTTING_DOWN) || (api.getStatus() == JDA.Status.SHUTDOWN)) {
        LOG.debug("Rejected task after shutdown", ex);
      } else {
        LOG.error("Was unable to schedule next packet due to rejected execution by threadpool", ex);
      }
    }
  }
  
  private void handleChunkSync(DataObject chunkOrSyncRequest) {
    LOG.debug("Sending chunk/sync request {}", chunkOrSyncRequest);
    boolean success = send(
      DataObject.empty()
      .put("op", Integer.valueOf(8))
      .put("d", chunkOrSyncRequest));
    

    if (success) {
      chunkQueue.remove();
    }
  }
  
  private void handleAudioRequest(ConnectionRequest audioRequest) {
    long channelId = audioRequest.getChannelId();
    long guildId = audioRequest.getGuildIdLong();
    Guild guild = api.getGuildById(guildId);
    if (guild == null)
    {
      LOG.debug("Discarding voice request due to null guild {}", Long.valueOf(guildId));
      
      queuedAudioConnections.remove(guildId);
      return;
    }
    ConnectionStage stage = audioRequest.getStage();
    AudioManager audioManager = guild.getAudioManager();
    DataObject packet;
    DataObject packet; switch (1.$SwitchMap$net$dv8tion$jda$internal$audio$ConnectionStage[stage.ordinal()])
    {
    case 1: 
    case 2: 
      packet = newVoiceClose(guildId);
      break;
    case 3: 
    default: 
      packet = newVoiceOpen(audioManager, channelId, guild.getIdLong());
    }
    LOG.debug("Sending voice request {}", packet);
    if (send(packet))
    {



      audioRequest.setNextAttemptEpoch(System.currentTimeMillis() + 10000L);
      


      GuildVoiceState voiceState = guild.getSelfMember().getVoiceState();
      client.updateAudioConnection0(guild.getIdLong(), voiceState.getChannel());
    }
  }
  
  private void handleNormalRequest()
  {
    DataObject message = (DataObject)ratelimitQueue.peek();
    if (message != null)
    {
      LOG.debug("Sending normal message {}", message);
      if (send(message)) {
        ratelimitQueue.remove();
      }
    }
  }
  
  private boolean send(DataObject request)
  {
    needRateLimit = (!client.send(request, false));
    attemptedToSend = true;
    return !needRateLimit;
  }
  
  protected DataObject newVoiceClose(long guildId)
  {
    return 
    
      DataObject.empty().put("op", Integer.valueOf(4)).put("d", DataObject.empty()
      .put("guild_id", Long.toUnsignedString(guildId))
      .putNull("channel_id")
      .put("self_mute", Boolean.valueOf(false))
      .put("self_deaf", Boolean.valueOf(false)));
  }
  
  protected DataObject newVoiceOpen(AudioManager manager, long channel, long guild)
  {
    return 
    
      DataObject.empty().put("op", Integer.valueOf(4)).put("d", DataObject.empty()
      .put("guild_id", Long.valueOf(guild))
      .put("channel_id", Long.valueOf(channel))
      .put("self_mute", Boolean.valueOf(manager.isSelfMuted()))
      .put("self_deaf", Boolean.valueOf(manager.isSelfDeafened())));
  }
}
