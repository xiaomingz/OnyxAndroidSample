/**
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.onyx.latinime.dictionarypack;

import com.onyx.latinime.R;
import com.onyx.latinime.latin.utils.FragmentUtils;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

/**
 * Preference screen.
 */
public final class DictionarySettingsActivity extends PreferenceActivity {
    private static final String DEFAULT_FRAGMENT = DictionarySettingsFragment.class.getName();
    LinearLayout headers;
    private Toolbar bar;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        adjustContentLayout();
    }

    @Override
    public Intent getIntent() {
        final Intent modIntent = new Intent(super.getIntent());
        modIntent.putExtra(EXTRA_SHOW_FRAGMENT, DEFAULT_FRAGMENT);
        modIntent.putExtra(EXTRA_NO_HEADERS, true);
        // Important note : the original intent should contain a String extra with the key
        // DictionarySettingsFragment.DICT_SETTINGS_FRAGMENT_CLIENT_ID_ARGUMENT so that the
        // fragment can know who the client is.
        return modIntent;
    }

    // TODO: Uncomment the override annotation once we start using SDK version 19.
    // @Override
    public boolean isValidFragment(String fragmentName) {
        return FragmentUtils.isValidFragment(fragmentName);
    }

    private void initToolbar() {
        headers = (LinearLayout) findViewById(android.R.id.list).getParent();
        headers.setPadding(0, 0, 0, 0);
        LinearLayout root = (LinearLayout)headers.getParent().getParent();
        bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0);
        toolbarTitle = bar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.configure_dictionaries_title);
        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void adjustContentLayout() {
        LinearLayout contentLayout = (LinearLayout) headers.getParent();
        int paddingLeftRight = getResources().getDimensionPixelSize(R.dimen.content_layout_padding);
        contentLayout.setPadding(paddingLeftRight, 0, paddingLeftRight, 0);
    }
}
