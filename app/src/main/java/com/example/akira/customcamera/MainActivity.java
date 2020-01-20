package com.example.akira.customcamera;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener,
         Camera.PictureCallback, SurfaceHolder.Callback {

    File mSavePath;
    Camera mCamera;
    SurfaceView mView;
    ImageView mImage;
    ImageView sImage;
    FragmentManager fragmentManager;

    private MediaScannerConnection mediaScannerConnection;
    MediaScannerConnection.MediaScannerConnectionClient mediaScannerConnectionClient =
            new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                    String path = mSavePath.getPath();
                    String mimeType = "image/*";
                    mediaScannerConnection.scanFile(path, mimeType);
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    mediaScannerConnection.disconnect();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        findViewById(R.id.camera_button).setOnClickListener(this);
        findViewById(R.id.gallery_button).setOnClickListener(this);
        mView = (SurfaceView) findViewById(R.id.preview);
        mImage = (ImageView) findViewById(R.id.small_image);
        sImage = (ImageView) findViewById(R.id.sepia_image);
        mView.getHolder().addCallback(this);
        mView.setOnClickListener(this);
        mCamera = Camera.open();
        fragmentManager = getFragmentManager();
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
            float wScale = (float) dispWidth / size.width;
            float hScale = (float) dispHeight / size.height;
            float scale = wScale > hScale ? hScale : wScale;
            layoutParams.width = (int) (size.width * scale);
            layoutParams.height = (int) (size.height * scale);
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
    public void onPictureTaken(byte[] data, Camera camera) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String s = df.format(new Date());
        StringBuffer fileName = new StringBuffer("M");
        fileName.append(s).append(".jpg");
        mSavePath = new File(Environment.getExternalStorageDirectory(), fileName.toString());
        ColorChangeTask task = new ColorChangeTask();
        task.execute(bitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_button:
                mCamera.takePicture(null, null, this);
                break;
            case R.id.preview:
                mCamera.autoFocus(null);
                break;
            case R.id.gallery_button:
                Intent intent = new Intent(this,PhotoActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }


    private class ColorChangeTask extends AsyncTask<Bitmap,Void,Bitmap> {

        WaitFragment wf = WaitFragment.newInstance("Please wait","少々お待ちください");

        @Override
        protected void onPreExecute() {
            wf.show(fragmentManager, "dialog");
        }

        @Override
        protected Bitmap doInBackground(Bitmap... urls) {

            Bitmap src = urls[0];
            int width = src.getWidth();
            int height = src.getHeight();
            int pixels[] = new int[width * height];
            src.getPixels(pixels, 0, width, 0, 0, width, height);
            for (int j=0; j<height; j++) {
                for (int i=0; i<width; i++) {
                    int pixelColor = pixels[i + j * width];
                    int y = (int) (76 * Color.red(pixelColor) + 150 * Color.green(pixelColor)
                            + 29 * Color.blue(pixelColor)) >> 8;
                    pixels[i + j * width] = Color.rgb((y * 240) >> 8,
                            (y * 180) >> 8,
                            (y * 128) >> 8);
                }
            }
            Bitmap dst = src.copy(Bitmap.Config.ARGB_8888,true);
            dst.setPixels(pixels,0,width,0,0,width,height);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(mSavePath);
                dst.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                mediaScannerConnection = new MediaScannerConnection(getApplicationContext(),mediaScannerConnectionClient);
                mediaScannerConnection.connect();
            } catch (FileNotFoundException e) {
                Log.e("Error", e.toString());
            }
            return dst;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            wf.dismiss();
            sImage.setImageBitmap(result);
            mCamera.startPreview();
        }
    }

}