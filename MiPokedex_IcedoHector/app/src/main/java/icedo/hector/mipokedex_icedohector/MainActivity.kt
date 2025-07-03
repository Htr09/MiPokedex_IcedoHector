package icedo.hector.mipokedex_icedohector

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class MainActivity : AppCompatActivity() {
    val CLOUD_NAME = "dob719uzm"
    val UPLOAD_PRESET = "pokemon-upload"
    var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initCloudinary() {
        val config: MutableMap<String, String> = HashMap<String, String>()
        config["cloud_name"] = CLOUD_NAME
        MediaManager.init(this, config)
    }

    fun uploadPokemon(): String {
        var url: String = ""
        if (imageUri != null) {
            MediaManager.get().upload(imageUri).unsigned(UPLOAD_PRESET)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Log.d("Start", "Upload start")
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        Log.d("Progress", "Upload in progress")
                    }

                    override fun onSuccess(
                        requestId: String?,
                        resultData: MutableMap<Any?, Any?>?
                    ) {
                        url = resultData?.get("url") as String? ?: ""

                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        TODO("Not yet implemented")
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        TODO("Not yet implemented")
                    }

                }).dispatch()


        }

        return url
    }


}