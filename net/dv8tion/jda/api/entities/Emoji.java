package net.dv8tion.jda.api.entities;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EncodingUtil;

























public class Emoji
  implements SerializableData, IMentionable
{
  private final String name;
  private final long id;
  private final boolean animated;
  
  private Emoji(String name, long id, boolean animated)
  {
    this.name = name;
    this.id = id;
    this.animated = animated;
  }
  






  @Nonnull
  public String getName()
  {
    return name;
  }
  

  public long getIdLong()
  {
    return id;
  }
  





  public boolean isAnimated()
  {
    return animated;
  }
  







  public boolean isUnicode()
  {
    return id == 0L;
  }
  





  public boolean isCustom()
  {
    return !isUnicode();
  }
  












  @Nonnull
  public static Emoji fromUnicode(@Nonnull String code)
  {
    Checks.notEmpty(code, "Unicode");
    if ((code.startsWith("U+")) || (code.startsWith("u+")))
    {
      StringBuilder emoji = new StringBuilder();
      String[] codepoints = code.trim().split("\\s*[uU]\\+");
      for (String codepoint : codepoints)
        emoji.append(codepoint.isEmpty() ? "" : EncodingUtil.decodeCodepoint("U+" + codepoint));
      code = emoji.toString();
    }
    return new Emoji(code, 0L, false);
  }
  















  @Nonnull
  public static Emoji fromEmote(@Nonnull String name, long id, boolean animated)
  {
    Checks.notEmpty(name, "Name");
    return new Emoji(name, id, animated);
  }
  











  @Nonnull
  public static Emoji fromEmote(@Nonnull Emote emote)
  {
    Checks.notNull(emote, "Emote");
    return fromEmote(emote.getName(), emote.getIdLong(), emote.isAnimated());
  }
  
























  @Nonnull
  public static Emoji fromMarkdown(@Nonnull String code)
  {
    Matcher matcher = Message.MentionType.EMOTE.getPattern().matcher(code);
    if (matcher.matches()) {
      return fromEmote(matcher.group(1), Long.parseUnsignedLong(matcher.group(2)), code.startsWith("<a"));
    }
    return fromUnicode(code);
  }
  










  @Nonnull
  public static Emoji fromData(@Nonnull DataObject emoji)
  {
    return new Emoji(emoji.getString("name"), emoji
      .getUnsignedLong("id", 0L), emoji
      .getBoolean("animated"));
  }
  

  @Nonnull
  public DataObject toData()
  {
    DataObject json = DataObject.empty().put("name", name);
    if (id != 0L)
    {

      json.put("id", Long.valueOf(id)).put("animated", Boolean.valueOf(animated));
    }
    return json;
  }
  

  @Nonnull
  public String getAsMention()
  {
    return id == 0L ? name : String.format("<%s:%s:%s>", new Object[] { animated ? "a" : "", name, Long.toUnsignedString(id) });
  }
  

  public int hashCode()
  {
    return Objects.hash(new Object[] { name, Long.valueOf(id), Boolean.valueOf(animated) });
  }
  

  public boolean equals(Object obj)
  {
    if (obj == this) return true;
    if (!(obj instanceof Emoji)) return false;
    Emoji other = (Emoji)obj;
    return (id == id) && (animated == animated) && (Objects.equals(name, name));
  }
  

  public String toString()
  {
    return "E:" + name + "(" + id + ")";
  }
}
