package com.onyx.android.sdk.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.onyx.android.sdk.R;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.RxManager;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.CompatibilityUtil;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.ReflectUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.wifi.common.BaseSecurity;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.onyx.android.sdk.wifi.PskType.WPA;
import static com.onyx.android.sdk.wifi.PskType.WPA2;
import static com.onyx.android.sdk.wifi.PskType.WPA_WPA2;


/**
 * Created by solskjaer49 on 2016/12/1 16:27.
 */

public class WifiAdmin {
    private static final String TAG = WifiAdmin.class.getSimpleName();

    private class RxBuildResultListRequest extends RxRequest {

        private List<AccessPoint> resultPoints = new ArrayList<>();

        private RxBuildResultListRequest() {
        }

        public List<AccessPoint> getResultPoints() {
            return resultPoints;
        }

        @Override
        public void execute() throws Exception {
            List<AccessPoint> list = buildResultList(wifiManager.getScanResults());
            if (CollectionUtils.isNonBlank(list)) {
                resultPoints.addAll(list);
            }
        }
    }

    private WifiManager wifiManager;
    private List<ScanResult> wifiScanResultList;
    private Context context;
    private IntentFilter wifiStateFilter;
    private Callback callback;
    private BroadcastReceiver wifiStateReceiver;

    private RxManager rxManager;

    /**
     * These values are matched in string arrays -- changes must be kept in sync
     */
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;

    private static final String CONFIGURED_NETWORKS_CHANGED_ACTION = "android.net.wifi.CONFIGURED_NETWORKS_CHANGE";
    private static final String LINK_CONFIGURATION_CHANGED_ACTION = "android.net.wifi.LINK_CONFIGURATION_CHANGED";


    private static final int[] STATE_SECURED = {
            R.attr.state_encrypted
    };

    private static final int[] STATE_NONE = {};

    public WifiAdmin setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public interface Callback {
        void onWifiStateChange(boolean isWifiEnable,int wifiExtraState);

        void onScanResultReady(List<AccessPoint> scanResult);

        void onSupplicantStateChanged(NetworkInfo.DetailedState state);

        void onNetworkConnectionChange(NetworkInfo.DetailedState state);

        void onLinkConfigurationChange(List<AccessPoint> scanResult);

        void onConfiguredNetworksChange(List<AccessPoint> scanResult);
    }

    public static class DefaultCallback implements Callback{

        @Override
        public void onWifiStateChange(boolean isWifiEnable, int wifiExtraState) {

        }

        @Override
        public void onScanResultReady(List<AccessPoint> scanResult) {

        }

        @Override
        public void onSupplicantStateChanged(NetworkInfo.DetailedState state) {

        }

        @Override
        public void onNetworkConnectionChange(NetworkInfo.DetailedState state) {

        }

        @Override
        public void onLinkConfigurationChange(List<AccessPoint> scanResult) {

        }

        @Override
        public void onConfiguredNetworksChange(List<AccessPoint> scanResult) {

        }
    }

    public WifiAdmin(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        initWifiStateFilterAndReceiver();
    }

    public WifiAdmin(Context context, Callback callback) {
        this(context);
        setCallback(callback);
    }

    public RxManager getRxManager() {
        if (rxManager == null) {
            rxManager = RxManager.Builder.sharedSingleThreadManager();
        }
        return rxManager;
    }

