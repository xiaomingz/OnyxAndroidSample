package com.onyx.android.sdk.utils;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by zhuzeng on 2/11/16.
 */
public class RectUtils {

    public static final String TAG = "RectUtils";
    private static boolean debug = false;

    public static final int DEFAULT_RECT_CENTER_DISTANCE_ERROR_RANGE = 2;
    private static final int RECT_UNION_DISTANCE_THRESHOLD = 60;

    public static boolean isDebug() {
        return debug;
    }

    static public String toString(final List<Rect> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Rect r : list) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(r.toString());
            first = false;
        }
        return sb.toString();
    }

    static public Rect toRect(final RectF source) {
        if (source == null) {
            return null;
        }
        return new Rect((int)source.left, (int)source.top, (int)source.right, (int)source.bottom);
    }

    static public List<Rect> toRectList(final List<RectF> source) {
        if (source == null) {
            return null;
        }

        ArrayList<Rect> list = new ArrayList<>();
        for (RectF r : source) {
            list.add(toRect(r));
        }
        return list;
    }

    static public RectF toRectF(final Rect source) {
        if (source == null) {
            return null;
        }
        return new RectF(source.left, source.top, source.right, source.bottom);
    }

    static public RectF remove(final RectF parent, final float childTop, final float childHeight, final float spacing) {
        if (childTop + childHeight +  spacing >= parent.bottom) {
            return null;
        }
        return new RectF(parent.left, childTop + childHeight + spacing, parent.right, parent.bottom);
    }

    static public RectF rectangle(double result[]) {
        float left = (float)result[0];
        float top = (float)result[1];
        float right = (float)result[2];
        float bottom = (float)result[3];
        RectF rect = new RectF(left, top , right, bottom);
        return rect;
    }

    static public PointF getBeginTop(List<RectF> list) {
        RectF target = list.get(0);
        return new PointF(target.left, target.top);
    }

    static public PointF getBeginBottom(List<RectF> list) {
        RectF target = list.get(0);
        return new PointF(target.left, target.bottom);
    }

    static public PointF getEndBottom(List<RectF> list) {
        RectF target = list.get(list.size() - 1);
        return new PointF(target.right, target.bottom);
    }

    /**
     * compare base line of two rectangle
     *
     * return 0 if on the same baseline (with tolerance)
     * return positive value if rect1 below rect2
     * return negative value if rect1 above rect2
     *
     * @param rect1
     * @param rect2
     * @return
     */
    static public int compareBaseLine(RectF rect1, RectF rect2) {
        final int tolerance = 10;
        int compare = (int)(rect1.bottom - rect2.bottom);
        if (Math.abs(compare) < tolerance && rect1.top < rect2.bottom && rect2.top < rect1.bottom) {
            return 0;
        }
        if ((rect1.top <= rect2.top && rect1.bottom >= rect2.bottom) ||
                (rect2.top <= rect1.top && rect2.bottom >= rect1.bottom)) {
            // if one rectangle is in the vertical range of another, we treat they as if they are on the same baseline
            return 0;
        }
        float overlay = Math.min(rect1.bottom, rect2.bottom) - Math.max(rect1.top, rect2.top);
        if (overlay > rect1.height() / 2 || overlay > rect2.height() / 2) {
            // if overlay is big enough, we think they are on the same baseline
            return 0;
        }
        return compare;
    }

    static public List<RectF> mergeRectanglesByBaseLine(List<RectF> list) {
        List<RectF> baseList = new ArrayList<>();
        for (RectF rect : list) {
            // force-brute iterating two list, since we will not have to much to compare
            boolean foundBaseLine = false;
            for (int i = 0; i < baseList.size(); i++) {
                RectF baseRect = baseList.get(i);
                if (compareBaseLine(rect, baseRect) == 0) {
                    foundBaseLine = true;
                    baseRect.union(rect);
                    break;
                }
            }
            if (!foundBaseLine) {
                baseList.add(new RectF(rect));
            }
        }
        return baseList;
    }

    static public List<RectF> mergeBlockRectanglesByWord(List<RectF> orderedWordList) {
        final int WORD_SPACING = 20;
        List<RectF> lineList = new ArrayList<>();
        RectF lastWordRect = null;
        RectF blockRect = null;
        float space;
        for (RectF wordRect : orderedWordList) {
            if (blockRect == null) {
                blockRect = new RectF(wordRect);
                lineList.add(blockRect);
                lastWordRect = wordRect;
                continue;
            }
            space = Math.max(1.5f * (Math.max(lastWordRect.width(), lastWordRect.height()) + Math.max(wordRect.width(),
                    wordRect.height())), WORD_SPACING);
            if (getDistance(lastWordRect, wordRect) < space  && isWordInBlock(blockRect, wordRect)) {
                blockRect.union(wordRect);
            } else {
                blockRect = new RectF(wordRect);
                lineList.add(blockRect);
            }
            lastWordRect = wordRect;
        }
        return lineList;
    }

    static public boolean isWordInBlock(RectF blockRect, RectF wordRect) {
        // punctuation may be below the word
        final int PUNCTUATION_WORD_VERTICAL_SPACING = -3;
        return compareBaseLine(blockRect, wordRect) == 0 ||
                blockRect.bottom - wordRect.top > PUNCTUATION_WORD_VERTICAL_SPACING;
    }

    static public float getDistance(RectF a, RectF b) {
        float dx = a.centerX() - b.centerX();
        float dy = a.centerY() - b.centerY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    static public boolean isSameCenterWithinErrorRange(Rect a, Rect b) {
        return isSameCenterWithinErrorRange(a, b, DEFAULT_RECT_CENTER_DISTANCE_ERROR_RANGE);
    }

    static public boolean isSameCenterWithinErrorRange(Rect a, Rect b, int errorRange) {
        float distance = getDistance(toRectF(a), toRectF(b));
        boolean isSameCenterWithinErrorRange = distance <= errorRange;
        if (!isSameCenterWithinErrorRange) {
            Debug.d("isSameCenterWithinErrorRange, two rect center distance = " + distance);
        }
        return isSameCenterWithinErrorRange;
    }

    static public ArrayList<RectF> mergeAdjacentRectangles(final List<RectF> list) {
        ArrayList<RectF> mergedList = new ArrayList<>();
        if (CollectionUtils.isNullOrEmpty(list)) {
            return mergedList;
        }

        for (RectF src: list) {
            boolean merged = false;
            for (RectF toMerge : mergedList) {
                if (canMerge(src, toMerge)) {
                    merged = true;
                    toMerge.union(src);
                    break;
                }
            }

            if (!merged) {
                mergedList.add(new RectF(src));
            } else {
                mergedList = mergeAdjacentRectangles(mergedList);
            }
        }

        return mergedList;
    }

    static public boolean canMerge(final RectF a, final RectF b) {
        return (a.left <= b.right && b.left <= a.right && a.top <= b.bottom && b.top <= a.bottom) &&
                ((Float.compare(a.left, b.left) == 0 && Float.compare(a.right, b.right) == 0) ||
                        (Float.compare(a.top, b.top) == 0 && Float.compare(a.bottom, b.bottom) == 0));
    }

    static public List<RectF> cutRectByExcludingRegions(RectF source, final List<RectF> excluding) {
        List<RectF> result = new ArrayList<>();

        if (excluding == null || excluding.size() <= 0) {
            result.add(normalizedOf(source));
            return result;
        }

        List<RectF> excludeByLeft = new ArrayList<>();
        for (RectF r : excluding) {
            if (r == null || r.left == r.right || r.top == r.bottom) {
                // ignore invalid rect inputs
                continue;
            }
            excludeByLeft.add(normalizedOf(r));
        }
        if (excludeByLeft.size() <= 0) {
            result.add(normalizedOf(source));
            return result;
        }

        Collections.sort(excludeByLeft, new Comparator<RectF>() {
            @Override
            public int compare(RectF o1, RectF o2) {
                return Float.compare(o1.left, o2.left);
            }
        });

        RectF outBound = new RectF(source);

        do {
            RectF leftMost = excludeByLeft.get(0);

            RectF innerBound = new RectF(excludeByLeft.get(0));
            for (RectF r : excludeByLeft) {
                innerBound.union(r);
            }

            // out bounding rectangles of regions to exclude
            if (innerBound.left > outBound.left) {
                result.add(new RectF(outBound.left, outBound.top, innerBound.left, outBound.bottom));
            }
            if (innerBound.right < outBound.right) {
                result.add(new RectF(innerBound.right, outBound.top, outBound.right, outBound.bottom));
            }
            if (innerBound.top > outBound.top) {
                result.add(new RectF(innerBound.left, outBound.top, innerBound.right, innerBound.top));
            }
            if (innerBound.bottom < outBound.bottom) {
                result.add(new RectF(innerBound.left, innerBound.bottom, innerBound.right, outBound.bottom));
            }

            RectF nextLeft = null;
            List<RectF> leftList = new ArrayList<>();
            for (RectF rect : excludeByLeft) {
                if (rect.left == leftMost.left) {
                    leftList.add(rect);
                } else {
                    // nextLeft can be null, in this case,
                    // we think all excluding rectangles align on the left
                    nextLeft = rect;
                    break;
                }
            }

            Collections.sort(leftList, new Comparator<RectF>() {
                @Override
                public int compare(RectF o1, RectF o2) {
                    return Float.compare(o1.right, o2.right);
                }
            });

            float right = leftList.get(0).right;
            if (nextLeft != null) {
                right = Math.min(right, nextLeft.left);
            }

            List<RectF> topList = new ArrayList<>(leftList);
            Collections.sort(topList, new Comparator<RectF>() {
                @Override
                public int compare(RectF o1, RectF o2) {
                    return Float.compare(o1.top, o2.top);
                }
            });

            List<Pair<AtomicReference<Float>, AtomicReference<Float>>> segmentsByY = new ArrayList<>();
            for (RectF r : topList) {
                if (segmentsByY.isEmpty()) {
                    segmentsByY.add(new Pair<>(new AtomicReference<>(r.top), new AtomicReference<>(r.bottom)));
                    continue;
                }
                boolean combined = false;
                for (Pair<AtomicReference<Float>, AtomicReference<Float>> segment : segmentsByY) {
                    if (r.bottom < segment.first.get() || r.top > segment.second.get()) {
                        continue;
                    }

                    if (r.top < segment.first.get()) {
                        segment.first.set(r.top);
                    }
                    if (r.bottom > segment.second.get()) {
                        segment.second.set(r.bottom);
                    }
                    combined = true;
                    break;
                }
                if (!combined) {
                    segmentsByY.add(new Pair<>(new AtomicReference<>(r.top), new AtomicReference<>(r.bottom)));
                }
            }

            Collections.sort(segmentsByY, new Comparator<Pair<AtomicReference<Float>, AtomicReference<Float>>>() {
                @Override
                public int compare(Pair<AtomicReference<Float>, AtomicReference<Float>> o1, Pair<AtomicReference<Float>, AtomicReference<Float>> o2) {
                    return Float.compare(o1.first.get(), o2.first.get());
                }
            });

            Pair<AtomicReference<Float>, AtomicReference<Float>> top = segmentsByY.get(0);
            if (top.first.get() > innerBound.top) {
                result.add(new RectF(innerBound.left, innerBound.top, right, top.first.get()));
            }

            for (int j = 1; j < segmentsByY.size(); j++) {
                Pair<AtomicReference<Float>, AtomicReference<Float>> above = segmentsByY.get(j - 1);
                Pair<AtomicReference<Float>, AtomicReference<Float>> below = segmentsByY.get(j);
                if (below.first.get() > above.second.get()) {
                    result.add(new RectF(innerBound.left, above.second.get(), right, below.first.get()));
                }
            }

            Collections.sort(segmentsByY, new Comparator<Pair<AtomicReference<Float>, AtomicReference<Float>>>() {
                @Override
                public int compare(Pair<AtomicReference<Float>, AtomicReference<Float>> o1, Pair<AtomicReference<Float>, AtomicReference<Float>> o2) {
                    return Float.compare(o1.second.get(), o2.second.get());
                }
            });

            Pair<AtomicReference<Float>, AtomicReference<Float>> bottom = segmentsByY.get(segmentsByY.size() - 1);
            if (bottom.second.get() < innerBound.bottom) {
                result.add(new RectF(innerBound.left, bottom.second.get(), right, innerBound.bottom));
            }

            List<RectF> removeList = new ArrayList<>();
            for (RectF rect : leftList) {
                if (Float.compare(rect.right, right) <= 0) {
                    removeList.add(rect);
                } else {
                    rect.left = right;
                }
            }

            for (RectF rect : removeList) {
                excludeByLeft.remove(rect);
            }

            outBound = new RectF(innerBound);
            outBound.left = right;
        } while (excludeByLeft.size() > 0);

        return result;
    }

    static private void addToUniqueList(final List<RectF> list, RectF rect) {
        boolean find = false;
        for (RectF r : list) {
            if (r.left == rect.left && r.top == rect.top &&
                    r.right == rect.right && r.bottom == rect.bottom) {
                find = true;
                break;
            }
        }
        if (!find) {
            list.add(rect);
        }
    }

    static public void expand(RectF rectF, float value) {
        if (rectF == null) {
            return;
        }
        rectF.set(rectF.left - value,
                rectF.top - value,
                rectF.right + value,
                rectF.bottom + value);
    }

    static public float square(final List<RectF> list) {
        float square = 0;
        if (list == null || list.size() <= 0) {
            return 0;
        }

        for (RectF r : list) {
            Debug.e(RectUtils.class, "square rect -> " + r.toString());
        }

        if (list.size() == 1) {
            square = list.get(0).width() * list.get(0).height();
            Debug.e(RectUtils.class, "square result -> " + square);
            return square;
        }

        Map<Integer, List<RectF>> intersectionMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                RectF r1 = new RectF(list.get(i));
                RectF r2 = new RectF(list.get(j));
                if (r1.intersect(r2)) {
                    Debug.e(RectUtils.class, "intersects: %d, %d -> %s", i, j, r1.toString());
                    if (intersectionMap.get(i) == null) {
                        intersectionMap.put(i, new ArrayList<RectF>());
                    }
                    addToUniqueList(intersectionMap.get(i), r1);
                    if (intersectionMap.get(j) == null) {
                        intersectionMap.put(j, new ArrayList<RectF>());
                    }
                    addToUniqueList(intersectionMap.get(j), r1);
                }
            }
            RectF r = list.get(i);
            float s1 = r.width() * r.height();
            float s2 = square(intersectionMap.get(i));
            square += (s1 - s2);
            Debug.e(RectUtils.class, "%s -> %f, intersections -> %f, square -> %f", r.toString(), s1, s2, square);
        }

        List<RectF> intersections = new ArrayList<>();
        for (List<RectF> l : intersectionMap.values()) {
            for (RectF r : l) {
                addToUniqueList(intersections, r);
            }
        }
        float s1 = square(intersections);
        Debug.e(RectUtils.class, "intersections square -> " + s1);
        square += s1;
        Debug.e(RectUtils.class, "square result -> " + square);
        return square;
    }

    public static void translate(RectF rectF, float dx, float dy) {
        if (rectF == null) {
            return;
        }
        rectF.set(rectF.left + dx,
                rectF.top + dy,
                rectF.right + dx,
                rectF.bottom + dy);
    }

    public static void directionScale(RectF rectF, float sx, float sy, RectF relativelyRectF) {
        if (rectF == null) {
            return;
        }
        float dx = getDistanceXAfterScale(relativelyRectF, sx);
        float dy = getDistanceYAfterScale(relativelyRectF, sy);
        rectF.set(rectF.left*Math.abs(sx),
                rectF.top*Math.abs(sy),
                rectF.right*Math.abs(sx),
                rectF.bottom*Math.abs(sy));
        RectUtils.translate(rectF, -dx, -dy);
    }

    public static void scale(RectF rectF, float sx, float sy) {
        if (rectF == null) {
            return;
        }
        rectF.set(rectF.left*sx,
                rectF.top*sy,
                rectF.right*sx,
                rectF.bottom*sy);
    }

    public static void scale(Rect rect, float sx, float sy) {
        if (rect == null) {
            return;
        }
        rect.set((int)(rect.left*sx),
                (int)(rect.top*sy),
                (int)(rect.right*sx),
                (int)(rect.bottom*sy));
    }

    public static float getDistanceXAfterScale(RectF rectF, float sx) {
        float dx;
        if (sx > 0) {
            dx = rectF.left * Math.abs(sx) - rectF.left;
        }else {
            dx = rectF.right * Math.abs(sx) - rectF.right;
        }
        return dx;
    }

    public static float getDistanceYAfterScale(RectF rectF, float sy) {
        float dy;
        if (sy > 0) {
            dy = rectF.top * Math.abs(sy) - rectF.top;
        }else {
            dy = rectF.bottom * Math.abs(sy) - rectF.bottom;
        }
        return dy;
    }

    public static void normalize(final RectF region) {
        float left = Math.min(region.left, region.right);
        float right = Math.max(region.left, region.right);
        float top = Math.min(region.top, region.bottom);
        float bottom = Math.max(region.top, region.bottom);
        region.set(left, top, right, bottom);
    }

    /**
     * create new normalized rect
     *
     * @param region
     * @return
     */
    public static RectF normalizedOf(final RectF region) {
        float left = Math.min(region.left, region.right);
        float right = Math.max(region.left, region.right);
        float top = Math.min(region.top, region.bottom);
        float bottom = Math.max(region.top, region.bottom);
        return new RectF(left, top, right, bottom);
    }

    public static RectF boundingRect(final List<RectF> list) {
        RectF rect = new RectF();
        if (list == null || list.size() <= 0) {
            return rect;
        }
        for(RectF r : list) {
            rect.union(r);
        }
        return rect;
    }

    public static boolean intersect(RectF o1, RectF o2) {
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.intersect(o2);
    }

    public static boolean intersect(Rect o1, Rect o2) {
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.intersect(o2);
    }

    public static boolean isNullOrEmpty(Rect rect) {
        if (rect == null) {
            return true;
        }
        return rect.isEmpty();
    }

    public static boolean isNullOrEmpty(RectF rect) {
        if (rect == null) {
            return true;
        }
        return rect.isEmpty();
    }

    public static boolean contains(Rect container, Rect subset) {
        if (container == null || subset == null) {
            return false;
        }
        return container.left <= subset.left
                && container.top <= subset.top
                && container.right >= subset.right
                && container.bottom >= subset.bottom;
    }

    public static boolean contains(RectF container, RectF subset) {
        if (container == null || subset == null) {
            return false;
        }
        return container.left <= subset.left
                && container.top <= subset.top
                && container.right >= subset.right
                && container.bottom >= subset.bottom;
    }

    public static boolean hasEdgesOverlap(Rect rect1, Rect rect2) {
        if (rect1 == null || rect2 == null) {
            return false;
        }
        return rect1.left == rect2.left
                || rect1.top == rect2.top
                || rect1.right == rect2.right
                || rect1.bottom == rect2.bottom;
    }

    public static RectF computeRelativeRect(RectF compareParentRect, RectF compareRect, RectF parentRect) {
        float left = parentRect.width() * compareRect.left / compareParentRect.width();
        float top = parentRect.height() * compareRect.top / compareParentRect.height();
        float width = parentRect.width() * compareRect.width() / compareParentRect.width();
        float height = parentRect.height() * compareRect.height() / compareParentRect.height();
        return new RectF(left, top, left + width, top + height);
    }

    public static boolean isSameSize(RectF rect1, RectF rect2) {
        if (rect1 == null || rect2 == null) {
            return false;
        }
        return rect1.width() == rect2.width()
                && rect2.height() == rect2.height();
    }

    public static List<RectF> mergeRectanglesByDistance(List<RectF> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return list;
        }
        int rawInputListSize = list.size();
        int count = 0;
        Benchmark benchmark = new Benchmark();
        sort(list);
        long sortTime = benchmark.duration();
        benchmark.restart();
        int successUnionIndex;
        do {
            successUnionIndex = -1;
            int size = list.size();
            RectF o1;
            RectF o2;
            for (int i = 0; i < size; i++) {
                o1 = list.get(i);
                for (int j = 0; j < size; j++) {
                    count++;
                    if (i == j) {
                        continue;
                    }
                    o2 = list.get(j);
                    if (o1.equals(o2)) {
                        continue;
                    }
                    if (isDistanceInThreshold(o1, o2, RECT_UNION_DISTANCE_THRESHOLD)) {
                        o1.union(o2);
                        successUnionIndex = j;
                        break;
                    }
                }

                if (successUnionIndex >= 0) {
                    break;
                }
            }

            if (successUnionIndex >= 0) {
                list.remove(successUnionIndex);
            }

        } while (successUnionIndex >= 0);

        if (Debug.getDebug()) {
            Debug.d(TAG, "mergeRectanglesByDistance compare count = " + count
                    + ", raw input size = " + rawInputListSize + ", output size = " + list.size()
                    + ", sort time = " + sortTime + " ms"
                    + ", merge time = " + benchmark.duration() + " ms");
            for (RectF rect : list) {
                Debug.v(TAG, "output rect = " + rect.toShortString() + ", width = " + rect.width() + ", height = " + rect.height());
            }
        }
        return list;
    }

    private static void sort(List<RectF> list) {
        Collections.sort(list, new Comparator<RectF>() {
            @Override
            public int compare(RectF o1, RectF o2) {
                if (o1.left - o2.left > 0) {
                    return 1;
                }
                if (o1.left - o2.left < 0) {
                    return -1;
                }
                if (o1.top - o2.top > 0) {
                    return 1;
                }
                if (o1.top - o2.top < 0) {
                    return -1;
                }
                if (o1.right - o2.right > 0) {
                    return 1;
                }
                if (o1.right - o2.right < 0) {
                    return -1;
                }
                if (o1.bottom - o2.bottom > 0) {
                    return 1;
                }
                if (o1.bottom - o2.bottom < 0) {
                    return -1;
                }
                return 0;
            }
        });
    }

    public static boolean isDistanceInThreshold(RectF o1, RectF o2, int threshold) {
        float distanceByQuadrant = getDistanceByQuadrant(o1, o2);
        if (distanceByQuadrant < 0) {
            String msg = "getDistanceByQuadrant = " + distanceByQuadrant + ", o1 = " + o1 + ", o2 = " + o2;
            Debug.e(TAG, msg);
        }
        return distanceByQuadrant <= threshold;
    }

    public static float getDistanceByQuadrant(RectF rect1, RectF rect2) {
        if (isIntersect(rect1, rect2)) {
            return 0;
        }
        // origin point: rect2 (left, bottom), rect1 do not intersect rect2
        float x1;
        float y1;
        float x2;
        float y2;
        if (rect1.bottom < rect2.bottom) {
            if (rect1.left > rect2.left) {
                // first quadrant
                x2 = rect2.right;
                y2 = rect2.top;
                x1 = rect1.left;
                y1 = rect1.bottom;
                if (y1 > y2) {
                    return x1 - x2;
                }
                if (x1 < x2) {
                    return y2 - y1;
                }
            } else {
                // second quadrant
                x2 = rect2.right;
                y2 = rect2.bottom;
                x1 = rect1.left;
                y1 = rect1.top;
                if (y1 > y2) {
                    return x2 - x1;
                }
                if (x1 > x2) {
                    return y2 - y1;
                }
            }
        } else {
            if (rect1.left < rect2.left) {
                // third quadrant
                x2 = rect2.left;
                y2 = rect2.bottom;
                x1 = rect1.right;
                y1 = rect1.top;
                if (y1 < y2) {
                    return x2 - x1;
                }
                if (x1 > x2) {
                    return y1 - y2;
                }
            } else {
                // fourth quadrant
                x2 = rect2.right;
                y2 = rect2.bottom;
                x1 = rect1.left;
                y1 = rect1.top;
                if (y1 < y2) {
                    return x1 - x2;
                }
                if (x1 < x2) {
                    return y1 - y2;
                }
            }
        }
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static boolean isIntersect(RectF o1, RectF o2) {
        return o1.left < o2.right && o2.left < o1.right
                && o1.top < o2.bottom && o2.top < o1.bottom;
    }

    public static boolean isSameBaseLine(RectF o1, RectF o2) {
        return compareBaseLine(o1, o2) == 0;
    }

    public static List<RectF> cleanEmptyRect(List<RectF> list) {
        List<RectF> rectFList = new ArrayList<>();
        for (RectF rectF : list) {
            if (!rectF.isEmpty()) {
                rectFList.add(rectF);
            }
        }
        return rectFList;
    }

    @NonNull
    public static List<RectF> loadEqualRectList(List<RectF> rectList1, List<RectF> rectList2) {
        List<RectF> equalRectList = new ArrayList<>();
        RectF find;
        for (RectF current : rectList1) {
            find = null;
            for (RectF last : rectList2) {
                if (current.equals(last)) {
                    equalRectList.add(last);
                    find = last;
                    break;
                }
            }
            if (find != null) {
                rectList2.remove(find);
            }
        }
        return equalRectList;
    }

    public static void unionDiffRectList(RectF output, List<RectF> rectList1, List<RectF> rectList2) {
        List<RectF> equalCursorRectList = loadEqualRectList(rectList2, rectList1);
        rectList2.removeAll(equalCursorRectList);
        rectList1.removeAll(equalCursorRectList);
        RectUtils.union(output, rectList2);
        RectUtils.union(output, rectList1);
    }

    public static void union(RectF output, List<RectF> inputList) {
        if (CollectionUtils.isNullOrEmpty(inputList)) {
            return;
        }
        for (RectF rectF : inputList) {
            output.union(rectF);
        }
    }

}
