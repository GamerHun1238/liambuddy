package net.dv8tion.jda.api.audit;

public enum TargetType
{
  GUILD,  CHANNEL,  ROLE,  MEMBER,  INVITE,  WEBHOOK,  EMOTE,  INTEGRATION,  STAGE_INSTANCE,  UNKNOWN;
  
  private TargetType() {}
}
