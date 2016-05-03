package com.oink.walkingwithpug.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.oink.walkingwithpug.screens.GameScreen;
import com.oink.walkingwithpug.Utils;

public class Pug extends Unit {
    private static final String PUG_MOVING_TEXTURE = "game/actors/pug_animated.png";
    private static final String PUG_PEEING_TEXTURE = "game/actors/pug_peeing.png";

    private TextureRegion pugTexture;
    private GameScreen screen;

    public static final int MAX_SCARY_LEVEL = 100;
    public static final int MAX_POOP_LEVEL = 100;
    public static final int MAX_PEE_LEVEL = 100;
    private static final float POOP_BORDER = 70f;
    private static final float PEE_BORDER = 60f;

    private Animation movingAnimation;
    private Animation peeingAnimation;
    float stateTime;

    private boolean isMoving;
    private boolean isPeeing;
    private boolean isPooping;

    private float speed;

    private float scaryLevel;
    private float poopLevel;
    private float peeLevel;

    boolean needToPee;
    boolean needToPoop;

    public Pug(float scale, GameScreen screen) {
        super();
        this.screen = screen;

        movingAnimation = Utils.createAnimation(PUG_MOVING_TEXTURE, 1, 4);
        peeingAnimation = Utils.createAnimation(PUG_PEEING_TEXTURE, 1, 2);

        stateTime = 0;
        speed = 0;
        pugTexture = movingAnimation.getKeyFrame(stateTime, true);
        isMoving = false;
        isPeeing = false;
        isPooping = false;

        scaryLevel = 0f;
        poopLevel = 0f;
        peeLevel = 0f;

        //Setup actor parameters
        //setHeight(pugTexture.getRegionHeight() * scale);
        //setWidth(pugTexture.getRegionWidth() * scale);
        setHeight(pugTexture.getRegionHeight());
        setWidth(pugTexture.getRegionWidth());

        setBounds(getX(), getY(), getWidth(), getHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);

        addToPee(new RandomXS128().nextInt(50));
        addToPoop(new RandomXS128().nextInt(50));
        needToPee = false;
        needToPoop = false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isPeeing || isPooping) {
            pugTexture = peeingAnimation.getKeyFrame(stateTime, true);
            stateTime += Gdx.graphics.getDeltaTime() * 2;
            drawPee(batch);
        }
        else {
            pugTexture = movingAnimation.getKeyFrame(stateTime, true);
            if (isMoving && screen.game.isRunning) stateTime += Gdx.graphics.getDeltaTime() * speed;
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
        peeRenderer.circle(coordinates.x, coordinates.y, (MAX_PEE_LEVEL - getPeeLevel()) / 10);
        peeRenderer.end();

        Gdx.graphics.getGL20().glDisable(GL20.GL_BLEND);
        batch.begin();
    }

    @Override
    public void act(float delta) {
        addNeeds();
        needToPee = needToPee || ((int)getPeeLevel() > PEE_BORDER);

        if (needToPee && screen.map.canPeeOn(getCenterX(), getCenterY())) {
            Gdx.app.log("Pug status", "Peeing");
            pee(delta);
            return;
        }

        needToPoop = needToPoop || ((int)getPoopLevel() > POOP_BORDER);

        if (needToPoop) {
            Gdx.app.log("Pug status", "Pooping");
            poop(delta);
            return;
        }

        thinkNeedToMove(delta);
    }

    private void pee(float delta) {
        isMoving = false;
        isPeeing = true;
        addToPee(-10 * delta);

        if (getPeeLevel() == 0) {
            needToPee = false;
            isPeeing = false;
        }
    }

    private void poop(float delta) {
        isMoving = false;
        isPooping = true;

        addToPoop(-5 * delta);

        if (getPoopLevel() == 0) {
            needToPoop = false;
            isPooping = false;
        }
    }

    private void thinkNeedToMove(float delta) {
        Vector2 eyeVector = new Vector2(getVectorTo(screen.roulette));
        eyeVector.scl(delta);
        speed = eyeVector.len() / 5;

        //If pug too far from roulette, move to roulette, set running == true
        if (getDistanceTo(screen.roulette) > GameScreen.MAX_LINE_LENGTH && canMoveBy(eyeVector)) {
            //Gdx.app.log("Pug status", "Moving");
            moveToRoulette(eyeVector);
            isMoving = true;
        }
        else {
            //Gdx.app.log("Pug status", "Staying");
            isMoving = false;
        }
    }

    private boolean canMoveBy(Vector2 eyeVector) {
        float newX = getCenterX() + eyeVector.x;
        float newY = getCenterY() + eyeVector.y;
        if (screen.map.getObstacleOn(newX, newY) != null) {
            return false;
        }
        else {
            return true;
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
        scaryLevel = MathUtils.clamp(scaryLevel + value, 0, MAX_SCARY_LEVEL);
    }

    public float getPoopLevel() {
        return poopLevel;
    }

    public void addToPoop(float value) {
        poopLevel = MathUtils.clamp(poopLevel + value, 0, MAX_POOP_LEVEL);
    }

    public float getPeeLevel() {
        return peeLevel;
    }

    public void addToPee(float value) {
        peeLevel = MathUtils.clamp(peeLevel + value, 0, MAX_PEE_LEVEL);
        if (peeLevel == MAX_PEE_LEVEL) {
            addToScary(value);
        }
    }

    public Vector2 getPugNeckPoint() {
        float neckX = getWidth() / 2;
        float neckY = getHeight() * 2f / 3f;

        if (isPeeing || isPooping) {
            neckY = getHeight() * 4f / 7f;
        }
        return localToParentCoordinates(new Vector2(neckX, neckY));
    }
}
