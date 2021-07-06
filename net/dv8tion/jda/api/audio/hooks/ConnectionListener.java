package net.dv8tion.jda.api.audio.hooks;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.User;

public abstract interface ConnectionListener
{
  public abstract void onPing(long paramLong);
  
  public abstract void onStatusChange(@Nonnull ConnectionStatus paramConnectionStatus);
  
  public abstract void onUserSpeaking(@Nonnull User paramUser, boolean paramBoolean);
  
  public void onUserSpeaking(@Nonnull User user, @Nonnull EnumSet<SpeakingMode> modes) {}
  
  public void onUserSpeaking(@Nonnull User user, boolean speaking, boolean soundshare) {}
}
