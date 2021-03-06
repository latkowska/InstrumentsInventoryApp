package com.example.android.instrumentsinventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.URI;

public class InstrumentsProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InstrumentsProvider.class.getSimpleName();


    public static final int INSTRUMENTS = 100;
    public static final int INSTRUMENT_ID = 101;

    //UriMatcher object to match a content URI to a corresponding code.

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(InstrumentsContract.MusicalInstrumentsEntry.CONTENT_AUTHORITY, "musical_instruments", 100);
        uriMatcher.addURI(InstrumentsContract.MusicalInstrumentsEntry.CONTENT_AUTHORITY, "musical_instruments/#", 101);
    }

    private InstrumentsDbHelper mInstrumentDBHelper;

    @Override
    public boolean onCreate() {
        mInstrumentDBHelper = new InstrumentsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mInstrumentDBHelper.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);
        switch (match) {
            case INSTRUMENTS:
                cursor = database.query(InstrumentsContract.MusicalInstrumentsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case INSTRUMENT_ID:

                selection = InstrumentsContract.MusicalInstrumentsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(InstrumentsContract.MusicalInstrumentsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Query URI Error" + uri);
        }
        // Set notification URI on the Cursor. If it changes Cursor update is required.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case INSTRUMENTS:
                return InstrumentsContract.MusicalInstrumentsEntry.CONTENT_LIST_TYPE;
            case INSTRUMENT_ID:
                return InstrumentsContract.MusicalInstrumentsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case INSTRUMENTS:
                return insertInstrument(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertInstrument(Uri uri, ContentValues values) {
        String instrumentName = values.getAsString(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_NAME);
        if (instrumentName == null || instrumentName.trim().length() == 0) {
            throw new IllegalArgumentException("Instrument name required.");
        }

        Integer instrumentPrice = values.getAsInteger(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_PRICE);
        if (instrumentPrice == null || instrumentPrice < 0) {
            throw new IllegalArgumentException("Instrument price required.");

        }
        Integer instrumentQuantity = values.getAsInteger(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_QUANTITY);
        if (instrumentQuantity == null || instrumentQuantity < 0) {
            throw new IllegalArgumentException("Instruments quantity at least 0.");

        }

        String instrumentCountryOfOrigin = values.getAsString(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_COUNTRY_OF_ORIGIN);
        if (instrumentCountryOfOrigin == null || instrumentCountryOfOrigin.trim().length() == 0) {
            throw new IllegalArgumentException("Instrument requires country of origin.");
        }

        String supplierName = values.getAsString(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null || supplierName.trim().length() == 0) {
            throw new IllegalArgumentException("Supplier name required.");
        }

        String supplierPhone = values.getAsString(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_PHONE_NUMER);
        if (supplierPhone == null || supplierPhone.trim().length() == 0) {
            throw new IllegalArgumentException("Supplier phone number required.");
        }

        // Get writable database
        SQLiteDatabase database = mInstrumentDBHelper.getWritableDatabase();
        // Insert the new instrument with the given values.
        long id = database.insert(InstrumentsContract.MusicalInstrumentsEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify listeners that content uri has been updated.
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mInstrumentDBHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;
        final int match = uriMatcher.match(uri);
        switch (match) {
            case INSTRUMENTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(InstrumentsContract.MusicalInstrumentsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INSTRUMENT_ID:
                // Delete a single row given by the ID in the URI
                selection = InstrumentsContract.MusicalInstrumentsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InstrumentsContract.MusicalInstrumentsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case INSTRUMENTS:
                return updateInstrument(uri, contentValues, selection, selectionArgs);
            case INSTRUMENT_ID:
                selection = InstrumentsContract.MusicalInstrumentsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInstrument(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateInstrument(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_NAME)) {
            String instrumentName = values.getAsString(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_NAME);
            if (instrumentName == null || instrumentName.trim().length() == 0) {
                throw new IllegalArgumentException("Update - instrument requires name.");
            }
        }
        if (values.containsKey(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_PRICE)) {
            Integer instrumentPrice = values.getAsInteger(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_PRICE);
            if (instrumentPrice == null || instrumentPrice < 0) {
                throw new IllegalArgumentException("Update - instrument requires valid price.");

            }
        }
        if (values.containsKey(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_QUANTITY)) {
            Integer instrumentQuantity = values.getAsInteger(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_QUANTITY);
            if (instrumentQuantity == null || instrumentQuantity < 0) {
                throw new IllegalArgumentException("Update - instruments quantity at least 0.");

            }
        }
        if (values.containsKey(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_COUNTRY_OF_ORIGIN)) {
            String instrumentCountryOfOrigin = values.getAsString(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_COUNTRY_OF_ORIGIN);
            if (instrumentCountryOfOrigin == null || instrumentCountryOfOrigin.trim().length() == 0) {
                throw new IllegalArgumentException("Update - instrument requires country of origin.");
            }
        }
        if (values.containsKey(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null || supplierName.trim().length() == 0) {
                throw new IllegalArgumentException("Update - ssupplier name required.");
            }
        }
        if (values.containsKey(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_PHONE_NUMER)) {
            String supplierPhone = values.getAsString(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_PHONE_NUMER);
            if (supplierPhone == null || supplierPhone.trim().length() == 0) {
                throw new IllegalArgumentException("Update - supplier phone number required.");
            }
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mInstrumentDBHelper.getWritableDatabase();
        int rowsUpdated = database.update(InstrumentsContract.MusicalInstrumentsEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}

