package net.dv8tion.jda.api.audio.factory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.VoiceChannel;

@NotThreadSafe
public abstract interface IPacketProvider
{
  @Nonnull
  public abstract String getIdentifier();
  
  @Nonnull
  public abstract VoiceChannel getConnectedChannel();
  
  @Nonnull
  public abstract DatagramSocket getUdpSocket();
  
  @Nonnull
  public abstract InetSocketAddress getSocketAddress();
  
  @Nullable
  public abstract ByteBuffer getNextPacketRaw(boolean paramBoolean);
  
  @Nullable
  public abstract DatagramPacket getNextPacket(boolean paramBoolean);
  
  public abstract void onConnectionError(@Nonnull ConnectionStatus paramConnectionStatus);
  
  public abstract void onConnectionLost();
}
