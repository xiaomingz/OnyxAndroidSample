package com.onyx.gallery.action;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.account.OnyxAccountModel;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.ResManager;
import com.onyx.gallery.bundle.GlobalEditBundle;
import com.onyx.gallery.common.BaseEditAction;
import com.onyx.gallery.request.ShareToCloudRequest;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/1/3 16:35
 *     desc   :
 * </pre>
 */
public class ShareToCloudAction extends BaseEditAction<ShareToCloudRequest> {

    private String filePath;

    public ShareToCloudAction(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void execute(RxCallback<ShareToCloudRequest> callback) {
        OnyxAccountModel accountModel = getOnyxAccountModel();
        if (accountModel == null) {
            return;
        }
        CloudManager cloudManager = new CloudManager().useContentCloudConf();
        ShareToCloudRequest request = new ShareToCloudRequest(cloudManager, filePath)
                .setToken(accountModel.getToken())
                .setAccountUid(String.valueOf(accountModel.getUid()));
        cloudManager.submitRequest(ResManager.getAppContext(), request, callback);
    }

    private OnyxAccountModel getOnyxAccountModel() {
        return GlobalEditBundle.Companion.getInstance().getOnyxAccountModel();
    }
}
