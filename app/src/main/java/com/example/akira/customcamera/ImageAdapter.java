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
 * Created by Akira on 2017/03/01.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Bitmap bitmaps[];

    ImageAdapter(Context context,ContentResolver resolver) {
        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
        cursor.moveToFirst();
        int loopCount = cursor.getCount();
        long id = cursor.getLong(cursor.getColumnIndex("_id"));
        bitmaps = new Bitmap[loopCount];
        for ( int i = 0; i < loopCount ; i++ ) {
            Bitmap thumbNail = MediaStore.Images.Thumbnails.getThumbnail(resolver,id, MediaStore.Images.Thumbnails.MICRO_KIND,null);
            bitmaps[i] = thumbNail;
            cursor.moveToNext();
        }
        mContext = context;
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
    public int getCount() {
        int i = bitmaps.length;
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85,85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8,8,8,8);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(bitmaps[position]);
        return imageView;
    }
}
