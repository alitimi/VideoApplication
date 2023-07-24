package com.android.pupildetection.core.opencv;

import android.util.Log;

import com.android.pupildetection.data.CascadeData;

import org.opencv.core.Mat;

public class OpencvApi {

    private static final String TAG = OpencvApi.class.getSimpleName();

    /**
     *
     * @param input
     * @return int[] contains info of detected face in Rect form
     * int[0] -> face x
     * int[1] -> face y
     * int[2] -> face width
     * int[3] -> face height
     */
    public static int[][] detectFrontalFace(Mat input){
        int[][] result = null;
        try{
            result = OpencvNative.DetectFrontalFace(CascadeData.cascade_frontalface, input.getNativeObjAddr());
        } catch (Exception e){
            Log.d(TAG, "Exception: "+e.getLocalizedMessage());
        }
        return result;
    }

    public static int[][] detectEyes(Mat input, int[][] detectedFaces){
        return OpencvNative.DetectEyes(CascadeData.cascade_eyes, input.getNativeObjAddr(), detectedFaces);
    }
}
