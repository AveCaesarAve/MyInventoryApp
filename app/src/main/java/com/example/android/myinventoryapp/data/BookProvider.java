package com.example.android.myinventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.myinventoryapp.data.BookContract.AddedBook;

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();
    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    private BookDBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:

                cursor = database.query(AddedBook.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOKS_ID:

                selection = AddedBook._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(AddedBook.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {

        String name = values.getAsString(AddedBook.COLUMN_TITLE);
        if (name == null) {
            throw new IllegalArgumentException("Book requires a title");
        }

        Double price = values.getAsDouble(AddedBook.COLUMN_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Book requires price.");
        }

        int quantity = values.getAsInteger(AddedBook.COLUMN_QUANTITY);
        String supplierName = values.getAsString(AddedBook.COLUMN_SUPPLIER_NAME);
        String supplierPhone = values.getAsString(AddedBook.COLUMN_SUPPLIER_PHONE_NO);

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(AddedBook.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOKS_ID:

                selection = AddedBook._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(AddedBook.COLUMN_TITLE)) {
            String name = values.getAsString(AddedBook.COLUMN_TITLE);
            if (name == null) {
                throw new IllegalArgumentException("Book requires a title");
            }
        }

        if (values.containsKey(AddedBook.COLUMN_PRICE)) {
            Double price = values.getAsDouble(AddedBook.COLUMN_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Book requires a price");
            }
        }

        if (values.containsKey(AddedBook.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(AddedBook.COLUMN_QUANTITY);
        }

        if (values.containsKey(AddedBook.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(AddedBook.COLUMN_SUPPLIER_NAME);
        }

        if (values.containsKey(AddedBook.COLUMN_SUPPLIER_PHONE_NO)) {
            String supplierPhone = values.getAsString(AddedBook.COLUMN_SUPPLIER_PHONE_NO);
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(AddedBook.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:

                rowsDeleted = database.delete(AddedBook.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:

                selection = AddedBook._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(AddedBook.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return AddedBook.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return AddedBook.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}