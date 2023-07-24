package com.android.pupildetection.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.pupildetection.R;

import java.util.Arrays;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = CameraActivity.class.getSimpleName();

    private SurfaceView sv_camera;
    private Button b_camera;

    private CameraManager cameraManager;
    private String cameraId;
    private CameraDevice mCamera;
    private SurfaceHolder mHolder;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest mCaptureRequest;
    private CaptureRequest.Builder mPreviewCaptureRequestBuilder;
    private ImageReader mImageReader;
    private int mPreviewFormat = ImageFormat.YUV_420_888;
    private Size mPreviewSize = new Size(-1, -1);

    private int width, height;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initView();

        // get camera
        getCamera();

        // set preview
        setPreview();

        // open camera
        openCamera();

    }

    private void initView(){
        sv_camera = findViewById(R.id.sv_camera);

        width = sv_camera.getWidth();
        height = sv_camera.getHeight();
        mHolder = sv_camera.getHolder();

        b_camera = findViewById(R.id.b_camera);
        b_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void getCamera() {
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String[] cameraIds;
        try {
            cameraIds = cameraManager.getCameraIdList();

            if (cameraIds.length == 0) return;

            cameraId = cameraIds[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setPreview(){
        mHolder.addCallback(surfaceHolderCallback);
        mHolder.setFixedSize(width, height);
    }

    private void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 500);
                return;
            }
            cameraManager.openCamera(cameraId, cameraDeviceCallback, null);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startCameraCaptureSession() throws CameraAccessException{
        if(mCamera == null || mHolder.isCreating()) return;
//
//        mImageReader = ImageReader.newInstance(width, height, mPreviewFormat, 2);
//        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
//            @Override
//            public void onImageAvailable(ImageReader reader) {
//                Image image = reader.acquireLatestImage();
//                if(image == null) return;
//
//                Image.Plane[] planes = image.getPlanes();
//                assert (planes.length == 3);
//                assert (image.getFormat() == mPreviewFormat);
//
//            }
//        }, mBackgroundHandler);
//

        Surface previewSurface = mHolder.getSurface();

        // create preview's CaptureRequest.Builder
        mPreviewCaptureRequestBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        mPreviewCaptureRequestBuilder.addTarget(previewSurface);

        try{
            mCamera.createCaptureSession(Arrays.asList(previewSurface), captureSessionCallback, null);
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    /**
     * Callbacks
     */

    // callback for camera open
    CameraDevice.StateCallback cameraDeviceCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCamera = camera;
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            mCamera = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            mCamera = null;
            Log.e(TAG, "Camera Error: "+error);
        }
    };

    // callback for preview
    SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                startCameraCaptureSession();
            } catch (CameraAccessException e){
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    CameraCaptureSession.StateCallback captureSessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            mCaptureSession = session;
            try{
                mPreviewCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                mCaptureSession.setRepeatingRequest(
                        mPreviewCaptureRequestBuilder.build(),
                        mCaptureCallback,
                        null);
            } catch (CameraAccessException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.e(TAG, "Capture Session onConfigureFailed");
        }
    };

    CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            process(result);
        }

        private void process(CaptureResult result){

        }
    };

}
