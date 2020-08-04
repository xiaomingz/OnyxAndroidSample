package com.onyx.gallery.request;

import androidx.annotation.Nullable;

import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.config.oss.OssConfig;
import com.onyx.android.sdk.data.manager.OssManager;
import com.onyx.android.sdk.data.model.ResponeData;
import com.onyx.android.sdk.data.model.share.ShareUrl;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.ResManager;

import java.io.File;

import retrofit2.Response;

import static com.onyx.android.sdk.data.Constant.OSS_OBJECT_ACL_TAG;
import static com.onyx.android.sdk.data.Constant.OSS_PUBLIC_READ_TAG;
import static com.onyx.android.sdk.data.Constant.OSS_SHARE_FILE_EXPIRED_TIME_SECOND;
import static com.onyx.android.sdk.data.Constant.SHARE_TAG;
import static com.onyx.android.sdk.data.Constant.TEXT_PLAIN_CONTENT_TYPE;


/**
 * <pre>
 *     author : lxw
 *     time   : 2019/1/3 16:47
 *     desc   :
 * </pre>
 */
public class ShareToCloudRequest extends BaseCloudRequest {

    private String filePath;
    private String shareUrl;
    private String token;
    private String accountUid;

    public ShareToCloudRequest(@Nullable CloudManager cloudManager, String filePath) {
        super(cloudManager);
        this.filePath = filePath;
    }

    public ShareToCloudRequest setToken(String token) {
        this.token = token;
        return this;
    }

    public ShareToCloudRequest setAccountUid(String accountUid) {
        this.accountUid = accountUid;
        return this;
    }

    @Override
    public void execute() throws Exception {
        OssManager ossManager = new OssManager(ResManager.getAppContext(), OssConfig.NoteOssConfig()
                .setToken(token));
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setHeader(OSS_OBJECT_ACL_TAG, OSS_PUBLIC_READ_TAG);
        if (FileUtils.isTxtFile(filePath)) {
            metadata.setContentType(TEXT_PLAIN_CONTENT_TYPE);
        }
        PutObjectRequest objectRequest = new PutObjectRequest(ossManager.getOssBucketName(),
                getFileObjectKey(),
                filePath,
                metadata);
        String objectKey = ossManager.syncUploadFile(getContext(), objectRequest);
        Response<ResponeData<ShareUrl>> response = executeCall(ServiceFactory
                .getShareService(getCloudManager()
                        .getCloudConf()
                        .getApiBase())
                .generateShareUrl(getBearerToken(token), objectKey, ossManager.getOssBucketName(), OSS_SHARE_FILE_EXPIRED_TIME_SECOND));
        if (!response.isSuccessful()) {
            throw new Exception(response.errorBody().string());
        }
        shareUrl = response.body().data.url;
    }

    private String getFileObjectKey() {
        return accountUid + File.separator + SHARE_TAG + File.separator + new File(filePath).getName();
    }

    public String getShareUrl() {
        return shareUrl;
    }


}
