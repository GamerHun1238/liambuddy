package net.dv8tion.jda.api.utils;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.utils.Checks;






































public class MarkdownSanitizer
{
  public static final int NORMAL = 0;
  public static final int BOLD = 1;
  public static final int ITALICS_U = 2;
  public static final int ITALICS_A = 4;
  public static final int MONO = 8;
  public static final int MONO_TWO = 16;
  public static final int BLOCK = 32;
  public static final int SPOILER = 64;
  public static final int UNDERLINE = 128;
  public static final int STRIKE = 256;
  public static final int QUOTE = 512;
  public static final int QUOTE_BLOCK = 1024;
  private static final int ESCAPED_BOLD = -2147483647;
  private static final int ESCAPED_ITALICS_U = -2147483646;
  private static final int ESCAPED_ITALICS_A = -2147483644;
  private static final int ESCAPED_MONO = -2147483640;
  private static final int ESCAPED_MONO_TWO = -2147483632;
  private static final int ESCAPED_BLOCK = -2147483616;
  private static final int ESCAPED_SPOILER = -2147483584;
  private static final int ESCAPED_UNDERLINE = -2147483520;
  private static final int ESCAPED_STRIKE = -2147483392;
  private static final int ESCAPED_QUOTE = -2147483136;
  private static final int ESCAPED_QUOTE_BLOCK = -2147482624;
  private static final Pattern codeLanguage = Pattern.compile("^\\w+\n.*", 40);
  private static final Pattern quote = Pattern.compile("> +.*", 40);
  private static final Pattern quoteBlock = Pattern.compile(">>>\\s+\\S.*", 40);
  




  private static final TIntObjectMap<String> tokens = new TIntObjectHashMap();
  static { tokens.put(0, "");
    tokens.put(1, "**");
    tokens.put(2, "_");
    tokens.put(4, "*");
    tokens.put(5, "***");
    tokens.put(8, "`");
    tokens.put(16, "``");
    tokens.put(32, "```");
    tokens.put(64, "||");
    tokens.put(128, "__");
    tokens.put(256, "~~");
  }
  



  public MarkdownSanitizer()
  {
    ignored = 0;
    strategy = SanitizationStrategy.REMOVE;
  }
  
  public MarkdownSanitizer(int ignored, @Nullable SanitizationStrategy strategy)
  {
    this.ignored = ignored;
    this.strategy = (strategy == null ? SanitizationStrategy.REMOVE : strategy);
  }
  









  @Nonnull
  public static String sanitize(@Nonnull String sequence)
  {
    return sanitize(sequence, SanitizationStrategy.REMOVE);
  }
  





  private int ignored;
  




  private SanitizationStrategy strategy;
  



  @Nonnull
  public static String sanitize(@Nonnull String sequence, @Nonnull SanitizationStrategy strategy)
  {
    Checks.notNull(sequence, "String");
    Checks.notNull(strategy, "Strategy");
    return new MarkdownSanitizer().withStrategy(strategy).compute(sequence);
  }
  













  @Nonnull
  public static String escape(@Nonnull String sequence)
  {
    return escape(sequence, 0);
  }
  














  @Nonnull
  public static String escape(@Nonnull String sequence, int ignored)
  {
    return 
    

      new MarkdownSanitizer().withIgnored(ignored).withStrategy(SanitizationStrategy.ESCAPE).compute(sequence);
  }
  











  @Nonnull
  public MarkdownSanitizer withStrategy(@Nonnull SanitizationStrategy strategy)
  {
    Checks.notNull(strategy, "Strategy");
    this.strategy = strategy;
    return this;
  }
  









  @Nonnull
  public MarkdownSanitizer withIgnored(int ignored)
  {
    this.ignored |= ignored;
    return this;
  }
  
