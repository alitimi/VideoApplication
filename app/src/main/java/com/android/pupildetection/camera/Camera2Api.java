package com.android.pupildetection.camera;

import android.content.Context;
import android.util.AttributeSet;

import org.opencv.android.JavaCamera2View;

public class Camera2Api extends JavaCamera2View {
    public Camera2Api(Context context, int cameraId) {
        super(context, cameraId);
    }

    public Camera2Api(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


}
