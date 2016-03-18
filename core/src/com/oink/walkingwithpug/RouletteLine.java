package com.oink.walkingwithpug;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

class RouletteLine {
    final float lineWidth = 5;
    ShapeRenderer renderer;
    Pug pug;
    Roulette roulette;

    RouletteLine(Pug pug, Roulette roulette) {
        renderer = new ShapeRenderer();
        this.pug = pug;
        this.roulette = roulette;
    }

    void setProjectionMatrix(Matrix4 matrix) {
        renderer.setProjectionMatrix(matrix);
    }

    /**
     * Drawing line with width = lineWidth
     * @param batch
     */
    void draw(Batch batch) {
        float rouletteCenterDX = roulette.reversed ? -roulette.getOriginX() : roulette.getOriginX();
        batch.end();

        renderer.begin(ShapeRenderer.ShapeType.Line);

        //Drawing lines
        for (float i = -lineWidth / 2; i <= lineWidth / 2; ++i) {
            renderer.line(
                    pug.getCenterX() + i,
                    pug.getCenterY(),
                    roulette.getCenterX() + rouletteCenterDX,
                    roulette.getCenterY() + i
            );
        }
        renderer.end();

        batch.begin();
    }
}
