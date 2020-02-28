package com.onyx.android.sdk.wifi;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Build;
import android.text.TextUtils;

import com.onyx.android.sdk.R;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.utils.CompatibilityUtil;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.ReflectUtil;
import com.onyx.android.sdk.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.onyx.android.sdk.wifi.WifiAdmin.SECURITY_PSK;
import static com.onyx.android.sdk.wifi.WifiAdmin.SECURITY_WEP;


/**
 * Created by solskjaer49 on 2016/12/3 12:16.
 */

public class WifiUtil {
    public static final String ACTION_WIFI_ENABLE = "android.intent.action.WIFI_ENABLE";

    private static final String SSID_REGEX = "^\"(.*)\"$";
    private static final String SSID_REPLACEMENT = "$1";

    private static Method SET_CACERTIFICATE_ALIAS, SET_CLIENTCERTIFICATE_ALIAS, SET_CACERTIFICATE_PATH;

    private static final String SYSTEM_CA_STORE_PATH = "/system/etc/security/cacerts";

    public static final String USE_SYSTEM_CA = "use_system_ca";
    public static final String DO_NOT_VALIDATE_CA = "do_not_validate_ca";

    public static
    @WifiBand.WifiBandDef
    int convertFrequencyToBand(int freq) {
        if (freq >= 2412 && freq <= 2484) {
            return WifiBand.B_G_N_NETWORK;
        } else if (freq >= 5170 && freq <= 5825) {
            return WifiBand.A_H_J_N_AC_NETWORK;
        } else {
            return WifiBand.UNKNOWN;
        }
    }

    public static String getBandString(Context context, int frequnecy) {
        switch (convertFrequencyToBand(frequnecy)) {
            case WifiBand.B_G_N_NETWORK:
                return context.getString(R.string.bgn_net_work);
            case WifiBand.A_H_J_N_AC_NETWORK:
                return context.getString(R.string.ac_network);
            case WifiBand.UNKNOWN:
            default:
                return context.getString(R.string.unknown_network);
        }
    }

    public static boolean isSameSSID(String ssid1, String ssid2) {
        return (ssid1.replaceAll(SSID_REGEX, SSID_REPLACEMENT))
                .equals((ssid2.replaceAll(SSID_REGEX, SSID_REPLACEMENT)));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static void initEnterpriseConfigReflectMethod() {
        if (SET_CACERTIFICATE_ALIAS != null && SET_CLIENTCERTIFICATE_ALIAS != null) {
            return;
        }
        SET_CACERTIFICATE_ALIAS = ReflectUtil.
                getMethodSafely(WifiEnterpriseConfig.class,
                        "setCaCertificateAlias", String.class);
        SET_CLIENTCERTIFICATE_ALIAS = ReflectUtil.getMethodSafely(WifiEnterpriseConfig.class,
                "setClientCertificateAlias", String.class);
        SET_CACERTIFICATE_PATH = ReflectUtil.getMethodSafely(WifiEnterpriseConfig.class,
                "setCaPath", String.class);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static WifiConfiguration buildWifiEAPConfig(String ssid, int eapMethod, int phase2Type, String eapCaCert,
                                                       String eapUserCert, String identity, String anonymous, String passWord) {
        initEnterpriseConfigReflectMethod();
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
        config.enterpriseConfig = new WifiEnterpriseConfig();
        config.enterpriseConfig.setEapMethod(eapMethod);
        config.enterpriseConfig.setPhase2Method(phase2Type);

        ReflectUtil.invokeMethodSafely(SET_CACERTIFICATE_ALIAS, config.enterpriseConfig, (String) null);
        ReflectUtil.invokeMethodSafely(SET_CACERTIFICATE_PATH, config.enterpriseConfig, (String) null);

        if (TextUtils.isEmpty(eapCaCert) || eapCaCert.equalsIgnoreCase(DO_NOT_VALIDATE_CA)) {
            // ca_cert already set to null, so do nothing.
        } else if (eapCaCert.equalsIgnoreCase(USE_SYSTEM_CA)) {
            ReflectUtil.invokeMethodSafely(SET_CACERTIFICATE_PATH, config.enterpriseConfig, SYSTEM_CA_STORE_PATH);
        } else {
            ReflectUtil.invokeMethodSafely(SET_CACERTIFICATE_ALIAS, config.enterpriseConfig, eapCaCert);
        }

        ReflectUtil.invokeMethodSafely(SET_CLIENTCERTIFICATE_ALIAS, config.enterpriseConfig, eapUserCert);

        config.enterpriseConfig.setIdentity(identity);
        config.enterpriseConfig.setAnonymousIdentity(anonymous);
        config.enterpriseConfig.setPassword(passWord);
        return config;
    }

    private static List<String> loadCertificates(WifiCertType certType, Collection<String> defaultValues) {
        List<String> certificateList;
        String[] certs = null;
        switch (certType) {
            case CA_CERT:
                certs = Device.currentDevice().loadCACertificate();
                break;
            case USER_CERT:
                certs = Device.currentDevice().loadUserCertificate();
                break;
        }
        if (certs == null || certs.length == 0) {
            certificateList = new ArrayList<>(defaultValues);
        } else {
            certificateList = Arrays.asList(certs);
            certificateList.addAll(defaultValues);
        }

        return certificateList;
    }

    public static List<String> loadCACertificates(String defaultValue) {
        return loadCertificates(WifiCertType.CA_CERT, Collections.singleton(defaultValue));
    }

    public static List<String> loadCACertificates(Collection<String> defaultValues) {
        return loadCertificates(WifiCertType.CA_CERT, defaultValues);
    }

    public static List<String> loadUserCertificates(String defaultValue) {
        return loadCertificates(WifiCertType.USER_CERT, Collections.singleton(defaultValue));
    }

    public static boolean isValidPassword(int securityMode, String password) {
        if (StringUtils.isNullOrEmpty(password)) {
            return false;
        }
        switch (securityMode) {
            case SECURITY_WEP:
                return password.length() >= 5;
            case SECURITY_PSK:
                return password.length() >= 8;
            default:
                return password.length() >= 5;
        }
    }

    public static void reevaluateNetwork(Context context) {
        if (!CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.M)) {
            return;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network;
        if (connectivityManager != null) {
            network = connectivityManager.getActiveNetwork();
            if (network != null) {
                connectivityManager.reportNetworkConnectivity(network, true);
            } else {
                Debug.i("No Active Network, abandon network reevaluate");
            }
        }
    }
}
