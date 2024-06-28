package com.rtc.core

import com.rtc.core.play.Status

abstract class Intercom {


    public abstract fun play(app: String, streamId: String)

    public abstract fun stop()

    public abstract fun setOnErrorListener(listener: (code: Int, msg: String) -> Unit)

    public abstract fun setOnStatusListener(listener: (status: Status) -> Unit)
}