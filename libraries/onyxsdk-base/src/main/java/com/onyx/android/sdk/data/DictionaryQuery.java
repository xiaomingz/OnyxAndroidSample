package com.onyx.android.sdk.data;

import java.util.List;

/**
 * Created by seeksky on 2018/5/16.
 */

public class DictionaryQuery {

    public static final int DICT_STATE_ERROR = -2;
    public static final int DICT_STATE_PARAM_ERROR = -1;
    public static final int DICT_STATE_QUERY_SUCCESSFUL = 0;
    public static final int DICT_STATE_QUERY_FAILED = 1;
    public static final int DICT_STATE_LOADING = 2;
    public static final int DICT_STATE_NO_DATA = 3;

    private int state;
    private List<Dictionary> list;

    public DictionaryQuery(int state) {
        this.state = state;
    }

    public Dictionary createDictionary(int state, String dictName, String keyword, String explanation) {
        Dictionary dictionary = new Dictionary(state, dictName, keyword, explanation);
        return dictionary;
    }

    public static class Dictionary {
        private int state;
        private String dictName;
        private String keyword;
        private String explanation;

        public Dictionary(int state, String dictName, String keyword, String explanation) {
            this.state = state;
            this.dictName = dictName;
            this.keyword = keyword;
            this.explanation = explanation;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getDictName() {
            return dictName;
        }

        public void setDictName(String dictName) {
            this.dictName = dictName;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<Dictionary> getList() {
        return list;
    }

    public void setList(List<Dictionary> list) {
        this.list = list;
    }
}
