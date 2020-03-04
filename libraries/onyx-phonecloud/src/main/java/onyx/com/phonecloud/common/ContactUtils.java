package onyx.com.phonecloud.common;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;

import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import onyx.com.phonecloud.model.Address;
import onyx.com.phonecloud.model.CallLogInfo;
import onyx.com.phonecloud.model.ContactInfo;
import onyx.com.phonecloud.model.ContentValue;
import onyx.com.phonecloud.model.Email;
import onyx.com.phonecloud.model.ImInfo;
import onyx.com.phonecloud.model.PhoneNumber;
import onyx.com.phonecloud.model.Website;

import static android.provider.ContactsContract.Contacts.Entity.RAW_CONTACT_ID;

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
                int type = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.Phone.TYPE));
                String data = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER));
                list.add(new PhoneNumber(type, data));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public static List<Email> queryEmails(Cursor cursor) {
        List<Email> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int type = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.Email.TYPE));
                String data = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Email.ADDRESS));
                list.add(new Email(type, data));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public static List<Address> queryAddress(Cursor cursor) {
        List<Address> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int type = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.StructuredPostal.TYPE));
                String data = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
                list.add(new Address(type, data));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public static List<Website> queryWebsite(Cursor cursor) {
        List<Website> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String data = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Website.URL));
                int type = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.Website.TYPE));
                list.add(new Website(type, data));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public static List<ImInfo> queryImInfoList(Cursor cursor) {
        List<ImInfo> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int type = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.Im.TYPE));
                String data = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Im.DATA));
                list.add(new ImInfo(type, data));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public static List<CallLogInfo> queryCallLogs(Cursor cursor) {
        List<CallLogInfo> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                CallLogInfo callLog = new CallLogInfo();
                int id = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
                callLog.setId(id);
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                callLog.setNumber(number);
                int presentation = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.NUMBER_PRESENTATION));
                callLog.setPresentation(presentation);
                long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                callLog.setDate(date);
                long duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
                callLog.setDuration(duration);
                int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                callLog.setType(type);
                String subscription_component_name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_COMPONENT_NAME));
                callLog.setSubscriptionComponentName(subscription_component_name);
                long subscription_id = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
                callLog.setSubscriptionId(subscription_id);
                int isNew = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.NEW));
                callLog.setUnused(isNew);
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                callLog.setName(name);
                String numbertype = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE));
                callLog.setNumbertype(numbertype);
                String numberlaber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_LABEL));
                callLog.setNumberlaber(numberlaber);
                String countryiso = cursor.getString(cursor.getColumnIndex(CallLog.Calls.COUNTRY_ISO));
                callLog.setCountryiso(countryiso);
                String voicemail_uri = cursor.getString(cursor.getColumnIndex(CallLog.Calls.VOICEMAIL_URI));
                callLog.setVoicemailUri(voicemail_uri);
                String is_read = cursor.getString(cursor.getColumnIndex(CallLog.Calls.IS_READ));
                callLog.setIsRead(is_read);
                String geocoded_location = cursor.getString(cursor.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION));
                callLog.setGeocodedLocation(geocoded_location);
                String lookup_uri = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_LOOKUP_URI));
                callLog.setLookupUri(lookup_uri);
                String matched_number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_MATCHED_NUMBER));
                callLog.setMatchedNumber(matched_number);
                String normalized_number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NORMALIZED_NUMBER));
                callLog.setNormalizedNumber(normalized_number);
                int photo_id = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_ID));
                callLog.setPhotoId(photo_id);
                String photo_uri = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI));
                callLog.setPhotoUri(photo_uri);
                String formatted_number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_FORMATTED_NUMBER));
                callLog.setFormattedNumber(formatted_number);
                long last_modified = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.LAST_MODIFIED));
                callLog.setLastModified(last_modified);
                list.add(callLog);
            } while (cursor.moveToNext());
        }
        return list;
    }

    @SuppressLint("MissingPermission")
    public static boolean insetCallLog(ContentResolver resolver, List<CallLogInfo> infos) {
        if (CollectionUtils.isNullOrEmpty(infos)) {
            return false;
        }
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        for (CallLogInfo callLogInfo : infos) {
            ContentValues values = new ContentValues();
            values.clear();
            values.put(CallLog.Calls._ID, callLogInfo.getId());
            values.put(CallLog.Calls.NUMBER, callLogInfo.getNumber());
            values.put(CallLog.Calls.NUMBER_PRESENTATION, callLogInfo.getPresentation());
            values.put(CallLog.Calls.TYPE, callLogInfo.getType());
            values.put(CallLog.Calls.DATE, callLogInfo.getDate());
            values.put(CallLog.Calls.DATE, callLogInfo.getDate());
            values.put(CallLog.Calls.DURATION, callLogInfo.getDuration());
            values.put(CallLog.Calls.TYPE, callLogInfo.getType());
            values.put(CallLog.Calls.PHONE_ACCOUNT_COMPONENT_NAME, callLogInfo.getSubscriptionComponentName());
            values.put(CallLog.Calls.PHONE_ACCOUNT_ID, callLogInfo.getSubscriptionId());
            values.put(CallLog.Calls.NEW, callLogInfo.getUnused());
            values.put(CallLog.Calls.CACHED_NAME, callLogInfo.getName());
            values.put(CallLog.Calls.CACHED_NUMBER_TYPE, callLogInfo.getNumbertype());
            operations.add(ContentProviderOperation.
                    newInsert(CallLog.Calls.CONTENT_URI)
                    .withValues(values).withYieldAllowed(true).build());
        }
        try {
            resolver.applyBatch(CallLog.AUTHORITY, operations);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean insetContactInfo(ContentResolver resolver, List<ContactInfo> infos) {
        if (CollectionUtils.isNullOrEmpty(infos)) {
            return false;
        }
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex;
        for (ContactInfo info : infos) {
            rawContactInsertIndex = operations.size();
            ContentValues values = new ContentValues();
            operations.add(ContentProviderOperation.
                    newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValues(values).withYieldAllowed(true).build());
            putContactData(operations,
                    CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                    rawContactInsertIndex,
                    new ContentValue(CommonDataKinds.StructuredName.DISPLAY_NAME, info.getContactName())
            );
            putContactData(operations,
                    CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
                    rawContactInsertIndex,
                    new ContentValue(CommonDataKinds.Organization.COMPANY, info.getCompany()),
                    new ContentValue(CommonDataKinds.Organization.TITLE, info.getWorkName())
            );
            putContactData(operations,
                    CommonDataKinds.Note.CONTENT_ITEM_TYPE,
                    rawContactInsertIndex,
                    new ContentValue(CommonDataKinds.Note.NOTE, info.getNote())
            );
            for (PhoneNumber phoneNumber : info.getNumbers()) {
                putContactData(operations,
                        CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                        rawContactInsertIndex,
                        new ContentValue(CommonDataKinds.Phone.DATA, phoneNumber.getData()),
                        new ContentValue(CommonDataKinds.Phone.TYPE, phoneNumber.getType())
                );
            }

            for (Email email : info.getEmails()) {
                putContactData(operations,
                        CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                        rawContactInsertIndex,
                        new ContentValue(CommonDataKinds.Email.DATA, email.getData()),
                        new ContentValue(CommonDataKinds.Email.TYPE, email.getType())
                );
            }

            for (Address address : info.getAddresses()) {
                putContactData(operations,
                        CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE,
                        rawContactInsertIndex,
                        new ContentValue(CommonDataKinds.StructuredPostal.DATA, address.getData()),
                        new ContentValue(CommonDataKinds.StructuredPostal.TYPE, address.getType())
                );
            }

            for (Website webInfo : info.getWebList()) {
                putContactData(operations,
                        CommonDataKinds.Website.CONTENT_ITEM_TYPE,
                        rawContactInsertIndex,
                        new ContentValue(CommonDataKinds.Website.URL, webInfo.getData()),
                        new ContentValue(CommonDataKinds.Website.TYPE, webInfo.getType())
                );
            }

            for (ImInfo imInfo : info.getImInfoList()) {
                putContactData(operations,
                        CommonDataKinds.Im.CONTENT_ITEM_TYPE,
                        rawContactInsertIndex,
                        new ContentValue(CommonDataKinds.Im.DATA, imInfo.getData()),
                        new ContentValue(CommonDataKinds.Im.TYPE, imInfo.getType())
                );
            }

            putContactData(operations,
                    CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE,
                    rawContactInsertIndex,
                    new ContentValue(CommonDataKinds.SipAddress.DATA, info.getSipAddress())
            );
        }
        try {
            resolver.applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void putContactData(List<ContentProviderOperation> operations, String mimeType, int rawContactInsertIndex, ContentValue... params) {
        ContentValues values = new ContentValues();
        values.put("mimetype", mimeType);
        for (ContentValue param : params) {
            if (!StringUtils.isNullOrEmpty(param.getValue())) {
                values.put(param.getKey(), param.getValue());
            } else if (param.getIntValue() != -1) {
                values.put(param.getKey(), param.getIntValue());
            } else if (param.getLongValue() != -1) {
                values.put(param.getKey(), param.getLongValue());
            }
        }
        operations.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValues(values).withYieldAllowed(true).build());
    }
}
