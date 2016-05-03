package com.oink.walkingwithpug.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
    private Image backgroundImage;
    private Image pugImage;

    private TextureRegion currentPugFrame;
    private Image logoSprite;
    private Stage stage;
    private Table table;

    private float angleFactor;

    ImageButton newGameButton;
    ImageButton quitButton;

    public MainMenuScreen(final PugGame game) {

        game.isRunning = false;

        this.game = game;
        stage = new Stage(new StretchViewport(
                PugGame.MENU_VIEWPORT_WIDTH,
                PugGame.MENU_VIEWPORT_HEIGHT * game.getAspectRatio()
        ));
        Gdx.input.setInputProcessor(stage);

        configureActors();
        addActorsToStage();
        createActions();

        angleFactor = 1;
        //stage.setDebugAll(true);
    }

    private void configureActors() {
        //Create logo with properties.
        logoSprite = new Image(new Texture(Gdx.files.internal(GAME_LOGO_TEXTURE)));
        logoSprite.setSize(logoSprite.getWidth() * PugGame.MENU_TEXTURE_SCALE, logoSprite.getHeight() * PugGame.MENU_TEXTURE_SCALE);
        logoSprite.setOrigin(logoSprite.getWidth() / 2, logoSprite.getHeight() / 2);
        logoSprite.setPosition(stage.getWidth() * 2f / 5f - logoSprite.getOriginX(), stage.getHeight() * 3f / 4f - logoSprite.getOriginY());

        currentPugFrame = new TextureRegion(new Texture(Gdx.files.internal(PUG_TEXTURE)));

        createButtons();
        createImages();
        createTable();
    }

    private void createButtons() {
        //Create some buttons.
        newGameButton = Utils.makeButton(BUTTON_NEW_GAME_TEXTURE, PugGame.MENU_TEXTURE_SCALE);
        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new DayAcceptScreen(game));
            }
        });
        quitButton = Utils.makeButton(BUTTON_QUIT_TEXTURE, PugGame.MENU_TEXTURE_SCALE);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    private void createImages() {
        Texture backgroundTexture = new Texture(Gdx.files.internal(BACKGROUND_TEXTURE));
        backgroundImage = new Image(backgroundTexture);
        pugImage = new Image(currentPugFrame);

        backgroundImage.setFillParent(true);
        pugImage.setPosition(
                stage.getWidth() / 2 - stage.getWidth() / 20,
                stage.getHeight() / 2 - stage.getHeight() / 20
        );
        pugImage.setSize(
                currentPugFrame.getRegionWidth() * PugGame.MENU_TEXTURE_SCALE,
                currentPugFrame.getRegionHeight() * PugGame.MENU_TEXTURE_SCALE
        );
    }

    private void createTable() {
        table = new Table();
        table.setFillParent(true);
        table.align(Align.bottom);
        table.padBottom(stage.getHeight() / 6);
        table.add(newGameButton).height(newGameButton.getHeight()).width(newGameButton.getWidth()).expandX();
        table.add(quitButton).height(quitButton.getHeight()).width(quitButton.getWidth()).expandX();
    }

    private void createActions() {
        logoSprite.addAction(fadeOut(0));

        newGameButton.addAction(fadeOut(0));
        quitButton.addAction(fadeOut(0));

        stage.addAction(sequence(moveTo(0, -stage.getHeight()), moveTo(0, 0, 1f), run(new Runnable() {
            @Override
            public void run() {
                logoSprite.addAction(fadeIn(1f));
                newGameButton.addAction(fadeIn(1f));
                quitButton.addAction(fadeIn(1f));
            }
        })));
    }

    private void addActorsToStage() {
        stage.addActor(backgroundImage);
        stage.addActor(pugImage);
        stage.addActor(logoSprite);
        stage.addActor(table);
    }


    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 141f / 255f, 200f / 255f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (logoSprite.getRotation() > 5) angleFactor = -1;
        if (logoSprite.getRotation() < 0) angleFactor = 1;
        logoSprite.rotateBy(1.5f * delta * angleFactor);

        //Drawing
        stage.act();
        stage.draw();

        stage.getBatch().begin();
        stage.getBatch().end();
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
