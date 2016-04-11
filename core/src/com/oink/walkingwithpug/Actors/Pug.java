package com.oink.walkingwithpug.Actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.oink.walkingwithpug.PugGame;
import com.oink.walkingwithpug.Screens.GameScreen;

public class Pug extends Unit {
    private TextureRegion pugTexture;
    private GameScreen screen;

    final int maxScaryLevel;
    final int maxPoopLevel;
    final int maxPeeLevel;

    private Animation movingAnimation;
    private Animation peeingAnimation;
    float stateTime;
    private boolean isMoving;
    private float speed;

    private float scaryLevel = 0f;
    private float poopLevel = 0f;
    private float peeLevel = 0f;

    boolean needToPee;
    boolean needToPoop;
    private float poopBorder = 70f;
    private float peeBorder = 60f;

    public Pug(float scale, GameScreen screen) {
        super();
        this.screen = screen;

        maxScaryLevel = 100;
        maxPoopLevel = 100;
        maxPeeLevel = 100;

        movingAnimation = PugGame.createAnimation("game/actors/pug_animated.png", 2, 2);
        peeingAnimation = PugGame.createAnimation("game/actors/pug_peeing.png", 1, 2);

        stateTime = 0;
        speed = 0;
        pugTexture = movingAnimation.getKeyFrame(stateTime, true);
        isMoving = false;

        //Setup actor parameters
        setBounds(getX(), getY(), getWidth(), getHeight());

        setHeight(pugTexture.getRegionHeight());
        setWidth(pugTexture.getRegionWidth());
        setOrigin(getWidth() / 2, getHeight() / 2);
        setRotation(270);

        addToPee(new RandomXS128().nextInt(50));
        addToPoop(new RandomXS128().nextInt(50));

        setScale(scale);
        needToPee = false;
        needToPoop = false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (needToPee || needToPoop) {
            pugTexture = peeingAnimation.getKeyFrame(stateTime, true);
            stateTime += Gdx.graphics.getDeltaTime() * 2;
            drawPee(batch);
        }
        else if (isMoving && screen.game.isRunning) {
            pugTexture = movingAnimation.getKeyFrame(stateTime, true);
            stateTime += Gdx.graphics.getDeltaTime() * speed;
        }
        batch.draw(pugTexture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    private void drawPee(Batch batch) {
        ShapeRenderer peeRenderer = new ShapeRenderer();
        peeRenderer.setColor(1f, 1f, 0f, 0.6f);
        peeRenderer.setProjectionMatrix(screen.camera.combined);

        batch.end();

        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        peeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Vector2 coordinates = localToParentCoordinates(new Vector2(getWidth() / 2, getHeight() / 7));
        peeRenderer.circle(coordinates.x, coordinates.y, peeBorder - getPeeLevel());
        peeRenderer.end();

        Gdx.graphics.getGL20().glDisable(GL20.GL_BLEND);
        batch.begin();
    }

    @Override
    public void act(float delta) {
        addNeeds();
        needToPee = needToPee || ((int)getPeeLevel() > peeBorder);

        if (needToPee) {
            pee(delta);
            return;
        }

        needToPoop = needToPoop || ((int)getPoopLevel() > poopBorder);

        if (needToPoop) {
            poop(delta);
            return;
        }

        thinkNeedToMove(delta);
    }

    private void pee(float delta) {
        isMoving = false;
        addToPee(-10 * delta);

        if (getPeeLevel() == 0) {
            needToPee = false;
        }
    }

    private void poop(float delta) {
        isMoving = false;
        addToPoop(-5 * delta);

        if (getPoopLevel() == 0) {
            needToPoop = false;
        }
    }

    private void thinkNeedToMove(float delta) {
        Vector2 eyeVector = new Vector2(
                (screen.roulette.getX() + screen.roulette.getOriginX() - (getX() + getOriginX())) * delta,
                (screen.roulette.getY() + screen.roulette.getOriginY() - (getY() + getOriginY())) * delta);
        speed = eyeVector.len() / 5;

        //If pug too far from roulette, move to roulette, set running == true
        if (Vector2.dst2(getX() + getOriginX(), getY() + getOriginY(),
                screen.roulette.getX() + screen.roulette.getOriginX(),
                screen.roulette.getY() + screen.roulette.getOriginY()
        ) > screen.maxLineLengthSquared) {
            moveToRoulette(eyeVector);
            isMoving = true;
        }
        else {
            isMoving = false;
        }
    }

    private void moveToRoulette(Vector2 eyeVector) {
        moveBy(eyeVector.x, eyeVector.y);

        float angle = eyeVector.angle() - 90;
        if (angle < 0) angle += 360;

        setRotation(angle); //Add 90 degrees because of pug texture directed to top
    }

    private void addNeeds() {
        addToPee(5f * Gdx.graphics.getDeltaTime());
        addToPoop(0.3f * Gdx.graphics.getDeltaTime());
    }


    public float getScaryLevel() {
        return scaryLevel;
    }

    public void addToScary(float value) {
        scaryLevel = MathUtils.clamp(scaryLevel + value, 0, maxScaryLevel);
    }

    public float getPoopLevel() {
        return poopLevel;
    }

    public void addToPoop(float value) {
        poopLevel = MathUtils.clamp(poopLevel + value, 0, maxPoopLevel);
    }

    public float getPeeLevel() {
        return peeLevel;
    }

    public void addToPee(float value) {
        peeLevel = MathUtils.clamp(peeLevel + value, 0, maxPeeLevel);
    }
}
