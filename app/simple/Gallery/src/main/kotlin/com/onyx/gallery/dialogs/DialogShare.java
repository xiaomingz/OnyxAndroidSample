package com.onyx.gallery.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.onyx.android.sdk.data.model.account.OnyxAccountModel;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.rxbroadcast.RxBroadcast;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.ResManager;
import com.onyx.gallery.R;
import com.onyx.gallery.action.ShareToCloudAction;
import com.onyx.gallery.bundle.GlobalEditBundle;
import com.onyx.gallery.common.BaseNoteDialog;
import com.onyx.gallery.databinding.DialogShareBinding;
import com.onyx.gallery.request.MakeQRCodeRequest;
import com.onyx.gallery.request.ShareToCloudRequest;
import com.onyx.gallery.utils.StatusLayoutManagerUtils;
import com.onyx.gallery.viewmodel.ShareViewModel;

import java.io.File;
import java.util.Date;

import io.reactivex.disposables.Disposable;
import me.bakumon.statuslayoutmanager.library.OnStatusChildClickListener;
import me.bakumon.statuslayoutmanager.library.StatusLayoutManager;

import static com.onyx.gallery.helpers.ConstantsKt.OSS_SHARE_FILE_EXPIRED_TIME_SECOND;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/1/3 16:03
 *     desc   :
 * </pre>
 */
public class DialogShare extends BaseNoteDialog implements OnStatusChildClickListener {

    private DialogShareBinding binding;
    private String shareFilePath;
    private StatusLayoutManager statusLayoutManager;
    private ShareViewModel viewModel;
    private Disposable networkDisposable;
    private OnyxAccountModel accountModel;

    public DialogShare(@NonNull Context context) {
        super(context, R.style.FullScreenDialog);
        initView(context);
    }

    public DialogShare setShareFilePath(String shareFilePath) {
        this.shareFilePath = shareFilePath;
        return this;
    }

    private void initView(Context context) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_share,
                null,
                false);
        viewModel = new ShareViewModel();
        binding.setModel(viewModel);
        setContentView(binding.getRoot());
        initStatusLayoutManager();
        initTitleBar();
    }

    private void initTitleBar() {
        if (binding.titleBar == null) {
            return;
        }
        binding.titleBar.textTitle.setText(R.string.scan_share);
        binding.titleBar.textTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initStatusLayoutManager() {
        statusLayoutManager = StatusLayoutManagerUtils
                .getDefault(binding.statusContent)
                .setOnStatusChildClickListener(this)
                .build();
    }

    @Override
    public void show() {
        super.show();
        registerNetworkReceiver();
        checkWifi();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        fullScreen(getContext());
    }

    private void checkWifi() {
        if (NetworkUtil.isWiFiConnected(ResManager.getAppContext())) {
            return;
        }
        StatusLayoutManagerUtils.showOpenWifiTipLayout(statusLayoutManager);
    }

    private void registerNetworkReceiver() {
        if (networkDisposable != null) {
            return;
        }
        networkDisposable = RxBroadcast.connectivityChange(ResManager.getAppContext(), new RxCallback<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean connected) {
                if (connected) {
                    unregisterNetworkReceiver();
                    shareToCloud();
                }
            }
        });
    }

    private void unregisterNetworkReceiver() {
        if (networkDisposable == null) {
            return;
        }
        networkDisposable.dispose();
        networkDisposable = null;
    }

    private void showShareQRCode(String shareUrl) {
        binding.tips.setVisibility(View.VISIBLE);
        int qrSize = ResManager.getDimens(R.dimen.share_note_qr_image_size);
        MakeQRCodeRequest request = new MakeQRCodeRequest()
                .setUrl(shareUrl)
                .setWidth(qrSize)
                .setHeight(qrSize);
        GlobalEditBundle.Companion.getInstance().enqueue(request, new RxCallback<MakeQRCodeRequest>() {
            @Override
            public void onNext(@NonNull MakeQRCodeRequest makeQRCodeRequest) {
                binding.qrView.setImageBitmap(makeQRCodeRequest.getBitmap());
                loadPdfPageCount();
            }
        });
    }

    private void shareToCloud() {
        statusLayoutManager.showCustomLayout(R.layout.status_layout_generating_qr_code);
        new ShareToCloudAction(shareFilePath, accountModel).execute(new RxCallback<ShareToCloudRequest>() {
            @Override
            public void onNext(@NonNull ShareToCloudRequest shareToCloudRequest) {
                showShareQRCode(shareToCloudRequest.getShareUrl());
                statusLayoutManager.showSuccessLayout();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                super.onError(e);
                statusLayoutManager.showCustomLayout(R.layout.status_layout_upload_note_failed, R.id.quit);
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        unregisterNetworkReceiver();
    }

    private void loadPdfPageCount() {
        updateFileInfo();
    }

    private void updateFileInfo() {
        File file = new File(shareFilePath);
        String size = FileUtils.getFileSize(FileUtils.getFileSize(file));
        Date date = new Date(System.currentTimeMillis() + OSS_SHARE_FILE_EXPIRED_TIME_SECOND * 1000);
        String time = DateTimeUtil.formatDate(date);
        viewModel.setFileTitle(ResManager.getString(R.string.file_name_des) + file.getName());
        viewModel.setFileSize(ResManager.getString(R.string.file_size_des) + size);
        viewModel.setFileTime(ResManager.getString(R.string.file_expired_time_des) + time);
    }

    private GlobalEditBundle getGlobalEditBundle() {
        return GlobalEditBundle.Companion.getInstance();
    }

    @Override
    public void onEmptyChildClick(View view) {

    }

    @Override
    public void onErrorChildClick(View view) {

    }

    @Override
    public void onCustomerChildClick(View view) {
        if (view.getId() == R.id.quit) {
            dismiss();
        } else if (view.getId() == R.id.tv_open_wifi) {
            NetworkUtil.enableWifiOpenAndDetect(getContext());
            StatusLayoutManagerUtils.showWifiOpeningLayout(statusLayoutManager);
        }
    }

    public DialogShare setAccountModel(OnyxAccountModel accountModel) {
        this.accountModel = accountModel;
        return this;
    }
}
