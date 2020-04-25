package com.onyx.calculator;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;


import java.util.Hashtable;
import java.util.Map;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by suicheng on 2016/6/27.
 */
public class PageRecyclerView extends RecyclerView {

    public static final String NEXT_PAGE = "nextPage";
    public static final String PREV_PAGE = "prevPage";
    public static final String MOVE_LEFT = "moveLeft";
    public static final String MOVE_RIGHT = "moveRight";
    public static final String MOVE_UP = "moveUp";
    public static final String MOVE_DOWN = "moveDown";

    private static final String TAG = PageRecyclerView.class.getSimpleName();
    private GPaginator paginator;
    public enum TouchDirection {Horizontal, Vertical}
    private int currentFocusedPosition = - 1;
    private OnPagingListener onPagingListener;
    private int rows = 0;
    private int columns = 1;
    private float lastX, lastY;
    private OnChangeFocusListener onChangeFocusListener;
    private Map<Integer, String> keyBindingMap = new Hashtable<>();
    private int originPaddingBottom;
    private int itemDecorationHeight = 0;
    private boolean pageTurningCycled = false;

    public interface OnPagingListener {
        void onPageChange(int position, int itemCount, int pageSize);
    }

    public interface OnChangeFocusListener {
        void onFocusChange(int prev, int current);
    }

    public PageRecyclerView(Context context) {
        super(context);
        init();
    }

    public PageRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PageRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setCurrentFocusedPosition(int currentFocusedPosition) {
        int lastFocusedPosition = this.currentFocusedPosition;
        this.currentFocusedPosition = currentFocusedPosition;
        getAdapter().notifyItemChanged(lastFocusedPosition);
        getAdapter().notifyItemChanged(currentFocusedPosition);
        if (onChangeFocusListener != null){
            onChangeFocusListener.onFocusChange(lastFocusedPosition, currentFocusedPosition);
        }
    }

    public int getItemDecorationHeight() {
        return itemDecorationHeight;
    }

    public void setItemDecorationHeight(int itemDecorationHeight) {
        this.itemDecorationHeight = itemDecorationHeight;
    }

    public int getCurrentFocusedPosition() {
        return currentFocusedPosition;
    }

    private void nextFocus(int focusedPosition){
        if (!paginator.isItemInCurrentPage(focusedPosition)){
            nextPage();
        }
        setCurrentFocusedPosition(focusedPosition);
    }

    public int getOriginPaddingBottom() {
        return originPaddingBottom;
    }

    private void prevFocus(int focusedPosition){
        if (!paginator.isItemInCurrentPage(focusedPosition)){
            prevPage();
        }
        setCurrentFocusedPosition(focusedPosition);
    }

    public void nextColumn(){
        int focusedPosition = paginator.nextColumn(currentFocusedPosition);
        if (focusedPosition < paginator.getSize()){
            nextFocus(focusedPosition);
        }
    }

    public void prevColumn(){
        int focusedPosition = paginator.prevColumn(currentFocusedPosition);
        if (focusedPosition >= 0){
            prevFocus(focusedPosition);
        }
    }

    public void nextRow(){
        int focusedPosition = paginator.nextRow(currentFocusedPosition);
        if (focusedPosition < paginator.getSize()){
            nextFocus(focusedPosition);
        }
    }

    public void prevRow(){
        int focusedPosition = paginator.prevRow(currentFocusedPosition);
        if (focusedPosition >= 0){
            prevFocus(focusedPosition);
        }
    }

    public void setOnChangeFocusListener(OnChangeFocusListener onChangeFocusListener) {
        this.onChangeFocusListener = onChangeFocusListener;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (!(adapter instanceof PageAdapter)){
            throw new IllegalArgumentException("Please use PageAdapter!");
        }
        rows = ((PageAdapter) adapter).getRowCount();
        columns = ((PageAdapter) adapter).getColumnCount();
        int size = ((PageAdapter) adapter).getDataCount();
        paginator = new GPaginator(rows, columns,size);
        paginator.setCurrentPage(0);

        LayoutManager layoutManager = getDisableLayoutManager();
        if (layoutManager instanceof GridLayoutManager){
            ((GridLayoutManager) layoutManager).setSpanCount(columns);
        }
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        originPaddingBottom = bottom;
    }

