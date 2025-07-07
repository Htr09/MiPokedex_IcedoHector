package icedo.hector.mipokedex_icedohector

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.firestore.FirebaseFirestore

class AddPokemon : AppCompatActivity() {

    companion object {
        var isCloudinaryInitialized = false
    }


    val CLOUD_NAME = "dob719uzm"
    val REQUEST_IMAGE_GET = 1
    val UPLOAD_PRESET = "pokemon-upload"
    var imageUri: Uri? = null

    private lateinit var name: EditText
    private lateinit var number: EditText
    private lateinit var upload: Button
    private lateinit var save: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_pokemon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        name= findViewById(R.id.etName)
        number = findViewById(R.id.etNumber)
        upload = findViewById(R.id.btnUploadImage)
        save = findViewById(R.id.btnSavePokemon)


        initCloudinary()

        upload.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent,REQUEST_IMAGE_GET)
        }

        save.setOnClickListener {
            uploadPokemon()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val fullPhotoUri: Uri? = data?.data
            if (fullPhotoUri != null) {
                imageUri = fullPhotoUri // Guarda el URI de la imagen seleccionada
                changeImage(fullPhotoUri) // Actualiza la vista con la imagen seleccionada
            } else {
                Log.e("ImageSelection", "No image URI returned")
            }
        }
    }

    fun changeImage(uri:Uri){
        val thumbnail: ImageView = findViewById(R.id.thumbnail)
        try{
            thumbnail.setImageURI(uri)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    private fun initCloudinary() {
        if (!isCloudinaryInitialized) {
            val config: MutableMap<String, String> = HashMap()
            config["cloud_name"] = CLOUD_NAME
            MediaManager.init(this, config)
            isCloudinaryInitialized = true
        }
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
                        val imageUrl = resultData?.get("secure_url") as String? ?: ""
                        val pokemon = hashMapOf(
                            "name" to name.text.toString(),
                            "number" to number.text.toString(),
                            "imageUrl" to imageUrl
                        )
                        db.collection("pokemons")
                            .add(pokemon)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Pokemon saved successfully")
                                finish() // Close the activity after saving
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error saving Pokemon", e)
                            }

                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e("Cloudinary", "Image upload failed: ${error.description}")
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {

                    }

                }).dispatch()


        }

        return url
    }

}