package com.onyx.android.sdk.data;

/**
 * Create at 2018/9/12 by anypwx
 **/
public class SettingPowerDefaultConfig {
    private int screenTimeout = -1;
    private int powerOffTimeout = -1;
    private int wifiInactivityTimeout = -1;
    private boolean enablePowerSavedMode;
    private boolean openFrontLight;

    public int getScreenTimeout() {
        return screenTimeout;
    }

    public void setScreenTimeout(int screenTimeout) {
        this.screenTimeout = screenTimeout;
    }

    public int getPowerOffTimeout() {
        return powerOffTimeout;
    }

    public void setPowerOffTimeout(int powerOffTimeout) {
        this.powerOffTimeout = powerOffTimeout;
    }

    public int getWifiInactivityTimeout() {
        return wifiInactivityTimeout;
    }

    public void setWifiInactivityTimeout(int wifiInactivityTimeout) {
        this.wifiInactivityTimeout = wifiInactivityTimeout;
    }

    public boolean isOpenFrontLight() {
        return openFrontLight;
    }

    public void setOpenFrontLight(boolean openFrontLight) {
        this.openFrontLight = openFrontLight;
    }

    public boolean isEnablePowerSavedMode() {
        return enablePowerSavedMode;
    }

    public void setEnablePowerSavedMode(boolean enablePowerSavedMode) {
        this.enablePowerSavedMode = enablePowerSavedMode;
    }
}
