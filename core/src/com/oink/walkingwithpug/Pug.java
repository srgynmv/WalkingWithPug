package com.oink.walkingwithpug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class Pug extends Actor {
    private TextureRegion pugTexture;
    private GameScreen screen;

    private float life = 100f;

    void removeLife(float value) {
        life -= value;
        if (life < 0) life = 0;
    }

    int getLife() {
        return (int)life;
    }

    public Pug(float scale, GameScreen screen) {
        super();
        this.screen = screen;

        pugTexture = new TextureRegion(new Texture(Gdx.files.internal("pug.png")));

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
        batch.draw(pugTexture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public void act(float delta) {
        Vector2 eyeVector = new Vector2(
                (screen.roulette.getX() + screen.roulette.getOriginX() - (getX() + getOriginX())) * delta,
                (screen.roulette.getY() + screen.roulette.getOriginY() - (getY() + getOriginY())) * delta);

        //If pug too far from roulette, move to roulette, set running == true
        if (Vector2.dst2(getX() + getOriginX(), getY() + getOriginY(),
                screen.roulette.getX() + screen.roulette.getOriginX(),
                screen.roulette.getY() + screen.roulette.getOriginY()
        ) > screen.maxLineLengthSquared) {
            moveBy(eyeVector.x, eyeVector.y);
            //running = true;

            float angle = eyeVector.angle() - 90;
            if (angle < 0) angle += 360;

            setRotation(angle); //Add 90 degrees because of pug texture directed to top
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
