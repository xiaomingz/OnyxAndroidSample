package onyx.com.phonecloud.request;

import com.onyx.android.sdk.rx.RxRequest;

import java.util.ArrayList;

import onyx.com.phonecloud.common.SmsUtils;
import com.android.mms.model.ShortMessage;

/**
 * Created by TonyXie on 2020-03-03
 */
public class InsetSmsRequest extends RxRequest {
    private ArrayList<ShortMessage> list;

    @Override
    public void execute() throws Exception {
        SmsUtils.insertSmsList(getContext(), list);
    }

    public InsetSmsRequest setList(ArrayList<ShortMessage> list) {
        this.list = list;
        return this;
    }
}
