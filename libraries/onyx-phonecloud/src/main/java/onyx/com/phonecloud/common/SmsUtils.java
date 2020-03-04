package onyx.com.phonecloud.common;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;

import com.android.mms.model.PartModel;
import com.android.mms.model.MmsModel;
import com.android.mms.model.SmsModelList;
import com.android.mms.model.ThreadModel;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import com.android.mms.model.ShortMessage;

/**
 * Created by TonyXie on 2020-03-03
 */
public class SmsUtils {
    public static final String SERVICE_ACTION = "service_action";
    public static final String INSERT_SMS = "insert_sms";
    public static final String SMS_MODE_LIST = "sms_mode_list";
    public static final String SMS_SERVICE_ACTION = "android.intent.action.OnyxSmsService";
    public static final String SMS_PACKAGE_NAME = "com.android.mms";

    public static List<ShortMessage> querySmsList(Cursor cursor) {
        List<ShortMessage> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                ShortMessage shortMessage = new ShortMessage();
                String id = cursor.getString(cursor.getColumnIndex(Telephony.Sms._ID));
                shortMessage.setId(id);
                int threadId = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.THREAD_ID));
                shortMessage.setThreadId(threadId);
                String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                shortMessage.setAddress(address);
                int person = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.PERSON));
                shortMessage.setPerson(person);
                long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
                shortMessage.setDate(date);
                long dateSent = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE_SENT));
                shortMessage.setDateSent(dateSent);
                int protocol = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.PROTOCOL));
                shortMessage.setProtocol(protocol);
                int read = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.READ));
                shortMessage.setRead(read);
                int status = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.STATUS));
                shortMessage.setStatus(status);
                int type = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.TYPE));
                shortMessage.setType(type);
                int replyPathPresent = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.REPLY_PATH_PRESENT));
                shortMessage.setReplyPathPresent(replyPathPresent);
                String subject = cursor.getString(cursor.getColumnIndex(Telephony.Sms.SUBJECT));
                shortMessage.setSubject(subject);
                String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                shortMessage.setBody(body);
                String serviceCenter = cursor.getString(cursor.getColumnIndex(Telephony.Sms.SERVICE_CENTER));
                shortMessage.setServiceCenter(serviceCenter);
                int locked = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.LOCKED));
                shortMessage.setLocked(locked);
                int errorCode = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.ERROR_CODE));
                shortMessage.setErrorCode(errorCode);
                int seen = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.SEEN));
                shortMessage.setSeen(seen);
                list.add(shortMessage);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public static boolean insertSmsList(Context context, ArrayList<ShortMessage> infos) {
        if (CollectionUtils.isNullOrEmpty(infos)) {
            return false;
        }
        SmsModelList smsModelList = new SmsModelList();
        smsModelList.smsList = infos;
        Intent intent = new Intent();
        intent.putExtra(SMS_MODE_LIST, smsModelList);
        intent.putExtra(SERVICE_ACTION, INSERT_SMS);
        intent.setAction(SMS_SERVICE_ACTION);
        intent.setPackage(SMS_PACKAGE_NAME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        return true;
    }

    public static Cursor getCursorByThreadId(Context context, Uri uri, String threadId) {
        String idWhere = Telephony.Sms.THREAD_ID + " = ? ";
        String[] params = new String[]{threadId};
        return context.getContentResolver().query(uri,
                null, idWhere, params, null);
    }

    public static MmsModel queryMmsModel(Cursor cursor) {
        MmsModel mmsModel = new MmsModel();
        mmsModel.setId(cursor.getInt(cursor.getColumnIndex(Telephony.Mms._ID)));
        mmsModel.setDate(cursor.getLong(cursor.getColumnIndex(Telephony.Mms.Inbox.DATE)));
        mmsModel.setMsgBox(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.MESSAGE_BOX)));
        mmsModel.setRead(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.READ)));
        mmsModel.setSub(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Inbox.SUBJECT)));
        mmsModel.setSubCharset(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.SUBJECT_CHARSET)));
        mmsModel.setContentType(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Inbox.CONTENT_TYPE)));
        mmsModel.setContentLocation(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Inbox.CONTENT_LOCATION)));
        mmsModel.setExp(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.EXPIRY)));
        mmsModel.setmCls(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Inbox.MESSAGE_CLASS)));
        mmsModel.setmType(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.MESSAGE_TYPE)));
        mmsModel.setVersion(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.MMS_VERSION)));
        mmsModel.setmSize(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.MESSAGE_SIZE)));
        mmsModel.setrR(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.READ_REPORT)));
        mmsModel.setRptA(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.REPORT_ALLOWED)));
        mmsModel.setRespSt(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.RESPONSE_STATUS)));
        mmsModel.setStatus(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.STATUS)));
        mmsModel.setTrId(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Inbox.TRANSACTION_ID)));
        mmsModel.setRetrSt(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.RETRIEVE_STATUS)));
        mmsModel.setRetrTxt(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Inbox.RETRIEVE_TEXT)));
        mmsModel.setRetrTxtCs(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.RETRIEVE_TEXT_CHARSET)));
        mmsModel.setReadStatus(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.READ_STATUS)));
        mmsModel.setCtCls(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.CONTENT_CLASS)));
        mmsModel.setRespTxt(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Inbox.RESPONSE_TEXT)));
        mmsModel.setdTm(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.DELIVERY_TIME)));
        mmsModel.setdRpt(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.DELIVERY_REPORT)));
        mmsModel.setLocked(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.LOCKED)));
        mmsModel.setSeen(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Inbox.SEEN)));
        return mmsModel;
    }

    public static List<PartModel> queryPartList(Cursor cursor) {
        List<PartModel> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                PartModel partModel = new PartModel();
                partModel.setSeq(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Part._ID)));
                partModel.setCt(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Part.CONTENT_TYPE)));
                partModel.setName(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Part.NAME)));
                partModel.setChset(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Part.CHARSET)));
                partModel.setCd(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Part.CONTENT_DISPOSITION)));
                partModel.setFn(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Part.FILENAME)));
                partModel.setCid(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Part.CONTENT_ID)));
                partModel.setCl(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Part.CONTENT_LOCATION)));
                partModel.setCttS(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Part.CT_START)));
                partModel.setCttT(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Part.CT_TYPE)));
                partModel.setData(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Part._DATA)));
                partModel.setText(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Part.TEXT)));
                list.add(partModel);
//                partModel.setAddress(cursor.getString(cursor.getColumnIndex(Telephony.Mms.Addr.ADDRESS)));
//                partModel.setAddressType(cursor.getInt(cursor.getColumnIndex(Telephony.Mms.Addr.TYPE)));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public static ThreadModel queryThreadModel(Cursor threadCursor) {
        ThreadModel threadModel = new ThreadModel();
        threadModel.setDate(threadCursor.getLong(threadCursor.getColumnIndex(Telephony.Threads.DATE)));
        threadModel.setMessageCount(threadCursor.getInt(threadCursor.getColumnIndex(Telephony.Threads.MESSAGE_COUNT)));
        threadModel.setSnippet(threadCursor.getString(threadCursor.getColumnIndex(Telephony.Threads.SNIPPET)));
        threadModel.setSnippetCharset(threadCursor.getInt(threadCursor.getColumnIndex(Telephony.Threads.SNIPPET_CHARSET)));
        threadModel.setRead(threadCursor.getInt(threadCursor.getColumnIndex(Telephony.Threads.READ)));
        threadModel.setType(threadCursor.getInt(threadCursor.getColumnIndex(Telephony.Threads.TYPE)));
        return threadModel;
    }
}
