package com.oink.walkingwithpug.screens;

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
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.oink.walkingwithpug.actors.Enemy;
import com.oink.walkingwithpug.actors.Map;
import com.oink.walkingwithpug.actors.Pug;
import com.oink.walkingwithpug.PugGame;
import com.oink.walkingwithpug.actors.Roulette;
import com.oink.walkingwithpug.Utils;

public class GameScreen implements Screen {
    private static final String MAP_TEXTURE = "game/random_map.png";
    private static final String BUTTON_PAUSE_TEXTURE = "game/buttons/pause";
    private static final String PAUSE_BACKGROUND_TEXTURE = "game/pause_menu/pause_background.png";
    private static final String PAUSE_CONTINUE_TEXTURE = "game/pause_menu/continue";
    private static final String PAUSE_EXIT_TO_MENU_TEXTURE = "game/pause_menu/exit_to_menu";
    private static final String LOSE_BACKGROUND_TEXTURE = "game/lose_menu/background.png";
    private static final String LOSE_EXIT_TO_MENU_TEXTURE = "game/lose_menu/exit_to_menu";
    private static final String LOSE_RESTART_TEXTURE = "game/lose_menu/restart";

    public static final float CAMERA_SCALING = 0.01f;
    public static final float CAMERA_MAX_ZOOM = 1.5f;
    public static final float CAMERA_MIN_ZOOM = 1.0f;
    public static final float ENEMY_CREATE_TIME = 3f;
    public static final float MAX_LINE_LENGTH = PugGame.GAME_VIEWPORT_WIDTH / 4;
    public static final int MAX_ENEMY_COUNT = 0;

    public PugGame game;
    public Stage stage;
    public Roulette roulette;
    public Pug pug;
    public ImageButton pauseButton;
    public Map map;

    Container pauseContainer;
    Label scaryLabel;
    Label poopLabel;
    Label peeLabel;

    float enemyTimer;
    Group enemiesGroup;
    Group runningGameGroup;
    VerticalGroup labelGroup;
    Table pauseTable;
    Table loseTable;

    public OrthographicCamera camera;
    private Container<VerticalGroup> labelContainer;

    GameScreen(final PugGame game) {
        this.game = game;

        //Making camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        //Making viewport and input processor
        stage = new Stage(new StretchViewport(
                PugGame.GAME_VIEWPORT_WIDTH,
                PugGame.GAME_VIEWPORT_HEIGHT * game.getAspectRatio(),
                camera
        ));
        Gdx.input.setInputProcessor(stage);

        //Creating map
        map = new Map(this);
        configureActors();
        addActorsToStage();

        //Initialize the position of camera at start
        camera.position.x = map.getHomePosition().x;
        camera.position.y = map.getHomePosition().y;

        enemyTimer = 0;

        game.isRunning = true;
        //stage.setDebugAll(true);
    }

    private void configureActors() {
        //Settings up the scales of pug and roulette
        pug = new Pug(this);
        roulette = new Roulette(this);

        //Create groups
        labelGroup = new VerticalGroup();
        enemiesGroup = new Group();
        runningGameGroup = new Group();

        //Create screens
        pauseTable = createPauseScreen();
        loseTable = createLoseScreen();

        createButtons();
        createLabels();
        pauseContainer = new Container<ImageButton>(pauseButton);
        labelContainer = new Container<VerticalGroup>(labelGroup);

        setUiParameters(labelContainer);
        setUiParameters(pauseContainer);

        labelGroup.align(Align.topRight);
        labelGroup.setScale(PugGame.MENU_TEXTURE_SCALE);
        labelGroup.setTransform(true);

        labelContainer.align(Align.topRight);

        pauseContainer.size(pauseButton.getWidth(), pauseButton.getHeight());
        pauseContainer.align(Align.topLeft);

        roulette.setCenterPosition(map.getHomePosition().x, map.getHomePosition().y);
        pug.setCenterPosition(map.getHomePosition().x, map.getHomePosition().y);
    }

