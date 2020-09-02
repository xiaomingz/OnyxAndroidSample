package com.onyx.gallery.viewmodel;


import androidx.databinding.ObservableField;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/1/21 17:10
 *     desc   :
 * </pre>
 */
public class ShareViewModel {

    public ObservableField<String> fileTitle = new ObservableField<>("");
    public ObservableField<String> fileSize = new ObservableField<>("");
    public ObservableField<String> fileTime = new ObservableField<>("");
    public ObservableField<String> filePageCounts = new ObservableField<>("");

    public void setFileSize(String size) {
        this.fileSize.set(size);
    }

    public void setFileTime(String time) {
        this.fileTime.set(time);
    }

    public void setFileTitle(String title) {
        this.fileTitle.set(title);
    }

    public void setFilePageCounts(String counts) {
        this.filePageCounts.set(counts);
    }
}
