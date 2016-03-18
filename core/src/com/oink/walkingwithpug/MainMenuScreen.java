package com.oink.walkingwithpug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * This class makes a new game and exit buttons on the screen.
 */
public class MainMenuScreen implements Screen {

    final PugGame game;

    private Texture backgroundTexture;
    private TextureRegion currentPugFrame;
    private Sprite logoSprite;
    private Stage stage;
    private Table table;

    ImageButton newGameButton;
    ImageButton quitButton;

    float textureScale;

    MainMenuScreen(final PugGame game) {

        backgroundTexture = new Texture(Gdx.files.internal("menu_bgrd.png"));

        this.game = game;
        stage = new Stage(new StretchViewport(game.worldWidth * game.viewportRatio, game.worldHeight * game.viewportRatio * game.ratio));
        Gdx.input.setInputProcessor(stage);

        textureScale = stage.getWidth() / backgroundTexture.getWidth() ;

        Gdx.app.log("SCALE", textureScale + "");
        //Create some buttons.
        newGameButton = makeButton("new_game");
        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });
        quitButton = makeButton("quit");
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        //Create logo with properties.
        logoSprite = new Sprite(new Texture(Gdx.files.internal("game_logo.png")));
        logoSprite.setSize(logoSprite.getWidth() * textureScale, logoSprite.getHeight() * textureScale);
        logoSprite.setOrigin(logoSprite.getWidth() / 2, logoSprite.getHeight() / 2);
        logoSprite.setPosition(stage.getWidth() * 2f / 5f - logoSprite.getOriginX(), stage.getHeight() * 3f / 4f - logoSprite.getOriginY());

        currentPugFrame = new TextureRegion(new Texture(Gdx.files.internal("menu_pug.png")));

        table = new Table();
        table.setFillParent(true);

        table.align(Align.center | Align.bottom);
        table.padBottom(stage.getHeight() / 6);
        table.add(newGameButton).height(newGameButton.getHeight());
        table.add(quitButton).height(quitButton.getHeight());

        stage.addActor(table);
    }

    private ImageButton makeButton(String name) {
        TextureRegionDrawable buttonUp = new TextureRegionDrawable(new TextureRegion(new Texture(name + ".png")));
        TextureRegionDrawable buttonDown = new TextureRegionDrawable(new TextureRegion(new Texture(name + "_pressed.png")));
        ImageButton button;
        button = new ImageButton(buttonUp, buttonDown);
        button.setSize(button.getWidth() * textureScale, button.getHeight() * textureScale);
        //button.setBounds(button.getX(), button.getY(), button.getWidth(), button.getHeight());
        return button;
    }

    @Override
    public void show() {

    }

    float kof = 1;

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0.5f, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (logoSprite.getRotation() > 5) kof = -1;
        if (logoSprite.getRotation() < 0) kof = 1;
        logoSprite.rotate(1.5f * delta * kof);

        stage.act(delta);

        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, stage.getWidth(), stage.getHeight());
        stage.getBatch().draw(
                currentPugFrame,
                stage.getWidth() / 2 - stage.getWidth() / 20,
                stage.getHeight() / 2 - stage.getHeight() / 20,
                currentPugFrame.getRegionWidth() * textureScale,
                currentPugFrame.getRegionHeight() * textureScale
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