    private void initWifiStateFilterAndReceiver() {
        wifiStateFilter = new IntentFilter();
        wifiStateFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifiStateFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiStateFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        wifiStateFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiStateFilter.addAction(CONFIGURED_NETWORKS_CHANGED_ACTION);
        wifiStateFilter.addAction(LINK_CONFIGURATION_CHANGED_ACTION);
        wifiStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case WifiManager.WIFI_STATE_CHANGED_ACTION:
                        int wifiExtraState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_UNKNOWN);
                        callback.onWifiStateChange(wifiManager.isWifiEnabled(), wifiExtraState);
                        if (wifiManager.isWifiEnabled()) {
                            triggerWifiScan();
                        }
                        break;
                    case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                        buildResultListAsync(new RxCallback<List<AccessPoint>>() {
                            @Override
                            public void onNext(@NonNull List<AccessPoint> accessPoints) {
                                callback.onScanResultReady(accessPoints);
                            }
                        });
                        break;
                    case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                        NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf((SupplicantState)
                                intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
                        callback.onSupplicantStateChanged(state);
                        break;
                    case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                        callback.onNetworkConnectionChange(info.getDetailedState());
                        break;
                    case LINK_CONFIGURATION_CHANGED_ACTION:
                        buildResultListAsync(new RxCallback<List<AccessPoint>>() {
                            @Override
                            public void onNext(@NonNull List<AccessPoint> accessPoints) {
                                callback.onLinkConfigurationChange(accessPoints);
                            }
                        });
                        break;
                    case CONFIGURED_NETWORKS_CHANGED_ACTION:
                        buildResultListAsync(new RxCallback<List<AccessPoint>>() {
                            @Override
                            public void onNext(@NonNull List<AccessPoint> accessPoints) {
                                callback.onConfiguredNetworksChange(accessPoints);
                            }
                        });
                        break;
                }
            }
        };
    }

    private void buildResultListAsync(final RxCallback<List<AccessPoint>> callback) {
        final RxBuildResultListRequest request = new RxBuildResultListRequest();
        getRxManager().enqueue(request, new RxCallback<RxRequest>() {
            @Override
            public void onNext(@NonNull RxRequest rxRequest) {
                callback.onNext(request.getResultPoints());
            }
        });
    }

    private List<AccessPoint> buildResultList(Collection<ScanResult> scanResults) {
        HashMap<String, AccessPoint> resultMap = new HashMap<>();
        AccessPoint connectedPoint = null;
        for (ScanResult item : scanResults) {
            if (StringUtils.isNullOrEmpty(item.SSID)) {
                continue;
            }
            AccessPoint point = new AccessPoint(item, this);
            if (point.getWifiConfiguration() != null) {
                if (Debug.getDebug()) {
                    Log.e(TAG, point.getWifiConfiguration().SSID + "(networkID):" + point.getWifiConfiguration().networkId);
                }
            }
            if (isAccessPointCurrentConnected(point)) {
                updateIfConnected(point);
                connectedPoint = point;
            }
            if (checkAccessPointLegality(point, resultMap)) {
                resultMap.put(point.getScanResult().SSID, point);
            }
        }
        List<AccessPoint> resultList = new LinkedList<>(resultMap.values());
        Collections.sort(resultList, new Comparator<AccessPoint>() {
            @Override
            public int compare(AccessPoint a1, AccessPoint a2) {
                return a2.getSignalLevel() - a1.getSignalLevel();
            }
        });
        if (connectedPoint != null) {
            resultList.remove(connectedPoint);
            resultList.add(0, connectedPoint);
        }
        return resultList;
    }

    private boolean isAccessPointCurrentConnected(AccessPoint accessPoint) {
        WifiInfo wifiInfo = getCurrentConnectionInfo();
        if (wifiInfo != null && accessPoint.getWifiConfiguration() != null) {
            return accessPoint.getWifiConfiguration().networkId == wifiInfo.getNetworkId()
                    && wifiInfo.getBSSID().equals(accessPoint.getScanResult().BSSID);
        }
        return false;
    }

    private void updateIfConnected(AccessPoint point) {
        point.updateWifiInfo();
        //TODO:ipv6 address?
        if (getCurrentConnectionInfo().getIpAddress() != 0) {
            point.setDetailedState(NetworkInfo.DetailedState.CONNECTED);
            point.setSecurityString(context.getString(R.string.wifi_connected));
        }
    }

    /**
     *  sync from Android Settings AccessPoint Class Logic.
     *  Logic:
     *  1. always add connected point.(we trusted system auto connect logic, assumed system always find strongest signal ssid to connected.)
     *  2. if not connected point SSID, check security,if same,pass.
     *  3. if security not same,just check the signal level,show the strongest one.
     */
    private boolean checkAccessPointLegality(AccessPoint point, HashMap<String, AccessPoint> map) {
        if (map.containsKey(point.getScanResult().SSID)) {
            AccessPoint originalPoint = map.get(point.getScanResult().SSID);
            if (originalPoint == null) {
                return true;
            }
            if (isAccessPointCurrentConnected(originalPoint)) {
                return false;
            }
            if (isAccessPointCurrentConnected(point)) {
                return true;
            }
            return originalPoint.getSecurity() != point.getSecurity() && originalPoint.getSignalLevel() < point.getSignalLevel();
        }
        return true;
    }

    public boolean registerReceiver() {
        if (context == null) {
            return false;
        }
        context.registerReceiver(wifiStateReceiver, wifiStateFilter);
        return true;
    }

    public boolean unregisterReceiver() {
        if (context == null) {
            return false;
        }
        context.unregisterReceiver(wifiStateReceiver);
        return true;
    }

    public void toggleWifi() {
        if (wifiManager != null) {
            wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
        }
    }

    public boolean isWifiEnabled() {
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    public int getWifiState() {
        return wifiManager == null ? WifiManager.WIFI_STATE_UNKNOWN : wifiManager.getWifiState();
    }

    public void setWifiEnabled(boolean isWifiEnabled){
        if (wifiManager != null) {
            wifiManager.setWifiEnabled(isWifiEnabled);
        }
    }

    public void triggerWifiScan() {
        if (wifiManager != null) {
            wifiManager.startScan();
        }
    }

    public WifiInfo getCurrentConnectionInfo() {
        return wifiManager.getConnectionInfo();
    }

    public int checkWifiState() {
        return wifiManager.getWifiState();
    }

    @Nullable
    public WifiConfiguration getWifiConfiguration(ScanResult result) {
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        if (CollectionUtils.isNullOrEmpty(configurations)) {
            return null;
        }
        for (WifiConfiguration configuration : configurations) {
            if (WifiUtil.isSameSSID(configuration.SSID, result.SSID)) {
                return configuration;
            }
        }
        return null;
    }

    public int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

    public String getSecurityString(AccessPoint accessPoint) {
        BaseSecurity securityString = new BaseSecurity();
        return securityString.getSecurityString(accessPoint);
    }

    public String getSecurityMode(ScanResult result, boolean concise, int security) {
        switch (security) {
            case SECURITY_EAP:
                return concise ? context.getString(R.string.wifi_security_short_eap) :
                        context.getString(R.string.wifi_security_eap);
            case SECURITY_PSK:
                switch (getPskType(result)) {
                    case WPA:
                        return concise ? context.getString(R.string.wifi_security_short_wpa) :
                                context.getString(R.string.wifi_security_wpa);
                    case WPA2:
                        return concise ? context.getString(R.string.wifi_security_short_wpa2) :
                                context.getString(R.string.wifi_security_wpa2);
                    case WPA_WPA2:
                        return concise ? context.getString(R.string.wifi_security_short_wpa_wpa2) :
                                context.getString(R.string.wifi_security_wpa_wpa2);
                    case PskType.UNKNOWN:
                    default:
                        return concise ? context.getString(R.string.wifi_security_short_psk_generic)
                                : context.getString(R.string.wifi_security_psk_generic);
                }
            case SECURITY_WEP:
                return concise ? context.getString(R.string.wifi_security_short_wep) :
                        context.getString(R.string.wifi_security_wep);
            case SECURITY_NONE:
            default:
                return concise ? "" : context.getString(R.string.wifi_security_none);
        }
    }

    private
    @PskType.PskTypeDef
    int getPskType(ScanResult result) {
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("WPA2-PSK");
        if (wpa2 && wpa) {
            return WPA_WPA2;
        } else if (wpa2) {
            return WPA2;
        } else if (wpa) {
            return WPA;
        } else {
            Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
            return PskType.UNKNOWN;
        }
    }

    public WifiInfo getWifiInfo(ScanResult result) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return null;
        }
        String ssid = wifiInfo.getSSID();
        if (StringUtils.isNullOrEmpty(ssid)) {
            return null;
        }

        /*
          After API 17,getSSID() will return ssid with quotation mark.
          ref link:https://developer.android.com/reference/android/net/wifi/WifiInfo.html#getSSID()
        */
        if (WifiUtil.isSameSSID(ssid, result.SSID)) {
            return wifiInfo;
        }
        return null;
    }

    public int getWifiSignalLevel(ScanResult result) {
        return WifiManager.calculateSignalLevel(result.level, 4);
    }

    public int[] getWifiImageState(int security) {
        return (security != SECURITY_NONE) ? STATE_SECURED : STATE_NONE;
    }

    public void connectWifi(AccessPoint accessPoint) {
        WifiConfiguration configuration = accessPoint.getWifiConfiguration();
        int networkId;
        if (configuration != null) {
            networkId = configuration.networkId;
        } else {
            configuration = createWifiConfiguration(accessPoint);
            networkId = wifiManager.addNetwork(configuration);
        }
        boolean success = wifiManager.enableNetwork(networkId, true);
        if (success) {
            wifiManager.saveConfiguration();
        }
    }

    public WifiConfiguration createWifiConfiguration(AccessPoint accessPoint) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + accessPoint.getScanResult().SSID + "\"";
        String password = accessPoint.getPassword();
        switch (accessPoint.getSecurity()) {
            case SECURITY_NONE:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case SECURITY_WEP:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                int length = password.length();
                // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                if ((length == 10 || length == 26 || length == 58) &&
                        password.matches("[0-9A-Fa-f]*")) {
                    config.wepKeys[0] = password;
                } else {
                    config.wepKeys[0] = '"' + password + '"';
                }
                break;
            case SECURITY_PSK:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                if (password.matches("[0-9A-Fa-f]{64}")) {
                    config.preSharedKey = password;
                } else {
                    config.preSharedKey = '"' + password + '"';
                }
                break;
            case SECURITY_EAP:
                //nothing to do
                break;
            default:
                return null;
        }
        return config;
    }

    public String getSignalString(int signal) {
        return context.getResources().getStringArray(R.array.wifi_signal)[signal];
    }

    public boolean forget(AccessPoint accessPoint) {
        if (accessPoint == null || accessPoint.getWifiConfiguration() == null) {
            return false;
        }
        wifiManager.removeNetwork(accessPoint.getWifiConfiguration().networkId);
        return wifiManager.saveConfiguration();
    }

    public String getLocalIPAddress() throws SocketException {
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                    return inetAddress.getHostAddress();
                }
            }
        }
        return "";
    }

    public int getPosition(List<AccessPoint> wifiList, NetworkInfo.DetailedState state, WifiInfo connectionInfo) {
        if (wifiList == null) {
            return -1;
        }
        int position = -1;
        for (int i = 0; i < wifiList.size(); i++) {
            AccessPoint accessPoint = wifiList.get(i);
            WifiConfiguration config = accessPoint.getWifiConfiguration();
            if (config == null) {
                continue;
            }
            int networkId = config.networkId;
            if (connectionInfo != null && networkId != -1
                    && networkId == connectionInfo.getNetworkId()) {
                position = i;
                break;
            }
        }
        return position;
    }

    public int getDisableReason(WifiConfiguration config) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.O)) {
            return ReflectUtil.getStaticInnerClassDeclareIntFieldSafely(config.getClass(),
                    "NetworkSelectionStatus", "mNetworkSelectionDisableReason");
        } else {
            return ReflectUtil.getDeclareIntFieldSafely(config.getClass(), config, "disableReason");
        }
    }

    public WifiConfiguration createWifiConfiguration(String ssid, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        WifiConfiguration tempConfig = this.isExist(ssid);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }

        switch (type){
            case SECURITY_NONE:
                config.wepKeys[0] = "";
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case SECURITY_WEP:
                config.hiddenSSID = true;
                config.wepKeys[0] = "\"" + password + "\"";
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case SECURITY_PSK:
                config.preSharedKey = "\"" + password + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.status = WifiConfiguration.Status.ENABLED;
                break;
        }
        return config;
    }

    private WifiConfiguration isExist(String ssid) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if (existingConfigs == null) {
            return null;
        }
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = wifiManager.addNetwork(wcg);
        wifiManager.enableNetwork(wcgID, true);
    }

    public void addNetwork(ArrayList<WifiConfiguration> wcgList) {
        if (wcgList == null || wcgList.isEmpty()) {
            return;
        }
        for (WifiConfiguration config : wcgList) {
            int wcgID = wifiManager.addNetwork(config);
            wifiManager.enableNetwork(wcgID, true);
        }
    }

    public void addNetwork(String ssid, String password, int securityType) {
        addNetwork(createWifiConfiguration(ssid, password, securityType));
    }

}