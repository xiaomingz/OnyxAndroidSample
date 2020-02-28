package com.onyx.android.sdk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.onyx.android.sdk.device.EnvironmentUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DefaultLogEntry {

    public static final SimpleDateFormat DATE_FORMAT_YYYY_MM_DD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());

    private static DefaultLogEntry sInstance;

    public static final String COMMAND_LOGCAT = "/system/bin/logcat -b system -b main ";
    public static final String COMMAND_LOGCAT_ALL = "/system/bin/logcat -b all ";
    public static final String COMMAND_LOGCAT_KERNEL = "/system/bin/logcat -d -b kernel ";
    public static final String COMMAND_SU = "su ";
    public static final String COMMAND_CAT = "/system/bin/cat ";
    public static final String COMMAND_LS = "/system/bin/ls ";
    public static final String COMMAND_ECHO = "/system/bin/echo ";
    public static final String COMMAND_BIN_SH = "/system/bin/sh ";
    public static final String COMMAND_DUMPSYS = "/system/bin/dumpsys ";
    public static final String COMMAND_DMSG = "/system/bin/dmesg ";

    public static final String LOGCAT_GET_NOTE = "-d ";
    public static final String LOGCAT_CLEAR_NOTE = "-c ";
    public static final String LOGCAT_FORMAT_SET = "-v ";
    public static final String TAG_FORMAT_TAG = "tag ";
    public static final String TAG_FORMAT_TIME = "time ";

    public static final String DIR_SDCARD = EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath();
    public static final String DIR_PROC = "/proc/";
    public static final String VERSION = "version";
    public static final String LAST_KMSG = "last_kmsg";

    public static final String SEPARATOR_STAR = "\r\n***********************************\r\n\r\n";

    public static final String POWER = "power";
    public static final String BATTERY = "battery";
    public static final String BATTERY_STATS = "batterystats";
    public static final String ALARM = "alarm";
    private static final String DATA_LOG_OUTPUT_DIR = "/data/local/log/";
    private static final String SHUTDOWN_LOGCAT_OUTPUT_PATH = DATA_LOG_OUTPUT_DIR + "last_logcat";
    private static final String SHUTDOWN_DMESG_OUTPUT_PATH = DATA_LOG_OUTPUT_DIR + "last_dmesg";
    private static final String STAGE_LOGCAT_OUTPUT_PATH = DATA_LOG_OUTPUT_DIR + "stage_logcat";
    private static final String STAGE_DMESG_OUTPUT_PATH = DATA_LOG_OUTPUT_DIR + "stage_dmesg";

    public static final String PROGRAM_EXEC_CAT_LINUX_VERSION = COMMAND_CAT + DIR_PROC + VERSION;
    public static final String PROGRAM_EXEC_CAT_LINUX_LAST_KMSG = COMMAND_CAT + DIR_PROC + LAST_KMSG;
    public static final String PROGRAM_EXEC_CAT_LINUX_DMSG = COMMAND_DMSG;
    public static final String PROGRAM_EXEC_LS_SDCARD = COMMAND_LS + DIR_SDCARD;

    public static final String PROGRAM_EXEC_DUMPSYS_ALL = COMMAND_DUMPSYS;
    public static final String PROGRAM_EXEC_DUMPSYS_POWER = COMMAND_DUMPSYS + POWER;
    public static final String PROGRAM_EXEC_DUMPSYS_BATTERY = COMMAND_DUMPSYS + BATTERY;
    public static final String PROGRAM_EXEC_DUMPSYS_BATTERY_STATS = COMMAND_DUMPSYS + BATTERY_STATS;
    public static final String PROGRAM_EXEC_DUMPSYS_ALARM = COMMAND_DUMPSYS + ALARM;

    public static final String PROGRAM_EXEC_WAKEUP_REASON_SUSPEND_HISTORY = COMMAND_CAT + "/sys/kernel/wakeup_reasons/suspend_history";
    public static final String PROGRAM_EXEC_CAT_SHUTDOWN_LOG_LOGCAT = COMMAND_CAT + SHUTDOWN_LOGCAT_OUTPUT_PATH;
    public static final String PROGRAM_EXEC_CAT_SHUTDOWN_LOG_DMESG = COMMAND_CAT + SHUTDOWN_DMESG_OUTPUT_PATH;
    public static final String PROGRAM_EXEC_CAT_STAGE_LOG_LOGCAT = COMMAND_CAT + STAGE_LOGCAT_OUTPUT_PATH;
    public static final String PROGRAM_EXEC_CAT_STAGE_LOG_DMESG = COMMAND_CAT + STAGE_DMESG_OUTPUT_PATH;

    public static final String ENCODING_TYPE = "utf-8";
    public static final String OUTPUT_FILE_ZIP_PREFIX = "feedback_";
    public static final String OUTPUT_FILE_LOGCAT_PREFIX = "logcat_";
    public static final String OUTPUT_FILE_KERNEL_PREFIX = "kernel_";
    public static final String OUTPUT_FILE_DUMPSYS_PREFIX = "dumpsys_";
    public static final String OUTPUT_FILE_DUMPSYS_SPECIFY_PREFIX = "dumpsys_specify_";
    public static final String OUTPUT_FILE_WAKEUP_REASONS_PREFIX = "wakeup_reasons_";
    public static final String OUTPUT_FILE_KMSG_PREFIX = "kmsg_";
    public static final String OUTPUT_FILE_ANR_PREFIX = "anr_";
    public static final String OUTPUT_FILE_TXT_EXTENSION = ".txt";
    public static final String OUTPUT_FILE_ZIP_EXTENSION = ".zip";
    protected static final String OUTPUT_FILE_APK_INFO_PREFIX = "appsInfo_";
    public static final String OUTPUT_FILE_SHUTDOWN_LOGCAT_PREFIX = "shutdown_logcat_";
    public static final String OUTPUT_FILE_SHUTDOWN_DMESG_PREFIX = "shutdown_dmesg_";
    public static final String OUTPUT_FILE_STAGE_LOGCAT_PREFIX = "stage_logcat_";
    public static final String OUTPUT_FILE_STAGE_DMESG_PREFIX = "stage_dmesg_";

    public static final String PROGRAM_EXEC_LOGCAT_GET_NOTE = COMMAND_LOGCAT + LOGCAT_GET_NOTE
            + LOGCAT_FORMAT_SET + TAG_FORMAT_TAG
            + LOGCAT_FORMAT_SET + TAG_FORMAT_TIME;

    public static final String PROGRAM_EXEC_LOGCAT_CLEAR_NOTE = COMMAND_LOGCAT + LOGCAT_CLEAR_NOTE;

    public static final String ANR_TRACES_PATH = "/data/anr/traces.txt";

    public static final String PROGRAM_EXEC_CAT_ANR = COMMAND_CAT + ANR_TRACES_PATH;

    protected String[] commandLogcatSet = new String[]{
            PROGRAM_EXEC_LOGCAT_GET_NOTE,
            PROGRAM_EXEC_CAT_LINUX_VERSION,
    };

    protected String[] commandKernelSet = new String[]{
            PROGRAM_EXEC_CAT_LINUX_VERSION,
            PROGRAM_EXEC_CAT_LINUX_DMSG
    };

    protected String[] commandClearNoteSet = new String[]{
            PROGRAM_EXEC_LOGCAT_CLEAR_NOTE,
    };

    protected String[] commandDumpsysSet = new String[]{
            PROGRAM_EXEC_DUMPSYS_ALL
    };

    protected String[] commandDumpsysSpecifySet = new String[]{
            PROGRAM_EXEC_DUMPSYS_POWER,
            PROGRAM_EXEC_DUMPSYS_BATTERY,
            PROGRAM_EXEC_DUMPSYS_BATTERY_STATS,
            PROGRAM_EXEC_DUMPSYS_ALARM
    };

    protected String[] commandWakeupReasonSet = new String[]{
            PROGRAM_EXEC_WAKEUP_REASON_SUSPEND_HISTORY
    };

    protected String[] commandKmsgSet = new String[]{
            PROGRAM_EXEC_CAT_LINUX_LAST_KMSG
    };

    public String[] commandANRSet = new String[]{
            PROGRAM_EXEC_CAT_ANR
    };

    protected String[] commandShutdownLogLogcat = new String[]{
            PROGRAM_EXEC_CAT_SHUTDOWN_LOG_LOGCAT
    };

    protected String[] commandShutdownLogDmesg = new String[]{
            PROGRAM_EXEC_CAT_SHUTDOWN_LOG_DMESG
    };

    protected String[] commandStageLogLogcat = new String[]{
            PROGRAM_EXEC_CAT_STAGE_LOG_LOGCAT
    };

    protected String[] commandStageLogDmesg = new String[]{
            PROGRAM_EXEC_CAT_STAGE_LOG_DMESG
    };

    public File generateFeedBackFile(final Context context, File... argFiles) {
        File zipFile = null;
        File[] wantCompressFiles = null;
        try {
            File logcatFile = generateLogcatFile(context);
            File kernelFile = generateKernelFile(context);
            File dumpsysFile = generateDumpsysFile(context);
            File dumpsysSpecifyFile = generateDumpsysSpecifyFile(context);
            File wakeupReasonsFile = generateWakeupReasonsFile(context);
            File kmsgFile = generateKmsgFile(context);
            File anrFile = generateAnrFile(context);
            File appsFile = generateAppsInfoFile(context, context.getFilesDir().getAbsolutePath(), OUTPUT_FILE_APK_INFO_PREFIX);
            File shutdownLogcatFile = generateShutdownLogcatFile(context);
            File shutdownDmesgFile = generateShutdownDmesgFile(context);
            File stageLogcatFile = generateStageLogcatFile(context);
            File stageDmesgFile = generateStageDmesgFile(context);

            List<File> fileList = new ArrayList<>();
            safeAdd(fileList, logcatFile);
            safeAdd(fileList, kernelFile);
            safeAdd(fileList, dumpsysFile);
            safeAdd(fileList, dumpsysSpecifyFile);
            safeAdd(fileList, wakeupReasonsFile);
            safeAdd(fileList, kmsgFile);
            safeAdd(fileList, anrFile);
            safeAdd(fileList, appsFile);
            safeAdd(fileList, shutdownLogcatFile);
            safeAdd(fileList, shutdownDmesgFile);
            safeAdd(fileList, stageLogcatFile);
            safeAdd(fileList, stageDmesgFile);
            if (argFiles != null && argFiles.length > 0) {
                for (File file : argFiles) {
                    if (file != null && file.exists()) {
                        fileList.add(file);
                    }
                }
            }
            wantCompressFiles = new File[fileList.size()];
            fileList.toArray(wantCompressFiles);
            zipFile = new File(context.getFilesDir(), getFileNameBasedOnDate(OUTPUT_FILE_ZIP_PREFIX, OUTPUT_FILE_ZIP_EXTENSION));
            boolean isSuccess = ZipUtils.compress(wantCompressFiles, zipFile);
            if (!isSuccess) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            deleteFiles(wantCompressFiles);
        }
        return zipFile;
    }

    private void safeAdd(List<File> list, File file) {
        if (list != null && file != null) {
            list.add(file);
        }
    }

    protected File generateLogcatFile(Context context) throws IOException {
        return generateExceptionFile(context, commandLogcatSet, OUTPUT_FILE_LOGCAT_PREFIX);
    }

    protected File generateKernelFile(Context context) throws IOException {
        return generateExceptionFile(context, commandKernelSet, OUTPUT_FILE_KERNEL_PREFIX);
    }

    protected File generateDumpsysFile(Context context) throws IOException {
        return generateExceptionFile(context, commandDumpsysSet, OUTPUT_FILE_DUMPSYS_PREFIX);
    }

    protected File generateDumpsysSpecifyFile(Context context) throws IOException {
        return generateExceptionFile(context, commandDumpsysSpecifySet, OUTPUT_FILE_DUMPSYS_SPECIFY_PREFIX);
    }

    protected File generateWakeupReasonsFile(Context context) throws IOException {
        return generateExceptionFile(context, commandWakeupReasonSet, OUTPUT_FILE_WAKEUP_REASONS_PREFIX);
    }

    protected File generateKmsgFile(Context context) throws IOException {
        return generateExceptionFile(context, commandKmsgSet, OUTPUT_FILE_KMSG_PREFIX);
    }

    protected File generateAnrFile(Context context) throws IOException {
        return generateExceptionFile(context, commandANRSet, OUTPUT_FILE_ANR_PREFIX);
    }

    protected File generateShutdownLogcatFile(Context context) throws IOException {
        return generateExceptionFile(context, commandShutdownLogLogcat, OUTPUT_FILE_SHUTDOWN_LOGCAT_PREFIX);
    }

    protected File generateShutdownDmesgFile(Context context) throws IOException {
        return generateExceptionFile(context, commandShutdownLogDmesg, OUTPUT_FILE_SHUTDOWN_DMESG_PREFIX);
    }

    protected File generateStageLogcatFile(Context context) throws IOException {
        return generateExceptionFile(context, commandStageLogLogcat, OUTPUT_FILE_STAGE_LOGCAT_PREFIX);
    }

    protected File generateStageDmesgFile(Context context) throws IOException {
        return generateExceptionFile(context, commandStageLogDmesg, OUTPUT_FILE_STAGE_DMESG_PREFIX);
    }

    public static File generateExceptionFile(Context context, String[] commandSet, String filePrefix) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (String command : commandSet) {
            ShellUtils.CommandResult commandResult = ShellUtils.execCommand(command, false, true);
            if (commandResult.successMsg != null) {
                stringBuilder.append(commandResult.successMsg);
            }
            stringBuilder.append(SEPARATOR_STAR);
        }
        File logcatFile = new File(context.getFilesDir(), getFileNameBasedOnDate(filePrefix, OUTPUT_FILE_TXT_EXTENSION));
        FileOutputStream out = new FileOutputStream(logcatFile);
        out.write(stringBuilder.toString().getBytes(ENCODING_TYPE));
        out.flush();
        out.close();
        Log.i(filePrefix + "File success:" + logcatFile.getName(), "" + logcatFile.length());
        return logcatFile;
    }

    protected static String getFileNameBasedOnDate(String prefix, String suffix) {
        return prefix + DATE_FORMAT_YYYY_MM_DD_HHMMSS.format(new Date()) + suffix;
    }

    protected static File generateAppsInfoFile(Context context, String fileDirPath, String filePrefix) throws IOException {
        File logcatFile = null;
        PrintWriter out = null;
        try {
            logcatFile = new File(fileDirPath, getFileNameBasedOnDate(filePrefix, OUTPUT_FILE_TXT_EXTENSION));
            out = new PrintWriter(logcatFile);
            List<PackageInfo> allInstalledPackageList = context.getPackageManager().getInstalledPackages(0);
            for (PackageInfo packageInfo : allInstalledPackageList) {
                out.println("appName:" + packageInfo.applicationInfo.loadLabel(context.getPackageManager()));
                out.println("packageName:" + packageInfo.packageName);
                out.println("versionCode:" + packageInfo.versionCode);
                out.println("versionName:" + packageInfo.versionName);
                out.println(SEPARATOR_STAR);
            }
            out.flush();
        } catch (Exception ignored) {
        } finally {
            FileUtils.closeQuietly(out);
        }
        return logcatFile;
    }

    public static void deleteFiles(File[] files) {
        if (files == null || files.length <= 0) {
            return;
        }
        for (File file : files) {
            if (file != null && file.exists()) {
                FileUtils.deleteFile(file, true);
            }
        }
    }

    public static DefaultLogEntry createLog() {
        if (sInstance == null) {
            sInstance = new DefaultLogEntry();
        }
        return sInstance;
    }
}
