package com.rtc.core

import com.rtc.core.base.Platform

class NativeLib {

    fun init(baseUrl: String, platform: Platform) {

    }

    fun makePushUrl(app: String, streamId: String, srs: Platform): String {
        return makePushUrl(app, streamId, Platform.ZLMediaKit)
    }



    external fun makePlayUrl(app:String,streamId:String): String

    external fun makePushUrl(app:String,streamId:String): String


    companion object {
        val instance: NativeLib by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NativeLib()
        }

        // Used to load the 'rtc' library on application startup.
        init {
            System.loadLibrary("rtc")
        }
    }
}