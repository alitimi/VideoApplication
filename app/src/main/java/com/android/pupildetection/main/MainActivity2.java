package com.android.pupildetection.main;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import org.opencv.videoio.VideoWriter;
import org.opencv.core.Size;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity2 extends BaseActivity implements MainContract.View {

    private static ArrayList<Bitmap> bitmaps;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SETTINGS_ACTIVITY_REQUEST_CODE = 200;

    private static final String VIDEO_MIME_TYPE = "video/avc";
    private static final int FRAME_RATE = 30;
    private static final int IFRAME_INTERVAL = 5;
    private static final int TIMEOUT_USEC = 10000; // 10ms

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
    private static String user;
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
        user = b.getString("user");
        previousState = "Center";
        mPresenter = new MainPresenter(this);

//        File directory;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            directory = new File(Environment.getExternalStorageDirectory() + "/Recordings/");
//        } else {
//            directory = new File(Environment.getExternalStorageDirectory() + "/Recordings/");
//        }
//        if (!directory.exists() && !directory.mkdirs()) {
//            // Handle error here; maybe the directory creation failed
//        }
//        File outputFile = new File(directory, "output.avi");
//        outputPath = outputFile.getAbsolutePath();
//        int fourcc = VideoWriter.fourcc('M', 'J', 'P', 'G'); // or use 'X','V','I','D' or others
//        Size frameSize = new Size(720, 960);
//        videoWriter = new VideoWriter(outputPath, fourcc, 1, frameSize, true);

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
            runOnUiThread(() -> camera.setVisibility(View.INVISIBLE));

            // Create and show the progress dialog
            ProgressDialog progressDialog = new ProgressDialog(MainActivity2.this);
            progressDialog.setMessage("Converting to video...");
            progressDialog.setIndeterminate(true); // This means the progress bar will be animated without showing actual progress
            progressDialog.setCancelable(false); // This means users cannot cancel the dialog by tapping outside
            progressDialog.show();

            new Thread(() -> {
                // Convert bitmaps in the background
                convertBitmapsToMP4(bitmaps);

                // After the conversion is done, move to the next activity
                runOnUiThread(() -> {
                    progressDialog.dismiss(); // Dismiss the progress dialog

                    Intent newIntent = new Intent(MainActivity2.this, ExaminationActivity.class);
                    newIntent.putExtra("num", number);
                    startActivity(newIntent);

                    // Release the MediaPlayer
                });
            }).start();

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
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
    public void updateCurrentStatus2(int status, int messageResId, int position, boolean maxLeft, boolean maxRight, boolean maxCenter, int left, int right, int center) {

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

    @Override
    public void saveVideo(ArrayList<Bitmap> bitmap) {
//        bitmaps = new ArrayList<>();
//        bitmaps.addAll(bitmap);
//        convertBitmapsToMP4(bitmaps);
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
    public void updateCurrentStatus3(ArrayList<Bitmap> bitmap, int status, int messageResId, int position, boolean maxLeft, boolean maxRight, boolean maxCenter, int left, int right, int center) {
        // update current status text field
        bitmaps = new ArrayList<>();
        bitmaps.addAll(bitmap);
        String message2 = this.getString(messageResId);
        runOnUiThread(() -> {
            tv_instruction2.setText(message2 + position + left + "/" + right + "/" + center + "/l:" + maxLeft + "/r:" + maxRight + "/c:" + maxCenter);
            if (maxLeft) {
                if (previousState.equals("Right")) {
                    applyZoomWithAnimation(getRightZoomMatrix(), getLeftZoomMatrix());
                    previousState = "Left";
                } else if (previousState.equals("Center")){
                    applyZoomWithAnimation(originalMatrix, getLeftZoomMatrix());
                    previousState = "Left";
                }
            }
            if (maxRight) {
                if (previousState.equals("Left")) {
                    applyZoomWithAnimation(getLeftZoomMatrix(), getRightZoomMatrix());
                    previousState = "Right";
                } else if (previousState.equals("Center")){
                    applyZoomWithAnimation(originalMatrix, getRightZoomMatrix());
                    previousState = "Right";
                }
            }
            if (maxCenter) {
                if (previousState.equals("Left") || previousState.equals("Right")) {
                    applyZoomWithAnimation(previousState.equals("Left") ? getLeftZoomMatrix() : getRightZoomMatrix(), originalMatrix);
                    previousState = "Center";
                } else {
                    //Do Nothing
                }
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

    public static void convertBitmapsToMP4(ArrayList<Bitmap> bitmaps1) {
        MediaCodec codec = null;
        MediaMuxer muxer = null;

        try {
            int videoWidth = bitmaps1.get(0).getWidth();
            int videoHeight = bitmaps1.get(0).getHeight();
            MediaFormat format = MediaFormat.createVideoFormat(VIDEO_MIME_TYPE, videoWidth, videoHeight);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_BIT_RATE, 2000000);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

            codec = MediaCodec.createEncoderByType(VIDEO_MIME_TYPE);
            codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            Surface inputSurface = codec.createInputSurface();
            codec.start();

            String OUTPUT_FILE = Environment.getExternalStorageDirectory() + "/" + user + ".mp4";
            muxer = new MediaMuxer(OUTPUT_FILE, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int trackIndex = -1;
            boolean muxerStarted = false;

            for (int i = 0; i < bitmaps1.size(); i++) {
                drawBitmapToSurface(inputSurface, bitmaps1.get(i), videoWidth, videoHeight);

                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                int encoderStatus;
                do {
                    encoderStatus = codec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                    if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        // no output available yet
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        // should happen before receiving buffers
                        if (muxerStarted) {
                            throw new RuntimeException("format changed twice");
                        }
                        MediaFormat newFormat = codec.getOutputFormat();
                        trackIndex = muxer.addTrack(newFormat);
                        muxer.start();
                        muxerStarted = true;
                    } else if (encoderStatus < 0) {
                        // unexpected status
                    } else {
                        ByteBuffer encodedData = codec.getOutputBuffer(encoderStatus);
                        if (muxerStarted) {
                            muxer.writeSampleData(trackIndex, encodedData, bufferInfo);
                        }
                        codec.releaseOutputBuffer(encoderStatus, false);
                    }
                } while (encoderStatus >= 0);
            }

            codec.signalEndOfInputStream(); // Inform codec of the end of stream.
            // Rest of the cleanup and finalizing code...

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (muxer != null) {
                try {
                    muxer.stop();
                    muxer.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (codec != null) {
                codec.stop();
                codec.release();
            }
        }
    }

    private static void drawBitmapToSurface(Surface surface, Bitmap bitmap, int width, int height) {
        Canvas canvas = surface.lockCanvas(null);
        try {
            Rect rect = new Rect(0, 0, width, height);
            canvas.drawBitmap(bitmap, null, rect, null);
        } finally {
            surface.unlockCanvasAndPost(canvas);
        }
    }

}

