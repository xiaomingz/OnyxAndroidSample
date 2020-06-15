package com.onyx.gallery.databinding

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import com.onyx.gallery.R

/**
 * Created by Leung on 2020/5/6
 */

object ViewAdapter {

    @JvmStatic
    @BindingAdapter("setTextViewTypeface")
    fun setTextViewTypeface(textView: TextView, typeface: Typeface?) {
        textView.typeface = typeface
    }

    @JvmStatic
    @BindingAdapter("checkedOrUnChecked")
    fun setCheckedTextViewCheckedOrUnChecked(checkedTextView: CheckedTextView, isChecked: Boolean) {
        checkedTextView.isChecked = isChecked
    }

    @JvmStatic
    @BindingAdapter("setLeftHint")
    fun setLeftHint(view: TextView, res: Int) {
        view.setBackgroundResource(R.drawable.bg_img_click_solid)
        view.setCompoundDrawablesWithIntrinsicBounds(
                getHintDrawable(view.context, res),
                null,
                null,
                null
        )
    }


    @JvmStatic
    @BindingAdapter("visibleOrGone")
    fun setViewVisibleOrGone(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("visibleOrInvisible")
    fun setViewVisibleOrInvisible(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    @JvmStatic
    @BindingAdapter("setViewActivated")
    fun setActivatedByView(view: View, isActivated: Boolean) {
        view.isActivated = isActivated
    }

    @JvmStatic
    @BindingAdapter("setHint")
    fun setHint(view: ImageView, res: Int) {
        view.setBackgroundResource(R.drawable.bg_img_click_solid)
        view.setImageDrawable(getHintDrawable(view.context, res))
    }

    private fun getHintDrawable(context: Context, res: Int): Drawable? {
        return getHintDrawable(context, res, R.color.background_black, R.color.white)
    }

    private fun getHintDrawable(context: Context, res: Int, imgColorRes: Int, imgPressColorRes: Int): Drawable? {
        val colors = intArrayOf(
                ContextCompat.getColor(context, imgPressColorRes),
                ContextCompat.getColor(context, imgColorRes))
        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(android.R.attr.state_pressed)
        states[1] = intArrayOf()
        return createDrawable(context, res, colors, states)
    }

    private fun createDrawable(context: Context, res: Int, colors: IntArray, states: Array<IntArray?>): Drawable? {
        val drawable = ContextCompat.getDrawable(context, res)
        drawable?.let {
            var drawable: Drawable = DrawableCompat.wrap(it)
            val colorStateList = ColorStateList(states, colors)
            val stateListDrawable = StateListDrawable()
            for (state in states) {
                stateListDrawable.addState(state, drawable)
            }
            val state = stateListDrawable.constantState
            drawable = DrawableCompat.wrap(if (state == null) drawable else state.newDrawable()).mutate()
            DrawableCompat.setTintList(drawable, colorStateList)
            return drawable
        }
        return null
    }

}