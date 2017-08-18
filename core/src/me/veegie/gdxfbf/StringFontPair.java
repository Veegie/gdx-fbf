package me.veegie.gdxfbf;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Describes a simple String-BitmapFont pair. Can be used to store associations between tags and
 * BitmapFonts, as well as specifying the font to use to draw a String fragment in a formatted
 * block of text.
 */
public final class StringFontPair
{
    private String     str;
    private BitmapFont font;

    /**
     * Creates a new StringFontPair with the specified String and BitmapFont.
     *
     * @param str  the String to be used for the given font (e.g. "b" for bold), or the String
     *             that is to be drawn using the associated font
     * @param font the font to associate with the preceding String
     */
    StringFontPair(String str, BitmapFont font)
    {
        this.str = str;
        this.font = font;
    }

    public String getString()
    {
        return str;
    }

    public BitmapFont getFont()
    {
        return font;
    }
}
