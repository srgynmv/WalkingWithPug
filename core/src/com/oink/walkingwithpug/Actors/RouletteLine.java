package com.oink.walkingwithpug.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class RouletteLine {
    private static final float LINE_WIDTH = 5;

    ShapeRenderer renderer;
    Pug pug;
    Roulette roulette;

    RouletteLine(com.oink.walkingwithpug.actors.Pug pug, Roulette roulette) {
        renderer = new ShapeRenderer();
        this.pug = pug;
        this.roulette = roulette;
    }

    public void setProjectionMatrix(Matrix4 matrix) {
        renderer.setProjectionMatrix(matrix);
    }

    /**
     * Drawing line with width = LINE_WIDTH
     * @param batch
     */
    void draw(Batch batch) {
        float rouletteCenterDX = roulette.reversed ? -roulette.getOriginX() : roulette.getOriginX();
        Vector2 pugNeckPoint = pug.getPugNeckPoint();

        batch.end();
        renderer.begin(ShapeRenderer.ShapeType.Line);

        //Drawing lines
        for (float i = -LINE_WIDTH / 2; i <= LINE_WIDTH / 2; ++i) {
            renderer.line(
                    pugNeckPoint.x,
                    pugNeckPoint.y,
                    roulette.getCenterX() + rouletteCenterDX,
                    roulette.getCenterY() + i
            );
        }
        renderer.end();

        batch.begin();
    }
}
