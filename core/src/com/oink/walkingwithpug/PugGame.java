package com.oink.walkingwithpug;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.awt.Font;

/**
 * Main class for the game
 */
public class PugGame extends Game {
	SpriteBatch batch;
	Texture img;
	BitmapFont font;

	@Override
	public void create () {
		batch = new SpriteBatch();

        //Loading resources

        img = new Texture(Gdx.files.internal("Walking with pug.jpg"));
        font = loadFont("pixfont.ttf", 45);

        // Creating the main menu
        setScreen(new MainMenuScreen(this));

	}

	@Override
	public void render () {
        super.render();
	}

    @Override
    public void dispose() {
        img.dispose();
        batch.dispose();

        super.dispose();
    }

    private BitmapFont loadFont(String path, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;

        BitmapFont font = generator.generateFont(parameter);

        generator.dispose();

        return font;
    }
}
