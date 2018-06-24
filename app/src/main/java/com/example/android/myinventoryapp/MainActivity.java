package com.example.android.myinventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.myinventoryapp.data.BookContract.AddedBook;

/**
 * Displays list of books that were entered and stored in the app database.
 */
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    BookCursorAdapter cursorAdapter;
    private Uri currentBookUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Setup FloatingActionButton to open EditorActivity */
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        ListView bookListV = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        bookListV.setEmptyView(emptyView);

        cursorAdapter = new BookCursorAdapter(this, null);
        bookListV.setAdapter(cursorAdapter);

        bookListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                currentBookUri = ContentUris.withAppendedId(AddedBook.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });
        TextView quantityTextV = findViewById(R.id.quantity);

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    private void insertBook() {

        ContentValues values = new ContentValues();
        values.put(AddedBook.COLUMN_TITLE, getString(R.string.dummyTitle));
        values.put(AddedBook.COLUMN_QUANTITY, getString(R.string.dummyQuantity));
        values.put(AddedBook.COLUMN_PRICE, getString(R.string.dummyPrice));
        values.put(AddedBook.COLUMN_SUPPLIER_NAME, getString(R.string.dummySupplier));
        values.put(AddedBook.COLUMN_SUPPLIER_PHONE_NO, getString(R.string.dummyPhoneNumber));

        Uri newUri = getContentResolver().insert(AddedBook.CONTENT_URI, values);
    }

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(AddedBook.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AddedBook._ID,
                AddedBook.COLUMN_TITLE,
                AddedBook.COLUMN_PRICE,
                AddedBook.COLUMN_QUANTITY};

        return new CursorLoader(this,
                AddedBook.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        cursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.insert_data:
                insertBook();
                return true;

            case R.id.delete_all:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
