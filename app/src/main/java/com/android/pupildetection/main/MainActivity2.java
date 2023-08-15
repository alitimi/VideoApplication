package com.android.pupildetection.main;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.android.pupildetection.R;
import com.android.pupildetection.core.ui.BaseActivity;
import com.android.pupildetection.core.ui.BluePointView;
import com.android.pupildetection.core.ui.GreenPointView;
import com.android.pupildetection.core.ui.RedPointView;
import com.android.pupildetection.data.CascadeData;
import com.android.pupildetection.settings.SettingsActivity;
import org.opencv.android.CameraBridgeViewBase;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MainActivity2 extends BaseActivity implements MainContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SETTINGS_ACTIVITY_REQUEST_CODE = 200;

    private MainContract.Presenter mPresenter;

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    Double leftPosition;
    Double rightPosition;
    int number;
    String previousState;
    private Matrix originalMatrix = new Matrix();
    private CameraBridgeViewBase camera;
    private TextView tv_instruction;
    private TextView tv_instruction2;
    private MediaPlayer mediaPlayer;
    private RedPointView redPointView;
    private GreenPointView greenPointView;
    private BluePointView bluePointView;
    TextureView video;
    TextView lookAtCamera;
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
        previousState = "Center";
        mPresenter = new MainPresenter(this);

        camera = findViewById(R.id.jcv_camera);
        camera.setVisibility(SurfaceView.VISIBLE);
        camera.setCvCameraViewListener(mPresenter.getCvCameraViewListener());
        camera.setCameraIndex(1); // front 1, back 0
        camera.enableFpsMeter();

        tv_instruction = findViewById(R.id.tv_instruction);
        tv_instruction2 = findViewById(R.id.tv_instruction2);
        video = findViewById(R.id.vdVw);
        lookAtCamera = findViewById(R.id.lookAtCamera);
        redPointView = findViewById(R.id.redPoint);
        greenPointView = findViewById(R.id.greenPoint);
        bluePointView = findViewById(R.id.bluePoint);

        video.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                setupMediaPlayer();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
                // Handle size changes if needed
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                // Handle texture updates if needed
            }
        });


        //Location of Media File
        if (number == 1) {
            uri1 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening1);
        } else if (number == 2){
            uri1 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening1);
        } else if (number == 3){
            uri1 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening1);
        } else if (number == 4){
            uri1 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening1);
        } else if (number == 5){
            uri1 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videolistening1);
        }

    }

    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();


                // Set the data source of the video
        try {
            mediaPlayer.setDataSource(this, uri1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Set the SurfaceTexture as the output surface for the MediaPlayer
                mediaPlayer.setSurface(new Surface(video.getSurfaceTexture()));

                // Prepare the MediaPlayer asynchronously
                mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                    // Start playing the video
                    mediaPlayer.start();

                    // Initializing the original matrix and applying it
                    originalMatrix.set(video.getTransform(null));
                    applyZoom(originalMatrix);
                });
                mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                    // Video playback has finished
                    // Perform your desired action, such as changing the intent
                    Intent newIntent = new Intent(MainActivity2.this, ExaminationActivity.class);
                    newIntent.putExtra("num", number);
                    startActivity(newIntent);

                    // Release the MediaPlayer
                    mediaPlayer.release();
                    mediaPlayer = null;
                });
                mediaPlayer.prepareAsync();

    }

    private Matrix getLeftZoomMatrix() {
        Matrix matrix = new Matrix();

        // Calculate the scale factor for zooming in
        float scaleFactor = 1.4f; // Increase this value to zoom in more

        if (originalMatrix.isIdentity()) {
            originalMatrix.set(matrix);
        }

        // Translate the matrix to the right by half the width of the view
        matrix.setTranslate(video.getWidth() / 1.4f, 0);

        // Scale the matrix by the scaleFactor
        matrix.postScale(scaleFactor, scaleFactor, video.getWidth()/2, video.getHeight()/2);

        // Translate the matrix back to its original position
        matrix.postTranslate(-video.getWidth() / 1.2f, 0);

        return matrix;
    }

    private Matrix getRightZoomMatrix() {
        Matrix matrix = new Matrix();

        // Calculate the scale factor for zooming in
        float scaleFactor = 1.4f; // Increase this value to zoom in more

        if (originalMatrix.isIdentity()) {
            originalMatrix.set(matrix);
        }

        // Translate the matrix to the left by half the width of the view
        matrix.setTranslate(-video.getWidth() / 1.4f, 0);

        // Scale the matrix by the scaleFactor
        matrix.postScale(scaleFactor, scaleFactor, video.getWidth()/2, video.getHeight()/2);

        // Translate the matrix back to its original position
        matrix.postTranslate(video.getWidth() / 1.2f, 0);

        return matrix;
    }

    private void applyZoom(Matrix matrix) {
        video.setTransform(matrix);
    }

    // Create an animation between two matrices
    private Matrix createAnimatorMatrix(Matrix startMatrix, Matrix endMatrix, float fraction) {
        float[] startValues = new float[9];
        float[] endValues = new float[9];
        float[] resultValues = new float[9];

        startMatrix.getValues(startValues);
        endMatrix.getValues(endValues);

        for (int i = 0; i < 9; i++) {
            resultValues[i] = startValues[i] + fraction * (endValues[i] - startValues[i]);
        }

        Matrix resultMatrix = new Matrix();
        resultMatrix.setValues(resultValues);
        return resultMatrix;
    }

    private void applyZoomWithAnimation(Matrix startMatrix, Matrix endMatrix) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(animation -> {
            float fraction = animation.getAnimatedFraction();
            Matrix currentMatrix = createAnimatorMatrix(startMatrix, endMatrix, fraction);
            video.setTransform(currentMatrix);
        });
        animator.setDuration(350); // Animation duration in milliseconds
        animator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
