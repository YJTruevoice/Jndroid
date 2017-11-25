package com.yakir.jndroid.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author yakir
 *         Created on 2017/8/8.
 * @function Jndroid.
 */

public class Book implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
