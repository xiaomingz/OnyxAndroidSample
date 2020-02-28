package com.onyx.android.sdk.common.event;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/2/21 15:08
 *     desc   :
 * </pre>
 */
public class SystemUIChangedEvent {

    public String type;
    public boolean open;

    public SystemUIChangedEvent(String type, boolean open) {
        this.type = type;
        this.open = open;
    }
}
