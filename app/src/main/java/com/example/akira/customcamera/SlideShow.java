package com.example.akira.customcamera;

/**
 * Created by Akira on 2017/05/29.
 */
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;


public class SlideShow extends Activity implements View.OnClickListener{

    private ViewFlipper mViewFlipper;
    private Animation leftinAnim;
    private Animation leftoutAnim;
    private Animation rightinAnim;
    private Animation rightoutAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        findViewById(R.id.imageButton_fwd).setOnClickListener(this);
        findViewById(R.id.imageButton_prev).setOnClickListener(this);
        mViewFlipper = (ViewFlipper) findViewById(R.id.mViewFlipper);
        leftinAnim = AnimationUtils.loadAnimation(this,R.anim.leftinanim);
        leftoutAnim = AnimationUtils.loadAnimation(this,R.anim.leftoutanim);
        rightinAnim = AnimationUtils.loadAnimation(this,R.anim.rightinanim);
        rightoutAnim = AnimationUtils.loadAnimation(this,R.anim.rightoutanim);

        Intent intent = getIntent();
        int position = intent.getIntExtra("POSITION",0);
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
        cursor.moveToFirst();
        int loopCount = cursor.getCount();
        Bitmap [] bitmaps = new Bitmap[loopCount];
        for ( int i = 0; i < loopCount ; i++ ) {
            long id = cursor.getLong(cursor.getColumnIndex("_id"));
            Uri bmpUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id);
 //           bitmaps[i] = thumbNail;
            cursor.moveToNext();
        }
    }

    @Override
    public  void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButton_fwd:
                mViewFlipper.setInAnimation(leftinAnim);
                mViewFlipper.setOutAnimation(rightoutAnim);
                mViewFlipper.showNext();
                break;
            case R.id.imageButton_prev:
                mViewFlipper.setInAnimation(rightinAnim);
                mViewFlipper.setOutAnimation(leftoutAnim);
                mViewFlipper.showPrevious();
                break;
        }
    }
}

