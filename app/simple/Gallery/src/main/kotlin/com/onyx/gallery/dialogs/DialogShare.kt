package com.onyx.gallery.dialogs;

import android.content.DialogInterface
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.View
import android.widget.RelativeLayout
import com.onyx.android.sdk.data.model.account.OnyxAccountModel
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.rxbroadcast.RxBroadcast
import com.onyx.android.sdk.utils.DateTimeUtil
import com.onyx.android.sdk.utils.FileUtils
import com.onyx.android.sdk.utils.NetworkUtil
import com.onyx.android.sdk.utils.ResManager
import com.onyx.gallery.R
import com.onyx.gallery.action.ShareToCloudAction
import com.onyx.gallery.bundle.EditBundle.Companion.newSingleThreadManager
import com.onyx.gallery.databinding.DialogShareBinding
import com.onyx.gallery.helpers.OSS_SHARE_FILE_EXPIRED_TIME_SECOND
import com.onyx.gallery.request.MakeQRCodeRequest
import com.onyx.gallery.request.ShareToCloudRequest
import com.onyx.gallery.utils.StatusLayoutManagerUtils
import com.onyx.gallery.viewmodel.ShareViewModel
import io.reactivex.disposables.Disposable
import me.bakumon.statuslayoutmanager.library.OnStatusChildClickListener
import me.bakumon.statuslayoutmanager.library.StatusLayoutManager
import java.io.File
import java.util.*


/**
 * Created by Leung 2020/9/15 10:35
 **/
class DialogShare(val shareFilePath: String, val accountModel: OnyxAccountModel) : BaseDialog<DialogShareBinding>(), OnStatusChildClickListener {

    private var statusLayoutManager: StatusLayoutManager? = null
    private var viewModel: ShareViewModel? = null
    private var networkDisposable: Disposable? = null

    override fun getLayoutRes(): Int = R.layout.dialog_share

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        resetUiSize()
        fullScreen()
    }

    override fun onStart() {
        super.onStart()
        fullScreen()
    }

    private fun resetUiSize() {
        val layoutParams: RelativeLayout.LayoutParams = binding.tips.layoutParams as RelativeLayout.LayoutParams
        layoutParams.setMargins(0, ResManager.getDimens(R.dimen.dialog_share_margin_top), 0, ResManager.getDimens(R.dimen.share_view_padding))
        binding.tips.layoutParams = layoutParams
    }

    private fun fullScreen() {
        val dm = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(dm)
        getDialog()!!.window.setLayout(dm.widthPixels, getDialog()!!.window.attributes.height)
    }

    override fun initBinding(binding: DialogShareBinding) {
        viewModel = ShareViewModel()
        binding.model = viewModel
        initStatusLayoutManager()
        initTitleBar()
        registerNetworkReceiver()
        checkWifi()
    }

    private fun initTitleBar() {
        if (binding.titleBar == null) {
            return
        }
        binding.titleBar.textTitle.setText(R.string.scan_share)
        binding.titleBar.textTitle.setOnClickListener { dismiss() }
    }

    private fun initStatusLayoutManager() {
        statusLayoutManager = StatusLayoutManagerUtils
                .getDefault(binding.statusContent)
                .setOnStatusChildClickListener(this)
                .build()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        unregisterNetworkReceiver()
    }

    override fun onEmptyChildClick(view: View) {}

    override fun onErrorChildClick(view: View) {}

    override fun onCustomerChildClick(view: View) {
        if (view.id == R.id.quit) {
            dismiss()
        } else if (view.id == R.id.tv_open_wifi) {
            NetworkUtil.enableWifiOpenAndDetect(context)
            StatusLayoutManagerUtils.showWifiOpeningLayout(statusLayoutManager)
        }
    }

    private fun checkWifi() {
        if (NetworkUtil.isWiFiConnected(ResManager.getAppContext())) {
            return
        }
        StatusLayoutManagerUtils.showOpenWifiTipLayout(statusLayoutManager)
    }

    private fun registerNetworkReceiver() {
        if (networkDisposable != null) {
            return
        }
        networkDisposable = RxBroadcast.connectivityChange(ResManager.getAppContext(), object : RxCallback<Boolean?>() {
            override fun onNext(connected: Boolean) {
                if (connected) {
                    unregisterNetworkReceiver()
                    shareToCloud()
                }
            }
        })
    }

    private fun unregisterNetworkReceiver() {
        networkDisposable?.dispose()
        networkDisposable = null
    }

    private fun shareToCloud() {
        statusLayoutManager!!.showCustomLayout(R.layout.status_layout_generating_qr_code)
        ShareToCloudAction(shareFilePath, accountModel).execute(object : RxCallback<ShareToCloudRequest?>() {
            override fun onNext(shareToCloudRequest: ShareToCloudRequest) {
                showShareQRCode(shareToCloudRequest.shareUrl)
                statusLayoutManager!!.showSuccessLayout()
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                statusLayoutManager!!.showCustomLayout(R.layout.status_layout_upload_note_failed, R.id.quit)
            }
        })
    }

    private fun showShareQRCode(shareUrl: String) {
        binding.tips.visibility = View.VISIBLE
        val qrSize = ResManager.getDimens(R.dimen.share_note_qr_image_size)
        val request = MakeQRCodeRequest()
                .setUrl(shareUrl)
                .setWidth(qrSize)
                .setHeight(qrSize)
        newSingleThreadManager().enqueue(request, object : RxCallback<MakeQRCodeRequest?>() {
            override fun onNext(makeQRCodeRequest: MakeQRCodeRequest) {
                binding.qrView.setImageBitmap(makeQRCodeRequest.bitmap)
                loadPdfPageCount()
            }
        })
    }

    private fun loadPdfPageCount() {
        updateFileInfo()
    }

    private fun updateFileInfo() {
        val file = File(shareFilePath)
        val size = FileUtils.getFileSize(FileUtils.getFileSize(file))
        val date = Date(System.currentTimeMillis() + OSS_SHARE_FILE_EXPIRED_TIME_SECOND * 1000)
        val time = DateTimeUtil.formatDate(date)
        viewModel?.run {
            setFileTitle(ResManager.getString(R.string.file_name_des).toString() + file.name)
            setFileSize(ResManager.getString(R.string.file_size_des).toString() + size)
            setFileTime(ResManager.getString(R.string.file_expired_time_des).toString() + time)
        }
    }

}
