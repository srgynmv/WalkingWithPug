package com.oink.walkingwithpug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

class Roulette extends Actor {

    Texture rouletteTexture;
    Texture rouletteTextureReversed;
    RouletteLine rouletteLine;

    float dy = 5;
    boolean animationFlag = true;
    float animationTime = 0f;

    Roulette(float scale) {
        super();
        rouletteLine = new RouletteLine();

        rouletteTexture = new Texture(Gdx.files.internal("Roulette.png"));
        rouletteTextureReversed = new Texture(Gdx.files.internal("RouletteReversed.png"));

        setHeight(rouletteTexture.getHeight() * scale);
        setWidth(rouletteTexture.getWidth() * scale);
        setBounds(getX(), getY(), getWidth(), getHeight());
        setTouchable(Touchable.enabled);

        makeListeners(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        //Make roulette animated
        animationTime += Gdx.graphics.getDeltaTime();
        if (animationTime > 0.3f) animateRoulette();

        //Draw line
        rouletteLine.draw(batch);

        if (getX() + getWidth() / 2 > rouletteLine.x1) {
            batch.draw(rouletteTextureReversed, getX(), getY(), getWidth(), getHeight());
        }
        else {
            batch.draw(rouletteTexture, getX(), getY(), getWidth(), getHeight());
        }
    }

    private void makeListeners(final Roulette roulette) {
        roulette.addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                //Gdx.app.log("INFO", "Dragging...");
                roulette.moveBy(x - roulette.getWidth() / 2, y - roulette.getHeight() / 2);
            }
        });
    }

    private void animateRoulette() {
        if (animationFlag) {
            setY(getY() - dy);
        } else {
            setY(getY() + dy);
        }
        animationFlag = !animationFlag;
        animationTime = 0f;
    }
}

class RouletteLine {
    final float lineWidth = 5;
    ShapeRenderer renderer;
    float x1, y1, x2, y2;

    RouletteLine() {
        renderer = new ShapeRenderer();
    }

    void setPoints(Pug pug, Roulette roulette) {
        //Pug coordinates
        x1 = pug.getX() + pug.getWidth() / 2;
        y1 = pug.getY() + pug.getHeight();
        //Roulette coordinates
        x2 = roulette.getX() + roulette.getWidth() / 2;
        y2 = roulette.getY() + roulette.getHeight() / 2;

        if (x2 > x1) {
            x2 -= roulette.getWidth() / 2;
        } else {
            x2 += roulette.getWidth() / 2;
        }

        y1 -= pug.getHeight() / 4;
    }

    void setProjectionMatrix(Matrix4 matrix) {
        renderer.setProjectionMatrix(matrix);
    }

    /**
     * Drawing line with width = lineWidth
     * @param batch
     */
    void draw(Batch batch) {
        batch.end();

        renderer.begin(ShapeRenderer.ShapeType.Line);
        //Drawing lines
        for (float i = -lineWidth / 2; i <= lineWidth / 2; ++i) {
            renderer.line(x1 + i, y1, x2, y2 + i);
        }
        renderer.end();

        batch.begin();
    }
}