  private int getRegion(int index, @Nonnull String sequence)
  {
    if (sequence.length() - index >= 3)
    {
      String threeChars = sequence.substring(index, index + 3);
      switch (threeChars)
      {
      case "```": 
        return doesEscape(index, sequence) ? -2147483616 : 32;
      case "***": 
        return doesEscape(index, sequence) ? -2147483643 : 5;
      }
    }
    if (sequence.length() - index >= 2)
    {
      String twoChars = sequence.substring(index, index + 2);
      switch (twoChars)
      {
      case "**": 
        return doesEscape(index, sequence) ? -2147483647 : 1;
      case "__": 
        return doesEscape(index, sequence) ? -2147483520 : 128;
      case "~~": 
        return doesEscape(index, sequence) ? -2147483392 : 256;
      case "``": 
        return doesEscape(index, sequence) ? -2147483632 : 16;
      case "||": 
        return doesEscape(index, sequence) ? -2147483584 : 64;
      }
    }
    char current = sequence.charAt(index);
    switch (current)
    {
    case '*': 
      return doesEscape(index, sequence) ? -2147483644 : 4;
    case '_': 
      return doesEscape(index, sequence) ? -2147483646 : 2;
    case '`': 
      return doesEscape(index, sequence) ? -2147483640 : 8;
    }
    return 0;
  }
  
  private boolean hasCollision(int index, @Nonnull String sequence, char c)
  {
    if (index < 0)
      return false;
    return (index < sequence.length() - 1) && (sequence.charAt(index + 1) == c);
  }
  
  private int findEndIndex(int afterIndex, int region, @Nonnull String sequence)
  {
    if (isEscape(region))
      return -1;
    int lastMatch = afterIndex + getDelta(region) + 1;
    while (lastMatch != -1)
    {
      switch (region)
      {
      case 5: 
        lastMatch = sequence.indexOf("***", lastMatch);
        break;
      case 1: 
        lastMatch = sequence.indexOf("**", lastMatch);
        if ((lastMatch != -1) && (hasCollision(lastMatch + 1, sequence, '*')))
        {
          lastMatch += 3; }
        break;
      

      case 4: 
        lastMatch = sequence.indexOf('*', lastMatch);
        if ((lastMatch != -1) && (hasCollision(lastMatch, sequence, '*')))
        {
          if (hasCollision(lastMatch + 1, sequence, '*')) {
            lastMatch += 3; continue;
          }
          lastMatch += 2; }
        break;
      

      case 128: 
        lastMatch = sequence.indexOf("__", lastMatch);
        break;
      case 2: 
        lastMatch = sequence.indexOf('_', lastMatch);
        if ((lastMatch != -1) && (hasCollision(lastMatch, sequence, '_')))
        {
          lastMatch += 2; }
        break;
      

      case 64: 
        lastMatch = sequence.indexOf("||", lastMatch);
        break;
      case 32: 
        lastMatch = sequence.indexOf("```", lastMatch);
        break;
      case 16: 
        lastMatch = sequence.indexOf("``", lastMatch);
        if ((lastMatch != -1) && (hasCollision(lastMatch + 1, sequence, '`')))
        {
          lastMatch += 3; }
        break;
      

      case 8: 
        lastMatch = sequence.indexOf('`', lastMatch);
        if ((lastMatch != -1) && (hasCollision(lastMatch, sequence, '`')))
        {
          if (hasCollision(lastMatch + 1, sequence, '`')) {
            lastMatch += 3; continue;
          }
          lastMatch += 2; }
        break;
      

      case 256: 
        lastMatch = sequence.indexOf("~~", lastMatch);
        break;
      default: 
        return -1;
        
        if ((lastMatch == -1) || (!doesEscape(lastMatch, sequence)))
          return lastMatch;
        lastMatch++;
      } }
    return -1;
  }
  
  @Nonnull
  private String handleRegion(int start, int end, @Nonnull String sequence, int region)
  {
    String resolved = sequence.substring(start, end);
    switch (region)
    {
    case 8: 
    case 16: 
    case 32: 
      return resolved;
    }
    return new MarkdownSanitizer(ignored, strategy).compute(resolved);
  }
  

