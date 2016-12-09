package com.cczq.booksearch.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cczq.booksearch.MainActivity;
import com.cczq.booksearch.R;

/**
 * Created by Shyuan on 2016/10/11.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ((Button) view.findViewById(R.id.btnLogout)).setOnClickListener(this);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogout) {
            Log.d("Try to logout", "debug");
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.logoutUser();
        }
    }
}
