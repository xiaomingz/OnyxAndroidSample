package com.android.mms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by TonyXie on 2020-03-04
 */
public class ThreadModel implements Parcelable {

    private long date;
    private int messageCount;
    private String snippet;
    private int snippetCharset;
    private int read;
    private int type;
    private List<ShortMessage> shortMessages;
    private List<MmsModel> mmsModelList;

    public ThreadModel() {
    }

    protected ThreadModel(Parcel in) {
        date = in.readLong();
        messageCount = in.readInt();
        snippet = in.readString();
        snippetCharset = in.readInt();
        read = in.readInt();
        type = in.readInt();
        shortMessages = in.createTypedArrayList(ShortMessage.CREATOR);
        mmsModelList = in.createTypedArrayList(MmsModel.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(date);
        dest.writeInt(messageCount);
        dest.writeString(snippet);
        dest.writeInt(snippetCharset);
        dest.writeInt(read);
        dest.writeInt(type);
        dest.writeTypedList(shortMessages);
        dest.writeTypedList(mmsModelList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ThreadModel> CREATOR = new Creator<ThreadModel>() {
        @Override
        public ThreadModel createFromParcel(Parcel in) {
            return new ThreadModel(in);
        }

        @Override
        public ThreadModel[] newArray(int size) {
            return new ThreadModel[size];
        }
    };

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
