package com.android.mms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TonyXie on 2020-03-03
 */
public class ShortMessage implements Parcelable {

    private String id;
    private int threadId;
    private String address;
    private int person;
    private long date;
    private long dateSent;
    private int protocol;
    private int read;
    private int status;
    private int type;
    private int replyPathPresent;
    private String subject;
    private String body;
    private String serviceCenter;
    private int locked;
    private int errorCode;
    private int seen;

    public ShortMessage() {
    }

    protected ShortMessage(Parcel in) {
        id = in.readString();
        threadId = in.readInt();
        address = in.readString();
        person = in.readInt();
        date = in.readLong();
        dateSent = in.readLong();
        protocol = in.readInt();
        read = in.readInt();
        status = in.readInt();
        type = in.readInt();
        replyPathPresent = in.readInt();
        subject = in.readString();
        body = in.readString();
        serviceCenter = in.readString();
        locked = in.readInt();
        errorCode = in.readInt();
        seen = in.readInt();
    }

    public static final Creator<ShortMessage> CREATOR = new Creator<ShortMessage>() {
        @Override
        public ShortMessage createFromParcel(Parcel in) {
            return new ShortMessage(in);
        }

        @Override
        public ShortMessage[] newArray(int size) {
            return new ShortMessage[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPerson() {
        return person;
    }

    public void setPerson(int person) {
        this.person = person;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDateSent() {
        return dateSent;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getReplyPathPresent() {
        return replyPathPresent;
    }

    public void setReplyPathPresent(int replyPathPresent) {
        this.replyPathPresent = replyPathPresent;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getServiceCenter() {
        return serviceCenter;
    }

    public void setServiceCenter(String serviceCenter) {
        this.serviceCenter = serviceCenter;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(threadId);
        dest.writeString(address);
        dest.writeInt(person);
        dest.writeLong(date);
        dest.writeLong(dateSent);
        dest.writeInt(protocol);
        dest.writeInt(read);
        dest.writeInt(status);
        dest.writeInt(type);
        dest.writeInt(replyPathPresent);
        dest.writeString(subject);
        dest.writeString(body);
        dest.writeString(serviceCenter);
        dest.writeInt(locked);
        dest.writeInt(errorCode);
        dest.writeInt(seen);
    }
}
