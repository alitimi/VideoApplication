package com.android.pupildetection.settings;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.android.pupildetection.R;
import com.android.pupildetection.camera.CameraActivity;
import com.android.pupildetection.core.ui.BaseActivity;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;

import java.util.Collections;
import java.util.List;

public class SettingsActivity extends BaseActivity implements SettingsContract.View {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final int IMAGE_CAPTURE_REQUEST_CODE = 300;

    private SettingsContract.Presenter mPresenter;

    private JavaCamera2View jcv_settings_camera;
    private ImageView iv_settings_preview;

    public SeekBar sb_settings_scaling,
            sb_settings_brightness,
            sb_settings_contrast,
            sb_settings_edge,
            sb_settings_gamma;
    private EditText et_settings_scaling,
            et_settings_brightness,
            et_settings_contrast,
            et_settings_edge,
            et_settings_gamma;
    private Button b_settings_capture,
            b_settings_save,
            b_settings_reset,
            b_settings_exit;

    @Override
    public void setPresenter(SettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void initView(){
        setContentView(R.layout.activity_setting);

        mPresenter = new SettingsPresenter(this);

        jcv_settings_camera = findViewById(R.id.jcv_settings_camera);
        jcv_settings_camera.setVisibility(SurfaceView.VISIBLE);
        jcv_settings_camera.setCvCameraViewListener(mPresenter.getCvCameraViewListener());
        jcv_settings_camera.setCameraIndex(1);
        jcv_settings_camera.enableFpsMeter();

        b_settings_capture = findViewById(R.id.b_settings_capture);
        b_settings_save = findViewById(R.id.b_settings_save);
        b_settings_reset = findViewById(R.id.b_settings_reset);
        b_settings_exit = findViewById(R.id.b_settings_exit);

        et_settings_scaling = findViewById(R.id.et_settings_scaling);
        et_settings_brightness = findViewById(R.id.et_settings_brightness);
        et_settings_contrast = findViewById(R.id.et_settings_contrast);
        et_settings_edge = findViewById(R.id.et_settings_edge);
        et_settings_gamma = findViewById(R.id.et_settings_gamma);

        sb_settings_scaling = findViewById(R.id.sb_settings_scaling);
        sb_settings_brightness = findViewById(R.id.sb_settings_brightness);
        sb_settings_contrast = findViewById(R.id.sb_settings_contrast);
        sb_settings_edge = findViewById(R.id.sb_settings_edge);
        sb_settings_gamma = findViewById(R.id.sb_settings_gamma);

        iv_settings_preview = findViewById(R.id.iv_settings_preview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
        observe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void updateCurrentStatus2(int status, int messageResId, boolean maxLeft, boolean maxRight, boolean maxCenter, int left, int right, int center) {

    }

    @Override
    protected void enableView() {
        jcv_settings_camera.enableView();
    }

    @Override
    protected void disableView() {
        if(jcv_settings_camera != null)
            jcv_settings_camera.disableView();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onCameraPermissionGranted(){
        Log.d(TAG, "onCameraPermissionGranted");
        List<? extends CameraBridgeViewBase> cameraViews = Collections.singletonList(jcv_settings_camera);
        if (cameraViews == null) {
            return;
        }
        for (CameraBridgeViewBase cameraBridgeViewBase: cameraViews) {
            if (cameraBridgeViewBase != null) {
                cameraBridgeViewBase.setCameraPermissionGranted();
            }
        }
    }

    private void observe(){
        mPresenter.observeEditText(et_settings_scaling, this, sb_settings_scaling, getResources().getInteger(R.integer.scaling_max));
        mPresenter.observeEditText(et_settings_brightness, this, sb_settings_brightness, getResources().getInteger(R.integer.brightness_max));
        mPresenter.observeEditText(et_settings_contrast, this, sb_settings_contrast, getResources().getInteger(R.integer.contrast_max));
        mPresenter.observeEditText(et_settings_edge, this, sb_settings_edge, getResources().getInteger(R.integer.edge_max));
        mPresenter.observeEditText(et_settings_gamma, this, sb_settings_gamma, getResources().getInteger(R.integer.gamma_max));

        mPresenter.observeSeekBar(sb_settings_scaling, this, et_settings_scaling);
        mPresenter.observeSeekBar(sb_settings_brightness, this, et_settings_brightness);
        mPresenter.observeSeekBar(sb_settings_contrast, this, et_settings_contrast);
        mPresenter.observeSeekBar(sb_settings_edge, this, et_settings_edge);
        mPresenter.observeSeekBar(sb_settings_gamma, this, et_settings_gamma);

        mPresenter.observeView(b_settings_capture, 0);
        mPresenter.observeView(b_settings_save, 1);
        mPresenter.observeView(b_settings_reset, 2);
        mPresenter.observeView(b_settings_exit, 3);
        mPresenter.observeView(jcv_settings_camera, 4);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_CAPTURE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                // RESULT_OK
            } else {
                // RESULT_CANCELED || FAILED
            }
        }
    }

    @Override
    public void finishActivity() {
        Intent finishIntent = new Intent();
        setResult(RESULT_OK, finishIntent);
        finish();
    }

    @Override
    public void startCameraActivity() {
        Intent cameraIntent = new Intent(this, CameraActivity.class);
        startActivity(cameraIntent);
    }
}
