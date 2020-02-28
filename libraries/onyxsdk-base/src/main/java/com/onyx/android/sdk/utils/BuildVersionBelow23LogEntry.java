package com.onyx.android.sdk.utils;

public class BuildVersionBelow23LogEntry extends DefaultLogEntry {

    private static BuildVersionBelow23LogEntry sInstance;

    public static final String PROGRAM_EXEC_LOGCAT_GET_NOTE = COMMAND_LOGCAT + LOGCAT_GET_NOTE
            + LOGCAT_FORMAT_SET + TAG_FORMAT_TAG
            + LOGCAT_FORMAT_SET + TAG_FORMAT_TIME;

    public static final String PROGRAM_EXEC_LOGCAT_CLEAR_NOTE = COMMAND_LOGCAT + LOGCAT_CLEAR_NOTE;

    public static final String ANR_TRACES_PATH = "/data/anr/traces.txt";

    public static final String PROGRAM_EXEC_CAT_ANR = COMMAND_CAT + ANR_TRACES_PATH;

    public static BuildVersionBelow23LogEntry createLog() {
        if (sInstance == null) {
            sInstance = new BuildVersionBelow23LogEntry();
        }
        return sInstance;
    }

    private BuildVersionBelow23LogEntry() {
        commandLogcatSet = new String[]{
                PROGRAM_EXEC_LOGCAT_GET_NOTE,
                PROGRAM_EXEC_CAT_LINUX_VERSION,
        };

        commandANRSet = new String[]{
                PROGRAM_EXEC_CAT_ANR
        };
    }
}
