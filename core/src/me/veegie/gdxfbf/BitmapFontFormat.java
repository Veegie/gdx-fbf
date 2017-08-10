package me.veegie.gdxfbf;

public enum BitmapFontFormat implements IBitmapFontFormat
{
    BOLD("b"), UNDERLINE("u"), STRIKETHROUGH("s"), ITALIC("i");

    String tag;

    BitmapFontFormat(String tag)
    {
        this.tag = tag;
    }

    public String getTag()
    {
        return tag;
    }
}
