package com.oink.walkingwithpug;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Main class for the game
 */
public class PugGame extends Game {
    public static final float VIEWPORT_RATIO = 0.1f;
    public static final int WORLD_WIDTH = 10000;
    public static final int WORLD_HEIGHT = 10000;
    public static final float VIEWPORT_WIDTH = WORLD_WIDTH * VIEWPORT_RATIO;
    public static final float VIEWPORT_HEIGHT = WORLD_HEIGHT * VIEWPORT_RATIO;
    public static final float BASE_SCREEN_WIDTH = 1920f;
    public static final float TEXTURE_SCALE = VIEWPORT_WIDTH / BASE_SCREEN_WIDTH;
    public static final int FONT_SIZE = 30;
    private static final String TTF_FONT = "fonts/raw_font.ttf";
    private static final String BITMAP_FONT = "fonts/font.fnt";


    public SpriteBatch batch;
    public BitmapFont font;

    public boolean isRunning;

    @Override
    public void create() {
        batch = new SpriteBatch();

        //Loading resources

        try {
            font = Utils.loadFont(TTF_FONT, FONT_SIZE);
        }
        catch (GdxRuntimeException e) {
            font = new BitmapFont(Gdx.files.internal(BITMAP_FONT));
        }

        // Creating the main menu
        setScreen(new MainMenuScreen(this));

    }

    /**
     * Keeps screen proportion right
     * @return Aspect ratio
     */
    public float getAspectRatio(){
        return Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();
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



}