package com.rtc.core

import com.rtc.core.base.Platform

object RTCCore {

    var BASE_URL = ""
    var PLATFORM = Platform.ZLMediaKit

    fun init(baseUrl: String) {
        BASE_URL = baseUrl
        PLATFORM = Platform.ZLMediaKit
        NativeLib.instance.init(baseUrl)
    }

//    fun init(baseUrl: String, platform: Platform = Platform.ZLMediaKit) {
//        BASE_URL = baseUrl
//        PLATFORM = platform
//        NativeLib.instance.init(baseUrl, platform)
//    }

}