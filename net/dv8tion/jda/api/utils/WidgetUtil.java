package net.dv8tion.jda.api.utils;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;





























public class WidgetUtil
{
  public static final String WIDGET_PNG = Requester.DISCORD_API_PREFIX + "guilds/%s/widget.png?style=%s";
  public static final String WIDGET_URL = Requester.DISCORD_API_PREFIX + "guilds/%s/widget.json";
  



  public static final String WIDGET_HTML = "<iframe src=\"https://discord.com/widget?id=%s&theme=%s\" width=\"%d\" height=\"%d\" allowtransparency=\"true\" frameborder=\"0\"></iframe>";
  



  public WidgetUtil() {}
  



  @Nonnull
  public static String getWidgetBanner(@Nonnull Guild guild, @Nonnull BannerType type)
  {
    Checks.notNull(guild, "Guild");
    return getWidgetBanner(guild.getId(), type);
  }
  













  @Nonnull
  public static String getWidgetBanner(@Nonnull String guildId, @Nonnull BannerType type)
  {
    Checks.notNull(guildId, "GuildId");
    Checks.notNull(type, "BannerType");
    return String.format(WIDGET_PNG, new Object[] { guildId, type.name().toLowerCase() });
  }
  
















  @Nonnull
  public static String getPremadeWidgetHtml(@Nonnull Guild guild, @Nonnull WidgetTheme theme, int width, int height)
  {
    Checks.notNull(guild, "Guild");
    return getPremadeWidgetHtml(guild.getId(), theme, width, height);
  }
  

















  @Nonnull
  public static String getPremadeWidgetHtml(@Nonnull String guildId, @Nonnull WidgetTheme theme, int width, int height)
  {
    Checks.notNull(guildId, "GuildId");
    Checks.notNull(theme, "WidgetTheme");
    Checks.notNegative(width, "Width");
    Checks.notNegative(height, "Height");
    return Helpers.format("<iframe src=\"https://discord.com/widget?id=%s&theme=%s\" width=\"%d\" height=\"%d\" allowtransparency=\"true\" frameborder=\"0\"></iframe>", new Object[] { guildId, theme.name().toLowerCase(), Integer.valueOf(width), Integer.valueOf(height) });
  }
  
























  @Nullable
  public static Widget getWidget(@Nonnull String guildId)
    throws RateLimitedException
  {
    return getWidget(MiscUtil.parseSnowflake(guildId));
  }
  
