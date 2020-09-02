package com.onyx.gallery.action;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.account.OnyxAccountModel;
import com.onyx.android.sdk.rx.RxAction;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.ResManager;
import com.onyx.gallery.common.BaseAction;
import com.onyx.gallery.request.ShareToCloudRequest;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/1/3 16:35
 *     desc   :
 * </pre>
 */
public class ShareToCloudAction extends RxAction<ShareToCloudRequest> {

    private String filePath;
    private OnyxAccountModel accountModel;

    public ShareToCloudAction(String filePath, OnyxAccountModel accountModel) {
        this.filePath = filePath;
        this.accountModel = accountModel;
    }

    @Override
    public void execute(RxCallback<ShareToCloudRequest> callback) {
        CloudManager cloudManager = new CloudManager().useContentCloudConf();
        ShareToCloudRequest request = new ShareToCloudRequest(cloudManager, filePath)
                .setToken(accountModel.getToken())
                .setAccountUid(String.valueOf(accountModel.getUid()));
        cloudManager.submitRequest(ResManager.getAppContext(), request, callback);
    }

}
