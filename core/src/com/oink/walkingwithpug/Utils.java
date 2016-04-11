package com.oink.walkingwithpug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 *
 */
public class Utils {
    /**
     * Converts .ttf font to the bitmap font with required size
     *
     * @param path path to .ttf font
     * @param size required size
     * @return generated bitmap font
     */
    public static BitmapFont loadFont(String path, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 1.5f;

        BitmapFont font = generator.generateFont(parameter);

        generator.dispose();

        return font;
    }

    public static ImageButton makeButton(String name, float textureScale) {
        TextureRegionDrawable buttonUp = new TextureRegionDrawable(new TextureRegion(new Texture(name + ".png")));
        TextureRegionDrawable buttonDown = new TextureRegionDrawable(new TextureRegion(new Texture(name + "_pressed.png")));

        ImageButton button = new ImageButton(buttonUp, buttonDown);
        button.setSize(button.getWidth() * textureScale, button.getHeight() * textureScale);
        button.setBounds(0, 0, button.getWidth(), button.getHeight());
        return button;
    }

    public static Animation createAnimation(String spriteSheetName, int rows, int cols) {
        TextureRegion spriteFrames[];
        Texture spriteSheet = new Texture(Gdx.files.internal(spriteSheetName));
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth()/cols, spriteSheet.getHeight()/rows);
        spriteFrames = new TextureRegion[cols * rows];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                spriteFrames[index++] = tmp[i][j];
            }
        }
        Animation animation = new Animation(1f / (rows * cols), spriteFrames);
        return animation;
    }

}
