package com.onyx.android.sdk.data;

import java.util.List;

/**
 * Create at 2018/7/5 by anypwx
 **/
public class TutorialPowerManageConfigModel {
    private List<String> tutorialSettingSequence;
    private String systemScreenOffKey;
    private String systemPowerOffKey;
    private String systemWakeUpFrontLightKey;
    private boolean powerSaveMode;
    private String systemWifiInactivityKey;
    private List<Integer> screenScreenOffValues;
    private List<Integer> powerOffTimeoutValues;
    private List<Integer> networkInactivityTimeoutValues;

    public boolean isPowerSaveMode() {
        return powerSaveMode;
    }

    public void setPowerSaveMode(boolean powerSaveMode) {
        this.powerSaveMode = powerSaveMode;
    }

    public List<String> getTutorialSettingSequence() {
        return tutorialSettingSequence;
    }

    public void setTutorialSettingSequence(List<String> tutorialSettingSequence) {
        this.tutorialSettingSequence = tutorialSettingSequence;
    }

    public String getSystemScreenOffKey() {
        return systemScreenOffKey;
    }

    public void setSystemScreenOffKey(String systemScreenOffKey) {
        this.systemScreenOffKey = systemScreenOffKey;
    }

    public String getSystemPowerOffKey() {
        return systemPowerOffKey;
    }

    public void setSystemPowerOffKey(String systemPowerOffKey) {
        this.systemPowerOffKey = systemPowerOffKey;
    }

    public String getSystemWakeUpFrontLightKey() {
        return systemWakeUpFrontLightKey;
    }

    public void setSystemWakeUpFrontLightKey(String systemWakeUpFrontLightKey) {
        this.systemWakeUpFrontLightKey = systemWakeUpFrontLightKey;
    }

    public String getSystemWifiInactivityKey() {
        return systemWifiInactivityKey;
    }

    public void setSystemWifiInactivityKey(String systemWifiInactivityKey) {
        this.systemWifiInactivityKey = systemWifiInactivityKey;
    }

    public List<Integer> getScreenScreenOffValues() {
        return screenScreenOffValues;
    }

    public void setScreenScreenOffValues(List<Integer> screenScreenOffValues) {
        this.screenScreenOffValues = screenScreenOffValues;
    }

    public List<Integer> getPowerOffTimeoutValues() {
        return powerOffTimeoutValues;
    }

    public void setPowerOffTimeoutValues(List<Integer> powerOffTimeoutValues) {
        this.powerOffTimeoutValues = powerOffTimeoutValues;
    }

    public List<Integer> getNetworkInactivityTimeoutValues() {
        return networkInactivityTimeoutValues;
    }

    public void setNetworkInactivityTimeoutValues(List<Integer> networkInactivityTimeoutValues) {
        this.networkInactivityTimeoutValues = networkInactivityTimeoutValues;
    }
}
