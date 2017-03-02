package com.example.akira.customcamera;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.Arrays;

public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
        int loopCount = cursor.getCount();
        for ( ; loopCount >0 ; loopCount--) {
            long id = cursor.getLong(cursor.getColumnIndex("_id"));
            Bitmap thumbNail = MediaStore.Images.Thumbnails.getThumbnail(resolver,id, MediaStore.Images.Thumbnails.MICRO_KIND,null);

            cursor.moveToNext();
        }

        Log.d("TEST", Arrays.toString(cursor.getColumnNames()));
        Log.d("TEST",cursor.getCount() + "");

    }
}
