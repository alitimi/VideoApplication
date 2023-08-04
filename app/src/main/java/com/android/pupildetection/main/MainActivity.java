package com.android.pupildetection.main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import com.android.pupildetection.R;
import com.android.pupildetection.core.ui.BaseActivity;
import com.android.pupildetection.data.CascadeData;
import com.android.pupildetection.settings.SettingsActivity;
import org.opencv.android.CameraBridgeViewBase;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity implements MainContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SETTINGS_ACTIVITY_REQUEST_CODE = 200;

    private MainContract.Presenter mPresenter;

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    int number;
    private CameraBridgeViewBase camera;
    private TextView tv_instruction;
    private TextView tv_instruction2;
    VideoView videoView;
    VideoView videoViewLeft;
    VideoView videoViewRight;
    VideoView videoViewCenter;
    Uri uri1;
    Uri uri2;
    Uri uri3;

//    private Button b_register, b_verify, b_delete, b_cancel, b_save, b_settings;

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        Bundle b = getIntent().getExtras();
        number = b.getInt("num");

        mPresenter = new MainPresenter(this);

        camera = findViewById(R.id.jcv_camera);
        camera.setVisibility(SurfaceView.VISIBLE);
        camera.setCvCameraViewListener(mPresenter.getCvCameraViewListener());
        camera.setCameraIndex(1); // front 1, back 0
        camera.enableFpsMeter();

        tv_instruction = findViewById(R.id.tv_instruction);
        tv_instruction2 = findViewById(R.id.tv_instruction2);
        videoView = findViewById(R.id.vdVw);
        videoViewCenter = findViewById(R.id.vdVwCenter);
        videoViewLeft = findViewById(R.id.vdVwLeft);
        videoViewRight = findViewById(R.id.vdVwRight);


        //Set MediaController  to enable play, pause, forward, etc options.
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        //Location of Media File
        if (number == 1) {
            uri1 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening1);
            uri2 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening1left);
            uri3 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening1right);
        } else if (number == 2){
            uri1 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening2);
//            uri2 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening2left);
//            uri3 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening2right);
        } else if (number == 3){
            uri1 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening3);
//            uri2 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening3left);
//            uri3 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening3right);
        } else if (number == 4){
            uri1 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening4);
//            uri2 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening4left);
//            uri3 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening4right);
        } else if (number == 5){
            uri1 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening5);
//            uri2 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening5left);
//            uri3 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening5right);
        }

        //Starting VideView By Setting MediaController and URI
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri1);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Intent intent = new Intent(MainActivity.this, ExaminationActivity.class);
                intent.putExtra("num", number);
                startActivity(intent);
            }
        });

        videoViewLeft.setVideoURI(uri2);
        videoViewLeft.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setVolume(0, 0);
            }
        });
        videoViewRight.setVideoURI(uri3);
        videoViewRight.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setVolume(0, 0);
            }
        });
        videoViewCenter.setVideoURI(uri1);
        videoViewCenter.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setVolume(0, 0);
            }
        });

        videoView.requestFocus();

        videoView.start();
        videoViewLeft.start();
        videoViewRight.start();
        videoViewCenter.start();
        Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        videoView.startAnimation(animZoomIn);


//        b_register = findViewById(R.id.b_register);
//        b_verify = findViewById(R.id.b_verify);
//        b_delete = findViewById(R.id.b_delete);
//        b_cancel = findViewById(R.id.b_cancel);
//        b_save = findViewById(R.id.b_save);
//        b_settings = findViewById(R.id.b_settings);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
//        setClicks();
    }

