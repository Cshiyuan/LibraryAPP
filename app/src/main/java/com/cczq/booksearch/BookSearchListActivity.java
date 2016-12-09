package com.cczq.booksearch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cczq.booksearch.Model.Book;
import com.cczq.booksearch.adapter.BookListAdapter;
import com.cczq.booksearch.loginresgister.utils.SessionManager;
import com.cczq.booksearch.utils.SQLiteHandler;
import com.cczq.booksearch.utils.configURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

/**
 * Created by bb on 2016/12/9.
 */

public class BookSearchListActivity extends Activity implements View.OnClickListener {

    ListView list;
    EditText etKw; // 输入模糊搜索的关键词key EditText控件
    Button search_button;


    private SQLiteHandler db;
    public SessionManager session;


    private BookListAdapter bookListAdapter;
//    private List<bookinfo> listInfo = new ArrayList<bookinfo>(); // 声明一个list，动态存储要显示的信息

    private ProgressDialog pd;

    private BookSearchListActivity getInstance() {
        return this;
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booklist);



        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        //创建碎片管理器
//        fragmentManager = getSupportFragmentManager();

//        if (!session.isLoggedIn()) {
////            logoutUser();
//        }


        bookListAdapter = new BookListAdapter(this, this);

        // 绑定Layout里面的ListView
        list = (ListView) findViewById(R.id.bookListView);

        list.setAdapter(bookListAdapter);
        etKw = (EditText) findViewById(R.id.book_text);
        search_button = (Button) findViewById(R.id.search_button);
        search_button.setOnClickListener(this);
        // 新页面接收数据
        //Bundle bundle = this.getIntent().getExtras();
        // 接收bookname值
        //String con = bundle.getString("bookname");
        //PostAsyncTask task = new PostAsyncTask(); // 异步查询
        //task.execute(con);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_button: // analyser
                String con = etKw.getText().toString().trim(); // 获得模糊搜索字段
                if ("".equals(con)) {
                    Toast.makeText(this, "请输入描述", Toast.LENGTH_SHORT).show();
                    break;
                }
                bookListAdapter.bookData.clear();  //清楚原本的信息
                searchBook(con);
                break;
        }
        //说明点击的是列表项
        if(view.getId() == -1)
        {
            String slf = bookListAdapter.bookData.get((int) view.getTag()).slfName;
            String bookName =  bookListAdapter.bookData.get((int) view.getTag()).bookName;
//            Log.d("asdad", String.valueOf(view.getId()));
//            intent = new Intent(this, CheckMemberMissionActivity.class);
//            bundle = new Bundle();
////        bundle.putSerializable("group", group);
//            bundle.putInt("UID", UID);
            //提交记录
            commitRecord(bookName,slf);
//            intent.putExtras(bundle);
//            startActivity(intent);
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("slf_name", slf);
            bundle.putString("bookname", bookName);
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);
            BookSearchListActivity.this.finish();
        }
    }


    private void searchBook(final String bookName) {
        //用来取消请求
//        setRefreshing(true);
        String tag_string_req = "req_check_book";

        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_SEARCHBOOK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    int ret = jObj.getInt("ret");
                    JSONObject data = jObj.getJSONObject("data");
                    int code = data.getInt("code");

                    //检查error节点
                    if (ret == 200) {
                        onRefreshComplete(data.getJSONArray("info"));
                    } else {
                        //错误信息
                        String errorMsg = jObj.getString("msg") + data.getString("msg");
                        //显示错误信息
                        Toast.makeText(BookSearchListActivity.this,
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
                Log.e(LOG_TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
//                setRefreshing(false)            }
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // POST的参数到服务器
                Map<String, String> params = new HashMap<String, String>();
                params.put("BookName", bookName);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void onRefreshComplete(JSONArray json) {
        //数据

//        bookListAdaper.missionData.clear();
        for (int i = 0; i < json.length(); i++) {
            Book book = new Book();
            try {
                JSONObject jsonObject = json.getJSONObject(i);
                book.bookName = jsonObject.getString("book_name");
                book.slfName = jsonObject.getString("slf_name");
                book.details = jsonObject.getString("author") + "    " + jsonObject.getString("pub_house");
                book.urlCover = jsonObject.getString("cover");
                book.urlCoverThumb = jsonObject.getString("cover_thumb");
                bookListAdapter.bookData.add(book);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // setListAdapter(missionListAdapter);
        bookListAdapter.notifyDataSetChanged();
//        setRefreshing(false);
    }



    private void commitRecord(final String bookName, final String slf)
    {
        //用来取消请求
//        setRefreshing(true);
        String tag_string_req = "req_commit_record";

        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_COMMITRECORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    int ret = jObj.getInt("ret");
                    JSONObject data = jObj.getJSONObject("data");
                    int code = data.getInt("code");

                    //检查error节点
                    if (ret == 200) {
//                        onRefreshComplete(data.getJSONArray("info"));
                    } else {
                        //错误信息
                        String errorMsg = jObj.getString("msg") + data.getString("msg");
                        //显示错误信息
                        Toast.makeText(BookSearchListActivity.this,
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
                Log.e(LOG_TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
//                setRefreshing(false)            }
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // POST的参数到服务器
                Map<String, String> params = new HashMap<String, String>();
                params.put("bookName", bookName);
                params.put("slf", slf);
                params.put("UID", Integer.toString(session.UID()));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
