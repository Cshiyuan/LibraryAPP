package com.cczq.booksearch.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.cczq.booksearch.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Shyuan on 2016/10/11.
 */

public class PersonalFragment extends Fragment {

    private CircleImageView circleImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setHasOptionsMenu(false);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);

        circleImageView = (CircleImageView) view.findViewById(R.id.profile_image);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .width(150)  // width in px
                .height(150) // height in px
                .endConfig()
                .buildRect("AD", ColorGenerator.MATERIAL.getRandomColor());
        circleImageView.setImageDrawable(drawable);
        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//
//        View view = inflater.inflate(R.layout.fragment_settings, container, false);
//        ((Button)view.findViewById(R.id.btnLogout)).setOnClickListener(this);
//        return view;
//
//    }
}
