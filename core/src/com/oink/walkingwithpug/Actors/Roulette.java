package com.oink.walkingwithpug.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.oink.walkingwithpug.PugGame;
import com.oink.walkingwithpug.screens.GameScreen;

public class Roulette extends Unit {
    private static final String ROULETTE_TEXTURE = "game/actors/Roulette.png";
    //private static final String ROULETTE_REVERSED_TEXTURE = "game/actors/RouletteReversed.png";
    private static final float DY = 5;
    private static final float ANIMATION_TIME = 0.3f;
    public static final float MAX_DISTANCE = PugGame.GAME_VIEWPORT_WIDTH;

    private Texture rouletteTexture;
    //private Texture rouletteTextureReversed;
    public RouletteLine rouletteLine;

    private float animationTimer;

    private boolean animationFlag;
    public boolean isDragging;
    boolean reversed;

    private GameScreen screen;

    public Roulette(GameScreen screen) {
        this.screen = screen;
        rouletteLine = new RouletteLine(screen.pug, this);
        animationTimer = 0;
        animationFlag = true;
        isDragging = false;

        rouletteTexture = new Texture(Gdx.files.internal(ROULETTE_TEXTURE));
        //rouletteTextureReversed = new Texture(Gdx.files.internal(ROULETTE_REVERSED_TEXTURE));

        setHeight(rouletteTexture.getHeight());
        setWidth(rouletteTexture.getWidth());
        setTouchable(Touchable.enabled);

        makeListeners(this);

        setOrigin(getWidth() / 2, getHeight() / 2);

        reversed = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        //Make roulette animated
        animationTimer = Math.min(animationTimer + Gdx.graphics.getDeltaTime(), ANIMATION_TIME);
        if (animationTimer == ANIMATION_TIME && !isDragging && screen.game.isRunning) {
            //animateRoulette();
            animationTimer = 0;
        }

        //Draw roulette rope
        rouletteLine.draw(batch);

        correctReverse();

        setScale(1f);
        batch.draw(rouletteTexture,
                getX(),
                getY(),
                getWidth(),
                getHeight(),
                0,
                0,
                (int)getWidth(),
                (int)getHeight(),
                reversed,
                false);
//        if (!reversed) {
//            batch.draw(rouletteTexture, getX(), getY(), getWidth(), getHeight());
//        } else {
//            batch.draw(rouletteTextureReversed, getX(), getY(), getWidth(), getHeight());
//        }
    }

    private void correctReverse() {
        if (reversed) {
            if (getCenterX() < screen.pug.getCenterX()) {
                reversed = false;
            }
        }
        else {
            if (getCenterX() > screen.pug.getCenterX()) {
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
        float newX = getCenterX() + x;
        float newY = getCenterY() + y;
        if (screen.pug.getDistanceTo(newX, newY) < MAX_DISTANCE) {
            super.moveBy(x, y);
            clampInScreenBounds();
        }
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

    @Deprecated
    private void animateRoulette() {
        if (animationFlag) {
            setY(getY() - DY);
        } else {
            setY(getY() + DY);
        }
        animationFlag = !animationFlag;
    }

    @Override
    public void act(float delta) {
    }
}

