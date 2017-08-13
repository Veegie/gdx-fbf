package me.veegie.gdxfbf;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes a special collection of BitmapFonts, used for drawing in-line formatted text,
 * formatting text appropriately as specified in user-defined tags. Each font in the collection
 * has a corresponding text tag. After setup, formatted text blocks can be parsed and drawn in
 * one step using the draw method.
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

    Map<String, BitmapFont> fonts;

    char tagLeftBracket;
    char tagRightBracket;
    char tagClose;

    /**
     * Creates a new FormattableBitmapFont using the specified tag-font pairs and the default
     * bracket and tag close characters.
     *
     * @param tagFontPairs any number of tag-font pairs to be used in this formattable font.
     */
    public FormattableBitmapFont(TagFontPair... tagFontPairs)
    {
        this(TAG_LEFT_BRACKET_DEFAULT, TAG_RIGHT_BRACKET_DEFAULT, TAG_CLOSE_DEFAULT, Arrays
                .asList(tagFontPairs));
    }

    public FormattableBitmapFont(char tagLeftBracket, char tagRightBracket, char tagClose,
                                 TagFontPair... tagFontPairs)
    {
        this(tagLeftBracket, tagRightBracket, tagClose, Arrays.asList(tagFontPairs));
    }

    public FormattableBitmapFont(char tagLeftBracket, char tagRightBracket, char tagClose,
                                 List<TagFontPair> tagFontPairs)
    {
        this.tagLeftBracket = tagLeftBracket;
        this.tagRightBracket = tagRightBracket;
        this.tagClose = tagClose;
        fonts = new HashMap<String, BitmapFont>(tagFontPairs.size());
        for (TagFontPair pair : tagFontPairs)
        {
            fonts.put(pair.getTag(), pair.getFont());
        }
    }
}
