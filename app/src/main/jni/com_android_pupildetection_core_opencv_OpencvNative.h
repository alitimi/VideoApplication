/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_android_pupildetection_core_opencv_OpencvNative */

#ifndef _Included_com_android_pupildetection_core_opencv_OpencvNative
#define _Included_com_android_pupildetection_core_opencv_OpencvNative
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_android_pupildetection_core_opencv_OpencvNative
 * Method:    ConvertRGBtoGray
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_com_android_pupildetection_core_opencv_OpencvNative_ConvertRGBtoGray
  (JNIEnv *, jobject, jlong, jlong);


JNIEXPORT void JNICALL Java_com_android_pupildetection_main_MainPresenter_initVideoWriter(JNIEnv* env, jobject, jstring outputPath);

JNIEXPORT void JNICALL Java_com_android_pupildetection_main_MainPresenter_writeFrame(JNIEnv* env, jobject, jlong matAddr);


/*
 * Class:     com_android_pupildetection_core_opencv_OpencvNative
 * Method:    LoadCascade
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_android_pupildetection_core_opencv_OpencvNative_LoadCascade
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_android_pupildetection_core_opencv_OpencvNative
 * Method:    DetectFrontalFace
 * Signature: (JJJ)V
 */
JNIEXPORT jobjectArray JNICALL Java_com_android_pupildetection_core_opencv_OpencvNative_DetectFrontalFace
  (JNIEnv *, jobject, jlong, jlong);

/*
 * Class:     com_android_pupildetection_core_opencv_OpencvNative
 * Method:    DetectEyes
 * Signature: (JJJ)V
 */
JNIEXPORT jobjectArray JNICALL Java_com_android_pupildetection_core_opencv_OpencvNative_DetectEyes
  (JNIEnv *, jobject, jlong, jlong, jobjectArray);

/*
 * Class:     com_android_pupildetection_core_opencv_OpencvNative
 * Method:    DetectPupil
 * Signature: (JJJJ)V
 */
JNIEXPORT void JNICALL Java_com_android_pupildetection_core_opencv_OpencvNative_DetectPupil
  (JNIEnv *, jobject, jlong, jlong, jlong, jlong);

#ifdef __cplusplus
}
#endif
#endif
