package com.example.aliano.clientcircadian_va;

/**
 * Created by Alexis on 16.09.2016.
 */
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private int[] mThumbIds;
    // Constructor
    public ImageAdapter(Context c, List<Integer> mThumbIds) {
        mContext = c;
        int[] cltI = new int[mThumbIds.size()]; // convert List<Integer> to int[]
        for(int i = 0;i < cltI.length;i++)
            cltI[i] = mThumbIds.get(i);
        this.mThumbIds = cltI;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200)); //test
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(8, 8, 8, 8);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // Keep all Images in array
    /*
    public Integer[] mThumbIds = {
    };*/
}