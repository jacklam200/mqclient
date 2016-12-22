package cn.mqclient.service.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LinZaixiong on 2016/10/15.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static SQLiteDatabase mDb;
    private static DatabaseHelper mHelper;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "musicstore_new";
    private static final String TABLE_MUSIC = "music_info";
    private static final String TABLE_FOLDER = "folder_info";

    public static SQLiteDatabase getInstance(Context context) {
        if (mDb == null) {
            mDb = getHelper(context).getWritableDatabase();
        }
        return mDb;
    }

    public static DatabaseHelper getHelper(Context context) {
        if(mHelper == null) {
            mHelper = new DatabaseHelper(context);
        }
        return mHelper;
    }

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "
                + TABLE_MUSIC
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " songid integer, albumid integer, duration integer, musicname varchar(10), "
                + "artist char, data char, folder char, musicnamekey char, artistkey char, favorite integer)");
       db.execSQL("create table "
                + TABLE_FOLDER
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, folder_name char, folder_path char)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUSIC);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDER);
            onCreate(db);
        }
    }

    public void deleteTables(Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOLDER, null, null);
        db.delete(TABLE_MUSIC, null, null);
    }
}
