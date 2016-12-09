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
import com.cczq.booksearch.AppController;
import com.cczq.booksearch.MainActivity;
import com.cczq.booksearch.R;
import com.cczq.booksearch.loginresgister.utils.SessionManager;
import com.cczq.booksearch.utils.CheckUtils;
import com.cczq.booksearch.utils.SQLiteHandler;
import com.cczq.booksearch.utils.configURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 注册的Resgister
 * Created by Shyuan on 2016/9/23.
 */

public class RegisterActivity extends Activity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private SessionManager sessionManager;
    private SQLiteHandler db;

    private SweetAlertDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText("Reg ...");
        progressDialog.setCancelable(false);

        // Session manager
        sessionManager = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // 检查用户是否已登陆
        if (sessionManager.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // 注册按钮监听器
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();


                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && CheckUtils.isEmail(email)) {
                    //进行用户注册


                    registerUser(name, email, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "请填写正确的信息（邮箱格式）", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // 点击跳转到登陆界面
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * 存储用户信息在本地，并且提交注册信息到注册的api
     *
     * @param name
     * @param email
     * @param password
     */
    private void registerUser(final String name, final String email,
                              final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                //隐藏进度条
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int ret = jObj.getInt("ret");
                    JSONObject data = jObj.getJSONObject("data");
                    int code = data.getInt("code");

                    if (ret == 200 && code == 200) {
                        //注册成功

                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("注册成功！")
                                .setContentText("确认!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        // 跳转到登陆界面
                                        sDialog.dismissWithAnimation();
                                        Intent intent = new Intent(
                                                RegisterActivity.this,
                                                LoginActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                })
                                .show();

                    } else {

                        //显示错误信息
                        String errorMsg = jObj.getString("msg") + data.getString("msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //隐藏进度条
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // POST的参数
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("username", name);
                params.put("email", email);
                params.put("password", password);
                return params;
            }

        };

        // 加入到请求队列中
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * 显示进度条对话框
     */
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private Activity getActivity() {
        return this;
    }
}
