package onyx.com.phonecloud.request;

import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.onyx.android.sdk.rx.RxRequest;

import java.util.List;

import onyx.com.phonecloud.common.SmsUtils;
import com.android.mms.model.ShortMessage;

/**
 * Created by TonyXie on 2020-03-03
 */
public class LoadSmsRequest extends RxRequest {
    private List<ShortMessage> list;

    @Override
    public void execute() throws Exception {
        Uri queryUri = Telephony.Sms.CONTENT_URI;
        Cursor cursor = null;
        try {
            cursor = getContext().getContentResolver().query(queryUri, null, null, null, null);
            list = SmsUtils.querySmsList(cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    public List<ShortMessage> getList() {
        return list;
    }
}
