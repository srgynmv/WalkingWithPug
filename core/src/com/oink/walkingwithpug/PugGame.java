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
    //Ratio that keeps screen proportion right
    float ratio;

    final float viewportRatio = 0.1f;
    final int worldWidth = 10000;
    final int worldHeight = 10000;
    //The maximum possible length of the rope in which pug is not running to roulette

    SpriteBatch batch;
    BitmapFont font;

    boolean isRunning;

    @Override
    public void create() {
        batch = new SpriteBatch();
        ratio = Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();
        //Loading resources

        try {
            font = loadFont("pixfont.ttf", 30);
        }
        catch (GdxRuntimeException e) {
            font = new BitmapFont(Gdx.files.internal("font.fnt"));
        }

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
     *
     * @param path path to .ttf font
     * @param size required size
     * @return generated bitmap font
     */
    private BitmapFont loadFont(String path, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 1.5f;

        BitmapFont font = generator.generateFont(parameter);

        generator.dispose();

        return font;
    }

    static ImageButton makeButton(String name, float textureScale) {
        TextureRegionDrawable buttonUp = new TextureRegionDrawable(new TextureRegion(new Texture(name + ".png")));
        TextureRegionDrawable buttonDown = new TextureRegionDrawable(new TextureRegion(new Texture(name + "_pressed.png")));

        ImageButton button = new ImageButton(buttonUp, buttonDown);
        button.setSize(button.getWidth() * textureScale, button.getHeight() * textureScale);
        button.setBounds(0, 0, button.getWidth(), button.getHeight());
        return button;
    }

    static Animation createAnimation(String spriteSheetName, int rows, int cols) {
        Texture spriteSheet;
        TextureRegion spriteFrames[];
        spriteSheet = new Texture(Gdx.files.internal(spriteSheetName));
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth()/cols, spriteSheet.getHeight()/rows);
        spriteFrames = new TextureRegion[cols * rows];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                spriteFrames[index++] = tmp[i][j];
            }
        }
        Animation animation = new Animation(1 / 4f, spriteFrames);
        return animation;
    }
}