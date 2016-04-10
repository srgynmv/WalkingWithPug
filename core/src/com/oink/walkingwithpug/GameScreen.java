package com.oink.walkingwithpug;

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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.StretchViewport;


public class GameScreen implements Screen {

    PugGame game;
    Stage stage;
    Roulette roulette;
    Pug pug;
    ImageButton pauseButton;
    //DEBUG
    Texture map;
    Table table;
    Label scaryLabel;
    Label poopLabel;
    Label peeLabel;
    //END_DEBUG

    float enemyTimer;
    Group enemiesGroup;
    Group runningGameGroup;
    Table pauseTable;

    float textureScale;

    int maxEnemyCount;

    OrthographicCamera camera;

    float maxLineLengthSquared;

    GameScreen(final PugGame game) {
        Gdx.app.log("INFO", "In a GameScreen constructor");
        this.game = game;
        //Settings up the scales of pug and roulette
        pug = new Pug(0.4f, this);
        roulette = new Roulette(0.25f, this);

        //DEBUG
        map = new Texture(Gdx.files.internal("random_map.png"));
        game.isRunning = true;

        //Making camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        //Making viewport and input processor
        stage = new Stage(new StretchViewport(game.worldWidth * game.viewportRatio, game.worldHeight * game.viewportRatio * game.ratio, camera));
        Gdx.input.setInputProcessor(stage);

        textureScale = stage.getWidth() / 1920f;

        pauseButton = PugGame.makeButton("pause", stage.getWidth() / 1920f);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pauseGame();
            }
        });

        Label.LabelStyle style = new Label.LabelStyle(game.font, Color.YELLOW);
        scaryLabel = new Label("Scary: 0", style);
        poopLabel = new Label("Pee: 0", style);
        peeLabel = new Label("Poop: 0", style);

        table = new Table();
        //Align table on the upper left corner
        table.align(Align.topLeft);
        table.setFillParent(true);
        table.setTransform(true);
        table.setOrigin(stage.getWidth()/2,stage.getHeight()/2);
        table.add(pauseButton).width(pauseButton.getWidth()).height(pauseButton.getHeight());
        table.add(scaryLabel).expandX().right().padRight(stage.getWidth() / 10);
//        table.row();
//        table.add(peeLabel).expandX().right().padRight(stage.getWidth() / 10);
//        table.row();
//        table.add(poopLabel).expandX().right().padRight(stage.getWidth() / 10);

        pug.setX(stage.getWidth() / 2 - pug.getWidth() / 2);

        roulette.setX(stage.getWidth() / 2 - roulette.getWidth() / 2);
        roulette.setY(stage.getHeight() / 2 - roulette.getHeight() / 2);

        enemiesGroup = new Group();
        runningGameGroup = new Group();
        pauseTable = createPauseScreen();

        enemyTimer = 0;

        stage.addActor(runningGameGroup);
        runningGameGroup.addActor(enemiesGroup);
        runningGameGroup.addActor(pug);
        runningGameGroup.addActor(roulette);
        stage.addActor(table);
        stage.addActor(pauseTable);


        maxLineLengthSquared = stage.getWidth() / 4;
        maxLineLengthSquared *= maxLineLengthSquared;

        maxEnemyCount = 3;

        Gdx.app.log("CAMERA ZOOM", "" + camera.zoom);

        //stage.setDebugAll(true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0.5f, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.isRunning) {
            //Checks, need to move camera or not
            updateCamera();

            //Update matrix for roulette line
            roulette.rouletteLine.setProjectionMatrix(stage.getCamera().combined);

            //Update pug levels
            scaryLabel.setText("Scary: " + pug.getScaryLevel());
            stage.act(delta);

            //DEBUG
            table.setPosition(camera.position.x - stage.getWidth() / 2, camera.position.y - stage.getHeight() / 2);
            table.setScale(camera.zoom);
            pauseTable.setPosition(camera.position.x - stage.getWidth() / 2, camera.position.y - stage.getHeight() / 2);
            pauseTable.setScale(camera.zoom);
            //Drawing map

            //TODO Make scary works better!
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
        stage.getBatch().draw(map, 0, 0, game.worldWidth, game.worldHeight);
        stage.getBatch().end();

        stage.draw();
    }

    private Enemy createEnemy() {
        //Making coordinates for new enemy
        Gdx.app.log("Camera x: ", "" + stage.getCamera().position.x);
        Gdx.app.log("Camera y: ", "" + stage.getCamera().position.y);

        float newX = MathUtils.random(0, game.worldWidth);
        float newY = MathUtils.random(0, game.worldHeight);
        if (newX >= stage.getCamera().position.x - stage.getWidth() / 2 && newX <= stage.getCamera().position.x + stage.getWidth() / 2) newX += stage.getWidth() * 2;
        if (newY >= stage.getCamera().position.y - stage.getHeight() / 2 && newY <= stage.getCamera().position.y + stage.getHeight() / 2) newY += stage.getHeight() * 2;
        Gdx.app.log("INFO", "Add new enemy at X: " + newX + " and Y: " + newY);
        return new Enemy(0.7f, newX, newY, this);
    }

    private void updateCamera() {
        float cameraScaling = 0.01f;
        //If player doesn't touch screen, camera translates to rouletteLineMiddle.
        //Else camera moves to roulette.
        //TODO Change 10 with cameraSize - depending value

        //Difference on X between Roulette center and camera center.
        //Same to Y.
        float rouletteDx = roulette.getX() + roulette.getOriginX() - stage.getCamera().position.x;
        float rouletteDy = roulette.getY() + roulette.getOriginY() - stage.getCamera().position.y;

        //X and Y of roulette line middle.
        float rouletteLineMiddleX = (pug.getX() + pug.getOriginX() + roulette.getX() + roulette.getOriginX()) / 2;
        float rouletteLineMiddleY = (pug.getY() + pug.getOriginY() +roulette.getY() + roulette.getOriginY()) / 2;

        if (roulette.isDragging) {
            //If roulette is moving, zooming up camera
            camera.zoom = Math.min(1.5f, camera.zoom + cameraScaling);
        }
        else {
            //If staying, zooming down
            camera.zoom = Math.max(1.0f, camera.zoom - cameraScaling);
        }

        //Viewport scaled width and height.
        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        Vector3 oldCameraPosition = new Vector3(camera.position);

        if (roulette.isDragging) {
            camera.position.x = MathUtils.clamp(
                    camera.position.x + rouletteDx * Gdx.graphics.getDeltaTime(),
                    effectiveViewportWidth / 2,
                    game.worldWidth - effectiveViewportWidth / 2
            );

            camera.position.y = MathUtils.clamp(
                    camera.position.y + rouletteDy / game.ratio * Gdx.graphics.getDeltaTime(),
                    effectiveViewportHeight / 2,
                    game.worldWidth - effectiveViewportHeight / 2
            );

            roulette.moveBy((camera.position.x - oldCameraPosition.x), (camera.position.y - oldCameraPosition.y));
            //Gdx.app.log("INFO", "Moving by: " + (camera.position.x - oldCameraPosition.x) + " " + (camera.position.y - oldCameraPosition.y));
        }
        else {
            stage.getCamera().translate(
                    (rouletteLineMiddleX - stage.getCamera().position.x) * Gdx.graphics.getDeltaTime(),
                    (rouletteLineMiddleY - stage.getCamera().position.y) * Gdx.graphics.getDeltaTime(),
                    0
            );

            camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2, game.worldWidth - effectiveViewportWidth / 2);
            camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2, game.worldWidth - effectiveViewportHeight / 2);
        }

