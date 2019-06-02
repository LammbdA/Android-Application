package com.example.t_rex;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "gameDB";
    public static final String TABLE_GAME = "game";

    public static final String KEY_ID = "_id";
    public static final String KEY_SCORE = "score";
    public static final String KEY_MONEY = "money";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_GAME + "(" + KEY_ID
                + " integer primary key," + KEY_SCORE + " integer," + KEY_MONEY + " integer" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_GAME);
        onCreate(db);
    }
}
