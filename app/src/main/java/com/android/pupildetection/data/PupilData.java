package com.android.pupildetection.data;

import org.opencv.core.Point;

public class PupilData {

    public int pupilsDetected;
    public Point[] pupils;

    public PupilData(int pupilsDetected, Point[] pupils){
        this.pupilsDetected = pupilsDetected;
        this.pupils = pupils;
    }
}
