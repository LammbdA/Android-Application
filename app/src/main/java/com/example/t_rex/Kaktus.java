package com.example.t_rex;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Kaktus extends GameObject {
    private int speed;
    private Bitmap spritesheet;

    public Kaktus(Bitmap res, int x, int score) {
        width = res.getWidth();
        height = res.getHeight();
        this.x = x;
        y = (int) (GamePanel.HEIGHT - height * 1.4);
        speed = GamePanel.WIDTH / 70 + score / 100;
        spritesheet = res;
    }

    public void update() {
        x -= speed;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(spritesheet, x, y, null);
    }

    @Override
    public int getY() {
        return super.getY();
    }

    @Override
    public int getWidth() {
        return width - 10;
    }

    public Rect getRectangle() {
        return new Rect(x + GamePanel.WIDTH / 40, y + GamePanel.WIDTH / 40, x + width - GamePanel.WIDTH / 40, y + height - GamePanel.WIDTH / 40);
    }
}
