package icedo.hector.mipokedex_icedohector

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val pokemons = mutableListOf<Map<String, String>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRegister: FloatingActionButton = findViewById(R.id.btnAddPokemon)
        btnRegister.setOnClickListener {
            val intent = Intent(this, AddPokemon::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        val listView: ListView = findViewById(R.id.listview)
        var adapter = listView.adapter as? PokemonAdapter
        if (adapter == null) {
            adapter = PokemonAdapter(this, pokemons)
            listView.adapter = adapter
        }
        fetchPokemons(adapter)
    }

    private fun fetchPokemons(adapter: PokemonAdapter) {
        db.collection("pokemons")
            .get()
            .addOnSuccessListener { result ->
                pokemons.clear()
                for (document in result) {
                    val pokemon = mapOf(
                        "name" to (document.getString("name") ?: ""),
                        "number" to (document.getString("number") ?: ""),
                        "imageUrl" to (document.getString("imageUrl") ?: "")
                    )
                    pokemons.add(pokemon)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching pokemons", e)
            }
    }
    }

