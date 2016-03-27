package com.oink.walkingwithpug;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Main class for the game
 */
public class PugGame extends Game {
    //Ratio that keeps screen proportion right
    float ratio;

    final float viewportRatio = 0.1f;
    final int worldWidth = 10000;
    final int worldHeight = 10000;
    //The maximum possible length of the rope in which pug is not running to roulette

    SpriteBatch batch;
    BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        ratio = Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();
        //Loading resources

        font = loadFont("pixfont.ttf", 32);

        // Creating the main menu
        setScreen(new MainMenuScreen(this));

    }

    @Override
    public void render() {
        super.render();
    }


    /**
     * This method dispose all loaded resources
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        super.dispose();
    }

    /**
     * Converts .ttf font to the bitmap font with required size
     * @param path path to .ttf font
     * @param size required size
     * @return generated bitmap font
     */
    private BitmapFont loadFont(String path, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;

        BitmapFont font = generator.generateFont(parameter);

        generator.dispose();

        return font;
    }
}
