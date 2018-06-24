package com.example.android.myinventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.myinventoryapp.data.BookContract.AddedBook;

public class BookDBHelper extends SQLiteOpenHelper{

    /** Name of the database file */
    private static final String DATABASE_NAME = "booksDataBase.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link BookDBHelper}.
     *
     * @param context of the app
     */
    public BookDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE =  "CREATE TABLE " + AddedBook.TABLE_NAME + " ("
                + AddedBook._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AddedBook.COLUMN_TITLE + " TEXT NOT NULL, "
                + AddedBook.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + AddedBook.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + AddedBook.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + AddedBook.COLUMN_SUPPLIER_PHONE_NO + " TEXT);";

        /* Execute the SQL statement */
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }
}
