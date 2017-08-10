package me.veegie.gdxfbf;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.EnumMap;

public class FormattableBitmapFont {

    EnumMap<? extends IBitmapFontFormat, BitmapFont> fonts;

	SpriteBatch batch;

	public FormattableBitmapFont()
    {
    }

}
