package com.rtc.core

import com.rtc.core.push.PushMode

abstract class ZLMRTCPusher {

    public abstract fun bind(surface: RTCSurfaceView, localPreview: Boolean)

    abstract fun push(
        app: String,
        streamId: String,
        mode: PushMode = PushMode.CAMERA,
        inputFile: String = ""
    )

    abstract fun stop()


    public abstract fun setOnErrorListener(listener: (code: Int, msg: String) -> Unit)


}