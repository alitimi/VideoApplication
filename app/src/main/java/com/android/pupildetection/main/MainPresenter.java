package com.android.pupildetection.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.jakewharton.rxbinding3.view.RxView;
import com.android.pupildetection.R;
import com.android.pupildetection.core.opencv.OpencvApi;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainPresenter implements MainContract.Presenter, CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private MainContract.View mView;

    private static ArrayList<Bitmap> bitmaps = new ArrayList<>();
    int left = 0;
    int right = 0;
    int center = 0;
    Boolean centerPassed = false;
    Boolean leftPassed = false;
    Boolean rightPassed = false;
    Double averageLeft;
    Double averageRight;
    Double eyeLeftAverageLeft;
    Double eyeRightAverageLeft;
    Double eyeLeftAverageRight;
    Double eyeRightAverageRight;
    Double positionAverage = 0.0;
    float centerPosition;
    int frameNumber = 0;
    private static ArrayList<Double> centerPositions = new ArrayList<>();
    private static ArrayList<Double> EyeLeftcenterPositions = new ArrayList<>();
    private static ArrayList<Double> EyeRightcenterPositions = new ArrayList<>();
    private static ArrayList<Double> leftPositions = new ArrayList<>();
    private static ArrayList<Double> EyeLeftleftPositions = new ArrayList<>();
    private static ArrayList<Double> EyeRightleftPositions = new ArrayList<>();
    private static ArrayList<Double> rightPositions = new ArrayList<>();
    private static ArrayList<Double> EyeLeftrightPositions = new ArrayList<>();
    private static ArrayList<Double> EyeRightrightPositions = new ArrayList<>();

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
//        mView.saveVideo(bitmaps);
    }

    public native void initVideoWriter(String outputPath);
    public native void writeFrame(long matAddr);

    private static Bitmap convertMatToBitMap(Mat input){
        Bitmap bmp = null;
        Mat rgb = new Mat();
        Imgproc.cvtColor(input, rgb, Imgproc.COLOR_BGR2RGB);

        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rgb, bmp);
            bitmaps.add(bmp);
        }
        catch (CvException e){
            Log.d("Exception",e.getMessage());
        }
        return bmp;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        frameNumber += 1;
        Mat matInput = inputFrame.rgba();
