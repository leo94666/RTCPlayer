package com.rtc.core.push

import android.content.Context
import org.webrtc.CapturerObserver
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoCapturer
import org.webrtc.VideoFrame

class FileVideoCapturer: VideoCapturer {


    private interface VideoReader {
        val nextFrame: VideoFrame?

        fun close()
    }

    private class VideoReaderMp4 : VideoReader {
        override val nextFrame: VideoFrame?
            get() = TODO("Not yet implemented")

        override fun close() {
            TODO("Not yet implemented")
        }
    }



    override fun initialize(
        surfaceTextureHelper: SurfaceTextureHelper?,
        applicationContext: Context?,
        capturerObserver: CapturerObserver?
    ) {

    }

    override fun startCapture(width: Int, height: Int, framerate: Int) {

    }

    override fun stopCapture() {

    }

    override fun changeCaptureFormat(width: Int, height: Int, framerate: Int) {

    }

    override fun dispose() {

    }

    override fun isScreencast(): Boolean {
        return false
    }
}