package com.drwat.lab2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by drwat on 05.04.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "mydatabase.db";
    public static final int VERSION = 2;
    public static final String TABLE_NAME = "Grocery";
    public static final String GROCERY_ID = "_id";
    public static final String GROCERY_NAME = "name";
    public static final String GROCERY_COUNT = "count";
    public static final String GROCERY_IMAGE_PATH = "image_path";
    public static final String DATABASE_QUERY =
            "CREATE TABLE " + TABLE_NAME + " (" + GROCERY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            GROCERY_NAME + " VARCHAR, " + GROCERY_COUNT + " VARCHAR, " +
            GROCERY_IMAGE_PATH + " VARCHAR);";
    private static final String  FILE_DIR = "DATABASE";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,Environment.getExternalStorageDirectory()+ File.separator + FILE_DIR + File.separator + DB_NAME, factory, version);
        Log.w("DATABASE", Environment.getExternalStorageDirectory() + File.separator + FILE_DIR + File.separator + DB_NAME);
    }

    public DatabaseHelper(Context context) {
        super(context,  DB_NAME, null, VERSION);
        Log.w("DATABASE", Environment.getExternalStorageDirectory() + File.separator + FILE_DIR + File.separator + DB_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite", "Обновление базы данных с версии " + oldVersion + " до " + newVersion);
        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME);
        onCreate(db);
    }
}
