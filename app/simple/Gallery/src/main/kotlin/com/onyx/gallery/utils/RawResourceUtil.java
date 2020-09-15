package com.onyx.gallery.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Leung 2020/9/15 16:32
 **/
public class RawResourceUtil {
    public static String contentOfRawResource(Context context, int rawResourceId) {
        BufferedReader breader = null;
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(rawResourceId);
            breader = new BufferedReader(new InputStreamReader(is));
            StringBuilder total = new StringBuilder();
            String line = null;
            while ((line = breader.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        } catch (Exception e) {
            if (rawResourceId > 0) {
                e.printStackTrace();
            }
        } finally {
            closeQuietly(breader);
            closeQuietly(is);
        }
        return null;
    }

    static public void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