  private int getDelta(int region)
  {
    switch (region)
    {
    case -2147483643: 
    case -2147483616: 
    case 5: 
    case 32: 
      return 3;
    case -2147483647: 
    case -2147483632: 
    case -2147483584: 
    case -2147483520: 
    case -2147483392: 
    case 1: 
    case 16: 
    case 64: 
    case 128: 
    case 256: 
      return 2;
    case 8: 
    case -2147483646: 
    case -2147483644: 
    case -2147483640: 
    case -2147483136: 
    case 2: 
    case 4: 
      return 1;
    }
    return 0;
  }
  

  private void applyStrategy(int region, @Nonnull String seq, @Nonnull StringBuilder builder)
  {
    if (strategy == SanitizationStrategy.REMOVE)
    {
      if (codeLanguage.matcher(seq).matches()) {
        builder.append(seq.substring(seq.indexOf("\n") + 1));
      } else
        builder.append(seq);
      return;
    }
    String token = (String)tokens.get(region);
    if (token == null)
      throw new IllegalStateException("Found illegal region for strategy ESCAPE '" + region + "' with no known format token!");
    if (region == 128) {
      token = "_\\_";
    } else if (region == 1) {
      token = "*\\*";
    } else if (region == 5) {
      token = "*\\*\\*";
    }
    
    builder.append("\\").append(token).append(seq).append("\\").append(token);
  }
  
  private boolean doesEscape(int index, @Nonnull String seq)
  {
    int backslashes = 0;
    for (int i = index - 1; i > -1; i--)
    {
      if (seq.charAt(i) != '\\')
        break;
      backslashes++;
    }
    return backslashes % 2 != 0;
  }
  
  private boolean isEscape(int region)
  {
    return (0x80000000 & region) != 0;
  }
  
  private boolean isIgnored(int nextRegion)
  {
    return (nextRegion & ignored) == nextRegion;
  }
  













  @Nonnull
  public String compute(@Nonnull String sequence)
  {
    Checks.notNull(sequence, "Input");
    StringBuilder builder = new StringBuilder();
    String end = handleQuote(sequence, false);
    if (end != null) { return end;
    }
    for (int i = 0; i < sequence.length();)
    {
      int nextRegion = getRegion(i, sequence);
      if (nextRegion == 0)
      {
        if ((sequence.charAt(i) == '\n') && (i + 1 < sequence.length()))
        {
          String result = handleQuote(sequence.substring(i + 1), true);
          if (result != null) {
            return result;
          }
        }
        builder.append(sequence.charAt(i++));
      }
      else
      {
        int endRegion = findEndIndex(i, nextRegion, sequence);
        if ((isIgnored(nextRegion)) || (endRegion == -1))
        {
          int delta = getDelta(nextRegion);
          for (int j = 0; j < delta; j++) {
            builder.append(sequence.charAt(i++));
          }
        } else {
          int delta = getDelta(nextRegion);
          applyStrategy(nextRegion, handleRegion(i + delta, endRegion, sequence, nextRegion), builder);
          i = endRegion + delta;
        } } }
    return builder.toString();
  }
  

  private String handleQuote(@Nonnull String sequence, boolean newline)
  {
    if ((!isIgnored(512)) && (quote.matcher(sequence).matches()))
    {
      int start = sequence.indexOf('>');
      if (start < 0)
        start = 0;
      StringBuilder builder = new StringBuilder(compute(sequence.substring(start + 2)));
      if (strategy == SanitizationStrategy.ESCAPE)
        builder.insert(0, "\\> ");
      if (newline)
        builder.insert(0, '\n');
      return builder.toString();
    }
    
    if ((!isIgnored(1024)) && (quoteBlock.matcher(sequence).matches()))
    {
      if (strategy == SanitizationStrategy.ESCAPE)
        return compute("\\".concat(sequence));
      return compute(sequence.substring(4));
    }
    return null;
  }
  




  public static enum SanitizationStrategy
  {
    REMOVE, 
    




    ESCAPE;
    
    private SanitizationStrategy() {}
  }
}
