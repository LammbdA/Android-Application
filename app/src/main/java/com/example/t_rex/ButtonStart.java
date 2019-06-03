package com.example.t_rex;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class ButtonStart extends GameButtons {

    private Bitmap image;
    private int x;
    private int y;

    public ButtonStart(Bitmap image) {
        this.image = image;
        x = GamePanel.WIDTH / 2 - this.image.getWidth() / 2;
        y = GamePanel.HEIGHT / 2 - this.image.getHeight() / 2;
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
