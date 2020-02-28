package com.onyx.android.sdk.utils;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.onyx.android.sdk.data.GAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhuzeng on 12/9/15.
 */
public class CollectionUtils {

    static public boolean isNullOrEmpty(final Collection list) {
        return list == null || list.size() <= 0;
    }

    static public boolean isNullOrEmpty(final SparseArray array) {
        return array == null || array.size() <= 0;
    }

    static public boolean isNonBlank(final Collection list) {
        return list != null && list.size() > 0;
    }

    static public boolean isNullOrEmpty(final Map map) {
        return map == null || map.size() <= 0;
    }

    static public boolean isNullOrEmpty(final GAdapter adapter) {
        return adapter == null || isNullOrEmpty(adapter.getList());
    }

    static public boolean contains(final Set<String> set, final String value) {
        if (set != null && set.size() > 0) {
            return set.contains(value);
        }
        return true;
    }

    static public boolean contains(final Collection<String> source, final Collection<String> target) {
        if (source == null && target == null) {
            return true;
        }
        if (source == null || target == null) {
            return false;
        }
        for (String string : target) {
            if (source.contains(string)) {
                return true;
            }
        }
        return false;
    }

    static public boolean equals(final Collection firstSet, final Collection secondSet) {
        if (firstSet == null || secondSet == null) {
            return false;
        }
        return firstSet.equals(secondSet);
    }

    static public boolean contains(final List<String> list, final String string) {
        if (list == null) {
            return true;
        }
        if (list.contains(string)) {
            return true;
        }
        return false;
    }

    static public <T> boolean safelyContains(final Collection<T> collection, final T t) {
        if (collection == null) {
            return false;
        }
        return collection.contains(t);
    }

    static public int getSize(Collection collection) {
        return collection == null ? 0 : collection.size();
    }

    static public int getSize(Map map) {
        return map == null ? 0 : map.size();
    }

    static public boolean safelyContains(final Set<String> set, final String string) {
        return set != null && set.contains(string);
    }

    static public boolean safelyContains(final List<String> list, final String string) {
        if (list == null) {
            return true;
        }
        if (list.contains(string)) {
            return true;
        }
        return false;
    }

    public static boolean safelyReverseContains(List<String> list, String string) {
        if (StringUtils.isNullOrEmpty(string) || CollectionUtils.isNullOrEmpty(list)) {
            return false;
        }
        for (String item : list) {
            if (string.contains(item)) {
                return true;
            }
        }
        return false;
    }

    public static void diff(final Collection<String> origin, final Collection<String> target, final Collection<String> diff) {
        for (String s : target) {
            if (!origin.contains(s)) {
                diff.add(s);
            }
        }
    }

    public static <T> void safeAddAll(Collection<T> originList, Collection<T> targetList) {
        if (originList == null || CollectionUtils.isNullOrEmpty(targetList)) {
            return;
        }
        originList.addAll(targetList);
    }

    public static <T> void safeAddAll(Collection<T> originList, Collection<T> targetList, boolean clear) {
        if (originList == null) {
            return;
        }
        if (clear) {
            originList.clear();
        }
        safeAddAll(originList, targetList);
    }

    public static <K, V> void safeAddAllMap(Map<K, V> originMap, Map<K, V> targetMap) {
        if (originMap == null || CollectionUtils.isNullOrEmpty(targetMap)) {
            return;
        }
        originMap.putAll(targetMap);
    }

    public static void clear(Collection list) {
        if (list != null) {
            list.clear();
        }
    }

    public static <T> void ensureAddAll(Collection<T> originList, Collection<T> targetList) {
        if (originList == null || isNullOrEmpty(targetList)) {
            return;
        }
        originList.addAll(targetList);
    }

