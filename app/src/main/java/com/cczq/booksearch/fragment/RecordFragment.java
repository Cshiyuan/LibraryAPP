package com.cczq.booksearch.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cczq.booksearch.AppController;
import com.cczq.booksearch.MainActivity;
import com.cczq.booksearch.Model.Record;
import com.cczq.booksearch.R;
import com.cczq.booksearch.adapter.RecordListAdapter;
import com.cczq.booksearch.utils.configURL;
import com.cczq.booksearch.widget.SwipeRefreshListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Shyuan on 2016/10/11.
 */

public class RecordFragment extends SwipeRefreshListFragment {

    private static final String LOG_TAG = RecordFragment.class.getSimpleName();

    private static final int LIST_ITEM_COUNT = 20;

    private RecordListAdapter recordListAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recordListAdapter = new RecordListAdapter(getActivity());
        setListAdapter(recordListAdapter);
        setColorScheme(R.color.greenyellow, R.color.lawngreen, R.color.mediumspringgreen, R.color.lightgreen);

        //下拉刷新
        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                initiateRefresh();
            }
        });
        initiateRefresh();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //System.out.println("Click On List Item!!!");
//        Mission mission = missionListAdapter.missionData.get(position);
//        Intent intent = new Intent(getActivity(), countDownActivity.class);
//        //序列化传递对象
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("mission", mission);
//        intent.putExtras(bundle);
//        startActivity(intent);

    }

    private void initiateRefresh() {
        Log.i(LOG_TAG, "initiateRefresh");
        //用来取消请求
        setRefreshing(true);
        String tag_string_req = "req_check_mission";

        //建立StringRequest
        StringRequest strReq = new StringRequest(Request.Method.POST,
                configURL.URL_CHECKRECORD, new Response.Listener<String>() {
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
                        Toast.makeText(getActivity(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // 抛出JSON的错误Exception
                    e.printStackTrace();
                    setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Login Error: " + error.getMessage());
//                    Toast.makeText(getApplicationContext(),
//                            error.getMessage(), Toast.LENGTH_LONG).show();
                setRefreshing(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                MainActivity mainActivity = (MainActivity) getActivity();
                // POST的参数到服务器
                Map<String, String> params = new HashMap<String, String>();
                params.put("UID", Integer.toString(mainActivity.session.UID()));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void onRefreshComplete(JSONArray json) {
        //数据

        recordListAdapter.recordData.clear();
        for (int i = 0; i < json.length(); i++) {
            Record record = new Record();
            try {
                JSONObject jsonObject = json.getJSONObject(i);
                record.bookName = jsonObject.getString("book_name");
                record.slf = jsonObject.getString("slf");
                record.time = jsonObject.getString("search_time");
                recordListAdapter.recordData.add(record);

//                {
//                    "RID": "4",
//                        "UID": "1",
//                        "book_name": "数据结构",
//                        "slf": "F2016",
//                        "search_time": "2016-12-09 12:31:33"
//                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // setListAdapter(missionListAdapter);
        recordListAdapter.notifyDataSetChanged();
        setRefreshing(false);

    }


}