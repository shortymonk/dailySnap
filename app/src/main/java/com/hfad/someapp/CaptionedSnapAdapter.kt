package com.hfad.someapp

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

class CaptionedSnapAdapter(
    private val snaps: List<String>,
    private val cacheDir: File,
    private val asyncLoader: LifecycleCoroutineScope
    ) : RecyclerView.Adapter<CaptionedSnapAdapter.SnapViewHolder>() {

    inner class SnapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val snapStubImageView: ImageView = itemView.findViewById(R.id.snap_stub)
        val snapImageView: ImageView = itemView.findViewById(R.id.snap_image)
        private val snapName: TextView = itemView.findViewById(R.id.snap_name)
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_caption_snap, parent, false)
        return SnapViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SnapViewHolder, position: Int) {
        holder.updateText(position)
        val blank = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val imageFile = File(cacheDir, snaps[position].hashCode().toString() + ".jpeg")

        if (imageFile.exists()) {
            holder.snapImageView.apply {
                setImageURI(imageFile.toUri())
                animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .duration = 250L
            }
            holder.snapStubImageView.visibility = View.GONE
        } else {
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

            asyncLoader.launch(Dispatchers.IO) {
                if ((holder.job != null) && (!holder.job!!.isActive)) {
                    holder.job!!.cancel()
                }
                holder.job = async {

                    val bitmap = loadBitmap(position)

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
    }

    override fun getItemCount(): Int {
        return snaps.size
    }
    
    fun loadBitmap(position: Int): Bitmap? {
        val fullPath = snaps[position]
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(fullPath)
        val bitmap = retriever.getScaledFrameAtTime(
            0, MediaMetadataRetriever.OPTION_CLOSEST,
            200, 200
        )
        val name = fullPath.hashCode().toString() + ".jpeg"
        val file = File(cacheDir, name)

        if (!file.exists()) {
            val fOut = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
            Log.d("file is written", name)
        }
        return bitmap
    }
}

