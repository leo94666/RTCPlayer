package com.rtc.core.push

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.rtc.core.NativeLib
import com.rtc.core.RTCSurfaceView
import com.rtc.core.ZLMRTCPusher
import com.rtc.core.base.ActivityLauncher
import com.rtc.core.base.Error
import com.rtc.core.base.runMain
import com.rtc.core.client.HttpClient
import com.rtc.core.client.PeerConnectionClient
import org.json.JSONObject
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.EglBase
import org.webrtc.FileVideoCapturer
import org.webrtc.IceCandidate
import org.webrtc.PeerConnectionFactory
import org.webrtc.RendererCommon
import org.webrtc.ScreenCapturerAndroid
import org.webrtc.SessionDescription
import org.webrtc.StatsReport
import org.webrtc.VideoCapturer
import java.math.BigInteger
import kotlin.random.Random

class ZLMRTCPusherImpl(val context: FragmentActivity) : ZLMRTCPusher(),
    PeerConnectionClient.PeerConnectionEvents {


    private var peerConnectionClient: PeerConnectionClient? = null

    private var eglBase: EglBase? = null

    private var RTCSurfaceViewRenderer: RTCSurfaceView? = null

    private var localHandleId = BigInteger.valueOf(Random(2048).nextLong())

    private var ScreenShareServiceIntent: Intent? = null


    private var app: String = ""
    private var streamId: String = ""

    private var screenWidth = 1280
    private var screenHeight = 720

    private var listener: ((Int, String) -> Unit)? = null

    override fun setOnErrorListener(listener: (code: Int, msg: String) -> Unit) {
        this.listener = listener
    }

    private fun initPeerConnectionClient(): PeerConnectionClient {
        localHandleId = BigInteger.valueOf(Random(2048).nextLong())
        eglBase = EglBase.create()
        return PeerConnectionClient(
            context, eglBase,
            PeerConnectionClient.PeerConnectionParameters(
                true,
                false,
                false,
                screenWidth,
                screenHeight,
                15,
                0,
                "VP8",
                true,
                false,
                0,
                "OPUS",
                true,
                false,
                false,
                true,
                false,
                false,
                false,
                false, false, false, null
            ), this
        )
    }

    private fun createVideoCapture(context: Context?): VideoCapturer? {
        val videoCapturer: VideoCapturer? = if (Camera2Enumerator.isSupported(context)) {
            createCameraCapture(Camera2Enumerator(context))
        } else {
            createCameraCapture(Camera1Enumerator(true))
        }

        return videoCapturer
    }


    private fun createScreenCapture(context: Context?): VideoCapturer? {
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

    /**
     * Return the width of screen, in pixel.
     *
     * @return the width of screen, in pixel
     */
    private fun getScreenWidth(): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            ?: return -1
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.x
    }

    /**
     * Return the height of screen, in pixel.
     *
     * @return the height of screen, in pixel
     */
    private fun getScreenHeight(): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            ?: return -1
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.y
    }

    private fun logger(msg: String) {
        Log.i("ZLMRTCPusherImpl", msg)
    }


    override fun bind(surface: RTCSurfaceView, localPreview: Boolean) {
        this.RTCSurfaceViewRenderer = surface
    }


    override fun push(app: String, streamId: String, mode: PushMode, inputFile: String) {
        this.app = app
        this.streamId = streamId
        screenWidth = getScreenWidth()
        screenHeight = getScreenHeight()
        if (peerConnectionClient == null) {
            peerConnectionClient = initPeerConnectionClient()
        } else {
            return
        }
        RTCSurfaceViewRenderer?.init(eglBase?.eglBaseContext, object : RendererCommon.RendererEvents {
            override fun onFirstFrameRendered() {
                //Toast.makeText(context, "onFirstFrameRendered", Toast.LENGTH_SHORT).show()
                logger("====================================onFirstFrameRendered")
            }

            override fun onFrameResolutionChanged(
                videoWidth: Int,
                videoHeight: Int,
                rotation: Int
            ) {

            }
        })
        //rtcSurfaceViewRenderer?.setEnableHardwareScaler(true)
        //rtcSurfaceViewRenderer?.setEnableHardwareScaler(true)
        //surfaceViewRenderer?.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_BALANCED)
        peerConnectionClient?.setAudioEnabled(true)
        peerConnectionClient?.setVideoEnabled(true)
        peerConnectionClient?.createPeerConnectionFactory(PeerConnectionFactory.Options())

        if (mode == PushMode.CAMERA) {
            peerConnectionClient?.createPeerConnection(createVideoCapture(context), localHandleId)
            peerConnectionClient?.createOffer(localHandleId)

        } else if (mode == PushMode.SCREEN) {

            val mediaProjectionManager = context.getSystemService(
                Context.MEDIA_PROJECTION_SERVICE
            ) as MediaProjectionManager

            ActivityLauncher.init(context).startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent()
            ) { resultCode, data ->
                if (resultCode == Activity.RESULT_OK) {
                    ScreenShareServiceIntent = Intent(context, ScreenShareService::class.java)
                    ScreenShareServiceIntent?.let {
                        ContextCompat.startForegroundService(
                            context,
                            it
                        )
                        val screenCapturerAndroid =
                            ScreenCapturerAndroid(data, object : MediaProjection.Callback() {

                            })
                        peerConnectionClient?.createPeerConnection(
                            screenCapturerAndroid,
                            localHandleId
                        )
                        peerConnectionClient?.createOffer(localHandleId)
                    }

                }
            }

        } else {
            peerConnectionClient?.createPeerConnection(FileVideoCapturer(inputFile), localHandleId)
            peerConnectionClient?.createOffer(localHandleId)
        }
    }

    override fun stop() {
        ScreenShareServiceIntent?.let {
            context.stopService(it)
        }
        RTCSurfaceViewRenderer?.clearImage()
        RTCSurfaceViewRenderer?.release()
        peerConnectionClient?.stopVideoSource()
        peerConnectionClient?.close()
        peerConnectionClient = null
    }

    override fun onLocalDescription(handleId: BigInteger?, sdp: SessionDescription?) {
        val url = NativeLib().makePushUrl(app, streamId)
        logger("handleId: $url")
        val doPost = HttpClient.doPost(
            url,
            mutableMapOf(Pair("sdp", sdp?.description)),
            mutableMapOf()
        )
        try {
            val result = JSONObject(doPost)
            logger(result.toString())
            val code = result.getInt("code")
            if (code == 0) {
                logger("handleId: $doPost")
                val sdp = result.getString("sdp")
                peerConnectionClient?.setRemoteDescription(
                    handleId,
                    SessionDescription(SessionDescription.Type.ANSWER, sdp)
                )
            } else {
                val msg = result.getString("msg")
                logger("handleId: $msg")
                peerConnectionClient?.setAudioEnabled(false)
                peerConnectionClient?.setVideoEnabled(false)

                runMain {
                    listener?.invoke(
                        code,
                        msg
                    )
                   stop()
                }
//                ScreenShareServiceIntent?.let {
//                    context.stopService(it)
//                }
            }
        } catch (e: Exception) {
            runMain {
                listener?.invoke(
                    Error.ERROR_CODE_PARSE_FAILED.code,
                    Error.ERROR_CODE_PARSE_FAILED.message
                )
               stop()
            }
//            ScreenShareServiceIntent?.let {
//                context.stopService(it)
//            }
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
        if (handleId == localHandleId) {
            peerConnectionClient?.setVideoRender(handleId, RTCSurfaceViewRenderer)
        }
    }

    override fun onRemoteRender(handleId: BigInteger?) {

    }

}