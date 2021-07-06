package net.dv8tion.jda.api.interactions.components;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.internal.interactions.ButtonImpl;
import net.dv8tion.jda.internal.utils.Checks;






























































































public abstract interface Button
  extends Component
{
  @Nonnull
  public abstract String getLabel();
  
  @Nonnull
  public abstract ButtonStyle getStyle();
  
  @Nullable
  public abstract String getUrl();
  
  @Nullable
  public abstract Emoji getEmoji();
  
  public abstract boolean isDisabled();
  
  @Nonnull
  @CheckReturnValue
  public Button asDisabled()
  {
    return new ButtonImpl(getId(), getLabel(), getStyle(), getUrl(), true, getEmoji());
  }
  





  @Nonnull
  @CheckReturnValue
  public Button asEnabled()
  {
    return new ButtonImpl(getId(), getLabel(), getStyle(), getUrl(), false, getEmoji());
  }
  








  @Nonnull
  @CheckReturnValue
  public Button withDisabled(boolean disabled)
  {
    return new ButtonImpl(getId(), getLabel(), getStyle(), getUrl(), disabled, getEmoji());
  }
  








  @Nonnull
  @CheckReturnValue
  public Button withEmoji(@Nullable Emoji emoji)
  {
    return new ButtonImpl(getId(), getLabel(), getStyle(), getUrl(), isDisabled(), emoji);
  }
  











  @Nonnull
  @CheckReturnValue
  public Button withLabel(@Nonnull String label)
  {
    Checks.notEmpty(label, "Label");
    Checks.notLonger(label, 80, "Label");
    return new ButtonImpl(getId(), label, getStyle(), getUrl(), isDisabled(), getEmoji());
  }
  











  @Nonnull
  @CheckReturnValue
  public Button withId(@Nonnull String id)
  {
    Checks.notEmpty(id, "ID");
    Checks.notLonger(id, 100, "ID");
    return new ButtonImpl(id, getLabel(), getStyle(), null, isDisabled(), getEmoji());
  }
  











  @Nonnull
  @CheckReturnValue
  public Button withUrl(@Nonnull String url)
  {
    Checks.notEmpty(url, "URL");
    Checks.notLonger(url, 512, "URL");
    return new ButtonImpl(null, getLabel(), ButtonStyle.LINK, url, isDisabled(), getEmoji());
  }
  













  @Nonnull
  @CheckReturnValue
  public Button withStyle(@Nonnull ButtonStyle style)
  {
    Checks.notNull(style, "Style");
    Checks.check(style != ButtonStyle.UNKNOWN, "Cannot make button with unknown style!");
    if ((getStyle() == ButtonStyle.LINK) && (style != ButtonStyle.LINK))
      throw new IllegalArgumentException("You cannot change a link button to another style!");
    if ((getStyle() != ButtonStyle.LINK) && (style == ButtonStyle.LINK))
      throw new IllegalArgumentException("You cannot change a styled button to a link button!");
    return new ButtonImpl(getId(), getLabel(), style, getUrl(), isDisabled(), getEmoji());
  }
  















  @Nonnull
  public static Button primary(@Nonnull String id, @Nonnull String label)
  {
    Checks.notEmpty(id, "Id");
    Checks.notEmpty(label, "Label");
    Checks.notLonger(id, 100, "Id");
    Checks.notLonger(label, 80, "Label");
    return new ButtonImpl(id, label, ButtonStyle.PRIMARY, false, null);
  }
  

















  @Nonnull
  public static Button primary(@Nonnull String id, @Nonnull Emoji emoji)
  {
    Checks.notEmpty(id, "Id");
    Checks.notNull(emoji, "Emoji");
    Checks.notLonger(id, 100, "Id");
    return new ButtonImpl(id, "", ButtonStyle.PRIMARY, false, emoji);
  }
  















  @Nonnull
  public static Button secondary(@Nonnull String id, @Nonnull String label)
  {
    Checks.notEmpty(id, "Id");
    Checks.notEmpty(label, "Label");
    Checks.notLonger(id, 100, "Id");
    Checks.notLonger(label, 80, "Label");
    return new ButtonImpl(id, label, ButtonStyle.SECONDARY, false, null);
  }
  

















  @Nonnull
  public static Button secondary(@Nonnull String id, @Nonnull Emoji emoji)
  {
    Checks.notEmpty(id, "Id");
    Checks.notNull(emoji, "Emoji");
    Checks.notLonger(id, 100, "Id");
    return new ButtonImpl(id, "", ButtonStyle.SECONDARY, false, emoji);
  }
  















  @Nonnull
  public static Button success(@Nonnull String id, @Nonnull String label)
  {
    Checks.notEmpty(id, "Id");
    Checks.notEmpty(label, "Label");
    Checks.notLonger(id, 100, "Id");
    Checks.notLonger(label, 80, "Label");
    return new ButtonImpl(id, label, ButtonStyle.SUCCESS, false, null);
  }
  

















  @Nonnull
  public static Button success(@Nonnull String id, @Nonnull Emoji emoji)
  {
    Checks.notEmpty(id, "Id");
    Checks.notNull(emoji, "Emoji");
    Checks.notLonger(id, 100, "Id");
    return new ButtonImpl(id, "", ButtonStyle.SUCCESS, false, emoji);
  }
  















  @Nonnull
  public static Button danger(@Nonnull String id, @Nonnull String label)
  {
    Checks.notEmpty(id, "Id");
    Checks.notEmpty(label, "Label");
    Checks.notLonger(id, 100, "Id");
    Checks.notLonger(label, 80, "Label");
    return new ButtonImpl(id, label, ButtonStyle.DANGER, false, null);
  }
  

















  @Nonnull
  public static Button danger(@Nonnull String id, @Nonnull Emoji emoji)
  {
    Checks.notEmpty(id, "Id");
    Checks.notNull(emoji, "Emoji");
    Checks.notLonger(id, 100, "Id");
    return new ButtonImpl(id, "", ButtonStyle.DANGER, false, emoji);
  }
  


















  @Nonnull
  public static Button link(@Nonnull String url, @Nonnull String label)
  {
    Checks.notEmpty(url, "URL");
    Checks.notEmpty(label, "Label");
    Checks.notLonger(url, 512, "URL");
    Checks.notLonger(label, 80, "Label");
    return new ButtonImpl(null, label, ButtonStyle.LINK, url, false, null);
  }
  




















  @Nonnull
  public static Button link(@Nonnull String url, @Nonnull Emoji emoji)
  {
    Checks.notEmpty(url, "URL");
    Checks.notNull(emoji, "Emoji");
    Checks.notLonger(url, 512, "URL");
    return new ButtonImpl(null, "", ButtonStyle.LINK, url, false, emoji);
  }
  



















  @Nonnull
  public static Button of(@Nonnull ButtonStyle style, @Nonnull String idOrUrl, @Nonnull String label)
  {
    Checks.check(style != ButtonStyle.UNKNOWN, "Cannot make button with unknown style!");
    Checks.notNull(style, "Style");
    Checks.notNull(label, "Label");
    Checks.notLonger(label, 80, "Label");
    if (style == ButtonStyle.LINK)
      return link(idOrUrl, label);
    Checks.notEmpty(idOrUrl, "Id");
    Checks.notLonger(idOrUrl, 100, "Id");
    return new ButtonImpl(idOrUrl, label, style, false, null);
  }
  



















  @Nonnull
  public static Button of(@Nonnull ButtonStyle style, @Nonnull String idOrUrl, @Nonnull Emoji emoji)
  {
    Checks.check(style != ButtonStyle.UNKNOWN, "Cannot make button with unknown style!");
    Checks.notNull(style, "Style");
    Checks.notNull(emoji, "Emoji");
    if (style == ButtonStyle.LINK)
      return link(idOrUrl, emoji);
    Checks.notEmpty(idOrUrl, "Id");
    Checks.notLonger(idOrUrl, 100, "Id");
    return new ButtonImpl(idOrUrl, "", style, false, emoji);
  }
  



























  @Nonnull
  public static Button of(@Nonnull ButtonStyle style, @Nonnull String idOrUrl, @Nullable String label, @Nullable Emoji emoji)
  {
    if (label != null)
      return of(style, idOrUrl, label).withEmoji(emoji);
    if (emoji != null)
      return of(style, idOrUrl, emoji);
    throw new IllegalArgumentException("Cannot build a button without a label and emoji. At least one has to be provided as non-null.");
  }
}
