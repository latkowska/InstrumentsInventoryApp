package com.example.android.instrumentsinventoryapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
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

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantitytEditText;
    private EditText mCountryOfOrigintEditText;
    private EditText mSupllierNameEditText;
    private EditText mSupplierPhoneEditText;
    private Button mIncrementButton;
    private Button mDecrementButton;
    private static final int EXISTING_INSTRUMENT_LOADER = 0;
    private Uri mCurrentInstrumentUri;
    private int instrumentQuantity = 0;

    private boolean mInstrumenttHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInstrumenttHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new instrument or editing an existing one.
        Intent intent = getIntent();
        mCurrentInstrumentUri = intent.getData();
        // If the intent DOES NOT contain a instrument content URI, then we know that we are
        // creating a new instrument.
        if (mCurrentInstrumentUri == null) {
            // This is a new instrument - change the app bar
            setTitle(getString(R.string.editor_activity_title_new_instrument));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing instrument - change app bar
            setTitle(getString(R.string.editor_activity_title_edit_instrument));
            // Initialize a loader to read the instrument data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISTING_INSTRUMENT_LOADER, null, this);
        }


        // Find all relevant views that we will need to read user input from.
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantitytEditText = (EditText) findViewById(R.id.edit_quantity);
        mIncrementButton = (Button) findViewById(R.id.increment_quantity);
        mDecrementButton = (Button) findViewById(R.id.decrement_quantity);
        mCountryOfOrigintEditText = (EditText) findViewById(R.id.edit_country_of_origin);
        mSupllierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);

        // Setup OnTouchListeners on all the input fields and onClickListener for buttons.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantitytEditText.setOnTouchListener(mTouchListener);
        mCountryOfOrigintEditText.setOnTouchListener(mTouchListener);
        mSupllierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionIncrementButton();
            }
        });
        mDecrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionDecrementButton();
            }
        });
    }

    public void actionIncrementButton() {
        instrumentQuantity = Integer.parseInt(mQuantitytEditText.getText().toString());
        instrumentQuantity = instrumentQuantity + 1;
        mQuantitytEditText.setText(Integer.toString(instrumentQuantity));
    }

    public void actionDecrementButton() {
        instrumentQuantity = Integer.parseInt(mQuantitytEditText.getText().toString());
        if (instrumentQuantity > 0) {
            instrumentQuantity = instrumentQuantity - 1;
        }
        mQuantitytEditText.setText(Integer.toString(instrumentQuantity));
    }


    //Get user input from the editor and save this data in db.
    private void saveInstrument() {

        ContentValues values = new ContentValues();

        //Get input values from EditText fields
        String nameString = mNameEditText.getText().toString().trim();
        String priceString;
        priceString = mPriceEditText.getText().toString().trim();
        if (TextUtils.isEmpty(priceString)) {
            priceString = "0";
        }

        String quantityString;
        quantityString = mQuantitytEditText.getText().toString().trim();
        if (TextUtils.isEmpty(quantityString)) {
            quantityString = "0";
        }
        String countryOfOriginString = mCountryOfOrigintEditText.getText().toString();
        String supplierNameString = mSupllierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        // Check if this is supposed to be a new instrument
        // and check if all the fields in the editor are blank
        if (mCurrentInstrumentUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(countryOfOriginString) &&
                TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneString)) {
            // No fields were modified return to home screen.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and instrument attributes from the editor are the values.
        boolean flag = true;
        if (TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) ||
                TextUtils.isEmpty(countryOfOriginString) ||
                TextUtils.isEmpty(supplierNameString) ||
                TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, R.string.empty_field_warning, Toast.LENGTH_LONG).show();
            flag = false;
        } else {
            values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_NAME, nameString);
            values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_PRICE, priceString);
            values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_QUANTITY, quantityString);
            values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_INSTRUMENT_COUNTRY_OF_ORIGIN, countryOfOriginString);
            values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
            values.put(InstrumentsContract.MusicalInstrumentsEntry.COLUMN_SUPPLIER_PHONE_NUMER, supplierPhoneString);
        }

        if (mCurrentInstrumentUri == null & flag) {
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
                finish();
            }
        } else {
            if (TextUtils.isEmpty(nameString) ||
                    TextUtils.isEmpty(priceString) ||
                    TextUtils.isEmpty(quantityString) ||
                    TextUtils.isEmpty(countryOfOriginString) ||
                    TextUtils.isEmpty(supplierNameString) ||
                    TextUtils.isEmpty(supplierPhoneString)) {
                Toast.makeText(this, R.string.empty_field_warning, Toast.LENGTH_LONG).show();
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
                    finish();

                }
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

    /**
     * This method is called after invalidateOptionsMenu(),that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new instrument, hide the "Delete" menu item.
        if (mCurrentInstrumentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save instrument to db
                saveInstrument();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                // If the instrument hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mInstrumenttHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
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
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the instrument hasn't changed, continue with handling back button press
        if (!mInstrumenttHasChanged) {
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {

        if (mCurrentInstrumentUri == null) {
            return null;
        }
        // Since the editor shows all instrument attributes, define a projection that contains
        // all columns from the musical_instruments table
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

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
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
     * Prompt the user to confirm that they want to delete this instrument.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteInstrument();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
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
     * Perform the deletion of the instrument in the database.
     */
    private void deleteInstrument() {
        // Only perform the delete if this is an existing instrument.
        if (mCurrentInstrumentUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentInstrumentUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_instrument_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_instrument_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    public void orderMore(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mSupplierPhoneEditText.getText().toString()));
        startActivity(intent);
    }
}