//        Gdx.app.log("CAMERA X", "" + camera.position.x);
//        Gdx.app.log("CAMERA Y", "" + camera.position.y);
//        Gdx.app.log("CAMERAS EQUALS", (camera.position.equals(oldCameraPosition)?"TRUE":"FALSE"));
        stage.getCamera().update();
    }

    private Table createPauseScreen() {
        final Table pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseTable.setTransform(true);
        pauseTable.align(Align.bottom);
        pauseTable.setOrigin(stage.getWidth() / 2, stage.getHeight() / 2);

        TextureRegion pauseBackgroundTexture = new TextureRegion(new Texture(Gdx.files.internal("pause_background.png")));

        Image backgroundImage = new Image(pauseBackgroundTexture);
        backgroundImage.setScaling(Scaling.fill);
        backgroundImage.setSize(
                pauseBackgroundTexture.getRegionWidth() * textureScale,
                pauseBackgroundTexture.getRegionHeight() * textureScale
        );

        ImageButton continueButton = PugGame.makeButton("continue", textureScale);
        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                continueGame();
            }
        });
        ImageButton exitToMenuButton = PugGame.makeButton("exit_to_menu", textureScale);
        exitToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        //pauseTable.add(backgroundImage).height(backgroundImage.getHeight()).width(backgroundImage.getWidth());
        pauseTable.padBottom(stage.getHeight() / 6);
        pauseTable.addActor(backgroundImage);
        pauseTable.add(continueButton).height(continueButton.getHeight()).width(continueButton.getWidth()).expandX();
        pauseTable.add(exitToMenuButton).height(exitToMenuButton.getHeight()).width(exitToMenuButton.getWidth()).expandX();
        pauseTable.setVisible(false);
        return pauseTable;
    }

    private void pauseGame() {
        game.isRunning = false;

        table.setTouchable(Touchable.disabled);
        runningGameGroup.setTouchable(Touchable.disabled);
        pauseTable.setVisible(true);
    }

    private void continueGame() {
        game.isRunning = true;

        runningGameGroup.setTouchable(Touchable.enabled);
        table.setTouchable(Touchable.childrenOnly);
        pauseTable.setVisible(false);
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
        pauseGame();
        //Save data
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
