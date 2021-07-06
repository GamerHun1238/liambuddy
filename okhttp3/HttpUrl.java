package okhttp3;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okhttp3.internal.publicsuffix.PublicSuffixDatabase;
import okio.Buffer;














































































































































































































































































public final class HttpUrl
{
  private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  

  static final String USERNAME_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
  

  static final String PASSWORD_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
  

  static final String PATH_SEGMENT_ENCODE_SET = " \"<>^`{}|/\\?#";
  
  static final String PATH_SEGMENT_ENCODE_SET_URI = "[]";
  
  static final String QUERY_ENCODE_SET = " \"'<>#";
  
  static final String QUERY_COMPONENT_REENCODE_SET = " \"'<>#&=";
  
  static final String QUERY_COMPONENT_ENCODE_SET = " !\"#$&'(),/:;<=>?@[]\\^`{|}~";
  
  static final String QUERY_COMPONENT_ENCODE_SET_URI = "\\^`{|}";
  
  static final String FORM_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~";
  
  static final String FRAGMENT_ENCODE_SET = "";
  
  static final String FRAGMENT_ENCODE_SET_URI = " \"#<>\\^`{|}";
  
  final String scheme;
  
  private final String username;
  
  private final String password;
  
  final String host;
  
  final int port;
  
  private final List<String> pathSegments;
  
  @Nullable
  private final List<String> queryNamesAndValues;
  
  @Nullable
  private final String fragment;
  
  private final String url;
  

  HttpUrl(Builder builder)
  {
    scheme = scheme;
    username = percentDecode(encodedUsername, false);
    password = percentDecode(encodedPassword, false);
    host = host;
    port = builder.effectivePort();
    pathSegments = percentDecode(encodedPathSegments, false);
    

    queryNamesAndValues = (encodedQueryNamesAndValues != null ? percentDecode(encodedQueryNamesAndValues, true) : null);
    

    fragment = (encodedFragment != null ? percentDecode(encodedFragment, false) : null);
    url = builder.toString();
  }
  
