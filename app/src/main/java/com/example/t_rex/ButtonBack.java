package com.example.t_rex;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class ButtonBack extends GameButtons{

    private Bitmap image;
    private int x;
    private int y;

    public ButtonBack(Bitmap image) {
        this.image = image;
        x = GamePanel.WIDTH - GamePanel.WIDTH / 8;
        y = GamePanel.HEIGHT - GamePanel.HEIGHT / 5;
        width = this.image.getWidth();
        height = this.image.getHeight();
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }

    @Override
    public Rect getRectangle() {
        return new Rect(x, y, x + width, y + height);
    }
}
