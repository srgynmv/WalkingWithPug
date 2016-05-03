package com.oink.walkingwithpug;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.oink.walkingwithpug.screens.MainMenuScreen;

/**
 * Main class for the game
 */
public class PugGame extends Game {
    public static final float VIEWPORT_RATIO = 0.1f;
    public static final int WORLD_WIDTH = 6400;
    public static final int WORLD_HEIGHT = 6400;
    public static final float MENU_VIEWPORT_WIDTH = 1000;
    public static final float MENU_VIEWPORT_HEIGHT = 1000;
    public static final float GAME_VIEWPORT_WIDTH = 500;
    public static final float GAME_VIEWPORT_HEIGHT = 500;
    public static final float BASE_SCREEN_WIDTH = 1920f;
    public static final float MENU_TEXTURE_SCALE = MENU_VIEWPORT_WIDTH / BASE_SCREEN_WIDTH;
    public static final float GAME_TEXTURE_SCALE = GAME_VIEWPORT_WIDTH / BASE_SCREEN_WIDTH;
    public static final int FONT_SIZE = 20;
    public static final float FONT_BORDER_WIDTH = 1.5f;
    public static final String TTF_FONT = "fonts/raw_font.ttf";
    public static final String BITMAP_FONT = "fonts/font.fnt";

    public SpriteBatch batch;
    public BitmapFont font;

    public boolean isRunning;

    @Override
    public void create() {
        batch = new SpriteBatch();

        //Loading resources

        try {
            font = Utils.loadFont(TTF_FONT, FONT_SIZE, Color.BLACK, FONT_BORDER_WIDTH);
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