package com.oink.walkingwithpug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {

    PugGame game;
    Stage stage;
    Roulette roulette;
    Pug pug;

    //DEBUG
    Texture map;

    GameScreen(final PugGame game) {
        Gdx.app.log("INFO", "In a GameScreen constructor");
        this.game = game;
        //Settings up the scales of pug and roulette
        roulette = new Roulette(0.25f);
        pug = new Pug(0.4f, this);

        //DEBUG
        map = new Texture(Gdx.files.internal("random_map.png"));

        //Making viewport
        stage = new Stage(new FitViewport(game.worldWidth * game.viewportRatio, game.worldHeight * game.viewportRatio * game.ratio));

        Gdx.input.setInputProcessor(stage);

        pug.setX(stage.getWidth() / 2 - pug.getWidth() / 2);

        //roulette.setX(stage.getWidth() / 2 - roulette.getWidth() / 2);
        //roulette.setY(stage.getHeight() / 2 - roulette.getHeight() / 2);

        stage.addActor(pug);
        stage.addActor(roulette);

        game.maxLineLengthSquared = stage.getWidth() / 4;
        game.maxLineLengthSquared *= game.maxLineLengthSquared;
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0.5f, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        moveCamera();

        stage.getCamera().update();

        roulette.rouletteLine.setPoints(pug, roulette);
        roulette.rouletteLine.setProjectionMatrix(stage.getCamera().combined);

        stage.act(delta);

        //DEBUG
        //Drawing map
        stage.getBatch().begin();
        stage.getBatch().draw(map, 0, 0, map.getWidth() * 8, map.getHeight() * 8);
        stage.getBatch().end();

        stage.draw();
    }

    private void moveCamera() {
        //Difference on X between Roulette center and camera center.
        //Same to Y.
        float rouletteDx = roulette.getX() + roulette.getOriginX() - stage.getCamera().position.x;
        float rouletteDy = roulette.getY() + roulette.getOriginY() - stage.getCamera().position.y;

        //X and Y of roulette line middle.
        float rouletteLineMiddleX = (pug.getX() + pug.getOriginX() + roulette.getX() + roulette.getOriginX()) / 2;
        float rouletteLineMiddleY = (pug.getY() + pug.getOriginY() +roulette.getY() + roulette.getOriginY()) / 2;

        //If player doesn't touch screen, camera translates to rouletteLineMiddle.
        //Else camera moves to roulette.
        //TODO
        //Change 10 with cameraSize - depending value
        if (roulette.isDragging) {
            if (Math.abs(rouletteDx) > 10) {
                stage.getCamera().translate(rouletteDx * Gdx.graphics.getDeltaTime(), 0, 0);
                roulette.moveBy(rouletteDx * Gdx.graphics.getDeltaTime(), 0);
            }
            if (Math.abs(rouletteDy) > 10 * game.ratio) {
                stage.getCamera().translate(0, rouletteDy / game.ratio * Gdx.graphics.getDeltaTime(), 0);
                roulette.moveBy(0, rouletteDy / game.ratio * Gdx.graphics.getDeltaTime());
            }
        }
        else {
            stage.getCamera().translate(
                    (rouletteLineMiddleX - stage.getCamera().position.x) * Gdx.graphics.getDeltaTime(),
                    (rouletteLineMiddleY - stage.getCamera().position.y) * Gdx.graphics.getDeltaTime(),
                    0
            );
        }
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
