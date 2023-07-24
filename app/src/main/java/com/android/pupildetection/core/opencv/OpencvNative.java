package com.android.pupildetection.core.opencv;

public class OpencvNative {
    public OpencvNative(){}
    // Native
    public static native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);
    public static native long LoadCascade(String cascadeFileName);

    public static native int[][] DetectFrontalFace(long cascade_face, long matAddrInput);
    public static native int[][] DetectEyes(long cascade_eye, long matAddrInput, int[][] detectedFaces);

    public static native void DetectPupil(long cascade_face, long cascade_eye, long matAddrInput, long matAddrResult);

    
}
