package com.oink.walkingwithpug.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Unit extends Actor {
    /**
     * Returns distance to other unit.
     * @param unit Other unit
     * @return distance to unit
     */
    public float getDistanceTo(Unit unit) {
        return Vector2.dst(getCenterX(), getCenterY(), unit.getCenterX(), unit.getCenterY());
    }

    /**
     * Returns angle to unit.
     * @param unit Other unit
     */
    public float getAngleTo(Unit unit) {
        Vector2 vec = new Vector2(
                (unit.getCenterX() - getCenterX()),
                (unit.getCenterY() - getCenterY()));
        return vec.angle();
    }

    /**
     * Returns vector directed to unit.
     * @param unit Other unit
     */
    public Vector2 getVectorTo(Unit unit) {
        return new Vector2(unit.getCenterX() - getCenterX(), unit.getCenterY() - getCenterY());
    }

    public void setCenterX(float x) {
        setX(x - getOriginX());
    }

    public void setCenterY(float y) {
        setY(y - getOriginY());
    }

    public void setCenterPosition(float x, float y) {
        setCenterX(x);
        setCenterY(y);
    }
    public float getCenterX()
    {
        return getX() + getOriginX();
    }

    public float getCenterY()
    {
        return getY() + getOriginY();
    }
}
