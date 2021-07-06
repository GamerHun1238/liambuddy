package okhttp3;

import java.util.Collections;
import java.util.List;





























public abstract interface CookieJar
{
  public static final CookieJar NO_COOKIES = new CookieJar()
  {
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {}
    
    public List<Cookie> loadForRequest(HttpUrl url) {
      return Collections.emptyList();
    }
  };
  
  public abstract void saveFromResponse(HttpUrl paramHttpUrl, List<Cookie> paramList);
  
  public abstract List<Cookie> loadForRequest(HttpUrl paramHttpUrl);
}
