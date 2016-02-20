package com.oink.walkingwithpug;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class Pug extends Actor {
    Texture pugTexture;
    GameScreen screen;

    public Pug(float scale, GameScreen screen) {
        super();
        this.screen = screen;

        pugTexture = new Texture(Gdx.files.internal("pug.png"));

        setHeight(pugTexture.getHeight() * scale);
        setWidth(pugTexture.getWidth() * scale);
        setBounds(getX(), getY(), getWidth(), getHeight());

        setTouchable(Touchable.enabled);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(pugTexture, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void act(float delta) {
        Vector2 eyeVector = new Vector2((screen.roulette.getX() - getX()) * delta, (screen.roulette.getY() - getY()) * delta);

        //Gdx.app.log("INFO", "" + Vector2.dst2(this.getX(), this.getY(), screen.roulette.getX(), screen.roulette.getY()));
        //Gdx.app.log("INFO", "" + screen.game.maxLineLengthSquared);
        Gdx.app.log("ANGLE", "" + eyeVector.angle());
        if (Vector2.dst2(this.getX(), this.getY(), screen.roulette.getX(), screen.roulette.getY()) > screen.game.maxLineLengthSquared) {
            moveBy(eyeVector.x, eyeVector.y);
            setRotation(eyeVector.angle());
        }
    }
}
