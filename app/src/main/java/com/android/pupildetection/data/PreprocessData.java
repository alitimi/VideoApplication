package com.android.pupildetection.data;

public class PreprocessData {
    public int scaling, brightness, contrast, edge, gamma;

    public PreprocessData(int scaling, int brightness, int contrast, int edge, int gamma){
        this.scaling =scaling;
        this.brightness = brightness;
        this.contrast = contrast;
        this.edge = edge;
        this.gamma = gamma;
    }

    public int getScaling() {
        return scaling;
    }

    public void setScaling(int scaling) {
        this.scaling = scaling;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getContrast() {
        return contrast;
    }

    public void setContrast(int contrast) {
        this.contrast = contrast;
    }

    public int getEdge() {
        return edge;
    }

    public void setEdge(int edge) {
        this.edge = edge;
    }

    public int getGamma() {
        return gamma;
    }

    public void setGamma(int gamma) {
        this.gamma = gamma;
    }
}
