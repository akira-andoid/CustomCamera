package com.example.akira.customcamera;

import android.content.ContentResolver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.GridView;

import java.lang.reflect.Array;
import java.util.Arrays;

public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ContentResolver resolver = getContentResolver();
        GridView gridView = (GridView) findViewById(R.id.photo_grid);
        gridView.setAdapter(new ImageAdapter(this,resolver));
    }
}
