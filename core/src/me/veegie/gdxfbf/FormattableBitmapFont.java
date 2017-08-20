package me.veegie.gdxfbf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes a special collection of BitmapFonts, used for drawing in-line formatted text,
 * formatting text appropriately as specified in user-defined tags. For example, the user can
 * specify a "bold" tag to use the bold version of a font for a portion of a string. Each font in
 * the collection has a corresponding text tag. After setup, formatted text blocks can be parsed
 * and drawn in one step using the draw method.
 */
public class FormattableBitmapFont
{
    /**
     * The default character to use for the opening bracket in tags.
     */
    private static final char TAG_LEFT_BRACKET_DEFAULT  = '<';
    /**
     * The default character to use for the closing bracket in tags.
     */
    private static final char TAG_RIGHT_BRACKET_DEFAULT = '>';
    /**
     * The default character to use to signify a closing tag.
     */
    private static final char TAG_CLOSE_DEFAULT         = '/';

    /**
     * The BitmapFont upon which the formatted fonts are based on.
     */
    private BitmapFont baseFont;

    /**
     * The character used to denote the left bracket in a tag.
     */
    private char tagLeftBracket;

    /**
     * The character used to denote the right bracket in a tag.
     */
    private char tagRightBracket;

    /**
     * The character used to indicate that a tag is a closing tag.
     */
    private char tagClose;

    /**
     * A map allowing access to each formatted BitmapFont, with tag names as keys.
     */
    private Map<String, BitmapFont> fonts;

    /**
     * A regular expression string constructed during the creation of the FormattableBitmapFont
     * to quickly search provided strings for the presence of tags.
     */
    private String tagsRegex;

    /**
     * Creates a new FormattableBitmapFont using the specified tag-font pairs and the default
     * bracket and tag close characters.
     *
     * @param baseFont     the BitmapFont upon which the formatted fonts are based
     * @param tagFontPairs any number of tag-font pairs to be used in this formattable font.
     */
    public FormattableBitmapFont(BitmapFont baseFont, StringFontPair... tagFontPairs)
    {
        this(TAG_LEFT_BRACKET_DEFAULT, TAG_RIGHT_BRACKET_DEFAULT, TAG_CLOSE_DEFAULT, baseFont,
             Arrays.asList(tagFontPairs));
    }

    /**
     * Creates a new FormattableBitmapFont using the specified tag-font pairs and the specified
     * characters for tag brackets and closing tags.
     *
     * @param tagLeftBracket  the character to use as the left bracket in tags
     * @param tagRightBracket the character to use as the right bracket in tags
     * @param tagClose        the character to use to signify that a tag is a closing tag
     * @param baseFont        the BitmapFont upon which the formatted fonts are based
     * @param tagFontPairs    any number of tag-font pairs to be used in this formattable font
     */
    public FormattableBitmapFont(char tagLeftBracket, char tagRightBracket, char tagClose,
                                 BitmapFont baseFont, StringFontPair... tagFontPairs)
    {
        this(tagLeftBracket, tagRightBracket, tagClose, baseFont, Arrays.asList(tagFontPairs));
    }

    /**
     * Creates a new FormattableBitmapFont using the specified tag-font pairs and the specified
     * characters for tag brackets and closing tags.
     *
     * @param tagLeftBracket  the character to use as the left-bracket in tags
     * @param tagRightBracket the character to use as the right bracket in tags
     * @param tagClose        the character to use to signify that a tag is a closing tag
     * @param baseFont        the BitmapFont upon which the formatted fonts are based
     * @param tagFontPairs    a list of tag-font pairs to be used in this formattable font
     */
    public FormattableBitmapFont(char tagLeftBracket, char tagRightBracket, char tagClose,
                                 BitmapFont baseFont, List<StringFontPair> tagFontPairs)
    {
        this.tagLeftBracket = tagLeftBracket;
        this.tagRightBracket = tagRightBracket;
        this.tagClose = tagClose;
        this.baseFont = baseFont;
        fonts = new HashMap<String, BitmapFont>(tagFontPairs.size());

        // We need to search only for valid tags when determining if a string needs to be drawn
        // as a formatted string. Construct a regex to quickly search strings for all supplied
        // tags while we also populate the tag-to-BitmapFont map.
        StringBuilder sb = new StringBuilder(tagFontPairs.size() * 4 + 2);
        sb.append('(');
        for (int i = 0; i < tagFontPairs.size(); i++)
        {
            StringFontPair pair = tagFontPairs.get(i);
            fonts.put(pair.getString(), pair.getFont());
            sb.append(escapeRegex(pair.getString()));
            if (i + 1 < tagFontPairs.size())
            {
                sb.append('|');
            }
        }
        sb.append(')');

        tagsRegex = ".*" + escapeRegexChar(this.tagLeftBracket) + sb.toString() +
                    escapeRegexChar(this.tagRightBracket) + ".*" +
                    escapeRegexChar(this.tagLeftBracket) + escapeRegexChar(this.tagClose) + "\\1" +
                    escapeRegexChar(this.tagRightBracket) + ".*";
    }

