package tahadeta.example.mascover.ui.wallpaper

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import tahadeta.example.mascover.R
import tahadeta.example.mascover.data.Wallpaper
import tahadeta.example.mascover.util.WallpaperHelper
import tahadeta.example.mascover.util.listFavourite

class WallpaperAdapter(
    private val context: Context?,
    private val list: List<Wallpaper>
) : RecyclerView.Adapter<WallpaperAdapter.ViewHolder>() {
    /**
     * Find all the views of the list item
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageAddFavourite: ImageView = itemView.findViewById(R.id.addFavouriteImage)
        private val imageWallpaper: ImageView = itemView.findViewById(R.id.imageWallpaper)

        fun bindView(item: Wallpaper, position: Int, context: Context?) {
            WallpaperHelper.setImage(imageWallpaper, item.pathPoster.toString())
            Log.d("FERETJJD", "enter ==== ")

            val drawable1 = ContextCompat.getDrawable(
                context!!.applicationContext,
                R.drawable.star
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bindView(item, position, context)

        val img = holder.itemView.findViewById<ImageView>(R.id.addFavouriteImage)

        img.setOnClickListener {
            // Compare Favourite Image using Tag
            if (img.tag.equals("notFavourite")) {
                img.setImageResource(R.drawable.star_fill)
                img.tag = "isFavourite"
                // set an action to save data in our SharedPreferences
                listFavourite.add(item)
            } else {
                img.setImageResource(R.drawable.star)
                img.tag = "notFavourite"
                // set an action to save data in our SharedPreferences
                // implement remove
                listFavourite.remove(item)
            }
        }

        // Parse path of the poster to be displayed
        holder.itemView.findViewById<ImageView>(R.id.imageWallpaper).setOnClickListener {

            val action = ListWallpaperFragmentDirections
                .actionListWallpaperFragmentToDetailWallpaperFragment(
                    item.idWallpaper.toString(), item.pathPoster4K.toString()
                )
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.wallpaper_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = list.size
}