  /* Error */
  @Nullable
  public static Widget getWidget(long guildId)
    throws RateLimitedException
  {
    // Byte code:
    //   0: lload_0
    //   1: invokestatic 25	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   4: ldc 6
    //   6: invokestatic 3	net/dv8tion/jda/internal/utils/Checks:notNull	(Ljava/lang/Object;Ljava/lang/String;)V
    //   9: new 26	okhttp3/OkHttpClient$Builder
    //   12: dup
    //   13: invokespecial 27	okhttp3/OkHttpClient$Builder:<init>	()V
    //   16: invokevirtual 28	okhttp3/OkHttpClient$Builder:build	()Lokhttp3/OkHttpClient;
    //   19: astore_3
    //   20: new 29	okhttp3/Request$Builder
    //   23: dup
    //   24: invokespecial 30	okhttp3/Request$Builder:<init>	()V
    //   27: getstatic 31	net/dv8tion/jda/api/utils/WidgetUtil:WIDGET_URL	Ljava/lang/String;
    //   30: iconst_1
    //   31: anewarray 9	java/lang/Object
    //   34: dup
    //   35: iconst_0
    //   36: lload_0
    //   37: invokestatic 25	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   40: aastore
    //   41: invokestatic 12	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   44: invokevirtual 32	okhttp3/Request$Builder:url	(Ljava/lang/String;)Lokhttp3/Request$Builder;
    //   47: ldc 33
    //   49: aconst_null
    //   50: invokevirtual 34	okhttp3/Request$Builder:method	(Ljava/lang/String;Lokhttp3/RequestBody;)Lokhttp3/Request$Builder;
    //   53: ldc 35
    //   55: getstatic 36	net/dv8tion/jda/internal/requests/Requester:USER_AGENT	Ljava/lang/String;
    //   58: invokevirtual 37	okhttp3/Request$Builder:header	(Ljava/lang/String;Ljava/lang/String;)Lokhttp3/Request$Builder;
    //   61: ldc 38
    //   63: ldc 39
    //   65: invokevirtual 37	okhttp3/Request$Builder:header	(Ljava/lang/String;Ljava/lang/String;)Lokhttp3/Request$Builder;
    //   68: invokevirtual 40	okhttp3/Request$Builder:build	()Lokhttp3/Request;
    //   71: astore 4
    //   73: aload_3
    //   74: aload 4
    //   76: invokevirtual 41	okhttp3/OkHttpClient:newCall	(Lokhttp3/Request;)Lokhttp3/Call;
    //   79: invokeinterface 42 1 0
    //   84: astore 5
    //   86: aload 5
    //   88: invokevirtual 43	okhttp3/Response:code	()I
    //   91: istore 6
    //   93: aload 5
    //   95: invokestatic 44	net/dv8tion/jda/internal/utils/IOUtil:getBody	(Lokhttp3/Response;)Ljava/io/InputStream;
    //   98: astore 7
    //   100: iload 6
    //   102: lookupswitch	default:+248->350, 200:+50->152, 400:+131->233, 403:+147->249, 404:+131->233, 429:+171->273
    //   152: aload 7
    //   154: astore 8
    //   156: new 45	net/dv8tion/jda/api/utils/WidgetUtil$Widget
    //   159: dup
    //   160: aload 8
    //   162: invokestatic 46	net/dv8tion/jda/api/utils/data/DataObject:fromJson	(Ljava/io/InputStream;)Lnet/dv8tion/jda/api/utils/data/DataObject;
    //   165: aconst_null
    //   166: invokespecial 47	net/dv8tion/jda/api/utils/WidgetUtil$Widget:<init>	(Lnet/dv8tion/jda/api/utils/data/DataObject;Lnet/dv8tion/jda/api/utils/WidgetUtil$1;)V
    //   169: astore 9
    //   171: aload 8
    //   173: ifnull +8 -> 181
    //   176: aload 8
    //   178: invokevirtual 48	java/io/InputStream:close	()V
    //   181: aload 5
    //   183: ifnull +8 -> 191
    //   186: aload 5
    //   188: invokevirtual 49	okhttp3/Response:close	()V
    //   191: aload 9
    //   193: areturn
    //   194: astore 9
    //   196: aload 8
    //   198: ifnull +20 -> 218
    //   201: aload 8
    //   203: invokevirtual 48	java/io/InputStream:close	()V
    //   206: goto +12 -> 218
    //   209: astore 10
    //   211: aload 9
    //   213: aload 10
    //   215: invokevirtual 51	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   218: aload 9
    //   220: athrow
    //   221: astore 8
    //   223: new 53	java/io/UncheckedIOException
    //   226: dup
    //   227: aload 8
    //   229: invokespecial 54	java/io/UncheckedIOException:<init>	(Ljava/io/IOException;)V
    //   232: athrow
    //   233: aconst_null
    //   234: astore 8
    //   236: aload 5
    //   238: ifnull +8 -> 246
    //   241: aload 5
    //   243: invokevirtual 49	okhttp3/Response:close	()V
    //   246: aload 8
    //   248: areturn
    //   249: new 45	net/dv8tion/jda/api/utils/WidgetUtil$Widget
    //   252: dup
    //   253: lload_0
    //   254: aconst_null
    //   255: invokespecial 55	net/dv8tion/jda/api/utils/WidgetUtil$Widget:<init>	(JLnet/dv8tion/jda/api/utils/WidgetUtil$1;)V
    //   258: astore 8
    //   260: aload 5
    //   262: ifnull +8 -> 270
    //   265: aload 5
    //   267: invokevirtual 49	okhttp3/Response:close	()V
    //   270: aload 8
    //   272: areturn
    //   273: aload 7
    //   275: astore 10
    //   277: aload 10
    //   279: invokestatic 46	net/dv8tion/jda/api/utils/data/DataObject:fromJson	(Ljava/io/InputStream;)Lnet/dv8tion/jda/api/utils/data/DataObject;
    //   282: ldc 56
    //   284: invokevirtual 57	net/dv8tion/jda/api/utils/data/DataObject:getLong	(Ljava/lang/String;)J
    //   287: lstore 8
    //   289: aload 10
    //   291: ifnull +38 -> 329
    //   294: aload 10
    //   296: invokevirtual 48	java/io/InputStream:close	()V
    //   299: goto +30 -> 329
    //   302: astore 11
    //   304: aload 10
    //   306: ifnull +20 -> 326
    //   309: aload 10
    //   311: invokevirtual 48	java/io/InputStream:close	()V
    //   314: goto +12 -> 326
    //   317: astore 12
    //   319: aload 11
    //   321: aload 12
    //   323: invokevirtual 51	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   326: aload 11
    //   328: athrow
    //   329: goto +8 -> 337
    //   332: astore 10
    //   334: lconst_0
    //   335: lstore 8
    //   337: new 59	net/dv8tion/jda/api/exceptions/RateLimitedException
    //   340: dup
    //   341: getstatic 31	net/dv8tion/jda/api/utils/WidgetUtil:WIDGET_URL	Ljava/lang/String;
    //   344: lload 8
    //   346: invokespecial 60	net/dv8tion/jda/api/exceptions/RateLimitedException:<init>	(Ljava/lang/String;J)V
    //   349: athrow
    //   350: new 61	java/lang/IllegalStateException
    //   353: dup
    //   354: new 62	java/lang/StringBuilder
    //   357: dup
    //   358: invokespecial 63	java/lang/StringBuilder:<init>	()V
    //   361: ldc 64
    //   363: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   366: iload 6
    //   368: invokevirtual 66	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   371: ldc 67
    //   373: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   376: aload 5
    //   378: invokevirtual 68	okhttp3/Response:message	()Ljava/lang/String;
    //   381: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   384: invokevirtual 69	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   387: invokespecial 70	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   390: athrow
    //   391: astore 6
    //   393: aload 5
    //   395: ifnull +20 -> 415
    //   398: aload 5
    //   400: invokevirtual 49	okhttp3/Response:close	()V
    //   403: goto +12 -> 415
    //   406: astore 7
    //   408: aload 6
    //   410: aload 7
    //   412: invokevirtual 51	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   415: aload 6
    //   417: athrow
    //   418: astore 5
    //   420: new 53	java/io/UncheckedIOException
    //   423: dup
    //   424: aload 5
    //   426: invokespecial 54	java/io/UncheckedIOException:<init>	(Ljava/io/IOException;)V
    //   429: athrow
    // Line number table:
    //   Java source line #206	-> byte code offset #0
    //   Java source line #209	-> byte code offset #9
    //   Java source line #210	-> byte code offset #20
    //   Java source line #211	-> byte code offset #37
    //   Java source line #212	-> byte code offset #50
    //   Java source line #213	-> byte code offset #58
    //   Java source line #214	-> byte code offset #65
    //   Java source line #215	-> byte code offset #68
    //   Java source line #217	-> byte code offset #73
    //   Java source line #219	-> byte code offset #86
    //   Java source line #220	-> byte code offset #93
    //   Java source line #222	-> byte code offset #100
    //   Java source line #226	-> byte code offset #152
    //   Java source line #228	-> byte code offset #156
    //   Java source line #229	-> byte code offset #171
    //   Java source line #256	-> byte code offset #181
    //   Java source line #228	-> byte code offset #191
    //   Java source line #226	-> byte code offset #194
    //   Java source line #230	-> byte code offset #221
    //   Java source line #232	-> byte code offset #223
    //   Java source line #237	-> byte code offset #233
    //   Java source line #256	-> byte code offset #236
    //   Java source line #237	-> byte code offset #246
    //   Java source line #239	-> byte code offset #249
    //   Java source line #256	-> byte code offset #260
    //   Java source line #239	-> byte code offset #270
    //   Java source line #243	-> byte code offset #273
    //   Java source line #245	-> byte code offset #277
    //   Java source line #246	-> byte code offset #289
    //   Java source line #243	-> byte code offset #302
    //   Java source line #250	-> byte code offset #329
    //   Java source line #247	-> byte code offset #332
    //   Java source line #249	-> byte code offset #334
    //   Java source line #251	-> byte code offset #337
    //   Java source line #254	-> byte code offset #350
    //   Java source line #217	-> byte code offset #391
    //   Java source line #257	-> byte code offset #418
    //   Java source line #259	-> byte code offset #420
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	430	0	guildId	long
    //   19	55	3	client	okhttp3.OkHttpClient
    //   71	4	4	request	okhttp3.Request
    //   84	315	5	response	okhttp3.Response
    //   418	7	5	e	java.io.IOException
    //   91	276	6	code	int
    //   391	25	6	localThrowable4	Throwable
    //   98	176	7	data	java.io.InputStream
    //   406	5	7	localThrowable5	Throwable
    //   154	48	8	stream	java.io.InputStream
    //   221	50	8	e	java.io.IOException
    //   287	3	8	retryAfter	long
    //   329	1	8	retryAfter	long
    //   335	10	8	retryAfter	long
    //   169	23	9	localWidget	Widget
    //   194	25	9	localThrowable	Throwable
    //   194	25	9	localThrowable6	Throwable
    //   209	5	10	localThrowable1	Throwable
    //   275	35	10	stream	java.io.InputStream
    //   332	3	10	e	Exception
    //   302	25	11	localThrowable2	Throwable
    //   317	5	12	localThrowable3	Throwable
    // Exception table:
    //   from	to	target	type
    //   156	171	194	java/lang/Throwable
    //   201	206	209	java/lang/Throwable
    //   152	181	221	java/io/IOException
    //   194	221	221	java/io/IOException
    //   277	289	302	java/lang/Throwable
    //   309	314	317	java/lang/Throwable
    //   273	329	332	java/lang/Exception
    //   86	181	391	java/lang/Throwable
    //   194	236	391	java/lang/Throwable
    //   249	260	391	java/lang/Throwable
    //   273	391	391	java/lang/Throwable
    //   398	403	406	java/lang/Throwable
    //   73	191	418	java/io/IOException
    //   194	246	418	java/io/IOException
    //   249	270	418	java/io/IOException
    //   273	418	418	java/io/IOException
  }
  
