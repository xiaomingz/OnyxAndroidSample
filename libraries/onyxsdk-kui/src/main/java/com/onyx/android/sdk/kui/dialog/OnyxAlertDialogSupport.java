package com.onyx.android.sdk.kui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.kui.R;
import com.onyx.android.sdk.utils.ObjectHolder;
import com.onyx.android.sdk.utils.ResManager;
import com.onyx.android.sdk.utils.StringUtils;

import java.lang.ref.WeakReference;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

/**
 * New Dialog For Onyx Apps
 * Use Params To Config Dialog Properties.
 * Params settings support builder mode,could set all properties in one line.
 * Created by solskjaer49 on 15/12/1 11:54 12:03.
 */
public class OnyxAlertDialogSupport extends DialogFragment {
    static final String TAG = OnyxAlertDialogSupport.class.getSimpleName();
    private RelativeLayout alertTittleBarLayout;
    private LinearLayout functionPanelLayout;
    private TextView tittleTextView, alertMessageView, pageSizeIndicator;
    private ImageView closeButtonTopRight;
    private ImageView prevPageView, nextPageView;
    private Button positiveButton;
    private static ObjectHolder<OnyxAlertDialogSupport> dialogHolder = new ObjectHolder<>();
    private WeakReference<OnyxAlertDialogSupport> onyxAlertDialogWeakReference;

    protected Button getPositiveButton() {
        return positiveButton;
    }

    private Button negativeButton;
    private Button neutralButton;
    private View customContentView, topDividerLine, functionButtonDividerLine, bottomDivider, btnNeutralDivider;
    private Params params = new Params();
    private DialogEventsListener eventsListener;

    public interface CustomViewAction {
        void onCreateCustomView(View customView, TextView pageIndicator);
    }

    public interface DialogEventsListener {
        void onCancel(OnyxAlertDialogSupport dialog, DialogInterface dialogInterface);

        void onDismiss(OnyxAlertDialogSupport dialog, DialogInterface dialogInterface);
    }

    public void setDialogEventsListener(DialogEventsListener listener) {
        this.eventsListener = listener;
    }

    public Params getParams() {
        return params;
    }

