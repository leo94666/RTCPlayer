#include <jni.h>
#include <string>

using namespace std;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_rtc_core_NativeLib_makePlayUrl(JNIEnv *env, jobject thiz, jstring app, jstring stream_id) {
    const char *appString = env->GetStringUTFChars(app, 0);
    const char *streamIdString = env->GetStringUTFChars(stream_id, 0);
    char url[100];
    sprintf(url,"https://zlmediakit.com/index/api/webrtc?app=%s&stream=%s&type=play",appString,streamIdString);
    return env->NewStringUTF(url);
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_rtc_core_NativeLib_makePushUrl(JNIEnv *env, jobject thiz, jstring app, jstring stream_id) {
    const char *appString = env->GetStringUTFChars(app, 0);
    const char *streamIdString = env->GetStringUTFChars(stream_id, 0);
    char url[100];
    sprintf(url,"https://zlmediakit.com/index/api/webrtc?app=%s&stream=%s&type=push",appString,streamIdString);
    return env->NewStringUTF(url);
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_rtc_core_NativeLib_makeEchoUrl(JNIEnv *env, jobject thiz, jstring app, jstring stream_id) {
    const char *appString = env->GetStringUTFChars(app, 0);
    const char *streamIdString = env->GetStringUTFChars(stream_id, 0);
    char url[100];
    sprintf(url,"https://zlmediakit.com/index/api/webrtc?app=%s&stream=%s&type=echo",appString,streamIdString);
    return env->NewStringUTF(url);
}

