package com.example.akira.customcamera;

/**
 * Created by Akira on 2017/05/29.
 */
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import java.io.IOException;


public class SlideShow extends Activity implements View.OnClickListener {

    private Animation leftinAnim;
    private Animation leftoutAnim;
    private Animation rightinAnim;
    private Animation rightoutAnim;
    private int position;

    private long id;
    private Uri bmpUri;
    private ContentResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        findViewById(R.id.imageButton_fwd).setOnClickListener(this);
        findViewById(R.id.imageButton_prev).setOnClickListener(this);
        leftinAnim = AnimationUtils.loadAnimation(this, R.anim.leftinanim);
        leftoutAnim = AnimationUtils.loadAnimation(this, R.anim.leftoutanim);
        rightinAnim = AnimationUtils.loadAnimation(this, R.anim.rightinanim);
        rightoutAnim = AnimationUtils.loadAnimation(this, R.anim.rightoutanim);

        Intent intent = getIntent();
        position = intent.getIntExtra("POSITION", 0);
        resolver = getContentResolver();
        RecyclerView rv = (RecyclerView) findViewById(R.id.mRecyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButton_fwd:
                mViewFlipper.setInAnimation(leftinAnim);
                mViewFlipper.setOutAnimation(rightoutAnim);
                position++;
                if (loopCount >= position) {
                    id = cursor.getLong(cursor.getColumnIndex("_id"));
                    bmpUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    try {
                        bitmaps[position] = MediaStore.Images.Media.getBitmap(resolver, bmpUri);
                    } catch (IOException e) {
                        Log.e("io error.", e.toString());
                    }
                    nexImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    nexImage.setImageBitmap(bitmaps[position]);
                    mViewFlipper.addView(nexImage);
                    mViewFlipper.showNext();
                }
                break;
            case R.id.imageButton_prev:
                mViewFlipper.setInAnimation(rightinAnim);
                mViewFlipper.setOutAnimation(leftoutAnim);
                position--;
                if (0 <= position) {
                    id = cursor.getLong(cursor.getColumnIndex("_id"));
                    bmpUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    try {
                        bitmaps[position] = MediaStore.Images.Media.getBitmap(resolver, bmpUri);
                    } catch (IOException e) {
                        Log.e("io error.", e.toString());
                    }
                    prvImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    prvImage.setImageBitmap(bitmaps[position]);
                    mViewFlipper.addView(nexImage);
                    mViewFlipper.showPrevious();
                    break;
                }
        }
    }
}

