package com.example.t_rex;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Missile extends GameObject {
    private int speed;
    private Animation animation = new Animation();
    private Bitmap image;

    public Missile(Bitmap image, int x, int y, int score) {
        this.x = x;
        this.y = y;
        this.image = image;
        width = this.image.getWidth() / 2;
        height = this.image.getHeight();

        speed = GamePanel.WIDTH / 40 + score / 100;

        Bitmap[] img = new Bitmap[2];
        for (int i = 0; i < img.length; i++) {
            img[i] = Bitmap.createBitmap(this.image, i * width, 0, width, height);
        }

        animation.setFrames(img);
        animation.setDelay(100);
    }

    public void update() {
        x -= speed;
        animation.update();
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(), x, y, null);
    }

    @Override
    public int getWidth() {
        return width - 10;
    }

}