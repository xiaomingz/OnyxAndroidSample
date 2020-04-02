package onyx.com.phonecloud.request;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;

import com.onyx.android.sdk.rx.RxRequest;

import java.util.ArrayList;
import java.util.List;

import onyx.com.phonecloud.common.ContactUtils;
import onyx.com.phonecloud.model.ContactInfo;

/**
 * Created by TonyXie on 2020-02-27
 */
public class LoadContactInfoRequest extends RxRequest {
    private List<ContactInfo> contacts = new ArrayList<>();

    @Override
    public void execute() throws Exception {
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = null;
        Cursor phoneCursor = null;
        Cursor emailCursor = null;
        Cursor addressCursor = null;
        Cursor orgCur = null;
        Cursor noteCur = null;
        Cursor webCur = null;
        Cursor imCur = null;
        Cursor sipCur = null;
        try {
            cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                ContactInfo contactBean = new ContactInfo();
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String contactName = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME));
                contactBean.setContactName(contactName);

                orgCur = ContactUtils.getCursorByMimeType(contentResolver, contactId, CommonDataKinds.Organization.CONTENT_ITEM_TYPE);
                if (orgCur.moveToFirst()) {
                    String company = orgCur.getString(orgCur.getColumnIndex(CommonDataKinds.Organization.COMPANY));
                    String workName = orgCur.getString(orgCur.getColumnIndex(CommonDataKinds.Organization.TITLE));
                    contactBean.setCompany(company);
                    contactBean.setWorkName(workName);
                }

                noteCur = ContactUtils.getCursorByMimeType(contentResolver, contactId, CommonDataKinds.Note.CONTENT_ITEM_TYPE);
                if (noteCur.moveToFirst()) {
                    String note = noteCur.getString(orgCur.getColumnIndex(CommonDataKinds.Note.NOTE));
                    contactBean.setNote(note);
                }

                phoneCursor = ContactUtils.getCursorByMimeType(contentResolver, contactId, CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                contactBean.setNumbers(ContactUtils.queryPhoneNumber(phoneCursor));

                emailCursor = ContactUtils.getCursorByMimeType(contentResolver, contactId, CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                contactBean.setEmails(ContactUtils.queryEmails(emailCursor));

                addressCursor = ContactUtils.getCursorByMimeType(contentResolver, contactId, CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
                contactBean.setAddresses(ContactUtils.queryAddress(addressCursor));

                webCur = ContactUtils.getCursorByMimeType(contentResolver, contactId, CommonDataKinds.Website.CONTENT_ITEM_TYPE);
                contactBean.setWebList(ContactUtils.queryWebsite(webCur));

                imCur = ContactUtils.getCursorByMimeType(contentResolver, contactId, CommonDataKinds.Im.CONTENT_ITEM_TYPE);
                contactBean.setImInfoList(ContactUtils.queryImInfoList(imCur));

                sipCur = ContactUtils.getCursorByMimeType(contentResolver, contactId, CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE);
                if (sipCur.moveToFirst()) {
                    String sipAddress = orgCur.getString(orgCur.getColumnIndex(CommonDataKinds.SipAddress.DATA));
                    contactBean.setSipAddress(sipAddress);
                }
                contacts.add(contactBean);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            if (phoneCursor != null) {
                phoneCursor.close();
                phoneCursor = null;
            }
            if (emailCursor != null) {
                emailCursor.close();
                emailCursor = null;
            }
            if (addressCursor != null) {
                addressCursor.close();
                addressCursor = null;
            }
            if (orgCur != null) {
                orgCur.close();
                orgCur = null;
            }
            if (noteCur != null) {
                noteCur.close();
                noteCur = null;
            }
            if (webCur != null) {
                webCur.close();
                webCur = null;
            }
            if (imCur != null) {
                imCur.close();
                imCur = null;
            }
            if (sipCur != null) {
                sipCur.close();
                sipCur = null;
            }
        }
    }

    public List<ContactInfo> getContacts() {
        return contacts;
    }
}
