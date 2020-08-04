package com.onyx.gallery.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntRange;

import com.onyx.android.sdk.utils.ResManager;
import com.onyx.android.sdk.utils.RxTimerUtil;
import com.onyx.gallery.R;

/**
 * Created by ming on 2017/1/7.
 */

public class ToastUtils {

    public static final int LONG_DELAY = 3500;
    public static final int SHORT_DELAY = 2000;
    public static final long CUSTOM_SHORT_TIME = 600;
    private static String oldMsg;
    protected static Toast toast = null;
    protected static LayoutInflater inflater = null;
    private static long oneTime = 0;
    private static long twoTime = 0;
    private static int textSize = ResManager.getDimens(R.dimen.kcb_item_title_text_size);

    public static void showToast(Context appContext, String s) {
        try {
            if (toast == null || toast.getView() == null || toast.getView().findViewById(android.R.id.message) == null) {
                toast = Toast.makeText(appContext.getApplicationContext(), s, Toast.LENGTH_SHORT);
                toast.show();
                oneTime = System.currentTimeMillis();
            } else {
                twoTime = System.currentTimeMillis();
                if (s.equals(oldMsg)) {
                    if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                        toast.show();
                    }
                } else {
                    oldMsg = s;
                    toast.setText(s);
                    toast.show();
                }
            }
            oneTime = twoTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTextSize(int textSize) {
        ToastUtils.textSize = textSize;
    }

    public static void showCustomToast(Context appContext, String s) {
        showToastWithPosition(appContext, s, ResManager.getDimens(R.dimen.dialog_posision_x), ResManager.getDimens(R.dimen.dialog_posision_y));
    }

    public static void showCustomToastFromWorkingThread(final Context appContext, final String s) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                showCustomToast(appContext, s);
            }
        });
    }

    private static void showToastWithPosition(Context appContext, String s, int dx, int dy) {
        if (toast == null) {
            toast = new Toast(appContext);
        }
        if (inflater == null) {
            inflater = LayoutInflater.from(appContext);
        }
        View view = inflater.inflate(R.layout.customs_toast_layout, null);
        TextView textView = view.findViewById(R.id.textview_message);
        textView.setText(s);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        toast.setGravity(Gravity.CENTER, dx, dy);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    public static void showCustomToast(Context appContext, int resId) {
        showCustomToast(appContext, appContext.getString(resId));
    }

    public static void showToast(Context appContext, int resId) {
        showCustomToast(appContext, appContext.getString(resId));
    }

    public static void showScreenCenterToast(Context appContext, int resId) {
        showToastWithPosition(appContext, appContext.getString(resId), 0, 0);
    }

    public static void showScreenCenterToast(Context appContext, String msg) {
        showScreenCenterToast(appContext, msg, 0, 0);
    }

    public static void showToastWithActivity(Activity activity, String s, @IntRange(from = 0, to = ToastUtils.SHORT_DELAY) long time) {
        showToastWithActivity(activity, s);
        RxTimerUtil.timer(time, new RxTimerUtil.TimerObserver() {
            @Override
            public void onNext(Long aLong) {
                ToastUtils.cancel();
            }
        });
    }

    public static void showToastWithActivity(Activity activity, String s) {
        if (activity == null) {
            return;
        }
        if (toast == null) {
            toast = new Toast(activity.getApplicationContext());
        }
        if (inflater == null) {
            inflater = LayoutInflater.from(activity.getApplicationContext());
        }
        View view = inflater.inflate(R.layout.customs_toast_layout, null);
        TextView textView = view.findViewById(R.id.textview_message);
        textView.setText(s);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        int width = (int) ResManager.getScreenWidth();
        int height = (int) ResManager.getScreenHeight();
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        view.layout(0, 0, width, height);

        Point point = getOffsetPoint(activity, textView.getHeight(), textView.getWidth());
        toast.setGravity(Gravity.LEFT, point.x, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    public static Point getOffsetPoint(Activity activity, int height, int width) {
        int deviceWidth = (int) ResManager.getScreenWidth();
        if (ScreenUtils.isLandscape()) {
            deviceWidth = (int) ResManager.getScreenHeight();
        }
        if (width > deviceWidth / 2) {
            width = deviceWidth / 2;
        }

        View view = activity.getWindow().getDecorView();
        Point point = new Point();
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        point.x = location[0];
        point.y = location[1];
        Point windowPoint = ResManager.getWindowDefaultSize(activity);
        point.x = point.x + windowPoint.x / 2 - width / 2;
        point.y = point.y + windowPoint.y / 2 - height / 2;
        return point;
    }

    public static void showScreenCenterToast(Context appContext, String msg, int dx, int dy) {
        showToastWithPosition(appContext, msg, dx, dy);
    }

    public static void cancel() {
        if (toast == null) {
            return;
        }
        toast.cancel();
    }
}
