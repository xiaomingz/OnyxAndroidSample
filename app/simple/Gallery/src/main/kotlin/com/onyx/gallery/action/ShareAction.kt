package com.onyx.gallery.action

import androidx.fragment.app.FragmentActivity
import com.onyx.android.sdk.rx.RxAction
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.gallery.R
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.dialogs.DialogShare
import com.onyx.gallery.request.GetOnyxAccountInfoRequest
import com.onyx.gallery.utils.ToastUtils

/**
 * Created by Leung 2020/8/6 14:39
 **/
class ShareAction(private val activity: FragmentActivity, private val filePath: String) : RxAction<BaseRequest>() {

    override fun execute(rxCallback: RxCallback<BaseRequest>?) {
        EditBundle.newSingleThreadManager().enqueue(GetOnyxAccountInfoRequest(EditBundle(activity)), object : RxCallback<GetOnyxAccountInfoRequest>() {
            override fun onNext(request: GetOnyxAccountInfoRequest) {
                if (request.accountModel == null) {
                    ToastUtils.showScreenCenterToast(activity, R.string.share_with_no_account_tips)
                    return
                }
                DialogShare().apply {
                    shareFilePath = filePath
                    accountModel = request.accountModel!!
                }.show(activity.supportFragmentManager, DialogShare::class.java.simpleName)
            }
        })
    }

}