    public OnyxAlertDialogSupport setParams(Params params) {
        this.params = params;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }
        setDialogPosition();
        adjustButton();
        if (params.dialogHeight != ViewGroup.LayoutParams.WRAP_CONTENT
                || params.dialogWidth != ViewGroup.LayoutParams.WRAP_CONTENT) {
            getDialog().getWindow().setLayout(params.dialogWidth, params.dialogHeight);
        } else {
            getDialog().getWindow().setLayout(getDefaultWidth(params.isUsePercentageWidth()), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        getDialog().setCanceledOnTouchOutside(params.canceledOnTouchOutside);
    }

    private void adjustButton() {
        if (!params.isEnableNegativeButton() && !params.isEnableNeutralButton()) {
            positiveButton.setBackgroundResource(R.drawable.imagebtn_bg_bottom_radius);
        }
        if (!params.isEnablePositiveButton() && !params.isEnableNeutralButton()) {
            negativeButton.setBackgroundResource(R.drawable.imagebtn_bg_bottom_radius);
        }
        if (!params.isEnableNegativeButton() && !params.isEnablePositiveButton()) {
            neutralButton.setBackgroundResource(R.drawable.imagebtn_bg_bottom_radius);
        }
    }

    private void setDialogPosition() {
        Window window = getDialog().getWindow();
        if (window == null) {
            return;
        }
        if (params.isEnableSidebarPosition()) {
            adjustWindow(window);
        }
        if (params.isInputTypeMode()) {
            adjustWindowWithInputTypeMode(window);
        }
    }

    private  void adjustWindow(Window window){
        WindowManager.LayoutParams attr = window.getAttributes();
        if (params.p == null) {
            params.setDialogPosition(new Point(ResManager.getDimens(R.dimen.dialog_posision_x), ResManager.getDimens(R.dimen.dialog_posision_y)));
        }
        attr.x = params.p.x;
        attr.y = params.p.y;
        window.setAttributes(attr);
    }

    protected  void adjustWindowWithInputTypeMode(Window window) {
        window.setGravity(Gravity.TOP);
        window.getAttributes().y = ResManager.getDimens(R.dimen.dialog_position_input_type_y);
    }

    protected int getDefaultWidth(boolean usePercentageWidth) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        int screenWidth = Math.min(dm.widthPixels, dm.heightPixels);
        return usePercentageWidth ? (screenWidth * 7 / 10) : screenWidth - getResources().getDimensionPixelSize(R.dimen.onyx_alert_dialog_width_margin);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NO_FRAME, params.dialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(params.customLayoutResID == -1 ?
                params.defaultLayoutResID : params.customLayoutResID, container, false);
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.layout_dialog);
        alertTittleBarLayout = (RelativeLayout) view.findViewById(R.id.dialog_tittleBar);
        tittleTextView = (TextView) alertTittleBarLayout.findViewById(R.id.textView_title);
        closeButtonTopRight = (ImageView) alertTittleBarLayout.findViewById(R.id.button_close);
        prevPageView = (ImageView) alertTittleBarLayout.findViewById(R.id.button_previous);
        nextPageView = (ImageView) alertTittleBarLayout.findViewById(R.id.button_next);
        pageSizeIndicator = (TextView) alertTittleBarLayout.findViewById(R.id.page_size_indicator);
        alertMessageView = (TextView) view.findViewById(R.id.alert_msg_text);
        functionPanelLayout = (LinearLayout) view.findViewById(R.id.dialog_button_bar);
        topDividerLine = view.findViewById(R.id.top_divider_line);
        bottomDivider = view.findViewById(R.id.bottom_divider_line);
        btnNeutralDivider = view.findViewById(R.id.button_panel_neutral_divider);
        positiveButton = (Button) view.findViewById(R.id.btn_ok);
        negativeButton = (Button) view.findViewById(R.id.btn_cancel);
        neutralButton = (Button) view.findViewById(R.id.btn_neutral);
        functionButtonDividerLine = view.findViewById(R.id.button_panel_divider);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        closeButtonTopRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        customByParams(view);
        return view;
    }

    private void customByParams(View parentView) {
        if (params.enableTittle) {
            alertTittleBarLayout.setVisibility(View.VISIBLE);
            tittleTextView.setText(params.tittleString);
        } else {
            topDividerLine.setVisibility(View.GONE);
            alertTittleBarLayout.setVisibility(View.GONE);
        }

        if (params.enableCloseButtonTopRight) {
            closeButtonTopRight.setVisibility(View.VISIBLE);
        }
        if (params.enablePageIndicatorPanel) {
            prevPageView.setVisibility(View.VISIBLE);
            nextPageView.setVisibility(View.VISIBLE);
            if (params.prevPageAction != null) {
                prevPageView.setOnClickListener(params.prevPageAction);
            }
            if (params.nextPageAction != null) {
                nextPageView.setOnClickListener(params.nextPageAction);
            }
        }

        setNegativeButton(params.enableNegativeButton, params.negativeButtonText, params.negativeAction);
        setPositiveButton(params.enablePositiveButton, params.positiveButtonText, params.positiveAction);
        setNeutralButton(params.enableNeutralButton, params.neutralButtonText, params.neutralAction);
        setEnableFunctionPanel(params.enableFunctionPanel);
        if (!(params.enableNegativeButton && params.enablePositiveButton)) {
            functionButtonDividerLine.setVisibility(View.GONE);
        }
        parentView.findViewById(R.id.button_function_panel).setVisibility(params.enablePageIndicator ? View.VISIBLE : View.GONE);
        pageSizeIndicator.setVisibility(params.enablePageIndicator ? View.VISIBLE : View.GONE);

        if (params.alertMsgGravity != Gravity.CENTER) {
            alertMessageView.setGravity(params.alertMsgGravity);
        }

        if (params.alertMsgTextPixelSize > 0) {
            alertMessageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, params.alertMsgTextPixelSize);
        }

        if (params.customContentLayoutResID != -1) {
            setCustomContentLayout(parentView, params.customContentLayoutResID,
                    params.customLayoutHeight, params.customLayoutWidth);
            params.customViewAction.onCreateCustomView(customContentView, pageSizeIndicator);
        } else {
            setAlertMsg(params.alertMsgString);
        }
        if (params.customLayoutBackgroundResId != -1) {
            ViewGroup viewGroup = (ViewGroup) parentView.findViewById(R.id.layout_dialog);
            viewGroup.setBackgroundResource(params.customLayoutBackgroundResId);
        }
        if (params.keyAction != null) {
            getDialog().setOnKeyListener(params.keyAction);
        }
    }

    private void setCustomContentLayout(View parentView, int layoutID, int layoutHeight, int layoutWidth) {
        //using custom Layout must define id at top level custom layout.
        alertMessageView.setVisibility(View.GONE);
        if (customContentView == null) {
            customContentView = getActivity().getLayoutInflater().inflate(layoutID, null);
            RelativeLayout parentLayout = (RelativeLayout) parentView;
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(layoutWidth,
                    layoutHeight);
            p.addRule(RelativeLayout.BELOW, R.id.top_divider_line);
            parentLayout.addView(customContentView, p);
            View bottomDivider = parentLayout.findViewById(R.id.bottom_divider_line);
            RelativeLayout.LayoutParams dividerParams = (RelativeLayout.LayoutParams) bottomDivider.getLayoutParams();
            dividerParams.addRule(RelativeLayout.BELOW, customContentView.getId());
            bottomDivider.setLayoutParams(dividerParams);
        }
    }

    private void resetCustomContentView() {
        customContentView = null;
    }

    public void show(FragmentManager manager) {
        this.show(manager, getClass().getSimpleName());
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        EpdController.disableRegal();
        super.show(manager, tag);
        onyxAlertDialogWeakReference = new WeakReference<>(this);
        dialogHolder.add(onyxAlertDialogWeakReference);
    }

    @Override
    public void dismiss() {
        EpdController.enableRegal();
        super.dismiss();
        dialogHolder.remove(onyxAlertDialogWeakReference);
    }

    public static void dismissAll() {
        for (WeakReference<OnyxAlertDialogSupport> onyxAlertDialogWeakReference : dialogHolder.getCopyOfObjectList()) {
            OnyxAlertDialogSupport onyxAlertDialog = onyxAlertDialogWeakReference.get();
            if (onyxAlertDialog != null) {
                onyxAlertDialog.dismiss();
            }
        }
        dialogHolder.clear();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (eventsListener != null) {
            eventsListener.onCancel(OnyxAlertDialogSupport.this, dialog);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (eventsListener != null) {
            eventsListener.onDismiss(OnyxAlertDialogSupport.this, dialog);
        }
        resetCustomContentView();
    }

    public static class Params {
        /**
         * use this class to setup dialog
         * all params have default values,just config the item which u really need is ok.
         *
         * @param enableTittle use this value to configure tittleBar visibility.
         * @param enableFunctionPanel use this value to configure FunctionPanel visibility.
         * @param enablePositiveButton use this value to configure Positive Button visibility.
         * @param enableNegativeButton use this value to configure Negative Button visibility.
         * @param enableNeutralButton use this value to configure NeutralButton Button visibility.
         * @param enablePageIndicator use this value to configure Page Indicator visibility.
         * @param canceledOnTouchOutside use this value to configure cancel this dialog when touch outside.
         * @param customContentLayoutResID use this value to configure Custom Content Layout ID.
         * @param customLayoutResID use this value to configure CustomLayout ID,Some Custom Manufacture will require a total different
         * layout,so use this id to change the whole dialog outside Layout,but remember should provide all id exist in
         * onyx_custom_alert_dialog.(Future will add id check.)
         * @param customLayoutHeight use this value to configure Custom Layout Height.
         * @param customLayoutWidth use this value to configure Custom Layout Width.
         * @param dialogWidth use this value to configure Dialog Width.
         * @param dialogHeight use this value to configure Dialog Height.
         * @param tittleString use this value to configure Dialog tittle String.
         * @param alertMsgString use this value to configure Dialog message String.
         * @param positiveAction use this value to configure Positive Action,if custom here,u may have to dismiss the dialog in ur action.
         * @param negativeAction use this value to configure Negative Action,if custom here,u may have to dismiss the dialog in ur action.
         * @param neutralAction use this value to configure Neutral Action,if custom here,u may have to dismiss the dialog in ur action.
         * @param customViewAction use this value to configure customView Action,when ur view is load,it would give u the view which u inject,
         * and the page indicator,u can setup action by findViewById in your custom view and do the custom action.
         * @param usePercentageWidth use this flag to configure use percentage width or not,
         * if not,dialog itself would use margin Left&Right in x dp,
         * which defines in values/dimens.xml/onyx_alert_dialog_width_margin.
         * @param isInputTypeMode use this value to position Dialog when Input Method Editor appear
         * @param alertMsgGravity allow outside to control alert msg gravity.
         * @param dialogTheme changeDialogTheme
         * @param alertMsgTextPixelSize use this value to configure alert message text size.
         */
        boolean enableTittle = true;
        boolean enableFunctionPanel = true;
        boolean enablePositiveButton = true;
        boolean enableNegativeButton = true;
        boolean enableNeutralButton = false;
        boolean enableCloseButtonTopRight = false;
        boolean enablePageIndicatorPanel = false;
        boolean enablePageIndicator = false;
        boolean canceledOnTouchOutside = true;
        boolean usePercentageWidth = true;
        boolean isInputTypeMode = false;
        int customContentLayoutResID = -1;
        int customLayoutResID = -1;
        int customLayoutBackgroundResId = -1;
        String neutralButtonText = "";
        String positiveButtonText = "";
        String negativeButtonText = "";
        Point p;
        final int defaultLayoutResID = R.layout.onyx_custom_alert_dialog;

        int customLayoutHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        int customLayoutWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int dialogWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        String tittleString = "";
        String alertMsgString = "";

        int dialogTheme = 0;
        float alertMsgTextPixelSize = -1f;
        int alertMsgGravity = Gravity.CENTER;

        private boolean enableSidebarPosition = true;

        View.OnClickListener positiveAction, negativeAction, neutralAction;
        View.OnClickListener prevPageAction, nextPageAction;
        DialogInterface.OnKeyListener keyAction;
        CustomViewAction customViewAction = new CustomViewAction() {
            @Override
            public void onCreateCustomView(View customView, TextView pageIndicator) {
                Log.i(TAG, "onCreateCustomView");
            }
        };

        public float getAlertMsgTextPixelSize() {
            return alertMsgTextPixelSize;
        }

        public Params setAlertMsgTextPixelSize(float alertMsgTextPixelSize) {
            this.alertMsgTextPixelSize = alertMsgTextPixelSize;
            return this;
        }

        public int getAlertMsgGravity() {
            return alertMsgGravity;
        }

        public Params setAlertMsgGravity(int alertMsgGravity) {
            this.alertMsgGravity = alertMsgGravity;
            return this;
        }

        public boolean isEnableTittle() {
            return enableTittle;
        }

        public Params setEnableTittle(boolean enableTittle) {
            this.enableTittle = enableTittle;
            return this;
        }

        public boolean isEnablePositiveButton() {
            return enablePositiveButton;
        }

        public Params setEnablePositiveButton(boolean enablePositiveButton) {
            this.enablePositiveButton = enablePositiveButton;
            return this;
        }

        public boolean isEnableNegativeButton() {
            return enableNegativeButton;
        }

        public Params setEnableNegativeButton(boolean enableNegativeButton) {
            this.enableNegativeButton = enableNegativeButton;
            return this;
        }

        public int getCustomContentLayoutResID() {
            return customContentLayoutResID;
        }

        public Params setCustomContentLayoutResID(int customContentLayoutResID) {
            this.customContentLayoutResID = customContentLayoutResID;
            return this;
        }

        public int getCustomLayoutBackgroundResId() {
            return customLayoutBackgroundResId;
        }

        public Params setCustomLayoutBackgroundResId(int customBackgroundLayoutResId) {
            this.customLayoutBackgroundResId = customBackgroundLayoutResId;
            return this;
        }

        public String getTittleString() {
            return tittleString;
        }

        public Params setTittleString(String tittleString) {
            this.tittleString = tittleString;
            return this;
        }

        public String getAlertMsgString() {
            return alertMsgString;
        }

        public Params setAlertMsgString(String alertMsgString) {
            this.alertMsgString = alertMsgString;
            return this;
        }

        public View.OnClickListener getNegativeAction() {
            return negativeAction;
        }

        public Params setNegativeAction(View.OnClickListener negativeAction) {
            this.negativeAction = negativeAction;
            return this;
        }

        public View.OnClickListener getPositiveAction() {
            return positiveAction;
        }

        public Params setPositiveAction(View.OnClickListener positiveAction) {
            this.positiveAction = positiveAction;
            return this;
        }

        public View.OnClickListener getPrevPageAction() {
            return prevPageAction;
        }

        public Params setPrevPageAction(View.OnClickListener prevPageAction) {
            this.prevPageAction = prevPageAction;
            return this;
        }

        public View.OnClickListener getNextPageAction() {
            return nextPageAction;
        }

        public Params setNextPageAction(View.OnClickListener nextPageAction) {
            this.nextPageAction = nextPageAction;
            return this;
        }

        public int getCustomLayoutHeight() {
            return customLayoutHeight;
        }

        public Params setCustomLayoutHeight(int customLayoutHeight) {
            this.customLayoutHeight = customLayoutHeight;
            return this;
        }

        public int getCustomLayoutWidth() {
            return customLayoutWidth;
        }

        public Params setCustomLayoutWidth(int customLayoutWidth) {
            this.customLayoutWidth = customLayoutWidth;
            return this;
        }

        public Params setCustomViewAction(CustomViewAction customViewAction) {
            this.customViewAction = customViewAction;
            return this;
        }

        public boolean isEnablePageIndicator() {
            return enablePageIndicator;
        }

        public Params setEnablePageIndicator(boolean enablePageIndicator) {
            this.enablePageIndicator = enablePageIndicator;
            return this;
        }

        public boolean isEnableCloseButtonTopRight() {
            return enableCloseButtonTopRight;
        }

        public Params setEnableCloseButtonTopRight(boolean enable) {
            this.enableCloseButtonTopRight = enable;
            return this;
        }

        public boolean isEnablePageIndicatorPanel() {
            return enablePageIndicatorPanel;
        }

        public Params setEnablePageIndicatorPanel(boolean enable) {
            this.enablePageIndicatorPanel = enable;
            return this;
        }

        public Params setDialogHeight(int dialogHeight) {
            this.dialogHeight = dialogHeight;
            return this;
        }

        public Params setDialogWidth(int dialogWidth) {
            this.dialogWidth = dialogWidth;
            return this;
        }

        public boolean isEnableFunctionPanel() {
            return enableFunctionPanel;
        }

        public Params setEnableFunctionPanel(boolean enableFunctionPanel) {
            this.enableFunctionPanel = enableFunctionPanel;
            return this;
        }

        public boolean isCanceledOnTouchOutside() {
            return canceledOnTouchOutside;
        }

        public Params setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public boolean isUsePercentageWidth() {
            return usePercentageWidth;
        }

        public Params setUsePercentageWidth(boolean usePercentageWidth) {
            this.usePercentageWidth = usePercentageWidth;
            return this;
        }

        public boolean isInputTypeMode() {
            return isInputTypeMode;
        }

        public Params setInputTypeMode(boolean isInputTypeMode) {
            this.isInputTypeMode = isInputTypeMode;
            return this;
        }

        public boolean isEnableNeutralButton() {
            return enableNeutralButton;
        }

        public Params setEnableNeutralButton(boolean enableNeutralButton) {
            this.enableNeutralButton = enableNeutralButton;
            return this;
        }

        public String getNeutralButtonText() {
            return neutralButtonText;
        }

        public Params setNeutralButtonText(String neutralButtonText) {
            this.neutralButtonText = neutralButtonText;
            return this;
        }

        public View.OnClickListener getNeutralAction() {
            return neutralAction;
        }

        public Params setNeutralAction(View.OnClickListener neutralAction) {
            this.neutralAction = neutralAction;
            return this;
        }

        public int getCustomLayoutResID() {
            return customLayoutResID;
        }

        public Params setCustomLayoutResID(int customLayoutResID) {
            this.customLayoutResID = customLayoutResID;
            return this;
        }

        public String getPositiveButtonText() {
            return positiveButtonText;
        }

        public Params setPositiveButtonText(String positiveButtonText) {
            this.positiveButtonText = positiveButtonText;
            return this;
        }

        public String getNegativeButtonText() {
            return negativeButtonText;
        }

        public Params setNegativeButtonText(String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            return this;
        }

        public DialogInterface.OnKeyListener getKeyAction(){
            return keyAction;
        }

        public Params setKeyAction(DialogInterface.OnKeyListener keyAction) {
            this.keyAction = keyAction;
            return this;
        }

        public int getDialogTheme() {
            return dialogTheme;
        }

        public Params setDialogTheme(int dialogTheme) {
            this.dialogTheme = dialogTheme;
            return this;
        }

        public Point getDialogPosition() {
            return p;
        }

        public Params setDialogPosition(Point p) {
            this.p = p;
            return this;
        }

        public boolean isEnableSidebarPosition() {
            return enableSidebarPosition;
        }

        public Params setEnableSidebarPosition(boolean enableSidebarPosition) {
            this.enableSidebarPosition = enableSidebarPosition;
            return this;
        }
    }

    public void setAlertMsg(String alertMsg) {
        alertMessageView.setVisibility(View.VISIBLE);
        alertMessageView.setText(alertMsg);
    }

    public void setEnableFunctionPanel(boolean enable) {
        functionPanelLayout.setVisibility(enable ? View.VISIBLE : View.GONE);
        bottomDivider.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    public void setNegativeButton(boolean enable, String text, View.OnClickListener listener) {
        setButtonView(negativeButton, enable, text, listener);
        if (!(getParams().isEnablePositiveButton() && enable)) {
            functionButtonDividerLine.setVisibility(View.GONE);
        }
    }

    public void setPositiveButton(boolean enable, String text, View.OnClickListener listener) {
        setButtonView(positiveButton, enable, text, listener);
        if (!(getParams().isEnableNegativeButton() && enable)) {
            functionButtonDividerLine.setVisibility(View.GONE);
        }
    }

    public void setNeutralButton(boolean enable, String text, View.OnClickListener listener) {
        setButtonView(neutralButton, enable, text, listener);
        btnNeutralDivider.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    private void setButtonView(Button button, boolean enable, String text, View.OnClickListener listener) {
        if (!enable) {
            button.setVisibility(View.GONE);
            return;
        }
        button.setVisibility(View.VISIBLE);
        if (StringUtils.isNotBlank(text)) {
            button.setText(text);
        }
        if (listener != null) {
            button.setOnClickListener(listener);
        }
    }
}