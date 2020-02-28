package com.onyx.android.sdk.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.RxManager;
import com.onyx.android.sdk.rx.RxRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onyx.android.sdk.utils.BroadcastHelper.FLOAT_BUTTON_STATUS;

/**
 * Created by zhuzeng on 9/9/14.

 01-06 11:01:31.500    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[0]: android.intent.action.MEDIA_UNMOUNTED data file:///mnt/sdcard
 01-06 11:01:31.510    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[1]: android.intent.action.MEDIA_UNMOUNTED data file:///mnt/sdcard
 01-06 11:01:32.350    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[2]: android.intent.action.MEDIA_UNMOUNTED data file:///mnt/sdcard
 01-06 11:01:32.400    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[3]: android.intent.action.MEDIA_UNMOUNTED data file:///mnt/sdcard
 01-06 11:01:32.420    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[4]: android.intent.action.MEDIA_SHARED data file:///mnt/sdcard

 01-06 11:01:46.910    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[5]: android.intent.action.MEDIA_UNMOUNTED data file:///mnt/sdcard
 01-06 11:01:47.030    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[6]: android.intent.action.MEDIA_UNMOUNTED data file:///mnt/sdcard
 01-06 11:01:47.230    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[7]: android.intent.action.MEDIA_MOUNTED data file:///mnt/sdcard
 01-06 11:01:48.000    8083-8083/com.onyx.content.browser I/DeviceReceiver﹕ Received[8]: android.intent.action.MEDIA_MOUNTED data file:///mnt/sdcard

 */

public class DeviceReceiver extends BroadcastReceiver {

    private static final String TAG = DeviceReceiver.class.getSimpleName();
    public static final String TRIGGER = "trigger_notification";
    public static final String PARSE_PUSH_NOTIFICATION = "com.onyx.parsePushNotification";

    public static final String NO_SAVED_NETWORK_CONNECTED_ACTION = "action.no.saved.network.connected";
    public static final String START_ONYX_SETTINGS = "start_onyx_settings";

    public static final String OPEN_DOCUMENT_ACTION = "com.onyx.open";
    public static final String MTP_EVENT_ACTION = "com.onyx.action.MTP_EVENT_ACTION";
    public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";

    public static final String SYSTEM_UI_DIALOG_OPEN_ACTION = "com.android.systemui.SYSTEM_UI_DIALOG_OPEN_ACTION";
    public static final String SYSTEM_UI_DIALOG_CLOSE_ACTION = "com.android.systemui.SYSTEM_UI_DIALOG_CLOSE_ACTION";
    public static final String DIALOG_TYPE = "dialog_type";
    public static final String DIALOG_TYPE_BRIGHTNESS = "dialog_type_brightness";
    public static final String DIALOG_TYPE_USB_STORAGE_CONNECTION = "dialog_type_usb_storage_connection";
    public static final String DIALOG_TYPE_NOTIFICATION_PANEL = "dialog_type_notification_panel";
    public static final String DIALOG_TYPE_VOLUME = "dialog_type_volume";
    public static final String DIALOG_TYPE_LOW_BATTERY_WARNING = "dialog_type_low_battery_Warning";
    public static final String DIALOG_TYPE_POWER_OFF = "dialog_type_power_off";
    public static final String DIALOG_TYPE_SYSTEM_ERROR = "dialog_type_system_error";

    public static final String STATUS_BAR_ICON_REFRESH_START_ACTION = "com.android.systemui.STATUS_BAR_ICON_REFRESH_START_ACTION";
    public static final String STATUS_BAR_ICON_REFRESH_FINISH_ACTION = "com.android.systemui.STATUS_BAR_ICON_REFRESH_FINISH_ACTION";
    public static final String STATUS_BAR_SHOW_ACTION = "com.android.systemui.STATUS_BAR_SHOW_ACTION";
    public static final String STATUS_BAR_HIDE_ACTION = "com.android.systemui.STATUS_BAR_HIDE_ACTION";

    public static final String ONYX_KEYBOARD_SHOW = "com.onyx.ime.show";
    public static final String ONYX_KEYBOARD_HIDE = "com.onyx.ime.hide";

    public static final String SYSTEM_WAKE_UP = "com.android.system.WAKE_UP";
    public static final String SYSTEM_HOME = "com.android.systemui.HOME_BUTTON_CLICK";

