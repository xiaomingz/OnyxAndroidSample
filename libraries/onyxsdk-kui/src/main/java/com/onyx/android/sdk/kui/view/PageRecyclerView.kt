package com.onyx.android.sdk.kui.view

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.*
import android.view.View.OnFocusChangeListener
import android.widget.AbsListView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onyx.android.sdk.data.GPaginator
import com.onyx.android.sdk.data.KeyAction
import com.onyx.android.sdk.kui.R
import com.onyx.android.sdk.kui.helper.OnyxLinearSnapHelper
import java.util.*

class PageRecyclerView : RecyclerView {
    var paginator: GPaginator? = null

    enum class TouchDirection {
        Horizontal, Vertical
    }

    private var currentFocusedPosition = -1
    private var onPagingListener: OnPagingListener? = null
    private var rows = 0
    private var columns = 1
    private var lastX = 0f
    private var lastY = 0f
    private var onChangeFocusListener: OnChangeFocusListener? = null
    private var keyBindingMap: MutableMap<Int, String> = Hashtable()
    var originPaddingBottom = 0
        private set
    var itemDecorationHeight = 0
    var isPageTurningCycled = false
    private var canTouchPageTurning = true

    private val AUTO_SCROLL_DELAY = 25L
    private var isZoomEnabled = false
    private var isDragSelectionEnabled = false
    private var zoomListener: MyZoomListener? = null
    private var dragListener: MyDragListener? = null
    private var autoScrollHandler = Handler()

    private var scaleDetector: ScaleGestureDetector

    private var dragSelectActive = false
    private var lastDraggedIndex = -1
    private var minReached = 0
    private var maxReached = 0
    private var initialSelection = 0

    private var hotspotHeight = 0
    private var hotspotOffsetTop = 0
    private var hotspotOffsetBottom = 0

    private var hotspotTopBoundStart = 0
    private var hotspotTopBoundEnd = 0
    private var hotspotBottomBoundStart = 0
    private var hotspotBottomBoundEnd = 0
    private var autoScrollVelocity = 0

    private var inTopHotspot = false
    private var inBottomHotspot = false

    private var currScaleFactor = 1.0f
    private var lastUp = 0L    // allow only pinch zoom, not double tap

    // things related to parallax scrolling (for now only in the music player)
    // cut from https://github.com/ksoichiro/Android-ObservableScrollView
    var recyclerScrollCallback: RecyclerScrollCallback? = null
    private var mPrevFirstVisiblePosition = 0
    private var mPrevScrolledChildrenHeight = 0
    private var mPrevFirstVisibleChildHeight = -1
    private var mScrollY = 0

    // variables used for fetching additional items at scrolling to the bottom/top
    var endlessScrollListener: EndlessScrollListener? = null
    private var totalItemCount = 0
    private var lastMaxItemIndex = 0
    private var linearLayoutManager: LinearLayoutManager? = null
    private var linearSnapHelper = OnyxLinearSnapHelper()

    init {
        hotspotHeight = context.resources.getDimensionPixelSize(R.dimen.dragselect_hotspot_height)

        if (layoutManager is LinearLayoutManager) {
            linearLayoutManager = layoutManager as LinearLayoutManager
        }

        val gestureListener = object : MyGestureListener {
            override fun getLastUp() = lastUp

            override fun getScaleFactor() = currScaleFactor

            override fun setScaleFactor(value: Float) {
                currScaleFactor = value
            }

            override fun getZoomListener() = zoomListener
        }

        scaleDetector = ScaleGestureDetector(context, GestureListener(gestureListener))
        linearSnapHelper.attachToRecyclerView(this)
    }

    abstract class OnPagingListener {
        abstract fun onPageChange(position: Int, itemCount: Int, pageSize: Int)
        fun onFirstPage() {}
        fun onLastPage() {}
    }

