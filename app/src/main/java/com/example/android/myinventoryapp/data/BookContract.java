package com.example.android.myinventoryapp.data;

import android.provider.BaseColumns;

public final class BookContract {

    private BookContract(){}

    public final static class AddedBook implements BaseColumns{

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
