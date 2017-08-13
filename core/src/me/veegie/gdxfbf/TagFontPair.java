package me.veegie.gdxfbf;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Describes a simple Tag-BitmapFont pair.
 */
public final class TagFontPair
{
    private String     tag;
    private BitmapFont font;

    /**
     * Creates a new TagFontPair with the specified tag and BitmapFont.
     * @param tag the tag to be used for the given font (e.g. "b" for bold)
     * @param font the font to associate with the preceding tag
     */
    TagFontPair(String tag, BitmapFont font)
    {
        this.tag = tag;
        this.font = font;
    }

    public String getTag()
    {
        return tag;
    }

    public BitmapFont getFont()
    {
        return font;
    }
}
