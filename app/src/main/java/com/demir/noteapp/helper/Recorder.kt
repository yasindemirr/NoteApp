import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.demir.noteapp.R
import java.io.File

class Recorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var fileName: String? = null
    var file: File? = null
    var test: Int? = null
    val mediaPlayer = MediaPlayer()

    init {
        test
        file
        mediaPlayer
    }

    fun startRecording() {
        fileName = "${System.currentTimeMillis()}.mp3"
        file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName!!)
        recorder = MediaRecorder().apply {
            try {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file?.absolutePath)
                prepare()
                start()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun stopRecording(): String? {
        recorder?.apply {
            try {
                stop()
                release()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        return fileName
    }

    fun playAudioFile(filex: String) {
        if (filex.isNotEmpty()) {

            try {
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(filex)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                    test = R.drawable.resume_icon

                } else {
                    mediaPlayer.pause()
                    test = R.drawable.play_icon
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopPlaying() {
        mediaPlayer.stop()
        mediaPlayer.release()

    }
}