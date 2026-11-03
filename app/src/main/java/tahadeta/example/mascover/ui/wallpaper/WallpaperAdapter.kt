package tahadeta.example.mascover.ui.wallpaper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import tahadeta.example.mascover.R
import tahadeta.example.mascover.data.Wallpaper
import tahadeta.example.mascover.util.WallpaperHelper
import tahadeta.example.mascover.util.addFavourite
import tahadeta.example.mascover.util.listFavourite
import tahadeta.example.mascover.util.removeFavourite

class WallpaperAdapter(
    private val context: Context?,
    private val list: MutableList<Wallpaper>
) : RecyclerView.Adapter<WallpaperAdapter.ViewHolder>() {

    fun submit(items: List<Wallpaper>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }
    /**
     * Find all the views of the list item
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageAddFavourite: ImageView = itemView.findViewById(R.id.addFavouriteImage)
        private val imageWallpaper: ImageView = itemView.findViewById(R.id.imageWallpaper)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val newCl: ConstraintLayout = itemView.findViewById(R.id.new_cl)


        fun bindView(item: Wallpaper, position: Int, context: Context?) {

            val showNew = item.isNew || item.type.equals("NEW", ignoreCase = true)
            newCl.visibility = if (showNew) View.VISIBLE else View.GONE

            WallpaperHelper.setImage(imageWallpaper, item.pathPoster.toString(), context!!,progressBar)

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bindView(item, position, context)

        val img = holder.itemView.findViewById<ImageView>(R.id.addFavouriteImage)
        val alreadyFavourite = listFavourite.any { it.idWallpaper == item.idWallpaper }
        img.setImageResource(if (alreadyFavourite) R.drawable.star_fill else R.drawable.star)
        img.tag = if (alreadyFavourite) "isFavourite" else "notFavourite"

        img.setOnClickListener {
            // Compare Favourite Image using Tag
            if (img.tag.equals("notFavourite")) {
                img.setImageResource(R.drawable.star_fill)
                img.tag = "isFavourite"
                addFavourite(item)
            } else {
                img.setImageResource(R.drawable.star)
                img.tag = "notFavourite"
                removeFavourite(item)
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
