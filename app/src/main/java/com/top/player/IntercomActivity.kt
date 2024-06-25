package com.top.player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rtc.core.ZLMRTCPlayer
import com.rtc.core.play.SRSRTCPlayerImpl
import com.rtc.core.play.ZLMRTCPlayerImpl
import com.top.player.databinding.ActivityIntercomBinding
import com.top.player.databinding.ActivityPlayerBinding

class IntercomActivity: AppCompatActivity() {

    private val player: ZLMRTCPlayer by lazy {
        ZLMRTCPlayerImpl(this)
    }

    private val binding by lazy {
        ActivityIntercomBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setTitle("Intercom Demo")
    }
}