package onyx.com.phonecloud.model;

import com.android.mms.model.ThreadModel;

import java.util.List;

/**
 * Created by TonyXie on 2020-03-05
 */
public class UpdateEntity {
    private String title = "title";
    private String mac = "mac";
    private List<CallLogInfo> callLogs;
    private List<ContactInfo> contacts;
    private List<ThreadModel> threadModels;

    public List<CallLogInfo> getCallLogs() {
        return callLogs;
    }

    public void setCallLogs(List<CallLogInfo> callLogs) {
        this.callLogs = callLogs;
    }

    public List<ContactInfo> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactInfo> contacts) {
        this.contacts = contacts;
    }

    public List<ThreadModel> getThreadModels() {
        return threadModels;
    }

    public void setThreadModels(List<ThreadModel> threadModels) {
        this.threadModels = threadModels;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
