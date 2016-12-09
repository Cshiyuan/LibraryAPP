package com.cczq.booksearch;


import android.app.Application;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.cczq.booksearch.utils.LruBitmapCache;
import com.fengmap.android.FMMapSDK;

/**
 * 创建一个单例模式
 * Created by Shyuan on 2016/9/23.
 */

public class AppController extends Application {

    //获得字体文件
    private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf";
    public static Typeface canaroExtraBold;

    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private static AppController Instance;

    @Override
    public void onCreate() {
        FMMapSDK.init(this, "8nDxjoveVeOIOMJ3eehu");
        super.onCreate();
        initTypeface();
        Instance = this;
    }

    //单例模式
    public static synchronized AppController getInstance() {
        return Instance;
    }

    //返回requestQueue (单例模式)
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    //返回imageLoader (单例模式)
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (imageLoader == null) {
            imageLoader = new ImageLoader(this.requestQueue, new LruBitmapCache());
        }
        return this.imageLoader;
    }

    //加入请求队列
    public <T> void addToRequestQueue(Request<T> req, String tag)
    {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    //加入请求队列
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    //取消所有正在等待的请求队列
    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

    private void initTypeface() {
        canaroExtraBold = Typeface.createFromAsset(getAssets(), CANARO_EXTRA_BOLD_PATH);
    }

}







































