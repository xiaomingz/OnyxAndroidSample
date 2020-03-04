package com.android.mms.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TonyXie on 2020-03-03
 */
public class SmsModelList implements Serializable {
    private static final long serialVersionUID = 1235213654984563214L;

    public List<ShortMessage> smsList;

    public SmsModelList() {
    }

    public SmsModelList(List<ShortMessage> smsList) {
        this.smsList = smsList;
    }

    public int getSize() {
        return smsList == null ? 0 : smsList.size();
    }
}
