package com.example.android.myinventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.myinventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    private BookContract(){}

    public final static class AddedBook implements BaseColumns {

        /**
         * URI concatenation
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * Book table name.
         */
        public final static String TABLE_NAME = "library";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Title of the book.
         *
         * Type: STRING
         */
        public final static String COLUMN_TITLE = "title";

        /**
         * Price of the book.
         *
         * Type: INTEGER.
         */
        public final static String COLUMN_PRICE = "price";

        /**
         * Quantity of books.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * Book Supplier name.
         *
         * Type: STRING
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplierName";

        /**
         * Book Supplier phone number.
         *
         * Type: STRING
         */
        public final static String COLUMN_SUPPLIER_PHONE_NO = "supplierPhone";
    }
}
