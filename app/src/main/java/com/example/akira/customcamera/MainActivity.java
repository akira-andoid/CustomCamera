package com.example.akira.customcamera;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.OutputConfiguration;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Environment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;
import android.view.PixelCopy;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    protected final String TAG = "CustomCamera";

    File mSavePath = Environment.getExternalStorageDirectory();
    CameraManager mCameraManager;
    Handler cameraHandler;
    SurfaceView mSurfaceView;
    SurfaceHolder holder;
    Surface mSurface;
    CameraCharacteristics mCameraCharacteristics;
    ImageView mImage;
    ImageView sImage;
    FragmentManager fragmentManager;
    CameraDevice mCameraDevice = null;
    List cameraConfigurationList = new ArrayList();
    OutputConfiguration simpleOutputConfiguration;
    CameraCaptureSession mCameraCaptureSession;
    CaptureRequest.Builder builder;
    CaptureRequest mCaptureRequest;
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            createCameraSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
        }
    };

    CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            mCameraCaptureSession = session;
            createCaptureRequest();
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    };

    CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }
    };

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
        mSurfaceView = findViewById(R.id.preview);
        holder = mSurfaceView.getHolder();
        mSurface = holder.getSurface();
        mImage = findViewById(R.id.small_image);
        sImage = findViewById(R.id.sepia_image);
        findViewById(R.id.camera_button).setOnClickListener(this);
        findViewById(R.id.gallery_button).setOnClickListener(this);
        mSurfaceView.setOnClickListener(this);

        mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        String[] cameraID;
        int MY_REQUEST_IS_CAMERA = 1;

        cameraHandler = new Handler();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_REQUEST_IS_CAMERA);
        }
        try {
            cameraID = mCameraManager.getCameraIdList();
            mCameraManager.openCamera(cameraID[0],mStateCallback,cameraHandler);
            mCameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraID[0]);
        } catch (Exception e) {
            Log.e(TAG,"Error");
        }

        fragmentManager = getFragmentManager();
    }

    private void createCameraSession() {
        simpleOutputConfiguration = new OutputConfiguration(mSurface);
        cameraConfigurationList.add(simpleOutputConfiguration);
        try {
            mCameraDevice.createCaptureSessionByOutputConfigurations
                    (cameraConfigurationList, sessionStateCallback, null);
        } catch (Exception e) {
            Log.e(TAG,"Error");
        }
    }

    private void createCaptureRequest() {
        try {
            builder= mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_MANUAL);
            builder.addTarget(mSurface);
            mCaptureRequest = builder.build();
        } catch (Exception e) {
            Log.e(TAG,"Error");
        }
    }

    private void takePicture() {
        try {
            mCameraCaptureSession.stopRepeating();
            Bitmap destBitmap = Bitmap.createBitmap(mSurfaceView.getHeight(), mSurfaceView.getWidth(),
                    Bitmap.Config.ARGB_8888);
            PixelCopy.request(mSurface,destBitmap,listener,cameraHandler);
            File saveFile = new File(mSavePath.getPath(),"CustomCamera/test.jpg");
            FileOutputStream fos = new FileOutputStream(saveFile);
            destBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.close();
            mCameraCaptureSession.setRepeatingRequest(mCaptureRequest,null,null);
        } catch (Exception e) {
            Log.e(TAG,"error");
        }
        mediaScannerConnection.connect();
    }

    PixelCopy.OnPixelCopyFinishedListener listener = new PixelCopy.OnPixelCopyFinishedListener() {
        @Override
        public void onPixelCopyFinished(int copyResult) {
            if (copyResult != PixelCopy.SUCCESS) {
                Log.e(TAG,"Error");
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_button:
                takePicture();
                break;
            case R.id.preview:
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        }
    }

}