package onyx.com.phonecloud.action;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.mms.model.ThreadModel;
import com.onyx.android.sdk.rx.RxCallback;

import java.util.ArrayList;
import java.util.List;

import onyx.com.phonecloud.common.JSONUtils;
import onyx.com.phonecloud.common.TestU;

import com.android.mms.model.ShortMessage;

import onyx.com.phonecloud.request.InsetSmsRequest;
import onyx.com.phonecloud.request.LoadSmsRequest;
import onyx.com.phonecloud.request.LoadThreadRequest;

/**
 * Created by TonyXie on 2020-02-28
 */
public class LoadContactInfoAction extends BasePhoneCloudStoreAction {

    @Override
    public void execute(RxCallback callback) {
//        LoadContactInfoRequest loadContactInfoRequest = new LoadContactInfoRequest();
//        getCloudNoteManager().enqueue(loadContactInfoRequest, new RxCallback<LoadContactInfoRequest>() {
//            @Override
//            public void onNext(@NonNull LoadContactInfoRequest loadContactInfoRequest) {
//                List<ContactInfo> contacts = loadContactInfoRequest.getContacts();
//            }
//        });

//        InsetSmsRequest request = new InsetSmsRequest().setList((ArrayList<ShortMessage>) JSONUtils.toList(TestU.getSmsInfo(), ShortMessage.class));
//        getCloudNoteManager().enqueue(request, null);
        LoadThreadRequest request = new LoadThreadRequest();
        getCloudNoteManager().enqueue(request, new RxCallback<LoadThreadRequest>() {
            @Override
            public void onNext(@NonNull LoadThreadRequest loadSmsRequest) {
                String s = JSONUtils.toJson(loadSmsRequest.getList());
                Log.e("tony", s);
            }
        });

    }
}
