package com.top.player

import android.app.Application
import com.rtc.core.RTCCore
import com.rtc.core.base.Platform

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        //RTCCore.init("https://zlmediakit.com/")
        RTCCore.init("http://111.229.192.2:1985")

        //http://111.229.192.2:1985/rtc/v1/whep/?app=live&stream=li
    }
}