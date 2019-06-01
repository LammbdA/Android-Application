package com.example.t_rex;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    public static int WIDTH;
    public static int HEIGHT;
    private long missileStartTime;
    private long kaktusStartTime;
    private MainThread thread;
    private Player player;
    private Background bg;
    private Platform platform;
    private ArrayList<GameObject> gameObjects;
    private Random rand = new Random();
    private int best;
    private long startReset;
    private boolean reset;
    private boolean started;
    private boolean newGameCreated;
    private MediaPlayer soundJump, soundStuck, soundMap;

    public GamePanel(Context context) {
        super(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        WIDTH = display.getWidth();
        HEIGHT = display.getHeight();
        soundJump = MediaPlayer.create(context, R.raw.jump);
        soundStuck = MediaPlayer.create(context, R.raw.stuck);
        soundMap = MediaPlayer.create(context, R.raw.game);
        getHolder().addCallback(this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.rex));
        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), player.getScore());
        platform = new Platform(BitmapFactory.decodeResource(getResources(), R.drawable.platform), player.getScore());
        missileStartTime = System.nanoTime();
        kaktusStartTime = System.currentTimeMillis() / 1000;
        gameObjects = new ArrayList<>();
        gameObjects.add(player);
        gameObjects.add(platform);
        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int counter = 0;
        while (retry && counter < 1000) {
            counter++;
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!player.isPlaying() && newGameCreated && reset)
                player.setPlaying(true);
            if (player.isPlaying()) {
                if (!started)
                    started = true;
                reset = false;
                player.setJump(true);
                soundPlay(soundJump);
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            player.setJump(false);
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void update() {
        if (player.isPlaying()) {
            soundPlay(soundMap);
            player.update(gameObjects);
            platform.update();
            bg.update();

//            long missileElapsed = (System.nanoTime() - missileStartTime) / 1000000;
            long kaktusElapsed = System.currentTimeMillis() / 1000;
            int randomSecond = rand.nextInt(8) + 3;

            if (kaktusElapsed - kaktusStartTime >= randomSecond) {
                gameObjects.add(new Kaktus(BitmapFactory.decodeResource(getResources(), R.drawable.kaktus),
                        WIDTH + 10, player.getScore()));
                kaktusStartTime = System.currentTimeMillis() / 1000;
            }
            for (GameObject object : gameObjects) {
                if (object instanceof Kaktus) {
                    ((Kaktus) object).update();
                    if (object.getX() < -100) {
                        gameObjects.remove(object);
                        break;
                    }
                }
            }
        } else {
            soundPlay(soundStuck);
            if (!reset) {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
            }
            long resetElapsed = (System.nanoTime() - startReset) / 1000000;
            if (resetElapsed > 2500 && !newGameCreated)
                newGame();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas != null) {
            super.draw(canvas);
            bg.draw(canvas);
            player.draw(canvas);
            platform.draw(canvas);
            for (GameObject object : gameObjects) {
                if (object instanceof Missile)
                    object.draw(canvas);
                if (object instanceof Kaktus)
                    object.draw(canvas);
            }
            drawText(canvas);
            if (player.isShowText())
                drawLost(canvas);
        }
    }

    public void newGame() {
        player.setShowText(false);
        player.resetScore();
        player.setY((int) (HEIGHT - player.height * 1.3));
        for (Iterator<GameObject> it = gameObjects.iterator(); it.hasNext(); ) {
            GameObject aDrugStrength = it.next();
            if (!(aDrugStrength instanceof Player) && !(aDrugStrength instanceof Platform))
                it.remove();
        }
        if (player.getScore() > best)
            best = player.getScore();
        newGameCreated = true;
    }

    public void drawText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(WIDTH / 40);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DISTANCE: " + (player.getScore()), 30, 100, paint);
        canvas.drawText("BEST: " + best, WIDTH - 300, 100, paint);

        if (!player.isPlaying() && newGameCreated && reset) {
            Paint paint1 = new Paint();
            paint1.setTextSize(WIDTH / 30);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PRESS TO START", WIDTH / 2, HEIGHT / 2, paint1);
        }
    }

    public void drawLost(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(WIDTH / 30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("YOU LOST!", WIDTH / 2, HEIGHT / 2, paint);
        canvas.drawText("YOUR SCORE WAS: " + player.getScore(), WIDTH / 2, HEIGHT / 2 + 100, paint);
    }

    public void soundPlay(MediaPlayer sound) {
        sound.start();
    }
}
