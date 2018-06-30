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

/**
 * Displays list of books that were entered and stored in the app.
 */
public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the pet data loader */
    private static final int EXISTING_BOOK_LOADER = 0;

    /** Content URI for the existing book (null if it's a new book) */
    private Uri currentBookUri;

    /** EditText field to enter the book's title */
    private EditText mBookTitle;

    /** EditText field to enter the book's price*/
    private EditText mBookPrice;

    /** EditText field to enter the book's quantity */
    private EditText mBookQuantity;

    /** EditText field to enter the book's supplier name*/
    private EditText mBookSupplierName;

    /** EditText field to enter the book's supplier phone number */
    private EditText mBookSupplierPhoneNumber;

    /** Integer to check the errors */
    private int errorCheck = 0;

    /** Boolean flag that keeps track of whether the book has been edited (true) or not (false - default) */
    private boolean bookChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the bookChanged boolean to true.
     */
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

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        currentBookUri = intent.getData();

        // If the intent DOES NOT contain a book content URI, then we know that we are
        // creating a new book.
        if (currentBookUri == null) {

            // This is a new book, so change the app bar to say a new book is added"
            setTitle(getString(R.string.editor_activity_new_book));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {

            // Otherwise this is an existing book, so change app bar to say a book is edited"
            setTitle(getString(R.string.editor_activity_title_edit_book));

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mBookTitle = findViewById(R.id.bookTitle);
        mBookPrice = findViewById(R.id.bookPrice);
        mBookQuantity = findViewById(R.id.bookQuantity);
        mBookSupplierName = findViewById(R.id.bookSupplierName);
        mBookSupplierPhoneNumber = findViewById(R.id.bookSupplierPhoneNumber);
        Button increaseBtn = findViewById(R.id.increase);
        Button decreaseBtn = findViewById(R.id.decrease);
        Button callToSupplierBtn = findViewById(R.id.order);

        /* Setup the Call to Supplier intent */
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

        /* Increase button click handling */
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

        /* Decrease button click handling */
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

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mBookTitle.setOnTouchListener(mTouchListener);
        mBookPrice.setOnTouchListener(mTouchListener);
        mBookQuantity.setOnTouchListener(mTouchListener);
        mBookSupplierName.setOnTouchListener(mTouchListener);
        mBookSupplierPhoneNumber.setOnTouchListener(mTouchListener);
    }

    /**
     * Get user input from editor and save book into database.
     */
    private void insertBook() {
        /* Read from input fields. Use trim to eliminate leading or trailing white space */
        String titleBook = mBookTitle.getText().toString().trim();
        String priceBook = mBookPrice.getText().toString().trim();
        String quantityBook = mBookQuantity.getText().toString().trim();
        String supplierNameBook = mBookSupplierName.getText().toString().trim();
        String supplierPhoneBook = mBookSupplierPhoneNumber.getText().toString().trim();

        errorCheck = 0;

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (currentBookUri == null &&
                TextUtils.isEmpty(titleBook) && TextUtils.isEmpty(priceBook) &&
                TextUtils.isEmpty(quantityBook) && TextUtils.isEmpty(supplierNameBook) && TextUtils.isEmpty(supplierPhoneBook)) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues values = new ContentValues();

        /* Check if Book Title is valid and put into the database */
        if (TextUtils.isEmpty(titleBook)) {
            Toast.makeText(this, R.string.editor_title_error, Toast.LENGTH_SHORT).show();
            errorCheck = 1;
            return;
        }
        values.put(BookContract.AddedBook.COLUMN_TITLE, titleBook);

        /* Check if Book Price is valid and put into the database */
        if (TextUtils.isEmpty(priceBook)) {
            Toast.makeText(this, R.string.editor_price_error, Toast.LENGTH_SHORT).show();
            errorCheck = 1;
            return;
        }
        values.put(BookContract.AddedBook.COLUMN_PRICE, priceBook);

        /* Put the Quantity into the database */
        values.put(BookContract.AddedBook.COLUMN_QUANTITY, quantityBook);

        /* Check if Book Supplier Name is valid and put into the database */
        if (supplierNameBook.isEmpty()) {
            Toast.makeText(this, R.string.editor_supplier_name_error, Toast.LENGTH_SHORT).show();
            errorCheck = 1;
            return;
        }
        values.put(BookContract.AddedBook.COLUMN_SUPPLIER_NAME, supplierNameBook);

        /* Check if Supplier Phone Number is valid and put into the database*/
        if (supplierPhoneBook.isEmpty()) {
            Toast.makeText(this, R.string.editor_supplier_phone_error, Toast.LENGTH_SHORT).show();
            errorCheck = 1;
            return;
        }
        values.put(BookContract.AddedBook.COLUMN_SUPPLIER_PHONE_NO, supplierPhoneBook);

        /* Book Quantity check */
        int quantity = 0;

        if (!TextUtils.isEmpty(quantityBook)) {
            quantity = Integer.parseInt(quantityBook);
        }

        if (quantityBook.isEmpty()) {
            Toast.makeText(this, R.string.editor_quantity_error, Toast.LENGTH_SHORT).show();
            errorCheck = 1;
            return;
        }
        values.put(BookContract.AddedBook.COLUMN_QUANTITY, quantity);

        /* Determine if this is a new or existing book by checking if currentBookUri is null or not */
        if (currentBookUri == null) {
            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookContract.AddedBook.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_book_success), Toast.LENGTH_SHORT).show();
            }

        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: currentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because currentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(currentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Menu Inflater
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_add_item_screen, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item, as it is not needed in this case
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_item);
            menuItem.setVisible(false);
        }
        return true;
    }

    /**
     * Options item selection
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.save_item:
                insertBook();
                // Save book to database
                if (errorCheck == 0) {
                    // Exit activity
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.delete_item:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!bookChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Back button pressed
     **/
    @Override
    public void onBackPressed() {

        /* If the book hasn't changed, continue with handling back button press */
        if (!bookChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * OnCreate loader
     **/
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookContract.AddedBook._ID,
                BookContract.AddedBook.COLUMN_TITLE,
                BookContract.AddedBook.COLUMN_PRICE,
                BookContract.AddedBook.COLUMN_QUANTITY,
                BookContract.AddedBook.COLUMN_SUPPLIER_NAME,
                BookContract.AddedBook.COLUMN_SUPPLIER_PHONE_NO};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,       // Parent activity context
                currentBookUri,                     // Query the content URI for the current book
                projection,                         // Columns to include in the resulting Cursor
                null,                       // No selection clause
                null,                   // No selection arguments
                null);                      // Default sort order
    }

    /**
     * OnLoad Finished
     **/
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BookContract.AddedBook.COLUMN_TITLE);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.AddedBook.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.AddedBook.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.AddedBook.COLUMN_SUPPLIER_NAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(BookContract.AddedBook.COLUMN_SUPPLIER_PHONE_NO);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            Double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierNumber = cursor.getString(supplierNumberColumnIndex);

            // Update the views on the screen with the values from the database
            mBookTitle.setText(name);
            mBookPrice.setText(Double.toString(price));
            mBookQuantity.setText(Integer.toString(quantity));
            mBookSupplierName.setText(supplierName);
            mBookSupplierPhoneNumber.setText(supplierNumber);
        }
    }

    /**
     * OnLoader Reset
     **/
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mBookTitle.setText("");
        mBookPrice.setText("");
        mBookQuantity.setText("");
        mBookSupplierName.setText("");
        mBookSupplierPhoneNumber.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.editor_unsaved_changes_dialog);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Deletion confirmation warning
     **/
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

    /**
     * Delete edited book
     **/
    private void deleteBook() {
        if (currentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_book_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
