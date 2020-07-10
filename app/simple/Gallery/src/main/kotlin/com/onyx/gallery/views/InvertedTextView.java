package com.onyx.gallery.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.onyx.gallery.R;

/**
 * @author Kaiguang
 * @Description
 * @Time 2019/1/8
 */
public class InvertedTextView extends AppCompatTextView {

    private int DEFAULT_TEXT_COLOR = Color.BLACK;
    private int DEFAULT_INVERTED_COLOR = Color.WHITE;

    private int textNormalColor;
    private int invertedTextColor;

    public InvertedTextView(Context context) {
        this(context, null);
    }

    public InvertedTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public InvertedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.inverted_text_view);
        textNormalColor = array.getInteger(R.styleable.inverted_text_view_textNormalColor, DEFAULT_TEXT_COLOR);
        invertedTextColor = array.getInteger(R.styleable.inverted_text_view_invertedTextColor, DEFAULT_INVERTED_COLOR);
        setClickable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isActivated()) {
            return super.onTouchEvent(event);
        }

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setTextColor(invertedTextColor);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setTextColor(textNormalColor);
                break;
            case MotionEvent.ACTION_MOVE:
                float pointX = event.getX();
                float pointY = event.getY();
                if(pointX<0 || pointY<0 || pointX>getWidth() || pointY>getHeight()) {
                    setTextColor(textNormalColor);
                }
                break;
            default:
        }
        return super.onTouchEvent(event);
    }
}
