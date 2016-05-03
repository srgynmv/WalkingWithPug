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
    private static String MAP_NAME = "game/map.tmx";
    private static int TILE_SIZE;

    private TiledMap map;
    private GameScreen screen;

    public Map(GameScreen screen) {
        this.screen = screen;
        map = new TmxMapLoader().load(MAP_NAME);
        TILE_SIZE = map.getProperties().get("tilewidth", Integer.class);
    }

    //TODO this methods:
    public void draw(Batch batch) {
        OrthogonalTiledMapRenderer renderer = new OrthogonalTiledMapRenderer(map, batch);

        renderer.setView(screen.camera);
        renderer.render();
    }

    public Vector2 getHomePosition() {
        MapLayer pointsLayer = map.getLayers().get("points");
        RectangleMapObject object = (RectangleMapObject) pointsLayer.getObjects().get("homePoint");

        float centerX = object.getRectangle().getX() + object.getRectangle().getWidth() / 2;
        float centerY = object.getRectangle().getY() + object.getRectangle().getHeight() / 2;

        return new Vector2(centerX, centerY);
    }

    public boolean canPeeOn(float x, float y) {
        TiledMapTileLayer peeLayer = (TiledMapTileLayer)map.getLayers().get("peeLayer");
        TiledMapTileLayer.Cell cell = peeLayer.getCell((int)x / TILE_SIZE, (int)y / TILE_SIZE);
        return (cell != null);
    }

    public Object getObstacleOn(float x, float y) {
        TiledMapTileLayer obstacleLayer = (TiledMapTileLayer)map.getLayers().get("obstacles");
        TiledMapTileLayer.Cell cell = obstacleLayer.getCell((int)x / TILE_SIZE, (int)y / TILE_SIZE);
        return cell;
    }
}
