package com.onyx.gallery.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.onyx.android.sdk.utils.ResManager;
import com.onyx.gallery.R;

import me.bakumon.statuslayoutmanager.library.StatusLayoutManager;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2018/7/13 19:43
 *     desc   :
 * </pre>
 */
public class StatusLayoutManagerUtils {
    public static StatusLayoutManager.Builder getDefault(@NonNull View contentLayout) {
        Context context = contentLayout.getContext();
        return new StatusLayoutManager.Builder(contentLayout)
                .setDefaultLayoutsBackgroundColor(Color.WHITE)
                .setLoadingLayout(createScreenCenterView(R.layout.view_loading, context))
                .setEmptyLayout(createScreenCenterView(R.layout.view_empty, context))
                .setEmptyClickViewID(R.id.tv_retry)
                .setErrorLayout(createScreenCenterView(R.layout.view_error, context))
                .setErrorClickViewID(R.id.tv_retry);
    }

    public static View createScreenCenterView(int layoutId, Context context){
        FrameLayout contentView = new FrameLayout(context);
        int statusBarHeight = BarUtils.getStatusBarHeight();
        int titleBarHeight = ResManager.getDimens(R.dimen.title_bar_minheight);
        int titleMargin = ResManager.getDimens(R.dimen.title_bar_margin_top);

        int marginBottom = (statusBarHeight + titleBarHeight + titleMargin) / 2;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.setMargins(0,0,0,marginBottom);

        LayoutInflater ly = LayoutInflater.from(context);
        View childView = ly.inflate(layoutId,null);
        contentView.addView(childView,params);

        return contentView;
    }

    public static void showOpenWifiTipLayout(StatusLayoutManager manager) {
        manager.showCustomLayout(createScreenCenterView(R.layout.jdshop_view_open_wifi_tip, ResManager.getAppContext()), R.id.tv_open_wifi);
    }

    public static void showWifiOpeningLayout(StatusLayoutManager manager) {
        manager.showCustomLayout(createScreenCenterView(R.layout.jdshop_view_wifi_opening, ResManager.getAppContext()), R.id.tv_open_wifi);
    }
}