    public static <T extends Comparable<T>> boolean compare(@NonNull List<T> a, @NonNull List<T> b) {
        if (a.size() != b.size()) {
            return false;
        }
        Collections.sort(a);
        Collections.sort(b);
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equals(b.get(i)))
                return false;
        }
        return true;
    }

    @NonNull
    public static <K, V> Collection<V> getValuesFromKeySet(Map<K, V> map, Collection<K> keySet) {
        if (isNullOrEmpty(keySet) || isNullOrEmpty(map)) {
            return new HashSet<>();
        }
        Set<V> valueSet = new HashSet<>();
        for (K key : keySet) {
            if (map.containsKey(key)) {
                valueSet.add(map.get(key));
            }
        }
        return valueSet;
    }

    public static int getClosetValueFromCollection(Collection<Integer> collection, int validateValue) {
        List<Integer> dataList = new ArrayList<>(collection);
        Collections.sort(dataList);
        int[] dataArray = new int[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) {
            dataArray[i] = dataList.get(i);
        }
        int key = Arrays.binarySearch(dataArray, validateValue);
        if (key < 0) {
            key = Math.abs(key) - 1;
            return dataArray[key];
        } else {
            return validateValue;
        }
    }

    /**
     *  if data is [1 2 3 4 5 6 7 8 9 10]
     *  before transform display like this:
     *  1 2
     *  3 4
     *  5 6
     *  7 8
     *  9 10
     *
     *  after transform display like this:
     *  1 6
     *  2 7
     *  3 8
     *  4 9
     *  5 10
     *
     * @param data the data to transform
     * @return
     */
    public static <T> List<T> transformData(List<T> data, int col) {
        List<T> transformData = new ArrayList<>(data.size());
        int row =  (int)Math.ceil((double)data.size() / col);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                int index = i + j*row;
                if (index >= data.size()) {
                    break;
                }
                transformData.add(data.get(index));
            }
        }
        return transformData;
    }

    public static <T> List<T> safelySubList(List<T> list, int fromIndex, int toIndex) {
        if (isNullOrEmpty(list)) {
            return Collections.emptyList();
        }
        if (fromIndex < 0){
            fromIndex = 0;
        }
        if (toIndex > getSize(list)){
            toIndex = getSize(list);
        }
        return new ArrayList<>(list.subList(fromIndex, toIndex));
    }

    @NonNull
    public static <K, V> List<V> getList(Map<K, List<V>> map, K key) {
        List<V> list = map.get(key);
        if (list == null) {
            list = new ArrayList<>();
            map.put(key, list);
        }
        return list;
    }

    public static <T> T getLast(List<T> list) {
        if (isNullOrEmpty(list)) {
            return null;
        }
        int endIndex = getSize(list) - 1;
        return list.get(endIndex);
    }

    public static boolean isLastElement(List list, int index) {
        return index + 1 == getSize(list);
    }

    public static <T> boolean isLastElement(List<T> list, T t) {
        if (isNullOrEmpty(list)) {
            return false;
        }
        int index = list.indexOf(t);
        if (index < 0) {
            return false;
        }
        return index + 1 == getSize(list);
    }

    public static boolean isFirstElement(List list, int index) {
        if (isNullOrEmpty(list)) {
            return false;
        }
        return index == 0;
    }

    public static <T> boolean isFirstElement(List<T> list, T t) {
        if (isNullOrEmpty(list)) {
            return false;
        }
        int index = list.indexOf(t);
        return index == 0;
    }

    public static <T> T getElement(List<T> list, int currentIndex, boolean preElement) {
        if (preElement) {
            currentIndex--;
        } else {
            currentIndex++;
        }
        if (isOutOfRange(list, currentIndex)) {
            return null;
        }
        return list.get(currentIndex);
    }

    public static boolean isOutOfRange(List list, int index) {
        return index < 0 || index >= getSize(list);
    }

    public static <T> boolean safelyRemove(Collection<T> collection, Comparable<T> comparable,
                                           boolean abortFirstMatched) {
        boolean succeed = false;
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (comparable.compareTo(t) == 0) {
                succeed = true;
                iterator.remove();
                if (abortFirstMatched) {
                    break;
                }
            }
        }
        return succeed;
    }

    public static <T> boolean safelyEquals(Collection<T> collection, Comparable<T> comparable) {
        boolean succeed = false;
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (comparable.compareTo(t) == 0) {
                succeed = true;
                break;
            }
        }
        return succeed;
    }

    public static boolean safelyStringEquals(final Collection<String> collection, final String exceptString) {
        return safelyEquals(collection, new Comparable<String>() {
            @Override
            public int compareTo(@NonNull String o) {
                return o.compareTo(exceptString);
            }
        });
    }

    public static void swapList(List list, int fromPosition, int toPosition) {
        int size = CollectionUtils.getSize(list);
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition && (i + 1) < size; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition && (i - 1) >= 0; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
    }

    public static void swap(List list, int fromPosition, int toPosition) {
        Collections.swap(list, fromPosition, toPosition);
    }
}
