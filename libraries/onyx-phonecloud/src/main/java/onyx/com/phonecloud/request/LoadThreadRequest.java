package onyx.com.phonecloud.request;

import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;

import com.android.mms.model.MmsModel;
import com.android.mms.model.ThreadModel;
import com.onyx.android.sdk.rx.RxRequest;

import java.util.ArrayList;
import java.util.List;

import onyx.com.phonecloud.common.SmsUtils;


/**
 * Created by TonyXie on 2020-03-03
 */
public class LoadThreadRequest extends RxRequest {
    private List<ThreadModel> list = new ArrayList<>();

    @Override
    public void execute() throws Exception {
        Uri threadUir = Telephony.Threads.CONTENT_URI.buildUpon().appendQueryParameter("simple", "true").build();
        Cursor threadCursor = null;

        try {
            threadCursor = getContext().getContentResolver().query(threadUir, null, null, null, null);

            if (threadCursor.moveToFirst()) {
                do {
                    String threadId = threadCursor.getString(threadCursor.getColumnIndex(Telephony.Threads._ID));
                    Cursor smsCursor = null;
                    Cursor mmsCursor = null;
                    ThreadModel threadModel = SmsUtils.queryThreadModel(threadCursor);
                    try {
                        smsCursor = SmsUtils.getCursorByThreadId(getContext(), Telephony.Sms.CONTENT_URI, threadId);
                        threadModel.setShortMessages(SmsUtils.querySmsList(smsCursor));
                        mmsCursor = SmsUtils.getCursorByThreadId(getContext(), Telephony.Mms.CONTENT_URI, threadId);
                        List<MmsModel> mmsModels = new ArrayList<>();
                        if (mmsCursor.moveToFirst()) {
                            do {
                                MmsModel mmsModel = SmsUtils.queryMmsModel(mmsCursor);
                                Cursor partCursor = null;
                                Cursor addrCursor = null;
                                try {
                                    String selectionPart = "mid=?";
                                    partCursor = getContext().getContentResolver().query(Uri.parse("content://mms/part"), null,
                                            selectionPart, new String[]{String.valueOf(mmsModel.getId())}, null);
                                    mmsModel.setPartModels(SmsUtils.queryPartList(partCursor));
                                    addrCursor = getContext().getContentResolver().query(Uri.parse("content://mms/addr"), null,
                                            selectionPart, new String[]{String.valueOf(mmsModel.getId())}, null);
                                    if (addrCursor.moveToFirst()) {
                                        mmsModel.setAddress(addrCursor.getString(addrCursor.getColumnIndex(Telephony.Mms.Addr.ADDRESS)));
                                        mmsModel.setAddressType(addrCursor.getInt(addrCursor.getColumnIndex(Telephony.Mms.Addr.TYPE)));
                                    }
                                    mmsModel.setPartModels(SmsUtils.queryPartList(partCursor));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    if (partCursor != null) {
                                        partCursor.close();
                                        partCursor = null;
                                    }
                                    if (addrCursor != null) {
                                        addrCursor.close();
                                        addrCursor = null;
                                    }
                                }
                                mmsModels.add(mmsModel);
                            } while (mmsCursor.moveToNext());
                            threadModel.setMmsModelList(mmsModels);
                        }
                        list.add(threadModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (smsCursor != null) {
                            smsCursor.close();
                            smsCursor = null;
                        }
                        if (mmsCursor != null) {
                            mmsCursor.close();
                            mmsCursor = null;
                        }
                    }
                } while (threadCursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (threadCursor != null) {
                threadCursor.close();
                threadCursor = null;
            }
        }
    }

    public List<ThreadModel> getList() {
        return list;
    }
}
