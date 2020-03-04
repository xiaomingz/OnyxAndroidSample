package com.android.mms.model;

import java.io.Serializable;

/**
 * Created by TonyXie on 2020-03-03
 */
public class ShortMessage implements Serializable {
    private static final long serialVersionUID = 4562136545698456984L;

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
}
