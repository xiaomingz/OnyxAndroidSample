package com.onyx.android.sdk.data;

import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by joy on 10/19/17.
 */

public class PageRange {
    public String startPosition;
    public String endPosition;

    public PageRange() {
    }

    public PageRange(String start, String end) {
        startPosition = start;
        endPosition = end;
    }

    public static PageRange create(final String start, final String end) {
        return new PageRange(start, end);
    }

    public static PageRange copy(PageRange pageRange) {
        return new PageRange(pageRange.startPosition, pageRange.endPosition);
    }

    public String getStartPosition() {
        return startPosition;
    }

    public PageRange setStartPosition(String startPosition) {
        this.startPosition = startPosition;
        return this;
    }

    public String getEndPosition() {
        return endPosition;
    }

    public PageRange setEndPosition(String endPosition) {
        this.endPosition = endPosition;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PageRange pageRange = (PageRange) o;

        if (!StringUtils.safelyEquals(startPosition, pageRange.startPosition)) {
            return false;
        }
        return StringUtils.safelyEquals(endPosition, pageRange.endPosition);
    }

    @Override
    public int hashCode() {
        int result = startPosition != null ? startPosition.hashCode() : 0;
        result = 31 * result + (endPosition != null ? endPosition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "start=" + startPosition +
                ", end=" + endPosition +
                '}';
    }
}
