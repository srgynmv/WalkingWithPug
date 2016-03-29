package com.oink.walkingwithpug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class Pug extends Actor {
    private TextureRegion pugTexture;
    private GameScreen screen;
    private float scary = 0f;

    final int maxScaryLevel;

    //TESTING
    private Animation pugAnimation;
    float stateTime;
    private boolean isRunning;
    private float speed;
    ///

    void addScary(float value) {
        scary = Math.min(maxScaryLevel, scary + value);
    }

    int getScaryLevel() {
        return (int)scary;
    }

    public Pug(float scale, GameScreen screen) {
        super();
        this.screen = screen;

        maxScaryLevel = 100;

        //TESTING
        pugAnimation = PugGame.createAnimation("pug_animated.png", 2, 2);
        stateTime = 0;
        speed = 0;
        pugTexture = pugAnimation.getKeyFrame(stateTime, true);
        isRunning = false;
        ///

        //Setup actor parameters
        setHeight(pugTexture.getRegionHeight() * scale);
        setWidth(pugTexture.getRegionWidth() * scale);
        setBounds(getX(), getY(), getWidth(), getHeight());

        setOrigin(getWidth() / 2, getHeight() / 2);
        setRotation(270);

        setTouchable(Touchable.enabled);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        stateTime += Gdx.graphics.getDeltaTime() * speed;
        if (isRunning) pugTexture = pugAnimation.getKeyFrame(stateTime, true);
        batch.draw(pugTexture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public void act(float delta) {
        Vector2 eyeVector = new Vector2(
                (screen.roulette.getX() + screen.roulette.getOriginX() - (getX() + getOriginX())) * delta,
                (screen.roulette.getY() + screen.roulette.getOriginY() - (getY() + getOriginY())) * delta);
        speed = eyeVector.len() / 5;

        //If pug too far from roulette, move to roulette, set running == true
        if (Vector2.dst2(getX() + getOriginX(), getY() + getOriginY(),
                screen.roulette.getX() + screen.roulette.getOriginX(),
                screen.roulette.getY() + screen.roulette.getOriginY()
        ) > screen.maxLineLengthSquared) {
            moveBy(eyeVector.x, eyeVector.y);
            isRunning = true;

            float angle = eyeVector.angle() - 90;
            if (angle < 0) angle += 360;

            setRotation(angle); //Add 90 degrees because of pug texture directed to top
        }
        else {
            isRunning = false;
        }
    }

    public float getCenterX()
    {
        return getX() + getOriginX();
    }

    public float getCenterY()
    {
        return getY() + getOriginY();
    }
}
