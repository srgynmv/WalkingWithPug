package com.oink.walkingwithpug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

public class Roulette extends Actor {

    private Texture rouletteTexture;
    private Texture rouletteTextureReversed;
    RouletteLine rouletteLine;

    final float dy = 5;
    private float animationTime = 0f;

    private boolean animationFlag = true;
    boolean isDragging = false;
    boolean reversed;

    private GameScreen screen;

    Roulette(float scale, GameScreen screen) {
        this.screen = screen;
        rouletteLine = new RouletteLine(screen.pug, this);

        rouletteTexture = new Texture(Gdx.files.internal("Roulette.png"));
        rouletteTextureReversed = new Texture(Gdx.files.internal("RouletteReversed.png"));

        setHeight(rouletteTexture.getHeight() * scale);
        setWidth(rouletteTexture.getWidth() * scale);
        setBounds(getX(), getY(), getWidth(), getHeight());
        setTouchable(Touchable.enabled);

        makeListeners(this);

        setOrigin(getX() + getWidth() / 2 , getY() + getHeight() / 2);

        reversed = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        //Make roulette animated
        animationTime = Math.min(animationTime + Gdx.graphics.getDeltaTime(), 0.4f);
        if (animationTime > 0.3f && !isDragging && screen.game.isRunning) {
            animateRoulette();
            animationTime = 0f;
        }

        //Draw roulette rope
        rouletteLine.draw(batch);

        correctReverse();

        if (reversed) {
            batch.draw(rouletteTextureReversed, getX(), getY(), getWidth(), getHeight());
        }
        else {
            batch.draw(rouletteTexture, getX(), getY(), getWidth(), getHeight());
        }
    }

    private void correctReverse() {
        if (reversed) {
            if (getX() + getOriginX() < screen.pug.getCenterX()) {
                reversed = false;
            }
        }
        else {
            if (getX() + getOriginX() > screen.pug.getCenterX()) {
                reversed = true;
            }
        }
    }

    private void makeListeners(final Roulette roulette) {
        roulette.addListener(new DragListener() {
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                super.dragStart(event, x, y, pointer);
                roulette.isDragging = true;
                Gdx.app.log("INFO", "START Dragging.");
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                super.drag(event, x, y, pointer);
                roulette.moveBy(x - roulette.getOriginX(), y - roulette.getOriginY());
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                super.dragStop(event, x, y, pointer);
                Gdx.app.log("INFO", "STOP Dragging.");
                roulette.isDragging = false;
            }
        });
    }

    @Override
    public void moveBy(float x, float y) {
        super.moveBy(x, y);
        clampInScreenBounds();
    }

    private void clampInScreenBounds() {
        setX(MathUtils.clamp(
                getX(),
                screen.camera.position.x - screen.stage.getWidth() * screen.camera.zoom / 2,
                screen.camera.position.x + screen.stage.getWidth() * screen.camera.zoom / 2 - getWidth())
        );
        setY(MathUtils.clamp(
                getY(),
                screen.camera.position.y - screen.stage.getHeight() * screen.camera.zoom / 2,
                screen.camera.position.y + screen.stage.getHeight() * screen.camera.zoom / 2 - getHeight())
        );
    }

    private void animateRoulette() {
        if (animationFlag) {
            setY(getY() - dy);
        } else {
            setY(getY() + dy);
        }
        animationFlag = !animationFlag;
    }

    @Override
    public void act(float delta) {
    }

    public float getCenterX() {
        return getX() + getOriginX();
    }

    public float getCenterY() {
        return getY() + getOriginY();
    }
}

