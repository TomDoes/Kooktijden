package com.tomdoesburg.kooktijden;

/**
 * Created by Joost on 29-8-2014.
 */
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

public class MyViewPager extends ViewPager {

    static boolean swipingEnabled;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.swipingEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.swipingEnabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.swipingEnabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }



}