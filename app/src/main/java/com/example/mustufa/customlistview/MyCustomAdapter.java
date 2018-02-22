package com.example.mustufa.customlistview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mustufa on 2/15/2018.
 */

public class MyCustomAdapter extends BaseAdapter {

    private Context mContext;
    private String[] library;
    private LayoutInflater inflater;

    private static final String TAG = "MyCustomAdapter";
    public MyCustomAdapter(Context context, String[] data) {

        mContext= context;
        library = data;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {

        Log.d(TAG, "getCount: " + library.length);
        return library.length;
    }

    @Override
    public Object getItem(int position) {

        Log.d(TAG, "getItem:  " + position);
        return position;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Log.d(TAG, "getView: Method Running..");

        View mView = view;

        if(mView == null) {
            mView = inflater.inflate(R.layout.layout_list_items,null);

            TextView song_title = mView.findViewById(R.id.tite);
            ImageView image = mView.findViewById(R.id.list_image);

            song_title.setText(library[i]);
            image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_icon));

            Log.d(TAG, "getView: Successfully set up all views to adapter");

        }

        return mView;
    }
}
