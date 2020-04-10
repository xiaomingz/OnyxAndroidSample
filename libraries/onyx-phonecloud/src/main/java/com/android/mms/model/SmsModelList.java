package com.android.mms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by TonyXie on 2020-03-03
 */
public class SmsModelList implements Parcelable {

    public List<ThreadModel> threads;

    public SmsModelList() {
    }

    public SmsModelList(List<ThreadModel> threads) {
        this.threads = threads;
    }

    protected SmsModelList(Parcel in) {
        threads = in.createTypedArrayList(ThreadModel.CREATOR);
    }

    public static final Creator<SmsModelList> CREATOR = new Creator<SmsModelList>() {
        @Override
        public SmsModelList createFromParcel(Parcel in) {
            return new SmsModelList(in);
        }

        @Override
        public SmsModelList[] newArray(int size) {
            return new SmsModelList[size];
        }
    };

    public int getSize() {
        return threads == null ? 0 : threads.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(threads);
    }
}
