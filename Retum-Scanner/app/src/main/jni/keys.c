//
// Created by Rehman Murad Ali on 1/22/2018.
//

#include <jni.h>
#include <string.h>



JNIEXPORT jstring JNICALL
Java_com_example_vend_newmlkitdemoapp_cloud_vision_utils_CloudVisionHelper_a(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "AIzaSyBSENDZupOjnNDCjc2QZ2QoF9UnZvRT29g");
}





