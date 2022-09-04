package com.hfad.someapp

import android.media.ThumbnailUtils
import android.os.Build
import android.os.CancellationSignal
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class CaptionedSnapAdapter(private val snaps: List<String>) :
    RecyclerView.Adapter<CaptionedSnapAdapter.SnapViewHolder>() {

    class SnapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val snapVideoView: ImageView = itemView.findViewById(R.id.snap_thumbnail)
        val snapName: TextView = itemView.findViewById(R.id.snap_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_caption_snap, parent, false)
        return SnapViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: SnapViewHolder, position: Int) {
        val path = snaps[position]
        val size = Size(200, 200)
        val cs = CancellationSignal()
        val file = File(path)
        val thumbnail =  ThumbnailUtils.createVideoThumbnail(file, size, cs)
        holder.snapVideoView.apply {
            setImageBitmap(thumbnail)
            contentDescription = file.name
        }
        holder.snapName.text = file.nameWithoutExtension
    }

    override fun getItemCount(): Int {
        return snaps.size
    }
}