/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onyx.calculator;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class Calculator extends Activity implements PanelSwitcher.Listener, Logic.Listener,
        OnClickListener, PopupMenu.OnMenuItemClickListener {
    EventListener mListener = new EventListener();
    private CalculatorDisplay mDisplay;
    private Persist mPersist;
    private History mHistory;
    private Logic mLogic;
    private PageRecyclerView mPager;
    private View mClearButton;
    private View mBackspaceButton;
    private View mOverflowMenuButton;

    static final int BASIC_PANEL = 0;
    static final int ADVANCED_PANEL = 1;

    private static final String LOG_TAG = "Calculator";
    private static final boolean DEBUG = false;
    private static final boolean LOG_ENABLED = false;
    private static final String STATE_CURRENT_VIEW = "state-current-view";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Disable IME for this application
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        setContentView(R.layout.main);
        mPager = (PageRecyclerView) findViewById(R.id.panelswitch);
        if (mPager != null) {
            mPager.setLayoutManager(new DisableScrollLinearManager(this));
            mPager.setPageTurningCycled(false);
            mPager.setAdapter(new CalculatorAdapter(mPager));
        } else {
            // Single page UI
            final TypedArray buttons = getResources().obtainTypedArray(R.array.buttons);
            for (int i = 0; i < buttons.length(); i++) {
                setOnClickListener(null, buttons.getResourceId(i, 0));
            }
            buttons.recycle();
        }

        if (mClearButton == null) {
            mClearButton = findViewById(R.id.clear);
            mClearButton.setOnClickListener(mListener);
            mClearButton.setOnLongClickListener(mListener);
        }
        if (mBackspaceButton == null) {
            mBackspaceButton = findViewById(R.id.del);
            mBackspaceButton.setOnClickListener(mListener);
            mBackspaceButton.setOnLongClickListener(mListener);
        }

        mPersist = new Persist(this);
        mPersist.load();

        mHistory = mPersist.history;

        mDisplay = (CalculatorDisplay) findViewById(R.id.display);

        mLogic = new Logic(this, mHistory, mDisplay);
        mLogic.setListener(this);

        mLogic.setDeleteMode(mPersist.getDeleteMode());
        mLogic.setLineLength(mDisplay.getMaxDigits());

        HistoryAdapter historyAdapter = new HistoryAdapter(this, mHistory, mLogic);
        mHistory.setObserver(historyAdapter);

        if (mPager != null) {
            mPager.scrollToPosition(state == null ? 0 : state.getInt(STATE_CURRENT_VIEW, 0));
            mPager.setCurrentPage(state == null ? 0 : state.getInt(STATE_CURRENT_VIEW, 0));
        }

        mListener.setHandler(mLogic, mPager);
        mDisplay.setOnKeyListener(mListener);

        if (!ViewConfiguration.get(this).hasPermanentMenuKey()) {
            createFakeMenu();
        }

        mLogic.resumeWithHistory();
        updateDeleteMode();
    }

    private void updateDeleteMode() {
        if (mLogic.getDeleteMode() == Logic.DELETE_MODE_BACKSPACE) {
            mClearButton.setVisibility(View.GONE);
            mBackspaceButton.setVisibility(View.VISIBLE);
        } else {
            mClearButton.setVisibility(View.VISIBLE);
            mBackspaceButton.setVisibility(View.GONE);
        }
    }

    void setOnClickListener(View root, int id) {
        final View target = root != null ? root.findViewById(id) : findViewById(id);
        target.setOnClickListener(mListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.basic).setVisible(false);
        menu.findItem(R.id.advanced).setVisible(false);
        return true;
    }


    private void createFakeMenu() {
        mOverflowMenuButton = findViewById(R.id.overflow_menu);
        if (mOverflowMenuButton != null) {
            mOverflowMenuButton.setVisibility(View.VISIBLE);
            mOverflowMenuButton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.overflow_menu:
                PopupMenu menu = constructPopupMenu();
                if (menu != null) {
                    menu.show();
                }
                break;
        }
    }

    private PopupMenu constructPopupMenu() {
        final PopupMenu popupMenu = new PopupMenu(this, mOverflowMenuButton);
        mOverflowMenuButton.setOnTouchListener(popupMenu.getDragToOpenListener());
        final Menu menu = popupMenu.getMenu();
        popupMenu.inflate(R.menu.menu);
        popupMenu.setOnMenuItemClickListener(this);
        onPrepareOptionsMenu(menu);
        return popupMenu;
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return onOptionsItemSelected(item);
    }

    private boolean getBasicVisibility() {
        if (mPager != null) {
            RecyclerView.LayoutManager layoutManager = mPager.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                return mPager != null && firstVisibleItemPosition == BASIC_PANEL;
            }
            return mPager != null;
        }
        return false;
    }

    private boolean getAdvancedVisibility() {
        if (mPager != null) {
            RecyclerView.LayoutManager layoutManager = mPager.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                return mPager != null && firstVisibleItemPosition == ADVANCED_PANEL;
            }
            return mPager != null;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_history:
                mHistory.clear();
                mLogic.onClear();
                break;

            case R.id.basic:
                if (!getBasicVisibility() && mPager != null) {
                    mPager.scrollToPosition(BASIC_PANEL);
                    mPager.setCurrentPage(BASIC_PANEL);
                }
                break;

            case R.id.advanced:
                if (!getAdvancedVisibility() && mPager != null) {
                    mPager.scrollToPosition(ADVANCED_PANEL);
                    mPager.setCurrentPage(ADVANCED_PANEL);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (mPager != null) {
            RecyclerView.LayoutManager layoutManager = mPager.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                state.putInt(STATE_CURRENT_VIEW, firstVisibleItemPosition);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mLogic.updateHistory();
        mPersist.setDeleteMode(mLogic.getDeleteMode());
        mPersist.save();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK && getAdvancedVisibility()
                && mPager != null) {
            mPager.scrollToPosition(BASIC_PANEL);
            mPager.setCurrentPage(BASIC_PANEL);
            return true;
        } else {
            return super.onKeyDown(keyCode, keyEvent);
        }
    }

    static void log(String message) {
        if (LOG_ENABLED) {
            Log.v(LOG_TAG, message);
        }
    }

    @Override
    public void onChange() {
        invalidateOptionsMenu();
    }

    @Override
    public void onDeleteModeChange() {
        updateDeleteMode();
    }

    class CalculatorAdapter extends PageRecyclerView.PageAdapter {
        View mSimplePage;
        View mAdvancedPage;

        public CalculatorAdapter(PageRecyclerView parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View simplePage = inflater.inflate(R.layout.simple_pad, parent, false);
            final View advancedPage = inflater.inflate(R.layout.advanced_pad, parent, false);
            mSimplePage = simplePage;
            mAdvancedPage = advancedPage;

            final Resources res = getResources();
            final TypedArray simpleButtons = res.obtainTypedArray(R.array.simple_buttons);
            for (int i = 0; i < simpleButtons.length(); i++) {
                setOnClickListener(simplePage, simpleButtons.getResourceId(i, 0));
            }
            simpleButtons.recycle();

            final TypedArray advancedButtons = res.obtainTypedArray(R.array.advanced_buttons);
            for (int i = 0; i < advancedButtons.length(); i++) {
                setOnClickListener(advancedPage, advancedButtons.getResourceId(i, 0));
            }
            advancedButtons.recycle();

            final View clearButton = simplePage.findViewById(R.id.clear);
            if (clearButton != null) {
                mClearButton = clearButton;
            }

            final View backspaceButton = simplePage.findViewById(R.id.del);
            if (backspaceButton != null) {
                mBackspaceButton = backspaceButton;
            }
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public int getDataCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return BASIC_PANEL;
            } else {
                return ADVANCED_PANEL;
            }
        }

        @Override
        public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == BASIC_PANEL) {
                SimpleViewHolder holder = new SimpleViewHolder(mSimplePage);
                return holder;
            } else {
                AdvancedHolder holder = new AdvancedHolder(mAdvancedPage);
                return holder;
            }
        }

        @Override
        public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == 0) {
                SimpleViewHolder simpleViewHolder = (SimpleViewHolder) holder;
            } else {
                AdvancedHolder advancedHolder = (AdvancedHolder) holder;
            }
        }

        class SimpleViewHolder extends PageRecyclerView.ViewHolder {

            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }

        class AdvancedHolder extends PageRecyclerView.ViewHolder {

            public AdvancedHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
