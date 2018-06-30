package com.example.android.myinventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myinventoryapp.data.BookContract.AddedBook;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);

    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.element_listing, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the title for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView titleTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        Button saleBtn = view.findViewById(R.id.sale);

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(AddedBook.COLUMN_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(AddedBook.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(AddedBook.COLUMN_QUANTITY);

        // Read the book attributes from the Cursor for the current book
        String bookTitle = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);
        final int bookQuantityLimit = Integer.parseInt(bookQuantity);

        // If the book quantity is empty string or null, then use some default value
        // which is "0", so the TextView isn't blank.
        if (TextUtils.isEmpty(bookQuantity)) {
            bookQuantity = context.getString(R.string.default_quantity_of_books);
        }

        // Update the TextViews with the attributes for the current book
        titleTextView.setText(bookTitle);
        priceTextView.setText(bookPrice);
        quantityTextView.setText(bookQuantity);
        String currentId = cursor.getString(cursor.getColumnIndexOrThrow(AddedBook._ID));
        final Uri currentUri = ContentUris.withAppendedId(AddedBook.CONTENT_URI, Long.parseLong(currentId));


        // Sale button handling
        saleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if there are more then 0 books left
                if (bookQuantityLimit > 0) {
                    ContentValues values = new ContentValues();
                    values.put(AddedBook.COLUMN_QUANTITY, (bookQuantityLimit - 1));
                    int newUpdate = context.getContentResolver().update(currentUri, values, null, null);
                    if (newUpdate == 0)
                        Toast.makeText(context, R.string.editor_error_update, Toast.LENGTH_SHORT).show();

                // If there are not books left, display a Toast with message
                } else
                    Toast.makeText(context, R.string.quantity_check, Toast.LENGTH_SHORT).show();
            }
        });
    }
}