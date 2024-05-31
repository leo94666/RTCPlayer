package com.rtc.core.play

import android.content.Context
import android.graphics.Bitmap
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.util.Log
import com.rtc.core.NativeLib
import com.rtc.core.RTCSurfaceView
import com.rtc.core.ZLMRTCPlayer
import com.rtc.core.base.Error
import com.rtc.core.base.runMain
import com.rtc.core.client.HttpClient
import com.rtc.core.client.PeerConnectionClient
import org.json.JSONObject
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.PeerConnectionFactory
import org.webrtc.RendererCommon
import org.webrtc.SessionDescription
import org.webrtc.StatsReport
import org.webrtc.VideoCapturer
import java.io.File
import java.math.BigInteger
import kotlin.random.Random

class ZLMRTCPlayerImpl(val context: Context) : ZLMRTCPlayer(),
    PeerConnectionClient.PeerConnectionEvents {

    private var RTCSurfaceViewRenderer: RTCSurfaceView? = null

    private var eglBase: EglBase? = null

    private var defaultFps = 24


    private var peerConnectionClient: PeerConnectionClient? = null


    private var localHandleId = BigInteger.valueOf(Random(1024).nextLong())

    private var audioManager: AudioManager? = null

    private var app: String = ""
    private var streamId: String = ""


    private var listener: ((Int, String) -> Unit)? = null

    override fun setOnErrorListener(listener: (code: Int, msg: String) -> Unit) {
        this.listener = listener
    }


    private var preparedlistener: ((Status) -> Unit)? = null

    override fun setOnStatusListener(listener: (status: Status) -> Unit) {
        this.preparedlistener = listener
    }

    private fun logger(msg: String) {
        Log.i("ZLMRTCPlayerImpl", msg)
    }

    private fun createVideoCapture(context: Context?): VideoCapturer? {
        val videoCapturer: VideoCapturer? = if (Camera2Enumerator.isSupported(context)) {
            createCameraCapture(Camera2Enumerator(context))
        } else {
            createCameraCapture(Camera1Enumerator(true))
        }
        return videoCapturer
    }

    /**
     * 创建相机媒体流
     */
    private fun createCameraCapture(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames

        // Front facing camera not found, try something else
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        // First, try to find front facing camera
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }


        return null
    }

    private fun initPeerConnectionClient(): PeerConnectionClient {
        eglBase = EglBase.create()
        return PeerConnectionClient(
            context, eglBase,
            PeerConnectionClient.PeerConnectionParameters(
                false,
                false,
                false,
                1280,
                720,
                defaultFps,
                1024 * 1024 * 2,
                "H264",
                true,
                false,
                1024 * 1024 * 2,
                "opus",
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false, false, false, null
            ), this
        )
    }

    override fun bind(surface: RTCSurfaceView) {
        this.RTCSurfaceViewRenderer = surface
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager?.isSpeakerphoneOn = true
    }

    override fun play(app: String, streamId: String) {
        runMain {
            preparedlistener?.invoke(Status.PREPARING)
        }
        this.app = app
        this.streamId = streamId
        if (peerConnectionClient == null) {
            peerConnectionClient = initPeerConnectionClient()
        } else {
            return
        }
        RTCSurfaceViewRenderer?.init(eglBase?.eglBaseContext, object : RendererCommon.RendererEvents {
            override fun onFirstFrameRendered() {
                //Toast.makeText(context, "onFirstFrameRendered", Toast.LENGTH_SHORT).show()
                logger("====================================onFirstFrameRendered")
                runMain {
                    preparedlistener?.invoke(Status.PLAYING)
                }
            }

            override fun onFrameResolutionChanged(
                videoWidth: Int,
                videoHeight: Int,
                rotation: Int
            ) {

            }
        })
        peerConnectionClient?.setAudioEnabled(true)
        peerConnectionClient?.createPeerConnectionFactory(PeerConnectionFactory.Options())
        peerConnectionClient?.createPeerConnection(createVideoCapture(context), localHandleId)
        peerConnectionClient?.createOffer(localHandleId)
    }


    override fun setVolume(volume:Float){
        audioManager?.isSpeakerphoneOn = true
        val maxVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_SYSTEM) ?: 0
        val minVolume = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            audioManager?.getStreamMinVolume(AudioManager.STREAM_SYSTEM) ?: 0
        } else {
            0
        }


        audioManager?.setStreamVolume(
            AudioManager.STREAM_SYSTEM,
            (minVolume + (maxVolume - minVolume) * volume).toInt(),
//            AudioManager.FLAG_SHOW_UI
                    AudioManager.FLAG_PLAY_SOUND

        )
    }

    override fun stop() {
        RTCSurfaceViewRenderer?.clearImage()
        RTCSurfaceViewRenderer?.release()
        peerConnectionClient?.stopVideoSource()
        peerConnectionClient?.close()
        peerConnectionClient = null
        runMain {
            preparedlistener?.invoke(Status.STOP)
        }
    }

    override fun pause() {
        RTCSurfaceViewRenderer?.pauseVideo()
        runMain {
            preparedlistener?.invoke(Status.PAUSE)
        }
    }


    override fun resume() {
        RTCSurfaceViewRenderer?.setFpsReduction(defaultFps.toFloat())
        runMain {
            preparedlistener?.invoke(Status.RESUME)
        }
    }

    override fun capture(listener: (bitmap: Bitmap) -> Unit) {
        RTCSurfaceViewRenderer?.addFrameListener({
            listener.invoke(it)
        }, 1f)
    }

    override fun record(duration: Long, result: (path: String) -> Unit) {

        val savePath =
            context.cacheDir.absoluteFile.absolutePath + File.separator + System.currentTimeMillis() + ".mp4"
        peerConnectionClient?.setRecordEnable(true, savePath)
        Handler().postDelayed({
            peerConnectionClient?.setRecordEnable(false, savePath)
        }, duration)
    }


    override fun onLocalDescription(handleId: BigInteger?, sdp: SessionDescription?) {
        val url = NativeLib().makePlayUrl(app, streamId)
        logger("handleId: $url")
        logger("OFFER: \n" + sdp?.description)
        val doPost = HttpClient.doPost(
            url,
            mutableMapOf(Pair("sdp", sdp?.description)),
            mutableMapOf()
        )
        try {
            val result = JSONObject(doPost)
            val code = result.getInt("code")
            if (code == 0) {
                val sdp = result.getString("sdp")
                logger("ANSWER: \n$sdp")
                peerConnectionClient?.setRemoteDescription(
                    handleId,
                    SessionDescription(SessionDescription.Type.ANSWER, sdp)
                )
            } else {
                val msg = result.getString("msg")
                logger("handleId: $msg")
                runMain {
                    listener?.invoke(code, msg)
                    preparedlistener?.invoke(Status.ERROR)
                    stop()
                }

            }
        } catch (e: Exception) {
            runMain {
                listener?.invoke(
                    Error.ERROR_CODE_PARSE_FAILED.code,
                    Error.ERROR_CODE_PARSE_FAILED.message
                )
                preparedlistener?.invoke(Status.ERROR)
                stop()
            }

        }

    }

    override fun onIceCandidate(handleId: BigInteger?, candidate: IceCandidate?) {

    }

    override fun onIceCandidatesRemoved(
        handleId: BigInteger?,
        candidates: Array<out IceCandidate>?
    ) {

    }

    override fun onIceConnected(handleId: BigInteger?) {

    }

    override fun onIceDisconnected(handleId: BigInteger?) {

    }

    override fun onPeerConnectionClosed(handleId: BigInteger?) {

    }

    override fun onPeerConnectionStatsReady(
        handleId: BigInteger?,
        reports: Array<out StatsReport>?
    ) {

    }

    override fun onPeerConnectionError(handleId: BigInteger?, description: String?) {

    }

    override fun onLocalRender(handleId: BigInteger?) {
        logger("onLocalRender: " + handleId)
        //peerConnectionClient?.setVideoRender(handleId, surfaceViewRenderer)
//        if (handleId == localHandleId) {
//            peerConnectionClient?.setVideoRender(handleId, surfaceViewRenderer)
//        }
    }

    override fun onRemoteRender(handleId: BigInteger?) {
        logger("onRemoteRender: " + handleId)
        if (handleId == localHandleId) {
            peerConnectionClient?.setVideoRender(handleId, RTCSurfaceViewRenderer)
        }
    }


}