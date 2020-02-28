package onyx.com.phonecloud.common;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import onyx.com.phonecloud.model.Address;
import onyx.com.phonecloud.model.Email;
import onyx.com.phonecloud.model.ImInfo;
import onyx.com.phonecloud.model.PhoneNumber;
import onyx.com.phonecloud.model.Website;

/**
 * Created by TonyXie on 2020-02-28
 */
public class ContactUtils {
    public static Cursor getCursorByMimeType(ContentResolver contentResolver, String contactId, String mimeType) {
        String mimeTypeWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] imWhereParams = new String[]{contactId, mimeType};
        return contentResolver.query(ContactsContract.Data.CONTENT_URI,
                null, mimeTypeWhere, imWhereParams, null);
    }

    public static List<PhoneNumber> queryPhoneNumber(Cursor cursor) {
        List<PhoneNumber> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String data = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                list.add(new PhoneNumber(type, data));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public static List<Email> queryEmails(Cursor cursor) {
        List<Email> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                String data = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                list.add(new Email(type, data));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public static List<Address> queryAddress(Cursor cursor) {
        List<Address> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                String data = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
                list.add(new Address(type, data));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public static List<Website> queryWebsite(Cursor cursor) {
        List<Website> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String data = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.TYPE));
                list.add(new Website(type, data));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public static List<ImInfo> queryImInfoList(Cursor cursor) {
        List<ImInfo> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
                String data = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
                list.add(new ImInfo(type, data));
            } while (cursor.moveToNext());
        }
        return list;
    }

}
