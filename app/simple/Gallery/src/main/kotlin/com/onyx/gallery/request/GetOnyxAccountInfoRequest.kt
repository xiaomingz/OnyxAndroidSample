package com.onyx.gallery.request

import com.onyx.android.sdk.data.model.account.OnyxAccountModel
import com.onyx.android.sdk.data.provider.DataProviderManager
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/8/6 14:41
 **/
class GetOnyxAccountInfoRequest : BaseRequest() {
    var accountModel: OnyxAccountModel? = null
    override fun execute(drawHandler: DrawHandler) {
        val accountProvider = DataProviderManager.getRemoteAccountProvider()
        accountModel = accountProvider.loggedInAccount
    }
}