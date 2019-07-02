package com.example.t_rex;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Monet extends GameObject {

    private int speed;
    private Bitmap spritesheet;

    public Monet(Bitmap res, int x, int score) {
        width = res.getWidth();
        height = res.getHeight();
        this.x = x;
        y = (GamePanel.HEIGHT - height * 4);
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
    public int getWidth() {
        return width - 10;
    }

    public Rect getRectangle() {
        return new Rect(x + 15, y + 15, x + width - 15, y + height - 15);
    }
}
