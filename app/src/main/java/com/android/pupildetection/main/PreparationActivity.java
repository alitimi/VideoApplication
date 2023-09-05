package com.android.pupildetection.main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import com.android.pupildetection.R;
import com.android.pupildetection.core.ui.BaseActivity;
import com.android.pupildetection.data.CascadeData;
import com.android.pupildetection.settings.SettingsActivity;
import org.opencv.android.CameraBridgeViewBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PreparationActivity extends BaseActivity implements MainContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SETTINGS_ACTIVITY_REQUEST_CODE = 200;
    private MainContract.Presenter mPresenter;
    private CameraBridgeViewBase camera;
    private Button next;

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_preparation);
        Bundle b = getIntent().getExtras();
        int number = b.getInt("num");
        String user = b.getString("user");
        next = findViewById(R.id.okbutton);
        mPresenter = new MainPresenter(this);
        camera = findViewById(R.id.jcv_camera);
        camera.setVisibility(SurfaceView.VISIBLE);
        camera.setCvCameraViewListener(mPresenter.getCvCameraViewListener());
        camera.setCameraIndex(1); // front 1, back 0
        camera.enableFpsMeter();
        next.setOnClickListener(view -> {
            Intent intent = new Intent(PreparationActivity.this, MainActivity2.class);
            intent.putExtra("num", number);
            intent.putExtra("user", user);
            startActivity(intent);
        });
    }

    @Override
    protected void enableView() {
        camera.enableView();
        Log.d("jaycho", "readCascade: " + CascadeData.readCascade);
    }

    @Override
    protected void disableView() {
        if (camera != null)
            camera.disableView();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onCameraPermissionGranted() {
        Log.d(TAG, "onCameraPermissionGranted");
        List<? extends CameraBridgeViewBase> cameraViews = Collections.singletonList(camera);
        if (cameraViews == null) {
            return;
        }
        for (CameraBridgeViewBase cameraBridgeViewBase : cameraViews) {
            if (cameraBridgeViewBase != null) {
                cameraBridgeViewBase.setCameraPermissionGranted();
            }
        }
    }

    @Override
    public void startSettingsActivity() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivityForResult(settingsIntent, SETTINGS_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void updateCenterPosition(double leftCenter, double rightCenter, int num) {

    }

    @Override
    public void saveVideo(ArrayList<Bitmap> bitmaps) {

    }

    @Override
    public void updateCurrentStatus(int status, int messageResId) {

    }

    @Override
    public void updateCurrentStatus2(int status, int messageResId, int position, boolean maxLeft, boolean maxRight, boolean maxCenter, int left, int right, int center) {

    }

    @Override
    public void updateCurrentStatus3(ArrayList<Bitmap> bitmaps, int status, int messageResId, int position, boolean maxLeft, boolean maxRight, boolean maxCenter, int left, int right, int center) {

    }

}