  public static enum BannerType
  {
    SHIELD,  BANNER1,  BANNER2,  BANNER3,  BANNER4;
    

    private BannerType() {}
  }
  

  public static enum WidgetTheme
  {
    LIGHT,  DARK;
    
    private WidgetTheme() {}
  }
  
  public static class Widget
    implements ISnowflake
  {
    private final boolean isAvailable;
    private final long id;
    private final String name;
    private final String invite;
    private final TLongObjectMap<VoiceChannel> channels;
    private final TLongObjectMap<Member> members;
    
    private Widget(long guildId)
    {
      isAvailable = false;
      id = guildId;
      name = null;
      invite = null;
      channels = new TLongObjectHashMap();
      members = new TLongObjectHashMap();
    }
    






    private Widget(@Nonnull DataObject json)
    {
      String inviteCode = json.getString("instant_invite", null);
      if (inviteCode != null) {
        inviteCode = inviteCode.substring(inviteCode.lastIndexOf("/") + 1);
      }
      isAvailable = true;
      id = json.getLong("id");
      name = json.getString("name");
      invite = inviteCode;
      channels = MiscUtil.newLongMap();
      members = MiscUtil.newLongMap();
      
      DataArray channelsJson = json.getArray("channels");
      for (int i = 0; i < channelsJson.length(); i++)
      {
        DataObject channel = channelsJson.getObject(i);
        channels.put(channel.getLong("id"), new VoiceChannel(channel, this, null));
      }
      
      DataArray membersJson = json.getArray("members");
      for (int i = 0; i < membersJson.length(); i++)
      {
        DataObject memberJson = membersJson.getObject(i);
        Member member = new Member(memberJson, this, null);
        if (!memberJson.isNull("channel_id"))
        {
          VoiceChannel channel = (VoiceChannel)channels.get(memberJson.getLong("channel_id"));
          member.setVoiceState(new VoiceState(channel, memberJson
            .getBoolean("mute"), memberJson
            .getBoolean("deaf"), memberJson
            .getBoolean("suppress"), memberJson
            .getBoolean("self_mute"), memberJson
            .getBoolean("self_deaf"), member, this, null));
          

          channel.addMember(member);
        }
        members.put(member.getIdLong(), member);
      }
    }
    






    public boolean isAvailable()
    {
      return isAvailable;
    }
    

    public long getIdLong()
    {
      return id;
    }
    








    @Nonnull
    public String getName()
    {
      checkAvailable();
      
      return name;
    }
    









    @Nullable
    public String getInviteCode()
    {
      checkAvailable();
      
      return invite;
    }
    








    @Nonnull
    public List<VoiceChannel> getVoiceChannels()
    {
      checkAvailable();
      
      return Collections.unmodifiableList(new ArrayList(channels.valueCollection()));
    }
    













    @Nullable
    public VoiceChannel getVoiceChannelById(String id)
    {
      checkAvailable();
      
      return (VoiceChannel)channels.get(MiscUtil.parseSnowflake(id));
    }
    











    @Nullable
    public VoiceChannel getVoiceChannelById(long id)
    {
      checkAvailable();
      
      return (VoiceChannel)channels.get(id);
    }
    








    @Nonnull
    public List<Member> getMembers()
    {
      checkAvailable();
      
      return Collections.unmodifiableList(new ArrayList(members.valueCollection()));
    }
    













    @Nullable
    public Member getMemberById(String id)
    {
      checkAvailable();
      
      return (Member)members.get(MiscUtil.parseSnowflake(id));
    }
    











    @Nullable
    public Member getMemberById(long id)
    {
      checkAvailable();
      
      return (Member)members.get(id);
    }
    
    public int hashCode()
    {
      return Long.hashCode(id);
    }
    
    public boolean equals(Object obj)
    {
      if (!(obj instanceof Widget))
        return false;
      Widget oWidget = (Widget)obj;
      return (this == oWidget) || (id == oWidget.getIdLong());
    }
    

    public String toString()
    {
      return "W:" + (isAvailable() ? getName() : "") + '(' + id + ')';
    }
    
    private void checkAvailable()
    {
      if (!isAvailable) {
        throw new IllegalStateException("The widget for this Guild is unavailable!");
      }
    }
    
    public static class Member implements IMentionable
    {
      private final boolean bot;
      private final long id;
      private final String username;
      private final String discriminator;
      private final String avatar;
      private final String nickname;
      private final OnlineStatus status;
      private final Activity game;
      private final WidgetUtil.Widget widget;
      private WidgetUtil.Widget.VoiceState state;
      
      private Member(@Nonnull DataObject json, @Nonnull WidgetUtil.Widget widget) {
        this.widget = widget;
        bot = json.getBoolean("bot");
        id = json.getLong("id");
        username = json.getString("username");
        discriminator = json.getString("discriminator");
        avatar = json.getString("avatar", null);
        nickname = json.getString("nick", null);
        status = OnlineStatus.fromKey(json.getString("status"));
        game = (json.isNull("game") ? null : EntityBuilder.createActivity(json.getObject("game")));
      }
      
      private void setVoiceState(WidgetUtil.Widget.VoiceState voiceState)
      {
        state = voiceState;
      }
      





      public boolean isBot()
      {
        return bot;
      }
      





      @Nonnull
      public String getName()
      {
        return username;
      }
      

      public long getIdLong()
      {
        return id;
      }
      

      @Nonnull
      public String getAsMention()
      {
        return "<@" + getId() + ">";
      }
      





      @Nonnull
      public String getDiscriminator()
      {
        return discriminator;
      }
      







      @Nullable
      public String getAvatarId()
      {
        return avatar;
      }
      







      @Nullable
      public String getAvatarUrl()
      {
        String avatarId = getAvatarId();
        return avatarId == null ? null : String.format("https://cdn.discordapp.com/avatars/%s/%s.%s", new Object[] { getId(), avatarId, avatarId.startsWith("a_") ? ".gif" : ".png" });
      }
      






      @Nonnull
      public String getDefaultAvatarId()
      {
        return String.valueOf(Integer.parseInt(getDiscriminator()) % 5);
      }
      






      @Nonnull
      public String getDefaultAvatarUrl()
      {
        return String.format("https://cdn.discordapp.com/embed/avatars/%s.png", new Object[] { getDefaultAvatarId() });
      }
      







      @Nonnull
      public String getEffectiveAvatarUrl()
      {
        String avatarUrl = getAvatarUrl();
        return avatarUrl == null ? getDefaultAvatarUrl() : avatarUrl;
      }
      






      @Nullable
      public String getNickname()
      {
        return nickname;
      }
      






      @Nonnull
      public String getEffectiveName()
      {
        return nickname == null ? username : nickname;
      }
      






      @Nonnull
      public OnlineStatus getOnlineStatus()
      {
        return status;
      }
      








      @Nullable
      public Activity getActivity()
      {
        return game;
      }
      






      @Nonnull
      public WidgetUtil.Widget.VoiceState getVoiceState()
      {
        return state == null ? new WidgetUtil.Widget.VoiceState(this, widget, null) : state;
      }
      





      @Nonnull
      public WidgetUtil.Widget getWidget()
      {
        return widget;
      }
      
      public int hashCode()
      {
        return (widget.getId() + ' ' + id).hashCode();
      }
      
      public boolean equals(Object obj)
      {
        if (!(obj instanceof Member))
          return false;
        Member oMember = (Member)obj;
        return (this == oMember) || ((id == oMember.getIdLong()) && (widget.getIdLong() == oMember.getWidget().getIdLong()));
      }
      

      public String toString()
      {
        return "W.M:" + getName() + '(' + id + ')';
      }
    }
    
    public static class VoiceChannel implements ISnowflake
    {
      private final int position;
      private final long id;
      private final String name;
      private final List<WidgetUtil.Widget.Member> members;
      private final WidgetUtil.Widget widget;
      
      private VoiceChannel(@Nonnull DataObject json, @Nonnull WidgetUtil.Widget widget)
      {
        this.widget = widget;
        position = json.getInt("position");
        id = json.getLong("id");
        name = json.getString("name");
        members = new ArrayList();
      }
      
      private void addMember(@Nonnull WidgetUtil.Widget.Member member)
      {
        members.add(member);
      }
      





      public int getPosition()
      {
        return position;
      }
      

      public long getIdLong()
      {
        return id;
      }
      





      @Nonnull
      public String getName()
      {
        return name;
      }
      





      @Nonnull
      public List<WidgetUtil.Widget.Member> getMembers()
      {
        return members;
      }
      





      @Nonnull
      public WidgetUtil.Widget getWidget()
      {
        return widget;
      }
      
      public int hashCode()
      {
        return Long.hashCode(id);
      }
      
      public boolean equals(Object obj)
      {
        if (!(obj instanceof VoiceChannel))
          return false;
        VoiceChannel oVChannel = (VoiceChannel)obj;
        return (this == oVChannel) || (id == oVChannel.getIdLong());
      }
      

      public String toString()
      {
        return "W.VC:" + getName() + '(' + id + ')';
      }
    }
    
    public static class VoiceState
    {
      private final WidgetUtil.Widget.VoiceChannel channel;
      private final boolean muted;
      private final boolean deafened;
      private final boolean suppress;
      private final boolean selfMute;
      private final boolean selfDeaf;
      private final WidgetUtil.Widget.Member member;
      private final WidgetUtil.Widget widget;
      
      private VoiceState(@Nonnull WidgetUtil.Widget.Member member, @Nonnull WidgetUtil.Widget widget)
      {
        this(null, false, false, false, false, false, member, widget);
      }
      
      private VoiceState(@Nullable WidgetUtil.Widget.VoiceChannel channel, boolean muted, boolean deafened, boolean suppress, boolean selfMute, boolean selfDeaf, @Nonnull WidgetUtil.Widget.Member member, @Nonnull WidgetUtil.Widget widget)
      {
        this.channel = channel;
        this.muted = muted;
        this.deafened = deafened;
        this.suppress = suppress;
        this.selfMute = selfMute;
        this.selfDeaf = selfDeaf;
        this.member = member;
        this.widget = widget;
      }
      





      @Nullable
      public WidgetUtil.Widget.VoiceChannel getChannel()
      {
        return channel;
      }
      






      public boolean inVoiceChannel()
      {
        return channel != null;
      }
      





      public boolean isGuildMuted()
      {
        return muted;
      }
      





      public boolean isGuildDeafened()
      {
        return deafened;
      }
      





      public boolean isSuppressed()
      {
        return suppress;
      }
      





      public boolean isSelfMuted()
      {
        return selfMute;
      }
      





      public boolean isSelfDeafened()
      {
        return selfDeaf;
      }
      





      public boolean isMuted()
      {
        return (selfMute) || (muted);
      }
      





      public boolean isDeafened()
      {
        return (selfDeaf) || (deafened);
      }
      
      @Nonnull
      public WidgetUtil.Widget.Member getMember()
      {
        return member;
      }
      
      @Nonnull
      public WidgetUtil.Widget getWidget()
      {
        return widget;
      }
      
      public int hashCode()
      {
        return member.hashCode();
      }
      
      public boolean equals(Object obj)
      {
        if (!(obj instanceof VoiceState))
          return false;
        VoiceState oState = (VoiceState)obj;
        return (this == oState) || ((member.equals(oState.getMember())) && (widget.equals(oState.getWidget())));
      }
      
      public String toString()
      {
        return "VS:" + widget.getName() + ':' + member.getEffectiveName();
      }
    }
  }
}