    public static final String SYSTEM_UI_SCREEN_SHOT_START_ACTION = "com.android.systemui.SYSTEM_UI_SCREEN_SHOT_START_ACTION";
    public static final String SYSTEM_UI_SCREEN_SHOT_END_ACTION = "com.android.systemui.SYSTEM_UI_SCREEN_SHOT_END_ACTION";

    public static final String ICON_TYPE = "icon_type";
    public static final String ICON_TYPE_CTP_STATUS_ICON = "icon_type_ctp_status";
    public static final String ICON_TYPE_A2 = "icon_type_a2";

    public static final String TOAST_SHOW_ACTION = "com.android.systemui.TOAST_SHOW_ACTION";
    public static final String TOAST_HIDE_ACTION = "com.android.systemui.TOAST_HIDE_ACTION";

    public static final String ENTER_RECENT_INTERFACE_ACTION = "action.enter.recent.interface";
    public static final String ENTER_KEYGUARD_INTERFACE_ACTION = "action.enter.keyguard.interface";

    public static final String ONYX_RESET_PIN_CODE_ACTION = "action.onyx.reset.pincode";
    public static final String ONYX_FINGERPRINT_REMOVED_ACTION = "onyx.android.intent.action.FINGERPRINT_REMOVED_ACTION";
    public static final String ONYX_PIN_CODE_REMOVED_ACTION = "onyx.android.intent.action.ONYX_PIN_CODE_REMOVED_ACTION";
    public static final String FINGERPRINT_ACTION_LOCKOUT_RESET = "com.android.server.fingerprint.ACTION_LOCKOUT_RESET";

    public static final String ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ACTION_USB_DEVICE_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    public static final String ACTION_VOLUME_STATE_CHANGED = "android.os.storage.action.VOLUME_STATE_CHANGED";
    public static final String EXTRA_VOLUME_STATE = "android.os.storage.extra.VOLUME_STATE";

    public static final String ACTION_SHOW_INPUT_METHOD_PICKER = "com.android.server.InputMethodManagerService.SHOW_INPUT_METHOD_PICKER";
    public static final String ONYX_WIFI_SETTINGS_ACTION = "onyx.settings.action.wifi";

    public static final String ONYX_NOTE_COVER_UPDATE_ACTION = "onyx.note.cover.update.action";
    public static final String ONYX_NOTE_SAVE_QUIT_ACTION = "onyx.note.save.quit.action";

    public static int count = 0;
    static private FileObserver observer;
    private Map<String, String> storageState = new HashMap<String, String>();

    private BootCompleteListener bootCompleteListener;
    private PushNotificationListener pushNotificationListener;
    private NetworkConnectChangedListener networkConnectChangedListener;
    private UmsStateListener umsStateListener;
    private MediaStateListener mediaStateListener;
    private FileSystemListener fileSystemListener;
    private SettingsListener settingsListener;
    private OpenDocumentListener openDocumentListener;
    private LocaleChangedListener localeChangedListener;
    private BluetoothStateListener bluetoothStateListener;
    private MtpEventListener mtpEventListener;
    private UsbStateListener usbStateListener;
    private SystemUIChangeListener systemUIChangeListener;
    private WiFiStateChangedListener wiFiStateChangedListener;
    private ResetPinCodeListener resetPinCodeListener;
    private SystemKeyguardChangedListener keyguardChangedListener;
    private UsbDeviceStateChangeListener usbDeviceStateChangeListener;
    private VolumeStateChangeListener volumeStateChangeListener;
    private PackageChangedListener packageChangedListener;

    private ExtraIntentReceiver extraIntentReceiver;
    private List<IntentFilter> extraIntentFilterList = new ArrayList<>();
    private boolean lastNetworkConnected;
    private List<String> noFocusSystemDialogTypeList = Arrays.asList(DIALOG_TYPE_VOLUME);

    private RxManager rxManager;

    private static class GetNetworkInfoRequest extends RxRequest {

        public NetworkInfo info;

