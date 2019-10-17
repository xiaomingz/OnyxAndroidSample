package com.onyx.android.sample;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhilun on 2019/10/16.
 */
public class NoteDemoActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_OPEN_NOTE = 100;
    private static final String KEY_OPEN_NOTE_BEAN_JSON = "OPEN_NOTE_BEAN_JSON";
    private static final String OPEN_NOTE_BEAN = "OPEN_NOTE_BEAN";
    private static final String NOTE_PACKAGE_NAME = "com.onyx.android.note";
    private static final String SCRIBBLE_ACTIVITY_CLASS_PATH = "com.onyx.android.note.note.ui.ScribbleActivity";

    @Bind(R.id.textView_content)
    public TextView textViewContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_demo);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_create_note)
    public void createNewNote() {
        Intent intent = new Intent();
        intent.putExtra(OPEN_NOTE_BEAN, JSON.toJSONString(new OpenNoteBean().setTitle("Note").setJumpBackToNote(false)));
        ComponentName comp = new ComponentName(NOTE_PACKAGE_NAME, SCRIBBLE_ACTIVITY_CLASS_PATH);
        intent.setComponent(comp);
        startActivityForResult(intent, REQUEST_CODE_OPEN_NOTE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode && REQUEST_CODE_OPEN_NOTE == requestCode) {
            String json = data.getStringExtra(KEY_OPEN_NOTE_BEAN_JSON);
            try {
                OpenNoteBean openNoteBean = JSON.parseObject(json, OpenNoteBean.class);
                textViewContent.setText(openNoteBean.toString());
            } catch (Exception e) {
                textViewContent.setText("error occurs, please check log for details");
                e.printStackTrace();
            }
        }
    }

    public static class OpenNoteBean {
        public String documentId;
        public String parentUniqueId;
        public String title;
        public int associationType = 0;
        public String associationId;
        public boolean jumpBackToNote = true;

        public OpenNoteBean setDocumentId(String documentId) {
            this.documentId = documentId;
            return this;
        }

        public OpenNoteBean setParentUniqueId(String parentUniqueId) {
            this.parentUniqueId = parentUniqueId;
            return this;
        }

        public OpenNoteBean setTitle(String title) {
            this.title = title;
            return this;
        }

        public OpenNoteBean setAssociationType(int associationType) {
            this.associationType = associationType;
            return this;
        }

        public OpenNoteBean setAssociationId(String associationId) {
            this.associationId = associationId;
            return this;
        }

        public OpenNoteBean setJumpBackToNote(boolean jumpBackToNote) {
            this.jumpBackToNote = jumpBackToNote;
            return this;
        }

        @Override
        public String toString() {
            return "OpenNoteBean{" +
                    "documentId='" + documentId + '\'' +
                    ", parentUniqueId='" + parentUniqueId + '\'' +
                    ", title='" + title + '\'' +
                    ", associationType=" + associationType +
                    ", associationId='" + associationId + '\'' +
                    ", jumpBackToNote=" + jumpBackToNote +
                    '}';
        }
    }
}