    public List<GlyphLayout> draw(SpriteBatch batch, String str, float x, float y)
    {
        List<GlyphLayout> layouts = new ArrayList<GlyphLayout>();
        if (!str.matches(tagsRegex))
        {
            // Text does not appear to contain any complete tags. Simply draw directly.
            layouts.add(baseFont.draw(batch, str, x, y));
        }
        else
        {
            List<StringFontPair> formattedText = formatText(str);
            GlyphLayout          layout;
            float                curX          = x;
            float                curY          = y;
            for (StringFontPair pair : formattedText)
            {
                // Draw fragments using the correct font at the correct position, one after another.
                layout = pair.getFont().draw(batch, pair.getString(), curX, curY);
                GlyphRun lastRun = layout.runs.get(layout.runs.size - 1);
                curX += lastRun.width;
                // Only update y position if text wrapped.
                if (layout.runs.size > 1)
                {
                    curY += layout.height / layout.runs.size;
                }
                layouts.add(layout);
            }
        }
        return layouts;
    }

    private List<StringFontPair> formatText(String str)
    {
        String[]             fragments             = str.split(escapeRegexChar(tagLeftBracket));
        List<StringFontPair> formattedTextSegments = new ArrayList<StringFontPair>();
        for (String s : fragments)
        {
            int rBracketIndex = s.indexOf(tagRightBracket);
            if (rBracketIndex == -1)
            {
                // The first fragment of a formatted text block will not have a right-bracket
                // character unless the text block begins with a tag. Simply use the base font if
                // no tag is present.
                formattedTextSegments.add(new StringFontPair(s, baseFont));
            }
            else
            {
                String tag = s.substring(0, rBracketIndex);
                String text =
                        rBracketIndex + 1 == s.length() ? s.substring(rBracketIndex) : s.substring(
                                rBracketIndex + 1);
                BitmapFont font;
                if (tag.startsWith(String.valueOf(tagClose)))
                {
                    font = baseFont;
                }
                else
                {
                    font = fonts.get(tag);
                    if (font == null)
                    {
                        // This code snippet-esque library doesn't really need to fail
                        // spectacularly on user errors, so if an invalid tag is entered, just
                        // render it using the base font and log a warning message.
                        Gdx.app.log("WARNING", "gdx-fbf: Invalid or undefined font tag \"" + tag +
                                               "\" in input string.");
                        font = baseFont;
                    }
                }
                formattedTextSegments.add(new StringFontPair(text, font));
            }
        }
        return formattedTextSegments;
    }

    /**
     * Helper method to handle cases where the user has specified characters that require
     * escaping when used in a regular expression.
     *
     * @param str the regex string to escape
     * @return a properly-escaped version of the input string for regex operations
     */
    private static String escapeRegex(String str)
    {
        final StringBuilder           res      = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(str);
        char                          chr      = iterator.current();

        while (chr != CharacterIterator.DONE)
        {
            res.append(escapeRegexChar(chr));
            chr = iterator.next();
        }
        return res.toString();
    }

    /**
     * Helper method to handle cases where the user has specified characters that require
     * escaping when used in a regular expression.
     *
     * @param c the character to escape
     * @return a properly-escaped version of the input character as a string for regex operations
     */
    private static String escapeRegexChar(char c)
    {
        switch (c)
        {
            case '.':
                return "\\.";
            case '\\':
                return "\\\\";
            case '?':
                return "\\?";
            case '*':
                return "\\*";
            case '+':
                return "\\+";
            case '&':
                return "\\&";
            case ':':
                return "\\:";
            case '{':
                return "\\{";
            case '}':
                return "\\}";
            case '[':
                return "\\[";
            case ']':
                return "\\]";
            case '(':
                return "\\(";
            case ')':
                return "\\)";
            case '^':
                return "\\^";
            case '$':
                return "\\$";
            default:
                return String.valueOf(c);
        }
    }
}
