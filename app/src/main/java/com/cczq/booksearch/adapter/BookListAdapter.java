package com.cczq.booksearch.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.toolbox.ImageLoader;
import com.cczq.booksearch.AppController;
import com.cczq.booksearch.Model.Book;
import com.cczq.booksearch.R;

import java.util.ArrayList;

/**
 * Created by bb on 2016/12/7.
 */

public class BookListAdapter extends BaseAdapter {

    private View.OnClickListener onClickListener;

    public ArrayList<Book> bookData = new ArrayList<Book>();

    private LayoutInflater mInflater = null;

    public BookListAdapter(Context context, View.OnClickListener onClickListener) {
        super();
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return bookData.size();
    }

    @Override
    public Object getItem(int i) {
        return bookData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        BookViewHolder holder = null;
        if (view == null) {
            view = mInflater.inflate(R.layout.bookitem,viewGroup, false);
        }
        ImageView bookImageView = (ImageView) view.findViewById(R.id.book_ImageView);
        TextView bookNameTextView = (TextView) view.findViewById(R.id.bookName_TextView);
        TextView bookInfoTextView = (TextView) view.findViewById(R.id.info_TextView);
        TextView bookSlfTextView = (TextView) view.findViewById(R.id.slf_TextView);


        final Drawable drawable = getRectWithAnimation(bookData.get(i).bookName, i, ColorGenerator.MATERIAL.getRandomColor());

        if(bookData.get(i).urlCoverThumb == "null")
        {
            bookImageView.setImageDrawable(drawable);
//            修复动画在一些低于4.4的手机设备
            bookImageView.post(new Runnable() {
                @Override
                public void run() {
                    ((AnimationDrawable) drawable).stop();
                    ((AnimationDrawable) drawable).start();
                }
            });
        } else {
            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(bookImageView,
                    R.drawable.error_circle, R.drawable.error_center_x);
            imageLoader.get("http://119.29.186.160/LibraryWeb/Uploads/" + bookData.get(i).urlCoverThumb, listener);
        }

        bookNameTextView.setText(bookData.get(i).bookName);
        bookInfoTextView.setText(bookData.get(i).details);
        bookSlfTextView.setText(bookData.get(i).slfName);

        view.setOnClickListener(this.onClickListener);
        view.setTag(i);

        return view;
    }


    private Drawable getRectWithAnimation(String string, int delay, int color) {
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .rect();

        AnimationDrawable animationDrawable = new AnimationDrawable();

        for (int i = 0; i < string.length() - 1; i++) {
//            Log.d("debug", string.substring(i, i + 1));
            TextDrawable frame = builder.build(string.substring(i, i + 1), color);
            animationDrawable.addFrame(frame, 900 + delay * 100);
        }

        animationDrawable.setOneShot(false);
        animationDrawable.start();

        return animationDrawable;
    }
}
