package onyx.com.phonecloud.action;

import com.onyx.android.sdk.rx.RxAction;

import onyx.com.phonecloud.PhoneCloudStoreManager;

/**
 * Created by TonyXie on 2020-02-28
 */
public abstract class BasePhoneCloudStoreAction extends RxAction {
    protected PhoneCloudStoreManager getCloudNoteManager() {
        return PhoneCloudStoreManager.getInstance();
    }
}
