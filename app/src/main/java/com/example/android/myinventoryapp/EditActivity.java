package com.example.android.myinventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.myinventoryapp.data.BookContract.AddedBook;
import com.example.android.myinventoryapp.data.BookDBHelper;

public class EditActivity extends AppCompatActivity {

    private EditText mBookTitle;
    private EditText mBookPrice;
    private EditText mBookQuantity;
    private EditText mBookSupplierName;
    private EditText mBookSupplierPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mBookTitle = findViewById(R.id.bookTitle);
        mBookPrice = findViewById(R.id.bookPrice);
        mBookQuantity = findViewById(R.id.bookQuantity);
        mBookSupplierName = findViewById(R.id.bookSupplierName);
        mBookSupplierPhoneNumber = findViewById(R.id.bookSupplierPhoneNumber);
    }

    private void insertBook() {
        /* Read from input fields */
        String titleBook = mBookTitle.getText().toString().trim();
        int priceBook = Integer.parseInt(mBookPrice.getText().toString());
        int quantityBook = Integer.parseInt(mBookQuantity.getText().toString());
        String supplierNameBook = mBookSupplierName.getText().toString().trim();
        String supplierPhoneBook = mBookSupplierPhoneNumber.getText().toString().trim();

        /* Create database helper */
        BookDBHelper mDbHelper = new BookDBHelper(this);

        /* Get the database in writable mode */
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        /* Create a ContentValues object where column names are the keys, and take value from input fields */
        ContentValues values = new ContentValues();
        values.put(AddedBook.COLUMN_TITLE, titleBook);
        values.put(AddedBook.COLUMN_QUANTITY, quantityBook);
        values.put(AddedBook.COLUMN_PRICE, priceBook);
        values.put(AddedBook.COLUMN_SUPPLIER_NAME, supplierNameBook);
        values.put(AddedBook.COLUMN_SUPPLIER_PHONE_NO, supplierPhoneBook);

        /* Insert a new row for new line in the database, returning the ID of that new row. */
        long newRowId = db.insert(AddedBook.TABLE_NAME, null, values);

        /* Show a toast message depending on whether or not the insertion was successful */
        if (newRowId == -1) {
            /* If the row ID is -1, then there was an error with insertion. */
            Toast.makeText(this, R.string.toastError, Toast.LENGTH_SHORT).show();
        } else {
            /* Otherwise, the insertion was successful and we can display a toast with the row ID. */
            Toast.makeText(this, R.string.toastSuccessful + " " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    /** Inflater of the menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_item_screen, menu);
        return true;
    }

    /** Options selection */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_item:
                insertBook();
                finish();
                return true;

            case R.id.delete_item:
                // Do nothing
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
