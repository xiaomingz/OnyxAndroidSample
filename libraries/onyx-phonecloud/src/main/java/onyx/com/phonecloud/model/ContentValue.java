package onyx.com.phonecloud.model;

/**
 * Created by TonyXie on 2020-03-02
 */
public class ContentValue {
    private String key;
    private String value;
    private int intValue = -1;
    private long longValue = -1;

    public ContentValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public ContentValue(String key, int intValue) {
        this.key = key;
        this.intValue = intValue;
    }

    public ContentValue(String key, long longValue) {
        this.key = key;
        this.longValue = longValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public int getIntValue() {
        return intValue;
    }

    public long getLongValue() {
        return longValue;
    }
}
