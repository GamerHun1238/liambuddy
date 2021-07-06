package okhttp3;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

















public final class Challenge
{
  private final String scheme;
  private final Map<String, String> authParams;
  
  public Challenge(String scheme, Map<String, String> authParams)
  {
    if (scheme == null) throw new NullPointerException("scheme == null");
    if (authParams == null) throw new NullPointerException("authParams == null");
    this.scheme = scheme;
    Map<String, String> newAuthParams = new LinkedHashMap();
    for (Map.Entry<String, String> authParam : authParams.entrySet()) {
      String key = authParam.getKey() == null ? null : ((String)authParam.getKey()).toLowerCase(Locale.US);
      newAuthParams.put(key, (String)authParam.getValue());
    }
    this.authParams = Collections.unmodifiableMap(newAuthParams);
  }
  
  public Challenge(String scheme, String realm) {
    if (scheme == null) throw new NullPointerException("scheme == null");
    if (realm == null) throw new NullPointerException("realm == null");
    this.scheme = scheme;
    authParams = Collections.singletonMap("realm", realm);
  }
  
  public Challenge withCharset(Charset charset)
  {
    if (charset == null) throw new NullPointerException("charset == null");
    Map<String, String> authParams = new LinkedHashMap(this.authParams);
    authParams.put("charset", charset.name());
    return new Challenge(scheme, authParams);
  }
  
  public String scheme()
  {
    return scheme;
  }
  



  public Map<String, String> authParams()
  {
    return authParams;
  }
  
  public String realm()
  {
    return (String)authParams.get("realm");
  }
  
  public Charset charset()
  {
    String charset = (String)authParams.get("charset");
    if (charset != null) {
      try {
        return Charset.forName(charset);
      }
      catch (Exception localException) {}
    }
    return StandardCharsets.ISO_8859_1;
  }
  
  public boolean equals(@Nullable Object other) {
    return ((other instanceof Challenge)) && 
      (scheme.equals(scheme)) && 
      (authParams.equals(authParams));
  }
  
  public int hashCode() {
    int result = 29;
    result = 31 * result + scheme.hashCode();
    result = 31 * result + authParams.hashCode();
    return result;
  }
  
  public String toString() {
    return scheme + " authParams=" + authParams;
  }
}
