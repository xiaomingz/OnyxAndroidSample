package com.onyx.android.sdk.utils;

public class BuildVersionOver28LogEntry extends DefaultLogEntry {

    private static BuildVersionOver28LogEntry sInstance;

    public static final String PROGRAM_EXEC_LOGCAT_GET_NOTE = COMMAND_LOGCAT_ALL + LOGCAT_GET_NOTE
            + LOGCAT_FORMAT_SET + TAG_FORMAT_TAG
            + LOGCAT_FORMAT_SET + TAG_FORMAT_TIME;

    public static final String PROGRAM_EXEC_LOGCAT_CLEAR_NOTE = COMMAND_LOGCAT_ALL + LOGCAT_CLEAR_NOTE;

    public static final String ANR_TRACES_PATH = "/data/anr/anr_*";

    public static final String PROGRAM_EXEC_CAT_ANR = COMMAND_CAT + ANR_TRACES_PATH;

    public static BuildVersionOver28LogEntry createLog() {
        if (sInstance == null) {
            sInstance = new BuildVersionOver28LogEntry();
        }
        return sInstance;
    }

    private BuildVersionOver28LogEntry() {
        commandLogcatSet = new String[]{
                PROGRAM_EXEC_LOGCAT_GET_NOTE,
                PROGRAM_EXEC_CAT_LINUX_VERSION,
        };

        commandANRSet = new String[]{
                PROGRAM_EXEC_CAT_ANR
        };
    }
}
