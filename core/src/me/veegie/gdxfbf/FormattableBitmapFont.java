package me.veegie.gdxfbf;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
    private static final String TAG_LEFT_BRACKET_DEFAULT  = "<";
    /**
     * The default character to use for the closing bracket in tags.
     */
    private static final String TAG_RIGHT_BRACKET_DEFAULT = ">";
    /**
     * The default character to use to signify a closing tag.
     */
    private static final String TAG_CLOSE_DEFAULT         = "/";

    /**
     * The BitmapFont upon which the formatted fonts are based on.
     */
    private BitmapFont baseFont;

    /**
     * The character or string used to denote the left bracket in a tag.
     */
    private String tagLeftBracket;

    /**
     * The character or string used to denote the right bracket in a tag.
     */
    private String tagRightBracket;

    /**
     * The character or string used to indicate that a tag is a closing tag.
     */
    private String tagClose;

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
    public FormattableBitmapFont(BitmapFont baseFont, TagFontPair... tagFontPairs)
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
    public FormattableBitmapFont(String tagLeftBracket, String tagRightBracket, String tagClose,
                                 BitmapFont baseFont, TagFontPair... tagFontPairs)
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
    public FormattableBitmapFont(String tagLeftBracket, String tagRightBracket, String tagClose,
                                 BitmapFont baseFont, List<TagFontPair> tagFontPairs)
    {
        this.tagLeftBracket = escapeRegex(tagLeftBracket);
        this.tagRightBracket = escapeRegex(tagRightBracket);
        this.tagClose = escapeRegex(tagClose);
        this.baseFont = baseFont;
        fonts = new HashMap<String, BitmapFont>(tagFontPairs.size());

        // We need to search only for valid tags when determining if a string needs to be drawn
        // as a formatted string. Construct a regex to quickly search strings for all supplied
        // tags while we also populate the tag-to-BitmapFont map.
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (int i = 0; i < tagFontPairs.size(); i++)
        {
            TagFontPair pair = tagFontPairs.get(i);
            fonts.put(pair.getTag(), pair.getFont());
            sb.append(pair.getTag());
            if (i + 1 < tagFontPairs.size())
            {
                sb.append('|');
            }
        }
        sb.append(')');

        tagsRegex = ".*" + this.tagLeftBracket + sb.toString() + this.tagRightBracket + ".*" +
                    this.tagLeftBracket + this.tagClose + "\1" + this.tagRightBracket + ".*";
    }

    public GlyphLayout draw(SpriteBatch batch, String str, float x, float y)
    {
        GlyphLayout layout;
        if (!str.matches(tagsRegex))
        {
            // Text does not appear to contain any complete tags. Simply draw directly.
            layout = baseFont.draw(batch, str, x, y);
        }
        else
        {
            layout = baseFont.draw(batch, str, x, y);
        }
        return layout;
    }

    private List<TagFontPair> formatText(String str)
    {
        String[]          fragments             = str.split(tagLeftBracket);
        List<TagFontPair> formattedTextSegments = new ArrayList<TagFontPair>();
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
            switch (chr)
            {
                case '.':
                    res.append("\\.");
                    break;
                case '\\':
                    res.append("\\\\");
                    break;
                case '?':
                    res.append("\\?");
                    break;
                case '*':
                    res.append("\\*");
                    break;
                case '+':
                    res.append("\\+");
                    break;
                case '&':
                    res.append("\\&");
                    break;
                case ':':
                    res.append("\\:");
                    break;
                case '{':
                    res.append("\\{");
                    break;
                case '}':
                    res.append("\\}");
                    break;
                case '[':
                    res.append("\\[");
                    break;
                case ']':
                    res.append("\\]");
                    break;
                case '(':
                    res.append("\\(");
                    break;
                case ')':
                    res.append("\\)");
                    break;
                case '^':
                    res.append("\\^");
                    break;
                case '$':
                    res.append("\\$");
                    break;
                default:
                    res.append(chr);
                    break;
            }
            chr = iterator.next();
        }
        return res.toString();
    }
}
