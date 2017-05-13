package com.amandafarrell.www.scorekeeper.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Using  SQLiteOpenHelper class. When you use this class to obtain references to your database,
 * the system performs the potentially long-running operations of creating and updating
 * the database only when needed and not during app startup
 */

public class PlayerDbHelper extends SQLiteOpenHelper {

    //Database version must be incremented if database schema is changed
    private static final int DATABASE_VERSION = 1;
    //Name of the database file
    private static final String DATABASE_NAME = "scorekeeper.db";

    public PlayerDbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //deletes old database and schema and builds new database
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PlayerContract.PlayerEntry.TABLE_NAME + " (" +
                    PlayerContract.PlayerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PlayerContract.PlayerEntry.COLUMN_PLAYER_NAME + " TEXT, " +
                    PlayerContract.PlayerEntry.COLUMN_PLAYER_SCORE+ " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PlayerContract.PlayerEntry.TABLE_NAME;
}
