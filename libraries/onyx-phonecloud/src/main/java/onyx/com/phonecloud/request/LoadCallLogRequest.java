package onyx.com.phonecloud.request;

import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import com.onyx.android.sdk.rx.RxRequest;

import java.util.List;

import onyx.com.phonecloud.common.ContactUtils;
import onyx.com.phonecloud.model.CallLogInfo;

/**
 * Created by TonyXie on 2020-03-02
 */
public class LoadCallLogRequest extends RxRequest {
    private List<CallLogInfo> callLogs;

    @Override
    public void execute() throws Exception {
        Uri queryUri = CallLog.Calls.CONTENT_URI;
        Cursor cursor = getContext().getContentResolver().query(queryUri, null, null, null, null);
        try {
            callLogs = ContactUtils.queryCallLogs(cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    public List<CallLogInfo> getCallLogs() {
        return callLogs;
    }
}
