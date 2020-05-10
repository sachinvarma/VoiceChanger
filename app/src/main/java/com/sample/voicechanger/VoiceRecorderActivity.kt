package com.sample.voicechanger

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_voice_recorder.*
import java.io.IOException


/**
 * Class used to record the voice
 */
class VoiceRecorderActivity : AppCompatActivity() {
    private val LOG_TAG = "AudioRecordTest"
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private var fileName: String = ""
    private var playerBackground: MediaPlayer? = null
    private var playerEffects: MediaPlayer? = null
    private var recorder: MediaRecorder? = null
    private lateinit var countDownTimer: CountDownTimer
    private var permissions: Array<String> =
        arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_recorder)

        // Record to the external cache directory for visibility
        fileName = "${Environment.getExternalStorageDirectory()?.absolutePath}/audioRecord.mp3"

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

    }


    fun onClick(view: View) {

        when (view.id) {

            R.id.btRecord -> {

                if (btRecord.text == getString(R.string.record)) {
                    startRecording()
                } else {
                    btRecord.text = getString(R.string.record)
                    countDownTimer.cancel()
                    stopRecording()
                }
            }

            R.id.ivNext -> {

                startActivity(Intent(this, EffectsActivity::class.java))

            }
        }
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        playerBackground?.release()
        playerEffects?.release()
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        playerBackground = null
        playerEffects = null

    }

    /**
     * Function used to start the recordings
     */
    private fun startRecording() {
        if (ContextCompat.checkSelfPermission(
                this@VoiceRecorderActivity,
                Manifest.permission.RECORD_AUDIO
            ) == -1 ||
            ContextCompat.checkSelfPermission(
                this@VoiceRecorderActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == -1 ||
            ContextCompat.checkSelfPermission(
                this@VoiceRecorderActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == -1
        ) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
            return
        }
        ivNext.visibility = View.GONE
        btRecord.text = getString(R.string.stop)
        tvTimer.text = getString(R.string._30)
        countDownTimer = object : CountDownTimer(31000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                tvTimer.text = (millisUntilFinished / 1000).toString()
                if (tvTimer.text == "1") {
                    Handler().postDelayed({
                        if (!isFinishing) {
                            btRecord.performClick()
                            tvTimer.text = getString(R.string._30)
                        }
                    }, 1000)
                }

            }

            override fun onFinish() {}
        }
        countDownTimer.start()

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    /**
     * Function used to stop the recording
     */
    private fun stopRecording() {
        ivNext.visibility = View.VISIBLE
        playerEffects?.stop()
        playerBackground?.stop()

        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

}