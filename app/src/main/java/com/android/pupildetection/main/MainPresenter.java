package com.android.pupildetection.main;

import android.util.Log;
import android.widget.Button;
import com.jakewharton.rxbinding3.view.RxView;
import com.android.pupildetection.R;
import com.android.pupildetection.core.opencv.OpencvApi;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.util.concurrent.TimeUnit;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainPresenter implements MainContract.Presenter, CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private MainContract.View mView;

    int left = 0;
    int right = 0;
    int center = 0;

    public MainPresenter(MainContract.View mView){
        this.mView = mView;
    }

    private CompositeDisposable mCompositeDisposable;

    private int horizontalRatio;

    @Override
    public void subscribe() {
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

    @Override
    public void observeButton(Button button, int flag) {
        Disposable disposable = RxView.clicks(button)
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribe(data -> {
                    switch (flag){
                        case 0: // register
                            break;
                        case 1: // verify
                            break;
                        case 2: // delete
                            break;
                        case 3: // cancel
                            break;
                        case 4: // save
                            break;
                        case 5: // settings
                            mView.startSettingsActivity();
                            break;
                        default:
                            break;
                    }
                }, error -> {
                    Log.d(TAG, error.getLocalizedMessage());
                });
        mCompositeDisposable.add(disposable);
    }

    // CameraBridgeViewBase.CvCameraViewListener2
    @Override
    public CameraBridgeViewBase.CvCameraViewListener2 getCvCameraViewListener(){
        return this;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat matInput = inputFrame.rgba();
        Mat matModified = Imgproc.getRotationMatrix2D(new Point(matInput.cols()/2, matInput.rows()/2), 90, 1);
        Imgproc.warpAffine(matInput, matInput, matModified, matInput.size());

        int[][] detectedFace = OpencvApi.detectFrontalFace(matInput);
        if(detectedFace == null || detectedFace.length < 1) {
            // detected no face
            mView.updateCurrentStatus(-1,R.string.msg_face_not_detected);
            Imgproc.ellipse(matInput, new Point(matInput.cols()/2, matInput.rows()/2), new Size(matInput.cols()/3, matInput.rows()/1.5), 0, 0, 360, new Scalar(255, 0, 0), 10, 8, 0);
            return matInput;
        }
        Log.d(TAG, "face detected!: "+detectedFace.length);
        for(int i=0; i<detectedFace.length; i++){
            Log.d(TAG, "face"+i+":"+detectedFace[i][0]+"/"+detectedFace[i][1]+"/"+detectedFace[i][2]+"/"+detectedFace[i][3]);
        }

        boolean isEyesDetected = false;
        boolean maxLeft = false;
        boolean maxCenter = false;
        boolean maxRight = false;
        int eyesCnt = 0;
        int[][] detectedEyes = OpencvApi.detectEyes(matInput, detectedFace);
        if(detectedEyes != null && detectedEyes.length != 0){
            if(detectedEyes[0] != null) {
                Log.d(TAG, "pupil loc: " + detectedEyes[0][0] + " " + detectedEyes[0][1]);
                horizontalRatio = detectedEyes[0][0];
                Log.d(TAG, "H " + horizontalRatio + " ");
                float eyeCenter = (detectedEyes[0][2] + detectedEyes[0][3]) / 2;
                if (detectedEyes[0][3] - horizontalRatio >= horizontalRatio - detectedEyes[0][2]) {
                    Log.d(TAG, "Right");
                    right += 1;
                }
                if (detectedEyes[0][3] - horizontalRatio <= horizontalRatio - detectedEyes[0][2] + 20) {
                    Log.d(TAG, "Left");
                    left += 1;
                }
                if (Math.abs((detectedEyes[0][3] - horizontalRatio) - (horizontalRatio - detectedEyes[0][2])) < 20) {
                    Log.d(TAG, "Center");
                    center += 1;
                }
                isEyesDetected = true;
                eyesCnt++;
            }

        }

        if(isEyesDetected){
            if ((left > 30 && right < 20) || (right > 30 && left < 20) || center > 30) {
                if (Math.max(Math.max(left, right), center) == left) {
                    maxLeft = true;
                    mView.updateCurrentStatus2(eyesCnt, R.string.msg_look_left, horizontalRatio, maxLeft, maxRight, maxCenter, left, right, center);
                } else if (Math.max(Math.max(left, right), center) == right) {
                    maxRight = true;
                    mView.updateCurrentStatus2(eyesCnt, R.string.msg_look_right, horizontalRatio, maxLeft, maxRight, maxCenter, left, right, center);
                } else if (Math.max(Math.max(left, right), center) == center) {
                    maxCenter = true;
                    mView.updateCurrentStatus2(eyesCnt, R.string.msg_look_center, horizontalRatio, maxLeft, maxRight, maxCenter, left, right, center);
                } else {
                    mView.updateCurrentStatus2(eyesCnt, R.string.msg_eyes_not_trackable, horizontalRatio, maxLeft, maxRight, maxCenter, left, right, center);
                }
                left = 0;
                right = 0;
                center = 0;
            }
        } else {
            mView.updateCurrentStatus(0, R.string.msg_eyes_not_detected);
        }

//        if(isEyesDetected && left > 30 || center > 30 || right > 30){
//            if (Math.max(Math.max(left, right), center) == left) {
//                maxLeft = true;
//                mView.updateCurrentStatus2(eyesCnt ,R.string.msg_look_left, horizontalRatio, maxLeft, maxRight, maxCenter, left, right, center);
//            } else if (Math.max(Math.max(left, right), center) == right){
//                maxRight = true;
//                mView.updateCurrentStatus2(eyesCnt , R.string.msg_look_right, horizontalRatio, maxLeft, maxRight, maxCenter, left, right, center);
//            }
//            else if(Math.max(Math.max(left, right), center) == center) {
//                maxCenter = true;
//                mView.updateCurrentStatus2(eyesCnt, R.string.msg_look_center, horizontalRatio,maxLeft, maxRight, maxCenter, left, right, center);
//            } else {
//                mView.updateCurrentStatus2(eyesCnt, R.string.msg_eyes_not_trackable, horizontalRatio, maxLeft, maxRight, maxCenter, left, right, center);
//            }
//            left = 0;
//            right = 0;
//            center = 0;
//        } else {
//            mView.updateCurrentStatus(0, R.string.msg_eyes_not_detected);
//        }


        Imgproc.ellipse(matInput, new Point(matInput.cols()/2, matInput.rows()/2), new Size(matInput.cols()/3, matInput.rows()/1.5), 0, 0, 360, new Scalar(0, 255, 255), 10, 8, 0);

//        nativeMethod.ConvertRGBtoGray(matInput.getNativeObjAddr(), matInput.getNativeObjAddr());
//        Core.flip(matInput, matInput, 1);
//        Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
//        OpencvNative.DetectFrontalFace(CascadeData.cascade_frontalface, matInput.getNativeObjAddr(), matInput.getNativeObjAddr());
//        OpencvNative.DetectPupil(CascadeData.cascade_frontalface, CascadeData.cascade_eyes, matInput.getNativeObjAddr(), matInput.getNativeObjAddr());
//        OpencvNative.DetectEyes(CascadeData.cascade_frontalface, CascadeData.cascade_eyes, matInput.getNativeObjAddr(), matInput.getNativeObjAddr());
        return matInput;
    }
}
