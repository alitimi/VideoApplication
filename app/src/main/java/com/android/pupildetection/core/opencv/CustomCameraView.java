package com.android.pupildetection.core.opencv;

import android.content.Context;
import android.util.AttributeSet;

import org.opencv.android.JavaCamera2View;

public class CustomCameraView extends JavaCamera2View {
    public CustomCameraView(Context context, int cameraId) {
        super(context, cameraId);
    }

    public CustomCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void takePicture(){
        lockFocus();
    }

    private void lockFocus(){
//        try{
//            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
//                    CameraMetadata.CONTROL_AF_TRIGGER_START);
//
//            mCaptureSession.capture(mPreviewRequestBuilder.build(), ,)
//        }
    }
}
