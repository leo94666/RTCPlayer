#include <jni.h>
#include <string>

using namespace std;

static const char *basUrl = "https://zlmediakit.com";

extern "C"
JNIEXPORT void JNICALL
Java_com_rtc_core_NativeLib_init(JNIEnv *env, jobject thiz, jstring base_url) {
    basUrl = env->GetStringUTFChars(base_url, 0);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_rtc_core_NativeLib_makePlayUrl(JNIEnv *env, jobject thiz, jstring app, jstring stream_id) {
    const char *appString = env->GetStringUTFChars(app, 0);
    const char *streamIdString = env->GetStringUTFChars(stream_id, 0);
    char url[100];
    sprintf(url, "%s/index/api/webrtc?app=%s&stream=%s&type=play", basUrl, appString,
            streamIdString);
    return env->NewStringUTF(url);
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_rtc_core_NativeLib_makePushUrl(JNIEnv *env, jobject thiz, jstring app, jstring stream_id) {
    const char *appString = env->GetStringUTFChars(app, 0);
    const char *streamIdString = env->GetStringUTFChars(stream_id, 0);
    char url[100];
    sprintf(url, "%s/index/api/webrtc?app=%s&stream=%s&type=push", basUrl, appString,
            streamIdString);
    return env->NewStringUTF(url);
}


