package com.oink.walkingwithpug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
public class GameScreen implements Screen {

    PugGame game;
    Stage stage;
    Roulette roulette;
    Pug pug;

    GameScreen(final PugGame game) {
        Gdx.app.log("INFO", "In a GameScreen constructor");
        this.game = game;
        //Settings up the scales of pug and roulette
        roulette = new Roulette(0.25f);
        pug = new Pug(0.4f, this);

        //Ratio that keeps proportion right
        float ratio = Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();

        //Making viewport
        stage = new Stage(new FitViewport(game.worldWidth * game.viewportRatio, game.worldHeight * game.viewportRatio * ratio));

        Gdx.input.setInputProcessor(stage);

        pug.setX(stage.getWidth() / 2 - pug.getWidth() / 2);

        stage.addActor(pug);
        stage.addActor(roulette);

        game.maxLineLengthSquared = stage.getHeight() / 3;
        game.maxLineLengthSquared *= game.maxLineLengthSquared;
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0.5f, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getCamera().update();

        roulette.rouletteLine.setPoints(pug, roulette);
        roulette.rouletteLine.setProjectionMatrix(stage.getCamera().combined);

        stage.act(delta);
        stage.draw();
    }


    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