//        initVideoWriter(outputPath);
//        writeFrame(matInput.dataAddr());
        Mat matModified = Imgproc.getRotationMatrix2D(new Point(matInput.cols() / 2, matInput.rows() / 2), 90, 1);
        Imgproc.warpAffine(matInput, matInput, matModified, matInput.size());

        int[][] detectedFace = OpencvApi.detectFrontalFace(matInput);
        if (detectedFace == null || detectedFace.length < 1) {
            // detected no face
            mView.updateCurrentStatus(-1, R.string.msg_face_not_detected);
            Imgproc.ellipse(matInput, new Point(matInput.cols() / 2, matInput.rows() / 2), new Size(matInput.cols() / 3, matInput.rows() / 1.5), 0, 0, 360, new Scalar(255, 0, 0), 10, 8, 0);
            return matInput;
        }
        Log.d(TAG, "face detected!: " + detectedFace.length);
        for (int i = 0; i < detectedFace.length; i++) {
            Log.d(TAG, "face" + i + ":" + detectedFace[i][0] + "/" + detectedFace[i][1] + "/" + detectedFace[i][2] + "/" + detectedFace[i][3]);
        }

        boolean isEyesDetected = false;
        boolean maxLeft = false;
        boolean maxCenter = false;
        boolean maxRight = false;
        int eyesCnt = 0;
        int[][] detectedEyes = OpencvApi.detectEyes(matInput, detectedFace);
        Imgproc.ellipse(matInput, new Point(matInput.cols()/2, matInput.rows()/2), new Size(matInput.cols()/3, matInput.rows()/1.5), 0, 0, 360, new Scalar(0, 255, 255), 10, 8, 0);

        if (rightPositions.size() < 151) {

            if (detectedEyes != null && detectedEyes.length != 0) {
                if (detectedEyes[0] != null) {
                    if (detectedEyes[0].length > 4) {
                        centerPositions.add((double) detectedEyes[0][0]);
                        centerPositions.add((double) detectedEyes[0][4]);
                    }
                }
            }
            if (centerPositions.size() > 150) {
                if (!centerPassed) {
                    for (int j = 5; j < centerPositions.size(); j++) {
                        if (centerPositions.get(j) > 500) {
                            EyeRightcenterPositions.add(centerPositions.get(j));
                        } else {
                            EyeLeftcenterPositions.add(centerPositions.get(j));
                        }
                    }

                    averageLeft = calculateAverage(EyeLeftcenterPositions);
                    averageRight = calculateAverage(EyeRightcenterPositions);
                    mView.updateCenterPosition(averageLeft, averageRight, 1);
                    centerPassed = true;
                }

                if (detectedEyes != null && detectedEyes.length != 0) {
                    if (detectedEyes[0] != null) {
                        if (detectedEyes[0].length > 4) {
                            leftPositions.add((double) detectedEyes[0][0]);
                            leftPositions.add((double) detectedEyes[0][4]);
                        }
                    }
                }
                if (leftPositions.size() > 150) {
                    if (!leftPassed) {
                        for (int j = 5; j < leftPositions.size(); j++) {
                            if (leftPositions.get(j) > 500) {
                                EyeRightleftPositions.add(leftPositions.get(j));
                            } else {
                                EyeLeftleftPositions.add(leftPositions.get(j));
                            }
                        }
                        eyeLeftAverageLeft = calculateAverage(EyeLeftleftPositions);
                        eyeRightAverageLeft = calculateAverage(EyeRightleftPositions);
                        mView.updateCenterPosition(eyeLeftAverageLeft, eyeRightAverageLeft, 2);
                        leftPassed = true;
                    }

                    if (detectedEyes != null && detectedEyes.length != 0) {
                        if (detectedEyes[0] != null) {
                            if (detectedEyes[0].length > 4) {
                                rightPositions.add((double) detectedEyes[0][0]);
                                rightPositions.add((double) detectedEyes[0][4]);
                            }
                        }
                    }
                    if (rightPositions.size() >= 150) {
                        for (int j = 5; j < rightPositions.size(); j++) {
                            if (rightPositions.get(j) > 500) {
                                EyeRightrightPositions.add(rightPositions.get(j));
                            } else {
                                EyeLeftrightPositions.add(rightPositions.get(j));
                            }
                        }
                        eyeLeftAverageRight = calculateAverage(EyeLeftrightPositions);
                        eyeRightAverageRight = calculateAverage(EyeRightrightPositions);
                        mView.updateCenterPosition(eyeLeftAverageRight, eyeRightAverageRight, 3);
                    }
                }
            }
        }

        if (centerPositions.size() > 150 && leftPositions.size() > 150 && rightPositions.size() > 150) {
            if (detectedEyes != null && detectedEyes.length != 0) {
                if (detectedEyes[0] != null) {
                    Log.d(TAG, "pupil loc: " + detectedEyes[0][0] + " " + detectedEyes[0][1]);
                    horizontalRatio = detectedEyes[0][0];
                    Log.d(TAG, "H " + horizontalRatio + " ");
                    if (positionAverage != 0) {
                        positionAverage = 0.25 * positionAverage + 0.75 * horizontalRatio;
                    } else {
                        positionAverage = (double) horizontalRatio;
                    }
                    if (averageLeft != null && averageRight != null) {
                        if (positionAverage > 500) { // Right eye
                            if (positionAverage < eyeRightAverageRight + 1) {
                                Log.d(TAG, "Right");
                                right += 1;
                            } else if (eyeRightAverageLeft - 14 < positionAverage) {
                                Log.d(TAG, "Left");
                                left += 1;
                            } else {
                                Log.d(TAG, "Center");
                                center += 1;
                            }
                        } else { // Left eye
                            if (positionAverage < eyeLeftAverageRight + 1) {
                                Log.d(TAG, "Right");
                                right += 1;
                            } else if (eyeLeftAverageLeft - 14 < positionAverage) {
                                Log.d(TAG, "Left");
                                left += 1;
                            } else {
                                Log.d(TAG, "Center");
                                center += 1;
                            }
                        }
                    }

                    isEyesDetected = true;
                    eyesCnt++;
                }
            }
        }

