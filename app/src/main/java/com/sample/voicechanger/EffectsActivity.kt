package com.sample.voicechanger

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFmpeg.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.FFmpeg.RETURN_CODE_SUCCESS
import kotlinx.android.synthetic.main.activity_effects.*
import java.io.IOException

private const val LOG_TAG = "AudioRecordTest"

/**
 * Class used to Apply the effects
 */
class EffectsActivity : AppCompatActivity() {

    private var fileName: String = ""
    private var fileNameNew: String = ""
    private var fileNameMerge: String = ""
    private var isEffectAddedOnce = false

    private var recorder: MediaRecorder? = null

    private var player: MediaPlayer? = null
    /**
     * Function to start the Media Player
     */
    private fun start() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileNameNew)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    /**
     * Function to execute FFMPEG Query
     */
    private fun exceuteFFMPEG(cmd: Array<String>) {
        FFmpeg.execute(cmd)
        val rc = FFmpeg.getLastReturnCode()
        val output = FFmpeg.getLastCommandOutput()

        if (rc == RETURN_CODE_SUCCESS) {
            Log.i("GetInfo", "Command execution completed successfully.")
            hideProgress()
            isEffectAddedOnce = true
            start()
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.i("GetInfo", "Command execution cancelled by user.")
        } else {
            Log.i(
                "GetInfo",
                String.format(
                    "Command execution failed with rc=%d and output=%s.",
                    rc,
                    output
                )
            )
        }
    }

    /**
     * Function used to play the audio like a Radio
     */
    private fun playRadio(fileName1: String, fileName2: String) {
        showProgress()
        player?.stop()
        val cmd = arrayOf(
            "-y",
            "-i",
            fileName1,
            "-af",
            "atempo=1",
            fileName2
        )//Radio

        exceuteFFMPEG(cmd)

    }

    /**
     * Function used to play the audio like a Chipmunk
     */
    private fun playChipmunk(fileName1: String, fileName2: String) {
        showProgress()
        player?.stop()
        val cmd = arrayOf(
            "-y",
            "-i",
            fileName1,
            "-af",
            "asetrate=22100,atempo=1/2",
            fileName2
        )//Chipmunk
        exceuteFFMPEG(cmd)
    }

    /**
     * Function used to play the audio like a Robot
     */
    private fun playRobot(fileName1: String, fileName2: String) {
        showProgress()
        player?.stop()
        val cmd = arrayOf(
            "-y",
            "-i",
            fileName1,
            "-af",
            "asetrate=11100,atempo=4/3,atempo=1/2,atempo=3/4",
            fileName2
        )//Robot
        exceuteFFMPEG(cmd)
    }

    /**
     * Function used to play the audio like a Cave
     */
    private fun playCave(fileName1: String, fileName2: String) {
        showProgress()
        player?.stop()
        val cmd = arrayOf(
            "-y",
            "-i",
            fileName1,
            "-af",
            "aecho=0.8:0.9:1000:0.3",
            fileName2
        )//Cave

        exceuteFFMPEG(cmd)

    }


    fun onClick(view: View) {

        when (view.id) {

            R.id.ivBack -> {
                onBackPressed()
            }

            R.id.ivChipmunk -> {
                playChipmunk(fileName, fileNameNew)
            }

            R.id.ivRobot -> {
                playRobot(fileName, fileNameNew)
            }

            R.id.ivRadio -> {
                playRadio(fileName, fileNameNew)
            }

            R.id.ivCave -> {
                playCave(fileName, fileNameNew)
            }

        }

    }

    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.activity_effects)

        // Record to the external cache directory for visibility
        fileName = "${Environment.getExternalStorageDirectory()?.absolutePath}/audioRecord.mp3"
        fileNameNew =
            "${Environment.getExternalStorageDirectory()?.absolutePath}/audioRecordNew.mp3"
        fileNameMerge =
            "${Environment.getExternalStorageDirectory()?.absolutePath}/audioRecordMerge.mp3"


    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }

    private fun showProgress() {
        progress_circular.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progress_circular.visibility = View.GONE
    }
}