  public URL url()
  {
    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
  












  public URI uri()
  {
    String uri = newBuilder().reencodeForUri().toString();
    try {
      return new URI(uri);
    }
    catch (URISyntaxException e) {
      try {
        String stripped = uri.replaceAll("[\\u0000-\\u001F\\u007F-\\u009F\\p{javaWhitespace}]", "");
        return URI.create(stripped);
      } catch (Exception e1) {
        throw new RuntimeException(e);
      }
    }
  }
  
  public String scheme()
  {
    return scheme;
  }
  
  public boolean isHttps() {
    return scheme.equals("https");
  }
  










  public String encodedUsername()
  {
    if (username.isEmpty()) return "";
    int usernameStart = scheme.length() + 3;
    int usernameEnd = Util.delimiterOffset(url, usernameStart, url.length(), ":@");
    return url.substring(usernameStart, usernameEnd);
  }
  










  public String username()
  {
    return username;
  }
  










  public String encodedPassword()
  {
    if (password.isEmpty()) return "";
    int passwordStart = url.indexOf(':', scheme.length() + 3) + 1;
    int passwordEnd = url.indexOf('@');
    return url.substring(passwordStart, passwordEnd);
  }
  










  public String password()
  {
    return password;
  }
  


















  public String host()
  {
    return host;
  }
  











  public int port()
  {
    return port;
  }
  



  public static int defaultPort(String scheme)
  {
    if (scheme.equals("http"))
      return 80;
    if (scheme.equals("https")) {
      return 443;
    }
    return -1;
  }
  











  public int pathSize()
  {
    return pathSegments.size();
  }
  










  public String encodedPath()
  {
    int pathStart = url.indexOf('/', scheme.length() + 3);
    int pathEnd = Util.delimiterOffset(url, pathStart, url.length(), "?#");
    return url.substring(pathStart, pathEnd);
  }
  
  static void pathSegmentsToString(StringBuilder out, List<String> pathSegments) {
    int i = 0; for (int size = pathSegments.size(); i < size; i++) {
      out.append('/');
      out.append((String)pathSegments.get(i));
    }
  }
  










  public List<String> encodedPathSegments()
  {
    int pathStart = url.indexOf('/', scheme.length() + 3);
    int pathEnd = Util.delimiterOffset(url, pathStart, url.length(), "?#");
    List<String> result = new ArrayList();
    for (int i = pathStart; i < pathEnd;) {
      i++;
      int segmentEnd = Util.delimiterOffset(url, i, pathEnd, '/');
      result.add(url.substring(i, segmentEnd));
      i = segmentEnd;
    }
    return result;
  }
  










  public List<String> pathSegments()
  {
    return pathSegments;
  }
  













  @Nullable
  public String encodedQuery()
  {
    if (queryNamesAndValues == null) return null;
    int queryStart = url.indexOf('?') + 1;
    int queryEnd = Util.delimiterOffset(url, queryStart, url.length(), '#');
    return url.substring(queryStart, queryEnd);
  }
  
  static void namesAndValuesToQueryString(StringBuilder out, List<String> namesAndValues) {
    int i = 0; for (int size = namesAndValues.size(); i < size; i += 2) {
      String name = (String)namesAndValues.get(i);
      String value = (String)namesAndValues.get(i + 1);
      if (i > 0) out.append('&');
      out.append(name);
      if (value != null) {
        out.append('=');
        out.append(value);
      }
    }
  }
  





  static List<String> queryStringToNamesAndValues(String encodedQuery)
  {
    List<String> result = new ArrayList();
    for (int pos = 0; pos <= encodedQuery.length();) {
      int ampersandOffset = encodedQuery.indexOf('&', pos);
      if (ampersandOffset == -1) { ampersandOffset = encodedQuery.length();
      }
      int equalsOffset = encodedQuery.indexOf('=', pos);
      if ((equalsOffset == -1) || (equalsOffset > ampersandOffset)) {
        result.add(encodedQuery.substring(pos, ampersandOffset));
        result.add(null);
      } else {
        result.add(encodedQuery.substring(pos, equalsOffset));
        result.add(encodedQuery.substring(equalsOffset + 1, ampersandOffset));
      }
      pos = ampersandOffset + 1;
    }
    return result;
  }
  













  @Nullable
  public String query()
  {
    if (queryNamesAndValues == null) return null;
    StringBuilder result = new StringBuilder();
    namesAndValuesToQueryString(result, queryNamesAndValues);
    return result.toString();
  }
  













  public int querySize()
  {
    return queryNamesAndValues != null ? queryNamesAndValues.size() / 2 : 0;
  }
  











  @Nullable
  public String queryParameter(String name)
  {
    if (queryNamesAndValues == null) return null;
    int i = 0; for (int size = queryNamesAndValues.size(); i < size; i += 2) {
      if (name.equals(queryNamesAndValues.get(i))) {
        return (String)queryNamesAndValues.get(i + 1);
      }
    }
    return null;
  }
  












  public Set<String> queryParameterNames()
  {
    if (queryNamesAndValues == null) return Collections.emptySet();
    Set<String> result = new LinkedHashSet();
    int i = 0; for (int size = queryNamesAndValues.size(); i < size; i += 2) {
      result.add((String)queryNamesAndValues.get(i));
    }
    return Collections.unmodifiableSet(result);
  }
  

















  public List<String> queryParameterValues(String name)
  {
    if (queryNamesAndValues == null) return Collections.emptyList();
    List<String> result = new ArrayList();
    int i = 0; for (int size = queryNamesAndValues.size(); i < size; i += 2) {
      if (name.equals(queryNamesAndValues.get(i))) {
        result.add((String)queryNamesAndValues.get(i + 1));
      }
    }
    return Collections.unmodifiableList(result);
  }
  
















  public String queryParameterName(int index)
  {
    if (queryNamesAndValues == null) throw new IndexOutOfBoundsException();
    return (String)queryNamesAndValues.get(index * 2);
  }
  
















  public String queryParameterValue(int index)
  {
    if (queryNamesAndValues == null) throw new IndexOutOfBoundsException();
    return (String)queryNamesAndValues.get(index * 2 + 1);
  }
  










  @Nullable
  public String encodedFragment()
  {
    if (fragment == null) return null;
    int fragmentStart = url.indexOf('#') + 1;
    return url.substring(fragmentStart);
  }
  










  @Nullable
  public String fragment()
  {
    return fragment;
  }
  




  public String redact()
  {
    return 
    


      newBuilder("/...").username("").password("").build().toString();
  }
  


  @Nullable
  public HttpUrl resolve(String link)
  {
    Builder builder = newBuilder(link);
    return builder != null ? builder.build() : null;
  }
  
  public Builder newBuilder() {
    Builder result = new Builder();
    scheme = scheme;
    encodedUsername = encodedUsername();
    encodedPassword = encodedPassword();
    host = host;
    
    port = (port != defaultPort(scheme) ? port : -1);
    encodedPathSegments.clear();
    encodedPathSegments.addAll(encodedPathSegments());
    result.encodedQuery(encodedQuery());
    encodedFragment = encodedFragment();
    return result;
  }
  

  @Nullable
  public Builder newBuilder(String link)
  {
    try
    {
      return new Builder().parse(this, link);
    } catch (IllegalArgumentException ignored) {}
    return null;
  }
  


  @Nullable
  public static HttpUrl parse(String url)
  {
    try
    {
      return get(url);
    } catch (IllegalArgumentException ignored) {}
    return null;
  }
  





  public static HttpUrl get(String url)
  {
    return new Builder().parse(null, url).build();
  }
  


  @Nullable
  public static HttpUrl get(URL url)
  {
    return parse(url.toString());
  }
  
  @Nullable
  public static HttpUrl get(URI uri) { return parse(uri.toString()); }
  
  public boolean equals(@Nullable Object other)
  {
    return ((other instanceof HttpUrl)) && (url.equals(url));
  }
  
  public int hashCode() {
    return url.hashCode();
  }
  
  public String toString() {
    return url;
  }
  

















  @Nullable
  public String topPrivateDomain()
  {
    if (Util.verifyAsIpAddress(host)) return null;
    return PublicSuffixDatabase.get().getEffectiveTldPlusOne(host);
  }
  
  public static final class Builder { @Nullable
    String scheme;
    String encodedUsername = "";
    String encodedPassword = "";
    @Nullable
    String host; int port = -1;
    final List<String> encodedPathSegments = new ArrayList();
    @Nullable
    List<String> encodedQueryNamesAndValues;
    
    public Builder() {
      encodedPathSegments.add("");
    }
    
    public Builder scheme(String scheme) {
      if (scheme == null)
        throw new NullPointerException("scheme == null");
      if (scheme.equalsIgnoreCase("http")) {
        this.scheme = "http";
      } else if (scheme.equalsIgnoreCase("https")) {
        this.scheme = "https";
      } else {
        throw new IllegalArgumentException("unexpected scheme: " + scheme);
      }
      return this;
    }
    
    public Builder username(String username) {
      if (username == null) throw new NullPointerException("username == null");
      encodedUsername = HttpUrl.canonicalize(username, " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
      return this;
    }
    
    public Builder encodedUsername(String encodedUsername) {
      if (encodedUsername == null) throw new NullPointerException("encodedUsername == null");
      this.encodedUsername = HttpUrl.canonicalize(encodedUsername, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
      
      return this;
    }
    
    public Builder password(String password) {
      if (password == null) throw new NullPointerException("password == null");
      encodedPassword = HttpUrl.canonicalize(password, " \"':;<=>@[]^`{}|/\\?#", false, false, false, true);
      return this;
    }
    
    public Builder encodedPassword(String encodedPassword) {
      if (encodedPassword == null) throw new NullPointerException("encodedPassword == null");
      this.encodedPassword = HttpUrl.canonicalize(encodedPassword, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true);
      
      return this;
    }
    



    public Builder host(String host)
    {
      if (host == null) throw new NullPointerException("host == null");
      String encoded = canonicalizeHost(host, 0, host.length());
      if (encoded == null) throw new IllegalArgumentException("unexpected host: " + host);
      this.host = encoded;
      return this;
    }
    
    public Builder port(int port) {
      if ((port <= 0) || (port > 65535)) throw new IllegalArgumentException("unexpected port: " + port);
      this.port = port;
      return this;
    }
    
    int effectivePort() {
      return port != -1 ? port : HttpUrl.defaultPort(scheme);
    }
    
    public Builder addPathSegment(String pathSegment) {
      if (pathSegment == null) throw new NullPointerException("pathSegment == null");
      push(pathSegment, 0, pathSegment.length(), false, false);
      return this;
    }
    



    public Builder addPathSegments(String pathSegments)
    {
      if (pathSegments == null) throw new NullPointerException("pathSegments == null");
      return addPathSegments(pathSegments, false);
    }
    
    public Builder addEncodedPathSegment(String encodedPathSegment) {
      if (encodedPathSegment == null) {
        throw new NullPointerException("encodedPathSegment == null");
      }
      push(encodedPathSegment, 0, encodedPathSegment.length(), false, true);
      return this;
    }
    




    public Builder addEncodedPathSegments(String encodedPathSegments)
    {
      if (encodedPathSegments == null) {
        throw new NullPointerException("encodedPathSegments == null");
      }
      return addPathSegments(encodedPathSegments, true);
    }
    
    private Builder addPathSegments(String pathSegments, boolean alreadyEncoded) {
      int offset = 0;
      do {
        int segmentEnd = Util.delimiterOffset(pathSegments, offset, pathSegments.length(), "/\\");
        boolean addTrailingSlash = segmentEnd < pathSegments.length();
        push(pathSegments, offset, segmentEnd, addTrailingSlash, alreadyEncoded);
        offset = segmentEnd + 1;
      } while (offset <= pathSegments.length());
      return this;
    }
    
    public Builder setPathSegment(int index, String pathSegment) {
      if (pathSegment == null) throw new NullPointerException("pathSegment == null");
      String canonicalPathSegment = HttpUrl.canonicalize(pathSegment, 0, pathSegment.length(), " \"<>^`{}|/\\?#", false, false, false, true, null);
      
      if ((isDot(canonicalPathSegment)) || (isDotDot(canonicalPathSegment))) {
        throw new IllegalArgumentException("unexpected path segment: " + pathSegment);
      }
      encodedPathSegments.set(index, canonicalPathSegment);
      return this;
    }
    
    public Builder setEncodedPathSegment(int index, String encodedPathSegment) {
      if (encodedPathSegment == null) {
        throw new NullPointerException("encodedPathSegment == null");
      }
      String canonicalPathSegment = HttpUrl.canonicalize(encodedPathSegment, 0, encodedPathSegment.length(), " \"<>^`{}|/\\?#", true, false, false, true, null);
      
      encodedPathSegments.set(index, canonicalPathSegment);
      if ((isDot(canonicalPathSegment)) || (isDotDot(canonicalPathSegment))) {
        throw new IllegalArgumentException("unexpected path segment: " + encodedPathSegment);
      }
      return this;
    }
    
    public Builder removePathSegment(int index) {
      encodedPathSegments.remove(index);
      if (encodedPathSegments.isEmpty()) {
        encodedPathSegments.add("");
      }
      return this;
    }
    
    public Builder encodedPath(String encodedPath) {
      if (encodedPath == null) throw new NullPointerException("encodedPath == null");
      if (!encodedPath.startsWith("/")) {
        throw new IllegalArgumentException("unexpected encodedPath: " + encodedPath);
      }
      resolvePath(encodedPath, 0, encodedPath.length());
      return this;
    }
    


    public Builder query(@Nullable String query)
    {
      encodedQueryNamesAndValues = (query != null ? HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(query, " \"'<>#", false, false, true, true)) : null);
      return this;
    }
    


    public Builder encodedQuery(@Nullable String encodedQuery)
    {
      encodedQueryNamesAndValues = (encodedQuery != null ? HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(encodedQuery, " \"'<>#", true, false, true, true)) : null);
      return this;
    }
    
    public Builder addQueryParameter(String name, @Nullable String value)
    {
      if (name == null) throw new NullPointerException("name == null");
      if (encodedQueryNamesAndValues == null) encodedQueryNamesAndValues = new ArrayList();
      encodedQueryNamesAndValues.add(
        HttpUrl.canonicalize(name, " !\"#$&'(),/:;<=>?@[]\\^`{|}~", false, false, true, true));
      encodedQueryNamesAndValues.add(value != null ? 
        HttpUrl.canonicalize(value, " !\"#$&'(),/:;<=>?@[]\\^`{|}~", false, false, true, true) : 
        null);
      return this;
    }
    
    public Builder addEncodedQueryParameter(String encodedName, @Nullable String encodedValue)
    {
      if (encodedName == null) throw new NullPointerException("encodedName == null");
      if (encodedQueryNamesAndValues == null) encodedQueryNamesAndValues = new ArrayList();
      encodedQueryNamesAndValues.add(
        HttpUrl.canonicalize(encodedName, " \"'<>#&=", true, false, true, true));
      encodedQueryNamesAndValues.add(encodedValue != null ? 
        HttpUrl.canonicalize(encodedValue, " \"'<>#&=", true, false, true, true) : 
        null);
      return this;
    }
    
    public Builder setQueryParameter(String name, @Nullable String value) {
      removeAllQueryParameters(name);
      addQueryParameter(name, value);
      return this;
    }
    
    public Builder setEncodedQueryParameter(String encodedName, @Nullable String encodedValue) {
      removeAllEncodedQueryParameters(encodedName);
      addEncodedQueryParameter(encodedName, encodedValue);
      return this;
    }
    
    public Builder removeAllQueryParameters(String name) {
      if (name == null) throw new NullPointerException("name == null");
      if (encodedQueryNamesAndValues == null) return this;
      String nameToRemove = HttpUrl.canonicalize(name, " !\"#$&'(),/:;<=>?@[]\\^`{|}~", false, false, true, true);
      
      removeAllCanonicalQueryParameters(nameToRemove);
      return this;
    }
    
    public Builder removeAllEncodedQueryParameters(String encodedName) {
      if (encodedName == null) throw new NullPointerException("encodedName == null");
      if (encodedQueryNamesAndValues == null) return this;
      removeAllCanonicalQueryParameters(
        HttpUrl.canonicalize(encodedName, " \"'<>#&=", true, false, true, true));
      return this;
    }
    
    private void removeAllCanonicalQueryParameters(String canonicalName) {
      for (int i = encodedQueryNamesAndValues.size() - 2; i >= 0; i -= 2) {
        if (canonicalName.equals(encodedQueryNamesAndValues.get(i))) {
          encodedQueryNamesAndValues.remove(i + 1);
          encodedQueryNamesAndValues.remove(i);
          if (encodedQueryNamesAndValues.isEmpty()) {
            encodedQueryNamesAndValues = null;
            return;
          }
        }
      }
    }
    

    public Builder fragment(@Nullable String fragment)
    {
      encodedFragment = (fragment != null ? HttpUrl.canonicalize(fragment, "", false, false, false, false) : null);
      return this;
    }
    

    public Builder encodedFragment(@Nullable String encodedFragment)
    {
      this.encodedFragment = (encodedFragment != null ? HttpUrl.canonicalize(encodedFragment, "", true, false, false, false) : null);
      return this;
    }
    



    Builder reencodeForUri()
    {
      int i = 0; for (int size = encodedPathSegments.size(); i < size; i++) {
        String pathSegment = (String)encodedPathSegments.get(i);
        encodedPathSegments.set(i, 
          HttpUrl.canonicalize(pathSegment, "[]", true, true, false, true));
      }
      if (encodedQueryNamesAndValues != null) {
        int i = 0; for (int size = encodedQueryNamesAndValues.size(); i < size; i++) {
          String component = (String)encodedQueryNamesAndValues.get(i);
          if (component != null) {
            encodedQueryNamesAndValues.set(i, 
              HttpUrl.canonicalize(component, "\\^`{|}", true, true, true, true));
          }
        }
      }
      if (encodedFragment != null) {
        encodedFragment = HttpUrl.canonicalize(encodedFragment, " \"#<>\\^`{|}", true, true, false, false);
      }
      
      return this;
    }
    
    public HttpUrl build() {
      if (scheme == null) throw new IllegalStateException("scheme == null");
      if (host == null) throw new IllegalStateException("host == null");
      return new HttpUrl(this);
    }
    
    public String toString() {
      StringBuilder result = new StringBuilder();
      if (scheme != null) {
        result.append(scheme);
        result.append("://");
      } else {
        result.append("//");
      }
      
      if ((!encodedUsername.isEmpty()) || (!encodedPassword.isEmpty())) {
        result.append(encodedUsername);
        if (!encodedPassword.isEmpty()) {
          result.append(':');
          result.append(encodedPassword);
        }
        result.append('@');
      }
      
      if (host != null) {
        if (host.indexOf(':') != -1)
        {
          result.append('[');
          result.append(host);
          result.append(']');
        } else {
          result.append(host);
        }
      }
      
      if ((port != -1) || (scheme != null)) {
        int effectivePort = effectivePort();
        if ((scheme == null) || (effectivePort != HttpUrl.defaultPort(scheme))) {
          result.append(':');
          result.append(effectivePort);
        }
      }
      
      HttpUrl.pathSegmentsToString(result, encodedPathSegments);
      
      if (encodedQueryNamesAndValues != null) {
        result.append('?');
        HttpUrl.namesAndValuesToQueryString(result, encodedQueryNamesAndValues);
      }
      
      if (encodedFragment != null) {
        result.append('#');
        result.append(encodedFragment);
      }
      
      return result.toString();
    }
    

    Builder parse(@Nullable HttpUrl base, String input)
    {
      int pos = Util.skipLeadingAsciiWhitespace(input, 0, input.length());
      int limit = Util.skipTrailingAsciiWhitespace(input, pos, input.length());
      

      int schemeDelimiterOffset = schemeDelimiterOffset(input, pos, limit);
      if (schemeDelimiterOffset != -1) {
        if (input.regionMatches(true, pos, "https:", 0, 6)) {
          scheme = "https";
          pos += "https:".length();
        } else if (input.regionMatches(true, pos, "http:", 0, 5)) {
          scheme = "http";
          pos += "http:".length();
        }
        else {
          throw new IllegalArgumentException("Expected URL scheme 'http' or 'https' but was '" + input.substring(0, schemeDelimiterOffset) + "'");
        }
      } else if (base != null) {
        scheme = scheme;
      } else {
        throw new IllegalArgumentException("Expected URL scheme 'http' or 'https' but no colon was found");
      }
      


      boolean hasUsername = false;
      boolean hasPassword = false;
      int slashCount = slashCount(input, pos, limit);
      if ((slashCount >= 2) || (base == null) || (!scheme.equals(scheme)))
      {








        pos += slashCount;
        for (;;)
        {
          int componentDelimiterOffset = Util.delimiterOffset(input, pos, limit, "@/\\?#");
          

          int c = componentDelimiterOffset != limit ? input.charAt(componentDelimiterOffset) : -1;
          switch (c)
          {
          case 64: 
            if (!hasPassword) {
              int passwordColonOffset = Util.delimiterOffset(input, pos, componentDelimiterOffset, ':');
              
              String canonicalUsername = HttpUrl.canonicalize(input, pos, passwordColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true, null);
              


              encodedUsername = (hasUsername ? encodedUsername + "%40" + canonicalUsername : canonicalUsername);
              if (passwordColonOffset != componentDelimiterOffset) {
                hasPassword = true;
                encodedPassword = HttpUrl.canonicalize(input, passwordColonOffset + 1, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true, null);
              }
              

              hasUsername = true;
            } else {
              encodedPassword = (encodedPassword + "%40" + HttpUrl.canonicalize(input, pos, componentDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, true, null));
            }
            
            pos = componentDelimiterOffset + 1;
            break;
          

          case -1: 
          case 35: 
          case 47: 
          case 63: 
          case 92: 
            int portColonOffset = portColonOffset(input, pos, componentDelimiterOffset);
            if (portColonOffset + 1 < componentDelimiterOffset) {
              host = canonicalizeHost(input, pos, portColonOffset);
              port = parsePort(input, portColonOffset + 1, componentDelimiterOffset);
              if (port == -1)
              {
                throw new IllegalArgumentException("Invalid URL port: \"" + input.substring(portColonOffset + 1, componentDelimiterOffset) + '"');
              }
            } else {
              host = canonicalizeHost(input, pos, portColonOffset);
              port = HttpUrl.defaultPort(scheme);
            }
            if (host == null)
            {
              throw new IllegalArgumentException("Invalid URL host: \"" + input.substring(pos, portColonOffset) + '"');
            }
            pos = componentDelimiterOffset;
            break label705;
          }
          
        }
      }
      encodedUsername = base.encodedUsername();
      encodedPassword = base.encodedPassword();
      host = host;
      port = port;
      encodedPathSegments.clear();
      encodedPathSegments.addAll(base.encodedPathSegments());
      if ((pos == limit) || (input.charAt(pos) == '#')) {
        encodedQuery(base.encodedQuery());
      }
      
      label705:
      
      int pathDelimiterOffset = Util.delimiterOffset(input, pos, limit, "?#");
      resolvePath(input, pos, pathDelimiterOffset);
      pos = pathDelimiterOffset;
      

      if ((pos < limit) && (input.charAt(pos) == '?')) {
        int queryDelimiterOffset = Util.delimiterOffset(input, pos, limit, '#');
        encodedQueryNamesAndValues = HttpUrl.queryStringToNamesAndValues(HttpUrl.canonicalize(input, pos + 1, queryDelimiterOffset, " \"'<>#", true, false, true, true, null));
        
        pos = queryDelimiterOffset;
      }
      

      if ((pos < limit) && (input.charAt(pos) == '#')) {
        encodedFragment = HttpUrl.canonicalize(input, pos + 1, limit, "", true, false, false, false, null);
      }
      

      return this;
    }
    
    private void resolvePath(String input, int pos, int limit)
    {
      if (pos == limit)
      {
        return;
      }
      char c = input.charAt(pos);
      if ((c == '/') || (c == '\\'))
      {
        encodedPathSegments.clear();
        encodedPathSegments.add("");
        pos++;
      }
      else {
        encodedPathSegments.set(encodedPathSegments.size() - 1, "");
      }
      

      for (int i = pos; i < limit;) {
        int pathSegmentDelimiterOffset = Util.delimiterOffset(input, i, limit, "/\\");
        boolean segmentHasTrailingSlash = pathSegmentDelimiterOffset < limit;
        push(input, i, pathSegmentDelimiterOffset, segmentHasTrailingSlash, true);
        i = pathSegmentDelimiterOffset;
        if (segmentHasTrailingSlash) { i++;
        }
      }
    }
    
    private void push(String input, int pos, int limit, boolean addTrailingSlash, boolean alreadyEncoded)
    {
      String segment = HttpUrl.canonicalize(input, pos, limit, " \"<>^`{}|/\\?#", alreadyEncoded, false, false, true, null);
      
      if (isDot(segment)) {
        return;
      }
      if (isDotDot(segment)) {
        pop();
        return;
      }
      if (((String)encodedPathSegments.get(encodedPathSegments.size() - 1)).isEmpty()) {
        encodedPathSegments.set(encodedPathSegments.size() - 1, segment);
      } else {
        encodedPathSegments.add(segment);
      }
      if (addTrailingSlash) {
        encodedPathSegments.add("");
      }
    }
    
    private boolean isDot(String input) {
      return (input.equals(".")) || (input.equalsIgnoreCase("%2e"));
    }
    
    private boolean isDotDot(String input) {
      return (input.equals("..")) || 
        (input.equalsIgnoreCase("%2e.")) || 
        (input.equalsIgnoreCase(".%2e")) || 
        (input.equalsIgnoreCase("%2e%2e"));
    }
    


    @Nullable
    String encodedFragment;
    

    static final String INVALID_HOST = "Invalid URL host";
    

    private void pop()
    {
      String removed = (String)encodedPathSegments.remove(encodedPathSegments.size() - 1);
      

      if ((removed.isEmpty()) && (!encodedPathSegments.isEmpty())) {
        encodedPathSegments.set(encodedPathSegments.size() - 1, "");
      } else {
        encodedPathSegments.add("");
      }
    }
    



    private static int schemeDelimiterOffset(String input, int pos, int limit)
    {
      if (limit - pos < 2) { return -1;
      }
      char c0 = input.charAt(pos);
      if (((c0 < 'a') || (c0 > 'z')) && ((c0 < 'A') || (c0 > 'Z'))) { return -1;
      }
      for (int i = pos + 1; i < limit; i++) {
        char c = input.charAt(i);
        
        if (((c < 'a') || (c > 'z')) && ((c < 'A') || (c > 'Z')) && ((c < '0') || (c > '9')) && (c != '+') && (c != '-') && (c != '.'))
        {





          if (c == ':') {
            return i;
          }
          return -1;
        }
      }
      
      return -1;
    }
    
    private static int slashCount(String input, int pos, int limit)
    {
      int slashCount = 0;
      while (pos < limit) {
        char c = input.charAt(pos);
        if ((c != '\\') && (c != '/')) break;
        slashCount++;
        pos++;
      }
      


      return slashCount;
    }
    
    private static int portColonOffset(String input, int pos, int limit)
    {
      for (int i = pos; i < limit; i++) {
        switch (input.charAt(i)) {
        case '[':  do {
            i++; if (i >= limit) break;
          } while (input.charAt(i) != ']'); break;
        

        case ':': 
          return i;
        }
      }
      return limit;
    }
    
    @Nullable
    private static String canonicalizeHost(String input, int pos, int limit)
    {
      String percentDecoded = HttpUrl.percentDecode(input, pos, limit, false);
      return Util.canonicalizeHost(percentDecoded);
    }
    
    private static int parsePort(String input, int pos, int limit)
    {
      try {
        String portString = HttpUrl.canonicalize(input, pos, limit, "", false, false, false, true, null);
        int i = Integer.parseInt(portString);
        if ((i > 0) && (i <= 65535)) return i;
        return -1;
      } catch (NumberFormatException e) {}
      return -1;
    }
  }
  
  static String percentDecode(String encoded, boolean plusIsSpace)
  {
    return percentDecode(encoded, 0, encoded.length(), plusIsSpace);
  }
  
  private List<String> percentDecode(List<String> list, boolean plusIsSpace) {
    int size = list.size();
    List<String> result = new ArrayList(size);
    for (int i = 0; i < size; i++) {
      String s = (String)list.get(i);
      result.add(s != null ? percentDecode(s, plusIsSpace) : null);
    }
    return Collections.unmodifiableList(result);
  }
  
  static String percentDecode(String encoded, int pos, int limit, boolean plusIsSpace) {
    for (int i = pos; i < limit; i++) {
      char c = encoded.charAt(i);
      if ((c == '%') || ((c == '+') && (plusIsSpace)))
      {
        Buffer out = new Buffer();
        out.writeUtf8(encoded, pos, i);
        percentDecode(out, encoded, i, limit, plusIsSpace);
        return out.readUtf8();
      }
    }
    

    return encoded.substring(pos, limit);
  }
  
  static void percentDecode(Buffer out, String encoded, int pos, int limit, boolean plusIsSpace) {
    int codePoint;
    for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
      codePoint = encoded.codePointAt(i);
      if ((codePoint == 37) && (i + 2 < limit)) {
        int d1 = Util.decodeHexDigit(encoded.charAt(i + 1));
        int d2 = Util.decodeHexDigit(encoded.charAt(i + 2));
        if ((d1 != -1) && (d2 != -1)) {
          out.writeByte((d1 << 4) + d2);
          i += 2;
          continue;
        }
      } else if ((codePoint == 43) && (plusIsSpace)) {
        out.writeByte(32);
        continue;
      }
      out.writeUtf8CodePoint(codePoint);
    }
  }
  
