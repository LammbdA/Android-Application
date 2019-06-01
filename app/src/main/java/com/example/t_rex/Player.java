package com.example.t_rex;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;

public class Player extends GameObject {

    private Bitmap image;
    private final double JUMP_POWER;
    private final double GRAVITY = 1;
    private int score;
    private boolean jump;
    private boolean playing;
    private boolean onGround;
    private Animation animation = new Animation();
    private long startTime;
    private boolean showText;

    public Player(Bitmap res) {
        height = res.getHeight();
        width = res.getWidth() / 6;
        JUMP_POWER = height / 10;
        x = 100;
        y = (int) (GamePanel.HEIGHT - height * 1.3);
        dy = 0;
        score = 0;
        showText = false;

        Bitmap[] image = new Bitmap[6];
        this.image = res;
        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(this.image, i * width, 0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(100);
        startTime = System.nanoTime();
    }

    public void update(ArrayList<GameObject> objects) {
        long elapsed = (System.nanoTime() - startTime) / 1000000;
        if (elapsed > 10) {
            score++;
            startTime = System.nanoTime();
        }
        animation.update();

        if (jump && onGround)
            dy = -JUMP_POWER;
        if (!onGround)
            dy += GRAVITY;

        onGround = false;
        y += (int) Math.round(dy);
        collide(objects);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(), x, y, null);
    }

    public void collide(ArrayList<GameObject> object) {
        for (GameObject obj : object) {
            if (obj != this) {
                if (collision(this, obj)) {
                    if (obj instanceof Missile) {
                        score += 100;
                    } else {
                        if (obj instanceof Kaktus) {
                            playing = false;
                            showText = true;
                        } else {
                            dy = 0;
//                        this.getRectangle().bottom = obj.getRectangle().top;
                            y = obj.y - height;
                            onGround = true;

                        }
                    }
                }
            }
        }
    }

    public boolean collision(GameObject a, GameObject b) {
        return Rect.intersects(a.getRectangle(), b.getRectangle());
    }

    public int getScore() {
        return score;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void resetScore() {
        score = 0;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }
}
