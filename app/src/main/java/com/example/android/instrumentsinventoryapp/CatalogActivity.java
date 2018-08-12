package com.example.android.instrumentsinventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


import com.example.android.instrumentsinventoryapp.data.InstrumentsContract;
import com.example.android.instrumentsinventoryapp.data.InstrumentsCursorAdapter;


public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the pet data loader
     */
    private static final int INSTRUMENT_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    InstrumentsCursorAdapter mInstrumentCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the instrument data
        ListView instrumentListView = (ListView) findViewById(R.id.list);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_list);
        instrumentListView.setEmptyView(emptyView);


        mInstrumentCursorAdapter = new InstrumentsCursorAdapter(this, null);
        instrumentListView.setAdapter(mInstrumentCursorAdapter);

        // Kick off the loader
        getLoaderManager().initLoader(INSTRUMENT_LOADER, null, this);

    }

    //temporatry method to insert dummy data
    private void insertInstrumentData() {


        ContentValues values = new ContentValues();
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_NAME, "Piano");
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_PRICE, 2000);
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_QUANTITY, 1);
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_COUNTRY_OF_ORIGIN, "Poland");
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_NAME, "Jan");
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_PHONE_NUMER, 658789889);

        Uri newUri = getContentResolver().insert(InstrumentsContract.MusicalInstrumentsEntry.CONTENT_URI, values);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertInstrumentData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InstrumentsContract.MusicalInstrumentsEntry._ID,
                InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_NAME,
                InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_PRICE,
                InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_QUANTITY};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                InstrumentsContract.MusicalInstrumentsEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mInstrumentCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
// Callback called when the data needs to be deleted
        mInstrumentCursorAdapter.swapCursor(null);
    }
}








