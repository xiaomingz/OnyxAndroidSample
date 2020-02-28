package com.onyx.android.sdk.device;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.utils.Debug;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by zhuzeng on 10/16/15.
 */
class DeviceFileUtils {

    public static boolean saveContentToFile(String content, File fileForSave) {
        boolean succeed = true;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileForSave);
            out.write(content.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            succeed = false;
        } finally {
            closeQuietly(out);
        }
        return succeed;
    }

    public static String readContentOfFile(File fileForRead) {
        FileInputStream in = null;
        InputStreamReader reader = null;
        BufferedReader breader = null;
        try {
            in = new FileInputStream(fileForRead);
            reader = new InputStreamReader(in, "utf-8");
            breader = new BufferedReader(reader);

            String ls = System.getProperty("line.separator");

            StringBuilder sb = new StringBuilder();
            boolean firstLine = true;
            String line;
            while ((line = breader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                } else {
                    sb.append(ls);
                }
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(breader);
            closeQuietly(reader);
            closeQuietly(in);
        }
        return null;
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateMtpDb(Context context, File file) {
        MediaScannerConnection.scanFile(context,
                new String[]{file.getAbsolutePath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Debug.i(DeviceFileUtils.class, "file " + path
                                + " was scanned successfully: " + uri);
                    }
                });
    }

    @Nullable
    public static String getRealFilePathFromUri(Context context, Uri uri) {
        try {
            String filePath = null;
            if (uri != null) {
                if ("content".equals(uri.getScheme())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        filePath = getRealFilePathFromUriAboveAPI23(context, uri);
                    } else {
                        filePath = getRealFilePathFromUriByContentResolver(context,
                                uri, android.provider.MediaStore.Images.ImageColumns.DATA);
                    }
                } else {
                    filePath = uri.getPath();
                }
            }
            return filePath;
        } catch (Throwable tr) {
            return null;
        }
    }

    private static String getRealFilePathFromUriAboveAPI23(Context context, Uri uri) {
        String filePath;
        if (uri.getAuthority().contains("fileprovider")) {
            filePath = getRealFilePathFromFileProvider(context, uri);
        } else {
            filePath = getRealFilePathFromUriByContentResolver(context, uri, android.provider.MediaStore.Images.ImageColumns.DATA);
        }
        return filePath;
    }

    private static String getRealFilePathFromFileProvider(Context context, Uri uri) {
        String encodePath = null;
        try {
            encodePath = URLDecoder.decode(uri.getEncodedPath(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] split = encodePath.split(File.separator);
        String relativePath = encodePath.substring(("/" + split[1]).length());
        switch (split[1]) {
            case "external":
            case "bluetooth":
                return Device.currentDevice().getExternalStorageDirectory().getAbsolutePath() + relativePath;
            case "root":
                return relativePath;
            default:
                return "";
        }
    }

    public static String getRealFilePathFromUriByContentResolver(Context context, Uri uri, String projectionName) {
        String filePath = "";
        Cursor cursor = context.getContentResolver().query(uri, new String[] {projectionName}, null, null, null);
        if (cursor == null) {
            return filePath;
        }
        cursor.moveToFirst();
        filePath = cursor.getString(0);
        cursor.close();
        return filePath;
    }

}