package com.example.t_rex;

import android.graphics.Canvas;
import android.graphics.Rect;

public abstract class GameButtons {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public abstract void draw(Canvas canvas);

    public abstract Rect getRectangle();
}
