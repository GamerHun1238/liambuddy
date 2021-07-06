package net.dv8tion.jda.api.audio.factory;

import javax.annotation.Nonnull;


















public class DefaultSendFactory
  implements IAudioSendFactory
{
  public DefaultSendFactory() {}
  
  @Nonnull
  public IAudioSendSystem createSendSystem(@Nonnull IPacketProvider packetProvider)
  {
    return new DefaultSendSystem(packetProvider);
  }
}
