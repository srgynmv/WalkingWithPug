package com.oink.walkingwithpug.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Json;
import com.oink.walkingwithpug.PugGame;
import com.oink.walkingwithpug.screens.GameScreen;

import java.util.ArrayList;
import java.util.Iterator;

public class Map extends TiledMap {
    private static final String MAP_NAME = "game/map.tmx";
    public static final String PUG_HOME_NAME = "dogHome";
    public static final String GRANDMA_HOME_NAME = "grandmaHome";
    private static int TILE_SIZE;
    private final OrthogonalTiledMapRenderer renderer;

    private TiledMap map;
    private GameScreen screen;

    public Map(GameScreen screen) {
        this.screen = screen;
        map = new TmxMapLoader().load(MAP_NAME);
        TILE_SIZE = map.getProperties().get("tilewidth", Integer.class);
        renderer = new OrthogonalTiledMapRenderer(map);
        renderer.setView(screen.camera);
    }

    //TODO this methods:
    public void draw() {
        renderer.setView(screen.camera);
        renderer.render();
    }

    public Vector2 getHomePosition() {
        Rectangle homeRectangle = getRectangle("dogHome");

        float centerX = homeRectangle.getX() + homeRectangle.getWidth() / 2;
        float centerY = homeRectangle.getY() + homeRectangle.getHeight() / 2;

        return new Vector2(centerX, centerY);
    }

    public Rectangle getRectangle(String rectangleName) {
        MapLayer pointsLayer = map.getLayers().get("points");
        RectangleMapObject object = (RectangleMapObject) pointsLayer.getObjects().get(rectangleName);
        return object.getRectangle();
    }

    public boolean canPeeOn(float x, float y) {
        TiledMapTileLayer peeLayer = (TiledMapTileLayer)map.getLayers().get("peeLayer");
        TiledMapTileLayer.Cell cell = peeLayer.getCell((int)x / TILE_SIZE, (int)y / TILE_SIZE);
        return (cell != null);
    }

    public boolean canPeeOn(Vector2 v) {
        return canPeeOn(v.x, v.y);
    }

    public Object getObstacleOn(float x, float y) {
        TiledMapTileLayer obstacleLayer = (TiledMapTileLayer)map.getLayers().get("obstacles");
        TiledMapTileLayer.Cell cell = obstacleLayer.getCell((int)x / TILE_SIZE, (int)y / TILE_SIZE);
        return cell;
    }

    public Object getObstacleOn(Vector2 v) {
        return getObstacleOn(v.x, v.y);
    }
}
