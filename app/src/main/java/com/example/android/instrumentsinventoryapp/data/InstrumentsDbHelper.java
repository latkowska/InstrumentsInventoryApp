package com.example.android.instrumentsinventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InstrumentsDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InstrumentsDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "store.db";

    //Database version.
    private static final int DATABASE_VERSION = 1;

    public InstrumentsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the musical instruments table
        String SQL_CREATE_MUSICAL_INSTRUMENTS_TABLE = "CREATE TABLE " + InstrumentsContract.MusicalInstrumentsEntry.TABLE_NAME + " ("
                + InstrumentsContract.MusicalInstrumentsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_NAME + " TEXT NOT NULL, "
                + InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_PRICE + " INTEGER, "
                + InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_QUANTITY + " INTEGER, "
                + InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_COUNTRY_OF_ORIGIN + " TEXT NOT NULL, "
                + InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_PHONE_NUMER + " TEXT NOT NULL );";
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_MUSICAL_INSTRUMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // There is still version 1.
    }
}