    /**
     * Creating buttons for this stage.
     */
    private void createButtons() {
        pauseButton = Utils.makeButton(BUTTON_PAUSE_TEXTURE, PugGame.GAME_TEXTURE_SCALE);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pauseGame();
            }
        });
    }

    /**
     * Creating labels for this stage
     */
    private void createLabels() {
        scaryLabel = Utils.makeLabel("", game.font, Color.YELLOW);
        poopLabel = Utils.makeLabel("", game.font, Color.YELLOW);
        peeLabel = Utils.makeLabel("", game.font, Color.YELLOW);
    }

    /**
     * Add elements to screen in the correct order.
     */
    private void addActorsToStage() {
        labelGroup.addActor(scaryLabel);
        labelGroup.addActor(peeLabel);
        labelGroup.addActor(poopLabel);

        stage.addActor(runningGameGroup);
        runningGameGroup.addActor(enemiesGroup);
        runningGameGroup.addActor(pug);
        runningGameGroup.addActor(roulette);
        stage.addActor(pauseContainer);
        stage.addActor(labelContainer);
        stage.addActor(pauseTable);
        stage.addActor(loseTable);
    }

    /**
     * Setup start parameters like Transform, FillParent to true and origin to center.
     * @param group setting up parameters of this group
     */
    private void setUiParameters(WidgetGroup group) {
        group.setFillParent(true);
        group.setTransform(true);
        group.setOrigin(stage.getWidth() / 2, stage.getHeight() / 2);
    }

    @Override
    public void render(float delta) {
        //Gdx.gl20.glClearColor(0, 1, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.isRunning) {
            //Update camera position and ui position
            updateCamera();
            updateUi();

            //Update matrix for roulette line
            roulette.rouletteLine.setProjectionMatrix(stage.getCamera().combined);
            stage.act(delta);

            if (pug.getScaryLevel() >= Pug.MAX_SCARY_LEVEL) {
                loseGame();
            }

            //Creating enemies in runtime
            if (enemyTimer == ENEMY_CREATE_TIME && enemiesGroup.getChildren().size < MAX_ENEMY_COUNT) {
                enemyTimer = 0;
                enemiesGroup.addActor(createEnemy());
            }
            enemyTimer = Math.min(enemyTimer + delta, ENEMY_CREATE_TIME);
        }
        else {
            //Draw pause background
            camera.update();
        }

        //Drawing map and actors
        map.draw();
        stage.draw();
    }

    /**
     * Move and scales ui to left bottom corner of screen, updates info of ui.
     */
    private void updateUi() {
        //Update pug levels
        scaryLabel.setText("Scary: " + (int)pug.getScaryLevel());
        poopLabel.setText("Poop: " + (int)pug.getPoopLevel());
        peeLabel.setText("Pee: " + (int)pug.getPeeLevel());

        float bottomLeftCornerX = camera.position.x - stage.getWidth() / 2;
        float bottomLeftCornerY = camera.position.y - stage.getHeight() / 2;

        pauseContainer.setPosition(bottomLeftCornerX, bottomLeftCornerY);
        pauseContainer.setScale(camera.zoom);

        labelContainer.setPosition(bottomLeftCornerX, bottomLeftCornerY);
        labelContainer.setScale(camera.zoom);

        pauseTable.setPosition(bottomLeftCornerX, bottomLeftCornerY);
        pauseTable.setScale(camera.zoom);

        loseTable.setPosition(bottomLeftCornerX, bottomLeftCornerY);
        loseTable.setScale(camera.zoom);

        //Without this labels didn't work
        labelGroup.setOrigin(labelGroup.getWidth(), labelGroup.getHeight());
    }

    /**
     * Creates enemy at the random position.
     * @return new enemy
     */
    private Enemy createEnemy() {
        //Making coordinates for new enemy
        float newX = MathUtils.random(0, PugGame.WORLD_WIDTH);
        float newY = MathUtils.random(0, PugGame.WORLD_HEIGHT);
        if (newX >= stage.getCamera().position.x - stage.getWidth() / 2 && newX <= stage.getCamera().position.x + stage.getWidth() / 2) {
            newX += stage.getWidth() * 2;
        }
        if (newY >= stage.getCamera().position.y - stage.getHeight() / 2 && newY <= stage.getCamera().position.y + stage.getHeight() / 2) {
            newY += stage.getHeight() * 2;
        }
        return new Enemy(PugGame.GAME_TEXTURE_SCALE, newX, newY, this);
    }

    /**If player doesn't touch screen, camera translates to rouletteLineMiddle.
     * Else camera moves to roulette.
     */
    private void updateCamera() {
        //If roulette is moving, zooming up camera;
        //If staying, zooming down
        if (roulette.isDragging) {
            camera.zoom = Math.min(CAMERA_MAX_ZOOM, camera.zoom + CAMERA_SCALING);
        }
        else {
            camera.zoom = Math.max(CAMERA_MIN_ZOOM, camera.zoom - CAMERA_SCALING);
        }

        //Viewport scaled width and height.
        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        if (roulette.isDragging) {
            dragToRoulette(effectiveViewportWidth, effectiveViewportHeight, roulette.isDragging);
        }
        else {
            dragToRoulette(effectiveViewportWidth, effectiveViewportHeight, roulette.isDragging);
            //dragToLineMiddle(effectiveViewportWidth, effectiveViewportHeight);
        }

        stage.getCamera().update();
    }

    @Deprecated
    /**
     * Drags camera center to middle of roulette line center.
     */
    private void dragToLineMiddle(float effectiveViewportWidth, float effectiveViewportHeight) {
        //X and Y of roulette line middle.
        float rouletteLineMiddleX = (pug.getCenterX() + roulette.getCenterX()) / 2;
        float rouletteLineMiddleY = (pug.getCenterY() +roulette.getCenterY()) / 2;

        stage.getCamera().translate(
                (rouletteLineMiddleX - stage.getCamera().position.x) * Gdx.graphics.getDeltaTime(),
                (rouletteLineMiddleY - stage.getCamera().position.y) * Gdx.graphics.getDeltaTime(),
                0
        );

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2, PugGame.WORLD_WIDTH - effectiveViewportWidth / 2);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2, PugGame.WORLD_WIDTH - effectiveViewportHeight / 2);
    }

    /**
     * Drags camera center to roulette center.
     * @param effectiveViewportWidth viewport width with zooming
     * @param effectiveViewportHeight viewport height with zooming
     * @param isDragging state of roulette dragging
     */
    private void dragToRoulette(float effectiveViewportWidth, float effectiveViewportHeight, boolean isDragging) {
        //Difference on X between Roulette center and camera center.
        //Same to Y.
        float rouletteDx = roulette.getCenterX() - stage.getCamera().position.x;
        float rouletteDy = roulette.getCenterY() - stage.getCamera().position.y;

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

        if(isDragging) {
            roulette.moveBy((camera.position.x - oldCameraPosition.x), (camera.position.y - oldCameraPosition.y));
        }
    }

    /**
     * Creating and configuring positions to the pause screen.
     * @return Table with pause screen
     */
    private Table createPauseScreen() {
        TextureRegion pauseBackgroundTexture = new TextureRegion(new Texture(Gdx.files.internal(PAUSE_BACKGROUND_TEXTURE)));
        final Table pauseTable = new Table();
        Image backgroundImage = new Image(pauseBackgroundTexture);

        setUiParameters(pauseTable);
        pauseTable.align(Align.bottom);

        backgroundImage.setScaling(Scaling.fill);
        backgroundImage.setSize(
                pauseBackgroundTexture.getRegionWidth() * PugGame.GAME_TEXTURE_SCALE,
                pauseBackgroundTexture.getRegionHeight() * PugGame.GAME_TEXTURE_SCALE
        );

        ImageButton continueButton = Utils.makeButton(PAUSE_CONTINUE_TEXTURE, PugGame.GAME_TEXTURE_SCALE);
        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                continueGame();
            }
        });
        ImageButton exitToMenuButton = Utils.makeButton(PAUSE_EXIT_TO_MENU_TEXTURE, PugGame.GAME_TEXTURE_SCALE);
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

    /**
     * Creating and configuring positions to the lose screen.
     * @return Table with lose screen
     */
    private Table createLoseScreen() {
        TextureRegion loseBackgroundTexture = new TextureRegion(new Texture(Gdx.files.internal(LOSE_BACKGROUND_TEXTURE)));
        final Table loseTable = new Table();
        Image backgroundImage = new Image(loseBackgroundTexture);

        setUiParameters(loseTable);
        loseTable.align(Align.bottom);

        backgroundImage.setScaling(Scaling.fill);
        backgroundImage.setSize(
                loseBackgroundTexture.getRegionWidth() * PugGame.GAME_TEXTURE_SCALE,
                loseBackgroundTexture.getRegionHeight() * PugGame.GAME_TEXTURE_SCALE
        );

        ImageButton restartButton = Utils.makeButton(LOSE_RESTART_TEXTURE, PugGame.GAME_TEXTURE_SCALE);
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });

        ImageButton exitToMenuButton = Utils.makeButton(LOSE_EXIT_TO_MENU_TEXTURE, PugGame.GAME_TEXTURE_SCALE);
        exitToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        loseTable.padBottom(stage.getHeight() / 6);
        loseTable.addActor(backgroundImage);
        loseTable.add(restartButton).height(restartButton.getHeight()).width(restartButton.getWidth()).expandX();
        loseTable.add(exitToMenuButton).height(exitToMenuButton.getHeight()).width(exitToMenuButton.getWidth()).expandX();
        loseTable.setVisible(false);
        return loseTable;
    }

    private void loseGame() {
        game.isRunning = false;

        pauseContainer.setTouchable(Touchable.disabled);
        runningGameGroup.setTouchable(Touchable.disabled);
        loseTable.setVisible(true);
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
