package com.onyx.android.sdk.wifi.common;

import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Build;

import com.onyx.android.sdk.R;
import com.onyx.android.sdk.utils.CompatibilityUtil;
import com.onyx.android.sdk.utils.ResManager;
import com.onyx.android.sdk.wifi.AccessPoint;

/**
 * @author Kaiguang
 * @Description
 * @Time 2019/6/14
 */
public class BaseSecurity {
    /**
     * These values are matched in string arrays -- changes must be kept in sync
     */
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;

    public static final int DISABLED_UNKNOWN_REASON = 0;
    public static final int DISABLED_DNS_FAILURE = 1;
    public static final int DISABLED_DHCP_FAILURE = 2;
    public static final int DISABLED_AUTH_FAILURE = 3;
    public static final int DISABLED_ASSOCIATION_REJECT = 4;

    public String getSecurityString(AccessPoint accessPoint) {
        ScanResult result = accessPoint.getScanResult();
        NetworkInfo.DetailedState state = accessPoint.getDetailedState();
        int security = accessPoint.getSecurity();
        WifiConfiguration configuration = accessPoint.getWifiConfiguration();
        String securityMode = accessPoint.getSecurityMode();
        boolean wpsAvailable = security != SECURITY_EAP && result.capabilities.contains("WPS");
        StringBuilder summary = new StringBuilder();
        if (state != null) {
            return getDetailedState(state, result.SSID);
        } else if (configuration != null && configuration.status == WifiConfiguration.Status.DISABLED) {
            return disableReason(accessPoint);
        } else {
            // Is saved network
            if (configuration != null) {
                summary.append(ResManager.getString(R.string.wifi_remembered));
            }

            if (security != SECURITY_NONE) {
                String securityStrFormat;
                if (summary.length() == 0) {
                    securityStrFormat = ResManager.getString(R.string.wifi_secured_first_item);
                } else {
                    securityStrFormat = ResManager.getString(R.string.wifi_secured_second_item);
                }
                summary.append(String.format(securityStrFormat, securityMode));
            }

            // Only list WPS available for unsaved networks
            if (configuration == null && wpsAvailable) {
                if (summary.length() == 0) {
                    summary.append(ResManager.getString(R.string.wifi_wps_available_first_item));
                } else {
                    summary.append(ResManager.getString(R.string.wifi_wps_available_second_item));
                }
            }
        }
        return summary.toString();
    }

    private String getDetailedState(NetworkInfo.DetailedState state, String ssid) {
        String[] formats = ResManager.getStringArray((ssid == null) ? R.array.wifi_status : R.array.wifi_status_with_ssid);
        int index = state.ordinal();
        if (index >= formats.length || formats[index].length() == 0) {
            return null;
        }
        return String.format(formats[index], ssid);
    }

    protected String disableReason(AccessPoint accessPoint) {
        int disableReason = accessPoint.getDisableReason();
        switch (disableReason) {
            case DISABLED_AUTH_FAILURE:
                return ResManager.getString(R.string.wifi_disabled_password_failure);
            case DISABLED_DHCP_FAILURE:
            case DISABLED_DNS_FAILURE:
                return ResManager.getString(R.string.wifi_disabled_network_failure);
            case DISABLED_UNKNOWN_REASON:
            case DISABLED_ASSOCIATION_REJECT:
                return ResManager.getString(!CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.O_MR1)
                        || accessPoint.isConnected() ?
                        R.string.wifi_disabled_generic :
                        R.string.wifi_disabled_wrong_password);
        }
        return "";
    }
}
