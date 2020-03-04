package com.android.mms.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TonyXie on 2020-03-04
 */
public class MmsModel implements Serializable {
    private static final long serialVersionUID = 4562196324698456984L;
    private int id;
    private long date;
    private int msgBox;
    private int read;
    private String sub;
    private int subCharset;
    private String contentType;
    private String contentLocation;
    private long exp;
    private String mCls;
    private int mType;
    private int version;
    private int pri;
    private int mSize;
    private int rR;
    private int rptA;
    private int respSt;
    private int status;
    private String trId;
    private int retrSt;
    private String retrTxt;
    private int retrTxtCs;
    private int readStatus;
    private int ctCls;
    private String respTxt;
    private int dTm;
    private int dRpt;
    private int locked;
    private int seen;
    private String address;
    private int addressType;

    private List<PartModel> partModels;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getMsgBox() {
        return msgBox;
    }

    public void setMsgBox(int msgBox) {
        this.msgBox = msgBox;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public int getSubCharset() {
        return subCharset;
    }

    public void setSubCharset(int subCharset) {
        this.subCharset = subCharset;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentLocation() {
        return contentLocation;
    }

    public void setContentLocation(String contentLocation) {
        this.contentLocation = contentLocation;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public String getmCls() {
        return mCls;
    }

    public void setmCls(String mCls) {
        this.mCls = mCls;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getPri() {
        return pri;
    }

    public void setPri(int pri) {
        this.pri = pri;
    }

    public int getrR() {
        return rR;
    }

    public void setrR(int rR) {
        this.rR = rR;
    }

    public int getRptA() {
        return rptA;
    }

    public void setRptA(int rptA) {
        this.rptA = rptA;
    }

    public int getRespSt() {
        return respSt;
    }

    public void setRespSt(int respSt) {
        this.respSt = respSt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTrId() {
        return trId;
    }

    public void setTrId(String trId) {
        this.trId = trId;
    }

    public int getRetrSt() {
        return retrSt;
    }

    public void setRetrSt(int retrSt) {
        this.retrSt = retrSt;
    }

    public String getRetrTxt() {
        return retrTxt;
    }

    public void setRetrTxt(String retrTxt) {
        this.retrTxt = retrTxt;
    }

    public int getRetrTxtCs() {
        return retrTxtCs;
    }

    public void setRetrTxtCs(int retrTxtCs) {
        this.retrTxtCs = retrTxtCs;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public int getCtCls() {
        return ctCls;
    }

    public void setCtCls(int ctCls) {
        this.ctCls = ctCls;
    }

    public String getRespTxt() {
        return respTxt;
    }

    public void setRespTxt(String respTxt) {
        this.respTxt = respTxt;
    }

    public int getmSize() {
        return mSize;
    }

    public void setmSize(int mSize) {
        this.mSize = mSize;
    }

    public int getdTm() {
        return dTm;
    }

    public void setdTm(int dTm) {
        this.dTm = dTm;
    }

    public int getdRpt() {
        return dRpt;
    }

    public void setdRpt(int dRpt) {
        this.dRpt = dRpt;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
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

    public List<PartModel> getPartModels() {
        return partModels;
    }

    public void setPartModels(List<PartModel> partModels) {
        this.partModels = partModels;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
