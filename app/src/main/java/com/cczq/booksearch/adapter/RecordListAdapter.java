package com.cczq.booksearch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cczq.booksearch.Model.Record;
import com.cczq.booksearch.R;

import java.util.ArrayList;

/**
 * Created by bb on 2016/12/9.
 */

public class RecordListAdapter extends BaseAdapter {

//    private View.OnClickListener onClickListener;

    public ArrayList<Record> recordData = new ArrayList<Record>();

    private LayoutInflater mInflater = null;

    public RecordListAdapter(Context context) {
        super();
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.onClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return recordData.size();
    }

    @Override
    public Object getItem(int i) {
        return recordData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        BookViewHolder holder = null;
        if (view == null) {
            view = mInflater.inflate(R.layout.recorditem,viewGroup, false);
        }

        TextView recordNameTextView = (TextView) view.findViewById(R.id.rbookName_TextView);
        TextView recordSlfTextView = (TextView) view.findViewById(R.id.rslf_TextView);
        TextView recordTimeTextView = (TextView) view.findViewById(R.id.rtime_TextView);



        recordNameTextView.setText(recordData.get(i).bookName);
        recordSlfTextView.setText(recordData.get(i).slf);
        recordTimeTextView.setText(recordData.get(i).time);

//        view.setOnClickListener(this.onClickListener);
//        view.setTag(i);

        return view;
    }


}
