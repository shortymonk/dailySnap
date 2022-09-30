package com.shortymonk.someapp

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.FileOutputStream
import java.io.IOException

class CaptionedSnapAdapter(
    private val snapList: List<Snap>,
    private val scope: LifecycleCoroutineScope,
    private val context: Context,
   private val container: SnapContainer
    ) : RecyclerView.Adapter<CaptionedSnapAdapter.SnapViewHolder>() {

    inner class SnapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stubSnapImageView: ImageView = itemView.findViewById(R.id.snap_stub)
        val snapImageView: ImageView = itemView.findViewById(R.id.snap_image)
        private val snapName: TextView = itemView.findViewById(R.id.snap_name)
        val snapVideo: VideoView = itemView.findViewById(R.id.snap_video)
        var job: Job? = null
        var loadingPosition = 0

        fun updateText(position: Int) {
            val snap = snapList[position]
            snapName.apply {
                text = snap.name
                contentDescription = snap.name
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
        val snap = snapList[position]

        if (snap.thumbnail.exists()) {
            holder.snapImageView.apply {
                setImageURI(snap.thumbnailUri)
                hide(false)
            }
            holder.stubSnapImageView.visibility = View.GONE
        } else {
            holder.stubSnapImageView.apply {
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

            scope.launch(Dispatchers.IO) {
                if ((holder.job != null) && (!holder.job!!.isActive)) {
                    holder.job!!.cancel()
                }
                holder.job = async {

                    val bitmap = loadBitmap(position)

                    holder.snapImageView.post(Runnable {
                        if (holder.loadingPosition == position) {
                            holder.snapImageView.setImageBitmap(bitmap)
                            holder.snapImageView.hide(false)
                            holder.stubSnapImageView.hide(true)
                        }
                    })
                }
            }
        }

        holder.itemView.setOnClickListener { item ->
            container.videoView.apply {
                visibility = View.VISIBLE
                setVideoPath(snap.fullPath)
                this.start()
            }
            container.background.apply {
                visibility = View.VISIBLE
            }
            container.videoView.setOnClickListener(null)
        }

        container.background.setOnClickListener {
            container.videoView.apply {
                visibility = View.INVISIBLE
                stopPlayback()
            }
            it.visibility = View.INVISIBLE
        }

       /* holder.snapImageView.setOnClickListener {
            container.videoView.apply {
                setVideoPath(snap.fullPath)
                visibility = View.VISIBLE
            }
            container.background.visibility = View.VISIBLE
            val message = snapList[position].fullPath
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }*/
    }

    override fun getItemCount(): Int {
        return snapList.size
    }

    private fun loadBitmap(position: Int): Bitmap? {
        val snap = snapList[position]
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(snap.fullPath)
        val bitmap = retriever.getScaledFrameAtTime(
            0, MediaMetadataRetriever.OPTION_CLOSEST,
            200, 200
        )

        try {
            val fOut = FileOutputStream(snap.thumbnail)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
            Log.d("file is written", snap.name)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }

    private fun ImageView.hide(yes: Boolean) {
        val float = if (yes) 0f else 1f
        this.animate()
            .alpha(float)
            .scaleX(float)
            .scaleY(float)
            .duration = 250L
    }
}

