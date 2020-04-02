package com.android.dialer.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.contacts.common.util.PermissionsUtil;
import com.android.dialer.R;

public class QtiCallUtils {
    public static final String LOG_TAG = "QtiCallUtils";
    public static final String ACTION_LAUNCH_CONF_URI_DIALER = "org.codeaurora.confuridialer.ACTION_LAUNCH_CONF_URI_DIALER";
    public static final String ACTION_LAUNCH_CONF_DIALER = "org.codeaurora.confdialer.ACTION_LAUNCH_CONF_DIALER";
    public static final String CONFERNECE_NUMBER_KEY = "confernece_number_key";
    public static final int MAX_IMS_PHONE_COUNT = 2;

    public static void openConferenceUriDialerOr4gConferenceDialer(Context context) {
        if (!PermissionsUtil.hasPhonePermissions(context)) {
            return;
        }
        boolean shallOpenOperator4gDialer = false;
        int registeredImsPhoneCount = 0;
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final int phoneCount = telephonyManager.getPhoneCount();
        for (int i = 0; i < phoneCount; i++) {
            final boolean isImsConnected = isImsRegistered(context, i);
            if (isImsConnected) {
                registeredImsPhoneCount++;
                if (!shallOpenOperator4gDialer) {
                    shallOpenOperator4gDialer = true;
                } else {
                    registeredImsPhoneCount--;
                }
            }
        }
        if ((registeredImsPhoneCount < MAX_IMS_PHONE_COUNT) && shallOpenOperator4gDialer) {
            context.startActivity(getConferenceDialerIntent(null));
        } else if (shallOpenOperator4gDialer && (registeredImsPhoneCount > 1)) {
            openUserSelected4GDialer(context);
        } else {
            context.startActivity(getConferenceDialerIntent());
        }
    }

    public static boolean isImsRegistered(Context context, int phoneId) {
        try {
            final int[] subIds = ClazzUtils.getSubId(phoneId);
            int subId = SubscriptionManager.INVALID_SUBSCRIPTION_ID;
            if (subIds != null && subIds.length >= 1) {
                subId = subIds[0];
            }
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return ClazzUtils.isImsRegistered(telephonyManager, subId);
        } catch (Exception e) {
            Log.e(LOG_TAG, "QtiImsException = " + e);
            return false;
        }
    }

    public static Intent getConferenceDialerIntent() {
        Intent intent = new Intent(ACTION_LAUNCH_CONF_URI_DIALER);
        return intent;
    }

    public static Intent getConferenceDialerIntent(String number) {
        Intent intent = new Intent(ACTION_LAUNCH_CONF_DIALER);
        intent.putExtra(CONFERNECE_NUMBER_KEY, number);
        return intent;
    }

    public static void openUserSelected4GDialer(final Context context) {
        Resources resources = context.getResources();
        CharSequence options[] = new CharSequence[]{
                resources.getString(R.string.conference_uri_dialer_option),
                resources.getString(R.string.conference_4g_dialer_option)};
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogNoBackground);
        builder.setTitle(R.string.select_your_option);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //The user clicked on options[which]
                Log.d(LOG_TAG, "onClick : which option = " + which);
                if (which == 1) {
                    //Launch 4G conference dialer.
                    context.startActivity(getConferenceDialerIntent(null));
                } else {
                    //Launch conference URI dialer:
                    context.startActivity(getConferenceDialerIntent());
                }
            }
        });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //The user clicked on Cancel
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
