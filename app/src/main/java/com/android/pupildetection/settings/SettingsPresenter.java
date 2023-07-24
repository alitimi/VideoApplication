package com.android.pupildetection.settings;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxSeekBar;
import com.jakewharton.rxbinding3.widget.RxTextView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class SettingsPresenter implements SettingsContract.Presenter, CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = SettingsPresenter.class.getSimpleName();

    private SettingsContract.View mView;

    public SettingsPresenter(SettingsContract.View mView){
        this.mView = mView;
        mView.setPresenter(this);
    }

    private CompositeDisposable mCompositeDisposable;

    @Override
    public void subscribe() {
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

    @Override
    public void observeEditText(EditText editText, Activity activity, SeekBar seekbar, int max) {
        int maxValue = max-1;
        Disposable disposable = RxTextView.textChanges(editText)
                .debounce(400, TimeUnit.MILLISECONDS)
                .subscribe(charSequence -> {
                    if(TextUtils.isEmpty(charSequence)){
                        Log.d(TAG, "field empty");
                    } else if(Integer.parseInt(charSequence.toString()) < 0){
                        Log.d(TAG, "value less than 0");
                        activity.runOnUiThread(() -> {
                            editText.setText("0");
                        });
                    } else if(Integer.parseInt(charSequence.toString()) > maxValue){
                        Log.d(TAG, "value more than limit("+maxValue+")");
                        activity.runOnUiThread(() -> {
                            editText.setText(String.valueOf(maxValue));
                        });
                    } else {
                        Log.d(TAG, "value "+charSequence);
                        activity.runOnUiThread(() -> {
                            seekbar.setProgress(Integer.parseInt(charSequence.toString()));
                        });
                    }
                }, error -> {
                    Log.e(TAG, error.getLocalizedMessage());
                });
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void observeSeekBar(SeekBar seekBar, Activity activity, EditText editText) {
        Disposable disposable = RxSeekBar.changes(seekBar)
                .debounce(200, TimeUnit.MILLISECONDS)
                .subscribe(integer -> {
                    activity.runOnUiThread(() -> {
                        editText.setText(Integer.toString(integer));
                    });
                }, error -> {
                    Log.e(TAG, error.getLocalizedMessage());
                });
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void observeView(View view, int flag) {
        Disposable disposable = RxView.clicks(view)
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribe(data -> {
                    switch (flag){
                        case 0: // capture
                            Log.d(TAG, "capture");
                            mView.startCameraActivity();
                            break;
                        case 1: // save
                            Log.d(TAG, "save");
                            break;
                        case 2: // reset
                            Log.d(TAG, "reset");
                            break;
                        case 3: // exit
                            Log.d(TAG, "exit");
                            mView.finishActivity();
                            break;
                        case 4: // camera click
                            Log.d(TAG, "camera");
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
    public CameraBridgeViewBase.CvCameraViewListener2 getCvCameraViewListener() {
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
        Mat matInput = inputFrame.gray();
        Mat matModified = Imgproc.getRotationMatrix2D(new Point(matInput.cols()/2, matInput.rows()/2), 90, 1);
        Imgproc.warpAffine(matInput, matInput, matModified, matInput.size());
        return matInput;
    }

    // Native

}
