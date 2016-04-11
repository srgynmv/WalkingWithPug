package com.oink.walkingwithpug.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.oink.walkingwithpug.PugGame;
import com.oink.walkingwithpug.Utils;

/**
 * This class makes a new game and exit buttons on the screen.
 */
public class MainMenuScreen implements Screen {

    private static final String BACKGROUND_TEXTURE = "menu/background.png";
    private static final String BUTTON_NEW_GAME_TEXTURE = "menu/buttons/new_game";
    private static final String BUTTON_QUIT_TEXTURE ="menu/buttons/quit";
    private static final String GAME_LOGO_TEXTURE ="menu/game_logo.png";
    private static final String PUG_TEXTURE ="menu/pug.png";

    final PugGame game;

    private Texture backgroundTexture;
    private TextureRegion currentPugFrame;
    private Sprite logoSprite;
    private Stage stage;
    private Table table;

    private float angleFactor;

    ImageButton newGameButton;
    ImageButton quitButton;

    public MainMenuScreen(final PugGame game) {

        game.isRunning = false;

        backgroundTexture = new Texture(Gdx.files.internal(BACKGROUND_TEXTURE));

        this.game = game;
        stage = new Stage(new StretchViewport(
                PugGame.VIEWPORT_WIDTH,
                PugGame.VIEWPORT_HEIGHT * game.getAspectRatio()
        ));
        Gdx.input.setInputProcessor(stage);

        //Create some buttons.
        newGameButton = Utils.makeButton(BUTTON_NEW_GAME_TEXTURE, PugGame.TEXTURE_SCALE);
        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new com.oink.walkingwithpug.screens.GameScreen(game));
            }
        });
        quitButton = Utils.makeButton(BUTTON_QUIT_TEXTURE, PugGame.TEXTURE_SCALE);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        //Create logo with properties.
        logoSprite = new Sprite(new Texture(Gdx.files.internal(GAME_LOGO_TEXTURE)));
        logoSprite.setSize(logoSprite.getWidth() * PugGame.TEXTURE_SCALE, logoSprite.getHeight() * PugGame.TEXTURE_SCALE);
        logoSprite.setOrigin(logoSprite.getWidth() / 2, logoSprite.getHeight() / 2);
        logoSprite.setPosition(stage.getWidth() * 2f / 5f - logoSprite.getOriginX(), stage.getHeight() * 3f / 4f - logoSprite.getOriginY());

        currentPugFrame = new TextureRegion(new Texture(Gdx.files.internal(PUG_TEXTURE)));

        table = new Table();
        table.setFillParent(true);

        table.align(Align.bottom);
        table.padBottom(stage.getHeight() / 6);
        table.add(newGameButton).height(newGameButton.getHeight()).width(newGameButton.getWidth()).expandX();
        table.add(quitButton).height(quitButton.getHeight()).width(quitButton.getWidth()).expandX();

        stage.addActor(table);

        angleFactor = 1;
        //stage.setDebugAll(true);
    }


    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(1, 1, 1, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (logoSprite.getRotation() > 5) angleFactor = -1;
        if (logoSprite.getRotation() < 0) angleFactor = 1;
        logoSprite.rotate(1.5f * delta * angleFactor);

        //Drawing
        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, stage.getWidth(), stage.getHeight());
        stage.getBatch().draw(
                currentPugFrame,
                stage.getWidth() / 2 - stage.getWidth() / 20,
                stage.getHeight() / 2 - stage.getHeight() / 20,
                currentPugFrame.getRegionWidth() * PugGame.TEXTURE_SCALE,
                currentPugFrame.getRegionHeight() * PugGame.TEXTURE_SCALE
        );
        logoSprite.draw(stage.getBatch());
        stage.getBatch().end();

        stage.draw();
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
