package com.onyx.gallery.action

import android.app.Activity
import com.onyx.android.sdk.rx.RxAction
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.gallery.R
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.extensions.shareToCloud
import com.onyx.gallery.request.GetOnyxAccountInfoRequest
import com.onyx.gallery.utils.ToastUtils

/**
 * Created by Leung 2020/8/6 14:39
 **/
class ShareAction(private val activity: Activity, private val filePath: String) : RxAction<BaseRequest>() {

    override fun execute(rxCallback: RxCallback<BaseRequest>?) {
        EditBundle.newSingleThreadManager().enqueue(GetOnyxAccountInfoRequest(EditBundle(activity)), object : RxCallback<GetOnyxAccountInfoRequest>() {
            override fun onNext(request: GetOnyxAccountInfoRequest) {
                if (request.accountModel == null) {
                    ToastUtils.showScreenCenterToast(activity, R.string.share_with_no_account_tips)
                    return
                }
                activity.shareToCloud(filePath, request.accountModel!!)
            }
        })
    }

}