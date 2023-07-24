package com.android.pupildetection.settings;

import android.app.Activity;
import android.widget.EditText;
import android.widget.SeekBar;

import com.android.pupildetection.core.ui.BasePresenter;
import com.android.pupildetection.core.ui.BaseView;

import org.opencv.android.CameraBridgeViewBase;

public interface SettingsContract {

    interface View extends BaseView<Presenter> {
        void finishActivity();

        void startCameraActivity();
    }

    interface Presenter extends BasePresenter {
        void observeEditText(EditText editText, Activity activity, SeekBar seekbar, int max);
        void observeSeekBar(SeekBar seekBar, Activity activity, EditText editText);
        void observeView(android.view.View view, int flag);
        CameraBridgeViewBase.CvCameraViewListener2 getCvCameraViewListener();
    }
}
