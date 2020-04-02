package com.android.dialer.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.dialer.R;
import com.android.dialer.util.OnyxEngineeringModeUtil;

/**
 * Created by TonyXie on 2020-03-18
 */
public class CheckPsWDialog extends Dialog {
    private EditText editTextView;
    private CheckPswListener listener;

    public CheckPsWDialog(@NonNull Context context) {
        super(context, R.style.CustomDialogNoBackground);
        initView(context);
    }

    public CheckPsWDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    protected CheckPsWDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    private void initView(Context context) {
        setTitle(R.string.check_password);
        setCanceledOnTouchOutside(false);
        View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_check_psw, null);
        addContentView(rootView,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editTextView = rootView.findViewById(R.id.edit_text_view);
        View btConfirm = rootView.findViewById(R.id.bt_confirm);
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.check(OnyxEngineeringModeUtil.ENTER_DIAG_MODE_PSW.equals(editTextView.getText().toString()));
                }
            }
        });
        View btCancel = rootView.findViewById(R.id.bt_cancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface CheckPswListener {
        void check(boolean success);
    }

    public void setCheckPswListener(CheckPswListener listener) {
        this.listener = listener;
    }
}
