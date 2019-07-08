package com.example.t_rex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    // Display
    public static int WIDTH;
    public static int HEIGHT;

    // Position for bird spawn
    private final int FIRST_POSITION;
    private final int SECOND_POSITION;

    public static boolean onPause = false;
    private long missileStartTime;
    private long kaktusStartTime;
    private boolean kaktusCanAppear;

    private MainThread thread;
    // Game objects
    private Player player;
    private Background bg;
    private Background dark;
    private Platform platform;

    // Game buttons
    private ButtonStart buttonStart;
    private ButtonSound buttonSound;
    private ButtonSkins buttonSkins;
    private ButtonBack buttonBack;
    private boolean btnWasClicked = false;
    private boolean inSkinsMenu = false;

    private ArrayList<GameObject> gameObjects;
    private Random rand = new Random();

    private int best;
    private int money;

    private long startReset;
    private boolean reset;
    private boolean started;
    private boolean newGameCreated;
    private boolean btnPressed = false;
    private boolean wasCreated = false;
    private MediaPlayer soundMap;

    // Database
    private Database database;
    private SQLiteDatabase db;
    private ContentValues contentValues;

    private boolean drawGame;
    private boolean drawMenu;
    private boolean drawSkins;

    private boolean darkMode;
    private int darkModeTimer;

    public GamePanel(Context context) {
        super(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        WIDTH = display.getWidth();
        HEIGHT = display.getHeight();

        FIRST_POSITION = HEIGHT - HEIGHT / 5;
        SECOND_POSITION = HEIGHT / 2;

        soundMap = MediaPlayer.create(context, R.raw.game);

        database = new Database(context);

        getHolder().addCallback(this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!wasCreated) {
            wasCreated = true;
            player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.rex));
            bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), player.getScore());
            dark = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.backgroundark), player.getScore());
            platform = new Platform(BitmapFactory.decodeResource(getResources(), R.drawable.platform), player.getScore());
            buttonStart = new ButtonStart(BitmapFactory.decodeResource(getResources(), R.drawable.play));
            buttonSkins = new ButtonSkins(BitmapFactory.decodeResource(getResources(), R.drawable.skins));
            buttonBack = new ButtonBack(BitmapFactory.decodeResource(getResources(), R.drawable.back));
            gameObjects = new ArrayList<>();
            gameObjects.add(player);
            gameObjects.add(platform);

            drawMenu = true;
            drawGame = false;
            drawSkins = false;

            darkMode = false;
            darkModeTimer = 0;
        }
        if (!btnPressed) {
            buttonSound = new ButtonSound(BitmapFactory.decodeResource(getResources(), R.drawable.soundon));
            soundMap.start();
        } else
            buttonSound = new ButtonSound(BitmapFactory.decodeResource(getResources(), R.drawable.soundoff));

        missileStartTime = System.currentTimeMillis() / 1000;
        kaktusStartTime = System.currentTimeMillis() / 1000;
        kaktusCanAppear = true;

        db = database.getWritableDatabase();
        contentValues = new ContentValues();
        Cursor cursor = db.query(Database.TABLE_GAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int score = cursor.getColumnIndex(Database.KEY_SCORE);
            int money = cursor.getColumnIndex(Database.KEY_MONEY);
            best = cursor.getInt(score);
            this.money = cursor.getInt(money);
        }
        cursor.close();

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
        if (!onPause) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (collisionWithButtons(buttonStart, x, y) && !btnWasClicked) {
                    drawMenu = false;
                    drawGame = true;
                    drawSkins = false;
                    btnWasClicked = true;

                    if (!player.isPlaying() && newGameCreated && reset)
                        player.setPlaying(true);
                } else {
                    if (collisionWithButtons(buttonSkins, x, y) && !btnWasClicked) {
                        drawMenu = false;
                        drawGame = false;
                        drawSkins = true;
                        btnWasClicked = true;
                        inSkinsMenu = true;
                    } else {
                        if (collisionWithButtons(buttonBack, x, y) && inSkinsMenu) {
                            drawMenu = true;
                            drawGame = false;
                            drawSkins = false;
                            btnWasClicked = false;
                            inSkinsMenu = false;
                        } else {
                            if (collisionWithButtons(buttonSound, x, y) && !btnWasClicked) {
                                if (!btnPressed) {
                                    btnPressed = true;
                                    soundMap.pause();
                                    buttonSound.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.soundoff));
                                } else {
                                    btnPressed = false;
                                    soundMap.start();
                                    buttonSound.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.soundon));
                                }
                            }
                        }
                    }
                }
                if (player.isPlaying()) {
                    if (!started)
                        started = true;
                    reset = false;
                    player.setJump(true);
                }
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                player.setJump(false);
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    public void update() {
        if (!onPause) {
            if (player.isPlaying()) {
                if (player.getScore() - darkModeTimer >= 1000) {
                    darkMode = !darkMode;
                    darkModeTimer = player.getScore();
                }
                player.update(gameObjects);
                platform.update();
                if (!darkMode)
                    bg.update();
                else
                    dark.update();

                long kaktusElapsed = System.currentTimeMillis() / 1000;
                int randomSecond = rand.nextInt(4) + 2;
                spawnKaktus(randomSecond, kaktusElapsed);

                long missileElapsed = System.currentTimeMillis() / 1000;
                randomSecond = rand.nextInt(15) + 7;
                spawnMissile(randomSecond, missileElapsed);

            } else {
                if (!reset) {
                    newGameCreated = false;
                    startReset = System.nanoTime();
                    reset = true;
                }
                long resetElapsed = (System.nanoTime() - startReset) / 1000000;
                if (resetElapsed > 2500 && !newGameCreated) {
                    drawGame = false;
                    drawMenu = true;
                    btnWasClicked = false;
                    newGame();
                }
            }
        } else
            soundMap.pause();
    }

    @Override
    public void draw(Canvas canvas) {
        if (!onPause) {
            super.draw(canvas);
            if (drawMenu)
                drawMenu(canvas);
            if (drawGame)
                drawGame(canvas);
            if (drawSkins)
                drawSkinsChoose(canvas);
        }
    }

    public void drawGame(Canvas canvas) {
        if (!darkMode)
            bg.draw(canvas);
        else
            dark.draw(canvas);
        player.draw(canvas);
        platform.draw(canvas);
        for (GameObject object : gameObjects) {
            if (object instanceof Missile)
                object.draw(canvas);
            if (object instanceof Kaktus)
                object.draw(canvas);
            if (object instanceof Monet)
                object.draw(canvas);
        }
        drawText(canvas);
        if (player.isShowText())
            drawLost(canvas);
    }

    public void drawMenu(Canvas canvas) {
        if (!darkMode)
            bg.draw(canvas);
        else
            dark.draw(canvas);
        player.draw(canvas);
        platform.draw(canvas);
        buttonStart.draw(canvas);
        buttonSound.draw(canvas);
        buttonSkins.draw(canvas);
    }

    public void drawSkinsChoose(Canvas canvas) {
        if (!darkMode)
            bg.draw(canvas);
        else
            dark.draw(canvas);
        buttonBack.draw(canvas);
    }

    public void newGame() {
        player.setShowText(false);
        darkModeTimer = 0;
        darkMode = false;
        deleteObjects();
        player.setY((int) (HEIGHT - player.height * 1.3));
        for (Iterator<GameObject> it = gameObjects.iterator(); it.hasNext(); ) {
            GameObject aDrugStrength = it.next();
            if (!(aDrugStrength instanceof Player) && !(aDrugStrength instanceof Platform))
                it.remove();
        }
        if (player.getScore() > best) {
            best = player.getScore();

            contentValues.put(Database.KEY_SCORE, best);

            Cursor cursor = db.query(Database.TABLE_GAME, null, null, null, null, null, null);
            if (cursor.moveToFirst())
                db.update(Database.TABLE_GAME, contentValues, Database.KEY_ID + "= ?", new String[]{"1"});
            else
                db.insert(Database.TABLE_GAME, null, contentValues);
            cursor.close();
        }
        player.resetScore();
        newGameCreated = true;
    }

    public void drawText(Canvas canvas) {
        Paint paint = new Paint();
        if (!darkMode)
            paint.setColor(Color.BLACK);
        else
            paint.setColor(Color.WHITE);
        paint.setTextSize(WIDTH / 40);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DISTANCE: " + (player.getScore()), WIDTH / 20, HEIGHT / 10, paint);

        Cursor cursor = db.query(Database.TABLE_GAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int score = cursor.getColumnIndex(Database.KEY_SCORE);
            int money = cursor.getColumnIndex(Database.KEY_MONEY);
            canvas.drawText("BEST: " + cursor.getInt(score), WIDTH - WIDTH / 6, HEIGHT / 10, paint);
            canvas.drawText("MONEY: " + cursor.getInt(money), WIDTH / 2 - WIDTH / 20, HEIGHT / 10, paint);
        } else {
            canvas.drawText("BEST: " + 0, WIDTH - WIDTH / 6, HEIGHT / 10, paint);
            canvas.drawText("MONEY: " + 0, WIDTH / 2 - WIDTH / 20, HEIGHT / 10, paint);
        }
        cursor.close();
    }

    public void drawLost(Canvas canvas) {
        Paint paint = new Paint();
        if (!darkMode)
            paint.setColor(Color.BLACK);
        else
            paint.setColor(Color.WHITE);
        paint.setTextSize(WIDTH / 30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("YOU LOST!", WIDTH / 2, HEIGHT / 2, paint);
        canvas.drawText("YOUR SCORE WAS: " + player.getScore(), WIDTH / 2, HEIGHT / 2 + 100, paint);
    }

    public boolean collisionWithButtons(GameButtons obj, int x, int y) {
        return x > obj.getRectangle().left && x < obj.getRectangle().right && y < obj.getRectangle().bottom && y > obj.getRectangle().top;
    }

    private void spawnKaktus(int randomSecond, long kaktusElapsed) {
        if (kaktusElapsed - kaktusStartTime >= randomSecond && kaktusCanAppear) {
            gameObjects.add(new Kaktus(BitmapFactory.decodeResource(getResources(), R.drawable.kaktus),
                    WIDTH + 10, player.getScore()));
            gameObjects.add(new Monet(BitmapFactory.decodeResource(getResources(), R.drawable.monet),
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
            if (object instanceof Monet) {
                ((Monet) object).update();
                if (object.getX() < -100 || player.collision(player, object)) {
                    money++;
                    contentValues.put(Database.KEY_MONEY, money);
                    db.update(Database.TABLE_GAME, contentValues, Database.KEY_ID + "= ?", new String[]{"1"});
                    gameObjects.remove(object);
                    break;
                }
            }
        }
    }

    private void spawnMissile(int randomSecond, long missileElapsed) {
        int randomPosition = rand.nextInt(2) + 1;

        if (missileElapsed - missileStartTime >= randomSecond) {
            kaktusCanAppear = false;
            switch (randomPosition) {
                case 1:
                    gameObjects.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.bird),
                            2 * WIDTH, FIRST_POSITION, player.getScore()));
                    missileStartTime = System.currentTimeMillis() / 1000;
                    break;
                case 2:
                    gameObjects.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.bird),
                            2 * WIDTH, SECOND_POSITION, player.getScore()));
                    missileStartTime = System.currentTimeMillis() / 1000;
                    break;
            }
        }
        for (GameObject object : gameObjects) {
            if (object instanceof Missile) {
                ((Missile) object).update();
                if (object.getX() <= WIDTH / 2)
                    kaktusCanAppear = true;
                if (object.getX() < -10) {
                    gameObjects.remove(object);
                    kaktusCanAppear = true;
                    break;
                }
            }
        }
    }

    private void deleteObjects() {
        for (GameObject object : gameObjects) {
            if (object instanceof Missile || object instanceof Kaktus || object instanceof Monet)
                gameObjects.remove(object);
        }
        missileStartTime = System.currentTimeMillis() / 1000;
        kaktusStartTime = System.currentTimeMillis() / 1000;
        kaktusCanAppear = true;
    }
}
