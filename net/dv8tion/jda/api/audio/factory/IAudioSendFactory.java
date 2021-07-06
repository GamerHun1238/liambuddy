package net.dv8tion.jda.api.audio.factory;

import javax.annotation.Nonnull;

public abstract interface IAudioSendFactory
{
  @Nonnull
  public abstract IAudioSendSystem createSendSystem(@Nonnull IPacketProvider paramIPacketProvider);
}
