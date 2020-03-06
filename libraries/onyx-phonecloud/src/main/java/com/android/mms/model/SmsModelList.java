package com.android.mms.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TonyXie on 2020-03-03
 */
public class SmsModelList implements Serializable {
    private static final long serialVersionUID = 1235213654984563214L;

    public List<ThreadModel> threads;

    public SmsModelList() {
    }

    public SmsModelList(List<ThreadModel> threads) {
        this.threads = threads;
    }

    public int getSize() {
        return threads == null ? 0 : threads.size();
    }
}