    public void setOffsetPaddingBottom(int offsetBottom) {
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), offsetBottom);
    }

    public PageAdapter getPageAdapter() {
        PageAdapter pageAdapter = (PageAdapter) getAdapter();
        return pageAdapter;
    }

    public void resize(int newRows, int newColumns, int newSize) {
        if (paginator != null) {
            paginator.resize(newRows,newColumns,newSize);
        }
    }

    public void setCurrentPage(int currentPage) {
        if (paginator != null) {
            paginator.setCurrentPage(currentPage);
        }
    }

    public GPaginator getPaginator() {
        return paginator;
    }

    private void init() {
        originPaddingBottom = getPaddingBottom();
        setItemAnimator(null);
        setClipToPadding(true);
        setClipChildren(true);
        setLayoutManager(new DisableScrollLinearManager(getContext(), LinearLayoutManager.VERTICAL, false));
        setDefaultPageKeyBinding();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return processKeyAction(event) || super.dispatchKeyEvent(event);
    }

    private TouchDirection touchDirection = TouchDirection.Vertical;

    private int detectDirection(MotionEvent currentEvent) {
        return PageTurningDetector.detectBothAxisTuring(getContext(), (int) (currentEvent.getX() - lastX), (int) (currentEvent.getY() - lastY));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
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
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                int direction = detectDirection(ev);
                if (direction == PageTurningDirection.NEXT) {
                    nextPage();
                    return true;
                } else if (direction == PageTurningDirection.PREV) {
                    prevPage();
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    private boolean processKeyAction(KeyEvent event){
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return false;
        }
        final String args = keyBindingMap.get(event.getKeyCode());
        if (args == null){
            return false;
        }
        switch (args){
            case NEXT_PAGE:
                nextPage();
                break;
            case PREV_PAGE:
                prevPage();
                break;
            case MOVE_LEFT:
                prevColumn();
                break;
            case MOVE_RIGHT:
                nextColumn();
                break;
            case MOVE_DOWN:
                prevRow();
                break;
            case MOVE_UP:
                nextRow();
                break;
            default:
                nextPage();
        }
        return true;
    }

    public void setKeyBinding(Map<Integer, String> keyBindingMap){
        this.keyBindingMap = keyBindingMap;
    }

    public void setDefaultPageKeyBinding(){
        keyBindingMap.put(KeyEvent.KEYCODE_PAGE_DOWN, NEXT_PAGE);
        keyBindingMap.put(KeyEvent.KEYCODE_VOLUME_DOWN, NEXT_PAGE);
        keyBindingMap.put(KeyEvent.KEYCODE_PAGE_UP, PREV_PAGE);
        keyBindingMap.put(KeyEvent.KEYCODE_VOLUME_UP, PREV_PAGE);
    }

    public void setDefaultMoveKeyBinding(){
        keyBindingMap.put(KeyEvent.KEYCODE_PAGE_DOWN, MOVE_RIGHT);
        keyBindingMap.put(KeyEvent.KEYCODE_VOLUME_DOWN, MOVE_RIGHT);
        keyBindingMap.put(KeyEvent.KEYCODE_PAGE_UP, MOVE_LEFT);
        keyBindingMap.put(KeyEvent.KEYCODE_VOLUME_UP, MOVE_LEFT);
    }

    public void setOnPagingListener(OnPagingListener listener) {
        this.onPagingListener = listener;
    }

    public boolean isPageTurningCycled() {
        return pageTurningCycled;
    }

    public void setPageTurningCycled(boolean cycled) {
        this.pageTurningCycled = cycled;
    }

    public void prevPage() {
        if (paginator.prevPage()){
            onPageChange();
            return;
        }

        if (pageTurningCycled && paginator.pages() > 1 && paginator.isFirstPage()) {
            gotoPage(paginator.lastPage());
        }
    }

    public void nextPage() {
        if (paginator.nextPage()){
            onPageChange();
            return;
        }

        if (pageTurningCycled && paginator.pages() > 1 && paginator.isLastPage()) {
            gotoPage(0);
        }
    }

    public void gotoPage(int page) {
        if (paginator.gotoPage(page)) {
            onPageChange();
        }
    }

    public void gotoPageByIndex(final int index) {
        if (paginator.gotoPageByIndex(index)) {
            onPageChange();
        }
    }

    private void onPageChange() {
        int position =  paginator.getCurrentPageBegin();
        if (!paginator.isItemInCurrentPage(currentFocusedPosition)){
            setCurrentFocusedPosition(position);
        }
        managerScrollToPosition(position);
        if (onPagingListener != null){
            onPagingListener.onPageChange(position,getAdapter().getItemCount(), rows * columns);
        }
    }

    public void notifyDataSetChanged() {
        PageAdapter pageAdapter = getPageAdapter();
        if (pageAdapter == null) {
            return;
        }
        int gotoPage = paginator.getCurrentPage() == -1 ? 0 : paginator.getCurrentPage();
        resize(pageAdapter.getRowCount(), pageAdapter.getColumnCount(), getPageAdapter().getDataCount());
        if (gotoPage > getPaginator().lastPage()) {
            gotoPage(getPaginator().lastPage());
        }

        pageAdapter.notifyDataSetChanged();
    }

    private void managerScrollToPosition(int position) {
        getDisableLayoutManager().scrollToPositionWithOffset(position, 0);
    }

    private LinearLayoutManager getDisableLayoutManager(){
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        if (layoutManager instanceof DisableScrollLinearManager){
            layoutManager = (DisableScrollLinearManager) getLayoutManager();
        }
        return layoutManager;
    }

    private boolean isClipView(Rect rect, View view) {
        switch (touchDirection) {
            case Horizontal:
                return (rect.right - rect.left) < view.getWidth();
            case Vertical:
                return (rect.bottom - rect.top) < view.getHeight();
        }
        return false;
    }

    public static abstract class PageAdapter<VH extends ViewHolder> extends Adapter<VH>{

        protected PageRecyclerView pageRecyclerView;

        public abstract int getRowCount();
        public abstract int getColumnCount();
        public abstract int getDataCount();
        public abstract VH onPageCreateViewHolder(ViewGroup parent, int viewType);
        public abstract void onPageBindViewHolder(VH holder, int position);

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            pageRecyclerView = (PageRecyclerView) parent;
            return onPageCreateViewHolder(parent,viewType);
        }

        @Override
        public void onBindViewHolder(final VH holder, final int position) {
            final int adapterPosition = holder.getAdapterPosition();
            final View view = holder.itemView;
            if (view != null){
                if (position < getDataCount()){
                    view.setVisibility(VISIBLE);
                    view.setFocusable(true);
                    setupListener(view,adapterPosition);
                    updateFocusView(view,adapterPosition);
                    if (getPagePaginator().offsetInCurrentPage(position) == 0) {
                        view.requestFocus();
                    }
                    onPageBindViewHolder(holder,adapterPosition);
                }else {
                    view.setFocusable(false);
                    view.setVisibility(INVISIBLE);
                }

                adjustParentViewLayout(holder);
            }
        }

        protected void adjustParentViewLayout(final VH holder) {
            if (pageRecyclerView.getLayoutParams().height == WRAP_CONTENT) {
                return;
            }
            final int paddingBottom = pageRecyclerView.getOriginPaddingBottom();
            final int paddingTop = pageRecyclerView.getPaddingTop();
            final int parentViewHeight;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                parentViewHeight = pageRecyclerView.getLayoutParams().height > 0 ?
                        pageRecyclerView.getLayoutParams().height : pageRecyclerView.getHeight();
            } else {
                parentViewHeight = pageRecyclerView.getMeasuredHeight();
            }
            int parentHeight = parentViewHeight - paddingBottom - paddingTop - getRowCount() * pageRecyclerView.getItemDecorationHeight();
            double itemHeight =  ((double)parentHeight) / getRowCount();
            if (itemHeight > 0) {
                int actualHeight = (int)Math.floor(itemHeight);
                int deviation = parentHeight - actualHeight * getRowCount();
                holder.itemView.setLayoutParams(new AbsListView.LayoutParams(MATCH_PARENT, actualHeight));
                setParentHeightDeviation(deviation);
            }
        }

        /**
         * For a fixed height layout, when the division of a single item when the height of the time,
         * will lead to a certain error occurs, this function is to fill the error
         *
         * @param deviation deviation
         * @return
         */
        private void setParentHeightDeviation(int deviation) {
            int bottom = pageRecyclerView.getPaddingBottom();
            int offsetBottom = pageRecyclerView.getOriginPaddingBottom() + deviation;
            if (offsetBottom != bottom) {
                pageRecyclerView.setOffsetPaddingBottom(offsetBottom);
            }
        }

        @Override
        public int getItemCount() {
            int itemCountOfPage = getRowCount() * getColumnCount();
            int size = getDataCount();
            if (size != 0){
                int remainder = size % itemCountOfPage;
                if (remainder > 0){
                    int blankCount =  itemCountOfPage - remainder;
                    size=  size + blankCount;
                }
            }
            if (pageRecyclerView != null){
                pageRecyclerView.resize(getRowCount(),getColumnCount(),getDataCount());
            }
            return size;
        }

        private void updateFocusView(final View view, final int position){
            if (position == pageRecyclerView.getCurrentFocusedPosition()){
                view.setActivated(true);
            }else {
                view.setActivated(false);
            }
        }

        private void setupListener(final View view, final int position) {
            view.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN){
                        pageRecyclerView.setCurrentFocusedPosition(position);
                    }
                    return false;
                }
            });

            view.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if (!getPagePaginator().isItemInCurrentPage(position)) {
                            getPageRecyclerView().gotoPage(getPagePaginator().pageByIndex(position));
                        }
                    }
                }
            });

        }

        public PageRecyclerView getPageRecyclerView() {
            return pageRecyclerView;
        }

        public GPaginator getPagePaginator() {
            return getPageRecyclerView().getPaginator();
        }

    }
}