    interface OnChangeFocusListener {
        fun onFocusChange(prev: Int, current: Int)
    }

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        init()
    }

    fun setCurrentFocusedPosition(currentFocusedPosition: Int) {
        val lastFocusedPosition = this.currentFocusedPosition
        this.currentFocusedPosition = currentFocusedPosition
        adapter!!.notifyItemChanged(lastFocusedPosition)
        adapter!!.notifyItemChanged(currentFocusedPosition)
        if (onChangeFocusListener != null) {
            onChangeFocusListener!!.onFocusChange(lastFocusedPosition, currentFocusedPosition)
        }
    }

    fun getCurrentFocusedPosition(): Int {
        return currentFocusedPosition
    }

    private fun nextFocus(focusedPosition: Int) {
        if (!paginator!!.isItemInCurrentPage(focusedPosition)) {
            nextPage()
        }
        setCurrentFocusedPosition(focusedPosition)
    }

    private fun prevFocus(focusedPosition: Int) {
        if (!paginator!!.isItemInCurrentPage(focusedPosition)) {
            prevPage()
        }
        setCurrentFocusedPosition(focusedPosition)
    }

    fun nextColumn() {
        val focusedPosition = paginator!!.nextColumn(currentFocusedPosition)
        if (focusedPosition < paginator!!.size) {
            nextFocus(focusedPosition)
        }
    }

    fun prevColumn() {
        val focusedPosition = paginator!!.prevColumn(currentFocusedPosition)
        if (focusedPosition >= 0) {
            prevFocus(focusedPosition)
        }
    }

    fun nextRow() {
        val focusedPosition = paginator!!.nextRow(currentFocusedPosition)
        if (focusedPosition < paginator!!.size) {
            nextFocus(focusedPosition)
        }
    }

    fun prevRow() {
        val focusedPosition = paginator!!.prevRow(currentFocusedPosition)
        if (focusedPosition >= 0) {
            prevFocus(focusedPosition)
        }
    }

    fun setOnChangeFocusListener(onChangeFocusListener: OnChangeFocusListener?) {
        this.onChangeFocusListener = onChangeFocusListener
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        require(adapter is PageAdapter<*>) { "Please use PageAdapter!" }
        initAdapter(adapter as PageAdapter<*>?)
    }

    private fun initAdapter(adapter: PageAdapter<*>?) {
        initPaginator(adapter)
        initRecycledViewPool()
        initSpanCount()
    }

    private fun initSpanCount() {
        val layoutManager: LayoutManager? = disableLayoutManager
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanCount = columns
        }
    }

    private fun initPaginator(adapter: PageAdapter<*>?) {
        rows = adapter!!.rowCount
        columns = adapter.columnCount
        val size = adapter.dataCount()
        if (paginator == null) {
            paginator = GPaginator(rows, columns, size)
        }
        paginator!!.resize(rows, columns, size)
        paginator!!.currentPage = 0
    }

    private fun initRecycledViewPool() {
        val recycledViewSize = Math.max(5, rows * columns)
        recycledViewPool.setMaxRecycledViews(0, recycledViewSize)
        setRecycledViewPool(recycledViewPool)
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        originPaddingBottom = bottom
    }

    fun setOffsetPaddingBottom(offsetBottom: Int) {
        setPadding(paddingLeft, paddingTop, paddingRight, offsetBottom)
    }

    val pageAdapter: PageAdapter<*>?
        get() = adapter as PageAdapter<*>?

    fun resize(newRows: Int, newColumns: Int, newSize: Int) {
        if (paginator != null) {
            paginator!!.resize(newRows, newColumns, newSize)
        }
    }

    fun setCurrentPage(currentPage: Int) {
        if (paginator != null) {
            paginator!!.currentPage = currentPage
        }
    }

    private fun init() {
        originPaddingBottom = paddingBottom
        itemAnimator = null
        clipToPadding = true
        clipChildren = true
        layoutManager = DisableScrollLinearManager(context, LinearLayoutManager.VERTICAL, false)
        setDefaultPageKeyBinding()
    }

    fun reset() {
        init()
        initAdapter(pageAdapter)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return processKeyAction(event) || super.dispatchKeyEvent(event)
    }

    private val touchDirection = TouchDirection.Vertical
    private fun detectDirection(currentEvent: MotionEvent): Int {
        return PageTurningDetector.detectBothAxisTuring(context, (currentEvent.x - lastX).toInt(), (currentEvent.y - lastY).toInt())
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.x
                lastY = ev.y
            }
            MotionEvent.ACTION_MOVE -> return detectDirection(ev) != PageTurningDirection.NONE
            else -> {
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.x
                lastY = ev.y
            }
            MotionEvent.ACTION_UP -> {
                if (!hasWindowFocus()) return super.onTouchEvent(ev)
                val direction = detectDirection(ev)
                if (direction == PageTurningDirection.NEXT) {
                    if (canTouchPageTurning) {
                        nextPage()
                        return true
                    }
                } else if (direction == PageTurningDirection.PREV) {
                    if (canTouchPageTurning) {
                        prevPage()
                        return true
                    }
                }
            }
            else -> {
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun processKeyAction(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP) {
            return false
        }
        val args = keyBindingMap[event.keyCode] ?: return false
        when (args) {
            KeyAction.NEXT_PAGE -> nextPage()
            KeyAction.PREV_PAGE -> prevPage()
            KeyAction.MOVE_LEFT -> prevColumn()
            KeyAction.MOVE_RIGHT -> nextColumn()
            KeyAction.MOVE_DOWN -> prevRow()
            KeyAction.MOVE_UP -> nextRow()
            else -> nextPage()
        }
        return true
    }

    fun setKeyBinding(keyBindingMap: MutableMap<Int, String>) {
        this.keyBindingMap = keyBindingMap
    }

    fun setDefaultPageKeyBinding() {
        keyBindingMap[KeyEvent.KEYCODE_PAGE_DOWN] = KeyAction.NEXT_PAGE
        keyBindingMap[KeyEvent.KEYCODE_VOLUME_DOWN] = KeyAction.NEXT_PAGE
        keyBindingMap[KeyEvent.KEYCODE_PAGE_UP] = KeyAction.PREV_PAGE
        keyBindingMap[KeyEvent.KEYCODE_VOLUME_UP] = KeyAction.PREV_PAGE
    }

    fun setDefaultMoveKeyBinding() {
        keyBindingMap[KeyEvent.KEYCODE_PAGE_DOWN] = KeyAction.MOVE_RIGHT
        keyBindingMap[KeyEvent.KEYCODE_VOLUME_DOWN] = KeyAction.MOVE_RIGHT
        keyBindingMap[KeyEvent.KEYCODE_PAGE_UP] = KeyAction.MOVE_LEFT
        keyBindingMap[KeyEvent.KEYCODE_VOLUME_UP] = KeyAction.MOVE_LEFT
    }

    fun setOnPagingListener(listener: OnPagingListener?) {
        onPagingListener = listener
    }

    fun setCanTouchPageTurning(canTouchPageTurning: Boolean) {
        this.canTouchPageTurning = canTouchPageTurning
    }

    fun prevPage() {
        if (paginator == null) {
            return
        }
        if (paginator!!.prevPage()) {
            onPageChange()
            return
        }
        if (isPageTurningCycled && paginator!!.pages() > 1 && paginator!!.isFirstPage) {
            gotoPage(paginator!!.lastPage())
        }
        if (onPagingListener != null) {
            onPagingListener!!.onFirstPage()
        }
    }

    fun nextPage() {
        if (paginator == null) {
            return
        }
        if (paginator!!.nextPage()) {
            onPageChange()
            return
        }
        if (isPageTurningCycled && paginator!!.pages() > 1 && paginator!!.isLastPage) {
            gotoPage(0)
        }
        if (onPagingListener != null) {
            onPagingListener!!.onLastPage()
        }
    }

    fun gotoPage(page: Int) {
        if (paginator!!.gotoPage(page)) {
            onPageChange()
        }
    }

    fun gotoPageByIndex(index: Int) {
        if (paginator!!.gotoPageByIndex(index)) {
            onPageChange()
        }
    }

    private fun onPageChange() {
        val position = paginator!!.currentPageBegin
        if (!paginator!!.isItemInCurrentPage(currentFocusedPosition)) {
            setCurrentFocusedPosition(position)
        }
        managerScrollToPosition(position)
        if (onPagingListener != null) {
            onPagingListener!!.onPageChange(position, adapter!!.itemCount, rows * columns)
        }
    }

    fun notifyDataSetChanged() {
        val pageAdapter = pageAdapter ?: return
        val gotoPage = if (paginator!!.currentPage == -1) 0 else paginator!!.currentPage
        resize(pageAdapter.rowCount, pageAdapter.columnCount, pageAdapter.dataCount())
        if (gotoPage > paginator!!.lastPage()) {
            gotoPage(paginator!!.lastPage())
        }
        pageAdapter.notifyDataSetChanged()
    }

    fun notifyCurrentPageChanged() {
        if (adapter == null
                || paginator == null) {
            return
        }
        adapter!!.notifyItemRangeChanged(paginator!!.currentPageBegin, paginator!!.itemsInCurrentPage())
    }

    private fun managerScrollToPosition(position: Int) {
        disableLayoutManager!!.scrollToPositionWithOffset(position, 0)
    }

    private val disableLayoutManager: LinearLayoutManager?
        private get() {
            var layoutManager = layoutManager as LinearLayoutManager?
            if (layoutManager is DisableScrollLinearManager) {
                layoutManager = getLayoutManager() as DisableScrollLinearManager?
            } else if (layoutManager is DisableScrollGridManager) {
                layoutManager = getLayoutManager() as DisableScrollGridManager?
            }
            return layoutManager
        }

    private fun isClipView(rect: Rect, view: View): Boolean {
        return when (touchDirection) {
            TouchDirection.Horizontal -> rect.right - rect.left < view.width
            TouchDirection.Vertical -> rect.bottom - rect.top < view.height
        }
        return false
    }

    abstract class PageAdapter<VH : ViewHolder?> : Adapter<VH>() {
        var pageRecyclerView: PageRecyclerView? = null
            protected set
        protected var onItemClickListener = null
        abstract val rowCount: Int
        abstract val columnCount: Int
        abstract fun dataCount(): Int
        abstract fun onPageCreateViewHolder(parent: ViewGroup?, viewType: Int): VH
        abstract fun onPageBindViewHolder(holder: VH, position: Int)
        interface OnItemClickListener {
            fun onItemClick(position: Int)
        }

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
            this.onItemClickListener = onItemClickListener as Nothing?
        }

        protected fun getRowSpan(position: Int): Int {
            return 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            pageRecyclerView = parent as PageRecyclerView
            return onPageCreateViewHolder(parent, viewType)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val adapterPosition = holder!!.adapterPosition
            val view = holder.itemView
            if (view != null) {
                if (position < dataCount()) {
                    view.visibility = View.VISIBLE
                    view.isFocusable = true
                    setupListener(view, adapterPosition)
                    updateFocusView(view, adapterPosition)
                    if (pagePaginator!!.offsetInCurrentPage(position) == 0) {
                        view.requestFocus()
                    }
                    onPageBindViewHolder(holder, adapterPosition)
                } else {
                    view.isFocusable = false
                    view.visibility = View.INVISIBLE
                }
                adjustParentViewLayout(holder, position)
            }
        }

        protected fun adjustParentViewLayout(holder: VH, position: Int) {
            if (pageRecyclerView!!.layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                return
            }
            val paddingBottom = pageRecyclerView!!.originPaddingBottom
            val paddingTop = pageRecyclerView!!.paddingTop
            val parentViewHeight: Int
            parentViewHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (pageRecyclerView!!.layoutParams.height > 0) pageRecyclerView!!.layoutParams.height else pageRecyclerView!!.height
            } else {
                pageRecyclerView!!.measuredHeight
            }
            val parentHeight = parentViewHeight - paddingBottom - paddingTop - rowCount * pageRecyclerView!!.itemDecorationHeight
            val minRowSpanItemHeight = parentHeight * 1.0f / rowCount.toDouble()
            val itemHeight = getRowSpan(position) * minRowSpanItemHeight
            if (itemHeight > 0) {
                holder!!.itemView.layoutParams = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Math.floor(itemHeight).toInt())
                val deviation = (parentHeight - Math.floor(minRowSpanItemHeight) * rowCount).toInt()
                setParentHeightDeviation(deviation)
            }
        }

        /**
         * For a fixed height layout, when the division of a single item when the height of the time,
         * will lead to a certain error occurs, this function is to fill the error
         *
         * @param deviation deviation
         * @return
         */
        private fun setParentHeightDeviation(deviation: Int) {
            val bottom = pageRecyclerView!!.paddingBottom
            val offsetBottom = pageRecyclerView!!.originPaddingBottom + deviation
            if (offsetBottom != bottom) {
                pageRecyclerView!!.setOffsetPaddingBottom(offsetBottom)
            }
        }

        override fun getItemCount(): Int {
            val itemCountOfPage = rowCount * columnCount
            var size = dataCount()
            if (size != 0) {
                val remainder = size % itemCountOfPage
                if (remainder > 0) {
                    val blankCount = itemCountOfPage - remainder
                    size = size + blankCount
                }
            }
            if (pageRecyclerView != null) {
                pageRecyclerView!!.resize(rowCount, columnCount, dataCount())
            }
            return size
        }

        private fun updateFocusView(view: View, position: Int) {
            if (position == pageRecyclerView!!.getCurrentFocusedPosition()) {
                view.isActivated = true
            } else {
                view.isActivated = false
            }
        }

        private fun setupListener(view: View, position: Int) {
            view.setOnTouchListener(object : OnTouchListener {
                var lastX = 0
                var lastY = 0
                var CLICK_THRESHOLD = 10
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        lastX = event.rawX.toInt()
                        lastY = event.rawY.toInt()
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        val currentX = event.rawX.toInt()
                        val currentY = event.rawY.toInt()
                        if (Math.abs(currentX - lastX) > CLICK_THRESHOLD || Math.abs(currentY - lastY) > CLICK_THRESHOLD) {
                            // not click done.
                            pageRecyclerView!!.setCurrentFocusedPosition(position)
                        }
                    }
                    return false
                }
            })
            view.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    if (!pagePaginator!!.isItemInCurrentPage(position)) {
                        pageRecyclerView!!.gotoPage(pagePaginator!!.pageByIndex(position))
                    }
                }
            }
        }

        val pagePaginator: GPaginator?
            get() = pageRecyclerView!!.paginator
    }

    companion object {
        private val TAG = PageRecyclerView::class.java.simpleName
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        if (hotspotHeight > -1) {
            hotspotTopBoundStart = hotspotOffsetTop
            hotspotTopBoundEnd = hotspotOffsetTop + hotspotHeight
            hotspotBottomBoundStart = measuredHeight - hotspotHeight - hotspotOffsetBottom
            hotspotBottomBoundEnd = measuredHeight - hotspotOffsetBottom
        }
    }

    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            if (inTopHotspot) {
                scrollBy(0, -autoScrollVelocity)
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY)
            } else if (inBottomHotspot) {
                scrollBy(0, autoScrollVelocity)
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY)
            }
        }
    }

    fun resetItemCount() {
        totalItemCount = 0
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!dragSelectActive) {
            try {
                super.dispatchTouchEvent(ev)
            } catch (ignored: Exception) {
            }
        }

        when (ev.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                dragSelectActive = false
                inTopHotspot = false
                inBottomHotspot = false
                autoScrollHandler.removeCallbacks(autoScrollRunnable)
                currScaleFactor = 1.0f
                lastUp = System.currentTimeMillis()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (dragSelectActive) {
                    val itemPosition = getItemPosition(ev)
                    if (hotspotHeight > -1) {
                        if (ev.y in hotspotTopBoundStart..hotspotTopBoundEnd) {
                            inBottomHotspot = false
                            if (!inTopHotspot) {
                                inTopHotspot = true
                                autoScrollHandler.removeCallbacks(autoScrollRunnable)
                                autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY)
                            }

                            val simulatedFactor = (hotspotTopBoundEnd - hotspotTopBoundStart).toFloat()
                            val simulatedY = ev.y - hotspotTopBoundStart
                            autoScrollVelocity = (simulatedFactor - simulatedY).toInt() / 2
                        } else if (ev.y in hotspotBottomBoundStart..hotspotBottomBoundEnd) {
                            inTopHotspot = false
                            if (!inBottomHotspot) {
                                inBottomHotspot = true
                                autoScrollHandler.removeCallbacks(autoScrollRunnable)
                                autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY)
                            }

                            val simulatedY = ev.y + hotspotBottomBoundEnd
                            val simulatedFactor = (hotspotBottomBoundStart + hotspotBottomBoundEnd).toFloat()
                            autoScrollVelocity = (simulatedY - simulatedFactor).toInt() / 2
                        } else if (inTopHotspot || inBottomHotspot) {
                            autoScrollHandler.removeCallbacks(autoScrollRunnable)
                            inTopHotspot = false
                            inBottomHotspot = false
                        }
                    }

                    if (itemPosition != RecyclerView.NO_POSITION && lastDraggedIndex != itemPosition) {
                        lastDraggedIndex = itemPosition
                        if (minReached == -1) {
                            minReached = lastDraggedIndex
                        }

                        if (maxReached == -1) {
                            maxReached = lastDraggedIndex
                        }

                        if (lastDraggedIndex > maxReached) {
                            maxReached = lastDraggedIndex
                        }

                        if (lastDraggedIndex < minReached) {
                            minReached = lastDraggedIndex
                        }

                        dragListener?.selectRange(initialSelection, lastDraggedIndex, minReached, maxReached)

                        if (initialSelection == lastDraggedIndex) {
                            minReached = lastDraggedIndex
                            maxReached = lastDraggedIndex
                        }
                    }

                    return true
                }
            }
        }

        return if (isZoomEnabled) {
            scaleDetector.onTouchEvent(ev)
        } else {
            true
        }
    }

    fun setupDragListener(dragListener: MyDragListener?) {
        isDragSelectionEnabled = dragListener != null
        this.dragListener = dragListener
    }

    fun setupZoomListener(zoomListener: MyZoomListener?) {
        isZoomEnabled = zoomListener != null
        this.zoomListener = zoomListener
    }

    fun setDragSelectActive(initialSelection: Int) {
        if (dragSelectActive || !isDragSelectionEnabled)
            return

        lastDraggedIndex = -1
        minReached = -1
        maxReached = -1
        this.initialSelection = initialSelection
        dragSelectActive = true
        dragListener?.selectItem(initialSelection)
    }

    private fun getItemPosition(e: MotionEvent): Int {
        val v = findChildViewUnder(e.x, e.y) ?: return RecyclerView.NO_POSITION

        if (v.tag == null || v.tag !is RecyclerView.ViewHolder) {
            return RecyclerView.NO_POSITION
        }

        val holder = v.tag as RecyclerView.ViewHolder
        return holder.adapterPosition
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (endlessScrollListener != null) {
            if (totalItemCount == 0) {
                totalItemCount = adapter!!.itemCount
            }

            if (state == SCROLL_STATE_IDLE) {
                val lastVisiblePosition = linearLayoutManager?.findLastVisibleItemPosition() ?: 0
                if (lastVisiblePosition != lastMaxItemIndex && lastVisiblePosition == totalItemCount - 1) {
                    lastMaxItemIndex = lastVisiblePosition
                    endlessScrollListener!!.updateBottom()
                }

                val firstVisiblePosition = linearLayoutManager?.findFirstVisibleItemPosition() ?: -1
                if (firstVisiblePosition == 0) {
                    endlessScrollListener!!.updateTop()
                }
            }
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (recyclerScrollCallback != null) {
            if (childCount > 0) {
                val firstVisiblePosition = getChildAdapterPosition(getChildAt(0))
                val firstVisibleChild = getChildAt(0)
                if (firstVisibleChild != null) {
                    if (mPrevFirstVisiblePosition < firstVisiblePosition) {
                        mPrevScrolledChildrenHeight += mPrevFirstVisibleChildHeight
                    }

                    if (firstVisiblePosition == 0) {
                        mPrevFirstVisibleChildHeight = firstVisibleChild.height
                        mPrevScrolledChildrenHeight = 0
                    }

                    if (mPrevFirstVisibleChildHeight < 0) {
                        mPrevFirstVisibleChildHeight = 0
                    }

                    mScrollY = mPrevScrolledChildrenHeight - firstVisibleChild.top
                    recyclerScrollCallback?.onScrolled(mScrollY)
                }
            }
        }
    }

    class GestureListener(val gestureListener: MyGestureListener) : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private val ZOOM_IN_THRESHOLD = -0.4f
        private val ZOOM_OUT_THRESHOLD = 0.15f

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            gestureListener.apply {
                if (System.currentTimeMillis() - getLastUp() < 1000)
                    return false

                val diff = getScaleFactor() - detector.scaleFactor
                if (diff < ZOOM_IN_THRESHOLD && getScaleFactor() == 1.0f) {
                    getZoomListener()?.zoomIn()
                    setScaleFactor(detector.scaleFactor)
                } else if (diff > ZOOM_OUT_THRESHOLD && getScaleFactor() == 1.0f) {
                    getZoomListener()?.zoomOut()
                    setScaleFactor(detector.scaleFactor)
                }
            }
            return false
        }
    }

    interface MyZoomListener {
        fun zoomOut()

        fun zoomIn()
    }

    interface MyDragListener {
        fun selectItem(position: Int)

        fun selectRange(initialSelection: Int, lastDraggedIndex: Int, minReached: Int, maxReached: Int)
    }

    interface MyGestureListener {
        fun getLastUp(): Long

        fun getScaleFactor(): Float

        fun setScaleFactor(value: Float)

        fun getZoomListener(): MyZoomListener?
    }

    interface EndlessScrollListener {
        fun updateTop()

        fun updateBottom()
    }

    interface RecyclerScrollCallback {
        fun onScrolled(scrollY: Int)
    }

}