        @Override
        public void execute() throws Exception {
            ConnectivityManager connectionManager =
                    (ConnectivityManager) getContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectionManager == null) {
                return;
            }
            info = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }
    }

    public RxManager getRxManager() {
        if (rxManager == null) {
            rxManager = RxManager.Builder.sharedSingleThreadManager();
        }
        return rxManager;
    }

    static public class ExtraIntentReceiver {

        @NonNull
        public List<IntentFilter> getIntentFilterList() {
            return new ArrayList<>();
        }

        public void onReceive(Context context, Intent intent) {
        }
    }

    static public class BootCompleteListener {
        public void onReceivedBootComplete(Intent intent){
        }
    }

    static public class PushNotificationListener {
        public void onReceivedPushNotification(Intent intent){

        }
    }

    static public class NetworkConnectChangedListener {

        public void onNetworkConnectChanged(boolean connected) {
        }

        public void onUnableConnectNetworkEvent() {
        }
    }

    static public class WiFiStateChangedListener {
        public void onWiFiStateChanged(Context context, @NonNull NetworkInfo info) {

        }
    }

    static public class BluetoothStateListener {
        public void onBluetoothStateChanged(Intent intent) {

        }
    }

    static public class UmsStateListener {
        public void onUmsStateChanged(Intent intent){
        }
    }

    static public class MediaStateListener {
        public void onMediaBadRemoval(final Intent intent) {
        }

        public void onMediaMounted(final Intent intent) {
        }

        public void onMediaUnmounted(final Intent intent) {
        }

        public void onMediaRemoved(final Intent intent) {
        }

        public void onMediaChecking(final Intent intent) {
        }

        public void onMediaShared(final Intent intent) {
        }

        public void onMediaScanStarted(final Intent intent) {
        }

        public void onMediaScanFinished(final Intent intent) {
        }
    }

    static public class MtpEventListener {
        public void onMtpEvent(Intent intent){
        }
    }

    static public class UsbStateListener {
        public void onUsbState(Intent intent, boolean connected){
        }
    }

    static public class VolumeStateChangeListener {
        public void onVolumeStateChange(int state) {}
    }

    static public class UsbDeviceStateChangeListener {
        public void onUsbDeviceStateChange(boolean attached) {}
    }

    static public class FileSystemListener {
        public void onFileRemoved(final String path) {
        }

        public void onFileUpdated(final String path) {
        }

        public void onFileAdded(final String path) {
        }

        public void onFileMoved(final String path){
        }
    }

    static public class SettingsListener {
        public void onSystemSettingsClicked(Intent intent) {
        }
    }

    static public class OpenDocumentListener {
        public void onOpenDocumentAction(Intent intent, final String path) {
        }
    }

    static public class LocaleChangedListener {
        public void onLocaleChanged() {
        }
    }

    static public class ResetPinCodeListener {
        public void onResetPinCode(Context context, Intent intent) {

        }
    }

    static public class SystemKeyguardChangedListener {
        public void onSystemKeyguardChanged(Context context, Intent intent) {

        }
    }

    public static abstract class SystemUIChangeListener {
        public void onSystemUIChanged(final String action, boolean open) {}
        public void onSystemIconChanged(String action, @NonNull String iconType, boolean open) {}
        public void onNoFocusSystemDialogChanged(boolean open) {}
        public void onHomeClicked() {}
        public void onLowBattery(Intent intent) {}
        public void onShutDown(Intent intent) {}
        public void onReboot() {}
        public void onScreenShot(Intent intent, final boolean end) {}
        public void onOnyxKeyboardChanged(boolean open) {}
        public void onFloatButtonChanged(boolean active) {}
        public void onToastChanged(boolean show) {}
        public void onEnterRecent() {}
        public void onLockScreenChanged(boolean open) {}
        public void onStatusBarChanged(boolean show) {}
    }

    public static abstract class PackageChangedListener {
        public void onPackageAdded(Intent intent) {}
        public void onPackageReplaced(Intent intent) {}
        public void onPackageRemoved(Intent intent) {}
        public void onPackageFullRemoved(Intent intent) {}
        public void onPackageChanged(Intent intent) {}
    }

    public void initReceiver(Context context) {
        enable(context, true);
    }

    public IntentFilter fileFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(PARSE_PUSH_NOTIFICATION);
        filter.addAction(Intent.ACTION_UMS_CONNECTED);
        filter.addAction(Intent.ACTION_UMS_DISCONNECTED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_SHARED);
        filter.addAction(MTP_EVENT_ACTION);
        filter.addDataScheme("file");

        return filter;
    }

    public IntentFilter systemFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(PARSE_PUSH_NOTIFICATION);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addAction(ACTION_USB_STATE);
        filter.addAction(MTP_EVENT_ACTION);
        filter.addAction(Intent.ACTION_REBOOT);
        filter.addAction(ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ACTION_VOLUME_STATE_CHANGED);
        return filter;
    }

    public IntentFilter settingsFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TRIGGER);
        filter.addAction(START_ONYX_SETTINGS);
        filter.addAction(ONYX_RESET_PIN_CODE_ACTION);
        filter.addAction(ONYX_FINGERPRINT_REMOVED_ACTION);
        filter.addAction(ONYX_PIN_CODE_REMOVED_ACTION);
        return filter;
    }

    public IntentFilter openDocumentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(OPEN_DOCUMENT_ACTION);
        return filter;
    }

    public IntentFilter systemUIFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SYSTEM_UI_DIALOG_OPEN_ACTION);
        filter.addAction(SYSTEM_UI_DIALOG_CLOSE_ACTION);
        filter.addAction(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Intent.ACTION_SCREEN_ON : SYSTEM_WAKE_UP);
        filter.addAction(SYSTEM_HOME);
        filter.addAction(STATUS_BAR_ICON_REFRESH_START_ACTION);
        filter.addAction(STATUS_BAR_ICON_REFRESH_FINISH_ACTION);
        filter.addAction(SYSTEM_UI_SCREEN_SHOT_START_ACTION);
        filter.addAction(SYSTEM_UI_SCREEN_SHOT_END_ACTION);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        filter.addAction(ONYX_KEYBOARD_SHOW);
        filter.addAction(ONYX_KEYBOARD_HIDE);
        filter.addAction(BroadcastHelper.FLOAT_BUTTON_TOUCHE_ACTION);
        filter.addAction(TOAST_SHOW_ACTION);
        filter.addAction(TOAST_HIDE_ACTION);
        filter.addAction(ENTER_RECENT_INTERFACE_ACTION);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(ENTER_KEYGUARD_INTERFACE_ACTION);
        filter.addAction(STATUS_BAR_SHOW_ACTION);
        filter.addAction(STATUS_BAR_HIDE_ACTION);
        return filter;
    }

    // should re-create the observer.
    // the observer is not recursive, so have to create new observer for each folder
    private void startFileObserver() {
        if (observer != null) {
            observer.stopWatching();
        }
        observer = new FileObserver("/mnt/sdcard/Books") {
            @Override
            public void onEvent(int i, String s) {
                Log.i(TAG, "Received file changed event: " + i + " "  + s);
            }

        };
        observer.startWatching();
        Log.i(TAG, "start file observer");
    }

    // it's necessary to install default callback, otherwise, apps will
    // not receive push notifcation.
    public void installPushCallback(Context context) {
    }

    public void enable(Context context, boolean enable) {
        try {
            updateNetworkConnectedState(context);
            if (enable) {
                BroadcastHelper.ensureRegisterReceiver(context, this, fileFilter());
                BroadcastHelper.ensureRegisterReceiver(context, this, settingsFilter());
                BroadcastHelper.ensureRegisterReceiver(context, this, systemFilter());
                BroadcastHelper.ensureRegisterReceiver(context, this, openDocumentFilter());
                BroadcastHelper.ensureRegisterReceiver(context, this, systemUIFilter());
                BroadcastHelper.ensureRegisterReceiver(context, this, netWorkFilter());
                BroadcastHelper.ensureRegisterReceiver(context, this, getPackageFilter());
                registerExtraIntent(context);
                installPushCallback(context);
            } else {
                BroadcastHelper.ensureUnregisterReceiver(context, this);
            }
        } catch (Exception e) {
        }
        storageState.clear();
    }

    public void registerExtraIntent(Context context) {
        try {
            for (IntentFilter intentFilter : extraIntentFilterList) {
                BroadcastHelper.ensureRegisterReceiver(context, this, intentFilter);
            }
        } catch (Exception e) {
            Debug.e(e);
        }
    }

    private void updateNetworkConnectedState(Context context) {
        lastNetworkConnected = NetworkUtil.isWiFiConnected(context.getApplicationContext());
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        Log.i(TAG, "Received[" + count + "]: " + intent.getAction() + " data " + intent.getData());
        ++count;

        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
                onReceiveBootComplete(intent);
                break;
            case PARSE_PUSH_NOTIFICATION:
                onReceiveParsePushNotification(intent);
                break;
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
            case ConnectivityManager.CONNECTIVITY_ACTION:
                handleNetworkConnectChange(context);
                break;
            case NO_SAVED_NETWORK_CONNECTED_ACTION:
                onUnableConnectNetworkEvent();
                break;
            case Intent.ACTION_UMS_CONNECTED:
            case Intent.ACTION_UMS_DISCONNECTED:
                onUmsStateChanged(intent);
                break;
            case Intent.ACTION_POWER_CONNECTED:
            case Intent.ACTION_POWER_DISCONNECTED:
                onPowerStateChanged(intent);
                break;
            case Intent.ACTION_MEDIA_BAD_REMOVAL:
                onMediaStateChanged(intent);
                onMediaBadRemoval(intent);
                break;
            case Intent.ACTION_MEDIA_MOUNTED:
                onMediaStateChanged(intent);
                onMediaMounted(intent);
                break;
            case Intent.ACTION_MEDIA_REMOVED:
                onMediaStateChanged(intent);
                onMediaRemoved(intent);
                break;
            case Intent.ACTION_MEDIA_UNMOUNTED:
                onMediaUnmounted(intent);
                break;
            case Intent.ACTION_MEDIA_CHECKING:
                onMediaChecking(intent);
                break;
            case Intent.ACTION_MEDIA_SHARED:
                onMediaShared(intent);
                break;
            case Intent.ACTION_MEDIA_SCANNER_STARTED:
                onMediaScanStarted(intent);
                break;
            case Intent.ACTION_MEDIA_SCANNER_FINISHED:
                onMediaScanFinished(intent);
                break;
            case START_ONYX_SETTINGS:
                onStartSettings(intent);
                break;
            case OPEN_DOCUMENT_ACTION:
                onOpenDocumentAction(intent);
                break;
            case Intent.ACTION_LOCALE_CHANGED:
                onLocaleChanged();
                break;
            case MTP_EVENT_ACTION:
                onMtpEvent(intent);
                break;
            case ACTION_USB_STATE:
                onUsbStateChanged(intent);
                break;
            case SYSTEM_UI_DIALOG_OPEN_ACTION:
                notifyNoFocusSystemDialogChanged(intent, true);
            case STATUS_BAR_ICON_REFRESH_START_ACTION:
                notifySystemUIChange(intent, true);
                break;
            case SYSTEM_UI_DIALOG_CLOSE_ACTION:
                notifyNoFocusSystemDialogChanged(intent, false);
            case SYSTEM_WAKE_UP:
            case STATUS_BAR_ICON_REFRESH_FINISH_ACTION:
            case Intent.ACTION_SCREEN_ON:
                notifySystemUIChange(intent, false);
                break;
            case SYSTEM_HOME:
                notifyHomeClicked(intent);
                break;
            case SYSTEM_UI_SCREEN_SHOT_START_ACTION:
                notifyScreenShot(intent, false);
                break;
            case SYSTEM_UI_SCREEN_SHOT_END_ACTION:
                notifyScreenShot(intent, true);
                break;
            case Intent.ACTION_BATTERY_LOW:
                notifyLowBattery(intent);
                break;
            case Intent.ACTION_SHUTDOWN:
                notifyShutDown(intent);
                break;
            case ONYX_KEYBOARD_SHOW:
                notifyOnyxKeyboardChange(true);
                break;
            case ONYX_KEYBOARD_HIDE:
                notifyOnyxKeyboardChange(false);
                break;
            case BroadcastHelper.FLOAT_BUTTON_TOUCHE_ACTION:
                notifyFloatButtonChanged(intent);
                break;
            case TOAST_SHOW_ACTION:
                notifyToastChanged(true);
                break;
            case TOAST_HIDE_ACTION:
                notifyToastChanged(false);
                break;
            case ENTER_RECENT_INTERFACE_ACTION:
                notifyEnterRecent();
                break;
            case Intent.ACTION_REBOOT:
                notifyReboot();
                break;
            case ENTER_KEYGUARD_INTERFACE_ACTION:
                onLockScreenChanged(true);
                break;
            case Intent.ACTION_USER_PRESENT:
                onLockScreenChanged(false);
                break;
            case ONYX_RESET_PIN_CODE_ACTION:
                onResetPinCodeIntent(context, intent);
                break;
            case ONYX_FINGERPRINT_REMOVED_ACTION:
            case ONYX_PIN_CODE_REMOVED_ACTION:
                onSystemKeyguardChangedIntent(context, intent);
                break;
            case STATUS_BAR_SHOW_ACTION:
                onStatusBarChanged(true);
                break;
            case STATUS_BAR_HIDE_ACTION:
                onStatusBarChanged(false);
                break;
            case ACTION_USB_DEVICE_ATTACHED:
                onUsbDeviceStateChange(true);
                break;
            case ACTION_USB_DEVICE_DETACHED:
                onUsbDeviceStateChange(false);
                break;
            case ACTION_VOLUME_STATE_CHANGED:
                int volumeState = intent.getIntExtra(EXTRA_VOLUME_STATE, 0);
                onVolumeStateChange(volumeState);
                break;
            case Intent.ACTION_PACKAGE_ADDED:
            case Intent.ACTION_PACKAGE_REPLACED:
            case Intent.ACTION_PACKAGE_REMOVED:
            case Intent.ACTION_PACKAGE_FULLY_REMOVED:
                onPackageChanged(action, intent);
                break;
            default:
                onReceiveExtraIntent(context, intent);
                break;
        }
    }

    private void onUsbDeviceStateChange(boolean attached) {
        if (usbDeviceStateChangeListener != null) {
            usbDeviceStateChangeListener.onUsbDeviceStateChange(attached);
        }
    }

    private void onVolumeStateChange(int state) {
        if (volumeStateChangeListener != null) {
            volumeStateChangeListener.onVolumeStateChange(state);
        }
    }

    private void onResetPinCodeIntent(Context context, Intent intent) {
        if (resetPinCodeListener != null) {
            resetPinCodeListener.onResetPinCode(context, intent);
        }
    }

    private void onSystemKeyguardChangedIntent(Context context, Intent intent) {
        if (keyguardChangedListener != null) {
            keyguardChangedListener.onSystemKeyguardChanged(context, intent);
        }
    }

    private void onReceiveExtraIntent(Context context, Intent intent) {
        if (extraIntentReceiver == null) {
            return;
        }
        extraIntentReceiver.onReceive(context, intent);
    }

    public DeviceReceiver setExtraIntentReceiver(ExtraIntentReceiver extraIntentReceiver) {
        this.extraIntentReceiver = extraIntentReceiver;
        extraIntentFilterList.clear();
        extraIntentFilterList.addAll(extraIntentReceiver.getIntentFilterList());
        return this;
    }

    public DeviceReceiver setWiFiStateChangedListener(WiFiStateChangedListener wiFiStateChangedListener) {
        this.wiFiStateChangedListener = wiFiStateChangedListener;
        return this;
    }

    public void setBootCompleteListener(final BootCompleteListener l) {
        bootCompleteListener = l;
    }

    public void setPushNotificationListener(final PushNotificationListener l) {
        pushNotificationListener = l;
    }

    public void setNetworkConnectChangedListener(final NetworkConnectChangedListener l) {
        networkConnectChangedListener = l;
    }

    public void setUmsStateListener(final UmsStateListener l) {
        umsStateListener = l;
    }

    public void setMediaStateListener(final MediaStateListener l) {
        mediaStateListener = l;
    }

    public void setSettingsListener(final SettingsListener l) {
        settingsListener = l;
    }

    public void setOpenDocumentListener(final OpenDocumentListener l) {
        openDocumentListener = l;
    }

    public void setLocaleChangedListener(final LocaleChangedListener l) {
        localeChangedListener = l;
    }

    public void setBluetoothStateListener(final BluetoothStateListener l) {
        bluetoothStateListener = l;
    }

    public void setResetPinCodeListener(ResetPinCodeListener l) {
        this.resetPinCodeListener = l;
    }

    public void setKeyguardChangedListener(SystemKeyguardChangedListener l) {
        this.keyguardChangedListener = l;
    }

    public void setMtpEventListener(final MtpEventListener l) {
        mtpEventListener = l;
    }

    public void setUsbStateListener(final UsbStateListener l) {
        usbStateListener = l;
    }

    public void setSystemUIChangeListener(final SystemUIChangeListener listener) {
        systemUIChangeListener = listener;
    }

    public void setUsbDeviceStateChangeListener(UsbDeviceStateChangeListener usbDeviceStateChangeListener) {
        this.usbDeviceStateChangeListener = usbDeviceStateChangeListener;
    }

    public void setVolumeStateChangeListener(VolumeStateChangeListener volumeStateChangeListener) {
        this.volumeStateChangeListener = volumeStateChangeListener;
    }

    public void setPackageChangedListener(PackageChangedListener packageChangedListener) {
        this.packageChangedListener = packageChangedListener;
    }

    public SystemUIChangeListener getSystemUIChangeListener() {
        return systemUIChangeListener;
    }

    public void onReceiveBootComplete(Intent intent) {
        if (bootCompleteListener != null) {
            bootCompleteListener.onReceivedBootComplete(intent);
        }
    }

    public void onReceiveParsePushNotification(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Log.i(TAG, "ignore null bundle");
        }
        final String data = bundle.getString("com.parse.Data");
        Map<String, Object> map = JSON.parseObject(data, Map.class);
        Log.i(TAG, "map: " + map.toString());

        if (pushNotificationListener != null) {
            pushNotificationListener.onReceivedPushNotification(intent);
        }
    }

    public void onWiFiStateChanged(Context context, @NonNull NetworkInfo info) {
        if (wiFiStateChangedListener != null) {
            wiFiStateChangedListener.onWiFiStateChanged(context, info);
        }
    }

    public void onNetworkConnectChanged(boolean currentConnected) {
        if (networkConnectChangedListener != null) {
            networkConnectChangedListener.onNetworkConnectChanged(currentConnected);
        }
    }

    public void onUnableConnectNetworkEvent(){
        if (networkConnectChangedListener != null){
            networkConnectChangedListener.onUnableConnectNetworkEvent();
        }
    }

    public void onUmsStateChanged(Intent intent) {
        if (umsStateListener != null) {
            umsStateListener.onUmsStateChanged(intent);
        }
    }

    public void onPowerStateChanged(Intent intent) {
    }

    public void onLockScreenChanged(boolean open) {
        if (systemUIChangeListener != null) {
            systemUIChangeListener.onLockScreenChanged(open);
        }
    }

    public void onStatusBarChanged(boolean show) {
        if (systemUIChangeListener != null) {
            systemUIChangeListener.onStatusBarChanged(show);
        }
    }

    public void onMediaBadRemoval(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaBadRemoval(intent);
        }
    }

    public void onMediaMounted(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaMounted(intent);
        }
    }

    public void onMediaRemoved(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaRemoved(intent);
        }
    }

    public void onMediaUnmounted(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaUnmounted(intent);
        }
    }

    public void onMediaChecking(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaChecking(intent);
        }
    }

    public void onMediaShared(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaShared(intent);
        }
    }

    public void onMediaScanStarted(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaScanStarted(intent);
        }
    }

    public void onMediaScanFinished(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaScanFinished(intent);
        }
    }

    public void onMediaStateChanged(Intent intent) {
        if (mediaStateListener != null) {
            mediaStateListener.onMediaScanStarted(intent);
        }
    }

    public void onMtpEvent(Intent intent) {
        if (mtpEventListener != null) {
            mtpEventListener.onMtpEvent(intent);
        }
    }

    public void onUsbStateChanged(Intent intent) {
        if (usbStateListener != null) {
            usbStateListener.onUsbState(intent, intent.getExtras().getBoolean("connected"));
        }
    }

    public void setStorageState(final String mount, final String state) {
        storageState.put(mount, state);
    }

    public String getStorageState(final String mount) {
        if (storageState == null) {
            return null;
        }
        return storageState.get(mount);
    }

    static public boolean isExternalStorageEvent(final Context context, Intent intent) {
        final String string = FileUtils.getRealFilePathFromUri(context, intent.getData());
        if (EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath().contains(string)) {
            return true;
        }
        return false;
    }

    public boolean isStorageShared(final String mount) {
        final String value = storageState.get(mount);
        if (StringUtils.isNullOrEmpty(value)) {
            return false;
        }
        return value.equals(Intent.ACTION_MEDIA_SHARED);
    }

    public void onStartSettings(Intent intent) {
        if (settingsListener != null) {
            settingsListener.onSystemSettingsClicked(intent);
        }
    }

    public void onOpenDocumentAction(Intent intent) {
        if (openDocumentListener != null) {
            String path = intent.getStringExtra("path");
            openDocumentListener.onOpenDocumentAction(intent, path);
        }
    }

    public void onLocaleChanged() {
        if (localeChangedListener != null) {
            localeChangedListener.onLocaleChanged();   
        }
    }

    private void notifySystemUIChange(final Intent intent, boolean open) {
        if (getSystemUIChangeListener() == null) {
            return;
        }
        String iconType = intent.getStringExtra(ICON_TYPE);
        if (StringUtils.isNotBlank(iconType)) {
            getSystemUIChangeListener().onSystemIconChanged(intent.getAction(), iconType, open);
        } else {
            getSystemUIChangeListener().onSystemUIChanged(intent.getAction(), open);
        }
    }

    private void notifyNoFocusSystemDialogChanged(final Intent intent, boolean open) {
        if (getSystemUIChangeListener() == null) {
            return;
        }
        String dialogType = intent.getStringExtra(DIALOG_TYPE);
        if (noFocusSystemDialogTypeList.contains(dialogType)) {
            getSystemUIChangeListener().onNoFocusSystemDialogChanged(open);
        }
    }

    private void notifyHomeClicked(final Intent intent) {
        if (getSystemUIChangeListener() == null) {
            return;
        }
        getSystemUIChangeListener().onHomeClicked();
    }

    private void notifyScreenShot(final Intent intent,boolean end){
        if (getSystemUIChangeListener() == null) {
            return;
        }
        getSystemUIChangeListener().onScreenShot(intent, end);
    }

    private void notifyLowBattery(final Intent intent){
        if (getSystemUIChangeListener() == null) {
            return;
        }
        getSystemUIChangeListener().onLowBattery(intent);
    }

    private void notifyShutDown(final Intent intent){
        if (getSystemUIChangeListener() == null) {
            return;
        }
        getSystemUIChangeListener().onShutDown(intent);
    }

    private void notifyOnyxKeyboardChange(boolean open){
        if (getSystemUIChangeListener() == null) {
            return;
        }
        getSystemUIChangeListener().onOnyxKeyboardChanged(open);
    }

    private void notifyFloatButtonChanged(final Intent intent) {
        if (getSystemUIChangeListener() == null) {
            return;
        }
        boolean status = intent.getBooleanExtra(FLOAT_BUTTON_STATUS, false);
        getSystemUIChangeListener().onFloatButtonChanged(status);
    }

    private void notifyToastChanged(boolean show) {
        if (getSystemUIChangeListener() == null) {
            return;
        }
        getSystemUIChangeListener().onToastChanged(show);
    }

    private void notifyEnterRecent() {
        if (getSystemUIChangeListener() == null) {
            return;
        }
        getSystemUIChangeListener().onEnterRecent();
    }

    private void notifyReboot() {
        if (getSystemUIChangeListener() == null) {
            return;
        }
        getSystemUIChangeListener().onReboot();
    }

    /**
     * if there is any storage still in shared state. the shared state is used to ignore
     * reloading request when mount/umount.
     * @return
     */
    public boolean isAnyStorageShared() {
        return isStorageShared(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath()) ||
                isStorageShared(EnvironmentUtil.getRemovableSDCardDirectory().getAbsolutePath());
    }

    public void updateStorageState(final Context context, final Intent intent) {
        final String string = FileUtils.getRealFilePathFromUri(context, intent.getData());
        setStorageState(string, intent.getAction());
    }

    public IntentFilter netWorkFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(NO_SAVED_NETWORK_CONNECTED_ACTION);
        return intentFilter;
    }

    private void handleNetworkConnectChange(Context context) {
        handleWiFIStateChanged(context);

        getRxManager().enqueue(new GetNetworkInfoRequest(), new RxCallback<GetNetworkInfoRequest>() {
            @Override
            public void onNext(@NonNull GetNetworkInfoRequest getNetworkInfoRequest) {
                boolean currentConnected = NetworkUtil.isWiFiConnected(getNetworkInfoRequest.info);
                boolean networkChange = lastNetworkConnected ^ currentConnected;
                lastNetworkConnected = currentConnected;
                if (networkChange) {
                    onNetworkConnectChanged(currentConnected);
                }
            }
        });

    }

    private void handleWiFIStateChanged(final Context context) {
        if (wiFiStateChangedListener == null) {
            return;
        }
        getRxManager().enqueue(new GetNetworkInfoRequest(), new RxCallback<GetNetworkInfoRequest>() {
            @Override
            public void onNext(@NonNull GetNetworkInfoRequest getNetworkInfoRequest) {
                if (getNetworkInfoRequest.info != null) {
                    onWiFiStateChanged(context, getNetworkInfoRequest.info);
                }
            }
        });
    }

    public IntentFilter getPackageFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addDataScheme("package");
        return filter;
    }

    private void onPackageChanged(String action, Intent intent) {
        if (packageChangedListener == null) {
            return;
        }
        switch (action) {
            case Intent.ACTION_PACKAGE_ADDED:
                packageChangedListener.onPackageAdded(intent);
                break;
            case Intent.ACTION_PACKAGE_REPLACED:
                packageChangedListener.onPackageReplaced(intent);
                break;
            case Intent.ACTION_PACKAGE_REMOVED:
                packageChangedListener.onPackageRemoved(intent);
                break;
            case Intent.ACTION_PACKAGE_FULLY_REMOVED:
                packageChangedListener.onPackageFullRemoved(intent);
                break;
            default:
                break;
        }
        packageChangedListener.onPackageChanged(intent);
    }
}
