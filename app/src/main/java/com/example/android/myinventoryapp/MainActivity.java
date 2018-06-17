package com.example.android.myinventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.myinventoryapp.data.BookContract.AddedBook;
import com.example.android.myinventoryapp.data.BookDBHelper;

/**
 * Displays list of books that were entered and stored in the app database.
 */
public class MainActivity extends AppCompatActivity {

    private BookDBHelper mDbHelper;
    private TextView mDisplayData;
    private SQLiteDatabase mDbWritable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDisplayData = findViewById(R.id.databaseData);

        /* Setup FloatingActionButton to open EditorActivity */
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        /* Initiate the DBHelper */
        mDbHelper = new BookDBHelper(this);
        /* Set the database to writable mode */
        mDbWritable = mDbHelper.getWritableDatabase();
    }

    private void displayDatabaseInfo() {
        /* Set the database to readable mode */
        SQLiteDatabase dbReadable = mDbHelper.getReadableDatabase();

        /* Define a projection that specifies which columns from the database should be taken */
        String[] projection = {
                AddedBook._ID,
                AddedBook.COLUMN_TITLE,
                AddedBook.COLUMN_PRICE,
                AddedBook.COLUMN_QUANTITY,
                AddedBook.COLUMN_SUPPLIER_NAME,
                AddedBook.COLUMN_SUPPLIER_PHONE_NO};

        /* Perform a query on the book table */
        Cursor cursor = dbReadable.query(
                AddedBook.TABLE_NAME,            /* Define the table to query */
                projection,                    /* Define the columns to be returned */
                null,                 /* Null the columns for the selection */
                null,              /* Null the arguments for the selection */
                null,                  /* Do not perform grouping */
                null,                   /* Do not perform filtering */
                null);                 /* Do not perform ordering */

        try {
            mDisplayData.setText(getString(R.string.databaseOutput, cursor.getCount() + ""));
            mDisplayData.append(AddedBook._ID + " - " +
                    AddedBook.COLUMN_TITLE + " - " +
                    AddedBook.COLUMN_PRICE + " - " +
                    AddedBook.COLUMN_QUANTITY + " - " +
                    AddedBook.COLUMN_SUPPLIER_NAME + " - " +
                    AddedBook.COLUMN_SUPPLIER_PHONE_NO + "\n");

            /* index of each column */
            int idColumnIndex = cursor.getColumnIndex(AddedBook._ID);
            int titleColumnIndex = cursor.getColumnIndex(AddedBook.COLUMN_TITLE);
            int priceColumnIndex = cursor.getColumnIndex(AddedBook.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(AddedBook.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(AddedBook.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(AddedBook.COLUMN_SUPPLIER_PHONE_NO);

            /* Go through all returned rows in the cursor */
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentTitle = cursor.getString(titleColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                /* Display the values from each column of the current row in the cursor in the TextView */
                mDisplayData.append(("\n" + currentID + " - " +
                        currentTitle + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhone));
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded book data into the database. For debugging purposes only.
     */
    private void insertBook() {
        /* Sample book data */
        ContentValues values = new ContentValues();
        values.put(AddedBook.COLUMN_TITLE, R.string.dummyTitle);
        values.put(AddedBook.COLUMN_QUANTITY, R.string.dummyQuantity);
        values.put(AddedBook.COLUMN_PRICE, R.string.dummyPrice);
        values.put(AddedBook.COLUMN_SUPPLIER_NAME, R.string.dummySupplier);
        values.put(AddedBook.COLUMN_SUPPLIER_PHONE_NO, R.string.dummyPhoneNumber);

        mDbWritable.insert(AddedBook.TABLE_NAME, null, values);
    }

    /**
     * Clear all books data
     */
    private void deleteAllBooks() {
        mDbWritable.delete(AddedBook.TABLE_NAME, null, null);
    }

    /**
     * onStart actions
     */
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Inflater of the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    /**
     * Options selection
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_data:
                insertBook();
                displayDatabaseInfo();
                return true;
            case R.id.delete_all:
                deleteAllBooks();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
