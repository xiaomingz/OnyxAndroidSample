package com.android.mms.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TonyXie on 2020-03-04
 */
public class ThreadModel implements Serializable {
    private static final long serialVersionUID = 4562136892698456984L;

    private long date;
    private int messageCount;
    private String snippet;
    private int snippetCharset;
    private int read;
    private int type;
    private List<ShortMessage> shortMessages;
    private List<MmsModel> mmsModelList;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getSnippetCharset() {
        return snippetCharset;
    }

    public void setSnippetCharset(int snippetCharset) {
        this.snippetCharset = snippetCharset;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<ShortMessage> getShortMessages() {
        return shortMessages;
    }

    public void setShortMessages(List<ShortMessage> shortMessages) {
        this.shortMessages = shortMessages;
    }

    public List<MmsModel> getMmsModelList() {
        return mmsModelList;
    }

    public void setMmsModelList(List<MmsModel> mmsModelList) {
        this.mmsModelList = mmsModelList;
    }
}
