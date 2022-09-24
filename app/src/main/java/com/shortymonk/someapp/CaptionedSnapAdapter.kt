package com.shortymonk.someapp

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.FileOutputStream
import java.io.IOException

class CaptionedSnapAdapter(
    private val snapList: List<Snap>,
    private val scope: LifecycleCoroutineScope
    ) : RecyclerView.Adapter<CaptionedSnapAdapter.SnapViewHolder>() {

    inner class SnapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val snapStubImageView: ImageView = itemView.findViewById(R.id.snap_stub)
        val snapImageView: ImageView = itemView.findViewById(R.id.snap_image)
        private val snapName: TextView = itemView.findViewById(R.id.snap_name)
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

            scope.launch(Dispatchers.IO) {
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
}

