package tahadeta.example.mascover.ui.favourite

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import tahadeta.example.mascover.R
import tahadeta.example.mascover.data.Wallpaper
import tahadeta.example.mascover.util.WallpaperHelper

class FavouriteListAdapter(
    private val context: Context?,
    private val list: List<Wallpaper>
) : RecyclerView.Adapter<FavouriteListAdapter.ViewHolder>() {
    /**
     * Find all the views of the list item
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageWallpaper: ImageView = itemView.findViewById(R.id.imageWallpaper)

        fun bindView(item: Wallpaper, position: Int, context: Context?) {
            WallpaperHelper.setImage(imageWallpaper, item.pathPoster.toString())
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bindView(item, position, context)

        val img = holder.itemView.findViewById<ImageView>(R.id.addFavouriteImage)
        img.setImageResource(R.drawable.star_fill)

        img.setOnClickListener {
            // Compare Favourite Image using Tag
            if (img.tag.equals("notFavourite")) {
                img.setImageResource(R.drawable.star_fill)
                img.tag = "isFavourite"
            } else {
                img.setImageResource(R.drawable.star)
                img.tag = "notFavourite"
            }
        }

        // Parse path of the poster to be displayed
        holder.itemView.findViewById<ImageView>(R.id.imageWallpaper).setOnClickListener {
            val action = FavouriteFragmentDirections.actionFavouriteFragmentToDetailWallpaperFragment(
                item.pathPoster.toString(), item.pathPoster4K.toString()
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
