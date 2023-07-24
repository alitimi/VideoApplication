package com.android.pupildetection.util;

import java.io.File;

public class FileUtil {

    public static boolean createDir(String dir){
        File dirFile = new File(dir);
        if(!dirFile.exists())
            return dirFile.mkdir();
        else
            return false;
    }

    public static String[] getDirEntry(String dir){
        return new File(dir).list();
    }

    public static boolean copyFile(String orig, String dest){
        File origFile = new File(orig);
        File destFile = new File(dest);

        return false;
    }
}
