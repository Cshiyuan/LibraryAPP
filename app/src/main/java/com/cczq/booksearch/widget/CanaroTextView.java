package com.cczq.booksearch.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cczq.booksearch.AppController;

/**
 * Created by Shyuan on 2016/9/23.
 */

public class CanaroTextView extends TextView {

    public CanaroTextView(Context context) {
        this(context, null);
    }

    public CanaroTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanaroTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(AppController.canaroExtraBold);
    }
}