  static boolean percentEncoded(String encoded, int pos, int limit) {
    return (pos + 2 < limit) && 
      (encoded.charAt(pos) == '%') && 
      (Util.decodeHexDigit(encoded.charAt(pos + 1)) != -1) && 
      (Util.decodeHexDigit(encoded.charAt(pos + 2)) != -1);
  }
  









  static String canonicalize(String input, int pos, int limit, String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly, @Nullable Charset charset)
  {
    int codePoint;
    








    for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
      codePoint = input.codePointAt(i);
      if ((codePoint < 32) || (codePoint == 127) || ((codePoint >= 128) && (asciiOnly)) || 
      

        (encodeSet.indexOf(codePoint) != -1) || ((codePoint == 37) && ((!alreadyEncoded) || ((strict) && 
        (!percentEncoded(input, i, limit))))) || ((codePoint == 43) && (plusIsSpace)))
      {

        Buffer out = new Buffer();
        out.writeUtf8(input, pos, i);
        canonicalize(out, input, i, limit, encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, charset);
        
        return out.readUtf8();
      }
    }
    

    return input.substring(pos, limit);
  }
  

  static void canonicalize(Buffer out, String input, int pos, int limit, String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly, @Nullable Charset charset)
  {
    Buffer encodedCharBuffer = null;
    int codePoint;
    for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
      codePoint = input.codePointAt(i);
      if ((!alreadyEncoded) || ((codePoint != 9) && (codePoint != 10) && (codePoint != 12) && (codePoint != 13)))
      {

        if ((codePoint == 43) && (plusIsSpace))
        {
          out.writeUtf8(alreadyEncoded ? "+" : "%2B");
        } else { if ((codePoint < 32) || (codePoint == 127) || ((codePoint >= 128) && (asciiOnly)) || 
          

            (encodeSet.indexOf(codePoint) != -1) || ((codePoint == 37) && ((!alreadyEncoded) || ((strict) && 
            (!percentEncoded(input, i, limit))))))
          {
            if (encodedCharBuffer == null) {
              encodedCharBuffer = new Buffer();
            }
            
            if ((charset == null) || (charset.equals(StandardCharsets.UTF_8))) {
              encodedCharBuffer.writeUtf8CodePoint(codePoint);
            } else {
              encodedCharBuffer.writeString(input, i, i + Character.charCount(codePoint), charset);
            }
          }
          while (!encodedCharBuffer.exhausted()) {
            int b = encodedCharBuffer.readByte() & 0xFF;
            out.writeByte(37);
            out.writeByte(HEX_DIGITS[(b >> 4 & 0xF)]);
            out.writeByte(HEX_DIGITS[(b & 0xF)]);
            continue;
            

            out.writeUtf8CodePoint(codePoint);
          }
        } }
    }
  }
  
  static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly, @Nullable Charset charset) {
    return canonicalize(input, 0, input.length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, charset);
  }
  

  static String canonicalize(String input, String encodeSet, boolean alreadyEncoded, boolean strict, boolean plusIsSpace, boolean asciiOnly)
  {
    return canonicalize(input, 0, input
      .length(), encodeSet, alreadyEncoded, strict, plusIsSpace, asciiOnly, null);
  }
}
