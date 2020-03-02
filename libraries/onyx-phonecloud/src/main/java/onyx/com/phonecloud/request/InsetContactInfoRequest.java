package onyx.com.phonecloud.request;

import com.onyx.android.sdk.rx.RxRequest;

import java.util.List;

import onyx.com.phonecloud.common.ContactUtils;
import onyx.com.phonecloud.model.CallLogInfo;
import onyx.com.phonecloud.model.ContactInfo;

/**
 * Created by TonyXie on 2020-03-02
 */
public class InsetContactInfoRequest extends RxRequest {
    private List<ContactInfo> contactInfos;

    public InsetContactInfoRequest setContactInfoInfos(List<ContactInfo> contactInfos) {
        this.contactInfos = contactInfos;
        return this;
    }

    @Override
    public void execute() throws Exception {
        if (contactInfos == null) {
            return;
        }
        for (ContactInfo info : contactInfos) {
            ContactUtils.insetContactInfo(getContext().getContentResolver(), info);
        }
    }
}
