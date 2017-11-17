package com.example.akira.customcamera;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Akira on 2017/11/07.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ContentResolver mResolver;

    public ImageAdapter (Context context,ContentResolver resolver) {
        mContext = context;
        mResolver = resolver;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(mThumb(position));
        return imageView;
    }

    public Bitmap mThumb(int position) {
        Cursor cursor = mResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,null,null,null,null);
        cursor.moveToFirst();
        cursor.move(position);
        long id = cursor.getLong(cursor.getColumnIndex("_id"));
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(mResolver,id,MediaStore.Images.Thumbnails.MICRO_KIND,null);
        return bitmap;
    }
}
