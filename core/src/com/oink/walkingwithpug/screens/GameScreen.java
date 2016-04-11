package com.oink.walkingwithpug.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.oink.walkingwithpug.Actors.Enemy;
import com.oink.walkingwithpug.Actors.Pug;
import com.oink.walkingwithpug.PugGame;
import com.oink.walkingwithpug.Actors.Roulette;
import com.oink.walkingwithpug.Utils;

public class GameScreen implements Screen {

    private static final String RANDOM_MAP_TEXTURE = "game/random_map.png";
    private static final String BUTTON_PAUSE_TEXTURE = "game/buttons/pause";
    private static final String PAUSE_BACKGROUND_TEXTURE = "game/pause_menu/pause_background.png";
    private static final String PAUSE_CONTINUE_TEXTURE = "game/pause_menu/continue";
    private static final String PAUSE_EXIT_TO_MENU_TEXTURE = "game/pause_menu/exit_to_menu";



    public PugGame game;
    public Stage stage;
    public Roulette roulette;
    public Pug pug;
    public ImageButton pauseButton;

    public Texture map;
    Container pauseContainer;
    Label scaryLabel;
    Label poopLabel;
    Label peeLabel;

    float enemyTimer;
    Group enemiesGroup;
    Group runningGameGroup;
    VerticalGroup labelGroup;
    Table pauseTable;


    int maxEnemyCount;

    public OrthographicCamera camera;

    public float maxLineLengthSquared;

