package me.veegie.gdxfbf;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
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
public class FormattableBitmapFont {
    /**
     * The default character to use for the opening bracket in tags.
     */
    private static final String TAG_LEFT_BRACKET_DEFAULT = "<";
    /**
     * The default character to use for the closing bracket in tags.
     */
    private static final String TAG_RIGHT_BRACKET_DEFAULT = ">";
    /**
     * The default character to use to signify a closing tag.
     */
    private static final String TAG_CLOSE_DEFAULT = "/";

    private BitmapFont baseFont;
    private String tagLeftBracket;
    private String tagRightBracket;
    private String tagClose;

    private Map<String, BitmapFont> fonts;
    private String tagsRegex;


    /**
     * Creates a new FormattableBitmapFont using the specified tag-font pairs and the default
     * bracket and tag close characters.
     *
     * @param baseFont     the BitmapFont upon which the formatted fonts are based
     * @param tagFontPairs any number of tag-font pairs to be used in this formattable font.
     */
    public FormattableBitmapFont(BitmapFont baseFont, TagFontPair... tagFontPairs) {
        this(TAG_LEFT_BRACKET_DEFAULT, TAG_RIGHT_BRACKET_DEFAULT, TAG_CLOSE_DEFAULT, baseFont,
                Arrays.asList(tagFontPairs));
    }

    /**
     * Creates a new FormattableBitmapFont using the specified tag-font pairs and the specified
     * characters for tag brackets and closing tags.
     *
     * @param tagLeftBracket  the character to use as the left-bracket in tags
     * @param tagRightBracket the character to use as the right bracket in tags
     * @param tagClose        the character to use to signify that a tag is a closing tag
     * @param baseFont        the BitmapFont upon which the formatted fonts are based
     * @param tagFontPairs    any number of tag-font pairs to be used in this formattable font
     */
    public FormattableBitmapFont(String tagLeftBracket, String tagRightBracket, String tagClose,
                                 BitmapFont baseFont, TagFontPair... tagFontPairs) {
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
                                 BitmapFont baseFont, List<TagFontPair> tagFontPairs) {
        this.tagLeftBracket = tagLeftBracket;
        this.tagRightBracket = tagRightBracket;
        this.tagClose = tagClose;
        this.baseFont = baseFont;
        fonts = new HashMap<String, BitmapFont>(tagFontPairs.size());
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (int i = 0; i < tagFontPairs.size(); i++) {
            TagFontPair pair = tagFontPairs.get(i);
            fonts.put(pair.getTag(), pair.getFont());
            sb.append(pair.getTag());
            if(i+1 < tagFontPairs.size())
            {
                sb.append('|');
            }
        }
        sb.append(')');
        tagsRegex = ".*" + tagLeftBracket + sb.toString() + tagRightBracket + ".*" + tagLeftBracket + "\1" + tagClose + tagRightBracket + ".*";
    }

    public GlyphLayout draw(SpriteBatch batch, String seq, float x, float y) {
        GlyphLayout layout;
        if (!seq.matches(tagsRegex)) {
            // Text does not appear to contain any complete tags. Simply draw directly.
            layout = baseFont.draw(batch, seq, x, y);
        } else {
            layout = baseFont.draw(batch, seq, x, y);
        }
        return layout;
    }

    private List<TagFontPair> formatText(CharSequence seq) {
        List<TagFontPair> formattedTextSegments = new ArrayList<TagFontPair>();
        return formattedTextSegments;
    }
}
