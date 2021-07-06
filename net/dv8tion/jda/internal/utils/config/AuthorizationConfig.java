package net.dv8tion.jda.internal.utils.config;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.internal.utils.Checks;


















public final class AuthorizationConfig
{
  private String token;
  
  public AuthorizationConfig(@Nonnull String token)
  {
    Checks.notNull(token, "Token");
    setToken(token);
  }
  
  @Nonnull
  public AccountType getAccountType()
  {
    return AccountType.BOT;
  }
  
  @Nonnull
  public String getToken()
  {
    return token;
  }
  
  public void setToken(@Nonnull String token)
  {
    this.token = ("Bot " + token);
  }
}
