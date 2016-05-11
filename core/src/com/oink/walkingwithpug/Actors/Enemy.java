package com.oink.walkingwithpug.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.oink.walkingwithpug.screens.GameScreen;
import com.oink.walkingwithpug.Utils;

public class Enemy extends Unit {
    public static final String DOG_ANIMATED_TEXTURE =  "game/actors/enemy_dog_animated.png";
    private int life;

    private TextureRegion enemyTexture;
    private GameScreen screen;

    private Animation enemyAnimation;
    private float stateTime;
    private boolean isRunning;
    private float speed;

    public Enemy(float scale, float x, float y, GameScreen screen) {
        life = 100;
        this.screen = screen;

        makeListeners(this);

        enemyAnimation = Utils.createAnimation(DOG_ANIMATED_TEXTURE, 2, 2);
        stateTime = 0;
        stateTime = 0;
        speed = 0;
        enemyTexture = enemyAnimation.getKeyFrame(stateTime, true);

        setWidth(enemyTexture.getRegionWidth() * scale);
        setHeight(enemyTexture.getRegionHeight() * scale);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setX(x);
        setY(y);

        isRunning = false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        stateTime += Gdx.graphics.getDeltaTime() * speed;
        if (isRunning && screen.game.isRunning) enemyTexture = enemyAnimation.getKeyFrame(stateTime, true);
        batch.draw(enemyTexture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());}

    @Override
    public void act(float delta) {
        if (life <= 0) {
            this.remove();
            return;
        }

        //Make vector from enemy to pug
        Vector2 eyeVector = new Vector2(getVectorTo(screen.pug));
        eyeVector.scl(delta);
        speed = eyeVector.len() / 4;

        //If distance > 0 move and rotate to pug
        if (getDistanceTo(screen.pug) > screen.pug.getHeight() * screen.pug.getScaleY()) {
            if (canMoveBy(eyeVector)) {
                moveBy(eyeVector.x, eyeVector.y);
                isRunning = true;
            }
            else {
                isRunning = false;
            }

            float angle = eyeVector.angle() - 90;
            if (angle < 0) angle += 360;

            setRotation(angle); //Add 90 degrees because of texture directed to top
        }
        else {
            //TODO Make adding scary better!
            isRunning = false;
            screen.pug.addToScary(10f * delta);
        }
    }

    private boolean canMoveBy(Vector2 eyeVector) {
        Vector2 headPoint = getEnemyHeadPoint();
        headPoint.add(eyeVector);
        if (screen.map.getObstacleOn(headPoint) != null) {
            return false;
        }
        else {
            return true;
        }
    }

    public void makeListeners(final Enemy enemy) {
        enemy.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                life -= 50;
            }
        });
    }

    @Override
    public boolean remove() {
        return super.remove();
    }

    public Vector2 getEnemyHeadPoint() {
        float headX = getWidth() / 2;
        float headY = getHeight() * 7f / 8f;
        return localToParentCoordinates(new Vector2(headX, headY));
    }
}
