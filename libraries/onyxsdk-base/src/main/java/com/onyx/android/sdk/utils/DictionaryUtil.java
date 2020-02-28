package com.onyx.android.sdk.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.onyx.android.sdk.data.DictionaryQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seeksky on 2018/5/16.
 */

public class DictionaryUtil {


    private static final String STATE = "state";
    private static final String KEYWORD = "keyword";
    private static final String EXPLANATION = "explanation";
    private static final String DICT_NAME = "dictName";

    private static final int DICT_COUNT_LIMIT = 5;

    private final static String url = "content://com.onyx.dict.DictionaryProvider";
    /**
     *
     * @param context
     * @param keyword
     * @return
     */
    public static DictionaryQuery queryKeyWord(Context context, String keyword) {
        if (StringUtils.isNullOrEmpty(keyword)) {
            return new DictionaryQuery(DictionaryQuery.DICT_STATE_PARAM_ERROR);
        }
        return query(context, keyword);
    }

    private static DictionaryQuery query(Context context, String keyword) {
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse(url);
            keyword = StringUtils.trim(keyword);
            String []selectionArgs = new String[]{keyword, String.valueOf(DICT_COUNT_LIMIT)};
            cursor = context.getContentResolver().query(
                    uri, null, null, selectionArgs,
                    null);
            if (cursor == null) {
                return new DictionaryQuery(DictionaryQuery.DICT_STATE_ERROR);
            }
            if (cursor.getCount() == 0) {
                return new DictionaryQuery(DictionaryQuery.DICT_STATE_NO_DATA);
            }
            DictionaryQuery result = new DictionaryQuery(DictionaryQuery.DICT_STATE_QUERY_SUCCESSFUL);
            List<DictionaryQuery.Dictionary> list = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    DictionaryQuery.Dictionary dictionary = assemblyQueryResult(cursor, keyword);
                    list.add(dictionary);
                }while (cursor.moveToNext());
                result.setList(list);
                return result;
            }

        } catch (Exception e) {
            return new DictionaryQuery(DictionaryQuery.DICT_STATE_ERROR);
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return new DictionaryQuery(DictionaryQuery.DICT_STATE_ERROR);
    }

    private static DictionaryQuery.Dictionary assemblyQueryResult(Cursor cursor, String keyword) {
        String expString = "";
        int state = cursor.getInt(cursor.getColumnIndex(STATE));
        String dictName = cursor.getString(cursor.getColumnIndex(DICT_NAME));
        if (state == DictionaryQuery.DICT_STATE_QUERY_SUCCESSFUL) {
            expString += cursor.getString(cursor.getColumnIndex(EXPLANATION));
        }
        DictionaryQuery.Dictionary dictionary = new DictionaryQuery.Dictionary(state, dictName, keyword, expString);
        return dictionary;
    }
}
