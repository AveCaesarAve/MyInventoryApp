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

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.element_listing, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView titleTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        Button saleBtn = view.findViewById(R.id.sale);

        int nameColumnIndex = cursor.getColumnIndex(AddedBook.COLUMN_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(AddedBook.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(AddedBook.COLUMN_QUANTITY);

        String bookTitle = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);

        final int bookQuantityLimit = Integer.parseInt(bookQuantity);

        if (TextUtils.isEmpty(bookQuantity)) {
            bookQuantity = context.getString(R.string.default_quantity);
        }

        titleTextView.setText(bookTitle);
        priceTextView.setText(bookPrice);
        quantityTextView.setText(bookQuantity);
        String currentId = cursor.getString(cursor.getColumnIndexOrThrow(AddedBook._ID));
        final Uri currentUri = ContentUris.withAppendedId(AddedBook.CONTENT_URI, Long.parseLong(currentId));

        saleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bookQuantityLimit > 0) {
                    ContentValues values = new ContentValues();
                    values.put(AddedBook.COLUMN_QUANTITY, (bookQuantityLimit - 1));

                    int newUpdate = context.getContentResolver().update(currentUri, values, null, null);
                    if (newUpdate == 0)
                        Toast.makeText(context, R.string.error_update, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context, R.string.quantity_below_zero, Toast.LENGTH_SHORT).show();
            }
        });
    }
}