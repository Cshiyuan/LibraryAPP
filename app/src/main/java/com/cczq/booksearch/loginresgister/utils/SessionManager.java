package com.cczq.booksearch.loginresgister.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 利用SharedPreference判断是否已登陆的类
 * Created by Shyuan on 2016/9/23.
 */

public class SessionManager {

    //LogCat Preferences
    private static String TAG = SessionManager.class.getSimpleName();
    //Shared Preferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    //Shared pref mode
    int PRIVATE_MODE = 0;

    //Shared Preferences 文件名
    private static final String PREF_NAME = "AndroidHiveLogin";
    //存放在SharedPrefernces的判断是否登陆的KEY名
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_IS_UID = "UID";

    /**
     * SessionManager的构造函数
     *
     * @param context 上下文
     */
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * 设置是否已登陆的信息,并且将登陆用户的UID写入
     *
     * @param isLoggedIn 为布尔值
     */
    public void setLogin(boolean isLoggedIn, int UID) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        //将用户UID写入
        editor.putInt(KEY_IS_UID, UID);
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    /**
     * 判断是否已登陆，默认返回false未登陆
     *
     * @return
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    /**
     * 返回已登陆用户的UID
     *
     * @return
     */
    public int UID() {
        return pref.getInt(KEY_IS_UID, -1);
    }

}
