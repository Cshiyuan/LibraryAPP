package com.cczq.booksearch.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Shyuan on 2016/9/23.
 */

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    //所有静态的变量
    //数据库版本
    private static final int DATABASE_VERSION = 1;

    //数据库版本
    private static final String DATABASE_NAME = "missionforce";

    //登录表
    private static final String TABLE_LOGIN = "login";

    //登陆表的列名
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";

    /**
     * 构造函数，创建数据库
     * @param context
     */
    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 创建数据库
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建登陆表
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");

    }

    /**
     * 升级数据库
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果存在的话，DROP登录表
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        //重新创建数据库
        onCreate(db);
    }

    /**
     * 储存用户细节在数据库中
     * @param name
     * @param email
     * @param uid
     * @param created_at
     */
    public void addUser(String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // 用户名
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // url
        values.put(KEY_CREATED_AT, created_at); // 创建时间

        // Inserting Row
        long id = db.insert(TABLE_LOGIN, null, values);
        db.close(); // 关闭数据库链接

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * 获得用户数据从数据库中
     * @return
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }


    /**
     * 如果用户已经登陆在数据库中
     * */
    public int getRowCount() {

        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // 返回查询到的数据行数
        return rowCount;
    }

    /**
     * 删除数据库，所有数据重新创建
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        //删除所有行
        db.delete(TABLE_LOGIN, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }
}
