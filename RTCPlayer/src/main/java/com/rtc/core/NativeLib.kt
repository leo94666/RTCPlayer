package com.rtc.core

class NativeLib {

    /**
     * A native method that is implemented by the 'rtc' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun exchangeSessionDescription(description:String): String

    external fun makePlayUrl(app:String,streamId:String): String

    external fun makePushUrl(app:String,streamId:String): String

    external fun makeEchoUrl(app:String,streamId:String): String

    companion object {
        // Used to load the 'rtc' library on application startup.
        init {
            System.loadLibrary("rtc")
        }
    }
}