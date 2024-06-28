package com.rtc.core

import android.graphics.Bitmap
import com.rtc.core.play.Status


abstract class ZLMRTCPlayer {

    public abstract fun bind(surface: RTCSurfaceView)

    //拉流接口
    public abstract fun play(app: String, streamId: String)

    public abstract fun setVolume(volume:Float)

    public abstract fun stop()

    public abstract fun pause()

    public abstract fun resume()

    public abstract fun capture(listener: (bitmap: Bitmap) -> Unit)

    public abstract fun record(duration: Long, result: (path: String) -> Unit)


    public abstract fun setOnErrorListener(listener: (code: Int, msg: String) -> Unit)


    public abstract fun setOnStatusListener(listener: (status: Status) -> Unit)


}