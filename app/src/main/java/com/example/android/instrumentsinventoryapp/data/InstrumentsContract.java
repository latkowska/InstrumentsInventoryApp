package com.example.android.instrumentsinventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Musical Instruments Inventory app.
 */

//outer class
public final class InstrumentsContract {

    private InstrumentsContract() {
    }

    //inner class
    public static final class MusicalInstrumentsEntry implements BaseColumns {

        public static final String CONTENT_AUTHORITY = "com.example.android.instrumentsinventoryapp";

        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        public static final String INSTRUMENT_PATH = "musical_instruments"; //store.db


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, INSTRUMENT_PATH);

        /**
         * The MIME type for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + INSTRUMENT_PATH;

        /**
         * The MIME type for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + INSTRUMENT_PATH;


        public final static String TABLE_NAME = "musical_instruments";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_INSTRUMENT_NAME = "product_name";

        public final static String COLUMN_INSTRUMENT_PRICE = "price";

        public final static String COLUMN_INSTRUMENT_QUANTITY = "quantity";

        public final static String COLUMN_INSTRUMENT_COUNTRY_OF_ORIGIN = "country_of_origin";

        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";

        public final static String COLUMN_SUPPLIER_PHONE_NUMER = "supplier_phone_number";
    }
}

