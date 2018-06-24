package com.example.android.myinventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.myinventoryapp.data.BookContract;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri currentBookUri;
    private EditText mBookTitle;
    private EditText mBookPrice;
    private EditText mBookQuantity;
    private EditText mBookSupplierName;
    private EditText mBookSupplierPhoneNumber;

    private Button decreaseBtn;
    private Button increaseBtn;
    private Button callToSupplierBtn;

    private int checkSum = 0;

    private boolean bookChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        currentBookUri = intent.getData();

        if (currentBookUri == null) {

            setTitle(getString(R.string.editor_activity_new_book));

            invalidateOptionsMenu();
        } else {

            setTitle(getString(R.string.editor_activity_title_edit_book));

            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        mBookTitle = findViewById(R.id.bookTitle);
        mBookPrice = findViewById(R.id.bookPrice);
        mBookQuantity = findViewById(R.id.bookQuantity);
        mBookSupplierName = findViewById(R.id.bookSupplierName);
        mBookSupplierPhoneNumber = findViewById(R.id.bookSupplierPhoneNumber);
        increaseBtn = findViewById(R.id.increase);
        decreaseBtn = findViewById(R.id.decrease);
        callToSupplierBtn = findViewById(R.id.order);


        callToSupplierBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mBookSupplierPhoneNumber.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;
                if (!TextUtils.isEmpty(mBookQuantity.getText().toString())) {
                    quantity = Integer.parseInt(mBookQuantity.getText().toString());
                }
                quantity++;
                mBookQuantity.setText("" + quantity);
            }
        });

        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;
                if (!TextUtils.isEmpty(mBookQuantity.getText().toString())) {
                    quantity = Integer.parseInt(mBookQuantity.getText().toString());
                }
                if (quantity > 0) {
                    quantity--;
                }
                mBookQuantity.setText("" + quantity);
            }
        });

        mBookTitle.setOnTouchListener(mTouchListener);
        mBookPrice.setOnTouchListener(mTouchListener);
        mBookQuantity.setOnTouchListener(mTouchListener);
        mBookSupplierName.setOnTouchListener(mTouchListener);
        mBookSupplierPhoneNumber.setOnTouchListener(mTouchListener);

    }

    private void insertBook() {
        /* Read from input fields */
        String titleBook = mBookTitle.getText().toString().trim();
        String priceBook = mBookPrice.getText().toString().trim();
        String quantityBook = mBookQuantity.getText().toString().trim();
        String supplierNameBook = mBookSupplierName.getText().toString().trim();
        String supplierPhoneBook = mBookSupplierPhoneNumber.getText().toString().trim();

        checkSum = 0;
        if (currentBookUri == null &&
                TextUtils.isEmpty(titleBook) && TextUtils.isEmpty(priceBook) &&
                TextUtils.isEmpty(quantityBook) && TextUtils.isEmpty(supplierNameBook) && TextUtils.isEmpty(supplierPhoneBook)) {
            return;
        }

        ContentValues values = new ContentValues();
        if (TextUtils.isEmpty(titleBook)) {
            mBookTitle.setError(getString(R.string.title_error));
            checkSum = 1;
            return;
        }
        values.put(BookContract.AddedBook.COLUMN_TITLE, titleBook);
        if (TextUtils.isEmpty(priceBook)) {
            mBookPrice.setError(getString(R.string.price_error));
            checkSum = 1;
            return;
        }
        values.put(BookContract.AddedBook.COLUMN_PRICE, priceBook);
        values.put(BookContract.AddedBook.COLUMN_QUANTITY, quantityBook);
        if (supplierNameBook.isEmpty()) {
            mBookSupplierName.setError(getString(R.string.supplier_name_error));
            checkSum = 1;
            return;
        }
        values.put(BookContract.AddedBook.COLUMN_SUPPLIER_NAME, supplierNameBook);
        if (supplierPhoneBook.isEmpty()) {
            mBookSupplierPhoneNumber.setError(getString(R.string.supplier_phone_error));
            checkSum = 1;
            return;
        }
        values.put(BookContract.AddedBook.COLUMN_SUPPLIER_PHONE_NO, supplierPhoneBook);

        int quantity = 0;

        if (!TextUtils.isEmpty(quantityBook)) {
            quantity = Integer.parseInt(quantityBook);
        }
        if (quantityBook.isEmpty()) {
            mBookQuantity.setError(getString(R.string.quantity_error));
            checkSum = 1;
            return;
        }
        values.put(BookContract.AddedBook.COLUMN_QUANTITY, quantity);

        if (currentBookUri == null) {

            Uri newUri = getContentResolver().insert(BookContract.AddedBook.CONTENT_URI, values);

            if (newUri == null) {

                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_insert_book_success),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(currentBookUri, values, null, null);

            if (rowsAffected == 0) {

                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

        /* Create database helper *//*
        BookDBHelper mDbHelper = new BookDBHelper(this);

        *//* Get the database in writable mode *//*
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        *//* Create a ContentValues object where column names are the keys, and take value from input fields *//*
        ContentValues values = new ContentValues();
        values.put(AddedBook.COLUMN_TITLE, titleBook);
        values.put(AddedBook.COLUMN_QUANTITY, quantityBook);
        values.put(AddedBook.COLUMN_PRICE, priceBook);
        values.put(AddedBook.COLUMN_SUPPLIER_NAME, supplierNameBook);
        values.put(AddedBook.COLUMN_SUPPLIER_PHONE_NO, supplierPhoneBook);

        *//* Insert a new row for new line in the database, returning the ID of that new row. *//*
        long newRowId = db.insert(AddedBook.TABLE_NAME, null, values);

        *//* Show a toast message depending on whether or not the insertion was successful *//*
        if (newRowId == -1) {
            *//* If the row ID is -1, then there was an error with insertion. *//*
            Toast.makeText(this, R.string.toastError, Toast.LENGTH_SHORT).show();
        } else {
            *//* Otherwise, the insertion was successful and we can display a toast with the row ID. *//*
            Toast.makeText(this, R.string.toastSuccessful + " " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }*/

    /** Inflater of the menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_item_screen, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_item);
            menuItem.setVisible(false);
        }
        return true;
    }

    /** Options selection */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.save_item:
                insertBook();

                if (checkSum == 0) {
                    finish();
                } else {
                }
                return true;

            case R.id.delete_item:

                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:

                if (!bookChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!bookChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BookContract.AddedBook._ID,
                BookContract.AddedBook.COLUMN_TITLE,
                BookContract.AddedBook.COLUMN_PRICE,
                BookContract.AddedBook.COLUMN_QUANTITY,
                BookContract.AddedBook.COLUMN_SUPPLIER_NAME,
                BookContract.AddedBook.COLUMN_SUPPLIER_PHONE_NO};

        return new CursorLoader(this,
                currentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(BookContract.AddedBook.COLUMN_TITLE);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.AddedBook.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.AddedBook.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.AddedBook.COLUMN_SUPPLIER_NAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(BookContract.AddedBook.COLUMN_SUPPLIER_PHONE_NO);

            String name = cursor.getString(nameColumnIndex);
            Double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierNumber = cursor.getString(supplierNumberColumnIndex);

            mBookTitle.setText(name);
            mBookPrice.setText(Double.toString(price));
            mBookQuantity.setText(Integer.toString(quantity));
            mBookSupplierName.setText(supplierName);
            mBookSupplierPhoneNumber.setText(supplierNumber);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mBookTitle.setText("");
        mBookPrice.setText("");
        mBookQuantity.setText("");
        mBookSupplierName.setText("");
        mBookSupplierPhoneNumber.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {

        if (currentBookUri != null) {

            int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

}
