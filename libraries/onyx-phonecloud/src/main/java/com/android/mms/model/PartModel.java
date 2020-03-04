package com.android.mms.model;

import java.io.Serializable;

/**
 * Created by TonyXie on 2020-03-04
 */
public class PartModel implements Serializable {
    private static final long serialVersionUID = 4562196324698456853L;

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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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
}
