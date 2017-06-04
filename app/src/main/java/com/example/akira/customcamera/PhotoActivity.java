package com.example.akira.customcamera;

import android.content.ContentResolver;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Arrays;

public class PhotoActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ContentResolver resolver = getContentResolver();
        GridView gridView = (GridView) findViewById(R.id.photo_grid);
        gridView.setAdapter(new ImageAdapter(this,resolver));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
 //       Toast.makeText(PhotoActivity.this, "" + position + id,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,SlideShow.class);
        intent.putExtra("POSITION",position);
        startActivity(intent);
    }
}
