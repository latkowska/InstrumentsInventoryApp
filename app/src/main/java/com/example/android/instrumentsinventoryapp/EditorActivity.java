package com.example.android.instrumentsinventoryapp;

import android.content.ContentValues;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.instrumentsinventoryapp.data.InstrumentsContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantitytEditText;
    private EditText mCountryOfOrigintEditText;
    private EditText mSupllierNameEditText;
    private EditText mSupplierPhoneEditText;
    private Button mIncrementBtn;
    private Button mDecrementBtn;
    private int item_quantity = 0;
    private static final int EXISTING_INSTRUMENT_LOADER = 0;
    private Uri mCurrentInstrumentUri;
    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            mProductHasChanged = true;
            return false;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        mCurrentInstrumentUri = intent.getData();
        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new pet.
        if (mCurrentInstrumentUri == null) {
            // This is a new pet, so change the app bar to say "Add a Pet"
            setTitle(getString(R.string.editor_activity_title_new_instrument));
        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit Pet"
            setTitle(getString(R.string.editor_activity_title_edit_instrument));
            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISTING_INSTRUMENT_LOADER, null, this);
        }


        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantitytEditText = (EditText) findViewById(R.id.edit_quantity);
        mIncrementBtn = (Button) findViewById(R.id.increment_quantity);
        mDecrementBtn = (Button) findViewById(R.id.decrement_quantity);
        mCountryOfOrigintEditText = (EditText) findViewById(R.id.edit_country_of_origin);
        mSupllierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);
    }


    //get user input from the editor and save this data in db
    private void saveInstrument() { //insertInstrument()

        String nameString = mNameEditText.getText().toString().trim();

        String priceString = mPriceEditText.getText().toString().trim();
        int priceInt = Integer.parseInt(priceString);
        String quantityString = mQuantitytEditText.getText().toString().trim();
        int quantityInt = Integer.parseInt(quantityString);
        String countryOfOriginString = mCountryOfOrigintEditText.getText().toString();
        String supplierNameString = mSupllierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (mCurrentInstrumentUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(countryOfOriginString) &&
                TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneString)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_NAME, nameString);
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_PRICE, priceInt);
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_QUANTITY, quantityInt);
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_COUNTRY_OF_ORIGIN, countryOfOriginString);
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_PHONE_NUMER, supplierPhoneString);

        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_PRICE, price);

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_QUANTITY, quantity);

        if (mCurrentInstrumentUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(InstrumentsContract.MusicalInstrumentsEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_instrument_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_instrument_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentInstrumentUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_instrument_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_instrument_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save instrument to db
                saveInstrument(); //insertinstrument()
                //Exit activity after inserting and saving data from editor activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {

        if (mCurrentInstrumentUri == null) {
            return null;
        }
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                InstrumentsContract.MusicalInstrumentsEntry._ID,
                InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_NAME,
                InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_PRICE,
                InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_QUANTITY,
                InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_COUNTRY_OF_ORIGIN,
                InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_NAME,
                InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_PHONE_NUMER};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentInstrumentUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_QUANTITY);
            int countryOfOriginColumnIndex = cursor.getColumnIndex(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_COUNTRY_OF_ORIGIN);
            int suppierNameColumnIndex = cursor.getColumnIndex(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_PHONE_NUMER);
            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String countryOfOrigin = cursor.getString(countryOfOriginColumnIndex);
            String supplierName = cursor.getString(suppierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantitytEditText.setText(Integer.toString(quantity));
            mCountryOfOrigintEditText.setText(countryOfOrigin);
            mSupllierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);

        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantitytEditText.setText("");
        mCountryOfOrigintEditText.setText("");
        mSupllierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }
}


