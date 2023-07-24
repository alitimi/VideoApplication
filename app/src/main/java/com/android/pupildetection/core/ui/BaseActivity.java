package com.android.pupildetection.core.ui;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.pupildetection.core.opencv.OpencvNative;
import com.android.pupildetection.data.CascadeData;
import com.android.pupildetection.util.FileUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * activity 실행 시
 * 1. 퍼미션 확인
 * 2. 카메라 제어
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private static final int PERMISSION_REQUEST_CODE = 100;

    // BaseActivity를 상속받은 클래스에서 정의해야 하는 메소드
    protected abstract void initView();
    protected abstract void onCameraPermissionGranted();
    protected abstract void enableView();
    protected abstract void disableView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "onResume: Internal OpenCV library not found");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResume: OpenCV library found inside package");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableView();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        boolean havePermission = true;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestRequiredPermissions();
                havePermission = false;
            }
        }
        if(havePermission){
            Log.d(TAG, "havePermission");
            onAllPermissionsGranted();
        }
    }

    private void requestRequiredPermissions(){
        // 허용되지 않은 권한 확인
        ArrayList<String> notGrantedList = new ArrayList<>();

        if(checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED)
            notGrantedList.add(CAMERA); // 허용되지 않음: CAMERA

        if(checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            notGrantedList.add(WRITE_EXTERNAL_STORAGE); // 허용되지 않음: WRITE_EXTERNAL_STORAGE

        if(!notGrantedList.isEmpty()) {
            requestPermissions(notGrantedList.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        } else {
            // 모든 권한이 허용되어 있다면 requestRequiredPermissions 메소드로 들어오지 않음. 예기치 않은 오류.
            Toast.makeText(this, "Permission error. Please re-install app.", Toast.LENGTH_SHORT).show();
            System.exit(0); // 강제종료
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE){
            boolean permFlag = true;
            StringBuilder sb = new StringBuilder();
            // 허용되지 않은 권한을 표시하기 위해 문자열로 만듬
            for(int i=0; i<grantResults.length; i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    sb.append(permissions[i]+"\n");
                    permFlag = false; // 모든 권한이 허용되지 않음
                }
            }
            if(permFlag) { // 모든 권한 허용됨
                onAllPermissionsGranted();
            } else {
                showDialogForPermission("PupilDetection requires following permission:\n"+sb.toString());
            }
        } else {
            showDialogForPermission("Invalid request");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private void showDialogForPermission(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestRequiredPermissions();
                    }
                }).setNegativeButton("QUIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
    }

    private void onAllPermissionsGranted(){
        onExternalStoragePermissionGranted();
        onCameraPermissionGranted();
    }

    private void onExternalStoragePermissionGranted(){
        String[] filesInAssets = new String[0];
        try{
            filesInAssets = getAssets().list(""); // get entries in assets directory
        } catch (IOException e){
            e.printStackTrace();
        }

        if(filesInAssets.length < 1){ // if assets dir is empty
            Log.d(TAG, "Cannot load cascade files from assets directory.");
            showDialogForPermission("Cannot load cascade files from assets directory.");
            return;
        }

        // /storage/emulated/0/PupilDetection
        String projectDir = Environment.getExternalStorageDirectory().getPath()
                +File.separator+"PupilDetection";
        FileUtil.createDir(projectDir);

        // /storage/emulated/0/PupilDetection/cascade
        String baseDir = projectDir+File.separator+"cascade";
        FileUtil.createDir(baseDir);

        for(int i=0; i<filesInAssets.length; i++){
            String filename = filesInAssets[i];
            Log.d(TAG, "Current file from assets directory: "+filename);
            String targetDirs = baseDir+File.separator+filename; // full target file path
            Log.d(TAG, "Copy destination: "+targetDirs);
            File file = new File(targetDirs);
            if(!file.exists()) { // check if file exists
                Log.d(TAG, "File "+targetDirs+" does not exist. Copying ...");
                InputStream is;
                OutputStream os;
                try {
                    is = getAssets().open(filename);
                    os = new FileOutputStream(targetDirs);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = is.read(buffer)) != -1) { // read file
                        os.write(buffer, 0, read); // write file
                    }
                    is.close();
                    os.flush();
                    os.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "File "+targetDirs+" does exist. Skip copying.");
            }
            loadCascade(); // load cascade
        }
    }

    private void loadCascade(){
        if(!CascadeData.readCascade){ // prevent re-load cascades
            // get file entries
            String fileDir = Environment.getExternalStorageDirectory().getPath()
                    +File.separator+"PupilDetection"+File.separator+"cascade";
            String[] fileArr = FileUtil.getDirEntry(fileDir);

            // read cascades
            boolean faceFlag = false, eyeFlag = false;
            for(String filename : fileArr){
                Log.d(TAG, "filePath: "+fileDir+File.separator+filename);
                if(filename.contains("frontalface")) { // read frontalface cascade
                    CascadeData.cascade_frontalface = OpencvNative.LoadCascade(fileDir + File.separator + filename);
                    if(CascadeData.cascade_frontalface != 0) // if read not failed
                        faceFlag = true;
                }
                if(filename.contains("eye")) { // read eye cascade
                    CascadeData.cascade_eyes = OpencvNative.LoadCascade(fileDir + File.separator + filename);
                    if(CascadeData.cascade_eyes != 0) // if read not failed
                        eyeFlag = true;
                }
            }
            if(faceFlag && eyeFlag){ // check all cascades are read
                CascadeData.readCascade = true;
            }
        }
    }

    public abstract void updateCurrentStatus2(int status, int messageResId, boolean maxLeft, boolean maxRight, boolean maxCenter, int left, int right, int center);
}