//    private void setClicks(){
//        mPresenter.observeButton(b_register, 0);
//        mPresenter.observeButton(b_verify, 1);
//        mPresenter.observeButton(b_delete, 2);
//        mPresenter.observeButton(b_cancel, 3);
//        mPresenter.observeButton(b_save, 4);
//        mPresenter.observeButton(b_settings, 5);
//    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
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

    /**
     * @param status       -1 : not detected
     *                     n : # of eyes detected
     * @param messageResId
     */
    @Override
    public void updateCurrentStatus(int status, int messageResId) {
        // update current status text field
        String message = this.getString(messageResId);
        runOnUiThread(() -> tv_instruction.setText(message));
    }

    @Override
    public void updateCurrentStatus2(int status, int messageResId, int position, boolean maxLeft, boolean maxRight, boolean maxCenter, int left, int right, int center) {
        String message2 = this.getString(messageResId);
        runOnUiThread(() -> {
            tv_instruction2.setText(message2 + left + "/" + right + "/" + center + "/l:" + maxLeft + "/r:" + maxRight + "/c:" + maxCenter);
            if (maxLeft) {
                int time = videoViewLeft.getCurrentPosition();
//                time = Math.max(Math.max(videoViewRight.getCurrentPosition(), videoView.getCurrentPosition()), videoViewLeft.getCurrentPosition());
                videoView.stopPlayback();
                videoView.setVideoURI(uri2);
                videoView.start();
                videoView.seekTo(time);
//                videoViewRight.stopPlayback();
//                videoView.setVisibility(View.GONE);
//                videoViewRight.setVisibility(View.GONE);
//                videoViewLeft.setVisibility(View.VISIBLE);
//                if (time != videoViewLeft.getCurrentPosition()) {
//                    videoViewLeft.seekTo(time + 1000);
//                }
//                else {
//                    videoViewRight.seekTo(time);
//                }
//                videoViewLeft.start();
            }
            if (maxRight) {
                int time = videoViewRight.getCurrentPosition();;
//                time = Math.max(Math.max(videoViewLeft.getCurrentPosition(), videoView.getCurrentPosition()), videoViewRight.getCurrentPosition());
                videoView.stopPlayback();
                videoView.setVideoURI(uri3);
                videoView.start();
                videoView.seekTo(time);
//                videoViewLeft.stopPlayback();
//                videoView.setVisibility(View.GONE);
//                videoViewLeft.setVisibility(View.GONE);
//                videoViewRight.setVisibility(View.VISIBLE);
//                if (time == videoViewRight.getCurrentPosition()) {
//                    videoViewRight.seekTo(time + 1000);
//                }
//                else {
//                    videoViewRight.seekTo(time);
//                }
//                videoViewRight.start();


            }

            if (maxCenter) {
                int time = videoViewCenter.getCurrentPosition();
//                time = Math.max(Math.max(videoViewLeft.getCurrentPosition(), videoViewRight.getCurrentPosition()), videoView.getCurrentPosition());
                videoView.stopPlayback();
                videoView.setVideoURI(uri1);
                videoView.start();
                videoView.seekTo(time);
//                videoViewLeft.stopPlayback();
//                videoViewRight.stopPlayback();
//                videoViewLeft.setVisibility(View.GONE);
//                videoViewRight.setVisibility(View.GONE);
//                videoView.setVisibility(View.VISIBLE);
//                if (time != videoView.getCurrentPosition()) {
//                    videoView.seekTo(time + 1000);
//                }
//                else {
//                    videoViewRight.seekTo(time);
//                }
//                videoView.start();

            }
//            tv_instruction2.setVisibility(View.VISIBLE);
        });
    }

//    /**
//     * @param status       -1 : not detected
//     *                     n : # of eyes detected
//     * @param messageResId
//     */
//    @Override
//    public void updateCurrentStatus2(int status, int messageResId, boolean maxLeft, boolean maxRight, boolean maxCenter, int left, int right, int center) {
//        // update current status text field
//
//
//        String message2 = this.getString(messageResId);
//        runOnUiThread(() -> {
//            tv_instruction2.setText(message2 + left + "/" + right + "/" + center + "/l:" + maxLeft + "/r:" + maxRight + "/c:" + maxCenter);
//            if (maxLeft) {
//                int time = videoViewLeft.getCurrentPosition();
////                time = Math.max(Math.max(videoViewRight.getCurrentPosition(), videoView.getCurrentPosition()), videoViewLeft.getCurrentPosition());
//                videoView.stopPlayback();
//                videoView.setVideoURI(uri2);
//                videoView.start();
//                videoView.seekTo(time);
////                videoViewRight.stopPlayback();
////                videoView.setVisibility(View.GONE);
////                videoViewRight.setVisibility(View.GONE);
////                videoViewLeft.setVisibility(View.VISIBLE);
////                if (time != videoViewLeft.getCurrentPosition()) {
////                    videoViewLeft.seekTo(time + 1000);
////                }
////                else {
////                    videoViewRight.seekTo(time);
////                }
////                videoViewLeft.start();
//            }
//            if (maxRight) {
//                int time = videoViewRight.getCurrentPosition();;
////                time = Math.max(Math.max(videoViewLeft.getCurrentPosition(), videoView.getCurrentPosition()), videoViewRight.getCurrentPosition());
//                videoView.stopPlayback();
//                videoView.setVideoURI(uri3);
//                videoView.start();
//                videoView.seekTo(time);
////                videoViewLeft.stopPlayback();
////                videoView.setVisibility(View.GONE);
////                videoViewLeft.setVisibility(View.GONE);
////                videoViewRight.setVisibility(View.VISIBLE);
////                if (time == videoViewRight.getCurrentPosition()) {
////                    videoViewRight.seekTo(time + 1000);
////                }
////                else {
////                    videoViewRight.seekTo(time);
////                }
////                videoViewRight.start();
//
//
//            }
//
//            if (maxCenter) {
//                int time = videoViewCenter.getCurrentPosition();
////                time = Math.max(Math.max(videoViewLeft.getCurrentPosition(), videoViewRight.getCurrentPosition()), videoView.getCurrentPosition());
//                videoView.stopPlayback();
//                videoView.setVideoURI(uri1);
//                videoView.start();
//                videoView.seekTo(time);
////                videoViewLeft.stopPlayback();
////                videoViewRight.stopPlayback();
////                videoViewLeft.setVisibility(View.GONE);
////                videoViewRight.setVisibility(View.GONE);
////                videoView.setVisibility(View.VISIBLE);
////                if (time != videoView.getCurrentPosition()) {
////                    videoView.seekTo(time + 1000);
////                }
////                else {
////                    videoViewRight.seekTo(time);
////                }
////                videoView.start();
//
//            }
////            tv_instruction2.setVisibility(View.VISIBLE);
//        });
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // RESULT_OK
            } else {
                // RESULT_CANCELED || FAILED
            }
        }
    }
}
