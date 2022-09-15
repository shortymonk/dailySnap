package com.hfad.someapp
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class CaptionedSnapAdapter(private val snaps: List<String>) :
    RecyclerView.Adapter<CaptionedSnapAdapter.SnapViewHolder>() {

    inner class SnapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val snapImageView: ImageView = itemView.findViewById(R.id.snap_thumbnail)
        val snapStubImageView: ImageView = itemView.findViewById(R.id.snap_stub)
        val snapImageView: ImageView = itemView.findViewById(R.id.snap_image)
        val snapName: TextView = itemView.findViewById(R.id.snap_name)
        var job: Job? = null
        var loadingPosition = 0

        fun updateText(position: Int) {
            val path = snaps[position]
            snapName.apply {
                path.substringAfterLast("/")
                text = path.substringAfterLast("/")
                contentDescription = path.substringAfterLast("/")
            }
        }

        @RequiresApi(Build.VERSION_CODES.O_MR1)
        fun getImage(position: Int): Bitmap? {
            val path = snaps[position]
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(path)
            return retriever.getScaledFrameAtTime(
                0, MediaMetadataRetriever.OPTION_CLOSEST,
                100, 100
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_caption_snap, parent, false)
        return SnapViewHolder(itemView)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: SnapViewHolder, position: Int) {
        holder.updateText(position)
        val blank = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)

        holder.snapStubImageView.apply {
            setImageBitmap(blank)
            alpha = 1F
            scaleX = 1F
            scaleY = 1F
        }
        holder.snapImageView.apply {
            setImageBitmap(blank)
            alpha = 0F
        }

        holder.loadingPosition = position

        GlobalScope.launch {
            if ((holder.job != null) && (!holder.job!!.isActive)) {
                holder.job!!.cancel()
            }

            holder.job = async {
                val bitmap = holder.getImage(position)
                holder.snapImageView.post(Runnable {
                    if (holder.loadingPosition == position) {
                        holder.snapImageView.setImageBitmap(bitmap)
                        holder.snapImageView.animate()
                            .alpha(1f)
                            .scaleX(1f)
                            .scaleY(1f)
                            .duration = 250L
                        holder.snapStubImageView.animate()
                            .alpha(0f)
                            .scaleX(0f)
                            .scaleY(0f)
                            .duration = 250L
                    }
                })
            }
        }
    }

    override fun getItemCount(): Int {
        return snaps.size
    }
}

