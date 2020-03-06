package onyx.com.phonecloud.request;

import com.android.mms.model.ThreadModel;
import com.onyx.android.sdk.rx.RxRequest;

import java.util.ArrayList;

import onyx.com.phonecloud.common.SmsUtils;

/**
 * Created by TonyXie on 2020-03-03
 */
public class InsetThreadsRequest extends RxRequest {
    private ArrayList<ThreadModel> list;

    @Override
    public void execute() throws Exception {
        SmsUtils.insertThreadList(getContext(), list);
    }

    public InsetThreadsRequest setList(ArrayList<ThreadModel> list) {
        this.list = list;
        return this;
    }
}