//        setClicks();
    }


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

    @Override
    public void updateCenterPosition(double leftCenter, double rightCenter, int num) {
        leftPosition = leftCenter;
        rightPosition = rightCenter;
        if (leftCenter != 0 && rightCenter != 0){
            if (num == 1) {
                runOnUiThread(() -> {
                    video.setVisibility(View.INVISIBLE);
                    redPointView.setVisibility(View.INVISIBLE);
                    bluePointView.setVisibility(View.VISIBLE);
                    lookAtCamera.setText("Please Look At The Blue Point for a Few Seconds.");
                });
            } else if (num == 2) {
                runOnUiThread(() -> {
                video.setVisibility(View.INVISIBLE);
                bluePointView.setVisibility(View.INVISIBLE);
                greenPointView.setVisibility(View.VISIBLE);
                lookAtCamera.setText("Please Look At The Green Point for a Few Seconds.");
                });
            } else if (num == 3) {
                runOnUiThread(() -> {
                    video.setVisibility(View.VISIBLE);
                    lookAtCamera.setVisibility(View.INVISIBLE);
                    greenPointView.setVisibility(View.GONE);
                    bluePointView.setVisibility(View.GONE);
                    redPointView.setVisibility(View.GONE);
                    video.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                        @Override
                        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                            setupMediaPlayer();
                        }

                        @Override
                        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
                            // Handle size changes if needed
                        }

                        @Override
                        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                            return false;
                        }

                        @Override
                        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                            // Handle texture updates if needed
                        }
                    });
                });
            }
        }
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

    /**
     * @param status       -1 : not detected
     *                     n : # of eyes detected
     * @param messageResId
     */
    @Override
    public void updateCurrentStatus2(int status, int messageResId, int position, boolean maxLeft, boolean maxRight, boolean maxCenter, int left, int right, int center) {
        // update current status text field

        String message2 = this.getString(messageResId);
        runOnUiThread(() -> {
            tv_instruction2.setText(message2 + position + left + "/" + right + "/" + center + "/l:" + maxLeft + "/r:" + maxRight + "/c:" + maxCenter);
            if (maxLeft) {
                if (previousState.equals("Right")) {
                    applyZoomWithAnimation(getRightZoomMatrix(), originalMatrix);
                    previousState = "Center";
                } else if (previousState.equals("Center")){
                    applyZoomWithAnimation(originalMatrix, getLeftZoomMatrix());
                    previousState = "Left";
                }
            }
            if (maxRight) {
                if (previousState.equals("Left")) {
                    applyZoomWithAnimation(getLeftZoomMatrix(), originalMatrix);
                    previousState = "Center";
                } else if (previousState.equals("Center")){
                    applyZoomWithAnimation(originalMatrix, getRightZoomMatrix());
                    previousState = "Right";
                }
            }
            if (maxCenter) {
                applyZoomWithAnimation(previousState.equals("Left") ? getLeftZoomMatrix() : getRightZoomMatrix(), originalMatrix);
                previousState = "Center";
            }
        });

    }

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

