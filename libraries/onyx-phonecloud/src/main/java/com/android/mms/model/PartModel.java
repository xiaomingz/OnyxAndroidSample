package com.android.mms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TonyXie on 2020-03-04
 */
public class PartModel implements Parcelable {

    private int seq;
    private String ct;
    private String name;
    private int chset;
    private String cd;
    private String fn;
    private String cid;
    private String cl;
    private int cttS;
    private String cttT;
    private String data;
    private String text;
    private String address;
    private int addressType;

    public PartModel() {
    }

    protected PartModel(Parcel in) {
        seq = in.readInt();
        ct = in.readString();
        name = in.readString();
        chset = in.readInt();
        cd = in.readString();
        fn = in.readString();
        cid = in.readString();
        cl = in.readString();
        cttS = in.readInt();
        cttT = in.readString();
        data = in.readString();
        text = in.readString();
        address = in.readString();
        addressType = in.readInt();
    }

    public static final Creator<PartModel> CREATOR = new Creator<PartModel>() {
        @Override
        public PartModel createFromParcel(Parcel in) {
            return new PartModel(in);
        }

        @Override
        public PartModel[] newArray(int size) {
            return new PartModel[size];
        }
    };

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChset() {
        return chset;
    }

    public void setChset(int chset) {
        this.chset = chset;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCl() {
        return cl;
    }

    public void setCl(String cl) {
        this.cl = cl;
    }

    public int getCttS() {
        return cttS;
    }

    public void setCttS(int cttS) {
        this.cttS = cttS;
    }

    public String getCttT() {
        return cttT;
    }

    public void setCttT(String cttT) {
        this.cttT = cttT;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(seq);
        dest.writeString(ct);
        dest.writeString(name);
        dest.writeInt(chset);
        dest.writeString(cd);
        dest.writeString(fn);
        dest.writeString(cid);
        dest.writeString(cl);
        dest.writeInt(cttS);
        dest.writeString(cttT);
        dest.writeString(data);
        dest.writeString(text);
        dest.writeString(address);
        dest.writeInt(addressType);
    }
}
