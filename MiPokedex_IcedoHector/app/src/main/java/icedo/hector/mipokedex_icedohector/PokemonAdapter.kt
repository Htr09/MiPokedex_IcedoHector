package icedo.hector.mipokedex_icedohector

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class PokemonAdapter(private val context: Context, private val data: List<Map<String, String>>) : BaseAdapter() {

    override fun getCount(): Int = data.size

    override fun getItem(position: Int): Any = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.pokemon_item, parent, false)
        val pokemon = data[position]

        val nameTextView: TextView = view.findViewById(R.id.tvpokemonName)
        val numberTextView: TextView = view.findViewById(R.id.tvpokemonNumber)
        val imageView: ImageView = view.findViewById(R.id.ivpokemon)

        nameTextView.text = pokemon["name"]
        numberTextView.text = pokemon["number"]
        Glide.with(context).load(pokemon["imageUrl"]).into(imageView)

        return view
    }
}