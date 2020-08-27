package com.onyx.android.appcompat.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.onyx.android.appcompat.common.utils.PageTurningDetector;
import com.onyx.android.appcompat.common.utils.PageTurningDirection;

/**
 * Created by ming on 16/6/24.
 */
public class PageViewPager extends ViewPager {

    protected float lastX, lastY;
    private OnPagingListener pagingListener;
    private boolean isCanScroll = true;

    public PageViewPager(@NonNull Context context) {
        this(context, null);
    }

    public PageViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PageViewPager setPagingListener(OnPagingListener pagingListener) {
        this.pagingListener = pagingListener;
        return this;
    }

    public void setCanScroll(boolean canScroll) {
        isCanScroll = canScroll;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isCanScroll) {
            return false;
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                return (detectDirection(ev) != PageTurningDirection.NONE);
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isCanScroll) {
            return false;
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_UP:
                int direction = detectDirection(ev);
                if (direction == PageTurningDirection.NEXT) {
                    nextPage();
                    return true;
                } else if (direction == PageTurningDirection.PREV) {
                    prevPage();
                    return true;
                } else if (direction == PageTurningDirection.UP) {
                    upPage();
                    return true;
                } else if(direction == PageTurningDirection.DOWN) {
                    downPage();
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    protected void nextPage() {
        if (pagingListener != null && getAdapter() != null && getCurrentItem() >= getAdapter().getCount() - 1) {
            pagingListener.onLastPage();
        } else {
            setCurrentItem(getCurrentItem() + 1, false);
            onPageChange();
        }
    }

    protected void prevPage() {
        if (pagingListener != null && getAdapter() != null && getCurrentItem() <= 0) {
            pagingListener.onFirstPage();
        } else {
            setCurrentItem(getCurrentItem() - 1, false);
            onPageChange();
        }
    }

    protected void upPage() {
        prevPage();
    }

    protected void downPage() {
        nextPage();
    }

    private void onPageChange() {
        if (pagingListener != null && getAdapter() != null) {
            pagingListener.onPageChange(getCurrentItem(), getAdapter().getCount());
        }
    }

    protected int detectDirection(MotionEvent currentEvent) {
        return PageTurningDetector.detectBothAxisTuring(getContext(), (int) (currentEvent.getX() - lastX), (int) (currentEvent.getY() - lastY));
    }

    public static class OnPagingListener {
        public void onPageChange(int position, int pageSize) {
        }

        public void onFirstPage() {
        }

        public void onLastPage() {
        }
    }
}
