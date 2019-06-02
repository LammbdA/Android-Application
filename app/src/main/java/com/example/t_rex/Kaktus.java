package com.example.t_rex;

import android.graphics.Bitmap;
import android.graphics.Canvas;

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
}
