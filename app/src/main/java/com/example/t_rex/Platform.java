package com.example.t_rex;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Platform extends GameObject {

    private Bitmap image;
    private int xVel;
    private int speed;

    public Platform(Bitmap image, int score) {
        width = image.getWidth();
        height = image.getHeight();
        this.image = image;
        x = 0;
        xVel = 0;
        y = GamePanel.HEIGHT - height;
        speed = 18 + score / 100;
        dx = -speed;
    }

    public void update() {
        xVel += dx;
        if (xVel < -GamePanel.WIDTH) {
            xVel = 0;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, xVel, y, null);
        if (xVel < 0) {
            canvas.drawBitmap(image, xVel + GamePanel.WIDTH, y, null);
        }
    }

}
