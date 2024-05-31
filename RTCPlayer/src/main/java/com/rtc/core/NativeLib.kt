package com.rtc.core

class NativeLib {


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