package com.onyx.brightnesssample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.onyx.android.sdk.device.Device;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Onyx-lw on 2017/7/4.
 */

public class NaturalLightBrightnessTestActivity extends Activity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    private RatingBar mColdRatingBarLightSettings = null;
    private RatingBar mWarmRatingBarLightSettings = null;

    private List<Integer> mColdBrightness = Arrays.asList(0, 3, 6, 9, 12, 15, 17, 19, 21, 23, 25, 26, 27, 28, 29, 30, 31);
    private List<Integer> mWarmBrightness = Arrays.asList(0, 3, 6, 9, 12, 15, 17, 19, 21, 23, 25, 26, 27, 28, 29, 30, 31);
    private static final int COLD_LIGHT = 1;
    private static final int WARM_LIGHT = 0;

    private EditText coldEditText;
    private EditText warmEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.natural_brightness_test);
        initData();
        initView();
    }

    private void initData() {
        Integer[] mWarmValues = Device.currentDevice.getWarmLightValues(this);
        Integer[] mColdValues = Device.currentDevice.getColdLightValues(this);
        if (mWarmValues == null || mColdValues == null) {
            return;
        }
        mWarmBrightness = Arrays.asList(mWarmValues);
        mColdBrightness = Arrays.asList(mColdValues);

    }

    private void initView() {
        coldEditText = findViewById(R.id.et_cold);
        warmEditText = findViewById(R.id.et_warm);
        //cold light
        mColdRatingBarLightSettings = (RatingBar) findViewById(R.id.ratingbar_cold_light_settings);
        mColdRatingBarLightSettings.setFocusable(false);
        findViewById(R.id.imagebutton_cold_light_down).setOnClickListener(this);
        findViewById(R.id.imagebutton_cold_light_add).setOnClickListener(this);
        mColdRatingBarLightSettings.setOnRatingBarChangeListener(this);

        //warm light
        mWarmRatingBarLightSettings = (RatingBar) findViewById(R.id.ratingbar_warm_light_settings);
        mWarmRatingBarLightSettings.setFocusable(false);
        findViewById(R.id.imagebutton_warm_light_down).setOnClickListener(this);
        findViewById(R.id.imagebutton_warm_light_add).setOnClickListener(this);
        mWarmRatingBarLightSettings.setOnRatingBarChangeListener(this);


        mColdRatingBarLightSettings.setNumStars(mColdBrightness.size() - 1);
        mColdRatingBarLightSettings.setMax(mColdBrightness.size() - 1);
        mWarmRatingBarLightSettings.setNumStars(mWarmBrightness.size() - 1);
        mWarmRatingBarLightSettings.setMax(mWarmBrightness.size() - 1);


        findViewById(R.id.btn_cold_set).setOnClickListener(this);
        findViewById(R.id.btn_warm_set).setOnClickListener(this);

        TextView coldValueDescTextView = (TextView)findViewById(R.id.cold_value_desc);
        coldValueDescTextView.setText("当前范围:(" + mColdBrightness.get(0) + " - " + mColdBrightness.get(mColdBrightness.size()-1) + ")");

        TextView warmValueDescTextView = (TextView)findViewById(R.id.warm_value_desc);
        warmValueDescTextView.setText("当前范围:(" + mWarmBrightness.get(0) + " - " + mWarmBrightness.get(mWarmBrightness.size()-1) + ")");
    }

    @Override
    protected void onDestroy() {
        int warmLightConfigValue = Device.currentDevice.getWarmLightConfigValue(this);
        int coldLightConfigValue = Device.currentDevice.getColdLightConfigValue(this);
        setNaturalLightBrightnessValue(COLD_LIGHT, coldLightConfigValue);
        setNaturalLightBrightnessValue(WARM_LIGHT, warmLightConfigValue);
        super.onDestroy();
    }

    private void setColdLightRatingBarProgress() {
        int progress = mColdRatingBarLightSettings.getProgress();
        int value = mColdBrightness.get(progress);
        setNaturalLightBrightnessValue(COLD_LIGHT, value);
        setEditTextValue(coldEditText, String.valueOf(value));
    }

    private void setWarmLightRatingBarProgress() {
        int progress = mWarmRatingBarLightSettings.getProgress();
        int value = mWarmBrightness.get(progress);
        setNaturalLightBrightnessValue(WARM_LIGHT, value);
        setEditTextValue(warmEditText, String.valueOf(value));
    }

    private void setNaturalLightBrightnessValue(int type, int value) {
        switch (type) {
            case COLD_LIGHT:
                Device.currentDevice.setColdLightDeviceValue(this, value);
                break;
            case WARM_LIGHT:
                Device.currentDevice.setWarmLightDeviceValue(this, value);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagebutton_cold_light_down:
                mColdRatingBarLightSettings.setProgress(mColdRatingBarLightSettings.getProgress() - 1);
                break;
            case R.id.imagebutton_cold_light_add:
                mColdRatingBarLightSettings.setProgress(mColdRatingBarLightSettings.getProgress() + 1);
                break;
            case R.id.imagebutton_warm_light_down:
                mWarmRatingBarLightSettings.setProgress(mWarmRatingBarLightSettings.getProgress() - 1);
                break;
            case R.id.imagebutton_warm_light_add:
                mWarmRatingBarLightSettings.setProgress(mWarmRatingBarLightSettings.getProgress() + 1);
                break;
            case R.id.btn_cold_set:
                String coldInput = coldEditText.getText().toString();
                if(coldInput.trim().length() == 0) {
                    return;
                }
                int coldValue = Math.min(Integer.valueOf(coldInput), mColdBrightness.get(mColdBrightness.size()-1));
                setEditTextValue(coldEditText, String.valueOf(coldValue));
                setNaturalLightBrightnessValue(COLD_LIGHT, coldValue);
                break;
            case R.id.btn_warm_set:
                String warmInput = warmEditText.getText().toString();
                if(warmInput.trim().length() == 0) {
                    return;
                }
                int warmValue = Math.min(Integer.valueOf(warmInput), mWarmBrightness.get(mWarmBrightness.size()-1));
                setEditTextValue(warmEditText, String.valueOf(warmValue));
                setNaturalLightBrightnessValue(WARM_LIGHT, warmValue);
                break;
        }
    }

    private void setEditTextValue(EditText editText, String value) {
        if (editText == null || value == null) {
            return;
        }
        editText.setText(value);
        editText.setSelection(value.length());
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        switch (ratingBar.getId()) {
            case R.id.ratingbar_cold_light_settings:
                setColdLightRatingBarProgress();
                break;
            case R.id.ratingbar_warm_light_settings:
                setWarmLightRatingBarProgress();
                break;
        }
    }
}
