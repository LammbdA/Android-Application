package com.example.t_rex;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Background {

    private Bitmap image;
    private int x, y, dx;
    private int speed;

    public Background(Bitmap image, int score) {
        this.image = image;
        y = 0;
        x = 0;
        speed = GamePanel.WIDTH / 70 + score / 100;
        dx = -speed;
    }

    public void update() {
        x += dx;
        if (x < -GamePanel.WIDTH) {
            x = 0;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
        if (x < 0) {
            canvas.drawBitmap(image, x + GamePanel.WIDTH, y, null);
        }
    }
}