    GameScreen(final PugGame game) {
        this.game = game;

        //Making camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        //Making viewport and input processor
        stage = new Stage(new StretchViewport(
                PugGame.WORLD_WIDTH * PugGame.VIEWPORT_RATIO,
                PugGame.WORLD_HEIGHT * PugGame.VIEWPORT_RATIO * game.getAspectRatio(),
                camera
        ));
        Gdx.input.setInputProcessor(stage);

        //Settings up the scales of pug and roulette
        pug = new Pug(PugGame.TEXTURE_SCALE, this);
        roulette = new Roulette(PugGame.TEXTURE_SCALE, this);

        //Create groups
        labelGroup = new VerticalGroup();
        enemiesGroup = new Group();
        runningGameGroup = new Group();
        pauseTable = createPauseScreen();

        map = new Texture(Gdx.files.internal(RANDOM_MAP_TEXTURE));
        pauseButton = Utils.makeButton(BUTTON_PAUSE_TEXTURE, PugGame.TEXTURE_SCALE);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pauseGame();
            }
        });

        pauseContainer = new Container<ImageButton>(pauseButton);

        Label.LabelStyle style = new Label.LabelStyle(game.font, Color.YELLOW);
        scaryLabel = new Label("", style);
        poopLabel = new Label("", style);
        peeLabel = new Label("", style);

        setUiParameters(labelGroup);
        setUiParameters(pauseContainer);
        pauseContainer.size(pauseButton.getWidth(), pauseButton.getHeight());
        pauseContainer.align(Align.topLeft);

        labelGroup.addActor(scaryLabel);
        labelGroup.addActor(peeLabel);
        labelGroup.addActor(poopLabel);
        labelGroup.align(Align.topRight);

        pug.setX(stage.getWidth() / 2 - pug.getWidth() / 2);
        roulette.setX(stage.getWidth() / 2 - roulette.getWidth() / 2);
        roulette.setY(stage.getHeight() / 2 - roulette.getHeight() / 2);

        addActorsToStage();

        maxLineLengthSquared = stage.getWidth() / 4;
        maxLineLengthSquared *= maxLineLengthSquared;

        enemyTimer = 0;
        maxEnemyCount = 3;

        game.isRunning = true;
        //stage.setDebugAll(true);
    }

    private void addActorsToStage() {
        stage.addActor(runningGameGroup);
        runningGameGroup.addActor(enemiesGroup);
        runningGameGroup.addActor(pug);
        runningGameGroup.addActor(roulette);
        stage.addActor(pauseContainer);
        stage.addActor(labelGroup);
        stage.addActor(pauseTable);
    }

    private void setUiParameters(WidgetGroup group) {
        group.setFillParent(true);
        group.setTransform(true);
        group.setOrigin(stage.getWidth() / 2, stage.getHeight() / 2);
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0.5f, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.isRunning) {
            //Checks, need to move camera or not
            updateCamera();
            updateUi();

            //Update matrix for roulette line
            roulette.rouletteLine.setProjectionMatrix(stage.getCamera().combined);
            stage.act(delta);
            if (pug.getScaryLevel() >= 100) {
                pug.remove();
                roulette.remove();
            }
            if (enemyTimer > 3f && enemiesGroup.getChildren().size < maxEnemyCount) {
                enemyTimer = 0;
                enemiesGroup.addActor(createEnemy());
            }
            enemyTimer = Math.min(enemyTimer + delta, 4f);
        }
        else {
            //Draw pause background
            camera.update();
        }

        stage.getBatch().begin();
        stage.getBatch().draw(map, 0, 0, PugGame.WORLD_WIDTH, PugGame.WORLD_HEIGHT);
        stage.getBatch().end();

        stage.draw();
    }

    private void updateUi() {
        //Update pug levels
        scaryLabel.setText("Scary: " + (int)pug.getScaryLevel());
        poopLabel.setText("Poop: " + (int)pug.getPoopLevel());
        peeLabel.setText("Pee: " + (int)pug.getPeeLevel());

        float bottomLeftCornerX = camera.position.x - stage.getWidth() / 2;
        float bottomLeftCornerY = camera.position.y - stage.getHeight() / 2;

        pauseContainer.setPosition(bottomLeftCornerX, bottomLeftCornerY );
        pauseContainer.setScale(camera.zoom);
        labelGroup.setPosition(bottomLeftCornerX, bottomLeftCornerY);
        labelGroup.setScale(camera.zoom);
        pauseTable.setPosition(bottomLeftCornerX, bottomLeftCornerY);
        pauseTable.setScale(camera.zoom);
    }

    private Enemy createEnemy() {
        //Making coordinates for new enemy
        Gdx.app.log("Camera x: ", "" + stage.getCamera().position.x);
        Gdx.app.log("Camera y: ", "" + stage.getCamera().position.y);

        float newX = MathUtils.random(0, PugGame.WORLD_WIDTH);
        float newY = MathUtils.random(0, PugGame.WORLD_HEIGHT);
        if (newX >= stage.getCamera().position.x - stage.getWidth() / 2 && newX <= stage.getCamera().position.x + stage.getWidth() / 2) newX += stage.getWidth() * 2;
        if (newY >= stage.getCamera().position.y - stage.getHeight() / 2 && newY <= stage.getCamera().position.y + stage.getHeight() / 2) newY += stage.getHeight() * 2;
        Gdx.app.log("INFO", "Add new enemy at X: " + newX + " and Y: " + newY);
        return new Enemy(PugGame.TEXTURE_SCALE, newX, newY, this);
    }

    /**If player doesn't touch screen, camera translates to rouletteLineMiddle.
     * Else camera moves to roulette.
     */
    private void updateCamera() {
        float cameraScaling = 0.01f;

        //If roulette is moving, zooming up camera;
        //If staying, zooming down
        if (roulette.isDragging) {
            camera.zoom = Math.min(1.5f, camera.zoom + cameraScaling);
        }
        else {
            camera.zoom = Math.max(1.0f, camera.zoom - cameraScaling);
        }

        //Viewport scaled width and height.
        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        if (roulette.isDragging) {
            dragToRoulette(effectiveViewportWidth, effectiveViewportHeight);
        }
        else {
            dragToLineMiddle(effectiveViewportWidth, effectiveViewportHeight);
        }

        stage.getCamera().update();
    }

    private void dragToLineMiddle(float effectiveViewportWidth, float effectiveViewportHeight) {
        //X and Y of roulette line middle.
        float rouletteLineMiddleX = (pug.getX() + pug.getOriginX() + roulette.getX() + roulette.getOriginX()) / 2;
        float rouletteLineMiddleY = (pug.getY() + pug.getOriginY() +roulette.getY() + roulette.getOriginY()) / 2;

        stage.getCamera().translate(
                (rouletteLineMiddleX - stage.getCamera().position.x) * Gdx.graphics.getDeltaTime(),
                (rouletteLineMiddleY - stage.getCamera().position.y) * Gdx.graphics.getDeltaTime(),
                0
        );

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2, PugGame.WORLD_WIDTH - effectiveViewportWidth / 2);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2, PugGame.WORLD_WIDTH - effectiveViewportHeight / 2);
    }

    private void dragToRoulette(float effectiveViewportWidth, float effectiveViewportHeight) {
        //Difference on X between Roulette center and camera center.
        //Same to Y.
        float rouletteDx = roulette.getX() + roulette.getOriginX() - stage.getCamera().position.x;
        float rouletteDy = roulette.getY() + roulette.getOriginY() - stage.getCamera().position.y;

        Vector3 oldCameraPosition = new Vector3(camera.position);

        camera.position.x = MathUtils.clamp(
                camera.position.x + rouletteDx * Gdx.graphics.getDeltaTime(),
                effectiveViewportWidth / 2,
                PugGame.WORLD_WIDTH - effectiveViewportWidth / 2
        );

        camera.position.y = MathUtils.clamp(
                camera.position.y + rouletteDy / game.getAspectRatio() * Gdx.graphics.getDeltaTime(),
                effectiveViewportHeight / 2,
                PugGame.WORLD_WIDTH - effectiveViewportHeight / 2
        );

        roulette.moveBy((camera.position.x - oldCameraPosition.x), (camera.position.y - oldCameraPosition.y));
    }

    private Table createPauseScreen() {
        TextureRegion pauseBackgroundTexture = new TextureRegion(new Texture(Gdx.files.internal(PAUSE_BACKGROUND_TEXTURE)));
        final Table pauseTable = new Table();
        Image backgroundImage = new Image(pauseBackgroundTexture);

        setUiParameters(pauseTable);
        pauseTable.align(Align.bottom);

        backgroundImage.setScaling(Scaling.fill);
        backgroundImage.setSize(
                pauseBackgroundTexture.getRegionWidth() * PugGame.TEXTURE_SCALE,
                pauseBackgroundTexture.getRegionHeight() * PugGame.TEXTURE_SCALE
        );

        ImageButton continueButton = Utils.makeButton(PAUSE_CONTINUE_TEXTURE, PugGame.TEXTURE_SCALE);
        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                continueGame();
            }
        });
        ImageButton exitToMenuButton = Utils.makeButton(PAUSE_EXIT_TO_MENU_TEXTURE, PugGame.TEXTURE_SCALE);
        exitToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        pauseTable.padBottom(stage.getHeight() / 6);
        pauseTable.addActor(backgroundImage);
        pauseTable.add(continueButton).height(continueButton.getHeight()).width(continueButton.getWidth()).expandX();
        pauseTable.add(exitToMenuButton).height(exitToMenuButton.getHeight()).width(exitToMenuButton.getWidth()).expandX();
        pauseTable.setVisible(false);
        return pauseTable;
    }

    private void pauseGame() {
        game.isRunning = false;

        pauseContainer.setTouchable(Touchable.disabled);
        runningGameGroup.setTouchable(Touchable.disabled);
        pauseTable.setVisible(true);
    }

    private void continueGame() {
        game.isRunning = true;

        runningGameGroup.setTouchable(Touchable.enabled);
        pauseContainer.setTouchable(Touchable.childrenOnly);
        pauseTable.setVisible(false);
    }

    @Override
    public void pause() {
        pauseGame();
        //Save data
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
