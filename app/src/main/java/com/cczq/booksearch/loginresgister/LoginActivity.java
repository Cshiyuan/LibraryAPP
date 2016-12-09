package com.cczq.booksearch.loginresgister;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cczq.booksearch.R;
import com.cczq.booksearch.AppController;
import com.cczq.booksearch.MainActivity;
import com.cczq.booksearch.loginresgister.utils.SessionManager;
import com.cczq.booksearch.utils.configURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 登陆的Activity
 * Created by Shyuan on 2016/9/23.
 */

public class LoginActivity extends Activity {

    //便于logCat tag输出的tag
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button loginBtn;
    private Button linkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private SweetAlertDialog progressDialog;
    private SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.btnLogin);
        linkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        //初始化progressDialog
        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText("Logging in ...");
        progressDialog.setCancelable(false);

        //初始化SessionManager
        sessionManager = new SessionManager(getApplicationContext());

        //检查用户是否已登陆
        if (sessionManager.isLoggedIn()) {
            //如果用户已登陆，跳转到MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        //登陆按钮被点击
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                // 检查是否输入空值
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        //跳转到注册页面
        linkToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                // finish();
            }
        });
    }

    /**
     * 检查登陆认证
     *
     * @param email
     * @param password
     */
    private void checkLogin(final String email, final String password) {

        //用来取消请求
        String tag_string_req = "req_login";
        //显示进度窗口
        showDialog();

        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int ret = jObj.getInt("ret");
                    JSONObject data = jObj.getJSONObject("data");
                    int code = data.getInt("code");

                    //检查error节点
                    if (ret == 200 && code == 200) {
                        //用户成功登陆
                        //设置已登陆
                        sessionManager.setLogin(true, data.getInt("info"));
                        //开始主要活动
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);

                        //开始跳转
                        startActivity(intent);
                        finish();
                    } else {
                        //错误信息
                        String errorMsg = jObj.getString("msg") + data.getString("msg");
                        //显示错误信息
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // 抛出JSON的错误Exception
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //消失进度窗口
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // POST的参数到LoginURl服务器
                Map<String, String> params = new HashMap<String, String>();
                //params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        //添加到请求队列
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    /**
     * 显示进度窗口
     */
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    /**
     * 隐藏进度窗口
     */
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
