package okhttp3;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;



















public final class MediaType
{
  private static final String TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)";
  private static final String QUOTED = "\"([^\"]*)\"";
  private static final Pattern TYPE_SUBTYPE = Pattern.compile("([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)/([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)");
  private static final Pattern PARAMETER = Pattern.compile(";\\s*(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)=(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)|\"([^\"]*)\"))?");
  private final String mediaType;
  private final String type;
  private final String subtype;
  @Nullable
  private final String charset;
  
  private MediaType(String mediaType, String type, String subtype, @Nullable String charset)
  {
    this.mediaType = mediaType;
    this.type = type;
    this.subtype = subtype;
    this.charset = charset;
  }
  




  public static MediaType get(String string)
  {
    Matcher typeSubtype = TYPE_SUBTYPE.matcher(string);
    if (!typeSubtype.lookingAt()) {
      throw new IllegalArgumentException("No subtype found for: \"" + string + '"');
    }
    String type = typeSubtype.group(1).toLowerCase(Locale.US);
    String subtype = typeSubtype.group(2).toLowerCase(Locale.US);
    
    String charset = null;
    Matcher parameter = PARAMETER.matcher(string);
    for (int s = typeSubtype.end(); s < string.length(); s = parameter.end()) {
      parameter.region(s, string.length());
      if (!parameter.lookingAt())
      {
        throw new IllegalArgumentException("Parameter is not formatted correctly: \"" + string.substring(s) + "\" for: \"" + string + '"');
      }
      



      String name = parameter.group(1);
      if ((name != null) && (name.equalsIgnoreCase("charset")))
      {
        String token = parameter.group(2);
        String charsetParameter; String charsetParameter; if (token != null)
        {


          charsetParameter = (token.startsWith("'")) && (token.endsWith("'")) && (token.length() > 2) ? token.substring(1, token.length() - 1) : token;
        }
        else {
          charsetParameter = parameter.group(3);
        }
        if ((charset != null) && (!charsetParameter.equalsIgnoreCase(charset))) {
          throw new IllegalArgumentException("Multiple charsets defined: \"" + charset + "\" and: \"" + charsetParameter + "\" for: \"" + string + '"');
        }
        





        charset = charsetParameter;
      }
    }
    return new MediaType(string, type, subtype, charset);
  }
  

  @Nullable
  public static MediaType parse(String string)
  {
    try
    {
      return get(string);
    } catch (IllegalArgumentException ignored) {}
    return null;
  }
  




  public String type()
  {
    return type;
  }
  


  public String subtype()
  {
    return subtype;
  }
  

  @Nullable
  public Charset charset()
  {
    return charset(null);
  }
  

  @Nullable
  public Charset charset(@Nullable Charset defaultValue)
  {
    try
    {
      return charset != null ? Charset.forName(charset) : defaultValue;
    } catch (IllegalArgumentException e) {}
    return defaultValue;
  }
  




  public String toString()
  {
    return mediaType;
  }
  
  public boolean equals(@Nullable Object other) {
    return ((other instanceof MediaType)) && (mediaType.equals(mediaType));
  }
  
  public int hashCode() {
    return mediaType.hashCode();
  }
}