//        if(isEyesDetected){
//            if ((left > 30 && right < 20) || (right > 30 && left < 20) || center > 30) {
//                if (Math.max(Math.max(left, right), center) == left) {
//                    maxLeft = true;
//                    mView.updateCurrentStatus2(eyesCnt, R.string.msg_look_left, horizontalRatio, maxLeft, maxRight, maxCenter, left, right, center);
//                } else if (Math.max(Math.max(left, right), center) == right) {
//                    maxRight = true;
//                    mView.updateCurrentStatus2(eyesCnt, R.string.msg_look_right, horizontalRatio, maxLeft, maxRight, maxCenter, left, right, center);
//                } else if (Math.max(Math.max(left, right), center) == center) {
//                    maxCenter = true;
//                    mView.updateCurrentStatus2(eyesCnt, R.string.msg_look_center, horizontalRatio, maxLeft, maxRight, maxCenter, left, right, center);
//                } else {
//                    mView.updateCurrentStatus2(eyesCnt, R.string.msg_eyes_not_trackable, horizontalRatio, maxLeft, maxRight, maxCenter, left, right, center);
//                }
//                left = 0;
//                right = 0;
//                center = 0;
//            }
//        } else {
//            mView.updateCurrentStatus(0, R.string.msg_eyes_not_detected);
//        }

        if(isEyesDetected && (left > 9 || center > 15 || right > 9)){
            if (Math.max(Math.max(left, right), center) == left) {
                maxLeft = true;
                mView.updateCurrentStatus3(bitmaps, eyesCnt ,R.string.msg_look_left, horizontalRatio, maxLeft, maxRight, maxCenter, left, right, center);
            } else if (Math.max(Math.max(left, right), center) == right){
                maxRight = true;
                mView.updateCurrentStatus3(bitmaps,eyesCnt , R.string.msg_look_right, horizontalRatio, maxLeft, maxRight, maxCenter, left, right, center);
            }
            else if(Math.max(Math.max(left, right), center) == center) {
                maxCenter = true;
                mView.updateCurrentStatus3(bitmaps, eyesCnt, R.string.msg_look_center, horizontalRatio,maxLeft, maxRight, maxCenter, left, right, center);
            } else {
                mView.updateCurrentStatus3(bitmaps, eyesCnt, R.string.msg_eyes_not_trackable, horizontalRatio, maxLeft, maxRight, maxCenter, left, right, center);
            }
            left = 0;
            right = 0;
            center = 0;
        } else {
            mView.updateCurrentStatus(0, R.string.msg_eyes_not_detected);
        }


        Imgproc.ellipse(matInput, new Point(matInput.cols()/2, matInput.rows()/2), new Size(matInput.cols()/3, matInput.rows()/1.5), 0, 0, 360, new Scalar(0, 255, 255), 10, 8, 0);

//        nativeMethod.ConvertRGBtoGray(matInput.getNativeObjAddr(), matInput.getNativeObjAddr());
//        Core.flip(matInput, matInput, 1);
//        Core.rotate(matInput, matInput, Core.ROTATE_90_CLOCKWISE);
//        OpencvNative.DetectFrontalFace(CascadeData.cascade_frontalface, matInput.getNativeObjAddr(), matInput.getNativeObjAddr());
//        OpencvNative.DetectPupil(CascadeData.cascade_frontalface, CascadeData.cascade_eyes, matInput.getNativeObjAddr(), matInput.getNativeObjAddr());
//        OpencvNative.DetectEyes(CascadeData.cascade_frontalface, CascadeData.cascade_eyes, matInput.getNativeObjAddr(), matInput.getNativeObjAddr());

        //        if (frameNumber % 2 == 0) {
        convertMatToBitMap(matInput);
//        }
        return matInput;
    }

    private double calculateAverage(ArrayList <Double> marks) {
        double sum = 0;
        for (int i = 0; i < marks.size(); i++) {
            sum += marks.get(i);
        }
        return sum / marks.size();
    }
}
