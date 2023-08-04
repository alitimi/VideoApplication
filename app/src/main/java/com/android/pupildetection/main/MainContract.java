package com.android.pupildetection.main;

import android.widget.Button;

import com.android.pupildetection.core.ui.BasePresenter;
import com.android.pupildetection.core.ui.BaseView;

import org.opencv.android.CameraBridgeViewBase;

public interface MainContract {
    interface View extends BaseView<Presenter> {
        void startSettingsActivity();
        void updateCurrentStatus(int status, int messageResId);
        void updateCurrentStatus2(int status, int messageResId, int position, boolean maxLeft, boolean maxRight, boolean maxCenter, int left, int right, int center);
    }
    interface Presenter extends BasePresenter {
        void observeButton(Button button, int flag);
        CameraBridgeViewBase.CvCameraViewListener2 getCvCameraViewListener();
    }
}
