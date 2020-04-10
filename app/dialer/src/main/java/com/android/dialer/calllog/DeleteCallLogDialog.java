package com.android.dialer.calllog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog.Calls;

import com.android.dialer.R;
import com.android.dialer.calllog.inteface.DeleteCallHistoryListener;
import com.android.dialer.service.CachedNumberLookupService;
import com.android.dialerbind.ObjectFactory;

import android.util.SparseArray;

import androidx.annotation.NonNull;

public class DeleteCallLogDialog extends DialogFragment {
    private SparseArray<Long> itemIds;
    private static final CachedNumberLookupService mCachedNumberLookupService =
            ObjectFactory.newCachedNumberLookupService();

    public void setItemIds(SparseArray<Long> itemIds) {
        this.itemIds = itemIds;
    }

    private DeleteCallHistoryListener listener;

    public static void show(FragmentManager fragmentManager, @NonNull DeleteCallHistoryListener listener, SparseArray<Long> itemIds) {
        DeleteCallLogDialog dialog = new DeleteCallLogDialog();
        dialog.setItemIds(itemIds);
        dialog.listener = listener;
        dialog.show(fragmentManager, "deleteCallLogByIds");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ContentResolver resolver = getActivity().getContentResolver();
        final Context context = getActivity().getApplicationContext();
        final OnClickListener okListener =
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog progressDialog =
                                ProgressDialog.show(
                                        getActivity(), getString(R.string.clearCallLogProgress_title), "", true, false);
                        progressDialog.setOwnerActivity(getActivity());
                        final AsyncTask<Void, Void, Void> task =
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        for (int i = 0; i < itemIds.size(); i++) {
                                            Long id = itemIds.valueAt(i);
                                            resolver.delete(Calls.CONTENT_URI, Calls._ID + "=?", new String[]{String.valueOf(id)});
                                        }

                                        if (mCachedNumberLookupService != null) {
                                            mCachedNumberLookupService.clearAllCacheEntries(context);
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void result) {
                                        final Activity activity = progressDialog.getOwnerActivity();

                                        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                                            return;
                                        }

                                        listener.callHistoryDeleted();
                                        if (progressDialog != null && progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                    }
                                };
                        progressDialog.show();
                        task.execute();
                    }
                };
        return new AlertDialog.Builder(getActivity(), R.style.CustomDialogNoBackground)
                .setTitle(R.string.deleteCallLogConfirmation_title)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setMessage(R.string.deleteCallLogConfirmation)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, okListener)
                .setCancelable(true)
                .create();
    }
}
