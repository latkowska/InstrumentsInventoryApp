package com.example.android.instrumentsinventoryapp.data;

public class Instrument {
    private int mInstrumentId;
    private int mInstrumentQuantity;

    public Instrument(int mInstrumentId, int mInstrumentQuantity) {
        this.mInstrumentId = mInstrumentId;
        this.mInstrumentQuantity = mInstrumentQuantity;
    }

    public int getmInstrumentId() {
        return mInstrumentId;
    }

    public int getmInstrumentQuantity() {
        return mInstrumentQuantity;
    }
}
