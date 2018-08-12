package com.example.android.instrumentsinventoryapp;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.instrumentsinventoryapp.data.InstrumentsContract;

public class EditorActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantitytEditText;
    private EditText mCountryOfOrigintEditText;
    private EditText mSupllierNameEditText;
    private EditText mSupplierPhoneEditText;
    private Button mIncrementBtn;
    private Button mDecrementBtn;
    private int item_quantity = 0;
    private static final int LOADER_MANAGER_ID = 0;
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
    private void insertInstrument() {

        String nameString = mNameEditText.getText().toString().trim();

        String priceString = mPriceEditText.getText().toString().trim();
        int priceInt = Integer.parseInt(priceString);
        String quantityString = mQuantitytEditText.getText().toString().trim();
        int quantityInt = Integer.parseInt(quantityString);
        String countryOfOriginString = mCountryOfOrigintEditText.getText().toString();
        String supplierNameString = mSupllierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();


        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_NAME, nameString);
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_PRICE, priceInt);
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_QUANTITY, quantityInt);
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_COUNTRY_OF_ORIGIN, countryOfOriginString);
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_PHONE_NUMER, supplierPhoneString);

        // Insert a new instrument into the provider, returning the content URI for the new instrument.
        Uri newUri = getContentResolver().insert(InstrumentsContract.MusicalInstrumentsEntry.BASE_CONTENT_URI, values);

        //show a toas message
        //if succesful
        if (newUri == null) {
            Toast.makeText(this, "Inzert instrument failed", Toast.LENGTH_LONG).show(); //R.string.....
        } else {
            Toast.makeText(this, "Instrument succesfully added.", Toast.LENGTH_LONG).show();
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
                insertInstrument();
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
}


