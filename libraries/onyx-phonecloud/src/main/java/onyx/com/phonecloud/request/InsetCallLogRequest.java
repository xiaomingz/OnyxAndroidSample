package onyx.com.phonecloud.request;

import com.onyx.android.sdk.rx.RxRequest;

import java.util.List;

import onyx.com.phonecloud.common.ContactUtils;
import onyx.com.phonecloud.model.CallLogInfo;

/**
 * Created by TonyXie on 2020-03-02
 */
public class InsetCallLogRequest extends RxRequest {
    private List<CallLogInfo> callLogInfos;

    public InsetCallLogRequest setCallLogInfos(List<CallLogInfo> callLogInfos) {
        this.callLogInfos = callLogInfos;
        return this;
    }

    @Override
    public void execute() throws Exception {
        if (callLogInfos == null) {
            return;
        }
        for (CallLogInfo info : callLogInfos) {
            ContactUtils.insetCallLog(getContext().getContentResolver(), info);
        }
    }
}
