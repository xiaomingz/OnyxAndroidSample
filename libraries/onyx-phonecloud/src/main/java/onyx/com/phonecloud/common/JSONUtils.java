package onyx.com.phonecloud.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.onyx.android.sdk.utils.Debug;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class JSONUtils {
    
    public static <T> T parseObject(String json, Class<T> cls, Feature... features) {
        try {
            return JSON.parseObject(json, cls, features);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static public <T> T parseObject(String json, TypeReference<T> typeReference, Feature... features) {
        try {
            return JSON.parseObject(json, typeReference, features);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T toBean(byte[] json, Class<T> type) {
        T obj = null;
        try {
            String info = new String(json, "UTF-8");
            obj = JSON.parseObject(info, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static <T> T toBean(byte[] json, Type type) {
        T obj = null;
        try {
            String info = new String(json, "UTF-8");
            obj = JSON.parseObject(info, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static <T> T toBean(String json, Type type) {
        T obj;
        obj = JSON.parseObject(json, type);
        return obj;
    }

    public static String toJson(Object object) {
        String result = null;
        try {
            result = JSON.toJSONString(object);
        } catch (Exception e) {
        }
        return result;
    }

    public static <T> List<T> toList(String json, Type type){
        List<T> list = JSON.parseObject(json, type);
        return list;
    }

    @NonNull
    public static <T> List<T> toList(String json, Class<T> clazz) {
        List<T> list = null;
        try {
            list = JSON.parseArray(json, clazz);
        } catch (Exception e) {
            Debug.e(e);
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    @NonNull
    public static final JSONObject parseObject(String text) {
        JSONObject jsonObject = JSON.parseObject(text);
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        return jsonObject;
    }
}
