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

    private boolean moving;
    private boolean peeing;
    private boolean pooping;
    private boolean alreadyPeed;

    private float speed;

    private float scaryLevel;
    private float poopLevel;
    private float peeLevel;

    boolean needToPee;
    boolean needToPoop;

    public Pug(GameScreen screen) {
        super();
        this.screen = screen;

        movingAnimation = Utils.createAnimation(PUG_MOVING_TEXTURE, 1, 4, 1 / 8f);
        peeingAnimation = Utils.createAnimation(PUG_PEEING_TEXTURE, 1, 2);

        stateTime = 0;
        speed = 0;
        pugTexture = movingAnimation.getKeyFrame(stateTime, true);
        moving = false;
        peeing = false;
        pooping = false;
        alreadyPeed = false;

        scaryLevel = 0f;
        poopLevel = 0f;
        peeLevel = 0f;

        //Setup actor parameters
        setHeight(pugTexture.getRegionHeight());
        setWidth(pugTexture.getRegionWidth());
        setOrigin(getWidth() / 2, getHeight() / 2);

        addToPee(new RandomXS128().nextInt(50));
        addToPoop(new RandomXS128().nextInt(50));
        needToPee = false;
        needToPoop = false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (peeing || pooping) {
            pugTexture = peeingAnimation.getKeyFrame(stateTime, true);
            stateTime += Gdx.graphics.getDeltaTime() * 2;
            drawPee(batch);
        }
        else {
            pugTexture = movingAnimation.getKeyFrame(stateTime, true);
            if (moving && screen.game.isRunning) stateTime += Gdx.graphics.getDeltaTime() * speed;
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
        Vector2 coordinates = getPugPeePoint();
        peeRenderer.circle(coordinates.x, coordinates.y, (MAX_PEE_LEVEL - getPeeLevel()) / 7);
        peeRenderer.end();

        Gdx.graphics.getGL20().glDisable(GL20.GL_BLEND);
        batch.begin();
    }

    @Override
    public void act(float delta) {
        addNeeds();
        needToPee = needToPee || ((int)getPeeLevel() > PEE_BORDER);

        if (needToPee && screen.map.canPeeOn(getPugPeePoint())) {
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
        moving = false;
        peeing = true;
        addToPee(-20 * delta);

        if (getPeeLevel() == 0) {
            needToPee = false;
            peeing = false;

            //If pee level became 0, pug already peed.
            alreadyPeed = true;
        }
    }

    private void poop(float delta) {
        moving = false;
        pooping = true;

        addToPoop(-10 * delta);

        if (getPoopLevel() == 0) {
            needToPoop = false;
            pooping = false;
        }
    }

    private void thinkNeedToMove(float delta) {
        Vector2 eyeVector = new Vector2(getVectorTo(screen.roulette));
        eyeVector.scl(delta);
        speed = eyeVector.len() / 5;

        //If pug too far from roulette, move to roulette, set running == true
        if (getDistanceTo(screen.roulette) > GameScreen.MAX_LINE_LENGTH) {
            //Gdx.app.log("Pug status", "Moving");
            if (canMoveBy(eyeVector)) {
                moveBy(eyeVector.x, eyeVector.y);
                moving = true;
            }
            else {
                moving = false;
            }
            rotateToRoulette(eyeVector);
        }
        else {
            //Gdx.app.log("Pug status", "Staying");
            moving = false;
        }
    }

    private boolean canMoveBy(Vector2 eyeVector) {
        Vector2 headPoint = getPugHeadPoint();
        headPoint.add(eyeVector);
        if (screen.map.getObstacleOn(headPoint) != null) {
            return false;
        }
        else {
            return true;
        }
    }

    private void rotateToRoulette(Vector2 eyeVector) {
        float angle = eyeVector.angle() - 90;
        if (angle < 0) angle += 360;

        setRotation(angle); //Add 90 degrees because of pug texture directed to top
    }

    private void addNeeds() {
        if (alreadyPeed) {
            addToPee(1f * Gdx.graphics.getDeltaTime());
        }
        else {
            addToPee(3f * Gdx.graphics.getDeltaTime());
        }
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

        if (peeing || pooping) {
            neckY = getHeight() * 4f / 7f;
        }
        return localToParentCoordinates(new Vector2(neckX, neckY));
    }

    public Vector2 getPugPeePoint() {
        //Vector2 coordinates = localToParentCoordinates(new Vector2(getWidth() / 2, getHeight() / 7));
        float peeX = getWidth() / 2;
        float peeY = getHeight() / 7;
        return localToParentCoordinates(new Vector2(peeX, peeY));
    }

    public Vector2 getPugHeadPoint() {
        float headX = getWidth() / 2;
        float headY = getHeight() * 6f / 7f;
        return localToParentCoordinates(new Vector2(headX, headY));
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean isPeeing() {
        return peeing;
    }

    public boolean isPooping() {
        return pooping;
    }

    public boolean isAlreadyPeed() {
        return alreadyPeed;
    }
}
