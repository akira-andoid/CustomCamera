package com.example.akira.customcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
         Camera.PictureCallback, SurfaceHolder.Callback {

    File mSavePath;
    Camera mCamera;
    SurfaceView mView;
    ImageView mImage;
    MediaScannerConnection mediaScannerConnection;
    MediaScannerConnection.MediaScannerConnectionClient mediaScannerConnectionClient =
            new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                    String path = mSavePath.getPath();
                    String mimeType = "image/*";
                    mediaScannerConnection.scanFile(path,mimeType);
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    mediaScannerConnection.disconnect();
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mView.getHolder());
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
        mCamera.stopPreview();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int dispWidth = display.getWidth();
        int dispHeight = display.getHeight();
        Camera.Parameters params = mCamera.getParameters();
        try {
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            Camera.Size size = sizes.get(0);
            ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
            float wScale = (float)dispWidth / size.width;
            float hScale = (float)dispHeight / size.height;
            float scale = wScale > hScale ? hScale : wScale;
            layoutParams.width = (int) (size.width * scale);
            layoutParams.height = (int)(size.height * scale);
            mView.setLayoutParams(layoutParams);
            params.setPreviewSize(size.width, size.height);
            mCamera.setParameters(params);
        } catch (Exception e) {
            Log.e("error", e.toString());
            params.setPreviewSize(width, height);
            mCamera.setParameters(params);
        }
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        findViewById(R.id.camera_button).setOnClickListener(this);
        mView = (SurfaceView) findViewById(R.id.preview);
        mImage = (ImageView) findViewById(R.id.small_image);
        mView.getHolder().addCallback(this);
        mView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mView.setOnClickListener(this);
        mCamera = Camera.open();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String s = df.format(new Date());
        StringBuffer fileName = new StringBuffer("M");
        fileName.append(s).append(".jpg");
        mSavePath = new File(Environment.getExternalStorageDirectory(),fileName.toString());
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(mSavePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            mediaScannerConnection = new MediaScannerConnection(getApplicationContext(),
                    mediaScannerConnectionClient);
            mediaScannerConnection.connect();
        } catch (FileNotFoundException e) {
            Log.e("Error",e.toString());
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 4;
        Bitmap sample = BitmapFactory.decodeByteArray(data,0,data.length,opts);
        mImage.setImageBitmap(sample);
        mCamera.startPreview();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_button:
                mCamera.takePicture(null,null,this);
                break;
            case R.id.preview:
                mCamera.autoFocus(null);
                break;
            default: break;
        }
    }
}
